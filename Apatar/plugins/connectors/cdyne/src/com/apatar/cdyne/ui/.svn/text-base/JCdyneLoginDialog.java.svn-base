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



package com.apatar.cdyne.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
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
import javax.swing.border.EmptyBorder;
import com.apatar.cdyne.Cdyne;
import com.apatar.cdyne.CdyneUtils;
import com.apatar.ui.MouseHyperLinkEvent;
import com.apatar.ui.ApatarUiMain;

public class JCdyneLoginDialog extends JDialog {
	JPasswordField licenseKeyField = new JPasswordField();

	JButton okButton = new JButton("Ok");
	JButton cancelButton = new JButton("Cancel");

	Cdyne node;

	String url;

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

	private JCdyneLoginDialog(Cdyne node, JDialog owner, String url) throws HeadlessException {
		this.node = node;
		this.url = url;
		createDialog();
		addListeners();
		setLocationRelativeTo(owner);
	}

	public JCdyneLoginDialog(Cdyne node, JFrame owner, String url) throws HeadlessException {
		super(owner);
		this.node = node;
		this.url = url;
		createDialog();
		addListeners();
		setLocationRelativeTo(owner);
	}

	private void createDialog() {
		setTitle("CDYNE Account Information");
		setLayout( new BorderLayout(5,5) );
		setModal(true);

		setSize(400, 110);

		JPanel contactPanel = new JPanel();
		contactPanel.setLayout(new BoxLayout(contactPanel, BoxLayout.Y_AXIS));

		JPanel passPanel = new JPanel();
		passPanel.setLayout( new BoxLayout(passPanel,
				BoxLayout.X_AXIS) );
		passPanel.add( new JLabel("License Key") );
		passPanel.add(Box.createHorizontalStrut(10));
		passPanel.add(licenseKeyField);



		JPanel link1Panel = new JPanel(new BorderLayout());

		JLabel apatarLinkLabel = new JLabel("<html><a href='" + url + "'>here</a></html>");
		JLabel apatarHomeLabel = new JLabel("Do not have CDYNE account? Click ");

		apatarLinkLabel.addMouseListener( new MouseHyperLinkEvent() );
		apatarLinkLabel.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
		apatarLinkLabel.setToolTipText(url);

		link1Panel.add( apatarHomeLabel, BorderLayout.WEST );
		link1Panel.add( apatarLinkLabel, BorderLayout.CENTER );


		contactPanel.add( passPanel );
		contactPanel.add(Box.createVerticalStrut(10));
		contactPanel.add( link1Panel );

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

		licenseKeyField.setText(node.getLicenseKey());
	//	userNameField.setText(node.getUserName());
	}

	private void addListeners() {
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				String key = new String(licenseKeyField.getPassword());
				int result;
				try {
					if (key.equals("")) {
						JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,"Key field cannot be empty");
						return;
					}
					result = CdyneUtils.licenseCheck(key);
					JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME, CdyneUtils.getTextMessage(result));
					if (result >= 0) {
						node.setLicenseKey(key);
						option = OK_OPTION;
						setVisible(false);
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME, e.getMessage());
					e.printStackTrace();
				}
			}

		});
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				option = CANCEL_OPTION;
				setVisible(false);
			}

		});
	}


	public static boolean showDialog(Cdyne node, JDialog owner, String url) {
		JCdyneLoginDialog dlg = new JCdyneLoginDialog(node, owner, url);
		return showDialog(dlg);
	}

	public static boolean showDialog(Cdyne node, JFrame owner, String url) {
		JCdyneLoginDialog dlg = new JCdyneLoginDialog(node, owner, url);
		return showDialog(dlg);
	}

	private static boolean showDialog(JCdyneLoginDialog dlg) {
		dlg.setVisible(true);
		return dlg.option == OK_OPTION;
	}
}

