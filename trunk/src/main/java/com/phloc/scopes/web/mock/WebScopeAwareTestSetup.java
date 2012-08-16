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

import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.servlet.http.HttpSession;

import com.phloc.commons.annotations.DevelopersNote;

/**
 * Base class for all JUnit tests requiring correct web scope handling.
 * 
 * @author philip
 */
@NotThreadSafe
@Deprecated
@DevelopersNote ("It's preferred to use the WebScopeTestRule class instead!")
public final class WebScopeAwareTestSetup
{
  private static WebScopeTestRule s_aRule;

  private WebScopeAwareTestSetup ()
  {}

  public static void setupScopeTests (@Nullable final Map <String, String> aServletContextInitParams) throws Throwable
  {
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
