/*TODO recorded refactoring
 * класс MainETLPlugin перемиенован в MainApatarPlugin
 * *********************
 */

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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.java.plugin.PluginLifecycleException;
import org.java.plugin.boot.Application;
import org.java.plugin.boot.ApplicationPlugin;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.registry.Extension.Parameter;
import org.java.plugin.util.ExtendedProperties;

import com.apatar.ui.NodeFactory;

public class MainApatarPlugin extends ApplicationPlugin implements Application {
	private Collection<NodeFactory> loadNodesPlugins() {

		List<NodeFactory> list = new ArrayList<NodeFactory>();

		ExtensionPoint toolExtPoint = getManager().getRegistry()
				.getExtensionPoint("com.apatar.core", "Node");

		for (Extension ext : toolExtPoint.getConnectedExtensions()) {
			Parameter classParam = ext.getParameter("class");
			try {
				PluginDescriptor pluginDescr = ext
						.getDeclaringPluginDescriptor();

				getManager().activatePlugin(pluginDescr.getId());

				ClassLoader classLoader = getManager().getPluginClassLoader(
						pluginDescr);

				// add loader to be able to create any class object
				ApplicationData.addLoader(classLoader);
				Class nodeFactClass = classLoader.loadClass(classParam
						.valueAsString());

				list.add((NodeFactory) nodeFactClass.newInstance());
			} catch (PluginLifecycleException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return list;
	}

	@Override
	protected Application initApplication(ExtendedProperties arg0, String[] arg1)
			throws Exception {
		String pathPrj = (ApplicationData.REPOSITORIES == null ? ""
				: ApplicationData.REPOSITORIES);
		ApplicationData.REPOSITORIES = new String(pathPrj);

		if (!ApplicationData.DEBUG) {
			String logFileSuffix = "";
			if (ApplicationData.isSchedulerRunning) {
				logFileSuffix = "2";
			}

			File fOut = new File(pathPrj + "debug_output" + logFileSuffix
					+ ".txt");
			File fErr = new File(pathPrj + "error_output" + logFileSuffix
					+ ".txt");
			try {
				PrintStream printStreamOut = new PrintStream(
						new BufferedOutputStream(new FileOutputStream(fOut)),
						true);
				PrintStream printStreamErr = new PrintStream(
						new BufferedOutputStream(new FileOutputStream(fErr)),
						true);
				System.setOut(printStreamOut);
				System.setErr(printStreamErr);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			ApplicationData.clearLogs();
		}

		ApplicationData.setNodeFactoryCollection(loadNodesPlugins());

		ApplicationData.tempDataBase = new TempDataBase();

		CoreUtils.printInfoToConsol(CoreUtils.ALL_INFO);

		return this;
	}

	@Override
	protected void doStart() throws Exception {
	}

	@Override
	protected void doStop() throws Exception {
	}

	public void startApplication() throws Exception {
	}

	/*
	 * Set variables from command line
	 */
	protected Properties setSpecialVariables(String[] arg1) {
		Properties props = null;
		for (String arg : arg1) {
			StringTokenizer tokenizer = new StringTokenizer(arg, "=");
			if (tokenizer.countTokens() < 2) {
				continue;
			}
			String name = tokenizer.nextToken();
			String value = tokenizer.nextToken();
			if (name.equalsIgnoreCase("setting")) {
				props = new Properties();
				try {
					props.load(new FileInputStream(value));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				ApplicationData.SalesForceAppExchange = Boolean
						.parseBoolean(props.getProperty(
								"SalesForceAppExchange", "true"));
				ApplicationData.REPOSITORIES = props.getProperty("projectPath");
			}
			if (name.equalsIgnoreCase("debug")) {
				ApplicationData.DEBUG = Boolean.parseBoolean(value);
			}
		}
		return props;
	}

	/*
	 * Registration on Apatarforge
	 */
	/*
	 * TODO recorded refactoring в метод protected boolean registration добавлен
	 * параметр AbstractApatarActions action выполняющий интерактивные функции
	 * UI *********************
	 */
	protected boolean registration(Connection conn,
			AbstractApatarActions actions) {
		Statement st = null;
		try {
			st = conn.createStatement();
			String value = DataBaseTools.getOption("apatar.registration", st);
			if (value != null) {
				return false;
			}
			if (null != actions) {
				if (actions.callRegistrationMethod()) {
					DataBaseTools.setOption("apatar.registration", "yes", st);
				} else {
					System.exit(0);
					return false;
				}
			} else {
				return false;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			try {
				st.close();
				// conn.close();
				/*
				 * String pathPrj = (ApplicationData.REPOSITORIES == null ? "" :
				 * ApplicationData.REPOSITORIES);
				 * DataBaseTools.shutdownDerbyDB(pathPrj + "systemdb");
				 */
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	protected void createDebugOptions(Connection conn) {
		Statement st = null;
		try {
			st = conn.createStatement();
			ApplicationData.isClearLogsBeforeRun = Boolean
					.parseBoolean(DataBaseTools.getOption(
							"apatar.settings.debug.isClearLogsBeforeRun", st));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void createDateSettings(Connection conn) {
		Statement st = null;
		try {
			st = conn.createStatement();
			String value = DataBaseTools.getOption(
					"apatar.settings.date.createWithApplication", st);
			boolean createWithApplication = false;
			if (value == null) {
				return;
			}
			createWithApplication = Boolean.parseBoolean(value);
			String valuePatern = DataBaseTools.getOption(
					"apatar.settings.date.pattern", st);
			if (createWithApplication) {
				String dateFormat = DataBaseTools.getOption(
						"apatar.settings.date.dateFormat", st);
				String dateSeparator = DataBaseTools.getOption(
						"apatar.settings.date.dateSeparator", st);
				String timeFormat = DataBaseTools.getOption(
						"apatar.settings.date.timeFormat", st);
				String timeStandart = DataBaseTools.getOption(
						"apatar.settings.date.timeStandart", st);
				ApplicationData.APLICATION_DATE_SETTINGS.init(valuePatern,
						dateFormat, dateSeparator, timeFormat, timeStandart);
				ApplicationData.DATAMAP_DATE_SETTINGS.init(valuePatern,
						dateFormat, dateSeparator, timeFormat, timeStandart);
			} else {
				ApplicationData.APLICATION_DATE_SETTINGS.init(false,
						valuePatern);
				ApplicationData.DATAMAP_DATE_SETTINGS.init(false, valuePatern);
			}
			ApplicationData.DATAMAP_DATE_SETTINGS
					.init(ApplicationData.APLICATION_DATE_SETTINGS);

		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			try {
				st.close();
				// conn.close();
				/*
				 * String pathPrj = (ApplicationData.REPOSITORIES == null ? "" :
				 * ApplicationData.REPOSITORIES);
				 * DataBaseTools.shutdownDerbyDB(pathPrj + "systemdb");
				 */
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	protected void setHttpClient() {
		String pathPrj = (ApplicationData.REPOSITORIES == null ? ""
				: ApplicationData.REPOSITORIES);
		File fileProxy = new File(pathPrj + "proxy.properties");
		Properties propsProxy;
		propsProxy = new Properties();
		ApatarHttpClient client = new ApatarHttpClient();
		ApplicationData.httpClient = client;
		if (fileProxy.exists()) {
			try {
				propsProxy.load(new FileInputStream(fileProxy));
				client.setUseProxy(Boolean.parseBoolean(propsProxy.getProperty(
						"proxy.http.isUseProxy", "false")));
				client.setHost(propsProxy.getProperty("proxy.http.host"));
				client.setPort(Integer.parseInt(propsProxy.getProperty(
						"proxy.http.port", "false")));
				client.setUserName(propsProxy
						.getProperty("proxy.http.userName"));
				client.setPassword(propsProxy
						.getProperty("proxy.http.password"));
				if (client.isUseProxy()) {
					System.setProperty("http.proxyHost", client.getHost());
					System.setProperty("http.proxyPort", String.valueOf(client
							.getPort()));
					String proxyUser = client.getUserName();
					if (proxyUser != null) {
						System.setProperty("http.proxyUser", proxyUser);
						String proxyPassword = client.getPassword();
						if (proxyPassword != null) {
							System.setProperty("http.proxyPassword",
									proxyPassword);
						}
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		ApplicationData.httpClient = client;
	}

	protected void setDateSettings() {

	}

	protected static Connection getSystemDBConnection() {
		String pathPrj = (ApplicationData.REPOSITORIES == null ? ""
				: ApplicationData.REPOSITORIES);
		String url = "jdbc:derby:" + pathPrj + "systemdb;create=true";
		System.out.println("Project path=" + pathPrj);
		Connection conn = null;
		Statement st = null;
		try {
			Driver driver = (Driver) ApplicationData.classForName(
					"org.apache.derby.jdbc.EmbeddedDriver").newInstance();
			conn = driver.connect(url, new Properties());
			st = conn.createStatement();
			st.executeQuery("Select * From options");
			st.close();
			return conn;
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (SQLException e) {
			try {
				st
						.executeUpdate("Create Table options (name Varchar(255), value Varchar(255))");
				st.close();
				return conn;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}

}
