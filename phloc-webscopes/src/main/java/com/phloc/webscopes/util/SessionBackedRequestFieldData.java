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
package com.phloc.webscopes.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.scopes.IScope;
import com.phloc.webscopes.mgr.WebScopeManager;

/**
 * A specialized request field that uses a value stored in the session as the
 * default, in case no parameter is passed. If no value is in the session either
 * the hard coded default value is used.
 * 
 * @author philip
 */
public class SessionBackedRequestFieldData extends RequestFieldData
{
  public SessionBackedRequestFieldData (@Nonnull final String sFieldName)
  {
    super (sFieldName);
    _init ();
  }

  public SessionBackedRequestFieldData (@Nonnull final String sFieldName, @Nullable final String sDefaultValue)
  {
    super (sFieldName, sDefaultValue);
    _init ();
  }

  public SessionBackedRequestFieldData (@Nonnull final String sFieldName, final int nDefaultValue)
  {
    super (sFieldName, nDefaultValue);
    _init ();
  }

  @Nonnull
  @Nonempty
  private String _getSessionFieldName ()
  {
    return "$phloc.$requestfield." + getFieldName ();
  }

  private void _init ()
  {
    // get the request method
    final String sRequestValue = super.getRequestValueWithoutDefault ();
    // Allow empty values!
    if (sRequestValue != null)
      WebScopeManager.getSessionScope (true).setAttribute (_getSessionFieldName (), sRequestValue);
  }

  @Override
  public String getDefaultValue ()
  {
    final String sSuperDefaultValue = super.getDefaultValue ();
    final IScope aSessionScope = WebScopeManager.getSessionScope (false);
    return aSessionScope == null ? sSuperDefaultValue : aSessionScope.getAttributeAsString (_getSessionFieldName (),
                                                                                            sSuperDefaultValue);
  }
}
