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

public class TableConditionWrapper {
	private String tableName1;
	private String tableName2;
	private List<Condition> conditions;
	public TableConditionWrapper(String tableName1, String tableName2, List<Condition> conditions) {
		super();
		this.tableName1 = tableName1;
		this.tableName2 = tableName2;
		this.conditions = conditions;
	}
	
	public String getTableName1() {
		return tableName1;
	}
	public void setTableName1(String tableName1) {
		this.tableName1 = tableName1;
	}
	
	public List<Condition> getConditions() {
		return conditions;
	}
	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}
	
	public String getTableName2() {
		return tableName2;
	}
	public void setTableName2(String tableName2) {
		this.tableName2 = tableName2;
	}
}
