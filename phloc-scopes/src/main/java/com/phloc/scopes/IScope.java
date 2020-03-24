/**
 * Copyright (C) 2006-2015 phloc systems
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
package com.phloc.scopes;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.callback.INonThrowingCallableWithParameter;
import com.phloc.commons.callback.INonThrowingRunnableWithParameter;
import com.phloc.commons.collections.attrs.IAttributeContainer;
import com.phloc.commons.id.IHasID;

/**
 * This interface is used for all the common stuff of a scope. The following
 * types of scopes are present:
 * <ul>
 * <li>Global scope - once and only once</li>
 * <li>Application context - scope for an application (e.g. pDAF3 Config and
 * View application)</li>
 * <li>Session scope - for each user created session</li>
 * <li>Session application context - scope for an application within a
 * session</li>
 * <li>Request scope - for each user request</li>
 * </ul>
 * <br>
 * IMPORTANT: implementations of {@link IScope} must be thread safe!
 * 
 * @author Boris Gregorcic
 */
public interface IScope extends IAttributeContainer, IHasID <String>
{
  /**
   * Init the scope. In contrast to the constructor of a scope, this happens
   * after the scope has been registered in the scope manager.
   */
  void initScope ();

  /**
   * Get the ID of this scope. Each scope retrieves a unique ID within its type
   * of scope (request, session, application). This method needs to be callable
   * anytime and should not throw any exception!
   * 
   * @return the non-null ID of this context.
   */
  @Override
  String getID ();

  /**
   * @return <code>true</code> if this scope is neither in destruction nor
   *         destroyed.
   * @see #isInDestruction()
   * @see #isDestroyed()
   */
  boolean isValid ();

  /**
   * @return <code>true</code> if the scope is currently in the process of
   *         destruction.
   */
  boolean isInDestruction ();

  /**
   * @return <code>true</code> if the scope was already destroyed. This is
   *         especially important for long running scopes.
   */
  boolean isDestroyed ();

  /**
   * Destroys the scopes and all child scopes. This method is automatically
   * called, when a scope goes out of scope :)
   */
  void destroyScope ();

  /**
   * Perform stuff as a single action. All actions are executed in a write-lock!
   * 
   * @param aRunnable
   *        The action to be executed. May not be <code>null</code>.
   */
  void runAtomic (@Nonnull INonThrowingRunnableWithParameter <IScope> aRunnable);

  /**
   * Perform stuff as a single action. All actions are executed in a write-lock!
   * 
   * @param <T>
   *        The return type of the passed callback
   * @param aCallable
   *        The action to be executed. May not be <code>null</code>.
   * @return The result from the callback. May be <code>null</code>.
   */
  @Nullable
  <T> T runAtomic (@Nonnull INonThrowingCallableWithParameter <T, IScope> aCallable);

  /**
   * @return The non-<code>null</code> map with all contained attributes that
   *         implement the {@link IScopeRenewalAware} interface. May be empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  Map <String, IScopeRenewalAware> getAllScopeRenewalAwareAttributes ();
}
