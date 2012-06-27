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

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.MustImplementEqualsAndHashcode;
import com.phloc.scopes.AbstractSingleton;
import com.phloc.scopes.nonweb.domain.ISessionScope;
import com.phloc.scopes.nonweb.mgr.ScopeManager;

/**
 * This is the base class for singleton objects that reside in the session web
 * scope.
 * 
 * @see com.phloc.scopes.web.mgr.EWebScope#SESSION
 * @author philip
 */
@MustImplementEqualsAndHashcode
public abstract class SessionSingleton extends AbstractSingleton implements Serializable
{
  protected SessionSingleton ()
  {
    super ("getSessionSingleton");
  }

  @Nonnull
  private static ISessionScope _getStaticScope ()
  {
    return ScopeManager.getSessionScope ();
  }

  @Override
  @Nonnull
  protected final ISessionScope getScope ()
  {
    return _getStaticScope ();
  }

  protected static final boolean isSingletonInstantiated (@Nonnull final Class <? extends SessionSingleton> aClass)
  {
    return isSingletonInstantiated (_getStaticScope (), aClass);
  }

  @Nonnull
  protected static final <T extends SessionSingleton> T getSessionSingleton (@Nonnull final Class <T> aClass)
  {
    return getSingleton (_getStaticScope (), aClass);
  }

  @Nonnull
  public static final List <SessionSingleton> getAllSingletons ()
  {
    return getAllSingletons (_getStaticScope (), SessionSingleton.class);
  }
}
