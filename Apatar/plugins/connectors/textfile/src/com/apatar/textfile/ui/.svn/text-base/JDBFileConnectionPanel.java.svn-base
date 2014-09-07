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

package com.apatar.textfile.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

import com.apatar.core.Record;
import com.apatar.ui.ComponentBuilder;
import com.apatar.ui.JDefaultContextMenu;
import com.apatar.ui.ApatarUiMain;

public class JDBFileConnectionPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private GridBagConstraints gridBagConst;
	private GridBagConstraints gridBagConstChooseRadio;
	private GridBagConstraints gridBagConstChoose;
	private GridBagConstraints gridBagConstChooseFolder;
	private GridBagConstraints gridBagConstNewFileName;
	private GridBagConstraints gridBagConstCreateField;
	private GridBagConstraints gridBagConstDeleteField;
	

	private GridBagConstraints gridBagConstSeparator;
	
	private GridBagLayout gridBag;
	private GridBagLayout gridBagChoose;
	private GridBagLayout gridBagChooserRadio;
	private GridBagLayout gridBagChooseFolder;
	private GridBagLayout gridBagNewFileName;
	private GridBagLayout gridBagCreateField;
	private GridBagLayout gridBagDeleteField;
	private GridBagLayout gridBagSeparator;
	
	
	private JPanel panelChooserRadio;
	private JPanel panelChooseFile;
	private JPanel panelChooseFolder;
	private JPanel panelNewFileName;
	private JPanel panelCreateField;
	private JPanel panelDeleteField;
	private JPanel panelSeparator;
	

	private JLabel labelFile;
	private JLabel labelChooseFolder;
	private JLabel labelNewFileName;
	private JLabel labelNewField;
	private JLabel labelSeparator;
	private JLabel labelDeleteField;
	
	private static JTextField fieldFileName;
	private static JTextField fieldChooseFolder;
	private static JTextField fieldNewFileName;
	private static JTextField fieldNewFieldName;
	
	
	private JButton openFileButton;
	private JButton chooseFolderButton;
	private JButton addFieldButton;
	private JButton buttonDeleteField;

	private JRadioButton boxHasFile;
	private JRadioButton boxNewFile;
	
	private static DefaultTableModel tblModel;
	private static JTable fieldsTable;

	private JComboBox comboBoxSeparator;
	private static JComboBox comboBoxTypeFile;
	
	private static String pathToFile = ""; 
	//private static String typeOfFile = "";
	
	private static fileFilter FF = new fileFilter();
	private static folderFilter FolderF = new folderFilter();
	private final static JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));

	private String commaStr = "Comma (,)"; 
	private String semicomStr = "Semicolon (;)"; 
	private String barStr = "Bar (|)"; 
	private String tabStr = "Tab (->)"; 

	
	
	private static ActionListener buttonFileMouseListener = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
			fileChooser.setFileFilter(FF);
			
			int returnValue = fileChooser.showOpenDialog(ApatarUiMain.MAIN_FRAME);
			
			if( returnValue == JFileChooser.APPROVE_OPTION ) {
				File fileSrc = fileChooser.getSelectedFile();
				
				String fileName = "";
				pathToFile = "";
				
				String strArr[] = fileSrc.toString().split("\\\\");
				fileName		= strArr[strArr.length-1];
				String typeOfFile = fileName.substring(fileName.length()-4, fileName.length());
				
				setTypeOfNewFile(typeOfFile);
				
				for(int i=0; i<strArr.length-1; i++)
					pathToFile += strArr[i] + "\\";
				
				fileName = fileName.substring(0, fileName.length()-4);
				
				fieldFileName.setText( fileName );
			}
		}
	};
	
	private static ActionListener buttonFolderMouseListener = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
			fileChooser.setFileFilter(FolderF);
			
			int returnValue = fileChooser.showOpenDialog(ApatarUiMain.MAIN_FRAME);
			if( returnValue == JFileChooser.APPROVE_OPTION )
				fieldChooseFolder.setText( fileChooser.getSelectedFile().getAbsolutePath() );
		}
	};
	
	private static ActionListener addFieldMouseListener = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			String nameNewField = fieldNewFieldName.getText();
			Object contentTable[];
			
			if( !nameNewField.equals("") ){
				contentTable = new Object[2];
				contentTable[0] = nameNewField;
				contentTable[1] = "text";
				
				tblModel.addRow(contentTable);
				fieldNewFieldName.setText("");
			}
			
		}
	};
	
	private static ActionListener deleteFieldMouseListener = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			for(int i=fieldsTable.getSelectedRows().length-1; i>-1; i--)
				tblModel.removeRow( fieldsTable.getSelectedRows()[i] );
		}
	};
	
	
	@SuppressWarnings("serial")
	private class CellEditor extends DefaultCellEditor{

		public CellEditor(JTextField arg0) {
			super(arg0);
		}
		
		public Component getTableCellEditorComponent(JTable table, Object obj, boolean arg2, int row, int cell) {
			return null; 
		}
	}

	
	public JDBFileConnectionPanel() {
		super();
		this.gridBag	  = new GridBagLayout();
		setLayout(gridBag);
		
		this.boxHasFile = new JRadioButton("Choose file", true);
		this.boxNewFile = new JRadioButton("Create a new file", false);
		
		createListenersRadio();
		content();
	}

	private void createListenersRadio() {
		this.boxHasFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boxNewFile.setSelected(false);
				
				fieldFileName.setEnabled( true );
				fieldChooseFolder.setEnabled( false );
				fieldNewFileName.setEnabled( false );
				fieldNewFieldName.setEnabled( false );
				
				openFileButton.setEnabled( true );
				chooseFolderButton.setEnabled( false );
				addFieldButton.setEnabled( false );
				
				buttonDeleteField.setEnabled( false );
				comboBoxTypeFile.setEnabled( false );
				fieldsTable.setEnabled( false );
				
				openFileButton.addActionListener( buttonFileMouseListener );
				chooseFolderButton.removeActionListener( buttonFolderMouseListener );
			}
		});
		
		this.boxNewFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boxHasFile.setSelected(false);
				
				fieldFileName.setEnabled( false );
				fieldChooseFolder.setEnabled( true );
				fieldNewFileName.setEnabled( true );
				fieldNewFieldName.setEnabled( true );
				
				openFileButton.setEnabled( false );
				chooseFolderButton.setEnabled( true );
				addFieldButton.setEnabled( true );
				
				buttonDeleteField.setEnabled( true );
				comboBoxTypeFile.setEnabled( true );
				fieldsTable.setEnabled( true );
				
				openFileButton.removeActionListener( buttonFileMouseListener );
				chooseFolderButton.addActionListener( buttonFolderMouseListener );
			}
		});
	
	}
	
	public void content() {
//		---
		this.gridBagConst = new GridBagConstraints();
		
		this.gridBagChooserRadio	 = new GridBagLayout();
		this.gridBagConstChooseRadio = new GridBagConstraints();
		
		this.gridBagChoose		= new GridBagLayout();
		this.gridBagConstChoose = new GridBagConstraints();
		
		this.gridBagChooseFolder		= new GridBagLayout();
		this.gridBagConstChooseFolder	= new GridBagConstraints();
		
		this.gridBagNewFileName		 = new GridBagLayout();
		this.gridBagConstNewFileName = new GridBagConstraints();
		
		this.gridBagCreateField		 = new GridBagLayout();
		this.gridBagConstCreateField = new GridBagConstraints();
		
		//this.gridBagTable	   = new GridBagLayout();
		//this.gridBagConstTable = new GridBagConstraints();
		this.gridBagDeleteField			= new GridBagLayout();
		this.gridBagConstDeleteField	= new GridBagConstraints();
		
		
		this.gridBagSeparator	   = new GridBagLayout();
		this.gridBagConstSeparator = new GridBagConstraints();
		
		// ----------
		
		this.panelChooserRadio = new JPanel();
		this.panelChooserRadio.setLayout(gridBagChooserRadio);
		
		this.panelChooseFile = new JPanel();
		this.panelChooseFile.setLayout(gridBagChoose);

		this.panelChooseFolder = new JPanel();
		this.panelChooseFolder.setLayout(this.gridBagChooseFolder);
		
		this.panelNewFileName = new JPanel();
		this.panelNewFileName.setLayout(gridBagNewFileName);
		
		this.panelCreateField = new JPanel();
		this.panelCreateField.setLayout(gridBagCreateField);
		
		this.panelDeleteField = new JPanel(gridBagDeleteField);
		
		this.panelSeparator = new JPanel(gridBagSeparator);
		
		// ---------- 1
		
		gridBagConstChooseRadio.insets	  = new Insets(5, 5, 5, 5);
		gridBagConstChooseRadio.gridwidth = GridBagConstraints.REMAINDER;
		gridBagConstChooseRadio.weightx   = 1.0;  
		gridBagConstChooseRadio.fill	  = GridBagConstraints.HORIZONTAL;
		gridBagConstChooseRadio.anchor	  = GridBagConstraints.EAST;
		gridBagConstChoose.fill	= GridBagConstraints.HORIZONTAL;
		ComponentBuilder.makeComponent(boxHasFile,gridBagChooserRadio,gridBagConstChooseRadio,panelChooserRadio);
		ComponentBuilder.makeComponent(boxNewFile,gridBagChooserRadio,gridBagConstChooseRadio,panelChooserRadio);
		
		// ---------- 2
		
		gridBagConstChoose.insets	  = new Insets(5, 5, 5, 5);
		gridBagConstChoose.anchor	  = GridBagConstraints.EAST;
		gridBagConstChooseRadio.weightx   = 0.0; 
		gridBagConstChooseRadio.gridwidth = 1;
		labelFile = new JLabel("Text File Name:");
		ComponentBuilder.makeComponent(labelFile ,gridBagChoose,gridBagConstChoose,panelChooseFile);
		
		//gridBagConstChoose.ipadx   = 120;
		gridBagConstChoose.fill	   = GridBagConstraints.HORIZONTAL;
		gridBagConstChoose.anchor  = GridBagConstraints.EAST;
		gridBagConstChoose.weightx = 2.0;
		fieldFileName = new JTextField("");
		fieldFileName.setComponentPopupMenu(
				new JDefaultContextMenu(fieldFileName) );
		ComponentBuilder.makeComponent(fieldFileName ,gridBagChoose,gridBagConstChoose,panelChooseFile);
		
		gridBagConstChoose.ipadx  = 0;
		gridBagConstChoose.anchor = GridBagConstraints.WEST;
		gridBagConstChoose.weightx = 0.0;
		gridBagConstChoose.fill	  = GridBagConstraints.NONE;
		openFileButton = new JButton("Choose");
		ComponentBuilder.makeComponent(openFileButton ,gridBagChoose,gridBagConstChoose,panelChooseFile);
		
		openFileButton.addActionListener( buttonFileMouseListener ); 

		// ---------- 3
		
		gridBagConstChooseFolder.insets	 = new Insets(5, 5, 5, 5);
		
		gridBagConstChooseFolder.anchor	 = GridBagConstraints.EAST;
		labelChooseFolder = new JLabel("Folder Name:");
		ComponentBuilder.makeComponent(labelChooseFolder ,gridBagChooseFolder,gridBagConstChooseFolder,panelChooseFolder);
		
		gridBagConstChooseFolder.ipadx   = 120;
		gridBagConstChooseFolder.fill	 = GridBagConstraints.HORIZONTAL;
		gridBagConstChooseFolder.anchor  = GridBagConstraints.EAST;
		gridBagConstChooseFolder.weightx = 2.0;
		fieldChooseFolder = new JTextField("");
		fieldChooseFolder.setComponentPopupMenu(
				new JDefaultContextMenu(fieldChooseFolder) );
		fieldChooseFolder.setEnabled( false );
		ComponentBuilder.makeComponent(fieldChooseFolder,gridBagChooseFolder,gridBagConstChooseFolder,panelChooseFolder);

		gridBagConstChooseFolder.ipadx  = 0;
		gridBagConstChooseFolder.anchor = GridBagConstraints.WEST;
		gridBagConstChooseFolder.fill	  = GridBagConstraints.NONE;
		gridBagConstChooseFolder.weightx = 0.0;
		chooseFolderButton = new JButton("Choose Folder");
		chooseFolderButton.setEnabled( false );
		//chooseFolderButton.addActionListener( buttonFolderMouseListener );
		
		ComponentBuilder.makeComponent(chooseFolderButton,gridBagChooseFolder,gridBagConstChooseFolder,panelChooseFolder);

		// ---------- 4
		
		gridBagConstNewFileName.insets	 = new Insets(5, 5, 5, 5);
		gridBagConstNewFileName.weightx = 0.0;
		gridBagConstNewFileName.anchor	 = GridBagConstraints.EAST;
		labelNewFileName = new JLabel("New File Name:");
		ComponentBuilder.makeComponent(labelNewFileName, gridBagNewFileName,gridBagConstNewFileName,panelNewFileName);
		
		gridBagConstNewFileName.ipadx   = 120;
		gridBagConstNewFileName.fill     = GridBagConstraints.HORIZONTAL;
		gridBagConstNewFileName.anchor  = GridBagConstraints.EAST;
		gridBagConstNewFileName.weightx = 2.0;
		fieldNewFileName = new JTextField("");
		fieldNewFileName.setComponentPopupMenu(
				new JDefaultContextMenu(fieldNewFileName) );
		fieldNewFileName.setEnabled( false );
		ComponentBuilder.makeComponent(fieldNewFileName, gridBagNewFileName,gridBagConstNewFileName,panelNewFileName);

		gridBagConstNewFileName.ipadx  = 0;
		gridBagConstNewFileName.anchor = GridBagConstraints.WEST;
		gridBagConstNewFileName.fill	= GridBagConstraints.NONE;
		gridBagConstNewFileName.weightx = 0.0;
		comboBoxTypeFile = new JComboBox();
			comboBoxTypeFile.addItem(".txt"); 
			comboBoxTypeFile.addItem(".csv");
		comboBoxTypeFile.setEnabled( false );
		comboBoxTypeFile.setMinimumSize( new Dimension(50, 19) );
		comboBoxTypeFile.setMaximumSize( new Dimension(50, 19) );
		
		ComponentBuilder.makeComponent(comboBoxTypeFile, gridBagNewFileName,gridBagConstNewFileName,panelNewFileName);
		
		// ---------- 5
		
		gridBagConstCreateField.insets	 = new Insets(5, 5, 5, 5);
		gridBagConstCreateField.weightx = 0.0;
		gridBagConstCreateField.gridwidth = 1;
		gridBagConstCreateField.anchor	 = GridBagConstraints.EAST;
		labelNewField = new JLabel("New Fields In The Text File:");
		ComponentBuilder.makeComponent(labelNewField ,gridBagCreateField,gridBagConstCreateField,panelCreateField);
		
		//gridBagConstCreateField.ipadx   = 120;
		gridBagConstCreateField.fill	= GridBagConstraints.HORIZONTAL;
		gridBagConstCreateField.anchor  = GridBagConstraints.EAST;
		gridBagConstCreateField.fill	= GridBagConstraints.HORIZONTAL;
		gridBagConstCreateField.weightx = 2.0;
		fieldNewFieldName = new JTextField("");
		fieldNewFieldName.setComponentPopupMenu(
				new JDefaultContextMenu(fieldNewFieldName) );
		fieldNewFieldName.setEnabled( false );
		ComponentBuilder.makeComponent(fieldNewFieldName,gridBagCreateField,gridBagConstCreateField,panelCreateField);

		gridBagConstCreateField.ipadx  = 0;
		gridBagConstCreateField.anchor = GridBagConstraints.WEST;
		gridBagConstCreateField.fill	  = GridBagConstraints.NONE;
		gridBagConstCreateField.weightx = 0.0;
		addFieldButton = new JButton("Add field");
		addFieldButton.addActionListener( addFieldMouseListener );
		addFieldButton.setEnabled( false );
		ComponentBuilder.makeComponent(addFieldButton,gridBagCreateField,gridBagConstCreateField,panelCreateField);

		// ---------- 6
		
		tblModel = new DefaultTableModel();
		tblModel.addColumn("Field Name");
		tblModel.addColumn("Type");
		
		fieldsTable = new JTable(tblModel);
		fieldsTable.setEnabled( false );
		
		fieldsTable.setOpaque(false);
		fieldsTable.setBackground(null);
		fieldsTable.setBorder(null);
		
		fieldsTable.setShowGrid(false);
			
		fieldsTable.getColumn("Field Name").setCellEditor( new CellEditor(new JTextField()) );
		
		fieldsTable.getColumn("Type").setMaxWidth(150);
		fieldsTable.getColumn("Type").setMinWidth(150);
		fieldsTable.getColumn("Type").setPreferredWidth(150);
		fieldsTable.getColumn("Type").setCellEditor( new CellEditor(new JTextField()) );
		
		fieldsTable.setComponentPopupMenu(
				new JDefaultContextMenu(fieldsTable) );
		
		JScrollPane srollPane = new JScrollPane(fieldsTable);
		srollPane.setBorder(null);
		// ---------- 7
		
		gridBagConstDeleteField.insets = new Insets(5, 5, 5, 5);
		
		gridBagConstDeleteField.anchor = GridBagConstraints.EAST;
		labelDeleteField = new JLabel("Delete selected field:");
		ComponentBuilder.makeComponent(labelDeleteField ,gridBagDeleteField,
				gridBagConstDeleteField,panelDeleteField);
		
		gridBagConstDeleteField.anchor = GridBagConstraints.EAST;
		buttonDeleteField = new JButton("Delete");
		buttonDeleteField.setEnabled( false );
		buttonDeleteField.addActionListener( deleteFieldMouseListener );
		
		
		ComponentBuilder.makeComponent(buttonDeleteField, gridBagDeleteField,
				gridBagConstDeleteField,panelDeleteField);
		
		gridBagConstDeleteField.anchor  = GridBagConstraints.EAST;
		gridBagConstDeleteField.fill	  = GridBagConstraints.HORIZONTAL;
		gridBagConstDeleteField.anchor  = GridBagConstraints.EAST;
		gridBagConstDeleteField.weightx = 1.0;
		JLabel empty = new JLabel();
		ComponentBuilder.makeComponent(empty,gridBagDeleteField,
				gridBagConstDeleteField,panelDeleteField);
		
		// ---------- 8
		
		gridBagConstSeparator.insets = new Insets(5, 5, 5, 5);
		
		gridBagConstSeparator.anchor = GridBagConstraints.EAST;
		labelSeparator = new JLabel("Choose Separator:");
		ComponentBuilder.makeComponent(labelSeparator ,gridBagSeparator,gridBagConstSeparator,panelSeparator);
		
		gridBagConstSeparator.anchor = GridBagConstraints.EAST;
			comboBoxSeparator = new JComboBox();
				comboBoxSeparator.addItem(commaStr); 
				comboBoxSeparator.addItem(semicomStr);
				comboBoxSeparator.addItem(barStr);
				comboBoxSeparator.addItem(tabStr);
			comboBoxSeparator.setMinimumSize( new Dimension(100, 19) );
			comboBoxSeparator.setMaximumSize( new Dimension(100, 19) );
				
				
		ComponentBuilder.makeComponent(comboBoxSeparator, gridBagSeparator,gridBagConstSeparator,panelSeparator);
		
		gridBagConstSeparator.anchor  = GridBagConstraints.EAST;
		gridBagConstSeparator.fill	  = GridBagConstraints.HORIZONTAL;
		gridBagConstSeparator.anchor  = GridBagConstraints.EAST;
		gridBagConstSeparator.weightx = 1.0;
		JLabel emptySpace = new JLabel();
		ComponentBuilder.makeComponent(emptySpace,gridBagSeparator,gridBagConstSeparator,panelSeparator);
		
		
		// ---------------------------------------------------
		// -------- panels
		gridBagConst.gridwidth = GridBagConstraints.REMAINDER;
		gridBagConst.weightx   = 1.0;  
		gridBagConst.fill	   = GridBagConstraints.HORIZONTAL;
		gridBagConst.anchor	   = GridBagConstraints.EAST;
		
		ComponentBuilder.makeComponent(panelChooserRadio,	gridBag, gridBagConst, this);
		ComponentBuilder.makeComponent(panelChooseFile,		gridBag, gridBagConst, this);
		ComponentBuilder.makeComponent(panelChooseFolder,	gridBag, gridBagConst, this);
		ComponentBuilder.makeComponent(panelNewFileName,	gridBag, gridBagConst, this);
		ComponentBuilder.makeComponent(panelCreateField,	gridBag, gridBagConst, this);
		gridBagConst.fill	   = GridBagConstraints.BOTH;
		gridBagConst.weighty   = 10.0;
		ComponentBuilder.makeComponent(srollPane,			gridBag, gridBagConst, this);
		gridBagConst.fill	   = GridBagConstraints.HORIZONTAL;
		gridBagConst.weighty   = 0.0;
		ComponentBuilder.makeComponent(panelDeleteField,	gridBag, gridBagConst, this);
		ComponentBuilder.makeComponent(panelSeparator,		gridBag, gridBagConst, this);
		
		// -------- label for other space 
		
		gridBagConst.weighty = 1.0;
		ComponentBuilder.makeComponent(new JLabel(),gridBag,gridBagConst,this);
	}
	
	public boolean hasFile(){
		return boxHasFile.isSelected();
	}
	
	public String getFileName() {
		return fieldFileName.getText();
	}
	
	public void setFileName(String name){
		fieldFileName.setText( name );
	}
	
	public String getPathToFile(){
		return pathToFile;
	}
	
	public void setPathToFile(String path){
		pathToFile = path;
	}
	
	public String getTypeOfFile(){
		return getTypeOfNewFile();
	}
	
	public void setTypeOfFile(String type) {
		setTypeOfNewFile(type);
	}
	
	public String getNewFileName() {
		return fieldNewFileName.getText();
	}
	
	public void setNewFileName(String name){
		fieldNewFileName.setText(name);
	}
	
	public String getPathToNewFile(){
		return fieldChooseFolder.getText();
	}
	
	public void setPathToNewFile(String path){
		fieldChooseFolder.setText(path);
	}
	
	public String getTypeOfNewFile(){
		return (String)JDBFileConnectionPanel.comboBoxTypeFile.getSelectedItem();
	}
	
	public static void setTypeOfNewFile(String type){
		int number		= 0;
		String tmpType	= "";
		
		for(int i=0; i<comboBoxTypeFile.getItemCount(); i++ ){
			tmpType = comboBoxTypeFile.getItemAt( i ).toString();
			
			if( tmpType.equals( type ) ){
				number = i;
				break;
			}
		}

		comboBoxTypeFile.setSelectedIndex( number );
	}
	
	public String getSeparator(){
		String sep = (String)this.comboBoxSeparator.getSelectedItem();
		
		if( sep.equals(commaStr) )
			sep = ",";
		else if( sep.equals(semicomStr) )
			sep = ";";
		else if( sep.equals(barStr) )
			sep = "|";
		else if ( sep.equals(tabStr) )
			sep = "\t";

		return sep;
	}
	
	public void setSeparator(String separator){
		separator = separator.toLowerCase();
		
		if ( ",".equals( separator ) ){
			this.comboBoxSeparator.setSelectedItem( commaStr );
		} else if( ";".equals( separator ) ){
			this.comboBoxSeparator.setSelectedItem( semicomStr );
		} else if( "|".equals( separator ) ){
			this.comboBoxSeparator.setSelectedItem( barStr );
		} else if( "\t".equals( separator ) ){
			this.comboBoxSeparator.setSelectedItem(tabStr);
		}
		
		/*for(int i=0; i<this.comboBoxSeparator.getItemCount(); i++ ){
			tmpSep = this.comboBoxSeparator.getItemAt( i ).toString();
			tmpSep = tmpSep.toLowerCase();
			
			if( tmpSep.indexOf( separator ) != -1 ){
				number = i;
				break;
			}
		}*/
	}
	
	public List<String> getDbFields(){
		List<String> fields = new ArrayList<String>(); 
		
		for(int i=0; i<fieldsTable.getRowCount(); i++ ){
			String name = (String) fieldsTable.getValueAt( i, 0 );
			fields.add( name );
		}
		
		return fields;
	}
	
	public void setDbFields(List<Record> fields){
		Object contentTable[];
		
		for(int i=fieldsTable.getRowCount()-1; i>-1; i--)
			tblModel.removeRow( i );
		
		for( Record rec : fields ){
			
			contentTable = new Object[2];
			contentTable[0] = (String) rec.getFieldName();
			contentTable[1] = "text";
			
			tblModel.addRow(contentTable);
		}
	}
}

class fileFilter extends FileFilter{
	public boolean accept(File f) {
		/*if( f.isDirectory() )
			return false;
		else{*/
			String file = f.getName();
			if( f.isDirectory() || file.endsWith(".csv") || file.endsWith(".CSV") || file.endsWith(".txt") || file.endsWith(".TXT") )
				return true;
			else
				return false;
		//}
	}

	public String getDescription() {
		return (".xml, .XML, .csv, .CSV, .txt, .TXT");
	}
}
class folderFilter extends FileFilter{
	public boolean accept(File f) {
		if( f.isDirectory() )
			return true;
		else
			return false;
	}

	public String getDescription() {
		return ("");
	}
}