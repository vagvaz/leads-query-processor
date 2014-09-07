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

package com.apatar.webdav.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.WebdavResources;

import com.apatar.core.ApplicationData;
import com.apatar.ui.JDefaultContextMenu;

public class JWebDavTreeModePanel extends JPanel {

	private static final long serialVersionUID = 2L;

	private JTable folderTable = null;
	private DefaultTableModel tblModel = null;

	private JLabel title = null;

	private JLabel labelPath = null;
	private final static String WORD_PATH = "Path: ";

	private WebdavResource webdav = null;
	private WebdavResource currentres = null;
	private WebdavResources resources = null;
	String separator = "/";

	private String log = "";
	private String pass = "";
	private String url = "";

	private class TableKeyListener implements KeyListener {

		public void keyTyped(KeyEvent arg0) {
		}

		public void keyPressed(KeyEvent e) {
			if (10 == e.getKeyCode()) {
				int row = folderTable.getSelectedRow();

				String value = (String) folderTable.getValueAt(row, 0);
				makeWebDavPath(value);

				return;
			}
		}

		public void keyReleased(KeyEvent e) {
		}
	}

	@SuppressWarnings("serial")
	private class CellTableEditor extends DefaultCellEditor {

		Component comp = null;

		public CellTableEditor(JTextField arg0) {
			super(arg0);
			comp = arg0;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object obj,
				boolean arg2, int row, int cell) {
			String name = String.valueOf(obj);

			makeWebDavPath(name);

			return null;
		}
	}

	public JWebDavTreeModePanel() {
		super();
		createPanel();
	}

	public void setTitle(String titleText) {
		title.setText("Choose folder on " + titleText + " server.");
	}

	private void createPanel() {
		setLayout(new BorderLayout(5, 5));

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		add(panel, BorderLayout.CENTER);

		title = new JLabel("");
		panel.add(title);

		labelPath = new JLabel(WORD_PATH);

		panel.add(labelPath);

		panel.add(getFoldersPanel());
	}

	private JPanel getFoldersPanel() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);

		JScrollPane srollPane = new JScrollPane(createFolderTable());
		srollPane.setPreferredSize(new Dimension(470, 320));
		srollPane.setBorder(null);

		panel.add(srollPane);

		return panel;
	}

	private JTable createFolderTable() {
		tblModel = new DefaultTableModel();
		tblModel.addColumn("Folder List");

		folderTable = new JTable(tblModel);
		folderTable.setOpaque(false);
		folderTable.setBackground(null);
		folderTable.setBorder(null);
		folderTable.setShowGrid(false);

		folderTable.addKeyListener(new TableKeyListener());

		folderTable.getColumn("Folder List").setCellEditor(
				new CellTableEditor(new JTextField()));

		folderTable.setComponentPopupMenu(new JDefaultContextMenu(folderTable));

		return folderTable;
	}

	// -----------------

	private void removeRowsFromTable() {
		for (int i = folderTable.getRowCount() - 1; i > -1; i--) {
			tblModel.removeRow(i);
		}
	}

	private void setRowsToTable(WebdavResources resources) {
		removeRowsFromTable();

		Object contentTable[] = new Object[1];
		contentTable[0] = "..";
		tblModel.addRow(contentTable);

		WebdavResource[] res = resources.listResources();

		for (WebdavResource re : res) {
			contentTable = new Object[1];
			contentTable[0] = re.getDisplayName();
			tblModel.addRow(contentTable);
		}
	}

	private void setValueToLabelPath(String uri) {
		labelPath.setText(WORD_PATH + uri);
	}

	private void setDataFromWebDavToTable() {
		try {
			resources = currentres.getChildResources();
			setRowsToTable(resources);
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void makeWebDavPath(String name) {
		if ("..".equals(name)) {

			try {
				String path = currentres.getHttpURL().toString();
				path = path.replaceAll("%20", " ");

				if (!path.equalsIgnoreCase(url)) {
					int ind = path.lastIndexOf(separator);
					path = path.substring(0, ind);

					currentres = new WebdavResource(getHttpUrl(path), true);
				}
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			if (null != name) {
				try {
					currentres = currentres.getChildResources().getResource(
							name);
				} catch (HttpException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		setValueToLabelPath(currentres.getHttpURL().toString().replace("%20",
				" "));
		setDataFromWebDavToTable();
	}

	// -----------------

	private HttpsURL getHttpUrl(String url) {
		HttpsURL httpUrl = null;

		try {
			httpUrl = new HttpsURL(url);
			httpUrl.setUserinfo(log, pass);
		} catch (URIException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		return httpUrl;
	}

	public void openWebDavConnect(String url, String login, String password,
			String innerUri) {

		log = login;
		pass = password;
		this.url = url;

		try {
			if (ApplicationData.httpClient.isUseProxy()) {
				String proxyUser = ApplicationData.httpClient.getUserName();
				if (proxyUser != null) {
					Credentials cred = new UsernamePasswordCredentials(
							proxyUser, ApplicationData.httpClient.getPassword());
					webdav = new WebdavResource(
							getHttpUrl(this.url + innerUri),
							ApplicationData.httpClient.getHost(),
							ApplicationData.httpClient.getPort(), cred, true);
				} else {
					webdav = new WebdavResource(
							getHttpUrl(this.url + innerUri),
							ApplicationData.httpClient.getHost(),
							ApplicationData.httpClient.getPort(), true);
				}
			} else {
				webdav = new WebdavResource(getHttpUrl(this.url + innerUri),
						true);
			}
			currentres = webdav;
			resources = webdav.getChildResources();

			makeWebDavPath(null);

		} catch (URIException e) {
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void closeWebDavConnect() {
		try {
			// this.webdav.closeSession();
			webdav.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getWebDavUri() {
		int row = folderTable.getSelectedRow();

		if (folderTable.getRowCount() < row) {
			row = 0;
		}

		String uri = currentres.getHttpURL().toString().replace("%20", " ");
		uri = uri.replace(url, "");

		if (0 < row) {
			uri += separator + (String) folderTable.getValueAt(row, 0);
		}

		return uri;
	}
}
