/*TODO recorded refactoring
 * в класс TableInfo добавлено имплементирование интерфейса IPersistent
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

import java.util.Date;
import java.util.List;

import org.jdom.Element;

public class TableInfo implements IPersistent, Cloneable {
	String		tableName;

	SchemaTable	schemaTable	= new SchemaTable();

	public SchemaTable getSchemaTable() {
		return schemaTable;
	}

	public void setSchemaTable(SchemaTable schemaTable) {
		this.schemaTable = schemaTable;
	}

	public TableInfo() {
		super();
		tableName = "temp" + new Date().getTime();
	}

	public TableInfo(String tableName) {
		super();
		this.tableName = "temp" + new Date().getTime() + tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<Record> getRecords() {
		return getSchemaTable().getRecords();
	}

	public Element saveToElement() {
		Element root = new Element("tableInfo");

		Element tnE = new Element("tableName");
		tnE.setText(tableName);
		root.addContent(tnE);
		root.addContent(schemaTable.saveToElement());

		return root;
	}

	public void initFromElement(Element root) {
		tableName = root.getChildText("tableName");
		schemaTable.initFromElement(root);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		TableInfo newTi = new TableInfo(tableName);
		newTi.setSchemaTable((SchemaTable) schemaTable.clone());
		return newTi;
	}

	public TableInfo getClonedTi() throws Exception {
		return (TableInfo) clone();
	}
}
