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
package com.phloc.scopes.web.impl;

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
import com.phloc.commons.lang.ServiceLoaderBackport;
import com.phloc.scopes.web.fileupload.FileUploadException;
import com.phloc.scopes.web.fileupload.IFileItem;
import com.phloc.scopes.web.fileupload.IFileItemFactory;
import com.phloc.scopes.web.fileupload.IFileItemFactoryProviderSPI;
import com.phloc.scopes.web.fileupload.IProgressListener;
import com.phloc.scopes.web.fileupload.ProgressListenerProvider;
import com.phloc.scopes.web.fileupload.io.DiskFileItem;
import com.phloc.scopes.web.fileupload.io.DiskFileItemFactory;
import com.phloc.scopes.web.fileupload.servlet.ServletFileUpload;
import com.phloc.scopes.web.mock.MockHttpServletRequest;
import com.phloc.scopes.web.singleton.GlobalWebSingleton;

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
   * The maximum size of a single file (in bytes) that will be handled
   */
  public static final long MAX_REQUEST_SIZE = 5 * CGlobal.BYTES_PER_GIGABYTE;

  private static final Logger s_aLogger = LoggerFactory.getLogger (RequestWebScope.class);
  private static IFileItemFactoryProviderSPI s_aFIFP;

  static
  {
    final List <IFileItemFactoryProviderSPI> aFIFPList = ContainerHelper.newList (ServiceLoaderBackport.load (IFileItemFactoryProviderSPI.class));
    if (aFIFPList.isEmpty ())
      s_aFIFP = null;
    else
    {
      s_aFIFP = aFIFPList.get (0);
      if (aFIFPList.size () > 1)
        s_aLogger.warn ("More than one IFileItemFactoryProviderSPI implementation found! Using " + s_aFIFP);
    }
  }

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
