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
package com.phloc.webscopes.domain;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpSession;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.scopes.nonweb.domain.ISessionScope;
import com.phloc.webscopes.IWebScope;

/**
 * Interface for a single session scope object.
 * 
 * @author philip
 */
public interface ISessionWebScope extends ISessionScope, IWebScope
{
  /**
   * Get the underlying HTTP session. Important: do not use it to access the
   * attributes within the session. Use only the scope API for this, so that the
   * synchronization is consistent!
   * 
   * @return The underlying HTTP session. Never <code>null</code>.
   */
  @Nonnull
  HttpSession getSession ();

  /**
   * Returns <code>true</code> if the client does not yet know about the session
   * or if the client chooses not to join the session. For example, if the
   * server used only cookie-based sessions, and the client had disabled the use
   * of cookies, then a session would be new on each request.
   * 
   * @return <code>true</code> if the server has created a session, but the
   *         client has not yet joined
   * @exception IllegalStateException
   *            if this method is called on an already invalidated session
   */
  boolean isNew ();

  /**
   * Returns the maximum time interval, in seconds, that the servlet container
   * will keep this session open between client accesses. After this interval,
   * the servlet container will invalidate the session. The maximum time
   * interval can be set with the <code>setMaxInactiveInterval</code> method. A
   * negative time indicates the session should never timeout.
   * 
   * @return an integer specifying the number of seconds this session remains
   *         open between client requests
   */
  long getMaxInactiveInterval ();

  /**
   * {@inheritDoc}
   */
  @Nullable
  ISessionApplicationWebScope getSessionApplicationScope (@Nonnull @Nonempty String sApplicationID,
                                                          boolean bCreateIfNotExisting);
}
