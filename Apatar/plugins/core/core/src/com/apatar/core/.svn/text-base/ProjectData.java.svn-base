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

import java.util.Date;

import org.jdom.Element;

public class ProjectData implements IPersistent {
	long	id;
	String	type;
	String	subType;
	String	name;

	Object	data;

	public ProjectData() {
		super();
		id = new Date().getTime();
		type = "";
		subType = "";
	}

	public ProjectData(String type, String subType, String name, Object data) {
		this();
		this.type = type;
		this.subType = subType;
		this.name = name;
		setData(data);
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		if (data instanceof IPersistent) {
			this.data = data;
		} else {
			System.out.println(data.getClass().getName());
			System.out.println("Class should realize IPersisten");
		}
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public Element saveToElement() {
		Element rv = PersistentUtils.CreateElement(this);
		rv.setAttribute("id", "" + id);
		rv.setAttribute("type", type);
		rv.setAttribute("subtype", subType);
		rv.setAttribute("name", name);
		if (data != null) {
			rv.addContent(((IPersistent) data).saveToElement());
		}
		return rv;
	}

	public void initFromElement(Element e) {
		String value;
		value = e.getAttributeValue("id");
		if (value != null) {
			id = Long.parseLong(value);
		} else {
			ApplicationData.COUNT_INIT_ERROR++;
		}
		type = e.getAttributeValue("type");
		subType = e.getAttributeValue("subtype");
		if ("msexel".equalsIgnoreCase(subType)) {
			subType = "msexcel";
		}
		name = e.getAttributeValue("name");

		if (e.getChildren().size() > 0) {
			IPersistent ip = PersistentUtils.CreateObject((Element) e
					.getChildren().get(0));
			data = ip;
		} else {
			data = null;
		}
	}
}
