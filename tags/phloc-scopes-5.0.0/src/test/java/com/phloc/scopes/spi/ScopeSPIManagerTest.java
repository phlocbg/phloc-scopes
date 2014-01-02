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
package com.phloc.scopes.spi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.phloc.scopes.ScopeUtils;
import com.phloc.scopes.nonweb.domain.ISessionScope;
import com.phloc.scopes.nonweb.mgr.ScopeManager;

/**
 * Test class for class {@link ScopeSPIManager}.
 * 
 * @author philip
 */
public final class ScopeSPIManagerTest
{
  static
  {
    ScopeUtils.setLifeCycleDebuggingEnabled (true);
  }

  @Test
  public void testNonWebGlobalScope ()
  {
    // Create global scope only
    int nPrev = AbstractScopeSPI.getBegin ();
    ScopeManager.onGlobalBegin ("global");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());

    // End global scope
    nPrev = AbstractScopeSPI.getEnd ();
    ScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getEnd ());
  }

  @Test
  public void testNonWebRequestScope ()
  {
    // Create global scope
    int nPrev = AbstractScopeSPI.getBegin ();
    ScopeManager.onGlobalBegin ("global");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());

    // Create request scope
    nPrev = AbstractScopeSPI.getBegin ();
    ScopeManager.onRequestBegin ("appid", "scopeid", "sessionid");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());

    // End request scope
    nPrev = AbstractScopeSPI.getEnd ();
    ScopeManager.onRequestEnd ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getEnd ());

    // End global scope
    nPrev = AbstractScopeSPI.getEnd ();
    ScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getEnd ());
  }

  @Test
  public void testNonWebApplicationScope ()
  {
    // Create global scope
    int nPrev = AbstractScopeSPI.getBegin ();
    ScopeManager.onGlobalBegin ("global");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());

    // Create request scope
    nPrev = AbstractScopeSPI.getBegin ();
    ScopeManager.onRequestBegin ("appid", "scopeid", "sessionid");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());

    // Create application scope
    nPrev = AbstractScopeSPI.getBegin ();
    ScopeManager.getApplicationScope ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());

    // End request scope
    nPrev = AbstractScopeSPI.getEnd ();
    ScopeManager.onRequestEnd ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getEnd ());

    // End global scope and application scope
    nPrev = AbstractScopeSPI.getEnd ();
    ScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 2, AbstractScopeSPI.getEnd ());
  }

  @Test
  public void testNonWebApplicationScopes ()
  {
    // Create global scope
    int nPrev = AbstractScopeSPI.getBegin ();
    ScopeManager.onGlobalBegin ("global");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());

    // Create request scope
    nPrev = AbstractScopeSPI.getBegin ();
    ScopeManager.onRequestBegin ("appid", "scopeid", "sessionid");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());

    // Create application scope
    nPrev = AbstractScopeSPI.getBegin ();
    ScopeManager.getApplicationScope ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());

    // Create second application scope
    nPrev = AbstractScopeSPI.getBegin ();
    ScopeManager.getApplicationScope ("any other blabla");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());

    // End request scope
    nPrev = AbstractScopeSPI.getEnd ();
    ScopeManager.onRequestEnd ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getEnd ());

    // End global scope and application scopes
    nPrev = AbstractScopeSPI.getEnd ();
    ScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 3, AbstractScopeSPI.getEnd ());
  }

  @Test
  public void testNonWebSessionScopes ()
  {
    // Create global scope
    int nPrev = AbstractScopeSPI.getBegin ();
    ScopeManager.onGlobalBegin ("global");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());

    // Create request scope
    nPrev = AbstractScopeSPI.getBegin ();
    ScopeManager.onRequestBegin ("appid", "scopeid", "sessionid");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());

    // Create application scope
    nPrev = AbstractScopeSPI.getBegin ();
    ScopeManager.getApplicationScope ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());

    // Create second application scope
    nPrev = AbstractScopeSPI.getBegin ();
    ScopeManager.getApplicationScope ("any other blabla");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());

    // Begin session scope
    nPrev = AbstractScopeSPI.getBegin ();
    final ISessionScope aSessionScope = ScopeManager.getSessionScope ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());

    // End request scope
    nPrev = AbstractScopeSPI.getEnd ();
    ScopeManager.onRequestEnd ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getEnd ());

    // End session scope
    nPrev = AbstractScopeSPI.getEnd ();
    ScopeManager.destroySessionScope (aSessionScope);
    assertEquals (nPrev + 1, AbstractScopeSPI.getEnd ());

    // End global scope and application scopes
    nPrev = AbstractScopeSPI.getEnd ();
    ScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 3, AbstractScopeSPI.getEnd ());
  }

  @Test
  public void testNonWebSessionApplicationScopes ()
  {
    // Create global scope
    int nPrev = AbstractScopeSPI.getBegin ();
    ScopeManager.onGlobalBegin ("global");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());

    // Create request scope
    nPrev = AbstractScopeSPI.getBegin ();
    ScopeManager.onRequestBegin ("appid", "scopeid", "sessionid");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());

    // Create application scope
    nPrev = AbstractScopeSPI.getBegin ();
    ScopeManager.getApplicationScope ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());

    // Create second application scope
    nPrev = AbstractScopeSPI.getBegin ();
    ScopeManager.getApplicationScope ("any other blabla");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());

    // Begin session scope
    nPrev = AbstractScopeSPI.getBegin ();
    final ISessionScope aSessionScope = ScopeManager.getSessionScope ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());

    // Get session application scope
    nPrev = AbstractScopeSPI.getBegin ();
    ScopeManager.getSessionApplicationScope ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());

    // Get second session application scope
    nPrev = AbstractScopeSPI.getBegin ();
    ScopeManager.getSessionApplicationScope ("session web scope for testing");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());

    // End request scope
    nPrev = AbstractScopeSPI.getEnd ();
    ScopeManager.onRequestEnd ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getEnd ());

    // End session scope and session application scopes
    nPrev = AbstractScopeSPI.getEnd ();
    ScopeManager.destroySessionScope (aSessionScope);
    assertEquals (nPrev + 3, AbstractScopeSPI.getEnd ());

    // End global scope and application scopes
    nPrev = AbstractScopeSPI.getEnd ();
    ScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 3, AbstractScopeSPI.getEnd ());
  }
}