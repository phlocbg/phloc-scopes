/**
 * Copyright (C) 2006-2014 phloc systems
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