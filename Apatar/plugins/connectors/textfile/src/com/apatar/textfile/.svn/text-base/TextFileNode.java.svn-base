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

package com.apatar.textfile;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import org.jdom.Element;

import com.apatar.core.AbstractApatarActions;
import com.apatar.core.AbstractNonJdbcDataBaseNode;
import com.apatar.core.ApplicationData;
import com.apatar.core.DBTypeRecord;
import com.apatar.core.DataBaseInfo;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DataProcessingInfo;
import com.apatar.core.ERecordType;
import com.apatar.core.RDBTable;
import com.apatar.core.Record;
import com.apatar.core.SchemaTable;
import com.apatar.textfile.ui.JDBFileConnectionPanel;
import com.apatar.textfile.ui.TextFileConnectionDescriptor;
import com.apatar.ui.wizard.TableModeDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class TextFileNode extends AbstractNonJdbcDataBaseNode {

	FileConnectionInfo fci = null;

	static DataBaseInfo dbi = new DataBaseInfo("", "", "", "", true, true,
			false, false, false);

	@Override
	public DataBaseInfo getDataBaseInfo() {
		return dbi;
	}

	public FileConnectionInfo getConnectionInfo() {
		return fci;
	}

	static {
		List<DBTypeRecord> rcList = dbi.getAvailableTypes();
		rcList.add(new DBTypeRecord(ERecordType.LongText, "LONGVARCHAR", 0,
				255, false, false));
	}

	public TextFileNode() {
		super();
		title = "TextFile";
		fci = new FileConnectionInfo(this);
	}

	@Override
	public ImageIcon getIcon() {
		return TextfileUtils.READ_MYSQL_NODE_ICON;
	}

	@Override
	public void createDatabaseParam(Wizard wizard) {

		java.awt.datatransfer.StringSelection stringSelection = new java.awt.datatransfer.StringSelection(
				"ura clipbord rabotaet");

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		ClipboardOwner owner = new ClipboardOwner() {
			public void lostOwnership(Clipboard clipboard, Transferable contents) {
			}
		};
		clipboard.setContents(stringSelection, owner);

		wizard.getDialog().setTitle(title + " Property");

		WizardPanelDescriptor descriptor1 = new TextFileConnectionDescriptor(
				this, new JDBFileConnectionPanel());
		WizardPanelDescriptor descriptor2 = new TableModeDescriptor(this,
				TextFileConnectionDescriptor.IDENTIFIER,
				WizardPanelDescriptor.FINISH);

		wizard.registerWizardPanel(TextFileConnectionDescriptor.IDENTIFIER,
				descriptor1);
		wizard.registerWizardPanel(TableModeDescriptor.IDENTIFIER, descriptor2);

		wizard.setKeyForReferringToDescription("help.connector.textfile");
		wizard.setCurrentPanel(TextFileConnectionDescriptor.IDENTIFIER,
				Wizard.NEXT_BUTTON_ACTION_COMMAND);

		wizard.showModalDialog();
	}

	@Override
	public void createSchemaTable(AbstractApatarActions actions)
			throws ClassNotFoundException, SQLException {
		SchemaTable st = getTiForConnection(OUT_CONN_POINT_NAME)
				.getSchemaTable();
		st.removeAllRecord();
		String tableName = getTableName();
		try {
			ResultSetMetaData metaData = DataBaseTools.getRSWithAllFields(
					tableName, fci, getDataBaseInfo()).getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				List<DBTypeRecord> recs = dbi.getAvailableTypes();
				Record rec = new Record(DBTypeRecord.getRecordByOriginalType(
						recs, "LONGVARCHAR"), metaData.getColumnName(i), 255,
						true, false, false);
				st.addRecord(rec);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void TransformTDBtoRDB(int mode) {
		try {
			DataBaseTools.completeTransfer();
			DataProcessingInfo srcPI = new DataProcessingInfo(ApplicationData
					.getTempDataBase().getDataBaseInfo(), getTiForConnection(
					IN_CONN_POINT_NAME).getTableName(), getTiForConnection(
					IN_CONN_POINT_NAME).getSchemaTable().getRecords(),
					ApplicationData.getTempJDBC());
			DataProcessingInfo destPI = new DataProcessingInfo(dbi,
					getTableName(), getTiForConnection(OUT_CONN_POINT_NAME)
							.getSchemaTable().getRecords(), fci);

			DataBaseTools.TransferData(true, true, srcPI, destPI, mode,
					identificationFields, null, false);
			// DataBaseTools.TransferData(srcPI, destPI, mode,
			// identificationFields, false);

		} catch (Exception e) {
			ApplicationData.ProcessingProgress.Log(e);
			e.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();
		}

	}

	@Override
	protected void TransformRDBtoTDB() {
		try {
			DataBaseTools.completeTransfer();
			DataProcessingInfo srcPI = new DataProcessingInfo(dbi,
					getTableName(), getTiForConnection(OUT_CONN_POINT_NAME)
							.getSchemaTable().getRecords(), fci);
			DataProcessingInfo destPI = new DataProcessingInfo(ApplicationData
					.getTempDataBase().getDataBaseInfo(), getTiForConnection(
					OUT_CONN_POINT_NAME).getTableName(), getTiForConnection(
					OUT_CONN_POINT_NAME).getSchemaTable().getRecords(),
					ApplicationData.getTempJDBC());

			DataBaseTools.TransferData(srcPI, destPI, true);

		} catch (Exception e) {
			ApplicationData.ProcessingProgress.Log(e);
			e.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();
		}
	}

	// save node
	@Override
	public Element saveToElement() {
		Element readNode = super.saveToElement();
		readNode.addContent(fci.saveToElement());

		return readNode;
	}

	// load node
	@Override
	public void initFromElement(Element node) {
		if (node == null) {
			ApplicationData.COUNT_INIT_ERROR++;
			return;
		}

		super.initFromElement(node);

		Element e = node.getChild(fci.getClass().getName());
		fci.initFromElement(e);
	}

	@Override
	public List<RDBTable> getTableList() throws Exception {
		List<RDBTable> rv = new ArrayList<RDBTable>();
		rv.add(getTable());
		return rv;
	}

	@Override
	public void deleteAllRecordsInRDB() {
		try {
			DataBaseTools.clearRecords(null, fci, getTableName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Record> getFieldList(AbstractApatarActions actions)
			throws Exception {

		List<Record> res = new ArrayList<Record>();

		if ("".equalsIgnoreCase(fci.getPathToFile())
				|| "".equalsIgnoreCase(getTableName())) {
			return res;
		}

		String tableName = getTableName();

		ResultSetMetaData metaData = DataBaseTools.getRSWithAllFields(
				tableName, fci, getDataBaseInfo()).getMetaData();

		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			res.add(new Record(DBTypeRecord.getRecordByOriginalType(dbi
					.getAvailableTypes(), "LONGVARCHAR"), metaData
					.getColumnName(i), 255, true, false, false));
		}

		return res;
	}

}
