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
package com.phloc.scopes.singleton.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.phloc.scopes.mock.ScopeTestRule;
import com.phloc.scopes.singleton.ApplicationSingleton;

/**
 * Test class for class {@link ApplicationSingletonTreeWithUniqueID}.
 * 
 * @author philip
 */
public final class ApplicationSingletonTreeWithUniqueIDTest
{
  @Rule
  public final TestRule m_aScopeRule = new ScopeTestRule ();

  @Test
  public void testBasic () throws Exception
  {
    assertTrue (ApplicationSingleton.getAllSingletons ().isEmpty ());
    assertFalse (ApplicationSingleton.isSingletonInstantiated (MockApplicationSingletonTreeWithUniqueID.class));
    assertNull (ApplicationSingleton.getSingletonIfInstantiated (MockApplicationSingletonTreeWithUniqueID.class));

    final MockApplicationSingletonTreeWithUniqueID a = MockApplicationSingletonTreeWithUniqueID.getInstance ();
    assertTrue (ApplicationSingleton.isSingletonInstantiated (MockApplicationSingletonTreeWithUniqueID.class));
    assertSame (a, ApplicationSingleton.getSingletonIfInstantiated (MockApplicationSingletonTreeWithUniqueID.class));
    assertEquals (0, a.get ());
    a.inc ();
    assertEquals (1, a.get ());

    final MockApplicationSingletonTreeWithUniqueID b = MockApplicationSingletonTreeWithUniqueID.getInstance ();
    assertSame (a, b);
  }
}
