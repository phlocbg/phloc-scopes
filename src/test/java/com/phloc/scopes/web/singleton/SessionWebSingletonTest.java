package com.phloc.scopes.web.singleton;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.phloc.commons.mock.PhlocTestUtils;
import com.phloc.scopes.web.mock.AbstractWebScopeAwareTestSuite;
import com.phloc.scopes.web.singleton.SessionWebSingleton;

/**
 * Test class for class {@link SessionWebSingleton}.<br>
 * Note: must reside here for Mock* stuff!
 * 
 * @author philip
 */
public final class SessionWebSingletonTest extends AbstractWebScopeAwareTestSuite
{
  @Test
  public void testSerialize () throws Exception
  {
    final MockSessionWebSingleton a = MockSessionWebSingleton.getInstance ();
    assertEquals (0, a.get ());
    a.inc ();
    assertEquals (1, a.get ());
    PhlocTestUtils.testDefaultSerialization (a);
  }
}
