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

package com.apatar.as400;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import com.apatar.core.ApplicationData;
import com.apatar.core.JdbcParams;

public class AS400JdbcParams extends JdbcParams {

	public AS400JdbcParams() {
		super();
		init();
	}

	protected void init() {
		super.setJdbcDriver("com.ibm.as400.access.AS400JDBCDriver");
//		super.setJdbcDriver("sun.jdbc.odbc.JdbcOdbcDriver");
		super.setDriverName("");//
//		super.setPort(3306);
	}

	public String getConnUrl() {
//		return "jdbc:odbc:;Driver={iSeries Access ODBC Driver};System="+super.getHost()+";Uid="+super.getUserName()+";Pwd="+super.getPassword()+";library=QUSRSYS";
		return "jdbc:as400://"+super.getHost()+"/"+super.getDbName()+";naming=system;errors=full;Uid="+super.getUserName()+";Pwd="+super.getPassword()+";";
	}

	public Properties getProperties() {
		return null;
	}

	public void setProperties(Properties property) {}

	protected boolean isConnectionValid() {
		if (connection == null){
			return false;
		}
		return true;
	}

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
