package com.phloc.scopes.util;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.callback.INonThrowingCallable;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.scopes.mgr.ScopeManager;
import com.phloc.scopes.mock.ScopeAwareTestSetup;

/**
 * Abstract implementation of {@link Callable} that handles WebScopes correctly.
 * 
 * @author philip
 * @param <DATATYPE>
 *        The return type of the function.
 */
public abstract class AbstractScopeAwareCallable <DATATYPE> implements INonThrowingCallable <DATATYPE>
{
  private final String m_sApplicationID;
  private final String m_sRequestID;
  private final String m_sSessionID;

  public AbstractScopeAwareCallable ()
  {
    this (ScopeManager.getApplicationScope ().getID (),
          ScopeAwareTestSetup.MOCK_REQUEST_SCOPE_ID,
          ScopeAwareTestSetup.MOCK_SESSION_SCOPE_ID);
  }

  public AbstractScopeAwareCallable (@Nonnull @Nonempty final String sApplicationID,
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
   * Implement your code in here
   * 
   * @return The return value of the {@link #call()} method.
   */
  @Nullable
  protected abstract DATATYPE scopedRun ();

  @Nullable
  public final DATATYPE call ()
  {
    ScopeManager.onRequestBegin (m_sApplicationID, m_sRequestID, m_sSessionID);
    try
    {
      final DATATYPE ret = scopedRun ();
      return ret;
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
