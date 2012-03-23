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
package com.phloc.scopes.web.mock;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionListener;

import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;

@NotThreadSafe
public final class MockHttpListener
{
  private static List <EventListener> s_aListener = new ArrayList <EventListener> ();

  private MockHttpListener ()
  {}

  public static void setToDefault ()
  {
    s_aListener.clear ();
    s_aListener.add (new MockServletContextListener ());
    s_aListener.add (new MockHttpSessionListener ());
    s_aListener.add (new MockServletRequestListener ());
  }

  public static void addListener (@Nullable final EventListener aListener)
  {
    if (aListener != null)
      s_aListener.add (aListener);
  }

  public static void removeListeners (@Nonnull final Class <? extends EventListener> aListenerClass)
  {
    for (final EventListener aListener : ContainerHelper.newList (s_aListener))
      if (aListener.getClass ().equals (aListenerClass))
        s_aListener.remove (aListener);
  }

  public static void removeAllListeners ()
  {
    s_aListener.clear ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public static List <ServletContextListener> getAllServletContextListeners ()
  {
    final List <ServletContextListener> ret = new ArrayList <ServletContextListener> ();
    for (final EventListener aListener : s_aListener)
      if (aListener instanceof ServletContextListener)
        ret.add ((ServletContextListener) aListener);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static List <HttpSessionListener> getAllHttpSessionListeners ()
  {
    final List <HttpSessionListener> ret = new ArrayList <HttpSessionListener> ();
    for (final EventListener aListener : s_aListener)
      if (aListener instanceof HttpSessionListener)
        ret.add ((HttpSessionListener) aListener);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static List <ServletRequestListener> getAllServletRequestListeners ()
  {
    final List <ServletRequestListener> ret = new ArrayList <ServletRequestListener> ();
    for (final EventListener aListener : s_aListener)
      if (aListener instanceof ServletRequestListener)
        ret.add ((ServletRequestListener) aListener);
    return ret;
  }
}
