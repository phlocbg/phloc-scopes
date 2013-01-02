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
package com.phloc.scopes.web.fileupload;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.IsSPIInterface;

/**
 * SPI for a provider of a custom file item factory that should be used instead
 * of the default one.
 * 
 * @author boris
 */
@IsSPIInterface
public interface IFileItemFactoryProviderSPI
{
  /**
   * @return Retrieves the custom factory implementation, may not be
   *         <code>null</code>
   */
  @Nonnull
  IFileItemFactory getCustomFactory ();
}
