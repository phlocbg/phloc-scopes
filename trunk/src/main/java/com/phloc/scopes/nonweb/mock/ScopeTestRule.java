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

import java.io.File;

import javax.annotation.Nonnull;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.phloc.commons.annotations.OverrideOnDemand;

public class ScopeTestRule implements TestRule
{
  public static final File STORAGE_PATH = new File ("target/junittest").getAbsoluteFile ();

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
  protected void before () throws Throwable
  {
    ScopeAwareTestSetup.setupScopeTests ();
  }

  @OverrideOnDemand
  protected void after () throws Throwable
  {
    ScopeAwareTestSetup.shutdownScopeTests ();
  }
}
