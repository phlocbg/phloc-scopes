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
package com.phloc.scopes.web.mgr;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.scopes.IWebScope;

/**
 * This enumeration defines all the possible web scopes including some utility
 * methods on it.
 * 
 * @author philip
 */
public enum EWebScope
{
  /** The global scope. */
  GLOBAL,
  /** The application scope. */
  APPLICATION,
  /** The session scope */
  SESSION,
  /** The session application scope */
  SESSION_APPLICATION,
  /** The request scope. */
  REQUEST;

  @Nonnull
  public IWebScope getScope ()
  {
    return getScope (true);
  }

  @Nullable
  public IWebScope getScope (final boolean bCreateIfNotExisting)
  {
    return getScope (this, bCreateIfNotExisting);
  }

  /**
   * Resolve the currently matching web scope of the given {@link EWebScope}
   * value.
   * 
   * @param eScope
   *        The scope to resolve to a real scope.
   * @return The matching IScope.
   * @throws IllegalArgumentException
   *         If an illegal enumeration value is passed.
   */
  @Nullable
  public static IWebScope getScope (@Nonnull final EWebScope eScope, final boolean bCreateIfNotExisting)
  {
    switch (eScope)
    {
      case GLOBAL:
        return WebScopeManager.getGlobalScope ();
      case APPLICATION:
        return WebScopeManager.getApplicationScope (bCreateIfNotExisting);
      case SESSION:
        return WebScopeManager.getSessionScope (bCreateIfNotExisting);
      case SESSION_APPLICATION:
        return WebScopeManager.getSessionApplicationScope (bCreateIfNotExisting);
      case REQUEST:
        return WebScopeManager.getRequestScope ();
      default:
        throw new IllegalArgumentException ("Unknown scope: " + eScope);
    }
  }
}
