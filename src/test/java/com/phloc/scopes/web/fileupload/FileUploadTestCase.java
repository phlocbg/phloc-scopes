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
package com.phloc.scopes.web.fileupload;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import com.phloc.scopes.web.fileupload.io.DiskFileItemFactory;
import com.phloc.scopes.web.fileupload.servlet.ServletFileUpload;
import com.phloc.scopes.web.mock.MockHttpServletRequest;

/**
 * Base class for deriving test cases.
 */
public abstract class FileUploadTestCase extends TestCase
{
  protected static final String CONTENT_TYPE = "multipart/form-data; boundary=---1234";

  protected List <IFileItem> parseUpload (final byte [] bytes) throws FileUploadException
  {
    return parseUpload (bytes, CONTENT_TYPE);
  }

  protected List <IFileItem> parseUpload (final byte [] bytes, final String contentType) throws FileUploadException
  {
    final ServletFileUpload upload = new ServletFileUpload (new DiskFileItemFactory (10240));
    final HttpServletRequest request = MockHttpServletRequest.createWithContent (bytes, contentType);

    final List <IFileItem> fileItems = upload.parseRequest (request);
    return fileItems;
  }

  protected List <IFileItem> parseUpload (final String content) throws UnsupportedEncodingException, FileUploadException
  {
    final byte [] bytes = content.getBytes ("US-ASCII");
    return parseUpload (bytes, CONTENT_TYPE);
  }
}
