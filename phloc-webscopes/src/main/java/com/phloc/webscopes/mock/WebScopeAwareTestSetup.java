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
package com.phloc.webscopes.mock;

import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.servlet.http.HttpSession;

import com.phloc.commons.annotations.DevelopersNote;
import com.phloc.web.mock.MockHttpServletRequest;
import com.phloc.web.mock.MockServletContext;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Base class for all JUnit tests requiring correct web scope handling.
 * 
 * @author philip
 */
@NotThreadSafe
@DevelopersNote ("It's preferred to use the WebScopeTestRule class instead!")
@Deprecated
@SuppressFBWarnings ("LI_LAZY_INIT_UPDATE_STATIC")
public final class WebScopeAwareTestSetup
{
  /** Mock servlet context name */
  public static final String MOCK_CONTEXT = WebScopeTestRule.MOCK_CONTEXT_PATH;

  private static volatile WebScopeTestRule s_aRule;

  private WebScopeAwareTestSetup ()
  {}

  public static void setupScopeTests ()
  {
    setupScopeTests (null);
  }

  public static void setupScopeTests (@Nullable final Map <String, String> aServletContextInitParams)
  {
    if (s_aRule != null)
      throw new IllegalStateException ();
    s_aRule = new WebScopeTestRule (aServletContextInitParams);
    s_aRule.before ();
  }

  public static void shutdownScopeTests ()
  {
    if (s_aRule != null)
    {
      s_aRule.after ();
      s_aRule = null;
    }
  }

  @Nullable
  public static MockServletContext getServletContext ()
  {
    return s_aRule.getServletContext ();
  }

  @Nullable
  public static MockHttpServletRequest getRequest ()
  {
    return s_aRule.getRequest ();
  }

  @Nullable
  public static HttpSession getSession (final boolean bCreateIfNotExisting)
  {
    return s_aRule.getSession (bCreateIfNotExisting);
  }
}
