package com.phloc.scopes.nonweb.mock;

import java.io.File;

import javax.annotation.Nonnull;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.phloc.commons.annotations.OverrideOnDemand;

public class ScopeTestRule implements TestRule
{
  public static final File STORAGE_PATH = new File ("target/junittest").getAbsoluteFile ();

  @Nonnull
  public final Statement apply (@Nonnull final Statement aBase, final Description aDescription)
  {
    return new Statement ()
    {
      @Override
      public void evaluate () throws Throwable
      {
        before ();
        try
        {
          aBase.evaluate ();
        }
        finally
        {
          after ();
        }
      }
    };
  }

  @OverrideOnDemand
  protected void before ()
  {
    ScopeAwareTestSetup.setupScopeTests ();
  }

  @OverrideOnDemand
  protected void after ()
  {
    ScopeAwareTestSetup.shutdownScopeTests ();
  }
}
