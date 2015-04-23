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

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.apatar.core.RDBTable;
import com.apatar.ui.JSortedList;

public class JRecordSourcePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JSortedList<RDBTable> listTables;
	
	private JSplitPane splitPane;

	public JRecordSourcePanel() {
		super();
		setLayout(new BorderLayout());
		content();
		createListeners();
	}
	
	public void content() {
		listTables = new JSortedList<RDBTable>(new RDBTable.RDBTableComparator());
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(new JScrollPane(listTables));
		splitPane.setDividerLocation(270);
		add(splitPane,BorderLayout.CENTER);
	}
	
	private void createListeners() {

	}
	
	public void addTableName(RDBTable table) {
		listTables.add(table, true);
	}

	public void clearAllTables() {
		listTables.removeAll();
	}
	
	public RDBTable getSelectedValue()
	{
		return (RDBTable)listTables.getSelectedValue();
	}
	
	public void clear()
	{
		((DefaultListModel)listTables.getModel()).clear();
	}

	// select RDB tables by name
	public void setSelectedValue(RDBTable value)
	{
		DefaultListModel dm = ((DefaultListModel)listTables.getModel());
		for(int i = 0; i < dm.getSize(); i++)
		{
			if (((RDBTable)dm.get(i)).getTableName().equalsIgnoreCase(value.getTableName()))
			{
				listTables.setSelectedValue(dm.get(i), true);
				return;
			}
		}
	}
}
