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
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import com.apatar.core.ApplicationData;
import com.apatar.core.DBTypeRecord;
import com.apatar.core.DataConversionAlgorithm;
import com.apatar.core.ERecordType;
import com.apatar.core.Record;
import com.apatar.core.SchemaTable;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.GetInputs;

public class JSchemaDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	JTableSchemaPanel panel;
	
	public static final int OK_OPTION = 0;
	public static final int CANCEL_OPTION = 1;
	
	private JButton buttonOk = new JButton("Ok");
    private JButton buttonCancel = new JButton("Cancel");
    
    int option;
    
    SchemaTable schema;
    List<DBTypeRecord> dbTypeRecords;
    
    GetInputs getInputs;
    
	public JSchemaDialog(boolean isModal, List<DBTypeRecord> recs, SchemaTable schema, GetInputs inputs) throws HeadlessException {
		super(ApatarUiMain.MAIN_FRAME, isModal);
		
		Rectangle rc = getGraphicsConfiguration().getBounds(); 
		setBounds(50, 10, rc.width-200, rc.height-200);
		
		setTitle("Edit Schema");
		
		this.schema = schema;
		this.dbTypeRecords = recs;
		this.getInputs = inputs;
		
		getContentPane().setLayout(new BorderLayout(5,5));
		panel = new JTableSchemaPanel(ApplicationData.getTempDataBase().getDataBaseInfo().getAvailableTypes(), schema.getRecords(), getInputs);
		getContentPane().add(panel, BorderLayout.CENTER);
		getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);
		updateColumn();
	}
	
	public void getRecords(ArrayList<Record> records) {
		records.clear();
		panel.getRecords(records, ApplicationData.getTempDataBase().getDataBaseInfo().getAvailableTypes());
	}
	
	private JPanel createButtonPanel() {
		JPanel panel = new JPanel();
		BoxLayout layout = new BoxLayout(panel, BoxLayout.X_AXIS);
		panel.setLayout(layout);
		panel.add(Box.createHorizontalGlue());
		
		/*panel.add(addInputs);
		panel.add(Box.createHorizontalStrut(15));*/
		panel.add(buttonOk);
		panel.add(Box.createHorizontalStrut(5));
		panel.add(buttonCancel);
		
		buttonOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if (!updateRecords())
            		return;
                option=OK_OPTION;
                setDialogVisible(false);
            }
        });
		buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                option=CANCEL_OPTION;
                setDialogVisible(false);
            }
        });

		return panel;
	}
	
	void setDialogVisible(boolean b) {
		super.setVisible(b);
	}
	
	public int showDialog() {
		updateColumn();
    	super.setVisible(true);
        return option;
    }
	
	public void updateColumn() {
		((DefaultTableModel)panel.tableSchema.getModel()).setRowCount(0);
		for (Record record : schema.getRecords()) 
		{
			Object[] obj = new Object[] {record.isPrimaryKey(), record.getFieldName(),
					record.getType(), record.getLength()+ ""};
			((DefaultTableModel)panel.tableSchema.getModel()).insertRow(panel.tableSchema.getRowCount(),obj);
		}
	}
	
	public boolean updateRecords() {
		DefaultTableModel tableModel = (DefaultTableModel)panel.tableSchema.getModel();
		int count = tableModel.getRowCount();
		List<Record> recs = schema.getRecords();
		List<Record> backupRecs = new ArrayList<Record>(recs);
		panel.getRecords(schema.getRecords(), dbTypeRecords);
		recs.clear();
		HashSet<String> names = new HashSet<String>();
		HashSet<String> duplicates = new HashSet<String>();
		for (int i = 0; i< count; i++) {
			String fieldName = tableModel.getValueAt(i, 1).toString();
			
			if (names.contains(fieldName)) {
				duplicates.add(fieldName);
			} else {
				names.add(fieldName);
			}
			DBTypeRecord dbtRec = DataConversionAlgorithm.bestRecordLookup(ApplicationData.getTempDataBase().getDataBaseInfo().getAvailableTypes(), (ERecordType)tableModel.getValueAt(i, 2), Long.parseLong(tableModel.getValueAt(i, 3).toString()));
			long length;
			try {
				length = Integer.parseInt(tableModel.getValueAt(i, 3).toString());
			} catch(NumberFormatException e) {
				length = dbtRec.getLength();
			}
			
			boolean pk = Boolean.parseBoolean(tableModel.getValueAt(i, 0).toString());
			
			Record rec = new Record(dbtRec, fieldName, length, true, true, pk);
			
			recs.add(rec);
		}
		if (duplicates.size() > 0) {
			String fieldDuplicate = "";
			for (String fd : duplicates) {
				fieldDuplicate += fd +"\n";
			}
			String message = "Next duplicated field names were detected in your input:\n" + 
								fieldDuplicate +
								"Please consider other names to avoid duplicates."; 

			JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME, message);
			schema.updateRecords(backupRecs);
			return false;
		}
		return true;
	}
}
