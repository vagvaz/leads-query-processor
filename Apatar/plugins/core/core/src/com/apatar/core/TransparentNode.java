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

package com.apatar.core;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.ImageIcon;

public abstract class TransparentNode extends DataTransNode {
	
	public final void BeforeExecute() {
		setOutputSchemaTable();
		super.BeforeExecute();
	}

	public abstract TableInfo getDebugTableInfo();

	public void beforeEdit() {
		setOutputSchemaTable();
	}
	
	public abstract ImageIcon getIcon();

	public abstract void Transform() throws SocketException;
	
	protected void setOutputSchemaTable() {
		TableInfo iTI = getTiForConnection(inputConnectionList.get(0).getName());
		if (iTI == null)
			return;
		Collection<ConnectionPoint> outputs = getOutputConnPoints();
		for (Iterator<ConnectionPoint> it = outputs.iterator(); it.hasNext();) {
			TableInfo ti = getThisSideTableInfo(it.next());
			ArrayList<Record> recs = new ArrayList<Record>(iTI.getSchemaTable().getRecords());
			SchemaTable st = new SchemaTable(recs);
			if (ti != null)
				ti.setSchemaTable(st);
		}
	}

}
