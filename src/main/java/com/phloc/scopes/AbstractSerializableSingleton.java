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
package com.phloc.scopes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.commons.callback.INonThrowingRunnableWithParameter;

/**
 * Base class for all singletons that implement the {@link Serializable}
 * interface.
 * 
 * @author philip
 */
public abstract class AbstractSerializableSingleton extends AbstractSingleton implements Serializable
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractSerializableSingleton.class);

  @Deprecated
  @UsedViaReflection ("For Serializable interface implementation in derived classes!")
  protected AbstractSerializableSingleton ()
  {}

  /**
   * Ctor.
   * 
   * @param sRequiredMethodName
   *        The required method name to check for the correct invocation of the
   *        singleton. Only checked in debugMode.
   */
  @UsedViaReflection
  protected AbstractSerializableSingleton (@Nonnull final String sRequiredMethodName)
  {
    super (sRequiredMethodName);
  }

  private void readObject (@Nonnull final ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    in.defaultReadObject ();
    registerSingletonAfterRead ();
  }

  private void writeObject (@Nonnull final ObjectOutputStream out) throws IOException
  {
    out.defaultWriteObject ();
  }

  /**
   * This method is purely for registering this instance, after reading from a
   * serialized stream.
   */
  protected final void registerSingletonAfterRead ()
  {
    registerSingletonAfterRead (true);
  }

  /**
   * This method is purely for registering this instance, after reading from a
   * serialized stream.
   * 
   * @param bAllowOverwrite
   *        if <code>true</code> only a warning is emitted, if the scope of this
   *        object already contains another singleton of this class and
   *        therefore overwrites this assignment, else and exception is thrown.
   */
  protected final void registerSingletonAfterRead (final boolean bAllowOverwrite)
  {
    final String sSingletonScopeKey = getSingletonScopeKey (getClass ());
    final IScope aScope = getScope ();
    aScope.runAtomic (new INonThrowingRunnableWithParameter <IScope> ()
    {
      public void run (@Nullable final IScope aInnerScope)
      {
        final AbstractSerializableSingleton aSingleton = AbstractSerializableSingleton.this;
        if (aScope.containsAttribute (sSingletonScopeKey))
        {
          final String sMsg = "The scope " +
                              aScope.getID () +
                              " already has a singleton of class " +
                              aSingleton.getClass ();
          if (bAllowOverwrite)
            s_aLogger.warn (sMsg + " - overwriting it!");
          else
            throw new IllegalStateException (sMsg);
        }

        // Set the abstract singleton in the scope and not this runnable...
        aScope.setAttribute (sSingletonScopeKey, aSingleton);
      }
    });
  }
}
