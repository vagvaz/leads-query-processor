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

package com.apatar.customtable;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;

import org.jdom.Document;
import org.jdom.Element;

import com.apatar.core.AbstractApatarActions;
import com.apatar.core.AbstractDataBaseNode;
import com.apatar.core.AbstractNonJdbcDataBaseNode;
import com.apatar.core.ApplicationData;
import com.apatar.core.DBTypeRecord;
import com.apatar.core.DataBaseInfo;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DataProcessingInfo;
import com.apatar.core.ERecordType;
import com.apatar.core.ETableMode;
import com.apatar.core.JdbcObject;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.RDBTable;
import com.apatar.core.Record;
import com.apatar.core.SchemaTable;
import com.apatar.core.TableInfo;
import com.apatar.customtable.ui.DataDescriptor;
import com.apatar.customtable.ui.TableSchemaDescriptor;
import com.apatar.ui.schematable.JTableSchemaPanel;
import com.apatar.ui.wizard.TableModeDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class CustomTableNode extends AbstractNonJdbcDataBaseNode {

	static final DataBaseInfo dataBaseInfo = new DataBaseInfo("", "", "\"",
			"\"", true, true, false, true, false);

	static {
		List<DBTypeRecord> rcList = dataBaseInfo.getAvailableTypes();
		
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "BIGINT", 8, 8, false,true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "INT", 4, 4, false,true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "INTEGER", 4, 4,false, true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "SMALLINT", 2, 2,false, true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "TINYINT", 1, 1,false, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "FLOAT", 8, 8, false,true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DOUBLE", 8, 8, false,true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "REAL", 8, 8, false,true));

		rcList.add(new DBTypeRecord(ERecordType.Boolean, "BIT", 1, 1, false,false));
		rcList.add(new DBTypeRecord(ERecordType.Boolean, "BOOLEAN", 1, 1,false, false));
		
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DECIMAL", 16, 16,false, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "NUMERIC", 16, 16,false, true));
		
		rcList.add(new DBTypeRecord(ERecordType.Text, "CHAR", 1, 255, false,false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "CHARACTER", 1, 255,false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "VARCHAR", 1, 255, false,false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "VARCHAR_IGNORECASE", 1,255, false, false));
		rcList.add(new DBTypeRecord(ERecordType.LongText, "LONGVARCHAR", 1,255, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Date, "DATE", 4, 4, false,false));
		rcList.add(new DBTypeRecord(ERecordType.Time, "TIME", 3, 3, false,false));
		rcList.add(new DBTypeRecord(ERecordType.Timestamp, "DATETIME", 8, 8,false, false));
		rcList.add(new DBTypeRecord(ERecordType.Timestamp, "TIMESTAMP", 8, 8,false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "BINARY", 0, 65535,false, false));
		rcList.add(new DBTypeRecord(ERecordType.Clob, "CLOB", 0, 65535, false,false));
		rcList.add(new DBTypeRecord(ERecordType.VarBinary, "VARBINARY", 0,65535, false, false));
		rcList.add(new DBTypeRecord(ERecordType.LongVarBinary, "LONGVARBINARY",0, 65535, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Object, "OBJECT", 0, 65535,false, false));
		rcList.add(new DBTypeRecord(ERecordType.Object, "OTHER", 0, 65535,false, false));
	}

	// data to be inserted before the transformation
	Document data = new Document(new Element("root"));
	

	public Document getData() {
		return data;
	}

	public void setData(Document doc) {
		data = doc;
	}

	public CustomTableNode() {
		super();
		title = "Custom Table_GIANNIS";

		// table name is the name of the output table
		table = new RDBTable(getTiForConnection(OUT_CONN_POINT_NAME).getTableName(), ETableMode.ReadWrite);
	}

	@Override
	public void createDatabaseParam(Wizard wizard) {
		wizard.getDialog().setTitle(title + " Property");

		SchemaTable schema = getTiForConnection(
				AbstractDataBaseNode.OUT_CONN_POINT_NAME).getSchemaTable();

		TableSchemaDescriptor descriptor1 = new TableSchemaDescriptor(new JTableSchemaPanel(ApplicationData.getTempDataBase()
						.getDataBaseInfo().getAvailableTypes(), schema.getRecords()), this);
		wizard.registerWizardPanel(TableSchemaDescriptor.IDENTIFIER,
				descriptor1);

DataDescriptor descriptor2 = new DataDescriptor(this,
				TableSchemaDescriptor.IDENTIFIER,
				WizardPanelDescriptor.FINISH);
		wizard.registerWizardPanel(DataDescriptor.IDENTIFIER, descriptor2);

		WizardPanelDescriptor descriptor3 = new TableModeDescriptor(this,
				DataDescriptor.IDENTIFIER, WizardPanelDescriptor.FINISH);
		wizard.registerWizardPanel(TableModeDescriptor.IDENTIFIER, descriptor3);

		wizard.setKeyForReferringToDescription("help.connector.custom_table");
		wizard.setCurrentPanel(TableSchemaDescriptor.IDENTIFIER,
				Wizard.NEXT_BUTTON_ACTION_COMMAND);

		wizard.showModalDialog();
	}

	@Override
	public ImageIcon getIcon() {
		return CustomUtils.READ_CUSTOM_TABLE_NODE_ICON;
	}

	@Override
	public Element saveToElement() {
		Element node = super.saveToElement();
		Element datas = new Element("datas");

		datas.addContent(getRootElement(data.getRootElement().getChildren()));
		node.addContent(datas);

		return node;
	}

	@Override
	public void initFromElement(Element node) {
		super.initFromElement(node);

		if (ApplicationData.DATAMAP_VERSION
				.compareToIgnoreCase("Apatar_v1.0.8.6") != -1) {
			data = new Document();

			Element datas = node.getChild("datas").getChild("root");

			if (datas != null) {
				Element root = getRootElement(node.getChild("datas").getChild(
						"root").getChildren());
				data.setRootElement(root);
			} else {
				initData(node);
			}
		} else {
			initData(node);
		}
	}

	private Element getRootElement(List children) {
		Element root = new Element("root");

		for (Iterator it = children.iterator(); it.hasNext();) {
			Element row = (Element) it.next();
			Element newRow = new Element(row.getName());

			for (Iterator ch = row.getChildren().iterator(); ch.hasNext();) {
				Element cell = (Element) ch.next();
				Element newCell = new Element(cell.getName());
				newCell.addContent(cell.getValue());

				newRow.addContent(newCell);
			}

			root.addContent(newRow);
		}

		return root;
	}

	@Override
	public List<RDBTable> getTableList() throws Exception {
		ArrayList<RDBTable> rv = new ArrayList<RDBTable>();
		rv.add(table);
		return rv;
	}

	// this method is called before any transformation to make sure that
	// all the required internal tables exist
	@Override
	public void BeforeExecute() {
		super.BeforeExecute();

		// fill the resulting database after creation with the data that has to
		// be filled in
		try {
			KeyInsensitiveMap datas = new KeyInsensitiveMap();
			TableInfo ti = getTiForConnection(OUT_CONN_POINT_NAME);

			List<Record> records = ti.getSchemaTable().getRecords();
			List children = data.getRootElement().getChildren();

			for (Iterator it = children.iterator(); it.hasNext();) {
				Element row = (Element) it.next();
				datas.clear();

				int pos = 0;
				for (Iterator ch = row.getChildren().iterator(); ch.hasNext();) {
					Element cell = (Element) ch.next();

					datas.put(records.get(pos).getFieldName(), new JdbcObject(
							cell.getValue(), Types.VARCHAR));
					pos++;
				}
				DataBaseTools.insertData(new DataProcessingInfo(dataBaseInfo,
						ti.getTableName(), ti.getRecords(), ApplicationData
								.getTempJDBC()), datas);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void TransformRDBtoTDB() {
		// This method logically is not required for CustomTableNode
		// all the data is already stored in the output table
	}

	@Override
	protected void TransformTDBtoRDB(int mode) {
		try {
			DataBaseTools.completeTransfer();

			DataProcessingInfo srcPI = new DataProcessingInfo(dataBaseInfo,
					getTiForConnection(IN_CONN_POINT_NAME).getTableName(),
					getTiForConnection(IN_CONN_POINT_NAME).getSchemaTable()
							.getRecords(), ApplicationData.getTempJDBC());
			DataProcessingInfo destPI = new DataProcessingInfo(dataBaseInfo,
					getTableName(), getTiForConnection(OUT_CONN_POINT_NAME)
							.getSchemaTable().getRecords(), ApplicationData
							.getTempJDBC());

			DataBaseTools.TransferData(srcPI, destPI, mode,
					identificationFields, false);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();
		}
	}

	// table is internal it is already created
	@Override
	public void createSchemaTable(AbstractApatarActions actions)
			throws Exception {
	}

	@Override
	public void deleteAllRecordsInRDB() throws Exception {
	}

	@Override
	public List<Record> getFieldList(AbstractApatarActions actions)
			throws Exception {
		TableInfo ti = getTiForConnection(OUT_CONN_POINT_NAME);

		List<Record> res = new ArrayList<Record>();
		for (Record rec : ti.getSchemaTable().getRecords()) {
			DBTypeRecord dbirec = DBTypeRecord.getRecordByOriginalType(
					getDataBaseInfo().getAvailableTypes(), rec
							.getOriginalType());
			res.add(new Record(dbirec, rec.getFieldName(), rec.getLength(), rec
					.isNullable(), false, rec.isPrimaryKey()));
		}

		return ti.getSchemaTable().getRecords();
	}

	@Override
	public DataBaseInfo getDataBaseInfo() {
		return dataBaseInfo;
	}

	private void convertDataToCurrentVersion(Object[][] oldData) {
		Element root = new Element("root");

		int rowCount = oldData.length;
		int colCount = oldData[0].length;

		for (int i = 0; i < rowCount; i++) {
			Element row = new Element("row");
			List<Record> recs = getTiForConnection(
					AbstractDataBaseNode.OUT_CONN_POINT_NAME).getRecords();
			for (int j = 0; j < colCount; j++) {
				Element cell = new Element(recs.get(j).getFieldName());
				Object content = oldData[i][j];
				if (null == content) {
					cell.addContent("");
				} else {
					cell.addContent(content.toString());
				}
				row.addContent(cell);
			}

			root.addContent(row);
		}

		data.addContent(root);

	}

	private void initData(Element node) {
		Element eldatas = node.getChild("datas");
		if (eldatas == null) {
			ApplicationData.COUNT_INIT_ERROR++;
			return;
		}
		List datas = eldatas.getChildren();
		if (datas == null) {
			ApplicationData.COUNT_INIT_ERROR++;
			return;
		}
		Object[][] dataOldFormat = new Object[datas.size()][getTiForConnection(
				OUT_CONN_POINT_NAME).getSchemaTable().getRecords().size()];

		int i = 0;
		for (Iterator it = datas.iterator(); it.hasNext();) {
			Element dataElement = (Element) it.next();
			for (Iterator it2 = dataElement.getChildren().iterator(); it2
					.hasNext();) {
				Element column = (Element) it2.next();
				String str = column.getAttributeValue("index");
				if (str != null) {
					int index = Integer.parseInt(str);
					dataOldFormat[i][index] = column.getValue();
				} else {
					ApplicationData.COUNT_INIT_ERROR++;
				}
			}
			i++;
		}
		convertDataToCurrentVersion(dataOldFormat);
	}
}
