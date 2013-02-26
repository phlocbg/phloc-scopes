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
package com.phloc.webscopes;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.phloc.webscopes.factory.DefaultWebScopeFactory;
import com.phloc.webscopes.factory.IWebScopeFactory;

/**
 * The meta scope factory holding both the factory for non-web scopes as well as
 * the factory for web-scopes.
 * 
 * @author philip
 */
@NotThreadSafe
public final class MetaWebScopeFactory
{
  private static IWebScopeFactory s_aWebScopeFactory = new DefaultWebScopeFactory ();

  private MetaWebScopeFactory ()
  {}

  /**
   * Set the default web scope factory
   * 
   * @param aWebScopeFactory
   *        The scope factory to use. May not be <code>null</code>.
   */
  public static void setWebScopeFactory (@Nonnull final IWebScopeFactory aWebScopeFactory)
  {
    if (aWebScopeFactory == null)
      throw new NullPointerException ("webScopeFactory");
    s_aWebScopeFactory = aWebScopeFactory;
  }

  /**
   * @return The scope factory for web scopes. Never <code>null</code>.
   */
  @Nonnull
  public static IWebScopeFactory getWebScopeFactory ()
  {
    return s_aWebScopeFactory;
  }
}
