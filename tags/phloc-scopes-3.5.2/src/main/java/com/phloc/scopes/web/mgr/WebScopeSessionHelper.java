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

import com.phloc.scopes.ISurvivingSessionRenewal;
import com.phloc.scopes.web.domain.ISessionApplicationWebScope;

@Immutable
public final class WebScopeSessionHelper
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (WebScopeSessionHelper.class);

  private WebScopeSessionHelper ()
  {}

  @Nonnull
  public final static HttpSession renewSessionApplicationScope (@Nonnull final HttpServletRequest aHttpRequest)
  {
    if (aHttpRequest == null)
      throw new NullPointerException ("request");

    // Get all attributes to be copied
    final Map <String, Object> aSurvivingAttributes = new HashMap <String, Object> ();
    final ISessionApplicationWebScope aScope = WebScopeManager.getSessionApplicationScope (false);
    if (aScope != null)
    {
      // collect all attributes that should survive the session renewal
      for (final Map.Entry <String, Object> aEntry : aScope.getAllAttributes ().entrySet ())
      {
        final Object aValue = aEntry.getValue ();
        if (aValue instanceof ISurvivingSessionRenewal)
          aSurvivingAttributes.put (aEntry.getKey (), aValue);
      }
    }

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
      for (final Map.Entry <String, Object> aEntry : aSurvivingAttributes.entrySet ())
        aNewScope.setAttribute (aEntry.getKey (), aEntry.getValue ());

      s_aLogger.info ("Attributes which survived session renewal: " + aSurvivingAttributes.keySet ());
    }

    return aSession;
  }
}
