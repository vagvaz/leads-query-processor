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
 


package com.apatar.core;

import java.util.ArrayList;
import java.util.List;

public class SynchronizationRecord {
	ArrayList<SynchronizationField> fields = new ArrayList<SynchronizationField>();
	
	public void addField(SynchronizationField field) {
		fields.add(field);
	}
	public void removeField(SynchronizationField field) {
		fields.remove(field);
	}
	public void removeField(String fieldName) {
		for (SynchronizationField field : fields) {
			if (field.getName().equalsIgnoreCase(fieldName)) {
				fields.remove(field);
			}
		}
	}
	
	public void setFieldValue(String fieldName, Object value) {
		for (SynchronizationField field : fields) {
			if (field.getName().equalsIgnoreCase(fieldName)) {
				field.setValue(value);
			}
		}
	}
	
	public Object getFieldValue(String fieldName) {
		for (SynchronizationField field : fields) {
			if (field.getName().equalsIgnoreCase(fieldName)) {
				return field.getValue();
			}
		}
		return null;
	}
	
	public static boolean isPresent(List<SynchronizationRecord> sRecs, SynchronizationRecord sRec, List<String> identif) {
		for (SynchronizationRecord sr : sRecs) {
			boolean eq = true;
			for (String fn : identif) {
				Object obj1 = sr.getFieldValue(fn);
				Object obj2 = sRec.getFieldValue(fn);
				if (obj1 == null || obj2 == null) {
					eq = false;
					break;
				}
				if (!obj2.equals(obj1)) {
					eq = false;
					break;
				}
			}
			if (eq == true)
				return true;
		}
		return false;
	}
}

