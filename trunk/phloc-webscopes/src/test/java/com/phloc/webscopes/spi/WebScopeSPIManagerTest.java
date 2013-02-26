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
package com.phloc.webscopes.spi;

import static org.junit.Assert.assertEquals;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

import com.phloc.scopes.ScopeUtils;
import com.phloc.scopes.spi.ScopeSPIManager;
import com.phloc.web.mock.MockHttpServletRequest;
import com.phloc.web.mock.MockHttpServletResponse;
import com.phloc.web.mock.MockServletContext;
import com.phloc.webscopes.mgr.WebScopeManager;
import com.phloc.webscopes.mock.WebScopeTestInit;

/**
 * Test class for class {@link ScopeSPIManager}.
 * 
 * @author philip
 */
public final class WebScopeSPIManagerTest
{
  static
  {
    WebScopeTestInit.setCoreMockHttpListeners ();
    ScopeUtils.setLifeCycleDebuggingEnabled (true);
  }

  @Test
  public void testWebGlobalScope ()
  {
    // Create global scope only
    int nPrev = AbstractWebScopeSPI.getBegin ();
    new MockServletContext ();
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getBegin ());

    // End global scope
    nPrev = AbstractWebScopeSPI.getEnd ();
    WebScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getEnd ());
  }

  @Test
  public void testWebRequestScope ()
  {
    // Create global scope
    int nPrev = AbstractWebScopeSPI.getBegin ();
    new MockServletContext ();
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getBegin ());

    // Create request scope
    nPrev = AbstractWebScopeSPI.getBegin ();
    WebScopeManager.onRequestBegin ("appid", new MockHttpServletRequest (), new MockHttpServletResponse ());
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getBegin ());

    // End request scope
    nPrev = AbstractWebScopeSPI.getEnd ();
    WebScopeManager.onRequestEnd ();
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getEnd ());

    // End global scope
    nPrev = AbstractWebScopeSPI.getEnd ();
    WebScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getEnd ());
  }

  @Test
  public void testWebApplicationScope ()
  {
    // Create global scope
    int nPrev = AbstractWebScopeSPI.getBegin ();
    new MockServletContext ();
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getBegin ());

    // Create request scope
    nPrev = AbstractWebScopeSPI.getBegin ();
    WebScopeManager.onRequestBegin ("appid", new MockHttpServletRequest (), new MockHttpServletResponse ());
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getBegin ());

    // Create application scope
    nPrev = AbstractWebScopeSPI.getBegin ();
    WebScopeManager.getApplicationScope ();
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getBegin ());

    // End request scope
    nPrev = AbstractWebScopeSPI.getEnd ();
    WebScopeManager.onRequestEnd ();
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getEnd ());

    // End global scope and application scope
    nPrev = AbstractWebScopeSPI.getEnd ();
    WebScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 2, AbstractWebScopeSPI.getEnd ());
  }

  @Test
  public void testWebApplicationScopes ()
  {
    // Create global scope
    int nPrev = AbstractWebScopeSPI.getBegin ();
    final ServletContext aSC = new MockServletContext ();
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getBegin ());

    // Create request scope
    nPrev = AbstractWebScopeSPI.getBegin ();
    new MockHttpServletRequest (aSC);
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getBegin ());

    // Create application scope
    nPrev = AbstractWebScopeSPI.getBegin ();
    WebScopeManager.getApplicationScope ();
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getBegin ());

    // Create second application scope
    nPrev = AbstractWebScopeSPI.getBegin ();
    WebScopeManager.getApplicationScope ("any other blabla");
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getBegin ());

    // End request scope
    nPrev = AbstractWebScopeSPI.getEnd ();
    WebScopeManager.onRequestEnd ();
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getEnd ());

    // End global scope and application scopes
    nPrev = AbstractWebScopeSPI.getEnd ();
    WebScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 3, AbstractWebScopeSPI.getEnd ());
  }

  @Test
  public void testWebSessionScopes ()
  {
    // Create global scope
    int nPrev = AbstractWebScopeSPI.getBegin ();
    final ServletContext aSC = new MockServletContext ();
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getBegin ());

    // Create request scope
    nPrev = AbstractWebScopeSPI.getBegin ();
    final HttpServletRequest aRequest = new MockHttpServletRequest (aSC);
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getBegin ());

    // Create application scope
    nPrev = AbstractWebScopeSPI.getBegin ();
    WebScopeManager.getApplicationScope ();
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getBegin ());

    // Create second application scope
    nPrev = AbstractWebScopeSPI.getBegin ();
    WebScopeManager.getApplicationScope ("any other blabla");
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getBegin ());

    // Begin session scope
    nPrev = AbstractWebScopeSPI.getBegin ();
    aRequest.getSession ();
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getBegin ());

    // End request scope
    nPrev = AbstractWebScopeSPI.getEnd ();
    WebScopeManager.onRequestEnd ();
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getEnd ());

    // End session scope
    nPrev = AbstractWebScopeSPI.getEnd ();
    aRequest.getSession ().invalidate ();
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getEnd ());

    // End global scope and application scopes
    nPrev = AbstractWebScopeSPI.getEnd ();
    WebScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 3, AbstractWebScopeSPI.getEnd ());
  }

  @Test
  public void testWebSessionApplicationScopes ()
  {
    // Create global scope
    int nPrev = AbstractWebScopeSPI.getBegin ();
    final ServletContext aSC = new MockServletContext ();
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getBegin ());

    // Create request scope
    nPrev = AbstractWebScopeSPI.getBegin ();
    final HttpServletRequest aRequest = new MockHttpServletRequest (aSC);
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getBegin ());

    // Create application scope
    nPrev = AbstractWebScopeSPI.getBegin ();
    WebScopeManager.getApplicationScope ();
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getBegin ());

    // Create second application scope
    nPrev = AbstractWebScopeSPI.getBegin ();
    WebScopeManager.getApplicationScope ("any other blabla");
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getBegin ());

    // Begin session scope
    nPrev = AbstractWebScopeSPI.getBegin ();
    aRequest.getSession ();
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getBegin ());

    // Get session application scope
    nPrev = AbstractWebScopeSPI.getBegin ();
    WebScopeManager.getSessionApplicationScope ();
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getBegin ());

    // Get second session application scope
    nPrev = AbstractWebScopeSPI.getBegin ();
    WebScopeManager.getSessionApplicationScope ("session web scope for testing");
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getBegin ());

    // End request scope
    nPrev = AbstractWebScopeSPI.getEnd ();
    WebScopeManager.onRequestEnd ();
    assertEquals (nPrev + 1, AbstractWebScopeSPI.getEnd ());

    // End session scope and session application scopes
    nPrev = AbstractWebScopeSPI.getEnd ();
    aRequest.getSession ().invalidate ();
    assertEquals (nPrev + 3, AbstractWebScopeSPI.getEnd ());

    // End global scope and application scopes
    nPrev = AbstractWebScopeSPI.getEnd ();
    WebScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 3, AbstractWebScopeSPI.getEnd ());
  }
}
