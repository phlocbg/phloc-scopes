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
package com.phloc.scopes.web.mgr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.Serializable;

import javax.annotation.Nullable;

import org.junit.Test;

import com.phloc.scopes.IScopeRenewalAware;
import com.phloc.scopes.web.domain.ISessionApplicationWebScope;
import com.phloc.scopes.web.domain.ISessionWebScope;
import com.phloc.scopes.web.mock.AbstractWebScopeAwareTestSuite;

/**
 * Test class for class {@link WebScopeSessionHelper}.
 * 
 * @author philip
 */
public final class WebScopeSessionHelperTest extends AbstractWebScopeAwareTestSuite
{
  private static final class MockScopeRenewalAware implements IScopeRenewalAware, Serializable
  {
    private final String m_sStr;

    public MockScopeRenewalAware (@Nullable final String s)
    {
      m_sStr = s;
    }

    @Nullable
    public String getString ()
    {
      return m_sStr;
    }
  }

  @Test
  public void testRenewSessionScopeEmpty ()
  {
    WebScopeSessionHelper.renewCurrentSessionScope ();
  }

  @Test
  public void testRenewSessionScopeDefault ()
  {
    ISessionWebScope aWS = WebScopeManager.getSessionScope (true);
    aWS.setAttribute ("a1", new MockScopeRenewalAware ("session1"));
    aWS.setAttribute ("a2", new MockScopeRenewalAware ("session2"));
    aWS.setAttribute ("a21", "session21");
    assertEquals (3, aWS.getAllAttributes ().size ());

    // Contains renewal and non-renewal aware attrs
    ISessionApplicationWebScope aAWS1 = aWS.getSessionApplicationScope ("app1", true);
    aAWS1.setAttribute ("a3", new MockScopeRenewalAware ("session3"));
    aAWS1.setAttribute ("a4", new MockScopeRenewalAware ("session4"));
    aAWS1.setAttribute ("a41", "session41");
    assertEquals (3, aAWS1.getAllAttributes ().size ());

    // Contains only renewal aware attrs
    ISessionApplicationWebScope aAWS2 = aWS.getSessionApplicationScope ("app2", true);
    aAWS2.setAttribute ("a5", new MockScopeRenewalAware ("session5"));
    aAWS2.setAttribute ("a6", new MockScopeRenewalAware ("session6"));
    assertEquals (2, aAWS2.getAllAttributes ().size ());

    // Contains only non-renewal aware attrs
    ISessionApplicationWebScope aAWS3 = aWS.getSessionApplicationScope ("app3", true);
    aAWS3.setAttribute ("a7", "session7");
    aAWS3.setAttribute ("a8", "session8");
    assertEquals (2, aAWS3.getAllAttributes ().size ());

    assertEquals (3, aWS.getAllSessionApplicationScopes ().size ());

    // Main renew session
    final String sOldSessionID = aWS.getID ();
    WebScopeSessionHelper.renewCurrentSessionScope ();

    // Check session scope
    aWS = WebScopeManager.getSessionScope (false);
    assertNotNull (aWS);
    assertFalse (aWS.getID ().equals (sOldSessionID));
    assertEquals (2, aWS.getAllAttributes ().size ());

    // Only 2 session application scopes had scope renewal aware attrs
    assertEquals (2, aWS.getAllSessionApplicationScopes ().size ());

    aAWS1 = aWS.getSessionApplicationScope ("app1", false);
    assertNotNull (aAWS1);
    assertEquals (2, aAWS1.getAllAttributes ().size ());

    aAWS2 = aWS.getSessionApplicationScope ("app2", false);
    assertNotNull (aAWS2);
    assertEquals (2, aAWS2.getAllAttributes ().size ());
    assertNotNull (aAWS2.getAttributeObject ("a5"));
    assertEquals ("session6", ((MockScopeRenewalAware) aAWS2.getAttributeObject ("a6")).getString ());

    // Had no scope renewal aware attrs:
    aAWS3 = aWS.getSessionApplicationScope ("app3", false);
    assertNull (aAWS3);
  }
}
