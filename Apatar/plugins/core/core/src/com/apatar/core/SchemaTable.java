/*TODO recorded refactoring
 * в класс SchemaTable добавлена имплементирование интерфейса IPersistent
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

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

public class SchemaTable implements IPersistent, Cloneable {
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		SchemaTable newST = new SchemaTable();
		for (Record rec : records) {
			newST.addRecord(rec.clone());
		}
		return newST;
	}

	List<Record>	records;

	public SchemaTable() {
		super();
		records = new ArrayList<Record>();
	}

	public SchemaTable(List<Record> records) {
		super();
		this.records = records;
	}

	public List<Record> getRecords() {
		return records;
	}

	public void addRecord(Record rec) {
		records.add(rec);
	}

	public void removeRecord(Record rec) {
		records.remove(rec);
	}

	public void removeAllRecord() {
		records.clear();
	}

	public void updateRecords(List<Record> recs) {
		records = recs;
	}

	public Element saveToElement() {
		Element root = new Element("records");
		for (Record rec : records) {
			root.addContent(rec.saveToElement());
		}
		return root;
	}

	public void initFromElement(Element root) {
		records.clear();
		Element elem = root.getChild("records");

		for (Object recElem : elem.getChildren()) {
			Record rec = (Record) PersistentUtils
					.CreateObject((Element) recElem);
			if (rec != null) {
				records.add(rec);
			}
		}
	}

}
