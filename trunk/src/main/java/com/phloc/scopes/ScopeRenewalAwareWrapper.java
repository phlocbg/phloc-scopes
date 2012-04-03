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

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.phloc.commons.ICloneable;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.compare.EqualsUtils;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.mutable.IReadonlyWrapper;
import com.phloc.commons.mutable.IWrapper;
import com.phloc.commons.state.EChange;
import com.phloc.commons.string.ToStringGenerator;

/**
 * This is a wrapper around any object that is scope renewal aware.
 * 
 * @author Philip
 * @param <DATATYPE>
 *        The type of object to be wrapped. Must implement {@link Serializable}.
 */
@NotThreadSafe
public final class ScopeRenewalAwareWrapper <DATATYPE extends Serializable> implements
                                                                            IWrapper <DATATYPE>,
                                                                            ICloneable <ScopeRenewalAwareWrapper <DATATYPE>>,
                                                                            IScopeRenewalAware
{
  private DATATYPE m_aObj;

  /**
   * Default constructor.
   */
  public ScopeRenewalAwareWrapper ()
  {}

  /**
   * Constructor with an existing object.
   * 
   * @param aObj
   *        The existing object. May be <code>null</code>.
   */
  public ScopeRenewalAwareWrapper (@Nullable final DATATYPE aObj)
  {
    m_aObj = aObj;
  }

  /**
   * Copy constructor. Only takes wrappers of the same type.
   * 
   * @param rhs
   *        The other wrapper to use. May not be <code>null</code>.
   */
  public ScopeRenewalAwareWrapper (@Nonnull final IReadonlyWrapper <DATATYPE> rhs)
  {
    if (rhs == null)
      throw new NullPointerException ("rhs");
    m_aObj = rhs.get ();
  }

  @Nullable
  public DATATYPE get ()
  {
    return m_aObj;
  }

  @Nonnull
  public EChange set (@Nullable final DATATYPE aObj)
  {
    if (EqualsUtils.nullSafeEquals (m_aObj, aObj))
      return EChange.UNCHANGED;
    m_aObj = aObj;
    return EChange.CHANGED;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ScopeRenewalAwareWrapper <DATATYPE> getClone ()
  {
    return new ScopeRenewalAwareWrapper <DATATYPE> (m_aObj);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof ScopeRenewalAwareWrapper <?>))
      return false;
    final ScopeRenewalAwareWrapper <?> rhs = (ScopeRenewalAwareWrapper <?>) o;
    return EqualsUtils.nullSafeEquals (m_aObj, rhs.m_aObj);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aObj).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("obj", m_aObj).toString ();
  }

  /**
   * Static factory method with automatic type deduction.
   * 
   * @param <DATATYPE>
   *        The type to be wrapped.
   * @param aObj
   *        The object to be wrapped.
   * @return The wrapped object.
   */
  @Nonnull
  public static <DATATYPE extends Serializable> ScopeRenewalAwareWrapper <DATATYPE> create (@Nullable final DATATYPE aObj)
  {
    return new ScopeRenewalAwareWrapper <DATATYPE> (aObj);
  }
}
