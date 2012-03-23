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
package com.phloc.scopes.web.factory;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.scopes.web.domain.IApplicationWebScope;
import com.phloc.scopes.web.domain.IGlobalWebScope;
import com.phloc.scopes.web.domain.IRequestWebScope;
import com.phloc.scopes.web.domain.ISessionApplicationWebScope;
import com.phloc.scopes.web.domain.ISessionWebScope;
import com.phloc.scopes.web.impl.ApplicationWebScope;
import com.phloc.scopes.web.impl.GlobalWebScope;
import com.phloc.scopes.web.impl.RequestWebScope;
import com.phloc.scopes.web.impl.SessionApplicationWebScope;
import com.phloc.scopes.web.impl.SessionWebScope;

/**
 * Standalone version of the scope factory. No dependencies to Web components.
 * 
 * @author philip
 */
public class DefaultWebScopeFactory implements IWebScopeFactory
{
  public DefaultWebScopeFactory ()
  {}

  @Nonnull
  public IGlobalWebScope createGlobalScope (@Nonnull final ServletContext aServletContext)
  {
    return new GlobalWebScope (aServletContext);
  }

  @Nonnull
  public final IApplicationWebScope createApplicationScope (@Nonnull @Nonempty final String sScopeID)
  {
    return new ApplicationWebScope (sScopeID);
  }

  @Nonnull
  public ISessionWebScope createSessionScope (@Nonnull final HttpSession aHttpSession)
  {
    return new SessionWebScope (aHttpSession);
  }

  @Nonnull
  public final ISessionApplicationWebScope createSessionApplicationScope (@Nonnull @Nonempty final String sScopeID)
  {
    return new SessionApplicationWebScope (sScopeID);
  }

  @Nonnull
  public IRequestWebScope createRequestScope (@Nonnull final HttpServletRequest aHttpRequest,
                                              @Nonnull final HttpServletResponse aHttpResponse)
  {
    return new RequestWebScope (aHttpRequest, aHttpResponse);
  }
}
