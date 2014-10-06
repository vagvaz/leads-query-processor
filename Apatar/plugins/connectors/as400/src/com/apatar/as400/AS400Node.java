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

package com.apatar.as400;

import java.util.List;

import javax.swing.ImageIcon;

import propertysheet.JPropertySheetPage;

import com.apatar.core.AbstractJdbcDataBase;
import com.apatar.core.ApplicationData;
import com.apatar.core.DBTypeRecord;
import com.apatar.core.DataBaseInfo;
import com.apatar.core.ERecordType;
import com.apatar.core.TableInfo;
import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.JdbcRecordSourceDescriptor;
import com.apatar.ui.wizard.TableModeDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class AS400Node extends AbstractJdbcDataBase {
	static final DataBaseInfo dataBaseInfo = new DataBaseInfo("", "", "", "",
			true, true, true, true, true);

	static {
		List<DBTypeRecord> rcList = dataBaseInfo.getAvailableTypes();
		rcList.add(new DBTypeRecord(ERecordType.Boolean, "BOOL", 1, 1, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "BIT", 64, 64, false,
				true));

		rcList.add(new DBTypeRecord(ERecordType.Numeric, "BIGINT", 8, 8, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "INT", 4, 4, true,
				true));
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
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "NUMERIC", 8, 8,
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
		rcList.add(new DBTypeRecord(ERecordType.Binary, "CHAR FOR BIT DATA", 0,
				255, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "CHAR() FOR BIT DATA",
				0, 255, false, false));
		rcList.add(new DBTypeRecord(ERecordType.VarBinary, "VARBINARY", 0,
				65535, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "CHAR", 1, 255, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "VARCHAR", 1, 255, false,
				true));

		rcList.add(new DBTypeRecord(ERecordType.Date, "DATE", 3, 3, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Time, "TIME", 3, 3, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Timestamp, "DATETIME", 8, 8,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Timestamp, "TIMESTAMP", 4, 4,
				false, false));

		rcList.add(new DBTypeRecord(ERecordType.Text, "TEXT", 0, (int) Math
				.pow(2, 16), false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "TINYTEXT", 1, 255,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "MEDIUMTEXT", 1,
				(int) Math.pow(2, 24), false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "LONGTEXT", 1, (int) Math
				.pow(2, 32), false, false));

		rcList.add(new DBTypeRecord(ERecordType.Enum, "ENUM", 1, 2, false,
				false));
		rcList
				.add(new DBTypeRecord(ERecordType.Enum, "SET", 1, 8, false,
						false));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "YEAR", 2, 2, true,
				false));

	}

	public AS400Node() {
		super();
		title = "AS400";
	}

	@Override
	public ImageIcon getIcon() {
		return AS400Utils.WRITE_AS400_NODE_ICON;
	}

	@Override
	public void createDatabaseParam(Wizard wizard) {

		try {
			WizardPanelDescriptor descriptor1 = new DBConnectionDescriptor(
					this, new JPropertySheetPage(wizard.getDialog()),
					JdbcRecordSourceDescriptor.IDENTIFIER, ApplicationData
							.classForName("com.apatar.as400.AS400JdbcParams"),
					"db_connector", "as400");
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

			wizard.setKeyForReferringToDescription("help.connector.as400");
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

}
