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

package com.apatar.msexcel;

import java.util.List;

import org.jdom.Element;

import com.apatar.core.ColumnNodeFactory;
import com.apatar.core.Record;
import com.apatar.core.RecordDescriptionBase;

public class MsExcelRecord extends Record {
	int number;

	public MsExcelRecord() {
		super();
	}

	public MsExcelRecord(RecordDescriptionBase field, String fieldName,
			long lenght, boolean nullable, boolean signed, boolean primaryKey) {
		super(field, fieldName, lenght, nullable, signed, primaryKey);
	}

	public MsExcelRecord(RecordDescriptionBase field, String fieldName,
			long length, boolean signed) {
		super(field, fieldName, length, signed);
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	@Override
	public Element saveToElement() {
		Element root = super.saveToElement();
		root.setAttribute("number", "" + number);
		return root;
	}

	@Override
	public void initFromElement(Element root) {
		super.initFromElement(root);
		number = Integer.parseInt(root.getAttributeValue("number"));
	}

	static public int getPosition(List<MsExcelRecord> recs, String name) {
		for (MsExcelRecord rec : recs) {
			if (rec.fieldName.equalsIgnoreCase(name)) {
				return rec.getNumber();
			}
		}
		return -1;
	}

	static public String getFieldName(List<MsExcelRecord> recs, int pos) {
		for (MsExcelRecord rec : recs) {
			if (rec.number == pos) {
				return rec.getFieldName();
			}
		}
		return null;
	}

	static public MsExcelRecord getRecordByPosition(List<Record> recs, int pos) {
		for (Record rec1 : recs) {
			MsExcelRecord rec = (MsExcelRecord) rec1;
			if (rec.number == pos) {
				return rec;
			}
		}
		return null;
	}

	@Override
	public ColumnNodeFactory createColumnNodeFactory(String connectionName,
			String category, boolean inbound) {
		return new MsExcelColumnNodeFactory(this, connectionName, category,
				inbound);
	}
}
