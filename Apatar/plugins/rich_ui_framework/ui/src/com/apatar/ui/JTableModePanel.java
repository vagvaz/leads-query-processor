/*TODO refactoring
 * добавлены геттеры и сеттеры для элементов управления.
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

package com.apatar.ui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.apatar.core.AbstractDataBaseNode;
import com.apatar.core.Record;

public class JTableModePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	// This list should work with list of records

	JList fields = new JList(new DefaultListModel());
	JCheckBox isDeleteAll = new JCheckBox(
			"Clear the selected table before any data written.");
	JRadioButton insertMode = new JRadioButton("Insert Mode");
	JRadioButton updateMode = new JRadioButton("Update Mode");
	JRadioButton syncMode = new JRadioButton("Synchronization Mode");
	JRadioButton deleteMode = new JRadioButton("Delete Mode");

	public JTableModePanel() {
		super();
		createPanel();

		fields.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		fields.setComponentPopupMenu(new JDefaultContextMenu(fields));
		fields.setComponentPopupMenu(new JDefaultContextMenu(fields));
	}

	private void createPanel() {
		setLayout(new BorderLayout(5, 5));

		JPanel modePanel = new JPanel();
		modePanel.setLayout(new BoxLayout(modePanel, BoxLayout.X_AXIS));
		add(modePanel, BorderLayout.NORTH);
		modePanel.add(insertMode);
		modePanel.add(Box.createHorizontalStrut(5));
		modePanel.add(updateMode);
		modePanel.add(Box.createHorizontalStrut(5));
		modePanel.add(syncMode);
		modePanel.add(Box.createHorizontalStrut(5));
		modePanel.add(deleteMode);
		modePanel.add(Box.createHorizontalGlue());

		add(new JScrollPane(fields), BorderLayout.CENTER);
		add(isDeleteAll, BorderLayout.SOUTH);

		ButtonGroup modeBG = new ButtonGroup();
		modeBG.add(insertMode);
		modeBG.add(updateMode);
		modeBG.add(syncMode);
		modeBG.add(deleteMode);
		insertMode.setSelected(true);
	}

	// Please note that every item is the record
	public List<String> getSelectedFields() {
		List<String> res = new ArrayList<String>();
		Object[] selObj = fields.getSelectedValues();
		for (Object element : selObj) {
			res.add(((Record) element).getFieldName());
		}
		return res;
	}

	public void fillFields(List<Record> f) {
		((DefaultListModel) fields.getModel()).removeAllElements();

		if (f == null) {
			return;
		}

		for (Record rec : f) {
			((DefaultListModel) fields.getModel()).addElement(rec);
		}
	}

	public void selectFields(List<String> f) {
		if (f == null) {
			return;
		}

		List<Integer> selection = new ArrayList<Integer>();
		DefaultListModel model = ((DefaultListModel) fields.getModel());
		for (int i = 0; i < model.getSize(); i++) {
			if (f.contains(((Record) model.get(i)).getFieldName())) {
				selection.add(i);
			}
		}
		int si[] = new int[selection.size()];
		for (int i = 0; i < selection.size(); i++) {
			si[i] = selection.get(i);
		}
		fields.setSelectedIndices(si);
	}

	public int getMode() {
		if (insertMode.isSelected()) {
			return AbstractDataBaseNode.INSERT_MODE;
		} else if (updateMode.isSelected()) {
			return AbstractDataBaseNode.UPDATE_MODE;
		} else if (deleteMode.isSelected()) {
			return AbstractDataBaseNode.DELETE_MODE;
		} else {
			return AbstractDataBaseNode.SYNCHRONIZE_MODE;
		}
	}

	public void setMode(int mode) {
		if (mode == AbstractDataBaseNode.INSERT_MODE) {
			insertMode.setSelected(true);
		} else if (mode == AbstractDataBaseNode.SYNCHRONIZE_MODE) {
			syncMode.setSelected(true);
		} else if (mode == AbstractDataBaseNode.DELETE_MODE) {
			deleteMode.setSelected(true);
		} else {
			updateMode.setSelected(true);
		}
	}

	public boolean isDeleteAll() {
		return isDeleteAll.isSelected();
	}

	public void setDeleteAll(boolean b) {
		isDeleteAll.setSelected(b);
	}

	public void setEnableDeleteAll(boolean b) {
		isDeleteAll.setEnabled(b);
	}

	public void setEnableUpdateMode(boolean b) {
		updateMode.setEnabled(b);
	}

	public void setEnableDeleteMode(boolean b) {
		deleteMode.setEnabled(b);
	}

	public void setEnableInsertMode(boolean b) {
		insertMode.setEnabled(b);
	}

	public void setEnableSynchronizationMode(boolean b) {
		syncMode.setEnabled(b);
	}

	/**
	 * @return the isDeleteAll
	 */
	public JCheckBox getIsDeleteAll() {
		return isDeleteAll;
	}

	/**
	 * @param isDeleteAll
	 *            the isDeleteAll to set
	 */
	public void setIsDeleteAll(JCheckBox isDeleteAll) {
		this.isDeleteAll = isDeleteAll;
	}

	/**
	 * @return the insertMode
	 */
	public JRadioButton getInsertMode() {
		return insertMode;
	}

	/**
	 * @param insertMode
	 *            the insertMode to set
	 */
	public void setInsertMode(JRadioButton insertMode) {
		this.insertMode = insertMode;
	}

	/**
	 * @return the updateMode
	 */
	public JRadioButton getUpdateMode() {
		return updateMode;
	}

	/**
	 * @param updateMode
	 *            the updateMode to set
	 */
	public void setUpdateMode(JRadioButton updateMode) {
		this.updateMode = updateMode;
	}

	/**
	 * @return the syncMode
	 */
	public JRadioButton getSyncMode() {
		return syncMode;
	}

	/**
	 * @param syncMode
	 *            the syncMode to set
	 */
	public void setSyncMode(JRadioButton syncMode) {
		this.syncMode = syncMode;
	}

	/**
	 * @return the deleteMode
	 */
	public JRadioButton getDeleteMode() {
		return deleteMode;
	}

	/**
	 * @param deleteMode
	 *            the deleteMode to set
	 */
	public void setDeleteMode(JRadioButton deleteMode) {
		this.deleteMode = deleteMode;
	}
}
