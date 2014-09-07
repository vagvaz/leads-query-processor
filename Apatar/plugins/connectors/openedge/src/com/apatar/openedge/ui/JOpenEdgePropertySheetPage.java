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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.plaf.PanelUI;

import org.jdom.JDOMException;

import propertysheet.JPropertyNameDialog;
import propertysheet.JPropertySheetPage;

import com.apatar.core.ApplicationData;
import com.apatar.core.ProjectData;

public class JOpenEdgePropertySheetPage extends JPropertySheetPage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5983064169325191944L;

	public JOpenEdgePropertySheetPage(JDialog ownerDialog) {
		super(ownerDialog);
		owner = ownerDialog;
	}

	@Override
	protected void createComponent() {
		setLayout(new BorderLayout(5, 5));
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout(5, 5));
		mainPanel.add(existentProjectDataList, BorderLayout.NORTH);

		buttonNew = new JButton("New Connection");
		buttonNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {

					JPropertyNameDialog dlg = new JPropertyNameDialog(owner,
							"Enter property names", true);
					dlg.setVisible(true);
					if (dlg.isOk()) {
						ProjectData prjData = new ProjectData(dataType,
								dataSubType, "", classType.newInstance());
						prjData.setName(dlg.getNameConnector());
						ApplicationData.getProject().addProjectData(prjData);

						existentProjectDataList.addItem(prjData);
						existentProjectDataList.setSelectedItem(prjData);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		buttonDelete = new JButton("Delete Connection");
		buttonDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);

				// shouldn't delete the last one
				// !!! remove this condtion and ask Valery Shikunets to fix all
				// the bugs
				// about this (2 - 3 days of bugs)
				if (existentProjectDataList.getItemCount() > 1) {
					ApplicationData.getProject().removeProjectData(
							currentProjectData);
				}
			}
		});

		GridLayout buttonGrid = new GridLayout(1, 2, 10, 10);
		JPanel panelButton = new JPanel(buttonGrid);
		panelButton.add(buttonNew);
		panelButton.add(buttonDelete);
		mainPanel.add(panelButton, BorderLayout.SOUTH);

		mainPanel.add(panel, BorderLayout.CENTER);

		add(mainPanel, BorderLayout.CENTER);

		JPanel selectPanel = new JPanel(new GridLayout(1, 1));
		JButton browseButton = new JButton("Manage Driver Files");
		selectPanel.add(browseButton);
		add(selectPanel, BorderLayout.SOUTH);

		browseButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				/*
				 * JFileChooser fileChooser = new JFileChooser();
				 * fileChooser.setMultiSelectionEnabled(false);
				 * fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
				 * fileChooser.showOpenDialog(ApatarUiMain.MAIN_FRAME); File
				 * file = fileChooser.getSelectedFile(); File copyFile = new
				 * File("./plugins/com.apatar.db2/lib/" + file.getName()); try {
				 * ApplicationData.createFile(copyFile, new
				 * FileInputStream(file)); } catch (FileNotFoundException e1) {
				 * e1.printStackTrace(); } catch (IOException e1) {
				 * e1.printStackTrace(); }
				 */
				JManageDriversDialog dlg;
				try {
					dlg = new JManageDriversDialog(owner);
					dlg.setVisible(true);
				} catch (HeadlessException e1) {
					e1.printStackTrace();
				} catch (JDOMException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		// is called when combob box item is selected (100% of Valery Shikunets)
		existentProjectDataList.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				Object item = existentProjectDataList.getSelectedItem();

				if (item instanceof ProjectData) {
					ProjectData prjData = (ProjectData) existentProjectDataList
							.getSelectedItem();
					currentProjectData = prjData;
					panel.updateBean(prjData.getData());
				}
			}
		});

		existentProjectDataList.setSelectedItem(currentProjectData);
		panel.updateBean(currentProjectData.getData());
	}

	@Override
	public ProjectData init(long dataId, Class classType, String type,
			String subtype) throws Exception {
		// TODO Auto-generated method stub
		
		
		return super.init(dataId, classType, type, subtype);
	}

}
