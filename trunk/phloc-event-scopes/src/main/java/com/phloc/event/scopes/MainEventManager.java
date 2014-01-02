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

import com.phloc.event.impl.helper.AbstractEventHelper;
import com.phloc.event.impl.helper.SynchronousEventHelper;
import com.phloc.event.resultaggregator.impl.DispatchResultAggregatorBooleanAnd;
import com.phloc.event.sync.mgr.impl.BidirectionalSynchronousMulticastEventManager;
import com.phloc.scopes.IScopeDestructionAware;

/**
 * Wraps the main event manager so that it becomes scope destruction aware
 * 
 * @author philip
 */
final class MainEventManager extends BidirectionalSynchronousMulticastEventManager implements IScopeDestructionAware
{
  public MainEventManager ()
  {
    super (AbstractEventHelper.getObserverQueueFactory (),
           SynchronousEventHelper.createSynchronousEventDispatcherFactory (DispatchResultAggregatorBooleanAnd.class,
                                                                           new ScopedEventObservingExceptionHandler ()));
  }

  public void onScopeDestruction () throws Exception
  {
    // Stop the event manager
    stop ();
  }
}
