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
package com.phloc.scopes.mock;

import java.io.File;

import javax.annotation.concurrent.Immutable;

import com.phloc.commons.annotations.PresentForCodeCoverage;
import com.phloc.scopes.mgr.ScopeManager;

/**
 * This class provides the initialization handling for scopes in unit tests.
 * 
 * @author philip
 */
@Immutable
public final class ScopeAwareTestSetup
{
  public static final String MOCK_GLOBAL_SCOPE_ID = "mock.global";
  public static final String MOCK_APPLICATION_SCOPE_ID = "mock.appid";
  public static final String MOCK_REQUEST_SCOPE_ID = "mock.request";
  public static final String MOCK_SESSION_SCOPE_ID = "mock.session";

  public static final File STORAGE_PATH = new File ("target/junittest").getAbsoluteFile ();

  @SuppressWarnings ("unused")
  @PresentForCodeCoverage
  private static final ScopeAwareTestSetup s_aInstance = new ScopeAwareTestSetup ();

  private ScopeAwareTestSetup ()
  {}

  /**
   * Run this before tests are executed to initialize a global scope and a
   * request.
   */
  public static void setupScopeTests ()
  {
    // begin request
    ScopeManager.onGlobalBegin (MOCK_GLOBAL_SCOPE_ID);

    // begin request
    ScopeManager.onRequestBegin (MOCK_APPLICATION_SCOPE_ID, MOCK_REQUEST_SCOPE_ID, MOCK_SESSION_SCOPE_ID);
  }

  /**
   * Run this after your tests to shutdown the request and the global scope.
   */
  public static void shutdownScopeTests ()
  {
    // end request
    ScopeManager.onRequestEnd ();

    // shutdown global context
    ScopeManager.onGlobalEnd ();
  }
}
