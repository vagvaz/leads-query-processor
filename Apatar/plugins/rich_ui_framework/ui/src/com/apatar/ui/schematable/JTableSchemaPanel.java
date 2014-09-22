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

package com.apatar.ui.schematable;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.apatar.core.DBTypeRecord;
import com.apatar.core.DataConversionAlgorithm;
import com.apatar.core.ERecordType;
import com.apatar.core.Record;
import com.apatar.ui.GetInputs;

public class JTableSchemaPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	int numberNextField = 0;

	JSchemaTable tableSchema;
	JButton addField;
	JButton removeField;
	private boolean schemaChanged = false;

	JButton getInputs;

	// JButton primaryKey;
	JComboBox typeField;
	DefaultCellEditor typeEditor;
	private int textTypePosition = 0;

	JTextField nameField;
	DefaultCellEditor nameEditor;

	JTextField sizeField;
	DefaultCellEditor sizeEditor;

	boolean change = true;

	private List<DBTypeRecord> dbirecords;

	private List<Record> records;

	private GetInputs node = null;

	private List<DBTypeRecord> recs = null;

	private ApatarCellEditorListener cellEditorListner = new ApatarCellEditorListener();

	// HashSet<String> columnNames = new HashSet<String>();

	public JTableSchemaPanel(List<DBTypeRecord> name, List<Record> records,
			GetInputs node) {
		super();
		dbirecords = name;
		this.records = records;
		this.node = node;
		createPanel();
		generateListeners();
	}

	public JTableSchemaPanel(List<DBTypeRecord> name, List<Record> records) {
		this(name, records, null);
	}

	private void createPanel() {
		setLayout(new BorderLayout());
		JToolBar panelButton = new JToolBar();

		addField = new JButton(new ImageIcon(this.getClass().getResource(
				"add.png")));
		removeField = new JButton(new ImageIcon(this.getClass().getResource(
				"delete.png")));
		// primaryKey = new JButton(new
		// ImageIcon(this.getClass().getResource("primary.png")));

		addField.setToolTipText("Add column");
		removeField.setToolTipText("Remove selected column");
		// primaryKey.setToolTipText("Set/Reset primary key");

		panelButton.add(addField);
		panelButton.add(removeField);
		// panelButton.add(primaryKey);

		if (node != null) {
			getInputs = new JButton("Add from Inputs");
			panelButton.add(getInputs);
		}

		panelButton.setFloatable(false);

		tableSchema = new JSchemaTable(new DefaultTableModel(new Object[][] {},
				new Object[] { " ", "Name", "Data Type", "Size" }));

		TableColumn column = tableSchema.getColumn(" ");
		column.setMinWidth(0);
		column.setMaxWidth(0);
		column.setCellRenderer(new SchemaCellRenderer());

		column = tableSchema.getColumn("Name");
		column.setMinWidth(250);
		column.setMaxWidth(250);
		nameField = new JTextField();
		nameEditor = new DefaultCellEditor(nameField);
		nameEditor.addCellEditorListener(cellEditorListner);
		column.setCellEditor(nameEditor);

		column = tableSchema.getColumn("Data Type");

		// Object[] types = new Object[dbirecords.size()];
		Set<ERecordType> types = new HashSet<ERecordType>();

		// int i=0;
		for (DBTypeRecord rec : dbirecords) {
			types.add(rec.getType());
		}
		TreeSet<ERecordType> sortedTypes = new TreeSet<ERecordType>(types);
		typeField = new JComboBox(sortedTypes.toArray());
		typeField.setSelectedItem(ERecordType.Text);
		textTypePosition = typeField.getSelectedIndex();
		typeEditor = new DefaultCellEditor(typeField);
		column.setCellEditor(typeEditor);
		typeField.setSelectedItem(ERecordType.Text);
		column.setMinWidth(100);
		column.setMaxWidth(100);

		add(panelButton, BorderLayout.NORTH);
		add(new JScrollPane(tableSchema), BorderLayout.CENTER);
		typeEditor.addCellEditorListener(cellEditorListner);
		column = tableSchema.getColumn("Size");
		sizeField = new JTextField();
		sizeEditor = new DefaultCellEditor(sizeField);
		sizeEditor.addCellEditorListener(cellEditorListner);
		column.setCellEditor(sizeEditor);
	}

	private class ApatarCellEditorListener implements CellEditorListener {
		@Override
		public void editingStopped(ChangeEvent e) {
			checkIfSchemaChanged();
		}

		@Override
		public void editingCanceled(ChangeEvent e) {
			checkIfSchemaChanged();
		}
	}

	private void checkIfSchemaChanged() {
		if (schemaChanged) {
			return;
		}
		List<Record> recordsFromGrid = new ArrayList<Record>();
		getRecords(recordsFromGrid, recs);
		if (recordsFromGrid.size() != records.size()) {
			schemaChanged = true;
			return;
		}
		for (Record record : recordsFromGrid) {
			if (!record
					.equalTo(getRecordByName(record.getFieldName(), records))) {
				schemaChanged = true;
				return;
			}
		}
	}

	private Record getRecordByName(String fieldName, List<Record> records) {
		for (Record record : records) {
			if (record.getFieldName().equals(fieldName)) {
				return record;
			}
		}
		return null;
	}

	private void generateListeners() {

		addField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				schemaChanged = true;
				((DefaultTableModel) tableSchema.getModel()).insertRow(
						tableSchema.getRowCount(), new Object[] {
								new Boolean(false), getFieldName(),
								typeField.getItemAt(textTypePosition), 255 });
				tableSchema.selectRow(tableSchema.getRowCount() - 1);
			}
		});
		/*
		 * primaryKey.addActionListener(new ActionListener() { public void
		 * actionPerformed(ActionEvent arg0) { int row =
		 * tableSchema.getSelectedRow(); boolean bool =
		 * (Boolean)tableSchema.getValueAt(row,0); if (bool == true)
		 * tableSchema.setValueAt(false,row,0); else
		 * tableSchema.setValueAt(true,row,0); } });
		 */
		removeField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				schemaChanged = true;
				int[] selRows = tableSchema.getSelectedRows();
				if (selRows.length > 0) {
					for (int selRow : selRows) {
						((DefaultTableModel) tableSchema.getModel())
								.removeRow(tableSchema.getSelectedRow());
					}
					((DefaultTableModel) tableSchema.getModel())
							.fireTableDataChanged();
				}
			}
		});

		if (getInputs != null) {
			getInputs.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					schemaChanged = true;
					List<Record> recs = node.getInputs();
					if (recs == null) {
						return;
					}
					for (Record record : recs) {
						String fn = record.getFieldName();
						((DefaultTableModel) tableSchema.getModel()).insertRow(
								tableSchema.getRowCount(), new Object[] {
										new Boolean(false), fn,
										record.getType(), 255 });
						// records.add(record);
					}
					tableSchema.selectRow(tableSchema.getRowCount() - 1);
				}
			});
		}
	}

	public void getRecords(List<Record> records, List<DBTypeRecord> recs) {
		// records.clear();
		for (int i = 0; i < tableSchema.getRowCount(); i++) {
			ERecordType type = (ERecordType) tableSchema.getValueAt(i, 2);
			Object obj = tableSchema.getValueAt(i, 3);
			String size;
			String fieldName = tableSchema.getValueAt(i, 1).toString();
			if (obj != null) {
				size = obj.toString();
			} else {
				size = "";
			}
			boolean primary = (Boolean) tableSchema.getValueAt(i, 0);
			long sizeInByte = Long.parseLong(size);
			DBTypeRecord dbtRec = DataConversionAlgorithm.bestRecordLookup(
					recs, type, sizeInByte);

			if (dbtRec != null) {
				records.add(new Record(dbtRec, fieldName, sizeInByte, true,
						false, primary));
			}
		}
	}

	public void generateSchema(List<Record> records) {
		if (records == null) {
			return;
		}
		for (Record record : records) {
			Object[] obj;
			long size = record.getLength();
			boolean primary = record.isPrimaryKey();
			obj = new Object[] { primary, record.getFieldName(),
					record.getType(), size > 0 ? "" + size : null };
			((DefaultTableModel) tableSchema.getModel()).insertRow(tableSchema
					.getRowCount(), obj);
		}
		this.records = records;

	}

	public int getNumberNextField() {
		return numberNextField;
	}

	public void setNumberNextField(int numberNextField) {
		this.numberNextField = numberNextField;
	}

	public void stopCurrentCellEditing() {
		if (tableSchema.getCellEditor() != null) {
			tableSchema.getCellEditor().stopCellEditing();
		}
	}

	private String getFieldName() {
		while (numberNextField <= Integer.MAX_VALUE) {
			String nameColumn = "Column" + numberNextField++;

			if (records == null) {
				return nameColumn;
			}
			// Record.getRecordByFieldName(records, nameColumn);
			if (Record.getRecordByFieldName(records, nameColumn) == null) {
				return nameColumn;
			}
		}
		numberNextField = 0;
		return getFieldName();
	}

	/**
	 * @return the schemaChanged
	 */
	public boolean isSchemaChanged() {
		return schemaChanged;
	}

	/**
	 * @param recs
	 *            the recs to set
	 */
	public void setRecs(List<DBTypeRecord> recs) {
		this.recs = recs;
	}

	/**
	 * @param schemaChanged
	 *            the schemaChanged to set
	 */
	public void setSchemaChanged(boolean schemaChanged) {
		this.schemaChanged = schemaChanged;
	}

}
