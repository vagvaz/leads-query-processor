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

import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;

import org.jdom.Element;

public class ConnectionPoint implements IPersistent {

	protected boolean inbound;
	protected String name;
	protected AbstractNode node;
	protected List<Connector> connectors;
	protected int countConnection;
	protected boolean isMultipleConnection;
	protected Object variableValue;
	private JLabel connLabel;
	String comment;
	int positionNumber = 1;

	public ConnectionPoint(AbstractNode node) {
		this.node = node;
		connectors = new LinkedList<Connector>();
		countConnection = 0;
	}

	public ConnectionPoint(String name, boolean inbound, AbstractNode node,
			boolean multi) {
		this(name, inbound, node);
		isMultipleConnection = multi;
	}

	public ConnectionPoint(String name, boolean inbound, AbstractNode node,
			boolean multi, String comment) {
		this(name, inbound, node, multi);
		this.comment = comment;
	}

	public ConnectionPoint(String name, boolean inbound, AbstractNode node) {
		this(node);
		this.name = name;
		this.inbound = inbound;
	}

	public ConnectionPoint(String name, boolean inbound, AbstractNode node,
			String comment) {
		this(name, inbound, node);
		this.comment = comment;
	}

	public ConnectionPoint(AbstractNode node, int positionNumber) {
		this(node);
		this.positionNumber = positionNumber;
	}

	public ConnectionPoint(String name, boolean inbound, AbstractNode node,
			boolean multi, int positionNumber) {
		this(name, inbound, node);
		isMultipleConnection = multi;
		this.positionNumber = positionNumber;
	}

	public ConnectionPoint(String name, boolean inbound, AbstractNode node,
			boolean multi, String comment, int positionNumber) {
		this(name, inbound, node, multi);
		this.comment = comment;
		this.positionNumber = positionNumber;
	}

	public ConnectionPoint(String name, boolean inbound, AbstractNode node,
			int positionNumber) {
		this(node);
		this.name = name;
		this.inbound = inbound;
		this.positionNumber = positionNumber;
	}

	public ConnectionPoint(String name, boolean inbound, AbstractNode node,
			String comment, int positionNumber) {
		this(name, inbound, node);
		this.comment = comment;
		this.positionNumber = positionNumber;
	}

	protected void addConnector(Connector conn) {

		assert (!inbound || (0 == connectors.size()));
		connectors.add(conn);
		// please note that here we are to setup sufficient connectoin point in
		// connector
		if (inbound) {
			conn.setEnd(this);
		} else {
			conn.setBegin(this);
		}
	}

	protected void addConnectors(List<Connector> conn) {
		assert (!inbound || (0 == connectors.size()));
		connectors.addAll(conn);
		for (Connector cn : conn) {
			// please note that here we are to setup sufficient connectoin point
			// in connector
			if (inbound) {
				cn.setEnd(this);
			} else {
				cn.setBegin(this);
			}
		}
	}

	protected void removeConnector(Connector conn) {

		connectors.remove(conn);
	}

	public String getName() {
		return name;
	}

	public AbstractNode getNode() {
		return node;
	}

	public boolean isInbound() {
		return inbound;
	}

	public boolean isOutbound() {
		return !inbound;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getPositionNumber() {
		return positionNumber;
	}

	public void setPositionNumber(int positionNumber) {
		this.positionNumber = positionNumber;
	}

	public boolean canConnectTo(ConnectionPoint cp) {

		if (inbound != cp.inbound) {
			if (inbound) {
				return 0 == connectors.size();
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public List<Connector> getConnectors() {
		return connectors;
	}

	public int getCountConnection() {
		return countConnection;
	}

	public void incrementCountConnection() {
		countConnection++;
	}

	public void decrementCountConnection() {
		countConnection--;
	}

	public JLabel getConnLabel() {
		return connLabel;
	}

	public void setConnLabel(JLabel label) {
		connLabel = label;
	}

	public boolean getIsMultipleConnection() {
		return isMultipleConnection;
	}

	public void setIsMultipleConnection(boolean flag) {
		isMultipleConnection = flag;
	}

	public Object getVariableValue() {
		return variableValue;
	}

	public void setVariableValue(Object value) {
		variableValue = value;
	}

	public void initFromElement(Element e) {
		inbound = Boolean.parseBoolean(e.getAttributeValue("inbound"));
		name = e.getAttributeValue("name");
		isMultipleConnection = Boolean.parseBoolean(e
				.getAttributeValue("isMultipleConnection"));
		String posNumber = e.getAttributeValue("positionNumber");
		if (posNumber != null) {
			positionNumber = Integer.parseInt(posNumber);
		}
		comment = e.getChildText("Comment");
	}

	public Element saveToElement() {
		Element e = new Element("ConnectionPoint");
		e.setAttribute("inbound", "" + inbound);
		e.setAttribute("name", name);
		e.setAttribute("isMultipleConnection", "" + isMultipleConnection);
		e.setAttribute("positionNumber", "" + positionNumber);
		if (comment != null) {
			Element elComment = new Element("Comment");
			elComment.setText(comment);
			e.addContent(elComment);
		}
		return e;
	}
}
