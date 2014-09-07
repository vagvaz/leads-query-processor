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

import java.util.List;

import org.jdom.Element;

public class Record extends RecordDescriptionBase implements Cloneable {
	protected String fieldName = "";
	protected boolean primaryKey = false;
	protected boolean signed = false;
	protected boolean nullable = true;
	protected boolean readOnly = false;

	// default constructor for persistency
	public Record() {
	}

	public boolean equalTo(Record record) {
		if (record == null) {
			return false;
		}
		if (!getFieldName().equals(record.getFieldName())) {
			return false;
		}
		if (this.getSqlType() != record.getSqlType()) {
			return false;
		}
		if (getLength() != record.getLength()) {
			return false;
		}
		return true;
	}

	// *******************************
	// Set of constructors for field descriptions
	// *******************************
	public Record(RecordDescriptionBase field, String fieldName, long length,
			boolean nullable, boolean signed, boolean primaryKey) {
		super(field.getType(), field.getOriginalType(), field.getLengthMin(),
				length);
		this.nullable = nullable;
		this.signed = signed;
		this.primaryKey = primaryKey;
		this.fieldName = fieldName;
		sqlType = field.getSqlType();
	}

	public Record(RecordDescriptionBase field, String fieldName, long length,
			boolean signed) {
		this(field, fieldName, length, true, signed, false);
	}

	public Record(RecordDescriptionBase field, String fieldName, long length,
			boolean signed, boolean nullable) {
		this(field, fieldName, length, nullable, signed, false);
	}

	// ******************************************************
	// Save/Load functionality
	// ******************************************************
	@Override
	public Element saveToElement() {
		Element root = super.saveToElement();
		root.setAttribute("fieldName", fieldName);
		root.setAttribute("primaryKey", primaryKey + "");
		root.setAttribute("signed", primaryKey + "");
		root.setAttribute("nullable", primaryKey + "");
		root.setAttribute("readonly", String.valueOf(isReadOnly()));
		return root;
	}

	@Override
	public void initFromElement(Element root) {
		super.initFromElement(root);
		fieldName = root.getAttributeValue("fieldName");
		primaryKey = Boolean.parseBoolean(root.getAttributeValue("primaryKey"));
		signed = Boolean.parseBoolean(root.getAttributeValue("signed"));
		nullable = Boolean.parseBoolean(root.getAttributeValue("nullable"));
		setReadOnly(Boolean.parseBoolean(root.getAttributeValue("readonly")));
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public boolean isNullable() {
		return nullable;
	}

	@Override
	public String toString() {
		return String.format("(%s) %s", getType().toString(), fieldName);
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public boolean isSigned() {
		return signed;
	}

	public static Record getRecordByFieldName(List<Record> recs, String name) {
		for (Record rec : recs) {
			if (rec.getFieldName().equalsIgnoreCase(name)) {
				return rec;
			}
		}
		return null;
	}

	public ColumnNodeFactory createColumnNodeFactory(String connectionName,
			String category, boolean inbound) {
		return new ColumnNodeFactory(this, connectionName, category, inbound);
	}

	@Override
	public Record clone() {
		Record clone = null;
		try {
			clone = (Record) super.clone();
		} catch (CloneNotSupportedException e) {
			System.err.println(this.getClass() + " can't be cloned");
		}
		return clone;
	}

	/**
	 * @return the readOnly
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * @param readOnly
	 *            the readOnly to set
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

}
