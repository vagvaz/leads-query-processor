/*TODO recorded refactoring
 * метод public static List<Record> getAllFieldFromTable(JdbcParams params, String tableName, DataBaseInfo bi, boolean withMessage) throws SQLException, ClassNotFoundException
 * удалён как неиспользуемый
 * *********************
 */

/*
 _______________________
 Apatar Open Source Data Integration
 Copyright (C) 2005-2007, Apatar, Inc.
 info@apatar.com
 195 Meadow St., 2nd Floor
 Chicopee, MA 01013

 пїЅпїЅпїЅ This program is free software; you can redistribute it and/or modify
 пїЅпїЅпїЅ it under the terms of the GNU General Public License as published by
 пїЅпїЅпїЅ the Free Software Foundation; either version 2 of the License, or
 пїЅпїЅпїЅ (at your option) any later version.

 пїЅпїЅпїЅ This program is distributed in the hope that it will be useful,
 пїЅпїЅпїЅ but WITHOUT ANY WARRANTY; without even the implied warranty of
 пїЅпїЅпїЅ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.пїЅ See the
 пїЅпїЅпїЅ GNU General Public License for more details.

 пїЅпїЅпїЅ You should have received a copy of the GNU General Public License along
 пїЅпїЅпїЅ with this program; if not, write to the Free Software Foundation, Inc.,
 пїЅпїЅпїЅ 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 ________________________

 */

package com.apatar.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;

// http://docs.codehaus.org/display/CASTOR/Type+Mapping
/*
 * TABLE_CAT String => table catalog (may be null) TABLE_SCHEM String => table
 * schema (may be null) TABLE_NAME String => table name TABLE_TYPE String =>
 * table type. Typical types are "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL
 * TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM". REMARKS String =>
 * explanatory comment on the table TYPE_CAT String => the types catalog (may be
 * null) TYPE_SCHEM String => the types schema (may be null) TYPE_NAME String =>
 * type name (may be null) SELF_REFERENCING_COL_NAME String => name of the
 * designated "identifier" column of a typed table (may be null) REF_GENERATION
 * String => specifies how values in SELF_REFERENCING_COL_NAME are created.
 * Values are "SYSTEM", "USER", "DERIVED". (may be null) int getColumnType(int
 * column) Retrieves the designated column's SQL type. String
 * getColumnTypeName(int column) Retrieves the designated column's
 * database-specific type name. boolean isAutoIncrement(int column) Indicates
 * whether the designated column is automatically numbered, thus read-only. int
 * isNullable(int column) Indicates the nullability of values in the designated
 * column. boolean isReadOnly(int column) Indicates whether the designated
 * column is definitely not writable. boolean isWritable(int column)
 */

/**
 * @author sm
 * 
 */
public class DataBaseTools {

	// ************************************************************************************************
	// Genereal JDBC helpers (metadata)
	// ************************************************************************************************

	public static PreparedStatement insertPs = null;
	public static PreparedStatement updatePs = null;
	public static PreparedStatement deletePs = null;
	public static PreparedStatement selectPs = null;

	public static void completeTransfer() {
		try {
			insertPs.close();
		} catch (Exception e) {
		}
		insertPs = null;
		try {
			selectPs.close();
		} catch (Exception e) {
		}
		selectPs = null;
		try {
			updatePs.close();
		} catch (Exception e) {
		}
		updatePs = null;
		deletePs = null;
		try {
			deletePs.close();
		} catch (Exception e) {
		}
		deletePs = null;
	}

	// return table list for this connection
	public static List<RDBTable> getTableList(JdbcParams params)
			throws Exception {
		Connection con = params.getConnection();
		DatabaseMetaData databaseMetaData = con.getMetaData();
		String[] tableTypes = { "TABLE" };

		ResultSet rs = databaseMetaData.getTables(null, null, null, tableTypes);
		ArrayList<RDBTable> rv = new ArrayList<RDBTable>();
		while (rs.next()) {
			rv.add(new RDBTable(rs.getString("TABLE_NAME"),
					ETableMode.ReadWrite));
		}
		// con.close();
		rs.close();
		return rv;
	}

	public static List<Record> getFieldWithSqlQuery(JdbcParams params,
			String sqlQuery, DataBaseInfo bi, AbstractApatarActions action)
			throws SQLException, ClassNotFoundException {
		/*
		 * ResultSet rsPk =
		 * params.getConnection().getMetaData().getPrimaryKeys(null, null,
		 * tableName); List<String> pkNames = new ArrayList<String>();
		 * while(rsPk.next()) { pkNames.add(rsPk.getString("COLUMN_NAME")); }
		 * rsPk.close();
		 */

		PreparedStatement ps = params.getConnection()
				.prepareStatement(sqlQuery);

		ResultSetMetaData rsmd;
		try {
			rsmd = ps.getMetaData();
		} catch (Exception e) {

			System.err.println("Error occured. Driver says: `" + e.getMessage()
					+ "`");
			ps = params.getConnection().prepareStatement(sqlQuery);
			ps.setFetchSize(0);

			rsmd = ps.executeQuery().getMetaData();
		}
		if (rsmd == null) {
			ps = params.getConnection().prepareStatement(sqlQuery);
			ps.setFetchSize(0);

			rsmd = ps.executeQuery().getMetaData();
		}

		return createFieldList(rsmd, bi, action);
	}

	/**
	 * @param rsmd
	 * @param bi
	 * @param withMessage
	 *            - should application show message window or not
	 * @return
	 * @throws SQLException
	 */
	private static List<Record> createFieldList(ResultSetMetaData rsmd,
			DataBaseInfo bi, AbstractApatarActions action) throws SQLException {
		List<Record> res = new ArrayList<Record>();
		ArrayList<String> notSupportedTypes = new ArrayList<String>();
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			// drop signed/unsigned word
			String columnTypeName = rsmd.getColumnTypeName(i).toUpperCase();
			columnTypeName = columnTypeName.replace(" SIGNED", "").trim();
			columnTypeName = columnTypeName.replace(" UNSIGNED", "").trim();

			System.out.println("columnTypeName `" + rsmd.getColumnName(i)
					+ "`=" + columnTypeName);
			DBTypeRecord typeRec = DBTypeRecord.getRecordByOriginalType(bi
					.getAvailableTypes(), columnTypeName);

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
			Record rec = new Record(typeRec, rsmd.getColumnName(i),
					columnDisplaySize, isNullable, rsmd.isSigned(i), false);
			res.add(rec);
		}

		if (notSupportedTypes.size() > 0 && (null != action)) {

			String message = "The following data types are not supported:";
			for (String type : notSupportedTypes) {
				message += "\n" + type;
			}
			action.dialogAction(message);
		}
		return res;
	}

	// ***********************************************************************************************
	// Intersection processing
	// ***********************************************************************************************
	// TODO - change intersection with accordance to data types
	public static List<Record> intersectionRecords(List<Record> rec1,
			List<Record> rec2, boolean mainList) {
		List<Record> res = new ArrayList<Record>();
		for (Record r1 : rec1) {
			for (Record r2 : rec2) {
				if (r1.getFieldName().equalsIgnoreCase(r2.getFieldName())) {
					if (mainList) {
						res.add(r1);
					} else {
						res.add(r2);
					}
					break;
				}
			}
		}
		return res;
	}

	public static List<Record> intersectionRecords(List<Record> rec,
			KeyInsensitiveMap datas, boolean mainList) {
		List<Record> res = new ArrayList<Record>();
		for (String name : datas.keySet()) {
			for (Record r : rec) {
				if (name.equalsIgnoreCase(r.getFieldName())) {
					if (mainList) {
						res.add(r);
					} else {
						// this branch is executed rarely.
						// should be checked correctly first
						Record result = new Record(r, name, r.getLength(), r
								.isNullable(), r.isSigned(), r.isPrimaryKey());
						res.add(result);
					}
					break;
				}
			}
		}
		return res;
	}

	// ************************************************************************************************
	// SQL creation
	// ************************************************************************************************
	public static class SQLQueryString {
		public String query = "";
		public List<String> queryOrder = new ArrayList<String>();

		public SQLQueryString() {
			super();
		}
	}

	public static class SQLCreationData {
		public static final String LEFT_JOIN = "left";
		public static final String RIGHT_JOIN = "right";
		public static final String INNER_JOIN = "inner";

		private String joinType = "";
		public List<Record> records = null;
		public String tableName = null;

		public SQLCreationData(List<Record> records, String tableName) {
			super();
			this.records = records;
			this.tableName = tableName;
		}

		public SQLCreationData(List<Record> records, String tableName,
				String joinType) {
			super();
			this.records = records;
			this.tableName = tableName;
			this.joinType = joinType;
		}

		/**
		 * @return the joinType
		 */
		public String getJoinType() {
			return joinType;
		}

		/**
		 * @param joinType
		 *            the joinType to set
		 */
		public void setJoinType(String joinType) {
			this.joinType = joinType;
		}
	}

	// returns array of possible sql string modifications
	private static String[] GetMods() {
		String[] all_modifications = new String[] { "%post_insert%",
				"%post_select%", "%post_update%", "%post_from%",
				"%post_delete%" };
		return all_modifications;
	}

	private static void UpdateModes(SQLQueryString sqs,
			Map<String, String> modifications) {
		// if (modifications == null)
		// return;
		// apply required sql modifications
		for (String mode : GetMods()) {
			if (modifications != null && modifications.containsKey(mode)) {
				sqs.query = sqs.query.replace(mode, modifications.get(mode));
			} else {
				sqs.query = sqs.query.replace(mode, "");
			}
		}
	}

	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	// SQL
	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	public static SQLQueryString CreateSelectString(DataBaseInfo dbi,
			SQLCreationData[] data, Map<String, String> modifications) {
		SQLQueryString sqs = new SQLQueryString();
		// build sql string

		String query = "SELECT %post_select% ";
		String selectFrom = "";

		boolean first = true;
		boolean availability = false;
		for (int i = 0; i < data.length; i++) {
			String tn;
			if (dbi != null) {
				tn = dbi.getStartSymbolEdgingTableName() + data[i].tableName
						+ dbi.getFinishSymbolEdgingTableName();
			} else {
				tn = data[i].tableName;
			}
			if (i > 0) {
				selectFrom += ", ";
			}
			selectFrom += tn;

			for (Object element : data[i].records) {
				Record rec = (Record) element;
				if (!first) {
					query += ", ";
				} else {
					first = false;
				}

				String field = dbi.getStartSymbolEdgingFieldName()
						+ rec.getFieldName()
						+ dbi.getFinishSymbolEdgingFieldName();
				query += (dbi.isUseTableName() == false) ? field : tn + "."
						+ field;
				availability = true;
			}
		}
		query += " FROM " + selectFrom + " %post_from% ";
		sqs.query = query;

		UpdateModes(sqs, modifications);
		return availability ? sqs : null;
	}

	// this method should support several tables info passed
	public static SQLQueryString CreateSelectString(DataBaseInfo dbi,
			SQLCreationData[] data, TableConditionWrapper conditions) {
		String where = " WHERE ";

		boolean first = true;
		for (Condition cond : conditions.getConditions()) {
			if (!first) {
				where += " AND ";
			} else {
				first = false;
			}
			String tn = dbi.getStartSymbolEdgingTableName()
					+ conditions.getTableName1()
					+ dbi.getFinishSymbolEdgingTableName();
			String field = dbi.getStartSymbolEdgingFieldName()
					+ cond.getColumn1() + dbi.getFinishSymbolEdgingFieldName();
			// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			where += (dbi.isUseTableName() == false) ? field : tn + "." + field;
			where += '=';

			tn = dbi.getStartSymbolEdgingTableName()
					+ conditions.getTableName2()
					+ dbi.getFinishSymbolEdgingTableName();
			field = dbi.getStartSymbolEdgingFieldName() + cond.getColumn2()
					+ dbi.getFinishSymbolEdgingFieldName();
			where += (dbi.isUseTableName() == false) ? field : conditions
					.getTableName2()
					+ "." + field;
		}

		Map<String, String> mods = new HashMap<String, String>();
		mods.put("%post_from%", where);

		return CreateSelectString(dbi, data, mods);
	}

	// this method should support several tables info passed using JOIN
	// TODO rewrite this method to make possible to join more than 2 tables
	public static SQLQueryString CreateSelectStringByJoin(DataBaseInfo dbi,
			SQLCreationData[] data, TableConditionWrapper conditions,
			boolean addTableIdentifiersToFNames) {

		String select = "select ";
		int i = 1;
		SQLQueryString res = new SQLQueryString();
		for (SQLCreationData creationData : data) {
			// creationData.records
			String fieldsPrefix = "";
			if (addTableIdentifiersToFNames) {
				fieldsPrefix = creationData.tableName + "_";
			}
			for (Record record : creationData.records) {
				// select += "\"" + record.getFieldName() + "\"" + ", ";
				select += creationData.tableName
						+ "."
						+ "\""
						+ record.getFieldName()
						+ "\""
						+ (addTableIdentifiersToFNames ? " as " + fieldsPrefix
								+ "" + record.getFieldName() : "") + ", ";
				// res.queryOrder.add(record.getFieldName());
			}
			i++;
		}
		String joins = data[1].getJoinType() + " join " + data[1].tableName
				+ " on ";
		select = select.substring(0, select.length() - 2) + " from "
				+ data[0].tableName + " ";

		for (Condition condition : conditions.getConditions()) {
			joins += data[1].tableName + "." + "\"" + condition.getColumn2()
					+ "\"" + " = " + data[0].tableName + "." + "\""
					+ condition.getColumn1() + "\"" + " and ";
		}
		res.query = select + " " + joins.substring(0, joins.length() - 5);

		System.out.println("join query `" + res.query + "`");
		return res;
	}

	// map fields should be identical to the field in database
	public static SQLQueryString CreateSelectString(DataBaseInfo dbi,
			SQLCreationData data, KeyInsensitiveMap mapFields) {
		// if no condition should be added
		if (mapFields == null || mapFields.size() == 0) {
			return CreateSelectString(dbi, new SQLCreationData[] { data },
					(Map<String, String>) null);
		}

		List<String> order = new ArrayList<String>();
		String where = " WHERE ";

		boolean first = true;
		for (String field : mapFields.keySet()) {
			if (!first) {
				where += " AND ";
			} else {
				first = false;
			}

			String tn = dbi.getStartSymbolEdgingTableName() + data.tableName
					+ dbi.getFinishSymbolEdgingTableName();
			String fld = dbi.getStartSymbolEdgingFieldName() + field
					+ dbi.getFinishSymbolEdgingFieldName();
			where += (dbi.isUseTableName() == false) ? fld : tn + "." + fld;
			where += "= ?";

			order.add(field);
		}

		Map<String, String> mods = new HashMap<String, String>();
		mods.put("%post_from%", where);
		SQLQueryString sqs = CreateSelectString(dbi,
				new SQLCreationData[] { data }, mods);
		sqs.queryOrder.addAll(order);
		return sqs;
	}

	// delete sql query
	public static SQLQueryString CreateDeleteString(DataBaseInfo dbi,
			SQLCreationData data, List<String> identificationFields,
			Map<String, String> modifications) {

		if (identificationFields == null) {
			throw new IllegalArgumentException(
					"identificationFields cannot be null");
		}
		SQLQueryString sqs = new SQLQueryString();

		String tn;
		if (dbi != null) {
			tn = dbi.getStartSymbolEdgingTableName() + data.tableName
					+ dbi.getFinishSymbolEdgingTableName();
		} else {
			tn = data.tableName;
		}

		String query = "DELETE FROM %post_delete% " + tn;
		String where = " WHERE ";
		boolean wherefirst = true;

		List<String> updateValuesOrder = new ArrayList<String>();

		for (Record rec : data.records) {
			// TODO case sencitive identification fields problem
			if (identificationFields.contains(rec.getFieldName())) {
				if (!wherefirst) {
					where += " AND ";
				} else {
					wherefirst = false;
				}

				String fld = dbi.getStartSymbolEdgingFieldName()
						+ rec.getFieldName()
						+ dbi.getFinishSymbolEdgingFieldName();
				where += fld + "=?";
				updateValuesOrder.add(rec.getFieldName());
			}
		}
		query += where;

		sqs.query = query;
		sqs.queryOrder.addAll(updateValuesOrder);

		System.out.println("DELETE query: `" + sqs.query + "`");

		UpdateModes(sqs, modifications);
		return sqs;
	}

	// Insert Update
	public static SQLQueryString CreateIUString(DataBaseInfo dbi,
			SQLCreationData data, boolean Insert,
			List<String> identificationFields, Map<String, String> modifications) {
		SQLQueryString sqs = new SQLQueryString();

		String tn;
		if (dbi != null) {
			tn = dbi.getStartSymbolEdgingTableName() + data.tableName
					+ dbi.getFinishSymbolEdgingTableName();
		} else {
			tn = data.tableName;
		}

		String query = Insert ? "INSERT %post_insert% INTO " + tn + " ("
				: "UPDATE %post_update% " + tn + " SET ";
		String where = " WHERE ";
		boolean wherefirst = true;

		String values = "";

		List<String> updateValuesOrder = new ArrayList<String>();

		boolean first = true;
		for (Record rec : data.records) {
			// TODO case sencitive identification fields problem
			if (!Insert && identificationFields != null
					&& identificationFields.contains(rec.getFieldName())) {
				if (!wherefirst) {
					where += " AND ";
				} else {
					wherefirst = false;
				}

				String fld = dbi.getStartSymbolEdgingFieldName()
						+ rec.getFieldName()
						+ dbi.getFinishSymbolEdgingFieldName();
				where += fld + "=?";
				updateValuesOrder.add(rec.getFieldName());
			} else {
				if (!first) {
					query += ", ";
					values += ", ";
				} else {
					first = false;
				}
				// assignment is either with
				String fld = dbi.getStartSymbolEdgingFieldName()
						+ rec.getFieldName()
						+ dbi.getFinishSymbolEdgingFieldName();
				if (Insert) {
					query += fld;
					values += "?";
				} else {
					query += fld + " = ?";
				}

				sqs.queryOrder.add(rec.getFieldName());
			}
		}

		if (Insert) {
			query += ") VALUES (" + values + ")";
		} else {
			query += where;
		}

		sqs.query = query;
		sqs.queryOrder.addAll(updateValuesOrder);

		System.out.println("UI query: `" + sqs.query + "`");

		UpdateModes(sqs, modifications);
		return sqs;
	}

	public static ResultSet getRSWithAllFields(String tableName,
			JdbcParams params, DataBaseInfo dbi) throws SQLException,
			ClassNotFoundException {
		String tn;
		if (dbi != null) {
			tn = dbi.getStartSymbolEdgingTableName() + tableName
					+ dbi.getFinishSymbolEdgingTableName();
		} else {
			tn = tableName;
		}
		Statement st = params.getStatement();
		ResultSet rs = st.executeQuery(String.format("select * from %s", tn));
		return rs;
	}

	public static int getRecordsCount(String tableName, JdbcParams params,
			DataBaseInfo dbi) throws SQLException, ClassNotFoundException {
		String tn;
		if (dbi != null) {
			tn = dbi.getStartSymbolEdgingTableName() + tableName
					+ dbi.getFinishSymbolEdgingTableName();
		} else {
			tn = tableName;
		}
		Statement st = params.getStatement();
		ResultSet rs = st.executeQuery(String.format(
				"select count(*) as cnt from %s", tn));
		rs.next();
		int res = rs.getInt(1);
		rs.close();
		return res;
	}

	public static ResultSet getScrollableRSWithAllFields(String tableName,
			JdbcParams params, DataBaseInfo dbi) throws SQLException,
			ClassNotFoundException {
		String tn;
		if (dbi != null) {
			tn = dbi.getStartSymbolEdgingTableName() + tableName
					+ dbi.getFinishSymbolEdgingTableName();
		} else {
			tn = tableName;
		}
		Statement st = params.getConnection().createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet rs = st.executeQuery(String.format("select * from %s", tn));
		return rs;
	}

	public static int getRScount(String tableName, JdbcParams params,
			DataBaseInfo dbi) throws SQLException, ClassNotFoundException {
		String tn;
		if (dbi != null) {
			tn = dbi.getStartSymbolEdgingTableName() + tableName
					+ dbi.getFinishSymbolEdgingTableName();
		} else {
			tn = tableName;
		}
		Statement st = params.getStatement();
		ResultSet rs = st.executeQuery(String.format("select count(*) from %s",
				tn));
		rs.next();
		return rs.getInt(1);
	}

	public static ResultSet getRSWhithSqlQuery(String sqlQuery,
			JdbcParams params) throws SQLException, ClassNotFoundException {
		Statement st = params.getStatement();
		ResultSet rs = st.executeQuery(sqlQuery);
		return rs;
	}

	// ******************************************************************************************************
	// Typization
	// ******************************************************************************************************
	private static void setObjectInPreparedStatement(PreparedStatement ps,
			int index, Object value) throws SQLException {
		if (value instanceof JdbcObject) {
			int type = ((JdbcObject) value).getType();
			value = ((JdbcObject) value).getValue();
			if (value == null) {
				ps.setNull(index, type);
				return;
			}
		}
		/*
		 * if (value instanceof String) { ps.setString(index, value.toString());
		 * } else
		 */
		if (value instanceof Timestamp) {
			ps.setTimestamp(index, (Timestamp) value);
		} else if (value instanceof Time) {
			ps.setTime(index, (Time) value);
		} else {
			if (value instanceof Date) {
				// convert one date to another date
				ps.setDate(index, new java.sql.Date(((Date) value).getTime()));
			} else if (value instanceof Double) {
				ps.setDouble(index, (Double) value);
			} else if (value instanceof Float) {
				ps.setFloat(index, (Float) value);
			} else if (value instanceof BigInteger) {
				ps.setDouble(index, ((BigInteger) value).doubleValue());
			} else if (value instanceof InputStream) {
				try {
					InputStream is = (InputStream) value;
					if (!(is instanceof FileInputStream)
							&& !(is instanceof ByteArrayInputStream)) {
						File file = ApplicationData.createTempFile(is, "tmp"
								+ index);
						is = new FileInputStream(file);
					}
					ps.setBinaryStream(index, is, is.available());

				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (value instanceof Boolean) {
				ps.setBoolean(index, (Boolean) value);// (index,
				// (Boolean)value ? new
				// ByteArrayInputStream(new
				// byte[] {1}) : new
				// ByteArrayInputStream(new
				// byte[] {0}), 1);
			} else if (value instanceof Blob) {
				ps.setBlob(index, (Blob) value);
			} else if (value instanceof Clob) {
				ps.setClob(index, (Clob) value);
			} else {
				ps.setObject(index, value);
			}
		}
	}

	private static PreparedStatement getPS(Connection conTarget, String query,
			int mode, boolean alwaysNew) throws Exception {
		if (alwaysNew) {
			return conTarget.prepareStatement(query);
		}
		if (mode == AbstractDataBaseNode.INSERT_MODE) {
			if (insertPs == null) {
				insertPs = conTarget.prepareStatement(query);
			}
			return insertPs;
		} else if (mode == AbstractDataBaseNode.UPDATE_MODE) {
			if (updatePs == null) {
				updatePs = conTarget.prepareStatement(query);
			}
			return updatePs;
		} else if (mode == AbstractDataBaseNode.DELETE_MODE) {
			if (deletePs == null) {
				deletePs = conTarget.prepareStatement(query);
			}
			return updatePs;
		} else {
			if (selectPs == null) {
				selectPs = conTarget.prepareStatement(query);
			}
			return selectPs;
		}
	}

	private static PreparedStatement getPreparedStatement(Connection conTarget,
			SQLQueryString sqs, KeyInsensitiveMap data, int mode)
			throws Exception {
		return getPreparedStatement(conTarget, sqs, data, mode, false);
	}

	/*
	 * Returns PreparedStatement
	 */
	private static PreparedStatement getPreparedStatement(Connection conTarget,
			SQLQueryString sqs, KeyInsensitiveMap data, int mode,
			boolean alwaysNew) throws Exception {

		PreparedStatement statement = getPS(conTarget, sqs.query, mode,
				alwaysNew);

		int i = 1;
		for (String field : sqs.queryOrder) {
			// Record rc = Record.getRecordByName(records, field);
			try {
				setObjectInPreparedStatement(statement, i++, data.get(field,
						true));
			} catch (SQLException e) {
				/*
				 * //TODO - there are some kind of specific error about date
				 * format // there are some kind of specific error ( // for
				 * example "Value '0000-00-00' can not be represented as
				 * java.sql.Date" // for mysql - they are to be processed in
				 * every database engine differently if (rc.isNullable(i) ==
				 * ResultSetMetaData.columnNullable) {
				 * System.out.println(e.getMessage());
				 * System.out.println("NullValue: " + i + ":" +
				 * rs.getMetaData().getColumnName(i));
				 * statementInsert.setNull(i, colType); } else {}
				 */
				ApplicationData.ProcessingProgress.Log(e.getMessage());
				System.out.println(field + ": " + e.getMessage());
				// e.printStackTrace();
			}
		}
		return statement;
	}

	public static KeyInsensitiveMap GetDataFromRS(ResultSet rs)
			throws SQLException {
		KeyInsensitiveMap rv = new KeyInsensitiveMap();
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			String nameColumn = rsmd.getColumnName(i);
			rv.put(nameColumn, new JdbcObject(getValueFromResultSet(rs, i),
					rsmd.getColumnType(i)));
		}
		return rv;
	}

	public static KeyInsensitiveMap GetJdbcObjectFromRS(ResultSet rs,
			ResultSetMetaData rsmd, List<Record> recs) throws SQLException {
		KeyInsensitiveMap rv = new KeyInsensitiveMap();
		// ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			String nameColumn = rsmd.getColumnName(i);
			rv
					.put(nameColumn, new JdbcObject(
							getValueFromResultSet(rs, i), Record
									.getRecordByFieldName(recs, nameColumn)
									.getSqlType()));
		}
		return rv;
	}

	public static KeyInsensitiveMap GetJdbcObjectFromRS(ResultSet rs,
			ResultSetMetaData rsmd) throws SQLException {
		KeyInsensitiveMap rv = new KeyInsensitiveMap();
		// ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			String nameColumn = rsmd.getColumnName(i);
			rv.put(nameColumn, new JdbcObject(getValueFromResultSet(rs, i),
					rsmd.getColumnType(i)));
		}
		return rv;
	}

	private static Object getValueFromResultSet(ResultSet rs, int i) {
		Object value = null;
		try {
			ResultSetMetaData rsmd = rs.getMetaData();

			switch (rsmd.getColumnType(i)) {
			case Types.VARCHAR:
				value = rs.getString(i);
				break;
			case Types.CHAR:
				value = rs.getString(i);
				break;
			case Types.DATE:
				value = rs.getDate(i);
				break;
			case Types.TIME:
				value = rs.getTime(i);
				break;
			case Types.TIMESTAMP:
				value = rs.getTimestamp(i);
				break;
			case Types.BLOB:
				value = rs.getBlob(i);
				break;
			default:
				value = rs.getObject(i);
			}
		} catch (SQLException e) {
			ApplicationData.ProcessingProgress.Log(e.getMessage());
		}
		return value;
	}

	// modify prepared sql
	// !!!! this method assumes that ? mark has associated value
	// TODO - modify prepared sql
	public static SQLQueryString getPreparedSQL(SQLQueryString sqs,
			KeyInsensitiveMap data) throws SQLException {
		// replace from end
		for (String field : sqs.queryOrder) {
			Object ob = data.get(field, true);
			if (ob instanceof JdbcObject) {
				ob = ((JdbcObject) ob).getValue();
			}
			ob = ob.toString().replaceAll("\\'", "\\\\'");
			String fieldvalue = ob instanceof String ? String.format("'%s'", ob
					.toString()) : ob.toString();
			sqs.query = sqs.query.replaceFirst("\\?", Matcher
					.quoteReplacement(fieldvalue));
		}
		return sqs;
	}

	public static PreparedStatement getPreparedStatementWithStringData(
			Connection conTarget, SQLQueryString sqs, KeyInsensitiveMap data)
			throws SQLException {
		PreparedStatement statementInsert = conTarget
				.prepareStatement(sqs.query);
		int index = 1;
		for (String field : sqs.queryOrder) {
			Object ob = data.get(field, true);
			if (ob instanceof JdbcObject) {
				ob = ((JdbcObject) ob).getValue();
			}
			// String fieldvalue = ob instanceof String ? String.format("'%s'",
			// ob.toString()) : ob.toString();
			statementInsert.setString(index++, ob.toString());
		}
		return statementInsert;
	}

	// ******************************************************************************************************
	// User - specific functions
	// ******************************************************************************************************

	public static void TransferData(DataProcessingInfo srcPI,
			DataProcessingInfo destPI, int mode,
			List<String> identificationFields, boolean toTemp) throws Exception {
		TransferData(srcPI, destPI, mode, identificationFields, null, toTemp);
	}

	public static void TransferData(DataProcessingInfo srcPI,
			DataProcessingInfo destPI, boolean toTemp) throws Exception {
		TransferData(srcPI, destPI, AbstractDataBaseNode.INSERT_MODE, null,
				toTemp);
	}

	public static void TransferData(DataProcessingInfo srcPI,
			DataProcessingInfo destPI, int mode,
			List<String> identificationFields,
			Map<String, String> modificators, boolean toTemp,
			boolean doCommitAfterProcessing) throws Exception {
		TransferData(srcPI, destPI, mode, identificationFields, modificators,
				toTemp);
		if (doCommitAfterProcessing) {
			destPI.getConnection().commit();
		}
	}
	
	public static void TransferData(DataProcessingInfo srcPI,
			DataProcessingInfo destPI, int mode,
			List<String> identificationFields,
			Map<String, String> modificators, boolean toTemp) throws Exception {
		TransferData(false, srcPI, destPI, mode, identificationFields,
				modificators, toTemp);
	}

	/**
	 * @author apon
	 */
	public static void TransferData(DataProcessingInfo srcPI,
			DataProcessingInfo destPI, int mode,
			List<String> identificationFields, boolean toTemp, LinkedList result) throws Exception {
		TransferData(srcPI, destPI, mode, identificationFields, null, toTemp,result);
	}
	
	/**
	 * @author apon
	 */
	public static void TransferData(DataProcessingInfo srcPI,
			DataProcessingInfo destPI, int mode,
			List<String> identificationFields,
			Map<String, String> modificators, boolean toTemp, LinkedList result) throws Exception {
		
		List<Record> records1 = DataBaseTools.intersectionRecords(srcPI.getColumnsForProcess(), destPI.getColumnsForProcess(), true);
		
		SQLCreationData cds = new SQLCreationData(records1, srcPI
				.getTableName());
		
		List<Record> records2 = DataBaseTools.intersectionRecords(srcPI
				.getColumnsForProcess(), destPI.getColumnsForProcess(), false);
		
		SQLCreationData destcds = new SQLCreationData(records2, destPI
				.getTableName());

		SQLQueryString sss = CreateSelectString(srcPI.getDataBaseInfo(),
				new SQLCreationData[] { cds }, modificators);

		if (sss == null) {
			return;
		}

		ResultSet rs = null;
		Statement st = srcPI.getJdbcParams().getStatement();
		rs = st.executeQuery(sss.query);

		/*
		 * SQLQueryString ins = mode == AbstractDataBaseNode.INSERT_MODE ?
		 * CreateIUString(destPI.getDataBaseInfo(), destcds, true,
		 * identificationFields, modificators) :
		 * CreateIUString(destPI.getDataBaseInfo(), destcds, false,
		 * identificationFields, modificators);
		 */

//		TransferData(rs, mode, destPI, destcds, identificationFields,
//				modificators, toTemp);
		
		while(rs.next()){
			result.add(rs.getString(1));
		}
		
		rs.close();
	}
	
	public static void TransferData(boolean alwaysNewPS,
			DataProcessingInfo srcPI, DataProcessingInfo destPI, int mode,
			List<String> identificationFields,
			Map<String, String> modificators, boolean toTemp) throws Exception {
		TransferData(true, alwaysNewPS, srcPI, destPI, mode,
				identificationFields, modificators, toTemp);
	}

	public static void TransferData(boolean fakeBoolean, boolean alwaysNewPS,
			DataProcessingInfo srcPI, DataProcessingInfo destPI, int mode,
			List<String> identificationFields,
			Map<String, String> modificators, boolean toTemp) throws Exception {
		List<Record> records1 = DataBaseTools.intersectionRecords(srcPI
				.getColumnsForProcess(), destPI.getColumnsForProcess(), true);
		SQLCreationData cds = new SQLCreationData(records1, srcPI
				.getTableName());
		List<Record> records2 = DataBaseTools.intersectionRecords(srcPI
				.getColumnsForProcess(), destPI.getColumnsForProcess(), false);
		SQLCreationData destcds = new SQLCreationData(records2, destPI
				.getTableName());

		SQLQueryString sss = CreateSelectString(srcPI.getDataBaseInfo(),
				new SQLCreationData[] { cds }, modificators);

		if (sss == null) {
			return;
		}

		ResultSet rs = null;
		Statement st = srcPI.getJdbcParams().getStatement();
		rs = st.executeQuery(sss.query);

		/*
		 * SQLQueryString ins = mode == AbstractDataBaseNode.INSERT_MODE ?
		 * CreateIUString(destPI.getDataBaseInfo(), destcds, true,
		 * identificationFields, modificators) :
		 * CreateIUString(destPI.getDataBaseInfo(), destcds, false,
		 * identificationFields, modificators);
		 */

		TransferData(rs, mode, destPI, destcds, identificationFields,
				modificators, toTemp, alwaysNewPS);
		rs.close();
	}

	public static void TransferData(ResultSet rs, int mode,
			DataProcessingInfo destPI, SQLCreationData destcds,
			List<String> identificationFields,
			Map<String, String> modificators, boolean toTemp) throws Exception {
		TransferData(rs, mode, destPI, destcds, identificationFields,
				modificators, toTemp, false);
	}

	public static void TransferData(ResultSet rs, int mode,
			DataProcessingInfo destPI, SQLCreationData destcds,
			List<String> identificationFields,
			Map<String, String> modificators, boolean toTemp,
			boolean alwaysNewPS) throws Exception {
		ResultSetMetaData rsmd = rs.getMetaData();
		while (rs.next()) {
			try {
				KeyInsensitiveMap data = null;
				if (toTemp
						&& ApplicationData.DATAMAP_VERSION
								.compareToIgnoreCase("Apatar_v1.1.2.4") != -1) {
					data = GetJdbcObjectFromRS(rs, rsmd, destPI
							.getColumnsForProcess());
				} else {
					data = GetJdbcObjectFromRS(rs, rsmd);
				}

				// boolean insertResult = false;
				if (mode == AbstractDataBaseNode.INSERT_MODE) {
					SQLQueryString ins = CreateIUString(destPI
							.getDataBaseInfo(), destcds, true,
							identificationFields, modificators);
					PreparedStatement ps = getPreparedStatement(destPI
							.getConnection(), ins, data, mode, alwaysNewPS);
					ps.executeUpdate();
				}

				// in case of update mode or if an insertion failed
				if (mode == AbstractDataBaseNode.UPDATE_MODE
						|| mode == AbstractDataBaseNode.DELETE_MODE) {
					KeyInsensitiveMap selectDatas = new KeyInsensitiveMap();
					KeyInsensitiveMap values = DataBaseTools.GetDataFromRS(rs);
					for (String substr : values.keySet()) {
						if (identificationFields.contains(substr)) {
							selectDatas.put(substr, values.get(substr, false));
						}
					}

					SQLQueryString sqs = DataBaseTools.CreateSelectString(
							destPI.getDataBaseInfo(), destcds, selectDatas);

					if (sqs == null) {
						return;
					}

					// PreparedStatement selectPs = DataBaseTools
					// .getPreparedStatement(destPI.getConnection(), sqs,
					// values, 0);
					sqs = DataBaseTools.getPreparedSQL(sqs, values);

					// ResultSet selectRs = selectPs.executeQuery();
					ResultSet selectRs = executeSelect(sqs.query, destPI
							.getJdbcParams());
					SQLQueryString ins = null;
					if (!selectRs.next()
							&& mode != AbstractDataBaseNode.DELETE_MODE) {
						// insert
						ins = CreateIUString(destPI.getDataBaseInfo(), destcds,
								true, identificationFields, modificators);
					} else {
						if (!selectRs.next()) {
							if (mode == AbstractDataBaseNode.DELETE_MODE) {
								ins = CreateDeleteString(destPI
										.getDataBaseInfo(), destcds,
										identificationFields, modificators);
							} else {
								// update
								ins = CreateIUString(destPI.getDataBaseInfo(),
										destcds, false, identificationFields,
										modificators);
							}
						} else {
							System.err.println("Found more than one match");
							continue;
						}
					}

					// ins = CreateIUString(destPI.getDataBaseInfo(),
					// destcds, false, identificationFields, modificators);
					PreparedStatement ps = getPreparedStatement(destPI
							.getConnection(), ins, data, mode, alwaysNewPS);
					// System.err.println(ins.query);
					try {
						ps.execute();
					} catch (UnsupportedOperationException e) {
						ps.executeUpdate();
					}
					// ps.close();
				}

				// log support
				if (!ApplicationData.ProcessingProgress.Step()) {
					return;
				}

			} catch (SQLException e) {
				e.printStackTrace();

				// log an error and be able to cancel processing if needed
				if (!ApplicationData.ProcessingProgress.Log(e.getMessage())) {
					return;
				}
			}
		}
	}

	public static PreparedStatement getPreparedStatementForInsert(
			String tableName, DataProcessingInfo destPI) throws Exception {
		String insertQuery = "insert into " + tableName;
		String queryFields = "";
		String insertQueryValues = "";

		for (Record mappedColumn : destPI.getColumnsForProcess()) {
			if ("".equals(queryFields)) {
				queryFields += destPI.getPreparedColumnsName(mappedColumn
						.getFieldName());
			} else {
				queryFields += ", "
						+ destPI.getPreparedColumnsName(mappedColumn
								.getFieldName());
			}
			if ("".equals(insertQueryValues)) {
				insertQueryValues += "?";
			} else {
				insertQueryValues += ", ?";
			}
		}
		queryFields = "(" + queryFields + ")";
		insertQueryValues = "(" + insertQueryValues + ")";
		insertQuery += queryFields + " values " + insertQueryValues;

		return destPI.getConnection().prepareStatement(insertQuery);
	}

	public static void TransferData(String sqlQuery, JdbcParams params,
			List<Record> srcRecords, DataProcessingInfo destPI, boolean toTemp)
			throws Exception {
		ResultSet rs = null;
		rs = executeSelect(sqlQuery, params);

		List<Record> records2 = DataBaseTools.intersectionRecords(srcRecords,
				destPI.getColumnsForProcess(), false);
		SQLCreationData destcds = new SQLCreationData(records2, destPI
				.getTableName());

		// SQLQueryString ins = CreateIUString(destPI.getDataBaseInfo(),
		// destcds, true, null, null);

		TransferData(rs, AbstractDataBaseNode.INSERT_MODE, destPI, destcds,
				null, null, toTemp);
		rs.close();
	}

	public static void clearRecords(DataBaseInfo dbi, JdbcParams params,
			String tableName) throws Exception {

		String tn;
		if (dbi != null) {
			tn = dbi.getStartSymbolEdgingTableName() + tableName
					+ dbi.getFinishSymbolEdgingTableName();
		} else {
			tn = tableName;
		}

		Statement st = params.getStatement();

		st.executeUpdate("DELETE FROM " + tn);

		// st.close();
	}

	// works for insert only
	public static void insertData(DataProcessingInfo destinationTableInfo,
			ResultSet rs, ResultSetMetaData rsmd) throws Exception {
		KeyInsensitiveMap data = GetJdbcObjectFromRS(rs, rsmd,
				destinationTableInfo.getColumnsForProcess());
		insertData(destinationTableInfo, data);
	}

	/**
	 * @param destinationTableInfo
	 * @param fields
	 * @param sqs
	 *            - will be returned
	 * @return
	 * @throws Exception
	 */
	public static PreparedStatement prepareToInsert(
			DataProcessingInfo destinationTableInfo, List<Record> fields,
			SQLQueryString sqs) throws Exception {
		PreparedStatement insertPs = null;
		sqs = CreateIUString(
				destinationTableInfo.getDataBaseInfo(),
				new SQLCreationData(fields, destinationTableInfo.getTableName()),
				true, null, null);
		insertPs = destinationTableInfo.getJdbcParams().getConnection()
				.prepareStatement(sqs.query);

		return insertPs;
	}

	public static void insertData(SQLQueryString sqs,
			PreparedStatement statementInsert, KeyInsensitiveMap data)
			throws Exception {
		int i = 1;
		for (String field : sqs.queryOrder) {
			try {
				setObjectInPreparedStatement(statementInsert, i++, data.get(
						field, true));
			} catch (SQLException e) {
				ApplicationData.ProcessingProgress.Log(e.getMessage());
				System.out.println(field + ": " + e.getMessage());
			}
		}
		statementInsert.execute();
		data.clear();
	}

	public static void completeInsert(PreparedStatement statementInsert)
			throws Exception {
		statementInsert.close();
	}

	public static void insertData(DataProcessingInfo destinationTableInfo,
			KeyInsensitiveMap data) throws Exception {
		insertData(destinationTableInfo, data, false);
	}

	public static void insertData(DataProcessingInfo destinationTableInfo,
			KeyInsensitiveMap data, boolean alwaysNewPs) throws Exception {
		List<Record> records = DataBaseTools.intersectionRecords(
				destinationTableInfo.getColumnsForProcess(), data, false);

		// if no records to insert
		if (records.size() == 0) {
			return;
		}

		SQLQueryString sqs = CreateIUString(destinationTableInfo
				.getDataBaseInfo(), new SQLCreationData(records,
				destinationTableInfo.getTableName()), true, null, null);

		PreparedStatement ps = getPreparedStatement(destinationTableInfo
				.getJdbcParams().getConnection(), sqs, data,
				AbstractDataBaseNode.INSERT_MODE, alwaysNewPs);

		ps.execute();

		// ps.close();

		data.clear();
	}

	public static void insertData(DataProcessingInfo destinationTableInfo,
			ResultSet rs, List<Condition> replacements, ResultSet reprs)
			throws Exception {

		KeyInsensitiveMap inputData = GetDataFromRS(rs);
		KeyInsensitiveMap replaceData = GetDataFromRS(reprs);

		for (Condition rep : replacements) {
			inputData.put(rep.column1, replaceData.get(rep.column2, true));
		}

		insertData(destinationTableInfo, inputData);
	}

	public static ResultSet executeSelect(SQLQueryString sqs, JdbcParams params)
			throws SQLException, ClassNotFoundException {
		Statement st = params.getStatement();
		ResultSet rs = st.executeQuery(sqs.query);
		// st.close();
		return rs;
	}

	public static ResultSet executeSelect(String query, JdbcParams params)
			throws SQLException, ClassNotFoundException {
		Statement st = null;
		st = params.getStatement();

		ResultSet rs = st.executeQuery(query);
		// st.close();
		return rs;
	}

	public static int getRecordsCount(String tableName, JdbcParams params)
			throws Exception {
		return executeSelect(
				"select count(*) as \"cnt\" from \"" + tableName + "\"", params)
				.getInt("cnt");
	}

	/**
	 * Executes UPDATE, INSERT or DELETE query
	 * 
	 * @param query
	 * @param params
	 * @param returnGeneratedKeys
	 *            TODO
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static int executeUpdate(String query, JdbcParams params,
			boolean returnGeneratedKeys) throws SQLException,
			ClassNotFoundException {
		Statement st = params.getStatement();

		if (returnGeneratedKeys) {
			return st.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
		} else {
			return st.executeUpdate(query);
		}
	}

	public static void createTable(JdbcParams params,
			DataBaseInfo dataBaseInfo, List<Record> records, String tableName)
			throws SQLException, ClassNotFoundException, Exception {
		StringBuffer createSQL = new StringBuffer("");
		createSQL.append("CREATE TABLE  ");
		createSQL.append(tableName);
		createSQL.append(" (");
		StringBuffer primary = new StringBuffer();
		boolean first = true;
		boolean firstPr = true;
		for (Record record : records) {
			if (!first) {
				createSQL.append(", ");
			} else {
				first = false;
			}
			if (record.isPrimaryKey()) {
				if (!firstPr) {
					primary.append(", ");
				} else {
					firstPr = false;
				}
			}

			String field = dataBaseInfo.getStartSymbolEdgingFieldName()
					+ record.getFieldName()
					+ dataBaseInfo.getFinishSymbolEdgingFieldName();
			createSQL.append(field).append(" ");

			if (record.getType() == ERecordType.VarBinary) {
				long size = record.getLength();
				if (size > 32672) {
					size = 32672;
				}
				createSQL.append(String.format(record.getOriginalType(), size));
			} else {
				String origType = record.getOriginalType();
				if (record.getType() == ERecordType.Text) {
					// createSQL.append("VARCHAR");
					long size = record.getLength();
					if (size > 32672) {
						createSQL.append("LONG VARCHAR");
					} else {
						if (size >= 0) {
							createSQL.append("VARCHAR");
							if (size == 0) {
								size = 1;
							}
							createSQL.append("(").append(size).append(")");
						} else {
							if (record.getRealDBType().equals("TEXT")) {
								createSQL.append("LONG VARCHAR");
							} else {
								throw new ApatarException(
										"Unsupported datatype: `"
												+ record.getRealDBType() + "`");
							}
						}
					}
					/*
					 * if (size >= 0) { if (size == 0) size = 1; }
					 * createSQL.append("(").append(size).append(")");
					 */
				}
				// else
				// if (origType.equalsIgnoreCase("CHAR")) {
				// createSQL.append("VARCHAR");
				// }
				else {
					createSQL.append(origType);
				}
				if (record.isPrimaryKey()) {
					primary.append(record.getFieldName());
				}
			}
		}
		/*
		 * if (!firstPr) createSQL.append(", PRIMARY
		 * KEY(").append(primary).append(")");
		 */
		createSQL.append(")");

		System.out.println(createSQL.toString());
		Statement statement = params.getStatement();
		try {
			System.out
					.println("Trying to DROP table before creating it. Table name `"
							+ tableName + "`");
			String dropTable = "DROP TABLE " + tableName;
			statement.executeUpdate(dropTable);
			System.out
					.println("Table `" + tableName + "` dropped successfully");
		} catch (SQLException e) {
			System.out.println("Failed to drop table `" + tableName + "`");
		}

		try {
			statement.executeUpdate(createSQL.toString());
			System.out
					.println("Table `" + tableName + "` created successfully");
		} catch (Exception e) {
			System.err.println("Failed to create table `" + tableName + "`");
			e.printStackTrace();
		}
	}

	public static void deleteTable(DataBaseInfo dbi, Connection conn,
			String tableName) throws SQLException {
		Statement st = conn.createStatement();
		st.executeUpdate("DROP TABLE " + tableName + " IF EXISTS;");
		st.close();
	}

	public static void shutdownDerbyDB(String name) {
		String url = "jdbc:derby:" + name + ";shutdown=true";
		try {
			Driver driver = (Driver) ApplicationData.classForName(
					"org.apache.derby.jdbc.EmbeddedDriver").newInstance();
			driver.connect(url, new Properties());
		} catch (SQLException e) {
			try {
				if (ApatarRegExp.matchRegExp("Database '.*systemdb' shutdown.",
						e.getMessage())
						&& e.getErrorCode() == 45000
						&& e.getSQLState().equalsIgnoreCase("08006")) {
					System.out.println("Configuration database closed.");
				} else {
					e.printStackTrace();
				}
			} catch (ApatarException e1) {
				e1.printStackTrace();
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static String getOption(String key, Statement st)
			throws SQLException {
		ResultSet rs = st.executeQuery("SELECT value from options where name='"
				+ key + "'");
		if (!rs.next()) {
			return null;
		}
		return rs.getString("value");
	}

	public static void setOption(String key, String value, Statement st)
			throws SQLException {
		String option = getOption(key, st);
		if (option != null) {
			st.executeUpdate("UPDATE options SET value='" + value
					+ "' WHERE name='" + key + "'");
		} else {
			st.executeUpdate("INSERT INTO options VALUES('" + key + "', '"
					+ value + "')");
		}
	}

	/**
	 * Sets value into PreparedStatement
	 * 
	 * @param ps
	 *            - PreparedStatement to set value to
	 * @param value
	 * @param columnType
	 *            - SqlType of the value
	 * @param i
	 *            - index of the PreparedStatement's item
	 * @throws Exception
	 */
	public static void setDataToPS(PreparedStatement ps, Object value,
			int columnType, int i) throws Exception {
		if (null == value) {
			// if (columnType == Types.VARCHAR) {
			// ps.setString(i, "");
			// } else {
			try {
				ps.setNull(i, Types.NULL);
			} catch (SQLDataException e) {
				ps.setNull(i, columnType);
			}
			// }
			return;
		}
		switch (columnType) {
		case Types.BIGINT:
		case Types.NUMERIC:
			try {
				if (value instanceof JdbcObject) {
					ps.setLong(i, (Long) ((JdbcObject) value).getValue());
				} else {
					ps.setLong(i, (Long) value);
				}
			} catch (ClassCastException e) {
				if (value instanceof Number) {
					if (value instanceof Short) {
						ps.setLong(i, ((Short) value).longValue());
					}
					if (value instanceof Integer) {
						ps.setLong(i, ((Integer) value).longValue());
					}
					if (value instanceof BigInteger) {
						ps.setLong(i, ((BigInteger) value).longValue());
					}
					if (value instanceof BigDecimal) {
						ps.setLong(i, ((BigDecimal) value).longValue());
					}
					if (value instanceof Byte) {
						ps.setLong(i, ((Byte) value).longValue());
					}
				} else {
					throw e;
				}
			}
			break;

		case Types.CHAR:
		case Types.LONGVARCHAR:
		case Types.VARCHAR:
		case Types.NCHAR:
		case Types.LONGNVARCHAR:
		case Types.NVARCHAR:
			ps.setString(i, (String) value);

			break;
		case Types.CLOB:
			ps.setClob(i, (Clob) value);
			break;

		case Types.NCLOB:
			ps.setNClob(i, (NClob) value);
			break;

		case Types.BLOB:
			ps.setBlob(i, (Blob) value);
			break;

		case Types.DATE:
			if (value instanceof Timestamp) {
				ps.setTimestamp(i, (Timestamp) value);
			} else {
				ps.setDate(i, (java.sql.Date) value);
			}
			break;

		case Types.TIME:
			ps.setTime(i, (Time) value);

			break;

		case Types.TIMESTAMP:
			ps.setTimestamp(i, (Timestamp) value);

			break;

		case Types.BOOLEAN:
			if (value instanceof Number) {
				if (((Number) value).intValue() != 0) {
					ps.setBoolean(i, true);
				} else {
					ps.setBoolean(i, false);
				}
			} else {
				ps.setBoolean(i, (Boolean) value);
			}

			break;

		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.REAL:
			ps.setDouble(i, (Double) value);

			break;

		case Types.FLOAT:
			ps.setFloat(i, (Float) value);

			break;

		case Types.INTEGER:
			ps.setInt(i, (Integer) value);

			break;

		case Types.NULL:
			ps.setNull(i, Types.NULL);

			break;

		case Types.REF:
			ps.setRef(i, (Ref) value);

			break;

		case Types.SMALLINT:
		case Types.TINYINT:
			ps.setShort(i, (Short) value);

			break;

		}
	}

}
