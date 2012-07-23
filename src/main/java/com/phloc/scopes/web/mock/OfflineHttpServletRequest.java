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

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.servlet.ServletContext;

/**
 * A special {@link MockHttpServletRequest} that throws
 * {@link UnsupportedOperationException} exceptions for server or path related
 * queries. So it is "offline" only :)
 * 
 * @author philip
 */
@NotThreadSafe
public final class OfflineHttpServletRequest extends MockHttpServletRequest
{
  public OfflineHttpServletRequest ()
  {
    super ();
  }

  public OfflineHttpServletRequest (@Nullable final ServletContext aSC)
  {
    super (aSC);
  }

  @Override
  public String getScheme ()
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  public String getServerName ()
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  public String getProtocol ()
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  public int getServerPort ()
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  public String getMethod ()
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  public String getPathInfo ()
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  public String getPathTranslated ()
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  public String getQueryString ()
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  public String getRemoteHost ()
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  public String getRemoteAddr ()
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  public String getAuthType ()
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  public int getRemotePort ()
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  public String getRemoteUser ()
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  public String getContentType ()
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  public int getContentLength ()
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  public String getServletPath ()
  {
    throw new UnsupportedOperationException ();
  }
}
