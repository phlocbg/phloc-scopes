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
import com.phloc.scopes.nonweb.domain.IApplicationScope;
import com.phloc.scopes.nonweb.mgr.ScopeManager;

/**
 * This is the base class for singleton objects that reside in the application
 * scope. This is the same for web scopes and non-web scopes, as application
 * scopes are managed in the global scope which is also identical for web scopes
 * and non-web scopes.
 * 
 * @see com.phloc.scopes.nonweb.mgr.EScope#APPLICATION
 * @author philip
 */
public abstract class ApplicationSingleton extends AbstractSingleton
{
  protected ApplicationSingleton ()
  {
    super ("getApplicationSingleton");
  }

  @Nonnull
  private static IApplicationScope _getStaticScope ()
  {
    return ScopeManager.getApplicationScope (true);
  }

  @Override
  @Nonnull
  protected final IApplicationScope getScope ()
  {
    return _getStaticScope ();
  }

  protected static final boolean isSingletonInstantiated (@Nonnull final Class <? extends ApplicationSingleton> aClass)
  {
    return isSingletonInstantiated (_getStaticScope (), aClass);
  }

  @Nonnull
  protected static final <T extends ApplicationSingleton> T getApplicationSingleton (@Nonnull final Class <T> aClass)
  {
    return getSingleton (_getStaticScope (), aClass);
  }

  @Nonnull
  public static final List <ApplicationSingleton> getAllSingletons ()
  {
    return getAllSingletons (_getStaticScope (), ApplicationSingleton.class);
  }
}
