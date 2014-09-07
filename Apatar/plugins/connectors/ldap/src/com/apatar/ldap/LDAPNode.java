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

package com.apatar.ldap;

import java.util.List;

import javax.swing.ImageIcon;

import propertysheet.JPropertySheetPage;

import com.apatar.core.AbstractJdbcDataBase;
import com.apatar.core.DBTypeRecord;
import com.apatar.core.DataBaseInfo;
import com.apatar.core.ERecordType;
import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.JdbcRecordSourceDescriptor;
import com.apatar.ui.wizard.TableModeDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class LDAPNode extends AbstractJdbcDataBase {
	
	static final DataBaseInfo dataBaseInfo = new DataBaseInfo("", "", "", "", true,
			true, true, true, false);
	
	public DataBaseInfo getDataBaseInfo() {
		return dataBaseInfo;
	}
	
	static {
		List<DBTypeRecord> rcList = dataBaseInfo.getAvailableTypes();
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "TIMESTAMP", 8, 8, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "VARCHAR", 0, 255, false, false));
	}
	
	public LDAPNode() {
		super();
		title = "Ldap";
		
		// this is read only node
		inputConnectionList.clear();
	}
	
	public ImageIcon getIcon() {
		return GeneralJdbcUtils.READ_ORACLE_NODE_ICON;
	}

	public void createDatabaseParam(Wizard wizard) {
		try {
	        WizardPanelDescriptor descriptor1 = new DBConnectionDescriptor(this,
	        	new JPropertySheetPage(wizard.getDialog()),
	        	JdbcRecordSourceDescriptor.IDENTIFIER,
	        	Class.forName("com.apatar.ldap.LDAPParams"), "db_connector", "ldap");
	        wizard.registerWizardPanel(DBConnectionDescriptor.IDENTIFIER, descriptor1);
	
	        WizardPanelDescriptor descriptor2 = new JdbcRecordSourceDescriptor(this, 
	        		DBConnectionDescriptor.IDENTIFIER, TableModeDescriptor.IDENTIFIER);
	        wizard.registerWizardPanel(JdbcRecordSourceDescriptor.IDENTIFIER, descriptor2);
	        
	        WizardPanelDescriptor descriptor3 = new TableModeDescriptor(this, JdbcRecordSourceDescriptor.IDENTIFIER, WizardPanelDescriptor.FINISH);
	        wizard.registerWizardPanel(TableModeDescriptor.IDENTIFIER, descriptor3);
	
	        wizard.setKeyForReferringToDescription("help.connector.ldap");
	        wizard.setCurrentPanel(DBConnectionDescriptor.IDENTIFIER, Wizard.NEXT_BUTTON_ACTION_COMMAND);
	        wizard.showModalDialog();
	        
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
