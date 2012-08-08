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
package com.phloc.scopes.nonweb.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.state.EContinue;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.scopes.AbstractMapBasedScope;
import com.phloc.scopes.MetaScopeFactory;
import com.phloc.scopes.ScopeUtils;
import com.phloc.scopes.nonweb.domain.ISessionApplicationScope;
import com.phloc.scopes.nonweb.domain.ISessionScope;
import com.phloc.scopes.spi.ScopeSPIManager;

/**
 * Default implementation of the {@link ISessionScope} interface
 * 
 * @author philip
 */
@ThreadSafe
public class SessionScope extends AbstractMapBasedScope implements ISessionScope
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (SessionScope.class);

  private final Map <String, ISessionApplicationScope> m_aSessionAppScopes = new HashMap <String, ISessionApplicationScope> ();

  public SessionScope (@Nonnull @Nonempty final String sScopeID)
  {
    super (sScopeID);

    // Sessions are always displayed to see what's happening
    if (ScopeUtils.debugScopeLifeCycle (s_aLogger))
      s_aLogger.info ("Created session scope '" + getID () + "'");
  }

  public void initScope ()
  {}

  @Override
  protected final void destroyOwnedScopes ()
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      for (final ISessionApplicationScope aSessionAppScope : m_aSessionAppScopes.values ())
      {
        // Invoke SPIs
        ScopeSPIManager.onSessionApplicationScopeEnd (aSessionAppScope);

        // destroy the scope
        aSessionAppScope.destroyScope ();
      }
      m_aSessionAppScopes.clear ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Override
  protected void postDestroy ()
  {
    if (ScopeUtils.debugScopeLifeCycle (s_aLogger))
      s_aLogger.info ("Destroyed session scope '" + getID () + "'");
  }

  @Nonnull
  public EContinue selfDestruct ()
  {
    // Nothing to do here!
    return EContinue.CONTINUE;
  }

  @Nonnull
  protected ISessionApplicationScope createSessionApplicationScope (@Nonnull @Nonempty final String sApplicationID)
  {
    return MetaScopeFactory.getScopeFactory ().createSessionApplicationScope (sApplicationID);
  }

  @Nullable
  public ISessionApplicationScope getSessionApplicationScope (@Nonnull @Nonempty final String sApplicationID,
                                                              final boolean bCreateIfNotExisting)
  {
    if (StringHelper.hasNoText (sApplicationID))
      throw new IllegalArgumentException ("applicationID");

    // To make the ID unique, prepend the application ID with this scope ID
    final String sAppScopeID = getID () + '.' + sApplicationID;
    ISessionApplicationScope aSessionAppScope;

    // Try with read-lock only
    m_aRWLock.readLock ().lock ();
    try
    {
      aSessionAppScope = m_aSessionAppScopes.get (sAppScopeID);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }

    if (aSessionAppScope == null && bCreateIfNotExisting)
    {
      m_aRWLock.writeLock ().lock ();
      try
      {
        // Check again - now in write lock
        aSessionAppScope = m_aSessionAppScopes.get (sAppScopeID);
        if (aSessionAppScope == null)
        {
          // Definitively not present
          aSessionAppScope = createSessionApplicationScope (sAppScopeID);
          m_aSessionAppScopes.put (sAppScopeID, aSessionAppScope);
          aSessionAppScope.initScope ();

          // Invoke SPIs
          ScopeSPIManager.onSessionApplicationScopeBegin (aSessionAppScope);
        }
      }
      finally
      {
        m_aRWLock.writeLock ().unlock ();
      }
    }
    return aSessionAppScope;
  }

  @Nonnull
  @ReturnsMutableCopy
  public Map <String, ISessionApplicationScope> getAllSessionApplicationScopes ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return ContainerHelper.newMap (m_aSessionAppScopes);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnegative
  public int getSessionApplicationScopeCount ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aSessionAppScopes.size ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("sessionAppScopes", m_aSessionAppScopes)
                            .toString ();
  }
}
