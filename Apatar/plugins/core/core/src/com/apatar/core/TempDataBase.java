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
//http://docs.codehaus.org/display/CASTOR/Type+Mapping
package com.apatar.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.apatar.ui.ApatarUiMain;

public class TempDataBase {
	Connection connection;
	Statement statement;
	TempDatabaseJdbcParams jdbcParams = new TempDatabaseJdbcParams();

	static final DataBaseInfo dataBaseInfo = new DataBaseInfo("", "", "\"",
			"\"", true, true, true, true, false);

	static {

		List<DBTypeRecord> rcList = dataBaseInfo.getAvailableTypes();
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "BIGINT", 8, 8, false,
				true, Types.BIGINT));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "INT", 4, 4, false,
				true, Types.INTEGER));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "INTEGER", 4, 4,
				false, true, Types.INTEGER));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "BLOB", 0, (int) Math
				.pow(2, 16), false, false, Types.BLOB));
		rcList.add(new DBTypeRecord(ERecordType.Boolean, "SMALLINT", 2, 2,
				false, true, Types.BOOLEAN));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DOUBLE", 8, 8, false,
				true, Types.DOUBLE));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DECIMAL", 8, 8,
				false, true, Types.DECIMAL));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "REAL", 4, 4, false,
				true, Types.REAL));
		rcList.add(new DBTypeRecord(ERecordType.Text, "VARCHAR", 1, 32672,
				false, false, Types.VARCHAR));
		// rcList.add(new DBTypeRecord(ERecordType.Decimal, "NUMERIC", 16, 16,
		// false, true, Types.NUMERIC));
		// rcList.add(new DBTypeRecord(ERecordType.Boolean, "CHAR FOR BIT DATA",
		// 1, 1, false, false, Types.BIT));
		rcList.add(new DBTypeRecord(ERecordType.LongText, "LONG VARCHAR", 1,
				32000, false, false, Types.LONGVARCHAR));
		rcList.add(new DBTypeRecord(ERecordType.Date, "DATE", 4, 4, false,
				false, Types.DATE));
		rcList.add(new DBTypeRecord(ERecordType.Time, "TIME", 3, 3, false,
				false, Types.TIME));
		rcList.add(new DBTypeRecord(ERecordType.Timestamp, "TIMESTAMP", 8, 8,
				false, false, Types.TIMESTAMP));
		rcList.add(new DBTypeRecord(ERecordType.Clob, "CLOB", 0, 255, false,
				false, Types.CLOB));
		rcList.add(new DBTypeRecord(ERecordType.VarBinary,
				"VARCHAR(%d) FOR BIT DATA", 0, 65535, false, false,
				Types.VARBINARY));
		rcList.add(new DBTypeRecord(ERecordType.LongVarBinary,
				"LONG VARCHAR FOR BIT DATA", 0, 65535, false, false,
				Types.LONGVARBINARY));

	}

	public JdbcParams getJdbcParams() {
		return jdbcParams;
	}

	public TempDataBase() {
		super();
		jdbcParams.setUserName("");
		jdbcParams.setPassword(new PasswordString());
		jdbcParams.setJdbcDriver("org.apache.derby.jdbc.EmbeddedDriver");

		try {

			statement = jdbcParams.getStatement();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			JOptionPane
					.showMessageDialog(
							ApatarUiMain.MAIN_FRAME,
							"The application is already runnning.\nOnly one instance of the application is allowed at a moment.");
			e.printStackTrace();
			System.exit(0);
		}

	}

	public TableInfo addTable(String tableName, ArrayList<Record> records)
			throws Exception {
		TableInfo tableInfo = new TableInfo();
		tableInfo.setTableName(tableName);
		DataBaseTools.createTable(jdbcParams, dataBaseInfo, records, tableName);
		return tableInfo;
	}

	private void addTable(TableInfo ti) throws Exception {
		SchemaTable schema = ti.getSchemaTable();
		List<Record> records = schema.getRecords();
		if (records.size() == 0) {
			return;
		}

		DataBaseTools.createTable(jdbcParams, dataBaseInfo, records, ti
				.getTableName());

	}

	public void executeQuery(String sql) throws SQLException {
		statement.executeQuery(sql);
	}

	public Object getDatabaseParam() {
		return jdbcParams;
	}

	// make sure that table really exists
	// and it has sufficient table structure
	public void EnsureTableExists(TableInfo ti) throws Exception {
		addTable(ti);
	}

	public Statement getStatement() {
		return statement;
	}

	public DataBaseInfo getDataBaseInfo() {
		return dataBaseInfo;
	}

	public Connection getConnection() {
		return connection;
	}

}
