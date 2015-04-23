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

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.apatar.core.TableInfo;

public class JOutputPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	TableInfo tableInfo;
	String connectionName;
	JComponent parent;

	JShortcutBar shortcutBar;
	JOutputsToolBar outputsToolBar;
	GetInputs node;

	public JOutputPanel(TableInfo tableInfo, String connectoinName,
			JComponent parent, GetInputs node) {
		super();
		this.tableInfo = tableInfo;
		this.parent = parent;
		connectionName = connectoinName;
		this.node = node;
		createPanel();
	}

	public TableInfo getTableInfo() {
		return tableInfo;
	}

	private void createPanel() {
		setLayout(new BorderLayout());

		outputsToolBar = new JOutputsToolBar(this, tableInfo.getSchemaTable(),
				node);
		// add(outputsToolBar, BorderLayout.NORTH);

		shortcutBar = new JShortcutBar(tableInfo, connectionName, "", true,
				SwingConstants.LEFT);
		add(shortcutBar, BorderLayout.CENTER);
	}

	@Override
	public JComponent getParent() {
		return parent;
	}

	public void updateSchema() {
		remove(shortcutBar);
		shortcutBar = new JShortcutBar(tableInfo, connectionName, "output",
				true, SwingConstants.LEFT);
		add(shortcutBar, BorderLayout.CENTER);
		parent.updateUI();
	}
}
