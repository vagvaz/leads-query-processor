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

package com.apatar.db2;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Types;
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
import com.apatar.db2.ui.DB2DBConnectionDescriptor;
import com.apatar.db2.ui.JDB2PropertySheetPage;
import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.JdbcRecordSourceDescriptor;
import com.apatar.ui.wizard.TableModeDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class DB2Node extends AbstractJdbcDataBase {

	static final DataBaseInfo dataBaseInfo = new DataBaseInfo("", "", "", "",
			true, true, true, true, true);

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
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "SMALLINT", 2, 2,
				false, true, Types.SMALLINT));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DOUBLE", 8, 8, false,
				true, Types.DOUBLE));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "REAL", 4, 4, false,
				true, Types.REAL));
		rcList.add(new DBTypeRecord(ERecordType.Text, "VARCHAR", 1, 255, false,
				false, Types.VARCHAR));
		rcList.add(new DBTypeRecord(ERecordType.Text, "VARGRAPHIC", 1, 255,
				false, false, Types.VARCHAR));
		rcList.add(new DBTypeRecord(ERecordType.Text, "CHARACTER", 1, 255,
				false, false, Types.VARCHAR));
		rcList.add(new DBTypeRecord(ERecordType.Text, "CHAR", 1, 255, false,
				false, Types.VARCHAR));
		rcList.add(new DBTypeRecord(ERecordType.Text, "GRAPHIC", 1, 255, false,
				false, Types.VARCHAR));
		rcList.add(new DBTypeRecord(ERecordType.Text, "XML", 1, 255, false,
				false, Types.VARCHAR));
		rcList.add(new DBTypeRecord(ERecordType.Text, "LONG VARGRAPHIC", 1,
				255, false, false, Types.VARCHAR));
		rcList.add(new DBTypeRecord(ERecordType.Text, "LONG GRAPHIC", 1, 255,
				false, false, Types.VARCHAR));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DECIMAL", 16, 16,
				false, true, Types.DECIMAL));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "NUMERIC", 16, 16,
				false, true, Types.NUMERIC));
		rcList.add(new DBTypeRecord(ERecordType.Boolean, "CHAR FOR BIT DATA",
				1, 1, false, false, Types.BIT));
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
		rcList.add(new DBTypeRecord(ERecordType.Clob, "DBCLOB", 0, 255, false,
				false, Types.CLOB));
		rcList.add(new DBTypeRecord(ERecordType.VarBinary,
				"VARCHAR(%d) FOR BIT DATA", 0, 65535, false, false,
				Types.VARBINARY));
		rcList.add(new DBTypeRecord(ERecordType.Binary,
				"LONG VARCHAR FOR BIT DATA", 0, 65535, false, false,
				Types.LONGVARBINARY));

	}

	public DB2Node() {
		super();
		title = "DB2";
	}

	@Override
	public ImageIcon getIcon() {
		return DB2Utils.READ_DB2_NODE_ICON;
	}

	@Override
	public void createDatabaseParam(Wizard wizard) {

		try {
			WizardPanelDescriptor descriptor1 = new DB2DBConnectionDescriptor(
					this, new JDB2PropertySheetPage(wizard.getDialog()),
					JdbcRecordSourceDescriptor.IDENTIFIER, ApplicationData
							.classForName("com.apatar.db2.DB2JdbcParams"),
					"db_connector", "sybase");
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

			wizard.setKeyForReferringToDescription("help.connector.db2");
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

	/*
	 * public List<Action> getSpetionalAction() { List<Action> actions = new
	 * ArrayList<Action>(); Action action = new AbstractAction("Add License") {
	 * public void actionPerformed(ActionEvent arg0) { JAddLicenseFileDialog dlg
	 * = new JAddLicenseFileDialog(ApatarUiMain.MAIN_FRAME);
	 * dlg.setVisible(true); } }; actions.add(action); return actions; }
	 */
	@Override
	protected void TransformTDBtoRDB(int mode) {
		super.TransformTDBtoRDB(mode, true);
	}

}