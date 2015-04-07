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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;

/**
 *  *  * @author mashko
 *	
 *	DefaultContextMenu - 
 */

public class JDefaultContextMenu extends JPopupMenu {

	private static final long serialVersionUID = 1L;

	public static final int COPY		= 1;
	public static final int CUT			= 2;
	public static final int DELETE		= 3;
	public static final int CLEARALL	= 4;
	
	private JComponent comp = null;
	
	/**
	 * @param  JComponent - component which has rightclick popup Menu 
	 */
	
	public JDefaultContextMenu(JComponent component){
		this.comp = component;
		addMenu();
	}
	
	/**
	 *   For different component ContextMenu includes different actions 
	 */
	public void addMenu(){
		
		add( new CopyMethod(this.comp) );
		
		if( this.comp instanceof JTextComponent ) {
			add( new CutMethod(this.comp) );
		}
		
		if( this.comp instanceof JTextComponent ) {
			add( new PasteMethod(this.comp) );
		}
		
		if( this.comp instanceof JTextComponent ) {
			add( new JSeparator() );
			add( new ClearAllMethod(this.comp) );
		}
		
	}
	
	/**
	 *   Inner class
	 *   Functional - copy in buffer selected text
	 */
	private class CopyMethod extends AbstractAction {
		
		private static final long serialVersionUID = 1L;
		
		JComponent comp = null;
		
		public CopyMethod(JComponent component){
			super("Copy");
			this.comp = component;
		}
	
		public void actionPerformed(ActionEvent evnt) {
			String text = "";
			
			if( this.comp instanceof JTextComponent )
				text = ((JTextComponent)comp).getSelectedText();
			else if( this.comp instanceof JLabel )
				text = ((JLabel)comp).getText();
			else if( this.comp instanceof JTable ){
				JTable tbl = ((JTable)comp);
				
				Object value = null;
				
				int rowsCount[] = tbl.getSelectedRows(); 
				
				//for(int row=rowsCount.length-1; row>-1; row-- ){
				for(int row=0; row<rowsCount.length; row++ ){
					
					for(int i=0; i<tbl.getColumnCount(); i++){
						value = tbl.getModel().getValueAt(
								rowsCount[row], i);
					
						text += ((value == null) ? "" : value.toString() ) + "\t";
					}
					
					text += "\n";
					
				}
				
				text = text.substring(0, text.length()-1);
			} else if( this.comp instanceof JList ){
				JList list = ((JList)comp);
				
				Object values[] = list.getSelectedValues();
				
				for(int i=0; i<values.length; i++  )
					text += ((values[i] == null)
							? "" : values[i].toString() ) + "\n";
				
				text = text.substring(0, text.length()-1);
			}

			if( null == text )
				text = "";
			
			StringSelection stsel  = new StringSelection(text);
			
	        Clipboard system = Toolkit.getDefaultToolkit().getSystemClipboard();
	        system.setContents(stsel,stsel);
		}

	}
	
	/**
	 *   Inner class
	 *   Functional - copy in buffer selected text and remove him from component
	 */
	private class CutMethod extends AbstractAction {

		private static final long serialVersionUID = 1L;

		JComponent comp = null;
		
		public CutMethod(JComponent component){
			super("Cut");
			this.comp = component;
		}
		
		public void actionPerformed(ActionEvent evnt) {
			if( this.comp instanceof JTextComponent)
				((JTextComponent)comp).cut();
		}

	}
	
	/**
	 *   Inner class
	 *   Functional - insert text in component from buffer
	 */
	private class PasteMethod extends AbstractAction {
		
		private static final long serialVersionUID = 1L;

		JComponent comp = null;
		
		public PasteMethod(JComponent component){
			super("Paste");
			this.comp = component;
		}
		
		public void actionPerformed(ActionEvent evnt) {
			if( this.comp instanceof JTextComponent)
				((JTextComponent)comp).paste();
		}
	
	}
	
	/**
	 *   Inner class
	 *   Functional - delete all text from component 
	 */
	private class ClearAllMethod extends AbstractAction {

		private static final long serialVersionUID = 1L;

		JComponent comp = null;
		
		public ClearAllMethod(JComponent component){
			super("ClearAll");
			this.comp = component;
		}
		
		public void actionPerformed(ActionEvent evnt) {
			if( this.comp instanceof JTextComponent)
				((JTextComponent)comp).setText("");
		}
	}
	
	
}

