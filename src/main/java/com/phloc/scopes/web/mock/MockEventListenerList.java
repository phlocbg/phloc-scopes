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
import com.phloc.commons.state.EChange;

/**
 * This class holds the different listeners ({@link ServletContextListener} ,
 * {@link HttpSessionListener} and {@link ServletRequestListener})
 * 
 * @author philip
 */
@NotThreadSafe
public final class MockEventListenerList
{
  private final List <EventListener> m_aListener = new ArrayList <EventListener> ();

  public MockEventListenerList ()
  {}

  @Nonnull
  public EChange setFrom (@Nonnull final MockEventListenerList aList)
  {
    if (m_aListener.isEmpty () && aList.m_aListener.isEmpty ())
      return EChange.UNCHANGED;

    m_aListener.clear ();
    m_aListener.addAll (aList.m_aListener);
    return EChange.CHANGED;
  }

  @Nonnull
  public EChange addListener (@Nullable final EventListener aListener)
  {
    if (aListener == null)
      return EChange.UNCHANGED;
    return EChange.valueOf (m_aListener.add (aListener));
  }

  @Nonnull
  public EChange removeListeners (@Nullable final Class <? extends EventListener> aListenerClass)
  {
    EChange ret = EChange.UNCHANGED;
    if (aListenerClass != null)
    {
      // Create a copy of the list
      for (final EventListener aListener : ContainerHelper.newList (m_aListener))
        if (aListener.getClass ().equals (aListenerClass))
          ret = ret.or (EChange.valueOf (m_aListener.remove (aListener)));
    }
    return ret;
  }

  @Nonnull
  public EChange removeAllListeners ()
  {
    if (m_aListener.isEmpty ())
      return EChange.UNCHANGED;
    m_aListener.clear ();
    return EChange.CHANGED;
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <ServletContextListener> getAllServletContextListeners ()
  {
    final List <ServletContextListener> ret = new ArrayList <ServletContextListener> ();
    for (final EventListener aListener : m_aListener)
      if (aListener instanceof ServletContextListener)
        ret.add ((ServletContextListener) aListener);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <HttpSessionListener> getAllHttpSessionListeners ()
  {
    final List <HttpSessionListener> ret = new ArrayList <HttpSessionListener> ();
    for (final EventListener aListener : m_aListener)
      if (aListener instanceof HttpSessionListener)
        ret.add ((HttpSessionListener) aListener);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <ServletRequestListener> getAllServletRequestListeners ()
  {
    final List <ServletRequestListener> ret = new ArrayList <ServletRequestListener> ();
    for (final EventListener aListener : m_aListener)
      if (aListener instanceof ServletRequestListener)
        ret.add ((ServletRequestListener) aListener);
    return ret;
  }
}
