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

package com.apatar.ui.wizard;

import javax.swing.JOptionPane;

import com.apatar.core.AbstractDataBaseNode;
import com.apatar.core.ApplicationData;
import com.apatar.core.JdbcParams;
import com.apatar.core.LogUtils;
import com.apatar.core.RDBTable;
import com.apatar.ui.ApatarUiMain;

public class JdbcRecordSourceDescriptor extends WizardPanelDescriptor {

	public static final String IDENTIFIER = "RECORDSOURCE_PANEL";

	AbstractDataBaseNode node;
	JJdbcRecordSourcePanel panel;
	Object backDescriptor;
	Object nextDescriptor;

	public JdbcRecordSourceDescriptor(AbstractDataBaseNode node,
			Object backDescriptor, Object nextDescriptor) {
		super();

		setPanelDescriptorIdentifier(IDENTIFIER);
		this.node = node;
		panel = new JJdbcRecordSourcePanel(node.getDataBaseInfo());
		this.backDescriptor = backDescriptor;
		this.nextDescriptor = nextDescriptor;
		setPanelComponent(panel);
	}

	@Override
	public Object getNextPanelDescriptor() {
		return nextDescriptor;
	}

	@Override
	public Object getBackPanelDescriptor() {
		return backDescriptor;
	}

	@Override
	public void aboutToDisplayPanel() {
		panel.clear();
		try {

			for (RDBTable rtt : node.getTableList()) {
				panel.addTableName(rtt);
			}

			panel.setSelectedValue(node.getTable());
			JdbcParams params = (JdbcParams) ApplicationData.getProject()
					.getProjectData(node.getConnectionDataID()).getData();
			if (params != null) {
				String query = params.getSqlQuery();
				if (query != null && !query.equals("")) {
					panel.setSqlQuery(params.getSqlQuery());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME, LogUtils
					.GetExceptionMessage(e));
		}
		getWizard().setTitleComment("Record Source");
		getWizard().setAdditionalComment(
				"Provides information on which records should be returned");
	}

	@Override
	public void displayingPanel() {

	}

	@Override
	public int aboutToHidePanel(String actionCommand) {
		if (actionCommand.equals(Wizard.NEXT_BUTTON_ACTION_COMMAND)) {
			try {
				RDBTable table = panel.getSelectedValue();
				if (table == null) {
					JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
							"Please, select table name");
					return LEAVE_CURRENT_PANEL;
				}
				node.setTable(table);
				JdbcParams params = (JdbcParams) ApplicationData.getProject()
						.getProjectData(node.getConnectionDataID()).getData();
				params.setSqlQuery(panel.getSqlQuery().getText());
			} catch (Exception e) {
				JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME, e
						.getMessage());
				return LEAVE_CURRENT_PANEL;
			}
		}
		if (actionCommand.equals(Wizard.BACK_BUTTON_ACTION_COMMAND)) {
			node.setConnectionToNull();
		}
		return CHANGE_PANEL;
	}

	public JJdbcRecordSourcePanel getPanel() {
		return panel;
	}

	public void setPanel(JJdbcRecordSourcePanel panel) {
		this.panel = panel;
	}

	public AbstractDataBaseNode getNode() {
		return node;
	}

	public void setNode(AbstractDataBaseNode node) {
		this.node = node;
	}

}
