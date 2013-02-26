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
package com.phloc.webscopes.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.equals.EqualsUtils;
import com.phloc.commons.idfactory.GlobalIDFactory;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.scopes.AbstractMapBasedScope;
import com.phloc.scopes.ScopeUtils;
import com.phloc.web.fileupload.IFileItem;
import com.phloc.web.servlet.request.RequestHelper;
import com.phloc.webscopes.domain.IRequestWebScope;

/**
 * A request web scopes that does not parse multipart requests.
 * 
 * @author philip
 */
public class RequestWebScopeNoMultipart extends AbstractMapBasedScope implements IRequestWebScope
{
  // Because of transient field
  private static final long serialVersionUID = 78563987233146L;

  private static final Logger s_aLogger = LoggerFactory.getLogger (RequestWebScopeNoMultipart.class);
  private static final String REQUEST_ATTR_SCOPE_INITED = "$request.scope.inited";

  protected final transient HttpServletRequest m_aHttpRequest;
  protected final transient HttpServletResponse m_aHttpResponse;

  @Nonnull
  @Nonempty
  private static String _createScopeID (@Nonnull final HttpServletRequest aHttpRequest)
  {
    if (aHttpRequest == null)
      throw new NullPointerException ("httpRequest");

    return GlobalIDFactory.getNewIntID () + "@" + aHttpRequest.getRequestURI ();
  }

  public RequestWebScopeNoMultipart (@Nonnull final HttpServletRequest aHttpRequest,
                                     @Nonnull final HttpServletResponse aHttpResponse)
  {
    super (_createScopeID (aHttpRequest));
    if (aHttpResponse == null)
      throw new NullPointerException ("httpResponse");

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
    if (getAndSetAttributeFlag (REQUEST_ATTR_SCOPE_INITED))
    {
      s_aLogger.warn ("Scope was already inited: " + toString ());
      return;
    }

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

  @Override
  protected void postDestroy ()
  {
    if (ScopeUtils.debugScopeLifeCycle (s_aLogger))
      s_aLogger.info ("Destroyed request web scope '" + getID () + "'");
  }

  @Nonnull
  @Nonempty
  public final String getSessionID ()
  {
    return getSessionID (true);
  }

  @Nullable
  public final String getSessionID (final boolean bCreateIfNotExisting)
  {
    final HttpSession aSession = m_aHttpRequest.getSession (bCreateIfNotExisting);
    return aSession == null ? null : aSession.getId ();
  }

  @Nullable
  public List <String> getAttributeValues (@Nullable final String sName)
  {
    return getAttributeValues (sName, null);
  }

  @Nullable
  public List <String> getAttributeValues (@Nullable final String sName, @Nullable final List <String> aDefault)
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

  public boolean hasAttributeValue (@Nullable final String sName, @Nullable final String sDesiredValue)
  {
    return EqualsUtils.equals (getAttributeAsString (sName), sDesiredValue);
  }

  public boolean hasAttributeValue (@Nullable final String sName,
                                    @Nullable final String sDesiredValue,
                                    final boolean bDefault)
  {
    final String sValue = getAttributeAsString (sName);
    return sValue == null ? bDefault : EqualsUtils.equals (sValue, sDesiredValue);
  }

  public String getCharacterEncoding ()
  {
    return m_aHttpRequest.getCharacterEncoding ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public Map <String, IFileItem> getAllUploadedFileItems ()
  {
    final Map <String, IFileItem> ret = new HashMap <String, IFileItem> ();
    final Enumeration <String> aEnum = getAttributeNames ();
    while (aEnum.hasMoreElements ())
    {
      final String sAttrName = aEnum.nextElement ();
      final Object aAttrValue = getAttributeObject (sAttrName);
      if (aAttrValue instanceof IFileItem)
        ret.put (sAttrName, (IFileItem) aAttrValue);
    }
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <IFileItem> getAllUploadedFileItemValues ()
  {
    final List <IFileItem> ret = new ArrayList <IFileItem> ();
    final Enumeration <String> aEnum = getAttributeNames ();
    while (aEnum.hasMoreElements ())
    {
      final String sAttrName = aEnum.nextElement ();
      final Object aAttrValue = getAttributeObject (sAttrName);
      if (aAttrValue instanceof IFileItem)
        ret.add ((IFileItem) aAttrValue);
      else
        if (aAttrValue instanceof IFileItem [])
          for (final IFileItem aChild : (IFileItem []) aAttrValue)
            ret.add (aChild);
    }
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

  @Nullable
  public String getPathInfo ()
  {
    return RequestHelper.getPathInfo (m_aHttpRequest);
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

  public long getContentLength ()
  {
    return RequestHelper.getContentLength (m_aHttpRequest);
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
    return RequestHelper.getFullServerName (m_aHttpRequest.getScheme (),
                                            m_aHttpRequest.getServerName (),
                                            m_aHttpRequest.getServerPort ());
  }

  @Nonnull
  public String getFullServerPath ()
  {
    return _getFullServerPath ().toString ();
  }

  @Nonnull
  public String getContextPath ()
  {
    return m_aHttpRequest.getContextPath ();
  }

  @Nonnull
  public String getFullContextPath ()
  {
    return _getFullServerPath ().append (m_aHttpRequest.getContextPath ()).toString ();
  }

  /**
   * This is a heuristic method to determine whether a request is for a file
   * (e.g. x.jsp) or for a servlet. It is assumed that regular servlets don't
   * have a '.' in their name!
   * 
   * @param sServletPath
   *        The non-<code>null</code> servlet path to check
   * @return <code>true</code> if it is assumed that the request is file based,
   *         <code>false</code> if it can be assumed to be a regular servlet.
   */
  public boolean isFileBasedRequest (@Nonnull final String sServletPath)
  {
    return sServletPath.indexOf ('.') >= 0;
  }

  @Nonnull
  public String getContextAndServletPath ()
  {
    final String sServletPath = getServletPath ();
    if (isFileBasedRequest (sServletPath))
      return m_aHttpRequest.getContextPath () + sServletPath;
    // For servlets that are not files, we need to append a trailing slash
    return m_aHttpRequest.getContextPath () + sServletPath + '/';
  }

  @Nonnull
  public String getFullContextAndServletPath ()
  {
    final String sServletPath = getServletPath ();
    if (isFileBasedRequest (sServletPath))
      return getFullContextPath () + sServletPath;
    // For servlets, we need to append a trailing slash
    return getFullContextPath () + sServletPath + '/';
  }

  @Nonnull
  public String getURL ()
  {
    return RequestHelper.getURL (m_aHttpRequest);
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
    return RequestHelper.getRequestHeaders (m_aHttpRequest, sName);
  }

  @Nullable
  public Enumeration <String> getRequestHeaderNames ()
  {
    return RequestHelper.getRequestHeaderNames (m_aHttpRequest);
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

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("httpRequest", m_aHttpRequest)
                            .append ("httpResponse", m_aHttpResponse)
                            .toString ();
  }
}
