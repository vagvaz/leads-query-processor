/*TODO recorded refactoring
 *  all methods writeXMLDate renamed to writeXMLData
 ******************
 *  method:
 *  	public static File writeXMLData(String fileSrc, Project project, boolean autoReplace)
 *  now throws an exception:
 *  	public static File writeXMLData(String fileSrc, Project project, boolean autoReplace) throws ApatarException
 ******************
 *  method updatePane has moved to com.apatar.ui.UiUtils
 ******************
 *  all methods readXMLDate renamed to readXMLData
 ******************
 *  readXMLData method now returns file.getName() to caller. new method signature is:
 *  	public static String readXMLData(String fileSrc, Project project) throws SAXException, IOException
 *  this method now redirects IOException handling to caller code to handle it
 *  this method now returns file.getName() string to set Application title
 ******************
 *  method setDateAndTimeSettings renamed to loadDateAndTimeSettings
 ******************
 *	метод loadDateAndTimeSettings теперь не статический он вызывается экземпляром класса ReadWriteXMLDataUi
 ******************
 * метод public void writeXMLData(Project project, Writer writer) теперь не статический
 ******************
 * методы saveDocumentToFile теперь не статические. Удалён дублирующийся код из этих методов
 ******************
 * метод public static Document loadDocument(String xmlSrc) удалён как неиспользуемый
 ******************
 * метод public static boolean isValidDbXmlFile(String srcFile) удалён как неиспользуемый
 ******************
 * удалена обработка ошибок. отлавливание exception'ов должно происходить в вызывающем коде
 ******************
 ******************
 */
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

package com.apatar.core;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ReadWriteXMLData {

	private String encoding = null;

	/**
	 * Saves project into file specified in fileSrc parameter
	 * 
	 * @param fileSrc
	 *            - path to project file
	 * @param project
	 *            - project object
	 * @param autoReplace
	 *            - if <b>true</b> than replace if file fileSrc exists without
	 *            prompt. if <b>false</b> throws an exception
	 * @return file descriptor
	 * @throws IOException
	 */
	public File writeXMLData(String fileSrc, Project project,
			boolean autoReplace, AbstractReadWriteXMLDataActions actions)
			throws IOException {
		File newFile = null;

		String Src = fileSrc.toString();

		if (!Src.endsWith(".aptr") && !Src.endsWith(".APTR")) {
			Src += ".aptr";
		}

		newFile = new File(Src);
		boolean flag = true;

		if (!newFile.exists()) {
			newFile.createNewFile();
		} else {
			if (!autoReplace && (null != actions)) {
				flag = actions.doReplaceDatamapFile();
			} else {
				flag = true;
			}
		}

		if (flag) {
			FileWriter out = new FileWriter(Src);
			writeXMLData(project, out);

		}
		return newFile;
	}

	public void writeXMLData(Project project, Writer writer) throws IOException {
		Element root = new Element("document");

		root.setAttribute("version", ApplicationData.VERSION);

		root.addContent(ApplicationData.DATAMAP_DATE_SETTINGS.saveToElement());

		Map<Integer, AbstractNode> nodes = project.getNodes();
		for (Integer integer : nodes.keySet()) {
			AbstractNode node = nodes.get(integer);
			System.out.println("> " + node);
			root.addContent(node.saveToElement());
		}

		List<Connector> connectors = project.getConnectors();
		for (int j = 0; j < connectors.size(); j++) {
			Connector connector = connectors.get(j);
			root.addContent(connector.saveToElement());
		}

		// save project data
		project.saveProjectData(root);

		Document doc = new Document(root);
		saveDocumentToFile(doc, writer);
	}

	/**
	 * saving Xml document to file
	 * 
	 * @param doc
	 *            Document to save
	 * @param writer
	 *            Writer for save
	 * @throws IOException
	 */
	public void saveDocumentToFile(Document doc, Writer writer)
			throws IOException {
		Format format = Format.getPrettyFormat();
		if (null != encoding) {
			format.setEncoding(encoding);
		}
		XMLOutputter xo = new XMLOutputter(format);
		xo.output(doc, writer);
		writer.close();
	}

	/**
	 * sets the encodin than saving Xml document to file after saving complete -
	 * sets encoding to null
	 * 
	 * @param doc
	 *            Document to save
	 * @param writer
	 *            Writer for save
	 * @param enc
	 *            encoding
	 * @throws IOException
	 */
	public void saveDocumentToFile(Document doc, Writer writer, String enc)
			throws IOException {
		encoding = enc;
		try {
			saveDocumentToFile(doc, writer);
		} finally {
			encoding = null;
		}

	}

	/**
	 * Loads project from file
	 * 
	 * @param fileSrc
	 *            - path to project file
	 * @param project
	 *            - project instance
	 * @return fileName to put it to the application window title
	 * @throws SAXException
	 * @throws IOException
	 * @throws ApatarException
	 * @throws JDOMException
	 */
	public String readXMLData(String fileSrc, Project project)
			throws SAXException, IOException, JDOMException {

		File file = new File(fileSrc);
		FileReader fr = new FileReader(file);
		Element root = GetRootElementFromReader(fr);
		readXMLData(root, project);
		return file.getName();
	}

	private static Element GetRootElementFromReader(Reader reader)
			throws IOException, JDOMException {
		Document doc = null;
		Element root = null;

		// InputSource inSource = new InputSource(is);

		SAXBuilder builder = new SAXBuilder();
		// try {
		doc = builder.build(reader);
		// } catch (JDOMException e) {
		// cannot open the document;
		// throw new ApatarException(String.format(
		// "Cannot open the .aptr file. \n An %s error has occured.",
		// new Object[] { e.getMessage() }));
		// }
		root = doc.getRootElement();

		reader.close();
		return root;
	}

	/*
	 * TODO recorded refactoring в класс ReadWriteXMLData добавлен метод
	 * isDatamapOlderThan12 проверяющий версию датамапы. *********************
	 */
	/**
	 * Method checks if datamap is older than version 1.2.x.x if datamap is
	 * older than 1.2.x.x version - it have to be converted to new format.
	 * 
	 * @param fileSrc
	 * @return <b>true</b> if datamap is older, <b>false</b> if datamap has
	 *         1.2.x.x version or newer
	 * @throws IOException
	 * @throws JDOMException
	 * @throws NumberFormatException
	 * @throws ApatarException
	 */
	public boolean isDatamapOlderThan12(String fileSrc) throws IOException,
			JDOMException, NumberFormatException, ApatarException {
		File file = new File(fileSrc);
		FileReader fr = new FileReader(file);
		Element root = GetRootElementFromReader(fr);
		String version = root.getAttribute("version").getValue();
		List<String> versions;
		try {
			versions = ApatarRegExp.getSubstrings(
					"Apatar_v([\\d]+)\\.([\\d]+)\\..*", version);
		} catch (ApatarException e) {
			versions = ApatarRegExp.getSubstrings(
					"Apatar\\sv([\\d]+)\\.([\\d]+)\\..*", version);
		}
		Integer major_version = Integer.parseInt(versions.get(0));
		Integer minor_version = Integer.parseInt(versions.get(1));

		if (major_version > 1 || (major_version == 1 && minor_version >= 2)) {
			return false;
		} else {
			return true;
		}
	}

	public void loadDateAndTimeSettings(String fileSrc,
			AbstractReadWriteXMLDataActions actions) throws IOException,
			JDOMException {
		File file = new File(fileSrc);
		FileReader fr = new FileReader(file);
		Element root = GetRootElementFromReader(fr);
		Element elemDTS = root.getChild("DateAndTime");
		if (elemDTS != null) {
			DateAndTimeSettings dts = new DateAndTimeSettings();
			dts.initFromElement(elemDTS);

			if (!dts.getPattern().equals(
					ApplicationData.DATAMAP_DATE_SETTINGS.getPattern())) {
				if (null != actions) {
					if (actions.doReplaceDateTimeSettings()) {
						ApplicationData.DATAMAP_DATE_SETTINGS.init(dts);
					}
				}
			}
		}
	}

	public static void readXMLData(Reader data, Project project)
			throws SAXException, IOException, JDOMException {
		Element root = GetRootElementFromReader(data);
		readXMLData(root, project);
	}

	public static void readXMLData(Element root, Project project)
			throws SAXException {

		// now clear the project and load the new one
		project.removeAllElements();

		// TODO - it is required to calculate this values from the nodes
		// downloaded
		int maxIdNode = 0;
		int maxIdConn = 0;
		for (Object element : project.getNodes().keySet()) {
			maxIdNode = Math.max(maxIdNode, Integer
					.parseInt(element.toString()));
		}

		for (Object element : project.getConnectors()) {
			maxIdConn = Math
					.max(maxIdConn, (int) ((Connector) element).getId());
		}

		project.setLastIdNode(maxIdNode);
		project.setLastIdConnector(maxIdConn);

		// save project data
		project.initProjectData(root);

		List<?> nodesList = root.getChildren();
		List<Element> arrowList = new ArrayList<Element>();

		Element nodeElement;
		String nodeName;

		for (int i = 0; i < nodesList.size(); i++) {
			nodeElement = (Element) nodesList.get(i);
			nodeName = nodeElement.getName();

			if (nodeName.equals("arrow")) {
				arrowList.add(nodeElement);
			} else {
				if (nodeName.equals("node")) {
					AbstractNode newNode = CreateNode(nodeElement);
					// if plugin is not available
					if (newNode == null) {
						continue;
					}

					project.addNode(newNode);
				}

			}
		}

		int start_id, end_id;
		Element arrowNode;
		String beginConnName, endConnName;
		ConnectionPoint beginConnPoint, endConnPoint;
		AbstractNode startNode, finishNode;

		for (int i = 0; i < arrowList.size(); i++) {
			arrowNode = arrowList.get(i);
			start_id = Integer
					.parseInt(arrowNode.getAttributeValue("begin_id"));
			end_id = Integer.parseInt(arrowNode.getAttributeValue("end_id"));
			beginConnName = arrowNode.getAttributeValue("begin_conn_name");
			endConnName = arrowNode.getAttributeValue("end_conn_name");
			beginConnPoint = null;
			endConnPoint = null;
			startNode = null;
			finishNode = null;

			startNode = project.getNode(start_id);
			finishNode = project.getNode(end_id);
			if (startNode == null || finishNode == null) {
				continue;
			}

			// try {
			beginConnPoint = startNode.getConnPoint(beginConnName);
			endConnPoint = finishNode.getConnPoint(endConnName);
			// } catch (Exception e) {
			// e.printStackTrace();
			// }

			if ((beginConnPoint != null) && (endConnPoint != null)) {
				beginConnPoint.incrementCountConnection();
				endConnPoint.incrementCountConnection();
				project.connect(beginConnPoint, endConnPoint);
			}
		}

	}

	private static AbstractNode CreateNode(Element element) {
		String nodeClassParameter = element.getAttributeValue("nodeClass");
		System.out.println("nodeClassParameter = `" + nodeClassParameter + "`");

		if ("com.apatar.msexel.MsExelNode".equalsIgnoreCase(nodeClassParameter)) {
			nodeClassParameter = "com.apatar.msexcel.MsExcelNode";
		}
		if ("com.apatar.msexel.MsExcelColumnNode"
				.equalsIgnoreCase(nodeClassParameter)) {
			nodeClassParameter = "com.apatar.msexcel.MsExcelColumnNode";
		}
		AbstractNode newNode = CoreUtils.createNode(nodeClassParameter);
		try {

			if (element != null) {
				newNode.initFromElement(element);
			} else {
				ApplicationData.COUNT_INIT_ERROR++;
			}
		} catch (Exception e) {
			System.err.println("Failed creating node `" + nodeClassParameter
					+ "`");
		}
		return newNode;

	}

	public static Element getRootElement(String fileSrc) throws JDOMException,
			IOException {
		InputStream is = new FileInputStream(fileSrc);
		return getRootElement(is);
	}

	public static Element getRootElement(File file) throws JDOMException,
			IOException {
		InputStream is = new FileInputStream(file);
		return getRootElement(is);
	}

	public static Element getRootElement(InputStream is) throws JDOMException,
			IOException {
		Document doc = null;
		Element root = null;
		InputSource inSource = new InputSource(is);

		SAXBuilder builder = new SAXBuilder();
		doc = builder.build(inSource);
		root = doc.getRootElement();
		is.close();
		return root;
	}

	public static Element getRootElement(Document doc) throws JDOMException,
			IOException {
		Element root = null;
		root = doc.getRootElement();
		return root;
	}

	public static Document loadDocument(File file) throws JDOMException,
			IOException {
		SAXBuilder builder = new SAXBuilder();
		return builder.build(file);
	}

	public static Document loadDocument(InputStream is) throws JDOMException,
			IOException {
		SAXBuilder builder = new SAXBuilder();
		return builder.build(is);
	}

	/*
	 * Load document from URL @url - URL
	 */
	public static Document loadDocument(URL url) throws JDOMException,
			IOException {
		if (ApplicationData.httpClient.isUseProxy()
				&& (ApplicationData.httpClient.getUserName() != null)) {
			DataInputStream di = null;

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			String encodedUserPwd = Base64
					.encodeBytes((ApplicationData.httpClient.getUserName()
							+ ":" + ApplicationData.httpClient.getPassword())
							.getBytes());
			con.setRequestProperty("Proxy-Authorization", "Basic "
					+ encodedUserPwd);
			di = new DataInputStream(con.getInputStream());
			return loadDocument(di);
		}
		SAXBuilder builder = new SAXBuilder();
		return builder.build(url);
	}

}
