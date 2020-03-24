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
import java.util.List;
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
  private static final Logger LOG = LoggerFactory.getLogger (ScopeSessionManager.class);
  private static final IStatisticsHandlerCounter s_aUniqueSessionCounter = StatisticsManager.getCounterHandler (ScopeSessionManager.class.getName () +
                                                                                                                "$UNIQUE_SESSIONS"); //$NON-NLS-1$

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

    this.m_aRWLock.readLock ().lock ();
    try
    {
      return this.m_aSessionScopes.get (sScopeID);
    }
    finally
    {
      this.m_aRWLock.readLock ().unlock ();
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
    ValueEnforcer.notNull (aSessionScope, "SessionScope"); //$NON-NLS-1$

    final String sSessionID = aSessionScope.getID ();
    this.m_aRWLock.writeLock ().lock ();
    try
    {
      if (this.m_aSessionScopes.put (sSessionID, aSessionScope) != null)
        LOG.error ("Overwriting session scope with ID '" + sSessionID + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    finally
    {
      this.m_aRWLock.writeLock ().unlock ();
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
    ValueEnforcer.notNull (aSessionScope, "SessionScope"); //$NON-NLS-1$

    // Only handle scopes that are not yet destructed
    if (aSessionScope.isValid ())
    {
      final String sSessionID = aSessionScope.getID ();

      boolean bCanDestroyScope = true;
      this.m_aRWLock.writeLock ().lock ();
      try
      {
        // Only if we're not just in destruction of exactly this session
        if (this.m_aSessionsInDestruction.add (sSessionID))
        {
          // Remove from map
          final ISessionScope aRemovedScope = this.m_aSessionScopes.remove (sSessionID);
          if (aRemovedScope != aSessionScope)
          {
            LOG.error ("Ending an unknown session with ID '" + sSessionID + "'"); //$NON-NLS-1$ //$NON-NLS-2$
            LOG.error ("  Scope to be removed: " + aSessionScope); //$NON-NLS-1$
            LOG.error ("  Removed scope:       " + aRemovedScope); //$NON-NLS-1$
          }
          bCanDestroyScope = true;
        }
        else
          LOG.info ("Already destructing session '" + sSessionID + "'"); //$NON-NLS-1$ //$NON-NLS-2$
      }
      finally
      {
        this.m_aRWLock.writeLock ().unlock ();
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
          this.m_aRWLock.writeLock ().lock ();
          try
          {
            this.m_aSessionsInDestruction.remove (sSessionID);
          }
          finally
          {
            this.m_aRWLock.writeLock ().unlock ();
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
    this.m_aRWLock.readLock ().lock ();
    try
    {
      return !this.m_aSessionScopes.isEmpty ();
    }
    finally
    {
      this.m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * @return The number of managed session scopes. Always &ge; 0.
   */
  @Nonnegative
  public int getSessionCount ()
  {
    this.m_aRWLock.readLock ().lock ();
    try
    {
      return this.m_aSessionScopes.size ();
    }
    finally
    {
      this.m_aRWLock.readLock ().unlock ();
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
    this.m_aRWLock.readLock ().lock ();
    try
    {
      return ContainerHelper.newList (this.m_aSessionScopes.values ());
    }
    finally
    {
      this.m_aRWLock.readLock ().unlock ();
    }
  }

  private void checkIfAnySessionsExist (final Collection <? extends ISessionScope> aScopes)
  {
    if (containsAnySession ())
    {
      final List <String> aResidual = ContainerHelper.newList ();
      this.m_aRWLock.writeLock ().lock ();
      try
      {
        for (final ISessionScope aScope : aScopes)
        {
          if (this.m_aSessionScopes.remove (aScope.getID ()) != null)
          {
            aResidual.add (aScope.getID ());
          }
        }
      }
      finally
      {
        this.m_aRWLock.writeLock ().unlock ();
        if (!aResidual.isEmpty ())
        {
          LOG.error ("Removed {} left over session scopes: {}", String.valueOf (aResidual.size ()), aResidual); //$NON-NLS-1$
        }
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
    final Collection <? extends ISessionScope> aAllScopes = getAllSessionScopes ();

    for (final ISessionScope aSessionScope : aAllScopes)
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
    checkIfAnySessionsExist (aAllScopes);
  }

  /**
   * Remove all existing session scopes, and invoke the destruction methods on
   * the contained objects.
   */
  private void endAllSessionScopes ()
  {
    // end all session scopes without destroying the underlying sessions (make a
    // copy, because we're invalidating the sessions!)
    final Collection <? extends ISessionScope> aAllScopes = getAllSessionScopes ();
    for (final ISessionScope aSessionScope : aAllScopes)
    {
      // Remove from map
      onScopeEnd (aSessionScope);
    }
    // Sanity check in case something went wrong
    checkIfAnySessionsExist (aAllScopes);
  }

  public boolean isDestroyAllSessionsOnScopeEnd ()
  {
    this.m_aRWLock.readLock ().lock ();
    try
    {
      return this.m_bDestroyAllSessionsOnScopeEnd;
    }
    finally
    {
      this.m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnull
  public EChange setDestroyAllSessionsOnScopeEnd (final boolean bDestroyAllSessionsOnScopeEnd)
  {
    this.m_aRWLock.writeLock ().lock ();
    try
    {
      if (this.m_bDestroyAllSessionsOnScopeEnd == bDestroyAllSessionsOnScopeEnd)
        return EChange.UNCHANGED;
      this.m_bDestroyAllSessionsOnScopeEnd = bDestroyAllSessionsOnScopeEnd;
      return EChange.CHANGED;
    }
    finally
    {
      this.m_aRWLock.writeLock ().unlock ();
    }
  }

  public boolean isEndAllSessionsOnScopeEnd ()
  {
    this.m_aRWLock.readLock ().lock ();
    try
    {
      return this.m_bEndAllSessionsOnScopeEnd;
    }
    finally
    {
      this.m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnull
  public EChange setEndAllSessionsOnScopeEnd (final boolean bEndAllSessionsOnScopeEnd)
  {
    this.m_aRWLock.writeLock ().lock ();
    try
    {
      if (this.m_bEndAllSessionsOnScopeEnd == bEndAllSessionsOnScopeEnd)
        return EChange.UNCHANGED;
      this.m_bEndAllSessionsOnScopeEnd = bEndAllSessionsOnScopeEnd;
      return EChange.CHANGED;
    }
    finally
    {
      this.m_aRWLock.writeLock ().unlock ();
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
        endAllSessionScopes ();
    s_aInstance = null;
  }
}
