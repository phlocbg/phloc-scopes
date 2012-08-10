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
package com.phloc.scopes.nonweb.mock;

import com.phloc.scopes.nonweb.mgr.ScopeManager;

/**
 * Base class for all JUnit tests requiring correct scope handling.
 * 
 * @author philip
 */
public final class ScopeAwareTestSetup
{
  /** The application ID to use. */
  private static final String MOCK_APPID = "mock.appid";

  private ScopeAwareTestSetup ()
  {}

  public static void setupScopeTests ()
  {
    // begin request
    ScopeManager.onGlobalBegin ("mock.global");

    // begin request
    ScopeManager.onRequestBegin (MOCK_APPID, "mock.request", "mock.session");
  }

  public static void shutdownScopeTests ()
  {
    // end request
    ScopeManager.onRequestEnd ();

    // shutdown global context
    ScopeManager.onGlobalEnd ();
  }
}
