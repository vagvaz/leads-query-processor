/*
_______________________
Apatar Open Source Data Integration
Copyright (C) 2005-2007, Apatar, Inc.
info@apatar.com
195 Meadow St., 2nd Floor
Chicopee, MA 01013

��� This program is free software; you can redistribute it and/or modify
��� it under the terms of the GNU General Public License as published by
��� the Free Software Foundation; either version 2 of the License, or
��� (at your option) any later version.

��� This program is distributed in the hope that it will be useful,
��� but WITHOUT ANY WARRANTY; without even the implied warranty of
��� MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.� See the
��� GNU General Public License for more details.

��� You should have received a copy of the GNU General Public License along
��� with this program; if not, write to the Free Software Foundation, Inc.,
��� 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
________________________

 */

package com.apatar.functions.String;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.apatar.core.AbstractApatarFunction;
import com.apatar.functions.FunctionInfo;
import com.apatar.ui.FunctionCategory;

public class ClearHtmlTransformFunction extends AbstractApatarFunction {

	public Object execute(List list) {
		// String str = list.get(0).toString();

		String expression = "(.*?)(<html.*?>.*?</html>)(.*)";

		if (list.size() == 1) {
			if ((list.get(0) instanceof String)
					&& (expression instanceof String)) {

				if (list.get(0).toString().length() == 0) {
					return list.get(0);
				}

				String res = "";
				Pattern patt = Pattern.compile("<head.*?>.*?</head>",
						Pattern.MULTILINE + Pattern.CASE_INSENSITIVE
								+ Pattern.DOTALL);
				Matcher match = patt.matcher(list.get(0).toString());
				res = match.replaceAll("");
				// res =
				// list.get(0).toString().replaceAll("<head.*?>.*?</head>", "");
				patt = Pattern.compile("<style.*?>(.*?)</style>",
						Pattern.MULTILINE + Pattern.CASE_INSENSITIVE
								+ Pattern.DOTALL);
				match = patt.matcher(res);
				res = match.replaceAll("");

				patt = Pattern.compile("<[^\\s].*?>", Pattern.MULTILINE
						+ Pattern.CASE_INSENSITIVE + Pattern.DOTALL);
				match = patt.matcher(res);
				res = match.replaceAll("");

				patt = Pattern.compile("&lt;[^\\s].*?&gt;", Pattern.MULTILINE
						+ Pattern.CASE_INSENSITIVE + Pattern.DOTALL);
				match = patt.matcher(res);
				res = match.replaceAll("");

				patt = Pattern.compile("&[a-z]{1,6};", Pattern.MULTILINE
						+ Pattern.CASE_INSENSITIVE + Pattern.DOTALL);
				match = patt.matcher(res);
				res = match.replaceAll("");

				res = res.replaceAll("\\x00", " ");

				return res;

				/*
				 * boolean found_matches = false; String res = ""; String before
				 * = ""; String after = ""; System.out.println("ClearHtmlRegExp:
				 * expression = `"+expression+"`");
				 * System.out.println("ClearHtmlRegExp: String =
				 * `"+list.get(0)+"`"); try { Pattern patt =
				 * Pattern.compile(expression,
				 * Pattern.MULTILINE+Pattern.CASE_INSENSITIVE+Pattern.DOTALL);
				 * Matcher match = patt.matcher(list.get(0).toString());
				 * found_matches = match.matches(); before = match.group(1); res
				 * = match.group(2); after = match.group(3); } catch (Exception
				 * e) { System.err.println("ClearHtmlRegExp: expression =
				 * `"+expression+"`"); System.err.println("ClearHtmlRegExp:
				 * String = `"+list.get(0)+"`");
				 * System.err.println(e.getMessage()); // e.printStackTrace();
				 * return before + res + after; } if (found_matches){ Pattern
				 * patt = Pattern.compile(".*?<body.*?>(.*?)</body>.*",
				 * Pattern.MULTILINE+Pattern.CASE_INSENSITIVE+Pattern.DOTALL);
				 * Matcher match = patt.matcher(res); found_matches =
				 * match.matches(); if (found_matches) { res = match.group(1); }
				 * res = res.replaceAll("<.*?>", ""); res =
				 * res.replaceAll(".*?(<br>).*", "\n\r"); res =
				 * res.replaceAll(".*?(<div.*?>).*", ""); res =
				 * res.replaceAll(".*?(</div>).*", "\n\r"); res =
				 * res.replaceAll(".*?(<p.*?>).*", ""); res =
				 * res.replaceAll(".*?(</p>).*", "\n\r"); res =
				 * res.replaceAll("<.*?>(.*)</.*>", "$1"); // Pattern patt =
				 * Pattern.compile(".*?(<br>).*",
				 * Pattern.MULTILINE+Pattern.CASE_INSENSITIVE+Pattern.DOTALL);
				 * return res; } else { return list.get(0); }
				 */
			} else {
				return list.get(0);
			}
		} else {
			return list.get(0);
		}

	}

	static FunctionInfo fi = new FunctionInfo("Clear Html", 1, 1);
	static {
		fi.getCategories().add(FunctionCategory.String);
		fi.getCategories().add(FunctionCategory.ALL);
	}

	public FunctionInfo getFunctionInfo() {
		return fi;
	}

}
