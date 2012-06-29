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
package com.phloc.scopes.web.impl;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.state.EChange;
import com.phloc.scopes.AbstractMapBasedScope;
import com.phloc.scopes.ScopeUtils;
import com.phloc.scopes.web.domain.ISessionApplicationWebScope;

/**
 * Represents a single "session application scope". This is a scope that is
 * specific to the selected application within the global scope and to the
 * current user session.
 * 
 * @author philip
 */
@ThreadSafe
public class SessionApplicationWebScope extends AbstractMapBasedScope implements ISessionApplicationWebScope
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (SessionApplicationWebScope.class);

  public SessionApplicationWebScope (@Nonnull @Nonempty final String sScopeID)
  {
    super (sScopeID);

    if (ScopeUtils.debugScopeLifeCycle (s_aLogger))
      s_aLogger.info ("Created session application web scope '" + sScopeID + "'");
  }

  public void initScope ()
  {}

  @Override
  @Nonnull
  public EChange setAttribute (@Nonnull final String sName, @Nullable final Object aNewValueValue)
  {
    if (aNewValueValue != null && !(aNewValueValue instanceof Serializable))
      s_aLogger.warn ("Value of class " + aNewValueValue.getClass ().getName () + " should implement Serializable!");

    return super.setAttribute (sName, aNewValueValue);
  }

  @Override
  protected void postDestroy ()
  {
    if (ScopeUtils.debugScopeLifeCycle (s_aLogger))
      s_aLogger.info ("Destroyed session application web scope '" + getID () + "'");
  }
}
