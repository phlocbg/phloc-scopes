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
package com.phloc.scopes.spi;

import static org.junit.Assert.assertEquals;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

import com.phloc.scopes.ScopeUtils;
import com.phloc.scopes.nonweb.mgr.ScopeManager;
import com.phloc.scopes.web.mgr.WebScopeManager;
import com.phloc.scopes.web.mock.MockHttpServletRequest;
import com.phloc.scopes.web.mock.MockHttpServletResponse;
import com.phloc.scopes.web.mock.MockServletContext;
import com.phloc.scopes.web.mock.WebScopeTestInit;

/**
 * Test class for class {@link ScopeSPIManager}.
 * 
 * @author philip
 */
public final class ScopeSPIManagerTest
{
  static
  {
    WebScopeTestInit.setCoreMockHttpListeners ();
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
  public void testWebGlobalScope ()
  {
    // Create global scope only
    int nPrev = AbstractScopeSPI.getBegin ();
    new MockServletContext ();
    assertEquals (nPrev + 2, AbstractScopeSPI.getBegin ());

    // End global scope
    nPrev = AbstractScopeSPI.getEnd ();
    WebScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 2, AbstractScopeSPI.getEnd ());
  }

  @Test
  public void testWebRequestScope ()
  {
    // Create global scope
    int nPrev = AbstractScopeSPI.getBegin ();
    new MockServletContext ();
    assertEquals (nPrev + 2, AbstractScopeSPI.getBegin ());

    // Create request scope
    nPrev = AbstractScopeSPI.getBegin ();
    WebScopeManager.onRequestBegin ("appid", new MockHttpServletRequest (), new MockHttpServletResponse ());
    assertEquals (nPrev + 2, AbstractScopeSPI.getBegin ());

    // End request scope
    nPrev = AbstractScopeSPI.getEnd ();
    WebScopeManager.onRequestEnd ();
    assertEquals (nPrev + 2, AbstractScopeSPI.getEnd ());

    // End global scope
    nPrev = AbstractScopeSPI.getEnd ();
    WebScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 2, AbstractScopeSPI.getEnd ());
  }

  @Test
  public void testWebApplicationScope ()
  {
    // Create global scope
    int nPrev = AbstractScopeSPI.getBegin ();
    new MockServletContext ();
    assertEquals (nPrev + 2, AbstractScopeSPI.getBegin ());

    // Create request scope
    nPrev = AbstractScopeSPI.getBegin ();
    WebScopeManager.onRequestBegin ("appid", new MockHttpServletRequest (), new MockHttpServletResponse ());
    assertEquals (nPrev + 2, AbstractScopeSPI.getBegin ());

    // Create application scope
    nPrev = AbstractScopeSPI.getBegin ();
    WebScopeManager.getApplicationScope ();
    assertEquals (nPrev + 2, AbstractScopeSPI.getBegin ());

    // End request scope
    nPrev = AbstractScopeSPI.getEnd ();
    WebScopeManager.onRequestEnd ();
    assertEquals (nPrev + 2, AbstractScopeSPI.getEnd ());

    // End global scope and application scope
    nPrev = AbstractScopeSPI.getEnd ();
    WebScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 4, AbstractScopeSPI.getEnd ());
  }

  @Test
  public void testWebApplicationScopes ()
  {
    // Create global scope
    int nPrev = AbstractScopeSPI.getBegin ();
    final ServletContext aSC = new MockServletContext ();
    assertEquals (nPrev + 2, AbstractScopeSPI.getBegin ());

    // Create request scope
    nPrev = AbstractScopeSPI.getBegin ();
    new MockHttpServletRequest (aSC);
    assertEquals (nPrev + 2, AbstractScopeSPI.getBegin ());

    // Create application scope
    nPrev = AbstractScopeSPI.getBegin ();
    WebScopeManager.getApplicationScope ();
    assertEquals (nPrev + 2, AbstractScopeSPI.getBegin ());

    // Create second application scope
    nPrev = AbstractScopeSPI.getBegin ();
    WebScopeManager.getApplicationScope ("any other blabla");
    assertEquals (nPrev + 2, AbstractScopeSPI.getBegin ());

    // End request scope
    nPrev = AbstractScopeSPI.getEnd ();
    WebScopeManager.onRequestEnd ();
    assertEquals (nPrev + 2, AbstractScopeSPI.getEnd ());

    // End global scope and application scopes
    nPrev = AbstractScopeSPI.getEnd ();
    WebScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 6, AbstractScopeSPI.getEnd ());
  }

  @Test
  public void testWebSessionScopes ()
  {
    // Create global scope
    int nPrev = AbstractScopeSPI.getBegin ();
    final ServletContext aSC = new MockServletContext ();
    assertEquals (nPrev + 2, AbstractScopeSPI.getBegin ());

    // Create request scope
    nPrev = AbstractScopeSPI.getBegin ();
    final HttpServletRequest aRequest = new MockHttpServletRequest (aSC);
    assertEquals (nPrev + 2, AbstractScopeSPI.getBegin ());

    // Create application scope
    nPrev = AbstractScopeSPI.getBegin ();
    WebScopeManager.getApplicationScope ();
    assertEquals (nPrev + 2, AbstractScopeSPI.getBegin ());

    // Create second application scope
    nPrev = AbstractScopeSPI.getBegin ();
    WebScopeManager.getApplicationScope ("any other blabla");
    assertEquals (nPrev + 2, AbstractScopeSPI.getBegin ());

    // Begin session scope
    nPrev = AbstractScopeSPI.getBegin ();
    aRequest.getSession ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());

    // End request scope
    nPrev = AbstractScopeSPI.getEnd ();
    WebScopeManager.onRequestEnd ();
    assertEquals (nPrev + 2, AbstractScopeSPI.getEnd ());

    // End session scope
    nPrev = AbstractScopeSPI.getEnd ();
    aRequest.getSession ().invalidate ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getEnd ());

    // End global scope and application scopes
    nPrev = AbstractScopeSPI.getEnd ();
    WebScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 6, AbstractScopeSPI.getEnd ());
  }

  @Test
  public void testWebSessionApplicationScopes ()
  {
    // Create global scope
    int nPrev = AbstractScopeSPI.getBegin ();
    final ServletContext aSC = new MockServletContext ();
    assertEquals (nPrev + 2, AbstractScopeSPI.getBegin ());

    // Create request scope
    nPrev = AbstractScopeSPI.getBegin ();
    final HttpServletRequest aRequest = new MockHttpServletRequest (aSC);
    assertEquals (nPrev + 2, AbstractScopeSPI.getBegin ());

    // Create application scope
    nPrev = AbstractScopeSPI.getBegin ();
    WebScopeManager.getApplicationScope ();
    assertEquals (nPrev + 2, AbstractScopeSPI.getBegin ());

    // Create second application scope
    nPrev = AbstractScopeSPI.getBegin ();
    WebScopeManager.getApplicationScope ("any other blabla");
    assertEquals (nPrev + 2, AbstractScopeSPI.getBegin ());

    // Begin session scope
    nPrev = AbstractScopeSPI.getBegin ();
    aRequest.getSession ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());

    // Get session application scope
    nPrev = AbstractScopeSPI.getBegin ();
    WebScopeManager.getSessionApplicationScope ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());

    // Get second session application scope
    nPrev = AbstractScopeSPI.getBegin ();
    WebScopeManager.getSessionApplicationScope ("session web scope for testing");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());

    // End request scope
    nPrev = AbstractScopeSPI.getEnd ();
    WebScopeManager.onRequestEnd ();
    assertEquals (nPrev + 2, AbstractScopeSPI.getEnd ());

    // End session scope and session application scopes
    nPrev = AbstractScopeSPI.getEnd ();
    aRequest.getSession ().invalidate ();
    assertEquals (nPrev + 3, AbstractScopeSPI.getEnd ());

    // End global scope and application scopes
    nPrev = AbstractScopeSPI.getEnd ();
    WebScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 6, AbstractScopeSPI.getEnd ());
  }
}
