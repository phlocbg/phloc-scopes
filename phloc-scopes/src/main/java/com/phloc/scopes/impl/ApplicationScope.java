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
package com.phloc.scopes.impl;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.lang.CGStringHelper;
import com.phloc.scopes.AbstractMapBasedScope;
import com.phloc.scopes.ScopeUtils;
import com.phloc.scopes.domain.IApplicationScope;

/**
 * Represents a single "application scope".
 * 
 * @author Philip Helger
 */
@ThreadSafe
public class ApplicationScope extends AbstractMapBasedScope implements IApplicationScope
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (ApplicationScope.class);

  /**
   * Create a new application web scope with the given ID.
   * 
   * @param sScopeID
   *        The scope ID to be used. May neither be <code>null</code> nor empty.
   */
  public ApplicationScope (@Nonnull @Nonempty final String sScopeID)
  {
    super (sScopeID);

    if (ScopeUtils.debugApplicationScopeLifeCycle (s_aLogger))
      s_aLogger.info ("Created application scope '" +
                          sScopeID +
                          "' of class " +
                          CGStringHelper.getClassLocalName (this),
                      ScopeUtils.getDebugStackTrace ());
  }

  public void initScope ()
  {}

  @Override
  protected void postDestroy ()
  {
    if (ScopeUtils.debugApplicationScopeLifeCycle (s_aLogger))
      s_aLogger.info ("Destroyed application scope '" +
                          getID () +
                          "' of class " +
                          CGStringHelper.getClassLocalName (this),
                      ScopeUtils.getDebugStackTrace ());
  }
}
