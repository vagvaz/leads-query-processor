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

package com.apatar.filesystem;

import org.jdom.Element;

import com.apatar.core.ApplicationData;
import com.apatar.core.FolderPath;
import com.apatar.core.IPersistent;
import com.apatar.core.PersistentUtils;

public class FileParams implements IPersistent {
	
	FolderPath directory = new FolderPath();
	
	public void initFromElement(Element e) {
		String text = e.getChildText("directory");
		if (text != null)
			directory = new FolderPath(text);
		else
			ApplicationData.COUNT_INIT_ERROR++;
	}

	public Element saveToElement() {
		Element e = PersistentUtils.CreateElement(this);
		Element dir = new Element("directory");
		dir.setText(directory.getPath());
		e.addContent(dir);
		return e;
	}

	public FolderPath getDirectory() {
		return directory;
	}

	public void setDirectory(FolderPath directory) {
		this.directory = directory;
	}

}
