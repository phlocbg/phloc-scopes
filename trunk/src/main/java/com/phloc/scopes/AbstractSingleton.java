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
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.GlobalDebug;
import com.phloc.commons.annotations.OverrideOnDemand;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.commons.callback.INonThrowingCallableWithParameter;
import com.phloc.commons.exceptions.LoggedRuntimeException;
import com.phloc.commons.lang.ClassHelper;
import com.phloc.commons.mutable.MutableBoolean;
import com.phloc.commons.priviledged.PrivilegedActionAccessibleObjectSetAccessible;
import com.phloc.commons.stats.IStatisticsHandlerKeyedCounter;
import com.phloc.commons.stats.StatisticsManager;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;

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
  public static String getSingletonScopeKey (@Nonnull final Class <? extends AbstractSingleton> aClass)
  {
    if (aClass == null)
      throw new NullPointerException ("class");

    // Preallocate some bytes
    return new StringBuilder (255).append ("singleton.").append (aClass.getName ()).toString ();
  }

  /**
   * Check if a singleton is already instantiated inside a scope
   * 
   * @param aScope
   *        The scope to check. May be <code>null</code>.
   * @param aClass
   *        The class to be checked.
   * @return <code>true</code> if the singleton for the specified class is
   *         already instantiated, <code>false</code> otherwise.
   */
  protected static final boolean isSingletonInstantiated (@Nullable final IScope aScope,
                                                          @Nonnull final Class <? extends AbstractSingleton> aClass)
  {
    if (aClass == null)
      throw new NullPointerException ("class");

    if (aScope == null)
      return false;
    final String sSingletonScopeKey = getSingletonScopeKey (aClass);
    final AbstractSingleton aInstance = aClass.cast (aScope.getAttributeObject (sSingletonScopeKey));
    return aInstance != null;
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

  /**
   * Get the singleton object in the passed scope, using the passed class. If
   * the singleton is not yet instantiated, a new instance is created.
   * 
   * @param aScope
   *        The scope to be used. May not be <code>null</code>.
   * @param aClass
   *        The class to be used.
   * @return The singleton object.
   */
  @Nonnull
  protected static final <T extends AbstractSingleton> T getSingleton (@Nonnull final IScope aScope,
                                                                       @Nonnull final Class <T> aClass)
  {
    if (aScope == null)
      throw new NullPointerException ("scope");
    if (aClass == null)
      throw new NullPointerException ("class");

    final String sSingletonScopeKey = getSingletonScopeKey (aClass);

    // check if contained in passed scope
    T aInstance = aClass.cast (aScope.getAttributeObject (sSingletonScopeKey));
    if (aInstance == null)
    {
      // Some final objects to access them from the nested inner class
      final MutableBoolean aFinalWasInstantiated = new MutableBoolean (false);

      // Safe instantiation:
      aInstance = aScope.runAtomic (new INonThrowingCallableWithParameter <T, IScope> ()
      {
        public T call (@Nullable final IScope aInnerScope)
        {
          // try to resolve again in case it was set in the meantime
          T aInnerInstance = aClass.cast (aScope.getAttributeObject (sSingletonScopeKey));
          if (aInnerInstance == null)
          {
            // Main instantiation
            aInnerInstance = _instantiateSingleton (aClass, aScope);

            // Set in scope
            aScope.setAttribute (sSingletonScopeKey, aInnerInstance);

            // Remember that we instantiated the object
            aFinalWasInstantiated.set (true);

            // And some statistics
            s_aStatsCounterInstantiate.increment (sSingletonScopeKey);
          }

          // We have the instance - maybe from re-querying the scope, maybe from
          // instantiation
          return aInnerInstance;
        }
      });

      // Call outside the sync block, and after the instance was registered in
      // the scope
      if (aFinalWasInstantiated.booleanValue ())
        aInstance.onAfterInstantiation ();
    }
    return aInstance;
  }

  @Nonnull
  @ReturnsMutableCopy
  protected static final <T extends AbstractSingleton> List <T> getAllSingletons (@Nullable final IScope aScope,
                                                                                  @Nonnull final Class <T> aDesiredClass)
  {
    if (aDesiredClass == null)
      throw new NullPointerException ("desiredClass");

    final List <T> ret = new ArrayList <T> ();
    if (aScope != null)
      for (final String sAttrName : aScope.getAllAttributeNames ())
      {
        final Object aScopeValue = aScope.getAttributeObject (sAttrName);
        if (aScopeValue != null && aDesiredClass.isAssignableFrom (aScopeValue.getClass ()))
          ret.add (aDesiredClass.cast (aScopeValue));
      }
    return ret;
  }

  @Override
  @Nonnull
  public String toString ()
  {
    return new ToStringGenerator (this).toString ();
  }
}
