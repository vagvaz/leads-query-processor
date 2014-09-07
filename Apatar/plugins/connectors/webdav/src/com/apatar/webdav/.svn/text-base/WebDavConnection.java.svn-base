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

package com.apatar.webdav;

import org.jdom.Element;

import com.apatar.core.ApplicationData;
import com.apatar.core.IPersistent;
import com.apatar.core.PasswordString;
import com.apatar.core.PersistentUtils;

public class WebDavConnection implements IPersistent {

	private String login = "";
	private PasswordString password = new PasswordString();
	private String url = "";

	public WebDavConnection() {
	}

	public WebDavConnection(String url, String login, PasswordString password) {
		this.url = url;
		this.login = login;
		this.password = password;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public PasswordString getPassword() {
		return password;
	}

	public void setPassword(PasswordString password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Element saveToElement() {
		Element e = PersistentUtils.CreateElement(this);

		e.setAttribute("url", url);
		e.setAttribute("login", login);

		e.addContent(getPassword().saveToElement());

		return e;
	}

	public void initFromElement(Element e) {
		url = e.getAttributeValue("url");
		login = e.getAttributeValue("login");

		if (url == null || login == null) {
			ApplicationData.COUNT_INIT_ERROR++;
		}

		PersistentUtils.InitObjectFromChild(password, e);
	}
}