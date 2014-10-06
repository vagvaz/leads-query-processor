/*
_______________________

Apatar Open Source Data Integration

Copyright (C) 2005-2007, Apatar, Inc.

info@apatar.com

195 Meadow St., 2nd Floor

Chicopee, MA 01013

 

    This program is free software; you can redistribute it and/or modify

    it under the terms of the GNU General Public License as published by

    the Free Software Foundation; either version 2 of the License, or

    (at your option) any later version.

 

    This program is distributed in the hope that it will be useful,

    but WITHOUT ANY WARRANTY; without even the implied warranty of

    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the

    GNU General Public License for more details.

 

    You should have received a copy of the GNU General Public License along

    with this program; if not, write to the Free Software Foundation, Inc.,

    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

________________________

 */

package com.apatar.msexcel;

import org.jdom.Element;

import com.apatar.core.ColumnNode;
import com.apatar.core.ConnectionPoint;
import com.apatar.core.PersistentUtils;
import com.apatar.core.Record;

public class MsExcelColumnNode extends ColumnNode {

	protected MsExcelRecord erecord = new MsExcelRecord();

	public MsExcelColumnNode(Record rec, String connectionName,
			String category, boolean inbound) {
		super(rec, connectionName, category, inbound);
		// if record is passed as null (is not from the wizard, but from save
		// load)
		if (rec != null) {
			erecord = (MsExcelRecord) rec;
		}
		if (erecord != null) {
			updateTitle();
		}
	}

	@Override
	public Record getRecord() {
		return erecord;
	}

	@Override
	public Element saveToElement() {
		Element columnNode = super.saveToElement();
		columnNode.setAttribute("inbound", String.valueOf(inbound));
		columnNode.setAttribute("connectionName", connectionName);
		columnNode.setAttribute("category", category);
		columnNode.addContent(erecord.saveToElement());
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
		PersistentUtils.InitObjectFromChild(erecord, e);
	}

}
