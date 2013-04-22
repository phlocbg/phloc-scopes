package com.phloc.scopes.util;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.callback.INonThrowingRunnable;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.scopes.mgr.ScopeManager;
import com.phloc.scopes.mock.ScopeAwareTestSetup;

/**
 * Abstract implementation of {@link Runnable} that handles WebScopes correctly.
 * 
 * @author Philip Helger
 */
public abstract class AbstractScopeAwareRunnable implements INonThrowingRunnable
{
  private final String m_sApplicationID;
  private final String m_sRequestID;
  private final String m_sSessionID;

  public AbstractScopeAwareRunnable ()
  {
    this (ScopeManager.getApplicationScope ().getID (),
          ScopeAwareTestSetup.MOCK_REQUEST_SCOPE_ID,
          ScopeAwareTestSetup.MOCK_SESSION_SCOPE_ID);
  }

  public AbstractScopeAwareRunnable (@Nonnull @Nonempty final String sApplicationID,
                                     @Nonnull @Nonempty final String sRequestID,
                                     @Nonnull @Nonempty final String sSessionID)
  {
    if (StringHelper.hasNoText (sApplicationID))
      throw new IllegalArgumentException ("applicationID");
    if (StringHelper.hasNoText (sRequestID))
      throw new IllegalArgumentException ("requestID");
    if (StringHelper.hasNoText (sSessionID))
      throw new IllegalArgumentException ("sessionID");

    m_sApplicationID = sApplicationID;
    m_sRequestID = sRequestID;
    m_sSessionID = sSessionID;
  }

  /**
   * Implement your code in here.
   */
  protected abstract void scopedRun ();

  public final void run ()
  {
    ScopeManager.onRequestBegin (m_sApplicationID, m_sRequestID, m_sSessionID);
    try
    {
      scopedRun ();
    }
    finally
    {
      ScopeManager.onRequestEnd ();
    }
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("applicationID", m_sApplicationID)
                                       .append ("requestID", m_sRequestID)
                                       .append ("sessionID", m_sSessionID)
                                       .toString ();
  }
}
