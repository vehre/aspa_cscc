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

/**
    Declares Token types used by bouth parsers
*/
public class CommonConstants {
    private CommonConstants() {}

	/** The common prefix of all anonymous functions in JavaScript */
	public static final String KEY_START = "$$$$";

	private static final int MIN_TOKEN = 100000; //10^5
    public static final int OBJECT = MIN_TOKEN + 1;
    public static final int TEMPLATE = MIN_TOKEN + 3;
    public static final int INSTANCE = MIN_TOKEN + 4;
    public static final int ALL_ARGS = MIN_TOKEN + 5;
    public static final int INVALID_OBJECT = MIN_TOKEN + 6;
    public static final int NON_APPLICABLE_HEADER = MIN_TOKEN + 7;
    public static final int TRANSLATE_ROOT = MIN_TOKEN + 8;
    public static final int ANY = MIN_TOKEN + 9;
    public static final int UNSUPPORTED = MIN_TOKEN + 10;
    public static final int UKNOWN_TYPE = MIN_TOKEN + 11;
}
