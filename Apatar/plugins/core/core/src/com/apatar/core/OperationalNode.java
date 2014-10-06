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
import java.util.List;

import javax.swing.ImageIcon;

public abstract class OperationalNode extends AbstractNode {

	protected boolean readLastNodeData = false;

	public OperationalNode() {
		width = 60;
		height = 60;
	}

	// this method is called before any transformation to make sure that
	// all the required internal tables exist
	public void BeforeExecute() {
		for (Object name : outputConnectionList.values()) {
			try {
				ApplicationData.tempDataBase
						.EnsureTableExists((TableInfo) name);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public abstract TableInfo getDebugTableInfo();

	// Merge all the destination schemas into the Output Schemas
	public void MergeSchemas(String connectionPointName) {
		ConnectionPoint cpt = getConnPoint(connectionPointName);
		TableInfo oTI = outputConnectionList.get(cpt);
		if (oTI == null) {
			return;
		}
		List<AbstractNode> nextNodes = AbstractNode
				.getNextNodes(getConnPoint(connectionPointName));
		List<SchemaTable> schemas = new ArrayList<SchemaTable>();
		for (AbstractNode node : nextNodes) {
			AbstractNode nextNode = node;
			schemas.add(nextNode.getExpectedShemaTable());
		}
		schemas.add(oTI.getSchemaTable());
		oTI.setSchemaTable(AbstractNode.mergeSchemaTable(schemas));
		oTI.getSchemaTable().updateRecords(
				ApplicationData.convertToTempDbType(oTI.getSchemaTable()
						.getRecords()));
	}

	public void beforeEdit() {

	}

	public void afterEdit(boolean editResult, AbstractApatarActions actions) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.AbstractNode#canSynchronyze()
	 */
	@Override
	public boolean canSynchronyze() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.AbstractNode#getIcon()
	 */
	@Override
	public ImageIcon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.Node#realEdit(com.apatar.core.AbstractApatarActions)
	 */
	public boolean realEdit(AbstractApatarActions actions) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.ITransformer#Transform()
	 */
	public void Transform() throws SocketException {
		// TODO Auto-generated method stub

	}

}
