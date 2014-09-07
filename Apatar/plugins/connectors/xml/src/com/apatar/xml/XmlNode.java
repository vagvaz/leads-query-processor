/*
_______________________
Apatar Open Source Data Integration
Copyright (C) 2005-2007, Apatar, Inc.
info@apatar.com
195 Meadow St., 2nd Floor
Chicopee, MA 01013

### This program is free software; you can redistribute it and/or modify
### it under the terms of the GNU General Public License as published by
### the Free Software Foundation; either version 2 of the License, or
### (at your option) any later version.

### This program is distributed in the hope that it will be useful,
### but WITHOUT ANY WARRANTY; without even the implied warranty of
### MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.# See the
### GNU General Public License for more details.

### You should have received a copy of the GNU General Public License along
### with this program; if not, write to the Free Software Foundation, Inc.,
### 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
________________________

 */

package com.apatar.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;

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
import com.apatar.core.ETableMode;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.RDBTable;
import com.apatar.core.ReadWriteXMLData;
import com.apatar.core.Record;
import com.apatar.core.SchemaTable;
import com.apatar.core.TableInfo;
import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.RecordSourceDescriptor;
import com.apatar.ui.wizard.TableModeDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class XmlNode extends AbstractNonJdbcDataBaseNode {

	static DataBaseInfo dbi = new DataBaseInfo("", "", "", "", true, true,
			true, true, false);

	static {
		List<DBTypeRecord> rcList = dbi.getAvailableTypes();
		rcList.add(new DBTypeRecord(ERecordType.LongText, "VARCHAR", 0, 64000,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "BIGINT", 8, 8, false,
				false));
	}

	static String FIELD_ID = "_id";
	static String FIELD_PARENT_ID = "_parent_id";
	static String FIELD_TEXT = "__text__";

	List<String> nameAttributes;
	Document doc;

	Map<String, Namespace> namespaces = new HashMap<String, Namespace>();

	Map<Element, XmlChild> elements = new HashMap<Element, XmlChild>();

	long nextId;

	public XmlNode() {
		super();
		title = "XML";

	}

	@Override
	public ImageIcon getIcon() {
		return XmlUtils.READ_XML_NODE_ICON;
	}

	@Override
	public void createDatabaseParam(Wizard wizard) {
		try {
			WizardPanelDescriptor descriptor1 = new DBConnectionDescriptor(
					this, new JPropertySheetPage(wizard.getDialog()),
					RecordSourceDescriptor.IDENTIFIER, Class
							.forName("com.apatar.xml.XmlJdbcParams"),
					"db_connector", "xml");
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

			wizard.setKeyForReferringToDescription("help.connector.xml");
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
	protected void TransformRDBtoTDB() {
		DataBaseTools.completeTransfer();
		if (connectionDataId == -1) {
			return;
		}
		try {
			TableInfo ti = getTiForConnection(OUT_CONN_POINT_NAME);

			XmlJdbcParams xmlParams = (XmlJdbcParams) ApplicationData
					.getProject().getProjectData(getConnectionDataID())
					.getData();

			if (doc == null) {
				doc = getDocument(xmlParams);
			}

			nextId = 1;
			elements.clear();
			fillElements(doc.getRootElement());

			List nl = getElementsByTagName(getTableName(), doc);

			for (Object obj : nl) {
				KeyInsensitiveMap datas = new KeyInsensitiveMap();
				Element elem = (Element) obj;

				List attrs = elem.getAttributes();
				for (Object obj2 : attrs) {
					Attribute attr = (Attribute) obj2;
					String name = attr.getName();
					datas.put(name, attr.getValue());
				}

				List childElems = elem.getChildren();
				for (Object obj2 : childElems) {
					Element childElem = (Element) obj2;
					String name = getDatabaseName(childElem);
					datas.put(name,
							(childElem.getText().length() > 32700 ? childElem
									.getText().subSequence(0, 32699)
									: childElem.getText()));
				}

				datas.put(FIELD_ID, elements.get(elem).getId());
				datas.put(FIELD_PARENT_ID, elements.get(elem).getParentId());

				datas.put(FIELD_TEXT, elem.getText());

				DataBaseInfo dbi = ApplicationData.getTempDataBase()
						.getDataBaseInfo();
				DataBaseTools.insertData(new DataProcessingInfo(dbi, ti
						.getTableName(), ti.getRecords(), ApplicationData
						.getTempJDBC()), datas);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();
		}

	}

	@Override
	protected void TransformTDBtoRDB(int mode) {
		try {
			DataBaseTools.completeTransfer();
			File file = ((XmlJdbcParams) ApplicationData.getProject()
					.getProjectData(getConnectionDataID()).getData()).getFile();
			if (doc == null) {
				doc = ReadWriteXMLData.loadDocument(file);
			}

			TableInfo iTI = getTiForConnection(IN_CONN_POINT_NAME);

			if (nameAttributes == null) {
				nameAttributes = getAttributeNames(doc);
			}

			ResultSet rs = DataBaseTools.getRSWithAllFields(iTI.getTableName(),
					ApplicationData.getTempJDBC(), ApplicationData
							.getTempDataBaseInfo());

			if (mode == AbstractDataBaseNode.INSERT_MODE) {
				insertData(rs, nameAttributes, doc);
			} else {
				updateData(rs, nameAttributes, doc);
			}

			FileWriter writer = new FileWriter(file);

			// TODO decide to use ReadWriteXMLDataUi instead of ReadWriteXMLData
			ReadWriteXMLData rwXMLdata = new ReadWriteXMLData();
			rwXMLdata.saveDocumentToFile(doc, writer);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();
		}
	}

	@Override
	public void createSchemaTable(AbstractApatarActions actions)
			throws Exception {
		SchemaTable st = getTiForConnection(OUT_CONN_POINT_NAME)
				.getSchemaTable();
		if (connectionDataId != -1) {
			st.updateRecords(getFieldList(null));
		}
	}

	@Override
	public void deleteAllRecordsInRDB() throws Exception {
		if (doc == null) {
			doc = getDocument((XmlJdbcParams) ApplicationData.getProject()
					.getProjectData(getConnectionDataID()).getData());
		}

		if (nameAttributes == null) {
			nameAttributes = getAttributeNames(doc);
		}
		doc.getRootElement().removeContent();
	}

	@Override
	public List<Record> getFieldList(AbstractApatarActions actions)
			throws Exception {
		List<Record> fields = new ArrayList<Record>();

		addChildRecord(fields);

		Document doc = getDocument((XmlJdbcParams) ApplicationData.getProject()
				.getProjectData(getConnectionDataID()).getData());
		List<String> currentFields = new ArrayList<String>();
		List elements = getElementsByTagName(getTableName(), doc);
		for (Object obj : elements) {
			Element elem = (Element) obj;
			currentFields.clear();
			for (Object obj2 : elem.getAttributes()) {
				Attribute attr = (Attribute) obj2;
				String name = attr.getName();
				if (currentFields.contains(name)) {
					currentFields.remove(name);
				} else {
					currentFields.add(name);
				}
			}
			List<String> allCurrentFields = new ArrayList<String>();
			for (Object obj2 : elem.getChildren()) {
				Element childElem = (Element) obj2;
				String name = getDatabaseName(childElem);

				if (currentFields.contains(name)) {
					currentFields.remove(name);
				} else {
					if (!allCurrentFields.contains(name)) {
						currentFields.add(name);
						allCurrentFields.add(name);
					}
				}
			}
			List<DBTypeRecord> recs = dbi.getAvailableTypes();
			for (String currentField : currentFields) {
				boolean contains = false;
				for (Record rec : fields) {
					if (rec.getFieldName().equalsIgnoreCase(currentField)) {
						contains = true;
						break;
					}
				}
				if (!contains) {
					fields.add(new Record(DBTypeRecord.getRecordByOriginalType(
							recs, "VARCHAR"), currentField, 255, true, false,
							false));
				}
			}
		}

		return fields;
	}

	@Override
	public List<RDBTable> getTableList() throws Exception {

		Document doc;

		List<RDBTable> tables = new ArrayList<RDBTable>();

		XmlJdbcParams xmlParams = (XmlJdbcParams) ApplicationData.getProject()
				.getProjectData(getConnectionDataID()).getData();

		doc = getDocument(xmlParams);

		Element root = doc.getRootElement();
		generateTable(root, tables, xmlParams.isReadFromFile());

		return tables;

	}

	private void generateTable(Element el, List<RDBTable> tables,
			boolean isReadWrite) {

		Namespace ns = el.getNamespace();
		String nsPrefix = ns.getPrefix();
		RDBTable table = new RDBTable(getDatabaseName(el),
				isReadWrite ? ETableMode.ReadWrite : ETableMode.ReadOnly);
		if (!tables.contains(table)) {
			if (!nsPrefix.equals("")) {
				namespaces.put(nsPrefix, ns);
			}
			tables.add(table);
		}

		for (Object obj : el.getChildren()) {
			if (obj instanceof Element) {
				Element childEl = (Element) obj;
				generateTable(childEl, tables, isReadWrite);
			}
		}
	}

	private void fillElements(Element el) {

		long id = nextId++;
		long parentId = 0;
		Element parentElement = el.getParentElement();
		if (parentElement != null) {
			if (elements.containsKey(parentElement)) {
				parentId = elements.get(parentElement).getId();
			}
		}
		XmlChild xc = new XmlChild(id, parentId);
		elements.put(el, xc);

		for (Object obj : el.getChildren()) {
			if (obj instanceof Element) {
				fillElements((Element) obj);
			}
		}
	}

	private List getElementsByTagName(String name, Document doc)
			throws JDOMException, IOException {
		XPath xpath = XPath.newInstance("//" + parseName(name));
		return xpath.selectNodes(ReadWriteXMLData.getRootElement(doc));
	}

	private String parseName(String name) {
		int pos = name.indexOf("____", 1);
		String prefix = "";
		if (pos > 0) {
			prefix = name.substring(0, pos);
			name = name.substring(pos + 4);
			name = prefix + ":" + name;
		}
		return name;
	}

	private Element getLastElementsByTagName(String name, Document doc)
			throws JDOMException, IOException {
		XPath xpath = XPath.newInstance("//" + parseName(name) + "[last()]");
		List elements = xpath.selectNodes(ReadWriteXMLData.getRootElement(doc));
		if (elements.size() < 1) {
			return ReadWriteXMLData.getRootElement(doc);
		}
		return (Element) xpath
				.selectNodes(ReadWriteXMLData.getRootElement(doc)).get(0);
	}

	private void insertData(ResultSet rs, List<String> nameAttributes,
			Document doc) {
		TableInfo iTI = getTiForConnection(IN_CONN_POINT_NAME);
		String tableName = getTableName();
		try {
			Element lastElem = getLastElementsByTagName(tableName, doc);
			Element parent;
			if (lastElem == null) {
				parent = ReadWriteXMLData.getRootElement(doc);
			} else {
				parent = lastElem.getParentElement();
				if (parent == null) {
					parent = lastElem;
				}
			}

			List<Record> records = iTI.getSchemaTable().getRecords();
			while (rs.next()) {
				insertElement(rs, parent, records, tableName, nameAttributes);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void insertElement(ResultSet rs, Element parent,
			List<Record> records, String tableName, List<String> nameAttributes)
			throws SQLException {
		Element element = createNewElement(tableName);

		for (Record rec : records) {
			String name = rec.getFieldName();
			if (name.equals(FIELD_ID)) {
				continue;
			}
			if (name.equals(FIELD_PARENT_ID)) {
				continue;
			}

			String value = rs.getString(name);
			if (name.equals(FIELD_TEXT)) {
				if (value != null && !value.equals("")) {
					element.setText(value);
				}
				continue;
			}

			if (value == null) {
				continue;
			}
			if (nameAttributes.contains(name)) {
				element.setAttribute(name, value);
			} else {
				Element child = createNewElement(name);
				child.setText(value);
				element.addContent(child);
			}
		}
		parent.addContent(element);
	}

	private void updateData(ResultSet rs, List<String> nameAttributes,
			Document doc) {

		TableInfo iTI = getTiForConnection(IN_CONN_POINT_NAME);
		List<Record> records = iTI.getSchemaTable().getRecords();

		try {
			while (rs.next()) {

				String strXPath = "//" + getTableName();
				for (String str : getIdentificationFields()) {
					strXPath += "[child::" + str + "[text()='"
							+ rs.getString(str) + "' or @" + str + "='"
							+ rs.getString(str) + "']]";
				}

				XPath xpath = XPath.newInstance(strXPath);
				List selElems = xpath.selectNodes(ReadWriteXMLData
						.getRootElement(doc));
				int size = selElems.size();
				if (size > 1) {
					continue;
				} else if (size == 0) {
					insertElement(rs, ReadWriteXMLData.getRootElement(doc),
							records, getTableName(), nameAttributes);
					continue;
				}

				Element selElem = (Element) selElems.get(0);

				for (Record rec : records) {
					String name = rec.getFieldName();
					String value = rs.getString(name);
					if (nameAttributes.contains(name)) {
						if (value == null) {
							selElem.removeAttribute(name);
							continue;
						}
						Attribute attr = selElem.getAttribute(name);
						if (attr == null) {
							attr = new Attribute(name, value);
							continue;
						}
						attr.setValue(value);
						continue;
					} else {
						if (value == null) {
							selElem.removeChildren(name);
							continue;
						}
						Element child = selElem.getChild(name);
						if (child == null) {
							child = createNewElement(name);
						}
						child.setText(value);
					}
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public List<String> getAttributeNames(Document doc) throws JDOMException,
			IOException {
		List<String> fields = new ArrayList<String>();
		List elements = getElementsByTagName(getTableName(), doc);
		for (Object objelem : elements) {
			Element elem = (Element) objelem;
			for (Object objAttr : elem.getAttributes()) {
				Attribute attr = (Attribute) objAttr;
				fields.add(attr.getName());
			}
		}
		return fields;
	}

	@Override
	public Element saveToElement() {
		Element element = super.saveToElement();
		Element nssElement = new Element("namespases");
		element.addContent(nssElement);
		for (Namespace ns : namespaces.values()) {
			Element elem = new Element("namespase");
			elem.setAttribute("prefix", ns.getPrefix());
			elem.setAttribute("uri", ns.getURI());
			nssElement.addContent(elem);
		}
		return element;
	}

	@Override
	public void initFromElement(Element node) {
		super.initFromElement(node);
		Element element = node.getChild("namespases");
		for (Object obj : element.getChildren()) {
			Element childElem = (Element) obj;
			String prefix = childElem.getAttributeValue("prefix");
			Namespace ns = Namespace.getNamespace(prefix, childElem
					.getAttributeValue("uri"));
			namespaces.put(prefix, ns);
		}
	}

	public Element createNewElement(String name) {
		Element element;
		Namespace ns = null;
		int pos = name.indexOf("____", 1);
		if (pos > 0) {
			String prefix = name.substring(0, pos);
			name = name.substring(pos);
			ns = namespaces.get(prefix);
			element = new Element(name);
			if (ns != null) {
				element.setNamespace(ns);
			}
		}
		element = new Element(name);
		if (ns != null) {
			element.setNamespace(ns);
		}
		return element;
	}

	public String getDatabaseName(Element elem) {
		String nsPrefix = elem.getNamespacePrefix();
		return nsPrefix + (nsPrefix.equals("") ? "" : "____") + elem.getName();
	}

	@Override
	public DataBaseInfo getDataBaseInfo() {
		return dbi;
	}

	public static Document getDocument(XmlJdbcParams xmlParams)
			throws JDOMException, IOException {

		Document doc;

		if (xmlParams.isReadFromFile()) {
			File file = xmlParams.getFile();
			doc = ReadWriteXMLData.loadDocument(file);
		} else {
			URL url = new URL(xmlParams.getUrl());
			doc = ReadWriteXMLData.loadDocument(url);
		}
		return doc;
	}

	private static void addChildRecord(List<Record> recs) {
		recs.add(new Record(DBTypeRecord.getRecordByOriginalType(dbi
				.getAvailableTypes(), "BIGINT"), FIELD_ID, 8, false));
		recs.add(new Record(DBTypeRecord.getRecordByOriginalType(dbi
				.getAvailableTypes(), "BIGINT"), FIELD_PARENT_ID, 8, false));
		recs.add(new Record(DBTypeRecord.getRecordByOriginalType(dbi
				.getAvailableTypes(), "VARCHAR"), FIELD_TEXT, 255, true, false,
				false));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.AbstractDataBaseNode#validateConnectionData()
	 */
	@Override
	public boolean validateConnectionData() {
		lastErrorMessage = "";
		XmlJdbcParams c = (XmlJdbcParams) ApplicationData.getProject()
				.getProjectData(connectionDataId).getData();
		if (c.isReadFromFile()
				&& (c.getFile() == null || !c.getFile().isFile() || !c
						.getFile().exists())) {
			lastErrorMessage = "Please select proper file";
		}
		if (!c.isReadFromFile()
				&& (c.getUrl() == null || "".equals(c.getUrl()))) {
			lastErrorMessage = "URL should not be empty";
		}
		if (!isLastErrorMessageEmpty()) {
			return false;
		}
		return true;
	}

}
