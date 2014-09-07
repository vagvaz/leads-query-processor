/*
_______________________

Apatar Open Source Data Integration

Copyright (C) 2005-2007, Apatar, Inc.

info@apatar.com

195 Meadow St., 2nd Floor

Chicopee, MA 01013

 

    This program is free software; you can redistribute it and/or modify

    it under the terms of the GNU General Public License as published by

    the Free Software Foundation; either version 2 of the License, or

    (at your option) any later version.

 

    This program is distributed in the hope that it will be useful,

    but WITHOUT ANY WARRANTY; without even the implied warranty of

    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the

    GNU General Public License for more details.

 

    You should have received a copy of the GNU General Public License along

    with this program; if not, write to the Free Software Foundation, Inc.,

    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

________________________

 */

package com.apatar.rss;

import java.io.File;

import org.jdom.Element;

import com.apatar.core.ApplicationData;
import com.apatar.core.IPersistent;
import com.apatar.core.PersistentUtils;

public class LoadParams implements IPersistent {
	File sourceFile;
	String sourceUrl;
	boolean readFeedFromFile = false;

	public LoadParams() {
		super();
	}

	public boolean isReadFeedFromFile() {
		return readFeedFromFile;
	}

	public void setReadFeedFromFile(boolean readFeedFromFile) {
		this.readFeedFromFile = readFeedFromFile;
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public void initFromElement(Element e) {
		String attribute = e.getChildText("readFeedFromFile");
		if (attribute != null) {
			readFeedFromFile = Boolean.parseBoolean(attribute);
		} else {
			ApplicationData.COUNT_INIT_ERROR++;
		}
		String filePath = e.getChildText("filePath");
		if (filePath != null) {
			sourceFile = new File(filePath);
		} else if (readFeedFromFile) {
			ApplicationData.COUNT_INIT_ERROR++;
		}
		sourceUrl = e.getChildText("url");
	}

	public Element saveToElement() {
		Element e = PersistentUtils.CreateElement(this);
		Element elReadFromFile = new Element("readFeedFromFile");
		elReadFromFile.setText("" + readFeedFromFile);
		e.addContent(elReadFromFile);
		if (sourceFile != null) {
			Element path = new Element("filePath");
			path.setText(sourceFile.getPath());
			e.addContent(path);
		}
		Element elUrl = new Element("url");
		elUrl.setText(sourceUrl);
		e.addContent(elUrl);

		return e;
	}
}
