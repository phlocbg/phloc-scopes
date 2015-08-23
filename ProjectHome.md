Abstracts scope handling for web and non-web applications.
Contains both the scope management methods as well as scoped singletons with managed destructions.

Major changes in v6.0.0:
  * Fixed package naming so that "nonweb" was removed where applicables, as the web scopes now reside in package "com.phloc.webscopes"

Major changes in v5.0.0:
  * Splitted library into phloc-scopes (non-web scopes only) and phloc-webscopes as part of the [phloc-webbasics](http://code.google.com/p/phloc-webbasics) project

Major changes in v4.5.0:
  * Depencies have been reversed. phloc-scopes now depends on [phloc-web](http://code.google.com/p/phloc-webbasics)

Major changes in v4.0.0:
  * Session and session application scopes (and singletons) are available for non-web scopes too
  * Changed the storage of web request scopes, so that the content is stored in a map and not in the HttpServletRequest itself

Depends on [phloc-commons](http://code.google.com/p/phloc-commons).


---


On Twitter: <a href='https://twitter.com/phloccom'>Follow @phloccom</a>


---


[YourKit](http://www.yourkit.com/) is kindly supporting open source projects with its full-featured Java Profiler.

[YourKit, LLC](http://www.yourkit.com/) is the creator of innovative and intelligent tools for profiling
Java and .NET applications. Take a look at YourKit's leading software products:
[YourKit Java Profiler](http://www.yourkit.com/java/profiler/index.jsp) and
[YourKit .NET Profiler](http://www.yourkit.com/.net/profiler/index.jsp).