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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.state.EChange;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.scopes.MetaScopeFactory;
import com.phloc.scopes.nonweb.impl.SessionScope;
import com.phloc.scopes.web.domain.ISessionApplicationWebScope;
import com.phloc.scopes.web.domain.ISessionWebScope;

/**
 * Default implementation of the {@link ISessionWebScope} interface
 * 
 * @author philip
 */
@ThreadSafe
public class SessionWebScope extends SessionScope implements ISessionWebScope
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (SessionWebScope.class);

  private final HttpSession m_aHttpSession;

  public SessionWebScope (@Nonnull final HttpSession aHttpSession)
  {
    super (aHttpSession.getId ());
    m_aHttpSession = aHttpSession;
  }

  @Override
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
  public EChange setAttribute (@Nonnull final String sName, @Nullable final Object aNewValue)
  {
    if (aNewValue != null && !(aNewValue instanceof Serializable))
      s_aLogger.warn ("Value of class " + aNewValue.getClass ().getName () + " should implement Serializable!");

    return super.setAttribute (sName, aNewValue);
  }

  @Override
  @Nonnull
  protected ISessionApplicationWebScope createSessionApplicationScope (@Nonnull @Nonempty final String sApplicationID)
  {
    return MetaScopeFactory.getWebScopeFactory ().createSessionApplicationScope (sApplicationID);
  }

  @Override
  @Nullable
  public ISessionApplicationWebScope getSessionApplicationScope (@Nonnull @Nonempty final String sApplicationID,
                                                                 final boolean bCreateIfNotExisting)
  {
    return (ISessionApplicationWebScope) super.getSessionApplicationScope (sApplicationID, bCreateIfNotExisting);
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
    return ToStringGenerator.getDerived (super.toString ()).append ("httpSession", m_aHttpSession).toString ();
  }
}
