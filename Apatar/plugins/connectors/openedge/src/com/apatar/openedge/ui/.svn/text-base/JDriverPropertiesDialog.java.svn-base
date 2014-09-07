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

import com.apatar.openedge.DriverProperties;
import com.apatar.ui.ApatarFileFilter;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.ComponentBuilder;

public class JDriverPropertiesDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9073714509345197318L;
	public static int OK_OPTION = 1;
	public static int CANCEL_OPTION = 0;

	int option = CANCEL_OPTION;

	JTextField selectedFile = new JTextField();

	JButton okButton = new JButton("Ok");
	JButton cancelButton = new JButton("Cancel");

	JButton browseButton = new JButton("Browse");

	boolean browsed = false;

	JManageDriversDialog owner;

	public JDriverPropertiesDialog(JManageDriversDialog owner)
			throws HeadlessException {
		this(owner, null);
		this.owner = owner;
	}

	public JDriverPropertiesDialog(JManageDriversDialog owner,
			String driverPath) {
		super(owner);
		this.owner = owner;
		selectedFile.setText(driverPath);
		createDialog();
	}

	private void createDialog() {
		setModal(true);
		setSize(450, 120);
		setResizable(false);
		setTitle("Driver Properties");
		GridBagLayout layout = new GridBagLayout();
		getContentPane().setLayout(layout);

		GridBagConstraints con = new GridBagConstraints();
		con.insets = new Insets(5, 10, 5, 10);
		con.fill = GridBagConstraints.BOTH;

		ComponentBuilder.makeComponent(new JLabel("Driver Path:"), layout,
				con, getContentPane());
		con.weightx = 2.0;
		ComponentBuilder.makeComponent(selectedFile, layout, con, getContentPane());
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
				final JFileChooser fileChooser = new JFileChooser();
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
				String lPath = selectedFile.getText();
				if (lPath == null || lPath.equals("")) {
					JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
							"Please enter Driver Path.");
					return;
				}
				
				if (owner.isDriverNameAlreadyExists(lPath)) {
					JOptionPane
							.showMessageDialog(
									ApatarUiMain.MAIN_FRAME,
									"Driver already exists. Please select a different driver to proceed.");
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

	public DriverProperties getDriverProperties() {
		String lPath = selectedFile.getText();
		
		return new DriverProperties(lPath);
	}

	public static DriverProperties showDialog(JManageDriversDialog owner) {
		JDriverPropertiesDialog dlg = new JDriverPropertiesDialog(owner);
		dlg.setVisible(true);
		dlg.dispose();
		return dlg.option == OK_OPTION ? dlg.getDriverProperties() : null;
	}

	public static void showDialog(JManageDriversDialog owner,
			DriverProperties lp) {
		JDriverPropertiesDialog dlg = new JDriverPropertiesDialog(owner, lp.getPath());
		dlg.setVisible(true);
		dlg.dispose();
		if (dlg.option == CANCEL_OPTION) {
			return;
		}
		if (dlg.browsed) {
			lp.setPath(dlg.selectedFile.getText());
		}
	}
}
