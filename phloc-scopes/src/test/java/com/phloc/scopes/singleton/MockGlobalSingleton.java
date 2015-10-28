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

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.scopes.singleton.GlobalSingleton;

/**
 * Mock global singleton
 * 
 * @author Philip Helger
 */
public final class MockGlobalSingleton extends GlobalSingleton
{
  static int s_nCtorCount = 0;
  static int s_nDtorCount = 0;

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
