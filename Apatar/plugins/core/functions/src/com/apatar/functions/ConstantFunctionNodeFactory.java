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

package com.apatar.functions;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.java.plugin.standard.StandardPluginClassLoader;

import com.apatar.core.AbstractNode;
import com.apatar.core.ERecordType;
import com.apatar.functions.constant.AbstractConstantApatarFunction;
import com.apatar.ui.FunctionCategory;
import com.apatar.ui.NodeFactory;
import com.apatar.ui.UiUtils;

public class ConstantFunctionNodeFactory extends NodeFactory {

	String					classFunction;

	ConstantFunctionInfo	fi	= new ConstantFunctionInfo();

	public ConstantFunctionNodeFactory() {
		super();
	}

	public ConstantFunctionNodeFactory(ClassLoader classLoader,
			String classFunction) {
		super();

		this.classFunction = classFunction;

		try {
			if (this.classFunction != null && this.classFunction.length() > 0) {

				StandardPluginClassLoader validfunctionClass = (StandardPluginClassLoader) classLoader;
				AbstractConstantApatarFunction validateFunc = (AbstractConstantApatarFunction) validfunctionClass
						.loadClass(this.classFunction).newInstance();
				fi = validateFunc.getFunctionInfo();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public AbstractNode createNode() {
		return new ConstantFunctionNode(classFunction, fi);
	}

	@Override
	public ImageIcon getIcon() {
		String type = (fi).getType().name();
		ImageIcon icon = null;

		if (type.equals(ERecordType.Numeric.toString())) {
			icon = UiUtils.NUMERIC_COLUMN_NODE_ICON;
		} else if (type.equals(ERecordType.Decimal.toString())) {
			icon = UiUtils.DECIMAL_COLUMN_NODE_ICON;
		} else if (type.equals(ERecordType.Binary.toString())) {
			icon = UiUtils.BINARY_COLUMN_NODE_ICON;
		} else if (type.equals(ERecordType.Boolean.toString())) {
			icon = UiUtils.BOOLEAN_COLUMN_NODE_ICON;
		} else if (type.equals(ERecordType.Text.toString())) {
			icon = UiUtils.TEXT_COLUMN_NODE_ICON;
		} else if (type.equals(ERecordType.Date.toString())) {
			icon = UiUtils.DATE_COLUMN_NODE_ICON;
		} else if (type.equals(ERecordType.Timestamp.toString())) {
			icon = UiUtils.DATETIME_COLUMN_NODE_ICON;
		} else if (type.equals(ERecordType.Time.toString())) {
			icon = UiUtils.TIME_COLUMN_NODE_ICON;
		} else if (type.equals(ERecordType.TextUnicode.toString())) {
			icon = UiUtils.TEXTUNICODE_COLUMN_NODE_ICON;
		} else if (type.equals(ERecordType.Currency.toString())) {
			icon = UiUtils.CURRENCY_COLUMN_NODE_ICON;
		} else if (type.equals(ERecordType.XML.toString())) {
			icon = UiUtils.XML_COLUMN_NODE_ICON;
		} else if (type.equals(ERecordType.Media.toString())) {
			icon = UiUtils.MEDIA_COLUMN_NODE_ICON;
		} else if (type.equals(ERecordType.Spacial.toString())) {
			icon = UiUtils.SPACIAL_COLUMN_NODE_ICON;
		} else if (type.equals(ERecordType.Object.toString())) {
			icon = UiUtils.OBJECT_COLUMN_NODE_ICON;
		} else if (type.equals(ERecordType.Enum.toString())) {
			icon = UiUtils.ENUM_COLUMN_NODE_ICON;
		} else {
			icon = UiUtils.SMALL_COLUMN_ICON;
		}

		return icon;
	}

	@Override
	public List<String> getCategory() {
		List<String> res = new ArrayList<String>();
		if (fi.getCategories().size() != 0) {
			for (FunctionCategory fc : fi.getCategories()) {
				res.add(fc.toString());
			}
		} else {
			res.add("Operations");
		}
		return res;
	}

	@Override
	public String getTitle() {
		return fi.getDisplayName();
	}

	public void loadClassFunction(ClassLoader classLoader, String classFunction) {
		this.classFunction = classFunction;

		try {
			if (this.classFunction != null && this.classFunction.length() > 0) {

				StandardPluginClassLoader validfunctionClass = (StandardPluginClassLoader) classLoader;
				AbstractConstantApatarFunction validateFunc = (AbstractConstantApatarFunction) validfunctionClass
						.loadClass(this.classFunction).newInstance();
				fi = validateFunc.getFunctionInfo();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getNodeClass() {
		return ConstantFunctionNode.class.getName();
	}

	@Override
	public int getHorizontalTextPosition() {
		return JLabel.RIGHT;
	}

	@Override
	public int getVerticalTextPosition() {
		return JLabel.CENTER;
	}

	@Override
	public Color getTextColor() {
		return Color.BLACK;
	}

	@Override
	public boolean MainPaneNode() {
		return false;
	}

}
