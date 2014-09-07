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

package com.apatar.oracle;

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

public class OracleNode extends AbstractJdbcDataBase {

	static final DataBaseInfo dataBaseInfo = new DataBaseInfo("", "", "", "",
			true, true, true, true, true);
	static {
		List<DBTypeRecord> rcList = dataBaseInfo.getAvailableTypes();
		rcList.add(new DBTypeRecord(ERecordType.Boolean, "BOOLEAN", 1, 1,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "CHAR", 0, 2000, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "NCHAR", 0, 2000, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "LONG", 0,
				0x80000000/* 2 GB */, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "VARCHAR2", 0, 4000,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "NVARCHAR2", 0, 4000,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "CHARACTER", 0, 2000,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "INT", 38, 38, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "INTEGER", 38, 38,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "SMALLINT", 38, 38,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DEC", 38, 38, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DECIMAL", 38, 38,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "NUMERIC", 38, 38,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "NUMBER", 38, 38,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DOUBLE PRECISION",
				126, 126, true, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "FLOAT", 126, 126,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "REAL", 126, 126,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "BINARY_DOUBLE", 8, 8,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "BINARY_FLOAT", 4, 4,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Date, "DATE", 7, 7, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Timestamp, "TIMESTAMP", 7, 7,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Date, "INTERVAL DAY TO SECOND",
				8, 8, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Date, "INTERVAL YEAR TO MONTH",
				8, 8, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Date, "INTERVALYM", 8, 8,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Date, "INTERVALDS", 8, 8,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.LongVarBinary, "LONG RAW", 0,
				0x80000000/* 2 GB */, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "MLSLABEL", 2, 5,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.VarBinary, "RAW", 2000, 2000,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "ROWID", 18, 18, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "UROWID", 4000, 4000,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "ORDAudio", 0, 65535,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "ORDDoc", 0, 65535,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "ORDImage", 0, 65535,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "ORDImageSignature", 0,
				65535, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "SI_AverageColor", 0,
				65535, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "SI_Color", 0, 65535,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "SI_ColorHistogram", 0,
				65535, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "SI_FeatureList", 0,
				65535, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "SI_PositionalColor",
				0, 65535, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "SI_StilImage", 0,
				65535, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "SI_Texture", 0, 65535,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "ORDVideo", 0, 65535,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "SDO_GEOMETRY", 0,
				65535, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "SDO_RASTER", 0, 65535,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "URIType", 0,
				0x80000000, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "DBURIType", 0,
				0x80000000, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "HTTPURIType", 0,
				0x80000000, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "XDBURIType", 0,
				0x80000000, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "BFILE", 0,
				0xFFFFFFFF/*
						 * 4�� - 1 ����
						 */, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "BLOB", 0,
				0xFFFFFFFF/*
						 * 4�� - 1 ����
						 */, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Clob, "CLOB", 0,
				0xFFFFFFFF/*
						 * 4�� - 1 ����
						 */, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Clob, "NCLOB", 0,
				0xFFFFFFFF/*
						 * 4�� - 1 ����
						 */, false, false));

	}

	public OracleNode() {
		super();
		title = "Oracle";
	}

	@Override
	public ImageIcon getIcon() {
		return OracleUtils.READ_ORACLE_NODE_ICON;
	}

	@Override
	public void createDatabaseParam(Wizard wizard) {

		try {
			WizardPanelDescriptor descriptor1 = new DBConnectionDescriptor(
					this,
					new JPropertySheetPage(wizard.getDialog()),
					JdbcRecordSourceDescriptor.IDENTIFIER,
					ApplicationData
							.classForName("com.apatar.oracle.OracleJdbcParams"),
					"db_connector", "oracle");
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

			wizard.setKeyForReferringToDescription("help.connector.oracle");
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.AbstractJdbcDataBase#TransformTDBtoRDB(int)
	 */
	@Override
	protected void TransformTDBtoRDB(int mode) {
		super.TransformTDBtoRDB(mode, true);
	}

}
