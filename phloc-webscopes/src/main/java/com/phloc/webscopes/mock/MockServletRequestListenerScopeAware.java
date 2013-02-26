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
package com.phloc.webscopes.mock;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.string.StringHelper;
import com.phloc.web.mock.MockServletRequestListener;
import com.phloc.webscopes.mgr.WebScopeManager;

/**
 * This mock listeners is responsible for creating
 * 
 * @author philip
 */
@ThreadSafe
public class MockServletRequestListenerScopeAware extends MockServletRequestListener
{
  /** The application ID to use. */
  public static final String MOCK_APPLICATION_ID = "mock.appid";

  private final String m_sApplicationID;

  public MockServletRequestListenerScopeAware ()
  {
    this (MOCK_APPLICATION_ID);
  }

  public MockServletRequestListenerScopeAware (@Nonnull @Nonempty final String sApplicationID)
  {
    if (StringHelper.hasNoText (sApplicationID))
      throw new IllegalArgumentException ("applicationID");
    m_sApplicationID = sApplicationID;
  }

  @Nonnull
  @Nonempty
  public String getApplicationID ()
  {
    return m_sApplicationID;
  }

  @Override
  public void requestInitialized (@Nonnull final ServletRequestEvent aEvent)
  {
    super.requestInitialized (aEvent);
    WebScopeManager.onRequestBegin (m_sApplicationID,
                                    (HttpServletRequest) aEvent.getServletRequest (),
                                    getCurrentMockResponse ());
  }

  @Override
  public void requestDestroyed (@Nonnull final ServletRequestEvent aEvent)
  {
    WebScopeManager.onRequestEnd ();
    super.requestDestroyed (aEvent);
  }
}
