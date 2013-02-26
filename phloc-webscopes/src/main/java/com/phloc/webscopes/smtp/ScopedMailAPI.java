package com.phloc.webscopes.smtp;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.commons.state.ESuccess;
import com.phloc.scopes.nonweb.singleton.GlobalSingleton;
import com.phloc.web.smtp.IEmailData;
import com.phloc.web.smtp.queue.MailAPI;
import com.phloc.web.smtp.settings.ISMTPSettings;

/**
 * Scope aware wrapper around {@link MailAPI} class so that it is gracefully
 * stopped when the global scope is stopped.
 * 
 * @author philip
 */
public final class ScopedMailAPI extends GlobalSingleton
{
  @UsedViaReflection
  @Deprecated
  public ScopedMailAPI ()
  {}

  @Nonnull
  public static ScopedMailAPI getInstance ()
  {
    return getGlobalSingleton (ScopedMailAPI.class);
  }

  /**
   * Unconditionally queue a mail
   * 
   * @param aSMTPSettings
   *        The SMTP settings to use. May not be <code>null</code>.
   * @param aMailData
   *        The data of the email to be send. May not be <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  public ESuccess queueMail (@Nonnull final ISMTPSettings aSMTPSettings, @Nonnull final IEmailData aMailData)
  {
    return MailAPI.queueMail (aSMTPSettings, aMailData);
  }

  @Nonnegative
  public int getTotalQueueLength ()
  {
    return MailAPI.getTotalQueueLength ();
  }

  @Override
  protected void onDestroy ()
  {
    // Stop mail queues
    MailAPI.stop ();
  }
}
