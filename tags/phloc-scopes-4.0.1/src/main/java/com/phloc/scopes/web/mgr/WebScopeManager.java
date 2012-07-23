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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.DevelopersNote;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.scopes.MetaScopeFactory;
import com.phloc.scopes.nonweb.domain.ISessionScope;
import com.phloc.scopes.nonweb.mgr.ScopeManager;
import com.phloc.scopes.nonweb.mgr.ScopeSessionManager;
import com.phloc.scopes.web.domain.IApplicationWebScope;
import com.phloc.scopes.web.domain.IGlobalWebScope;
import com.phloc.scopes.web.domain.IRequestWebScope;
import com.phloc.scopes.web.domain.ISessionApplicationWebScope;
import com.phloc.scopes.web.domain.ISessionWebScope;

/**
 * This is the main manager class for web scope handling.
 * 
 * @author philip
 */
@Immutable
public final class WebScopeManager
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (WebScopeManager.class);

  private WebScopeManager ()
  {}

  // --- global scope ---

  @Nonnull
  public static IGlobalWebScope onGlobalBegin (@Nonnull final ServletContext aServletContext)
  {
    final IGlobalWebScope aGlobalScope = MetaScopeFactory.getWebScopeFactory ().createGlobalScope (aServletContext);
    ScopeManager.setGlobalScope (aGlobalScope);
    return aGlobalScope;
  }

  public static boolean isGlobalScopePresent ()
  {
    return ScopeManager.isGlobalScopePresent ();
  }

  @Nonnull
  public static IGlobalWebScope getGlobalScope ()
  {
    // Note: if you get an exception here, and you're in the unit test, please
    // derived from AbstractWebScopeAwareTestCase
    return (IGlobalWebScope) ScopeManager.getGlobalScope ();
  }

  public static void onGlobalEnd ()
  {
    ScopeManager.onGlobalEnd ();
  }

  // --- application scope ---

  /**
   * Get or create the current application scope using the application ID
   * present in the request scope.
   * 
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static IApplicationWebScope getApplicationScope ()
  {
    return (IApplicationWebScope) ScopeManager.getApplicationScope ();
  }

  /**
   * Get or create the current application scope using the application ID
   * present in the request scope.
   * 
   * @param bCreateIfNotExisting
   *        if <code>false</code> an no application scope is present, none will
   *        be created
   * @return <code>null</code> if bCreateIfNotExisting is <code>false</code> and
   *         no such scope is present
   */
  @Nullable
  public static IApplicationWebScope getApplicationScope (final boolean bCreateIfNotExisting)
  {
    return (IApplicationWebScope) ScopeManager.getApplicationScope (bCreateIfNotExisting);
  }

  /**
   * Get or create an application scope.
   * 
   * @param sApplicationID
   *        The ID of the application scope be retrieved or created. May neither
   *        be <code>null</code> nor empty.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static IApplicationWebScope getApplicationScope (@Nonnull @Nonempty final String sApplicationID)
  {
    return (IApplicationWebScope) ScopeManager.getApplicationScope (sApplicationID);
  }

  /**
   * Get or create an application scope.
   * 
   * @param sApplicationID
   *        The ID of the application scope be retrieved or created. May neither
   *        be <code>null</code> nor empty.
   * @param bCreateIfNotExisting
   *        if <code>false</code> an no application scope is present, none will
   *        be created
   * @return <code>null</code> if bCreateIfNotExisting is <code>false</code> and
   *         no such scope is present
   */
  @Nullable
  public static IApplicationWebScope getApplicationScope (@Nonnull @Nonempty final String sApplicationID,
                                                          final boolean bCreateIfNotExisting)
  {
    return (IApplicationWebScope) ScopeManager.getApplicationScope (sApplicationID, bCreateIfNotExisting);
  }

  // --- session scope ---

  @Nonnull
  public static ISessionWebScope onSessionBegin (@Nonnull final HttpSession aHttpSession)
  {
    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("SESSION BEGIN: isNew? = " + aHttpSession.isNew ());

    final ISessionWebScope aSessionWebScope = MetaScopeFactory.getWebScopeFactory ().createSessionScope (aHttpSession);
    ScopeSessionManager.getInstance ().onScopeBegin (aSessionWebScope);
    return aSessionWebScope;
  }

  @Nonnull
  public static ISessionWebScope getSessionScope ()
  {
    return getSessionScope (ScopeManager.DEFAULT_CREATE_SCOPE);
  }

  @Nullable
  @DevelopersNote ("This is only for project-internal use!")
  public static ISessionWebScope internalGetOrCreateSessionScope (@Nonnull final HttpSession aHttpSession,
                                                                  final boolean bCreateIfNotExisting)
  {
    if (aHttpSession == null)
      throw new NullPointerException ("httpSession");

    // Do we already have a session web scope for the session?
    final String sSessionID = aHttpSession.getId ();
    ISessionWebScope aSessionWebScope = (ISessionWebScope) ScopeSessionManager.getInstance ()
                                                                              .getSessionScopeOfID (sSessionID);
    if (aSessionWebScope == null && bCreateIfNotExisting)
    {
      // This can e.g. happen in tests, when there are no registered
      // listeners for session events!
      s_aLogger.warn ("Creating a new session web scope for ID '" +
                      sSessionID +
                      "' but there should already be one! Check your HttpSessionListener implementation. See com.phloc.scopes.web.servlet.WebScopeListener for an example");

      // Create a new session scope
      aSessionWebScope = onSessionBegin (aHttpSession);
    }
    return aSessionWebScope;
  }

  /**
   * Get the session scope from the current request scope.
   * 
   * @param bCreateIfNotExisting
   *        if <code>true</code> a new session scope (and a new HTTP session if
   *        required) is created if none is existing so far.
   * @return <code>null</code> if no session scope is present, and none should
   *         be created.
   */
  @Nullable
  public static ISessionWebScope getSessionScope (final boolean bCreateIfNotExisting)
  {
    // Try to to resolve the current request scope
    final IRequestWebScope aRequestScope = getRequestScopeOrNull ();
    if (aRequestScope != null)
    {
      // Check if we have an HTTP session object
      final HttpSession aHttpSession = aRequestScope.getSession (bCreateIfNotExisting);
      if (aHttpSession != null)
        return internalGetOrCreateSessionScope (aHttpSession, bCreateIfNotExisting);
    }
    else
    {
      // If we want a session scope, we expect the return value to be non-null!
      if (bCreateIfNotExisting)
        throw new IllegalStateException ("No request scope is present, so no session scope can be created!");
    }
    return null;
  }

  public static void onSessionEnd (@Nonnull final HttpSession aHttpSession)
  {
    final ScopeSessionManager aSSM = ScopeSessionManager.getInstance ();
    final ISessionScope aSessionScope = aSSM.getSessionScopeOfID (aHttpSession.getId ());
    if (aSessionScope != null)
    {
      // Regular scope end
      aSSM.onScopeEnd (aSessionScope);
    }
    else
    {
      // Ensure session is invalidated anyhow, even if no session scope is
      // present.
      // Happens in Tomcat startup if sessions that where serialized in
      // a previous invocation are invalidated on Tomcat restart
      s_aLogger.warn ("Found no session scope but invalidating session '" + aHttpSession.getId () + "' anyway");
      try
      {
        aHttpSession.invalidate ();
      }
      catch (final IllegalStateException ex)
      {
        // session already invalidated
      }
    }
  }

  // --- session application scope ---

  @Nonnull
  public static ISessionApplicationWebScope getSessionApplicationScope ()
  {
    return getSessionApplicationScope (ScopeManager.DEFAULT_CREATE_SCOPE);
  }

  @Nullable
  public static ISessionApplicationWebScope getSessionApplicationScope (final boolean bCreateIfNotExisting)
  {
    return getSessionApplicationScope (ScopeManager.getRequestApplicationID (), bCreateIfNotExisting);
  }

  @Nonnull
  public static ISessionApplicationWebScope getSessionApplicationScope (@Nonnull @Nonempty final String sApplicationID)
  {
    return getSessionApplicationScope (sApplicationID, ScopeManager.DEFAULT_CREATE_SCOPE);
  }

  @Nullable
  public static ISessionApplicationWebScope getSessionApplicationScope (@Nonnull @Nonempty final String sApplicationID,
                                                                        final boolean bCreateIfNotExisting)
  {
    final ISessionWebScope aSessionScope = getSessionScope (bCreateIfNotExisting);
    return aSessionScope == null ? null : aSessionScope.getSessionApplicationScope (sApplicationID,
                                                                                    bCreateIfNotExisting);
  }

  // --- request scopes ---

  @Nonnull
  public static IRequestWebScope onRequestBegin (@Nonnull final String sApplicationID,
                                                 @Nonnull final HttpServletRequest aHttpRequest,
                                                 @Nonnull final HttpServletResponse aHttpResponse)
  {
    final IRequestWebScope aRequestScope = MetaScopeFactory.getWebScopeFactory ().createRequestScope (aHttpRequest,
                                                                                                      aHttpResponse);
    ScopeManager.setAndInitRequestScope (sApplicationID, aRequestScope);
    return aRequestScope;
  }

  @Nullable
  public static IRequestWebScope getRequestScopeOrNull ()
  {
    // Just cast
    return (IRequestWebScope) ScopeManager.getRequestScopeOrNull ();
  }

  public static boolean isRequestScopePresent ()
  {
    return ScopeManager.isRequestScopePresent ();
  }

  @Nonnull
  public static IRequestWebScope getRequestScope ()
  {
    // Just cast
    return (IRequestWebScope) ScopeManager.getRequestScope ();
  }

  public static void onRequestEnd ()
  {
    ScopeManager.onRequestEnd ();
  }
}
