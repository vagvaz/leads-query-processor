/*
_______________________
Apatar Open Source Data Integration
Copyright (C) 2005-2007, Apatar, Inc.
info@apatar.com
195 Meadow St., 2nd Floor
Chicopee, MA 01013

??? This program is free software; you can redistribute it and/or modify
??? it under the terms of the GNU General Public License as published by
??? the Free Software Foundation; either version 2 of the License, or
??? (at your option) any later version.

??? This program is distributed in the hope that it will be useful,
??? but WITHOUT ANY WARRANTY; without even the implied warranty of
??? MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.? See the
??? GNU General Public License for more details.

??? You should have received a copy of the GNU General Public License along
??? with this program; if not, write to the Free Software Foundation, Inc.,
??? 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
________________________

 */
package com.apatar.postgresql;

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

public class PostgreSqlNode extends AbstractJdbcDataBase {

	static final DataBaseInfo dataBaseInfo = new DataBaseInfo("\"", "\"", "\"",

	"\"", true, true, true, true, true);

	public PostgreSqlNode() {

		super();

		title = "PostgreSQL";

	}

	static {

		List<DBTypeRecord> rcList = dataBaseInfo.getAvailableTypes();

		rcList.add(new DBTypeRecord(ERecordType.Numeric, "int2", 2, 2, true,

		true));

		rcList.add(new DBTypeRecord(ERecordType.Numeric, "int4", 4, 4, true,

		true));

		rcList.add(new DBTypeRecord(ERecordType.Numeric, "int8", 8, 8, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "decimal", 8, 8, true,

		true));

		rcList.add(new DBTypeRecord(ERecordType.Decimal, "numeric", 8, 8, true,

		true));

		rcList.add(new DBTypeRecord(ERecordType.Decimal, "float4", 4, 4, true,

		true));

		rcList.add(new DBTypeRecord(ERecordType.Decimal, "float8", 8, 8, true,

		true));

		rcList.add(new DBTypeRecord(ERecordType.Numeric, "serial", 4, 4, false,

		true));

		rcList.add(new DBTypeRecord(ERecordType.Numeric, "bigserial", 8, 8,

		false, true));

		rcList.add(new DBTypeRecord(ERecordType.Numeric, "tid", 4, 4, true,

		true));

		rcList.add(new DBTypeRecord(ERecordType.Numeric, "xid", 4, 4, true,

		true));

		rcList.add(new DBTypeRecord(ERecordType.Numeric, "money", 4, 4, true,

		true));

		rcList

		.add(new DBTypeRecord(ERecordType.Text, "char", 1, 1, false,

		true));

		rcList.add(new DBTypeRecord(ERecordType.Text, "bpchar", 1, 1, false,

		true));

		rcList.add(new DBTypeRecord(ERecordType.Text, "character", 1, 1, false,

		true));

		rcList.add(new DBTypeRecord(ERecordType.Text, "character varying", 1,

		1, false, true));

		rcList.add(new DBTypeRecord(ERecordType.Text, "varchar", 0, 0x40000000,

		false, true));

		rcList.add(new DBTypeRecord(ERecordType.Text, "aclitem", 0, 0x40000000,

		false, true));

		rcList.add(new DBTypeRecord(ERecordType.Text, "name", 0, 64, false,

		true));

		rcList.add(new DBTypeRecord(ERecordType.Text, "text", 0, 0x40000000,

		false, true));

		rcList.add(new DBTypeRecord(ERecordType.Binary, "bytea", 1048576,

		1048576, false, true));

		rcList.add(new DBTypeRecord(ERecordType.Timestamp, "timestamp", 8, 8,
				false,

				true));

		rcList.add(new DBTypeRecord(ERecordType.Time, "interval", 12, 12,

		false, true));

		rcList

		.add(new DBTypeRecord(ERecordType.Date, "date", 4, 4, false,

		true));

		rcList.add(new DBTypeRecord(ERecordType.Time, "time", 8, 12, false,

		true));

		rcList.add(new DBTypeRecord(ERecordType.Date, "abstime", 8, 12, false,

		true));

		rcList.add(new DBTypeRecord(ERecordType.Date, "reltime", 8, 12, false,

		true));

		rcList.add(new DBTypeRecord(ERecordType.Date, "tinterval", 8, 12,

		false, true));

		rcList.add(new DBTypeRecord(ERecordType.Time, "timetz", 8, 12, false,

		true));

		rcList.add(new DBTypeRecord(ERecordType.Time, "time without time zone",

		8, 12, false, true));

		rcList.add(new DBTypeRecord(ERecordType.Timestamp, "timestamptz", 8, 8,

		false, true));

		rcList.add(new DBTypeRecord(ERecordType.Timestamp,

		"timestamp without time zone", 8, 8, false, true));

		rcList.add(new DBTypeRecord(ERecordType.Boolean, "bool", 1, 1, false,

		false));

		rcList.add(new DBTypeRecord(ERecordType.Binary, "point", 16, 16, false,

		false));

		rcList.add(new DBTypeRecord(ERecordType.Binary, "line", 32, 32, false,

		false));

		rcList.add(new DBTypeRecord(ERecordType.Binary, "lseg", 32, 32, false,

		false));

		rcList.add(new DBTypeRecord(ERecordType.Binary, "box", 32, 32, false,

		false));

		rcList.add(new DBTypeRecord(ERecordType.Binary, "path", 16, 1048576,

		false, false));

		rcList.add(new DBTypeRecord(ERecordType.Binary, "polygon", 40, 1048576,

		false, false));

		rcList.add(new DBTypeRecord(ERecordType.Binary, "circle", 24, 24,

		false, false));

		rcList.add(new DBTypeRecord(ERecordType.Numeric, "iod", 4, 4, true,

		true));

		rcList.add(new DBTypeRecord(ERecordType.Text, "refcursor", 12, 24,

		false, false));

		rcList.add(new DBTypeRecord(ERecordType.Text, "regclass", 12, 24,

		false, false));

		rcList.add(new DBTypeRecord(ERecordType.Text, "regoper", 12, 24, false,

		false));

		rcList.add(new DBTypeRecord(ERecordType.Text, "regoperator", 12, 24,

		false, false));

		rcList.add(new DBTypeRecord(ERecordType.Text, "regproc", 12, 24, false,

		false));

		rcList.add(new DBTypeRecord(ERecordType.Text, "regprocedure", 12, 24,

		false, false));

		rcList.add(new DBTypeRecord(ERecordType.Text, "regtype", 12, 24, false,

		false));

		rcList.add(new DBTypeRecord(ERecordType.Text, "smgr", 12, 24, false,

		false));

		rcList.add(new DBTypeRecord(ERecordType.Text, "cidr", 12, 24, false,

		false));

		rcList.add(new DBTypeRecord(ERecordType.Text, "cid", 12, 24, false,

		false));

		rcList.add(new DBTypeRecord(ERecordType.Text, "oid", 4, 4, true, true));

		rcList.add(new DBTypeRecord(ERecordType.Text, "inet", 12, 24, false,

		false));

		rcList.add(new DBTypeRecord(ERecordType.Text, "macaddr", 6, 6, false,

		false));

		rcList.add(new DBTypeRecord(ERecordType.Text, "bit", 0, 255, false,

		false));

		rcList.add(new DBTypeRecord(ERecordType.Text, "varbit", 0, 255, false,

		false));

		rcList.add(new DBTypeRecord(ERecordType.Text, "varying", 0, 255, false,

		false));

		rcList.add(new DBTypeRecord(ERecordType.Text, "bit varying", 0, 255,

		false, false));

		rcList.add(new DBTypeRecord(ERecordType.Binary, "Arrays", 0, 65535,

		false, false));

	}

	@Override
	public DataBaseInfo getDataBaseInfo() {

		return dataBaseInfo;

	}

	@Override
	protected void TransformTDBtoRDB(int mode) {

		super.TransformTDBtoRDB(mode, true);

	}

	@Override
	public void createDatabaseParam(Wizard wizard) {

		try {

			WizardPanelDescriptor descriptor1 = new DBConnectionDescriptor(

			this,

			new JPropertySheetPage(wizard.getDialog()),

			JdbcRecordSourceDescriptor.IDENTIFIER,

			ApplicationData

			.classForName("com.apatar.postgresql.PostgreSqlJdbcParams"),

			"db_connector", "postgresql");

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

			wizard.setKeyForReferringToDescription("help.connector.postgresql");

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
	public ImageIcon getIcon() {

		return PostgreSqlUtils.READ_POSTGRE_NODE_ICON;

	}

}