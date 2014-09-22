/*TODO recorded refactoring
 * в класс AbstractNode добавлено имплементирование интерфейса IPersistent
 * *********************
 */

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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.ImageIcon;

import org.jdom.Element;

import com.apatar.ui.UiUtils;

public abstract class AbstractNode implements Node, NodeUI, ITransformer,
		IPersistent {

	protected int id = 0;
	protected String title;
	protected Point position = new Point(0, 0);

	public static final int FIRST_POSITION = 1;
	public static final int MIDDLE_POSITION = 2;
	public static final int LAST_POSITION = 3;

	protected MapForOutputConnectionPoint outputConnectionList = new MapForOutputConnectionPoint();
	protected List<ConnectionPoint> inputConnectionList = new ArrayList<ConnectionPoint>();
	protected List<ConnectionPoint> extConnectionList = new ArrayList<ConnectionPoint>();
	protected String lastErrorMessage = "";

	public boolean isLastErrorMessageEmpty() {
		return lastErrorMessage == null || "".equals(lastErrorMessage);
	}

	public String getKeyForReferringToDescription() {
		return "";
	}

	public AbstractNode() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public abstract ImageIcon getIcon();

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position.setLocation(position.x, position.y);
	}

	public SchemaTable getOutputSchema(ConnectionPoint cp) {
		return outputConnectionList.get(cp).getSchemaTable();
	}

	public final void edit(AbstractApatarActions actions) {
		// actually to avoid relinking we have to store all the connection
		// the the set of connnection points and relink it when the node is
		// reloaded
		// by cancel action

		HashMap<String, List<Connector>> cashedConnectors = new HashMap<String, List<Connector>>();

		for (ConnectionPoint cpt : getConnPoints()) {
			cashedConnectors.put(cpt.getName(), cpt.getConnectors());
		}

		beforeEdit();
		Element backup = saveToElement();
		boolean resultEdit = realEdit(actions) == true;
		afterEdit(resultEdit, actions);
		if (!resultEdit) {
			if (backup != null) {
				initFromElement(backup);
			}
			// relink connectors
			for (ConnectionPoint cpt : getConnPoints()) {
				cpt.addConnectors(cashedConnectors.get(cpt.getName()));
			}
		} else {
			ApplicationData.STATUS_APPLICATION = ApplicationData.EDITED_STATUS;
		}
	}

	public Element saveToElement() {
		Element readNode = new Element("node");
		readNode.setAttribute("id", String.valueOf(getId()));
		readNode.setAttribute("posX", String.valueOf(getPosition().x));
		readNode.setAttribute("posY", String.valueOf(getPosition().y));
		readNode.setAttribute("width", String.valueOf(getWidth()));
		readNode.setAttribute("height", String.valueOf(getHeigth()));
		readNode.setAttribute("nodeClass", this.getClass().getName());
		readNode.setAttribute("title", title);

		readNode.addContent(saveOutputConnectionPoints());
		readNode.addContent(saveInputConnectionPoints());
		readNode.addContent(saveExtConnectionPoints());

		return readNode;
	}

	public void initFromElement(Element e) {
		initFromElementWithoutConnectionPoint(e);

		loadOutputConnectionPoints((Element) e.getChildren(
				"OutputConnectionPoints").get(0));
		// loadInputConnectionPoints((Element)e.getChildren("InputConnectionPoints").get(0));
		// loadExtConnectionPoints((Element)e.getChildren("ExtConnectionPoints").get(0));
	}

	private Element saveInputConnectionPoints() {
		Element rv = new Element("InputConnectionPoints");
		for (ConnectionPoint connectionPoint : inputConnectionList) {
			rv.addContent((connectionPoint).saveToElement());
		}
		return rv;
	}

	private Element saveExtConnectionPoints() {
		Element rv = new Element("ExtConnectionPoints");
		for (ConnectionPoint connectionPoint : extConnectionList) {
			rv.addContent((connectionPoint).saveToElement());
		}
		return rv;
	}

	private final Element saveOutputConnectionPoints() {
		Element rv = new Element("OutputConnectionPoints");
		for (ConnectionPoint cp : outputConnectionList.keySet()) {
			Element el = cp.saveToElement();
			TableInfo ti = getTiForConnection(cp.name);
			if (ti != null) {
				el.addContent(ti.saveToElement());
			}
			rv.addContent(el);
		}
		return rv;
	}

	protected final void loadInputConnectionPoints(Element e) {
		// inputConnectionList.clear();
		List list = e.getChildren("ConnectionPoint");
		for (Iterator it = list.iterator(); it.hasNext();) {
			Element el = (Element) it.next();
			ConnectionPoint cp = new ConnectionPoint(this);
			cp.initFromElement(el);
			inputConnectionList.add(cp);
		}
	}

	/*
	 * protected final void loadExtConnectionPoints(Element e) {
	 * extConnectionList.clear(); List list = e.getChildren("ConnectionPoint");
	 * for(Iterator it = list.iterator(); it.hasNext();) { Element el =
	 * (Element)it.next(); ConnectionPoint cp = new ConnectionPoint(this);
	 * cp.initFromElement(el); extConnectionList.add(cp); } }
	 */

	private final void loadOutputConnectionPoints(Element e) {
		// outputConnectionList.clear();
		List list = e.getChildren("ConnectionPoint");
		for (Iterator it = list.iterator(); it.hasNext();) {
			Element el = (Element) it.next();
			ConnectionPoint cp;
			cp = outputConnectionList.getByName(el.getAttributeValue("name"));
			if (cp == null) {
				cp = new ConnectionPoint(this);
				cp.initFromElement(el);
			}

			List cpChildren = el.getChildren("tableInfo");
			TableInfo ti = null;
			if (cpChildren.size() > 0) {
				ti = new TableInfo();
				ti.initFromElement((Element) cpChildren.get(0));
			}
			outputConnectionList.put(cp, ti);
		}
	}

	// ********************************************************************
	// Positional
	// ********************************************************************
	protected int width;
	protected int height;

	public final int getWidth() {
		return width;
	};

	public final int getHeigth() {
		return height;
	};

	public int getInlinePosition() {
		int inCount = 0;
		int outCount = 0;
		for (ConnectionPoint connectionPoint : inputConnectionList) {
			inCount += (connectionPoint).getConnectors().size();
		}

		for (ConnectionPoint connectionPoint : extConnectionList) {
			inCount += (connectionPoint).getConnectors().size();
		}

		for (ConnectionPoint connectionPoint : outputConnectionList.keySet()) {
			outCount += (connectionPoint).getConnectors().size();
		}

		if (inCount == 0) {
			return AbstractNode.FIRST_POSITION;
		} else {
			if (outCount == 0) {
				return AbstractNode.LAST_POSITION;
			} else {
				return AbstractNode.MIDDLE_POSITION;
			}
		}
	}

	public ConnectionPoint getConnPoint(String name) {
		for (Object element : outputConnectionList.keySet()) {
			ConnectionPoint rv = (ConnectionPoint) element;
			if (rv.getName().equals(name)) {
				return rv;
			}
		}
		for (Object element : inputConnectionList) {
			ConnectionPoint rv = (ConnectionPoint) element;
			if (rv.getName().equals(name)) {
				return rv;
			}
		}

		for (Object element : extConnectionList) {
			ConnectionPoint rv = (ConnectionPoint) element;
			if (rv.getName().equals(name)) {
				return rv;
			}
		}
		return null;
	}

	public Collection<ConnectionPoint> getConnPoints() {
		List<ConnectionPoint> list = new ArrayList<ConnectionPoint>();
		list.addAll(outputConnectionList.keySet());
		list.addAll(inputConnectionList);
		list.addAll(extConnectionList);
		return list;
	}

	public Collection<ConnectionPoint> getInputConnPoints() {
		ArrayList<ConnectionPoint> listCP = new ArrayList<ConnectionPoint>();
		listCP.addAll(inputConnectionList);
		return listCP;
	}

	public Collection<ConnectionPoint> getIncomingConnPoints() {
		ArrayList<ConnectionPoint> listCP = new ArrayList<ConnectionPoint>();
		listCP.addAll(inputConnectionList);
		listCP.addAll(extConnectionList);
		return listCP;
	}

	public Collection<ConnectionPoint> getOutputConnPoints() {
		ArrayList<ConnectionPoint> listCP = new ArrayList<ConnectionPoint>();
		listCP.addAll(outputConnectionList.keySet());
		return listCP;
	}

	public Collection<ConnectionPoint> getExtConnPoints() {
		ArrayList<ConnectionPoint> listCP = new ArrayList<ConnectionPoint>();
		listCP.addAll(extConnectionList);
		return listCP;
	}

	public static TableInfo getOtherSideTableInfo(ConnectionPoint cp) {
		if (cp.getConnectors().size() == 0) {
			return null;
		}
		ConnectionPoint othersidecp = cp.getConnectors().get(0).getBegin();
		TableInfo ti = (othersidecp.getNode())
				.getThisSideTableInfo(othersidecp);
		return ti;
	}

	public TableInfo getThisSideTableInfo(ConnectionPoint cp) {
		// TODO method getThisSideTableInfo
		// ConnectionPoint othersidecp = outputConnectionList.get(cp);
		// TableInfo ti = null; //=
		// othersidecp.getNode().getSchema(othersidecp);
		return outputConnectionList.get(cp);
	}

	public TableInfo getTiForConnection(String connectionName) {
		ConnectionPoint cp = getConnPoint(connectionName);
		if (inputConnectionList.contains(cp) || extConnectionList.contains(cp)) {
			return getOtherSideTableInfo(cp);
		} else {
			return getThisSideTableInfo(cp);
		}
	}

	public static void mergeSchemaTables(SchemaTable schema1,
			SchemaTable schema2) {
		List<Record> records = schema1.getRecords();
		for (Object element : schema2.getRecords()) {
			Record rec = (Record) element;
			if (records.contains(rec)) {
				continue;
			}
			records.add(rec);
		}
	}

	public SchemaTable getExpectedShemaTable() {
		return null;
	}

	public static SchemaTable mergeSchemaTable(List<SchemaTable> schemas) {
		SchemaTable resSchema = new SchemaTable();
		List<Record> recs = resSchema.getRecords();
		boolean contains = false;
		for (SchemaTable schema : schemas) {
			if (schema != null) {
				for (Record rec : schema.getRecords()) {
					contains = false;

					for (Record rec2 : recs) {
						if (rec.getFieldName().equalsIgnoreCase(
								rec2.getFieldName())) {
							contains = true;
							break;
						}
					}

					if (contains) {
						continue;
					}
					recs.add(rec);
				}
			}
		}
		return resSchema;
	}

	public static List<AbstractNode> getNextNodes(ConnectionPoint cp) {
		ArrayList<AbstractNode> result = new ArrayList<AbstractNode>();
		for (Object element : cp.getConnectors()) {
			Connector con = (Connector) element;
			result.add(con.getEnd().getNode());
		}
		return result;
	}

	public static List<AbstractNode> getPrevNodes(ConnectionPoint cp) {
		ArrayList<AbstractNode> result = new ArrayList<AbstractNode>();
		for (Object element : cp.getConnectors()) {
			Connector con = (Connector) element;
			result.add(con.getBegin().getNode());
		}
		return result;
	}

	public ImageIcon getInputConnPointIcon() {
		return UiUtils.INPUT_CONN_POINT_ICON;
	}

	public ImageIcon getOutputConnPointIcon() {
		return UiUtils.OUTPUT_CONN_POINT_ICON;
	}

	public ImageIcon getExtConnPointIcon() {
		return UiUtils.EXT_CONN_POINT_ICON;
	}

	public String getConnectedNodeName(String connectionPointName) {
		ConnectionPoint cp = getConnPoint(connectionPointName);
		if (cp.getConnectors().size() == 0) {
			return "";
		}
		NodeUI node = cp.getConnectors().get(0).getBegin().getNode();
		return node.getTitle();
	}

	public AbstractNode getConnectedNode(String connectionPointName) {
		ConnectionPoint cp = getConnPoint(connectionPointName);
		if (cp.getConnectors().size() == 0) {
			return null;
		}
		return cp.getConnectors().get(0).getBegin().getNode();
	}

	// **************************************************************************
	// order functionality
	// **************************************************************************
	int order = 0;

	public int getExecutionOrder() {
		return order;
	}

	public void setExecutionOrder(int order) {
		this.order = order;
	}

	public static class OrderComparator implements Comparator<AbstractNode> {
		public int compare(AbstractNode o1, AbstractNode o2) {

			return o1.getExecutionOrder() - o2.getExecutionOrder();
		}
	}

	protected void initFromElementWithoutConnectionPoint(Element e) {
		// read positional information
		getPosition().x = Integer.parseInt(e.getAttributeValue("posX"));
		getPosition().y = Integer.parseInt(e.getAttributeValue("posY"));
		width = Integer.parseInt(e.getAttributeValue("width"));
		height = Integer.parseInt(e.getAttributeValue("height"));

		id = Integer.parseInt(e.getAttributeValue("id"));
		title = e.getAttributeValue("title");
	}

	public int getFontStyle() {
		return -1;
	}

	public List<Action> getSpecialAction() {
		return null;
	}

	/**
	 * @return the canSynchronyze
	 */
	public boolean canSynchronyze() {
		return false;
	}

	/**
	 * @return the lastErrorMessage
	 */
	public String getLastErrorMessage() {
		return lastErrorMessage;
	}
}
