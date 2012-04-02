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
package com.phloc.scopes.web.servlet;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.scopes.web.domain.IRequestWebScope;
import com.phloc.scopes.web.mgr.WebScopeManager;

/**
 * Internal class from scope aware filter and servlets.
 * 
 * @author philip
 */
@Immutable
public final class RequestScopeInitializer
{
  private final IRequestWebScope m_aRequestScope;
  private final boolean m_bCreatedIt;

  /**
   * Ctor.
   * 
   * @param aRequestScope
   *        The request scope to be used. May not be <code>null</code>.
   * @param bCreatedIt
   *        <code>true</code> if the request scope was newly created,
   *        <code>false</code> if an existing request web scope is reused.
   */
  private RequestScopeInitializer (@Nonnull final IRequestWebScope aRequestScope, final boolean bCreatedIt)
  {
    if (aRequestScope == null)
      throw new NullPointerException ("requestScope");
    m_aRequestScope = aRequestScope;
    m_bCreatedIt = bCreatedIt;
  }

  /**
   * @return The request web scope to be used.
   */
  @Nonnull
  public IRequestWebScope getRequestScope ()
  {
    return m_aRequestScope;
  }

  /**
   * Destroy the current request scope if it was initialized here.
   */
  public void destroyScope ()
  {
    if (m_bCreatedIt)
    {
      // End the scope after the complete filtering process (if it was
      // created)
      WebScopeManager.onRequestEnd ();
    }
  }

  @Nonnull
  public static RequestScopeInitializer create (@Nonnull @Nonempty final String sApplicationID,
                                                @Nonnull final HttpServletRequest aHttpRequest,
                                                @Nonnull final HttpServletResponse aHttpResponse)
  {
    if (WebScopeManager.isRequestScopePresent ())
    {
      // A scope is already present - e.g. from a scope aware filter
      return new RequestScopeInitializer (WebScopeManager.getRequestScope (), false);
    }

    // No scope present
    final IRequestWebScope aRequestScope = WebScopeManager.onRequestBegin (sApplicationID, aHttpRequest, aHttpResponse);
    return new RequestScopeInitializer (aRequestScope, true);
  }
}
