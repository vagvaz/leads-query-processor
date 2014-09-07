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

package com.apatar.customtable.ui;

import java.util.List;

import com.apatar.core.AbstractDataBaseNode;
import com.apatar.core.Record;
import com.apatar.core.SchemaTable;
import com.apatar.customtable.CustomTableNode;
import com.apatar.ui.schematable.JTableSchemaPanel;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class TableSchemaDescriptor extends WizardPanelDescriptor {

	public static final String IDENTIFIER = "TABLE_SCHEMA_PANEL";

	JTableSchemaPanel panel;
	AbstractDataBaseNode node;

	public TableSchemaDescriptor(JTableSchemaPanel panel,
			AbstractDataBaseNode node) {
		super(IDENTIFIER, panel);
		this.panel = panel;
		this.node = node;
		SchemaTable sch = node.getTiForConnection(
				AbstractDataBaseNode.OUT_CONN_POINT_NAME).getSchemaTable();
		panel.generateSchema(sch.getRecords());
		panel.setRecs(node.getDataBaseInfo().getAvailableTypes());
	}

	@Override
	public Object getNextPanelDescriptor() {
		return DataDescriptor.IDENTIFIER;
	}

	@Override
	public Object getBackPanelDescriptor() {
		return null;
	}

	@Override
	public void aboutToDisplayPanel() {
		SchemaTable sch = node.getTiForConnection(
				AbstractDataBaseNode.OUT_CONN_POINT_NAME).getSchemaTable();
		panel.setNumberNextField(sch.getRecords().size());
		if (getLastButtonPressed() != null
				&& getLastButtonPressed().equals(
						Wizard.BACK_BUTTON_ACTION_COMMAND)) {
			panel.setSchemaChanged(false);
		}
	}

	@Override
	public void displayingPanel() {
	}

	@Override
	public int aboutToHidePanel(String actionCommand) {
		SchemaTable sch = node.getTiForConnection(
				AbstractDataBaseNode.OUT_CONN_POINT_NAME).getSchemaTable();
		List<Record> recs = sch.getRecords();
		recs.clear();
		panel.getRecords(recs, node.getDataBaseInfo().getAvailableTypes());
		panel.stopCurrentCellEditing();
		if (panel.isSchemaChanged()) {
			((CustomTableNode) node).setData(null);
		}
		return CHANGE_PANEL;
	}

}
