package com.apatar.ui;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
_______________________

Apatar Open Source Data Integration

Copyright (C) 2005-2007, Apatar, Inc.

info@apatar.com

195 Meadow St., 2nd Floor

Chicopee, MA 01013

 

    This program is free software; you can redistribute it and/or modify

    it under the terms of the GNU General Public License as published by

    the Free Software Foundation; either version 2 of the License, or

    (at your option) any later version.

 

    This program is distributed in the hope that it will be useful,

    but WITHOUT ANY WARRANTY; without even the implied warranty of

    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the

    GNU General Public License for more details.

 

    You should have received a copy of the GNU General Public License along

    with this program; if not, write to the Free Software Foundation, Inc.,

    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

________________________

 */

public class JComponentWithCommentPanel extends JPanel {
	String comment;
	JComponent comp;
	public JComponentWithCommentPanel(String comment, JComponent comp, int strut) {
		super();
		this.comment = comment;
		this.comp = comp;
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		add(new JLabel(comment));
		add(Box.createHorizontalStrut(strut));
		add(comp);
		add(Box.createHorizontalGlue());
	}
	public JComponentWithCommentPanel(String comment, JComponent comp) {
		this(comment, comp, 5);
	}
}

