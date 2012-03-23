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

import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.GlobalDebug;
import com.phloc.commons.annotations.OverrideOnDemand;
import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.commons.callback.INonThrowingRunnableWithParameter;
import com.phloc.commons.exceptions.LoggedRuntimeException;
import com.phloc.commons.lang.ClassHelper;
import com.phloc.commons.mutable.MutableBoolean;
import com.phloc.commons.mutable.Wrapper;
import com.phloc.commons.priviledged.PrivilegedActionAccessibleObjectSetAccessible;
import com.phloc.commons.stats.IStatisticsHandlerKeyedCounter;
import com.phloc.commons.stats.StatisticsManager;
import com.phloc.commons.string.StringHelper;

/**
 * Base class for all singletons.
 * 
 * @author philip
 */
public abstract class AbstractSingleton implements IScopeDestructionAware
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractSingleton.class);
  private static final IStatisticsHandlerKeyedCounter s_aStatsCounterInstantiate = StatisticsManager.getKeyedCounterHandler (AbstractSingleton.class);

  @Deprecated
  @UsedViaReflection ("For Serializable interface implementation in derived classes!")
  protected AbstractSingleton ()
  {}

  /**
   * Ctor.
   * 
   * @param sRequiredMethodName
   *        The required method name to check for the correct invocation of the
   *        singleton. Only checked in debugMode.
   */
  @UsedViaReflection
  protected AbstractSingleton (@Nonnull final String sRequiredMethodName)
  {
    if (StringHelper.hasNoText (sRequiredMethodName))
      throw new IllegalArgumentException ("requiredMethodName");

    // Check the call stack to avoid manual instantiation
    // Only required while developing
    if (GlobalDebug.isDebugMode ())
    {
      boolean bFound = false;

      // check if this method is called indirectly via the
      // correct method...
      for (final StackTraceElement aStackTraceElement : Thread.currentThread ().getStackTrace ())
      {
        final String sMethodName = aStackTraceElement.getMethodName ();
        if (sMethodName.equals (sRequiredMethodName))
        {
          bFound = true;
          break;
        }

        // Special handling when deserializing from a stream
        if (aStackTraceElement.getClassName ().equals (ObjectInputStream.class.getName ()) &&
            sMethodName.equals ("readOrdinaryObject"))
        {
          bFound = true;
          break;
        }
      }

      if (!bFound)
        throw new IllegalStateException ("You cannot instantiate the class " +
                                         getClass ().getName () +
                                         " manually! Use the method " +
                                         sRequiredMethodName +
                                         " instead!");
    }
  }

  /**
   * Called after the singleton was instantiated. The constructor has finished,
   * and calling getInstance will work!
   */
  @OverrideOnDemand
  protected void onAfterInstantiation ()
  {}

  /**
   * Called when the singleton is destroyed. Perform all cleanup in this method.
   * 
   * @throws Exception
   *         If something goes wrong
   */
  @OverrideOnDemand
  protected void onDestroy () throws Exception
  {}

  /*
   * Implementation of IScopeDestructionAware
   */
  public final void onScopeDestruction () throws Exception
  {
    onDestroy ();
  }

  @Nonnull
  protected abstract IScope getScope ();

  @Nonnull
  protected final String getScopeID ()
  {
    return getScope ().getID ();
  }

  /**
   * Create the key which is used to reference the object within the scope.
   * 
   * @param aClass
   *        The class for which the key is to be created. May not be
   *        <code>null</code>.
   * @return The non-<code>null</code> key.
   */
  @Nonnull
  private static String _getSingletonScopeKey (@Nonnull final Class <?> aClass)
  {
    // Preallocate some bytes
    return new StringBuilder (255).append ("singleton.").append (aClass.getName ()).toString ();
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
    final String sSingletonScopeKey = _getSingletonScopeKey (getClass ());
    getScope ().runAtomic (new INonThrowingRunnableWithParameter <IScope> ()
    {
      public void run (@Nonnull final IScope aInnerScope)
      {
        if (aInnerScope.containsAttribute (sSingletonScopeKey))
        {
          final String sMsg = "The scope " + aInnerScope.getID () + " already has a singleton of class " + getClass ();
          if (bAllowOverwrite)
            s_aLogger.warn (sMsg + " - overwriting it!");
          else
            throw new IllegalStateException (sMsg);
        }

        // Set the abstract singleton in the scope and not this runnable...
        aInnerScope.setAttribute (sSingletonScopeKey, AbstractSingleton.this);
      }
    });
  }

  @Nonnull
  private static <T> T _instantiateSingleton (@Nonnull final Class <T> aClass, @Nonnull final IScope aScope)
  {
    // create new object in passed scope
    try
    {
      if (s_aLogger.isDebugEnabled ())
        s_aLogger.debug ("Created singleton for '" + aClass + "' in scope " + aScope.toString ());

      // Check if class is public, non-abstract etc.
      if (!ClassHelper.isInstancableClass (aClass))
        throw new IllegalStateException ("Class " + aClass + " is not instancable!");

      final Constructor <T> aCtor = aClass.getDeclaredConstructor ((Class <?> []) null);

      // Ubuntu: java.security.AccessControlException: access denied
      // (java.lang.reflect.ReflectPermission suppressAccessChecks)
      if (false)
        AccessController.doPrivileged (new PrivilegedActionAccessibleObjectSetAccessible (aCtor));

      // Invoke default ctor
      return aCtor.newInstance ((Object []) null);
    }
    catch (final Throwable t)
    {
      throw LoggedRuntimeException.newException (t);
    }
  }

  @Nonnull
  protected static <T extends AbstractSingleton> T getSingleton (@Nonnull final IScope aScope,
                                                                 @Nonnull final Class <T> aClass)
  {
    final String sSingletonScopeKey = _getSingletonScopeKey (aClass);

    // check if contained in passed scope
    T aInstance = aClass.cast (aScope.getCastedAttribute (sSingletonScopeKey));
    if (aInstance == null)
    {
      final MutableBoolean aFinalWasInstantiated = new MutableBoolean (false);
      final Wrapper <T> aFinalInstance = new Wrapper <T> ();

      aScope.runAtomic (new INonThrowingRunnableWithParameter <IScope> ()
      {
        public void run (final IScope aInnerScope)
        {
          // try to resolve again in case it was set in the meantime
          T aInnerInstance = aClass.cast (aInnerScope.getCastedAttribute (sSingletonScopeKey));
          if (aInnerInstance == null)
          {
            aInnerInstance = _instantiateSingleton (aClass, aInnerScope);
            aInnerScope.setAttribute (sSingletonScopeKey, aInnerInstance);
            aFinalWasInstantiated.set (true);
            s_aStatsCounterInstantiate.increment (sSingletonScopeKey);
          }
          aFinalInstance.set (aInnerInstance);
        }
      });

      // Extract from wrapper
      aInstance = aFinalInstance.get ();

      // Call outside the sync block, and after the instance was registered in
      // the scope
      if (aFinalWasInstantiated.booleanValue ())
        aInstance.onAfterInstantiation ();
    }
    return aInstance;
  }

  @Nonnull
  protected static final <T extends AbstractSingleton> List <T> getAllSingletons (@Nonnull final IScope aScope,
                                                                                  @Nonnull final Class <T> aDesiredClass)
  {
    final List <T> ret = new ArrayList <T> ();
    for (final String sAttrName : aScope.getAllAttributeNames ())
    {
      final Object aScopeValue = aScope.getAttributeObject (sAttrName);
      if (aScopeValue != null && aDesiredClass.isAssignableFrom (aScopeValue.getClass ()))
        ret.add (aDesiredClass.cast (aScopeValue));
    }
    return ret;
  }
}
