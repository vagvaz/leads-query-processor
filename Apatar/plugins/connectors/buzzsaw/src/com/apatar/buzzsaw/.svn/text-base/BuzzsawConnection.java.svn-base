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

package com.apatar.buzzsaw;

import org.jdom.Element;

import com.apatar.core.ApplicationData;
import com.apatar.core.IPersistent;
import com.apatar.core.PasswordString;
import com.apatar.core.PersistentUtils;

public class BuzzsawConnection implements IPersistent {
	
	private String login	= "";
	private PasswordString password	= new PasswordString();
	private String url		= "";
	
	public BuzzsawConnection(){
		if ( ApplicationData.DEBUG ) {
			this.url		= "https://webdav.buzzsaw.com/apatar_buzzsaw2";
			this.login		= "smartmaxx1";
			this.password	= new PasswordString("Ilovebuzzsaw7");
		}
	}
	
	public BuzzsawConnection(String url, String login, 
			PasswordString password, String uri){
		this.url		= url;
		this.login		= login;
		this.password	= password;
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
	
	public String getUrl(){
		return this.url;
	}
	
	public void setUrl(String url){
		this.url = url;
	}
	
	public Element saveToElement() {
		Element e = PersistentUtils.CreateElement(this);
		
		e.setAttribute("url",		this.url);
		e.setAttribute("login",		this.login);
		
		e.addContent( getPassword().saveToElement() );
		
		return e;
	}

	public void initFromElement(Element e) {
		this.url		= e.getAttributeValue("url");
		this.login		= e.getAttributeValue("login");
		
		if (this.url == null || this.login == null)
			ApplicationData.COUNT_INIT_ERROR++;
		
		PersistentUtils.InitObjectFromChild(password, e);
	}
}
