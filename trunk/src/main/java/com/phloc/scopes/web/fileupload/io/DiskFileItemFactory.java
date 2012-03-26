/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.phloc.scopes.web.fileupload.io;

import java.io.File;

import com.phloc.commons.annotations.VisibleForTesting;
import com.phloc.scopes.web.fileupload.FileItem;
import com.phloc.scopes.web.fileupload.FileItemFactory;

/**
 * <p>
 * The default {@link com.phloc.scopes.web.fileupload.FileItemFactory}
 * implementation. This implementation creates
 * {@link com.phloc.scopes.web.fileupload.FileItem} instances which keep their
 * content either in memory, for smaller items, or in a temporary file on disk,
 * for larger items. The size threshold, above which content will be stored on
 * disk, is configurable, as is the directory in which temporary files will be
 * created.
 * </p>
 * <p>
 * If not otherwise configured, the default configuration values are as follows:
 * <ul>
 * <li>Size threshold is 10KB.</li>
 * <li>Repository is the system default temp directory, as returned by
 * <code>System.getProperty("java.io.tmpdir")</code>.</li>
 * </ul>
 * </p>
 * <p>
 * Temporary files, which are created for file items, should be deleted later
 * on. The best way to do this is using a {@link FileCleaningTracker}, which you
 * can set on the {@link DiskFileItemFactory}. However, if you do use such a
 * tracker, then you must consider the following: Temporary files are
 * automatically deleted as soon as they are no longer needed. (More precisely,
 * when the corresponding instance of {@link java.io.File} is garbage
 * collected.) This is done by the so-called reaper thread, which is started
 * automatically when the class
 * {@link com.phloc.scopes.web.fileupload.io.FileCleaningTracker} is loaded. It
 * might make sense to terminate that thread, for example, if your web
 * application ends. See the section on "Resource cleanup" in the users guide of
 * commons-fileupload.
 * </p>
 * 
 * @author <a href="mailto:martinc@apache.org">Martin Cooper</a>
 * @since FileUpload 1.1
 * @version $Id: DiskFileItemFactory.java 735374 2009-01-18 02:18:45Z jochen $
 */
public class DiskFileItemFactory implements FileItemFactory
{
  /**
   * The directory in which uploaded files will be stored, if stored on disk.
   */
  private File _repository;

  /**
   * The threshold above which uploads will be stored on disk.
   */
  private int _sizeThreshold;

  /**
   * <p>
   * The instance of {@link FileCleaningTracker}, which is responsible for
   * deleting temporary files.
   * </p>
   * <p>
   * May be null, if tracking files is not required.
   * </p>
   */
  private final FileCleaningTracker fileCleaningTracker;

  // ----------------------------------------------------------- Constructors

  @VisibleForTesting
  public DiskFileItemFactory ()
  {
    this (10240, null);
  }

  public DiskFileItemFactory (final int sizeThreshold, final File repository)
  {
    this (sizeThreshold, repository, null);
  }

  /**
   * Constructs a preconfigured instance of this class.
   * 
   * @param sizeThreshold
   *        The threshold, in bytes, below which items will be retained in
   *        memory and above which they will be stored as a file.
   * @param repository
   *        The data repository, which is the directory in which files will be
   *        created, should the item size exceed the threshold.
   */
  public DiskFileItemFactory (final int sizeThreshold, final File repository, final FileCleaningTracker aTracker)
  {
    this._sizeThreshold = sizeThreshold;
    this._repository = repository;
    fileCleaningTracker = aTracker;
  }

  // ------------------------------------------------------------- Properties

  /**
   * Returns the directory used to temporarily store files that are larger than
   * the configured size threshold.
   * 
   * @return The directory in which temporary files will be located.
   * @see #setRepository(java.io.File)
   */
  public File getRepository ()
  {
    return _repository;
  }

  /**
   * Sets the directory used to temporarily store files that are larger than the
   * configured size threshold.
   * 
   * @param repository
   *        The directory in which temporary files will be located.
   * @see #getRepository()
   */
  public void setRepository (final File repository)
  {
    this._repository = repository;
  }

  /**
   * Returns the size threshold beyond which files are written directly to disk.
   * The default value is 10240 bytes.
   * 
   * @return The size threshold, in bytes.
   * @see #setSizeThreshold(int)
   */
  public int getSizeThreshold ()
  {
    return _sizeThreshold;
  }

  /**
   * Sets the size threshold beyond which files are written directly to disk.
   * 
   * @param sizeThreshold
   *        The size threshold, in bytes.
   * @see #getSizeThreshold()
   */
  public void setSizeThreshold (final int sizeThreshold)
  {
    this._sizeThreshold = sizeThreshold;
  }

  // --------------------------------------------------------- Public Methods

  /**
   * Create a new {@link com.phloc.scopes.web.fileupload.io.DiskFileItem}
   * instance from the supplied parameters and the local factory configuration.
   * 
   * @param fieldName
   *        The name of the form field.
   * @param contentType
   *        The content type of the form field.
   * @param isFormField
   *        <code>true</code> if this is a plain form field; <code>false</code>
   *        otherwise.
   * @param fileName
   *        The name of the uploaded file, if any, as supplied by the browser or
   *        other client.
   * @return The newly created file item.
   */
  public FileItem createItem (final String fieldName,
                              final String contentType,
                              final boolean isFormField,
                              final String fileName)
  {
    final DiskFileItem result = new DiskFileItem (fieldName,
                                                  contentType,
                                                  isFormField,
                                                  fileName,
                                                  _sizeThreshold,
                                                  _repository);
    final FileCleaningTracker tracker = fileCleaningTracker;
    if (tracker != null)
    {
      tracker.track (result.getTempFile (), this);
    }
    return result;
  }
}
