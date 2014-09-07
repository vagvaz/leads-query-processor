/*
_______________________
Apatar Open Source Data Integration
Copyright (C) 2005-2007, Apatar, Inc.
info@apatar.com
195 Meadow St., 2nd Floor
Chicopee, MA 01013

��� This program is free software; you can redistribute it and/or modify
��� it under the terms of the GNU General Public License as published by
��� the Free Software Foundation; either version 2 of the License, or
��� (at your option) any later version.

��� This program is distributed in the hope that it will be useful,
��� but WITHOUT ANY WARRANTY; without even the implied warranty of
��� MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.� See the
��� GNU General Public License for more details.

��� You should have received a copy of the GNU General Public License along
��� with this program; if not, write to the Free Software Foundation, Inc.,
��� 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
________________________

*/

package com.apatar.http.ui;

import java.util.List;

import com.apatar.core.AbstractDataBaseNode;
import com.apatar.core.DBTypeRecord;
import com.apatar.core.DataConversionAlgorithm;
import com.apatar.core.ERecordType;
import com.apatar.core.Record;
import com.apatar.core.SchemaTable;
import com.apatar.http.HttpNode;
import com.apatar.ui.schematable.JTableSchemaPanel;
import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class TableSchemaDescriptor extends WizardPanelDescriptor {
	
	public static final String IDENTIFIER = "HTTP_TABLE_SCHEMA_PANEL";
	
	JTableSchemaPanel panel;
	AbstractDataBaseNode node;

	public TableSchemaDescriptor(JTableSchemaPanel panel, AbstractDataBaseNode node) {
		super(IDENTIFIER,panel);
		this.panel = panel;
		this.node = node;
		SchemaTable sch = node.getTiForConnection(HttpNode.OUT_CONN_POINT_NAME).getSchemaTable();
		panel.generateSchema(sch.getRecords());
	}
	
	public Object getNextPanelDescriptor() {
        return WizardPanelDescriptor.FINISH;
    }
    
    public Object getBackPanelDescriptor() {
    	return DBConnectionDescriptor.IDENTIFIER;
    }
    
    public void aboutToDisplayPanel() {
		SchemaTable sch = node.getTiForConnection(HttpNode.OUT_CONN_POINT_NAME).getSchemaTable();
    	panel.setNumberNextField(sch.getRecords().size());
	}
	
	public void displayingPanel() {
	}
	
	public int aboutToHidePanel(String actionCommand) {
		SchemaTable sch = node.getTiForConnection(HttpNode.OUT_CONN_POINT_NAME).getSchemaTable();
		List<Record> recs = sch.getRecords();
		recs.clear();
		panel.getRecords(recs, node.getDataBaseInfo().getAvailableTypes());
		panel.stopCurrentCellEditing();
		if (!recs.get(0).getFieldName().equalsIgnoreCase("Response")) {
			DBTypeRecord dbtRec = DataConversionAlgorithm.bestRecordLookup(node.getDataBaseInfo().getAvailableTypes(), ERecordType.Text, 255);
			recs.add(0, new Record(dbtRec, "Response", 255, false, false, false));
		}
		return CHANGE_PANEL;
	}

}
