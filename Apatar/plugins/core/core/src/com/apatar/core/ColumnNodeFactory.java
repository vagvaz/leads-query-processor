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

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import com.apatar.ui.NodeFactory;
import com.apatar.ui.UiUtils;

public class ColumnNodeFactory extends NodeFactory {
	protected String connectionName = "";
	protected String category = "";

	protected Record record = null;
	protected boolean inbound = false;

	protected int sqlType;

	public ColumnNodeFactory() {
		super();
	}

	public Record getRecord() {
		return record;
	}

	public ColumnNodeFactory(Record rec, String connectionName,
			String category, boolean inbound) {
		super();
		record = rec;
		this.inbound = inbound;
		this.connectionName = connectionName;
		this.category = category;
		// this.sqlType = sqlType;
	}

	@Override
	public List<String> getCategory() {
		List<String> res = new ArrayList<String>();
		res.add((category == null || category.length() == 0) ? connectionName
				: category);
		return res;
	}

	@Override
	public String getTitle() {
		return getRecord().getFieldName();
	}

	@Override
	public ImageIcon getIcon() {
		String type = getRecord().getType().name();
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
		} else if (type.equals(ERecordType.Timestamp.toString())) {
			icon = UiUtils.DATETIME_COLUMN_NODE_ICON;
		} else if (type.equals(ERecordType.Date.toString())) {
			icon = UiUtils.DATE_COLUMN_NODE_ICON;
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
	public AbstractNode createNode() {
		return new ColumnNode(record, connectionName, category, inbound);
	}

	@Override
	public int getHorizontalTextPosition() {
		return SwingConstants.RIGHT;
	}

	@Override
	public int getVerticalTextPosition() {
		return SwingConstants.CENTER;
	}

	@Override
	public String getNodeClass() {
		return ColumnNode.class.getName();
	}

	@Override
	public Color getTextColor() {
		return Color.BLACK;
	}

	@Override
	public int getFontStile() {
		if (inbound) {
			return getRecord().isNullable() ? 0 : Font.BOLD;
		}
		return 0;
	}

	@Override
	public boolean MainPaneNode() {
		return false;
	}

}
