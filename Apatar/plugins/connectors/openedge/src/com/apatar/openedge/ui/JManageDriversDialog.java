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

package com.apatar.openedge.ui;

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
import com.apatar.openedge.DriverProperties;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.ComponentBuilder;

public class JManageDriversDialog extends JDialog {

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

	ArrayList<DriverProperties> driverProperties = new ArrayList<DriverProperties>();

	HashMap<String, Element> driverElements = new HashMap<String, Element>();

	Element runtimeElement;
	Document doc;

	public JManageDriversDialog(JDialog owner) throws HeadlessException,
			JDOMException, IOException {
		super(owner);
		this.owner = owner;
		createDialog();
		addListeners();
		getDrivers();
		addToList();
	}

	private void getDrivers() throws JDOMException, IOException {
		String pathPrj = (ApplicationData.REPOSITORIES == null ? ""
				: ApplicationData.REPOSITORIES);
		System.out.println("OpenEdge.JManageDriversDialog.getDrivers: Path="
				+ pathPrj + "plugins/connectors/openedge/plugin.xml");
		doc = ReadWriteXMLData.loadDocument(new File(pathPrj
				+ "plugins/connectors/openedge/plugin.xml"));
		runtimeElement = doc.getRootElement().getChild("runtime");
		List childs = runtimeElement.getChildren("library");
		for (Object obj : childs) {
			Element elem = (Element) obj;
			String id = elem.getAttributeValue("id");
			if (id.indexOf("driver_") == 0) {
				driverElements.put(id.replaceFirst("driver_", ""), elem);
				DriverProperties lp = new DriverProperties(elem.getAttributeValue("path"));
				driverProperties.add(lp);
			}
		}
	}

	private void addToList() {
		for (DriverProperties lp : driverProperties) {
			listModel.addElement(lp);
		}
	}

	private void createDialog() {
		setModal(true);
		setSize(350, 450);
		setTitle("Manage OpenEdge Drivers");
		GridBagLayout layout = new GridBagLayout();
		getContentPane().setLayout(layout);

		GridBagConstraints con = new GridBagConstraints();
		con.insets = new Insets(10, 10, 10, 10);
		con.gridwidth = GridBagConstraints.REMAINDER;
		con.fill = GridBagConstraints.BOTH;
		con.weightx = 2.0;
		ComponentBuilder.makeComponent(new JLabel("Drivers List"), layout, con,
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
									"Are you sure you want to delete driver(s) from Apatar application package?",
									"Warning!", JOptionPane.YES_NO_OPTION,
									JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION) {
						return;
					}
					DriverProperties lp = (DriverProperties) obj;
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

	private void deleteDriverFile(String path) {
		File rf = new File(path);
		rf.delete();
	}

	public ArrayList<DriverProperties> getDriverProperties() {
		return driverProperties;
	}

	private class OkActionListener implements ActionListener {

		JManageDriversDialog dlg;

		public OkActionListener(JManageDriversDialog dlg) {
			super();
			this.dlg = dlg;
		}

		public void actionPerformed(ActionEvent e) {
			String pathPrj = (ApplicationData.REPOSITORIES == null ? ""
					: ApplicationData.REPOSITORIES);
			for (DriverProperties lp : driverProperties) {
				String originalName = lp.getOriginalName();
				Element elem = driverElements.get(originalName);
				if (elem != null) {
					if (lp.isDeleted()) {
						boolean removed = runtimeElement.removeContent(elem);
						if (removed) {
							deleteDriverFile(pathPrj
									+ "plugins/connectors/openedge/"
									+ elem.getAttributeValue("path"));
						}
					}
					elem.setAttribute("id", "driver_" + lp.getName());
				} else {
					if (lp.isDeleted()) {
						continue;
					}
					elem = new Element("library");
					Element export = new Element("export");
					export.setAttribute("prefix", "*");
					elem.addContent(export);

					elem.setAttribute("id", "driver_" + lp.getName());
					elem.setAttribute("path", "lib/"
							+ (new File(lp.getPath()).getName()));
					elem.setAttribute("type", "code");

					runtimeElement.addContent(elem);
				}
				if (lp.isUpdatedPath()) {
					File file = new File(lp.getPath());

					deleteDriverFile(pathPrj + "plugins/connectors/openedge/"
							+ elem.getAttributeValue("path"));
					FileInputStream fis = null;
					try {
						fis = new FileInputStream(file);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
					try {
						ApplicationData.createFile(new File(pathPrj
								+ "plugins/connectors/openedge/lib/"
								+ file.getName()), fis);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					elem.setAttribute("path", "lib/" + file.getName());
					elem.setAttribute("type", "code");
				}
			}
			// TODO decide to use ReadWriteXMLDataUi instead of ReadWriteXMLData
			ReadWriteXMLData rwXMLdata = new ReadWriteXMLData();
			try {
				rwXMLdata.saveDocumentToFile(doc, new FileWriter(new File(
						pathPrj + "plugins/connectors/openedge/plugin.xml")));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			dlg.dispose();
		}
	}

	public boolean isDriverNameAlreadyExists(String name) {
		for (DriverProperties lp : driverProperties) {
			if (lp.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	private class AddActionListener implements ActionListener {

		JManageDriversDialog dlg;

		public AddActionListener(JManageDriversDialog dlg) {
			super();
			this.dlg = dlg;
		}

		public void actionPerformed(ActionEvent arg0) {
			DriverProperties lp = JDriverPropertiesDialog.showDialog(dlg);
			if (lp != null) {
				lp.setUpdatedPath(true);
				driverProperties.add(lp);
				listModel.addElement(lp);
			}
		}
	}

	private class EditActionListener implements ActionListener {

		JManageDriversDialog dlg;

		public EditActionListener(JManageDriversDialog dlg) {
			super();
			this.dlg = dlg;
		}

		public void actionPerformed(ActionEvent arg0) {
			DriverProperties lp = (DriverProperties) list.getSelectedValue();
			if (lp == null) {
				return;
			}
			JDriverPropertiesDialog.showDialog(dlg, lp);
			list.updateUI();
		}
	}

}
