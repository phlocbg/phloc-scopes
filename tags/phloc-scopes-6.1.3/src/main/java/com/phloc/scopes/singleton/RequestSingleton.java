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
   * Get the singleton object in the current request scope, using the passed
   * class. If the singleton is not yet instantiated, a new instance is created.
   * 
   * @param aClass
   *        The class to be used. May not be <code>null</code>. The class must
   *        be public as needs to have a public no-argument constructor.
   * @return The singleton object and never <code>null</code>.
   */
  @Nonnull
  protected static final <T extends RequestSingleton> T getRequestSingleton (@Nonnull final Class <T> aClass)
  {
    return getSingleton (_getStaticScope (true), aClass);
  }

  /**
   * Get the singleton object if it is already instantiated inside the current
   * request scope or <code>null</code> if it is not instantiated.
   * 
   * @param aClass
   *        The class to be checked. May not be <code>null</code>.
   * @return The singleton for the specified class is already instantiated,
   *         <code>null</code> otherwise.
   * @deprecated Use {@link #getRequestSingletonIfInstantiated(Class)} instead
   */
  @Deprecated
  @Nullable
  public static final <T extends RequestSingleton> T getSingletonIfInstantiated (@Nonnull final Class <T> aClass)
  {
    return getRequestSingletonIfInstantiated (aClass);
  }

  /**
   * Get the singleton object if it is already instantiated inside the current
   * request scope or <code>null</code> if it is not instantiated.
   * 
   * @param aClass
   *        The class to be checked. May not be <code>null</code>.
   * @return The singleton for the specified class is already instantiated,
   *         <code>null</code> otherwise.
   */
  @Nullable
  public static final <T extends RequestSingleton> T getRequestSingletonIfInstantiated (@Nonnull final Class <T> aClass)
  {
    return getSingletonIfInstantiated (_getStaticScope (false), aClass);
  }

  /**
   * Check if a singleton is already instantiated inside the current request
   * scope
   * 
   * @param aClass
   *        The class to be checked. May not be <code>null</code>.
   * @return <code>true</code> if the singleton for the specified class is
   *         already instantiated, <code>false</code> otherwise.
   * @deprecated Use {@link #isRequestSingletonInstantiated(Class)} instead
   */
  @Deprecated
  public static final boolean isSingletonInstantiated (@Nonnull final Class <? extends RequestSingleton> aClass)
  {
    return isRequestSingletonInstantiated (aClass);
  }

  /**
   * Check if a singleton is already instantiated inside the current request
   * scope
   * 
   * @param aClass
   *        The class to be checked. May not be <code>null</code>.
   * @return <code>true</code> if the singleton for the specified class is
   *         already instantiated, <code>false</code> otherwise.
   */
  public static final boolean isRequestSingletonInstantiated (@Nonnull final Class <? extends RequestSingleton> aClass)
  {
    return isSingletonInstantiated (_getStaticScope (false), aClass);
  }

  /**
   * Get all instantiated singleton objects registered in the current request
   * scope.
   * 
   * @return A non-<code>null</code> list with all instances of this class in
   *         the current request scope.
   * @deprecated Use {@link #getAllRequestSingletons()} instead
   */
  @Deprecated
  @Nonnull
  public static final List <RequestSingleton> getAllSingletons ()
  {
    return getAllRequestSingletons ();
  }

  /**
   * Get all instantiated singleton objects registered in the current request
   * scope.
   * 
   * @return A non-<code>null</code> list with all instances of this class in
   *         the current request scope.
   */
  @Nonnull
  public static final List <RequestSingleton> getAllRequestSingletons ()
  {
    return getAllSingletons (_getStaticScope (false), RequestSingleton.class);
  }
}
