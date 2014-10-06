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

package com.apatar.systemtray;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

import org.java.plugin.boot.Application;
import org.java.plugin.util.ExtendedProperties;
import org.jdesktop.jdic.tray.SystemTray;
import org.jdesktop.jdic.tray.TrayIcon;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.apatar.core.ApplicationData;
import com.apatar.core.DataBaseTools;
import com.apatar.core.MainApatarPlugin;
import com.apatar.core.ReadWriteXMLData;
import com.apatar.systemtray.ui.ApatarSchedulingActions;
import com.apatar.systemtray.ui.JSchedulingPropertyDialog;
import com.apatar.ui.MainApatarPluginActions;

public class Tray extends MainApatarPlugin implements Application {
	boolean isInit = false;
	JPopupMenu menu;
	SystemTray tray = null;

	private JSchedulingPropertyDialog dlg;

	boolean withoutUI = false;

	public JMenuItem quit;

	private final ApatarSchedulingTimer timer = new ApatarSchedulingTimer();
	private ApatarSchedulingActions actions = null;

	public void starting() {
		// initialization

		if (!withoutUI) {

			tray = SystemTray.getDefaultSystemTray();
			dlg = new JSchedulingPropertyDialog(this);
			dlg.setLocationRelativeTo(null);
			actions = new ApatarSchedulingActions(dlg);

			menu = new JPopupMenu("Menu");
			JMenuItem item1 = new JMenuItem("Properties");
			item1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					dlg.setVisible(true);
					dlg.renewTaskTable();
				}
			});
			menu.add(item1);
			menu.addSeparator();
			quit = new JMenuItem("Close");
			quit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					try {
						save();
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.exit(0);
				}
			});
			menu.add(quit);

			ImageIcon icon = new ImageIcon(Tray.class.getResource("clock.gif"));
			TrayIcon trayIcon = new TrayIcon(icon, "Apatar Scheduling", menu);
			trayIcon.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					dlg.setVisible(true);
					dlg.renewTaskTable();
				}
			});
			tray.addTrayIcon(trayIcon);
		}
		try {
			init();
		} catch (JDOMException e1) {
			e1.printStackTrace();
		}
		if (null != dlg) {
			dlg.setVisible(true);
			dlg.renewTaskTable();
		}
	}

	@Override
	public void startApplication() throws Exception {
		try {
			String lf = UIManager.getSystemLookAndFeelClassName();
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

		if (ApplicationData.isApplication) {
			return;
		}

		Connection conn = getSystemDBConnection();

		if (!registration(conn, new MainApatarPluginActions())) {
			setHttpClient();
		}

		createDateSettings(conn);
		createDebugOptions(conn);

		conn.close();
		String pathPrj = (ApplicationData.REPOSITORIES == null ? ""
				: ApplicationData.REPOSITORIES);
		DataBaseTools.shutdownDerbyDB(pathPrj + "systemdb");

		starting();
	}

	@Override
	protected Application initApplication(ExtendedProperties arg0, String[] arg1)
			throws Exception {
		ApplicationData.isSchedulerRunning = true;
		Properties props = setSpecialVariables(arg1);

		if (props == null) {
			props = new Properties();
			props.load(new FileInputStream("boot2noApex.properties"));
		}
		ApplicationData.withoutUI = Boolean.parseBoolean(props.getProperty(
				"withoutUI", "false"));

		ApplicationData.isApplication = false;

		super.initApplication(arg0, arg1);
		return this;
	}

	public void addScheduling(Scheduling scheduling) {
		String name = scheduling.getSchedulingName();
		if (timer.getSchedulings().containsKey(name)) {
			// timer.getSchedulings().get(name).setTaskEnabled(true);
		}
		timer.getSchedulings().put(name, scheduling);
	}

	public void deleteScheduling(Scheduling scheduling) {
		String name = scheduling.getSchedulingName();
		if (timer.getSchedulings().containsKey(name)) {
			// timer.getSchedulings().get(name).interrupt();
		}
		timer.getSchedulings().remove(name);
	}

	public void deleteScheduling(String name) {
		if (timer.getSchedulings().containsKey(name)) {
			// timer.getSchedulings().get(name).interrupt();
			timer.getSchedulings().remove(name);
		}
	}

	public Scheduling getScheduling(String name) {
		return timer.getSchedulings().get(name);
	}

	public void stopScheduling(String name) {
		if (timer.getSchedulings().containsKey(name)) {
			timer.getSchedulings().get(name).setTaskEnabled(false);
		}
	}

	public void startScheduling(String name) {
		if (timer.getSchedulings().containsKey(name)) {
			Scheduling scheduling = timer.getSchedulings().get(name);
			if (null == scheduling.getNextTaskRunDate()
					|| scheduling.getNextTaskRunDate().before(
							new GregorianCalendar())) {
				throw new RuntimeException(
						"Task will never run."
								+ (null != scheduling.getNextTaskRunDate() ? " Next task run date/time is: "
										+ scheduling.getNextTaskRunDate()
												.getTime().toString()
										: ""));
			} else {
				scheduling.setTaskEnabled(true);
			}
		}
	}

	private void save() throws IOException {
		Element root = new Element("Schedulings");
		Document doc = new Document(root);
		for (Scheduling scheduling : timer.getSchedulings().values()) {
			root.addContent(scheduling.generateXML());
		}
		// TODO decide to use ReadWriteXMLDataUi instead of ReadWriteXMLData
		ReadWriteXMLData rwXMLdata = new ReadWriteXMLData();
		rwXMLdata.saveDocumentToFile(doc, new FileWriter("schedulings.xml"));
	}

	protected void init() throws JDOMException {
		Element root;
		try {
			root = ReadWriteXMLData.getRootElement("schedulings.xml");
			Object[] objs = root.getChildren().toArray();
			for (Object element : objs) {
				Element elem = (Element) element;
				elem.detach();
				Scheduling sch = createScheduling(elem, actions);
				timer.getSchedulings().put(sch.getSchedulingName(), sch);
			}

		} catch (IOException e) {
		}
		timer.runManager();
	}

	public Scheduling createScheduling(Element elem,
			ApatarSchedulingActions actions) {
		return new Scheduling(elem, actions);
	}

	public Scheduling createScheduling(Element elem) {
		return new Scheduling(elem, actions);
	}

	public HashMap<String, Scheduling> getSchedulings() {
		return timer.getSchedulings();
	}

	public JSchedulingPropertyDialog getDlg() {
		return dlg;
	}

}
