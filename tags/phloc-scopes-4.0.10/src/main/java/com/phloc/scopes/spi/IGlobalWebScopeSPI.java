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
package com.phloc.scopes.spi;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.IsSPIInterface;
import com.phloc.scopes.web.domain.IGlobalWebScope;

/**
 * SPI for handling the global scope lifecycle. Is invoked only for web scopes.
 * 
 * @author philip
 */
@IsSPIInterface
public interface IGlobalWebScopeSPI
{
  /**
   * Called after the global web scope was started
   * 
   * @param aGlobalWebScope
   *        The global web scope object to be used
   */
  void onGlobalWebScopeBegin (@Nonnull IGlobalWebScope aGlobalWebScope);

  /**
   * Called before the global web scope is shut down
   * 
   * @param aGlobalWebScope
   *        The global web scope object to be used
   */
  void onGlobalWebScopeEnd (@Nonnull IGlobalWebScope aGlobalWebScope);
}
