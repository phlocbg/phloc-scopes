package com.phloc.event.scopes;

import static org.junit.Assert.assertEquals;

import javax.annotation.Nullable;

import com.phloc.commons.callback.INonThrowingRunnableWithParameter;
import com.phloc.event.IEvent;
import com.phloc.event.IEventType;
import com.phloc.event.impl.AbstractEventObserver;
import com.phloc.event.impl.EventTypeRegistry;

/**
 * Dummy observer.
 * 
 * @author philip
 */
public final class MockCountingObserver extends AbstractEventObserver
{
  public static final IEventType TOPIC = EventTypeRegistry.createEventType ("mytopic");

  private int m_nInvokeCount = 0;

  public MockCountingObserver ()
  {
    super (false, TOPIC);
  }

  public void onEvent (final IEvent aEvent, @Nullable final INonThrowingRunnableWithParameter <Object> aResultCallback)
  {
    assertEquals (aEvent.getEventType (), TOPIC);
    ++m_nInvokeCount;
  }

  public int getInvocationCount ()
  {
    return m_nInvokeCount;
  }
}
