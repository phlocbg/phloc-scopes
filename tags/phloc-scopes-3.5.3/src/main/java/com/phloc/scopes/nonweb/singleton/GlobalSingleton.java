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
package com.phloc.scopes.nonweb.singleton;

import java.util.List;

import javax.annotation.Nonnull;

import com.phloc.scopes.AbstractSingleton;
import com.phloc.scopes.nonweb.domain.IGlobalScope;
import com.phloc.scopes.nonweb.mgr.ScopeManager;

/**
 * This is the base class for singleton objects that reside in the global scope.
 * The global scope is identical for web scope and non-web scope applications.
 * 
 * @see com.phloc.scopes.nonweb.mgr.EScope#GLOBAL
 * @author philip
 */
public abstract class GlobalSingleton extends AbstractSingleton
{
  protected GlobalSingleton ()
  {
    super ("getGlobalSingleton");
  }

  @Nonnull
  private static IGlobalScope _getMyScope ()
  {
    return ScopeManager.getGlobalScope ();
  }

  @Override
  @Nonnull
  protected final IGlobalScope getScope ()
  {
    return _getMyScope ();
  }

  @Nonnull
  protected static <T extends GlobalSingleton> T getGlobalSingleton (@Nonnull final Class <T> aClass)
  {
    return getSingleton (_getMyScope (), aClass);
  }

  @Nonnull
  public static final List <GlobalSingleton> getAllSingletons ()
  {
    return getAllSingletons (_getMyScope (), GlobalSingleton.class);
  }
}
