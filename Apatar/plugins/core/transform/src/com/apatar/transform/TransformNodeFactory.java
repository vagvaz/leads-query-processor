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

package com.apatar.transform;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.apatar.core.AbstractNode;
import com.apatar.transform.TransformNode;
import com.apatar.transform.TransformUtils;
import com.apatar.ui.NodeFactory;

public class TransformNodeFactory extends NodeFactory {
	
	public TransformNodeFactory() {
		super();
	}

	public List<String> getCategory() {
		List<String> res = new ArrayList<String>();
		res.add("Operations");
		return res;
	}

	public String getTitle() {
		return "Transform";
	}

	public ImageIcon getIcon() {
		return TransformUtils.SMALL_TRANSFORM_ICON;
	}

	public AbstractNode createNode() {
		return new TransformNode();
	}

	public int getHorizontalTextPosition() {
		return JLabel.CENTER;
	}

	public int getVerticalTextPosition() {
		return JLabel.BOTTOM;
	}

	public String getNodeClass() {
		return TransformNode.class.getName();
	}
	
	public Color getTextColor() {
		return Color.BLACK;
	}

	public boolean MainPaneNode()
	{ return true; }
}
