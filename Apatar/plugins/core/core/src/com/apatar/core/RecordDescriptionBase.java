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

import java.sql.Types;

import org.jdom.Element;

/**
 *
 */
public class RecordDescriptionBase implements Cloneable, IPersistent {
	private String originalType = "";
	private String realDBType = "";
	private ERecordType type = ERecordType.Text;
	private long lengthMin;
	private long length; // maximum is equal to current in case of
	// real field
	protected int sqlType = Types.VARCHAR;

	public RecordDescriptionBase() {

	}

	public RecordDescriptionBase(ERecordType type, String originalType,
			long lengthMin, long length) {
		this.originalType = realDBType = originalType;
		this.type = type;
		this.lengthMin = lengthMin;
		this.length = length;
	}

	public RecordDescriptionBase(ERecordType type, String originalType,
			long lengthMin, long length, int sqlType) {
		this.originalType = originalType;
		this.type = type;
		this.lengthMin = lengthMin;
		this.length = length;
		this.sqlType = sqlType;
	}

	public RecordDescriptionBase(long lengthMin, long length) {
		super();
		this.lengthMin = lengthMin;
		this.length = length;
	}

	// ***********************************************
	// Properties
	// ***********************************************
	public long getLength() {
		return length;
	}

	public long getLengthMin() {
		return lengthMin;
	}

	public String getOriginalType() {
		return originalType;
	}

	public ERecordType getType() {
		return type;
	}

	public void setType(ERecordType type) {
		this.type = type;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public void setLengthMin(long lengthMin) {
		this.lengthMin = lengthMin;
	}

	public void setOriginalType(String originalType) {
		this.originalType = originalType;
	}

	public int getSqlType() {
		return sqlType;
	}

	public int getSqlType(boolean convertFromType) {
		if (convertFromType) {

			switch (type.ordinal()) {
			case 10: // numeric
				return Types.NUMERIC;
			case 5:
				return Types.DECIMAL;
			case 0:
				return Types.BINARY;
			case 17:
				return Types.VARBINARY;
			case 7:
				return Types.LONGVARBINARY;
			case 1:
				return Types.BOOLEAN;
			case 13:
				return Types.VARCHAR;
			case 8:
				return Types.LONGVARCHAR;
			case 4:
				return Types.DATE;
			case 15:
				return Types.TIME;
			case 14:
				return Types.VARCHAR;
			case 3:
				return Types.FLOAT;
			case 18:
				return Types.LONGVARCHAR;
			case 9:
				return Types.BLOB;
			case 12:
				return Types.BINARY;
			case 11:
				return Types.JAVA_OBJECT;
			case 6:
				return Types.ARRAY;
			case 16:
				return Types.TIMESTAMP;
			case 2:
				return Types.CLOB;
			default:
				return 0;
			}
		} else {
			return sqlType;
		}
	}

	public void setSqlType(int sqlType) {
		this.sqlType = sqlType;
	}

	public String getRealDBType() {
		return realDBType;
	}

	public void setRealDBType(String realDBType) {
		this.realDBType = realDBType;
	}

	// ******************************************************
	// Save/Load functionality
	// ******************************************************
	public Element saveToElement() {
		Element root = PersistentUtils.CreateElement(this);
		root.setAttribute("type", type.toString());
		root.setAttribute("originalType", originalType);
		root.setAttribute("lengthMin", "" + lengthMin);
		root.setAttribute("length", "" + length);
		return root;
	}

	public void initFromElement(Element root) {
		String strType = null;
		try {
			strType = root.getAttributeValue("type");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (strType.equals("DateTime")) {
			type = ERecordType.Timestamp;
			originalType = "TIMESTAMP";
		} else {
			type = ERecordType.valueOf(strType);
			originalType = root.getAttributeValue("originalType");
		}
		lengthMin = Long.parseLong(root.getAttributeValue("lengthMin"));
		length = Long.parseLong(root.getAttributeValue("length"));
	}
}
