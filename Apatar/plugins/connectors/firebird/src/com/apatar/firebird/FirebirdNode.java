//////////////////////////////////////////////////////////
// Firebird Node
// Author : Aamir Tauqeer (Kuwait)
// Date   : December 12, 2008
// Final  : December 25, 2008
//////////////////////////////////////////////////////////

package com.apatar.firebird;

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

public class FirebirdNode extends AbstractJdbcDataBase {
	public FirebirdNode() {
		title = "Firebird";
	}

	@Override
	public ImageIcon getIcon() {
		return FirebirdUtils.READ_FIRBIRD_NODE_ICON;
	}

	@Override
	public void createDatabaseParam(Wizard wizard) {
		try {
			WizardPanelDescriptor descriptor1 = new DBConnectionDescriptor(
					this,
					new JPropertySheetPage(wizard.getDialog()),
					"RECORDSOURCE_PANEL",
					ApplicationData
							.classForName("com.apatar.firebird.FirebirdJdbcParams"),
					"db_connector", "firbird");
			wizard.registerWizardPanel("DBCONNECTION_PANEL", descriptor1);
			WizardPanelDescriptor descriptor2 = new JdbcRecordSourceDescriptor(
					this, "DBCONNECTION_PANEL", "TABLEMODE_PANEL");
			wizard.registerWizardPanel("RECORDSOURCE_PANEL", descriptor2);
			WizardPanelDescriptor descriptor3 = new TableModeDescriptor(this,
					"RECORDSOURCE_PANEL", WizardPanelDescriptor.FINISH);
			wizard.registerWizardPanel("TABLEMODE_PANEL", descriptor3);
			wizard.setKeyForReferringToDescription("help.connector.firbird");
			wizard.setCurrentPanel("DBCONNECTION_PANEL",
					"NextButtonActionCommand");
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

	static final DataBaseInfo dataBaseInfo;

	static {
		dataBaseInfo = new DataBaseInfo("", "", "", "", true, true, true, true,
				true);
		List rcList = dataBaseInfo.getAvailableTypes();
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "SMALLINT", 38L, 38L,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "INTEGER", 38L, 38L,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "FLOAT", 38L, 38L,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DOUBLE PRECISION",
				38L, 38L, true, true));
		rcList.add(new DBTypeRecord(ERecordType.Date, "DATE", 7L, 7L, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Time, "TIME", 7L, 7L, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Timestamp, "TIMESTAMP", 7L, 7L,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DECIMAL", 38L, 38L,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "NUMERIC", 38L, 38L,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Text, "CHAR", 0L, 2000L, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "CHARACTER", 0L, 2000L,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "CHARACTER VARYING", 0L,
				2000L, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "VARCHAR", 0L, 2000L,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.TextUnicode, "NCHAR", 0L,
				2000L, false, false));
		rcList.add(new DBTypeRecord(ERecordType.TextUnicode,
				"NATIONAL CHARACTER", 0L, 2000L, false, false));
		rcList.add(new DBTypeRecord(ERecordType.TextUnicode, "NATIONAL CHAR",
				0L, 2000L, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "BLOB", 0L, -1L, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "BLOB SUB_TYPE 1", 0L,
				-1L, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "BLOB SUB_TYPE 0", 0L,
				-1L, false, false));

	}
}
