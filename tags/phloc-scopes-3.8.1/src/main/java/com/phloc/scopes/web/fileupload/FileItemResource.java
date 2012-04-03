/**
 * Copyright (C) 2006-2010 phloc systems
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.UnsupportedOperation;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.streams.StreamUtils;
import com.phloc.commons.string.ToStringGenerator;

/**
 * Implementation of the {@link IReadableResource} interface for uploaded
 * {@link IFileItem} objects.
 * 
 * @author philip
 */
public final class FileItemResource implements IReadableResource
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (FileItemResource.class);

  private final IFileItem m_aFileItem;
  private Integer m_aHashCode;

  public FileItemResource (@Nonnull final IFileItem aFileItem)
  {
    if (aFileItem == null)
      throw new NullPointerException ("fileItem");
    m_aFileItem = aFileItem;
  }

  @Nonnull
  public String getResourceID ()
  {
    return getPath ();
  }

  @Nonnull
  public String getPath ()
  {
    return m_aFileItem.getName ();
  }

  @Nullable
  public InputStream getInputStream ()
  {
    try
    {
      return m_aFileItem.getInputStream ();
    }
    catch (final IOException ex)
    {
      s_aLogger.error ("Failed to open FileItem input stream on " + m_aFileItem.getName (), ex);
      return null;
    }
  }

  @Nullable
  public Reader getReader (@Nonnull final String sCharset)
  {
    final InputStream aIS = getInputStream ();
    return StreamUtils.createReader (aIS, sCharset);
  }

  public boolean exists ()
  {
    return true;
  }

  @Nullable
  public URL getAsURL ()
  {
    s_aLogger.warn ("Cannot convert an IFileItem to a URL: " + toString ());
    return null;
  }

  @Nullable
  public File getAsFile ()
  {
    s_aLogger.warn ("Cannot convert an IFileItem to a File: " + toString ());
    return null;
  }

  @Nonnull
  @UnsupportedOperation
  public IReadableResource getReadableCloneForPath (@Nonnull final String sPath)
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof FileItemResource))
      return false;
    final FileItemResource rhs = (FileItemResource) o;
    return m_aFileItem.equals (rhs.m_aFileItem);
  }

  @Override
  public int hashCode ()
  {
    // We need a cached one!
    if (m_aHashCode == null)
      m_aHashCode = new HashCodeGenerator (this).append (m_aFileItem).getHashCodeObj ();
    return m_aHashCode.intValue ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("fileItem", m_aFileItem).toString ();
  }
}
