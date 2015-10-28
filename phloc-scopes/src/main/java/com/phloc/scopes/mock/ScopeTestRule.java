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
package com.phloc.scopes.mock;

import java.io.File;

import org.junit.rules.ExternalResource;

import com.phloc.commons.annotations.OverrideOnDemand;

/**
 * Special JUnit test rule to initialize scopes correctly.
 * 
 * @author Philip Helger
 */
public class ScopeTestRule extends ExternalResource
{
  public static final File STORAGE_PATH = ScopeAwareTestSetup.STORAGE_PATH;

  @Override
  @OverrideOnDemand
  public void before ()
  {
    ScopeAwareTestSetup.setupScopeTests ();
  }

  @Override
  @OverrideOnDemand
  public void after ()
  {
    ScopeAwareTestSetup.shutdownScopeTests ();
  }
}
