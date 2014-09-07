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
import com.apatar.functions.FunctionInfo;
import com.apatar.ui.FunctionCategory;

public class MiddleTransformFunction extends AbstractApatarFunction{
	
	private int beginIndex = 1;
	private int endIndex = Integer.MAX_VALUE;

	public Object execute(List list) {
		int len;
		
		if( list.get(0) instanceof String ){
			len = list.get(0).toString().length(); 
			if(len >= beginIndex) {
				if (len < endIndex)
					endIndex = len;
				return list.get(0).toString().substring(beginIndex-1, endIndex);
			}
			else
				return "";
		}
		else
			return list.get(0);
		
	}

	public int getBeginIndex() {
		return beginIndex;
	}

	public void setBeginIndex(int beginIndex) {
		this.beginIndex = beginIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

	public void initFromElement(Element e) {
		super.initFromElement(e);
		beginIndex = Integer.parseInt(e.getAttributeValue("Value1"));
		endIndex = Integer.parseInt(e.getAttributeValue("Value2"));
	}

	public Element saveToElement() {
		Element rv = super.saveToElement();
		rv.setAttribute("Value1", ""+beginIndex);
		rv.setAttribute("Value2", ""+endIndex);
		return rv;
	}
	
	public boolean isEditable()
	{
		return true;
	}
	
	static FunctionInfo fi = new FunctionInfo("Middle", 1, 1);
	static 
	{
		fi.getCategories().add(FunctionCategory.String);
		fi.getCategories().add(FunctionCategory.ALL);
	}
	
	public FunctionInfo getFunctionInfo() {
		return fi;
	}
}
