package com.phloc.scopes.web.fileupload;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.lang.SPIHelper;
import com.phloc.scopes.nonweb.singleton.GlobalSingleton;

public class ProgressListenerProvider extends GlobalSingleton
{
  private final IProgressListenerProvider m_aListernerProvider;
  private static final Logger s_aLogger = LoggerFactory.getLogger (ProgressListenerProvider.class);

  @Deprecated
  @UsedViaReflection
  public ProgressListenerProvider ()
  {
    final Collection <IProgressListenerProvider> aProviders = SPIHelper.getAllSPIImplementations (IProgressListenerProvider.class);
    if (aProviders.size () > 1)
    {
      s_aLogger.warn ("Found multiple providers for upload progress listeners, taking first one!");
    }
    this.m_aListernerProvider = ContainerHelper.getFirstElement (aProviders);
    if (this.m_aListernerProvider != null)
    {
      s_aLogger.info ("Using progress listener provider: " + this.m_aListernerProvider.getClass ().getName ());
    }
  }

  @Nonnull
  public static ProgressListenerProvider getInstance ()
  {
    return getGlobalSingleton (ProgressListenerProvider.class);
  }

  @Nullable
  public IProgressListener getProgressListener ()
  {
    return this.m_aListernerProvider == null ? null : this.m_aListernerProvider.getProgressListener ();
  }
}
