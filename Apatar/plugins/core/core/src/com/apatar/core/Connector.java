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

import org.jdom.Element;

public class Connector {

	private ConnectionPoint begin;
	private ConnectionPoint end;
	private final long id;

	public ConnectionPoint getBegin() {
		return begin;
	}

	public ConnectionPoint getEnd() {
		return end;
	}

	public void setBegin(ConnectionPoint cp) {
		begin = cp;
	}

	public void setEnd(ConnectionPoint cp) {
		end = cp;
	}

	public long getId() {
		return id;
	}

	private Connector(long id) {
		this.id = id;
	}

	// note that connection point for connecter is set in addConnector method
	public static Connector connect(ConnectionPoint begin, ConnectionPoint end,
			long id) {

		assert (begin.isOutbound());
		assert (end.isInbound());
		assert (begin.canConnectTo(end));
		assert (end.canConnectTo(begin));

		Connector conn = new Connector(id);
		begin.addConnector(conn);
		end.addConnector(conn);

		return conn;
	}

	public Element saveToElement() {
		try {
			Element arrowNode = new Element("arrow");
			arrowNode.setAttribute("id", String.valueOf(id));
			arrowNode.setAttribute("begin_id", String.valueOf((begin.getNode())
					.getId()));
			arrowNode.setAttribute("begin_conn_name", begin.getName());
			arrowNode.setAttribute("end_id", String.valueOf((end.getNode())
					.getId()));
			arrowNode.setAttribute("end_conn_name", end.getName());
			return arrowNode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public int hashCode() {
		return (int) (id ^ (id >>> 32));
	}

	@Override
	public boolean equals(Object obj) {
		if (id == ((Connector) obj).id) {
			return true;
		}
		return false;
	}

}
