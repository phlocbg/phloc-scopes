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
import com.phloc.scopes.nonweb.domain.IGlobalScope;
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

  /**
   * To be called, when the global web scope is initialized. Most commonly this
   * is called from within
   * {@link javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)}
   * 
   * @param aServletContext
   *        The source servlet context to be used to retrieve the scope ID. May
   *        not be <code>null</code>
   * @return The created global web scope
   */
  @Nonnull
  public static IGlobalWebScope onGlobalBegin (@Nonnull final ServletContext aServletContext)
  {
    final IGlobalWebScope aGlobalScope = MetaScopeFactory.getWebScopeFactory ().createGlobalScope (aServletContext);
    ScopeManager.setGlobalScope (aGlobalScope);
    return aGlobalScope;
  }

  /**
   * @return <code>true</code> if a global scope is defined, <code>false</code>
   *         if none is defined
   */
  public static boolean isGlobalScopePresent ()
  {
    return ScopeManager.isGlobalScopePresent ();
  }

  /**
   * @return The global scope object.
   * @throws IllegalStateException
   *         If no global web scope object is present
   */
  @Nonnull
  public static IGlobalWebScope getGlobalScope ()
  {
    // Note: if you get an exception here, and you're in the unit test, please
    // derived from AbstractWebScopeAwareTestCase
    final IGlobalScope aGlobalScope = ScopeManager.getGlobalScopeOrNull ();
    if (aGlobalScope == null)
      throw new IllegalStateException ("No global web scope object has been set!");
    try
    {
      return (IGlobalWebScope) aGlobalScope;
    }
    catch (final ClassCastException ex)
    {
      throw new IllegalStateException ("Gobal scope object is not a web scope!");
    }
  }

  /**
   * To be called, when the global web scope is destroyed. Most commonly this is
   * called from within
   * {@link javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)}
   */
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

  /**
   * To be called, when a session web scope is initialized. Most commonly this
   * is called from within
   * {@link javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)}
   * 
   * @param aHttpSession
   *        The source session to base the scope on. May not be
   *        <code>null</code>
   * @return The created global session scope
   */
  @Nonnull
  public static ISessionWebScope onSessionBegin (@Nonnull final HttpSession aHttpSession)
  {
    final ISessionWebScope aSessionWebScope = MetaScopeFactory.getWebScopeFactory ().createSessionScope (aHttpSession);
    ScopeSessionManager.getInstance ().onScopeBegin (aSessionWebScope);
    return aSessionWebScope;
  }

  /**
   * Get or create a session scope based on the current request scope. This is
   * the same as calling
   * <code>getSessionScope({@link ScopeManager#DEFAULT_CREATE_SCOPE})</code>
   * 
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static ISessionWebScope getSessionScope ()
  {
    return getSessionScope (ScopeManager.DEFAULT_CREATE_SCOPE);
  }

  /**
   * Internal method which does the main logic for session web scope creation
   * 
   * @param aHttpSession
   *        The underlying HTTP session
   * @param bCreateIfNotExisting
   *        if <code>true</code> if a new session web scope is created, if none
   *        is present
   * @return <code>null</code> if no session scope is present, and
   *         bCreateIfNotExisting is false
   */
  @Nullable
  @DevelopersNote ("This is only for project-internal use!")
  public static ISessionWebScope internalGetOrCreateSessionScope (@Nonnull final HttpSession aHttpSession,
                                                                  final boolean bCreateIfNotExisting)
  {
    if (aHttpSession == null)
      throw new NullPointerException ("httpSession");

    // Do we already have a session web scope for the session?
    final String sSessionID = aHttpSession.getId ();
    ISessionScope aSessionWebScope = ScopeSessionManager.getInstance ().getSessionScopeOfID (sSessionID);
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

    try
    {
      return (ISessionWebScope) aSessionWebScope;
    }
    catch (final ClassCastException ex)
    {
      throw new IllegalStateException ("Session scope object is not a web scope!");
    }
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

  /**
   * To be called, when a session web scope is destroyed. Most commonly this is
   * called from within
   * {@link javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)}
   * 
   * @param aHttpSession
   *        The source session to destroy the matching scope. May not be
   *        <code>null</code>
   */
  public static void onSessionEnd (@Nonnull final HttpSession aHttpSession)
  {
    if (aHttpSession == null)
      throw new NullPointerException ("httpSession");

    final ScopeSessionManager aSSM = ScopeSessionManager.getInstanceOrNull ();
    final ISessionScope aSessionScope = aSSM == null ? null : aSSM.getSessionScopeOfID (aHttpSession.getId ());
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
    try
    { // Just cast
      return (IRequestWebScope) ScopeManager.getRequestScopeOrNull ();
    }
    catch (final ClassCastException ex)
    {
      throw new IllegalStateException ("Request scope object is not a web scope!");
    }
  }

  public static boolean isRequestScopePresent ()
  {
    return ScopeManager.isRequestScopePresent ();
  }

  @Nonnull
  public static IRequestWebScope getRequestScope ()
  {
    try
    {
      // Just cast
      return (IRequestWebScope) ScopeManager.getRequestScope ();
    }
    catch (final ClassCastException ex)
    {
      throw new IllegalStateException ("Request scope object is not a web scope!");
    }
  }

  public static void onRequestEnd ()
  {
    ScopeManager.onRequestEnd ();
  }
}
