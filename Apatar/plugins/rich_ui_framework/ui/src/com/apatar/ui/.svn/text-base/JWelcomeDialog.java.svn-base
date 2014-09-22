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

package com.apatar.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class JWelcomeDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	int option = CANCEL_OPTION;

	public static final int CANCEL_OPTION = 0;
	public static final int CREATE_NEW_OPTION = 1;
	public static final int LOAD_OPTION = 2;
	public static final int CREATE_RSS_FEED_OPTION = 3;

	JButton createNew = new JButton("Create new DataMap");
	JButton load = new JButton("Load DataMap");

	JButton bOk = new JButton("Ok");
	JButton bCancel = new JButton("Cancel");

	public JWelcomeDialog(Frame owner, boolean modal) throws HeadlessException {
		super(owner, modal);
		setSize(350, 130);
		setResizable(false);
		createDialog();
		addListeners();
		setTitle("Welcome to Apatar Open Source Data Integration");
		setLocationRelativeTo(owner);
	}

	public JWelcomeDialog(Frame owner) throws HeadlessException {
		this(owner, true);
	}

	private void createDialog() {
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout(20, 20));

		((JPanel) contentPane).setBorder(BorderFactory.createEmptyBorder(10,
				10, 10, 10));

		JPanel selectPanel = new JPanel();
		selectPanel.setLayout(new GridLayout(2, 1, 10, 10));
		selectPanel.add(createNew);
		selectPanel.add(load);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(selectPanel);
		panel.add(Box.createHorizontalStrut(25));
		panel.add(new JLabel(UiUtils.APATAR_LOGO_ICON));
		panel.add(Box.createHorizontalStrut(10));

		contentPane.add(panel, BorderLayout.CENTER);
	}

	private void addListeners() {
		bOk.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (createNew.isSelected()) {
					option = CREATE_NEW_OPTION;
					setVisible(false);
					return;
				}
				if (load.isSelected()) {
					option = LOAD_OPTION;
					setVisible(false);
					return;
				}

			}

		});

		createNew.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				option = CREATE_NEW_OPTION;
				setVisible(false);
			}

		});
		load.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				option = LOAD_OPTION;
				setVisible(false);
			}
		});
		bCancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				option = CANCEL_OPTION;
				setVisible(false);
			}

		});
	}

	public int selectOption() {
		setVisible(true);
		return option;
	}

}
