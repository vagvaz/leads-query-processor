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

package com.apatar.mssql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import propertysheet.JPropertySheetPage;

import com.apatar.core.AbstractJdbcDataBase;
import com.apatar.core.ApatarRegExp;
import com.apatar.core.ApplicationData;
import com.apatar.core.DBTypeRecord;
import com.apatar.core.DataBaseInfo;
import com.apatar.core.ERecordType;
import com.apatar.core.JdbcParams;
import com.apatar.core.TableInfo;
import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.JdbcRecordSourceDescriptor;
import com.apatar.ui.wizard.TableModeDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class MsSqlNode extends AbstractJdbcDataBase {

	static Map<String, List> HSQL_TO_RDB = new HashMap<String, List>();
	static Map<String, String> RDB_TO_HSQL = new HashMap<String, String>();

	static final DataBaseInfo dataBaseInfo = new DataBaseInfo("[", "]", "[",
			"]", true, true, true, true, true);

	static {
		List<DBTypeRecord> rcList = dataBaseInfo.getAvailableTypes();
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "BIT", 1, 1, false,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "INT", 4, 4, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "INT IDENTITY", 4, 4,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "NUMERIC IDENTITY", 8,
				8, true, true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "SMALLINT", 2, 2,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "SMALLINT IDENTITY",
				2, 2, true, true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "TINYINT", 1, 1, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "BIGINT", 8, 8, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DECIMAL", 8, 8, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DECIMAL IDENTITY", 8,
				8, true, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "NUMERIC", 8, 8, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "REAL", 4, 4, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "FLOAT", 8, 8, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Clob, "TEXT", 0, 960, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Clob, "NTEXT", 0, 960, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "CHAR", 0, 8000, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "VARCHAR", 0, 8000,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Clob, "NCHAR", 0, 4000, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Clob, "NVARCHAR", 0, 4000,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.VarBinary, "BINARY", 0, 8000,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.VarBinary, "VARBINARY", 8,
				8000, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "IMAGE", 0, 960, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Timestamp, "DATETIME", 8, 8,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Date, "SMALLDATETIME", 4, 4,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "MONEY", 8, 8, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "SMALLMONEY", 4, 4,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "SQL_VARIANT", 0, 8016,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Timestamp, "TIMESTAMP", 8, 8,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Text, "UNIQUEIDENTIFIER", 0,
				16, false, false));
	}

	public MsSqlNode() {
		super();
		title = "MS SQL";
	}

	@Override
	public ImageIcon getIcon() {
		return MsSqlUtils.READ_MSSQL_NODE_ICON;
	}

	@Override
	public void createDatabaseParam(Wizard wizard) {
		wizard.setTitle(title + " Property");

		try {
			WizardPanelDescriptor descriptor1 = new DBConnectionDescriptor(
					this, new JPropertySheetPage(wizard.getDialog()),
					JdbcRecordSourceDescriptor.IDENTIFIER, ApplicationData
							.classForName("com.apatar.mssql.MsSqlJdbcParams"),
					"db_connector", "mssql");
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

			wizard.setKeyForReferringToDescription("help.connector.mssql");
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
		try {
			String tableName = "";
			boolean doUpdateIdentityInsert = true;
			if (ApatarRegExp.matchRegExp("(?i)select.*?from\\s+(.*?)\\s.*",
					((JdbcParams) ApplicationData.getProject().getProjectData(
							getConnectionDataID()).getData()).getSqlQuery())) {
				tableName = ApatarRegExp.getSubstrings(
						"(?i)select.*?from\\s+(.*?)\\s",
						((JdbcParams) ApplicationData.getProject()
								.getProjectData(getConnectionDataID())
								.getData()).getSqlQuery(), 1);
			} else if (ApatarRegExp.matchRegExp("(?i)select.*?from\\s+(.*)",
					((JdbcParams) ApplicationData.getProject().getProjectData(
							getConnectionDataID()).getData()).getSqlQuery())) {

				tableName = ApatarRegExp.getSubstrings(
						"(?i)select.*?from\\s+(.*)",
						((JdbcParams) ApplicationData.getProject()
								.getProjectData(getConnectionDataID())
								.getData()).getSqlQuery(), 1);
			} else {
				doUpdateIdentityInsert = false;
			}

			if (doUpdateIdentityInsert) {
				((JdbcParams) ApplicationData.getProject().getProjectData(
						getConnectionDataID()).getData()).getStatement()
						.execute("set IDENTITY_INSERT " + tableName + " ON");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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

}
