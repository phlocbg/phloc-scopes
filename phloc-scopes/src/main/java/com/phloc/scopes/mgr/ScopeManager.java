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
package com.phloc.scopes.mgr;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.ValueEnforcer;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.PresentForCodeCoverage;
import com.phloc.scopes.MetaScopeFactory;
import com.phloc.scopes.ScopeUtils;
import com.phloc.scopes.domain.IApplicationScope;
import com.phloc.scopes.domain.IGlobalScope;
import com.phloc.scopes.domain.IRequestScope;
import com.phloc.scopes.domain.ISessionApplicationScope;
import com.phloc.scopes.domain.ISessionScope;
import com.phloc.scopes.spi.ScopeSPIManager;

/**
 * This is the manager class for non-web scope handling. The following scopes
 * are supported:
 * <ul>
 * <li>global</li>
 * <li>application</li>
 * <li>request</li>
 * </ul>
 * 
 * @author Boris Gregorcic
 */
@ThreadSafe
public final class ScopeManager
{
  public static final boolean DEFAULT_CREATE_SCOPE = true;

  private static final Logger LOG = LoggerFactory.getLogger (ScopeManager.class);

  /**
   * The name of the attribute used to store the application scope in the
   * current request
   */
  private static final String REQ_APPLICATION_ID = "phloc.applicationscope"; //$NON-NLS-1$

  private static final Lock s_aGlobalLock = new ReentrantLock ();

  /** Global scope */
  @GuardedBy ("s_aGlobalLock")
  private static volatile IGlobalScope s_aGlobalScope;

  /** Request scope */
  private static final ThreadLocal <IRequestScope> s_aRequestScope = new ThreadLocal <> ();

  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final ScopeManager s_aInstance = new ScopeManager ();

  private ScopeManager ()
  {}

  // --- global scope ---

  /**
   * This method is only to be called by this class and the web scope manager!
   * 
   * @param aGlobalScope
   *        The scope to be set. May not be <code>null</code>.
   */
  public static void setGlobalScope (@Nonnull final IGlobalScope aGlobalScope)
  {
    ValueEnforcer.notNull (aGlobalScope, "GlobalScope"); //$NON-NLS-1$

    s_aGlobalLock.lock ();
    try
    {
      if (s_aGlobalScope != null)
        throw new IllegalStateException ("Another global scope is already present"); //$NON-NLS-1$

      s_aGlobalScope = aGlobalScope;

      aGlobalScope.initScope ();
      if (ScopeUtils.debugGlobalScopeLifeCycle (LOG))
        LOG.info ("Global scope '" + aGlobalScope.getID () + "' initialized!", ScopeUtils.getDebugStackTrace ()); //$NON-NLS-1$ //$NON-NLS-2$

      // Invoke SPIs
      ScopeSPIManager.onGlobalScopeBegin (aGlobalScope);
    }
    finally
    {
      s_aGlobalLock.unlock ();
    }
  }

  /**
   * This method is used to set the initial global scope.
   * 
   * @param sScopeID
   *        The scope ID to use
   * @return The created global scope object. Never <code>null</code>.
   */
  @Nonnull
  public static IGlobalScope onGlobalBegin (@Nonnull @Nonempty final String sScopeID)
  {
    final IGlobalScope aGlobalScope = MetaScopeFactory.getScopeFactory ().createGlobalScope (sScopeID);
    setGlobalScope (aGlobalScope);
    return aGlobalScope;
  }

  @Nullable
  public static IGlobalScope getGlobalScopeOrNull ()
  {
    final IGlobalScope ret = s_aGlobalScope;
    if (ret != null && ret.isValid ())
      return ret;
    // Return null if it is not set, in destruction or already destroyed
    return null;
  }

  public static boolean isGlobalScopePresent ()
  {
    return getGlobalScopeOrNull () != null;
  }

  @Nonnull
  public static IGlobalScope getGlobalScope ()
  {
    final IGlobalScope aGlobalScope = getGlobalScopeOrNull ();
    if (aGlobalScope == null)
      throw new IllegalStateException ("No global scope object has been set!"); //$NON-NLS-1$
    return aGlobalScope;
  }

  /**
   * To be called when the singleton global context is to be destroyed.
   */
  public static void onGlobalEnd ()
  {
    s_aGlobalLock.lock ();
    try
    {
      /**
       * This code removes all attributes set for the global context. This is
       * necessary, since the attributes would survive a Tomcat servlet context
       * reload if we don't kill them manually.<br>
       * Global scope variable may be null if onGlobalBegin() was never called!
       */
      if (s_aGlobalScope != null)
      {
        // Invoke SPI
        ScopeSPIManager.onGlobalScopeEnd (s_aGlobalScope);

        // Destroy and invalidate scope
        final String sDestroyedScopeID = s_aGlobalScope.getID ();
        s_aGlobalScope.destroyScope ();
        s_aGlobalScope = null;

        // done
        if (ScopeUtils.debugGlobalScopeLifeCycle (LOG))
          LOG.info ("Global scope '" + sDestroyedScopeID + "' shut down!", ScopeUtils.getDebugStackTrace ()); //$NON-NLS-1$ //$NON-NLS-2$
      }
      else
        LOG.warn ("No global scope present that could be shut down!"); //$NON-NLS-1$
    }
    finally
    {
      s_aGlobalLock.unlock ();
    }
  }

  // --- application scope ---

  /**
   * Get the application ID associated to the passed request scope
   * 
   * @param aRequestScope
   *        The request scope to use. May not be <code>null</code>.
   * @return <code>null</code> if no application ID is present
   */
  @Nullable
  public static String getRequestApplicationID (@Nonnull final IRequestScope aRequestScope)
  {
    return aRequestScope.getAttributeAsString (REQ_APPLICATION_ID);
  }

  /**
   * Get the application ID associated to the current request scope
   * 
   * @return Never <code>null</code>
   * @throws IllegalStateException
   *         if no application ID is present
   */
  @Nonnull
  public static String getRequestApplicationID ()
  {
    final String ret = getRequestApplicationID (getRequestScope ());
    if (ret == null)
      throw new IllegalStateException ("Weird state - no appid!"); //$NON-NLS-1$
    return ret;
  }

  /**
   * Get or create the current application scope using the application ID
   * present in the request scope.
   * 
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static IApplicationScope getApplicationScope ()
  {
    return getApplicationScope (DEFAULT_CREATE_SCOPE);
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
  public static IApplicationScope getApplicationScope (final boolean bCreateIfNotExisting)
  {
    return getApplicationScope (getRequestApplicationID (), bCreateIfNotExisting);
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
  public static IApplicationScope getApplicationScope (@Nonnull @Nonempty final String sApplicationID)
  {
    return getApplicationScope (sApplicationID, DEFAULT_CREATE_SCOPE);
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
  public static IApplicationScope getApplicationScope (@Nonnull @Nonempty final String sApplicationID,
                                                       final boolean bCreateIfNotExisting)
  {
    return getGlobalScope ().getApplicationScope (sApplicationID, bCreateIfNotExisting);
  }

  // --- session scope ---

  /**
   * Get the current session scope, based on the current request scope.
   * 
   * @return Never <code>null</code>.
   * @throws IllegalStateException
   *         If no request scope is present or if the underlying request scope
   *         does not have a session ID.
   */
  @Nonnull
  public static ISessionScope getSessionScope ()
  {
    return getSessionScope (ScopeManager.DEFAULT_CREATE_SCOPE);
  }

  /**
   * Get the current session scope, based on the current request scope.
   * 
   * @param bCreateIfNotExisting
   *        <code>true</code> to create a new scope, if none is present yet,
   *        <code>false</code> to return <code>null</code> if either no request
   *        scope or no session scope is present.
   * @return <code>null</code> if bCreateIfNotExisting is <code>false</code> and
   *         either no request scope or no session scope is present, the
   *         {@link ISessionScope} otherwise.
   * @throws IllegalStateException
   *         if bCreateIfNotExisting is <code>true</code> but no request scope
   *         is present. This exception is also thrown if the underlying request
   *         scope does not have a session ID.
   */
  @Nullable
  public static ISessionScope getSessionScope (final boolean bCreateIfNotExisting)
  {
    final IRequestScope aRequestScope = getRequestScopeOrNull ();
    if (aRequestScope != null)
    {
      final ScopeSessionManager aSSM = ScopeSessionManager.getInstance ();

      // Get the session ID from the underlying request
      final String sSessionID = aRequestScope.getSessionID (bCreateIfNotExisting);

      // Check if a matching session scope is present
      ISessionScope aSessionScope = aSSM.getSessionScopeOfID (sSessionID);
      if (aSessionScope == null && bCreateIfNotExisting)
      {
        if (sSessionID == null)
          throw new IllegalStateException ("Cannot create a SessionScope without a known session ID!"); //$NON-NLS-1$

        // Create a new session scope
        aSessionScope = MetaScopeFactory.getScopeFactory ().createSessionScope (sSessionID);

        // And register in the Session Manager
        aSSM.onScopeBegin (aSessionScope);
      }

      // We're done - maybe null
      return aSessionScope;
    }

    // If we want a session scope, we expect the return value to be non-null!
    if (bCreateIfNotExisting)
      throw new IllegalStateException ("No request scope is present, so no session scope can be created!"); //$NON-NLS-1$

    // No request scope present and no need to create a session
    return null;
  }

  /**
   * Manually destroy the passed session scope.
   * 
   * @param aSessionScope
   *        The session scope to be destroyed. May not be <code>null</code>.
   */
  public static void destroySessionScope (@Nonnull final ISessionScope aSessionScope)
  {
    ValueEnforcer.notNull (aSessionScope, "SessionScope"); //$NON-NLS-1$

    ScopeSessionManager.getInstance ().onScopeEnd (aSessionScope);
  }

  // --- session application scope ---

  @Nonnull
  public static ISessionApplicationScope getSessionApplicationScope ()
  {
    return getSessionApplicationScope (ScopeManager.DEFAULT_CREATE_SCOPE);
  }

  @Nullable
  public static ISessionApplicationScope getSessionApplicationScope (final boolean bCreateIfNotExisting)
  {
    return getSessionApplicationScope (ScopeManager.getRequestApplicationID (), bCreateIfNotExisting);
  }

  @Nonnull
  public static ISessionApplicationScope getSessionApplicationScope (@Nonnull @Nonempty final String sApplicationID)
  {
    return getSessionApplicationScope (sApplicationID, ScopeManager.DEFAULT_CREATE_SCOPE);
  }

  @Nullable
  public static ISessionApplicationScope getSessionApplicationScope (@Nonnull @Nonempty final String sApplicationID,
                                                                     final boolean bCreateIfNotExisting)
  {
    final ISessionScope aSessionScope = getSessionScope (bCreateIfNotExisting);
    // Session scope may only be null if bCreateIfNotExisting is false, else an
    // exception was already thrown in getSessionScope
    return aSessionScope == null ? null
                                 : aSessionScope.getSessionApplicationScope (sApplicationID, bCreateIfNotExisting);
  }

  // --- request scope ---

  /**
   * This method is only to be called by this class and the web scope manager!
   * 
   * @param sApplicationID
   *        The application ID to use. May neither be <code>null</code> nor
   *        empty.
   * @param aRequestScope
   *        The request scope to use. May not be <code>null</code>.
   */
  public static void setAndInitRequestScope (@Nonnull @Nonempty final String sApplicationID,
                                             @Nonnull final IRequestScope aRequestScope)
  {
    ValueEnforcer.notEmpty (sApplicationID, "ApplicationID"); //$NON-NLS-1$
    ValueEnforcer.notNull (aRequestScope, "RequestScope"); //$NON-NLS-1$
    if (!isGlobalScopePresent ())
      throw new IllegalStateException ("No global context present! May be the global context listener is not installed?"); //$NON-NLS-1$

    // Happens if an internal redirect happens in a web-application (e.g. for
    // 404 page)
    final IRequestScope aExistingRequestScope = s_aRequestScope.get ();
    if (aExistingRequestScope != null)
    {
      LOG.warn ("A request scope is already present - will overwrite it: {}", aExistingRequestScope); //$NON-NLS-1$
      if (aExistingRequestScope.isValid ())
      {
        // The scope shall be destroyed here, as this is most probably a
        // programming error!
        LOG.warn ("Destroying the old request scope before the new one gets initialized!"); //$NON-NLS-1$
        _destroyRequestScope (aExistingRequestScope);
      }
    }

    // set request context
    s_aRequestScope.set (aRequestScope);
    try
    {
      // assign the application ID to the current request
      if (aRequestScope.setAttribute (REQ_APPLICATION_ID, sApplicationID).isUnchanged ())
      {
        LOG.warn ("Failed to set the application ID '{}' into the request scope '{}'", //$NON-NLS-1$
                  sApplicationID,
                  aRequestScope.getID ());
      }
      // Now init the scope
      aRequestScope.initScope ();

      // call SPIs
      ScopeSPIManager.onRequestScopeBegin (aRequestScope);
    }
    catch (final Exception aEx)
    {
      LOG.error ("Removing request scope after error in initializtation", aEx); //$NON-NLS-1$
      onRequestEnd ();
      throw aEx;
    }
  }

  @Nonnull
  public static IRequestScope onRequestBegin (@Nonnull @Nonempty final String sApplicationID,
                                              @Nonnull @Nonempty final String sScopeID,
                                              @Nonnull @Nonempty final String sSessionID)
  {
    final IRequestScope aRequestScope = MetaScopeFactory.getScopeFactory ().createRequestScope (sScopeID, sSessionID);
    setAndInitRequestScope (sApplicationID, aRequestScope);
    return aRequestScope;
  }

  /**
   * @return The current request scope or <code>null</code> if no request scope
   *         is present.
   */
  @Nullable
  public static IRequestScope getRequestScopeOrNull ()
  {
    return s_aRequestScope.get ();
  }

  /**
   * @return <code>true</code> if a request scope is present, <code>false</code>
   *         otherwise
   */
  public static boolean isRequestScopePresent ()
  {
    return getRequestScopeOrNull () != null;
  }

  /**
   * @return The current request scope and never <code>null</code>.
   * @throws IllegalStateException
   *         If no request scope is present
   */
  @Nonnull
  public static IRequestScope getRequestScope ()
  {
    final IRequestScope aScope = getRequestScopeOrNull ();
    if (aScope == null)
      throw new IllegalStateException ("No request scope is available."); //$NON-NLS-1$
    return aScope;
  }

  private static void _destroyRequestScope (@Nonnull final IRequestScope aRequestScope)
  {
    // call SPIs
    ScopeSPIManager.onRequestScopeEnd (aRequestScope);

    // Destroy scope
    aRequestScope.destroyScope ();
  }

  /**
   * To be called after a request finished.
   */
  public static void onRequestEnd ()
  {
    final IRequestScope aRequestScope = getRequestScopeOrNull ();
    try
    {
      // Do we have something to destroy?
      if (aRequestScope != null)
      {
        _destroyRequestScope (aRequestScope);
      }
      else
      {
        // Happens after an internal redirect happened in a web-application
        // (e.g. for 404 page) for the original scope
        LOG.warn ("No request scope present that could be ended!"); //$NON-NLS-1$
      }
    }
    finally
    {
      // Remove from ThreadLocal
      s_aRequestScope.remove ();
    }
  }
}
