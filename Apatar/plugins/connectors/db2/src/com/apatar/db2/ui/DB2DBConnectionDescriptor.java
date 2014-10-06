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
 


package com.apatar.db2.ui;

import propertysheet.JPropertySheetPage;

import com.apatar.core.AbstractDataBaseNode;
import com.apatar.core.ProjectData;
import com.apatar.ui.wizard.DBConnectionDescriptor;

public class DB2DBConnectionDescriptor extends DBConnectionDescriptor {
public static final String IDENTIFIER = "DBCONNECTION_PANEL";
	
	protected AbstractDataBaseNode node;
	protected JPropertySheetPage panel;
	protected Object nextPanelDescriptor;
	protected Class classType;
	protected String type;
	protected String subtype;
	protected ProjectData projectData;

	public DB2DBConnectionDescriptor(AbstractDataBaseNode node, JPropertySheetPage panel,
			Object nextPanelDescriptor, Class classType, String type, String subtype) {
		super(node, panel, nextPanelDescriptor, classType, type, subtype);
		this.node = node;
		this.panel = panel;
		this.nextPanelDescriptor = nextPanelDescriptor;
		this.classType = classType;
		this.type = type;
		this.subtype = subtype;
	}
}

