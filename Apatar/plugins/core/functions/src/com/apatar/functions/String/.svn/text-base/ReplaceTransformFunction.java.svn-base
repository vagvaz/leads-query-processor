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

import org.jdom.Element;

import com.apatar.core.AbstractApatarFunction;
import com.apatar.core.Entities;
import com.apatar.functions.FunctionInfo;
import com.apatar.ui.FunctionCategory;

public class ReplaceTransformFunction extends AbstractApatarFunction {

	private String inputStr1 = "0";
	private String inputStr2 = "1";
	private Boolean treatNullAsEmptyString = false;

	@Override
	public String getTitle() {
		return "Replace";
	}

	public Object execute(List list) {
		String str = new String();
		String findRegExp, replaceStr;

		try {
			findRegExp = String.valueOf(inputStr1);
			replaceStr = String.valueOf(inputStr2);
		} catch (Exception e) {
			return null;
		}

		try {
			str = list.get(0).toString();
		} catch (NullPointerException e) {
			if (getTreatNullAsEmptyString()) {
				str = "";
			} else {
				return list.get(0);
			}
		}

		// if (list.get(0) instanceof String) {

		// String specialOperators[] = {"\\&", "\\|", "\\!", "\\{", "\\}",
		// "\\[", "\\]", "\\(", "\\)", "\\^", "~", "\\*", ":", "\"", "\\'",
		// "\\+", "-"};
		// for (int i= 0; i < specialOperators.length; i++) {
		// System.out.println("specialOperator=" + specialOperators[i] +
		// "index=" + i);
		// str = str.replaceAll(specialOperators[i], "\\" +
		// specialOperators[i]);
		// System.out.println(str);
		// }

		// str = str.replaceAll(findRegExp, replaceStr);
		if ("<apatar_newline>".equals(findRegExp)) {
			return str.replaceAll(findRegExp, "\r\n");
		} else if ("<apatar_uls>".equals(findRegExp)) {
			String lastChar = new String(str.substring(str.length() - 2));
			byte[] bb = lastChar.getBytes();
			if (bb[bb.length - 1] > 0 && bb[bb.length - 1] < 0x20) {
				if ((bb[bb.length - 2] == 10 && bb[bb.length - 1] == 13)
						|| (bb[bb.length - 2] == 13 && bb[bb.length - 1] == 10)) {
					return str;
				}
				return new String(str.substring(1, str.length() - 2));
				// System.err.println(bb[bb.length - 1]);
			}
			return str;
		} else {
			return str.replaceAll(findRegExp, replaceStr);
		}
		// } else {
		// return list.get(0);
		// }

	}

	public String getInputStr1() {
		return inputStr1;
	}

	public void setInputStr1(String inputStr1) {
		this.inputStr1 = inputStr1;
	}

	public String getInputStr2() {
		return inputStr2;
	}

	public void setInputStr2(String inputStr2) {
		this.inputStr2 = inputStr2;
	}

	@Override
	public void initFromElement(Element e) {
		super.initFromElement(e);
		inputStr1 = Entities.XML.unescape(e.getAttributeValue("Value1"));
		inputStr2 = Entities.XML.unescape(e.getAttributeValue("Value2"));
		setTreatNullAsEmptyString(Boolean.valueOf(Entities.XML.unescape(e
				.getAttributeValue("treatNullAsEmptyString"))));
	}

	@Override
	public Element saveToElement() {
		Element rv = super.saveToElement();
		rv.setAttribute("Value1", Entities.XML.escape(inputStr1));
		rv.setAttribute("Value2", Entities.XML.escape(inputStr2));
		rv.setAttribute("treatNullAsEmptyString", getTreatNullAsEmptyString()
				.toString());
		return rv;
	}

	@Override
	public boolean isEditable() {
		return true;
	}

	static FunctionInfo fi = new FunctionInfo("Replace", 1, 1);
	static {
		fi.getCategories().add(FunctionCategory.String);
		fi.getCategories().add(FunctionCategory.ALL);
	}

	public FunctionInfo getFunctionInfo() {
		return fi;
	}

	/**
	 * @param treatNullAsEmptyString
	 *            the treatNullAsEmptyString to set
	 */
	public void setTreatNullAsEmptyString(Boolean treatNullAsEmptyString) {
		this.treatNullAsEmptyString = treatNullAsEmptyString;
	}

	/**
	 * @return the treatNullAsEmptyString
	 */
	public Boolean getTreatNullAsEmptyString() {
		return treatNullAsEmptyString;
	}
}
