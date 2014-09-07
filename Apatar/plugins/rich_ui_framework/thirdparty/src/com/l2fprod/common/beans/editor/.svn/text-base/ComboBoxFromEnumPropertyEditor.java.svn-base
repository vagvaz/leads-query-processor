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

package com.l2fprod.common.beans.editor;

import java.lang.reflect.Field;

public class ComboBoxFromEnumPropertyEditor extends ComboBoxPropertyEditor {
	public void setValue(Object value) {
		Class c = value.getClass();
		Field[] fields = c.getDeclaredFields();
		Object[] objs = new Object[fields.length-1];
		int j = 0;
		for(int i=0; i < fields.length; i++) {
			if (!fields[i].getName().equalsIgnoreCase("ENUM$VALUES")) {
				try {
					objs[j++] = fields[i].get(null);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		super.setAvailableValues(objs);
		super.setValue(value);
	}
}
