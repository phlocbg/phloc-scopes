/**
 * Copyright (C) 2006-2012 phloc systems
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
package com.phloc.scopes.nonweb.mgr;

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
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.stats.IStatisticsHandlerCounter;
import com.phloc.commons.stats.StatisticsManager;
import com.phloc.scopes.nonweb.domain.ISessionScope;
import com.phloc.scopes.nonweb.singleton.GlobalSingleton;
import com.phloc.scopes.spi.ScopeSPIManager;

/**
 * Internal manager class for session web scopes.
 * 
 * @author philip
 */
@ThreadSafe
public final class ScopeSessionManager extends GlobalSingleton
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (ScopeSessionManager.class);
  private static final IStatisticsHandlerCounter s_aUniqueSessionCounter = StatisticsManager.getCounterHandler (ScopeSessionManager.class.getName () +
                                                                                                                "$UNIQUE_SESSIONS");

  /** All contained session scopes. */
  private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  private final Map <String, ISessionScope> m_aSessionScopes = new HashMap <String, ISessionScope> ();
  private final Set <String> m_aSessionsInDestruction = new HashSet <String> ();

  @Deprecated
  @UsedViaReflection
  public ScopeSessionManager ()
  {}

  @Nonnull
  public static ScopeSessionManager getInstance ()
  {
    return getGlobalSingleton (ScopeSessionManager.class);
  }

  /**
   * Get the session scope with the specified ID. If no such scope exists, no
   * further actions are taken.
   * 
   * @param sScopeID
   *        The ID to be resolved.
   * @return <code>null</code> if no such scope exists.
   */
  @Nullable
  public ISessionScope getSessionScopeOfID (@Nullable final String sScopeID)
  {
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

  public void onScopeBegin (@Nonnull final ISessionScope aSessionScope)
  {
    if (aSessionScope == null)
      throw new NullPointerException ("sessionScope");

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

  public void onScopeEnd (@Nonnull final ISessionScope aSessionScope)
  {
    if (aSessionScope == null)
      throw new NullPointerException ("sessionScope");

    final String sSessionID = aSessionScope.getID ();

    m_aRWLock.writeLock ().lock ();
    try
    {
      // Only if we're not just in destruction of exactly this session
      if (m_aSessionsInDestruction.add (sSessionID))
      {
        try
        {
          // Remove from map
          final ISessionScope aRemovedScope = m_aSessionScopes.remove (sSessionID);
          if (aRemovedScope != aSessionScope)
          {
            s_aLogger.error ("Ending an unknown session with ID '" + sSessionID + "'");
            s_aLogger.error ("  Scope to be removed: " + aSessionScope);
            s_aLogger.error ("  Removed scope:       " + aRemovedScope);
          }

          // Invoke SPIs
          ScopeSPIManager.onSessionScopeEnd (aSessionScope);

          // Destroy the scope
          aSessionScope.destroyScope ();
        }
        finally
        {
          m_aSessionsInDestruction.remove (sSessionID);
        }
      }
      else
        s_aLogger.info ("Already destructing session '" + sSessionID + "'");
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
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

  @Override
  protected void onDestroy ()
  {
    // destroy all session scopes (make a copy, because we're invalidating
    // the sessions!)
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
    if (getSessionCount () > 0)
    {
      m_aRWLock.writeLock ().lock ();
      try
      {
        s_aLogger.error ("The following session scopes are left over: " + m_aSessionScopes);
        m_aSessionScopes.clear ();
      }
      finally
      {
        m_aRWLock.writeLock ().unlock ();
      }
    }
  }
}
