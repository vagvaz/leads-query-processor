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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdom.Element;
import org.jdom.JDOMException;

import com.apatar.core.ApplicationData;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.ReadWriteXMLData;
import com.apatar.core.Record;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;
import com.apatar.rss.CreateNewParams;
import com.apatar.rss.RssElement;
import com.apatar.rss.RssNode;
import com.apatar.rss.Version;

public class SettingDescriptor extends WizardPanelDescriptor {
	public static final String IDENTIFIER = "SETTING_PANEL";

	JSettingPanel panel;
	RssNode node;

	RssElement item = new RssElement();

	public SettingDescriptor(RssNode node, JSettingPanel panel) {
		super(IDENTIFIER, panel);
		this.panel = panel;
		this.node = node;
	}

	public Object getNextPanelDescriptor() {
        return FINISH;
    }

    public Object getBackPanelDescriptor() {
    	return RssDBConnectionDescriptor.IDENTIFIER;
    }

    public void aboutToDisplayPanel() {
    	Object obj = ApplicationData.getProject().getProjectData(node.getConnectionDataID()).getData();
    	if (!(obj instanceof CreateNewParams))
    		return;
    	CreateNewParams params = (CreateNewParams)ApplicationData.getProject().getProjectData(node.getConnectionDataID()).getData();
    	RssElement element;
    	element = node.getChannel();
		if (element == null) {
			if (params.getVersion().equals(Version.RSS_1_0)) {
				element = RssNode.generateChannelStructureRss_1_0();
			} else {
				if (params.getVersion().equals(Version.RSS_2_0)) {
					element = RssNode.generateChannelStructureRss_2_0();
				} else {
					if (params.getVersion().equals(Version.ATOM_1_0))
						element = RssNode.generateFeedStructureAtom_1_0();
					else
						return;
				}
			}
			File file = params.getFile();
			if (file == null) {
				int index = 1;
				do {
					file = new File("rss" + index + ".xml");
					index++;
				} while(file.exists());
				params.setFile(file);
			}
			else {
				if (file.exists()) {
					try {
						Element root = ReadWriteXMLData.getRootElement(file);
						Element channelElement = root.getChild("channel");
						fillChanell(element, channelElement);
					} catch (JDOMException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			node.setChannel(element);
		}

		panel.channelPanel.fillSchema(element);

			if (params.getVersion().equals(Version.RSS_1_0)) {
				element = RssNode.generateItemStructureRss_1_0();
			} else {
				if (params.getVersion().equals(Version.RSS_2_0)) {
					element = RssNode.generateItemStructureRss_2_0();
				} else {
					if (params.getVersion().equals(Version.ATOM_1_0))
						element = RssNode.generateEntryStructureAtom_1_0();
					else
						return;
				}
			}
			panel.fieldPanel.setRootRssElement(element);
			item = new RssElement(element, false);
			RssElement itemRssElement = node.getItem();
			if (itemRssElement != null) {
				createTreeItem(element, itemRssElement);
			}

		panel.pathPanel.setPassword(node.getPassword());
		panel.pathPanel.setUserName(node.getUsername());
		panel.pathPanel.setPublish(node.isPublish());
    }

    public void displayingPanel() {

    }

    public int aboutToHidePanel(String actionCommand) {
    	if (actionCommand.equals(Wizard.NEXT_BUTTON_ACTION_COMMAND)) {

    		if (!panel.channelPanel.fillValueToRssElements()) {
    			JOptionPane.showMessageDialog(panel, "All channel fields must be filled in.");
    			return LEAVE_CURRENT_PANEL;
    		}

    		List<Record> recs = getFieldList(false);
    		if (recs.size() < 1) {
    			JOptionPane.showMessageDialog(panel, "Please create item.");
    			return LEAVE_CURRENT_PANEL;
    		}

    		node.setRssRecords(recs);

    		node.setItem(generateItem());

    		//node.setFile(file);

    		if (panel.pathPanel.isPublish()) {
	    		String str = panel.pathPanel.getUserName();
	    		if (str == null || str.equals("")) {
	    			JOptionPane.showMessageDialog(panel, "Incorrect User Name");
	    			return LEAVE_CURRENT_PANEL;
	    		}
	    		node.setUsername(str);
    		}
    		node.setPassword(panel.pathPanel.getPassword());

    		node.setPublish(panel.pathPanel.isPublish());
    	}
    	return CHANGE_PANEL;
    }

    public List<Record> getFieldList(boolean withMessage) {
    	List<Record> list = new ArrayList<Record>();
    	DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)panel.fieldPanel.treeModel.getRoot();
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

	private void createTreeItem(RssElement elem, RssElement itemRssElement) {
		panel.fieldPanel.createTreeItem(elem, itemRssElement);
	}

	private RssElement generateItem() {
		return panel.fieldPanel.generateItem();
	}

	/*
	 * Fill channel
	 * @rssElem - channel
	 * @channelElem - channel tag
	 */
	private void fillChanell(RssElement rssElem, Element channelElem) {
		for (RssElement child : rssElem.getChildrens()) {
			Element elChild = channelElem.getChild(child.getName());
			if (elChild != null) {
				child.setValue(elChild.getText());
			}
		}
	}

}

