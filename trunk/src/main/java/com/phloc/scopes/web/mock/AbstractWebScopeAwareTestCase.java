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
package com.phloc.scopes.web.mock;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;

import com.phloc.commons.mock.AbstractPhlocTestCase;
import com.phloc.scopes.web.mgr.WebScopeManager;

/**
 * Base class where the initialization of the scopes happens before each test
 * execution.
 * 
 * @author philip
 */
public abstract class AbstractWebScopeAwareTestCase extends AbstractPhlocTestCase
{
  protected static final String MOCK_CONTEXT = WebScopeAwareTestSetup.MOCK_CONTEXT;

  @Override
  protected void beforeSingleTest () throws Exception
  {
    super.beforeSingleTest ();
    WebScopeAwareTestSetup.setupScopeTests ();
  }

  @Nonnull
  protected final ServletContext getServletContext ()
  {
    return WebScopeManager.getGlobalScope ().getServletContext ();
  }

  @Override
  protected void afterSingleTest () throws Exception
  {
    WebScopeAwareTestSetup.shutdownScopeTests ();
    super.afterSingleTest ();
  }
}
