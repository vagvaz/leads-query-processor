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

import java.awt.Color;
import java.util.List;

import javax.swing.ImageIcon;

import com.apatar.core.AbstractNode;

public abstract class NodeFactory {

	public abstract List<String> getCategory();

	public abstract String getTitle();

	public abstract ImageIcon getIcon();

	public abstract AbstractNode createNode();

	public abstract String getNodeClass();

	public abstract int getHorizontalTextPosition();

	public abstract int getVerticalTextPosition();

	public abstract Color getTextColor();

	public int getFontStile() {
		return -1;
	}

	@Override
	public String toString() {
		return getTitle();
	}

	public abstract boolean MainPaneNode();
}
