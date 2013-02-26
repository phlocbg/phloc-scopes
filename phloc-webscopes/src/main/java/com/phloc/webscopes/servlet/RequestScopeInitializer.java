/**
 * Copyright (C) 2006-2013 phloc systems
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
package com.phloc.webscopes.servlet;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.string.StringHelper;
import com.phloc.scopes.nonweb.mgr.ScopeManager;
import com.phloc.webscopes.domain.IRequestWebScope;
import com.phloc.webscopes.mgr.WebScopeManager;

/**
 * Internal class from scope aware filter and servlets.
 * 
 * @author philip
 */
@Immutable
public final class RequestScopeInitializer
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (RequestScopeInitializer.class);

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
    if (StringHelper.hasNoText (sApplicationID))
      throw new IllegalArgumentException ("No application ID present!");

    // Check if a request scope is already present
    final IRequestWebScope aExistingRequestScope = WebScopeManager.getRequestScopeOrNull ();
    if (aExistingRequestScope != null)
    {
      // A scope is already present - e.g. from a scope aware filter

      // Check if scope is in destruction or destroyed!
      if (aExistingRequestScope.isValid ())
      {
        // Check the application IDs
        final String sExistingApplicationID = ScopeManager.getRequestApplicationID (aExistingRequestScope);
        if (!sApplicationID.equals (sExistingApplicationID))
        {
          // Application ID mismatch!
          s_aLogger.warn ("The existing request scope has the application ID '" +
                          sExistingApplicationID +
                          "' but now the application ID '" +
                          sApplicationID +
                          "' should be used. The old application ID '" +
                          sExistingApplicationID +
                          "' is continued to be used!!!");
        }
        return new RequestScopeInitializer (aExistingRequestScope, false);
      }

      // Wow...
      s_aLogger.error ("The existing request scope is no longer valid - creating a new one: " +
                       aExistingRequestScope.toString ());
    }

    // No valid scope present
    // -> create a new scope
    final IRequestWebScope aRequestScope = WebScopeManager.onRequestBegin (sApplicationID, aHttpRequest, aHttpResponse);
    return new RequestScopeInitializer (aRequestScope, true);
  }
}
