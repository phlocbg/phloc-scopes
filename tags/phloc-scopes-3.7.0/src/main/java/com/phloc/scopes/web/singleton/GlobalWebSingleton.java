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
package com.phloc.scopes.web.singleton;

import java.util.List;

import javax.annotation.Nonnull;

import com.phloc.scopes.AbstractSingleton;
import com.phloc.scopes.web.domain.IGlobalWebScope;
import com.phloc.scopes.web.mgr.WebScopeManager;

/**
 * This is the base class for singleton objects that reside in the global scope.
 * The global scope is identical for web scope and non-web scope applications.
 * 
 * @see com.phloc.scopes.web.mgr.EWebScope#GLOBAL
 * @author philip
 */
public abstract class GlobalWebSingleton extends AbstractSingleton
{
  protected GlobalWebSingleton ()
  {
    super ("getGlobalSingleton");
  }

  @Nonnull
  private static IGlobalWebScope _getMyScope ()
  {
    return WebScopeManager.getGlobalScope ();
  }

  @Override
  @Nonnull
  protected final IGlobalWebScope getScope ()
  {
    return _getMyScope ();
  }

  @Nonnull
  protected static <T extends GlobalWebSingleton> T getGlobalSingleton (@Nonnull final Class <T> aClass)
  {
    return getSingleton (_getMyScope (), aClass);
  }

  @Nonnull
  public static final List <GlobalWebSingleton> getAllSingletons ()
  {
    return getAllSingletons (_getMyScope (), GlobalWebSingleton.class);
  }
}