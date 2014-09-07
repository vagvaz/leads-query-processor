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

package com.apatar.amazon.s3;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JDialog;

import org.jdom.Element;

import propertysheet.JPropertySheetPage;

import com.amazon.s3.AWSAuthConnection;
import com.apatar.amazon.AmazonUtils;
import com.apatar.amazon.s3.ui.ListBucketDescriptor;
import com.apatar.core.AbstractApatarActions;
import com.apatar.core.AbstractNonJdbcDataBaseNode;
import com.apatar.core.ApplicationData;
import com.apatar.core.DBTypeRecord;
import com.apatar.core.DataBaseInfo;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DataProcessingInfo;
import com.apatar.core.ERecordType;
import com.apatar.core.ETableMode;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.RDBTable;
import com.apatar.core.Record;
import com.apatar.core.SchemaTable;
import com.apatar.core.TableInfo;
import com.apatar.core.DataBaseTools.SQLCreationData;
import com.apatar.core.DataBaseTools.SQLQueryString;
import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.RecordSourceDescriptor;
import com.apatar.ui.wizard.TableModeDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class AmazonS3Node extends AbstractNonJdbcDataBaseNode {

	static final DataBaseInfo dataBaseInfo = new DataBaseInfo("", "", "", "",
			true, true, false, true, false);

	public static String STRING_TYPE = "STRING";
	public static String BINARY_TYPE = "BINARY";

	List<Bucket> buckets;

	static {
		List<DBTypeRecord> rcList = dataBaseInfo.getAvailableTypes();
		rcList.add(new DBTypeRecord(ERecordType.Text, STRING_TYPE, 0, 65535,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, BINARY_TYPE, 0, 65535,
				false, false));
	}

	public AmazonS3Node() {
		super();
		title = "Amazon S3";
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
							.classForName("com.apatar.amazon.s3.AmazonS3Connection"),
					"db_connector", "amazonS3");
			wizard.registerWizardPanel(DBConnectionDescriptor.IDENTIFIER,
					descriptor1);

			WizardPanelDescriptor descriptor2 = new RecordSourceDescriptor(
					this, DBConnectionDescriptor.IDENTIFIER,
					ListBucketDescriptor.IDENTIFIER);
			wizard.registerWizardPanel(RecordSourceDescriptor.IDENTIFIER,
					descriptor2);

			WizardPanelDescriptor descriptor3 = new ListBucketDescriptor(this);
			wizard.registerWizardPanel(ListBucketDescriptor.IDENTIFIER,
					descriptor3);

			WizardPanelDescriptor descriptor4 = new TableModeDescriptor(this,
					ListBucketDescriptor.IDENTIFIER,
					WizardPanelDescriptor.FINISH);
			wizard.registerWizardPanel(TableModeDescriptor.IDENTIFIER,
					descriptor4);

			wizard.setKeyForReferringToDescription("help.connector.amazons3");
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

		st.updateRecords(getFieldList(actions));
	}

	@Override
	protected void TransformRDBtoTDB() {
		try {
			DataBaseTools.completeTransfer();
			AmazonS3Connection aS3C = (AmazonS3Connection) ApplicationData
					.getProject().getProjectData(connectionDataId).getData();
			AWSAuthConnection aConn = new AWSAuthConnection(aS3C
					.getAccessKeyID().getValue(), aS3C.getSecretAccessKey()
					.getValue());

			AmazonS3Function func = AmazonS3FunctionList
					.getAmazonS3FunctionByName(getTableName());

			TableInfo ti = getTiForConnection(IN_CONN_POINT_NAME);

			TableInfo tiOut = getTiForConnection(OUT_CONN_POINT_NAME);

			if (table.getMode() == ETableMode.ReadOnly) {
				executeFunction(func, null, aConn, tiOut);
				return;
			} else {
				if (aS3C.isChooseItemsFromList() && func.isAllowManually()) {
					for (Bucket bucket : buckets) {
						KeyInsensitiveMap kiMap = new KeyInsensitiveMap();
						kiMap.put("bucket", bucket.getName());
						if (func.isWorkingWithBucket()) {
							executeFunction(func, kiMap, aConn, tiOut);
						} else {
							for (String key : bucket.objectKeys) {
								kiMap.put("key", key);
								executeFunction(func, kiMap, aConn, tiOut);
							}
						}
					}
				} else {
					List<Record> selectionList = DataBaseTools
							.intersectionRecords(tiOut.getRecords(), ti
									.getRecords(), true);

					// read values from result set and put it in request
					SQLQueryString sqs = DataBaseTools
							.CreateSelectString(ApplicationData
									.getTempDataBase().getDataBaseInfo(),
									new SQLCreationData(selectionList, ti
											.getTableName()), null);

					if (sqs == null) {
						return;
					}

					ResultSet rs;
					try {
						rs = DataBaseTools.executeSelect(sqs, ApplicationData
								.getTempJDBC());

						while (rs.next()) {
							KeyInsensitiveMap rsData = DataBaseTools
									.GetDataFromRS(rs);
							executeFunction(func, rsData, aConn, tiOut);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		} finally {
			DataBaseTools.completeTransfer();
		}

	}

	@Override
	protected void TransformTDBtoRDB(int mode) {
		DataBaseTools.completeTransfer();
	}

	@Override
	public void deleteAllRecordsInRDB() throws Exception {
		// binding can be empty

	}

	@Override
	public List<Record> getFieldList(AbstractApatarActions actions)
			throws Exception {
		List<Record> rv = new ArrayList<Record>();

		AmazonS3Function func = AmazonS3FunctionList
				.getAmazonS3FunctionByName(getTableName());

		List<AmazonS3RequestParameters> merge = new ArrayList<AmazonS3RequestParameters>();
		merge.addAll(func.request);
		merge.addAll(func.response);

		for (AmazonS3RequestParameters param : merge) {
			DBTypeRecord dbt = DBTypeRecord.getRecordByOriginalType(
					dataBaseInfo.getAvailableTypes(), param.getType());
			rv.add(new Record(dbt, param.getName(), 65535, true, false, false));

		}

		return rv;
	}

	@Override
	public DataBaseInfo getDataBaseInfo() {
		return dataBaseInfo;
	}

	@Override
	public List<RDBTable> getTableList() throws Exception {
		List<RDBTable> list = new ArrayList<RDBTable>();
		for (AmazonS3Function func : AmazonS3FunctionList.functions.values()) {
			list.add(new RDBTable(func.getDisplayName(), func.getMode()));
		}
		return list;
	}

	public List<Bucket> getBuckets() {
		return buckets;
	}

	public void setBuckets(List<Bucket> buckets) {
		this.buckets = buckets;
	}

	private void executeFunction(AmazonS3Function func,
			KeyInsensitiveMap rsData, AWSAuthConnection aConn, TableInfo tiOut) {
		try {
			List<KeyInsensitiveMap> lkim = func.execute(rsData, aConn);

			for (KeyInsensitiveMap kim : lkim) {
				KeyInsensitiveMap insertData = new KeyInsensitiveMap();
				if (rsData != null) {
					insertData.putAll(rsData.getMap());
				}
				insertData.putAll(kim.getMap());

				DataBaseTools.insertData(new DataProcessingInfo(ApplicationData
						.getTempDataBase().getDataBaseInfo(), tiOut
						.getTableName(), tiOut.getRecords(), ApplicationData
						.getTempJDBC()), insertData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
