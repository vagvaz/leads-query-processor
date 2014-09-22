/*
_______________________
Apatar Open Source Data Integration
Copyright (C) 2005-2007, Apatar, Inc.
info@apatar.com
195 Meadow St., 2nd Floor
Chicopee, MA 01013

пїЅпїЅпїЅ This program is free software; you can redistribute it and/or modify
пїЅпїЅпїЅ it under the terms of the GNU General Public License as published by
пїЅпїЅпїЅ the Free Software Foundation; either version 2 of the License, or
пїЅпїЅпїЅ (at your option) any later version.

пїЅпїЅпїЅ This program is distributed in the hope that it will be useful,
пїЅпїЅпїЅ but WITHOUT ANY WARRANTY; without even the implied warranty of
пїЅпїЅпїЅ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.пїЅ See the
пїЅпїЅпїЅ GNU General Public License for more details.

пїЅпїЅпїЅ You should have received a copy of the GNU General Public License along
пїЅпїЅпїЅ with this program; if not, write to the Free Software Foundation, Inc.,
пїЅпїЅпїЅ 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
________________________

 */

package com.apatar.textfile;

import java.util.Properties;

import org.jdom.Element;

import com.apatar.core.ApplicationData;
import com.apatar.core.JdbcParams;
import com.apatar.core.PasswordString;

public class FileConnectionInfo extends JdbcParams {

	private String pathToFile = "";
	private String typeOfFile = "";
	private String separator = "";

	FileConnectionInfo(TextFileNode node) {
		setDriverName("Csv");
		setUserName("");
		setPassword(new PasswordString());
		setHost("localhost");
		setPort(0);
		setDbName("");
		setJdbcDriver("org.relique.jdbc.csv.CsvDriver");
	}

	public String getPathToFile() {
		return pathToFile;
	}

	public String getTypeOfFile() {
		return typeOfFile;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String value) {
		separator = value;
	}

	public void setPathToFile(String value) {
		pathToFile = value;
	}

	public void setTypeOfFile(String value) {
		typeOfFile = value;
	}

	@Override
	public String getConnUrl() {
		return "jdbc:relique:csv:" + pathToFile;
	}

	@Override
	public Properties getProperties() {
		Properties prop = new Properties();

		prop.setProperty("fileExtension", typeOfFile);
		prop.setProperty("separator", separator);

		return prop;
	}

	// save node
	@Override
	public Element saveToElement() {
		Element readNode = super.saveToElement();

		Element pathToFileElement = new Element("pathToFile");
		pathToFileElement.setText(pathToFile);
		readNode.addContent(pathToFileElement);

		/*
		 * Element typeOfFileElement = new Element("typeOfFile");
		 * typeOfFileElement.setText( typeOfFile );
		 * readNode.addContent(typeOfFileElement);
		 * 
		 * Element separatorElement = new Element("separator");
		 * separatorElement.setText( separator );
		 * readNode.addContent(separatorElement);
		 */
		return readNode;
	}

	@Override
	public void initFromElement(Element node) {
		super.initFromElement(node);

		pathToFile = node.getChildText("pathToFile");
		if (pathToFile == null) {
			ApplicationData.COUNT_INIT_ERROR++;
			/*
			 * typeOfFile = node.getChildText("typeOfFile"); separator =
			 * node.getChildText("separator");
			 */
		}
	}

	@Override
	public void setProperties(Properties property) {
		setTypeOfFile(property.getProperty("fileExtension"));
		setSeparator(property.getProperty("separator"));
	}

	@Override
	protected void init() {

	}

}
