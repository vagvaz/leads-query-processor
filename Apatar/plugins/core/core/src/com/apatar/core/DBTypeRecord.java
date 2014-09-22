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

public class DBTypeRecord extends RecordDescriptionBase {
	private boolean supportPK = false;
	private boolean supportSign = false;
	
	//default constructor for persistency
	public DBTypeRecord()
	{}
	
	//*************************
	//			Set of constructors for database types description
	//*************************
	public DBTypeRecord(ERecordType type, String originalType, long lengthMin,
			long length, boolean signed, boolean primaryKey) 
	{
		super(type, originalType, lengthMin, length);
		this.supportSign = signed;
		this.supportPK = primaryKey;
	}

	public DBTypeRecord(ERecordType type, String originalType, long lengthMin,
			long length)
	{
		this(type, originalType, lengthMin, length, false, false);
	}
	
	public DBTypeRecord(ERecordType type, String originalType, long lengthMin,
			long length, boolean signed, boolean primaryKey, int sqlType) 
	{
		super(type, originalType, lengthMin, length, sqlType);
		this.supportSign = signed;
		this.supportPK = primaryKey;
	}

	public DBTypeRecord(ERecordType type, String originalType, long lengthMin,
			long length, int sqlType)
	{
		this(type, originalType, lengthMin, length, false, false, sqlType);
	}

	
	//******************************************************
	//					Save/Load functionality	
	//******************************************************
	public Element saveToElement() {
		Element root = super.saveToElement();
		root.setAttribute("primaryKey","" + supportPK);
		root.setAttribute("signed","" + supportSign);
		return root;
	}
	
	public void initFromElement(Element root) {
		super.initFromElement(root);
		supportPK = Boolean.parseBoolean(root.getAttributeValue("primaryKey"));
		supportSign = Boolean.parseBoolean(root.getAttributeValue("signed"));
	}
	
	public static DBTypeRecord getRecordByOriginalType(List<DBTypeRecord> recs, String name) {
		for (DBTypeRecord rec : recs) {
			if (rec.getOriginalType().equalsIgnoreCase(name))
				return rec;
		}
		return null;
	}

	public boolean isSupportPK() {
		return supportPK;
	}

	public boolean isSupportSign() {
		return supportSign;
	}
	
}
