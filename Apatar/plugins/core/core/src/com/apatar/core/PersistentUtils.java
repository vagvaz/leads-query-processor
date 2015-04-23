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

package com.apatar.core;

import org.jdom.Element;

public class PersistentUtils {

	// !!! only this should be passed to this function..
	// this method should be used only from SaveToElement function
	public static Element CreateElement(IPersistent object) {
		Element rv = new Element(object.getClass().getName());
		return rv;
	}

	public static IPersistent CreateObject(Element element) {
		if ("com.apatar.msexel.MsExcelRecord".equalsIgnoreCase(element
				.getName())) {
			element.setName("com.apatar.msexcel.MsExcelRecord");
		}
		if ("com.apatar.msexel.MsExelConnection".equals(element.getName())) {
			element.setName("com.apatar.msexcel.MsExcelConnection");
		}
		Object obj = ApplicationData.CreateObject(element.getName());
		if (obj instanceof IPersistent) {
			IPersistent p = (IPersistent) obj;
			if (element != null) {
				p.initFromElement(element);
			}
			return (IPersistent) obj;
		}
		return null;
	}

	public static void InitObjectFromChild(IPersistent object, Element root,
			boolean isValidate) {
		if (object == null) {
			return;
		}
		if ("com.apatar.msexel.MsExcelColumnNode".equalsIgnoreCase(root
				.getAttributeValue("nodeClass"))) {
			root.setAttribute("nodeClass",
					"com.apatar.msexcel.MsExcelColumnNode");
		}
		Element element = null;
		if ("com.apatar.msexcel.MsExcelRecord".equalsIgnoreCase(object
				.getClass().getName())) {
			element = root.getChild("com.apatar.msexel.MsExcelRecord");
			if (element == null) {
				element = root.getChild(object.getClass().getName());
			}
		} else {
			element = root.getChild(object.getClass().getName());
		}
		if (element == null) {
			if (isValidate) {
				ApplicationData.COUNT_INIT_ERROR++;
			}
			return;
		}
		object.initFromElement(element);
	}

	public static void InitObjectFromChild(IPersistent object, Element root) {
		InitObjectFromChild(object, root, true);
	}

}
