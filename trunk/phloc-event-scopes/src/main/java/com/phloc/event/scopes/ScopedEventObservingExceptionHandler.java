package com.phloc.event.scopes;

import com.phloc.commons.exceptions.InitializationException;
import com.phloc.event.impl.EventObservingExceptionHandler;

/**
 * Specialized exception handler
 * 
 * @author philip
 */
final class ScopedEventObservingExceptionHandler extends EventObservingExceptionHandler
{
  @Override
  public void handleObservingException (final Throwable aThrowable)
  {
    // don't catch these exceptions:
    if (aThrowable instanceof InitializationException)
      throw (InitializationException) aThrowable;

    // Pass through!
    super.handleObservingException (aThrowable);
  }
}