/*TODO refactoring
 * добавлены геттер и сеттер для панели panel
 * *********************
 */
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

import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.apatar.core.AbstractDataBaseNode;
import com.apatar.core.DataBaseInfo;
import com.apatar.core.LogUtils;
import com.apatar.ui.ApatarActions;
import com.apatar.ui.JTableModePanel;

public class TableModeDescriptor extends WizardPanelDescriptor {

	public static final String IDENTIFIER = "TABLEMODE_PANEL";

	AbstractDataBaseNode node;
	JTableModePanel panel = new JTableModePanel();
	Object backDescriptor;
	Object nextDescriptor;

	public TableModeDescriptor(AbstractDataBaseNode node,
			Object backDescriptor, Object nextDescriptor) {
		super();
		setPanelDescriptorIdentifier(IDENTIFIER);
		setPanelComponent(panel);
		this.node = node;
		this.backDescriptor = backDescriptor;
		this.nextDescriptor = nextDescriptor;
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

		DataBaseInfo dbi = node.getDataBaseInfo();

		panel.setDeleteAll(node.isDeleteAllInRDB());
		panel.setMode(node.getMode());

		panel.setEnableDeleteAll(dbi.isSupportClearData());
		panel.setEnableInsertMode(dbi.isSupportInsertMode());
		panel.setEnableUpdateMode(dbi.isSupportUpdateMode());
		panel.setEnableDeleteMode(dbi.isSupportDeleteMode());
		panel.setEnableSynchronizationMode(dbi.isSupportSynchronization());

		if (!dbi.isSupportUpdateMode()) {
			panel.setMode(AbstractDataBaseNode.INSERT_MODE);
		}

		if (!dbi.isSupportInsertMode()) {
			panel.setMode(AbstractDataBaseNode.UPDATE_MODE);
		}

		if (!dbi.isSupportClearData()) {
			panel.setDeleteAll(false);
		}

		try {
			panel.fillFields(node.getFieldList(new ApatarActions(node)));
			panel.selectFields(node.getIdentificationFields());
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(getWizard().getDialog(), LogUtils
					.GetExceptionMessage(e));
		} catch (Exception e) {
			e.printStackTrace();
		}

		getWizard().setTitleComment("");
		getWizard().setAdditionalComment("");
	}

	@Override
	public void displayingPanel() {

	}

	@Override
	public int aboutToHidePanel(String actionCommand) {
		if (actionCommand.equals(Wizard.NEXT_BUTTON_ACTION_COMMAND)) {
			node.setMode(panel.getMode());
			node.setDeleteAllInRDB(panel.isDeleteAll());
			node.setIdentificationFields(panel.getSelectedFields());
		}
		return CHANGE_PANEL;
	}

	/**
	 * @return the panel
	 */
	public JTableModePanel getPanel() {
		return panel;
	}

	/**
	 * @param panel
	 *            the panel to set
	 */
	public void setPanel(JTableModePanel panel) {
		this.panel = panel;
	}

}
