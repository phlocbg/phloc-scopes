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
package com.phloc.scopes;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.phloc.scopes.nonweb.factory.DefaultScopeFactory;
import com.phloc.scopes.nonweb.factory.IScopeFactory;
import com.phloc.scopes.web.factory.DefaultWebScopeFactory;
import com.phloc.scopes.web.factory.IWebScopeFactory;

/**
 * The meta scope factory holding both the factory for non-web scopes as well as
 * the factory for web-scopes.
 * 
 * @author philip
 */
@NotThreadSafe
public final class MetaScopeFactory
{
  private static IScopeFactory s_aScopeFactory = new DefaultScopeFactory ();
  private static IWebScopeFactory s_aWebScopeFactory = new DefaultWebScopeFactory ();

  private MetaScopeFactory ()
  {}

  /**
   * Set the default non-web scope factory
   * 
   * @param aScopeFactory
   *        The scope factory to use. May not be <code>null</code>.
   */
  public static void setScopeFactory (@Nonnull final IScopeFactory aScopeFactory)
  {
    if (aScopeFactory == null)
      throw new NullPointerException ("scopeFactory");
    s_aScopeFactory = aScopeFactory;
  }

  /**
   * @return The scope factory for non-web scopes. Never <code>null</code>.
   */
  @Nonnull
  public static IScopeFactory getScopeFactory ()
  {
    return s_aScopeFactory;
  }

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
