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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import com.apatar.core.ApplicationData;
import com.apatar.core.CoreUtils;

public class JErrorDebugDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public static final int DEBUG_DIALOG = 1;
	public static final int ERROR_DIALOG = 2;
	
	private JButton okButton	= new JButton("Close");
	private JButton clearButton = new JButton("Clear");
	private JTextArea textArea	= new JTextArea();
	private int witchDialog		= 0;
	
	private File file			= null;
	private String content		= "";
	
	public JErrorDebugDialog(Frame frm, int dialog){
		super(frm);
		witchDialog = dialog;
		createDialog();
	}
	
	public JErrorDebugDialog(Frame frm, boolean flag, int dialog){
		super(frm, flag);
		witchDialog = dialog;
		createDialog();
	}
	
	public JErrorDebugDialog(Dialog dlg, int dialog){
		super(dlg);
		witchDialog = dialog;
		createDialog();
	}
	
	public JErrorDebugDialog(Dialog dlg, boolean flag, int dialog){
		super(dlg, flag);
		witchDialog = dialog;
		createDialog();
	}
	
	private void createDialog(){
		setLayout(new BorderLayout(5,5));
		setSize(400, 400);
		setResizable(true);
		
		String pathPrj = (ApplicationData.REPOSITORIES == null ? "" : ApplicationData.REPOSITORIES);
		
		if( DEBUG_DIALOG == witchDialog ){
			setTitle(ApplicationData.VERSION + " - Debug Console");
			file = new File(pathPrj + "debug_output.txt");
		} else if( ERROR_DIALOG == witchDialog ){
			setTitle(ApplicationData.VERSION + " - Error Console");
			file = new File(pathPrj + "error_output.txt");
		} else {
			setTitle(ApplicationData.VERSION);
			file = new File("");
		}
		
		readFromFile();
		
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.X_AXIS));
		textPanel.setBorder( new EmptyBorder(10, 10, 10, 10) );
		
		textArea.setEditable(false);
		textArea.setOpaque(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setFont(UiUtils.NORMAL_SIZE_12_FONT);
		textArea.setText( content );
		textArea.setComponentPopupMenu( new JDefaultContextMenu(textArea) );
		
		JScrollPane scroll = new JScrollPane(textArea);
		//scroll.setBorder(null);
		
		textPanel.add( scroll );
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(clearButton);
		buttonPanel.add(Box.createHorizontalStrut(15));
		buttonPanel.add(okButton);
		buttonPanel.setBorder( new EmptyBorder(5, 5, 5, 5) );
		
		
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				 dispose();
			}
		});
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				 try {
					PrintStream printStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(file)), true);
					if(DEBUG_DIALOG == witchDialog) {
						System.setOut(null);
						file.createNewFile();
						System.setOut(printStream);
						CoreUtils.printInfoToConsol(CoreUtils.DEBUG_INFO);
					} else
						if(ERROR_DIALOG == witchDialog) {
							System.setErr(null);
							file.createNewFile();
							System.setErr(printStream);
							CoreUtils.printInfoToConsol(CoreUtils.ERROR_INFO);
						}
					readFromFile();
					textArea.setText(content);
					textArea.updateUI();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		getContentPane().add(textPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);		
	}
	
	private void readFromFile() {
		if( file.exists() ){
			content = "";
			try {
				FileInputStream in	= new FileInputStream(file);
				
				int count = 4*1024*1024;
				byte[] buff = new byte[count];
				
				if (in != null) {
					
					int total = count;
					
					long length = file.length();
					if (length > count)
						in.skip(length - count);
					
					while( total >= count ) {
						total = in.read(buff, 0, count);
						content += new String(buff, 0, total);
					}
				}
				
				in.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}

