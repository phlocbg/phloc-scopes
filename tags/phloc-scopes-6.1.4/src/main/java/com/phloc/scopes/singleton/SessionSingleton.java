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

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.MustImplementEqualsAndHashcode;
import com.phloc.scopes.AbstractSingleton;
import com.phloc.scopes.domain.ISessionScope;
import com.phloc.scopes.mgr.ScopeManager;

/**
 * This is the base class for singleton objects that reside in the session
 * non-web scope.
 * 
 * @see com.phloc.scopes.mgr.EScope#SESSION
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public abstract class SessionSingleton extends AbstractSingleton implements Serializable
{
  protected SessionSingleton ()
  {
    super ("getSessionSingleton");
  }

  /**
   * @return The scope to be used for this type of singleton.
   */
  @Nonnull
  private static ISessionScope _getStaticScope (final boolean bCreateIfNotExisting)
  {
    return ScopeManager.getSessionScope (bCreateIfNotExisting);
  }

  /**
   * Get the singleton object in the current session scope, using the passed
   * class. If the singleton is not yet instantiated, a new instance is created.
   * 
   * @param aClass
   *        The class to be used. May not be <code>null</code>. The class must
   *        be public as needs to have a public no-argument constructor.
   * @return The singleton object and never <code>null</code>.
   */
  @Nonnull
  protected static final <T extends SessionSingleton> T getSessionSingleton (@Nonnull final Class <T> aClass)
  {
    return getSingleton (_getStaticScope (true), aClass);
  }

  /**
   * Get the singleton object if it is already instantiated inside the current
   * session scope or <code>null</code> if it is not instantiated.
   * 
   * @param aClass
   *        The class to be checked. May not be <code>null</code>.
   * @return The singleton for the specified class is already instantiated,
   *         <code>null</code> otherwise.
   * @deprecated Use {@link #getSessionSingletonIfInstantiated(Class)} instead
   */
  @Deprecated
  @Nullable
  public static final <T extends SessionSingleton> T getSingletonIfInstantiated (@Nonnull final Class <T> aClass)
  {
    return getSessionSingletonIfInstantiated (aClass);
  }

  /**
   * Get the singleton object if it is already instantiated inside the current
   * session scope or <code>null</code> if it is not instantiated.
   * 
   * @param aClass
   *        The class to be checked. May not be <code>null</code>.
   * @return The singleton for the specified class is already instantiated,
   *         <code>null</code> otherwise.
   */
  @Nullable
  public static final <T extends SessionSingleton> T getSessionSingletonIfInstantiated (@Nonnull final Class <T> aClass)
  {
    return getSingletonIfInstantiated (_getStaticScope (false), aClass);
  }

  /**
   * Check if a singleton is already instantiated inside the current session
   * scope
   * 
   * @param aClass
   *        The class to be checked. May not be <code>null</code>.
   * @return <code>true</code> if the singleton for the specified class is
   *         already instantiated, <code>false</code> otherwise.
   * @deprecated Use {@link #isSessionSingletonInstantiated(Class)} instead
   */
  @Deprecated
  public static final boolean isSingletonInstantiated (@Nonnull final Class <? extends SessionSingleton> aClass)
  {
    return isSessionSingletonInstantiated (aClass);
  }

  /**
   * Check if a singleton is already instantiated inside the current session
   * scope
   * 
   * @param aClass
   *        The class to be checked. May not be <code>null</code>.
   * @return <code>true</code> if the singleton for the specified class is
   *         already instantiated, <code>false</code> otherwise.
   */
  public static final boolean isSessionSingletonInstantiated (@Nonnull final Class <? extends SessionSingleton> aClass)
  {
    return isSingletonInstantiated (_getStaticScope (false), aClass);
  }

  /**
   * Get all instantiated singleton objects registered in the current session
   * scope.
   * 
   * @return A non-<code>null</code> list with all instances of this class in
   *         the current session scope.
   * @deprecated Use {@link #getAllSessionSingletons()} instead
   */
  @Deprecated
  @Nonnull
  public static final List <SessionSingleton> getAllSingletons ()
  {
    return getAllSessionSingletons ();
  }

  /**
   * Get all instantiated singleton objects registered in the current session
   * scope.
   * 
   * @return A non-<code>null</code> list with all instances of this class in
   *         the current session scope.
   */
  @Nonnull
  public static final List <SessionSingleton> getAllSessionSingletons ()
  {
    return getAllSingletons (_getStaticScope (false), SessionSingleton.class);
  }
}
