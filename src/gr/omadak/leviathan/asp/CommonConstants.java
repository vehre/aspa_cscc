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
public final class CommonConstants {
    private CommonConstants() {}

	/** The common prefix of all anonymous functions in JavaScript */
	public static final String KEY_START = "$$$$";

	private static final int MIN_TOKEN = 100000; //10^5
    /** Declares an AST which contains an Object reference */ 
    public static final int OBJECT = MIN_TOKEN + 1;
    /** AST of this type are used as place holders for other AST */
    public static final int TEMPLATE = MIN_TOKEN + 3;
    /** Template node used to indicate the place for an instance AST */ 
    public static final int INSTANCE = MIN_TOKEN + 4;
    /** Template AST which is substituted with all the arguments a method
    received*/
    public static final int ALL_ARGS = MIN_TOKEN + 5;
    /** The type for an uknown object */
    public static final int INVALID_OBJECT = MIN_TOKEN + 6;
    /** The type for headers which can not be translated */
    public static final int NON_APPLICABLE_HEADER = MIN_TOKEN + 7;
    /** Indicates the begining of the Root node for a Template AST */
    public static final int TRANSLATE_ROOT = MIN_TOKEN + 8;
    /** Indicates any type for an argument.
    Means that the type is indifferent */
    public static final int ANY = MIN_TOKEN + 9;
    /** Inidcates an unsupported method or object */
    public static final int UNSUPPORTED = MIN_TOKEN + 10;
    /** The type could not be resolved */
    public static final int UKNOWN_TYPE = MIN_TOKEN + 11;
    /** Nodes which has this type, are the product of a Member translation
    which returns an Object*/ 
    public static final int OBJECT_RET = MIN_TOKEN + 12;
	/** Command to store a property. The node will be removed from
		the produced tree
	*/
	public static final int STORE = MIN_TOKEN + 13;
	/** Placeholder for a stored property.
		The node will be replaced with the value of the stored
		property
	*/
	public static final int STORED = MIN_TOKEN + 14;
}
