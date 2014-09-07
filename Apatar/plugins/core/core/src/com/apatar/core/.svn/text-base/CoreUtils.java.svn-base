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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.apatar.ui.NodeFactory;

public class CoreUtils {

	public static Collection<AbstractNode> getChainNodes(AbstractNode node) {
		Collection<AbstractNode> res = new ArrayList<AbstractNode>();
		getChainNodes(res, node);
		return res;
	}

	public static AbstractNode getFirstNodeInChain(AbstractNode lastNodeInChain) {
		Collection<AbstractNode> nodes = new ArrayList<AbstractNode>();
		getChainNodes(nodes, lastNodeInChain);
		return (AbstractNode) nodes.toArray()[nodes.size() - 1];
	}

	public static void getChainNodes(Collection<AbstractNode> nodes,
			AbstractNode node) {
		nodes.add(node);
		Collection<ConnectionPoint> connPoints = node.getIncomingConnPoints();
		for (ConnectionPoint cp : connPoints) {
			List<Connector> connectors = cp.getConnectors();
			for (Connector connector : connectors) {
				AbstractNode beginNode = connector.getBegin().getNode();
				getChainNodes(nodes, beginNode);
			}
		}
	}

	public static ColumnNode findNodeInChainByConnectionName(AbstractNode node,
			String connectionName, AbstractNode startNode) {
		ColumnNode result = null;
		List<AbstractNode> list = null;
		for (ConnectionPoint cp : node.getConnPoints()) {
			if (cp.isInbound()) {
				list = AbstractNode.getPrevNodes(cp);
			} else {
				list = AbstractNode.getNextNodes(cp);
			}
			if (list.size() > 0) {
				AbstractNode abstractNode = list.get(0);
				if (abstractNode == startNode) {
					continue;
				}
				if (abstractNode instanceof ColumnNode) {
					ColumnNode colNode = (ColumnNode) abstractNode;
					if (colNode.getConnectionName().equals(connectionName)) {
						return colNode;
					}
				}
				return findNodeInChainByConnectionName(abstractNode,
						connectionName, startNode);
			}
		}
		return result;
	}

	public static AbstractNode[] Sort(Collection<AbstractNode> nodes) {
		AbstractNode[] ar = nodes.toArray(new AbstractNode[nodes.size()]);
		Arrays.sort(ar, new AbstractNode.OrderComparator());
		return ar;
	}

	public static void definitionExecutionOrder(Collection<AbstractNode> nodes) {
		int currentOrder = 0;
		for (AbstractNode node : nodes) {
			if (node.getInlinePosition() == AbstractNode.FIRST_POSITION) {
				currentOrder = definitionExecutionOrder(node, currentOrder) + 1;
			}
		}
	}

	private static int definitionExecutionOrder(AbstractNode node,
			int currentOrder) {
		if (node instanceof AbstractNode) {
			(node).setExecutionOrder(currentOrder++);
		}
		Collection<ConnectionPoint> connPoints = node.getOutputConnPoints();
		for (ConnectionPoint cp : connPoints) {
			List<Connector> connectors = cp.getConnectors();
			for (Connector connector : connectors) {
				AbstractNode endNode = connector.getEnd().getNode();
				currentOrder = definitionExecutionOrder(endNode, currentOrder) + 1;
			}
		}
		return currentOrder;
	}

	public static String getMD5(String value) {
		// generate hash password
		String md5password = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			StringBuffer sb = new StringBuffer();
			byte buf[] = value.getBytes();
			byte[] md5 = md.digest(buf);

			for (byte element : md5) {
				String tmpStr = "0" + Integer.toHexString((0xff & element));
				sb.append(tmpStr.substring(tmpStr.length() - 2));
			}

			md5password = sb.toString();
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}

		return md5password;
	}

	public static AbstractNode createNode(String nodeClassParameter) {
		Collection<NodeFactory> nodeFactory = ApplicationData
				.getNodeFactoryCollection();

		for (NodeFactory nf : nodeFactory) {
			String nodeClassName = nf.getNodeClass();

			if (nodeClassName.equals(nodeClassParameter)) {
				AbstractNode newNode = nf.createNode();
				return newNode;
			}
		}
		return null;
	}

	public static boolean validEmail(String email) throws ApatarException {
		String regexp = "^[a-zA-Z0-9_\\+-]+(\\.[a-zA-Z0-9_\\+-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.([a-zA-Z]{2,4})$";
		return ApatarRegExp.matchRegExp(regexp, email);
	}

	public static boolean validUrl(String url) throws ApatarException {
		String regexp = "[\\:a-zA-Z\\d\\u005F/\\-]+\\u002E[a-zA-Z\\d\\u005F\\u002E\\-\\&\\?\\=/]+";
		return ApatarRegExp.matchRegExp(regexp, url);
	}

	public static final int DEBUG_INFO = 1;
	public static final int ERROR_INFO = 2;
	public static final int ALL_INFO = 3;

	public static void printInfoToConsol(int info) {
		if (info == DEBUG_INFO || info == ALL_INFO) {
			System.out.println("*********************************");
			System.out.println("Start date " + new Date().toString());
			System.out.println("Version " + ApplicationData.VERSION);
			System.out.println("JRE Version "
					+ System.getProperty("java.version"));
			System.out.println("JRE Vendor "
					+ System.getProperty("java.vendor"));
			System.out.println("log4j.configuration = `"
					+ System.getProperty("log4j.configuration") + "`");
			System.out.println("user.dir= `" + System.getProperty("user.dir")
					+ "`");
		}
		if (info == ERROR_INFO || info == ALL_INFO) {
			System.err.println("*********************************");
			System.err.println("Start date " + new Date().toString());
			System.err.println("Version " + ApplicationData.VERSION);
			System.err.println("JRE Version "
					+ System.getProperty("java.version"));
			System.err.println("JRE Vendor "
					+ System.getProperty("java.vendor"));
			System.err.println("log4j.configuration = `"
					+ System.getProperty("log4j.configuration") + "`");

		}
	}

	/*
	 * TODO recorded refactoring в класс CoreUtils добавлен метод чтения файла
	 * как строки. *********************
	 */

	/**
	 * returns file content as a String
	 * 
	 * @param file
	 *            - java.io.File object
	 * @param encoding
	 *            - file encoding. can be set to null
	 * @return file content as a String
	 * @throws IOException
	 */
	public static String loadFileAsString(File file, String encoding)
			throws IOException {
		InputStreamReader f = encoding == null ? new FileReader(file)
				: new InputStreamReader(new FileInputStream(file), encoding);
		StringBuffer sb = new StringBuffer();
		try {
			char[] buf = new char[32768];
			int len;
			while ((len = f.read(buf, 0, buf.length)) >= 0) {
				sb.append(buf, 0, len);
			}
			return sb.toString();
		} finally {
			try {
				f.close();
			} catch (IOException e) {
				throw e;
			}
		}
	}

	/*
	 * TODO recorded refactoring в класс CoreUtils добавлен метод записи файла
	 * из строковой переменной. *********************
	 */
	/**
	 * writes string to a file
	 * 
	 * @param file
	 *            java.io.File object to write string to
	 * @param encoding
	 *            file encoding. can be set to null
	 * @param content
	 *            file content as a String
	 * @throws IOException
	 */
	public static void saveFileFromString(File file, String encoding,
			String content) throws IOException {
		if (content == null) {
			return;
		}
		char[] buf = new char[content.length()];
		content.getChars(0, buf.length, buf, 0);
		OutputStreamWriter f = encoding == null ? new FileWriter(file)
				: new OutputStreamWriter(new FileOutputStream(file), encoding);
		try {
			f.write(buf);
		} finally {
			f.close();
		}
	}

}
