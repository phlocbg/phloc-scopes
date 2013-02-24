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
package com.phloc.scopes.web.singleton;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.commons.hash.HashCodeGenerator;

public final class MockSessionWebSingleton extends SessionWebSingleton
{
  private int i = 0;

  @Deprecated
  @UsedViaReflection
  public MockSessionWebSingleton ()
  {}

  @Nonnull
  public static MockSessionWebSingleton getInstance ()
  {
    return getSessionSingleton (MockSessionWebSingleton.class);
  }

  public void inc ()
  {
    i++;
  }

  public int get ()
  {
    return i;
  }

  // For testing!
  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof MockSessionWebSingleton))
      return false;
    return i == ((MockSessionWebSingleton) o).i;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (i).getHashCode ();
  }
}
