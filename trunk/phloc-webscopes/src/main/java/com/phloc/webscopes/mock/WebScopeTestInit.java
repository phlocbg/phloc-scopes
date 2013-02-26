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

import javax.annotation.concurrent.Immutable;

import com.phloc.web.mock.MockHttpListener;
import com.phloc.webscopes.servlet.WebScopeListener;

@Immutable
public final class WebScopeTestInit
{
  private WebScopeTestInit ()
  {}

  /**
   * So that the mock HTTP listeners are available outside of this class
   */
  public static void setCoreMockHttpListeners ()
  {
    // Web listeners
    MockHttpListener.removeAllDefaultListeners ();
    MockHttpListener.addDefaultListener (new WebScopeListener ());
    MockHttpListener.addDefaultListener (new MockServletRequestListenerScopeAware ());
    MockHttpListener.setToDefault ();
  }
}
