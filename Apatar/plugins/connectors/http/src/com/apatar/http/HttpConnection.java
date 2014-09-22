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

package com.apatar.http;

import org.jdom.Element;

import com.apatar.core.ApplicationData;
import com.apatar.core.IPersistent;
import com.apatar.core.PersistentUtils;

public class HttpConnection implements IPersistent {

	String				url;
	HttpRequestMethod	httpRequestMethod	= HttpRequestMethod.post;

	public void initFromElement(Element e) {
		if (e == null) {
			ApplicationData.COUNT_INIT_ERROR++;
			return;
		}
		setUrl(e.getAttributeValue("url"));
		String method = e.getAttributeValue("method");
		if ("get".equals(method)) {
			setMethod(HttpRequestMethod.get);
		} else {
			setMethod(HttpRequestMethod.post);
		}

	}

	public Element saveToElement() {
		Element e = PersistentUtils.CreateElement(this);
		e.setAttribute("url", url);
		e.setAttribute("method", httpRequestMethod.toString());
		return e;
	}

	public Enum<HttpRequestMethod> getMethod() {
		return httpRequestMethod;
	}

	public void setMethod(Enum<HttpRequestMethod> requestMethod) {
		httpRequestMethod = (HttpRequestMethod) requestMethod;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
