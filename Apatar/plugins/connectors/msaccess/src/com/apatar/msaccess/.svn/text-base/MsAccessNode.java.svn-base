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

package com.apatar.msaccess;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;

import javax.swing.ImageIcon;

import propertysheet.JPropertySheetPage;

import com.apatar.core.AbstractDataBaseNode;
import com.apatar.core.AbstractJdbcDataBase;
import com.apatar.core.ApplicationData;
import com.apatar.core.DBTypeRecord;
import com.apatar.core.DataBaseInfo;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DataProcessingInfo;
import com.apatar.core.ERecordType;
import com.apatar.core.ETableMode;
import com.apatar.core.JdbcObject;
import com.apatar.core.JdbcParams;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.RDBTable;
import com.apatar.core.Record;
import com.apatar.core.DataBaseTools.SQLCreationData;
import com.apatar.core.DataBaseTools.SQLQueryString;
import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.JdbcRecordSourceDescriptor;
import com.apatar.ui.wizard.TableModeDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class MsAccessNode extends AbstractJdbcDataBase {

	static final DataBaseInfo dataBaseInfo = new DataBaseInfo("[", "]", "[",
			"]", true, true, true, true, true);

	static {
		List<DBTypeRecord> rcList = dataBaseInfo.getAvailableTypes();

		rcList.add(new DBTypeRecord(ERecordType.Numeric, "BYTE", 1, 1, false,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "BIT", 1, 1, false,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "INTEGER", 2, 2, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "SMALLINT", 2, 2,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "LONG", 4, 4, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "SINGLE", 4, 4, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DOUBLE", 8, 8, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DECIMAL", 12, 12,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "CURRENCY", 8, 8,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "COUNTER", 11, 11,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "AUTONUMBER", 4, 4,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "REPLICATIONID", 2, 2,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Boolean, "YESNO", 1, 1, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "TEXT", 0, 255, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "CHAR", 0, 255, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "VARCHAR", 0, 255, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Clob, "LONGCHAR", 0,
				1073741823, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Clob, "MEMO", 0, 65536, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "OLE", 0, 0x40000000,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "ATTACHMENT", 0,
				0x40000000, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "HYPERLINK", 0,
				0x40000000, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Timestamp, "DATETIME", 8, 8,
				false, false));
	}

	public MsAccessNode() {
		super();
		title = "MS Access";
	}

	@Override
	public ImageIcon getIcon() {
		return MsAccessUtils.READ_MSACCESS_NODE_ICON;
	}

	@Override
	public void createDatabaseParam(Wizard wizard) {

		try {
			WizardPanelDescriptor descriptor1 = new DBConnectionDescriptor(
					this,
					new JPropertySheetPage(wizard.getDialog()),
					JdbcRecordSourceDescriptor.IDENTIFIER,
					ApplicationData
							.classForName("com.apatar.msaccess.MsAccessConnection"),
					"db_connector", "msaccess");
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

			wizard.setKeyForReferringToDescription("help.connector.msaccess");
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
	protected void TransformTDBtoRDB(int mode) {
		DataBaseTools.completeTransfer();

		try {
			DataProcessingInfo srcPI = new DataProcessingInfo(ApplicationData
					.getTempDataBase().getDataBaseInfo(), getTiForConnection(
					IN_CONN_POINT_NAME).getTableName(), getTiForConnection(
					IN_CONN_POINT_NAME).getSchemaTable().getRecords(),
					ApplicationData.getTempJDBC());
			DataProcessingInfo destPI = new DataProcessingInfo(
					getDataBaseInfo(), getTableName(), getTiForConnection(
							OUT_CONN_POINT_NAME).getSchemaTable().getRecords(),
					((JdbcParams) ApplicationData.getProject().getProjectData(
							getConnectionDataID()).getData()));

			List<Record> records1 = DataBaseTools.intersectionRecords(srcPI
					.getColumnsForProcess(), destPI.getColumnsForProcess(),
					true);
			SQLCreationData cds = new SQLCreationData(records1, srcPI
					.getTableName());
			List<Record> records2 = DataBaseTools.intersectionRecords(srcPI
					.getColumnsForProcess(), destPI.getColumnsForProcess(),
					false);
			SQLCreationData destcds = new SQLCreationData(
					getColumnsForUpdate(), destPI.getTableName());

			Hashtable<String, String> table = new Hashtable<String, String>();

			SQLQueryString sss = DataBaseTools.CreateSelectString(srcPI
					.getDataBaseInfo(), new SQLCreationData[] { cds }, table);

			if (sss == null) {
				return;
			}

			Connection conTarget = destPI.getConnection();
			Connection conSource = srcPI.getConnection();
			ResultSet rs = null;
			rs = conSource.createStatement().executeQuery(sss.query);

			SQLQueryString q_ins = DataBaseTools.CreateIUString(destPI
					.getDataBaseInfo(), destcds, true, identificationFields,
					null);
			SQLQueryString q_upd = DataBaseTools.CreateIUString(destPI
					.getDataBaseInfo(), destcds, false, identificationFields,
					null);
			ResultSetMetaData rsmd = rs.getMetaData();
			if (rs.next()) {
				do {
					KeyInsensitiveMap data = DataBaseTools.GetJdbcObjectFromRS(
							rs, rsmd);

					Statement statement = conTarget.createStatement();
					String query = "";

					if (mode == AbstractDataBaseNode.INSERT_MODE) {
						query = getInsertQueryString(q_ins, data, destPI
								.getTableName());

					} else if (mode == AbstractDataBaseNode.UPDATE_MODE) {
						// fist find records to be updated.
						KeyInsensitiveMap identFields = new KeyInsensitiveMap();
						for (String record : getIdentificationFields()) {
							identFields.put(record, null);
						}

						String selectQuery = DataBaseTools.CreateSelectString(
								destPI.getDataBaseInfo(), new SQLCreationData(
										destPI.getColumnsForProcess(), destPI
												.getTableName()), identFields).query;
						for (String record : identFields.keySet()) {
							selectQuery = selectQuery
									.replaceFirst(
											"[?]",
											Matcher
													.quoteReplacement(getValueForSQL(data
															.get(record, true))));
						}
						ResultSet foundRecords = conTarget.createStatement()
								.executeQuery(selectQuery);

						if (foundRecords.next()) {
							query = getUpdateQueryString(q_upd, data, destPI
									.getTableName());
						} else {
							query = getInsertQueryString(q_ins, data, destPI
									.getTableName());
						}
					}

					System.out.println("MS Access query: `" + query + "`");
					statement.executeUpdate(query);
					statement.close();
					// log support
					if (!ApplicationData.ProcessingProgress.Step()) {
						return;
					}

				} while (rs.next());
			}
			conTarget.commit();
		} catch (Exception e) {
			ApplicationData.ProcessingProgress.Log(e);
			e.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();
		}
	}

	private String getInsertQueryString(SQLQueryString sqlQuery,
			KeyInsensitiveMap datas, String tableName) {

		String resultQuery = "INSERT INTO " + tableName + " (";

		List<String> fieldsName = new ArrayList<String>();
		List<Object> fieldsValue = new ArrayList<Object>();

		int pos = 0;
		for (String field : sqlQuery.queryOrder) {
			Object data = datas.get(field, true);

			if (null != data) {
				if (data instanceof JdbcObject) {
					if (null != ((JdbcObject) data).getValue()) {
						resultQuery += "[" + field + "], ";
						fieldsName.add(pos, field);
						fieldsValue.add(pos, data);
						pos++;
					}
				}
			}
		}

		resultQuery = resultQuery.substring(0, resultQuery.length() - 2)
				+ ") VALUES (";

		for (Object data : fieldsValue) {
			resultQuery += getValueForSQL(data) + ", ";
		}

		resultQuery = resultQuery.substring(0, resultQuery.length() - 2) + ")";

		return resultQuery;
	}

	private String getValueForSQL(Object data) {
		if (data instanceof JdbcObject) {
			data = ((JdbcObject) data).getValue();
		}
		String resultQuery = "";
		if (data instanceof Timestamp) {
			resultQuery += "'" + ((Timestamp) data).toString().split("\\.")[0]
					+ "'";
			// GregorianCalendar calendar = new GregorianCalendar();
			// calendar.setTimeInMillis(((Timestamp) data).getTime());
			// resultQuery += "'" + String.valueOf(((Date) data).getYear())
			// + "-" + String.valueOf(((Timestamp) data).toString()) + "-"
			// + String.valueOf(((Timestamp) data).getDay()) + " "
			// + String.valueOf(((Timestamp) data).getHours()) + ":"
			// + String.valueOf(((Timestamp) data).getMinutes()) + ":"
			// + String.valueOf(((Timestamp) data).getSeconds()) + "'";
			// resultQuery += "'" + calendar.get(GregorianCalendar.YEAR) + "-"
			// + calendar.get(GregorianCalendar.MONTH) + "-"
			// + calendar.get(GregorianCalendar.DAY_OF_MONTH) + " "
			// + calendar.get(GregorianCalendar.HOUR_OF_DAY) + ":"
			// + calendar.get(GregorianCalendar.MINUTE) + ":"
			// + calendar.get(GregorianCalendar.SECOND) + "'";
		} else if (data instanceof Date) {
			// convert one date to another date
			resultQuery += "'" + new java.sql.Date(((Date) data).getTime())
					+ "'";
		} else if (data instanceof InputStream) {
			resultQuery += (InputStream) data;
		} else if (data instanceof Time) {
			resultQuery += "'" + data + "'";
		} else if (data instanceof Number) {
			resultQuery += data;
		} else if (data instanceof java.sql.Clob) {
			try {
				String _data = ((java.sql.Clob) data).getSubString(1,
						(int) ((java.sql.Clob) data).length());
				// resultQuery += "'" + _data.replaceAll("'", "\\\\\\\\'") +
				// "'";
				resultQuery += "'" + _data.replaceAll("'", "''") + "'";
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			String _data = (String) data;
			// resultQuery += "'" + _data.replaceAll("'", "\\\\\\\\'") + "'";
			resultQuery += "'" + _data.replaceAll("'", "''") + "'";
		}

		return resultQuery;
	}

	private String getUpdateQueryString(SQLQueryString sqlQuery,
			KeyInsensitiveMap datas, String tableName) {

		String queryArr[] = (sqlQuery.query + ";").split("\\?");
		// String resultQuery = "UPDATE " + tableName + " SET ";
		String resultQuery = queryArr[0];

		int pos = 1;
		Object data = null;
		for (String field : sqlQuery.queryOrder) {

			data = datas.get(field, true);

			if (data instanceof JdbcObject) {
				data = ((JdbcObject) data).getValue();
			}

			if (null == data) {
				resultQuery += "''";

				if (pos < queryArr.length) {
					resultQuery += queryArr[pos];
					pos++;
				}

				continue;
			}

			resultQuery += getValueForSQL(data);

			if (pos < queryArr.length) {
				resultQuery += queryArr[pos];
				pos++;
			}
		}

		return resultQuery;
	}

	public List<Record> getFieldList(boolean withMessage)
			throws ClassNotFoundException, SQLException {
		List<Record> res = new ArrayList<Record>();

		JdbcParams params = (JdbcParams) ApplicationData.getProject()
				.getProjectData(getConnectionDataID()).getData();

		ResultSetMetaData rsmd = DataBaseTools.getRSWithAllFields(
				getTableName(), params, getDataBaseInfo()).getMetaData();

		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			// drop signed/unsigned word
			String columnTypeName = rsmd.getColumnTypeName(i).toUpperCase();
			columnTypeName = columnTypeName.replace(" SIGNED", "").trim();
			columnTypeName = columnTypeName.replace(" UNSIGNED", "").trim();

			DBTypeRecord Typerec = DBTypeRecord.getRecordByOriginalType(
					getDataBaseInfo().getAvailableTypes(), columnTypeName);

			res.add(new Record(Typerec, rsmd.getColumnName(i), rsmd
					.getColumnDisplaySize(i),
					DatabaseMetaData.columnNullable == rsmd.isNullable(i), rsmd
							.isSigned(i), false));
		}

		return res;
	}

	@Override
	public DataBaseInfo getDataBaseInfo() {
		return dataBaseInfo;
	}

	@Override
	public List<RDBTable> getTableList() throws Exception {
		JdbcParams params = (JdbcParams) ApplicationData.getProject()
				.getProjectData(getConnectionDataID()).getData();
		Connection con = params.getConnection();
		DatabaseMetaData databaseMetaData = con.getMetaData();

		ResultSet rs = databaseMetaData.getTables(null, null, null, null);
		ArrayList<RDBTable> rv = new ArrayList<RDBTable>();
		while (rs.next()) {
			rv.add(new RDBTable(rs.getString("TABLE_NAME"),
					ETableMode.ReadWrite));
		}
		return rv;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.AbstractJdbcDataBase#TransformRDBtoTDB()
	 */
	@Override
	public void TransformRDBtoTDB() {
		super.TransformRDBtoTDB();
		try {
			((JdbcParams) ApplicationData.getProject().getProjectData(
					getConnectionDataID()).getData()).getConnection().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
