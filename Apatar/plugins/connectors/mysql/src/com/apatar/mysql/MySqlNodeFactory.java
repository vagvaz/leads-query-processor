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

package com.apatar.mysql;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.apatar.core.AbstractNode;
import com.apatar.ui.NodeFactory;

public class MySqlNodeFactory extends NodeFactory {
	
	public AbstractNode createNode() {
		return new MySqlNode();
	}

	public List<String> getCategory() {
		List<String> res = new ArrayList<String>();
		res.add("Connectors");
		return res;
	}

	public ImageIcon getIcon() {
		return MySqlUtils.WRITE_MYSQL_ICON;
	}
	
	public String getTitle() {
		return "MySQL";
	}
	
	public String getNodeClass() {
		return MySqlNode.class.getName();
	}
	
	public int getHorizontalTextPosition() {
		return JLabel.CENTER;
	}

	public int getVerticalTextPosition() {
		return JLabel.BOTTOM;
	}
	
	public Color getTextColor() {
		return Color.BLACK;
	}
	public boolean MainPaneNode()
	{ return true; }
	
}
