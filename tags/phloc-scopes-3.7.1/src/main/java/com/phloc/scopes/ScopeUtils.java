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
package com.phloc.scopes;

import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;

/**
 * Global scope utility methods that don't nicely fit somewhere else.
 * 
 * @author philip
 */
@Immutable
public final class ScopeUtils
{
  private static final boolean DEBUG_SCOPE_LIFE_CYCLE = false;

  private ScopeUtils ()
  {}

  /**
   * This is a just a helper method to determine whether scope creation/deletion
   * issues should be logged or not.
   * 
   * @param aLogger
   *        The logger to check.
   * @return <code>true</code> if scope creation/deletion should be logged,
   *         <code>false</code> otherwise.
   */
  public static boolean debugScopeLifeCycle (final Logger aLogger)
  {
    return DEBUG_SCOPE_LIFE_CYCLE && aLogger.isInfoEnabled ();
  }
}
