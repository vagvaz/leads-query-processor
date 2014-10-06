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


package com.apatar.dbf;

import java.io.File;
import java.util.Properties;

import org.jdom.Element;

import com.apatar.core.ApplicationData;
import com.apatar.core.FolderPath;
import com.apatar.core.JdbcParams;
import com.apatar.core.PersistentUtils;

//public class MsAccessConnection implements IPersistent {
public class DbfParams extends JdbcParams{

	private FolderPath folder = null;
	
	public DbfParams(){
		this.folder	= new FolderPath("./");
		
		init();
	}
	
	public DbfParams(File file) {
		this.folder	= new FolderPath(file.getPath());
		
		init();
	}
	
	protected void init(){
		super.setJdbcDriver("sun.jdbc.odbc.JdbcOdbcDriver");
	}
	
	public FolderPath getFolder() {
		return folder;
	}

	public void setFolder(FolderPath folder) {
		this.folder = folder;
	}

	public Element saveToElement() {
		Element e = PersistentUtils.CreateElement(this);
		
		e.setAttribute("userName",	this.userName);
		
		e.addContent( getPassword().saveToElement() );
		
		return e;
	}

	public void initFromElement(Element e) {
		if (e == null) {
			ApplicationData.COUNT_INIT_ERROR++;
			return;
		}
		this.userName	= e.getAttributeValue("userName");
		if (this.userName == null)
			ApplicationData.COUNT_INIT_ERROR++;
	//	this.file	= new File( e.getAttributeValue("filePath") );
		
		PersistentUtils.InitObjectFromChild(password, e);
	}

	public String getConnUrl() {
		return "jdbc:odbc:Driver={Microsoft dBase Driver (*.dbf)};DBQ=" + ((folder == null) ? "" : folder.getPath());
	}
	
	public Properties getProperties() {
		return null;
	}

	public void setProperties(Properties property) {}
	
}

