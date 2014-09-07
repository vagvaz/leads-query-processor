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

import javax.swing.JTable;
import javax.swing.table.TableModel;

import com.apatar.ui.JDefaultContextMenu;

public class JSchemaTable extends JTable {
	private static final long serialVersionUID = 1L;

	public JSchemaTable() {
		super();
		setEditor();
		setComponentPopupMenu(new JDefaultContextMenu(this));
	}

	public JSchemaTable(Object[][] arg0, Object[] arg1) {
		super(arg0, arg1);
		setEditor();
		setComponentPopupMenu(new JDefaultContextMenu(this));
	}

	public JSchemaTable(TableModel arg0) {
		super(arg0);
		setEditor();
		setComponentPopupMenu(new JDefaultContextMenu(this));
	}

	private void setEditor() {
		/*
		 * NumberFormat nf = NumberFormat.getInstance();
		 * nf.setParseIntegerOnly(true); NumberFormatter intFormat = new
		 * NumberFormatter(nf); getColumn("Size").setCellEditor(new
		 * DefaultCellEditor(new JFormattedTextField(intFormat)));
		 */
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		/*
		 * switch (column) { case 0: return false; case 1: return true; case 2:
		 * return true; case 3: String type = (String)getValueAt(row,2); type =
		 * type.toUpperCase(); if (type.equals("VARCHAR")) return true; return
		 * false; default: return true; }
		 */
		return true;
	}

	public void selectRow(int row) {
		int numRow = getRowCount();

		if (numRow == 0) {
			return;
		}

		if (row > (numRow - 1)) {
			changeSelection((numRow - 1), 0, false, false);
		} else if (row < 0) {
			changeSelection(0, 0, false, false);
		} else {
			changeSelection(row, 0, false, false);
		}
	}
}
