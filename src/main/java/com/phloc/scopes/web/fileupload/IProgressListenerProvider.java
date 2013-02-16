package com.phloc.scopes.web.fileupload;

import com.phloc.commons.annotations.IsSPIInterface;

@IsSPIInterface
public interface IProgressListenerProvider
{
  /**
   * @return A progress listener instance for handling file uploads
   */
  IProgressListener getProgressListener ();
}
