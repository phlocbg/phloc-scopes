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
import com.phloc.webscopes.IWebScope;
import com.phloc.webscopes.mgr.EWebScope;

/**
 * Scope aware event manager for web scopes.
 * 
 * @author philip
 */
public final class WebScopedEventManager
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (WebScopedEventManager.class);
  private static final String ATTR_EVENT_MANAGER = WebScopedEventManager.class.getName ();

  private WebScopedEventManager ()
  {}

  @Nullable
  private static MainEventManager _getEventMgr (@Nonnull final IWebScope aScope)
  {
    if (aScope == null)
      throw new NullPointerException ("scope");

    return aScope.getCastedAttribute (ATTR_EVENT_MANAGER);
  }

  @Nonnull
  private static MainEventManager _getOrCreateEventMgr (@Nonnull final IWebScope aScope)
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
  public static EChange registerObserver (@Nonnull final EWebScope eScope, final IEventObserver aObserver)
  {
    IWebScope aScope = eScope.getScope (false);
    if (aScope == null)
    {
      s_aLogger.warn ("Creating scope of type " + eScope + " because of event observer registration");
      aScope = eScope.getScope ();
    }
    return registerObserver (aScope, aObserver);
  }

  @Nonnull
  public static EChange registerObserver (@Nonnull final IWebScope aScope, @Nonnull final IEventObserver aObserver)
  {
    return _getOrCreateEventMgr (aScope).registerObserver (aObserver);
  }

  @Nonnull
  public static EChange unregisterObserver (@Nonnull final EWebScope eScope, @Nonnull final IEventObserver aObserver)
  {
    final IWebScope aScope = eScope.getScope (false);
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
    for (final EWebScope eCurrentScope : EWebScope.values ())
    {
      // get current instance of scope
      final IWebScope aScope = eCurrentScope.getScope (false);
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
