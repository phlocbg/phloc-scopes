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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.DevelopersNote;
import com.phloc.commons.string.StringHelper;
import com.phloc.scopes.IScopeRenewalAware;
import com.phloc.scopes.web.domain.ISessionApplicationWebScope;
import com.phloc.scopes.web.domain.ISessionWebScope;

@Immutable
public final class WebScopeSessionHelper
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (WebScopeSessionHelper.class);

  private WebScopeSessionHelper ()
  {}

  @Nonnull
  @Deprecated
  @DevelopersNote ("This method is incorrect, as it only takes the current session application scopes, but invalidates all session application scopes by invalidating the session!")
  public final static HttpSession renewSessionApplicationScope (@Nonnull final HttpServletRequest aHttpRequest)
  {
    if (aHttpRequest == null)
      throw new NullPointerException ("request");

    // Get all attributes to be copied
    final ISessionApplicationWebScope aScope = WebScopeManager.getSessionApplicationScope (false);
    final Map <String, IScopeRenewalAware> aSurvivingAttributes = aScope == null
                                                                                ? new HashMap <String, IScopeRenewalAware> ()
                                                                                : aScope.getAllScopeRenewalAwareAttributes ();

    // Main renew the session:

    // Is there any existing session?
    HttpSession aSession = aHttpRequest.getSession (false);
    if (aSession != null)
    {
      try
      {
        s_aLogger.info ("Invalidating session " + aSession.getId ());
        aSession.invalidate ();
      }
      catch (final IllegalStateException ex)
      {
        // session already invalidated
      }
    }

    // Ensure that we get a new session!
    aSession = aHttpRequest.getSession (true);

    if (!aSurvivingAttributes.isEmpty ())
    {
      // Set all surviving attributes in the new session application scope
      // Note: don't use the above scope object - get a new one!
      final ISessionApplicationWebScope aNewScope = WebScopeManager.getSessionApplicationScope (true);
      for (final Map.Entry <String, IScopeRenewalAware> aEntry : aSurvivingAttributes.entrySet ())
        aNewScope.setAttribute (aEntry.getKey (), aEntry.getValue ());

      s_aLogger.info ("Attributes which survived session renewal: " + aSurvivingAttributes.keySet ());
    }

    return aSession;
  }

  public final static void renewSessionScope ()
  {
    final ISessionWebScope aOldSessionScope = WebScopeManager.getSessionScope (false);
    if (aOldSessionScope != null)
    {
      // OK, we have a session scope to renew

      // Save all values from session scopes
      final Map <String, IScopeRenewalAware> aSessionScopeValues = aOldSessionScope.getAllScopeRenewalAwareAttributes ();

      // Save all values from all session application scopes
      final Map <String, Map <String, IScopeRenewalAware>> aSessionApplicationScopeValues = new HashMap <String, Map <String, IScopeRenewalAware>> ();
      for (final Map.Entry <String, ISessionApplicationWebScope> aEntry : aOldSessionScope.getAllSessionApplicationScopes ()
                                                                                          .entrySet ())
      {
        final Map <String, IScopeRenewalAware> aSurviving = aEntry.getValue ().getAllScopeRenewalAwareAttributes ();
        if (!aSurviving.isEmpty ())
        {
          // Remove the leading session ID
          final String sScopeApplicationID = StringHelper.trimStart (aEntry.getKey (), aOldSessionScope.getID () + '.');
          aSessionApplicationScopeValues.put (sScopeApplicationID, aSurviving);
        }
      }

      // renew the session
      try
      {
        s_aLogger.info ("Invalidating session " + aOldSessionScope.getID ());
        aOldSessionScope.getSession ().invalidate ();
      }
      catch (final Exception ex)
      {
        // session already invalidated???
        s_aLogger.warn ("Failed to invalidate session", ex);
      }

      // Ensure that we get a new session!
      final ISessionWebScope aNewSessionScope = WebScopeManager.getSessionScope (true);

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
  }
}
