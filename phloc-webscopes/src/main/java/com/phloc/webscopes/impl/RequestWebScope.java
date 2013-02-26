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
package com.phloc.webscopes.impl;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.CGlobal;
import com.phloc.commons.annotations.IsSPIImplementation;
import com.phloc.commons.annotations.OverrideOnDemand;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.commons.charset.CCharset;
import com.phloc.commons.collections.ArrayHelper;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.collections.multimap.IMultiMapListBased;
import com.phloc.commons.collections.multimap.MultiHashMapArrayListBased;
import com.phloc.commons.io.streams.StreamUtils;
import com.phloc.commons.lang.ServiceLoaderUtils;
import com.phloc.web.fileupload.FileUploadException;
import com.phloc.web.fileupload.IFileItem;
import com.phloc.web.fileupload.IFileItemFactory;
import com.phloc.web.fileupload.IFileItemFactoryProviderSPI;
import com.phloc.web.fileupload.IProgressListener;
import com.phloc.web.fileupload.io.DiskFileItem;
import com.phloc.web.fileupload.io.DiskFileItemFactory;
import com.phloc.web.fileupload.servlet.ServletFileUpload;
import com.phloc.web.mock.MockHttpServletRequest;
import com.phloc.webscopes.fileupload.ProgressListenerProvider;
import com.phloc.webscopes.singleton.GlobalWebSingleton;

/**
 * The default request web scope that also tries to parse multi part requests.
 * 
 * @author philip
 */
public class RequestWebScope extends RequestWebScopeNoMultipart
{
  /**
   * Wrapper around a {@link DiskFileItemFactory}, that is correctly cleaning
   * up, when the servlet context is destroyed.
   * 
   * @author philip
   */
  @IsSPIImplementation
  public static final class GlobalDiskFileItemFactory extends GlobalWebSingleton implements IFileItemFactory
  {
    private final DiskFileItemFactory m_aFactory = new DiskFileItemFactory (CGlobal.BYTES_PER_MEGABYTE, null);

    @UsedViaReflection
    @Deprecated
    public GlobalDiskFileItemFactory ()
    {}

    @Nonnull
    public static GlobalDiskFileItemFactory getInstance ()
    {
      return getGlobalSingleton (GlobalDiskFileItemFactory.class);
    }

    @Override
    protected void onDestroy ()
    {
      m_aFactory.deleteAllTemporaryFiles ();
    }

    public void setRepository (@Nullable final File aRepository)
    {
      m_aFactory.setRepository (aRepository);
    }

    @Nonnull
    public DiskFileItem createItem (final String sFieldName,
                                    final String sContentType,
                                    final boolean bIsFormField,
                                    final String sFileName)
    {
      return m_aFactory.createItem (sFieldName, sContentType, bIsFormField, sFileName);
    }

    @Nonnull
    @ReturnsMutableCopy
    public List <File> getAllTemporaryFiles ()
    {
      return m_aFactory.getAllTemporaryFiles ();
    }
  }

  /**
   * The maximum size of a single file (in bytes) that will be handled. May not
   * be larger than 2 GB as browsers cannot handle more than 2GB. See e.g.
   * http://www.motobit.com/help/ScptUtl/pa33.htm or
   * https://bugzilla.mozilla.org/show_bug.cgi?id=215450 Extensive analysis: <a
   * href=
   * "http://tomcat.10.n6.nabble.com/Problems-uploading-huge-files-gt-2GB-to-Tomcat-app-td4730850.html"
   * >here</a>
   */
  public static final long MAX_REQUEST_SIZE = 5 * CGlobal.BYTES_PER_GIGABYTE;

  private static final Logger s_aLogger = LoggerFactory.getLogger (RequestWebScope.class);
  private static final IFileItemFactoryProviderSPI s_aFIFP = ServiceLoaderUtils.getFirstSPIImplementation (IFileItemFactoryProviderSPI.class);

  public RequestWebScope (@Nonnull final HttpServletRequest aHttpRequest,
                          @Nonnull final HttpServletResponse aHttpResponse)
  {
    super (aHttpRequest, aHttpResponse);
  }

  /**
   * Check if the parsed request is a multi part request, potentially containing
   * uploaded files.
   * 
   * @return <code>true</code> if the current request is a multi part request
   */
  private boolean _isMultipartContent ()
  {
    return !(m_aHttpRequest instanceof MockHttpServletRequest) && ServletFileUpload.isMultipartContent (m_aHttpRequest);
  }

  private IFileItemFactory _getFactory ()
  {
    if (s_aFIFP != null)
      return s_aFIFP.getCustomFactory ();
    return GlobalDiskFileItemFactory.getInstance ();
  }

  @Override
  @OverrideOnDemand
  protected boolean addSpecialRequestAttributes ()
  {
    // check file uploads
    // Note: this handles only POST parameters!
    boolean bAddedFileUploadItems = false;
    if (_isMultipartContent ())
    {
      try
      {
        // Setup the ServletFileUpload....
        final ServletFileUpload aUpload = new ServletFileUpload (_getFactory ());
        aUpload.setSizeMax (MAX_REQUEST_SIZE);
        aUpload.setHeaderEncoding (CCharset.CHARSET_UTF_8);
        final IProgressListener aListener = ProgressListenerProvider.getInstance ().getProgressListener ();
        if (aListener != null)
          aUpload.setProgressListener (aListener);

        try
        {
          m_aHttpRequest.setCharacterEncoding (CCharset.CHARSET_UTF_8);
        }
        catch (final UnsupportedEncodingException ex)
        {
          s_aLogger.error ("Failed to set request character encoding to '" + CCharset.CHARSET_UTF_8 + "'", ex);
        }

        // Parse and write to temporary directory
        final IMultiMapListBased <String, String> aFormFields = new MultiHashMapArrayListBased <String, String> ();
        final IMultiMapListBased <String, IFileItem> aFormFiles = new MultiHashMapArrayListBased <String, IFileItem> ();
        for (final IFileItem aFileItem : aUpload.parseRequest (m_aHttpRequest))
        {
          if (aFileItem.isFormField ())
          {
            // We need to explicitly use the charset, as by default only the
            // charset from the content type is used!
            aFormFields.putSingle (aFileItem.getFieldName (), aFileItem.getString (CCharset.CHARSET_UTF_8_OBJ));
          }
          else
            aFormFiles.putSingle (aFileItem.getFieldName (), aFileItem);
        }

        // set all form fields
        for (final Map.Entry <String, List <String>> aEntry : aFormFields.entrySet ())
        {
          // Convert list of String to value (String or array of String)
          final List <String> aValues = aEntry.getValue ();
          final Object aValue = aValues.size () == 1 ? ContainerHelper.getFirstElement (aValues)
                                                    : ArrayHelper.newArray (aValues, String.class);
          setAttribute (aEntry.getKey (), aValue);
        }

        // set all form files
        for (final Map.Entry <String, List <IFileItem>> aEntry : aFormFiles.entrySet ())
        {
          // Convert list of String to value (String or array of String)
          final List <IFileItem> aValues = aEntry.getValue ();
          final Object aValue = aValues.size () == 1 ? ContainerHelper.getFirstElement (aValues)
                                                    : ArrayHelper.newArray (aValues, IFileItem.class);
          setAttribute (aEntry.getKey (), aValue);
        }

        // Parsing complex file upload succeeded -> do not use standard scan for
        // parameters
        bAddedFileUploadItems = true;
      }
      catch (final FileUploadException ex)
      {
        if (!StreamUtils.isKnownEOFException (ex.getCause ()))
          s_aLogger.error ("Error parsing multipart request content", ex);
      }
      catch (final RuntimeException ex)
      {
        s_aLogger.error ("Error parsing multipart request content", ex);
      }
    }
    return bAddedFileUploadItems;
  }
}
