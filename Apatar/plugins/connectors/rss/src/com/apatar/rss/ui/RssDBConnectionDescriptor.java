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

import propertysheet.JPropertySheetPage;

import com.apatar.core.AbstractDataBaseNode;
import com.apatar.rss.CreateNewParams;
import com.apatar.rss.LoadParams;
import com.apatar.rss.RssNode;
import com.apatar.ui.wizard.DBConnectionDescriptor;

public class RssDBConnectionDescriptor extends DBConnectionDescriptor {

	public RssDBConnectionDescriptor(AbstractDataBaseNode node,
			JPropertySheetPage panel) {
		super(node, panel, null, null, null, null);
	}

	@Override
	public Object getNextPanelDescriptor() {
		RssNode rssnode = (RssNode) node;
		if (rssnode.isCreateNew()) {
			return SettingDescriptor.IDENTIFIER;
		} else {
			return FINISH;
		}
	}

	@Override
	public Object getBackPanelDescriptor() {
		return TypeDescriptor.IDENTIFIER;
	}

	@Override
	public void aboutToDisplayPanel() {
		boolean createNew = ((RssNode) node).isCreateNew();
		try {

			projectData = panel.init(node.getConnectionDataID(),
					createNew ? CreateNewParams.class : LoadParams.class,
					"db_connector", createNew ? "createRss" : "loadRss");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
