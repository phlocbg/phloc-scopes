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
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.stats.IStatisticsHandlerCounter;
import com.phloc.commons.stats.StatisticsManager;
import com.phloc.scopes.MetaScopeFactory;
import com.phloc.scopes.nonweb.domain.ISessionScope;
import com.phloc.scopes.nonweb.singleton.GlobalSingleton;
import com.phloc.scopes.spi.ScopeSPIManager;

/**
 * Internal manager class for session web scopes.
 * 
 * @author philip
 */
public final class ScopeSessionManager extends GlobalSingleton
{
  private static final IStatisticsHandlerCounter s_aUniqueSessionCounter = StatisticsManager.getCounterHandler (ScopeSessionManager.class.getName () +
                                                                                                                "$UNIQUE_SESSIONS");

  /** All contained session scopes. */
  private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  private final Map <String, ISessionScope> m_aSessionScopes = new HashMap <String, ISessionScope> ();

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

  /**
   * Get the session scope matching the passed session ID.
   * 
   * @param sSessionID
   *        The session ID for which the session scope is used.
   * @param bCreateIfNotExisting
   *        if <code>true</code> and the scope is not existing, the scope is
   *        created
   * @return The matching session scope or <code>null</code> if no such session
   *         scope exists and bCreateIfNotExisting is false.
   */
  @Nullable
  public ISessionScope getSessionScope (@Nonnull @Nonempty final String sSessionID, final boolean bCreateIfNotExisting)
  {
    // Check if a matching session scope is present
    ISessionScope aSessionScope = getSessionScopeOfID (sSessionID);
    if (aSessionScope == null && bCreateIfNotExisting)
    {
      // create new scope
      m_aRWLock.writeLock ().lock ();
      try
      {
        // Try in write-lock to be 100% sure
        aSessionScope = m_aSessionScopes.get (sSessionID);
        if (aSessionScope == null)
        {
          aSessionScope = MetaScopeFactory.getScopeFactory ().createSessionScope (sSessionID);
          m_aSessionScopes.put (sSessionID, aSessionScope);
          aSessionScope.initScope ();

          // Invoke SPIs
          ScopeSPIManager.onSessionScopeBegin (aSessionScope);

          s_aUniqueSessionCounter.increment ();
        }
      }
      finally
      {
        m_aRWLock.writeLock ().unlock ();
      }
    }
    return aSessionScope;
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
    m_aRWLock.writeLock ().lock ();
    try
    {
      // destroy all session scopes
      for (final ISessionScope aSessionScope : m_aSessionScopes.values ())
      {
        // Invoke SPIs
        ScopeSPIManager.onSessionScopeEnd (aSessionScope);

        // Destroy the scope
        aSessionScope.destroyScope ();
      }
      m_aSessionScopes.clear ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }
}
