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

import javax.swing.JOptionPane;

import com.apatar.core.AbstractDataBaseNode;
import com.apatar.core.Record;
import com.apatar.customtable.CustomTableNode;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class DataDescriptor extends WizardPanelDescriptor {

	public static final String IDENTIFIER = "DATA_PANEL";

	List<Record> records;
	CustomTableNode node;
	JDataPanel panel;

	private Object NextDescriptor;
	private Object BackDescriptor;

	public DataDescriptor(CustomTableNode node, Object prev, Object next) {
		super();
		panel = new JDataPanel();

		setPanelDescriptorIdentifier(IDENTIFIER);
		setPanelComponent(panel);

		records = node.getTiForConnection(
				AbstractDataBaseNode.OUT_CONN_POINT_NAME).getSchemaTable()
				.getRecords();
		this.node = node;
		NextDescriptor = next;
		BackDescriptor = prev;
		// panel.addData(node.getData());
	}

	@Override
	public Object getNextPanelDescriptor() {
		return NextDescriptor;
	}

	@Override
	public Object getBackPanelDescriptor() {
		return BackDescriptor;
	}

	@Override
	public void aboutToDisplayPanel() {
		panel.clearColumn();
		for (Record record : records) {
			String name = record.getFieldName();
			panel.addColumn(name);
		}
		panel.clearData();
		panel.addData(node.getData());
	}

	@Override
	public void displayingPanel() {
	}

	@Override
	public int aboutToHidePanel(String actionCommand) {
		panel.stopCurrentCellEditing();
		try {
			// if (actionCommand.equals(Wizard.BACK_BUTTON_ACTION_COMMAND))
			// return CHANGE_PANEL;

			node.setData(panel.getData());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME, e
					.getMessage());
			return LEAVE_CURRENT_PANEL;
		}
		return CHANGE_PANEL;
	}
}
