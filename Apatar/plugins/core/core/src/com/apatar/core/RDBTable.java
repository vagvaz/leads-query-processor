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

import java.util.Comparator;

import org.jdom.Element;

public class RDBTable implements IPersistent{

	public String toString() {
		String result;
		String rv = "(undefined)";
		switch(mode)
		{
			case ReadOnly:
				rv = "(ro)";
			break;
			case ReadWrite:
				rv = "";
			break;
			
			case WriteOnly:
				rv = "(wo)";
			break;
		}
		result = rv + tableName;
		
		if (comment != null && !comment.equals(""))
			result += " (" + comment + ")";
		
		return result;
	}
	
	boolean support = true;
	public boolean isSupport() {
		return support;
	}
	public void setSupport(boolean support) {
		this.support = support;
	}
	
	public RDBTable(String name, ETableMode rwmode)
	{
		this.tableName = name;
		this.mode = rwmode;
	}
	public RDBTable(String tableName, ETableMode mode, String comment) {
		super();
		this.tableName = tableName;
		this.mode = mode;
		this.comment = comment;
	}
	
	String tableName = "";
	ETableMode mode = ETableMode.ReadWrite;
	String comment = null;
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public void initFromElement(Element e) {
		tableName = e.getAttributeValue("tableName");
		mode = ETableMode.valueOf(e.getAttributeValue("mode"));
		comment = e.getAttributeValue("comment");
	}
	public Element saveToElement() {
		Element rv = PersistentUtils.CreateElement(this);
		rv.setAttribute("tableName", tableName);
		rv.setAttribute("mode", mode.toString());
		if (comment != null)
			rv.setAttribute("comment", mode.toString());
		return rv;
	}

	public ETableMode getMode() {
		return mode;
	}

	public void setMode(ETableMode mode) {
		this.mode = mode;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof RDBTable) {
			RDBTable table = (RDBTable)obj;
			if (table.mode.equals(this.mode) && table.tableName.equalsIgnoreCase(this.tableName))
				return true;
		}
		return false;
	}
	
	public int hashCode() {
		return mode.hashCode() ^ tableName.hashCode();
	}
	
	public static class RDBTableComparator implements Comparator<RDBTable>
	{
		public int compare(RDBTable arg0, RDBTable arg1) {
			return arg0.getTableName().compareToIgnoreCase(arg1.getTableName());
		}
	}
}
