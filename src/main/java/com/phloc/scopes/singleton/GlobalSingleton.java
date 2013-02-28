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
package com.phloc.scopes.singleton;

import java.util.List;

import javax.annotation.Nonnull;

import com.phloc.scopes.AbstractSingleton;
import com.phloc.scopes.domain.IGlobalScope;
import com.phloc.scopes.mgr.ScopeManager;

/**
 * This is the base class for singleton objects that reside in the global scope.
 * The global scope is identical for web scope and non-web scope applications.
 * 
 * @see com.phloc.scopes.mgr.EScope#GLOBAL
 * @author philip
 */
public abstract class GlobalSingleton extends AbstractSingleton
{
  protected GlobalSingleton ()
  {
    super ("getGlobalSingleton");
  }

  /**
   * @return The scope to be used for this type of singleton.
   */
  @Nonnull
  private static IGlobalScope _getStaticScope (final boolean bMustBePresent)
  {
    return bMustBePresent ? ScopeManager.getGlobalScope () : ScopeManager.getGlobalScopeOrNull ();
  }

  /**
   * @return The scope to be used for this type of singleton.
   */
  @Override
  @Nonnull
  protected final IGlobalScope getScope ()
  {
    return _getStaticScope (true);
  }

  @Nonnull
  protected static final <T extends GlobalSingleton> T getGlobalSingleton (@Nonnull final Class <T> aClass)
  {
    return getSingleton (_getStaticScope (true), aClass);
  }

  public static final boolean isSingletonInstantiated (@Nonnull final Class <? extends GlobalSingleton> aClass)
  {
    return isSingletonInstantiated (_getStaticScope (false), aClass);
  }

  @Nonnull
  public static final List <GlobalSingleton> getAllSingletons ()
  {
    return getAllSingletons (_getStaticScope (false), GlobalSingleton.class);
  }
}