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

package com.apatar.buzzsaw;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JDialog;

import org.apache.commons.httpclient.HttpException;
import org.apache.webdav.lib.BaseProperty;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.WebdavResources;
import org.apache.webdav.lib.methods.XMLResponseMethodBase.Response;
import org.jdom.Element;
import org.w3c.dom.Node;

import propertysheet.JPropertySheetPage;

import com.apatar.buzzsaw.ui.BuzzsawFolderTreeDescriptor;
import com.apatar.core.AbstractDataBaseNode;
import com.apatar.core.ApplicationData;
import com.apatar.core.DBTypeRecord;
import com.apatar.core.DataBaseInfo;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DataProcessingInfo;
import com.apatar.core.ERecordType;
import com.apatar.core.JdbcObject;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.LogUtils;
import com.apatar.core.Record;
import com.apatar.core.SchemaTable;
import com.apatar.core.TableInfo;
import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;
import com.apatar.webdav.WebDavNode;
import com.apatar.webdav.ui.WebDavModeDescriptor;

public class BuzzsawNode extends WebDavNode {

	static DataBaseInfo dbi = new DataBaseInfo("", "", "", "", true, true,
			true, true, false);

	private final List<String> dinamicFields = new ArrayList<String>();

	private String innerUri = "";

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

	public BuzzsawNode() {
		super();
		title = "Buzzsaw";
		mode = AbstractDataBaseNode.INSERT_MODE;
	}

	private WebdavResource getBindingBuzzsaw() {
		return getBinding(getBuzzsawConnInfo().getUrl(), getInnerUri(),
				getBuzzsawConnInfo().getLogin(), getBuzzsawConnInfo()
						.getPassword().getValue());
	}

	@Override
	public DataBaseInfo getDataBaseInfo() {
		return dbi;
	}

	@Override
	public void createDatabaseParam(Wizard wizard) {
		JDialog wd = wizard.getDialog();

		wd.setTitle(title + " Property");

		try {
			WizardPanelDescriptor descriptor1 = new DBConnectionDescriptor(
					this,
					new JPropertySheetPage(wizard.getDialog()),
					BuzzsawFolderTreeDescriptor.IDENTIFIER,
					ApplicationData
							.classForName("com.apatar.buzzsaw.BuzzsawConnection"),
					"db_connector", "buzzsaw");
			wizard.registerWizardPanel(DBConnectionDescriptor.IDENTIFIER,
					descriptor1);

			WizardPanelDescriptor descriptor2 = new BuzzsawFolderTreeDescriptor(
					this, DBConnectionDescriptor.IDENTIFIER,
					WebDavModeDescriptor.IDENTIFIER);
			wizard.registerWizardPanel(BuzzsawFolderTreeDescriptor.IDENTIFIER,
					descriptor2);

			WizardPanelDescriptor descriptor3 = new WebDavModeDescriptor(this,
					BuzzsawFolderTreeDescriptor.IDENTIFIER,
					WizardPanelDescriptor.FINISH);
			wizard.registerWizardPanel(WebDavModeDescriptor.IDENTIFIER,
					descriptor3);

			wizard.setKeyForReferringToDescription("help.connector.buzzsaw");
			wizard.setCurrentPanel(DBConnectionDescriptor.IDENTIFIER,
					Wizard.NEXT_BUTTON_ACTION_COMMAND);

			wizard.showModalDialog();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createSchemaTable() throws Exception {
		SchemaTable st = getTiForConnection(OUT_CONN_POINT_NAME)
				.getSchemaTable();
		st.removeAllRecord();

		WebdavResource resource = getBindingBuzzsaw();

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

		WebdavResources resources = resource.getChildResources();
		WebdavResource[] res = resources.listResources();

		Enumeration en = null;
		dinamicFields.clear();

		for (WebdavResource re : res) {
			en = re.propfindMethod(0);

			Enumeration properties = ((Response) en.nextElement())
					.getProperties();

			while (properties.hasMoreElements()) {
				BaseProperty prop = (BaseProperty) properties.nextElement();
				addDinamicFieldToList(prop.getElement());
			}
		}

		// add dinamic fields to SchemaTable
		for (String string : dinamicFields) {
			st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
					"VARCHAR"), string, 255, false, false, false));
		}
	}

	private void addDinamicFieldToList(Node node) {
		String fieldNameSpace = node.getNamespaceURI();
		String fieldName = node.getLocalName();

		// does node have children or not
		if (node.hasChildNodes()) {
			for (int i = 0; i < node.getChildNodes().getLength(); i++) {
				if (1 == node.getChildNodes().item(i).getNodeType()) {
					addDinamicFieldToList(node.getChildNodes().item(i));
				}
			}
		}

		// is this bazzsaw property or not
		if ("http://www.buzzsaw.com/projectpoint"
				.equalsIgnoreCase(fieldNameSpace)) {
			fieldName = "Buzzsaw_" + fieldName;
		}

		// does dinamicFields have fieldName or not
		if (!dinamicFields.contains(fieldName)) {
			dinamicFields.add(fieldName);
		}
	}

	private void putPropertyValueToTable(Node node, KeyInsensitiveMap datas) {
		String fieldNameSpace = node.getNamespaceURI();
		String fieldName = node.getLocalName();
		String fieldValue = node.getTextContent();

		// does node have children or not
		if (node.hasChildNodes()) {
			for (int i = 0; i < node.getChildNodes().getLength(); i++) {
				if (1 == node.getChildNodes().item(i).getNodeType()) {
					putPropertyValueToTable(node.getChildNodes().item(i), datas);
				}
			}
		}

		// is this bazzsaw property or not
		if ("http://www.buzzsaw.com/projectpoint"
				.equalsIgnoreCase(fieldNameSpace)) {
			fieldName = "Buzzsaw_" + fieldName;
		}

		List<Record> recs = getTiForConnection(OUT_CONN_POINT_NAME)
				.getRecords();
		Record rec = Record.getRecordByFieldName(recs, fieldName);
		if (rec != null) {
			datas.put(fieldName, new JdbcObject(fieldValue, rec.getSqlType()));
		}
	}

	@Override
	public String getTitle() {
		String result = super.getTitle();
		if ("WebDav".equals(result)) {
			result = "Buzzsaw";
		}
		return result;
	}

	public BuzzsawConnection getBuzzsawConnInfo() {
		return (BuzzsawConnection) ApplicationData.getProject().getProjectData(
				connectionDataId).getData();
	}

	@Override
	public String getInnerUri() {
		return innerUri;
	}

	@Override
	public void setInnerUri(String uri) {
		innerUri = uri;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.AbstractDataBaseNode#TransformTDBtoRDB(int)
	 */
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

				resource = getBindingBuzzsaw();
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
			WebdavResource resource = getBindingBuzzsaw();
			WebdavResources resources = resource.getChildResources();
			WebdavResource[] res = resources.listResources();

			for (WebdavResource re : res) {
				if (!ApplicationData.ProcessingProgress.Status()) {
					return;
				}

				createTable(re);
			}

			deleteTmpFiles();
		} catch (Exception e) {
			ApplicationData.ProcessingProgress.Log(e);
			e.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();
		}

	}

	private void createTable(WebdavResource resource) throws Exception {
		KeyInsensitiveMap record = createBuzzsawRecord(resource);

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

			for (WebdavResource res : ress) {
				if (!ApplicationData.ProcessingProgress.Status()) {
					return;
				}

				createTable(res);
			}
		}

	}

	private KeyInsensitiveMap createBuzzsawRecord(WebdavResource res) {
		KeyInsensitiveMap datas = createRecord(res, getBuzzsawConnInfo()
				.getUrl(), getInnerUri());

		try {
			Enumeration en = res.propfindMethod(0);
			Enumeration properties = ((Response) en.nextElement())
					.getProperties();

			while (properties.hasMoreElements()) {
				BaseProperty prop = (BaseProperty) properties.nextElement();
				putPropertyValueToTable(prop.getElement(), datas);
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return datas;
	}

	@Override
	public void deleteAllRecordsInRDB() throws Exception {
		WebdavResource resource = getBindingBuzzsaw();
		removeResourses(resource);

	}

	@Override
	public ImageIcon getIcon() {
		return BuzzsawUtils.READ_BUZZSAW_NODE_ICON;
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
		lastErrorMessage = "";
		try {
			WebdavResource resource = getBindingBuzzsaw();
			if (resource == null) {
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			if (isLastErrorMessageEmpty()) {
				lastErrorMessage = LogUtils.GetExceptionMessage(e);
			}
			return false;
		}
	}

}
