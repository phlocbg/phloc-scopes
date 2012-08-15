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
package com.phloc.scopes.nonweb.mock;

import javax.annotation.Nonnull;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.phloc.commons.annotations.OverrideOnDemand;
import com.phloc.commons.string.ToStringGenerator;

/**
 * Abstract JUnit rule for before and after a test handing.
 * 
 * @author philip
 */
// FIXME use version from phloc-commons in > 3.7.2
public abstract class AbstractBeforeAfterTestRule implements TestRule
{
  @Nonnull
  public final Statement apply (@Nonnull final Statement aBase, final Description aDescription)
  {
    return new Statement ()
    {
      @Override
      public void evaluate () throws Throwable
      {
        before ();
        try
        {
          aBase.evaluate ();
        }
        finally
        {
          after ();
        }
      }
    };
  }

  @OverrideOnDemand
  protected abstract void before () throws Throwable;

  @OverrideOnDemand
  protected abstract void after () throws Throwable;

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).toString ();
  }
}
