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

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.phloc.commons.annotations.OverrideOnDemand;
import com.phloc.commons.exceptions.InitializationException;
import com.phloc.commons.lang.CGStringHelper;
import com.phloc.commons.string.StringHelper;

public class DefaultScopeAwareFilter implements Filter
{
  private String m_sApplicationID;

  /**
   * @param aFilterConfig
   *        The filter configuration
   * @return The application ID for this filter.
   */
  @OverrideOnDemand
  protected String getApplicationID (@Nonnull final FilterConfig aFilterConfig)
  {
    return CGStringHelper.getClassLocalName (getClass ());
  }

  public void init (@Nonnull final FilterConfig aFilterConfig) throws ServletException
  {
    m_sApplicationID = getApplicationID (aFilterConfig);
    if (StringHelper.hasNoText (m_sApplicationID))
      throw new InitializationException ("Failed retrieve a valid application ID!");
  }

  public final void doFilter (final ServletRequest aRequest, final ServletResponse aResponse, final FilterChain aChain) throws IOException,
                                                                                                                       ServletException
  {
    final HttpServletRequest aHttpRequest = (HttpServletRequest) aRequest;
    final HttpServletResponse aHttpResponse = (HttpServletResponse) aResponse;

    // Check if a scope needs to be created
    final RequestScopeInitializer aRequestScopeInitializer = RequestScopeInitializer.create (m_sApplicationID,
                                                                                             aHttpRequest,
                                                                                             aHttpResponse);
    try
    {
      // Continue as usual
      aChain.doFilter (aHttpRequest, aHttpResponse);
    }
    finally
    {
      // Destroy the scope
      aRequestScopeInitializer.destroyScope ();
    }
  }

  @OverrideOnDemand
  public void destroy ()
  {}
}
