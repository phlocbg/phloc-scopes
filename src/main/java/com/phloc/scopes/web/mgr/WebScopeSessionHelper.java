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
package com.phloc.scopes.web.mgr;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.state.EChange;
import com.phloc.commons.string.StringHelper;
import com.phloc.scopes.IScopeRenewalAware;
import com.phloc.scopes.nonweb.domain.ISessionApplicationScope;
import com.phloc.scopes.web.domain.ISessionApplicationWebScope;
import com.phloc.scopes.web.domain.ISessionWebScope;

@Immutable
public final class WebScopeSessionHelper
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (WebScopeSessionHelper.class);

  private WebScopeSessionHelper ()
  {}

  @Nonnull
  private static Map <String, Map <String, IScopeRenewalAware>> _getSessionApplicationScopeValues (@Nonnull final ISessionWebScope aOldSessionScope)
  {
    final Map <String, Map <String, IScopeRenewalAware>> aSessionApplicationScopeValues = new HashMap <String, Map <String, IScopeRenewalAware>> ();
    for (final Map.Entry <String, ISessionApplicationScope> aEntry : aOldSessionScope.getAllSessionApplicationScopes ()
                                                                                     .entrySet ())
    {
      // Get all values from the current session application scope
      final Map <String, IScopeRenewalAware> aSurviving = aEntry.getValue ().getAllScopeRenewalAwareAttributes ();
      if (!aSurviving.isEmpty ())
      {
        // Remove the leading session ID
        final String sScopeApplicationID = StringHelper.trimStart (aEntry.getKey (), aOldSessionScope.getID () + '.');
        aSessionApplicationScopeValues.put (sScopeApplicationID, aSurviving);
      }
    }
    return aSessionApplicationScopeValues;
  }

  private static void _restoreScopeAttributes (@Nonnull final ISessionWebScope aNewSessionScope,
                                               @Nonnull final Map <String, IScopeRenewalAware> aSessionScopeValues,
                                               @Nonnull final Map <String, Map <String, IScopeRenewalAware>> aSessionApplicationScopeValues)
  {
    // restore the session scope attributes
    for (final Map.Entry <String, IScopeRenewalAware> aEntry : aSessionScopeValues.entrySet ())
      aNewSessionScope.setAttribute (aEntry.getKey (), aEntry.getValue ());

    // restore the session application scope attributes
    for (final Map.Entry <String, Map <String, IScopeRenewalAware>> aEntry : aSessionApplicationScopeValues.entrySet ())
    {
      // Create the session application scope in the new session scope
      final ISessionApplicationWebScope aNewSessionApplicationScope = aNewSessionScope.getSessionApplicationScope (aEntry.getKey (),
                                                                                                                   true);

      // Put all attributes in
      for (final Map.Entry <String, IScopeRenewalAware> aInnerEntry : aEntry.getValue ().entrySet ())
        aNewSessionApplicationScope.setAttribute (aInnerEntry.getKey (), aInnerEntry.getValue ());
    }
  }

  /**
   * @deprecated Use {@link #renewCurrentSessionScope()} instead
   */
  @Deprecated
  public static void renewSessionScope ()
  {
    renewCurrentSessionScope ();
  }

  @Nonnull
  public static EChange renewCurrentSessionScope ()
  {
    // Get the old session scope
    final ISessionWebScope aOldSessionScope = WebScopeManager.getSessionScope (false);
    if (aOldSessionScope == null)
      return EChange.UNCHANGED;

    // OK, we have a session scope to renew

    // Save all values from session scopes
    final Map <String, IScopeRenewalAware> aSessionScopeValues = aOldSessionScope.getAllScopeRenewalAwareAttributes ();

    // Save all values from all session application scopes
    final Map <String, Map <String, IScopeRenewalAware>> aSessionApplicationScopeValues = _getSessionApplicationScopeValues (aOldSessionScope);

    // renew the session
    try
    {
      s_aLogger.info ("Invalidating session " + aOldSessionScope.getID ());
      aOldSessionScope.getSession ().invalidate ();
    }
    catch (final IllegalStateException ex)
    {
      // session already invalidated???
      s_aLogger.warn ("Failed to invalidate session", ex);
    }

    // Ensure that we get a new session!
    final ISessionWebScope aNewSessionScope = WebScopeManager.getSessionScope (true);

    _restoreScopeAttributes (aNewSessionScope, aSessionScopeValues, aSessionApplicationScopeValues);
    return EChange.CHANGED;
  }
}
