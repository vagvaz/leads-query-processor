/*
_______________________
Apatar Open Source Data Integration
Copyright (C) 2005-2007, Apatar, Inc.
info@apatar.com
195 Meadow St., 2nd Floor
Chicopee, MA 01013

### This program is free software; you can redistribute it and/or modify
### it under the terms of the GNU General Public License as published by
### the Free Software Foundation; either version 2 of the License, or
### (at your option) any later version.

### This program is distributed in the hope that it will be useful,
### but WITHOUT ANY WARRANTY; without even the implied warranty of
### MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.# See the
### GNU General Public License for more details.

### You should have received a copy of the GNU General Public License along
### with this program; if not, write to the Free Software Foundation, Inc.,
### 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
________________________

*/

package com.apatar.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class TempDatabaseJdbcParams extends JdbcParams{

	/*
	 * Create connection url
	 */
	public String getConnUrl() {
		String pathPrj = (ApplicationData.REPOSITORIES == null ? "" : ApplicationData.REPOSITORIES);
		String url = "jdbc:derby:" + pathPrj + "tempdb/";
		String folder = ApplicationData.isApplication ? "application" : "scheduling";
		url += folder + "/temp_db;create=true";
		return url;
	}

	public Properties getProperties() {
		return null;
	}

	public void setProperties(Properties property) {}

	public Connection getConnection() throws ClassNotFoundException, SQLException {
		if (connection == null)
			generateConnection();

		return connection;
	}

	@Override
	protected void init() {

	}
}
