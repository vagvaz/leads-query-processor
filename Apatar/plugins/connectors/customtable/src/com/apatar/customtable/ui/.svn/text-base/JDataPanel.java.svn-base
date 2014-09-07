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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;

import org.jdom.Document;
import org.jdom.Element;

import com.apatar.customtable.CustomUtils;
import com.apatar.ui.JDefaultContextMenu;

public class JDataPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	JTable tableData;
	JButton addField;
	JButton removeField;

	public JDataPanel() {
		super();
		createPanel();
		generateListeners();
	}

	private void createPanel() {
		setLayout(new BorderLayout());
		JToolBar panelButton = new JToolBar();

		addField = new JButton(CustomUtils.ADD);
		removeField = new JButton(CustomUtils.DELETE);

		addField.setToolTipText("Add data row");
		removeField.setToolTipText("Remove Selected data row");

		panelButton.add(addField);
		panelButton.add(removeField);

		panelButton.setFloatable(false);

		tableData = new JTable(new DefaultTableModel(new Object[][] {},
				new Object[] {}));
		tableData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableData.setComponentPopupMenu(new JDefaultContextMenu(tableData));

		add(panelButton, BorderLayout.NORTH);
		add(new JScrollPane(tableData), BorderLayout.CENTER);
	}

	private void generateListeners() {
		addField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				((DefaultTableModel) tableData.getModel())
						.addRow(new Object[] {});
			}
		});
		removeField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				((DefaultTableModel) tableData.getModel()).removeRow(tableData
						.getSelectedRow());
			}
		});
	}

	public void addColumn(String name) {
		((DefaultTableModel) tableData.getModel()).addColumn(name);
	}

	public void clearColumn() {
		((DefaultTableModel) tableData.getModel()).setColumnCount(0);
	}

	/*
	 * public Object[] getFieldValues(int row) { int
	 * count=tableData.getColumnCount(); Object[] values = new Object[count];
	 * for (int i=0;i<count;i++) { values[i]=tableData.getValueAt(row,i); }
	 * return values; }
	 */

	public int getRecordCount() {
		return tableData.getRowCount();
	}

	public Document getData() {
		Document doc = new Document();
		Element root = new Element("root");

		int rowCount = tableData.getRowCount();
		int colCount = tableData.getColumnCount();

		for (int i = 0; i < rowCount; i++) {
			Element row = new Element("row");

			for (int j = 0; j < colCount; j++) {
				Element cell = new Element(tableData.getColumnName(j));
				Object content = tableData.getValueAt(i, j);
				if (null == content) {
					cell.addContent("");
				} else {
					cell.addContent(content.toString());
				}
				row.addContent(cell);
			}

			root.addContent(row);
		}

		doc.addContent(root);

		return doc;
	}

	public void addData(Document data) {
		if (null != data && null != data.getRootElement()) {

			int columnCount = tableData.getColumnCount();

			List children = data.getRootElement().getChildren();

			for (Iterator it = children.iterator(); it.hasNext();) {
				Element xmlRow = (Element) it.next();
				Object tblRow[] = new Object[columnCount];
				for (int i = 0; i < columnCount; i++) {
					String name = tableData.getColumnName(i);
					Element cell = xmlRow.getChild(name);

					if (null != cell) {
						tblRow[i] = cell.getValue();
					}
				}
				((DefaultTableModel) tableData.getModel()).addRow(tblRow);
			} // for

		}
	}

	public void clearData() {
		for (int i = tableData.getRowCount() - 1; i > -1; i--) {
			((DefaultTableModel) tableData.getModel()).removeRow(i);
		}

		// ((DefaultTableModel)tableData.getModel()).setRowCount(0);
	}

	public void stopCurrentCellEditing() {
		if (tableData.getCellEditor() != null) {
			tableData.getCellEditor().stopCellEditing();
		}
	}
}
