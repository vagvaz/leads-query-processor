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

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSource;
import java.text.Collator;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;


public class JShortcutTree extends DNDTree {
	private static final long serialVersionUID = 1L;

	private Map<String, DefaultMutableTreeNode> catTabs = new HashMap<String, DefaultMutableTreeNode>();

	DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();

	Comparator comparator = Collator.getInstance();

	public JShortcutTree(Collection<NodeFactory> nodes) {
		super();
		setModel(new DefaultTreeModel(rootNode));
		setCellRenderer(new ShortcutCellRenderer());

		if (nodes==null)
			return;

		for (Iterator<NodeFactory> it = nodes.iterator(); it.hasNext(); ) {
			NodeFactory nf = it.next();
			if (nf.MainPaneNode())
			{
				try {
					//System.out.println(nf.getClass().getName());
					for (Object obj : nf.getCategory()) {
						DefaultMutableTreeNode node = getCategoryTab(obj.toString());
						DefaultMutableTreeNode insert = new DefaultMutableTreeNode(nf);
						node.insert(insert, findInsertionPoint(node, insert));
						//node.add(new DefaultMutableTreeNode(nf));
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		expandAll();
		setRootVisible(false);
		setRowHeight(getRowHeight()+3);
	}

	private DefaultMutableTreeNode getCategoryTab(String cat) {

		DefaultMutableTreeNode node = catTabs.get(cat);
		if (null == node) {
			node = new DefaultMutableTreeNode(cat);

			rootNode.insert(node, findInsertionPoint(rootNode, node));
			catTabs.put(cat, node);
		}

		return node;
	}

	public void dragGestureRecognized(DragGestureEvent dge) {
		DefaultMutableTreeNode selnode=null;
		Object selected = getSelectionPath();
		if(selected != null) {
			TreePath selpath=(TreePath)selected;
			selnode=(DefaultMutableTreeNode)selpath.getLastPathComponent();
			if ( selected != null ) {
	            Transferable transfer=dragDropStart();
	            if (transfer==null)
	            	transfer = new StringSelection(selected.toString());
	            NodeFactory nf = (NodeFactory)selnode.getUserObject();
	            dge.startDrag(DragSource.DefaultMoveDrop, DndTransferData.createNodeData(nf));
			}
		}
	}

	public void expandAll() {
	    int row = 0;
	    while (row < getRowCount()) {
	      expandRow(row);
	      row++;
	    }
	 }

	@SuppressWarnings("unchecked")
	int findInsertionPoint(DefaultMutableTreeNode root, DefaultMutableTreeNode insert) {
		  // Copy the model data references to a Vector.
		  int size = root.getChildCount();
		  Vector<Object> list = new Vector<Object>();
		  for (int x = 0; x < size; ++x) {
		    String o = root.getChildAt(x).toString();
		    list.addElement(o);
		  }

		  // Find the new element's insertion point.
		  int insertionPoint = Collections.binarySearch(list, insert.toString(), comparator);
		  if (insertionPoint < 0 ) {
		    insertionPoint = -(insertionPoint + 1);
		  }
		  return insertionPoint;
	}
}
