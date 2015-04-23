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

import java.awt.Font;
import java.net.SocketException;
import java.util.List;

import javax.swing.ImageIcon;

import org.jdom.Element;

import com.apatar.ui.UiUtils;

public class ColumnNode extends NonOperationalNode {

	protected boolean inbound;
	protected String connectionName;

	// this is category (supposed to be used to identify one column node from
	// another)
	// usually contains table name
	protected String category;

	// contains column record
	protected Record record = new Record();

	public static final String CONN_POINT = "point";

	int sqlType;

	public ColumnNode(Record rec, String connectionName, String category,
			boolean inbound) {
		super();
		this.inbound = inbound;
		if (inbound) {
			inputConnectionList.add(new ConnectionPoint(CONN_POINT, inbound,
					this, false, 1));
		} else {
			outputConnectionList.put(new ConnectionPoint(CONN_POINT, inbound,
					this, true, 1), null);
		}
		this.connectionName = connectionName;
		this.category = category;
		// if record is passed as null (is not from the wizard, but from save
		// load)
		if (rec != null) {
			record = rec;
		}
		if (getRecord() != null) {
			updateTitle();
		}
	}

	public Record getRecord() {
		return record;
	}

	// update the title of the column node
	// column node title consits of category.column_name
	public void updateTitle() {
		title = (category != null && category.length() > 0) ? category + '.'
				+ getRecord().getFieldName() : getRecord().getFieldName();
	}

	// update title of the node
	public void updateCategory(OperationalNode node) {
		ConnectionPoint cp = node.getConnPoint(connectionName);
		if (cp != null && cp.connectors.size() > 0) {
			category = cp.connectors.get(0).getBegin().getNode().getTitle();
		} else {
			category = "";
		}
		updateTitle();
	}

	@Override
	public Element saveToElement() {
		Element columnNode = super.saveToElement();
		columnNode.setAttribute("inbound", String.valueOf(inbound));
		columnNode.setAttribute("connectionName", connectionName);
		columnNode.setAttribute("category", (null == category ? "" : category));
		columnNode.addContent(record.saveToElement());
		return columnNode;
	}

	@Override
	public void initFromElement(Element e) {
		super.initFromElementWithoutConnectionPoint(e);
		inbound = Boolean.parseBoolean(e.getAttributeValue("inbound"));
		inputConnectionList.clear();
		outputConnectionList.clear();
		if (inbound) {
			inputConnectionList.add(new ConnectionPoint(CONN_POINT, inbound,
					this, false, 1));
		} else {
			outputConnectionList.put(new ConnectionPoint(CONN_POINT, inbound,
					this, true, 1), null);
		}
		connectionName = e.getAttributeValue("connectionName");
		category = e.getAttributeValue("category");
		PersistentUtils.InitObjectFromChild(record, e);
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
		} else if (type.equals(ERecordType.Date.toString())
				|| type.equals(ERecordType.Timestamp.toString())) {
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

	public boolean isInbound() {
		return inbound;
	}

	@Override
	public int getInlinePosition() {
		if (inbound) {
			return AbstractNode.LAST_POSITION;
		}
		return AbstractNode.FIRST_POSITION;
	}

	@Override
	public String toString() {
		return title;
	}

	public int getSqlType() {
		return getRecord().getSqlType();
	}

	public String getConnectionName() {
		return connectionName;
	}

	public String getColumnName() {
		return getRecord().getFieldName();
	}

	// for column Node the transform should be empty
	// becuase Column Node is filled from outside
	public void Transform() throws SocketException {
		if (isInbound()) {
			List<Connector> connectors = getConnPoint(ColumnNode.CONN_POINT)
					.getConnectors();
			if (connectors == null || connectors.size() < 1) {
				return;
			}
			Object obj = connectors.get(0).getBegin().getNode();
			if (obj instanceof NonOperationalNode) {
				setResult(((NonOperationalNode) obj).getResult());
			}
		}
	}

	public boolean realEdit(AbstractApatarActions actions) {
		return false;
	}

	@Override
	public int getFontStyle() {
		if (inbound) {
			return getRecord().isNullable() ? 0 : Font.BOLD;
		}
		return 0;
	}

}