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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import javax.swing.ImageIcon;

import org.jdom.Element;

import com.apatar.core.ERecordType;
import com.apatar.functions.ConstantFunctionInfo;
import com.apatar.ui.FunctionCategory;
import com.apatar.ui.UiUtils;

public class FileFunction extends AbstractConstantApatarFunction {

	File value = null; 
	
	static ConstantFunctionInfo fi = new ConstantFunctionInfo("File Constant", 0, 1, ERecordType.Binary);
	static {
		fi.getCategories().add(FunctionCategory.Constant);
		fi.getCategories().add(FunctionCategory.ALL);
	}
	
	public Object execute(List l) {
		try {
			if( null != value )
				return new FileInputStream(value);
			else
				return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ConstantFunctionInfo getFunctionInfo() {
		return fi;
	}

	public File getValue() {
		return value;
	}
	
	public void setValue(File value) {
		this.value = value;
	}
	
	public boolean isEditable() {
		return true;
	}

	public ImageIcon getIcon() {
		return UiUtils.BINARY_COLUMN_NODE_ICON;
	}

	public void initFromElement(Element e) {
		super.initFromElement(e);
		String path = e.getAttributeValue("value");
		if( !"".equals( path ) )
			value = new File( path );
		else
			value = null;
	}

	public Element saveToElement() {
		Element e = super.saveToElement();
		e.setAttribute("value", (null == value ? "" : value.getPath()) );
		return e;
	}
	
}

