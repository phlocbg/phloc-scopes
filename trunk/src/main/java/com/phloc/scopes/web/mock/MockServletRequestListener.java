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
package com.phloc.scopes.web.mock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.string.StringHelper;
import com.phloc.scopes.web.mgr.WebScopeManager;

/**
 * This mock listeners is responsible for creating
 * 
 * @author philip
 */
@ThreadSafe
public final class MockServletRequestListener implements ServletRequestListener
{
  /** The application ID to use. */
  public static final String MOCK_APPLICATION_ID = "mock.appid";

  private final String m_sApplicationID;
  private MockHttpServletResponse m_aResp;

  public MockServletRequestListener ()
  {
    this (MOCK_APPLICATION_ID);
  }

  public MockServletRequestListener (@Nonnull @Nonempty final String sApplicationID)
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

  public void requestInitialized (@Nonnull final ServletRequestEvent aEvent)
  {
    m_aResp = new MockHttpServletResponse ();
    WebScopeManager.onRequestBegin (m_sApplicationID, (HttpServletRequest) aEvent.getServletRequest (), m_aResp);
  }

  @Nullable
  public MockHttpServletResponse getCurrentMockResponse ()
  {
    return m_aResp;
  }

  public void requestDestroyed (@Nonnull final ServletRequestEvent aEvent)
  {
    WebScopeManager.onRequestEnd ();
    m_aResp = null;
  }
}
