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

import com.phloc.commons.annotations.MustImplementEqualsAndHashcode;
import com.phloc.scopes.AbstractSerializableSingleton;
import com.phloc.scopes.web.domain.ISessionWebScope;
import com.phloc.scopes.web.mgr.WebScopeManager;

/**
 * This is the base class for singleton objects that reside in the session web
 * scope.
 * 
 * @see com.phloc.scopes.web.mgr.EWebScope#SESSION
 * @author philip
 */
@MustImplementEqualsAndHashcode
public abstract class SessionWebSingleton extends AbstractSerializableSingleton
{
  protected SessionWebSingleton ()
  {
    super ("getSessionSingleton");
  }

  /**
   * @return The scope to be used for this type of singleton.
   */
  @Nonnull
  private static ISessionWebScope _getStaticScope ()
  {
    return WebScopeManager.getSessionScope (true);
  }

  /**
   * @return The scope to be used for this type of singleton.
   */
  @Override
  @Nonnull
  protected final ISessionWebScope getScope ()
  {
    return _getStaticScope ();
  }

  public static final boolean isSingletonInstantiated (@Nonnull final Class <? extends SessionWebSingleton> aClass)
  {
    return isSingletonInstantiated (_getStaticScope (), aClass);
  }

  @Nonnull
  protected static final <T extends SessionWebSingleton> T getSessionSingleton (@Nonnull final Class <T> aClass)
  {
    return getSingleton (_getStaticScope (), aClass);
  }

  @Nonnull
  public static final List <SessionWebSingleton> getAllSingletons ()
  {
    return getAllSingletons (_getStaticScope (), SessionWebSingleton.class);
  }
}
