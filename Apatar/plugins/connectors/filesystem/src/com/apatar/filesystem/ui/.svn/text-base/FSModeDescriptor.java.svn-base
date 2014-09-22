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

package com.apatar.filesystem.ui;

import com.apatar.filesystem.FileSystemNode;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class FSModeDescriptor extends WizardPanelDescriptor {
	
	public static final String IDENTIFIER = "FSMODE_PANEL";
	
	JFSModePanel panel = new JFSModePanel();
	Object backDescriptor;
	Object nextDescriptor;
	FileSystemNode node;
	
	public FSModeDescriptor(FileSystemNode node, Object backDescriptor, Object nextDescriptor) {
		super();
		setPanelDescriptorIdentifier(IDENTIFIER);
		setPanelComponent(panel);
		this.backDescriptor = backDescriptor;
		this.nextDescriptor = nextDescriptor;
		this.node = node;
	}
	
	public Object getNextPanelDescriptor() {
        return nextDescriptor;
    }
    
    public Object getBackPanelDescriptor() {
    	return backDescriptor;
    }
    
    public void aboutToDisplayPanel() {
		panel.setDeleteAll(node.isDeleteAllInRDB());
		panel.setMode( node.getMode() );
		getWizard().setTitleComment("");
		getWizard().setAdditionalComment("");
    }

    public void displayingPanel() {

    }

    public int aboutToHidePanel(String actionCommand) {
    	if (actionCommand.equals(Wizard.NEXT_BUTTON_ACTION_COMMAND)) {
    		node.setMode(panel.getMode());
    		node.setDeleteAllInRDB(panel.isDeleteAll());
    	}
    	return CHANGE_PANEL;
    }
}
