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
package gr.omadak.leviathan.asp;

public class TokenMap {
    private TokenMap() {}
    static boolean IS_JS;

    public static boolean isSame(int code, int form, boolean formIsVb) {
        if (formIsVb && IS_JS) {
            form = getJSCode(form);
        } else if (!formIsVb && !IS_JS) {
            form = getVbCode(form);
        }
        return code == form;
    }


    public static int getJSCode(int vbCode) {
        int result;
        switch (vbCode) {
            case TreeVbsTokenTypes.DSTRING:
            result = TreeJsTokenTypes.STRING_LITERAL; break;
            case TreeVbsTokenTypes.EXPR:
            result = TreeJsTokenTypes.EXPR; break;
            case TreeVbsTokenTypes.METHOD_CALL:
            result = TreeJsTokenTypes.METHOD_CALL; break;
            case TreeVbsTokenTypes.ARGLIST_VALUES:
            result = TreeJsTokenTypes.ELIST; break;
            default: result = -1; break;
        }
        return result;
    }


    public static int getVbCode(int jsCode) {
        int result;
        switch (jsCode) {
            case TreeJsTokenTypes.STRING_LITERAL:
            result = TreeVbsTokenTypes.DSTRING; break;
            case TreeJsTokenTypes.EXPR:
            result = TreeVbsTokenTypes.EXPR; break;
            case TreeJsTokenTypes.METHOD_CALL:
            result = TreeVbsTokenTypes.METHOD_CALL; break;
            case TreeJsTokenTypes.ELIST:
            result = TreeVbsTokenTypes.ARGLIST_VALUES; break;
            default: result = -1; break;
        }
        return result;
    }


    public static int getCode(int vbCode) {
        return IS_JS ? getJSCode(vbCode) : vbCode;
    }
}
