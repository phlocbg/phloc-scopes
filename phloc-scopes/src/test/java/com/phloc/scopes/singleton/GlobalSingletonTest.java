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
package com.phloc.scopes.singleton;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.phloc.scopes.mock.ScopeTestRule;

/**
 * Test class for class {@link GlobalSingleton}.
 * 
 * @author Philip Helger
 */
public final class GlobalSingletonTest
{
  @Rule
  public final TestRule m_aScopeRule = new ScopeTestRule ();

  @BeforeClass
  public static void beforeClass ()
  {
    assertEquals (0, MockGlobalSingleton.s_nCtorCount);
    assertEquals (0, MockGlobalSingleton.s_nDtorCount);
  }

  @AfterClass
  public static void afterClass ()
  {
    assertEquals (1, MockGlobalSingleton.s_nCtorCount);
    assertEquals (1, MockGlobalSingleton.s_nDtorCount);
  }

  @Test
  public void testBasic ()
  {
    assertTrue (GlobalSingleton.getAllGlobalSingletons ().isEmpty ());
    assertFalse (GlobalSingleton.isGlobalSingletonInstantiated (MockGlobalSingleton.class));
    assertNull (GlobalSingleton.getGlobalSingletonIfInstantiated (MockGlobalSingleton.class));

    final MockGlobalSingleton a = MockGlobalSingleton.getInstance ();
    assertNotNull (a);
    assertTrue (GlobalSingleton.isGlobalSingletonInstantiated (MockGlobalSingleton.class));
    assertSame (a, GlobalSingleton.getGlobalSingletonIfInstantiated (MockGlobalSingleton.class));

    assertNotNull (MockGlobalSingleton.getInstance ());
    assertSame (a, MockGlobalSingleton.getInstance ());
  }
}
