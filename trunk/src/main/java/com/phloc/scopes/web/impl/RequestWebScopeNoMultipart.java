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
package com.phloc.scopes.web.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.OverrideOnDemand;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.callback.AdapterRunnableToCallableWithParameter;
import com.phloc.commons.callback.INonThrowingCallableWithParameter;
import com.phloc.commons.callback.INonThrowingRunnableWithParameter;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.collections.attrs.AbstractReadonlyAttributeContainer;
import com.phloc.commons.equals.EqualsUtils;
import com.phloc.commons.idfactory.GlobalIDFactory;
import com.phloc.commons.lang.GenericReflection;
import com.phloc.commons.state.EChange;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.scopes.IScope;
import com.phloc.scopes.IScopeDestructionAware;
import com.phloc.scopes.IScopeRenewalAware;
import com.phloc.scopes.ScopeUtils;
import com.phloc.scopes.web.domain.IRequestWebScope;
import com.phloc.scopes.web.fileupload.IFileItem;

/**
 * A request web scopes that does not parse multipart requests.
 * 
 * @author philip
 */
public class RequestWebScopeNoMultipart extends AbstractReadonlyAttributeContainer implements IRequestWebScope
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (RequestWebScopeNoMultipart.class);
  private static final String REQUEST_ATTR_SCOPE_INITED = "$request.scope.inited";

  protected final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  private final String m_sScopeID;
  protected final HttpServletRequest m_aHttpRequest;
  protected final HttpServletResponse m_aHttpResponse;
  private boolean m_bInDestruction = false;
  private boolean m_bDestroyed = false;

  public RequestWebScopeNoMultipart (@Nonnull final HttpServletRequest aHttpRequest,
                                     @Nonnull final HttpServletResponse aHttpResponse)
  {
    if (aHttpRequest == null)
      throw new NullPointerException ("httpRequest");
    if (aHttpResponse == null)
      throw new NullPointerException ("httpResponse");

    m_sScopeID = GlobalIDFactory.getNewIntID () + "@" + aHttpRequest.getRequestURI ();
    m_aHttpRequest = aHttpRequest;
    m_aHttpResponse = aHttpResponse;

    // done initialization
    if (ScopeUtils.debugScopeLifeCycle (s_aLogger))
      s_aLogger.info ("Created request web scope '" + getID () + "'");
  }

  @OverrideOnDemand
  protected boolean addSpecialRequestAttributes ()
  {
    return false;
  }

  @OverrideOnDemand
  protected void postAttributeInit ()
  {}

  public final void initScope ()
  {
    // Avoid double initialization of a scope, because for file uploads, the
    // parameters can only be extracted once!
    // As the parameters are stored directly in the HTTP request, we're not
    // loosing any data here!
    if (getAttributeObject (REQUEST_ATTR_SCOPE_INITED) != null)
      return;
    setAttribute (REQUEST_ATTR_SCOPE_INITED, Boolean.TRUE);

    // where some extra items (like file items) handled?
    final boolean bAddedSpecialRequestAttrs = addSpecialRequestAttributes ();

    // set parameters as attributes (handles GET and POST parameters)
    final Enumeration <?> aEnum = m_aHttpRequest.getParameterNames ();
    while (aEnum.hasMoreElements ())
    {
      final String sParamName = (String) aEnum.nextElement ();

      // Avoid double setting a parameter!
      if (bAddedSpecialRequestAttrs && containsAttribute (sParamName))
        continue;

      // Check if it is a single value or not
      final String [] aParamValues = m_aHttpRequest.getParameterValues (sParamName);
      if (aParamValues.length == 1)
        setAttribute (sParamName, aParamValues[0]);
      else
        setAttribute (sParamName, aParamValues);
    }

    postAttributeInit ();

    // done initialization
    if (ScopeUtils.debugScopeLifeCycle (s_aLogger))
      s_aLogger.info ("Initialized request web scope '" + getID () + "'");
  }

  @Nonnull
  @Nonempty
  public final String getID ()
  {
    return m_sScopeID;
  }

  public final void runAtomic (@Nonnull final INonThrowingRunnableWithParameter <IScope> aRunnable)
  {
    // Wrap runnable in callable
    runAtomic (AdapterRunnableToCallableWithParameter.createAdapter (aRunnable));
  }

  @Nullable
  public final <T> T runAtomic (@Nonnull final INonThrowingCallableWithParameter <T, IScope> aCallable)
  {
    if (aCallable == null)
      throw new NullPointerException ("callable");

    m_aRWLock.writeLock ().lock ();
    try
    {
      return aCallable.call (this);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  public EChange clear ()
  {
    // Create a copy of the list!
    final List <String> aNames = ContainerHelper.newList (getAttributeNames ());
    if (aNames.isEmpty ())
      return EChange.UNCHANGED;
    for (final String sAttrName : aNames)
      removeAttribute (sAttrName);
    return EChange.CHANGED;
  }

  public boolean isValid ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return !m_bInDestruction && !m_bDestroyed;
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  public boolean isInDestruction ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_bInDestruction;
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  public boolean isDestroyed ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_bDestroyed;
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  public void destroyScope ()
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      if (m_bDestroyed)
        throw new IllegalStateException ("Scope is already destroyed!");
      if (m_bInDestruction)
        throw new IllegalStateException ("Scope is already in destruction!");
      m_bInDestruction = true;
    }
    finally
    {
      m_aRWLock.writeLock ().lock ();
    }

    // Call callback (if special interface is implemented)
    for (final Object aValue : getAllAttributes ().values ())
      if (aValue instanceof IScopeDestructionAware)
        try
        {
          ((IScopeDestructionAware) aValue).onScopeDestruction ();
        }
        catch (final Throwable t)
        {
          s_aLogger.error ("Failed to call destruction method in scope " + getID () + " on " + aValue, t);
        }

    // Remove all attributes here
    clear ();

    // Finished destruction process -> remember this
    m_aRWLock.writeLock ().lock ();
    try
    {
      m_bDestroyed = true;
      m_bInDestruction = false;
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }

    if (ScopeUtils.debugScopeLifeCycle (s_aLogger))
      s_aLogger.info ("Destroyed request web scope '" + getID () + "'");
  }

  public boolean containsAttribute (final String sName)
  {
    return getAttributeObject (sName) != null;
  }

  @Nonnull
  @ReturnsMutableCopy
  public Map <String, Object> getAllAttributes ()
  {
    final Map <String, Object> ret = new HashMap <String, Object> ();
    final Enumeration <String> aEnum = getAttributeNames ();
    while (aEnum.hasMoreElements ())
    {
      final String sAttrName = aEnum.nextElement ();
      final Object aAttrValue = getAttributeObject (sAttrName);
      ret.put (sAttrName, aAttrValue);
    }
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllAttributeNames ()
  {
    return ContainerHelper.newSet (getAttributeNames ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public Collection <Object> getAllAttributeValues ()
  {
    final List <Object> ret = new ArrayList <Object> ();
    final Enumeration <String> aEnum = getAttributeNames ();
    while (aEnum.hasMoreElements ())
    {
      final String sAttrName = aEnum.nextElement ();
      ret.add (getAttributeObject (sAttrName));
    }
    return ret;
  }

  @Nullable
  public List <String> getAttributeValues (final String sName)
  {
    return getAttributeValues (sName, null);
  }

  @Nullable
  public List <String> getAttributeValues (final String sName, @Nullable final List <String> aDefault)
  {
    final Object aValue = getAttributeObject (sName);
    if (aValue instanceof String [])
    {
      // multiple values passed in the request
      return ContainerHelper.newList ((String []) aValue);
    }
    if (aValue instanceof String)
    {
      // single value passed in the request
      return ContainerHelper.newList ((String) aValue);
    }
    return aDefault;
  }

  public boolean hasAttributeValue (final String sName, @Nullable final String sDesiredValue)
  {
    return EqualsUtils.equals (getAttributeAsString (sName), sDesiredValue);
  }

  public boolean hasAttributeValue (final String sName, final String sDesiredValue, final boolean bDefault)
  {
    final String sValue = getAttributeAsString (sName);
    return sValue == null ? bDefault : EqualsUtils.equals (sValue, sDesiredValue);
  }

  public String getCharacterEncoding ()
  {
    return m_aHttpRequest.getCharacterEncoding ();
  }

  @Nonnull
  public EChange removeAttribute (final String sName)
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      if (m_aHttpRequest.getAttribute (sName) == null)
        return EChange.UNCHANGED;
      m_aHttpRequest.removeAttribute (sName);
      return EChange.CHANGED;
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  public EChange setAttribute (final String sName, final Object aNewValue)
  {
    if (s_aLogger.isTraceEnabled ())
      s_aLogger.trace ("name='" + sName + "' -- '" + aNewValue + "'");

    m_aRWLock.writeLock ().lock ();
    try
    {
      final Object aOldValue = m_aHttpRequest.getAttribute (sName);
      if (EqualsUtils.equals (aOldValue, aNewValue))
        return EChange.UNCHANGED;

      m_aHttpRequest.setAttribute (sName, aNewValue);
      return EChange.CHANGED;
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nullable
  public Object getAttributeObject (final String sName)
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aHttpRequest.getAttribute (sName);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnull
  public Enumeration <String> getAttributeNames ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return GenericReflection.uncheckedCast (m_aHttpRequest.getAttributeNames ());
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnull
  @ReturnsMutableCopy
  public Map <String, IFileItem> getAllUploadedFileItems ()
  {
    final Map <String, IFileItem> ret = new HashMap <String, IFileItem> ();
    for (final Map.Entry <String, Object> aEntry : getAllAttributes ().entrySet ())
      if (aEntry.getValue () instanceof IFileItem)
        ret.put (aEntry.getKey (), (IFileItem) aEntry.getValue ());
    return ret;
  }

  public String getScheme ()
  {
    return m_aHttpRequest.getScheme ();
  }

  public String getServerName ()
  {
    return m_aHttpRequest.getServerName ();
  }

  public String getServerProtocolVersion ()
  {
    return m_aHttpRequest.getProtocol ();
  }

  public int getServerPort ()
  {
    return m_aHttpRequest.getServerPort ();
  }

  public String getMethod ()
  {
    return m_aHttpRequest.getMethod ();
  }

  public String getPathInfo ()
  {
    // get path Info without the ;jsessionid... parameter
    final String sPathInfo = m_aHttpRequest.getPathInfo ();
    if (StringHelper.hasNoText (sPathInfo))
      return sPathInfo;

    // Strip session ID parameter
    final int nIndex = sPathInfo.indexOf (';');
    return nIndex == -1 ? sPathInfo : sPathInfo.substring (0, nIndex);
  }

  public String getPathTranslated ()
  {
    return m_aHttpRequest.getPathTranslated ();
  }

  public String getQueryString ()
  {
    return m_aHttpRequest.getQueryString ();
  }

  public String getRemoteHost ()
  {
    return m_aHttpRequest.getRemoteHost ();
  }

  public String getRemoteAddr ()
  {
    return m_aHttpRequest.getRemoteAddr ();
  }

  public String getAuthType ()
  {
    return m_aHttpRequest.getAuthType ();
  }

  public String getRemoteUser ()
  {
    return m_aHttpRequest.getRemoteUser ();
  }

  public String getContentType ()
  {
    return m_aHttpRequest.getContentType ();
  }

  public int getContentLength ()
  {
    return m_aHttpRequest.getContentLength ();
  }

  public String getServletPath ()
  {
    return m_aHttpRequest.getServletPath ();
  }

  public HttpSession getSession (final boolean bCreateIfNotExisting)
  {
    return m_aHttpRequest.getSession (bCreateIfNotExisting);
  }

  @Nonnull
  private StringBuilder _getFullServerPath ()
  {
    final String sScheme = m_aHttpRequest.getScheme ();
    final StringBuilder aSB = new StringBuilder (500);
    aSB.append (sScheme).append ("://").append (m_aHttpRequest.getServerName ());
    final int nPort = m_aHttpRequest.getServerPort ();
    // append non-standard port
    if (sScheme.equals ("http"))
    {
      if (nPort != 80)
        aSB.append (':').append (nPort);
    }
    else
      if (sScheme.equals ("https"))
      {
        if (nPort != 443)
          aSB.append (':').append (nPort);
      }
      else
        aSB.append (':').append (nPort);
    return aSB;
  }

  @Nonnull
  public String getFullServerPath ()
  {
    return _getFullServerPath ().toString ();
  }

  @Nonnull
  public String getFullContextPath ()
  {
    return _getFullServerPath ().append (m_aHttpRequest.getContextPath ()).toString ();
  }

  @Nonnull
  public String getContextAndServletPath ()
  {
    final String sServletPath = getServletPath ();
    // For servlets that are not files, we need to append a trailing slash
    if (sServletPath.indexOf (".") < 0)
      return m_aHttpRequest.getContextPath () + sServletPath + '/';
    return m_aHttpRequest.getContextPath () + sServletPath;
  }

  @Nonnull
  public String getFullContextAndServletPath ()
  {
    final String ret = getFullContextPath () + getServletPath ();
    // For servlets, we need to append a trailing slash
    if (!ret.endsWith (".jsp"))
      return ret + "/";
    return ret;
  }

  @Nonnull
  public String getURL ()
  {
    final StringBuilder aReqUrl = new StringBuilder (m_aHttpRequest.getRequestURL ());
    final String sQueryString = m_aHttpRequest.getQueryString (); // d=789
    if (sQueryString != null)
      aReqUrl.append ('?').append (sQueryString);
    return aReqUrl.toString ();
  }

  @Nonnull
  public String encodeURL (@Nonnull final String sURL)
  {
    return m_aHttpResponse.encodeURL (sURL);
  }

  @Nonnull
  public String encodeRedirectURL (@Nonnull final String sURL)
  {
    return m_aHttpResponse.encodeRedirectURL (sURL);
  }

  public boolean areCookiesEnabled ()
  {
    // Just check whether the session ID is appended to the URL or not
    return "a".equals (encodeURL ("a"));
  }

  @Nullable
  public String getRequestHeader (@Nullable final String sName)
  {
    return m_aHttpRequest.getHeader (sName);
  }

  @Nullable
  public Enumeration <String> getRequestHeaders (@Nullable final String sName)
  {
    return GenericReflection.<Enumeration <?>, Enumeration <String>> uncheckedCast (m_aHttpRequest.getHeaders (sName));
  }

  @Nullable
  public Enumeration <String> getRequestHeaderNames ()
  {
    return GenericReflection.<Enumeration <?>, Enumeration <String>> uncheckedCast (m_aHttpRequest.getHeaderNames ());
  }

  @Nonnull
  public HttpServletRequest getRequest ()
  {
    return m_aHttpRequest;
  }

  @Nonnull
  public HttpServletResponse getResponse ()
  {
    return m_aHttpResponse;
  }

  @Nonnull
  public OutputStream getOutputStream () throws IOException
  {
    return m_aHttpResponse.getOutputStream ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public final Map <String, IScopeRenewalAware> getAllScopeRenewalAwareAttributes ()
  {
    final Map <String, IScopeRenewalAware> ret = new HashMap <String, IScopeRenewalAware> ();
    for (final Map.Entry <String, Object> aEntry : getAllAttributes ().entrySet ())
    {
      final Object aValue = aEntry.getValue ();
      if (aValue instanceof IScopeRenewalAware)
        ret.put (aEntry.getKey (), (IScopeRenewalAware) aValue);
    }
    return ret;
  }

  public boolean isEmpty ()
  {
    return !getAttributeNames ().hasMoreElements ();
  }

  @Nonnegative
  public int size ()
  {
    int ret = 0;
    final Enumeration <String> aEnum = getAttributeNames ();
    while (aEnum.hasMoreElements ())
    {
      aEnum.nextElement ();
      ++ret;
    }
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("scopeID", m_sScopeID)
                                       .append ("httpRequest", m_aHttpRequest)
                                       .append ("httpResponse", m_aHttpResponse)
                                       .toString ();
  }
}
