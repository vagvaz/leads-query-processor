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

package com.apatar.openbravojdbc;

import java.util.Properties;

import org.jdom.Element;

import com.apatar.core.ApplicationData;
import com.apatar.core.JdbcParams;

public class OpenbravoJdbcConnection extends JdbcParams {

	protected Enum<ReceiveDatabaseType> databaseType	= ReceiveDatabaseType.PostgreSQL;
	private int postgresPort = 5432;
	private int oraclePort = 1521;

	protected void init() {
		this.setJdbcDriver(this.getJdbcDriver());
		this.setDriverName(this.getDatabaseType(true));
		if (isPostgreSQLdb()) {
			this.setPort(this.postgresPort);
		} else {
			this.setPort(this.oraclePort);
		}
	}

	public void setPort (int port) {
		if (isPostgreSQLdb()) {
			this.postgresPort = port;
		} else {
			this.oraclePort = port;
		}
		super.setPort(port);
	}

	public int getPort() {
		if (isPostgreSQLdb()) {
			return this.postgresPort;
		} else {
			return this.oraclePort;
		}
	}

	public String getJdbcDriver() {
		if (isPostgreSQLdb()) {
			return "org.postgresql.Driver";
		} else {
			return "oracle.jdbc.driver.OracleDriver";
		}
	}

	public String getConnUrl() {
		return "jdbc:"+this.getDatabaseType(true)+"://" + this.getHost() + ":" + this.getPort()
				+ "/" + this.getDbName();
	}

	public Properties getProperties() {
		return null;
	}

	public void setProperties(Properties property) {}

	public OpenbravoJdbcConnection(){
		super();
		init();
	}

	public Element saveToElement() {

		Element e = super.saveToElement();
		e.setAttribute("databaseType",	this.databaseType.toString());
		return e;
	}

	public void initFromElement(Element e) {
		super.initFromElement(e);

		String attribute = e.getAttributeValue("databaseType");
		if (attribute != null)
			this.databaseType = Enum.valueOf(ReceiveDatabaseType.class,
									attribute);
		else
			ApplicationData.COUNT_INIT_ERROR++;
	}


	public Enum<ReceiveDatabaseType> getDatabaseType(){
		return this.databaseType;
	}

	public String getDatabaseType (boolean getInLowerCase) {
		if (getInLowerCase) {
			return this.databaseType.toString().toLowerCase();
		} else {
			return this.databaseType.toString();
		}
	}

	public void setDatabaseType(Enum<ReceiveDatabaseType> databaseType){
		this.databaseType = databaseType;
	}

	public boolean isPostgreSQLdb() {
		if (this.getDatabaseType() == ReceiveDatabaseType.PostgreSQL) {
			return true;
		} else {
			return false;
		}
	}

	public String getDbName () {
		return "openbravo";
	}
}
