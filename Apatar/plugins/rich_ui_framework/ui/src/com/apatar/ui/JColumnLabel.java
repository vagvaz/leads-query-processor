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

import javax.swing.Icon;
import javax.swing.JLabel;

public class JColumnLabel extends JLabel {
	private static final long serialVersionUID = 1L;

	public JColumnLabel() {
		super();
	}

	public JColumnLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
	}

	public JColumnLabel(Icon image) {
		super(image);
	}

	public JColumnLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
	}

	public JColumnLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
	}

	public JColumnLabel(String text) {
		super(text);
	}

	public String toString() {
		return getText();
	}
}
