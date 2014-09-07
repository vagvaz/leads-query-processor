/*
_______________________

Apatar Open Source Data Integration

Copyright (C) 2005-2007, Apatar, Inc.

info@apatar.com

195 Meadow St., 2nd Floor

Chicopee, MA 01013



    This program is free software; you can redistribute it and/or modify

    it under the terms of the GNU General Public License as published by

    the Free Software Foundation; either version 2 of the License, or

    (at your option) any later version.



    This program is distributed in the hope that it will be useful,

    but WITHOUT ANY WARRANTY; without even the implied warranty of

    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the

    GNU General Public License for more details.



    You should have received a copy of the GNU General Public License along

    with this program; if not, write to the Free Software Foundation, Inc.,

    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

________________________

 */

package com.apatar.db2.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.apatar.core.ApplicationData;
import com.apatar.core.ReadWriteXMLData;
import com.apatar.db2.LicenseProperties;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.ComponentBuilder;

public class JManageLicensesDialog extends JDialog {

	/**
	 * TODO remove storing license info from plugin folder to user home folder
	 */
	private static final long serialVersionUID = -1460850777423868654L;
	DefaultListModel listModel = new DefaultListModel();
	JList list = new JList(listModel);
	JButton add = new JButton("Add");
	JButton edit = new JButton("Edit");
	JButton delete = new JButton("Delete");
	JButton ok = new JButton("Ok");
	JButton cancel = new JButton("Cancel");

	JDialog owner;

	ArrayList<LicenseProperties> licenseProperties = new ArrayList<LicenseProperties>();

	HashMap<String, Element> licenseElements = new HashMap<String, Element>();

	Element runtimeElement;
	Document doc;

	public JManageLicensesDialog(JDialog owner) throws HeadlessException,
			JDOMException, IOException {
		super(owner);
		this.owner = owner;
		createDialog();
		addListeners();
		getLicenses();
		addToList();
	}

	private void getLicenses() throws JDOMException, IOException {
		String pathPrj = (ApplicationData.REPOSITORIES == null ? ""
				: ApplicationData.REPOSITORIES);
		System.out.println("DB2.JManageLicensesDialog.getLicenses: Path="
				+ pathPrj + "plugins/connectors/db2/plugin.xml");
		doc = ReadWriteXMLData.loadDocument(new File(pathPrj
				+ "plugins/connectors/db2/plugin.xml"));
		runtimeElement = doc.getRootElement().getChild("runtime");
		List childs = runtimeElement.getChildren("library");
		for (Object obj : childs) {
			Element elem = (Element) obj;
			String id = elem.getAttributeValue("id");
			if (id.indexOf("license_") == 0) {
				licenseElements.put(id.replaceFirst("license_", ""), elem);
				LicenseProperties lp = new LicenseProperties(id.substring(8),
						elem.getAttributeValue("path"));
				licenseProperties.add(lp);
			}
		}
	}

	private void addToList() {
		for (LicenseProperties lp : licenseProperties) {
			listModel.addElement(lp);
		}
	}

	private void createDialog() {
		setModal(true);
		setSize(350, 450);
		setTitle("Manage DB2 Licenses");
		GridBagLayout layout = new GridBagLayout();
		getContentPane().setLayout(layout);

		GridBagConstraints con = new GridBagConstraints();
		con.insets = new Insets(5, 10, 5, 10);
		con.gridwidth = GridBagConstraints.REMAINDER;
		con.fill = GridBagConstraints.BOTH;
		con.weightx = 2.0;
		ComponentBuilder.makeComponent(new JLabel("License List"), layout, con,
				getContentPane());
		con.weighty = 2.0;
		ComponentBuilder.makeComponent(new JScrollPane(list), layout, con,
				getContentPane());
		con.weighty = 0.0;
		ComponentBuilder.makeComponent(createButtonPanel(), layout, con,
				getContentPane());
		ComponentBuilder.makeComponent(createOkButtonPanel(), layout, con,
				getContentPane());
	}

	private JPanel createButtonPanel() {
		GridLayout buttonGrid = new GridLayout(1, 2, 10, 10);
		JPanel panelButton = new JPanel(buttonGrid);
		panelButton.add(add);
		panelButton.add(edit);
		panelButton.add(delete);
		return panelButton;
	}

	private JPanel createOkButtonPanel() {
		JPanel panel = new JPanel();
		BoxLayout layout = new BoxLayout(panel, BoxLayout.X_AXIS);
		panel.setLayout(layout);
		panel.add(Box.createHorizontalGlue());
		panel.add(ok);
		panel.add(Box.createHorizontalStrut(5));
		panel.add(cancel);
		panel.add(Box.createHorizontalStrut(5));
		return panel;
	}

	private void addListeners() {
		add.addActionListener(new AddActionListener(this));

		edit.addActionListener(new EditActionListener(this));

		delete.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				for (Object obj : list.getSelectedValues()) {
					if (JOptionPane
							.showConfirmDialog(
									ApatarUiMain.MAIN_FRAME,
									"Are you sure you want to delete license(s) from Apatar application package?",
									"Warning!", JOptionPane.YES_NO_OPTION,
									JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION) {
						return;
					}
					LicenseProperties lp = (LicenseProperties) obj;
					if (lp == null) {
						return;
					}
					listModel.removeElement(lp);
					lp.setDeleted(true);
				}
			}

		});

		ok.addActionListener(new OkActionListener(this));

		cancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				dispose();
			}

		});
	}

	private void deleteLicenseFile(String path) {
		File rf = new File(path);
		rf.delete();
	}

	public ArrayList<LicenseProperties> getLicenseProperties() {
		return licenseProperties;
	}

	private class OkActionListener implements ActionListener {

		JManageLicensesDialog dlg;

		public OkActionListener(JManageLicensesDialog dlg) {
			super();
			this.dlg = dlg;
		}

		public void actionPerformed(ActionEvent e) {
			String pathPrj = (ApplicationData.REPOSITORIES == null ? ""
					: ApplicationData.REPOSITORIES);
			for (LicenseProperties lp : licenseProperties) {
				String originalName = lp.getOriginalName();
				Element elem = licenseElements.get(originalName);
				if (elem != null) {
					if (lp.isDeleted()) {
						boolean removed = runtimeElement.removeContent(elem);
						if (removed) {
							deleteLicenseFile(pathPrj
									+ "plugins/connectors/db2/"
									+ elem.getAttributeValue("path"));
						}
					}
					elem.setAttribute("id", "license_" + lp.getName());
				} else {
					if (lp.isDeleted()) {
						continue;
					}
					elem = new Element("library");
					Element export = new Element("export");
					export.setAttribute("prefix", "*");
					elem.addContent(export);

					elem.setAttribute("id", "license_" + lp.getName());
					elem.setAttribute("path", "lib/license/"
							+ (new File(lp.getPath()).getName()));
					elem.setAttribute("type", "code");

					runtimeElement.addContent(elem);
				}
				if (lp.isUpdatedPath()) {
					File file = new File(lp.getPath());

					deleteLicenseFile(pathPrj + "plugins/connectors/db2/"
							+ elem.getAttributeValue("path"));
					FileInputStream fis = null;
					try {
						fis = new FileInputStream(file);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
					try {
						ApplicationData.createFile(new File(pathPrj
								+ "plugins/connectors/db2/lib/license/"
								+ file.getName()), fis);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					elem.setAttribute("path", "lib/license/" + file.getName());
					elem.setAttribute("type", "code");
				}
			}
			// TODO decide to use ReadWriteXMLDataUi instead of ReadWriteXMLData
			ReadWriteXMLData rwXMLdata = new ReadWriteXMLData();
			try {
				rwXMLdata.saveDocumentToFile(doc, new FileWriter(new File(
						pathPrj + "plugins/connectors/db2/plugin.xml")));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			dlg.dispose();
		}
	}

	public boolean isLicenseNameAlreadyExists(String name) {
		for (LicenseProperties lp : licenseProperties) {
			if (lp.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	private class AddActionListener implements ActionListener {

		JManageLicensesDialog dlg;

		public AddActionListener(JManageLicensesDialog dlg) {
			super();
			this.dlg = dlg;
		}

		public void actionPerformed(ActionEvent arg0) {
			LicenseProperties lp = JLicensePropertiesDialog.showDialog(dlg);
			if (lp != null) {
				lp.setUpdatedPath(true);
				licenseProperties.add(lp);
				listModel.addElement(lp);
			}
		}
	}

	private class EditActionListener implements ActionListener {

		JManageLicensesDialog dlg;

		public EditActionListener(JManageLicensesDialog dlg) {
			super();
			this.dlg = dlg;
		}

		public void actionPerformed(ActionEvent arg0) {
			LicenseProperties lp = (LicenseProperties) list.getSelectedValue();
			if (lp == null) {
				return;
			}
			JLicensePropertiesDialog.showDialog(dlg, lp);
			list.updateUI();
		}
	}

}
