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
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

import com.apatar.core.ApplicationData;
import com.apatar.core.CoreUtils;

public class JSubmitHelpDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	JTextArea text = new JTextArea();
	JButton sendButton = new JButton("Send");
	JButton cancel = new JButton("Cancel");
	
	JTextField firstNameField = new JTextField();
	JTextField lastNameField = new JTextField();
	JTextField emailField = new JTextField();
	
	public JSubmitHelpDialog() throws HeadlessException {
		super();
		setTitle(ApplicationData.VERSION + " - Submit Bug");
		createDialog();
		addListeners();
	}

	public JSubmitHelpDialog(Dialog arg0, boolean arg1) throws HeadlessException {
		super(arg0, arg1);
		setTitle(ApplicationData.VERSION + " - Submit Bug");
		createDialog();
		addListeners();
	}

	public JSubmitHelpDialog(Dialog arg0, String arg1, boolean arg2) throws HeadlessException {
		super(arg0, arg1, arg2);
		setTitle(ApplicationData.VERSION + " - Submit Bug");
		createDialog();
		addListeners();
	}

	public JSubmitHelpDialog(Dialog arg0, String arg1) throws HeadlessException {
		super(arg0, arg1);
		setTitle(ApplicationData.VERSION + " - Submit Bug");
		createDialog();
		addListeners();
	}

	public JSubmitHelpDialog(Dialog arg0) throws HeadlessException {
		super(arg0);
		setTitle(ApplicationData.VERSION + " - Submit Bug");
		createDialog();
		addListeners();
	}

	public JSubmitHelpDialog(Frame arg0, boolean arg1) throws HeadlessException {
		super(arg0, arg1);
		setTitle(ApplicationData.VERSION + " - Submit Bug");
		createDialog();
		addListeners();
	}

	public JSubmitHelpDialog(Frame arg0, String arg1, boolean arg2) throws HeadlessException {
		super(arg0, arg1, arg2);
		setTitle(ApplicationData.VERSION + " - Submit Bug");
		createDialog();
		addListeners();
	}

	public JSubmitHelpDialog(Frame arg0, String arg1) throws HeadlessException {
		super(arg0, arg1);
		setTitle(ApplicationData.VERSION + " - Submit Bug");
		createDialog();
		addListeners();
	}

	public JSubmitHelpDialog(Frame arg0) throws HeadlessException {
		super(arg0);
		setTitle(ApplicationData.VERSION + " - Submit Bug");
		createDialog();
		addListeners();
	}

	private void createDialog() {
		setLayout( new BorderLayout(5,5) );
		
		setSize(400, 400);
		
		JPanel textPanel = new JPanel(new BorderLayout(5,5));
		textPanel.setBorder( new EmptyBorder(10, 5, 5, 5) );
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		text.setFont(UiUtils.NORMAL_SIZE_12_FONT);
		
		textPanel.add( new JLabel("If you found a bug, please submit it here:"),
				BorderLayout.NORTH );
		textPanel.add( new JScrollPane(text), BorderLayout.CENTER  );
		
		JPanel contactPanel = new JPanel();
		contactPanel.setLayout(new BoxLayout(contactPanel, BoxLayout.Y_AXIS));
		
		JPanel firstNamePanel = new JPanel();
		firstNamePanel.setLayout( new BoxLayout(firstNamePanel,
				BoxLayout.X_AXIS) );
		firstNamePanel.add( new JLabel("Your First Name:") );
		firstNamePanel.add(Box.createHorizontalStrut(5));
		firstNamePanel.add( firstNameField );
		
		
		JPanel lastNamePanel = new JPanel();
		lastNamePanel.setLayout( new BoxLayout(lastNamePanel,
				BoxLayout.X_AXIS) );
		lastNamePanel.add( new JLabel("Your Last Name:") );
		lastNamePanel.add(Box.createHorizontalStrut(5));
		lastNamePanel.add( lastNameField );
		
		JPanel emailPanel = new JPanel();
		emailPanel.setLayout( new BoxLayout(emailPanel,
				BoxLayout.X_AXIS) );
		emailPanel.add( new JLabel("Your E-mail:") );
		emailPanel.add(Box.createHorizontalStrut(28));
		emailPanel.add( emailField );
		
		contactPanel.add( firstNamePanel );
		contactPanel.add( Box.createVerticalStrut(5) );
		contactPanel.add( lastNamePanel );
		contactPanel.add( Box.createVerticalStrut(5) );
		contactPanel.add( emailPanel );
		
		textPanel.add( contactPanel, BorderLayout.SOUTH );
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(sendButton);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(cancel);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.setBorder( new EmptyBorder(0, 0, 5, 0) );
		
		getContentPane().add(textPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
	}
	
	private void addListeners() {
		sendButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				
				 PostMethod filePost = new PostMethod(getUrl());

                 try{
                	 
                 	if ( !CoreUtils.validEmail(emailField.getText()) ){
                 		JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
                 				"E-mail address is invalid! Please, write a valid e-mail.");
                 		return;
                 	} 
                	 
                	List<File> targetFiles = getAttachFile();
                	
                    List<FilePart> fParts = new ArrayList<FilePart>();
                    int i = 1;
                	for (File file : targetFiles) {
                		try {
                			fParts.add(new FilePart("file"+i, file));
                			i++;
                		}
                        catch (java.io.FileNotFoundException ef) {}
                    }
                	
                	int size = fParts.size()+4;
                	Part[] parts = new Part[size];
                	
                	parts[0] = new StringPart("BugInformation", text.getText());
                	parts[1] = new StringPart("FirstName", firstNameField.getText());
                	parts[2] = new StringPart("LastName", lastNameField.getText());
                	parts[3] = new StringPart("Email", emailField.getText());
                    	
                    i = 4;
                    for (FilePart fp : fParts)
                    	parts[i++] = fp;
                                        
                    filePost.setRequestEntity(
                         new MultipartRequestEntity(parts, filePost.getParams() ) );
                    
                    HttpClient client = new HttpClient();
                    client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
                    int status = client.executeMethod(filePost);
                    
                    if (status != HttpStatus.SC_OK)
                    	JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
                    			"Upload failed, response=" + HttpStatus.getStatusText(status));
                    else {
                    	JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
                    			"Your message has been sent. Thank you!");
                    	dispose();
                    }
                     
                 } catch (Exception ex) {
                     ex.printStackTrace();
                 } finally {
                     filePost.releaseConnection();
                 }
			}
		});
		cancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				
				 dispose();
			}
		});
	}

	protected List<File> getAttachFile() {
		List<File> files = new ArrayList<File>();
		File file = new File("error_output.txt");
		if (file.exists())
			files.add(file);
		file = new File("error_output2.txt");
		if (file.exists())
			files.add(file);
		return files;
	}

	protected String getSubject() {
		return "SubmitBug";
	}

	protected String getUrl() {
		return "http://apatar.com/postbugs";
	}
	
}
