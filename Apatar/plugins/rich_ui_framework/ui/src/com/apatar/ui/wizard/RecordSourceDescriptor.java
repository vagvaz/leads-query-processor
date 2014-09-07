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
 


package com.apatar.ui.wizard;

import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.apatar.core.AbstractDataBaseNode;
import com.apatar.core.LogUtils;
import com.apatar.core.RDBTable;
import com.apatar.ui.ApatarUiMain;

public class RecordSourceDescriptor extends WizardPanelDescriptor {
	public static final String IDENTIFIER = "RECORDSOURCE_PANEL";
	
	protected AbstractDataBaseNode node;
	protected JRecordSourcePanel panel = new JRecordSourcePanel();
	Object backDescriptor;
	Object nextDescriptor;
	
	public RecordSourceDescriptor(AbstractDataBaseNode node, Object backDescriptor, Object nextDescriptor) {
		super();
		
		setPanelDescriptorIdentifier(IDENTIFIER);
		setPanelComponent(panel);
		this.node = node;
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
    	panel.clear();
    	try {
    		
    		for (RDBTable rtt : node.getTableList())
				panel.addTableName(rtt);
			
			panel.setSelectedValue(node.getTable());
			
    	} catch (IOException e) {
    		JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME, LogUtils.GetExceptionMessage(e));
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME, LogUtils.GetExceptionMessage(e));
		} catch (Exception e) {
			e.printStackTrace();
		}
		getWizard().setTitleComment("Record Source");
		getWizard().setAdditionalComment("Provides information on which records should be returned");
    }

    public void displayingPanel() {

    }

    public int aboutToHidePanel(String actionCommand) {
    	if (actionCommand.equals(Wizard.NEXT_BUTTON_ACTION_COMMAND)) {
    		try {
    			RDBTable table = (RDBTable)panel.getSelectedValue();
    			if (table == null) {
    				JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME, "Please, select table name");
    				return LEAVE_CURRENT_PANEL;
    			}
    			
    			if (!table.isSupport()) {
    				JOptionPane.showMessageDialog(getWizard().getDialog(), "Apatar is unable to get fields list of the selected table. Please select another table to proceed.");
    				return LEAVE_CURRENT_PANEL;
    			}
    			node.setTable(table);
    			
	    	} catch(Exception e) {
	    		JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME, e.getMessage());
	    		return LEAVE_CURRENT_PANEL;
	    	}
    	}
    	return CHANGE_PANEL;
    }
}

