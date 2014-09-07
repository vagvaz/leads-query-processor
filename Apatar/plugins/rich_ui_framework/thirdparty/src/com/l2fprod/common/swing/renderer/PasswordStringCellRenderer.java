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

package com.l2fprod.common.swing.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.SystemColor;

import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;

import com.apatar.core.PasswordString;
import com.l2fprod.common.model.DefaultObjectRenderer;
import com.l2fprod.common.model.ObjectRenderer;


/**
 *  PasswordStringCellRenderer
 */

public class PasswordStringCellRenderer extends DefaultTableCellRenderer
										implements ListCellRenderer {

	private static final long serialVersionUID = 1L;

	private ObjectRenderer objectRenderer = new DefaultObjectRenderer();

	private Color oddBackgroundColor = SystemColor.window;
	private Color evenBackgroundColor = SystemColor.window;
	private boolean showOddAndEvenRows = true;
	
	public void setOddBackgroundColor(Color c) {
	  oddBackgroundColor = c;
	}
	
	public void setEvenBackgroundColor(Color c) {
	  evenBackgroundColor = c;
	}
	
	public void setShowOddAndEvenRows(boolean b) {
	  showOddAndEvenRows = b;
	}
	
	public Component getListCellRendererComponent(JList list, Object value,
	  int index, boolean isSelected, boolean cellHasFocus) {
	  	
	  setBorder(null);
	  
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		
    setValue(value);
    
		return this;
	}
	
	public Component getTableCellRendererComponent(
		JTable table,
		Object value,
		boolean isSelected,
		boolean hasFocus,
		int row,
		int column) {
		super.getTableCellRendererComponent(
			table,
			value,
			isSelected,
			hasFocus,
			row,
			column);

		if (showOddAndEvenRows && !isSelected) {
			if (row % 2 == 0) {
				setBackground(oddBackgroundColor);
			} else {
				setBackground(evenBackgroundColor);
			}
		}
		
    setValue(value);
    
		return this;
	}

  public void setValue(Object value) {
	String text		= ((PasswordString)value).getValue();
	String stars	= "";
	Icon icon		= convertToIcon(value);
	
	if (text == null) {
		setText("");
		return;
	}

	for(int i=0; i<text.length(); i++)
		stars += "*";
	    
	setText( text == null ? "" : stars);
	setIcon(icon);
  }
  
  protected String convertToString(Object value) {
    return objectRenderer.getText(value);    
  }
  
  protected Icon convertToIcon(Object value) {
	return null;
  }
  
}