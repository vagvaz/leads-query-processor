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

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.apatar.core.ConnectionPoint;

public class DndTransferData implements Transferable {

	public static final DataFlavor createNodeData = new DataFlavor(
			NodeFactory.class, "Create Node Data");
	
	public static final DataFlavor moveNodeData = new DataFlavor(
			MoveNodeData.class, "Move Node Data");
	
	public static final DataFlavor linkNodeData = new DataFlavor(
			ConnectionPoint.class, "Link Nodes Data");	
	
	public static class MoveNodeData {
		
		public ArrayList<MoveRecord> nodePanels = new ArrayList<MoveRecord>();
		public Point origin;
		
		public MoveNodeData(List<JNodePanel> nodePanel, Point origin) {
			for(Iterator it = nodePanel.iterator(); it.hasNext();)
				nodePanels.add(new MoveRecord((JNodePanel)it.next()));
			this.origin = origin;			
		}
	}

	protected DataFlavor flavor;
	protected Object data;

	private DndTransferData(DataFlavor flavor, Object data) {

		this.flavor = flavor;
		this.data = data;
	}

	public static DndTransferData createNodeData(NodeFactory nf) {

		return new DndTransferData(createNodeData, nf);
	}
	
	public static DndTransferData createMoveData(List<JNodePanel> nodePanel, Point origin) {

		return new DndTransferData(moveNodeData, new MoveNodeData(nodePanel, origin));
	}
	
	public static DndTransferData createLinkData(ConnectionPoint connPoint) {

		return new DndTransferData(linkNodeData, connPoint);
	}
	

	public DataFlavor[] getTransferDataFlavors() {

		DataFlavor[] res = new DataFlavor[] {flavor};
		return res;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {

		return this.flavor.equals(flavor);
	}

	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		
		if (this.flavor.equals(flavor)) {
			return data;
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}

}
