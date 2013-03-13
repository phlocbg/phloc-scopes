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
package com.phloc.scopes.spi;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.mock.IMockException;
import com.phloc.scopes.nonweb.domain.IApplicationScope;
import com.phloc.scopes.nonweb.domain.IGlobalScope;
import com.phloc.scopes.nonweb.domain.IRequestScope;
import com.phloc.scopes.nonweb.domain.ISessionApplicationScope;
import com.phloc.scopes.nonweb.domain.ISessionScope;
import com.phloc.scopes.web.domain.IApplicationWebScope;
import com.phloc.scopes.web.domain.IGlobalWebScope;
import com.phloc.scopes.web.domain.IRequestWebScope;
import com.phloc.scopes.web.domain.ISessionApplicationWebScope;
import com.phloc.scopes.web.domain.ISessionWebScope;

/**
 * This is an internal class, that triggers the SPI implementations registered
 * for scope lifecycle SPI implementations. <b>Never</b> call this class from
 * the outside!
 * 
 * @author philip
 */
@Immutable
public final class ScopeSPIManager
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (ScopeSPIManager.class);

  // non-web scopes
  private static final List <IGlobalScopeSPI> s_aGlobalSPIs = new ArrayList <IGlobalScopeSPI> ();
  private static final List <IApplicationScopeSPI> s_aApplicationSPIs = new ArrayList <IApplicationScopeSPI> ();
  private static final List <ISessionScopeSPI> s_aSessionSPIs = new ArrayList <ISessionScopeSPI> ();
  private static final List <ISessionApplicationScopeSPI> s_aSessionApplicationSPIs = new ArrayList <ISessionApplicationScopeSPI> ();
  private static final List <IRequestScopeSPI> s_aRequestSPIs = new ArrayList <IRequestScopeSPI> ();

  // web scopes
  private static final List <IGlobalWebScopeSPI> s_aGlobalWebSPIs = new ArrayList <IGlobalWebScopeSPI> ();
  private static final List <IApplicationWebScopeSPI> s_aApplicationWebSPIs = new ArrayList <IApplicationWebScopeSPI> ();
  private static final List <ISessionWebScopeSPI> s_aSessionWebSPIs = new ArrayList <ISessionWebScopeSPI> ();
  private static final List <ISessionApplicationWebScopeSPI> s_aSessionApplicationWebSPIs = new ArrayList <ISessionApplicationWebScopeSPI> ();
  private static final List <IRequestWebScopeSPI> s_aRequestWebSPIs = new ArrayList <IRequestWebScopeSPI> ();

  static
  {
    // non-web scopes
    for (final IGlobalScopeSPI aSPI : ServiceLoader.load (IGlobalScopeSPI.class))
      s_aGlobalSPIs.add (aSPI);
    for (final IApplicationScopeSPI aSPI : ServiceLoader.load (IApplicationScopeSPI.class))
      s_aApplicationSPIs.add (aSPI);
    for (final ISessionScopeSPI aSPI : ServiceLoader.load (ISessionScopeSPI.class))
      s_aSessionSPIs.add (aSPI);
    for (final ISessionApplicationScopeSPI aSPI : ServiceLoader.load (ISessionApplicationScopeSPI.class))
      s_aSessionApplicationSPIs.add (aSPI);
    for (final IRequestScopeSPI aSPI : ServiceLoader.load (IRequestScopeSPI.class))
      s_aRequestSPIs.add (aSPI);

    // web scopes
    for (final IGlobalWebScopeSPI aSPI : ServiceLoader.load (IGlobalWebScopeSPI.class))
      s_aGlobalWebSPIs.add (aSPI);
    for (final IApplicationWebScopeSPI aSPI : ServiceLoader.load (IApplicationWebScopeSPI.class))
      s_aApplicationWebSPIs.add (aSPI);
    for (final ISessionWebScopeSPI aSPI : ServiceLoader.load (ISessionWebScopeSPI.class))
      s_aSessionWebSPIs.add (aSPI);
    for (final ISessionApplicationWebScopeSPI aSPI : ServiceLoader.load (ISessionApplicationWebScopeSPI.class))
      s_aSessionApplicationWebSPIs.add (aSPI);
    for (final IRequestWebScopeSPI aSPI : ServiceLoader.load (IRequestWebScopeSPI.class))
      s_aRequestWebSPIs.add (aSPI);
  }

  private ScopeSPIManager ()
  {}

  public static void onGlobalScopeBegin (@Nonnull final IGlobalScope aGlobalScope)
  {
    // non-web scope
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

    if (aGlobalScope instanceof IGlobalWebScope)
    {
      // web scope
      final IGlobalWebScope aGlobalWebScope = (IGlobalWebScope) aGlobalScope;
      for (final IGlobalWebScopeSPI aSPI : s_aGlobalWebSPIs)
        try
        {
          aSPI.onGlobalWebScopeBegin (aGlobalWebScope);
        }
        catch (final Throwable t)
        {
          s_aLogger.error ("Failed to invoke SPI method onGlobalWebScopeBegin on " +
                           aSPI +
                           " with scope " +
                           aGlobalWebScope, t instanceof IMockException ? null : t);
        }
    }
  }

  public static void onGlobalScopeEnd (@Nonnull final IGlobalScope aGlobalScope)
  {
    // non-web scope
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

    if (aGlobalScope instanceof IGlobalWebScope)
    {
      // web scope
      final IGlobalWebScope aGlobalWebScope = (IGlobalWebScope) aGlobalScope;
      for (final IGlobalWebScopeSPI aSPI : s_aGlobalWebSPIs)
        try
        {
          aSPI.onGlobalWebScopeEnd (aGlobalWebScope);
        }
        catch (final Throwable t)
        {
          s_aLogger.error ("Failed to invoke SPI method onGlobalWebScopeEnd on " +
                           aSPI +
                           " with scope " +
                           aGlobalWebScope, t instanceof IMockException ? null : t);
        }
    }
  }

  public static void onApplicationScopeBegin (@Nonnull final IApplicationScope aApplicationScope)
  {
    // non-web scope
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

    if (aApplicationScope instanceof IApplicationWebScope)
    {
      // web scope
      final IApplicationWebScope aApplicationWebScope = (IApplicationWebScope) aApplicationScope;
      for (final IApplicationWebScopeSPI aSPI : s_aApplicationWebSPIs)
        try
        {
          aSPI.onApplicationWebScopeBegin (aApplicationWebScope);
        }
        catch (final Throwable t)
        {
          s_aLogger.error ("Failed to invoke SPI method onApplicationWebScopeBegin on " +
                           aSPI +
                           " with scope " +
                           aApplicationWebScope, t instanceof IMockException ? null : t);
        }
    }
  }

  public static void onApplicationScopeEnd (@Nonnull final IApplicationScope aApplicationScope)
  {
    // non-web scope
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

    if (aApplicationScope instanceof IApplicationWebScope)
    {
      // web scope
      final IApplicationWebScope aApplicationWebScope = (IApplicationWebScope) aApplicationScope;
      for (final IApplicationWebScopeSPI aSPI : s_aApplicationWebSPIs)
        try
        {
          aSPI.onApplicationWebScopeEnd (aApplicationWebScope);
        }
        catch (final Throwable t)
        {
          s_aLogger.error ("Failed to invoke SPI method onApplicationWebScopeEnd on " +
                           aSPI +
                           " with scope " +
                           aApplicationWebScope, t instanceof IMockException ? null : t);
        }
    }
  }

  public static void onSessionScopeBegin (@Nonnull final ISessionScope aSessionScope)
  {
    // non-web scope
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

    if (aSessionScope instanceof ISessionWebScope)
    {
      // web scope
      final ISessionWebScope aSessionWebScope = (ISessionWebScope) aSessionScope;
      for (final ISessionWebScopeSPI aSPI : s_aSessionWebSPIs)
        try
        {
          aSPI.onSessionWebScopeBegin (aSessionWebScope);
        }
        catch (final Throwable t)
        {
          s_aLogger.error ("Failed to invoke SPI method onSessionWebScopeBegin on " +
                           aSPI +
                           " with scope " +
                           aSessionWebScope, t instanceof IMockException ? null : t);
        }
    }
  }

  public static void onSessionScopeEnd (@Nonnull final ISessionScope aSessionScope)
  {
    // non-web scope
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

    if (aSessionScope instanceof ISessionWebScope)
    {
      // web scope
      final ISessionWebScope aSessionWebScope = (ISessionWebScope) aSessionScope;
      for (final ISessionWebScopeSPI aSPI : s_aSessionWebSPIs)
        try
        {
          aSPI.onSessionWebScopeEnd (aSessionWebScope);
        }
        catch (final Throwable t)
        {
          s_aLogger.error ("Failed to invoke SPI method onSessionWebScopeEnd on " +
                           aSPI +
                           " with scope " +
                           aSessionWebScope, t instanceof IMockException ? null : t);
        }
    }
  }

  public static void onSessionApplicationScopeBegin (@Nonnull final ISessionApplicationScope aSessionApplicationScope)
  {
    // non-web scope
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

    if (aSessionApplicationScope instanceof ISessionApplicationWebScope)
    {
      // web scope
      final ISessionApplicationWebScope aSessionApplicationWebScope = (ISessionApplicationWebScope) aSessionApplicationScope;
      for (final ISessionApplicationWebScopeSPI aSPI : s_aSessionApplicationWebSPIs)
        try
        {
          aSPI.onSessionApplicationWebScopeBegin (aSessionApplicationWebScope);
        }
        catch (final Throwable t)
        {
          s_aLogger.error ("Failed to invoke SPI method onSessionApplicationWebScopeBegin on " +
                           aSPI +
                           " with scope " +
                           aSessionApplicationWebScope, t instanceof IMockException ? null : t);
        }
    }
  }

  public static void onSessionApplicationScopeEnd (@Nonnull final ISessionApplicationScope aSessionApplicationScope)
  {
    // non-web scope
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

    if (aSessionApplicationScope instanceof ISessionApplicationWebScope)
    {
      // web scope
      final ISessionApplicationWebScope aSessionApplicationWebScope = (ISessionApplicationWebScope) aSessionApplicationScope;
      for (final ISessionApplicationWebScopeSPI aSPI : s_aSessionApplicationWebSPIs)
        try
        {
          aSPI.onSessionApplicationWebScopeEnd (aSessionApplicationWebScope);
        }
        catch (final Throwable t)
        {
          s_aLogger.error ("Failed to invoke SPI method onSessionApplicationWebScopeEnd on " +
                           aSPI +
                           " with scope " +
                           aSessionApplicationWebScope, t instanceof IMockException ? null : t);
        }
    }
  }

  public static void onRequestScopeBegin (@Nonnull final IRequestScope aRequestScope)
  {
    // non-web scope
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

    if (aRequestScope instanceof IRequestWebScope)
    {
      // web scope
      final IRequestWebScope aRequestWebScope = (IRequestWebScope) aRequestScope;
      for (final IRequestWebScopeSPI aSPI : s_aRequestWebSPIs)
        try
        {
          aSPI.onRequestWebScopeBegin (aRequestWebScope);
        }
        catch (final Throwable t)
        {
          s_aLogger.error ("Failed to invoke SPI method onRequestWebScopeBegin on " +
                           aSPI +
                           " with scope " +
                           aRequestWebScope, t instanceof IMockException ? null : t);
        }
    }
  }

  public static void onRequestScopeEnd (@Nonnull final IRequestScope aRequestScope)
  {
    // non-web scope
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

    if (aRequestScope instanceof IRequestWebScope)
    {
      // web scope
      final IRequestWebScope aRequestWebScope = (IRequestWebScope) aRequestScope;
      for (final IRequestWebScopeSPI aSPI : s_aRequestWebSPIs)
        try
        {
          aSPI.onRequestWebScopeEnd (aRequestWebScope);
        }
        catch (final Throwable t)
        {
          s_aLogger.error ("Failed to invoke SPI method onRequestWebScopeEnd on " +
                           aSPI +
                           " with scope " +
                           aRequestWebScope, t instanceof IMockException ? null : t);
        }
    }
  }
}