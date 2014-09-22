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

public class DataBaseInfo {

	String startSymbolEdgingTableName;
	String finishSymbolEdgingTableName;
	String startSymbolEdgingFieldName;
	String finishSymbolEdgingFieldName;
	boolean supportUpdateMode;
	boolean supportInsertMode;
	boolean supportDeleteMode;
	boolean supportClearData;
	boolean useTableName;
	boolean supportSynchronize = false;

	List<DBTypeRecord> availableTypes = new ArrayList<DBTypeRecord>();

	public DataBaseInfo(String startSymbolEdgingTableName,
			String finishSymbolEdgingTableName,
			String startSymbolEdgingFieldName,
			String finishSymbolEdgingFieldName, boolean supportUpdateMode,
			boolean supportInsertMode, boolean supportClearData,
			boolean useTableName, boolean supportDeleteMode) {
		super();
		this.startSymbolEdgingTableName = startSymbolEdgingTableName;
		this.finishSymbolEdgingTableName = finishSymbolEdgingTableName;
		this.startSymbolEdgingFieldName = startSymbolEdgingFieldName;
		this.finishSymbolEdgingFieldName = finishSymbolEdgingFieldName;
		this.supportUpdateMode = supportUpdateMode;
		this.supportInsertMode = supportInsertMode;
		this.supportClearData = supportClearData;
		this.useTableName = useTableName;
		this.supportDeleteMode = supportDeleteMode;
	}

	public DataBaseInfo(String startSymbolEdgingTableName,
			String finishSymbolEdgingTableName,
			String startSymbolEdgingFieldName,
			String finishSymbolEdgingFieldName, boolean supportUpdateMode,
			boolean supportInsertMode, boolean supportClearData,
			boolean useTableName) {
		super();
		this.startSymbolEdgingTableName = startSymbolEdgingTableName;
		this.finishSymbolEdgingTableName = finishSymbolEdgingTableName;
		this.startSymbolEdgingFieldName = startSymbolEdgingFieldName;
		this.finishSymbolEdgingFieldName = finishSymbolEdgingFieldName;
		this.supportUpdateMode = supportUpdateMode;
		this.supportInsertMode = supportInsertMode;
		this.supportClearData = supportClearData;
		this.useTableName = useTableName;
		supportDeleteMode = false;
	}

	public DataBaseInfo(String startSymbolEdgingTableName,
			String finishSymbolEdgingTableName,
			String startSymbolEdgingFieldName,
			String finishSymbolEdgingFieldName, boolean supportUpdateMode,
			boolean supportInsertMode, boolean supportClearData,
			boolean supportSynchronize, boolean useTableName,
			boolean supportDeleteMode) {
		this(startSymbolEdgingTableName, finishSymbolEdgingTableName,
				startSymbolEdgingFieldName, finishSymbolEdgingFieldName,
				supportUpdateMode, supportInsertMode, supportClearData,
				useTableName, supportDeleteMode);
		this.supportSynchronize = supportSynchronize;
	}

	public boolean isSupportClearData() {
		return supportClearData;
	}

	public boolean isSupportUpdateMode() {
		return supportUpdateMode;
	}

	public boolean isSupportInsertMode() {
		return supportInsertMode;
	}

	public boolean isUseTableName() {
		return useTableName;
	}

	public String getFinishSymbolEdgingFieldName() {
		return finishSymbolEdgingFieldName;
	}

	public String getFinishSymbolEdgingTableName() {
		return finishSymbolEdgingTableName;
	}

	public String getStartSymbolEdgingFieldName() {
		return startSymbolEdgingFieldName;
	}

	public String getStartSymbolEdgingTableName() {
		return startSymbolEdgingTableName;
	}

	public List<DBTypeRecord> getAvailableTypes() {
		return availableTypes;
	}

	public boolean isSupportSynchronization() {
		return supportSynchronize;
	}

	/**
	 * @return the supportDeleteMode
	 */
	public boolean isSupportDeleteMode() {
		return supportDeleteMode;
	}
}
