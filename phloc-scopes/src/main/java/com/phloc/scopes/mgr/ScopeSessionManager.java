/**
 * Copyright (C) 2006-2015 phloc systems
 * http://www.phloc.com
 * office[at]phloc[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.phloc.scopes.mgr;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.ValueEnforcer;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.state.EChange;
import com.phloc.commons.stats.IStatisticsHandlerCounter;
import com.phloc.commons.stats.StatisticsManager;
import com.phloc.commons.string.StringHelper;
import com.phloc.scopes.domain.ISessionScope;
import com.phloc.scopes.singleton.GlobalSingleton;
import com.phloc.scopes.spi.ScopeSPIManager;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Internal manager class for session scopes.<br>
 * This class is only non-final so that the WebScopeSessionManager can be used
 * for web scopes!
 * 
 * @author Philip Helger
 */
@ThreadSafe
public class ScopeSessionManager extends GlobalSingleton
{
  public static final boolean DEFAULT_DESTROY_ALL_SESSIONS_ON_SCOPE_END = true;
  public static final boolean DEFAULT_END_ALL_SESSIONS_ON_SCOPE_END = true;
  private static final Logger s_aLogger = LoggerFactory.getLogger (ScopeSessionManager.class);
  private static final IStatisticsHandlerCounter s_aUniqueSessionCounter = StatisticsManager.getCounterHandler (ScopeSessionManager.class.getName () +
                                                                                                                "$UNIQUE_SESSIONS");

  private static volatile ScopeSessionManager s_aInstance = null;

  /** All contained session scopes. */
  private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  private final Map <String, ISessionScope> m_aSessionScopes = new HashMap <String, ISessionScope> ();
  private final Set <String> m_aSessionsInDestruction = new HashSet <String> ();
  @GuardedBy ("m_aRWLock")
  private boolean m_bDestroyAllSessionsOnScopeEnd = DEFAULT_DESTROY_ALL_SESSIONS_ON_SCOPE_END;
  @GuardedBy ("m_aRWLock")
  private boolean m_bEndAllSessionsOnScopeEnd = DEFAULT_END_ALL_SESSIONS_ON_SCOPE_END;

  @Deprecated
  @UsedViaReflection
  public ScopeSessionManager ()
  {}

  @Nonnull
  public static ScopeSessionManager getInstance ()
  {
    // This special handling is needed, because this global singleton is
    // required upon shutdown of the GlobalWebScope!
    if (s_aInstance == null)
      s_aInstance = getGlobalSingleton (ScopeSessionManager.class);
    return s_aInstance;
  }

  /**
   * Get the session scope with the specified ID. If no such scope exists, no
   * further actions are taken.
   * 
   * @param sScopeID
   *        The ID to be resolved. May be <code>null</code>.
   * @return <code>null</code> if no such scope exists.
   */
  @Nullable
  public ISessionScope getSessionScopeOfID (@Nullable final String sScopeID)
  {
    if (StringHelper.hasNoText (sScopeID))
      return null;

    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aSessionScopes.get (sScopeID);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * Register the passed session scope in the internal map, call the
   * {@link ISessionScope #initScope()} method and finally invoke the SPIs for
   * the new scope.
   * 
   * @param aSessionScope
   *        The session scope that was just created. May not be
   *        <code>null</code>.
   */
  public void onScopeBegin (@Nonnull final ISessionScope aSessionScope)
  {
    ValueEnforcer.notNull (aSessionScope, "SessionScope");

    final String sSessionID = aSessionScope.getID ();
    m_aRWLock.writeLock ().lock ();
    try
    {
      if (m_aSessionScopes.put (sSessionID, aSessionScope) != null)
        s_aLogger.error ("Overwriting session scope with ID '" + sSessionID + "'");
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }

    // Init the scope after it was registered
    aSessionScope.initScope ();

    // Invoke SPIs
    ScopeSPIManager.onSessionScopeBegin (aSessionScope);

    // Increment statistics counter
    s_aUniqueSessionCounter.increment ();
  }

  /**
   * Close the passed session scope gracefully. Each managed scope is guaranteed
   * to be destroyed only once. First the SPI manager is invoked, and afterwards
   * the scope is destroyed.
   * 
   * @param aSessionScope
   *        The session scope to be ended. May not be <code>null</code>.
   */
  public void onScopeEnd (@Nonnull final ISessionScope aSessionScope)
  {
    ValueEnforcer.notNull (aSessionScope, "SessionScope");

    // Only handle scopes that are not yet destructed
    if (aSessionScope.isValid ())
    {
      final String sSessionID = aSessionScope.getID ();

      boolean bCanDestroyScope = true;
      m_aRWLock.writeLock ().lock ();
      try
      {
        // Only if we're not just in destruction of exactly this session
        if (m_aSessionsInDestruction.add (sSessionID))
        {
          // Remove from map
          final ISessionScope aRemovedScope = m_aSessionScopes.remove (sSessionID);
          if (aRemovedScope != aSessionScope)
          {
            s_aLogger.error ("Ending an unknown session with ID '" + sSessionID + "'");
            s_aLogger.error ("  Scope to be removed: " + aSessionScope);
            s_aLogger.error ("  Removed scope:       " + aRemovedScope);
          }
          bCanDestroyScope = true;
        }
        else
          s_aLogger.info ("Already destructing session '" + sSessionID + "'");
      }
      finally
      {
        m_aRWLock.writeLock ().unlock ();
      }

      if (bCanDestroyScope)
      {
        // Destroy scope outside of write lock
        try
        {
          // Invoke SPIs
          ScopeSPIManager.onSessionScopeEnd (aSessionScope);

          // Destroy the scope
          aSessionScope.destroyScope ();
        }
        finally
        {
          // Remove from "in destruction" list
          m_aRWLock.writeLock ().lock ();
          try
          {
            m_aSessionsInDestruction.remove (sSessionID);
          }
          finally
          {
            m_aRWLock.writeLock ().unlock ();
          }
        }
      }
    }
  }

  /**
   * @return <code>true</code> if at least one session is present,
   *         <code>false</code> otherwise
   */
  public boolean containsAnySession ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return !m_aSessionScopes.isEmpty ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * @return The number of managed session scopes. Always &ge; 0.
   */
  @Nonnegative
  public int getSessionCount ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aSessionScopes.size ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * @return A non-<code>null</code>, mutable copy of all managed session
   *         scopes.
   */
  @Nonnull
  @ReturnsMutableCopy
  public Collection <? extends ISessionScope> getAllSessionScopes ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return ContainerHelper.newList (m_aSessionScopes.values ());
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  private void _checkIfAnySessionsExist ()
  {
    if (containsAnySession ())
    {
      m_aRWLock.writeLock ().lock ();
      try
      {
        s_aLogger.error ("The following " +
                         m_aSessionScopes.size () +
                         " session scopes are left over: " +
                         m_aSessionScopes);
        m_aSessionScopes.clear ();
      }
      finally
      {
        m_aRWLock.writeLock ().unlock ();
      }
    }
  }

  /**
   * Destroy all known session scopes. After this method it is ensured that the
   * internal session map is empty.
   */
  public void destroyAllSessions ()
  {
    // destroy all session scopes (make a copy, because we're invalidating
    // the sessions internally!)
    for (final ISessionScope aSessionScope : getAllSessionScopes ())
    {
      // Unfortunately we need a special handling here
      if (aSessionScope.selfDestruct ().isContinue ())
      {
        // Remove from map
        onScopeEnd (aSessionScope);
      }
      // Else the destruction was already started!
    }

    // Sanity check in case something went wrong
    _checkIfAnySessionsExist ();
  }

  /**
   * Remove all existing session scopes, and invoke the destruction methods on
   * the contained objects.
   */
  private void _endAllSessionScopes ()
  {
    // end all session scopes without destroying the underlying sessions (make a
    // copy, because we're invalidating the sessions!)
    for (final ISessionScope aSessionScope : getAllSessionScopes ())
    {
      // Remove from map
      onScopeEnd (aSessionScope);
    }

    // Sanity check in case something went wrong
    _checkIfAnySessionsExist ();
  }

  public boolean isDestroyAllSessionsOnScopeEnd ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_bDestroyAllSessionsOnScopeEnd;
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnull
  public EChange setDestroyAllSessionsOnScopeEnd (final boolean bDestroyAllSessionsOnScopeEnd)
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      if (m_bDestroyAllSessionsOnScopeEnd == bDestroyAllSessionsOnScopeEnd)
        return EChange.UNCHANGED;
      m_bDestroyAllSessionsOnScopeEnd = bDestroyAllSessionsOnScopeEnd;
      return EChange.CHANGED;
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  public boolean isEndAllSessionsOnScopeEnd ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_bEndAllSessionsOnScopeEnd;
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnull
  public EChange setEndAllSessionsOnScopeEnd (final boolean bEndAllSessionsOnScopeEnd)
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      if (m_bEndAllSessionsOnScopeEnd == bEndAllSessionsOnScopeEnd)
        return EChange.UNCHANGED;
      m_bEndAllSessionsOnScopeEnd = bEndAllSessionsOnScopeEnd;
      return EChange.CHANGED;
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @SuppressFBWarnings ("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
  @Override
  protected void onDestroy ()
  {
    if (isDestroyAllSessionsOnScopeEnd ())
      destroyAllSessions ();
    else
      if (isEndAllSessionsOnScopeEnd ())
        _endAllSessionScopes ();
    s_aInstance = null;
  }
}
