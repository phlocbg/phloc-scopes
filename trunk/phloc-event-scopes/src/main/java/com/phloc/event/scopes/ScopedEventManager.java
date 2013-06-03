package com.phloc.event.scopes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.state.EChange;
import com.phloc.event.IEvent;
import com.phloc.event.IEventObserver;
import com.phloc.event.IEventType;
import com.phloc.event.impl.BaseEvent;
import com.phloc.scopes.IScope;
import com.phloc.scopes.mgr.EScope;

/**
 * Scope aware event manager for non-web scopes.
 * 
 * @author philip
 */
public final class ScopedEventManager
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (ScopedEventManager.class);
  private static final String ATTR_EVENT_MANAGER = ScopedEventManager.class.getName ();

  private ScopedEventManager ()
  {}

  @Nullable
  private static MainEventManager _getEventMgr (@Nonnull final IScope aScope)
  {
    if (aScope == null)
      throw new NullPointerException ("scope");

    return aScope.getCastedAttribute (ATTR_EVENT_MANAGER);
  }

  @Nonnull
  private static MainEventManager _getOrCreateEventMgr (@Nonnull final IScope aScope)
  {
    if (aScope == null)
      throw new NullPointerException ("scope");

    // Does the scope already contain an event manager?
    MainEventManager aEventMgr = _getEventMgr (aScope);
    if (aEventMgr == null)
    {
      // Build the event manager
      aEventMgr = new MainEventManager ();

      // put it in scope and register the cleanup handler
      aScope.setAttribute (ATTR_EVENT_MANAGER, aEventMgr);
    }
    return aEventMgr;
  }

  @Nonnull
  public static EChange registerObserver (@Nonnull final EScope eScope, final IEventObserver aObserver)
  {
    IScope aScope = eScope.getScope (false);
    if (aScope == null)
    {
      s_aLogger.warn ("Creating scope of type " + eScope + " because of event observer registration");
      aScope = eScope.getScope ();
    }
    return registerObserver (aScope, aObserver);
  }

  @Nonnull
  public static EChange registerObserver (@Nonnull final IScope aScope, @Nonnull final IEventObserver aObserver)
  {
    return _getOrCreateEventMgr (aScope).registerObserver (aObserver);
  }

  @Nonnull
  public static EChange unregisterObserver (@Nonnull final EScope eScope, @Nonnull final IEventObserver aObserver)
  {
    final IScope aScope = eScope.getScope (false);
    if (aScope != null)
    {
      final MainEventManager aEventMgr = _getEventMgr (aScope);
      if (aEventMgr != null)
        return aEventMgr.unregisterObserver (aObserver);
    }
    return EChange.UNCHANGED;
  }

  /**
   * Notify observers without sender and without parameter.
   * 
   * @param aEventType
   *        The event type for which an event should be triggered
   * @return <code>true</code> if no observer vetoed against the event
   */
  public static boolean notifyObservers (final @Nonnull IEventType aEventType)
  {
    return notifyObservers (new BaseEvent (aEventType));
  }

  /**
   * Notify observers without sender and without parameter.
   * 
   * @param aEvent
   *        The event on which observers should be notified.
   * @return <code>true</code> if no observer vetoed against the event
   */
  public static boolean notifyObservers (@Nonnull final IEvent aEvent)
  {
    boolean bReturn = true;

    // for all scopes
    for (final EScope eCurrentScope : EScope.values ())
    {
      // get current instance of scope
      final IScope aScope = eCurrentScope.getScope (false);
      if (aScope != null)
      {
        // get event manager (may be null)
        final MainEventManager aEventMgr = _getEventMgr (aScope);
        if (aEventMgr != null)
        {
          // main event trigger
          final Object aReturn = aEventMgr.trigger (aEvent);
          if (aReturn instanceof Boolean)
            bReturn = ((Boolean) aReturn).booleanValue ();
        }
      }
    }
    return bReturn;
  }
}
