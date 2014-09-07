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

package com.apatar.webdav;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JDialog;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.httpclient.URIException;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.WebdavResources;
import org.jdom.Element;

import propertysheet.JPropertySheetPage;

import com.apatar.core.AbstractApatarActions;
import com.apatar.core.AbstractDataBaseNode;
import com.apatar.core.AbstractNonJdbcDataBaseNode;
import com.apatar.core.ApplicationData;
import com.apatar.core.DBTypeRecord;
import com.apatar.core.DataBaseInfo;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DataProcessingInfo;
import com.apatar.core.ERecordType;
import com.apatar.core.JdbcObject;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.LogUtils;
import com.apatar.core.RDBTable;
import com.apatar.core.Record;
import com.apatar.core.SchemaTable;
import com.apatar.core.TableInfo;
import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;
import com.apatar.webdav.ui.WebDavFolderTreeDescriptor;
import com.apatar.webdav.ui.WebDavModeDescriptor;

public class WebDavNode extends AbstractNonJdbcDataBaseNode {

	private WebdavResource webdav = null;
	protected String separator = "/";

	private String innerUri = "";

	protected List<File> listTmpFiles = new ArrayList<File>();

	static DataBaseInfo dbi = new DataBaseInfo("", "", "", "", true, true,
			true, true, false);

	static {
		List<DBTypeRecord> rcList = dbi.getAvailableTypes();
		rcList.add(new DBTypeRecord(ERecordType.Text, "VARCHAR", 0, 255, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "BINARY", 0,
				Long.MAX_VALUE, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "BIGINT", 8, 8, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Date, "DATETIME", 8, 8, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Boolean, "BOOLEAN", 1, 1,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "CHAR", 1, 1, false,
				false));
	}

	public WebDavNode() {
		super();
		title = "WebDAV";
		mode = AbstractDataBaseNode.INSERT_MODE;
	}

	public WebdavResource getBinding() {
		return getBinding(getConnectionInfo().getUrl(), getInnerUri(),
				getConnectionInfo().getLogin(), getConnectionInfo()
						.getPassword().getValue());
	}

	public WebdavResource getBinding(String url, String uri, String login,
			String pass) {
		try {
			if (null == webdav) {
				webdav = new WebdavResource(getHttpUrl(url + uri, login, pass),
						true);
			} else {
				webdav.setHttpURL(getHttpUrl(url + uri, login, pass));
			}

			WebdavResource.setDefaultAction(WebdavResource.ALL);
			webdav.setDebug(1);
			webdav.setProperties(2);

		} catch (IOException e) {
			lastErrorMessage = LogUtils.GetExceptionMessage(e);
			ApplicationData.ProcessingProgress.Log(e);
			e.printStackTrace();
		}

		return webdav;
	}

	private HttpsURL getHttpUrl(String url, String login, String pass) {
		HttpsURL httpUrl = null;

		try {
			httpUrl = new HttpsURL(url);
			httpUrl.setUserinfo(login, pass);
		} catch (URIException e) {
			ApplicationData.ProcessingProgress.Log(e);
			e.printStackTrace();
		} catch (NullPointerException e) {
			ApplicationData.ProcessingProgress.Log(e);
			e.printStackTrace();
		}

		return httpUrl;
	}

	@Override
	public void createDatabaseParam(Wizard wizard) {
		JDialog wd = wizard.getDialog();

		wd.setTitle(title + " Property");

		try {
			WizardPanelDescriptor descriptor1 = new DBConnectionDescriptor(
					this,
					new JPropertySheetPage(wizard.getDialog()),
					WebDavFolderTreeDescriptor.IDENTIFIER,
					ApplicationData
							.classForName("com.apatar.webdav.WebDavConnection"),
					"db_connector", "webdav");
			wizard.registerWizardPanel(DBConnectionDescriptor.IDENTIFIER,
					descriptor1);

			WizardPanelDescriptor descriptor2 = new WebDavFolderTreeDescriptor(
					this, DBConnectionDescriptor.IDENTIFIER,
					WebDavModeDescriptor.IDENTIFIER);
			wizard.registerWizardPanel(WebDavFolderTreeDescriptor.IDENTIFIER,
					descriptor2);

			WizardPanelDescriptor descriptor3 = new WebDavModeDescriptor(this,
					WebDavFolderTreeDescriptor.IDENTIFIER,
					WizardPanelDescriptor.FINISH);
			wizard.registerWizardPanel(WebDavModeDescriptor.IDENTIFIER,
					descriptor3);

			wizard.setKeyForReferringToDescription("help.connector.webdav");
			wizard.setCurrentPanel(DBConnectionDescriptor.IDENTIFIER,
					Wizard.NEXT_BUTTON_ACTION_COMMAND);

			wizard.showModalDialog();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public WebDavConnection getConnectionInfo() {
		return (WebDavConnection) ApplicationData.getProject().getProjectData(
				connectionDataId).getData();
	}

	public String getInnerUri() {
		return innerUri;
	}

	public void setInnerUri(String uri) {
		innerUri = uri;
	}

	@Override
	public DataBaseInfo getDataBaseInfo() {
		return dbi;
	}

	@Override
	public String getTitle() {
		String innerUri = getInnerUri();
		// String url = getBuzzsawConnInfo().getUrl();
		String url = "";
		try {
			url = getConnectionInfo().getUrl();
		} catch (Exception e) {
			url = "";
		}

		int index = -1;
		String result = "";

		if (!"".equals(innerUri)) {
			index = innerUri.lastIndexOf(separator);
			if (-1 != index) {
				result = innerUri.substring(index + 1, innerUri.length());
			}
		} else if (!"".equals(url)) {
			index = url.lastIndexOf(separator);
			if (-1 != index) {
				result = url.substring(index + 1, url.length());
			}
		}
		if ("".equals(result)) {
			result = "WebDav";
		}
		return result;
	}

	@Override
	public void createSchemaTable(AbstractApatarActions actions)
			throws Exception {
		SchemaTable st = getTiForConnection(OUT_CONN_POINT_NAME)
				.getSchemaTable();
		st.removeAllRecord();

		List<DBTypeRecord> recs = dbi.getAvailableTypes();
		st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
				"BINARY"), "Content", 20000, false, false, false));
		st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
				"VARCHAR"), "String_Content", 20000, false, false, false));
		st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
				"VARCHAR"), "Name", 255, false, false, false));
		st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
				"VARCHAR"), "Path", 255, false, false, false));
		st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
				"BIGINT"), "Size", 8, false, false, false));
		st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
				"DATETIME"), "Modified", 8, false, false, false));
		st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
				"BOOLEAN"), "Read", 1, false, false, false));
		st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
				"BOOLEAN"), "Write", 1, false, false, false));
		st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
				"BOOLEAN"), "isFolder", 1, false, false, false));
	}

	@Override
	protected void TransformTDBtoRDB(int mode) {
		try {
			DataBaseTools.completeTransfer();
			TableInfo ti = getTiForConnection(IN_CONN_POINT_NAME);
			ResultSet rs = DataBaseTools.getRSWithAllFields(ti.getTableName(),
					ApplicationData.tempDataBase.getJdbcParams(),
					ApplicationData.getTempDataBaseInfo());

			WebdavResource resource = null;

			while (rs.next()) {
				boolean isFolder = rs.getBoolean("isFolder");

				resource = getBinding();
				// pathRes - path to resource
				String pathRes = convertHttpToString(resource.getHttpURL());
				// path - inner path from db
				String path = rs.getString("Path");

				if (path.length() > 0) {
					if (separator.equals(path.substring(0, 1))
							|| "\\".equals(path.substring(0, 1))) {
						pathRes += path;
					} else {
						pathRes = pathRes + separator + path;
					}
				}

				if (isFolder) {
					resource.mkcolMethod(pathRes);
				} else {
					InputStream in = rs.getBinaryStream("Content");
					if (null != in) {
						resource.putMethod(pathRes, in);
						in.close();
					} else {
						// if Content field is null, but String_Content field is
						// not null
						String strContent = rs.getString("String_Content");
						if (null != strContent && !"".equals(strContent)) {
							byte[] bytes = strContent.getBytes();
							resource.putMethod(pathRes, bytes);
						} else {
							resource.putMethod(pathRes, "");
						}
					}
				}

				if (!ApplicationData.ProcessingProgress.Step()) {
					return;
				}

				ApplicationData.ProcessingProgress.Log("Uploading resource: "
						+ pathRes);
			}

		} catch (Exception e1) {
			ApplicationData.ProcessingProgress.Log(e1);
			e1.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();
		}
	}

	@Override
	protected void TransformRDBtoTDB() {
		try {
			DataBaseTools.completeTransfer();
			WebdavResource currentres = getBinding();
			WebdavResources resources = currentres.getChildResources();
			WebdavResource[] res = resources.listResources();

			for (WebdavResource element : res) {
				if (!ApplicationData.ProcessingProgress.Status()) {
					return;
				}

				createTable(element);
			}

			deleteTmpFiles();
		} catch (Exception e) {
			ApplicationData.ProcessingProgress.Log(e);
			e.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();
		}

	}

	protected void deleteTmpFiles() {
		File f = null;
		for (Iterator<File> it = listTmpFiles.iterator(); it.hasNext();) {
			f = it.next();
			f.delete();
		}
	}

	private void createTable(WebdavResource resource) throws Exception {
		KeyInsensitiveMap record = createRecord(resource, getConnectionInfo()
				.getUrl(), getInnerUri());

		if (!ApplicationData.ProcessingProgress.Step()) {
			return;
		}

		TableInfo ti = getTiForConnection(OUT_CONN_POINT_NAME);

		DataBaseInfo dbi = ApplicationData.getTempDataBase().getDataBaseInfo();
		DataBaseTools.insertData(new DataProcessingInfo(dbi, ti.getTableName(),
				ti.getRecords(), ApplicationData.getTempJDBC()), record);

		Object obj = record.get("Content", true);

		if (obj != null) {
			((InputStream) obj).close();
		}

		if (resource.listWebdavResources().length > 0) {
			WebdavResources resources = resource.getChildResources();
			WebdavResource[] ress = resources.listResources();

			for (WebdavResource element : ress) {
				if (!ApplicationData.ProcessingProgress.Status()) {
					return;
				}

				createTable(element);
			}
		} // if
	}

	protected KeyInsensitiveMap createRecord(WebdavResource res, String url,
			String uri) {
		KeyInsensitiveMap datas = new KeyInsensitiveMap();

		if (res.isCollection()) {
			datas.put("isFolder", true);
			datas.put("String_Content", "");
			datas.put("Content", new JdbcObject(null, Types.BLOB));
		} else {
			datas.put("isFolder", false);
			try {

				if (0 < res.getGetContentLength()) {
					InputStream in = res.getMethodData();
					File f = File.createTempFile("apatar", ".tmp");

					listTmpFiles.add(f);

					FileOutputStream fout = new FileOutputStream(f);
					StringBuffer strBuff = new StringBuffer();

					int read = 0;
					while (-1 != read) {

						if (!ApplicationData.ProcessingProgress.Status()) {
							return null;
						}

						read = in.read();
						fout.write(read);

						if (strBuff.length() < 2000) {
							strBuff.append((char) read);
						}
					}
					fout.close();

					datas.put("Content", new FileInputStream(f));
					datas.put("String_Content", strBuff.toString());
					in.close();

				} else {
					datas.put("Content", new JdbcObject(null, Types.BLOB));
					datas.put("String_Content", "");
				}
			} catch (HttpException e) {
				ApplicationData.ProcessingProgress.Log(e);
				e.printStackTrace();
			} catch (IOException e) {
				ApplicationData.ProcessingProgress.Log(e);
				e.printStackTrace();
			}
		}

		String path = convertHttpToString(res.getHttpURL());
		path = path.replace(url, "");
		path = path.replace(uri, "");

		datas.put("Name", res.getDisplayName());
		datas.put("Path", path);
		datas.put("Size", res.getGetContentLength());
		datas.put("Modified", new Date(res.getGetLastModified()));
		datas.put("Read", true);
		datas.put("Write", !res.isLocked());

		ApplicationData.ProcessingProgress.Log("Downloading resource: "
				+ convertHttpToString(res.getHttpURL()));

		return datas;
	}

	@Override
	public List<RDBTable> getTableList() throws Exception {
		return null;
	}

	@Override
	public List<Record> getFieldList(AbstractApatarActions actions)
			throws Exception {
		return null;
	}

	@Override
	public void deleteAllRecordsInRDB() throws Exception {
		WebdavResource resource = getBinding();
		removeResourses(resource);
	}

	protected void removeResourses(WebdavResource resource) {

		try {
			WebdavResource ress[] = resource.getChildResources()
					.listResources();

			for (WebdavResource element : ress) {

				if (element.listWebdavResources().length > 0) {
					removeResourses(element);
				}

				resource
						.deleteMethod(convertHttpToString(element.getHttpURL()));

				if (!ApplicationData.ProcessingProgress.Step()) {
					return;
				}

				ApplicationData.ProcessingProgress.Log("Removing resource: "
						+ convertHttpToString(element.getHttpURL()));
			}
		} catch (HttpException e) {
			ApplicationData.ProcessingProgress.Log(e);
			e.printStackTrace();
		} catch (IOException e) {
			ApplicationData.ProcessingProgress.Log(e);
			e.printStackTrace();
		}
	}

	@Override
	public ImageIcon getIcon() {
		return WebDavUtils.READ_WEBDAV_NODE_ICON;
	}

	protected String convertHttpToString(HttpURL url) {
		return url.toString().replace("%20", " ");
	}

	@Override
	public Element saveToElement() {
		Element e = super.saveToElement();
		e.setAttribute("innerUri", innerUri);

		return e;
	}

	@Override
	public void initFromElement(Element e) {
		super.initFromElement(e);
		innerUri = e.getAttributeValue("innerUri");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.AbstractDataBaseNode#validateConnectionData()
	 */
	@Override
	public boolean validateConnectionData() {
		try {
			WebDavConnection c = getConnectionInfo();
			if ("".equals(c.getUrl())) {
				lastErrorMessage = "URL should not be empty";
				return false;
			}
			lastErrorMessage = "";
			if (getBinding() == null) {
				return false;
			} else if (!isLastErrorMessageEmpty()) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			lastErrorMessage = LogUtils.GetExceptionMessage(e);
			return false;
		}
	}

}
