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
package com.phloc.scopes.web.mock;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.SystemProperties;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.UnsupportedOperation;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.IReadableResourceProvider;
import com.phloc.commons.io.resourceprovider.DefaultResourceProvider;
import com.phloc.commons.mime.MimeTypeDeterminator;
import com.phloc.commons.string.StringHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Mock implementation of the {@link ServletContext} interface.
 * 
 * @author philip
 */
@NotThreadSafe
public final class MockServletContext implements ServletContext
{
  public static final String DEFAULT_SERVLET_CONTEXT_NAME = "MockServletContext";
  public static final String DEFAULT_SERVLET_CONTEXT_PATH = "";
  private static final Logger s_aLogger = LoggerFactory.getLogger (MockServletContext.class);

  private final IReadableResourceProvider m_aResourceProvider;
  private final String m_sResourceBasePath;
  private String m_sContextPath = DEFAULT_SERVLET_CONTEXT_PATH;
  private final Map <String, ServletContext> m_aContexts = new HashMap <String, ServletContext> ();
  private final Properties m_aInitParameters = new Properties ();
  private final Map <String, Object> m_aAttributes = new HashMap <String, Object> ();
  private String m_sServletContextName = DEFAULT_SERVLET_CONTEXT_NAME;
  private boolean m_bInvalidated = false;

  /**
   * Create a new MockServletContext, using no base path and a
   * DefaultIResourceProvider (i.e. the classpath root as WAR root).
   */
  public MockServletContext ()
  {
    this (null, "", null, null);
  }

  /**
   * Create a new MockServletContext, using no base path and a
   * DefaultIResourceProvider (i.e. the classpath root as WAR root).
   * 
   * @param aInitParams
   *        The init parameter
   */
  public MockServletContext (@Nullable final Map <String, String> aInitParams)
  {
    this (null, "", null, aInitParams);
  }

  /**
   * Create a new MockServletContext.
   * 
   * @param sContextPath
   *        The context path to use
   */
  public MockServletContext (@Nullable final String sContextPath)
  {
    this (sContextPath, "", null, null);
  }

  /**
   * Create a new MockServletContext.
   * 
   * @param sContextPath
   * @param aInitParams
   *        The init parameter The context path to use
   */
  public MockServletContext (@Nullable final String sContextPath, @Nullable final Map <String, String> aInitParams)
  {
    this (sContextPath, "", null, aInitParams);
  }

  /**
   * Create a new MockServletContext.
   * 
   * @param sContextPath
   *        The context path to use
   * @param sResourceBasePath
   *        the WAR root directory (should not end with a slash)
   */
  public MockServletContext (@Nullable final String sContextPath, @Nullable final String sResourceBasePath)
  {
    this (sContextPath, sResourceBasePath, null, null);
  }

  /**
   * Create a new MockServletContext.
   * 
   * @param sContextPath
   *        The context path to use
   * @param sResourceBasePath
   *        the WAR root directory (should not end with a slash)
   * @param aResourceLoader
   *        the IReadableResourceProvider to use (or null for the default)
   */
  public MockServletContext (@Nullable final String sContextPath,
                             @Nullable final String sResourceBasePath,
                             @Nullable final IReadableResourceProvider aResourceLoader,
                             @Nullable final Map <String, String> aInitParams)
  {
    setContextPath (sContextPath);
    m_aResourceProvider = aResourceLoader != null ? aResourceLoader : new DefaultResourceProvider ();
    m_sResourceBasePath = sResourceBasePath != null ? sResourceBasePath : "";

    // Use JVM temp dir as ServletContext temp dir.
    final String sTempDir = SystemProperties.getTmpDir ();
    if (sTempDir != null)
      setAttribute ("tempdir", new File (sTempDir));

    if (aInitParams != null)
      for (final Map.Entry <String, String> aEntry : aInitParams.entrySet ())
        addInitParameter (aEntry.getKey (), aEntry.getValue ());

    final ServletContextEvent aSCE = new ServletContextEvent (this);
    for (final ServletContextListener aListener : MockHttpListener.getAllServletContextListeners ())
      aListener.contextInitialized (aSCE);
  }

  /**
   * Build a full resource location for the given path, prepending the resource
   * base path of this MockServletContext.
   * 
   * @param sPath
   *        the path as specified
   * @return the full resource path
   */
  @Nonnull
  protected String getResourceLocation (@Nonnull final String sPath)
  {
    return StringHelper.startsWith (sPath, '/') ? m_sResourceBasePath + sPath : m_sResourceBasePath + "/" + sPath;
  }

  public void setContextPath (@Nullable final String sContextPath)
  {
    if (sContextPath == null)
      m_sContextPath = "";
    else
      if (StringHelper.startsWith (sContextPath, '/'))
        m_sContextPath = sContextPath;
      else
        m_sContextPath = "/" + sContextPath;
  }

  /* This is a Servlet API 2.5 method. */
  @Nonnull
  public String getContextPath ()
  {
    return m_sContextPath;
  }

  public void registerContext (@Nonnull final String sContextPath, @Nonnull final ServletContext aContext)
  {
    if (sContextPath == null)
      throw new NullPointerException ("contextPath");
    if (aContext == null)
      throw new NullPointerException ("context");
    m_aContexts.put (sContextPath, aContext);
  }

  @Nullable
  public ServletContext getContext (@Nullable final String sContextPath)
  {
    if (m_sContextPath.equals (sContextPath))
      return this;
    return m_aContexts.get (sContextPath);
  }

  public int getMajorVersion ()
  {
    return 2;
  }

  public int getMinorVersion ()
  {
    return 5;
  }

  @Nullable
  public String getMimeType (@Nonnull final String sFilename)
  {
    return MimeTypeDeterminator.getMimeTypeFromFilename (sFilename);
  }

  @UnsupportedOperation
  @Deprecated
  public Set <String> getResourcePaths (final String sPath)
  {
    throw new UnsupportedOperationException ();
  }

  @Nullable
  public URL getResource (@Nonnull final String sPath) throws MalformedURLException
  {
    final IReadableResource aResource = m_aResourceProvider.getReadableResource (getResourceLocation (sPath));
    if (!aResource.exists ())
      return null;
    return aResource.getAsURL ();
  }

  @Nullable
  public InputStream getResourceAsStream (@Nonnull final String sPath)
  {
    final IReadableResource aResource = m_aResourceProvider.getReadableResource (getResourceLocation (sPath));
    if (!aResource.exists ())
      return null;
    return aResource.getInputStream ();
  }

  @Nonnull
  public RequestDispatcher getRequestDispatcher (@Nonnull final String sPath)
  {
    if (!StringHelper.startsWith (sPath, '/'))
      throw new IllegalArgumentException ("RequestDispatcher path at ServletContext level must start with '/'");
    return new MockRequestDispatcher (sPath);
  }

  @Nullable
  @Deprecated
  public RequestDispatcher getNamedDispatcher (@Nullable final String sPath)
  {
    return null;
  }

  @Deprecated
  public Servlet getServlet (@Nullable final String sName)
  {
    return null;
  }

  @Deprecated
  @Nonnull
  public Enumeration <Object> getServlets ()
  {
    return ContainerHelper.<Object> getEmptyEnumeration ();
  }

  @Deprecated
  @Nonnull
  public Enumeration <Object> getServletNames ()
  {
    return ContainerHelper.<Object> getEmptyEnumeration ();
  }

  public void log (@Nullable final String message)
  {
    s_aLogger.info (message);
  }

  @Deprecated
  public void log (@Nullable final Exception ex, @Nullable final String sMessage)
  {
    s_aLogger.info (sMessage, ex);
  }

  public void log (@Nullable final String sMessage, @Nullable final Throwable ex)
  {
    s_aLogger.info (sMessage, ex);
  }

  @Nonnull
  @SuppressFBWarnings ("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
  public String getRealPath (@Nonnull final String sPath)
  {
    final IReadableResource aResource = m_aResourceProvider.getReadableResource (getResourceLocation (sPath));
    if (aResource == null)
      throw new IllegalStateException ("Failed to get real path of '" + sPath + "'");
    final File aFile = aResource.getAsFile ();
    if (aFile == null)
      throw new IllegalStateException ("Failed to convert resource " + aResource + " to a file");
    return aFile.getAbsolutePath ();
  }

  @Nonnull
  @Nonempty
  public String getServerInfo ()
  {
    return "MockServletContext";
  }

  @Nullable
  public String getInitParameter (@Nonnull final String sName)
  {
    if (sName == null)
      throw new NullPointerException ("Parameter name must not be null");
    return m_aInitParameters.getProperty (sName);
  }

  public void addInitParameter (@Nonnull final String sName, @Nonnull final String sValue)
  {
    if (sName == null)
      throw new NullPointerException ("Parameter name must not be null");
    if (sValue == null)
      throw new NullPointerException ("Parameter value must not be null");
    m_aInitParameters.setProperty (sName, sValue);
  }

  @Nonnull
  public Enumeration <Object> getInitParameterNames ()
  {
    return m_aInitParameters.keys ();
  }

  @Nullable
  public Object getAttribute (@Nonnull final String sName)
  {
    if (sName == null)
      throw new NullPointerException ("Attribute name must not be null");
    return m_aAttributes.get (sName);
  }

  @Nonnull
  public Enumeration <String> getAttributeNames ()
  {
    return ContainerHelper.getEnumeration (m_aAttributes.keySet ());
  }

  public void setAttribute (@Nonnull final String sName, @Nullable final Object aValue)
  {
    if (sName == null)
      throw new NullPointerException ("Attribute name must not be null");
    if (aValue != null)
      m_aAttributes.put (sName, aValue);
    else
      m_aAttributes.remove (sName);
  }

  public void removeAttribute (@Nonnull final String sName)
  {
    if (sName == null)
      throw new NullPointerException ("Attribute name must not be null");
    m_aAttributes.remove (sName);
  }

  public void setServletContextName (@Nullable final String sServletContextName)
  {
    m_sServletContextName = sServletContextName;
  }

  @Nullable
  public String getServletContextName ()
  {
    return m_sServletContextName;
  }

  public void invalidate ()
  {
    if (m_bInvalidated)
      throw new IllegalArgumentException ("Servlet context already invalidated!");
    m_bInvalidated = true;

    final ServletContextEvent aSCE = new ServletContextEvent (this);
    for (final ServletContextListener aListener : MockHttpListener.getAllServletContextListeners ())
      aListener.contextDestroyed (aSCE);

    m_aAttributes.clear ();
  }
}
