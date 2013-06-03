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
