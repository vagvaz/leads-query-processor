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
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;


public abstract class JHelpDialog extends JDialog {
	JTextArea text = new JTextArea();
	JButton sendButton = new JButton("Send");
	
	public JHelpDialog() throws HeadlessException {
		super();
		createDialog();
		addListeners();
	}

	public JHelpDialog(Dialog arg0, boolean arg1) throws HeadlessException {
		super(arg0, arg1);
		createDialog();
		addListeners();
	}

	public JHelpDialog(Dialog arg0, String arg1, boolean arg2) throws HeadlessException {
		super(arg0, arg1, arg2);
		createDialog();
		addListeners();
	}

	public JHelpDialog(Dialog arg0, String arg1) throws HeadlessException {
		super(arg0, arg1);
		createDialog();
		addListeners();
	}

	public JHelpDialog(Dialog arg0) throws HeadlessException {
		super(arg0);
		createDialog();
		addListeners();
	}

	public JHelpDialog(Frame arg0, boolean arg1) throws HeadlessException {
		super(arg0, arg1);
		createDialog();
		addListeners();
	}

	public JHelpDialog(Frame arg0, String arg1, boolean arg2) throws HeadlessException {
		super(arg0, arg1, arg2);
		createDialog();
		addListeners();
	}

	public JHelpDialog(Frame arg0, String arg1) throws HeadlessException {
		super(arg0, arg1);
		createDialog();
		addListeners();
	}

	public JHelpDialog(Frame arg0) throws HeadlessException {
		super(arg0);
		createDialog();
		addListeners();
	}

	protected abstract List<File> getAttachFiles();
	protected abstract String getSubject();
	protected abstract String getUrl();
	
	private void createDialog() {
		setLayout(new BorderLayout(5,5));
		setSize(500,500);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(sendButton);
		
		getContentPane().add(text, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
	}
	
	private void addListeners() {
		sendButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				
				 PostMethod filePost = new PostMethod(getUrl());

                // filePost.getParameters().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true);
                 try {
                	List<File> targetFiles = getAttachFiles();
                	Part[] parts;
                	if (targetFiles != null) {
                		parts = new Part[targetFiles.size()+1];
                		int i=1;
                        for (File targetFile : targetFiles) {
                        	parts[i++] = new FilePart("file" + i, targetFile);
                        };
                	}
                	else
                		parts = new Part[1];
                	
                    parts[0] = new StringPart("FeatureRequest", text.getText());
                    
                    filePost.setRequestEntity(
                         new MultipartRequestEntity(parts, filePost.getParams())
                    );
                    HttpClient client = new HttpClient();
                    client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
                    int status = client.executeMethod(filePost);
                    if (status != HttpStatus.SC_OK) {
                    	JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME, "Upload failed, response=" + HttpStatus.getStatusText(status));
                    }

                     
                 } catch (Exception ex) {
                     ex.printStackTrace();
                 } finally {
                     filePost.releaseConnection();
                 }


			}
			
		});
	}
	
}
