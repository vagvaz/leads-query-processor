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

package com.apatar.sugarcrm;

import org.jdom.Element;

import com.apatar.core.ApplicationData;
import com.apatar.core.IPersistent;
import com.apatar.core.PasswordString;
import com.apatar.core.PersistentUtils;

public class SugarcrmConnection implements IPersistent {

	private String url		= "";
	private String userName	= "";
	private PasswordString password	= new PasswordString();
	
	public SugarcrmConnection() {
		if (ApplicationData.DebugAutoFill) {
			this.url		= "http://appserv3/sugarcrm/soap.php";
			this.userName	= "admin";
			this.password.setValue("63a9f0ea7bb98050796b649e85481845");
		}
	}
	
	public SugarcrmConnection(String url, String login, String pass){
		this.url		= url;
		this.userName	= login;
		this.password.setValue(pass);
	}

	public Element saveToElement() {
		Element e = PersistentUtils.CreateElement(this);
		
		e.setAttribute("url",		this.url);
		e.setAttribute("userName",	this.userName);
		
		e.addContent( getPassword().saveToElement() );
		
		return e;
	}

	public void initFromElement(Element e) {
		if (e == null) {
			ApplicationData.COUNT_INIT_ERROR++;
			return;
		}
			
		this.url		= e.getAttributeValue("url");
		this.userName	= e.getAttributeValue("userName");
		
		if (this.url == null || this.userName == null)
			ApplicationData.COUNT_INIT_ERROR++;

		PersistentUtils.InitObjectFromChild(password, e);
	}
	
	public String getUrl(){
		return this.url;
	}
	
	public void setUrl(String url){
		this.url = url;
	}
	
	public String getUserName(){
		return this.userName;
	}
	
	public void setUserName(String login){
		this.userName = login;
	}
	
	public PasswordString getPassword(){
		return this.password;
	}
	
	public void setPassword(PasswordString password){
		this.password = password;
	}
}
