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

package com.apatar.sybase;

import java.util.List;

import javax.swing.ImageIcon;

import propertysheet.JPropertySheetPage;

import com.apatar.core.AbstractJdbcDataBase;
import com.apatar.core.ApplicationData;
import com.apatar.core.DBTypeRecord;
import com.apatar.core.DataBaseInfo;
import com.apatar.core.ERecordType;
import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.JdbcRecordSourceDescriptor;
import com.apatar.ui.wizard.TableModeDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class SyBaseNode extends AbstractJdbcDataBase {

	static final DataBaseInfo dataBaseInfo = new DataBaseInfo("[", "]", "[",
			"]", true, true, true, true, true);

	static {
		List<DBTypeRecord> rcList = dataBaseInfo.getAvailableTypes();
		rcList.add(new DBTypeRecord(ERecordType.Text, "CHAR", 0, 32767, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "CHARACTER", 0, 32768,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "CHARACTER VARYING", 0,
				32769, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "VARCHAR", 0, 32770,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "LONG VARCHAR", 0,
				0x40000000, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "TEXT", 0, 0x40000000,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "INTEGER", 4, 4, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "INT", 4, 4, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "BIGINT", 4, 4, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "SMALLINT", 2, 2,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "REAL", 4, 4, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "FLOAT", 4, 4, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DOUBLE", 8, 8, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "TINYINT", 1, 1,
				false, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DECIMAL", 8, 8, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "NUMERIC", 8, 8,
				false, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "MONEY", 8, 8, false,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "SMALLMONEY", 8, 8,
				false, true));
		rcList.add(new DBTypeRecord(ERecordType.Date, "DATE", 4, 4, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Time, "TIME", 8, 8, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Date, "TIMESTAMP", 8, 8, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Date, "DATETIME", 8, 8, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Date, "SMALLDATETIME", 8, 8,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "BIT", 1, 1, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "BINARY", 0, 32767,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "LONG BINARY", 0,
				0x40000000, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "IMAGE", 0, 0x40000000,
				false, false));

	}

	public SyBaseNode() {
		super();
		title = "SyBASE";
	}

	@Override
	public ImageIcon getIcon() {
		return SyBaseUtils.READ_SYBASE_NODE_ICON;
	}

	@Override
	public void createDatabaseParam(Wizard wizard) {

		try {
			WizardPanelDescriptor descriptor1 = new DBConnectionDescriptor(
					this,
					new JPropertySheetPage(wizard.getDialog()),
					JdbcRecordSourceDescriptor.IDENTIFIER,
					ApplicationData
							.classForName("com.apatar.sybase.SyBaseJdbcParams"),
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

			wizard.setKeyForReferringToDescription("help.connector.sybase");
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

}