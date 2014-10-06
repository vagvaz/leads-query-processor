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
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;


public class ShortcutCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;

	public Component getTreeCellRendererComponent(JTree tree,Object value, 
						boolean selected,boolean expanded,boolean leaf,int row,
						boolean hasFocus) {
        Object obj = ((DefaultMutableTreeNode)value).getUserObject();
        
        if ( obj instanceof NodeFactory )
        	tree.setRowHeight( ((NodeFactory)obj).getIcon().getIconHeight()
        			+ 5 );
        
        DefaultTreeCellRenderer cmp = (DefaultTreeCellRenderer)super.
        					getTreeCellRendererComponent(tree, value, 
        					selected, expanded, leaf, row, hasFocus);
        
        if ( obj instanceof NodeFactory ) {
        	NodeFactory nf = (NodeFactory)obj;
        	setIcon( nf.getIcon() );
        }
        
        return cmp;
	}
}
