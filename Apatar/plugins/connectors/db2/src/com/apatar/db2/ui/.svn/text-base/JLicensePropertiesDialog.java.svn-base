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
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.apatar.db2.LicenseProperties;
import com.apatar.ui.ApatarFileFilter;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.ComponentBuilder;

public class JLicensePropertiesDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9073714509345197318L;
	public static int OK_OPTION = 1;
	public static int CANCEL_OPTION = 0;

	int option = CANCEL_OPTION;

	JTextField name = new JTextField();
	JTextField selectedFile = new JTextField();

	JButton okButton = new JButton("Ok");
	JButton cancelButton = new JButton("Cancel");

	JButton browseButton = new JButton("Browse");

	boolean browsed = false;

	JManageLicensesDialog owner;

	public JLicensePropertiesDialog(JManageLicensesDialog owner)
			throws HeadlessException {
		this(owner, null, null);
		this.owner = owner;
	}

	public JLicensePropertiesDialog(JManageLicensesDialog owner,
			String licenseName, String licensePath) {
		super(owner);
		this.owner = owner;
		name.setText(licenseName);
		selectedFile.setText(licensePath);
		createDialog();
	}

	private void createDialog() {
		setModal(true);
		setSize(450, 120);
		setResizable(false);
		setTitle("License Properties");
		GridBagLayout layout = new GridBagLayout();
		getContentPane().setLayout(layout);

		GridBagConstraints con = new GridBagConstraints();
		con.insets = new Insets(5, 10, 5, 10);
		con.fill = GridBagConstraints.BOTH;
		ComponentBuilder.makeComponent(new JLabel("License name:"), layout,
				con, getContentPane());
		con.weightx = 2.0;
		con.gridwidth = GridBagConstraints.REMAINDER;
		ComponentBuilder.makeComponent(name, layout, con, getContentPane());
		con.weightx = 0.0;
		con.gridwidth = 1;
		ComponentBuilder.makeComponent(new JLabel("License Path:"), layout,
				con, getContentPane());
		con.weightx = 2.0;
		ComponentBuilder.makeComponent(selectedFile, layout, con,
				getContentPane());
		con.weightx = 0.0;
		con.gridwidth = GridBagConstraints.REMAINDER;
		ComponentBuilder.makeComponent(browseButton, layout, con,
				getContentPane());
		con.weightx = 2.0;
		ComponentBuilder.makeComponent(createButtonPanel(), layout, con,
				getContentPane());

		con.weighty = 2.0;
		ComponentBuilder.makeComponent(new JPanel(), layout, con,
				getContentPane());

		browseButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				final JFileChooser fileChooser = new JFileChooser(System
						.getProperty("user.dir"));
				fileChooser.setMultiSelectionEnabled(false);
				fileChooser.setFileFilter(new ApatarFileFilter("jar"));

				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File fileSrc = fileChooser.getSelectedFile();
					selectedFile.setText(fileSrc.getPath());
					browsed = true;
				}
			}

		});
	}

	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(okButton);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(cancelButton);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.setBorder(new EmptyBorder(0, 0, 5, 0));

		okButton.setPreferredSize(cancelButton.getPreferredSize());
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String lName = name.getText();
				if (lName == null || lName.equals("")) {
					JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
							"Please enter License Name.");
					return;
				}

				if (owner.isLicenseNameAlreadyExists(lName)) {
					JOptionPane
							.showMessageDialog(
									ApatarUiMain.MAIN_FRAME,
									"License name already exists. Please enter a different license name to proceed.");
					return;
				}

				String lPath = selectedFile.getText();
				if (lPath == null || lPath.equals("")) {
					JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
							"Please enter License Path.");
					return;
				}

				option = OK_OPTION;
				setVisible(false);
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				option = CANCEL_OPTION;
				setVisible(false);
			}
		});

		return buttonPanel;
	}

	public LicenseProperties getLicenseProperties() {
		String lName = name.getText();
		String lPath = selectedFile.getText();
		return new LicenseProperties(lName, lPath);
	}

	public static LicenseProperties showDialog(JManageLicensesDialog owner) {
		JLicensePropertiesDialog dlg = new JLicensePropertiesDialog(owner);
		dlg.setVisible(true);
		dlg.dispose();
		return dlg.option == OK_OPTION ? dlg.getLicenseProperties() : null;
	}

	public static void showDialog(JManageLicensesDialog owner,
			LicenseProperties lp) {
		JLicensePropertiesDialog dlg = new JLicensePropertiesDialog(owner, lp
				.getName(), lp.getPath());
		dlg.setVisible(true);
		dlg.dispose();
		if (dlg.option == CANCEL_OPTION) {
			return;
		}
		lp.setName(dlg.name.getText());
		if (dlg.browsed) {
			lp.setPath(dlg.selectedFile.getText());
		}
	}
}
