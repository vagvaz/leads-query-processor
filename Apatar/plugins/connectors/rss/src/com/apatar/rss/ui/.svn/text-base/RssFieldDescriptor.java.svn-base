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

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.Record;
import com.apatar.ui.wizard.RecordSourceDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;
import com.apatar.rss.RssElement;
import com.apatar.rss.RssNode;

public class RssFieldDescriptor extends WizardPanelDescriptor {
	
	public static final String IDENTIFIER = "FIELD_PANEL";
	
	RssNode node;
	JRssFieldPanel panel;
	
	public RssFieldDescriptor(RssNode node, JRssFieldPanel panel) {
		super(IDENTIFIER, panel);
		this.node = node;
		this.panel = panel;
	}
	
	public Object getNextPanelDescriptor() {
        return RecordSourceDescriptor.IDENTIFIER;
    }
    
    public Object getBackPanelDescriptor() {
    	return ChannelDescriptor.IDENTIFIER;
    }
    
    public void aboutToDisplayPanel() {
    	RssElement element = node.getItemForRssVersion();
		if (element == null)
			return;
		
		panel.setRootRssElement(element);
    }

    public void displayingPanel() {

    }

    public int aboutToHidePanel(String actionCommand) {
    	if (actionCommand.equals(Wizard.NEXT_BUTTON_ACTION_COMMAND)) {
    		node.setRssRecords(getFieldList(false));
    	}
    	return CHANGE_PANEL;
    }
    
    public List<Record> getFieldList(boolean withMessage) {
    	List<Record> list = new ArrayList<Record>();
    	DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)panel.treeModel.getRoot();
    	KeyInsensitiveMap kim = new KeyInsensitiveMap();

    	getFieldList(list, rootNode, kim);
    	
    	return RssNode.generateFieldListFromKeyInsensitiveMap(kim, node.getRssElementsByName());
    }

	private void getFieldList(List<Record> records, DefaultMutableTreeNode node, KeyInsensitiveMap currentKim) {
		
		int childCount = node.getChildCount();
		for (int i = 0; i < childCount; i++) {
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)node.getChildAt(i);
			RssElement childRssElem = (RssElement)childNode.getUserObject();
			String fullChildName = childRssElem.generateFieldName();
			RssNode.putFieldCount(currentKim, fullChildName, childRssElem);
			getFieldList(records, childNode, currentKim);
		}
		
	}
	
}

