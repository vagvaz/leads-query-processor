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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractJdbcDataBase extends AbstractDataBaseNode {

	@Override
	public void createSchemaTable(AbstractApatarActions actions)
			throws Exception {
		if (connectionDataId != -1) {
			SchemaTable st = getTiForConnection(OUT_CONN_POINT_NAME)
					.getSchemaTable();
			st.removeAllRecord();

			// DataBaseTools.getFieldList((JdbcParams)ApplicationData.getProject().getProjectData(connectionDataId).getData(),
			// getTableName(), getDataBaseInfo())
			for (Record rec : getFieldList(actions)) {
				st.addRecord(rec);
			}
		}
	}

	@Override
	public void TransformRDBtoTDB() {
		doTransformRDBtoTDB();
	}

	public void TransformRDBtoTDB(boolean closeConnectionAfterReading) {
		doTransformRDBtoTDB();
		if (closeConnectionAfterReading) {
			JdbcParams params = (JdbcParams) ApplicationData.getProject()
					.getProjectData(getConnectionDataID()).getData();
			try {
				params.getConnection().close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void doTransformRDBtoTDB() {
		try {
			DataBaseTools.completeTransfer();
			DataProcessingInfo destPI = new DataProcessingInfo(ApplicationData
					.getTempDataBase().getDataBaseInfo(), getTiForConnection(
					OUT_CONN_POINT_NAME).getTableName(), getTiForConnection(
					OUT_CONN_POINT_NAME).getSchemaTable().getRecords(),
					ApplicationData.getTempJDBC());

			JdbcParams params = (JdbcParams) ApplicationData.getProject()
					.getProjectData(getConnectionDataID()).getData();

			List<Record> srcRecords = getTiForConnection(OUT_CONN_POINT_NAME)
					.getSchemaTable().getRecords();
			// transfer real data
			String sqlQuery = params.getSqlQuery();
			if (sqlQuery == null) {
				sqlQuery = "SELECT * FROM " + getTableName();
			}
			DataBaseTools.TransferData(sqlQuery, params, srcRecords, destPI,
					true);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();
		}
		//		
		//		
		// try {
		//
		// DataProcessingInfo destPI = new DataProcessingInfo(ApplicationData
		// .getTempDataBase().getDataBaseInfo(), getTiForConnection(
		// OUT_CONN_POINT_NAME).getTableName(), getTiForConnection(
		// OUT_CONN_POINT_NAME).getSchemaTable().getRecords(),
		// ApplicationData.getTempJDBC());
		//
		// JdbcParams params = (JdbcParams) ApplicationData.getProject()
		// .getProjectData(getConnectionDataID()).getData();
		//
		// // transfer real data
		// String sqlQuery = params.getSqlQuery();
		// if (sqlQuery == null) {
		// sqlQuery = "SELECT * FROM " + getTableName();
		// }
		// ResultSet srcRows = DataBaseTools.executeSelect(sqlQuery, params);
		// PreparedStatement insertPs = DataBaseTools
		// .getPreparedStatementForInsert(destPI
		// .getPreparedTableName(), destPI);
		//
		// while (srcRows.next()) {
		// ParameterMetaData pm = insertPs.getParameterMetaData();
		// for (int i = 0; i < destPI.getColumnsForProcess().size(); i++) {
		// Object value = srcRows.getObject(destPI
		// .getColumnsForProcess().get(i).getFieldName());
		// int colType = destPI.getColumnsForProcess().get(i)
		// .getSqlType(true);
		// DataBaseTools.setDataToPS(insertPs, value, colType, i + 1);
		// }
		// insertPs.execute();
		// ApplicationData.ProcessingProgress.Step();
		// }
		// insertPs.close();
		// // old data transfer method
		// // DataBaseTools.TransferData(sqlQuery, params, srcRecords, destPI,
		// // true);
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		ApplicationData.ProcessingProgress.Reset();
	}

	protected void TransformTDBtoRDB(int mode, boolean doCommitAfterProcessing) {
		// try {
		//
		// DataProcessingInfo srcPI = new DataProcessingInfo(ApplicationData
		// .getTempDataBase().getDataBaseInfo(), getTiForConnection(
		// IN_CONN_POINT_NAME).getTableName(), getTiForConnection(
		// IN_CONN_POINT_NAME).getSchemaTable().getRecords(),
		// ApplicationData.getTempJDBC());
		// // DataProcessingInfo destPI = new DataProcessingInfo(
		// // getDataBaseInfo(), getTableName(), getTiForConnection(
		// // OUT_CONN_POINT_NAME).getSchemaTable().getRecords(),
		// // (JdbcParams) ApplicationData.getProject().getProjectData(
		// // getConnectionDataID()).getData());
		// DataProcessingInfo destPI = new DataProcessingInfo(
		// getDataBaseInfo(), getTableName(), getColumnsForUpdate(),
		// (JdbcParams) ApplicationData.getProject().getProjectData(
		// getConnectionDataID()).getData());
		//
		// DataBaseTools.TransferData(srcPI, destPI, mode,
		// identificationFields, null, false, doCommitAfterProcessing);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// ==============================================
		try {
			DataBaseTools.completeTransfer();
			if (mode == AbstractDataBaseNode.UPDATE_MODE
					|| mode == AbstractDataBaseNode.DELETE_MODE
					|| mode == AbstractDataBaseNode.SYNCHRONIZE_MODE) {
				if (null == identificationFields
						|| identificationFields.size() == 0) {
					System.err.println("No identification fields are selected");
					return;
				}
			}
			DataProcessingInfo srcPI = new DataProcessingInfo(ApplicationData
					.getTempDataBase().getDataBaseInfo(), getTiForConnection(
					IN_CONN_POINT_NAME).getTableName(), getTiForConnection(
					IN_CONN_POINT_NAME).getSchemaTable().getRecords(),
					ApplicationData.getTempJDBC());
			// DataProcessingInfo destPI = new DataProcessingInfo(
			// getDataBaseInfo(), getTableName(), getTiForConnection(
			// OUT_CONN_POINT_NAME).getSchemaTable().getRecords(),
			// (JdbcParams) ApplicationData.getProject().getProjectData(
			// getConnectionDataID()).getData());
			DataProcessingInfo destPI = new DataProcessingInfo(
					getDataBaseInfo(), getTableName(), getColumnsForUpdate(),
					(JdbcParams) ApplicationData.getProject().getProjectData(
							getConnectionDataID()).getData());

			// reading data from tmp table
			ResultSet srcRows = srcPI.getConnection().createStatement()
					.executeQuery("select * from " + srcPI.getTableName());
			String tableName = destPI.getPreparedTableName();
			String whereQueryPart = "where";
			String queryFields = "";
			String updateQuery = "update " + tableName + " set ";
			PreparedStatement insertPs = null;
			PreparedStatement modifyPs = null;
			PreparedStatement searchPs = null;
			List<Record> identFields = new ArrayList<Record>();
			List<Record> updateFields = new ArrayList<Record>();

			List<String> preparedIdentFields = new ArrayList<String>();
			for (String identFieldName : identificationFields) {
				preparedIdentFields.add(new String(destPI.getDataBaseInfo()
						.getStartSymbolEdgingFieldName()
						+ identFieldName
						+ destPI.getDataBaseInfo()
								.getFinishSymbolEdgingFieldName()));
			}
			// creating sqls and PreparedStatements
			if (mode == AbstractDataBaseNode.INSERT_MODE
					|| mode == AbstractDataBaseNode.UPDATE_MODE) {
				insertPs = DataBaseTools.getPreparedStatementForInsert(
						tableName, destPI);
			}
			if (mode == AbstractDataBaseNode.UPDATE_MODE
					|| mode == AbstractDataBaseNode.DELETE_MODE) {
				for (Record mappedColumn : destPI.getColumnsForProcess()) {
					// add to query part all fields except identificationFields
					if (!identificationFields.contains(mappedColumn
							.getFieldName())) {
						if ("".equals(queryFields)) {
							queryFields += destPI
									.getPreparedColumnsName(mappedColumn
											.getFieldName())
									+ "=?";
						} else {
							queryFields += ", "
									+ destPI
											.getPreparedColumnsName(mappedColumn
													.getFieldName()) + "=?";
						}
						updateFields.add(mappedColumn);
					}
				}
				for (String identFieldName : identificationFields) {
					if ("where".equals(whereQueryPart)) {
						whereQueryPart += " "
								+ destPI.getPreparedColumnsName(identFieldName)
								+ "=?";
					} else {
						whereQueryPart += " and "
								+ destPI.getPreparedColumnsName(identFieldName)
								+ "=?";
					}
					for (Record field : destPI.getColumnsForProcess()) {
						if (field.getFieldName().equals(identFieldName)) {
							identFields.add(field);
							updateFields.add(field);
							break;
						}
					}
				}
				updateQuery += queryFields + " " + whereQueryPart;
				searchPs = destPI.getConnection().prepareStatement(
						"select count(*) from " + tableName + " "
								+ whereQueryPart);
			}
			String deleteQuery = "delete from " + tableName + " "
					+ whereQueryPart;
			if (mode == AbstractDataBaseNode.DELETE_MODE) {
				modifyPs = destPI.getConnection().prepareStatement(deleteQuery);
			} else if (mode == AbstractDataBaseNode.UPDATE_MODE) {
				modifyPs = destPI.getConnection().prepareStatement(updateQuery);
			}
			while (srcRows.next()) {
				if (mode == AbstractDataBaseNode.INSERT_MODE) {
					insertOneRow(destPI, srcRows, insertPs);
				} else if (mode == AbstractDataBaseNode.UPDATE_MODE
						|| mode == AbstractDataBaseNode.DELETE_MODE) {
					// search for records to update or delete
					populateRowWithData(identFields, srcRows, searchPs);
					ResultSet searchRs = searchPs.executeQuery();
					searchRs.next();
					int rowsCount = searchRs.getInt(1);
					if (rowsCount == 0
							&& mode != AbstractDataBaseNode.DELETE_MODE) {
						// we have to insert record
						insertOneRow(destPI, srcRows, insertPs);
					} else if (rowsCount == 1) {
						// we have to update this record or delete it
						if (mode == AbstractDataBaseNode.DELETE_MODE) {
							populateRowWithData(identFields, srcRows, modifyPs);
						} else if (mode == AbstractDataBaseNode.UPDATE_MODE) {
							populateRowWithData(updateFields, srcRows, modifyPs);
						}
						modifyPs.execute();
					} else {
						System.err.println("Found more than one match");
					}

				}
				ApplicationData.ProcessingProgress.Step();
			}
			if (doCommitAfterProcessing) {
				destPI.getConnection().commit();
			}
			// old processing method
			// DataBaseTools.TransferData(srcPI, destPI, mode,
			// identificationFields, null, false, doCommitAfterProcessing);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();
		}
		// DataBaseTools.completeTransfer();
		ApplicationData.ProcessingProgress.Reset();
	}

	private void populateRowWithData(List<Record> identFields,
			ResultSet srcRows, PreparedStatement ps) throws Exception {
		for (int i = 0; i < identFields.size(); i++) {
			Object value = srcRows.getObject(identFields.get(i).getFieldName());
			int colType = identFields.get(i).getSqlType(true);
			DataBaseTools.setDataToPS(ps, value, colType, i + 1);
		}
	}

	private void insertOneRow(DataProcessingInfo destPI, ResultSet srcRows,
			PreparedStatement insertPs) throws Exception {
		for (int i = 0; i < destPI.getColumnsForProcess().size(); i++) {
			Object value = srcRows.getObject(destPI.getColumnsForProcess().get(
					i).getFieldName());
			int colType = destPI.getColumnsForProcess().get(i).getSqlType(true);
			DataBaseTools.setDataToPS(insertPs, value, colType, i + 1);
		}
		insertPs.execute();
	}

	public void moveDataFromTempToReal(List<String> identificationFields,
			boolean doCommitAfterProcessing) {
		try {
			DataProcessingInfo srcPI = new DataProcessingInfo(ApplicationData
					.getTempDataBase().getDataBaseInfo(), getTiForConnection(
					OUT_CONN_POINT_NAME).getTableName(), getTiForConnection(
					OUT_CONN_POINT_NAME).getSchemaTable().getRecords(),
					ApplicationData.getTempJDBC());
			DataProcessingInfo destPI = new DataProcessingInfo(
					getDataBaseInfo(), getTableName(), getTiForConnection(
							OUT_CONN_POINT_NAME).getSchemaTable().getRecords(),
					(JdbcParams) ApplicationData.getProject().getProjectData(
							getConnectionDataID()).getData());

			DataBaseTools.TransferData(srcPI, destPI,
					AbstractDataBaseNode.UPDATE_MODE, identificationFields,
					null, false, doCommitAfterProcessing);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.AbstractDataBaseNode#moveDataFromTempToReal()
	 */
	@Override
	public void moveDataFromTempToReal(List<String> identificationFields,
			TableInfo inputTi) {
		try {
			DataProcessingInfo srcPI = new DataProcessingInfo(ApplicationData
					.getTempDataBase().getDataBaseInfo(), getTiForConnection(
					OUT_CONN_POINT_NAME).getTableName(), getTiForConnection(
					OUT_CONN_POINT_NAME).getSchemaTable().getRecords(),
					ApplicationData.getTempJDBC());
			DataProcessingInfo destPI = new DataProcessingInfo(
					getDataBaseInfo(), getTableName(), getTiForConnection(
							OUT_CONN_POINT_NAME).getSchemaTable().getRecords(),
					(JdbcParams) ApplicationData.getProject().getProjectData(
							getConnectionDataID()).getData());

			DataBaseTools.TransferData(srcPI, destPI,
					AbstractDataBaseNode.UPDATE_MODE, identificationFields,
					null, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void TransformTDBtoRDB(int mode) {
		TransformTDBtoRDB(mode, false);
		// try {
		//
		// DataProcessingInfo srcPI = new DataProcessingInfo(ApplicationData
		// .getTempDataBase().getDataBaseInfo(), getTiForConnection(
		// IN_CONN_POINT_NAME).getTableName(), getTiForConnection(
		// IN_CONN_POINT_NAME).getSchemaTable().getRecords(),
		// ApplicationData.getTempJDBC());
		// DataProcessingInfo destPI = new DataProcessingInfo(
		// getDataBaseInfo(), getTableName(), getColumnsForUpdate(),
		// (JdbcParams) ApplicationData.getProject().getProjectData(
		// getConnectionDataID()).getData());
		//
		// DataBaseTools.TransferData(srcPI, destPI, mode,
		// identificationFields, null, false);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	// return table list for this connection
	@Override
	public List<RDBTable> getTableList() throws Exception {
		return DataBaseTools.getTableList((JdbcParams) ApplicationData
				.getProject().getProjectData(getConnectionDataID()).getData());
	}

	@Override
	public void setConnectionToNull() {
		((JdbcParams) ApplicationData.getProject().getProjectData(
				getConnectionDataID()).getData()).connection = null;
	}

	@Override
	public List<Record> getFieldList(AbstractApatarActions action)
			throws Exception {
		JdbcParams params = (JdbcParams) ApplicationData.getProject()
				.getProjectData(getConnectionDataID()).getData();
		return DataBaseTools.getFieldWithSqlQuery(params, params.getSqlQuery(),
				getDataBaseInfo(), action);
	}

	@Override
	public void deleteAllRecordsInRDB() throws Exception {
		DataBaseTools.clearRecords(getDataBaseInfo(),
				(JdbcParams) ApplicationData.getProject().getProjectData(
						getConnectionDataID()).getData(), getTableName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.apatar.core.AbstractDataBaseNode#executeUpdateQuery(java.lang.String)
	 */
	@Override
	public int executeUpdateQuery(String query) {
		int result = 0;
		try {
			result = DataBaseTools.executeUpdate(query,
					(JdbcParams) ApplicationData.getProject().getProjectData(
							getConnectionDataID()).getData(), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.AbstractDataBaseNode#getTotalRecodrsCount()
	 */
	@Override
	public int getTotalRecodrsCount(TableInfo ti) {
		return getTotalRecodrsCount(ti, (JdbcParams) ApplicationData
				.getProject().getProjectData(getConnectionDataID()).getData());
	}

	@Override
	public int getTotalRecodrsCount(TableInfo ti, JdbcParams params) {
		int result = 0;
		try {
			ResultSet rs = DataBaseTools.executeSelect("select count(*) from "
					+ ti.getTableName(), params);
			rs.next();
			result = rs.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.AbstractDataBaseNode#validateConnectionData()
	 */
	@Override
	public boolean validateConnectionData() {
		try {
			Connection con = ((JdbcParams) ApplicationData.getProject()
					.getProjectData(getConnectionDataID()).getData())
					.getConnection();
			if (con == null) {
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			lastErrorMessage = LogUtils.GetExceptionMessage(e);
			return false;
		}
	}

}
