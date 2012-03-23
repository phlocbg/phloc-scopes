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
import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.string.StringHelper;
import com.phloc.scopes.MetaScopeFactory;
import com.phloc.scopes.nonweb.domain.IApplicationScope;
import com.phloc.scopes.nonweb.domain.IGlobalScope;
import com.phloc.scopes.nonweb.domain.IRequestScope;

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
    s_aLogger.info ("Global scope initialized!");
  }

  /**
   * This method is used to set the initial global scope.
   * 
   * @param sScopeID
   *        The scope ID to use
   */
  public static void onGlobalBegin (@Nonnull @Nonempty final String sScopeID)
  {
    setGlobalScope (MetaScopeFactory.getScopeFactory ().createGlobalScope (sScopeID));
  }

  public static boolean isGlobalScopePresent ()
  {
    return s_aGlobalScope != null;
  }

  @Nonnull
  public static IGlobalScope getGlobalScope ()
  {
    if (s_aGlobalScope == null)
      throw new IllegalStateException ("No global scope object has been set!");
    return s_aGlobalScope;
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
      s_aGlobalScope.destroyScope ();
      s_aGlobalScope = null;

      // done
      s_aLogger.info ("Global scope shut down!");
    }
    else
      s_aLogger.warn ("No global scope present that could be ended!");
  }

  // --- application scope ---

  @Nonnull
  public static String getRequestApplicationID ()
  {
    final String ret = getRequestScope ().getCastedAttribute (REQ_APPLICATION_ID);
    if (ret == null)
      throw new IllegalStateException ("Weird state - no appid!");
    return ret;
  }

  @Nonnull
  public static IApplicationScope getApplicationScope ()
  {
    return getApplicationScope (true);
  }

  @Nonnull
  public static IApplicationScope getApplicationScope (final boolean bCreateIfNotExisting)
  {
    return getApplicationScope (getRequestApplicationID (), bCreateIfNotExisting);
  }

  @Nonnull
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
   *        emptry.
   * @param aRequestScope
   *        The request scope to use. May not be <code>null</code>.
   */
  public static void setRequestScope (@Nonnull @Nonempty final String sApplicationID,
                                      @Nonnull final IRequestScope aRequestScope)
  {
    if (StringHelper.hasNoText (sApplicationID))
      throw new IllegalArgumentException ("appID");
    if (aRequestScope == null)
      throw new NullPointerException ("requestScope");
    if (s_aGlobalScope == null)
      throw new IllegalStateException ("No global context present! May be the global context listener is not installed?");

    // Happens if an internal redirect happens in a web-application (e.g. for
    // 404 page)
    if (s_aRequestScope.get () != null)
      s_aLogger.warn ("A request scope is already present!");

    // set request context
    s_aRequestScope.set (aRequestScope);

    // assign the application ID to the current request
    aRequestScope.setAttribute (REQ_APPLICATION_ID, sApplicationID);

    // Now init the scope
    aRequestScope.initScope ();
  }

  public static void onRequestBegin (@Nonnull @Nonempty final String sApplicationID,
                                     @Nonnull @Nonempty final String sScopeID)
  {
    setRequestScope (sApplicationID, MetaScopeFactory.getScopeFactory ().createRequestScope (sScopeID));
  }

  public static boolean isRequestScopePresent ()
  {
    return s_aRequestScope.get () != null;
  }

  @Nonnull
  public static IRequestScope getRequestScope ()
  {
    final IRequestScope aScope = s_aRequestScope.get ();
    if (aScope == null)
      throw new IllegalStateException ("The request context is not available. If you're running the unittests, inherit your test class from a scopeAwareTestCase.");
    return aScope;
  }

  /**
   * To be called after a request finished.
   */
  public static void onRequestEnd ()
  {
    final IRequestScope aScope = s_aRequestScope.get ();
    try
    {
      // Do we have something to destroy?
      if (aScope != null)
        aScope.destroyScope ();
      else
      {
        // Happens after an internal redirect happened in a web-application
        // (e.g. for 404 page) for the original scope
        s_aLogger.warn ("No request scope present that could be ended!");
      }
    }
    finally
    {
      s_aRequestScope.remove ();
    }
  }
}
