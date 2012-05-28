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
package com.phloc.scopes.nonweb.mgr;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.string.StringHelper;
import com.phloc.scopes.MetaScopeFactory;
import com.phloc.scopes.nonweb.domain.IApplicationScope;
import com.phloc.scopes.nonweb.domain.IGlobalScope;
import com.phloc.scopes.nonweb.domain.IRequestScope;
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
 * @author philip
 */
@NotThreadSafe
public final class ScopeManager
{
  public static final boolean DEFAULT_CREATE_SCOPE = true;

  private static final Logger s_aLogger = LoggerFactory.getLogger (ScopeManager.class);

  /**
   * The name of the attribute used to store the application scope in the
   * current request
   */
  private static final String REQ_APPLICATION_ID = "phloc.applicationscope";

  /** Global scope */
  private static IGlobalScope s_aGlobalScope;

  /** Request scope */
  private static ThreadLocal <IRequestScope> s_aRequestScope = new ThreadLocal <IRequestScope> ();

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
    if (aGlobalScope == null)
      throw new NullPointerException ("globalScope");
    if (s_aGlobalScope != null)
      throw new IllegalStateException ("Another global scope is already present");

    s_aGlobalScope = aGlobalScope;

    aGlobalScope.initScope ();
    s_aLogger.info ("Global scope '" + aGlobalScope.getID () + "' initialized!");

    // Invoke SPIs
    ScopeSPIManager.onGlobalScopeBegin (aGlobalScope);
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
    return s_aGlobalScope;
  }

  public static boolean isGlobalScopePresent ()
  {
    return s_aGlobalScope != null;
  }

  @Nonnull
  public static IGlobalScope getGlobalScope ()
  {
    final IGlobalScope aGlobalScope = getGlobalScopeOrNull ();
    if (aGlobalScope == null)
      throw new IllegalStateException ("No global scope object has been set!");
    return aGlobalScope;
  }

  /**
   * To be called when the singleton global context is to be destroyed.
   */
  public static void onGlobalEnd ()
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
      s_aGlobalScope.destroyScope ();
      s_aGlobalScope = null;

      // done
      s_aLogger.info ("Global scope shut down!");
    }
    else
      s_aLogger.warn ("No global scope present that could be shut down!");
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
      throw new IllegalStateException ("Weird state - no appid!");
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
    if (StringHelper.hasNoText (sApplicationID))
      throw new IllegalArgumentException ("applicationID");
    if (aRequestScope == null)
      throw new NullPointerException ("requestScope");
    if (!isGlobalScopePresent ())
      throw new IllegalStateException ("No global context present! May be the global context listener is not installed?");

    // Happens if an internal redirect happens in a web-application (e.g. for
    // 404 page)
    final IRequestScope aExistingRequestScope = s_aRequestScope.get ();
    if (aExistingRequestScope != null)
    {
      s_aLogger.warn ("A request scope is already present - will overwrite it: " + aExistingRequestScope.toString ());
      if (aExistingRequestScope.isValid ())
      {
        // TODO shall the scope be destroyed here????
      }
    }

    // set request context
    s_aRequestScope.set (aRequestScope);

    // assign the application ID to the current request
    if (aRequestScope.setAttribute (REQ_APPLICATION_ID, sApplicationID).isUnchanged ())
    {
      s_aLogger.warn ("Failed to set the application ID '" +
                      sApplicationID +
                      "' into the request scope '" +
                      aRequestScope.getID () +
                      "'");
    }

    // Now init the scope
    aRequestScope.initScope ();

    // call SPIs
    ScopeSPIManager.onRequestScopeBegin (aRequestScope);
  }

  @Nonnull
  public static IRequestScope onRequestBegin (@Nonnull @Nonempty final String sApplicationID,
                                              @Nonnull @Nonempty final String sScopeID)
  {
    final IRequestScope aRequestScope = MetaScopeFactory.getScopeFactory ().createRequestScope (sScopeID);
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
      throw new IllegalStateException ("No request scope is available.");
    return aScope;
  }

  /**
   * To be called after a request finished.
   */
  public static void onRequestEnd ()
  {
    final IRequestScope aRequestScope = s_aRequestScope.get ();
    try
    {
      // Do we have something to destroy?
      if (aRequestScope != null)
      {
        // call SPIs
        ScopeSPIManager.onRequestScopeEnd (aRequestScope);

        // Destroy scope
        aRequestScope.destroyScope ();
      }
      else
      {
        // Happens after an internal redirect happened in a web-application
        // (e.g. for 404 page) for the original scope
        s_aLogger.warn ("No request scope present that could be ended!");
      }
    }
    finally
    {
      // Remove from ThreadLocal
      s_aRequestScope.remove ();
    }
  }
}
