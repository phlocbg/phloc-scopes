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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.phloc.scopes.web.fileupload.servlet.ServletFileUpload;
import com.phloc.scopes.web.mock.MockHttpServletRequest;

/**
 * Tests the progress listener.
 */
public final class IProgressListenerTest extends AbstractFileUploadTestCase
{
  private static class ProgressListenerImpl implements IProgressListener
  {
    private final long expectedContentLength;
    private final int expectedItems;
    private Long bytesRead;
    private Integer items;

    ProgressListenerImpl (final long pContentLength, final int pItems)
    {
      expectedContentLength = pContentLength;
      expectedItems = pItems;
    }

    public void update (final long pBytesRead, final long pContentLength, final int pItems)
    {
      assertTrue (pBytesRead >= 0 && pBytesRead <= expectedContentLength);
      assertTrue (pContentLength == -1 || pContentLength == expectedContentLength);
      assertTrue (pItems >= 0 && pItems <= expectedItems);

      assertTrue (bytesRead == null || pBytesRead >= bytesRead.longValue ());
      bytesRead = new Long (pBytesRead);
      assertTrue (items == null || pItems >= items.intValue ());
      items = new Integer (pItems);
    }

    void checkFinished ()
    {
      assertEquals (expectedContentLength, bytesRead.longValue ());
      assertEquals (expectedItems, items.intValue ());
    }
  }

  /**
   * Parse a very long file upload by using a progress listener.
   */
  @Test
  public void testProgressListener () throws Exception
  {
    final int NUM_ITEMS = 512;
    final ByteArrayOutputStream baos = new ByteArrayOutputStream ();
    for (int i = 0; i < NUM_ITEMS; i++)
    {
      final String header = "-----1234\r\n" +
                            "Content-Disposition: form-data; name=\"field" +
                            (i + 1) +
                            "\"\r\n" +
                            "\r\n";
      baos.write (header.getBytes ("US-ASCII"));
      for (int j = 0; j < 16384 + i; j++)
      {
        baos.write ((byte) j);
      }
      baos.write ("\r\n".getBytes ("US-ASCII"));
    }
    baos.write ("-----1234--\r\n".getBytes ("US-ASCII"));
    final byte [] contents = baos.toByteArray ();

    MockHttpServletRequest request = MockHttpServletRequest.createWithContent (contents,
                                                                               "multipart/form-data; boundary=---1234");
    _runTest (NUM_ITEMS, contents.length, request);
    request = new MockHttpServletRequest ()
    {
      @Override
      public int getContentLength ()
      {
        return -1;
      }
    };
    request.setContent (contents);
    request.setContentType ("multipart/form-data; boundary=---1234");
    _runTest (NUM_ITEMS, contents.length, request);
  }

  private void _runTest (final int NUM_ITEMS, final long pContentLength, final MockHttpServletRequest request) throws FileUploadException,
                                                                                                             IOException
  {
    final ServletFileUpload upload = new ServletFileUpload (null);
    final ProgressListenerImpl listener = new ProgressListenerImpl (pContentLength, NUM_ITEMS);
    upload.setProgressListener (listener);
    final IFileItemIterator iter = upload.getItemIterator (request);
    for (int i = 0; i < NUM_ITEMS; i++)
    {
      final IFileItemStream stream = iter.next ();
      final InputStream istream = stream.openStream ();
      for (int j = 0; j < 16384 + i; j++)
      {
        /**
         * This used to be assertEquals((byte) j, (byte) istream.read()); but
         * this seems to trigger a bug in JRockit, so we express the same like
         * this:
         */
        final byte b1 = (byte) j;
        final byte b2 = (byte) istream.read ();
        if (b1 != b2)
        {
          fail ("Expected " + b1 + ", got " + b2);
        }
      }
      assertEquals (-1, istream.read ());
    }
    assertTrue (!iter.hasNext ());
    listener.checkFinished ();
  }
}
