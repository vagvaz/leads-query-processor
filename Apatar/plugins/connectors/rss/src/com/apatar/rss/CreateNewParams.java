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

import com.apatar.core.IPersistent;
import com.apatar.core.PersistentUtils;

public class CreateNewParams implements IPersistent{
	Enum version = Version.RSS_2_0;
	File file;
	
	public CreateNewParams() {
		super();
	}
	public Enum getVersion() {
		return version;
	}
	public void setVersion(Enum version) {
		this.version = version;
	}

	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	
	public void initFromElement(Element e) {
		String filePath = e.getChildText("filePath");
		if (filePath != null)
			file = new File(filePath);
		
		version = Enum.valueOf(Version.class, e.getChildText("version"));
	}
	public Element saveToElement() {
		Element e = PersistentUtils.CreateElement(this);
		if (file != null) {
			Element path = new Element("filePath");
			path.setText(file.getPath());
			e.addContent(path);
		}
		
		
		Element elVersion = new Element("version");
		elVersion.setText(version.toString());
		e.addContent(elVersion);
		
		return e;
	}
}

