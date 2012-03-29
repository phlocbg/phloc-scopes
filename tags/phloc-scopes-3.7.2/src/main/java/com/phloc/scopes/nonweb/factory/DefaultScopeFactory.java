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
package com.phloc.scopes.nonweb.factory;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.scopes.nonweb.domain.IApplicationScope;
import com.phloc.scopes.nonweb.domain.IGlobalScope;
import com.phloc.scopes.nonweb.domain.IRequestScope;
import com.phloc.scopes.nonweb.impl.ApplicationScope;
import com.phloc.scopes.nonweb.impl.GlobalScope;
import com.phloc.scopes.nonweb.impl.RequestScope;

/**
 * Standalone version of the scope factory. No dependencies to Web components.
 * 
 * @author philip
 */
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
  public final IApplicationScope createApplicationScope (@Nonnull @Nonempty final String sScopeID)
  {
    return new ApplicationScope (sScopeID);
  }

  @Nonnull
  public IRequestScope createRequestScope (@Nonnull @Nonempty final String sScopeID)
  {
    return new RequestScope (sScopeID);
  }
}
