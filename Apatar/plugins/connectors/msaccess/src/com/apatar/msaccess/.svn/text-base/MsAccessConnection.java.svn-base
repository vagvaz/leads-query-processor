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

package com.apatar.msaccess;

import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import org.jdom.CDATA;
import org.jdom.Element;

import com.apatar.core.ApplicationData;
import com.apatar.core.JdbcParams;
import com.apatar.core.PasswordString;
import com.apatar.core.PersistentUtils;

// public class MsAccessConnection implements IPersistent {
public class MsAccessConnection extends JdbcParams {

	private String userName = "";
	protected String driverName = "";
	private PasswordString password = new PasswordString();
	private File file = null;
	protected String jdbcDriver = "sun.jdbc.odbc.JdbcOdbcDriver";

	public MsAccessConnection() {
		if (ApplicationData.DEBUG) {
			userName = "";
			password = new PasswordString("");
			file = new File("/");
		}

		init();
	}

	public MsAccessConnection(String login, PasswordString password, File file) {
		userName = login;
		this.password = password;
		this.file = file;

		init();
	}

	@Override
	protected void init() {
		super.setJdbcDriver("sun.jdbc.odbc.JdbcOdbcDriver");
	}

	@Override
	public String getUserName() {
		return userName;
	}

	@Override
	public void setUserName(String login) {
		userName = login;
	}

	@Override
	public PasswordString getPassword() {
		return password;
	}

	@Override
	public void setPassword(PasswordString password) {
		this.password = password;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public Element saveToElement() {

		Element e;
		try {
			e = super.saveToElement();
		} catch (Exception ex) {
			e = PersistentUtils.CreateElement(this);
		}

		e.setAttribute("userName", userName);

		if (null == file) {
			e.setAttribute("filePath", "");
		} else {
			e.setAttribute("filePath", file.getPath());
		}

		e.addContent(getPassword().saveToElement());
		if (sqlQuery != null) {
			Element elSqlQuery = new Element("sqlQuery");
			elSqlQuery.setContent(new CDATA(sqlQuery));
			e.addContent(elSqlQuery);
		}

		return e;
	}

	@Override
	public void initFromElement(Element e) {
		// super.initFromElement(e);
		userName = e.getAttributeValue("userName");
		file = new File(e.getAttributeValue("filePath"));
		setSqlQuery(e.getChildText("sqlQuery"));

		PersistentUtils.InitObjectFromChild(password, e);
	}

	@Override
	public String getConnUrl() {
		String url = "";
		if (file == null) {
			url = "jdbc:odbc:;DRIVER=Microsoft Access Driver " + "(*.mdb);DBQ=";
		} else {
			if (file.getPath().toLowerCase().endsWith(".mdb")) {
				url = "jdbc:odbc:;DRIVER=Microsoft Access Driver "
						+ "(*.mdb);DBQ=" + file.getPath();
			} else {
				url = "jdbc:odbc:;DRIVER=Microsoft Access Driver "
						+ "(*.mdb, *.accdb);DBQ=" + file.getPath();
			}
		}
		return url;
	}

	@Override
	public Properties getProperties() {
		return null;
	}

	@Override
	public void setProperties(Properties property) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.JdbcParams#generateConnection()
	 */
	@Override
	public Connection generateConnection() throws ClassNotFoundException,
			SQLException {
		Driver newDriver = null;
		try {
			newDriver = (Driver) ApplicationData.classForName(jdbcDriver)
					.newInstance();
			properties = getProperties();
			fillProperties();

			connection = newDriver.connect(getConnUrl(), properties);
			System.out.println(getConnUrl());
			if (statement != null) {
				try {
					statement.close();
				} catch (Exception e) {
				}
				statement = null;
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return connection;
	}

}
