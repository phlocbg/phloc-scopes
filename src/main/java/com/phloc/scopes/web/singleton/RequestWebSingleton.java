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
import com.phloc.scopes.web.domain.IRequestWebScope;
import com.phloc.scopes.web.mgr.WebScopeManager;

/**
 * This is the base class for singleton objects that reside in the request
 * scope. This class can be used for web scopes and non-web scopes as it handled
 * in the same object.
 * 
 * @see com.phloc.scopes.web.mgr.EWebScope#REQUEST
 * @author philip
 */
public abstract class RequestWebSingleton extends AbstractSingleton
{
  protected RequestWebSingleton ()
  {
    super ("getRequestSingleton");
  }

  @Nonnull
  private static IRequestWebScope _getStaticScope ()
  {
    return WebScopeManager.getRequestScope ();
  }

  @Override
  @Nonnull
  protected final IRequestWebScope getScope ()
  {
    return _getStaticScope ();
  }

  protected static final boolean isSingletonInstantiated (@Nonnull final Class <? extends RequestWebSingleton> aClass)
  {
    return isSingletonInstantiated (_getStaticScope (), aClass);
  }

  @Nonnull
  protected static final <T extends RequestWebSingleton> T getRequestSingleton (@Nonnull final Class <T> aClass)
  {
    return getSingleton (_getStaticScope (), aClass);
  }

  @Nonnull
  public static final List <RequestWebSingleton> getAllSingletons ()
  {
    return getAllSingletons (_getStaticScope (), RequestWebSingleton.class);
  }
}
