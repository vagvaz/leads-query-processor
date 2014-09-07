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

import com.apatar.rss.RssNode;
import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class TypeDescriptor extends WizardPanelDescriptor {
	public static final String IDENTIFIER = "TYPE_PANEL";
	JTypePanel panel;
	RssNode node;

	public TypeDescriptor(RssNode node) {
		super();
		setPanelDescriptorIdentifier(IDENTIFIER);
		panel = new JTypePanel();
		setPanelComponent(panel);
		this.node = node;
	}

	@Override
	public Object getNextPanelDescriptor() {
		return DBConnectionDescriptor.IDENTIFIER;
	}

	@Override
	public Object getBackPanelDescriptor() {
		return null;
	}

	@Override
	public void aboutToDisplayPanel() {
		if (node.isCreateNew()) {
			panel.createNew.setSelected(true);
		} else {
			panel.load.setSelected(true);
		}
	}

	@Override
	public int aboutToHidePanel(String actionCommand) {
		if (actionCommand.equals(Wizard.NEXT_BUTTON_ACTION_COMMAND)) {
			node.setCreateNew(panel.createNew.isSelected());
			return CHANGE_PANEL;
		}
		return CHANGE_PANEL;
	}

}
