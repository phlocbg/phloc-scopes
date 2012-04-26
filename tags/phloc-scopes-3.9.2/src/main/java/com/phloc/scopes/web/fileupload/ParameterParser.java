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
package com.phloc.scopes.web.fileupload;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple parser intended to parse sequences of name/value pairs. Parameter
 * values are exptected to be enclosed in quotes if they contain unsafe
 * characters, such as '=' characters or separators. Parameter values are
 * optional and can be omitted.
 * <p>
 * <code>param1 = value; param2 = "anything goes; really"; param3</code>
 * </p>
 * 
 * @author <a href="mailto:oleg@ural.ru">Oleg Kalnichevski</a>
 */
public final class ParameterParser
{
  /**
   * String to be parsed.
   */
  private char [] m_aChars = null;

  /**
   * Current position in the string.
   */
  private int m_nPos = 0;

  /**
   * Maximum position in the string.
   */
  private int m_nLen = 0;

  /**
   * Start of a token.
   */
  private int m_nIndex1 = 0;

  /**
   * End of a token.
   */
  private int m_nIndex2 = 0;

  /**
   * Whether names stored in the map should be converted to lower case.
   */
  private boolean m_bLowerCaseNames = false;

  /**
   * Default ParameterParser constructor.
   */
  public ParameterParser ()
  {
    super ();
  }

  /**
   * Are there any characters left to parse?
   * 
   * @return <tt>true</tt> if there are unparsed characters, <tt>false</tt>
   *         otherwise.
   */
  private boolean _hasChar ()
  {
    return this.m_nPos < this.m_nLen;
  }

  /**
   * A helper method to process the parsed token. This method removes leading
   * and trailing blanks as well as enclosing quotation marks, when necessary.
   * 
   * @param quoted
   *        <tt>true</tt> if quotation marks are expected, <tt>false</tt>
   *        otherwise.
   * @return the token
   */
  private String _getToken (final boolean quoted)
  {
    // Trim leading white spaces
    while ((m_nIndex1 < m_nIndex2) && (Character.isWhitespace (m_aChars[m_nIndex1])))
    {
      m_nIndex1++;
    }
    // Trim trailing white spaces
    while ((m_nIndex2 > m_nIndex1) && (Character.isWhitespace (m_aChars[m_nIndex2 - 1])))
    {
      m_nIndex2--;
    }
    // Strip away quotation marks if necessary
    if (quoted)
    {
      if (((m_nIndex2 - m_nIndex1) >= 2) && (m_aChars[m_nIndex1] == '"') && (m_aChars[m_nIndex2 - 1] == '"'))
      {
        m_nIndex1++;
        m_nIndex2--;
      }
    }
    String result = null;
    if (m_nIndex2 > m_nIndex1)
    {
      result = new String (m_aChars, m_nIndex1, m_nIndex2 - m_nIndex1);
    }
    return result;
  }

  /**
   * Tests if the given character is present in the array of characters.
   * 
   * @param ch
   *        the character to test for presense in the array of characters
   * @param charray
   *        the array of characters to test against
   * @return <tt>true</tt> if the character is present in the array of
   *         characters, <tt>false</tt> otherwise.
   */
  private boolean _isOneOf (final char ch, final char [] charray)
  {
    boolean result = false;
    for (final char element : charray)
    {
      if (ch == element)
      {
        result = true;
        break;
      }
    }
    return result;
  }

  /**
   * Parses out a token until any of the given terminators is encountered.
   * 
   * @param terminators
   *        the array of terminating characters. Any of these characters when
   *        encountered signify the end of the token
   * @return the token
   */
  private String _parseToken (final char [] terminators)
  {
    char ch;
    m_nIndex1 = m_nPos;
    m_nIndex2 = m_nPos;
    while (_hasChar ())
    {
      ch = m_aChars[m_nPos];
      if (_isOneOf (ch, terminators))
      {
        break;
      }
      m_nIndex2++;
      m_nPos++;
    }
    return _getToken (false);
  }

  /**
   * Parses out a token until any of the given terminators is encountered
   * outside the quotation marks.
   * 
   * @param terminators
   *        the array of terminating characters. Any of these characters when
   *        encountered outside the quotation marks signify the end of the token
   * @return the token
   */
  private String _parseQuotedToken (final char [] terminators)
  {
    char ch;
    m_nIndex1 = m_nPos;
    m_nIndex2 = m_nPos;
    boolean quoted = false;
    boolean charEscaped = false;
    while (_hasChar ())
    {
      ch = m_aChars[m_nPos];
      if (!quoted && _isOneOf (ch, terminators))
      {
        break;
      }
      if (!charEscaped && ch == '"')
      {
        quoted = !quoted;
      }
      charEscaped = (!charEscaped && ch == '\\');
      m_nIndex2++;
      m_nPos++;

    }
    return _getToken (true);
  }

  /**
   * Returns <tt>true</tt> if parameter names are to be converted to lower case
   * when name/value pairs are parsed.
   * 
   * @return <tt>true</tt> if parameter names are to be converted to lower case
   *         when name/value pairs are parsed. Otherwise returns <tt>false</tt>
   */
  public boolean isLowerCaseNames ()
  {
    return this.m_bLowerCaseNames;
  }

  /**
   * Sets the flag if parameter names are to be converted to lower case when
   * name/value pairs are parsed.
   * 
   * @param b
   *        <tt>true</tt> if parameter names are to be converted to lower case
   *        when name/value pairs are parsed. <tt>false</tt> otherwise.
   */
  public void setLowerCaseNames (final boolean b)
  {
    this.m_bLowerCaseNames = b;
  }

  /**
   * Extracts a map of name/value pairs from the given string. Names are
   * expected to be unique. Multiple separators may be specified and the
   * earliest found in the input string is used.
   * 
   * @param str
   *        the string that contains a sequence of name/value pairs
   * @param separators
   *        the name/value pairs separators
   * @return a map of name/value pairs
   */
  public Map <String, String> parse (final String str, final char [] separators)
  {
    if (separators == null || separators.length == 0)
    {
      return new HashMap <String, String> ();
    }
    char separator = separators[0];
    if (str != null)
    {
      int idx = str.length ();
      for (final char separator2 : separators)
      {
        final int tmp = str.indexOf (separator2);
        if (tmp != -1)
        {
          if (tmp < idx)
          {
            idx = tmp;
            separator = separator2;
          }
        }
      }
    }
    return parse (str, separator);
  }

  /**
   * Extracts a map of name/value pairs from the given string. Names are
   * expected to be unique.
   * 
   * @param str
   *        the string that contains a sequence of name/value pairs
   * @param separator
   *        the name/value pairs separator
   * @return a map of name/value pairs
   */
  public Map <String, String> parse (final String str, final char separator)
  {
    if (str == null)
    {
      return new HashMap <String, String> ();
    }
    return parse (str.toCharArray (), separator);
  }

  /**
   * Extracts a map of name/value pairs from the given array of characters.
   * Names are expected to be unique.
   * 
   * @param chars
   *        the array of characters that contains a sequence of name/value pairs
   * @param separator
   *        the name/value pairs separator
   * @return a map of name/value pairs
   */
  public Map <String, String> parse (final char [] chars, final char separator)
  {
    if (chars == null)
    {
      return new HashMap <String, String> ();
    }
    return parse (chars, 0, chars.length, separator);
  }

  /**
   * Extracts a map of name/value pairs from the given array of characters.
   * Names are expected to be unique.
   * 
   * @param chars
   *        the array of characters that contains a sequence of name/value pairs
   * @param offset
   *        - the initial offset.
   * @param length
   *        - the length.
   * @param separator
   *        the name/value pairs separator
   * @return a map of name/value pairs
   */
  @edu.umd.cs.findbugs.annotations.SuppressWarnings ("EI_EXPOSE_REP2")
  public Map <String, String> parse (final char [] chars, final int offset, final int length, final char separator)
  {
    if (chars == null)
    {
      return new HashMap <String, String> ();
    }
    final HashMap <String, String> params = new HashMap <String, String> ();
    this.m_aChars = chars;
    this.m_nPos = offset;
    this.m_nLen = length;

    String paramName = null;
    String paramValue = null;
    while (_hasChar ())
    {
      paramName = _parseToken (new char [] { '=', separator });
      paramValue = null;
      if (_hasChar () && (chars[m_nPos] == '='))
      {
        m_nPos++; // skip '='
        paramValue = _parseQuotedToken (new char [] { separator });
      }
      if (_hasChar () && (chars[m_nPos] == separator))
      {
        m_nPos++; // skip separator
      }
      if ((paramName != null) && (paramName.length () > 0))
      {
        if (this.m_bLowerCaseNames)
        {
          paramName = paramName.toLowerCase ();
        }
        params.put (paramName, paramValue);
      }
    }
    return params;
  }
}
