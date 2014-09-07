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

package com.apatar.webdav.ui;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.apatar.core.AbstractDataBaseNode;

public class JWebDavModePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	JCheckBox isDeleteAll = new JCheckBox("Clear all files.");
	JRadioButton yesMode = new JRadioButton("YES");
	JRadioButton noMode = new JRadioButton("NO");
	
	public JWebDavModePanel() {
		super();
		createPanel();
	}

	private void createPanel() {
		setLayout(new BorderLayout(5,5));
		
		JPanel modePanel = new JPanel();
		modePanel.setLayout(new BoxLayout(modePanel, BoxLayout.Y_AXIS));
		add(modePanel, BorderLayout.CENTER);
		modePanel.add(new JLabel("Overwrite existing file(s)?"));
		modePanel.add(Box.createVerticalStrut(5));
		modePanel.add(yesMode);
		modePanel.add(Box.createVerticalStrut(2));
		modePanel.add(noMode);
		modePanel.add(Box.createVerticalStrut(5));
		modePanel.add(isDeleteAll);
		modePanel.add(Box.createVerticalGlue());
		
		
		ButtonGroup modeBG = new ButtonGroup();
		modeBG.add(yesMode);
		modeBG.add(noMode);
		noMode.setSelected(true);
	}
	
	public int getMode() {
		if (yesMode.isSelected())
			return AbstractDataBaseNode.INSERT_MODE;
		return AbstractDataBaseNode.UPDATE_MODE;
	}
	
	public void setMode(int mode) {
		if (mode == AbstractDataBaseNode.INSERT_MODE)
			yesMode.setSelected(true);
		else
			noMode.setSelected(true);
	}
	
	public boolean isDeleteAll() {
		return isDeleteAll.isSelected();
	}
	
	public void setDeleteAll(boolean b) {
		isDeleteAll.setSelected(b);
	}
}
