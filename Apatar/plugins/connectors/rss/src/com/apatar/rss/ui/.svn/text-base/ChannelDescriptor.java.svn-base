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

import com.apatar.core.ApplicationData;
import com.apatar.ui.wizard.WizardPanelDescriptor;
import com.apatar.rss.CreateNewParams;
import com.apatar.rss.RssElement;
import com.apatar.rss.RssNode;
import com.apatar.rss.Version;

public class ChannelDescriptor extends WizardPanelDescriptor {
	
	public static final String IDENTIFIER = "CHANNEL_PANEL";
	
	JChannelPanel panel;
	
	RssNode node;
	
	public ChannelDescriptor(RssNode node, JChannelPanel panel) {
		super(IDENTIFIER, panel);
		this.node = node;
		this.panel = panel;
	}

	public Object getNextPanelDescriptor() {
        return RssFieldDescriptor.IDENTIFIER;
    }
    
    public Object getBackPanelDescriptor() {
    	return RssDBConnectionDescriptor.IDENTIFIER;
    }
    
    public void aboutToDisplayPanel() {
    	Object obj = ApplicationData.getProject().getProjectData(node.getConnectionDataID()).getData();
    	if (!(obj instanceof CreateNewParams))
    		return;
    	CreateNewParams params = (CreateNewParams)ApplicationData.getProject().getProjectData(node.getConnectionDataID()).getData();
    	RssElement element;// = new ArrayList<RssElement>();
		if (params.getVersion().equals(Version.RSS_1_0)) {
			element = RssNode.generateChannelStructureRss_1_0();
		} else {
			if (params.getVersion().equals(Version.RSS_2_0)) {
				element = RssNode.generateChannelStructureRss_2_0();
			} else
				return;
		}
		
		panel.fillSchema(element);
    }

    public void displayingPanel() {

    }

    public int aboutToHidePanel(String actionCommand) {
    	/*if (actionCommand.equals(Wizard.NEXT_BUTTON_ACTION_COMMAND)) {
    		RssJdbcParams params = (RssJdbcParams)ApplicationData.getProject().getProjectData(node.getConnectionDataID()).getData();
    		Element element = null;
    		if (params.getVersion().equals(Version.RSS_1_0)) {
    			element = new Element("channel");
    		} else {
    			if (params.getVersion().equals(Version.RSS_2_0)) {
    				element = new Element("channel");
    			} 
    		}
    		populateChannelElement(element);
    		node.setChannel(element);
    	}*/
    	return CHANGE_PANEL;
    }
    
}

