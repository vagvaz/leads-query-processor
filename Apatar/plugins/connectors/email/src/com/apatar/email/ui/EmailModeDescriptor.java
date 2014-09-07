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


package com.apatar.email.ui;

import javax.swing.JOptionPane;

import com.apatar.email.EmailNode;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class EmailModeDescriptor extends WizardPanelDescriptor {

	public static final String IDENTIFIER = "EMAIL_MODE_PANEL";

	private JEmailSettingPanel panel = new JEmailSettingPanel(); 
	
	Object backDescriptor;
	Object nextDescriptor;
	
	private EmailNode node = null; 
	
	public EmailModeDescriptor(EmailNode node, Object backDescriptor, Object nextDescriptor) {
		super();
		this.node = node;
		setPanelDescriptorIdentifier(IDENTIFIER);
		setPanelComponent(panel);
		this.backDescriptor = backDescriptor;
		this.nextDescriptor = nextDescriptor;
	}

	public Object getNextPanelDescriptor() {
        return nextDescriptor;
    }
    
    public Object getBackPanelDescriptor() {
    	return backDescriptor;
    }
    
    public void aboutToDisplayPanel() {
    	getWizard().setTitleComment("");
		getWizard().setAdditionalComment("");
		
		panel.setDeleteAll( node.isDeleteAllInRDB() );
		panel.setCountAttachedFiles( node.getCountAttachedFiles() );
    }
    
    public void displayingPanel() {
    }
    
    public int aboutToHidePanel(String actionCommand) {
    	if (actionCommand.equals(Wizard.NEXT_BUTTON_ACTION_COMMAND)) {
    		node.setDeleteAllInRDB(	panel.isDeleteAll() );
    		try {
    			node.setCountAttachedFiles( panel.getCountAttachedFiles() )  ;
    		} catch(NumberFormatException e) {
    			JOptionPane.showMessageDialog(panel, "The field should contain a number");
    			panel.setCountAttachedFiles(node.getCountAttachedFiles());
    			return LEAVE_CURRENT_PANEL;
    		}
    	}
    	
    	return CHANGE_PANEL;
    }
	
}

