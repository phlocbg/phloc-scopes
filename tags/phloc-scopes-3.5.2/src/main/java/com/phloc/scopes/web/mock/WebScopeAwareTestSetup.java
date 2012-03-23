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

/**
 * Base class for all JUnit tests requiring correct web scope handling.
 * 
 * @author philip
 */
public final class WebScopeAwareTestSetup
{
  public static final String MOCK_CONTEXT = "/MockContext";
  private static MockServletContext s_aServletContext;
  private static MockHttpServletRequest s_aRequest;

  static
  {
    MockHttpListener.setToDefault ();
  }

  private WebScopeAwareTestSetup ()
  {}

  public static void setupScopeTests ()
  {
    // Start global scope
    s_aServletContext = new MockServletContext (MOCK_CONTEXT);

    // Start request scope
    s_aRequest = new MockHttpServletRequest (s_aServletContext);
  }

  public static void shutdownScopeTests ()
  {
    // end request
    s_aRequest.invalidate ();
    s_aRequest = null;

    // shutdown global context
    s_aServletContext.invalidate ();
    s_aServletContext = null;
  }
}
