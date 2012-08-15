package com.phloc.scopes.web.mock;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.NotThreadSafe;
import javax.servlet.http.HttpSession;

import com.phloc.commons.annotations.OverrideOnDemand;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.scopes.nonweb.mock.ScopeTestRule;

@NotThreadSafe
public class WebScopeTestRule extends ScopeTestRule
{
  public static final String MOCK_CONTEXT = "/MockContext";

  private final Map <String, String> m_aServletContextInitParameters;
  private MockServletContext m_aServletContext;
  private MockHttpServletRequest m_aRequest;

  public WebScopeTestRule ()
  {
    this (null);
  }

  public WebScopeTestRule (@Nullable final Map <String, String> aServletContextInitParameters)
  {
    m_aServletContextInitParameters = aServletContextInitParameters;
  }

  @Nonnull
  @ReturnsMutableCopy
  @OverrideOnDemand
  public Map <String, String> getServletContextInitParameters ()
  {
    return ContainerHelper.newMap (m_aServletContextInitParameters);
  }

  @Override
  @OverrideOnDemand
  @OverridingMethodsMustInvokeSuper
  protected void before ()
  {
    // Start global scope -> triggers events
    m_aServletContext = new MockServletContext (MOCK_CONTEXT, getServletContextInitParameters ());

    // Start request scope -> triggers events
    m_aRequest = new MockHttpServletRequest (m_aServletContext);
  }

  @Override
  @OverrideOnDemand
  @OverridingMethodsMustInvokeSuper
  protected void after ()
  {
    if (m_aRequest != null)
    {
      // end request -> triggers events
      m_aRequest.invalidate ();
      m_aRequest = null;
    }

    if (m_aServletContext != null)
    {
      // shutdown global context -> triggers events
      m_aServletContext.invalidate ();
      m_aServletContext = null;
    }
  }

  @Nullable
  public final MockServletContext getServletContext ()
  {
    return m_aServletContext;
  }

  @Nullable
  public final MockHttpServletRequest getRequest ()
  {
    return m_aRequest;
  }

  @Nullable
  public final HttpSession getSession (final boolean bCreateIfNotExisting)
  {
    return m_aRequest == null ? null : m_aRequest.getSession (bCreateIfNotExisting);
  }
}
