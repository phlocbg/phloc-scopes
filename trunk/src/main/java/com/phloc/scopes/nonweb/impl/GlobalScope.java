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
import com.phloc.commons.annotations.OverrideOnDemand;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.scopes.AbstractMapBasedScope;
import com.phloc.scopes.MetaScopeFactory;
import com.phloc.scopes.ScopeUtils;
import com.phloc.scopes.nonweb.domain.IApplicationScope;
import com.phloc.scopes.nonweb.domain.IGlobalScope;
import com.phloc.scopes.spi.ScopeSPIManager;

/**
 * Base implementation of the {@link IGlobalScope} interface.<br>
 * Note: for synchronization issues, this class stores the attributes in a
 * separate map.
 * 
 * @author philip
 */
@ThreadSafe
public class GlobalScope extends AbstractMapBasedScope implements IGlobalScope
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (GlobalScope.class);

  private final Map <String, IApplicationScope> m_aAppScopes = new HashMap <String, IApplicationScope> ();

  public GlobalScope (@Nonnull @Nonempty final String sScopeID)
  {
    super (sScopeID);

    if (ScopeUtils.debugScopeLifeCycle (s_aLogger))
      s_aLogger.info ("Created global scope '" + getID () + "'");
  }

  public void initScope ()
  {}

  @Override
  protected void destroyOwnedScopes ()
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      for (final IApplicationScope aAppScope : m_aAppScopes.values ())
      {
        // Invoke SPIs
        ScopeSPIManager.onApplicationScopeEnd (aAppScope);

        // Destroy the scope
        aAppScope.destroyScope ();
      }
      m_aAppScopes.clear ();
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
      s_aLogger.info ("Destroyed global scope '" + getID () + "'");
  }

  /**
   * This method creates a new application scope. Override in WebScopeManager to
   * create an IApplicationWebScope!
   * 
   * @param sApplicationID
   *        The application ID to use
   * @return Never <code>null</code>.
   */
  @Nonnull
  @OverrideOnDemand
  protected IApplicationScope createApplicationScope (@Nonnull @Nonempty final String sApplicationID)
  {
    return MetaScopeFactory.getScopeFactory ().createApplicationScope (sApplicationID);
  }

  @Nullable
  public IApplicationScope getApplicationScope (@Nonnull @Nonempty final String sApplicationID,
                                                final boolean bCreateIfNotExisting)
  {
    // Read-lock only
    IApplicationScope aAppScope;
    m_aRWLock.readLock ().lock ();
    try
    {
      aAppScope = m_aAppScopes.get (sApplicationID);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }

    if (aAppScope == null && bCreateIfNotExisting)
    {
      // now write lock
      m_aRWLock.writeLock ().lock ();
      try
      {
        // Make sure it was not added in the mean time
        aAppScope = m_aAppScopes.get (sApplicationID);
        if (aAppScope == null)
        {
          aAppScope = createApplicationScope (sApplicationID);
          m_aAppScopes.put (sApplicationID, aAppScope);
          aAppScope.initScope ();

          // Invoke SPIs
          ScopeSPIManager.onApplicationScopeBegin (aAppScope);
        }
      }
      finally
      {
        m_aRWLock.writeLock ().unlock ();
      }
    }
    return aAppScope;
  }

  @Nonnull
  @ReturnsMutableCopy
  public Map <String, IApplicationScope> getAllApplicationScopes ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return ContainerHelper.newMap (m_aAppScopes);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnegative
  public int getApplicationScopeCount ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aAppScopes.size ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!super.equals (o))
      return false;
    final GlobalScope rhs = (GlobalScope) o;
    return m_aAppScopes.equals (rhs.m_aAppScopes);
  }

  @Override
  public int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).append (m_aAppScopes).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("appScopes", m_aAppScopes).toString ();
  }
}
