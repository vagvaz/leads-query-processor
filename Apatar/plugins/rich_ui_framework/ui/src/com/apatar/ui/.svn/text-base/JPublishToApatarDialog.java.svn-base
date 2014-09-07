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
 


package com.apatar.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.apatar.ui.MouseHyperLinkEvent;
import com.apatar.ui.wizard.JCommentPanel;


public class JPublishToApatarDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String apatarforgeUrl = "http://apatarforge.loc/";
	
	public static int OK_OPTION = 1;
	public static int CANCEL_OPTION = 0;
	
	int option = CANCEL_OPTION;
	
	JButton bOk = new JButton("Ok");
	JButton bCancel = new JButton("Cancel");
	JButton browse = new JButton("Browse");
	
	static DefaultTableModel tblModelFrom	= null;
	static JTable tagsTableFrom				= null;
	static DefaultTableModel tblModelTo		= null;
	static JTable tagsTableTo				= null;
	
	static JTextField textfieldAddNewTag			= null;
	
	JCheckBox selectFromFile	= new JCheckBox("Select DataMap from a file");
	JTextField nameFile			= new JTextField();
	JComboBox location;
	JTextField dataMapName			= new JTextField();
	JTextArea dataMapDescription	= new JTextArea();
	JCheckBox autoGenerateShortDescription = new JCheckBox("Autogenerate Short Desc", true);
	
	JTextArea shortDescription		= new JTextArea();
	
	JTextField username		= new JTextField();
	JPasswordField password = new JPasswordField();
	JLabel forgotPassLinkLabel = new JLabel();
	
	@SuppressWarnings("serial")
	private class CellEditor extends DefaultCellEditor{

		public CellEditor(JTextField arg0) {
			super(arg0);
		}
		
		public Component getTableCellEditorComponent(JTable table, Object obj, boolean arg2, int row, int cell) {
			return null; 
		}
	}
	
	private static ActionListener addTagsMouseListener = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			
			String tagName = "";
			Object contentTable[];

			List<String> existTags = new ArrayList<String>();
			
			for(int i=0; i<tblModelTo.getRowCount(); i++)
				existTags.add( (String) tagsTableTo.getValueAt(i, 0 ) );
			
			for(int i=tagsTableFrom.getSelectedRows().length-1; i>-1; i--){
				tagName = (String) tagsTableFrom.getValueAt(
							tagsTableFrom.getSelectedRows()[i], 0 );
				
				if( !tagName.equals("") && !existTags.contains(tagName) ){
					contentTable = new Object[1];
					contentTable[0] = tagName;
					
					tblModelTo.addRow(contentTable);
				}
			}
			
			tagsTableFrom.clearSelection();
		}
	};
	
	private static ActionListener addNewTagMouseListener = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			
			String tagName = textfieldAddNewTag.getText();
			Object contentTable[];
			
			List<String> existTags = new ArrayList<String>();
			
			for(int i=0; i<tblModelTo.getRowCount(); i++)
				existTags.add( (String) tagsTableTo.getValueAt(i, 0 ) );
			
			if( !tagName.equals("") && !existTags.contains(tagName)  ){
				contentTable = new Object[1];
				contentTable[0] = tagName;
				
				tblModelTo.addRow(contentTable);
			}
			
			textfieldAddNewTag.setText("");
		}
	};
	
	private static ActionListener deleteTagMouseListener = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			for(int i=tagsTableTo.getSelectedRows().length-1; i>-1; i--)
				tblModelTo.removeRow( tagsTableTo.getSelectedRows()[i] );
		}
	};
	
	public JPublishToApatarDialog(Frame arg0) throws HeadlessException {
		super(arg0);
		setSize( 500, 600);
		setModal(true);
		setTitle("Publish to Apatar");
		
		try {
			location = new JComboBox( getDataMapLocations() );
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		
		createDialog();
		createListeners();
	}
	
	private void createDialog() {
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		this.setLayout(gridbag);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		//c.insets = new Insets(5, 5, 5, 5);
		
		JPanel panelLogin = new JPanel();
		panelLogin.setBorder( new EmptyBorder(10, 10, 0, 10) );
		panelLogin.setLayout(new BoxLayout(panelLogin, BoxLayout.X_AXIS));
		panelLogin.add(new JLabel("User Name"));
		panelLogin.add(Box.createHorizontalStrut(5));
		panelLogin.add(username);
		username.setComponentPopupMenu(
				new JDefaultContextMenu(username) );
		panelLogin.add(Box.createHorizontalStrut(5));
		panelLogin.add(new JLabel("Password"));
		panelLogin.add(Box.createHorizontalStrut(5));
		panelLogin.add(password);

		JPanel panelForgotPassLink = new JPanel();
		panelForgotPassLink.setBorder( new EmptyBorder(10, 10, 0, 10) );
		panelForgotPassLink.setLayout(new BoxLayout(panelForgotPassLink, BoxLayout.X_AXIS));
		panelForgotPassLink.add(new JLabel("Lost your password? "));
		panelForgotPassLink.add(Box.createHorizontalStrut(5));
		panelForgotPassLink.add(forgotPassLinkLabel);
		
		forgotPassLinkLabel.setFont( UiUtils.NORMAL_SIZE_12_FONT );
		forgotPassLinkLabel.addMouseListener( new MouseHyperLinkEvent() );
		forgotPassLinkLabel.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
		forgotPassLinkLabel.setText("<html><a href='http://www.apatarforge.org/profile/lostpassword.html'>Click here to retrieve it</a></html>");

		
		JPanel panelFile = new JPanel();
		panelFile.setBorder( new EmptyBorder(10, 10, 0, 10) );
		panelFile.setLayout(new BoxLayout(panelFile, BoxLayout.X_AXIS));
		panelFile.add(selectFromFile);
		panelFile.add(Box.createHorizontalStrut(5));
		panelFile.add(new JLabel("New File "));
		panelFile.add(Box.createHorizontalStrut(5));
		panelFile.add(nameFile);
		panelFile.add(Box.createHorizontalStrut(5));
		panelFile.add(browse);
		nameFile.setEnabled(false);
		nameFile.setComponentPopupMenu( new JDefaultContextMenu(nameFile) );
		browse.setEnabled(false);
		
		JPanel panelLocation = new JPanel();
		panelLocation.setBorder( new EmptyBorder(10, 10, 0, 10) );
		panelLocation.setLayout(new BoxLayout(panelLocation, BoxLayout.X_AXIS));
		panelLocation.add(new JLabel("Suggest Location:"));
		panelLocation.add(Box.createHorizontalStrut(5));
		panelLocation.add(location);
		
		JPanel panelDMName = new JPanel();
		panelDMName.setBorder( new EmptyBorder(10, 10, 0, 10) );
		panelDMName.setLayout(new BoxLayout(panelDMName, BoxLayout.X_AXIS));
		JLabel datamapNameLabel = new JLabel("DataMap Name:");
		datamapNameLabel.setBorder( new EmptyBorder(0, 0, 0, 10 ) );
		panelDMName.add(datamapNameLabel);
		panelDMName.add(Box.createHorizontalStrut(5));
		panelDMName.add(dataMapName);
		dataMapName.setComponentPopupMenu(
				new JDefaultContextMenu(dataMapName) );
		
		
		JPanel panelDMDescription = new JPanel();
		panelDMDescription.setBorder( new EmptyBorder(10, 10, 0, 10) );
		panelDMDescription.setLayout(new BoxLayout(panelDMDescription, BoxLayout.X_AXIS));
		panelDMDescription.add(new JLabel("DataMap Description (16000 Chars)"));
		panelDMDescription.add(Box.createHorizontalStrut(5));
		//JScrollPane scroll = new JScrollPane(dataMapDescription);
		//scroll.setSize(300, 50);
		dataMapDescription.setLineWrap(true);
		dataMapDescription.setWrapStyleWord(true);
		panelDMDescription.add(new JScrollPane(dataMapDescription));
		
		JPanel panelDMShortDescription = new JPanel();
		panelDMShortDescription.setBorder( new EmptyBorder(10, 10, 0, 10) );
		panelDMShortDescription.setLayout(new BoxLayout(panelDMShortDescription, BoxLayout.X_AXIS));
		JLabel labelShort = new JLabel("Short Description:");
		labelShort.setBorder( new EmptyBorder(0, 0, 0, 85) );
		panelDMShortDescription.add( labelShort );
		panelDMShortDescription.add(Box.createHorizontalStrut(5));
		shortDescription.setLineWrap(true);
		shortDescription.setWrapStyleWord(true);
		panelDMShortDescription.add(new JScrollPane(shortDescription));
		setEnableShortDescription(false);
		
		// -------------
		
		JPanel panelTitleForTags = new JPanel();
		panelTitleForTags.setBorder( new EmptyBorder(10, 10, 0, 10) );
		panelTitleForTags.setLayout(new BoxLayout(panelTitleForTags, BoxLayout.X_AXIS));
		panelTitleForTags.add(new JLabel("Add tags associated with your DataMap:"));
		
		// -------------
		
		JPanel panelListAddedTags = new JPanel();
		panelListAddedTags.setBorder( new EmptyBorder(0, 10, 10, 10) );
		panelListAddedTags.setLayout(new BoxLayout(panelListAddedTags, BoxLayout.X_AXIS));
		
		// --
		JPanel panelFrom = new JPanel();
		panelFrom.setLayout(new BoxLayout(panelFrom, BoxLayout.Y_AXIS));
		JPanel panelMiddle = new JPanel();
		panelMiddle.setLayout(new BoxLayout(panelMiddle, BoxLayout.X_AXIS));
		JPanel panelTo = new JPanel();
		panelTo.setLayout(new BoxLayout(panelTo, BoxLayout.Y_AXIS));
		
		// --
		JPanel panelAddNewTag = new JPanel();
		panelAddNewTag.setLayout(new BoxLayout(panelAddNewTag, BoxLayout.X_AXIS));
		panelAddNewTag.add(new JLabel("Add new tag:"));
		panelAddNewTag.add(Box.createHorizontalStrut(5));
		textfieldAddNewTag = new JTextField();
		textfieldAddNewTag.setComponentPopupMenu(
				new JDefaultContextMenu(textfieldAddNewTag) );
		panelAddNewTag.add(textfieldAddNewTag);
		panelAddNewTag.add(Box.createHorizontalStrut(5));
		JButton buttonAddTag = new JButton("Add");
		buttonAddTag.addActionListener( addNewTagMouseListener );		
		panelAddNewTag.add( buttonAddTag );
		
		// --
		JPanel panelDeleteTags = new JPanel();
		panelDeleteTags.setLayout(new BoxLayout(panelDeleteTags, BoxLayout.X_AXIS));
		
		panelDeleteTags.add(new JLabel("Delete selected tag(s):"));
		panelDeleteTags.add(Box.createHorizontalStrut(5));
		JButton buttonDeleteTag = new JButton("Delete");
		buttonDeleteTag.addActionListener( deleteTagMouseListener );
		panelDeleteTags.add( buttonDeleteTag );
		panelDeleteTags.add( new JPanel() );
		
		// --
		
		JButton moveTag = new JButton(UiUtils.ARROW_ICON); 
		moveTag.addActionListener( addTagsMouseListener );
		panelMiddle.add( moveTag );
		
		
		tblModelFrom	= new DefaultTableModel();
		tblModelTo		= new DefaultTableModel();
		tblModelFrom.addColumn("Tag Name");
		tblModelTo.addColumn("Tag Name");
		
		tagsTableFrom	= new JTable(tblModelFrom);
		tagsTableTo		= new JTable(tblModelTo);
		
		tagsTableFrom.setOpaque(false);
		tagsTableFrom.setBackground(null);
		tagsTableFrom.setBorder(null);
		tagsTableFrom.setShowGrid(false);
		
		tagsTableTo.setOpaque(false);
		tagsTableTo.setBackground(null);
		tagsTableTo.setBorder(null);
		tagsTableTo.setShowGrid(false);
		
		tagsTableFrom.getColumn("Tag Name").setCellEditor( new CellEditor(new JTextField()) );
		tagsTableTo.getColumn("Tag Name").setCellEditor( new CellEditor(new JTextField()) );
				
		fillTableTags();
		
		tagsTableFrom.setComponentPopupMenu(
				new JDefaultContextMenu(tagsTableFrom) );
		tagsTableTo.setComponentPopupMenu(
				new JDefaultContextMenu(tagsTableTo) );
		
		JScrollPane srollPane = new JScrollPane(tagsTableFrom);
		srollPane.setBorder(null);
		
		JScrollPane srollPane2 = new JScrollPane(tagsTableTo);
		srollPane.setBorder(null);
		
		
		
		panelFrom.add( srollPane );
		panelFrom.add( Box.createVerticalStrut(5) );
		panelFrom.add( panelAddNewTag );
		
		panelTo.add(srollPane2);
		panelTo.add(Box.createVerticalStrut(5));
		panelTo.add(panelDeleteTags);
		
		
		panelListAddedTags.add( panelFrom );
		panelListAddedTags.add( Box.createHorizontalStrut(5) );
		panelListAddedTags.add( panelMiddle );
		panelListAddedTags.add( Box.createHorizontalStrut(5) );
		panelListAddedTags.add( panelTo );
		
		
		// -------
		
		
		JPanel panelButton = new JPanel();
		panelButton.setLayout(new BoxLayout(panelButton, BoxLayout.X_AXIS));
		panelButton.add(Box.createHorizontalGlue());
		panelButton.add(bOk);
		panelButton.add(Box.createHorizontalStrut(5));
		panelButton.add(bCancel);
		panelButton.add(Box.createHorizontalStrut(5));
		
		
		ComponentBuilder.makeComponent(new JCommentPanel(), gridbag, c, getContentPane());
				
		ComponentBuilder.makeComponent(panelLogin, gridbag, c, getContentPane());
		ComponentBuilder.makeComponent(panelForgotPassLink, gridbag, c, getContentPane());
		ComponentBuilder.makeComponent(panelFile, gridbag, c, getContentPane());
		ComponentBuilder.makeComponent(panelLocation, gridbag, c, getContentPane());
		ComponentBuilder.makeComponent(panelDMName, gridbag, c, getContentPane());
		
		c.fill = GridBagConstraints.BOTH;
		
		c.weighty = 2.0;
		ComponentBuilder.makeComponent(panelDMDescription, gridbag, c, getContentPane());
		
		c.weighty = 0.0;
		ComponentBuilder.makeComponent(autoGenerateShortDescription, gridbag, c, getContentPane());
		
		c.weighty = 1.0;
		ComponentBuilder.makeComponent(panelDMShortDescription, gridbag, c, getContentPane());
		
		c.weighty = 1.0;
		ComponentBuilder.makeComponent(panelTitleForTags, gridbag, c, getContentPane());
		
		c.weighty = 3.0;
		ComponentBuilder.makeComponent(panelListAddedTags, gridbag, c, getContentPane());
		
		c.weighty = 0.0;
		ComponentBuilder.makeComponent(new JSeparator(), gridbag, c, getContentPane());
		
		c.weighty = 1.0;
		ComponentBuilder.makeComponent(panelButton, gridbag, c, getContentPane());
	}
	
	public int getOption() {
		return option;
	}
	
	private void fillTableTags(){
		Object contentTable[];
		PablishObject tag = null;
		
		try {
			for( Iterator<PablishObject> it=getDataMapTags().iterator(); it.hasNext(); ){
				tag = it.next();
				if( !tag.getName().equals("") ){
					contentTable = new Object[1];
					contentTable[0] = tag.getName();
					
					tblModelFrom.addRow(contentTable);
				}
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		}
	}
	
	private void setEnableShortDescription(boolean enable){
		shortDescription.setEnabled( enable );
		
		if( enable )
			shortDescription.setBackground( Color.WHITE );
		else
			shortDescription.setBackground( Color.LIGHT_GRAY );
	}
	
	private void createListeners() {
		browse.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
				fileChooser.setMultiSelectionEnabled(false);
				
				int returnValue = fileChooser.showOpenDialog(getContentPane());
				if( returnValue == JFileChooser.APPROVE_OPTION ) {
					File fileSrc = fileChooser.getSelectedFile();
					nameFile.setText(fileSrc.getPath());
				}
			}
			
		});
		
		bCancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				option = CANCEL_OPTION;
				setVisible(false);
			}
			
		});
		
		bOk.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				option = OK_OPTION;
				setVisible(false);
			}
			
		});
		
		selectFromFile.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (selectFromFile.isSelected()) {
					nameFile.setEnabled(true);
					browse.setEnabled(true);
				} else {
					nameFile.setEnabled(false);
					browse.setEnabled(false);
				}
			}
		});
		
		autoGenerateShortDescription.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (autoGenerateShortDescription.isSelected()) 
					setEnableShortDescription( false );
				else
					setEnableShortDescription( true );
			}
		});
	}

	public String getDataMapDescription() {
		return dataMapDescription.getText();
	}
	public String getDataMapName() {
		return dataMapName.getText();
	}
	public String getFilePath() {
		return nameFile.getText();
	}
	public String getShortDescription() {
		if (autoGenerateShortDescription.isSelected()) {
			String descr = dataMapDescription.getText();
			if (descr.length() < 1000)
				return descr;
			return descr.substring(0, 1000);
		}
		else
			return shortDescription.getText();
	}
	public String getTags() {
		String returnText = "";
		
		for(int i=0; i<tagsTableTo.getRowCount(); i++ ){
			String name = (String) tagsTableTo.getValueAt( i, 0 );
			returnText += name + ", "; 
		}
		
		if( 2 < returnText.length() )
			returnText = returnText.substring(0, returnText.length()-2);
		
		return returnText;
	}
	public String getUserName() {
		return username.getText();
	}
	public String getPassword() {
		return new String(password.getPassword());
	}
	public PablishObject getDataMapLocation() {
		return (PablishObject)location.getSelectedItem();
	}
	public boolean isSelectFromFile() {
		return selectFromFile.isSelected();
	}

	Vector<PablishObject> getDataMapLocations() throws HttpException, IOException, JDOMException {
		Vector<PablishObject> result = new Vector<PablishObject>();
		PostMethod method = new PostMethod(apatarforgeUrl+"index.php");
		
		Part[] parts = new Part[4];
		parts[0] = new StringPart("option", "com_remository");
		parts[1] = new StringPart("func", "select");
		parts[2] = new StringPart("get", "category");
		parts[3] = new StringPart("no_html", "1");
		
		method.setRequestEntity(new MultipartRequestEntity(parts, method.getParams()));
		
		HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
        int status = client.executeMethod(method);
        if (status == HttpStatus.SC_OK) {
        	InputStream stream = method.getResponseBodyAsStream();
        	Document document = new Document();
        	SAXBuilder builder = new SAXBuilder();
        	document = builder.build(stream);
        	for (Object obj : document.getRootElement().getChildren("category")) {
        		Element elem = (Element)obj;
        		int id = Integer.parseInt(elem.getAttributeValue("id"));
        		result.add(new PablishObject(id, elem.getChildText("name")));
        	}
        }
		
		return result;
	}
	
	Vector<PablishObject> getDataMapTags() throws HttpException, IOException, JDOMException {
		Vector<PablishObject> result = new Vector<PablishObject>();
		PostMethod method = new PostMethod(apatarforgeUrl+"index.php");
		
		Part[] parts = new Part[4];
		parts[0] = new StringPart("option", "com_remository");
		parts[1] = new StringPart("func", "select");
		parts[2] = new StringPart("get", "tag");
		parts[3] = new StringPart("no_html", "1");
		
		method.setRequestEntity(new MultipartRequestEntity(parts, method.getParams()));
		
		HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
        int status = client.executeMethod(method);
        if (status == HttpStatus.SC_OK) {
        	InputStream stream = method.getResponseBodyAsStream();
        	Document document = new Document();
        	SAXBuilder builder = new SAXBuilder();
        	document = builder.build(stream);
        	for (Object obj : document.getRootElement().getChildren("tag")) {
        		Element elem = (Element)obj;
        		int id = Integer.parseInt(elem.getAttributeValue("id"));
        		result.add(new PablishObject(id, elem.getChildText("name")));
        	}
        }
		
		return result;
	}
	
	public static class PablishObject {
		int id;
		String name;
		
		public PablishObject(int id, String name) {
			super();
			this.id = id;
			this.name = name;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}
		
		public String toString() {
			return name;
		}
		
	}
}

