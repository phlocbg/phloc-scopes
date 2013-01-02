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

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

import com.phloc.scopes.web.mock.MockHttpServletRequest;

final class MockHttpServletRequestFactory
{
  @Nonnull
  public static HttpServletRequest createHttpServletRequestWithNullContentType ()
  {
    final byte [] requestData = "foobar".getBytes ();
    return MockHttpServletRequest.createWithContent (requestData);
  }

  public static HttpServletRequest createValidHttpServletRequest (final String [] strFileNames)
  {
    // todo - provide a real implementation

    final StringBuffer sbRequestData = new StringBuffer ();

    for (final String strFileName : strFileNames)
    {
      sbRequestData.append (strFileName);
    }

    byte [] requestData = null;
    requestData = sbRequestData.toString ().getBytes ();

    return MockHttpServletRequest.createWithContent (requestData, AbstractFileUploadBase.MULTIPART_FORM_DATA);
  }

  public static HttpServletRequest createInvalidHttpServletRequest ()
  {
    final byte [] requestData = "foobar".getBytes ();
    return MockHttpServletRequest.createWithContent (requestData, AbstractFileUploadBase.MULTIPART_FORM_DATA);
  }
}
