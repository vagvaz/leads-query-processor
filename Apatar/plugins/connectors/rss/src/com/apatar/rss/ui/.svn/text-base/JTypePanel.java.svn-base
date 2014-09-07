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

package com.apatar.rss.ui;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class JTypePanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 453149417833087265L;
	JRadioButton createNew = new JRadioButton("Create RSS Feed");
	JRadioButton load = new JRadioButton("Read RSS Feed");

	public JTypePanel() {
		super();
		createPanel();
	}

	private void createPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(createNew);
		add(load);
		add(Box.createVerticalGlue());

		ButtonGroup group = new ButtonGroup();
		group.add(load);
		group.add(createNew);

		load.setSelected(true);
	}

	public JRadioButton getCreateNew() {
		return createNew;
	}

	public void setCreateNew(JRadioButton createNew) {
		this.createNew = createNew;
	}

	public JRadioButton getLoad() {
		return load;
	}

	public void setLoad(JRadioButton load) {
		this.load = load;
	}

}
