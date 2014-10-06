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

package com.l2fprod.common.beans.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import com.apatar.core.FolderPath;
import com.apatar.ui.JDefaultContextMenu;
import com.apatar.ui.ApatarUiMain;
import com.l2fprod.common.swing.LookAndFeelTweaks;

public class FolderPathPropertyEditor extends AbstractPropertyEditor {

	private FolderPath folder = null;
	
	protected JTextField textfield;
	private JButton button;

	public FolderPathPropertyEditor() {
		editor = new JPanel(new BorderLayout(0, 0));
		((JPanel)editor).add("Center", textfield = new JTextField());
		((JPanel)editor).add("East", button = new FixedButton());
		textfield.setBorder(LookAndFeelTweaks.EMPTY_BORDER);    
		textfield.setComponentPopupMenu( new JDefaultContextMenu(textfield) );
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectFolder();
			}
		});
	}
	
	public Object getValue() {
		if ( "".equals( textfield.getText().trim() ) )
			this.folder.setPath( "" );
		else
	    	this.folder.setPath( textfield.getText() );
		
    	return folder;
	}

	public void setValue(Object value) {
	    
		if (value instanceof FolderPath){
	    	this.folder = (FolderPath) value;
	    	textfield.setText( folder.getPath() );
		} else
	    	textfield.setText("");
		
	}
		
	protected void selectFolder() {
		
		FolderFilter FF				= new FolderFilter();
		JFileChooser fileChooser	= new JFileChooser(
				System.getProperty("user.dir"));
		
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		fileChooser.setFileFilter(FF);
		
		int returnValue = fileChooser.showOpenDialog(ApatarUiMain.MAIN_FRAME);
		if( returnValue == JFileChooser.APPROVE_OPTION )
			textfield.setText( fileChooser.getSelectedFile().getAbsolutePath() );
	}
	
	class FolderFilter extends FileFilter{
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
	
}
