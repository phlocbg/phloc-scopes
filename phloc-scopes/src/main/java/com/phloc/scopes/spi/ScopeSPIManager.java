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
package com.phloc.scopes.spi;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.PresentForCodeCoverage;
import com.phloc.commons.lang.ServiceLoaderUtils;
import com.phloc.commons.mock.IMockException;
import com.phloc.scopes.domain.IApplicationScope;
import com.phloc.scopes.domain.IGlobalScope;
import com.phloc.scopes.domain.IRequestScope;
import com.phloc.scopes.domain.ISessionApplicationScope;
import com.phloc.scopes.domain.ISessionScope;

/**
 * This is an internal class, that triggers the SPI implementations registered
 * for scope lifecycle SPI implementations. <b>Never</b> call this class from
 * outside of this project!
 * 
 * @author Philip Helger
 */
@Immutable
public final class ScopeSPIManager
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (ScopeSPIManager.class);

  // non-web scopes
  private static final List <IGlobalScopeSPI> s_aGlobalSPIs;
  private static final List <IApplicationScopeSPI> s_aApplicationSPIs;
  private static final List <ISessionScopeSPI> s_aSessionSPIs;
  private static final List <ISessionApplicationScopeSPI> s_aSessionApplicationSPIs;
  private static final List <IRequestScopeSPI> s_aRequestSPIs;

  static
  {
    // Register all listeners
    s_aGlobalSPIs = ServiceLoaderUtils.getAllSPIImplementations (IGlobalScopeSPI.class);
    s_aApplicationSPIs = ServiceLoaderUtils.getAllSPIImplementations (IApplicationScopeSPI.class);
    s_aSessionSPIs = ServiceLoaderUtils.getAllSPIImplementations (ISessionScopeSPI.class);
    s_aSessionApplicationSPIs = ServiceLoaderUtils.getAllSPIImplementations (ISessionApplicationScopeSPI.class);
    s_aRequestSPIs = ServiceLoaderUtils.getAllSPIImplementations (IRequestScopeSPI.class);
  }

  @SuppressWarnings ("unused")
  @PresentForCodeCoverage
  private static final ScopeSPIManager s_aInstance = new ScopeSPIManager ();

  private ScopeSPIManager ()
  {}

  public static void onGlobalScopeBegin (@Nonnull final IGlobalScope aGlobalScope)
  {
    for (final IGlobalScopeSPI aSPI : s_aGlobalSPIs)
      try
      {
        aSPI.onGlobalScopeBegin (aGlobalScope);
      }
      catch (final Throwable t)
      {
        s_aLogger.error ("Failed to invoke SPI method onGlobalScopeBegin on " + aSPI + " with scope " + aGlobalScope,
                         t instanceof IMockException ? null : t);
      }
  }

  public static void onGlobalScopeEnd (@Nonnull final IGlobalScope aGlobalScope)
  {
    for (final IGlobalScopeSPI aSPI : s_aGlobalSPIs)
      try
      {
        aSPI.onGlobalScopeEnd (aGlobalScope);
      }
      catch (final Throwable t)
      {
        s_aLogger.error ("Failed to invoke SPI method onGlobalScopeEnd on " + aSPI + " with scope " + aGlobalScope,
                         t instanceof IMockException ? null : t);
      }
  }

  public static void onApplicationScopeBegin (@Nonnull final IApplicationScope aApplicationScope)
  {
    for (final IApplicationScopeSPI aSPI : s_aApplicationSPIs)
      try
      {
        aSPI.onApplicationScopeBegin (aApplicationScope);
      }
      catch (final Throwable t)
      {
        s_aLogger.error ("Failed to invoke SPI method onApplicationScopeBegin on " +
                         aSPI +
                         " with scope " +
                         aApplicationScope, t instanceof IMockException ? null : t);
      }
  }

  public static void onApplicationScopeEnd (@Nonnull final IApplicationScope aApplicationScope)
  {
    for (final IApplicationScopeSPI aSPI : s_aApplicationSPIs)
      try
      {
        aSPI.onApplicationScopeEnd (aApplicationScope);
      }
      catch (final Throwable t)
      {
        s_aLogger.error ("Failed to invoke SPI method onApplicationScopeEnd on " +
                         aSPI +
                         " with scope " +
                         aApplicationScope, t instanceof IMockException ? null : t);
      }
  }

  public static void onSessionScopeBegin (@Nonnull final ISessionScope aSessionScope)
  {
    for (final ISessionScopeSPI aSPI : s_aSessionSPIs)
      try
      {
        aSPI.onSessionScopeBegin (aSessionScope);
      }
      catch (final Throwable t)
      {
        s_aLogger.error ("Failed to invoke SPI method onSessionScopeBegin on " + aSPI + " with scope " + aSessionScope,
                         t instanceof IMockException ? null : t);
      }
  }

  public static void onSessionScopeEnd (@Nonnull final ISessionScope aSessionScope)
  {
    for (final ISessionScopeSPI aSPI : s_aSessionSPIs)
      try
      {
        aSPI.onSessionScopeEnd (aSessionScope);
      }
      catch (final Throwable t)
      {
        s_aLogger.error ("Failed to invoke SPI method onSessionScopeEnd on " + aSPI + " with scope " + aSessionScope,
                         t instanceof IMockException ? null : t);
      }
  }

  public static void onSessionApplicationScopeBegin (@Nonnull final ISessionApplicationScope aSessionApplicationScope)
  {
    for (final ISessionApplicationScopeSPI aSPI : s_aSessionApplicationSPIs)
      try
      {
        aSPI.onSessionApplicationScopeBegin (aSessionApplicationScope);
      }
      catch (final Throwable t)
      {
        s_aLogger.error ("Failed to invoke SPI method onSessionApplicationScopeBegin on " +
                         aSPI +
                         " with scope " +
                         aSessionApplicationScope, t instanceof IMockException ? null : t);
      }
  }

  public static void onSessionApplicationScopeEnd (@Nonnull final ISessionApplicationScope aSessionApplicationScope)
  {
    for (final ISessionApplicationScopeSPI aSPI : s_aSessionApplicationSPIs)
      try
      {
        aSPI.onSessionApplicationScopeEnd (aSessionApplicationScope);
      }
      catch (final Throwable t)
      {
        s_aLogger.error ("Failed to invoke SPI method onSessionApplicationScopeEnd on " +
                         aSPI +
                         " with scope " +
                         aSessionApplicationScope, t instanceof IMockException ? null : t);
      }
  }

  public static void onRequestScopeBegin (@Nonnull final IRequestScope aRequestScope)
  {
    for (final IRequestScopeSPI aSPI : s_aRequestSPIs)
      try
      {
        aSPI.onRequestScopeBegin (aRequestScope);
      }
      catch (final Throwable t)
      {
        s_aLogger.error ("Failed to invoke SPI method onRequestScopeBegin on " + aSPI + " with scope " + aRequestScope,
                         t instanceof IMockException ? null : t);
      }
  }

  public static void onRequestScopeEnd (@Nonnull final IRequestScope aRequestScope)
  {
    for (final IRequestScopeSPI aSPI : s_aRequestSPIs)
      try
      {
        aSPI.onRequestScopeEnd (aRequestScope);
      }
      catch (final Throwable t)
      {
        s_aLogger.error ("Failed to invoke SPI method onRequestScopeEnd on " + aSPI + " with scope " + aRequestScope,
                         t instanceof IMockException ? null : t);
      }
  }
}
