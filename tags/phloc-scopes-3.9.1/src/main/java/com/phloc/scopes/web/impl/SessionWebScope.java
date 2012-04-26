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
package com.phloc.scopes.web.impl;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.state.EChange;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.scopes.AbstractMapBasedScope;
import com.phloc.scopes.MetaScopeFactory;
import com.phloc.scopes.ScopeUtils;
import com.phloc.scopes.web.domain.ISessionApplicationWebScope;
import com.phloc.scopes.web.domain.ISessionWebScope;

/**
 * Default implementation of the {@link ISessionWebScope} interface
 * 
 * @author philip
 */
@ThreadSafe
public class SessionWebScope extends AbstractMapBasedScope implements ISessionWebScope
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (SessionWebScope.class);

  private final HttpSession m_aHttpSession;
  private final Map <String, ISessionApplicationWebScope> m_aSessionAppScopes = new HashMap <String, ISessionApplicationWebScope> ();

  public SessionWebScope (@Nonnull final HttpSession aHttpSession)
  {
    super (aHttpSession.getId ());
    m_aHttpSession = aHttpSession;

    // Sessions are always displayed to see what's happening
    if (ScopeUtils.debugScopeLifeCycle (s_aLogger))
      s_aLogger.info ("Created session web scope '" + getID () + "'");
  }

  public void initScope ()
  {
    // Copy all attributes from the HTTP session in this scope
    final Enumeration <?> aAttrNames = m_aHttpSession.getAttributeNames ();
    if (aAttrNames != null)
      while (aAttrNames.hasMoreElements ())
      {
        final String sAttrName = (String) aAttrNames.nextElement ();
        final Object aAttrValue = m_aHttpSession.getAttribute (sAttrName);
        setAttribute (sAttrName, aAttrValue);
      }
  }

  @Override
  @Nonnull
  public EChange setAttribute (@Nonnull final String sName, @Nonnull final Object aNewValue)
  {
    if (aNewValue != null && !(aNewValue instanceof Serializable))
      s_aLogger.warn ("Value of class " + aNewValue.getClass ().getName () + " should implement Serializable!");

    return super.setAttribute (sName, aNewValue);
  }

  @Override
  protected final void destroyOwnedScopes ()
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      // destroy all contained session application scopes first
      for (final ISessionApplicationWebScope aSessionAppScope : m_aSessionAppScopes.values ())
        aSessionAppScope.destroyScope ();
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
      s_aLogger.info ("Destroyed session web scope '" + getID () + "'");
  }

  @Nullable
  public ISessionApplicationWebScope getSessionApplicationScope (@Nonnull @Nonempty final String sApplicationID,
                                                                 final boolean bCreateIfNotExisting)
  {
    if (StringHelper.hasNoText (sApplicationID))
      throw new IllegalArgumentException ("applicationID");

    // To make the ID unique, prepend the application ID with this scope ID
    final String sAppScopeID = getID () + '.' + sApplicationID;
    ISessionApplicationWebScope aSessionAppScope;

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
          aSessionAppScope = MetaScopeFactory.getWebScopeFactory ().createSessionApplicationScope (sAppScopeID);
          m_aSessionAppScopes.put (sAppScopeID, aSessionAppScope);
          aSessionAppScope.initScope ();
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
  public Map <String, ISessionApplicationWebScope> getAllSessionApplicationScopes ()
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

  @Nonnull
  public HttpSession getSession ()
  {
    return m_aHttpSession;
  }

  public boolean isNew ()
  {
    return m_aHttpSession.isNew ();
  }

  public long getMaxInactiveInterval ()
  {
    return m_aHttpSession.getMaxInactiveInterval ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("httpSession", m_aHttpSession)
                            .append ("sessionAppScopes", m_aSessionAppScopes)
                            .toString ();
  }
}
