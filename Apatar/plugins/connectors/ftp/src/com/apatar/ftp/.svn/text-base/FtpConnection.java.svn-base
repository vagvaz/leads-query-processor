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

package com.apatar.ftp;

import org.jdom.Element;

import com.apatar.core.ApplicationData;
import com.apatar.core.IPersistent;
import com.apatar.core.PasswordString;
import com.apatar.core.PersistentUtils;

public class FtpConnection implements IPersistent {

	private String url		= "";
	private int port		= 21;
	private String login	= "";
	private PasswordString password	= new PasswordString();
	private boolean passive		= true;
	
	public FtpConnection(){
		this("", 21, "", new PasswordString(), true);
	}
	
	public FtpConnection(String url, int port, String login, 
			PasswordString password, boolean passive){
		this.url		= url;
		this.port		= port;
		this.login		= login;
		this.password	= password;
		this.passive	= passive;
	}
	
	public String getUrl(){
		return this.url;
	}
	
	public void setUrl(String url){
		this.url = url;
	}
	
	public int getPort(){
		return this.port;
	}
	
	public void setPort(int port){
		this.port = port;
	}
	
	public String getLogin(){
		return this.login;
	}
	
	public void setLogin(String login){
		this.login = login;
	}
	
	public PasswordString getPassword(){
		return this.password;
	}
	
	public void setPassword(PasswordString password){
		this.password = password;
	}
	
	public boolean getPassive(){
		return this.passive;
	}
	
	public void setPassive(boolean flag){
		this.passive = flag;
	}
		
	public Element saveToElement() {
		Element e = PersistentUtils.CreateElement(this);
		
		e.setAttribute("url",		this.url);
		e.setAttribute("port",		String.valueOf(this.port) );
		e.setAttribute("login",		this.login);
		e.setAttribute("passive",	String.valueOf(this.passive) );
		
		e.addContent( getPassword().saveToElement() );
		
		return e;
	}

	public void initFromElement(Element e) {
		this.url		= e.getAttributeValue("url");
		if (this.url == null)
			ApplicationData.COUNT_INIT_ERROR++;
		String attribute = e.getAttributeValue("port");
		if (attribute != null)
			this.port		= Integer.parseInt( attribute );
		else
			ApplicationData.COUNT_INIT_ERROR++;
		this.login		= e.getAttributeValue("login");
		attribute = e.getAttributeValue("passive");
		if (attribute != null)
			this.passive	= "true".equals( attribute );
		else
			ApplicationData.COUNT_INIT_ERROR++;
		
		PersistentUtils.InitObjectFromChild(password, e);
	}
}