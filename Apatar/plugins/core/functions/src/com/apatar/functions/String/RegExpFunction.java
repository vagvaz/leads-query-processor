/*
_______________________
Apatar Open Source Data Integration
Copyright (C) 2005-2007, Apatar, Inc.
info@apatar.com
195 Meadow St., 2nd Floor
Chicopee, MA 01013

### This program is free software; you can redistribute it and/or modify
### it under the terms of the GNU General Public License as published by
### the Free Software Foundation; either version 2 of the License, or
### (at your option) any later version.

### This program is distributed in the hope that it will be useful,
### but WITHOUT ANY WARRANTY; without even the implied warranty of
### MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.# See the
### GNU General Public License for more details.

### You should have received a copy of the GNU General Public License along
### with this program; if not, write to the Free Software Foundation, Inc.,
### 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
________________________

 */

package com.apatar.functions.String;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Element;

import com.apatar.core.AbstractApatarFunction;
import com.apatar.functions.FunctionInfo;
import com.apatar.ui.FunctionCategory;

public class RegExpFunction extends AbstractApatarFunction {

	static FunctionInfo	fi						= new FunctionInfo("RegExp", 1,
														1);
	private String		value					= "";
	private boolean		retrunEmptyIfNotFound	= false;

	public Object execute(List list) {

		String expression = getValue();

		if (list.size() == 1) {
			if ((list.get(0) instanceof String)
					&& (expression instanceof String)) {

				if (list.get(0).toString().length() == 0) {
					return list.get(0);
				}
				boolean found_matches = false;
				String res = "";
				System.out.println("RegExp: expression = `" + expression + "`");
				System.out.println("RegExp: String = `" + list.get(0) + "`");
				try {
					Pattern patt = Pattern.compile(expression,
							Pattern.MULTILINE);
					Matcher match = patt.matcher(list.get(0).toString());
					found_matches = match.matches();
					res = match.group(1);
				} catch (Exception e) {
					System.err.println("RegExp: expression = `" + expression
							+ "`");
					System.err
							.println("RegExp: String = `" + list.get(0) + "`");
					System.err.println(e.getMessage());
					// e.printStackTrace();
					if (isRetrunEmptyIfNotFound()) {
						return "";
					} else {
						return list.get(0);
					}
				}
				if (found_matches) {
					return res;
				} else {
					return list.get(0);
				}
			} else {
				return list.get(0);
			}
		} else {
			return list.get(0);
		}
	}

	static {
		fi.getCategories().add(FunctionCategory.String);
		fi.getCategories().add(FunctionCategory.ALL);
	}

	public FunctionInfo getFunctionInfo() {
		return fi;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *        the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the retrunEmptyIfNotFound
	 */
	public boolean isRetrunEmptyIfNotFound() {
		return retrunEmptyIfNotFound;
	}

	/**
	 * @param retrunEmptyIfNotFound
	 *        the retrunEmptyIfNotFound to set
	 */
	public void setRetrunEmptyIfNotFound(boolean retrunEmptyIfNotFound) {
		this.retrunEmptyIfNotFound = retrunEmptyIfNotFound;
	}

	@Override
	public void initFromElement(Element e) {
		super.initFromElement(e);
		setValue(e.getAttributeValue("Value"));
		try {
			setRetrunEmptyIfNotFound(Boolean.valueOf(e
					.getAttributeValue("retrunEmptyIfNotFound")));
		} catch (RuntimeException e1) {
			setRetrunEmptyIfNotFound(false);
		}
	}

	@Override
	public Element saveToElement() {
		Element rv = super.saveToElement();
		rv.setAttribute("Value", getValue());
		rv.setAttribute("retrunEmptyIfNotFound", String
				.valueOf(isRetrunEmptyIfNotFound()));
		return rv;
	}

	@Override
	public boolean isEditable() {
		return true;
	}

}
