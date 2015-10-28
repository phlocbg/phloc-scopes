/**
 * Copyright (C) 2006-2015 phloc systems
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
import com.phloc.scopes.domain.ISessionScope;
import com.phloc.scopes.mgr.ScopeManager;

/**
 * Test class for class {@link ScopeSPIManager}.
 * 
 * @author Philip Helger
 */
public final class ScopeSPIManagerTest
{
  static
  {
    ScopeUtils.setLifeCycleDebuggingEnabled (true);
  }

  @Test
  public void testGlobalScope ()
  {
    // Create global scope only
    int nPrev = AbstractScopeSPI.getBegin ();
    int nPrevT = AbstractThrowingScopeSPI.getBegin ();
    ScopeManager.onGlobalBegin ("global");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getBegin ());

    // End global scope
    nPrev = AbstractScopeSPI.getEnd ();
    nPrevT = AbstractThrowingScopeSPI.getEnd ();
    ScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getEnd ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getEnd ());
  }

  @Test
  public void testRequestScope ()
  {
    // Create global scope
    int nPrev = AbstractScopeSPI.getBegin ();
    int nPrevT = AbstractThrowingScopeSPI.getBegin ();
    ScopeManager.onGlobalBegin ("global");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getBegin ());

    // Create request scope
    nPrev = AbstractScopeSPI.getBegin ();
    nPrevT = AbstractThrowingScopeSPI.getBegin ();
    ScopeManager.onRequestBegin ("appid", "scopeid", "sessionid");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getBegin ());

    // End request scope
    nPrev = AbstractScopeSPI.getEnd ();
    nPrevT = AbstractThrowingScopeSPI.getEnd ();
    ScopeManager.onRequestEnd ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getEnd ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getEnd ());

    // End global scope
    nPrev = AbstractScopeSPI.getEnd ();
    nPrevT = AbstractThrowingScopeSPI.getEnd ();
    ScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getEnd ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getEnd ());
  }

  @Test
  public void testApplicationScope ()
  {
    // Create global scope
    int nPrev = AbstractScopeSPI.getBegin ();
    int nPrevT = AbstractThrowingScopeSPI.getBegin ();
    ScopeManager.onGlobalBegin ("global");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getBegin ());

    // Create request scope
    nPrev = AbstractScopeSPI.getBegin ();
    nPrevT = AbstractThrowingScopeSPI.getBegin ();
    ScopeManager.onRequestBegin ("appid", "scopeid", "sessionid");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getBegin ());

    // Create application scope
    nPrev = AbstractScopeSPI.getBegin ();
    nPrevT = AbstractThrowingScopeSPI.getBegin ();
    ScopeManager.getApplicationScope ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getBegin ());

    // End request scope
    nPrev = AbstractScopeSPI.getEnd ();
    nPrevT = AbstractThrowingScopeSPI.getEnd ();
    ScopeManager.onRequestEnd ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getEnd ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getEnd ());

    // End global scope and application scope
    nPrev = AbstractScopeSPI.getEnd ();
    nPrevT = AbstractThrowingScopeSPI.getEnd ();
    ScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 2, AbstractScopeSPI.getEnd ());
    assertEquals (nPrev + 2, AbstractThrowingScopeSPI.getEnd ());
  }

  @Test
  public void testApplicationScopes ()
  {
    // Create global scope
    int nPrev = AbstractScopeSPI.getBegin ();
    int nPrevT = AbstractThrowingScopeSPI.getBegin ();
    ScopeManager.onGlobalBegin ("global");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getBegin ());

    // Create request scope
    nPrev = AbstractScopeSPI.getBegin ();
    nPrevT = AbstractThrowingScopeSPI.getBegin ();
    ScopeManager.onRequestBegin ("appid", "scopeid", "sessionid");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getBegin ());

    // Create application scope
    nPrev = AbstractScopeSPI.getBegin ();
    nPrevT = AbstractThrowingScopeSPI.getBegin ();
    ScopeManager.getApplicationScope ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getBegin ());

    // Create second application scope
    nPrev = AbstractScopeSPI.getBegin ();
    nPrevT = AbstractThrowingScopeSPI.getBegin ();
    ScopeManager.getApplicationScope ("any other blabla");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getBegin ());

    // End request scope
    nPrev = AbstractScopeSPI.getEnd ();
    nPrevT = AbstractThrowingScopeSPI.getEnd ();
    ScopeManager.onRequestEnd ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getEnd ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getEnd ());

    // End global scope and application scopes
    nPrev = AbstractScopeSPI.getEnd ();
    nPrevT = AbstractThrowingScopeSPI.getEnd ();
    ScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 3, AbstractScopeSPI.getEnd ());
    assertEquals (nPrevT + 3, AbstractThrowingScopeSPI.getEnd ());
  }

  @Test
  public void testSessionScopes ()
  {
    // Create global scope
    int nPrev = AbstractScopeSPI.getBegin ();
    int nPrevT = AbstractThrowingScopeSPI.getBegin ();
    ScopeManager.onGlobalBegin ("global");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getBegin ());

    // Create request scope
    nPrev = AbstractScopeSPI.getBegin ();
    nPrevT = AbstractThrowingScopeSPI.getBegin ();
    ScopeManager.onRequestBegin ("appid", "scopeid", "sessionid");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getBegin ());

    // Create application scope
    nPrev = AbstractScopeSPI.getBegin ();
    nPrevT = AbstractThrowingScopeSPI.getBegin ();
    ScopeManager.getApplicationScope ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getBegin ());

    // Create second application scope
    nPrev = AbstractScopeSPI.getBegin ();
    nPrevT = AbstractThrowingScopeSPI.getBegin ();
    ScopeManager.getApplicationScope ("any other blabla");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getBegin ());

    // Begin session scope
    nPrev = AbstractScopeSPI.getBegin ();
    nPrevT = AbstractThrowingScopeSPI.getBegin ();
    final ISessionScope aSessionScope = ScopeManager.getSessionScope ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getBegin ());

    // End request scope
    nPrev = AbstractScopeSPI.getEnd ();
    nPrevT = AbstractThrowingScopeSPI.getEnd ();
    ScopeManager.onRequestEnd ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getEnd ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getEnd ());

    // End session scope
    nPrev = AbstractScopeSPI.getEnd ();
    nPrevT = AbstractThrowingScopeSPI.getEnd ();
    ScopeManager.destroySessionScope (aSessionScope);
    assertEquals (nPrev + 1, AbstractScopeSPI.getEnd ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getEnd ());

    // End global scope and application scopes
    nPrev = AbstractScopeSPI.getEnd ();
    nPrevT = AbstractThrowingScopeSPI.getEnd ();
    ScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 3, AbstractScopeSPI.getEnd ());
    assertEquals (nPrevT + 3, AbstractThrowingScopeSPI.getEnd ());
  }

  @Test
  public void testSessionApplicationScopes ()
  {
    // Create global scope
    int nPrev = AbstractScopeSPI.getBegin ();
    int nPrevT = AbstractThrowingScopeSPI.getBegin ();
    ScopeManager.onGlobalBegin ("global");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getBegin ());

    // Create request scope
    nPrev = AbstractScopeSPI.getBegin ();
    nPrevT = AbstractThrowingScopeSPI.getBegin ();
    ScopeManager.onRequestBegin ("appid", "scopeid", "sessionid");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getBegin ());

    // Create application scope
    nPrev = AbstractScopeSPI.getBegin ();
    nPrevT = AbstractThrowingScopeSPI.getBegin ();
    ScopeManager.getApplicationScope ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getBegin ());

    // Create second application scope
    nPrev = AbstractScopeSPI.getBegin ();
    nPrevT = AbstractThrowingScopeSPI.getBegin ();
    ScopeManager.getApplicationScope ("any other blabla");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getBegin ());

    // Begin session scope
    nPrev = AbstractScopeSPI.getBegin ();
    nPrevT = AbstractThrowingScopeSPI.getBegin ();
    final ISessionScope aSessionScope = ScopeManager.getSessionScope ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getBegin ());

    // Get session application scope
    nPrev = AbstractScopeSPI.getBegin ();
    nPrevT = AbstractThrowingScopeSPI.getBegin ();
    ScopeManager.getSessionApplicationScope ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getBegin ());

    // Get second session application scope
    nPrev = AbstractScopeSPI.getBegin ();
    nPrevT = AbstractThrowingScopeSPI.getBegin ();
    ScopeManager.getSessionApplicationScope ("session web scope for testing");
    assertEquals (nPrev + 1, AbstractScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getBegin ());

    // End request scope
    nPrev = AbstractScopeSPI.getEnd ();
    nPrevT = AbstractThrowingScopeSPI.getEnd ();
    ScopeManager.onRequestEnd ();
    assertEquals (nPrev + 1, AbstractScopeSPI.getEnd ());
    assertEquals (nPrevT + 1, AbstractThrowingScopeSPI.getEnd ());

    // End session scope and session application scopes
    nPrev = AbstractScopeSPI.getEnd ();
    nPrevT = AbstractThrowingScopeSPI.getEnd ();
    ScopeManager.destroySessionScope (aSessionScope);
    assertEquals (nPrev + 3, AbstractScopeSPI.getEnd ());
    assertEquals (nPrevT + 3, AbstractThrowingScopeSPI.getEnd ());

    // End global scope and application scopes
    nPrev = AbstractScopeSPI.getEnd ();
    nPrevT = AbstractThrowingScopeSPI.getEnd ();
    ScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 3, AbstractScopeSPI.getEnd ());
    assertEquals (nPrevT + 3, AbstractThrowingScopeSPI.getEnd ());
  }
}
