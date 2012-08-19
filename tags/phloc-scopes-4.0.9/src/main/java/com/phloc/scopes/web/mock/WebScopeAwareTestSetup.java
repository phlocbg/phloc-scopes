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
@DevelopersNote ("It's preferred to use the WebScopeTestRule class instead!")
public final class WebScopeAwareTestSetup
{
  public static final String MOCK_CONTEXT = "/MockContext";

  private static MockServletContext s_aServletContext;
  private static MockHttpServletRequest s_aRequest;

  private WebScopeAwareTestSetup ()
  {}

  public static void setupScopeTests ()
  {
    setupScopeTests (null);
  }

  public static void setupScopeTests (@Nullable final Map <String, String> aServletContextInitParams)
  {
    // Start global scope -> triggers events
    s_aServletContext = new MockServletContext (MOCK_CONTEXT, aServletContextInitParams);

    // Start request scope -> triggers events
    s_aRequest = new MockHttpServletRequest (s_aServletContext);
  }

  public static void shutdownScopeTests ()
  {
    if (s_aRequest != null)
    {
      // end request -> triggers events
      s_aRequest.invalidate ();
      s_aRequest = null;
    }

    if (s_aServletContext != null)
    {
      // shutdown global context -> triggers events
      s_aServletContext.invalidate ();
      s_aServletContext = null;
    }
  }

  @Nullable
  public static MockServletContext getServletContext ()
  {
    return s_aServletContext;
  }

  @Nullable
  public static MockHttpServletRequest getRequest ()
  {
    return s_aRequest;
  }

  @Nullable
  public static HttpSession getSession (final boolean bCreateIfNotExisting)
  {
    return s_aRequest == null ? null : s_aRequest.getSession (bCreateIfNotExisting);
  }
}
