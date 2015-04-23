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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Element;

import com.apatar.core.AbstractApatarFunction;
import com.apatar.functions.FunctionInfo;
import com.apatar.functions.ReplaceIfObject;
import com.apatar.ui.FunctionCategory;

public class ReplaceIfTransformFunction extends AbstractApatarFunction {

	private List<ReplaceIfObject> objects;
	private boolean caseSencitive = true;
	private boolean replacePartValue = false;
	private boolean treatNullAsEmptyString = false;

	@Override
	public String getTitle() {
		return "Replace With";
	}

	public Object execute(List list) {
		Object objInput = list.get(0);
		String input = "";
		if (objInput == null) {
			if (!isTreatNullAsEmptyString()) {
				return null;
			}
		} else {
			input = objInput.toString();
		}
		if (getObjects() == null) {
			return input;
		}

		for (ReplaceIfObject obj : getObjects()) {
			if (!isReplacePartValue()) {
				if (obj.getInput().equalsIgnoreCase("")
						&& input.equalsIgnoreCase("")) {
					return obj.getReplase();
				} else if (isCaseSencitive()) {
					if (obj.getInput().equals(input)) {
						return obj.getReplase();
					}
				} else {
					if (obj.getInput().equalsIgnoreCase(input)) {
						return obj.getReplase();
					}
				}
			} else {
				if (!isCaseSencitive()) {
					if ("".equalsIgnoreCase(obj.getInput())) {
						return input;
					}
					str = "";
					replacePartValueWhenCaseInSencitive(input, obj.getInput(),
							obj.getReplase());
					input = str;
					continue;
				} else {
					input = input.replaceAll(obj.getInput(), obj.getReplase());
					continue;
				}
			}
		}
		return input;
	}

	private String str;

	private void replacePartValueWhenCaseInSencitive(String value, String inp,
			String repl) {
		if ("".equalsIgnoreCase(inp)) {
			return;
		}
		Pattern patt = Pattern.compile("(.*?)(" + inp + ")(.*)",
				Pattern.CASE_INSENSITIVE);
		Matcher match = patt.matcher(value);
		boolean found_matches = match.matches();
		if (found_matches) {
			String group1 = match.group(1);
			String group3 = match.group(3);
			str += group1 + repl;
			replacePartValueWhenCaseInSencitive(group3, inp, repl);
		} else {
			str += value;
		}
	}

	public void setObjects(List<ReplaceIfObject> objects) {
		this.objects = objects;
	}

	public List<ReplaceIfObject> getObjects() {
		return objects;
	}

	public boolean isCaseSencitive() {
		return caseSencitive;
	}

	public void setCaseSencitive(boolean caseSencitive) {
		this.caseSencitive = caseSencitive;
	}

	public boolean isReplacePartValue() {
		return replacePartValue;
	}

	public void setReplacePartValue(boolean replacePartValue) {
		this.replacePartValue = replacePartValue;
	}

	@Override
	public void initFromElement(Element e) {
		super.initFromElement(e);
		List elems = e.getChildren("rif");
		if (elems == null) {
			return;
		}
		setObjects(new ArrayList<ReplaceIfObject>());
		for (Object obj : elems) {
			Element elRif = (Element) obj;
			ReplaceIfObject rif = new ReplaceIfObject(elRif
					.getAttributeValue("input"), elRif
					.getAttributeValue("replaseWith"));
			getObjects().add(rif);
		}
		Element settings = e.getChild("rifSettings");
		try {
			String setting = settings.getAttributeValue("caseSencitive");
			setCaseSencitive(setting.equals("true") ? true : false);
			setting = settings.getAttributeValue("replacePartValue");
			setReplacePartValue(setting.equals("true") ? true : false);
			setting = settings.getAttributeValue("treatNullAsEmptyString");
			setTreatNullAsEmptyString(setting.equals("true") ? true : false);
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}

	@Override
	public Element saveToElement() {
		Element rv = super.saveToElement();
		if (getObjects() == null) {
			return rv;
		}
		for (ReplaceIfObject obj : getObjects()) {
			Element elem = new Element("rif");
			elem.setAttribute("input", obj.getInput());
			elem.setAttribute("replaseWith", obj.getReplase());
			rv.addContent(elem);
		}
		Element elem = new Element("rifSettings");
		elem.setAttribute("caseSencitive", (isCaseSencitive() ? "true"
				: "false"));
		elem.setAttribute("replacePartValue", (isReplacePartValue() ? "true"
				: "false"));
		elem.setAttribute("treatNullAsEmptyString",
				(isTreatNullAsEmptyString() ? "true" : "false"));
		rv.addContent(elem);
		return rv;
	}

	@Override
	public boolean isEditable() {
		return true;
	}

	static FunctionInfo fi = new FunctionInfo("Replace With", 1, 1);
	static {
		fi.getCategories().add(FunctionCategory.String);
		fi.getCategories().add(FunctionCategory.ALL);
	}

	public FunctionInfo getFunctionInfo() {
		return fi;
	}

	/**
	 * @return the treatNullAsEmptyString
	 */
	public boolean isTreatNullAsEmptyString() {
		return treatNullAsEmptyString;
	}

	/**
	 * @param treatNullAsEmptyString
	 *            the treatNullAsEmptyString to set
	 */
	public void setTreatNullAsEmptyString(boolean treatNullAsEmptyString) {
		this.treatNullAsEmptyString = treatNullAsEmptyString;
	}
}
