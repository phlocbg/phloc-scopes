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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.phloc.commons.charset.CCharset;
import com.phloc.scopes.web.fileupload.io.DiskFileItemFactory;
import com.phloc.scopes.web.fileupload.servlet.ServletFileUpload;
import com.phloc.scopes.web.mock.MockHttpServletRequest;

/**
 * Unit test for items with varying sizes.
 */
public class SizesTest extends FileUploadTestCase
{
  /**
   * Runs a test with varying file sizes.
   */
  public void testFileUpload () throws IOException, FileUploadException
  {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream ();
    int add = 16;
    int num = 0;
    for (int i = 0; i < 16384; i += add)
    {
      if (++add == 32)
      {
        add = 16;
      }
      final String header = "-----1234\r\n" +
                            "Content-Disposition: form-data; name=\"field" +
                            (num++) +
                            "\"\r\n" +
                            "\r\n";
      baos.write (header.getBytes ("US-ASCII"));
      for (int j = 0; j < i; j++)
      {
        baos.write ((byte) j);
      }
      baos.write ("\r\n".getBytes ("US-ASCII"));
    }
    baos.write ("-----1234--\r\n".getBytes ("US-ASCII"));

    final List <IFileItem> fileItems = parseUpload (baos.toByteArray ());
    final Iterator <IFileItem> fileIter = fileItems.iterator ();
    add = 16;
    num = 0;
    for (int i = 0; i < 16384; i += add)
    {
      if (++add == 32)
      {
        add = 16;
      }
      final IFileItem item = fileIter.next ();
      assertEquals ("field" + (num++), item.getFieldName ());
      final byte [] bytes = item.get ();
      assertEquals (i, bytes.length);
      for (int j = 0; j < i; j++)
      {
        assertEquals ((byte) j, bytes[j]);
      }
    }
    assertTrue (!fileIter.hasNext ());
  }

  /**
   * Checks, whether limiting the file size works.
   */
  public void testFileSizeLimit () throws IOException, FileUploadException
  {
    final String request = "-----1234\r\n"
                           + "Content-Disposition: form-data; name=\"file\"; filename=\"foo.tab\"\r\n"
                           + "Content-Type: text/whatever\r\n"
                           + "\r\n"
                           + "This is the content of the file\n"
                           + "\r\n"
                           + "-----1234--\r\n";

    ServletFileUpload upload = new ServletFileUpload (new DiskFileItemFactory (10240));
    upload.setFileSizeMax (-1);
    HttpServletRequest req = MockHttpServletRequest.createWithContent (request.getBytes (CCharset.CHARSET_US_ASCII),
                                                                       CONTENT_TYPE);
    List <IFileItem> fileItems = upload.parseRequest (req);
    assertEquals (1, fileItems.size ());
    IFileItem item = fileItems.get (0);
    assertEquals ("This is the content of the file\n", new String (item.get ()));

    upload = new ServletFileUpload (new DiskFileItemFactory (10240));
    upload.setFileSizeMax (40);
    req = MockHttpServletRequest.createWithContent (request.getBytes (CCharset.CHARSET_US_ASCII), CONTENT_TYPE);
    fileItems = upload.parseRequest (req);
    assertEquals (1, fileItems.size ());
    item = fileItems.get (0);
    assertEquals ("This is the content of the file\n", new String (item.get ()));

    upload = new ServletFileUpload (new DiskFileItemFactory (10240));
    upload.setFileSizeMax (30);
    req = MockHttpServletRequest.createWithContent (request.getBytes (CCharset.CHARSET_US_ASCII), CONTENT_TYPE);
    try
    {
      upload.parseRequest (req);
      fail ("Expected exception.");
    }
    catch (final AbstractFileUploadBase.FileSizeLimitExceededException e)
    {
      assertEquals (30, e.getPermittedSize ());
    }
  }
}
