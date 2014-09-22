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

package com.apatar.rss;

import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;

import propertysheet.JPropertySheetPage;

import com.apatar.core.AbstractApatarActions;
import com.apatar.core.AbstractDataBaseNode;
import com.apatar.core.AbstractNode;
import com.apatar.core.AbstractNonJdbcDataBaseNode;
import com.apatar.core.ApatarException;
import com.apatar.core.ApatarRegExp;
import com.apatar.core.ApplicationData;
import com.apatar.core.ConnectionPoint;
import com.apatar.core.CoreUtils;
import com.apatar.core.DBTypeRecord;
import com.apatar.core.DataBaseInfo;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DataProcessingInfo;
import com.apatar.core.ERecordType;
import com.apatar.core.ETableMode;
import com.apatar.core.IPersistent;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.Project;
import com.apatar.core.ProjectData;
import com.apatar.core.RDBTable;
import com.apatar.core.ReadWriteXMLData;
import com.apatar.core.Record;
import com.apatar.core.SchemaTable;
import com.apatar.core.TableInfo;
import com.apatar.project.ProjectNode;
import com.apatar.rss.ui.JSettingPanel;
import com.apatar.rss.ui.RssDBConnectionDescriptor;
import com.apatar.rss.ui.SettingDescriptor;
import com.apatar.rss.ui.TypeDescriptor;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.JWorkPane;
import com.apatar.ui.ReadWriteXMLDataUi;
import com.apatar.ui.UiUtils;
import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class RssNode extends AbstractNonJdbcDataBaseNode {

	private static String PUBLISH_URL = "http://www.apatarforge.org/index.php?option=com_remository&func=savefile";

	private static final String RDF_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private static final String ATOM_URI = "http://www.w3.org/2005/Atom";

	private static final Namespace RDF_NS = Namespace.getNamespace("rdf",
			RDF_URI);
	private static final Namespace ATOM_NS = Namespace.getNamespace(ATOM_URI);

	public static String SEPARATOR = "__";

	static DataBaseInfo dbi = new DataBaseInfo("", "", "", "", true, true,
			true, true, false);

	RssElement channel;
	RssElement item;
	List<Record> rssRecords;
	// File file;
	boolean createNew;

	boolean publish = true;
	String username;
	String password;

	String publishId;

	private String rssTitle;
	private String description;

	Map<String, RssElement> rssElementsByName = new HashMap<String, RssElement>();

	static {
		List<DBTypeRecord> rcList = dbi.getAvailableTypes();
		rcList.add(new DBTypeRecord(ERecordType.LongText, "VARCHAR", 0, 255,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.LongText, "LONGVARCHAR", 0,
				255, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "BIGINT", 8, 8, false,
				false));
	}

	Document doc;

	Map<String, Namespace> namespaces = new HashMap<String, Namespace>();

	KeyInsensitiveMap firstLevelElement = new KeyInsensitiveMap();

	public RssNode() {
		super();
		title = "RSS";
	}

	@Override
	public ImageIcon getIcon() {
		return RssUtils.READ_RSS_NODE_ICON;
	}

	@Override
	public void createDatabaseParam(Wizard wizard) {
		try {
			WizardPanelDescriptor descriptor1 = new TypeDescriptor(this);
			wizard.registerWizardPanel(TypeDescriptor.IDENTIFIER, descriptor1);

			WizardPanelDescriptor descriptor2 = new RssDBConnectionDescriptor(
					this, new JPropertySheetPage(wizard.getDialog()));
			wizard.registerWizardPanel(DBConnectionDescriptor.IDENTIFIER,
					descriptor2);

			WizardPanelDescriptor descriptor3 = new SettingDescriptor(this,
					new JSettingPanel());
			wizard.registerWizardPanel(SettingDescriptor.IDENTIFIER,
					descriptor3);

			wizard.setKeyForReferringToDescription("help.connector.rss");
			wizard.setCurrentPanel(TypeDescriptor.IDENTIFIER,
					Wizard.NEXT_BUTTON_ACTION_COMMAND);
			wizard.showModalDialog();

			setTable(getTableAccordingToVersion(this, createNew));

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

			LoadParams loadParams = null;
			CreateNewParams createParams = null;

			Document doc;

			if (createNew) {
				createParams = (CreateNewParams) ApplicationData.getProject()
						.getProjectData(getConnectionDataID()).getData();
				doc = ReadWriteXMLData.loadDocument(createParams.getFile());
			} else {
				loadParams = (LoadParams) ApplicationData.getProject()
						.getProjectData(getConnectionDataID()).getData();
				doc = getDocument(loadParams);
			}

			List nl = getItems(doc);

			List<String> executed = new ArrayList<String>();

			for (Object obj : nl) {
				KeyInsensitiveMap datas = new KeyInsensitiveMap();
				Element elem = (Element) obj;
				try {
					for (Record rec : ti.getRecords()) {
						RssElement rssElem = rssElementsByName.get(rec
								.getFieldName());
						String fieldName = rssElem.generateFieldName();
						if (executed.contains(fieldName)) {
							continue;
						}
						List<String> values = rssElem.getValue(elem);
						int i = 1;
						boolean moreThanOne = false;
						if (values.size() > 1) {
							moreThanOne = true;
						}
						for (Object value : values) {
							String strIndex = (moreThanOne ? (SEPARATOR + i++)
									: "");
							datas.put(fieldName + strIndex, value);
						}
						executed.add(fieldName);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				DataBaseInfo dbi = ApplicationData.getTempDataBase()
						.getDataBaseInfo();
				DataBaseTools.insertData(new DataProcessingInfo(dbi, ti
						.getTableName(), ti.getRecords(), ApplicationData
						.getTempJDBC()), datas);
				executed.clear();
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
			LoadParams loadParams = null;
			CreateNewParams createParams = null;

			if (createNew) {
				createParams = (CreateNewParams) ApplicationData.getProject()
						.getProjectData(getConnectionDataID()).getData();
			} else {
				loadParams = (LoadParams) ApplicationData.getProject()
						.getProjectData(getConnectionDataID()).getData();
			}

			File file;

			Element channelElem = generateElement(channel);

			rssTitle = channel.getChild("title").getValue();
			RssElement rsselemDescr = channel.getChild("description");
			if (rsselemDescr != null) {
				description = rsselemDescr.getValue();
			} else {
				description = rssTitle;
			}

			if (createNew) {
				Enum version = createParams.getVersion();

				Element root = null;

				if (version.equals(Version.RSS_2_0)) {
					root = new Element("rss");
					root.setAttribute("version", "2.0");
					root.addContent(channelElem);
				} else {
					if (version.equals(Version.RSS_1_0)) {
						root = new Element("RDF");
						root.addNamespaceDeclaration(RDF_NS);
						root.setNamespace(RDF_NS);
						root.addContent(channelElem);
						Element items = new Element("items");
						Element seq = new Element("Seq");
						seq.setNamespace(RDF_NS);
						items.addContent(seq);
						channelElem.addContent(items);
					} else {
						if (version.equals(Version.ATOM_1_0)) {
							root = channelElem;
							root.setNamespace(ATOM_NS);
						}
					}

				}
				if (root == null) {
					return;
				}
				doc = new Document(root);
				file = createParams.getFile();

			} else {
				file = loadParams.getSourceFile();
				doc = ReadWriteXMLData.loadDocument(file);
			}

			TableInfo iTI = getTiForConnection(IN_CONN_POINT_NAME);

			ResultSet rs = DataBaseTools.getRSWithAllFields(iTI.getTableName(),
					ApplicationData.getTempJDBC(), ApplicationData
							.getTempDataBaseInfo());

			insertData(rs, doc, iTI, createNew);

			FileWriter writer = new FileWriter(file);
			// TODO decide to use ReadWriteXMLDataUi instead of ReadWriteXMLData
			// TODO decide to use or not this encoding
			ReadWriteXMLData rwXMLdata = new ReadWriteXMLData();
			rwXMLdata.saveDocumentToFile(doc, writer, "windows-1251");

			if (publish) {
				publishing();
			}

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
		// if (doc == null)
		// doc =
		// getDocument((RssJdbcParams)ApplicationData.getProject().getProjectData(getConnectionDataID()).getData());

		// doc.getRootElement().removeContent();
	}

	private List<Record> createFieldList(LoadParams params)
			throws HttpException, IOException, JDOMException {
		Document doc = getDocument(params);
		List items = getItems(doc);
		KeyInsensitiveMap kim = new KeyInsensitiveMap();
		KeyInsensitiveMap currentKim = new KeyInsensitiveMap();

		for (Object objItem : items) {
			currentKim.clear();
			Element item = (Element) objItem;

			List attrs = item.getAttributes();
			if (attrs != null) {
				for (Object objAttr : attrs) {
					Attribute childAttr = (Attribute) objAttr;
					String attrName = childAttr.getName();
					RssElement elemAttr = new RssElement(attrName, true);
					elemAttr.setNamespace(childAttr.getNamespace());
					putFieldCount(currentKim, attrName, elemAttr);
				}
			}

			List childs = item.getChildren();
			for (Object objChild : childs) {
				Element child = (Element) objChild;
				String nameChild = child.getName();

				RssElement elemChild = new RssElement(nameChild);

				List childAttrs = child.getAttributes();
				if (childAttrs != null) {
					for (Object objChildAttr : childAttrs) {
						Attribute childAttr = (Attribute) objChildAttr;
						String attrName = childAttr.getName();
						String fullAttrName = RssElement.generateFieldName(
								nameChild, attrName);
						RssElement elemChildAttr = new RssElement(attrName,
								true, elemChild);
						elemChildAttr.setNamespace(childAttr.getNamespace());
						putFieldCount(currentKim, fullAttrName, elemChildAttr);
					}
				}
				List childChilds = child.getChildren();
				if (childChilds != null) {
					for (Object objChildChild : childChilds) {
						Element childChild = (Element) objChildChild;
						String childName = childChild.getName();
						String fullChildName = RssElement.generateFieldName(
								nameChild, childName);
						RssElement elemChildChild = new RssElement(childName,
								elemChild);
						elemChildChild.setNamespace(childChild.getNamespace());
						putFieldCount(currentKim, fullChildName, elemChildChild);
					}
				}
				String childText = child.getText();
				if (childText != null) {
					putFieldCount(currentKim, nameChild, elemChild);
				}
			}
			for (String key : currentKim.keySet()) {
				RssField currentField = (RssField) currentKim.get(key, false);
				if (kim.containsKey(key, true)) {
					RssField field = (RssField) kim.get(key, true);
					if (currentField.getCount() > field.getCount()) {
						kim.put(key, currentField);
					}
				} else {
					kim.put(key, currentField);
				}
			}
		}

		return generateFieldListFromKeyInsensitiveMap(kim, rssElementsByName);
	}

	@Override
	public List<Record> getFieldList(AbstractApatarActions actions)
			throws Exception {

		if (createNew) {
			return rssRecords;
		}

		Object obj = ApplicationData.getProject().getProjectData(
				getConnectionDataID()).getData();
		if (!(obj instanceof LoadParams)) {
			return null;
		}

		LoadParams params = (LoadParams) obj;

		return createFieldList(params);
	}

	public static List<Record> generateFieldListFromKeyInsensitiveMap(
			KeyInsensitiveMap kim, Map<String, RssElement> rssElem) {
		List<DBTypeRecord> recs = dbi.getAvailableTypes();
		List<Record> fields = new ArrayList<Record>();
		for (String key : kim.getMap().keySet()) {
			RssField field = (RssField) kim.get(key, false);
			int count = field.getCount();
			Record rec = null;
			if (count > 0) {
				for (int i = 0; i <= count; i++) {
					String name = key + SEPARATOR + (i + 1);
					rec = new Record(DBTypeRecord.getRecordByOriginalType(recs,
							"LONGVARCHAR"), name, 255, true, false, false);
					fields.add(rec);
					rssElem.put(name, field.getElement());
				}
			} else {
				rec = new Record(DBTypeRecord.getRecordByOriginalType(recs,
						"LONGVARCHAR"), key, 255, true, false, false);
				fields.add(rec);
				rssElem.put(key, field.getElement());
			}

		}
		return fields;
	}

	@Override
	public List<RDBTable> getTableList() throws Exception {

		List<RDBTable> tables = new ArrayList<RDBTable>();

		tables.add(getTableAccordingToVersion(this, createNew));

		return tables;

	}

	private static RDBTable getTableAccordingToVersion(RssNode node,
			boolean createNew) throws HttpException, IOException, JDOMException {
		LoadParams loadParams = null;
		CreateNewParams createParams = null;

		Object data = ApplicationData.getProject().getProjectData(
				node.getConnectionDataID()).getData();
		if (data == null) {
			return null;
		}

		if (createNew) {
			createParams = (CreateNewParams) data;
		} else {
			loadParams = (LoadParams) data;
		}

		if (createNew) {
			return new RDBTable(createParams.getVersion().equals(
					Version.ATOM_1_0) ? "entry" : "item", ETableMode.ReadWrite);
		} else {
			Document doc = getDocument(loadParams);
			if (doc == null) {
				return null;
			}
			Enum version = determinationVersion(doc.getRootElement());
			return new RDBTable(version.equals(Version.ATOM_1_0) ? "entry"
					: "item", ETableMode.ReadWrite);
		}
	}

	private List getElementsByTagName(String name, Object obj)
			throws JDOMException, IOException {
		XPath xpath = XPath.newInstance("//" + name);
		return xpath.selectNodes(obj);
	}

	private void insertData(ResultSet rs, Document doc, TableInfo iTI,
			boolean createNew) throws SQLException {

		Enum version = determinationVersion(doc.getRootElement());

		Element root = getParentForItem(doc, version);

		List<Record> records = iTI.getSchemaTable().getRecords();
		if (root == null) {
			return;
		}
		while (rs.next()) {
			Element item = new Element(getTableName());
			root.addContent(item);
			firstLevelElement.clear();
			insertElement(rs, item, doc, records, version, createNew);
		}
	}

	private void insertElement(ResultSet rs, Element parent, Document doc,
			List<Record> records, Enum version, boolean createNew)
			throws SQLException {
		for (Record rec : records) {
			String name = rec.getFieldName();

			RssElement rssElement = rssElementsByName.get(name);

			String value = rs.getString(name);
			if (value == null || value.equals("")) {
				continue;
			}

			if (rssElement.isAttribute() && rssElement.getParent() == null) {
				parent.setAttribute(name, value, rssElement.getNamespace());
				continue;
			}

			String[] chain = RssElement.getChain(name);
			Element childItem = getParentElement(chain, parent);
			if (rssElement.isAttribute()) {
				childItem.setAttribute(rssElement.getName(), value);
			} else {
				if (rssElement.parent != null) {
					Element child = new Element(rssElement.getName());
					child.setText(value);
					childItem.addContent(child);
				} else {
					childItem.setText(value);
				}
			}
		}
		if (version.equals(Version.RSS_1_0)) {
			Element root = doc.getRootElement();
			Namespace rootNs = root.getNamespace();
			String aboutValue = getAttributeValue(parent, "about");
			Element eSeq = getChildElement(getChildElement(getChildElement(
					root, "channel"), "items"), "Seq");
			Element eLi = new Element("li", rootNs);
			eLi.setAttribute("resource", aboutValue);
			eSeq.addContent(eLi);
			System.out.println(eSeq);
		}

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

		Element elChannel = new Element("channel");
		element.addContent(elChannel);
		if (channel != null) {
			elChannel.addContent(channel.saveToElement());
		}

		Element publishInfo = new Element("publishInfo");
		element.addContent(publishInfo);
		if (publishId != null) {
			publishInfo.setAttribute("id", publishId);
		}
		publishInfo.setAttribute("isPublish", "" + publish);

		if (item != null) {
			Element elItem = new Element("item");
			element.addContent(elItem);
			elItem.addContent(item.saveToElement());
		}

		element.setAttribute("createNew", "" + createNew);

		return element;
	}

	@Override
	public void initFromElement(Element node) {
		super.initFromElement(node);
		Element element = node.getChild("namespases");

		createNew = Boolean.parseBoolean(node.getAttributeValue("createNew"));

		for (Object obj : element.getChildren()) {
			Element childElem = (Element) obj;
			String prefix = childElem.getAttributeValue("prefix");
			Namespace ns = Namespace.getNamespace(prefix, childElem
					.getAttributeValue("uri"));
			namespaces.put(prefix, ns);
		}

		Element elChannel = node.getChild("channel");
		if (elChannel != null) {
			channel = new RssElement();
			Element elRssElement = elChannel.getChild("rssElement");
			if (elRssElement != null) {
				channel.initFromElement(elRssElement);
			} else {
				if (createNew) {
					ApplicationData.COUNT_INIT_ERROR++;
				}
			}
		} else {
			ApplicationData.COUNT_INIT_ERROR++;
		}

		Element elPublish = node.getChild("publishInfo");
		publishId = elPublish.getAttributeValue("id");
		publish = Boolean
				.parseBoolean(elPublish.getAttributeValue("isPublish"));

		Element elItem = node.getChild("item");
		if (elItem != null) {
			item = new RssElement();
			Element elRssElement = elItem.getChild("rssElement");
			if (elRssElement != null) {
				item.initFromElement(elRssElement);
			} else {
				if (createNew) {
					ApplicationData.COUNT_INIT_ERROR++;
				}
			}
		} else {
			if (createNew) {
				ApplicationData.COUNT_INIT_ERROR++;
			}
		}
		try {
			if (!createNew) {
				LoadParams params = (LoadParams) ApplicationData.getProject()
						.getProjectData(getConnectionDataID()).getData();
				createFieldList(params);
			} else {
				List<Record> records = getTiForConnection(OUT_CONN_POINT_NAME)
						.getRecords();
				RssElement elementItem = getItemForRssVersion();
				generateRssElementsByName(records, elementItem);
			}

			if (!createNew) {
				channel = null;
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	@Override
	public DataBaseInfo getDataBaseInfo() {
		return dbi;
	}

	/*
	 * get Document @xmlParams - LoadParams
	 */
	public static Document getDocument(LoadParams xmlParams)
			throws HttpException, IOException, JDOMException {

		Document doc = null;

		if (xmlParams.isReadFeedFromFile()) {
			File file = xmlParams.getSourceFile();
			doc = ReadWriteXMLData.loadDocument(file);
		} else {
			String url = xmlParams.getSourceUrl();
			if (url == null || url.equals("")) {
				return null;
			}
			doc = ReadWriteXMLData.loadDocument(new URL(url));
			if (doc != null) {
				return doc;
			}
			GetMethod method = new GetMethod(url);
			HttpClient client = new HttpClient();
			client.executeMethod(method);
			InputStream stream = method.getResponseBodyAsStream();

			doc = ReadWriteXMLData.loadDocument(stream);
		}
		return doc;
	}

	public static Enum determinationVersion(Element root) {
		String nameElement = root.getName();
		if (nameElement.equalsIgnoreCase("RDF")) {
			return Version.RSS_1_0;
		} else {
			if (nameElement.equalsIgnoreCase("rss")) {
				String version = getAttributeValue(root, "version");
				if (version.equals("2.0")) {
					return Version.RSS_2_0;
				}
			} else {
				if (nameElement.equalsIgnoreCase("feed")) {
					return Version.ATOM_1_0;
				}
			}
		}
		return null;

	}

	public static RssElement generateItemStructureRss_2_0() {
		RssElement item = new RssElement("item");

		RssElement element = new RssElement("title"/* , item */);
		item.addChild(element);
		element = new RssElement("link"/* , item */);
		item.addChild(element);
		element = new RssElement("description"/* , item */);
		item.addChild(element);
		element = new RssElement("author"/* , item */);
		item.addChild(element);
		RssElement category = new RssElement("category", null, true);
		item.addChild(category);
		element = new RssElement("domain", true, category, false);
		category.addChild(element);
		element = new RssElement("comments"/* , item */);
		item.addChild(element);
		RssElement enclosure = new RssElement("enclosure"/* , item */);
		item.addChild(enclosure);
		element = new RssElement("url", true, enclosure);
		enclosure.addChild(element);
		element = new RssElement("length", true, enclosure);
		enclosure.addChild(element);
		element = new RssElement("type", true, enclosure);
		enclosure.addChild(element);
		RssElement guid = new RssElement("guid"/* , item */);
		item.addChild(guid);
		element = new RssElement("isPermaLink", true, guid);
		guid.addChild(element);
		element = new RssElement("pubDate"/* , item */);
		item.addChild(element);
		RssElement source = new RssElement("source"/* , item */);
		item.addChild(source);
		element = new RssElement("url", true, source);
		source.addChild(element);
		return item;
	}

	public static RssElement generateItemStructureRss_1_0() {
		RssElement item = new RssElement("item");

		RssElement element = new RssElement("about", true /* , item */);
		item.addChild(element);

		element = new RssElement("title"/* , item */);
		item.addChild(element);
		element = new RssElement("title"/* , item */);
		item.addChild(element);
		element = new RssElement("link"/* , item */);
		item.addChild(element);
		element = new RssElement("description"/* , item */);
		item.addChild(element);
		return item;
	}

	public static RssElement generateEntryStructureAtom_1_0() {
		RssElement entry = new RssElement("entry");

		RssElement author = new RssElement("author", null, true);
		RssElement element = new RssElement("name", author);
		author.addChild(element);
		element = new RssElement("uri", author);
		author.addChild(element);
		element = new RssElement("email", author);
		author.addChild(element);
		entry.addChild(author);

		RssElement link = new RssElement("link", false, true);
		element = new RssElement("href", true, link);
		link.addChild(element);
		element = new RssElement("rel", true, link);
		link.addChild(element);
		element = new RssElement("type", true, link);
		link.addChild(element);
		element = new RssElement("hreflang", true, link);
		link.addChild(element);
		element = new RssElement("title", true, link);
		link.addChild(element);
		element = new RssElement("length", true, link);
		link.addChild(element);
		entry.addChild(link);

		RssElement contributor = new RssElement("contributor", null, true);
		element = new RssElement("name", author);
		contributor.addChild(element);
		element = new RssElement("uri", author);
		contributor.addChild(element);
		element = new RssElement("email", author);
		contributor.addChild(element);
		entry.addChild(contributor);

		RssElement category = new RssElement("category", null, true);
		element = new RssElement("term", true, link);
		category.addChild(element);
		element = new RssElement("scheme", true, link);
		category.addChild(element);
		element = new RssElement("label", true, link);
		category.addChild(element);
		entry.addChild(category);

		RssElement content = new RssElement("content", null, false);
		element = new RssElement("type", true, link);
		content.addChild(element);
		entry.addChild(content);

		RssElement title = new RssElement("title", null, false);
		entry.addChild(title);

		RssElement updated = new RssElement("updated", null, false);
		entry.addChild(updated);

		RssElement summary = new RssElement("summary", null, true);
		entry.addChild(summary);

		RssElement id = new RssElement("id", null, false);
		entry.addChild(id);

		RssElement published = new RssElement("published", false, true);
		entry.addChild(published);

		RssElement rights = new RssElement("rights", false, true);
		entry.addChild(rights);

		/*
		 * RssElement source = new RssElement("source", false, true);
		 * entry.addChild(source);
		 */

		return entry;
	}

	public static RssElement generateChannelStructureRss_1_0() {
		RssElement channel = new RssElement("channel");

		RssElement reAbout = new RssElement("about", true, channel);
		reAbout.setNamespace(RDF_NS);
		channel.addChild(reAbout);
		RssElement element = new RssElement("title", channel);
		channel.addChild(element);
		element = new RssElement("link", channel);
		element
				.setValue("http://www.apatarforge.org/files/new-custom-rss-feeds.html");
		channel.addChild(element);
		element = new RssElement("description", channel);
		channel.addChild(element);

		return channel;
	}

	public static RssElement generateFeedStructureAtom_1_0() {
		RssElement feed = new RssElement("feed");

		RssElement title = new RssElement("title", feed);
		RssElement element = new RssElement("type", true, title);
		title.addChild(element);
		feed.addChild(title);

		RssElement link = new RssElement("link", feed);
		element = new RssElement("href", true, link);
		link.addChild(element);
		element = new RssElement("rel", true, link);
		link.addChild(element);
		element = new RssElement("type", true, link);
		link.addChild(element);
		element = new RssElement("hreflang", true, link);
		link.addChild(element);
		element = new RssElement("title", true, link);
		link.addChild(element);
		element = new RssElement("length", true, link);
		link.addChild(element);
		feed.addChild(link);

		element = new RssElement("updated", feed);
		feed.addChild(element);

		element = new RssElement("id", feed);
		feed.addChild(element);

		RssElement author = new RssElement("author", feed);
		element = new RssElement("name", author);
		author.addChild(element);
		element = new RssElement("uri", author);
		author.addChild(element);
		element = new RssElement("email", author);
		author.addChild(element);
		feed.addChild(author);

		return feed;
	}

	public static RssElement generateChannelStructureRss_2_0() {
		RssElement channel = new RssElement("channel");

		RssElement element = new RssElement("title", channel);
		channel.addChild(element);
		element = new RssElement("link", channel);
		element
				.setValue("http://www.apatarforge.org/files/new-custom-rss-feeds.html");
		channel.addChild(element);
		element = new RssElement("description", channel);
		channel.addChild(element);

		return channel;
	}

	public static int putFieldCount(KeyInsensitiveMap kim, String name,
			RssElement element) {
		int index = 0;
		if (kim.containsKey(name, true)) {
			RssField field = (RssField) kim.get(name, true);
			index = field.incrementCount();
			kim.put(name, field);
		} else {
			kim.put(name, new RssField(index, element));
		}
		return index;
	}

	public static class RssField {
		int count = 0;
		RssElement element;

		public RssField(int count, RssElement element) {
			super();
			this.count = count;
			this.element = element;
		}

		public int getCount() {
			return count;
		}

		public RssElement getElement() {
			return element;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public int incrementCount() {
			return ++count;
		}
	}

	public List<Element> getItems(Document doc) throws JDOMException,
			IOException {
		Element root = doc.getRootElement();
		Enum version = determinationVersion(root);
		List list = null;
		List<Element> result = new ArrayList<Element>();
		if (version.equals(Version.RSS_1_0)) {
			list = root.getChildren();
		} else {
			if (version.equals(Version.RSS_2_0)) {
				return getElementsByTagName("item", doc);
			} else if (version.equals(Version.ATOM_1_0)) {
				list = root.getChildren();
			}
		}

		if (list == null) {
			return null;
		}

		String tagName = getTableName();

		for (Object obj : list) {
			Element elem = (Element) obj;
			if (elem.getName().equalsIgnoreCase(tagName)) {
				result.add(elem);
			}
		}
		return result;
	}

	public List getItems() throws JDOMException, IOException {
		LoadParams loadParams = null;
		CreateNewParams createParams = null;

		Document doc = null;

		if (createNew) {
			createParams = (CreateNewParams) ApplicationData.getProject()
					.getProjectData(getConnectionDataID()).getData();
			File file = createParams.getFile();
			if (file.exists()) {
				doc = ReadWriteXMLData.loadDocument(file);
			} else {
				return null;
			}
		} else {
			loadParams = (LoadParams) ApplicationData.getProject()
					.getProjectData(getConnectionDataID()).getData();
			doc = getDocument(loadParams);
		}

		return getItems(doc);
	}

	private Element getParentForItem(Document doc, Enum version) {
		Element root = doc.getRootElement();
		if (version.equals(Version.RSS_1_0)) {
			return root;
		} else {
			if (version.equals(Version.RSS_2_0)) {
				return getChildElement(root, "channel");
			} else if (version.equals(Version.ATOM_1_0)) {
				return root;
			}
		}
		return null;
	}

	private Element getParentElement(String[] chain, Element parent) {
		boolean several = true;
		try {
			Integer.parseInt(chain[chain.length - 1]);
		} catch (Exception e) {
			several = false;
		}

		String name = chain[0];

		if (several) {
			name += SEPARATOR + chain[chain.length - 1];
		}

		Element elem = (Element) firstLevelElement.get(name, true);
		if (elem == null) {
			elem = new Element(chain[0]);
			parent.addContent(elem);
			firstLevelElement.put(name, elem);
		}

		return elem;
	}

	public RssElement getChannel() {
		return channel;
	}

	public void setChannel(RssElement channel) {
		this.channel = channel;
	}

	public List<Record> getRssRecords() {
		return rssRecords;
	}

	public void setRssRecords(List<Record> rssRecords) {
		this.rssRecords = rssRecords;
	}

	public String getPublishId() {
		return publishId;
	}

	public void setPublishId(String publishId) {
		this.publishId = publishId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isPublish() {
		return publish;
	}

	public void setPublish(boolean publish) {
		this.publish = publish;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public RssElement getItem() {
		return item;
	}

	public void setItem(RssElement item) {
		this.item = item;
	}

	public Map<String, RssElement> getRssElementsByName() {
		return rssElementsByName;
	}

	public void setRssElementsByName(Map<String, RssElement> rssElementsByName) {
		this.rssElementsByName = rssElementsByName;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public void setCreateNew(boolean createNew) {
		this.createNew = createNew;
	}

	static private Element getChildElement(Element element, String childName) {
		Element result = element.getChild(childName);
		if (result != null) {
			return result;
		}
		List childs = element.getChildren();
		for (Object obj : childs) {
			Element child = (Element) obj;
			if (child.getName().equalsIgnoreCase(childName)) {
				return child;
			}
		}
		return null;
	}

	static private String getAttributeValue(Element element, String attrName) {
		String value = element.getAttributeValue(attrName);
		if (value != null) {
			return value;
		}
		List childs = element.getAttributes();
		for (Object obj : childs) {
			Attribute child = (Attribute) obj;
			if (child.getName().equalsIgnoreCase(attrName)) {
				return child.getValue();
			}
		}
		return null;
	}

	static private Element generateElement(RssElement elem) {
		Element res = new Element(elem.getName());
		generateElement(res, elem);
		return res;
	}

	static private void generateElement(Element elem, RssElement rssElem) {
		for (RssElement childElem : rssElem.getChildrens()) {
			if (childElem.isAttribute()) {
				elem.setAttribute(childElem.getName(), childElem.getValue());
			} else {
				Element addingelem = new Element(childElem.getName());
				addingelem.setText(childElem.getValue());
				elem.addContent(addingelem);
				generateElement(addingelem, childElem);
			}
		}
	}

	/*
	 * Pablish RSS Feed to Apatarforge.org
	 */
	private void publishing() throws HttpException, IOException,
			ApatarException {
		Object obj = ApplicationData.getProject().getProjectData(
				getConnectionDataID()).getData();
		if (!(obj instanceof CreateNewParams)) {
			return;
		}

		CreateNewParams params = (CreateNewParams) obj;

		PostMethod method = new PostMethod(PUBLISH_URL);

		String tempFolderName = "publish/";
		File tempFolder = new File(tempFolderName);
		if (!tempFolder.exists()) {
			tempFolder.mkdir();
		}
		String fileName = rssTitle == null ? "temp" : rssTitle.replaceAll(
				"[|/\\:*?\"<> ]", "_")
				+ ".aptr";
		ReadWriteXMLDataUi rwXMLdata = new ReadWriteXMLDataUi();
		File tempFile = rwXMLdata.writeXMLData(fileName.toString(),
				ApplicationData.getProject(), true);

		int partCount = 15;
		if (publishId != null) {
			partCount++;
		}
		Part[] parts = new Part[partCount];
		parts[0] = new StringPart("option", "com_remository");
		parts[1] = new StringPart("task", "");
		parts[2] = new StringPart("element", "component");
		parts[3] = new StringPart("client", "");
		parts[4] = new StringPart("oldid", "0");
		parts[5] = new FilePart("userfile", tempFile);
		parts[6] = new StringPart("containerid", "15");
		parts[7] = new StringPart("filetitle", rssTitle == null ? "" : rssTitle);
		parts[8] = new StringPart("description", description == null ? ""
				: description);
		parts[9] = new StringPart("smalldesc", description == null ? ""
				: description);
		parts[10] = new StringPart("filetags", "RSS");
		parts[11] = new StringPart("pubExternal", "true");
		parts[12] = new StringPart("username", username);
		parts[13] = new StringPart("password", CoreUtils.getMD5(password));
		parts[14] = new FilePart("rssfile", params.getFile());
		if (publishId != null) {
			parts[15] = new StringPart("dmid", publishId);
		}

		method.setRequestEntity(new MultipartRequestEntity(parts, method
				.getParams()));

		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(
				10000);
		int status = client.executeMethod(method);
		if (status != HttpStatus.SC_OK) {
			JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
					"Upload failed, response="
							+ HttpStatus.getStatusText(status));
		} else {
			if (publishId == null) {
				StringBuffer buff = new StringBuffer(method
						.getResponseBodyAsString());

				Matcher matcher = ApatarRegExp.getMatcher(
						"<meta name=\"dmid\" content=\"[a-zA-Z_0-9]+\"", buff
								.toString());

				while (matcher.find()) {
					String result = matcher.group();
					if (result == null || result.equals("")) {
						JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
								"Publishing error");
						return;
					}
					result = result.replaceFirst(
							"<meta name=\"dmid\" content=\"", "");
					result = result.replace("\"", "");
					publishId = result;
					return;
				}
			}
			JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
					"Publishing error");
		}
	}

	private void generateRssElementsByName(List<Record> records,
			RssElement element) {
		rssElementsByName.clear();
		for (Record rec : records) {
			RssElement requiredRssElement = RssElement.getRssElementByFullName(
					rec.getFieldName(), element);
			if (requiredRssElement != null) {
				rssElementsByName.put(rec.getFieldName(), requiredRssElement);
			}
		}
	}

	public RssElement getItemForRssVersion() {
		Object obj = ApplicationData.getProject().getProjectData(
				getConnectionDataID()).getData();
		if ((obj instanceof CreateNewParams)) {
			return null;
		}
		CreateNewParams params = (CreateNewParams) obj;
		RssElement element;
		if (params.getVersion().equals(Version.RSS_1_0)) {
			element = RssNode.generateItemStructureRss_1_0();
		} else {
			if (params.getVersion().equals(Version.RSS_2_0)) {
				element = RssNode.generateItemStructureRss_2_0();
			} else {
				return null;
			}
		}
		return element;
	}

	@Override
	public void beforeEdit() {
		if (connectionDataId < 0) {
			createProjectData();
		}
		IPersistent persistent = (ApplicationData.getProject()
				.getProjectData(connectionDataId));
		if (persistent != null) {
			bakupProjectData = persistent.saveToElement();
		} else {

		}
	}

	void createProjectData() {
		ProjectData pd = new ProjectData();
		pd.setName("default connection");
		connectionDataId = pd.getId();
		ApplicationData.getProject().addProjectData(pd);
	}

	public static void createRssFeed(JWorkPane workPane) {
		AbstractNode rssnode = CoreUtils.createNode(RssNode.class.getName());
		Project prj = ApplicationData.getProject();
		if (rssnode != null) {
			rssnode.setPosition(new Point(300, 200));
			prj.addNode(rssnode);
			((RssNode) rssnode).setCreateNew(true);
		} else {
			return;
		}
		AbstractNode trnode = CoreUtils.createNode(ProjectNode.class
				.getName());
		if (trnode != null) {
			trnode.setPosition(new Point(200, 200));
			prj.addNode(trnode);
		} else {
			return;
		}

		try {
			ConnectionPoint beginConnPoint = trnode
					.getConnPoint(ProjectNode.OUTPUT_CONN_POINT);
			ConnectionPoint endConnPoint = rssnode
					.getConnPoint(AbstractDataBaseNode.IN_CONN_POINT_NAME);

			if ((beginConnPoint != null) && (endConnPoint != null)) {
				beginConnPoint.incrementCountConnection();
				endConnPoint.incrementCountConnection();
				prj.connect(beginConnPoint, endConnPoint);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		UiUtils.updatePane(prj, workPane);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.AbstractDataBaseNode#validateConnectionData()
	 */
	@Override
	public boolean validateConnectionData() {
		lastErrorMessage = "";
		if (isCreateNew()) {
			CreateNewParams c = (CreateNewParams) ApplicationData.getProject()
					.getProjectData(connectionDataId).getData();
			if (c.getFile() == null || "".equals(c.getFile().getPath())) {
				lastErrorMessage = "Please select proper file";
			}
		} else {
			LoadParams c = (LoadParams) ApplicationData.getProject()
					.getProjectData(connectionDataId).getData();
			if (c.isReadFeedFromFile()
					&& (c.getSourceFile() == null
							|| !c.getSourceFile().isFile() || !c
							.getSourceFile().exists())) {
				lastErrorMessage = "Please select proper file";
			}
			if (!c.isReadFeedFromFile()
					&& (c.getSourceUrl() == null || "".equals(c.getSourceUrl()))) {
				lastErrorMessage = "URL should not be empty";
			}
		}
		if (!isLastErrorMessageEmpty()) {
			return false;
		}
		return true;
	}

}
