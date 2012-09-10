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
package com.phloc.scopes.nonweb.domain;

import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.state.EContinue;
import com.phloc.scopes.IScope;

/**
 * Interface for a single session scope object.
 * 
 * @author philip
 */
public interface ISessionScope extends IScope
{
  /**
   * A special internal method that destroys the session. This is especially
   * relevant for session web scope, because it is all done via the invalidation
   * of the underlying HTTP session.
   * 
   * @return {@link EContinue#BREAK} to indicate that the regular destruction
   *         should not be performed!
   */
  @Nonnull
  EContinue selfDestruct ();

  /**
   * Create an application specific scope within the session.
   * 
   * @param sApplicationID
   *        The application ID to use. May not be <code>null</code>.
   * @param bCreateIfNotExisting
   *        Create the session application scope if does not yet exist. If
   *        <code>false</code> and the scope does not exist than
   *        <code>null</code> is returned.
   * @return <code>null</code> if bCreateIfNotExisting is <code>false</code> and
   *         the scope is not present
   */
  @Nullable
  ISessionApplicationScope getSessionApplicationScope (@Nonnull @Nonempty String sApplicationID,
                                                       boolean bCreateIfNotExisting);

  /**
   * Restore a persisted session application scope
   * 
   * @param sScopeID
   *        The ID of the restored application scope. May neither be
   *        <code>null</code> nor empty.
   * @param aScope
   *        The scope to be restored. May not be <code>null</code>.
   */
  void restoreSessionApplicationScope (@Nonnull @Nonempty String sScopeID, @Nonnull ISessionApplicationScope aScope);

  /**
   * @return A non-<code>null</code> map with all available session application
   *         scopes. The key is the application ID and the value is the scope.
   */
  @Nonnull
  Map <String, ISessionApplicationScope> getAllSessionApplicationScopes ();

  /**
   * @return The number of contained session application scopes. Always &ge; 0.
   */
  @Nonnegative
  int getSessionApplicationScopeCount ();
}
