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

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;

/** Creates a JTree with Drag and Drop facilities.
* <p>
* Create and use an object of DNDTree instead of a JTree to include Drag and Drop features for your tree.
* @author V. Shikunets
*/
public class DNDTree extends JTree implements DropTargetListener,DragSourceListener,DragGestureListener {
	private static final long serialVersionUID = 1L;

	/** The Drop position. */
	// strange member removed as it wasn't referenced anywhere
	//	private DropTarget dropTarget = new DropTarget (this, this);
	/** The Drag node.*/
	private DragSource dragSource = new DragSource();
	/** The dragged node.*/
	private DefaultMutableTreeNode selnode;
    private TreePath selpath;
	/** The droppped node.*/
	private DefaultMutableTreeNode dropnode;
    private TreePath droppath;

	/** The TreeModel for the tree.*/
	//private DefaultTreeModel treemodel=null;

	public DNDTree(TreeModel model){
		super(model);
		dragSource.createDefaultDragGestureRecognizer( this, DnDConstants.ACTION_COPY, this);
	}
    public DNDTree(TreeNode root) {
        super(root);
        dragSource.createDefaultDragGestureRecognizer( this, DnDConstants.ACTION_COPY, this);
    }
    public DNDTree() {
        super();
        dragSource.createDefaultDragGestureRecognizer( this, DnDConstants.ACTION_COPY, this);
    }


	/** Internally implemented, Do not override!*/
	public void drop(DropTargetDropEvent event){
		event.acceptDrop(DnDConstants.ACTION_COPY);
		Point droppoint=event.getLocation();
		droppath=getPathForLocation(droppoint.x,droppoint.y);
		if(droppath != null) {
			dropnode=(DefaultMutableTreeNode)droppath.getLastPathComponent();
            dragDropFinish();
			event.getDropTargetContext().dropComplete(true);
		}
	}

	/** Internally implemented, Do not override!*/
	public void dragGestureRecognized(DragGestureEvent event) {
		selnode=null;
		dropnode=null;
		Object selected = getSelectionPath();
		if(selected != null) {
			selpath=(TreePath)selected;
			selnode=(DefaultMutableTreeNode)selpath.getLastPathComponent();
			if ( selected != null ){
	            Transferable transfer=dragDropStart();
	            if (transfer==null) transfer = new StringSelection(selected.toString());
				dragSource.startDrag (event, DragSource.DefaultCopyNoDrop, transfer, this);
			}
			else{
			}
		}
	}
    /** override it!*/
    public Transferable dragDropStart() {
        return null;
    }

	/** override it!*/
	public void dragDropFinish() {
    }

 

	// source listeners
	public void dragDropEnd(DragSourceDropEvent event) {
	}
	public void dragEnter(DragSourceDragEvent event) {
    }
	public void dragExit(DragSourceEvent event) {
    }
	public void dragOver (DragSourceDragEvent event) {
    }
	public void dropActionChanged ( DragSourceDragEvent event) {
    }

    //target listeners
    public void dragEnter(DropTargetDragEvent event) {
		event.acceptDrag (DnDConstants.ACTION_COPY);
	}
    public void dragExit(DropTargetEvent event) {
    }
    public void dragOver(DropTargetDragEvent event) {
      /*  Point p = event.getLocation();
	    TreePath path = this.getPathForLocation(p.x, p.y);
	    if(path != null) {
	        this.setSelectionPath(path);
	    }*/
	}
    public void dropActionChanged (DropTargetDragEvent event) {
    }

    public DefaultMutableTreeNode getSelnode() {
        return selnode;
    }
    public void setSelnode(DefaultMutableTreeNode selnode) {
        this.selnode = selnode;
    }
    public TreePath getSelpath() {
        return selpath;
    }
    public void setSelpath(TreePath selpath) {
        this.selpath = selpath;
    }
    public DefaultMutableTreeNode getDropnode() {
        return dropnode;
    }
    public void setDropnode(DefaultMutableTreeNode dropnode) {
        this.dropnode = dropnode;
    }
    public TreePath getDroppath() {
        return droppath;
    }
    public void setDroppath(TreePath droppath) {
        this.droppath = droppath;
    }
    public TreePath askForLocation(int x, int y) {
    	return this.getPathForLocation(x,y);
    }
}



