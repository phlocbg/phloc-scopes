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
package com.phloc.scopes.factory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.scopes.domain.IApplicationScope;
import com.phloc.scopes.domain.IGlobalScope;
import com.phloc.scopes.domain.IRequestScope;
import com.phloc.scopes.domain.ISessionApplicationScope;
import com.phloc.scopes.domain.ISessionScope;
import com.phloc.scopes.impl.ApplicationScope;
import com.phloc.scopes.impl.GlobalScope;
import com.phloc.scopes.impl.RequestScope;
import com.phloc.scopes.impl.SessionApplicationScope;
import com.phloc.scopes.impl.SessionScope;

/**
 * Standalone version of the scope factory. No dependencies to Web components.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public class DefaultScopeFactory implements IScopeFactory
{
  public DefaultScopeFactory ()
  {}

  @Nonnull
  public IGlobalScope createGlobalScope (@Nonnull @Nonempty final String sScopeID)
  {
    return new GlobalScope (sScopeID);
  }

  @Nonnull
  public IApplicationScope createApplicationScope (@Nonnull @Nonempty final String sScopeID)
  {
    return new ApplicationScope (sScopeID);
  }

  @Nonnull
  public ISessionScope createSessionScope (@Nonnull @Nonempty final String sScopeID)
  {
    return new SessionScope (sScopeID);
  }

  @Nonnull
  public ISessionApplicationScope createSessionApplicationScope (@Nonnull @Nonempty final String sScopeID)
  {
    return new SessionApplicationScope (sScopeID);
  }

  @Nonnull
  public IRequestScope createRequestScope (@Nonnull @Nonempty final String sScopeID,
                                           @Nonnull @Nonempty final String sSessionID)
  {
    return new RequestScope (sScopeID, sSessionID);
  }
}
