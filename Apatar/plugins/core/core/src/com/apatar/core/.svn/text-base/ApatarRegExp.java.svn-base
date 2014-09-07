/*TODO recorded refactoring
 * класс ApatarRegExp теперь передаёт исключения на уровень выше.
 **********************
 */

/*
 _______________________

 Apatar Open Source Data Integration

 Copyright (C) 2005-2007, Apatar, Inc.

 info@apatar.com

 195 Meadow St., 2nd Floor

 Chicopee, MA 01013



 This program is free software; you can redistribute it and/or modify

 it under the terms of the GNU General Public License as published by

 the Free Software Foundation; either version 2 of the License, or

 (at your option) any later version.



 This program is distributed in the hope that it will be useful,

 but WITHOUT ANY WARRANTY; without even the implied warranty of

 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the

 GNU General Public License for more details.



 You should have received a copy of the GNU General Public License along

 with this program; if not, write to the Free Software Foundation, Inc.,

 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

 ________________________

 */

package com.apatar.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApatarRegExp {
	private static HashMap<String, Pattern> patterns = new HashMap<String, Pattern>();

	public ApatarRegExp() {
		super();
	}

	public static Pattern createPattern(String regex) throws ApatarException {
		Pattern patt = patterns.get(regex);
		try {
			if (patt == null) {
				patt = Pattern.compile(regex);
				patterns.put(regex, patt);
			}
		} catch (Exception e) {
			// System.err.println("RegExp: expression = `" + regex + "`");
			// System.err.println(e.getMessage());
			throw new ApatarException("RegExp exception: " + e.getMessage());
			// e.printStackTrace();
		}
		return patt;
	}

	public static boolean matchRegExp(String regex, String input)
			throws ApatarException {
		try {
			return createPattern(regex).matcher(input).matches();
		} catch (Exception e) {
			// System.err.println("RegExp: expression = `" + regex + "`");
			// System.err.println("RegExp: String = `" + input + "`");
			// System.err.println(e.getMessage());
			throw new ApatarException("RegExp exception: " + e.getMessage());
			// e.printStackTrace();
		}
	}

	public static Matcher getMatcher(String regex, String input)
			throws ApatarException {
		try {
			return createPattern(regex).matcher(input);
		} catch (Exception e) {
			// System.err.println("RegExp: expression = `" + regex + "`");
			// System.err.println("RegExp: String = `" + input + "`");
			// System.err.println(e.getMessage());
			throw new ApatarException("RegExp exception: " + e.getMessage());
		}
	}

	public static List<String> getSubstrings(String regex, String input)
			throws ApatarException {
		List<String> result = new ArrayList<String>();
		try {
			Matcher match = getMatcher(regex, input);
			match.matches();
			for (int i = 1; i <= match.groupCount(); i++) {
				result.add(match.group(i));
			}
		} catch (Exception e) {
			// System.err.println("RegExp: expression = `" + regex + "`");
			// System.err.println("RegExp: String = `" + input + "`");
			// System.err.println(e.getMessage());
			throw new ApatarException("RegExp exception: " + e.getMessage());
		}
		return result;
	}

	public static String getSubstrings(String regex, String input,
			int groupIndex) throws ApatarException {
		try {
			Matcher match = getMatcher(regex, input);
			match.matches();
			return match.group(groupIndex);
		} catch (Exception e) {
			// System.err.println("RegExp: expression = `" + regex + "`");
			// System.err.println("RegExp: String = `" + input + "`");
			// System.err.println(e.getMessage());
			throw new ApatarException("RegExp exception: " + e.getMessage());
		}
	}

	public static List<String> getAllSubstrings(String regex, String input,
			int groupIndex) throws ApatarException {
		List<String> result = new ArrayList<String>();
		try {
			Matcher match = getMatcher(regex, input);
			while (match.find()) {
				result.add(match.group(groupIndex));
			}
		} catch (Exception e) {
			// System.err.println("RegExp: expression = `" + regex + "`");
			// System.err.println("RegExp: String = `" + input + "`");
			// System.err.println(e.getMessage());
			throw new ApatarException("RegExp exception: " + e.getMessage());
		}
		return result;
	}
}
