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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.apatar.core.DataBaseInfo;
import com.apatar.core.DataBaseTools;
import com.apatar.core.JdbcParams;

public class JPreviewPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	JTable tablePreview;
	JdbcParams jdbcParams;
	String tableName;
	DataBaseInfo dbi;
	
	public JPreviewPanel(JdbcParams params, String table, DataBaseInfo dbi) {
		super();
		jdbcParams = params;
		tableName = table;
		this.dbi = dbi;
		setLayout(new BorderLayout());
		content();
		loadData();
	}
	
	public void content() {
		tablePreview = new JTable();
		tablePreview.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tablePreview.setComponentPopupMenu(
				new JDefaultContextMenu(tablePreview) );
		add(new JScrollPane(tablePreview),BorderLayout.CENTER);
	}
	
	public void addColumn(String name) {
		((DefaultTableModel)tablePreview.getModel()).addColumn(name);
	}
	
	public void addRow(Object[] obj) {
		((DefaultTableModel)tablePreview.getModel()).addRow(obj);
	}
	public void resetColumns() {
		((DefaultTableModel)tablePreview.getModel()).setColumnCount(0);
	}
	public void resetRows() {
		((DefaultTableModel)tablePreview.getModel()).setRowCount(0);
	}

    public void loadData() {
    	try {
			ResultSet rs = DataBaseTools.getRSWithAllFields(tableName, 
					jdbcParams, dbi);
			ResultSetMetaData rsmd=rs.getMetaData();
			int count=rsmd.getColumnCount();

			resetColumns();
			int n = 1;
			addColumn("No.");
			for (int i=1;i<=rsmd.getColumnCount();i++) {
				addColumn(rsmd.getColumnName(i));
			}
			resetRows();
			while (rs.next()) {
				Object[] obj = new Object[count + 1];
				obj[0] = n++;
				for (int i=1; i<=count; i++)
				{
					try{
						obj[i] = rs.getString(i);
					}catch(OutOfMemoryError e)
					{
						obj[i] = "Too much data to display";
					}
				}
				addRow(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
