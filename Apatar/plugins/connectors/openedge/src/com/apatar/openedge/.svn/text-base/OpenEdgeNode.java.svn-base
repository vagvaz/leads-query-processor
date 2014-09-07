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

package com.apatar.openedge;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.apatar.core.AbstractJdbcDataBase;
import com.apatar.core.ApplicationData;
import com.apatar.core.DBTypeRecord;
import com.apatar.core.DataBaseInfo;
import com.apatar.core.ERecordType;
import com.apatar.core.ETableMode;
import com.apatar.core.JdbcParams;
import com.apatar.core.RDBTable;
import com.apatar.openedge.ui.JOpenEdgePropertySheetPage;
import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.JdbcRecordSourceDescriptor;
import com.apatar.ui.wizard.TableModeDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class OpenEdgeNode extends AbstractJdbcDataBase {

	static final DataBaseInfo dataBaseInfo = new DataBaseInfo("", "", "", "",
			true, true, true, true, true, true);
	static {
		List<DBTypeRecord> rcList = dataBaseInfo.getAvailableTypes();
		rcList.add(new DBTypeRecord(ERecordType.Boolean, "BIT", 1, 1, false,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Text, "VARCHAR", 0, 30000,
				false, true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "TINYINT", 8, 8, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "SMALLINT", 16, 16,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "INTEGER", 32, 32,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "BIGINT", 64, 64,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DECIMAL", 126, 126,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "NUMERIC", 126, 126,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DOUBLE PRECISION",
				126, 126, true, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "FLOAT", 126, 126,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "REAL", 126, 126,
				true, true));
		rcList
				.add(new DBTypeRecord(ERecordType.Date, "DATE", 7, 7, false,
						true));
		rcList.add(new DBTypeRecord(ERecordType.Timestamp, "TIMESTAMP", 7, 7,
				false, true));
		rcList.add(new DBTypeRecord(ERecordType.LongVarBinary, "VARBINARY", 0,
				0x40000000/* 1 GB */, false, false));
		rcList.add(new DBTypeRecord(ERecordType.VarBinary, "RAW", 2000, 2000,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "ROWID", 18, 18, false,
				false));
	}

	public OpenEdgeNode() {
		super();
		title = "OpenEdge";
	}

	@Override
	public ImageIcon getIcon() {
		return OpenEdgeUtils.READ_OPENEDGE_NODE_ICON;
	}

	@Override
	public void createDatabaseParam(Wizard wizard) {

		try {

			WizardPanelDescriptor descriptor1 = new DBConnectionDescriptor(
					this,
					new JOpenEdgePropertySheetPage(wizard.getDialog()),
					JdbcRecordSourceDescriptor.IDENTIFIER,
					ApplicationData
							.classForName("com.apatar.openedge.OpenEdgeJdbcParams"),
					"db_connector", "openedge");
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

			wizard.setKeyForReferringToDescription("help.connector.openedge");
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
	public List<RDBTable> getTableList() throws Exception {
		JdbcParams params = (JdbcParams) ApplicationData.getProject()
				.getProjectData(getConnectionDataID()).getData();
		Connection con = params.getConnection();
		DatabaseMetaData databaseMetaData = con.getMetaData();
		String[] tableTypes = { "TABLE" };

		ResultSet rs = databaseMetaData.getTables(null, null, null, tableTypes);
		ArrayList<RDBTable> rv = new ArrayList<RDBTable>();
		while (rs.next()) {
			rv.add(new RDBTable(rs.getString("TABLE_SCHEM") + "."
					+ rs.getString("TABLE_NAME"), ETableMode.ReadWrite));
		}
		rs.close();
		return rv;

	}
}
