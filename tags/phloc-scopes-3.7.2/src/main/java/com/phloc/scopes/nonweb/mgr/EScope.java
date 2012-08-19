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
package com.phloc.scopes.nonweb.mgr;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.scopes.IScope;

/**
 * This enumeration defines all the possible non-web scopes including some
 * utility methods on it.
 * 
 * @author philip
 */
public enum EScope
{
  /** The global scope. */
  GLOBAL,
  /** The application scope. */
  APPLICATION,
  /** The request scope. */
  REQUEST;

  @Nonnull
  public IScope getScope ()
  {
    return getScope (true);
  }

  @Nullable
  public IScope getScope (final boolean bCreateIfNotExisting)
  {
    return getScope (this, bCreateIfNotExisting);
  }

  /**
   * Resolve the currently matching scope of the given {@link EScope} value.
   * 
   * @param eScope
   *        The scope to resolve to a real scope.
   * @return The matching IScope.
   * @throws IllegalArgumentException
   *         If an illegal enumeration value is passed.
   */
  @Nullable
  public static IScope getScope (@Nonnull final EScope eScope, final boolean bCreateIfNotExisting)
  {
    switch (eScope)
    {
      case GLOBAL:
        return ScopeManager.getGlobalScope ();
      case APPLICATION:
        return ScopeManager.getApplicationScope (bCreateIfNotExisting);
      case REQUEST:
        return ScopeManager.getRequestScope ();
      default:
        throw new IllegalArgumentException ("Unknown scope: " + eScope);
    }
  }
}