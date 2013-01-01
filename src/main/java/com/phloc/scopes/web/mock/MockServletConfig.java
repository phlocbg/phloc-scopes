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
package com.phloc.scopes.web.mock;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import com.phloc.commons.collections.ContainerHelper;

/**
 * Mock implementation of the {@link ServletConfig} interface.
 * 
 * @author philip
 */
@NotThreadSafe
public class MockServletConfig implements ServletConfig
{
  private final ServletContext m_aSC;
  private final String m_sServletName;
  private final Map <String, String> m_aInitParams = new HashMap <String, String> ();

  public MockServletConfig (@Nonnull final ServletContext aSC, @Nonnull final String sServletName)
  {
    this (aSC, sServletName, null);
  }

  public MockServletConfig (@Nonnull final ServletContext aSC,
                            @Nonnull final String sServletName,
                            @Nullable final Map <String, String> aInitParams)
  {
    if (aSC == null)
      throw new NullPointerException ("servletContext");
    if (sServletName == null)
      throw new NullPointerException ("name");
    m_aSC = aSC;
    m_sServletName = sServletName;
    if (aInitParams != null)
      m_aInitParams.putAll (aInitParams);
  }

  @Nonnull
  public String getServletName ()
  {
    return m_sServletName;
  }

  @Nonnull
  public ServletContext getServletContext ()
  {
    return m_aSC;
  }

  @Nullable
  public String getInitParameter (@Nullable final String sName)
  {
    return m_aInitParams.get (sName);
  }

  @Nonnull
  public Enumeration <String> getInitParameterNames ()
  {
    return ContainerHelper.getEnumeration (m_aInitParams.keySet ());
  }
}
