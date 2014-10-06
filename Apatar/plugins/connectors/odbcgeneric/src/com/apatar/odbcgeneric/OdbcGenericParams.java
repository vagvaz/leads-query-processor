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

package com.apatar.odbcgeneric;

import java.util.Properties;

import org.jdom.CDATA;
import org.jdom.Element;

import com.apatar.core.ApplicationData;
import com.apatar.core.JdbcParams;
import com.apatar.core.PersistentUtils;

//public class MsAccessConnection implements IPersistent {
public class OdbcGenericParams extends JdbcParams {

	private String DSN = null;

	public OdbcGenericParams() {
		DSN = "";
		init();
	}

	public OdbcGenericParams(String dsn) {
		DSN = dsn;
		init();
	}

	@Override
	protected void init() {
		super.setJdbcDriver("sun.jdbc.odbc.JdbcOdbcDriver");
	}

	public String getDSN() {
		return DSN;
	}

	public void setDSN(String dsn) {
		DSN = dsn;
	}

	@Override
	public Element saveToElement() {
		Element e = PersistentUtils.CreateElement(this);
		e.setAttribute("userName", userName);
		e.addContent(getPassword().saveToElement());
		e.setAttribute("DSN", DSN);
		if (sqlQuery != null) {
			Element elSqlQuery = new Element("sqlQuery");
			elSqlQuery.setContent(new CDATA(sqlQuery));
			e.addContent(elSqlQuery);
		}
		return e;
	}

	@Override
	public void initFromElement(Element e) {
		if (e == null) {
			ApplicationData.COUNT_INIT_ERROR++;
			return;
		}
		setDSN(e.getAttributeValue("DSN"));
		if (DSN == null) {
			ApplicationData.COUNT_INIT_ERROR++;
		}
		userName = e.getAttributeValue("userName");
		if (userName == null) {
			userName = "";
		}
		setSqlQuery(e.getChildText("sqlQuery"));
		PersistentUtils.InitObjectFromChild(password, e);
	}

	@Override
	public String getConnUrl() {
		return "jdbc:odbc:" + getDSN();
	}

	@Override
	public Properties getProperties() {
		return null;
	}

	@Override
	public void setProperties(Properties property) {
	}

}
