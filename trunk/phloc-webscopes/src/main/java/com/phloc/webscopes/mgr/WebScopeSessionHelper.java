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
package com.phloc.webscopes.mgr;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.state.EChange;
import com.phloc.commons.string.StringHelper;
import com.phloc.scopes.IScopeRenewalAware;
import com.phloc.scopes.nonweb.domain.ISessionApplicationScope;
import com.phloc.scopes.nonweb.mgr.ScopeSessionManager;
import com.phloc.webscopes.domain.ISessionApplicationWebScope;
import com.phloc.webscopes.domain.ISessionWebScope;

/**
 * Some utility methods to handle complex actions in session scopes.
 * 
 * @author philip
 */
@Immutable
public final class WebScopeSessionHelper
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (WebScopeSessionHelper.class);

  private WebScopeSessionHelper ()
  {}

  @Nonnull
  @ReturnsMutableCopy
  private static Map <String, Map <String, IScopeRenewalAware>> _getSessionApplicationScopeValues (@Nonnull final ISessionWebScope aOldSessionScope)
  {
    final Map <String, Map <String, IScopeRenewalAware>> aSessionApplicationScopeValues = new HashMap <String, Map <String, IScopeRenewalAware>> ();
    final Map <String, ISessionApplicationScope> aAllSessionApplicationScopes = aOldSessionScope.getAllSessionApplicationScopes ();
    if (!aAllSessionApplicationScopes.isEmpty ())
    {
      final String sPrefixToSkip = aOldSessionScope.getID () + '.';

      // For all existing session application scopes in the session scope
      for (final Map.Entry <String, ISessionApplicationScope> aEntry : aAllSessionApplicationScopes.entrySet ())
      {
        // Get all values from the current session application scope
        final Map <String, IScopeRenewalAware> aSurviving = aEntry.getValue ().getAllScopeRenewalAwareAttributes ();
        if (!aSurviving.isEmpty ())
        {
          // Remove the leading session ID
          final String sScopeApplicationID = StringHelper.trimStart (aEntry.getKey (), sPrefixToSkip);
          aSessionApplicationScopeValues.put (sScopeApplicationID, aSurviving);
        }
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
   * Renew the current session scope. This means all session and session
   * application scopes are cleared, and only attributes implementing the
   * {@link IScopeRenewalAware} interface are kept.
   * 
   * @param bInvalidateHttpSession
   *        if <code>true</code> the underlying HTTP session is also invalidated
   *        and a new session is created.
   * @return {@link EChange#UNCHANGED} if no session scope is present.
   */
  @Nonnull
  public static EChange renewCurrentSessionScope (final boolean bInvalidateHttpSession)
  {
    // Get the old session scope
    final ISessionWebScope aOldSessionScope = WebScopeManager.getSessionScope (false);
    if (aOldSessionScope == null)
      return EChange.UNCHANGED;

    // OK, we have a session scope to renew

    // Save all values from session scopes and from all session application
    // scopes
    final Map <String, IScopeRenewalAware> aSessionScopeValues = aOldSessionScope.getAllScopeRenewalAwareAttributes ();
    final Map <String, Map <String, IScopeRenewalAware>> aSessionApplicationScopeValues = _getSessionApplicationScopeValues (aOldSessionScope);

    // Clear the old the session scope
    if (bInvalidateHttpSession)
    {
      // renew the session
      s_aLogger.info ("Invalidating session " + aOldSessionScope.getID ());
      aOldSessionScope.selfDestruct ();
    }
    else
    {
      // Do not invalidate the underlying session - only renew the session scope
      // itself
      ScopeSessionManager.getInstance ().onScopeEnd (aOldSessionScope);
    }

    // Ensure that we get a new session!
    // Here it is OK to create a new session scope explicitly!
    final ISessionWebScope aNewSessionScope = WebScopeManager.internalGetSessionScope (true, true);
    _restoreScopeAttributes (aNewSessionScope, aSessionScopeValues, aSessionApplicationScopeValues);
    return EChange.CHANGED;
  }

  /**
   * Renew the session scope identified by the passed HTTP session. Note: the
   * underlying HTTP session is not invalidate, because we have no way to
   * retrieve a new underlying HTTP session, because no request is present.
   * 
   * @param aHttpSession
   *        The HTTP session to be renewed.
   * @return <code>null</code> if nothing was changed, the new session web scope
   *         otherwise.
   */
  @Nullable
  public static ISessionWebScope renewSessionScope (@Nonnull final HttpSession aHttpSession)
  {
    if (aHttpSession == null)
      throw new NullPointerException ("httpSession");

    // Get the old session scope
    final ISessionWebScope aOldSessionScope = WebScopeManager.internalGetOrCreateSessionScope (aHttpSession,
                                                                                               false,
                                                                                               false);
    if (aOldSessionScope == null)
      return null;

    // OK, we have a session scope to renew

    // Save all values from session scopes and from all session application
    // scopes
    final Map <String, IScopeRenewalAware> aSessionScopeValues = aOldSessionScope.getAllScopeRenewalAwareAttributes ();
    final Map <String, Map <String, IScopeRenewalAware>> aSessionApplicationScopeValues = _getSessionApplicationScopeValues (aOldSessionScope);

    // Do not invalidate the underlying session - only renew the session scope
    // itself because we don't have the possibility to create a new HTTP
    // session for an arbitrary user!
    ScopeSessionManager.getInstance ().onScopeEnd (aOldSessionScope);

    // Ensure that we get a new session!
    // Here it is OK to create a new session scope explicitly!
    final ISessionWebScope aNewSessionScope = WebScopeManager.internalGetOrCreateSessionScope (aHttpSession, true, true);
    _restoreScopeAttributes (aNewSessionScope, aSessionScopeValues, aSessionApplicationScopeValues);
    return aNewSessionScope;
  }
}
