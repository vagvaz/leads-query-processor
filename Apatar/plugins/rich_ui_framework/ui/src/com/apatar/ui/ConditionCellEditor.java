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

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

public class ConditionCellEditor extends JComboBox implements TableCellEditor {
	private static final long serialVersionUID = 1L;

	public Component getTableCellEditorComponent(JTable arg0, Object arg1, boolean arg2, int arg3, int arg4) {
		return null;
	}

	public Object getCellEditorValue() {
		return null;
	}

	public boolean isCellEditable(EventObject arg0) {
		return false;
	}

	public boolean shouldSelectCell(EventObject arg0) {
		return false;
	}

	public boolean stopCellEditing() {
		return false;
	}

	public void cancelCellEditing() {

	}

	public void addCellEditorListener(CellEditorListener arg0) {

	}

	public void removeCellEditorListener(CellEditorListener arg0) {

	}

}
