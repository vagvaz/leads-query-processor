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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;

import com.apatar.core.Condition;
import com.apatar.core.Record;
import com.apatar.core.TableInfo;

public class JConditionPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	JTable table;
	JButton addField;
	JButton removeField;

	JComboBox table1CB;
	JComboBox table2CB;

	String ti1Name;
	String ti2Name;

	public JConditionPanel(TableInfo ti1, TableInfo ti2, String tableName1,
			String tableName2) {
		super();

		table1CB = ti1 == null ? new JComboBox() : new JComboBox(
				getColumns(ti1));
		table2CB = ti2 == null ? new JComboBox() : new JComboBox(
				getColumns(ti2));

		ti1Name = tableName1;
		// if table names are identical
		ti2Name = (tableName2.equals(tableName1)) ? tableName2 + "*"
				: tableName2;

		createPanel();
		generateListeners();
	}

	private void createPanel() {
		setLayout(new BorderLayout());
		JToolBar panelButton = new JToolBar();

		addField = new JButton(new ImageIcon(this.getClass().getResource(
				"add.png")));
		removeField = new JButton(new ImageIcon(this.getClass().getResource(
				"delete.png")));

		panelButton.add(addField);
		panelButton.add(removeField);
		panelButton.addSeparator();
		JLabel message = new JLabel(
				"Join if the selected Input Table1 and Input Table2 fields contain equal values");
		message.setBorder(BorderFactory.createEmptyBorder());
		panelButton.add(message);
		panelButton.setFloatable(false);

		table = new JTable(new DefaultTableModel(new Object[][] {},
				new Object[] { ti1Name, ti2Name }));
		table.getColumn(ti1Name).setCellEditor(new DefaultCellEditor(table1CB));
		table.getColumn(ti2Name).setCellEditor(new DefaultCellEditor(table2CB));

		table.setComponentPopupMenu(new JDefaultContextMenu(table));

		add(panelButton, BorderLayout.NORTH);
		add(new JScrollPane(table), BorderLayout.CENTER);
	}

	private void generateListeners() {

		addField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				((DefaultTableModel) table.getModel()).insertRow(table
						.getRowCount(), new Object[] {});
			}
		});
		removeField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				((DefaultTableModel) table.getModel()).removeRow(table
						.getSelectedRow());
			}
		});
	}

	public List<Condition> getConditions() {
		List<Condition> conditions = new ArrayList<Condition>();
		for (int i = 0; i < table.getRowCount(); i++) {
			conditions.add(new Condition(table.getValueAt(i, 0).toString(),
					table.getValueAt(i, 1).toString()));
		}
		return conditions;
	}

	public void updateCondition(List<Condition> conditions) {
		DefaultTableModel tm = (DefaultTableModel) table.getModel();
		tm.setRowCount(0);
		for (Condition condition2 : conditions) {
			Condition condition = condition2;
			tm.addRow(new Object[] { condition.getColumn1(),
					condition.getColumn2() });
		}
	}

	private static Vector<String> getColumns(TableInfo ti) {
		Vector<String> columns = new Vector<String>();
		for (Record record : ti.getSchemaTable().getRecords()) {
			Record rec = record;
			columns.add(rec.getFieldName());
		}
		return columns;
	}
}
