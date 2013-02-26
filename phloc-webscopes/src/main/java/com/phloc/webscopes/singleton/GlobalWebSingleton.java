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
package com.phloc.webscopes.singleton;

import java.util.List;

import javax.annotation.Nonnull;

import com.phloc.scopes.AbstractSingleton;
import com.phloc.webscopes.domain.IGlobalWebScope;
import com.phloc.webscopes.mgr.WebScopeManager;

/**
 * This is the base class for singleton objects that reside in the global scope.
 * The global scope is identical for web scope and non-web scope applications.
 * 
 * @see com.phloc.webscopes.mgr.EWebScope#GLOBAL
 * @author philip
 */
public abstract class GlobalWebSingleton extends AbstractSingleton
{
  protected GlobalWebSingleton ()
  {
    super ("getGlobalSingleton");
  }

  /**
   * @param bMustBePresent
   *        if <code>true</code> the scope must be present, <code>false</code>
   *        if it may be <code>null</code>.
   * @return The scope to be used for this type of singleton.
   */
  @Nonnull
  private static IGlobalWebScope _getStaticScope (final boolean bMustBePresent)
  {
    return bMustBePresent ? WebScopeManager.getGlobalScope () : WebScopeManager.getGlobalScopeOrNull ();
  }

  /**
   * @return The scope to be used for this type of singleton.
   */
  @Override
  @Nonnull
  protected final IGlobalWebScope getScope ()
  {
    return _getStaticScope (true);
  }

  @Nonnull
  protected static final <T extends GlobalWebSingleton> T getGlobalSingleton (@Nonnull final Class <T> aClass)
  {
    return getSingleton (_getStaticScope (true), aClass);
  }

  public static final boolean isSingletonInstantiated (@Nonnull final Class <? extends GlobalWebSingleton> aClass)
  {
    return isSingletonInstantiated (_getStaticScope (false), aClass);
  }

  @Nonnull
  public static final List <GlobalWebSingleton> getAllSingletons ()
  {
    return getAllSingletons (_getStaticScope (false), GlobalWebSingleton.class);
  }
}
