/*
    This file is part of Aspa.

    Aspa is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    Aspa is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Aspa; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
header {
package gr.omadak.leviathan.asp;
<import>
}

class HtmlLexer extends Lexer;

options {
    exportVocab = Html;
    caseSensitive=false;
    testLiterals = false;
    filter=false;
    defaultErrorHandler = false;
    k = 3;
    charVocabulary = '\u0003'..'\u7FFF';
}

<init>

TAG
  <init_tag>
  :
  '<'
  (
      "!" (
              "--"
              ((' ' | '\t')=> SPACE_TAB |)
              (
                ('#')=> "#include" SPACE_TAB k:NAME (SPACE_WS)? '='
                  l:ATTRIBUTE_VALUE (SPACE_WS)? <attribute>
                | COMMENT_DATA
              )
              ("-->")?
              | ~'-' (~('\n' | '>'))*
          )
      | { (!ignoreServerSide) }? '%' <asp_start> // Only scan server side instructions when allowed to.
      |
      (
          n:NAME <check_script>
          (
          {isScript}?
              MSPACE_WS
              ATTRIBUTES
              '>' <check_server>
          |
          )
      )
      |
  )
  <determine_type>
  ;


PCDATA
	:	(
			/* See comment in WS.  Language for combining any flavor
			 * newline is ambiguous.  Shutting off the warning.
			 */
			options {
				generateAmbigWarnings=false;
			}
		:
        WS | ~('<' | '\n' | '\r')
	)+
    <change_html>
	;


END_TAG
  :
  "</" NAME <change_html>
  ;


protected LINE
  :
  ('\r')? '\n' { newline(); }
  ;


protected UNQUOTED
  :
  ~(' ' | '\t' | '\n' | '\r' | '-' | '<' | '>' | '\'' | '"')
  ;


protected SPACE_TAB
  :
  ' ' | '\t'
  ;


protected MSPACE_WS
  :
  (SPACE_TAB | WS)+
  ;


protected ATTRIBUTES
  :
  k:NAME
  (MSPACE_WS)? '=' l:ATTRIBUTE_VALUE
  <put_attr>
  (MSPACE_WS (LETTER)=> ATTRIBUTES |)
  |
  ;


protected ATTRIBUTE_VALUE
  :
  (
      (MSPACE_WS)?
      (
          '\'' (~('\'' | '\n' | '\r'))* '\''
          | '"' (~('"' | '\n' | '\r'))* '"'
      )
  )
  | (UNQUOTED)+
  ;


protected LETTER
  :
  'a' .. 'z'
  ;


protected NAME
  :
  LETTER (LETTER | '0' .. '9' | '.' | '-' | '_')*
  ;


// multiple-line comments
protected COMMENT_DATA
	:	(
			options {
				generateAmbigWarnings=false;
			}
		:
        {!(LA(2)=='-' && LA(3)=='>')}? '-' // allow '-' if not "-->"
        | {LA(2) != '%'}? '<'
        | WS
		| ~( '-' | '\n' | '\r' | '<')
		)*
	;


protected WS
  :
  (
			options {
				generateAmbigWarnings=false;
			}
		:
    LINE
  )+
  ;


protected SPACE_WS
  :
  SPACE_TAB | WS
  ;
