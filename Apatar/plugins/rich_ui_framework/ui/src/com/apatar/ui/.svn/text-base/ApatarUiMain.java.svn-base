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

package com.apatar.ui;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.java.plugin.boot.Application;
import org.java.plugin.util.ExtendedProperties;

import com.apatar.core.ApatarRegExp;
import com.apatar.core.ApplicationData;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DateAndTimeSettings;
import com.apatar.core.MainApatarPlugin;

public class ApatarUiMain extends MainApatarPlugin implements Application {

	public static JApatarMainUIFrame MAIN_FRAME = null;

	boolean isInit = false;

	SplashWindow splashScreen;
	boolean isWait = true;

	public static final String TMP_DIRECTORY = "temp";

	JWorkPane workPane;

	public static boolean saveProject() throws IOException {
		if (ApplicationData.NOEDITED_STATUS == ApplicationData.STATUS_APPLICATION) {
			return true;
		}
		if (ApplicationData.SAVED_STATUS == ApplicationData.STATUS_APPLICATION) {
			return true;
		}
		int flag = JOptionPane.showConfirmDialog(ApatarUiMain.MAIN_FRAME,
				"Do you want to save the project?", "Warning!",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (flag == JOptionPane.CANCEL_OPTION || flag == -1) {
			return false;
		}
		if (flag == JOptionPane.YES_OPTION) {
			boolean res;
			res = Actions.saveProject();
			if (!res) {
				return false;
			}
		}
		return true;
	}

	public static void exit() throws IOException {
		if (!saveProject()) {
			return;
		}

		String pathPrj = (ApplicationData.REPOSITORIES == null ? ""
				: ApplicationData.REPOSITORIES);
		File fileProxy = new File(pathPrj + "proxy.properties");
		Properties propsProxy;
		propsProxy = new Properties();
		if (fileProxy.exists()) {
			try {
				propsProxy.load(new FileInputStream(fileProxy));

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		propsProxy.setProperty("proxy.http.isUseProxy", ""
				+ ApplicationData.httpClient.isUseProxy());
		propsProxy.setProperty("proxy.http.host", ApplicationData.httpClient
				.getHost());
		propsProxy.setProperty("proxy.http.port", ""
				+ ApplicationData.httpClient.getPort());
		propsProxy.setProperty("proxy.http.userName",
				ApplicationData.httpClient.getUserName());
		propsProxy.setProperty("proxy.http.password",
				ApplicationData.httpClient.getPassword());

		try {
			propsProxy.store(new FileOutputStream(fileProxy), "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		saveDateAndTimeSettings();
		saveDebugSettings();

		System.exit(0);
	}

	private static void saveDateAndTimeSettings() {
		Connection conn = getSystemDBConnection();
		Statement st = null;
		try {
			st = conn.createStatement();

			DateAndTimeSettings dts = ApplicationData.APLICATION_DATE_SETTINGS;
			DataBaseTools.setOption(
					"apatar.settings.date.createWithApplication", ""
							+ dts.isCreateWithApplication(), st);
			DataBaseTools.setOption("apatar.settings.date.pattern", dts
					.getPattern(), st);
			DataBaseTools.setOption("apatar.settings.date.dateFormat", dts
					.getDateFormat(), st);
			DataBaseTools.setOption("apatar.settings.date.dateSeparator", dts
					.getDateSeparator(), st);
			DataBaseTools.setOption("apatar.settings.date.timeFormat", dts
					.getTimeFormat(), st);
			DataBaseTools.setOption("apatar.settings.date.timeStandart", dts
					.getTimeStandart(), st);

			st.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void saveDebugSettings() {
		Connection conn = getSystemDBConnection();
		Statement st = null;
		try {
			st = conn.createStatement();

			DataBaseTools.setOption(
					"apatar.settings.debug.isClearLogsBeforeRun", ""
							+ ApplicationData.isClearLogsBeforeRun, st);
			st.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JApatarMainUIFrame createApatarMainUIFrame() {
		JApatarMainUIFrame frame = new JApatarMainUIFrame(String.format(
				JApatarMainUIFrame.FRAME_TITLE, ""));
		frame.getContentPane().setLayout(new BorderLayout());

		JTabbedPane leftTabs = new JTabbedPane();
		JShortcutTree st = new JShortcutTree(ApplicationData
				.getNodeFactoryCollection());
		leftTabs.addTab("Functions", new JScrollPane(st));

		workPane = new JWorkPane(ApplicationData.getProject());

		Actions actions = new Actions(this, frame, workPane);
		frame.getContentPane().add(actions.getMenubar(), BorderLayout.NORTH);
		frame.getContentPane().add(actions.getToolbar(),
				BorderLayout.AFTER_LAST_LINE);

		JSplitPane leftSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				leftTabs, new JScrollPane(workPane));
		leftSplitter.setDividerLocation(200);
		leftSplitter.setDividerSize(3);
		leftSplitter.setOneTouchExpandable(true);
		frame.getContentPane().add(leftSplitter, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.setSize(600, 550);
		frame.setLocation(100, 100);

		return frame;
	}

	@Override
	protected Application initApplication(ExtendedProperties arg0, String[] arg1)
			throws Exception {

		ApplicationData.isApplication = true;

		setSpecialVariables(arg1);

		super.initApplication(arg0, arg1);
		// System.out.println(CryptTools.decrypt("oUeUjhCsrJlEaaT2QtGrYQ=="));
		for (String argument : arg1) {
			System.out.println(argument);
			if (argument.startsWith("datamap")) {
				try {
					ApplicationData.PROJECT_PATH = ApatarRegExp.getSubstrings(
							".*?=(.*)", argument, 1);
					isCreateWelcome = false;
				} catch (Exception e) {
					e.printStackTrace();
					ApplicationData.PROJECT_PATH = null;
				}
			}
			if (argument.startsWith("immediate_run")) {
				ApplicationData.IMMEDIATE_RUN = true;
			}
			if (argument.startsWith("close_after_finish")) {
				ApplicationData.CLOSE_AFTER_FINISH = true;
			}
			if (argument.startsWith("silent_run")) {
				ApplicationData.SILENT_RUN = true;
			}
		}
		return this;
	}

	private boolean isCreateWelcome = true;

	@Override
	public void startApplication() throws Exception {

		try {
			String lf = javax.swing.UIManager
					.getCrossPlatformLookAndFeelClassName();
			System.out.println("");
			System.out.println("getSystemLookAndFeelClassName=`" + lf + "`");
			if (lf.contains("windows") || lf.contains("motif")
					|| lf.contains("metal")) {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} else {
				UIManager
						.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			}
		} catch (Exception e) {
		}

		if (!ApplicationData.isApplication) {
			return;
		}
		splashScreen = new SplashWindow(UiUtils.SPLASH_ICON);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SplashThread st = new SplashThread();
				st.start();

				JApatarMainUIFrame frame = createApatarMainUIFrame();
				ApatarUiMain.MAIN_FRAME = frame;

				frame.setVisible(true);

				synchronized (st) {
					isWait = false;
					st.notify();
				}

				Connection conn = getSystemDBConnection();

				if (!registration(conn, new MainApatarPluginActions())) {
					setHttpClient();
				}

				createDateSettings(conn);
				createDebugOptions(conn);

				if (ApplicationData.PROJECT_PATH != null) {
					System.out.println("Trying to open datamap `"
							+ ApplicationData.PROJECT_PATH + "`");
					ApplicationData.COUNT_INIT_ERROR = 0;
					System.out.println("ApplicationData.PROJECT_PATH="
							+ ApplicationData.PROJECT_PATH);
					ReadWriteXMLDataUi rwXMLdata = new ReadWriteXMLDataUi();
					try {
						ApatarUiMain.MAIN_FRAME.setTitle(String.format(
								JApatarMainUIFrame.FRAME_TITLE, rwXMLdata
										.readXMLData(
												ApplicationData.PROJECT_PATH,
												ApplicationData.getProject())
										+ " - "));
					} catch (Exception e) {
						e.printStackTrace();
					}
					// ReadWriteXMLData.loadDateAndTimeSettings(ApplicationData.PROJECT_PATH);

					if (ApplicationData.COUNT_INIT_ERROR > 0) {
						JOptionPane
								.showMessageDialog(
										null,
										"An error occured while opening the DataMap: Uninitialized properties were found.\nPlease, check node(-s) configuration.");
					}
					UiUtils.updatePane(ApplicationData.getProject(), workPane);
					ApplicationData.STATUS_APPLICATION = ApplicationData.SAVED_STATUS;
				}

				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				String pathPrj = (ApplicationData.REPOSITORIES == null ? ""
						: ApplicationData.REPOSITORIES);
				DataBaseTools.shutdownDerbyDB(pathPrj + "systemdb");

				if (!isCreateWelcome) {
					if (ApplicationData.IMMEDIATE_RUN) {
						if (ApplicationData.COUNT_INIT_ERROR == 0) {
							System.out
									.println("Command line parameter `immediate_run` is set. Running datamap.");
							CountDownLatch doneSignal = new CountDownLatch(1);
							com.apatar.core.Runnable prj = new com.apatar.core.Runnable();
							ApplicationData.clearLogsBeforeRun();
							prj
									.Run(ApplicationData.getProject()
											.getNodes().values(), null,
											new ProcessingProgressActions(),
											doneSignal);
							try {
								doneSignal.await();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else {
							System.out
									.println("Command line parameter `immediate_run` is set. But cannot run datamap. Errors.");
						}
						if (ApplicationData.CLOSE_AFTER_FINISH) {
							try {
								System.out
										.println("Command line parameter `close_after_finish` is set. Exiting.");
								exit();
							} catch (IOException e) {
								e.printStackTrace();
								System.exit(0);
							}
						}
					}
					return;
				}

				JWelcomeDialog wDlg = new JWelcomeDialog(frame);
				int option = wDlg.selectOption();

				wDlg.dispose();

				if (option == JWelcomeDialog.LOAD_OPTION) {
					Actions.openProject(frame, workPane);
					ApplicationData.STATUS_APPLICATION = ApplicationData.SAVED_STATUS;
				}

			}
		});
	}

	private class SplashThread extends Thread {
		@Override
		public synchronized void run() {
			splashScreen.setVisible(true);
			try {
				while (isWait) {
					wait();
				}
			} catch (Exception e) {
			}
			splashScreen.setVisible(false);
			splashScreen.dispose();
		}

	}
}