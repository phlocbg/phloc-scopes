/**
 * Copyright (C) 2006-2014 phloc systems
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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.ValueEnforcer;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.equals.EqualsUtils;
import com.phloc.commons.lang.CGStringHelper;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.scopes.AbstractMapBasedScope;
import com.phloc.scopes.ScopeUtils;
import com.phloc.scopes.domain.IRequestScope;

/**
 * Default implementation for non-web request scopes.
 * 
 * @author Philip Helger
 */
public class RequestScope extends AbstractMapBasedScope implements IRequestScope
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (RequestScope.class);
  private final String m_sSessionID;

  public RequestScope (@Nonnull @Nonempty final String sScopeID, @Nonnull @Nonempty final String sSessionID)
  {
    super (sScopeID);
    m_sSessionID = ValueEnforcer.notEmpty (sSessionID, "SessionID");

    // done initialization
    if (ScopeUtils.debugRequestScopeLifeCycle (s_aLogger))
      s_aLogger.info ("Created request scope '" + sScopeID + "' of class " + CGStringHelper.getClassLocalName (this),
                      ScopeUtils.getDebugStackTrace ());
  }

  @Nonnull
  @Nonempty
  public final String getSessionID ()
  {
    return getSessionID (true);
  }

  @Nonnull
  @Nonempty
  public final String getSessionID (final boolean bCreateIfNotExisting)
  {
    return m_sSessionID;
  }

  public void initScope ()
  {}

  @Override
  protected void postDestroy ()
  {
    if (ScopeUtils.debugRequestScopeLifeCycle (s_aLogger))
      s_aLogger.info ("Destroyed request scope '" + getID () + "' of class " + CGStringHelper.getClassLocalName (this),
                      ScopeUtils.getDebugStackTrace ());
  }

  @Nullable
  public List <String> getAttributeValues (@Nullable final String sName)
  {
    return getAttributeValues (sName, null);
  }

  @Nullable
  public List <String> getAttributeValues (@Nullable final String sName, @Nullable final List <String> aDefault)
  {
    final Object aValue = getAttributeObject (sName);
    if (aValue instanceof String [])
    {
      // multiple values passed in the request
      return ContainerHelper.newList ((String []) aValue);
    }
    if (aValue instanceof String)
    {
      // single value passed in the request
      return ContainerHelper.newList ((String) aValue);
    }
    return aDefault;
  }

  public boolean hasAttributeValue (@Nullable final String sName, @Nullable final String sDesiredValue)
  {
    return EqualsUtils.equals (getAttributeAsString (sName), sDesiredValue);
  }

  public boolean hasAttributeValue (@Nullable final String sName,
                                    @Nullable final String sDesiredValue,
                                    final boolean bDefault)
  {
    final String sValue = getAttributeAsString (sName);
    return sValue == null ? bDefault : EqualsUtils.equals (sValue, sDesiredValue);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("sessionID", m_sSessionID).toString ();
  }
}
