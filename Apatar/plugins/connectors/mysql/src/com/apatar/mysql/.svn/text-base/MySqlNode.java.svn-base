/*
_______________________
Apatar Open Source Data Integration
Copyright (C) 2005-2007, Apatar, Inc.
info@apatar.com
195 Meadow St., 2nd Floor
Chicopee, MA 01013

��� This program is free software; you can redistribute it and/or modify
��� it under the terms of the GNU General Public License as published by
��� the Free Software Foundation; either version 2 of the License, or
��� (at your option) any later version.

��� This program is distributed in the hope that it will be useful,
��� but WITHOUT ANY WARRANTY; without even the implied warranty of
��� MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.� See the
��� GNU General Public License for more details.

��� You should have received a copy of the GNU General Public License along
��� with this program; if not, write to the Free Software Foundation, Inc.,
��� 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
________________________

 */

package com.apatar.mysql;

import java.sql.DatabaseMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import propertysheet.JPropertySheetPage;

import com.apatar.core.AbstractApatarActions;
import com.apatar.core.AbstractJdbcDataBase;
import com.apatar.core.ApatarException;
import com.apatar.core.ApatarRegExp;
import com.apatar.core.ApplicationData;
import com.apatar.core.DBTypeRecord;
import com.apatar.core.DataBaseInfo;
import com.apatar.core.ERecordType;
import com.apatar.core.JdbcParams;
import com.apatar.core.Record;
import com.apatar.core.TableInfo;
import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.JdbcRecordSourceDescriptor;
import com.apatar.ui.wizard.TableModeDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;
import com.mysql.jdbc.ResultSetMetaData;

public class MySqlNode extends AbstractJdbcDataBase {
	static final DataBaseInfo dataBaseInfo = new DataBaseInfo("`", "`", "`",
			"`", true, true, true, true, true);

	static {
		List<DBTypeRecord> rcList = dataBaseInfo.getAvailableTypes();
		rcList.add(new DBTypeRecord(ERecordType.Boolean, "BOOL", 1, 1, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "BIT", 64, 64, false,
				true));

		rcList.add(new DBTypeRecord(ERecordType.Numeric, "BIGINT", 8, 8, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "UNSIGNED BIGINT", 8,
				8, false, true, Types.BIGINT));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "INT", 4, 4, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "UNSIGNED INT", 4, 4,
				false, true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "INTEGER", 4, 4, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "MEDIUMINT", 3, 3,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "SMALLINT", 2, 2,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "TINYINT", 1, 1, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "FLOAT", 4, 4, false,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DOUBLE", 8, 8, false,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "REAL", 8, 8, false,
				true));

		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DECIMAL", 8, 8,
				false, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DEC", 8, 8, false,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "BLOB", 0, (int) Math
				.pow(2, 16), false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "LONGBLOB", 0,
				(int) Math.pow(2, 32), false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "MEDIUMBLOB", 0,
				16777216, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "TINYBLOB", 0, 255,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.VarBinary, "VARBINARY", 0,
				65535, false, false));

		rcList.add(new DBTypeRecord(ERecordType.Date, "DATE", 3, 3, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Time, "TIME", 3, 3, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Timestamp, "DATETIME", 8, 8,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Timestamp, "TIMESTAMP", 4, 4,
				false, false));

		rcList.add(new DBTypeRecord(ERecordType.Clob, "TEXT", 0, (int) Math
				.pow(2, 16), false, false));
		rcList.add(new DBTypeRecord(ERecordType.Clob, "TINYTEXT", 1, 255,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Clob, "MEDIUMTEXT", 1,
				(int) Math.pow(2, 24), false, false));
		rcList.add(new DBTypeRecord(ERecordType.Clob, "LONGTEXT", 1, (int) Math
				.pow(2, 32), false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "CHAR", 1, 255, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "VARCHAR", 1, 255, false,
				true));

		rcList.add(new DBTypeRecord(ERecordType.Enum, "ENUM", 1, 2, false,
				false));
		rcList
				.add(new DBTypeRecord(ERecordType.Enum, "SET", 1, 8, false,
						false));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "YEAR", 2, 2, true,
				false));

	}

	public MySqlNode() {
		super();
		title = "MySQL";
	}

	@Override
	public ImageIcon getIcon() {
		return MySqlUtils.WRITE_MYSQL_NODE_ICON;
	}

	@Override
	public void createDatabaseParam(Wizard wizard) {

		try {
			WizardPanelDescriptor descriptor1 = new DBConnectionDescriptor(
					this, new JPropertySheetPage(wizard.getDialog()),
					JdbcRecordSourceDescriptor.IDENTIFIER, ApplicationData
							.classForName("com.apatar.mysql.MySqlJdbcParams"),
					"db_connector", "mysql");
			wizard.registerWizardPanel(DBConnectionDescriptor.IDENTIFIER,
					descriptor1);

			WizardPanelDescriptor descriptor2 = new JdbcRecordSourceDescriptor(
					this, DBConnectionDescriptor.IDENTIFIER,
					TableModeDescriptor.IDENTIFIER);
			wizard.registerWizardPanel(JdbcRecordSourceDescriptor.IDENTIFIER,
					descriptor2);

			WizardPanelDescriptor descriptor3 = new TableModeDescriptor(this,
					JdbcRecordSourceDescriptor.IDENTIFIER,
					WizardPanelDescriptor.FINISH);
			wizard.registerWizardPanel(TableModeDescriptor.IDENTIFIER,
					descriptor3);

			wizard.setKeyForReferringToDescription("help.connector.mysql");
			wizard.setCurrentPanel(DBConnectionDescriptor.IDENTIFIER,
					Wizard.NEXT_BUTTON_ACTION_COMMAND);
			wizard.showModalDialog();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public DataBaseInfo getDataBaseInfo() {
		return dataBaseInfo;
	}

	@Override
	protected void TransformTDBtoRDB(int mode) {
		super.TransformTDBtoRDB(mode, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.apatar.core.AbstractJdbcDataBase#moveDataFromTempToReal(java.util
	 * .List)
	 */
	@Override
	public void moveDataFromTempToReal(List<String> identificationFields,
			TableInfo inputTi) {
		super.moveDataFromTempToReal(identificationFields, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.AbstractJdbcDataBase#TransformRDBtoTDB()
	 */
	@Override
	public void TransformRDBtoTDB() {
		super.TransformRDBtoTDB(true);
	}

	private String limitFromQuery = "";
	private String beforeLimitQuery = "";

	private List<String> addDistinct(List<String> list, String value) {
		if (!list.contains(value)) {
			list.add(value);
		}
		return list;
	}

	private List<String> getTablesFromQuery(String query) throws Exception {
		List<String> tables = new ArrayList<String>();
		String from = ApatarRegExp
				.getSubstrings(
						"(?i)(?s).*?from(.*?)((join)|(where)|(GROUP)|(HAVING)|(HAVING)|(LIMIT)|(PROCEDURE)).*",
						query, 1).trim();
		if (ApatarRegExp.matchRegExp(
				"(?i)(?s).*((inner)|(outer)|(left)|(right))", from)) {
			from = ApatarRegExp.getSubstrings(
					"(?i)(?s)(.*)\\s((inner)|(outer)|(left)|(right))", from, 1);
		}
		String from_tables[] = from.split(",");
		for (String string : from_tables) {
			tables = addDistinct(tables, clearTableName(string));
		}
		try {
			String joins = ApatarRegExp
					.getSubstrings(
							"(?i)(?s).*from.*?(join.*?)((where)|(GROUP)|(HAVING)|(HAVING)|(LIMIT)|(PROCEDURE)).*",
							query, 1).trim();
			List<String> joinsArray = ApatarRegExp.getAllSubstrings(
					"(?s)(?i)join\\s(.*?)\\s", joins, 1);
			for (String string : joinsArray) {
				tables = addDistinct(tables, clearTableName(string));
			}
		} catch (Exception e) {
		}

		return tables;
	}

	private String clearTableName(String tn) throws Exception {
		tn = tn.trim() + " ";
		tn = ApatarRegExp.getSubstrings("(?i)(.*?)\\s.*", tn, 1).trim();
		tn = tn.replaceAll(getDataBaseInfo().getFinishSymbolEdgingTableName(),
				"");
		tn = tn.replaceAll(getDataBaseInfo().getStartSymbolEdgingTableName(),
				"");
		return tn;
	}

	@Override
	public List<Record> getFieldList(AbstractApatarActions action)
			throws Exception {
		List<Record> result = new ArrayList<Record>();

		JdbcParams params = (JdbcParams) ApplicationData.getProject()
				.getProjectData(getConnectionDataID()).getData();

		String sqlQuery = params.getSqlQuery();

		if (ApatarRegExp.matchRegExp("(?m)(?s)(?i).*limit\\s\\d+", params
				.getSqlQuery().trim())
				|| ApatarRegExp.matchRegExp(
						"(?m)(?s)(?i).*limit\\s\\d+,.*?\\d+", params
								.getSqlQuery().trim())) {
			limitFromQuery = ApatarRegExp.getSubstrings(
					"(?m)(?s)(?i).*(limit.*)", params.getSqlQuery(), 1);
			beforeLimitQuery = ApatarRegExp.getSubstrings(
					"(?m)(?s)(?i)(.*)limit.*", params.getSqlQuery(), 1);
			sqlQuery = beforeLimitQuery;
		}
		sqlQuery = sqlQuery + " limit 0";

		com.mysql.jdbc.PreparedStatement ps = (com.mysql.jdbc.PreparedStatement) params
				.getConnection().prepareStatement(sqlQuery);
		com.mysql.jdbc.ResultSetMetaData rsmd;
		try {
			rsmd = (ResultSetMetaData) ps.getMetaData();
		} catch (Exception e) {

			System.err.println("Error  occured. Driver says: `"
					+ e.getMessage() + "`");
			ps = (com.mysql.jdbc.PreparedStatement) params.getConnection()
					.prepareStatement(sqlQuery);
			ps.setFetchSize(0);

			rsmd = (ResultSetMetaData) ps.executeQuery().getMetaData();
		}
		if (rsmd == null) {
			ps = (com.mysql.jdbc.PreparedStatement) params.getConnection()
					.prepareStatement(sqlQuery);
			ps.setFetchSize(0);

			rsmd = (ResultSetMetaData) ps.executeQuery().getMetaData();
		}

		// for (String table : tables) {
		// tablesMetaData.add(getTableMetaData(table, params));
		// }
		TableMetaData tmd = new TableMetaData(rsmd.toString(), params);
		System.out.println(rsmd.toString());
		ArrayList<String> notSupportedTypes = new ArrayList<String>();
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {

			ColumnMetaData columnTMD = tmd.getField(i - 1);
			if (columnTMD == null) {
				throw new ApatarException(
						"Can't get table metadata from server");
			}
			// drop signed/unsigned word
			String columnTypeName = columnTMD.getDataType();

			System.out.println("columnTypeName `" + columnTMD.getColumnName()
					+ "`=" + columnTypeName);
			DBTypeRecord typeRec = DBTypeRecord.getRecordByOriginalType(
					getDataBaseInfo().getAvailableTypes(), columnTypeName);

			if (typeRec == null) {
				// ApplicationData.ProcessingProgress.Log("Type " +
				// columnTypeName + "is not ...." );
				notSupportedTypes.add(columnTypeName);
				continue;
			}
			boolean isNullable;
			try {
				isNullable = DatabaseMetaData.columnNullable == rsmd
						.isNullable(i);
			} catch (Exception e) {
				isNullable = true;
			}
			long columnDisplaySize = 0;
			try {
				columnDisplaySize = rsmd.getColumnDisplaySize(i);
			} catch (Exception e) {
			}
			Record rec = new Record(typeRec, columnTMD.getColumnName(),
					columnDisplaySize, isNullable, rsmd.isSigned(i), false);
			// System.out.println("Detected MySQL data type: `" +
			// rec.getSqlType()
			// + "`");
			result.add(rec);
		}

		if (notSupportedTypes.size() > 0 && (null != action)) {

			String message = "The following data types are not supported: ";
			for (String type : notSupportedTypes) {
				message += "\n" + type;
			}
			action.dialogAction(message);
		}
		return result;
	}
}
