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
package com.phloc.scopes.web.fileupload.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.ReturnsMutableObject;
import com.phloc.commons.charset.CCharset;
import com.phloc.commons.charset.CharsetManager;
import com.phloc.commons.io.file.FileOperations;
import com.phloc.commons.io.file.SimpleFileIO;
import com.phloc.commons.io.streams.NonBlockingByteArrayInputStream;
import com.phloc.commons.io.streams.StreamUtils;
import com.phloc.scopes.web.fileupload.FileItem;
import com.phloc.scopes.web.fileupload.FileUploadException;
import com.phloc.scopes.web.fileupload.IFileItemHeaders;
import com.phloc.scopes.web.fileupload.IFileItemHeadersSupport;
import com.phloc.scopes.web.fileupload.InvalidFileNameException;
import com.phloc.scopes.web.fileupload.ParameterParser;
import com.phloc.scopes.web.fileupload.util.Streams;

/**
 * <p>
 * The default implementation of the
 * {@link com.phloc.scopes.web.fileupload.FileItem FileItem} interface.
 * <p>
 * After retrieving an instance you may either request all contents of file at
 * once using {@link #get()} or request an {@link java.io.InputStream
 * InputStream} with {@link #getInputStream()} and process the file without
 * attempting to load it into memory, which may come handy with large files.
 * 
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:sean@informage.net">Sean Legassick</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:jmcnally@apache.org">John McNally</a>
 * @author <a href="mailto:martinc@apache.org">Martin Cooper</a>
 * @author Sean C. Sullivan
 * @since FileUpload 1.1
 * @version $Id: DiskFileItem.java 963609 2010-07-13 06:56:47Z jochen $
 */
public class DiskFileItem implements FileItem, IFileItemHeadersSupport
{
  /**
   * Default content charset to be used when no explicit charset parameter is
   * provided by the sender. Media subtypes of the "text" type are defined to
   * have a default charset value of "ISO-8859-1" when received via HTTP.
   */
  public static final String DEFAULT_CHARSET = CCharset.CHARSET_ISO_8859_1;

  /**
   * UID used in unique file name generation.
   */
  private static final String UID = UUID.randomUUID ().toString ().replace (':', '_').replace ('-', '_');

  /**
   * Counter used in unique identifier generation.
   */
  private static int counter = 0;

  /**
   * The name of the form field as provided by the browser.
   */
  private String _fieldName;

  /**
   * The content type passed by the browser, or <code>null</code> if not
   * defined.
   */
  private final String _contentType;

  /**
   * Whether or not this item is a simple form field.
   */
  private boolean _isFormField;

  /**
   * The original filename in the user's filesystem.
   */
  private final String _fileName;

  /**
   * The size of the item, in bytes. This is used to cache the size when a file
   * item is moved from its original location.
   */
  private long size = -1;

  /**
   * The threshold above which uploads will be stored on disk.
   */
  private final int _sizeThreshold;

  /**
   * The directory in which uploaded files will be stored, if stored on disk.
   */
  private final File _repository;

  /**
   * Cached contents of the file.
   */
  private byte [] cachedContent;

  /**
   * Output stream for this item.
   */
  private transient DeferredFileOutputStream dfos;

  /**
   * The temporary file to use.
   */
  private transient File tempFile;

  /**
   * File to allow for serialization of the content of this item.
   */
  private File dfosFile;

  /**
   * The file items headers.
   */
  private IFileItemHeaders headers;

  // ----------------------------------------------------------- Constructors

  /**
   * Constructs a new <code>DiskFileItem</code> instance.
   * 
   * @param fieldName
   *        The name of the form field.
   * @param contentType
   *        The content type passed by the browser or <code>null</code> if not
   *        specified.
   * @param isFormField
   *        Whether or not this item is a plain form field, as opposed to a file
   *        upload.
   * @param fileName
   *        The original filename in the user's filesystem, or <code>null</code>
   *        if not specified.
   * @param sizeThreshold
   *        The threshold, in bytes, below which items will be retained in
   *        memory and above which they will be stored as a file.
   * @param repository
   *        The data repository, which is the directory in which files will be
   *        created, should the item size exceed the threshold.
   */
  public DiskFileItem (final String fieldName,
                       @Nullable final String contentType,
                       final boolean isFormField,
                       @Nullable final String fileName,
                       final int sizeThreshold,
                       @Nullable final File repository)
  {
    this._fieldName = fieldName;
    this._contentType = contentType;
    this._isFormField = isFormField;
    this._fileName = fileName;
    this._sizeThreshold = sizeThreshold;
    this._repository = repository;
  }

  // ------------------------------- Methods from javax.activation.DataSource

  /**
   * Returns an {@link java.io.InputStream InputStream} that can be used to
   * retrieve the contents of the file.
   * 
   * @return An {@link java.io.InputStream InputStream} that can be used to
   *         retrieve the contents of the file.
   * @throws IOException
   *         if an error occurs.
   */
  @Nonnull
  public InputStream getInputStream () throws IOException
  {
    if (!isInMemory ())
      return new FileInputStream (dfos.getFile ());

    if (cachedContent == null)
      cachedContent = dfos.getData ();

    return new NonBlockingByteArrayInputStream (cachedContent);
  }

  /**
   * Returns the content type passed by the agent or <code>null</code> if not
   * defined.
   * 
   * @return The content type passed by the agent or <code>null</code> if not
   *         defined.
   */
  @Nullable
  public String getContentType ()
  {
    return _contentType;
  }

  /**
   * Returns the content charset passed by the agent or <code>null</code> if not
   * defined.
   * 
   * @return The content charset passed by the agent or <code>null</code> if not
   *         defined.
   */
  @Nullable
  public String getCharSet ()
  {
    final ParameterParser parser = new ParameterParser ();
    parser.setLowerCaseNames (true);
    // Parameter parser can handle null input
    final Map <String, String> params = parser.parse (getContentType (), ';');
    return params.get ("charset");
  }

  /**
   * Returns the original filename in the client's filesystem.
   * 
   * @return The original filename in the client's filesystem.
   * @throws InvalidFileNameException
   *         The file name contains a NUL character, which might be an indicator
   *         of a security attack. If you intend to use the file name anyways,
   *         catch the exception and use InvalidFileNameException#getName().
   */
  @Nullable
  public String getName ()
  {
    return Streams.checkFileName (_fileName);
  }

  /**
   * Provides a hint as to whether or not the file contents will be read from
   * memory.
   * 
   * @return <code>true</code> if the file contents will be read from memory;
   *         <code>false</code> otherwise.
   */
  public boolean isInMemory ()
  {
    return cachedContent != null || dfos.isInMemory ();
  }

  /**
   * Returns the size of the file.
   * 
   * @return The size of the file, in bytes.
   */
  @Nonnegative
  public long getSize ()
  {
    if (size >= 0)
      return size;
    if (cachedContent != null)
      return cachedContent.length;
    if (dfos.isInMemory ())
      return dfos.getData ().length;
    return dfos.getFile ().length ();
  }

  /**
   * Returns the contents of the file as an array of bytes. If the contents of
   * the file were not yet cached in memory, they will be loaded from the disk
   * storage and cached.
   * 
   * @return The contents of the file as an array of bytes.
   */
  @ReturnsMutableObject (reason = "Speed")
  @edu.umd.cs.findbugs.annotations.SuppressWarnings ("EI_EXPOSE_REP")
  @Nullable
  public byte [] get ()
  {
    if (isInMemory ())
    {
      if (cachedContent == null)
        cachedContent = dfos.getData ();
      return cachedContent;
    }

    return SimpleFileIO.readFileBytes (dfos.getFile ());
  }

  /**
   * Returns the contents of the file as a String, using the specified encoding.
   * This method uses {@link #get()} to retrieve the contents of the file.
   * 
   * @param charset
   *        The charset to use.
   * @return The contents of the file, as a string.
   * @throws UnsupportedEncodingException
   *         if the requested character encoding is not available.
   */
  @Nonnull
  public String getString (final String charset) throws UnsupportedEncodingException
  {
    return new String (get (), charset);
  }

  /**
   * Returns the contents of the file as a String, using the default character
   * encoding. This method uses {@link #get()} to retrieve the contents of the
   * file.
   * 
   * @return The contents of the file, as a string.
   */
  @Nonnull
  public String getString ()
  {
    final byte [] rawdata = get ();
    String charset = getCharSet ();
    if (charset == null)
    {
      charset = DEFAULT_CHARSET;
    }
    return CharsetManager.getAsString (rawdata, charset);
  }

  /**
   * A convenience method to write an uploaded item to disk. The client code is
   * not concerned with whether or not the item is stored in memory, or on disk
   * in a temporary location. They just want to write the uploaded item to a
   * file.
   * <p>
   * This implementation first attempts to rename the uploaded item to the
   * specified destination file, if the item was originally written to disk.
   * Otherwise, the data will be copied to the specified file.
   * <p>
   * This method is only guaranteed to work <em>once</em>, the first time it is
   * invoked for a particular item. This is because, in the event that the
   * method renames a temporary file, that file will no longer be available to
   * copy or rename again at a later time.
   * 
   * @param file
   *        The <code>File</code> into which the uploaded item should be stored.
   * @throws Exception
   *         if an error occurs.
   */
  public void write (final File file) throws Exception
  {
    if (isInMemory ())
    {
      SimpleFileIO.writeFile (file, get ());
    }
    else
    {
      final File outputFile = getStoreLocation ();
      if (outputFile != null)
      {
        // Save the length of the file
        size = outputFile.length ();
        /*
         * The uploaded file is being stored on disk in a temporary location so
         * move it to the desired file.
         */
        if (!outputFile.renameTo (file))
        {
          BufferedInputStream in = null;
          BufferedOutputStream out = null;
          try
          {
            in = new BufferedInputStream (new FileInputStream (outputFile));
            out = new BufferedOutputStream (new FileOutputStream (file));
            StreamUtils.copyInputStreamToOutputStream (in, out);
          }
          finally
          {
            StreamUtils.close (in);
            StreamUtils.close (out);
          }
        }
      }
      else
      {
        /*
         * For whatever reason we cannot write the file to disk.
         */
        throw new FileUploadException ("Cannot write uploaded file to disk!");
      }
    }
  }

  /**
   * Deletes the underlying storage for a file item, including deleting any
   * associated temporary disk file. Although this storage will be deleted
   * automatically when the <code>FileItem</code> instance is garbage collected,
   * this method can be used to ensure that this is done at an earlier time,
   * thus preserving system resources.
   */
  public void delete ()
  {
    cachedContent = null;
    final File outputFile = getStoreLocation ();
    if (outputFile != null && outputFile.exists ())
      FileOperations.deleteFile (outputFile);
  }

  /**
   * Returns the name of the field in the multipart form corresponding to this
   * file item.
   * 
   * @return The name of the form field.
   * @see #setFieldName(java.lang.String)
   */
  public String getFieldName ()
  {
    return _fieldName;
  }

  /**
   * Sets the field name used to reference this file item.
   * 
   * @param fieldName
   *        The name of the form field.
   * @see #getFieldName()
   */
  public void setFieldName (final String fieldName)
  {
    this._fieldName = fieldName;
  }

  /**
   * Determines whether or not a <code>FileItem</code> instance represents a
   * simple form field.
   * 
   * @return <code>true</code> if the instance represents a simple form field;
   *         <code>false</code> if it represents an uploaded file.
   * @see #setFormField(boolean)
   */
  public boolean isFormField ()
  {
    return _isFormField;
  }

  /**
   * Specifies whether or not a <code>FileItem</code> instance represents a
   * simple form field.
   * 
   * @param state
   *        <code>true</code> if the instance represents a simple form field;
   *        <code>false</code> if it represents an uploaded file.
   * @see #isFormField()
   */
  public void setFormField (final boolean state)
  {
    _isFormField = state;
  }

  /**
   * Returns an {@link java.io.OutputStream OutputStream} that can be used for
   * storing the contents of the file.
   * 
   * @return An {@link java.io.OutputStream OutputStream} that can be used for
   *         storing the contents of the file.
   * @throws IOException
   *         if an error occurs.
   */
  @Nonnull
  public DeferredFileOutputStream getOutputStream () throws IOException
  {
    if (dfos == null)
    {
      final File outputFile = getTempFile ();
      dfos = new DeferredFileOutputStream (_sizeThreshold, outputFile);
    }
    return dfos;
  }

  // --------------------------------------------------------- Public methods

  /**
   * Returns the {@link java.io.File} object for the <code>FileItem</code>'s
   * data's temporary location on the disk. Note that for <code>FileItem</code>s
   * that have their data stored in memory, this method will return
   * <code>null</code>. When handling large files, you can use
   * {@link java.io.File#renameTo(java.io.File)} to move the file to new
   * location without copying the data, if the source and destination locations
   * reside within the same logical volume.
   * 
   * @return The data file, or <code>null</code> if the data is stored in
   *         memory.
   */
  @Nullable
  public File getStoreLocation ()
  {
    return dfos == null ? null : dfos.getFile ();
  }

  // ------------------------------------------------------ Protected methods

  /**
   * Removes the file contents from the temporary storage.
   */
  @Override
  protected void finalize ()
  {
    final File outputFile = dfos.getFile ();

    if (outputFile != null && outputFile.exists ())
      FileOperations.deleteFile (outputFile);
  }

  /**
   * Creates and returns a {@link java.io.File File} representing a uniquely
   * named temporary file in the configured repository path. The lifetime of the
   * file is tied to the lifetime of the <code>FileItem</code> instance; the
   * file will be deleted when the instance is garbage collected.
   * 
   * @return The {@link java.io.File File} to be used for temporary storage.
   */
  @Nonnull
  protected File getTempFile ()
  {
    if (tempFile == null)
    {
      File tempDir = _repository;
      if (tempDir == null)
        tempDir = new File (System.getProperty ("java.io.tmpdir"));

      final String tempFileName = "upload_" + UID + "_" + getUniqueId () + ".tmp";
      tempFile = new File (tempDir, tempFileName);
    }
    return tempFile;
  }

  // -------------------------------------------------------- Private methods

  /**
   * Returns an identifier that is unique within the class loader used to load
   * this class, but does not have random-like apearance.
   * 
   * @return A String with the non-random looking instance identifier.
   */
  private static String getUniqueId ()
  {
    final int limit = 100000000;
    int current;
    synchronized (DiskFileItem.class)
    {
      current = counter++;
    }
    String id = Integer.toString (current);

    // If you manage to get more than 100 million of ids, you'll
    // start getting ids longer than 8 characters.
    if (current < limit)
    {
      id = ("00000000" + id).substring (id.length ());
    }
    return id;
  }

  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object.
   */
  @Override
  public String toString ()
  {
    return "name=" +
           this.getName () +
           ", StoreLocation=" +
           String.valueOf (this.getStoreLocation ()) +
           ", size=" +
           this.getSize () +
           "bytes, " +
           "isFormField=" +
           isFormField () +
           ", FieldName=" +
           this.getFieldName ();
  }

  // -------------------------------------------------- Serialization methods

  /**
   * Writes the state of this object during serialization.
   * 
   * @param out
   *        The stream to which the state should be written.
   * @throws IOException
   *         if an error occurs.
   */
  private void writeObject (final ObjectOutputStream out) throws IOException
  {
    // Read the data
    if (dfos.isInMemory ())
    {
      cachedContent = get ();
    }
    else
    {
      cachedContent = null;
      dfosFile = dfos.getFile ();
    }

    // write out values
    out.defaultWriteObject ();
  }

  /**
   * Reads the state of this object during deserialization.
   * 
   * @param in
   *        The stream from which the state should be read.
   * @throws IOException
   *         if an error occurs.
   * @throws ClassNotFoundException
   *         if class cannot be found.
   */
  private void readObject (final ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    // read values
    in.defaultReadObject ();

    final OutputStream output = getOutputStream ();
    if (cachedContent != null)
    {
      output.write (cachedContent);
    }
    else
    {
      final FileInputStream input = new FileInputStream (dfosFile);
      StreamUtils.copyInputStreamToOutputStream (input, output);
      FileOperations.deleteFile (dfosFile);
      dfosFile = null;
    }
    output.close ();

    cachedContent = null;
  }

  /**
   * Returns the file item headers.
   * 
   * @return The file items headers.
   */
  public IFileItemHeaders getHeaders ()
  {
    return headers;
  }

  /**
   * Sets the file item headers.
   * 
   * @param pHeaders
   *        The file items headers.
   */
  public void setHeaders (final IFileItemHeaders pHeaders)
  {
    headers = pHeaders;
  }
}
