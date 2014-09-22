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

package com.apatar.flickr;

import org.jdom.Element;

import com.apatar.core.ApplicationData;
import com.apatar.core.IPersistent;
import com.apatar.core.PasswordString;
import com.apatar.core.PersistentUtils;

public class FlickrConnection implements IPersistent {

	private PasswordString key		= new PasswordString();
	private PasswordString secret	= new PasswordString();

	public FlickrConnection(){
		if (ApplicationData.DebugAutoFill) {
			this.key.setValue("403bfb1abfd6e04ce80c84a67782d8c6");
			this.secret.setValue("45044d7a47acfc61");
		} else {
			this.key.setValue("");
			this.secret.setValue("");
		}
	}
	
	public FlickrConnection(String key, String secret){
		this.key.setValue(key);
		this.secret.setValue(secret);
	}
	
	public PasswordString getKey(){
		return this.key;
	}
	
	public void setKey(PasswordString key){
		this.key = key;
	}
	
	public PasswordString getSecret(){
		return this.secret;
	}
	
	public void setSecret(PasswordString secret){
		this.secret = secret;
	}
	
	public Element saveToElement() {
		Element e = PersistentUtils.CreateElement(this);
		
		e.setAttribute("key",		this.key.getValue());
		e.setAttribute("secret",	this.secret.getValue());
		
		return e;
	}

	public void initFromElement(Element e) {
		this.key.setValue(e.getAttributeValue("key"));
		this.secret.setValue(e.getAttributeValue("secret"));
		if (key.getValue() == null || secret.getValue() == null)
			ApplicationData.COUNT_INIT_ERROR++;
	}
}
