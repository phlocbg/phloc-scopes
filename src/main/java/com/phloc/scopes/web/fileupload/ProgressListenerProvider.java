/**
 * Copyright (C) 2006-2013 phloc systems
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
package com.phloc.scopes.web.fileupload;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.lang.ServiceLoaderUtils;
import com.phloc.scopes.nonweb.singleton.GlobalSingleton;

public class ProgressListenerProvider extends GlobalSingleton
{
  private final IProgressListenerProvider m_aListernerProvider;
  private static final Logger s_aLogger = LoggerFactory.getLogger (ProgressListenerProvider.class);

  @Deprecated
  @UsedViaReflection
  public ProgressListenerProvider ()
  {
    final Collection <IProgressListenerProvider> aProviders = ServiceLoaderUtils.getAllSPIImplementations (IProgressListenerProvider.class);
    if (aProviders.size () > 1)
      s_aLogger.warn ("Found multiple providers for upload progress listeners, taking first one!");
    m_aListernerProvider = ContainerHelper.getFirstElement (aProviders);
    if (m_aListernerProvider != null)
      s_aLogger.info ("Using progress listener provider: " + m_aListernerProvider.getClass ().getName ());
  }

  @Nonnull
  public static ProgressListenerProvider getInstance ()
  {
    return getGlobalSingleton (ProgressListenerProvider.class);
  }

  @Nullable
  public IProgressListener getProgressListener ()
  {
    return m_aListernerProvider == null ? null : m_aListernerProvider.getProgressListener ();
  }
}
