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
package com.phloc.scopes.singleton;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.phloc.commons.mock.PhlocTestUtils;
import com.phloc.scopes.mock.ScopeTestRule;

/**
 * Test class for class {@link SessionApplicationSingleton}.<br>
 * Note: must reside here for Mock* stuff!
 * 
 * @author philip
 */
public final class SessionApplicationSingletonTest
{
  @Rule
  public final TestRule m_aScopeRule = new ScopeTestRule ();

  @Test
  public void testBasic () throws Exception
  {
    assertTrue (SessionApplicationSingleton.getAllSingletons ().isEmpty ());
    assertFalse (SessionApplicationSingleton.isSingletonInstantiated (MockSessionApplicationSingleton.class));
    assertNull (SessionApplicationSingleton.getSingletonIfInstantiated (MockSessionApplicationSingleton.class));

    final MockSessionApplicationSingleton a = MockSessionApplicationSingleton.getInstance ();
    assertTrue (SessionApplicationSingleton.isSingletonInstantiated (MockSessionApplicationSingleton.class));
    assertSame (a, SessionApplicationSingleton.getSingletonIfInstantiated (MockSessionApplicationSingleton.class));
    assertEquals (0, a.get ());
    a.inc ();
    assertEquals (1, a.get ());
    assertSame (a, MockSessionApplicationSingleton.getInstance ());

    PhlocTestUtils.testDefaultSerialization (a);
  }
}
