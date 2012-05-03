package com.phloc.scopes.web.singleton;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.commons.hash.HashCodeGenerator;

public final class MockSessionWebSingleton extends SessionWebSingleton
{
  private int i = 0;

  @Deprecated
  @UsedViaReflection
  public MockSessionWebSingleton ()
  {}

  @Nonnull
  public static MockSessionWebSingleton getInstance ()
  {
    return getSessionSingleton (MockSessionWebSingleton.class);
  }

  public void inc ()
  {
    i++;
  }

  public int get ()
  {
    return i;
  }

  // For testing!
  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof MockSessionWebSingleton))
      return false;
    return i == ((MockSessionWebSingleton) o).i;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (i).getHashCode ();
  }
}
