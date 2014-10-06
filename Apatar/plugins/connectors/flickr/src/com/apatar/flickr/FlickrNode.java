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

package com.apatar.flickr;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JDialog;

import org.apache.xmlrpc.XmlRpcException;
import org.jdom.JDOMException;

import propertysheet.JPropertySheetPage;

import com.apatar.core.AbstractApatarActions;
import com.apatar.core.AbstractNonJdbcDataBaseNode;
import com.apatar.core.ApplicationData;
import com.apatar.core.CoreUtils;
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

/*
 * Exceptions in logic 1. photo field - binary 2. _UploadPhoto - this function
 * is directed to another url
 */

public class FlickrNode extends AbstractNonJdbcDataBaseNode {
	
	static final DataBaseInfo dataBaseInfo = new DataBaseInfo("", "", "", "",
			false, true, false, true, false);

	static {
		List<DBTypeRecord> rcList = dataBaseInfo.getAvailableTypes();
		rcList.add(new DBTypeRecord(ERecordType.Text, "TEXT", 0, 65000, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "PHOTO", 0, (int) Math
				.pow(2, 32), false, false));
	}

	Map<FlickrPermission, String> tokens = new HashMap<FlickrPermission, String>();

	@Override
	public DataBaseInfo getDataBaseInfo() {
		return dataBaseInfo;
	}

	protected boolean isWait = false;

	public FlickrNode() {
		super();
		title = "Flickr";
	}

	@Override
	public void createDatabaseParam(Wizard wizard) {
		JDialog wizardDialog = wizard.getDialog();
		wizardDialog.setTitle(title + " Property");

		try {
			WizardPanelDescriptor descriptor1 = new DBConnectionDescriptor(
					this, new JPropertySheetPage(wizardDialog),
					RecordSourceDescriptor.IDENTIFIER, Class
							.forName("com.apatar.flickr.FlickrConnection"),
					"db_connector", "flickr");
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

			wizard.setKeyForReferringToDescription("help.connector.flickr");
			wizard.setCurrentPanel(DBConnectionDescriptor.IDENTIFIER,
					Wizard.NEXT_BUTTON_ACTION_COMMAND);

			wizard.showModalDialog();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Get Table from List
	 */
	private FlickrTable getFTable() {
		return FlickrTableList.getTableByName(getTableName());
	}

	// **************************************************************************
	// Utilities
	// **************************************************************************

	class RuntimeStream extends Thread {

		URL url = null;
		Process proc = null;

		@Override
		public void run() {
			Runtime r = Runtime.getRuntime();
			System.out.println("stream");

			try {
//				proc = r
//						.exec("C:\\Program Files\\Internet Explorer\\IEXPLORE.EXE "
//								+ url.toString());
				//apon
				proc = r.exec("/Applications/Firefox.app/Contents/MacOS/firefox-bin "+ url.toString());
				if (0 == proc.waitFor()) {
					isWait = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void finalize() throws Throwable {
			super.finalize();
			isWait = false;
		}

		@Override
		public void destroy() {
			isWait = false;
			try {
				super.finalize();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		public void setUrl(URL url) {
			this.url = url;
			System.out.println(this.url);
		}
	}

	// ***************************************************************************************
	// API Access
	// ***************************************************************************************
	String strApi = "403bfb1abfd6e04ce80c84a67782d8c6";
	String strSecret = "45044d7a47acfc61";

	protected String getFrob() throws IOException, XmlRpcException,
			JDOMException {
		// create parameters vector
		FlickrTable table = new FlickrTable("flickr.auth.getFrob");
		table.setResultXPath("//root");
		HashMap<String, Object> returns = new HashMap<String, Object>();
		returns.put("frob", "//frob");
		table.setReturnFieldPathes(returns);
		List<KeyInsensitiveMap> list = table.execute(this,
				new Hashtable<String, Object>(), strApi, strSecret);
		return (String) list.get(0).get("frob", true);
	}

	protected String login(FlickrPermission permission) throws IOException,
			XmlRpcException, JDOMException {

		if (tokens.containsKey(permission)) {
			return tokens.get(permission);
		}
		String frob = getFrob();
		String sig = String.format("%sapi_key%sfrob%sperms%s", strSecret,
				strApi, frob, permission);
		String url = String
				.format(
						"http://flickr.com/services/auth/?api_key=%s&perms=%s&frob=%s&api_sig=%s",
						strApi, permission, frob, CoreUtils.getMD5(sig));

		// pass url to browser and wait until it is closed
		RuntimeStream stream = new RuntimeStream();

		stream.setPriority(Thread.MAX_PRIORITY);
		stream.setUrl(new URL(url));
		stream.start();
		isWait = true;
		while (isWait) {
		}

		// after permission passed get token of the user and save it
		Hashtable<String, Object> hs = new Hashtable<String, Object>();
		hs.put("frob", frob);
		FlickrTable table = new FlickrTable("flickr.auth.getToken");
		table.setResultXPath("//root");
		HashMap<String, Object> returns = new HashMap<String, Object>();
		returns.put("token", "//token");
		table.setReturnFieldPathes(returns);
		List<KeyInsensitiveMap> list = table.execute(this, hs, strApi,
				strSecret);

		String str = list.get(0).get("token", false).toString();
		tokens.put(permission, str);

		return str;
	}

	// *********************************************************************************
	// Table List & field list support
	// *********************************************************************************
	@Override
	public List<RDBTable> getTableList() throws Exception {

		List<RDBTable> list = new ArrayList<RDBTable>();
		for (FlickrTable table : FlickrTableList.getFlickrTablesList().values()) {
			list.add(new RDBTable(table.getTableName(), table.getMode()));
		}
		return list;
	}

	@Override
	public List<Record> getFieldList(AbstractApatarActions actions)
			throws Exception {
		List<Record> rv = new ArrayList<Record>();
		FlickrTable table = FlickrTableList.getTableByName(getTableName());

		// Read database return only the list from return fields
		// Write database merges return and write fields both together
		HashMap<String, Object> merged = new HashMap<String, Object>();
		merged.putAll(table.getOptionalArguments());
		merged.putAll(table.getReturnFieldsPathes());

		for (String str : merged.keySet()) {
			DBTypeRecord dbt = DBTypeRecord.getRecordByOriginalType(
					dataBaseInfo.getAvailableTypes(), str
							.compareToIgnoreCase("photo") == 0 ? "PHOTO"
							: "TEXT");
			rv.add(new Record(dbt, str, 65000, true, false, false));

		}
		return rv;
	}

	// *********************************************************************************
	// Real transformation
	// *********************************************************************************
	@Override
	protected void TransformRDBtoTDB() {
		tokens.clear();
		try {
			DataBaseTools.completeTransfer();
			FlickrTable table = FlickrTableList.getTableByName(getTableName());
			String token = login(table.getFlickrPermissions());

			// prepare values from the optional in
			Hashtable<String, Object> values = new Hashtable<String, Object>();
			values.putAll(table.getOptionalArguments());
			values.put("auth_token", token);

			// FlickrUtils.Sign(values, strApi, strSecret);

			// KeyInsensitiveMap data;

			TableInfo outTI = getTiForConnection(OUT_CONN_POINT_NAME);
			DataProcessingInfo destinationTableInfo = new DataProcessingInfo(
					ApplicationData.getTempDataBase().getDataBaseInfo(), outTI
							.getTableName(), outTI.getRecords());

			/*
			 * Element root = table.execute(this, values, strApi, strSecret);
			 * XPath path = XPath.newInstance(table.getResultXPath()); List
			 * nodes = path.selectNodes(root); Map<String, Object>
			 * returnFieldPathes = table.getReturnFieldsPathes(); Map<String,
			 * Object> optionalArguments = table.getOptionalArguments();
			 * KeyInsensitiveMap data = new KeyInsensitiveMap(); TableInfo outTI
			 * = getTiForConnection(OUT_CONN_POINT_NAME); DataProcessingInfo
			 * destinationTableInfo = new
			 * DataProcessingInfo(ApplicationData.getTempDataBase
			 * ().getDataBaseInfo(), outTI.getTableName(), outTI.getRecords());
			 * for (Object obj : nodes) { Element elem = (Element)obj;
			 * data.clear(); for(String key : returnFieldPathes.keySet()) {
			 * XPath xpath =
			 * XPath.newInstance(returnFieldPathes.get(key).toString()); Object
			 * node = xpath.selectSingleNode(elem); data.put(key, node); } if
			 * (table.getMode() != ETableMode.ReadOnly) { for(String key :
			 * optionalArguments.keySet()) { data.put(key,
			 * optionalArguments.get(key)); } }
			 */

			List<KeyInsensitiveMap> list = table.execute(this, values, strApi,
					strSecret);

			for (KeyInsensitiveMap data : list) {
				DataBaseTools.insertData(destinationTableInfo, data);
				// }
			}
		} catch (XmlRpcException e) {
			// write RPC message into the console
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();
		}
	}

	// build record list to write to real database
	private List<Record> BuildDestinationRecordList() throws Exception {
		FlickrTable table = getFTable();
		List<Record> rv = new ArrayList<Record>();

		for (Record rec : getFieldList(null)) {
			if (table.getOptionalArguments().containsKey(rec.getFieldName())) {
				rv.add(rec);
			}
		}
		return rv;
	}

	@Override
	protected void TransformTDBtoRDB(int mode) {

		// write data to the destination
		TableInfo oti = getTiForConnection(IN_CONN_POINT_NAME);
		tokens.clear();
		try {
			DataBaseTools.completeTransfer();
			FlickrTable table = getFTable();
			String token = login(table.getFlickrPermissions());

			// prepare values from the optional in
			Hashtable<String, Object> values = new Hashtable<String, Object>();

			values.put("auth_token", token);

			// FlickrUtils.Sign(values, strApi, strSecret);

			// values.putAll(table.getOptionalArguments());
			Map<String, Object> optionalArgument = table.getOptionalArguments();
			for (String key : optionalArgument.keySet()) {
				String value = (String) optionalArgument.get(key);
				if (value != null) {
					values.put(key, value);
				}
			}

			// first get intersection of 2 record sets and make select query
			List<Record> selectionList = DataBaseTools.intersectionRecords(oti
					.getRecords(), BuildDestinationRecordList(), true);

			// read values from result set and put it in request
			SQLQueryString sqs = DataBaseTools.CreateSelectString(
					ApplicationData.getTempDataBase().getDataBaseInfo(),
					new SQLCreationData(selectionList, oti.getTableName()),
					null);

			if (sqs == null) {
				return;
				// load values from record set into the values
			}

			TableInfo outTI = getTiForConnection(OUT_CONN_POINT_NAME);
			DataProcessingInfo destinationTableInfo = new DataProcessingInfo(
					ApplicationData.getTempDataBase().getDataBaseInfo(), outTI
							.getTableName(), outTI.getRecords());

			// Map<String, Object> returnFieldPathes =
			// table.getReturnFieldsPathes();
			// Map<String, Object> optionalArguments =
			// table.getOptionalArguments();

			KeyInsensitiveMap data = new KeyInsensitiveMap();

			ResultSet rs = DataBaseTools.executeSelect(sqs, ApplicationData
					.getTempJDBC());
			while (rs.next()) {
				data.clear();
				KeyInsensitiveMap rsData = DataBaseTools.GetDataFromRS(rs);

				for (String key : rsData.keySet()) {
					Object value = rsData.get(key, true);
					if (value != null) {
						values.put(key, value);
					}
				}

				if (!ApplicationData.ProcessingProgress.Step()) {
					return;
				}

				// Send values
				// Element root;
				List<KeyInsensitiveMap> list;
				try {
					list = table.execute(this, values, strApi, strSecret);
				} catch (XmlRpcException e) {
					ApplicationData.ProcessingProgress.Log(e.getMessage());
					continue;
				}

				/*
				 * for(String key : optionalArguments.keySet()) { data.put(key,
				 * rs.getObject(key)); }
				 */

				/*
				 * String resXPath = table.getResultXPath(); if (resXPath !=
				 * null && !resXPath.equals("")) { XPath path =
				 * XPath.newInstance(resXPath); List nodes =
				 * path.selectNodes(root); for (Object obj : nodes) { Element
				 * elem = (Element)obj; for(String key :
				 * returnFieldPathes.keySet()) { XPath xpath =
				 * XPath.newInstance(returnFieldPathes.get(key).toString());
				 * Object node = xpath.selectSingleNode(elem); data.put(key,
				 * node); } // HERE read every Element valuse and put them into
				 * the database } }
				 */

				DataBaseTools.insertData(destinationTableInfo, list.get(0));
			}
		} catch (XmlRpcException e) {
			// write RPC message into the console
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();
		}
	}

	@Override
	public void deleteAllRecordsInRDB() throws Exception {
		//apon
System.err.print("deleteAllRecordsInRDB");
	}

	@Override
	public ImageIcon getIcon() {
		return FlickrUtils.READ_SUGARCRM_NODE_ICON;
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
	public SchemaTable getExpectedShemaTable() {
		List<Record> dr = null;
		try {
			dr = BuildDestinationRecordList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		SchemaTable rv = new SchemaTable();
		rv.updateRecords(dr);
		return rv;
	}

	@Override
	public final void Transform() {

		FlickrTable table = getFTable();
		ApplicationData.ProcessingProgress.Log("Start Writing");
		if (table.getMode() == ETableMode.ReadOnly) {
			TransformRDBtoTDB();
		} else {
			TransformTDBtoRDB(mode);
		}
	}

	public String getToken(FlickrPermission perm) throws IOException,
			XmlRpcException, JDOMException {
		if (tokens.containsKey(perm)) {
			return tokens.get(perm);
		}
		return login(perm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.AbstractDataBaseNode#validateConnectionData()
	 */
	@Override
	public boolean validateConnectionData() {
		FlickrConnection conn = (FlickrConnection) ApplicationData.getProject()
				.getProjectData(connectionDataId).getData();
		if ("".equals(conn.getKey().getValue())
				|| "".equals(conn.getSecret().getValue())) {
			lastErrorMessage = "Key and secret should not be empty";
			return false;
		}
		return true;
	}

}