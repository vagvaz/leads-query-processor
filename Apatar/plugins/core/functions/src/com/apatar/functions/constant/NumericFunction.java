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

package com.apatar.functions.constant;

import java.util.List;

import javax.swing.ImageIcon;

import org.jdom.Element;

import com.apatar.core.ERecordType;
import com.apatar.functions.ConstantFunctionInfo;
import com.apatar.ui.FunctionCategory;
import com.apatar.ui.UiUtils;

public class NumericFunction extends AbstractConstantApatarFunction {
	long value;

	public Object execute(List l) {
		return value;
	}

	static ConstantFunctionInfo fi = new ConstantFunctionInfo("Numeric Constant", 0, 1, ERecordType.Numeric);
	static
	{
		//fi.getCategories().add(FunctionCategory.Group_By);
		fi.getCategories().add(FunctionCategory.Filter);
	}

	public ConstantFunctionInfo getFunctionInfo() {
		return fi;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public boolean isEditable()
	{
		return true;
	}

	public ImageIcon getIcon() {
		return UiUtils.NUMERIC_COLUMN_NODE_ICON;
	}

	public void initFromElement(Element e) {
		super.initFromElement(e);
		value = Long.parseLong(e.getAttributeValue("value"));
	}

	public Element saveToElement() {
		Element rv = super.saveToElement();
		rv.setAttribute("value", ""+value);
		return rv;
	}
}