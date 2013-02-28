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
import javax.annotation.Nullable;

import com.phloc.scopes.AbstractSingleton;
import com.phloc.scopes.domain.IRequestScope;
import com.phloc.scopes.mgr.ScopeManager;

/**
 * This is the base class for singleton objects that reside in the request
 * scope. This class can be used for web scopes and non-web scopes as it handled
 * in the same object.
 * 
 * @see com.phloc.scopes.mgr.EScope#REQUEST
 * @author philip
 */
public abstract class RequestSingleton extends AbstractSingleton
{
  protected RequestSingleton ()
  {
    super ("getRequestSingleton");
  }

  /**
   * @return The scope to be used for this type of singleton.
   */
  @Nullable
  private static IRequestScope _getStaticScope (final boolean bMustBePresent)
  {
    return bMustBePresent ? ScopeManager.getRequestScope () : ScopeManager.getRequestScopeOrNull ();
  }

  /**
   * @return The scope to be used for this type of singleton.
   */
  @Override
  @Nonnull
  protected final IRequestScope getScope ()
  {
    return _getStaticScope (true);
  }

  @Nonnull
  protected static final <T extends RequestSingleton> T getRequestSingleton (@Nonnull final Class <T> aClass)
  {
    return getSingleton (_getStaticScope (true), aClass);
  }

  public static final boolean isSingletonInstantiated (@Nonnull final Class <? extends RequestSingleton> aClass)
  {
    return isSingletonInstantiated (_getStaticScope (false), aClass);
  }

  @Nonnull
  public static final List <RequestSingleton> getAllSingletons ()
  {
    return getAllSingletons (_getStaticScope (false), RequestSingleton.class);
  }
}
