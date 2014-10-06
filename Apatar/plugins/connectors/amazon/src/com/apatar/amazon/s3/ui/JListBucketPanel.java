/*
_______________________

Apatar Open Source Data Integration

Copyright (C) 2005-2007, Apatar, Inc.

info@apatar.com

195 Meadow St., 2nd Floor

Chicopee, MA 01013

 

    This program is free software; you can redistribute it and/or modify

    it under the terms of the GNU General Public License as published by

    the Free Software Foundation; either version 2 of the License, or

    (at your option) any later version.

 

    This program is distributed in the hope that it will be useful,

    but WITHOUT ANY WARRANTY; without even the implied warranty of

    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the

    GNU General Public License for more details.

 

    You should have received a copy of the GNU General Public License along

    with this program; if not, write to the Free Software Foundation, Inc.,

    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

________________________

*/
 
package com.apatar.amazon.s3.ui;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.apatar.amazon.s3.Bucket;
import com.apatar.ui.JDefaultContextMenu;
import com.amazon.s3.AWSAuthConnection;

public class JListBucketPanel extends JPanel {
	
	private static String PREVIOUS_LEVEL = ".. (Up to Bucket)";
	
	DefaultTableModel tableModel = new DefaultTableModel(new Object[] {"Amazon S3 Browser"}, 0);
	
	Bucket bucket;
	
	JTable tableBuckets = new JTable(tableModel);
	
	AWSAuthConnection connection;
	
	ListBucketDescriptor descriptor;
	
	boolean bucketLevel = true;

	public JListBucketPanel(ListBucketDescriptor descriptor) {
		super();
		this.descriptor = descriptor;
		
		tableBuckets.addKeyListener( new TableKeyListener() );
		
		tableBuckets.getColumn("Amazon S3 Browser").setCellEditor(
				new CellTableEditor(new JTextField()) );
		
		tableBuckets.setOpaque(false);
		tableBuckets.setBackground(null);
		tableBuckets.setBorder(null);
		tableBuckets.setShowGrid(false);
		tableBuckets.setComponentPopupMenu(
				new JDefaultContextMenu(tableBuckets) );
		add(new JScrollPane(tableBuckets));
	}
	
	public void fillBuckets(List<Bucket> buckets) {
		tableModel.setRowCount(0);
		for (Bucket bucket : buckets) {
			tableModel.addRow(new Object[] {bucket});
		}
	}
	
	public List<String> getSelectedNameBuckets() {
		List<String> buckets = new ArrayList<String>();
		int[] selRow = tableBuckets.getSelectedRows();
		for (int i = 0; i < selRow.length; i++) {
			buckets.add(tableBuckets.getValueAt(selRow[i], 0).toString());
		}
		return buckets;
	}
	
	private void fillBucket(Bucket bucket) throws MalformedURLException, IOException {
		bucket.updateBucket(Bucket.getAllKeys(bucket.getName(), connection));
	}
	
	public void setConnection(AWSAuthConnection connection) {
		this.connection = connection;
	}
	
	private void makeTable(Object object) throws MalformedURLException, IOException {
		if (object instanceof Bucket) {
			Bucket bucket = (Bucket)object;
			this.bucket = bucket;
			fillBucket(bucket);
			List<String> keys = new ArrayList<String>();
			keys.add(PREVIOUS_LEVEL);
			keys.addAll(bucket.getObjectKeys());
			updateTable(keys);
			bucketLevel = false;
		}
		else {
			if (PREVIOUS_LEVEL.equals(object.toString()) && tableBuckets.getSelectedRow() == 0) {
				updateTable(descriptor.buckets);
				bucketLevel = true;
			}
		}
	}
	
	private void updateTable(List list) {
		tableModel.setRowCount(0);
		for (Object obj : list) {
			tableModel.addRow(new Object[]{obj});
		}
	}
	
	public List<Bucket> getSelectedBucket() throws MalformedURLException, IOException {
		List<Bucket> result = new ArrayList<Bucket>();
		int[] rows = tableBuckets.getSelectedRows();
		if (bucketLevel) {
			for (int i = 0; i < rows.length; i++) {
				Object obj = tableBuckets.getValueAt(rows[i], 0);
				if (obj instanceof Bucket) {
					Bucket bucket = (Bucket)obj;
					Bucket.getAllKeys(bucket.getName(), connection);
					result.add(bucket);
				}
			}
		} else {
			List<String> keys = new ArrayList<String>();
			for (int i = 0; i < rows.length; i++) {
				keys.add(tableBuckets.getValueAt(rows[i], 0).toString());
			}
			this.bucket.updateBucket(keys);
			result.add(this.bucket);
		}
		return result;
	}
	
	private class TableKeyListener implements KeyListener{

		public void keyTyped(KeyEvent arg0) {}

		public void keyPressed(KeyEvent e) {
			
			if( KeyEvent.VK_ENTER == e.getKeyCode() ) {
				int row = tableBuckets.getSelectedRow();
				Object obj = tableBuckets.getValueAt(row, 0);
				try {
					makeTable(obj);
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return;
			}
			
		}

		public void keyReleased(KeyEvent e) {
		}
	}
	
	private class CellTableEditor extends DefaultCellEditor{

		Component comp = null;
		
		public CellTableEditor(JTextField arg0) {
			super(arg0);
			comp = arg0;
		}
		
		public Component getTableCellEditorComponent(JTable table, Object obj, boolean arg2, int row, int cell) {
			try {
				makeTable(obj);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			
			return null; 
		}
	}

}

