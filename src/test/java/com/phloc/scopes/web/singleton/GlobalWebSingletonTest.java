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
package com.phloc.scopes.web.singleton;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import javax.annotation.Nonnull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.scopes.nonweb.singleton.GlobalSingleton;
import com.phloc.scopes.web.mock.AbstractWebScopeAwareTestCase;

/**
 * Test class for class {@link GlobalSingleton}.
 * 
 * @author philip
 */
public final class GlobalWebSingletonTest extends AbstractWebScopeAwareTestCase
{
  private static int s_nCtorCount = 0;
  private static int s_nDtorCount = 0;

  /**
   * Default use case example.
   * 
   * @author philip
   */
  public static final class MockGlobalSingleton extends GlobalSingleton
  {
    @Deprecated
    @UsedViaReflection
    public MockGlobalSingleton ()
    {
      s_nCtorCount++;
    }

    @Nonnull
    public static MockGlobalSingleton getInstance ()
    {
      return getGlobalSingleton (MockGlobalSingleton.class);
    }

    @Override
    protected void onDestroy () throws Exception
    {
      s_nDtorCount++;
    }
  }

  @BeforeClass
  public static void beforeClass ()
  {
    assertEquals (0, s_nCtorCount);
    assertEquals (0, s_nDtorCount);
  }

  @AfterClass
  public static void afterClass ()
  {
    assertEquals (1, s_nCtorCount);
    assertEquals (1, s_nDtorCount);
  }

  @Test
  public void testAll ()
  {
    assertNotNull (MockGlobalSingleton.getInstance ());
    assertNotNull (MockGlobalSingleton.getInstance ());
    assertSame (MockGlobalSingleton.getInstance (), MockGlobalSingleton.getInstance ());
  }
}
