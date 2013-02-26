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

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nonnull;

import com.phloc.scopes.AbstractSingleton;
import com.phloc.webscopes.domain.ISessionApplicationWebScope;
import com.phloc.webscopes.mgr.WebScopeManager;

/**
 * This is the base class for singleton objects that reside in the
 * session-application scope.
 * 
 * @see com.phloc.webscopes.mgr.EWebScope#SESSION_APPLICATION
 * @author philip
 */
public abstract class SessionApplicationWebSingleton extends AbstractSingleton implements Serializable
{
  protected SessionApplicationWebSingleton ()
  {
    super ("getSessionApplicationSingleton");
  }

  /**
   * @param bCreateIfNotExisting
   *        if <code>true</code> the scope will be created if it is not existing
   * @return The scope to be used for this type of singleton.
   */
  @Nonnull
  private static ISessionApplicationWebScope _getStaticScope (final boolean bCreateIfNotExisting)
  {
    return WebScopeManager.getSessionApplicationScope (bCreateIfNotExisting);
  }

  /**
   * @return The scope to be used for this type of singleton.
   */
  @Override
  @Nonnull
  protected final ISessionApplicationWebScope getScope ()
  {
    return _getStaticScope (true);
  }

  @Nonnull
  protected static final <T extends SessionApplicationWebSingleton> T getSessionApplicationSingleton (@Nonnull final Class <T> aClass)
  {
    return getSingleton (_getStaticScope (true), aClass);
  }

  public static final boolean isSingletonInstantiated (@Nonnull final Class <? extends SessionApplicationWebSingleton> aClass)
  {
    return isSingletonInstantiated (_getStaticScope (false), aClass);
  }

  @Nonnull
  public static final List <SessionApplicationWebSingleton> getAllSingletons ()
  {
    return getAllSingletons (_getStaticScope (false), SessionApplicationWebSingleton.class);
  }
}
