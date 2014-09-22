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

package com.apatar.textfile.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.apatar.core.ETableMode;
import com.apatar.core.LogUtils;
import com.apatar.core.RDBTable;
import com.apatar.textfile.TextFileNode;
import com.apatar.ui.ApatarActions;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.wizard.TableModeDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class TextFileConnectionDescriptor extends WizardPanelDescriptor {
	public static final String IDENTIFIER = "DBCONNECTION_PANEL";
	TextFileNode node;
	JPanel panel;

	public TextFileConnectionDescriptor(TextFileNode node,
			JDBFileConnectionPanel panel) {
		super(IDENTIFIER, panel);
		this.node = node;
		this.panel = panel;
	}

	@Override
	public Object getNextPanelDescriptor() {
		return TableModeDescriptor.IDENTIFIER;
	}

	@Override
	public Object getBackPanelDescriptor() {
		return null;
	}

	@Override
	public void displayingPanel() {

	}

	@Override
	public void aboutToDisplayPanel() {
		JDBFileConnectionPanel panel = (JDBFileConnectionPanel) this.panel;

		if (null != node.getTableName()) {
			panel.setFileName(node.getTableName());
		}

		panel.setPathToFile(node.getConnectionInfo().getPathToFile());
		panel.setPathToNewFile(node.getConnectionInfo().getPathToFile());
		JDBFileConnectionPanel.setTypeOfNewFile(node.getConnectionInfo()
				.getTypeOfFile());
		panel.setSeparator(node.getConnectionInfo().getSeparator());
		try {
			panel.setDbFields(node.getFieldList(new ApatarActions(node)));
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(getWizard().getDialog(), LogUtils
					.GetExceptionMessage(e));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int aboutToHidePanel(String actionCommand) {

		try {
			if (actionCommand.equals(Wizard.CANCEL_BUTTON_ACTION_COMMAND)) {

				// return CHANGE_PANEL;

			} else if (actionCommand.equals(Wizard.NEXT_BUTTON_ACTION_COMMAND)) {

				JDBFileConnectionPanel panel = (JDBFileConnectionPanel) this.panel;
				if (true == panel.hasFile()) {

					String tableName = panel.getFileName();
					if (tableName == null || tableName.equals("")) {
						JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
								"Preset file");
						return LEAVE_CURRENT_PANEL;
					}

					node.setTable(new RDBTable(panel.getFileName(),
							ETableMode.ReadWrite));
					node.getConnectionInfo().setPathToFile(
							panel.getPathToFile());
					node.getConnectionInfo().setTypeOfFile(
							panel.getTypeOfFile());
				} else {

					String path = panel.getPathToNewFile();
					String separator = System.getProperty("file.separator");

					// try {
					if (!path.substring(path.length() - 1, path.length())
							.equals(separator)) {
						path += separator;
						/*
						 * } catch(Exception e) {
						 * JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
						 * "Incorrect path" ); return LEAVE_CURRENT_PANEL; }
						 */
					}

					String tableName = panel.getNewFileName();
					if (path == null || tableName == null
							|| tableName.equals("") || path.equals("")) {

						JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
								"Preset file");
						return LEAVE_CURRENT_PANEL;
					}
					List<String> fieldList;
					path += panel.getNewFileName() + panel.getTypeOfNewFile();
					fieldList = panel.getDbFields();

					try {
						File f = new File(path);
						f.createNewFile();

						BufferedWriter bw = new BufferedWriter(new FileWriter(
								path));
						for (int i = 0; i < fieldList.size(); i++) {
							bw.write(fieldList.get(i));

							if (i != fieldList.size() - 1) {
								if (panel.getSeparator().equals("TAB")) {
									bw.write("\t");
								} else if (panel.getSeparator().equals("SC")) {
									bw.write(";");
								} else {
									bw.write(panel.getSeparator());
								}
							}
						}

						bw.close();
					} catch (FileNotFoundException e) {
						JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
								"File not found");
						return LEAVE_CURRENT_PANEL;
					} catch (IOException e) {
						JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
								e.getMessage());
						return LEAVE_CURRENT_PANEL;
					}

					node.setTable(new RDBTable(panel.getNewFileName(),
							ETableMode.ReadWrite));
					node.getConnectionInfo().setPathToFile(
							panel.getPathToNewFile());
					node.getConnectionInfo().setTypeOfFile(
							panel.getTypeOfNewFile());
				}
				node.getConnectionInfo().setSeparator(panel.getSeparator());
			}
		} catch (Exception e) {
			return LEAVE_CURRENT_PANEL;
		}

		return CHANGE_PANEL;
	}
}
