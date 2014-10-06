/*
 _______________________
 Apatar Open Source Data Integration
 Copyright (C) 2005-2008, Apatar, Inc.
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
package com.apatar.editgrid.ui;

import javax.swing.JOptionPane;

import com.apatar.core.LogUtils;
import com.apatar.core.RDBTable;
import com.apatar.editgrid.EditgridNode;
import com.apatar.editgrid.EgWorkbook;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.wizard.RecordSourceDescriptor;
import com.apatar.ui.wizard.Wizard;

/**
 * @author Konstantin Maximchik
 */
public class WorkbookListDescriptor extends RecordSourceDescriptor {
	public static final String	IDENTIFIER	= "WORKBOOKLIST_PANEL";

	protected EditgridNode		node;

	/**
	 * @param node
	 * @param backDescriptor
	 * @param nextDescriptor
	 */
	public WorkbookListDescriptor(EditgridNode node, Object backDescriptor,
			Object nextDescriptor) {
		super(node, backDescriptor, nextDescriptor);
		this.node = node;
	}

	@Override
	public void aboutToDisplayPanel() {
		panel.clear();
		getWizard().setTitleComment("Workbooks");
		getWizard().setAdditionalComment(
				"Provides information on which records should be returned");
		try {

			for (RDBTable rtt : node.getWorkbookList()) {
				panel.addTableName(rtt);
			}

			panel.setSelectedValue(node.getWorkbookTable());

		} catch (Exception e) {
			JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME, LogUtils
					.GetExceptionMessage(e));
			e.printStackTrace();
		}
	}

	@Override
	public void displayingPanel() {

	}

	@Override
	public int aboutToHidePanel(String actionCommand) {
		if (actionCommand.equals(Wizard.NEXT_BUTTON_ACTION_COMMAND)) {
			try {
				EgWorkbook table = (EgWorkbook) panel.getSelectedValue();
				if (table == null) {
					JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
							"Please, select workbook");
					return LEAVE_CURRENT_PANEL;
				}
				node.setEgWorkbook(table);

			} catch (Exception e) {
				JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME, e
						.getMessage());
				return LEAVE_CURRENT_PANEL;
			}
		}
		return CHANGE_PANEL;
	}
}
