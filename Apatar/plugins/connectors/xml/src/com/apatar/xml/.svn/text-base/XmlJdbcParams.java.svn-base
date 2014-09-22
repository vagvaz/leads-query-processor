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
import java.util.Properties;

import org.jdom.Element;

import com.apatar.core.ApplicationData;
import com.apatar.core.IPersistent;
import com.apatar.core.PersistentUtils;

public class XmlJdbcParams implements IPersistent {

	File file;
	String url;
	boolean readFromFile = true;

	public XmlJdbcParams() {
		super();
	}

	public String getConnUrl() {
		if (!file.getPath().toLowerCase().endsWith(".xml")) {
			return null;
		}
		return "jdbc:webdocwf:xml:" + file.getPath().replace(".xml", "");
	}

	public void FillProperties(Properties prp) {
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void initFromElement(Element e) {
		String attr = e.getChildText("readFromFile");
		if (attr != null) {
			readFromFile = Boolean.parseBoolean(attr);
		} else {
			ApplicationData.COUNT_INIT_ERROR++;
		}
		String filePath = e.getChildText("filePath");
		if (readFromFile == true) {
			if (filePath != null && !filePath.equals("")) {
				file = new File(filePath);
			} else {
				ApplicationData.COUNT_INIT_ERROR++;
			}
		}
		url = e.getChildText("url");
		if (readFromFile == false) {
			if (url == null || url.equals("")) {
				ApplicationData.COUNT_INIT_ERROR++;
			}
		}

	}

	public boolean isReadFromFile() {
		return readFromFile;
	}

	public void setReadFromFile(boolean readFromFile) {
		this.readFromFile = readFromFile;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Element saveToElement() {
		Element e = PersistentUtils.CreateElement(this);
		Element elReadFromFile = new Element("readFromFile");
		elReadFromFile.setText("" + readFromFile);
		e.addContent(elReadFromFile);
		if (file != null) {
			Element path = new Element("filePath");
			path.setText(file.getPath());
			e.addContent(path);
		}
		Element elUrl = new Element("url");
		elUrl.setText(url);
		e.addContent(elUrl);

		return e;
	}

}
