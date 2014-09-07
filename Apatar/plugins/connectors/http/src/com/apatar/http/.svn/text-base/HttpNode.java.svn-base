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

package com.apatar.http;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JDialog;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.jdom.Element;

import propertysheet.JPropertySheetPage;

import com.apatar.core.AbstractApatarActions;
import com.apatar.core.AbstractDataBaseNode;
import com.apatar.core.AbstractNonJdbcDataBaseNode;
import com.apatar.core.ApplicationData;
import com.apatar.core.DBTypeRecord;
import com.apatar.core.DataBaseInfo;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DataConversionAlgorithm;
import com.apatar.core.DataProcessingInfo;
import com.apatar.core.ERecordType;
import com.apatar.core.ETableMode;
import com.apatar.core.JdbcObject;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.RDBTable;
import com.apatar.core.Record;
import com.apatar.core.SchemaTable;
import com.apatar.core.TableInfo;
import com.apatar.core.DataBaseTools.SQLCreationData;
import com.apatar.core.DataBaseTools.SQLQueryString;
import com.apatar.http.ui.TableSchemaDescriptor;
import com.apatar.ui.schematable.JTableSchemaPanel;
import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class HttpNode extends AbstractNonJdbcDataBaseNode {

	static final DataBaseInfo dataBaseInfo = new DataBaseInfo("", "", "\"",
			"\"", true, true, false, true, false);

	static {
		List<DBTypeRecord> rcList = dataBaseInfo.getAvailableTypes();
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "BIGINT", 8, 8, false,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "INT", 4, 4, false,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "INTEGER", 4, 4,
				false, true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "SMALLINT", 2, 2,
				false, true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "TINYINT", 1, 1,
				false, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "FLOAT", 8, 8, false,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DOUBLE", 8, 8, false,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "REAL", 8, 8, false,
				true));

		rcList.add(new DBTypeRecord(ERecordType.Boolean, "BIT", 1, 1, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Boolean, "BOOLEAN", 1, 1,
				false, false));

		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DECIMAL", 16, 16,
				false, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "NUMERIC", 16, 16,
				false, true));
		rcList.add(new DBTypeRecord(ERecordType.Text, "CHAR", 1, 255, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "CHARACTER", 1, 255,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "VARCHAR", 1, 255, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "VARCHAR_IGNORECASE", 1,
				255, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "LONGVARCHAR", 1, 255,
				false, false));

		rcList.add(new DBTypeRecord(ERecordType.Date, "DATE", 4, 4, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Time, "TIME", 3, 3, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Date, "DATETIME", 8, 8, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "TIMESTAMP", 8, 8,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "BINARY", 0, 65535,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "VARBINARY", 0, 65535,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "LONGVARBINARY", 0,
				65535, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Object, "OBJECT", 0, 65535,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Object, "OTHER", 0, 65535,
				false, false));

	}

	// data to be inserted before the transformation
	Object data[][] = new Object[0][0];

	public Object[][] getData() {
		return data;
	}

	public void setData(Object[][] data) {
		this.data = data;
	}

	public HttpNode() {
		super();
		title = "HTTP";

		// table name is the name of the output table
		table = new RDBTable(getTiForConnection(OUT_CONN_POINT_NAME)
				.getTableName(), ETableMode.ReadWrite);

		SchemaTable sch = getTiForConnection(
				AbstractDataBaseNode.OUT_CONN_POINT_NAME).getSchemaTable();
		DBTypeRecord dbtRec = DataConversionAlgorithm.bestRecordLookup(
				getDataBaseInfo().getAvailableTypes(), ERecordType.Text, 255);
		sch.getRecords().add(0,
				new Record(dbtRec, "Response", 255, false, false, false));
		;
	}

	@Override
	public void createDatabaseParam(Wizard wizard) {
		JDialog wd = wizard.getDialog();

		wd.setTitle(title + " Property");
		try {
			WizardPanelDescriptor descriptor1;

			descriptor1 = new DBConnectionDescriptor(this,
					new JPropertySheetPage(wd),
					TableSchemaDescriptor.IDENTIFIER, ApplicationData
							.classForName("com.apatar.http.HttpConnection"),
					"db_connector", "http");
			wizard.registerWizardPanel(DBConnectionDescriptor.IDENTIFIER,
					descriptor1);

			SchemaTable schema = getTiForConnection(
					AbstractDataBaseNode.OUT_CONN_POINT_NAME).getSchemaTable();
			TableSchemaDescriptor descriptor2 = new TableSchemaDescriptor(
					new JTableSchemaPanel(ApplicationData.getTempDataBase()
							.getDataBaseInfo().getAvailableTypes(), schema
							.getRecords()), this);
			wizard.registerWizardPanel(TableSchemaDescriptor.IDENTIFIER,
					descriptor2);

			wizard.setKeyForReferringToDescription("help.connector.http");
			wizard.setCurrentPanel(DBConnectionDescriptor.IDENTIFIER,
					Wizard.NEXT_BUTTON_ACTION_COMMAND);

			wizard.showModalDialog();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ImageIcon getIcon() {
		return HttpUtils.READ_CUSTOM_TABLE_NODE_ICON;
	}

	@Override
	public Element saveToElement() {
		Element node = super.saveToElement();

		Element datas = new Element("datas");
		for (Object[] element : data) {
			Element dataElement = new Element("data");
			for (int j = 0; j < element.length; j++) {
				Element column = new Element("column");
				if (element[j] != null) {
					column.addContent(element[j].toString());
					column.setAttribute("index", "" + j);
					dataElement.addContent(column);
				}
			}
			datas.addContent(dataElement);
		}
		node.addContent(datas);
		return node;
	}

	@Override
	public void initFromElement(Element node) {
		super.initFromElement(node);
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

			for (Object[] element : data) {
				datas.clear();
				for (int j = 0; j < element.length; j++) {
					datas.put(records.get(j).getFieldName(), element[j]
							.toString());
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
		DataBaseTools.completeTransfer();
		// This method logically is not required for HttpNode
		// all the data is already stored in the output table
	}

	@Override
	protected void TransformTDBtoRDB(int mode) {
		DataBaseTools.completeTransfer();
		HttpConnection conn = (HttpConnection) ApplicationData.getProject()
				.getProjectData(getConnectionDataID()).getData();
		String url = conn.getUrl();
		HttpRequestMethod httpRequestMethod = (HttpRequestMethod) conn
				.getMethod();

		TableInfo ti = getTiForConnection(IN_CONN_POINT_NAME);

		TableInfo tiOut = getTiForConnection(OUT_CONN_POINT_NAME);

		List<Record> selectionList = DataBaseTools.intersectionRecords(tiOut
				.getRecords(), ti.getRecords(), true);

		// read values from result set and put it in request
		SQLQueryString sqs = DataBaseTools.CreateSelectString(ApplicationData
				.getTempDataBase().getDataBaseInfo(), new SQLCreationData(
				selectionList, ti.getTableName()), null);

		if (sqs == null) {
			return;
		}

		ResultSet rs;
		try {
			rs = DataBaseTools
					.executeSelect(sqs, ApplicationData.getTempJDBC());

			while (rs.next()) {
				KeyInsensitiveMap rsData = DataBaseTools.GetDataFromRS(rs);

				HttpMethod hm;

				if (httpRequestMethod == HttpRequestMethod.post) {
					hm = sendPost(url, rsData);
				} else {
					hm = sendGet(url, rsData);
				}

				HttpClient client = new HttpClient();
				if (ApplicationData.httpClient.isUseProxy()) {
					HostConfiguration hostConfig = client
							.getHostConfiguration();
					hostConfig.setProxy(ApplicationData.httpClient.getHost(),
							ApplicationData.httpClient.getPort());
					String proxyUser = ApplicationData.httpClient.getUserName();
					if (proxyUser != null) {
						client.getState().setProxyCredentials(
								AuthScope.ANY,
								new UsernamePasswordCredentials(proxyUser,
										ApplicationData.httpClient
												.getPassword()));
					}
				}

				client.getHttpConnectionManager().getParams()
						.setConnectionTimeout(5000);
				int status = client.executeMethod(hm);

				KeyInsensitiveMap datas = new KeyInsensitiveMap();

				if (status != HttpStatus.SC_OK) {
					datas.put("Response", "Upload failed, response="
							+ HttpStatus.getStatusText(status));
				} else {
					datas.put("Response", hm.getResponseBodyAsString());
				}

				ti = getTiForConnection(OUT_CONN_POINT_NAME);

				List<Record> recs = getTiForConnection(
						AbstractDataBaseNode.OUT_CONN_POINT_NAME)
						.getSchemaTable().getRecords();

				for (int j = 1; j < recs.size(); j++) {
					Record rec = recs.get(j);
					datas.put(rec.getFieldName(), rs.getObject(rec
							.getFieldName()));
				}

				DataBaseTools.insertData(new DataProcessingInfo(ApplicationData
						.getTempDataBase().getDataBaseInfo(),
						ti.getTableName(), ti.getRecords(), ApplicationData
								.getTempJDBC()), datas);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		DataBaseTools.completeTransfer();
	}

	private HttpMethod sendPost(String url, KeyInsensitiveMap data) {

		try {

			PostMethod post = new PostMethod(url);
			List<Record> recs = getTiForConnection(
					AbstractDataBaseNode.OUT_CONN_POINT_NAME).getSchemaTable()
					.getRecords();
			Part[] parts = new Part[recs.size() - 1];
			for (int i = 1; i < recs.size(); i++) {
				Record rec = recs.get(i);
				String fieldName = rec.getFieldName();
				Object obj = data.get(fieldName, true);
				if (obj instanceof JdbcObject) {
					obj = ((JdbcObject) obj).getValue();
				}
				if (rec.getType() == ERecordType.Binary) {
					parts[i - 1] = new FilePart(fieldName, ApplicationData
							.createFile("temp.temp", (byte[]) obj));
				} else {
					parts[i - 1] = new StringPart(fieldName, obj.toString());
				}
			}
			post.setRequestEntity(new MultipartRequestEntity(parts, post
					.getParams()));

			return post;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private HttpMethod sendGet(String url, KeyInsensitiveMap data) {
		try {
			GetMethod get = new GetMethod(url);
			List<Record> recs = getTiForConnection(
					AbstractDataBaseNode.OUT_CONN_POINT_NAME).getSchemaTable()
					.getRecords();
			for (int i = 1; i < recs.size(); i++) {
				Record rec = recs.get(i);
				String fieldName = rec.getFieldName();
				Object obj = data.get(fieldName, true);
				if (obj instanceof JdbcObject) {
					obj = ((JdbcObject) obj).getValue();
				}
				if (rec.getType() == ERecordType.Binary) {
					continue;
				} else {
					get.addRequestHeader(fieldName, obj.toString());
				}
			}

			return get;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.AbstractDataBaseNode#validateConnectionData()
	 */
	@Override
	public boolean validateConnectionData() {
		HttpConnection conn = (HttpConnection) ApplicationData.getProject()
				.getProjectData(connectionDataId).getData();
		if (conn.getUrl() == null || "".equals(conn.getUrl())) {
			lastErrorMessage = "URL should not be empty";
			return false;
		}
		return true;
	}

}
