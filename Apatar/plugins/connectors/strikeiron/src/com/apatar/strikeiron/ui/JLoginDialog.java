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



package com.apatar.strikeiron.ui;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.apatar.strikeiron.Strikeiron;
import com.apatar.ui.ApatarUiMain;

public class JLoginDialog extends JDialog {
	JTextField userNameField = new JTextField();
	JPasswordField passwordField = new JPasswordField();

	JButton okButton = new JButton("Ok");
	JButton cancelButton = new JButton("Cancel");

	Strikeiron node;

	public static int CANCEL_OPTION = 0;
	public static int OK_OPTION = 1;

	public int option = CANCEL_OPTION;

	public boolean isLoginSuccessful() {
		if (option == OK_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	public JLoginDialog(Strikeiron node, JDialog owner) throws HeadlessException {
		this.node = node;
		createDialog();
		addListeners();
		setLocationRelativeTo(owner);
	}

	public JLoginDialog(Strikeiron node, JFrame owner) throws HeadlessException {
		super(owner);
		this.node = node;
		createDialog();
		addListeners();
		setLocationRelativeTo(owner);
	}

	private void createDialog() {
		setTitle("StrikeIron Account Information");
		setLayout( new BorderLayout(5,5) );
		setModal(true);

		setSize(400, 110);

		JPanel contactPanel = new JPanel();
		contactPanel.setLayout(new BoxLayout(contactPanel, BoxLayout.Y_AXIS));

		JPanel namePanel = new JPanel();
		namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
		namePanel.add( new JLabel("User Name") );
		namePanel.add(Box.createHorizontalStrut(5));
		namePanel.add( userNameField );

		JPanel passPanel = new JPanel();
		passPanel.setLayout( new BoxLayout(passPanel,
				BoxLayout.X_AXIS) );
		passPanel.add( new JLabel("Password") );
		passPanel.add(Box.createHorizontalStrut(10));
		passPanel.add( passwordField );

		contactPanel.add( namePanel );
		contactPanel.add( Box.createVerticalStrut(5) );
		contactPanel.add( passPanel );

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(okButton);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(cancelButton);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.setBorder( new EmptyBorder(0, 0, 5, 0) );

		getContentPane().add(contactPanel, BorderLayout.NORTH);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		passwordField.setText(node.getPassword());
		userNameField.setText(node.getUserName());
	}

	private void addListeners() {
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				String password = passwordField.getPassword().toString();
				String userName = userNameField.getText();

				if (userName.equals("")) {
					JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME, "Username should not be empty.");
					option = CANCEL_OPTION;
					return;
				} else {
					node.setPassword(password);
					node.setUserName(userName);
					setVisible(false);
					option = OK_OPTION;
				}
			}
		});
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				option = CANCEL_OPTION;
			}
		});
	}
}

