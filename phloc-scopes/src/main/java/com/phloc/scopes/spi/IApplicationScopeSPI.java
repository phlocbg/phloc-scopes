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
package com.phloc.scopes.spi;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.IsSPIInterface;
import com.phloc.scopes.domain.IApplicationScope;

/**
 * SPI for handling the application scope lifecycle. Is invoked for non-web and
 * web scopes.
 * 
 * @author Philip Helger
 */
@IsSPIInterface
public interface IApplicationScopeSPI
{
  /**
   * Called after the application scope was started
   * 
   * @param aApplicationScope
   *        The application scope object to be used. Never <code>null</code>.
   */
  void onApplicationScopeBegin (@Nonnull IApplicationScope aApplicationScope);

  /**
   * Called before the application scope is shut down
   * 
   * @param aApplicationScope
   *        The application scope object to be used. Never <code>null</code>.
   */
  void onApplicationScopeEnd (@Nonnull IApplicationScope aApplicationScope);
}
