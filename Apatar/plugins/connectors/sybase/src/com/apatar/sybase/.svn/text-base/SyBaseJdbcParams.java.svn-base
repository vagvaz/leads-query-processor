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

package com.apatar.sybase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Properties;

import com.apatar.core.ApplicationData;
import com.apatar.core.JdbcParams;
import com.sybase.jdbcx.SybDriver;

public class SyBaseJdbcParams extends JdbcParams {

	public SyBaseJdbcParams() {
		super();
		init();
	}

	@Override
	protected void init() {
		super.setJdbcDriver("com.sybase.jdbc3.jdbc.SybDriver");
		super.setDriverName("sybase");
		super.setPort(4747);
	}

	@Override
	public String getConnUrl() {
		return "jdbc:sybase:Tds:" + super.getHost() + ":" + super.getPort()
				+ "/" + super.getDbName();
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

		SybDriver newDriver = null;

		try {
			newDriver = (SybDriver) ApplicationData.classForName(jdbcDriver)
					.newInstance();
			newDriver.setVersion(com.sybase.jdbcx.SybDriver.VERSION_6);
			DriverManager.registerDriver(newDriver);
			properties = getProperties();
			fillProperties();

			connection = DriverManager.getConnection(getConnUrl(), properties);
			System.out.println(getConnUrl());
			connection.setAutoCommit(false);
			if (statement != null) {
				try {
					statement.close();
				} catch (Exception e) {
				}
				statement = null;
			}
			try {
				SQLWarning warning = null;
				warning = connection.getWarnings();
				while (null != warning) {
					System.err.println("SQL connection warning: " + warning);
					warning = warning.getNextWarning();
				}
			} catch (Exception e) {
				e.printStackTrace();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.JdbcParams#fillProperties()
	 */
	@Override
	public void fillProperties() {
		super.fillProperties();
		properties.setProperty("CHARSET", "utf8");
	}
}