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

package com.apatar.ftp.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.finj.FTPClient;
import org.finj.FTPException;
import org.finj.RemoteFile;

import com.apatar.ui.JDefaultContextMenu;

public class JFtpTreeModePanel extends JPanel {
	
	private static final long serialVersionUID = 2L;
	
	private JTable folderTable			= null;
	private DefaultTableModel tblModel	= null;
	
	private JLabel labelPath				= null;
	private final static String WORD_PATH	= "Path: ";
	
	protected FTPClient ftp		= null; 
	private String separator	= "/";
	private List<String> path	= new ArrayList<String>();
	String uri					= "";
	private boolean isPassive	= true;
	
	private class TableKeyListener implements KeyListener{

		public void keyTyped(KeyEvent arg0) {}

		public void keyPressed(KeyEvent e) {
			
			if( 10 == e.getKeyCode() ){
				int row = folderTable.getSelectedRow();
				
				String value = (String)folderTable.getValueAt(row, 0);
				makeFtpPath(value);
				
				return;
			}
			
		}

		public void keyReleased(KeyEvent e) {
			/*if( 10 == e.getKeyCode() ){
				int row = folderTable.getSelectedRow()-1;
				row = (row == -1) ? folderTable.getRowCount()-1 : row;
				int column = 0;
				
				String value = (String)folderTable.getValueAt(row, column);
				makeFtpPath(value);
				
				return;
			}*/
		}
	}
	
	@SuppressWarnings("serial")
	private class CellTableEditor extends DefaultCellEditor{

		Component comp = null;
		
		public CellTableEditor(JTextField arg0) {
			super(arg0);
			comp = arg0;
		}
		
		public Component getTableCellEditorComponent(JTable table, Object obj, boolean arg2, int row, int cell) {
			String name = String.valueOf( obj ); 
			
			makeFtpPath(name);

			return null; 
		}
	}
	
	public JFtpTreeModePanel(  ){
		super();
		createPanel();
		
		ftp = new FTPClient();
	}
	
	private void createPanel() {
		setLayout(new BorderLayout(5,5));
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		add(panel, BorderLayout.CENTER);
		
		panel.add(new JLabel("Choose folder on FTP server?"));
		
		labelPath = new JLabel(WORD_PATH);
		
		panel.add(labelPath);
		
		panel.add( getFoldersPanel() );
	}
	
	private JPanel getFoldersPanel(){
		JPanel panel = new JPanel();
		panel.setOpaque( false );
		
		JScrollPane srollPane = new JScrollPane(createFolderTable());
		srollPane.setPreferredSize(new Dimension(470, 320));
		srollPane.setBorder(null);
		
		panel.add(srollPane);
		
		return panel;
	}
	
	private JTable createFolderTable(){
		tblModel = new DefaultTableModel();
		tblModel.addColumn("Folder List");
		
		folderTable = new JTable(tblModel);
		folderTable.setOpaque(false);
		folderTable.setBackground(null);
		folderTable.setBorder(null);
		folderTable.setShowGrid(false);
		
		
		folderTable.addKeyListener( new TableKeyListener() );
		
		folderTable.getColumn("Folder List").setCellEditor(
				new CellTableEditor(new JTextField()) );
		
		folderTable.setComponentPopupMenu(
				new JDefaultContextMenu(folderTable) );
		
		return folderTable;
	}
	
	// -----------------
	
	private void removeRowsFromTable(){
		for(int i=folderTable.getRowCount()-1; i>-1; i--)
			tblModel.removeRow( i );
	}
	
	private void setRowsToTable(RemoteFile[] files){
		removeRowsFromTable();
		
		boolean isExist = false;
		
		for(int i=0; i<files.length; i++){
			if( "..".equals( files[i].getName()) ){
				isExist = true;
				break;
			}
		}
		
		Object contentTable[];
		
		if( !isExist ){
			contentTable = new Object[1];
			contentTable[0] = "..";
			tblModel.addRow(contentTable);
		}
		
		
		for(int i=0; i<files.length; i++){
			if( files[i].isFile() )
				continue;
			
			if( ".".equals( files[i].getName() ) )
				continue;
			
			contentTable = new Object[1];
			contentTable[0] = files[i].getName();
			tblModel.addRow(contentTable);
		}
	}
	
	private void setDataFromFtpToTable( List<String> path ){
		try {
			setRowsToTable( getFileDescriptors(path) );
		} catch (FTPException e) {
			e.printStackTrace();
		} 
	}
	
	private void createUri(List<String> path) {
		uri = "";
		for(Iterator<String> it=path.iterator(); it.hasNext();)
			uri += it.next();	
	}
	
	private RemoteFile[] getFileDescriptors(List<String> path) {
		if (path.size() == 0)
			return null;
		
		RemoteFile[] rf = null;
		
		boolean error = false;
		createUri(path);
		String firstUri = uri;
		while(true) {
			setValueToLabelPath(uri);
			try {
				rf = ftp.getFileDescriptors(uri, isPassive);
				if (error) {
					System.out.println("Ftp path:  Error getting folder " + firstUri + " Trying get " + uri + " folder");
					System.err.println("Ftp path:  Error getting folder " + firstUri + " Trying get " + uri + " folder");
				}
				return rf;
			} catch (FTPException e) {
				error = true;
				int size = path.size();
				if (size < 1)
					return null;
				path.remove(size - 1);
				path.remove(size - 2);
				createUri(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void makeFtpPath(String name){
		if( "..".equals( name ) ){
			if( 0 == path.size() )
				return;
			
			if( 1 < path.size() ){
				path.remove( path.size()-1 );
				path.remove( path.size()-1 );
			} else 
				return;
				
		} else {
			path.add( name );
			path.add( separator );
		}
		
		/*uri = "";
		for(Iterator<String> it=path.iterator(); it.hasNext(); )
			uri += it.next();
			
		setValueToLabelPath(uri);*/
		setDataFromFtpToTable( path );
	}
	
	private void setValueToLabelPath(String uri){
		labelPath.setText( WORD_PATH + uri );
	}
	
	// -----------------
	
	public void openFtpConnect(String host, int port, String login, 
			String password, boolean passive, String innerPath){
		try {
			isPassive = passive;
			
			if( ftp.isConnected() )
				return;
			
			ftp.open(	host,  port );
			ftp.login(	login, password.toCharArray()) ;
			ftp.isVerbose(false);
			
			String workingDir = ftp.getWorkingDirectory();
			
			path.clear();
			path.add(separator);
			//uri = "";
			
			String folders[] = null;
			
			if( separator.equals( innerPath ) )
				folders = workingDir.split(separator);
			else
				folders = innerPath.split(separator);
			
			for(int i=1; i<folders.length; i++){
				path.add( folders[i] );
				path.add( separator );
			}
			
			/*for(Iterator<String> it=path.iterator(); it.hasNext();)
				uri += it.next();			
			
			setValueToLabelPath(uri);*/
			
			setDataFromFtpToTable( path );

		} catch (FTPException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeFtpConnect(){
		try {
			if( ftp.isConnected() )
				ftp.close();
		} catch (FTPException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getFtpUri(){
		if( uri.length() != 0 ){
			if( !separator.equals( String.valueOf(uri.charAt(0)) ) )
				uri = separator + uri;
		}
		
		int row = folderTable.getSelectedRow();
		
		if( folderTable.getRowCount() < row )
			row = 0;
		
		if( 0 < row )
			uri += (String)folderTable.getValueAt(row, 0) + separator;
		
		return uri;
	}
}
