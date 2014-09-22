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

package com.apatar.amazon;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JDialog;

import org.jdom.Element;

import propertysheet.JPropertySheetPage;

import com.apatar.core.AbstractApatarActions;
import com.apatar.core.AbstractDataBaseNode;
import com.apatar.core.AbstractNonJdbcDataBaseNode;
import com.apatar.core.ApplicationData;
import com.apatar.core.DBTypeRecord;
import com.apatar.core.DataBaseInfo;
import com.apatar.core.ERecordType;
import com.apatar.core.RDBTable;
import com.apatar.core.Record;
import com.apatar.core.SchemaTable;
import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.RecordSourceDescriptor;
import com.apatar.ui.wizard.TableModeDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class AmazonNode extends AbstractNonJdbcDataBaseNode {

	static final DataBaseInfo	dataBaseInfo	= new DataBaseInfo("", "", "",
														"", true, true, true,
														true, false);

	static {
		List<DBTypeRecord> rcList = dataBaseInfo.getAvailableTypes();
		// currently salesforce supports only string type
		rcList.add(new DBTypeRecord(ERecordType.Text, "STRING", 0, 16864,
				false, false));
	}

	public AmazonNode() {
		super();
		title = "Amazon";
	}

	@Override
	public ImageIcon getIcon() {
		return AmazonUtils.READ_AMAZON_NODE_ICON;
	}

	@Override
	public Element saveToElement() {
		return super.saveToElement();
	}

	@Override
	public void initFromElement(Element e) {
		super.initFromElement(e);
	}

	@Override
	public void createDatabaseParam(Wizard wizard) {
		JDialog wd = wizard.getDialog();
		wd.setTitle(title + " Property");
		try {
			WizardPanelDescriptor descriptor1 = new DBConnectionDescriptor(
					this,
					new JPropertySheetPage(wd),
					RecordSourceDescriptor.IDENTIFIER,
					ApplicationData
							.classForName("com.apatar.amazon.AmazonConnection"),
					"db_connector", "amazon");
			wizard.registerWizardPanel(DBConnectionDescriptor.IDENTIFIER,
					descriptor1);

			WizardPanelDescriptor descriptor2 = new RecordSourceDescriptor(
					this, DBConnectionDescriptor.IDENTIFIER,
					TableModeDescriptor.IDENTIFIER);
			wizard.registerWizardPanel(RecordSourceDescriptor.IDENTIFIER,
					descriptor2);

			WizardPanelDescriptor descriptor3 = new TableModeDescriptor(this,
					RecordSourceDescriptor.IDENTIFIER,
					WizardPanelDescriptor.FINISH);
			wizard.registerWizardPanel(TableModeDescriptor.IDENTIFIER,
					descriptor3);

			wizard.setKeyForReferringToDescription("help.connector.amazon");
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
	public void createSchemaTable(AbstractApatarActions actions)
			throws Exception {
		if (connectionDataId == -1) {
			return;
		}

		SchemaTable st = getTiForConnection(OUT_CONN_POINT_NAME)
				.getSchemaTable();

		st.updateRecords(getFieldList(null));
	}

	@Override
	protected void TransformRDBtoTDB() {
		// binding can be empty
	}

	protected void InsertTDBtoRDB() {
	}

	private void updateTDBtoRDB() {
	}

	@Override
	protected void TransformTDBtoRDB(int mode) {
		if (mode == AbstractDataBaseNode.INSERT_MODE) {
			InsertTDBtoRDB();
		} else {
			updateTDBtoRDB();
		}

	}

	@Override
	public void deleteAllRecordsInRDB() throws Exception {
		// binding can be empty

	}

	@Override
	public List<Record> getFieldList(AbstractApatarActions actions)
			throws Exception {
		List<Record> res = new ArrayList<Record>();

		return res;
	}

	@Override
	public DataBaseInfo getDataBaseInfo() {
		return dataBaseInfo;
	}

	@Override
	public List<RDBTable> getTableList() throws Exception {
		return null;
	}

}
