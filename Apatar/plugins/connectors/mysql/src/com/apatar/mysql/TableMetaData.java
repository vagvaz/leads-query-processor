package com.apatar.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.apatar.core.ApatarRegExp;
import com.apatar.core.JdbcParams;

public class TableMetaData {
	private List<ColumnMetaData> fields = new ArrayList<ColumnMetaData>();
	private JdbcParams params;
	private String tableName;
	private String rsmdString;
	private List<TableMetaData> tables = null;

	public TableMetaData(JdbcParams params, String tn) throws Exception {
		super();
		this.params = params;
		tableName = tn;
		fillFieldsForTable();
	}

	public TableMetaData getTableByName(String tn) throws Exception {
		for (TableMetaData table : tables) {
			if (table.getTableName().equalsIgnoreCase(tn)) {
				return table;
			}
		}
		tables.add(new TableMetaData(params, tn));
		return getTableByName(tn);
	}

	public TableMetaData(JdbcParams params) {
		super();
		this.params = params;
	}

	public TableMetaData(String rsmdData, JdbcParams params) throws Exception {
		super();
		rsmdString = rsmdData;
		tables = new ArrayList<TableMetaData>();
		this.params = params;
		fillFieldsFromRSMDoutput();
	}

	private void fillFieldsFromRSMDoutput() throws Exception {
		fields.clear();
		List<String> fieldsData = ApatarRegExp.getAllSubstrings(
				"(?i)(?s)\\[(.*?)\\]", rsmdString, 1);
		for (String string : fieldsData) {
			System.out.println(string);
			String originalTableName = "";
			String columnName = "";
			String originalColumnName = "";
			String mysqlType = "";
			String flags = "";
			try {
				originalTableName = ApatarRegExp.getSubstrings(
						"(?i).*originalTableName=(.*),columnName.*", string, 1);
			} catch (Exception e) {
			}

			try {
				columnName = ApatarRegExp
						.getSubstrings(
								"(?i).*columnName=(.*),originalColumnName.*",
								string, 1);
			} catch (Exception e) {
			}
			try {
				originalColumnName = ApatarRegExp.getSubstrings(
						"(?i).*originalColumnName=(.*),mysqlType.*", string, 1);
			} catch (Exception e) {
			}
			try {
				mysqlType = ApatarRegExp.getSubstrings(
						"(?i).*mysqlType=(.*),flags.*", string, 1);
			} catch (Exception e) {
			}
			try {
				flags = ApatarRegExp.getSubstrings(
						"(?i).*flags=(.*),charsetIndex.*", string, 1);
			} catch (Exception e) {
			}
			ColumnMetaData column = null;
			if (!"".equals(originalTableName)) {
				column = getTableByName(originalTableName).getField(
						originalColumnName);
			} else {
				// String columnName, long charMaxLength, boolean
				// isNullable, String dataType
				mysqlType = getType(mysqlType, flags);
				long charMaxLength = 0;
				if ("VARCHAR".equals(mysqlType)) {
					charMaxLength = 255;
				}
				column = new ColumnMetaData(columnName, charMaxLength, true,
						mysqlType);
			}
			addFieldWithUniqueName(column);
		}
	}

	private String getIndexForField(String fieldName, String baseIndex) {
		if ("".equals(baseIndex)) {
			baseIndex = "1";
		} else {
			baseIndex = String.valueOf(Integer.valueOf(baseIndex) + 1);
		}
		for (ColumnMetaData field : fields) {
			if (field.getColumnName().equalsIgnoreCase(fieldName + baseIndex)) {
				return getIndexForField(fieldName, baseIndex);
			}
		}
		return baseIndex;
	}

	private void addFieldWithUniqueName(ColumnMetaData column) throws Exception {
		for (ColumnMetaData field : fields) {
			if (field.getColumnName().equalsIgnoreCase(column.getColumnName())) {
				String index = "";
				index = getIndexForField(column.getColumnName(), index);
				ColumnMetaData col = column.clone();
				col.setColumnName(column.getColumnName() + index);
				fields.add(col);
				return;
			}
		}
		fields.add(column);
	}

	private String getType(String mysqlType, String flags) throws Exception {
		String type = "";
		type = ApatarRegExp.getSubstrings("(?).*FIELD_TYPE_(.*?)\\)",
				mysqlType, 1);
		if ("VAR_STRING".equalsIgnoreCase(type)) {
			type = "TEXT";
		} else if ("STRING".equalsIgnoreCase(type)) {
			type = "CHAR";
		}

		return type;
	}

	private void fillFieldsForTable() throws Exception {
		PreparedStatement ps_md = params.getConnection().prepareStatement(
				"SELECT * FROM information_schema.COLUMNS "
						+ "WHERE TABLE_SCHEMA=? AND " + "TABLE_NAME=?");
		ps_md.setString(1, params.getDbName());
		ps_md.setString(2, tableName);
		ResultSet tableMetaData = ps_md.executeQuery();

		fields.clear();
		while (tableMetaData.next()) {
			String dataType = tableMetaData.getString("COLUMN_TYPE");
			if (ApatarRegExp.matchRegExp("(?i).*unsigned.*", dataType)) {
				dataType = "unsigned " + tableMetaData.getString("DATA_TYPE");
			} else {
				dataType = tableMetaData.getString("DATA_TYPE");
			}
			fields.add(new ColumnMetaData(tableMetaData
					.getString("COLUMN_NAME"), tableMetaData
					.getLong("CHARACTER_MAXIMUM_LENGTH"), (tableMetaData
					.getString("IS_NULLABLE").equalsIgnoreCase("yes") ? true
					: false), dataType));
		}
		tableMetaData.close();
	}

	/**
	 * @return the fields
	 */
	public List<ColumnMetaData> getFields() {
		return fields;
	}

	public ColumnMetaData getField(int i) {
		return getFields().get(i);
	}

	public ColumnMetaData getField(String fName) {
		for (ColumnMetaData column : getFields()) {
			if (column.getColumnName().equalsIgnoreCase(fName)) {
				return column;
			}
		}
		return null;
	}

	/**
	 * @return the params
	 */
	public JdbcParams getParams() {
		return params;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName
	 *            the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * @return the rsmdString
	 */
	public String getRsmdString() {
		return rsmdString;
	}

	/**
	 * @param rsmdString
	 *            the rsmdString to set
	 */
	public void setRsmdString(String rsmdString) {
		this.rsmdString = rsmdString;
	}

}
