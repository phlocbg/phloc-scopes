# Change log #
<a href='Hidden comment: This content is generated. Do not modify!'></a>
<table border='1' cellspacing='0'><thead><tr><th>Date</th><th>Action</th><th>Category</th><th>Incompatible?</th><th>Description</th></tr></thead><tbody>
<tr border='1'><td>4/14/14</td><td><i>Release <b>6.2.1</b></i></td></tr>
<tr border='1'><td>4/14/14</td><td><i>Release <b>6.2.0</b></i></td></tr>
<tr border='1'><td>2/28/14</td><td><i>Release <b>6.1.8</b></i></td></tr>
<tr><td>2/28/14</td><td>fix</td><td>tech</td><td></td><td>Improved automatic serialization handling for SessionSingleton and SessionApplicationSingleton</td></tr>
<tr border='1'><td>2/27/14</td><td><i>Release <b>6.1.7</b></i></td></tr>
<tr><td>2/27/14</td><td>add</td><td>api</td><td></td><td>Extended AbstractSingleton with protected setters for destruction state</td></tr>
<tr border='1'><td>2/25/14</td><td><i>Release <b>6.1.6</b></i></td></tr>
<tr><td>2/25/14</td><td>add</td><td>api</td><td></td><td>Extended AbstractSingleton with methods to determine the instantiation state</td></tr>
<tr><td>2/25/14</td><td>remove</td><td>api</td><td><b>yes</b></td><td>Removed all deprecated methods in <code>*</code>Singleton classes</td></tr>
<tr><td>2/25/14</td><td>update</td><td>api</td><td></td><td>ScopeUtils has now the possibility to enable/disable logging for certain scope types only</td></tr>
<tr border='1'><td>1/2/14</td><td><i>Release <b>6.1.5</b></i></td></tr>
<tr><td>1/2/14</td><td>update</td><td>tech</td><td></td><td>Updated to <a href='http://code.google.com/p/phloc-commons'>phloc-commons</a> 4.1.0</td></tr>
<tr border='1'><td>8/29/13</td><td><i>Release <b>6.1.4</b></i></td></tr>
<tr><td>8/28/13</td><td>update</td><td>tech</td><td></td><td>Updated to <a href='http://code.google.com/p/phloc-commons'>phloc-commons</a> 4.0.9</td></tr>
<tr><td>5/3/13</td><td>add</td><td>api</td><td></td><td>Added public methods isDestroyed() and isInDestruction() to AbstractSingleton</td></tr>
<tr><td>4/17/13</td><td>update</td><td>tech</td><td></td><td>Updated to <a href='http://code.google.com/p/phloc-commons'>phloc-commons</a> 4.0.3</td></tr>
<tr border='1'><td>3/27/13</td><td><i>Release <b>6.1.3</b></i></td></tr>
<tr><td>3/26/13</td><td>change</td><td>api</td><td></td><td>Fixed naming of singleton methods, so that the scope type is included in the method name</td></tr>
<tr border='1'><td>3/26/13</td><td><i>Release <b>6.1.2</b></i></td></tr>
<tr><td>3/25/13</td><td>add</td><td>api</td><td></td><td>Added new classes AbstractScopeAwareCallable and AbstractScopeAwareRunnable</td></tr>
<tr><td>3/13/13</td><td>add</td><td>tech</td><td></td><td>Added call to CommonsCleanup in ScopeAwareTestSetup</td></tr>
<tr><td>3/13/13</td><td>update</td><td>tech</td><td></td><td>Updated to <a href='http://code.google.com/p/phloc-commons'>phloc-commons</a> 4.0.0</td></tr>
<tr border='1'><td>3/13/13</td><td><i>Release <b>6.1.1</b></i></td></tr>
<tr><td>3/13/13</td><td>fix</td><td>api</td><td></td><td>Fixed template parameters of getSingletonIfInstantiated method</td></tr>
<tr border='1'><td>3/13/13</td><td><i>Release <b>6.1.0</b></i></td></tr>
<tr><td>3/13/13</td><td>add</td><td>test</td><td></td><td>Improved test coverage from 55 to 80% - no bugs found :)</td></tr>
<tr><td>3/13/13</td><td>change</td><td>api</td><td></td><td>Marked all singleton tree classes as abstract</td></tr>
<tr><td>3/13/13</td><td>add</td><td>api</td><td></td><td>Added new method getSingletonIfInstantiated to all singleton classes</td></tr>
<tr border='1'><td>2/28/13</td><td><i>Release <b>6.0.0</b></i></td></tr>
<tr><td>2/28/13</td><td>remove</td><td>api</td><td></td><td>Removed deprecated class AbstractScopeAwareTestCase</td></tr>
<tr><td>2/28/13</td><td>change</td><td>api</td><td><b>yes</b></td><td>Removed "nonweb" from all package names</td></tr>
<tr border='1'><td>2/27/13</td><td><i>Release <b>5.0.0</b></i></td></tr>
<tr><td>2/27/13</td><td>remove</td><td>api</td><td></td><td>Moved all web-related scope handling to a new separate project <a href='http://code.google.com/p/phloc-webbasics'>phloc-webscopes</a></td></tr>
<tr border='1'><td>2/27/13</td><td><i>Release <b>4.5.0</b></i></td></tr>
<tr><td>2/26/13</td><td>update</td><td>api</td><td><b>yes</b></td><td>Using <a href='http://code.google.com/p/phloc-webbasics'>phloc-web</a> 5.0.1 for all web-related stuff</td></tr>
<tr border='1'><td>2/24/13</td><td><i>Release <b>4.2.0</b></i></td></tr>
<tr><td>2/24/13</td><td>update</td><td>api</td><td><b>yes</b></td><td>Updated to <a href='http://code.google.com/p/phloc-commons'>phloc-commons</a> 3.9.6 because of extended ServiceLoader API</td></tr>
<tr><td>2/20/13</td><td>change</td><td>api</td><td><b>yes</b></td><td>Switch content length handling of request scope and file upload from int to long</td></tr>
<tr><td>2/16/13</td><td>add</td><td>tech</td><td></td><td>Added SPI IProgressListenerProvider for providing ProgressListener implementations for file upload</td></tr>
<tr><td>2/16/13</td><td>fix</td><td>tech</td><td></td><td>Added int to long conversion for request content length to retrieve proper values for simple overflows</td></tr>
<tr border='1'><td>1/24/13</td><td><i>Release <b>4.1.6</b></i></td></tr>
<tr><td>1/24/13</td><td>update</td><td>api</td><td><b>yes</b></td><td>Updated to <a href='http://code.google.com/p/phloc-commons'>phloc-commons</a> 3.9.4 because of extended tree API</td></tr>
<tr border='1'><td>1/2/13</td><td><i>Release <b>4.1.5</b></i></td></tr>
<tr><td>1/1/13</td><td>add</td><td>api</td><td></td><td>Added <code>*</code>SingletonTreeWithUniqueID implementation for all other scopes as well</td></tr>
<tr border='1'><td>11/2/12</td><td><i>Release <b>4.1.4</b></i></td></tr>
<tr><td>11/1/12</td><td>add</td><td>api</td><td></td><td>Added IFileItemFactoryProvider SPI interface for providing a custom item factory for file upload handling in RequestWebScope</td></tr>
<tr><td>9/28/12</td><td>add</td><td>api</td><td></td><td>Added method IRequestWebScopeWithoutResponse.getContextPath()</td></tr>
<tr><td>9/20/12</td><td>update</td><td>performance</td><td></td><td>Performance improvement by using <a href='http://code.google.com/p/phloc-commons'>phloc-commons</a> 3.8.4</td></tr>
<tr border='1'><td>9/18/12</td><td><i>Release <b>4.1.3</b></i></td></tr>
<tr><td>9/18/12</td><td>change</td><td>api</td><td><b>yes</b></td><td>Derived ScopeRenewalAwareWrapper from Wrapper - requires <a href='http://code.google.com/p/phloc-commons'>phloc-commons</a> 3.8.3</td></tr>
<tr><td>9/15/12</td><td>change</td><td>tech</td><td></td><td>Set default method in MockHttpServletRequest to "GET"</td></tr>
<tr><td>9/15/12</td><td>fix</td><td>tech</td><td></td><td>Fixed incorrect initialization of protocol value in MockHttpServletRequest</td></tr>
<tr border='1'><td>9/13/12</td><td><i>Release <b>4.1.2</b></i></td></tr>
<tr><td>9/13/12</td><td>change</td><td>api</td><td></td><td>Extracted a base interface IRequestWebScopeWithoutResponse from IRequestWebScope so that it can safely be used in the context of <a href='http://code.google.com/p/phloc-webbasics'>phloc-webbasics</a> UnifiedResponse</td></tr>
<tr border='1'><td>9/10/12</td><td><i>Release <b>4.1.1</b></i></td></tr>
<tr><td>9/10/12</td><td>add</td><td>api</td><td></td><td>Made session passivation/activation customizable in WebScopeManager. It is disabled by default.</td></tr>
<tr><td>9/10/12</td><td>remove</td><td>api</td><td><b>yes</b></td><td>Removed the AbstractSerializableSingleton class again because it was of no use</td></tr>
<tr><td>9/10/12</td><td>add</td><td>api</td><td></td><td>Added support for session passivation and activation for session web scopes</td></tr>
<tr><td>9/10/12</td><td>add</td><td>api</td><td></td><td>Extended WebScopeTestRule so that context path and init parameters can be set explicitly</td></tr>
<tr border='1'><td>9/10/12</td><td><i>Release <b>4.1.0</b></i></td></tr>
<tr><td>9/10/12</td><td>fix</td><td>tech</td><td></td><td>Fixed the session destruction because ScopeSessionManager.getInstance was not accessible upon shutdown</td></tr>
<tr><td>9/10/12</td><td>change</td><td>api</td><td></td><td>Made AbstractSingleton.getAllSingletons and isSingletonInstantiated work without a scope</td></tr>
<tr><td>9/10/12</td><td>change</td><td>api</td><td></td><td>Extracted new base class AbstractSerializableSingleton</td></tr>
<tr border='1'><td>9/6/12</td><td><i>Release <b>4.0.15</b></i></td></tr>
<tr><td>9/6/12</td><td>update</td><td>tech</td><td></td><td>Updated to <a href='http://code.google.com/p/phloc-commons'>phloc-commons</a> 3.8.0</td></tr>
<tr border='1'><td>9/5/12</td><td><i>Release <b>4.0.13</b></i></td></tr>
<tr><td>9/5/12</td><td>add</td><td>api</td><td></td><td>ScopeSessionManager now has a possibility to prevent all sessions from being ended upon global scope destruction when session destruction is prohibited so that the singletons are stored</td></tr>
<tr border='1'><td>8/31/12</td><td><i>Release <b>4.0.12</b></i></td></tr>
<tr><td>8/31/12</td><td>change</td><td>tech</td><td></td><td>Once again tried to resolve potential deadlock in GlobalScope handling</td></tr>
<tr border='1'><td>8/30/12</td><td><i>Release <b>4.0.11</b></i></td></tr>
<tr><td>8/30/12</td><td>change</td><td>tech</td><td></td><td>Really Fixed potential deadlock in GlobalScope handling</td></tr>
<tr border='1'><td>8/30/12</td><td><i>Release <b>4.0.10</b></i></td></tr>
<tr><td>8/30/12</td><td>change</td><td>tech</td><td></td><td>Fixed potential deadlock in GlobalScope handling, if one thread is trying to access the global scope while it is shutdown in another thread</td></tr>
<tr><td>8/19/12</td><td>add</td><td>api</td><td></td><td>ScopeSessionManager now has a possibility to prevent all sessions from being destroyed upon global scope destruction</td></tr>
<tr border='1'><td>8/19/12</td><td><i>Release <b>4.0.9</b></i></td></tr>
<tr><td>8/19/12</td><td>add</td><td>api</td><td></td><td>Added the possibility to disable HTTP event triggering in MockHttpServletRequest and OfflineHttpServletRequest</td></tr>
<tr><td>8/19/12</td><td>change</td><td>api</td><td></td><td>Made MockEventListenerList thread safe</td></tr>
<tr><td>8/19/12</td><td>change</td><td>tech</td><td></td><td>By default MockHttpListener does not contain any default listener</td></tr>
<tr border='1'><td>8/18/12</td><td><i>Release <b>4.0.8</b></i></td></tr>
<tr><td>8/18/12</td><td>fix</td><td>tech</td><td></td><td>Fixed testability with different mock HTTP listener configurations</td></tr>
<tr><td>8/18/12</td><td>fix</td><td>tech</td><td></td><td>Fixed missing debug message when destructing request web scopes</td></tr>
<tr border='1'><td>8/17/12</td><td><i>Release <b>4.0.7</b></i></td></tr>
<tr><td>8/17/12</td><td>change</td><td>tech</td><td></td><td>WebScopeTestRule now sets the MockHttpListeners to default on every invocation</td></tr>
<tr><td>8/17/12</td><td>add</td><td>api</td><td></td><td>The application ID of MockServletRequestListener can now be customized</td></tr>
<tr><td>8/17/12</td><td>add</td><td>api</td><td></td><td>Added method MockServletRequestListener.getCurrentMockResponse () to retrieve the current response</td></tr>
<tr border='1'><td>8/16/12</td><td><i>Release <b>4.0.6</b></i></td></tr>
<tr><td>8/16/12</td><td>change</td><td>api</td><td></td><td>WebScopeAwareTestSetup does not depend on JUnit</td></tr>
<tr><td>8/16/12</td><td>change</td><td>api</td><td></td><td>Some visibility and exception changes</td></tr>
<tr border='1'><td>8/16/12</td><td><i>Release <b>4.0.5</b></i></td></tr>
<tr><td>8/15/12</td><td>add</td><td>api</td><td></td><td>Added JUnit TestRule implementations for Scope and WebScope</td></tr>
<tr><td>8/15/12</td><td>remove</td><td>api</td><td><b>yes</b></td><td>Removed <code>*</code>ScopeAwareTestSuite classes</td></tr>
<tr><td>8/15/12</td><td>update</td><td>api</td><td></td><td>Improved testability of scope stuff</td></tr>
<tr><td>8/15/12</td><td>add</td><td>api</td><td></td><td>Made MockHttpListener default values customizable</td></tr>
<tr border='1'><td>8/10/12</td><td><i>Release <b>4.0.4</b></i></td></tr>
<tr><td>8/10/12</td><td>fix</td><td>tech</td><td></td><td>MockHttpServletRequest now disables x-gzip and x-compress accept encoding</td></tr>
<tr><td>8/10/12</td><td>add</td><td>api</td><td></td><td>Added some more MockHttpServletResponse sanity methods</td></tr>
<tr><td>8/10/12</td><td>fix</td><td>tech</td><td></td><td>Fixed MockHttpServletResponse.getContentAsString if no charset is defined</td></tr>
<tr><td>8/8/12</td><td>change</td><td>tech</td><td></td><td>SessionBackedRequestFieldData now allows for empty request values</td></tr>
<tr border='1'><td>8/8/12</td><td><i>Release <b>4.0.3</b></i></td></tr>
<tr><td>8/6/12</td><td>fix</td><td>tech</td><td></td><td>Fixed exception when creating a global web scope where the servlet context does not have a servlet context name</td></tr>
<tr><td>8/3/12</td><td>change</td><td>api</td><td></td><td>IFileItem.write now returns a success indicator</td></tr>
<tr border='1'><td>7/24/12</td><td><i>Release <b>4.0.2</b></i></td></tr>
<tr><td>7/24/12</td><td>add</td><td>api</td><td></td><td>Added methods in WebScopeSessionManager to handle ISessionWebScope objects only</td></tr>
<tr><td>7/24/12</td><td>change</td><td>api</td><td></td><td>Undeprecated WebScopeSessionManager</td></tr>
<tr border='1'><td>7/23/12</td><td><i>Release <b>4.0.1</b></i></td></tr>
<tr><td>7/23/12</td><td>add</td><td>api</td><td></td><td>Added new method ScopeSessionManager.destroyAllSessions</td></tr>
<tr><td>7/23/12</td><td>add</td><td>api</td><td></td><td>Added new method IGlobalScope.getApplicationScopeCount</td></tr>
<tr><td>7/23/12</td><td>add</td><td>api</td><td></td><td>Added new method ISessionScope.getSessionApplicationScopeCount</td></tr>
<tr><td>7/23/12</td><td>add</td><td>api</td><td></td><td>Extended MockHttpServletRequest so that a certain session ID can be used instead of always creating a new one</td></tr>
<tr><td>7/23/12</td><td>add</td><td>api</td><td></td><td>Improved API of AbstractWebScopeAwareTest(Suite|Case) and WebScopeAwareTestSetup</td></tr>
<tr><td>7/23/12</td><td>add</td><td>api</td><td></td><td>Added new method WebScopeSessionHelper.renewSessionScope(HttpSession,boolean)</td></tr>
<tr border='1'><td>7/12/12</td><td><i>Release <b>4.0.0</b></i></td></tr>
<tr><td>7/11/12</td><td>update</td><td>api</td><td></td><td>Improved backward compatibility</td></tr>
<tr border='1'><td>7/10/12</td><td><i>Release <b>4.0.0.RC1</b></i></td></tr>
<tr><td>7/10/12</td><td>add</td><td>api</td><td></td><td>Added class SessionApplicationSingleton</td></tr>
<tr><td>7/10/12</td><td>change</td><td>api</td><td><b>yes</b></td><td>Renamed SessionApplicationSingleton to SessionApplicationWebSingleton</td></tr>
<tr><td>7/10/12</td><td>change</td><td>api</td><td></td><td>SessionApplicationWebScope is now derived from SessionApplicationScope</td></tr>
<tr><td>7/10/12</td><td>change</td><td>tech</td><td><b>yes</b></td><td>Not storing request parameters in the HttpServletRequest itself but in a Map</td></tr>
<tr><td>7/10/12</td><td>change</td><td>api</td><td></td><td>Made WebScopeListener non-final</td></tr>
<tr><td>7/9/12</td><td>change</td><td>api</td><td></td><td>Default scope factories are non-final</td></tr>
<tr><td>6/25/12</td><td>add</td><td>feature</td><td></td><td>Started adding Session and SessionApplication scopes for non-web applications</td></tr>
<tr border='1'><td>6/25/12</td><td><i>Release <b>3.9.7</b></i></td></tr>
<tr><td>6/25/12</td><td>update</td><td>tech</td><td></td><td>Minor improvement in lock handling of RequestWebScopeNoMultipart</td></tr>
<tr><td>6/25/12</td><td>update</td><td>tech</td><td></td><td>Updated to <a href='http://code.google.com/p/phloc-commons'>phloc-commons</a> 3.6.1</td></tr>
<tr><td>6/6/12</td><td>add</td><td>api</td><td></td><td>Added new class GlobalSingletonTreeWithUniqueID</td></tr>
<tr><td>5/29/12</td><td>add</td><td>api</td><td></td><td>Added new method IRequestWebScope.getAllUploadedFileItemValues ()</td></tr>
<tr border='1'><td>5/28/12</td><td><i>Release <b>3.9.6</b></i></td></tr>
<tr><td>5/28/12</td><td>fix</td><td>tech</td><td></td><td>Fixed locking call in RequestWebScopeNoMultipart.destroyScope (was lock/lock instead of lock/unlock)</td></tr>
<tr><td>5/25/12</td><td>add</td><td>api</td><td></td><td>Added new MockServletContext constructor</td></tr>
<tr border='1'><td>5/24/12</td><td><i>Release <b>3.9.5</b></i></td></tr>
<tr><td>5/24/12</td><td>update</td><td>tech</td><td></td><td>Updated to <a href='http://code.google.com/p/phloc-commons'>phloc-commons</a> 3.5.6</td></tr>
<tr border='1'><td>5/23/12</td><td><i>Release <b>3.9.4</b></i></td></tr>
<tr><td>5/21/12</td><td>change</td><td>tech</td><td></td><td>Made WebScopeManager.getSessionScope(false) more robust</td></tr>
<tr><td>5/21/12</td><td>add</td><td>api</td><td></td><td>Added new method (Web)ScopeManager.getRequestScopeOrNull ()</td></tr>
<tr><td>5/11/12</td><td>add</td><td>api</td><td></td><td>Extended AbstractSingleton with a method isSingletonInstantiated</td></tr>
<tr><td>5/11/12</td><td>add</td><td>api</td><td></td><td>Extended IScope with a method runAtomic(INonThrowingCallableWithParameter)</td></tr>
<tr border='1'><td>5/3/12</td><td><i>Release <b>3.9.3</b></i></td></tr>
<tr><td>5/3/12</td><td>update</td><td>api</td><td></td><td>Made API of ScopeManager and WebScopeManager more complete</td></tr>
<tr><td>5/3/12</td><td>add</td><td>tech</td><td></td><td>Added SPI support for handling the scope lifecycle of all scope types</td></tr>
<tr border='1'><td>4/26/12</td><td><i>Release <b>3.9.2</b></i></td></tr>
<tr><td>4/26/12</td><td>add</td><td>api</td><td></td><td>Renamed utility classes (SessionBacked)RequestField to (SessionBacked)RequestFieldData</td></tr>
<tr border='1'><td>4/26/12</td><td><i>Release <b>3.9.1</b></i></td></tr>
<tr><td>4/26/12</td><td>add</td><td>api</td><td></td><td>Added new utility classes RequestField and SessionBackedRequestField</td></tr>
<tr><td>4/25/12</td><td>add</td><td>api</td><td></td><td>Added method getAllUploadedFileItems to class IRequestWebScope</td></tr>
<tr border='1'><td>4/23/12</td><td><i>Release <b>3.9.0</b></i></td></tr>
<tr><td>4/23/12</td><td>update</td><td>tech</td><td><b>yes</b></td><td>Updated to <a href='http://code.google.com/p/phloc-commons'>phloc-commons</a> 3.5.0</td></tr>
<tr border='1'><td>4/4/12</td><td><i>Release <b>3.8.5</b></i></td></tr>
<tr><td>4/4/12</td><td>update</td><td>tech</td><td></td><td>Updated to <a href='http://code.google.com/p/phloc-commons'>phloc-commons</a> 3.4.9</td></tr>
<tr border='1'><td>4/3/12</td><td><i>Release <b>3.8.4</b></i></td></tr>
<tr><td>4/3/12</td><td>change</td><td>api</td><td><b>yes</b></td><td>Renamed RequestWebScopeNoFileItems to RequestWebScopeNoMultipart</td></tr>
<tr><td>4/3/12</td><td>add</td><td>tech</td><td></td><td>Added a warning in case a request scope application ID is not used</td></tr>
<tr border='1'><td>4/3/12</td><td><i>Release <b>3.8.3</b></i></td></tr>
<tr><td>4/3/12</td><td>add</td><td>api</td><td></td><td>Made class ScopeRenewalAwareWrapper Serializable</td></tr>
<tr border='1'><td>4/3/12</td><td><i>Release <b>3.8.2</b></i></td></tr>
<tr><td>4/3/12</td><td>add</td><td>api</td><td></td><td>Added new class ScopeRenewalAwareWrapper</td></tr>
<tr><td>4/3/12</td><td>add</td><td>api</td><td></td><td>Added new class DefaultScopeAwareFilter as the most simple scope aware filter</td></tr>
<tr border='1'><td>4/3/12</td><td><i>Release <b>3.8.1</b></i></td></tr>
<tr><td>4/3/12</td><td>change</td><td>api</td><td></td><td>Made method AbstractSingleton.getSingletonScopeKey(Class) public</td></tr>
<tr><td>4/3/12</td><td>add</td><td>api</td><td></td><td>Added new class RequestWebScopeNoFileUpload that does not try to parse multipart requests</td></tr>
<tr><td>4/3/12</td><td>add</td><td>api</td><td></td><td>Added the class FileItemResource that encapsulates an IFileItem within an IReadableResource</td></tr>
<tr border='1'><td>4/3/12</td><td><i>Release <b>3.8.0</b></i></td></tr>
<tr><td>4/3/12</td><td>change</td><td>tech</td><td><b>yes</b></td><td>Changed the way how temporary files are deleted without using a separate thread, using a GlobalSingleton</td></tr>
<tr><td>4/3/12</td><td>change</td><td>tech</td><td><b>yes</b></td><td>Changed the naming of the fileupload interfaces</td></tr>
<tr border='1'><td>4/2/12</td><td><i>Release <b>3.7.3</b></i></td></tr>
<tr><td>4/2/12</td><td>add</td><td>tech</td><td></td><td>Added a new class WebScopeListener that solely handles global and session scope initialization an destruction</td></tr>
<tr><td>4/2/12</td><td>add</td><td>api</td><td></td><td>Added a new method WebScopeSessionManager.getSessionScopeOfID</td></tr>
<tr><td>4/2/12</td><td>add</td><td>tech</td><td></td><td>Added the possibility to dynamically enable/disable scope lifecycle debugging via ScopeUtils class</td></tr>
<tr border='1'><td>3/29/12</td><td><i>Release <b>3.7.2</b></i></td></tr>
<tr><td>3/29/12</td><td>fix</td><td>tech</td><td></td><td>Caught wrong exception in WebScopeSessionManager.onDestroy</td></tr>
<tr border='1'><td>3/27/12</td><td><i>Release <b>3.7.1</b></i></td></tr>
<tr><td>3/27/12</td><td>fix</td><td>tech</td><td></td><td>Fixed dynamic determination of servlet context ContextPath for Jetty</td></tr>
<tr border='1'><td>3/27/12</td><td><i>Release <b>3.7.0</b></i></td></tr>
<tr><td>3/27/12</td><td>add</td><td>api</td><td></td><td>Added the method IScope.getAllScopeRenewalAwareAttributes ()</td></tr>
<tr><td>3/27/12</td><td>add</td><td>api</td><td></td><td>Added the method WebScopeSessionHelper.renewSessionScope that correctly handles all session application scopes</td></tr>
<tr><td>3/27/12</td><td>change</td><td>api</td><td><b>yes</b></td><td>Renamed ISurvivingSessionRenewal to IScopeRenewalAware</td></tr>
<tr><td>3/27/12</td><td>add</td><td>api</td><td></td><td>Added new method ISessionWebScope.getAllSessionApplicationScopes ()</td></tr>
<tr><td>3/27/12</td><td>add</td><td>api</td><td></td><td>Added new method IGlobalScope.getAllApplicationScopes ()</td></tr>
<tr><td>3/27/12</td><td>add</td><td>api</td><td></td><td>Extended WebScopeSessionManager API with retrieval methods</td></tr>
<tr><td>3/27/12</td><td>fix</td><td>tech</td><td></td><td>Tried to add support for Servlet API 2.4 (no ServletContext.getContextPath method)</td></tr>
<tr border='1'><td>3/26/12</td><td><i>Release <b>3.6.0</b></i></td></tr>
<tr><td>3/26/12</td><td>add</td><td>tech</td><td></td><td>Added file upload handling into this project (based on commons-fileupload and commons-io)</td></tr>
<tr><td>3/26/12</td><td>fix</td><td>tech</td><td></td><td>Fixed some minor FindBugs issues</td></tr>
<tr border='1'><td>3/23/12</td><td><i>Release <b>3.5.3</b></i></td></tr>
<tr><td>3/23/12</td><td>add</td><td>api</td><td></td><td>Added new class RequestScopeInitializer to consistently handle nested request scopes.</td></tr>
<tr border='1'><td>3/23/12</td><td><i>Release <b>3.5.2</b></i></td></tr>
<tr><td>3/23/12</td><td>fix</td><td>tech</td><td></td><td>AbstractScopeAwareFilter can now handle multiple scope aware filters within a servlet filter chain</td></tr>
<tr><td>3/23/12</td><td>add</td><td>api</td><td></td><td>(Web)ScopeManager now has the methods isRequestScopePresent and isGlobalScopePresent</td></tr>
<tr><td>3/23/12</td><td>change</td><td>api</td><td></td><td>Added special base classes for Servlet filters and HTTP servlets handling the scopes</td></tr>
<tr border='1'><td>3/23/12</td><td><i>Release <b>3.5.1</b></i></td></tr>
<tr><td>3/22/12</td><td>change</td><td>api</td><td></td><td>Added special web scope test base classes AbstractWebScopeAwareTest<code>*</code></td></tr>
<tr><td>3/22/12</td><td>change</td><td>api</td><td></td><td>Moved servlet API here from phloc-net</td></tr>
<tr><td>3/22/12</td><td>fix</td><td>tech</td><td></td><td>Fixed error in locking</td></tr>
<tr border='1'><td>3/22/12</td><td><i>Release <b>3.5.0</b></i></td></tr>
<tr><td>3/22/12</td><td>change</td><td>api</td><td><b>yes</b></td><td>Total change of package structure to clearly reflect the changes between web and nonweb</td></tr>
<tr border='1'><td>3/20/12</td><td><i>Release <b>3.0.0</b></i></td></tr>
<tr><td>3/19/12</td><td>change</td><td>tech</td><td><b>yes</b></td><td>The SessionWebScope no longer directly operates on the HTTP Session but on a Map</td></tr>
<tr><td>3/19/12</td><td>change</td><td>api</td><td><b>yes</b></td><td>Swapped the dependency direction between <a href='http://code.google.com/p/phloc-commons'>phloc-events</a> and <a href='http://code.google.com/p/phloc-scopes'>phloc-scopes</a></td></tr>
<tr><td>3/19/12</td><td>add</td><td>api</td><td></td><td>Moved the web scopes + manager to this project</td></tr>
<tr><td>3/19/12</td><td>add</td><td>api</td><td></td><td>Added isValid and isInDestruction methods into IScope</td></tr>
<tr border='1'><td>3/14/12</td><td><i>Release <b>2.8.0</b></i></td></tr>
<tr><td>3/14/12</td><td>fix</td><td>tech</td><td></td><td>Fixed synchronization handling of scopes</td></tr>
<tr><td>3/14/12</td><td>add</td><td>api</td><td><b>yes</b></td><td>Added new method runAtomic in IScope to perform multiple actions in one write lock</td></tr>
<tr><td>3/13/12</td><td>change</td><td>tech</td><td></td><td>Made exception handling in scope destruction for invocation of IScopeDestructionAware more narrow and flexible</td></tr>
<tr border='1'><td>2/23/12</td><td><i>Release <b>2.7.0</b></i></td></tr>
<tr><td>2/23/12</td><td>add</td><td>api</td><td></td><td>Added new IScope method boolean isDestroyed()</td></tr>
<tr border='1'><td>1/27/12</td><td><i>Release <b>2.6.0</b></i></td></tr>
<tr><td>1/27/12</td><td>change</td><td>tech</td><td></td><td>Removed POM dependency from <a href='http://code.google.com/p/phloc-datetime'>phloc-datetime</a></td></tr>
<tr border='1'><td>1/21/12</td><td><i>Release <b>2.5.3</b></i></td></tr>
<tr border='1'><td>1/21/12</td><td><i>Release <b>2.5.2</b></i></td></tr>
<tr border='1'><td>9/12/11</td><td><i>Release <b>2.5.1</b></i></td></tr>
<tr><td>9/12/11</td><td>update</td><td>api</td><td></td><td>Updated to <a href='http://code.google.com/p/phloc-commons'>phloc-commons</a> 3.1.0</td></tr>
</tbody></table>