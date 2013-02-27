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
package com.phloc.scopes.nonweb.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.phloc.commons.mutable.MutableBoolean;
import com.phloc.scopes.IScopeDestructionAware;

/**
 * Test class for class {@link GlobalScope}.
 * 
 * @author philip
 */
public final class GlobalScopeTest
{
  @Test
  public void testAll ()
  {
    final GlobalScope aGS = new GlobalScope ("test");
    assertEquals ("test", aGS.getID ());
    assertEquals (0, aGS.getAttributeCount ());
    assertTrue (aGS.setAttribute ("key1", "whatsoever").isChanged ());
    assertEquals (1, aGS.getAttributeCount ());
    final MutableBoolean aDestroyed = new MutableBoolean (false);
    assertTrue (aGS.setAttribute ("key2", new IScopeDestructionAware ()
    {
      public void onScopeDestruction () throws Exception
      {
        // Mark as destroyed
        aDestroyed.set (true);
      }
    }).isChanged ());
    assertEquals (2, aGS.getAttributeCount ());
    // Check null value - no change
    assertTrue (aGS.setAttribute ("key3", null).isUnchanged ());
    assertEquals (2, aGS.getAttributeCount ());
    assertTrue (aGS.isValid ());
    assertFalse (aGS.isInDestruction ());
    assertFalse (aGS.isDestroyed ());

    // destroy scope
    aGS.destroyScope ();

    assertFalse (aGS.isValid ());
    assertFalse (aGS.isInDestruction ());
    assertTrue (aGS.isDestroyed ());

    // Did the scope destruction aware class trigger?
    assertTrue (aDestroyed.booleanValue ());
  }
}
