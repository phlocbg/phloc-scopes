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
import javax.annotation.concurrent.Immutable;
import javax.servlet.http.HttpSession;

import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Base class where the initialization of the scopes happens once before the
 * class and once afterwards. This is faster than performing the initialization
 * before each test separately.
 * 
 * @author philip
 */
@Immutable
@Deprecated
public abstract class AbstractWebScopeAwareTestSuite extends AbstractWebTestCase
{
  @BeforeClass
  public static void beforeClass () throws Exception
  {
    // No static init parameters here :(
    WebScopeAwareTestSetup.setupScopeTests ();
  }

  @Nonnull
  protected final MockServletContext getServletContext ()
  {
    return WebScopeAwareTestSetup.getServletContext ();
  }

  @Nonnull
  protected final MockHttpServletRequest getRequest ()
  {
    return WebScopeAwareTestSetup.getRequest ();
  }

  @Nonnull
  protected final HttpSession getSession (final boolean bCreateIfNotExisting)
  {
    return WebScopeAwareTestSetup.getSession (bCreateIfNotExisting);
  }

  @AfterClass
  public static void afterClass () throws Exception
  {
    WebScopeAwareTestSetup.shutdownScopeTests ();
  }
}
