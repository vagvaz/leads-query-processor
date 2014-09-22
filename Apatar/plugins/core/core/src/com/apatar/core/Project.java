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

package com.apatar.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

public class Project {

	private int										nextIdNode;
	private long									nextIdConnector;

	private final HashMap<Integer, AbstractNode>	nodes;

	private final ArrayList<Connector>				connectors;

	public Project() {
		nodes = new HashMap<Integer, AbstractNode>();
		connectors = new ArrayList<Connector>();
	}

	protected int getNextIdNode() {
		return ++nextIdNode;
	}

	protected long getNextIdConnector() {
		return ++nextIdConnector;
	}

	public void setLastIdNode(int id) {
		nextIdNode = id;
	}

	public void setLastIdConnector(long id) {
		nextIdConnector = id;
	}

	public void addNode(AbstractNode node) {
		int id = node.getId();
		if (id <= 0) {
			node.setId(getNextIdNode());
		} else {
			if (id > nextIdNode) {
				nextIdNode = id;
			}
		}

		ApplicationData.STATUS_APPLICATION = ApplicationData.EDITED_STATUS;
		nodes.put(node.getId(), node);
	}

	public AbstractNode getNode(int id) {
		return nodes.get(id);
	}

	public Map<Integer, AbstractNode> getNodes() {
		return nodes;
	}

	public List<Connector> getConnectors() {
		return connectors;
	}

	public void removeAllElements() {
		nodes.clear();
		connectors.clear();
		nextIdNode = 0;
		nextIdConnector = 0;
		projectDatas.clear();
	}

	public Connector connect(ConnectionPoint begin, ConnectionPoint end) {
		ApplicationData.STATUS_APPLICATION = ApplicationData.EDITED_STATUS;
		Connector conn = Connector.connect(begin, end, getNextIdConnector());
		connectors.add(conn);

		return conn;
	}

	Map<Long, ProjectData>	projectDatas	= new HashMap<Long, ProjectData>();

	public void addProjectData(ProjectData data) {
		if (projectDatas == null) {
			projectDatas = new HashMap<Long, ProjectData>();
		}
		projectDatas.put(data.getId(), data);
		ApplicationData.STATUS_APPLICATION = ApplicationData.EDITED_STATUS;
	}

	public ProjectData getProjectData(long id) {
		return projectDatas.get(id);
	}

	public void removeProjectData(ProjectData data) {
		projectDatas.remove(data.getId());
		ApplicationData.STATUS_APPLICATION = ApplicationData.EDITED_STATUS;
	}

	public void saveProjectData(Element element) {
		Element e = new Element("ProjectDatas");
		for (ProjectData projectData : projectDatas.values()) {
			e.addContent((projectData).saveToElement());
		}
		element.addContent(e);
	}

	public void initProjectData(Element element) {
		projectDatas.clear();
		Element e = element.getChild("ProjectDatas");
		for (Object elem : e.getChildren()) {
			Element curel = (Element) elem;
			ProjectData pd = (ProjectData) PersistentUtils.CreateObject(curel);
			projectDatas.put(pd.id, pd);
		}
	}

	public Map<Long, ProjectData> getProjectDatas(String type, String subType) {
		HashMap<Long, ProjectData> map = new HashMap<Long, ProjectData>();
		for (ProjectData data : projectDatas.values()) {
			if (data.getType().equals(type)
					&& data.getSubType().equals(subType)) {
				map.put(data.getId(), data);
			}
		}
		return map;
	}
}
