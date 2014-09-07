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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DataProcessingInfo {
	private final String tableName;
	private final List<Record> columnsForProcess;
	private final JdbcParams params;
	private final DataBaseInfo dbi;

	/*
	 * TO AVOID PROBLEMS DON'T CREATE THIS METHOD public
	 * DataProcessingInfo(TableInfo ti, Connection conn) {
	 * this(ti.getTableName(), ti.getSchemaTable().getRecords(), conn); }
	 */
	public DataProcessingInfo(DataBaseInfo dbi, String tableName,
			List<Record> records) {
		this(dbi, tableName, records, ApplicationData.getTempJDBC());
	}

	public DataProcessingInfo(DataBaseInfo dbi, String tableName,
			List<Record> records, JdbcParams params) {
		super();
		this.tableName = tableName;
		columnsForProcess = records;
		this.params = params;
		this.dbi = dbi;
	}

	public Connection getConnection() throws ClassNotFoundException,
			SQLException {
		return params.getConnection();
	}

	public List<Record> getColumnsForProcess() {
		return columnsForProcess;
	}

	public String getTableName() {
		return tableName;
	}

	public DataBaseInfo getDataBaseInfo() {
		return dbi;
	}

	public JdbcParams getJdbcParams() {
		return params;
	}

	/**
	 * @return the preparedTableName
	 */
	public String getPreparedTableName() {
		return dbi.getStartSymbolEdgingTableName() + tableName
				+ dbi.getFinishSymbolEdgingTableName();
	}

	/**
	 * @return the preparedColumnsForProcess
	 */
	public String getPreparedColumnsName(String columnName) {
		return dbi.getStartSymbolEdgingFieldName() + columnName
				+ dbi.getFinishSymbolEdgingFieldName();
	}

}
