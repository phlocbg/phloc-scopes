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
package com.phloc.scopes;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;

/**
 * Global scope utility methods that don't nicely fit somewhere else.
 * 
 * @author philip
 */
@ThreadSafe
public final class ScopeUtils
{
  private static final boolean DEFAULT_DEBUG_LIFE_CYCLE = false;

  private static final ReadWriteLock s_aRWLock = new ReentrantReadWriteLock ();
  private static boolean s_bLifeCycleDebugging = DEFAULT_DEBUG_LIFE_CYCLE;

  private ScopeUtils ()
  {}

  /**
   * Enable or disable scope life cycle debugging.
   * 
   * @param bDebugLifeCycle
   *        <code>true</code> if the scope life cycle should be debugged,
   *        <code>false</code> to disable it. By default is is disabled.
   */
  public static void setLifeCycleDebuggingEnabled (final boolean bDebugLifeCycle)
  {
    s_aRWLock.writeLock ().lock ();
    try
    {
      s_bLifeCycleDebugging = bDebugLifeCycle;
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }

  /**
   * @return <code>true</code> if scope life cycle debugging is enabled,
   *         <code>false</code> if it is disabled. The default value is
   *         disabled.
   */
  public static boolean isLifeCycleDebuggingEnabled ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      return s_bLifeCycleDebugging;
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * This is a just a helper method to determine whether scope creation/deletion
   * issues should be logged or not.
   * 
   * @param aLogger
   *        The logger to check.
   * @return <code>true</code> if scope creation/deletion should be logged,
   *         <code>false</code> otherwise.
   */
  public static boolean debugScopeLifeCycle (@Nonnull final Logger aLogger)
  {
    return isLifeCycleDebuggingEnabled () && aLogger.isInfoEnabled ();
  }
}
