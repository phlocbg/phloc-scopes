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
import javax.annotation.Nullable;

import com.phloc.scopes.AbstractSingleton;
import com.phloc.webscopes.domain.IApplicationWebScope;
import com.phloc.webscopes.mgr.WebScopeManager;

/**
 * This is the base class for singleton objects that reside in the application
 * scope. This is the same for web scopes and non-web scopes, as application
 * scopes are managed in the global scope which is also identical for web scopes
 * and non-web scopes.
 * 
 * @see com.phloc.webscopes.mgr.EWebScope#APPLICATION
 * @author philip
 */
public abstract class ApplicationWebSingleton extends AbstractSingleton
{
  protected ApplicationWebSingleton ()
  {
    super ("getApplicationSingleton");
  }

  /**
   * @param bCreateIfNotExisting
   *        if <code>true</code> the scope will be created if it is not existing
   * @return The scope to be used for this type of singleton.
   */
  @Nullable
  private static IApplicationWebScope _getStaticScope (final boolean bCreateIfNotExisting)
  {
    return WebScopeManager.getApplicationScope (bCreateIfNotExisting);
  }

  /**
   * @return The scope to be used for this type of singleton.
   */
  @Override
  @Nonnull
  protected final IApplicationWebScope getScope ()
  {
    return _getStaticScope (true);
  }

  @Nonnull
  protected static final <T extends ApplicationWebSingleton> T getApplicationSingleton (@Nonnull final Class <T> aClass)
  {
    return getSingleton (_getStaticScope (true), aClass);
  }

  public static final boolean isSingletonInstantiated (@Nonnull final Class <? extends ApplicationWebSingleton> aClass)
  {
    return isSingletonInstantiated (_getStaticScope (false), aClass);
  }

  @Nonnull
  public static final List <ApplicationWebSingleton> getAllSingletons ()
  {
    return getAllSingletons (_getStaticScope (false), ApplicationWebSingleton.class);
  }
}
