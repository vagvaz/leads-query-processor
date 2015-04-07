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
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Element;

public abstract class JdbcParams implements Cloneable, IPersistent {
	
	protected String driverName;

	protected String jdbcDriver;

	protected String userName;

	protected PasswordString password;
	
	protected String host;
	
	protected int port;
	
	protected String dbName;
	
	protected Connection connection;
	
	protected Statement statement;
	
	protected boolean ntIntegratedSecurity;
	
	protected Properties properties = null;
	
	protected String sqlQuery;
	
	boolean autocommit = true;
	
	public JdbcParams(String driverName,
			String jdbcDriver, String userName,
			PasswordString password, String host, int port,
			String dbName) {
		
		this.driverName = driverName;
		this.jdbcDriver = jdbcDriver;
		this.userName	= userName;
		this.password	= password;
		this.host		= host;
		this.port		= port;
		this.dbName		= dbName;
	}
	
	public JdbcParams(String driverName, String userName, PasswordString password) {
		super();
		this.driverName = driverName;
		this.userName = userName;
		this.password = password;
	}



	public JdbcParams(){
		this("", "", "", new PasswordString(), "", 0, "");
	}
//-- get
	public String getDriverName() {
		return driverName;
	}

	public String getJdbcDriver() {
		return jdbcDriver;
	}

	public PasswordString getPassword() {
		return password;
	}

	public String getUserName() {
		return userName;
	}
	
	public boolean isAutocommit() {
		return autocommit;
	}

	public void setAutocommit(boolean autocommit) {
		this.autocommit = autocommit;
	}

	public Connection getConnection() throws ClassNotFoundException, SQLException {
		if (!isConnectionValid()) {
			generateConnection();
		}
		return connection;
	}
	
	public Statement getStatement() throws SQLException, ClassNotFoundException {
		if (!isConnectionValid()) {
			generateConnection();
		}
		if (statement == null)
			statement = getConnection().createStatement();
		return statement;
	}

	public String toString() {
		return new StringBuffer("dbVendor: ").
			append("driverName: ").append(driverName).
			append(", jdbcDriver: ").append(jdbcDriver).
			append(", connUrl: ").toString();
	}
	
//-- set
	public void setDriverName( String value ) {
		driverName = value;
	}

	public void setJdbcDriver( String value ) {
		jdbcDriver = value;
	}

	public void setPassword( PasswordString value ) {
		password = value;
	}

	public void setUserName( String value ) {
		userName = value;
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	
	public boolean isNtIntegratedSecurity() {
		return ntIntegratedSecurity;
	}

	public void setNtIntegratedSecurity(boolean ntIntegratedSecurity) {
		this.ntIntegratedSecurity = ntIntegratedSecurity;
	}
	
	public String getSqlQuery() {
		return sqlQuery;
	}

	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

	public Element saveToElement() {
		Element element = PersistentUtils.CreateElement(this);
		element.setAttribute("driverName", getDriverName());
		element.setAttribute("jdbcDriver", getJdbcDriver());
		element.setAttribute("connUrl", 	 getConnUrl());
		element.setAttribute("userName", 	 getUserName());
		element.setAttribute("host",  	 getHost());
		element.setAttribute("port", 		 String.valueOf(getPort()));
		element.setAttribute("dbName",	 getDbName());
		
		if (sqlQuery != null) {
			Element elSqlQuery = new Element("sqlQuery");
			elSqlQuery.setContent(new CDATA(sqlQuery));
			element.addContent(elSqlQuery);
		}
		
		element.addContent( getPassword().saveToElement() );
		
		// save properties
		if( null != getProperties() ){
			Element propElement = new Element(Properties.class.getName());
			
			Enumeration<Object> enumProps = getProperties().keys();
			
			while( enumProps.hasMoreElements() ){
				String name = (String)enumProps.nextElement();
				propElement.setAttribute(name,
						getProperties().getProperty( name) );
			}
			
			element.addContent(propElement);
		}
		
		return element;
	}
	
	public void initFromElement(Element element) {
		init();
		if (element == null) {
			ApplicationData.COUNT_INIT_ERROR++;
			return;
		}
			
		setDriverName( element.getAttributeValue("driverName"));
		setJdbcDriver( element.getAttributeValue("jdbcDriver"));
		
		PersistentUtils.InitObjectFromChild(password, element);
		
		String value = element.getAttributeValue("userName");
		if (value != null)
			setUserName(value);
		else
			ApplicationData.COUNT_INIT_ERROR++;
		setUserName(value);
		value = element.getAttributeValue("port");
		if (value != null)
			setPort(Integer.parseInt(value));
		else
			ApplicationData.COUNT_INIT_ERROR++;
		setHost(element.getAttributeValue("host"));
		setDbName(element.getAttributeValue("dbName"));
		
		setSqlQuery(element.getChildText("sqlQuery"));
		
		Element propElement = element.getChild( Properties.class.getName() );
		if( null != propElement ){
			Properties prop = new Properties();
			
			Attribute attr = null;
			for(Iterator<Attribute> it = propElement.getAttributes().iterator(); it.hasNext();){
				attr = it.next();
				prop.setProperty(attr.getName(), attr.getValue() );
			}
			
			setProperties( prop );
		}
				
	}
	
	public Connection generateConnection() throws ClassNotFoundException, SQLException {
			Driver newDriver = null;
			try {
				newDriver = (Driver)ApplicationData.classForName(jdbcDriver).newInstance();
				properties = getProperties();
				fillProperties();
				
				connection = newDriver.connect(getConnUrl(), properties);
				System.out.println(getConnUrl());
				connection.setAutoCommit(false);
				if (statement != null) {
					try {
						statement.close();
					} catch (Exception e) {}
					statement = null;
				}
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return connection;
	}

	public void fillProperties() {
		if( null ==  properties)
			properties = new Properties();
		
		properties.setProperty("user", userName);
		properties.setProperty("password", password.getValue());
	}
	
	public JdbcParams clone() {
		JdbcParams clone = null;
		try{
            clone=(JdbcParams)super.clone();
        } catch(CloneNotSupportedException e) {
            System.err.println(this.getClass()+" can't be cloned");
        }
        return clone;
	}
	
	public abstract String getConnUrl();
	
	public abstract Properties getProperties();
	
	public abstract void setProperties(Properties property);
	
	protected abstract void init();
	
	protected boolean isConnectionValid() {
		if (connection == null)
			return false;
		try {
			if (connection.isClosed())
				return false;
		} catch(SQLException e) {
			try {
				connection.close();
			} catch(Exception ex) {}
			return false;
		}
		return true;
	}
}
