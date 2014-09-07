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
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import com.apatar.core.ApplicationData;

public class JOptionsDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5282270715282138645L;
	JTabbedPane tabbedPane = new JTabbedPane();
	JButton ok = new JButton("Ok");
	JButton cancel = new JButton("Cancel");

	JParametersProxyPanel parametersProxyPanel;
	JParametersDateAndTimePanel parametersDateAndTimePanel;
	private JDebugOptionsPanel parametersDebugOptions;

	public JOptionsDialog(Frame owner) throws HeadlessException {
		super(owner);
		setModal(true);

		setTitle("Options");

		setSize(500, 500);
		parametersProxyPanel = new JParametersProxyPanel(
				ApplicationData.httpClient);
		parametersDebugOptions = new JDebugOptionsPanel(
				ApplicationData.isClearLogsBeforeRun);
		parametersDateAndTimePanel = new JParametersDateAndTimePanel();
		setLayout(new BorderLayout());
		getContentPane().add(tabbedPane);
		tabbedPane.addTab("Proxy", parametersProxyPanel);
		tabbedPane.addTab("Date and Time", parametersDateAndTimePanel);
		tabbedPane.addTab("Debug Options", parametersDebugOptions);
		add(tabbedPane, BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);

		addListeners();
	}

	private JPanel createButtonPanel() {

		ok.setMinimumSize(cancel.getSize());

		JPanel buttonPanel = new JPanel();
		JSeparator separator = new JSeparator();
		Box buttonBox = new Box(BoxLayout.X_AXIS);

		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.add(separator, BorderLayout.NORTH);

		buttonBox.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));
		buttonBox.add(ok);
		buttonBox.add(Box.createHorizontalStrut(10));
		buttonBox.add(cancel);

		buttonPanel.add(buttonBox, java.awt.BorderLayout.EAST);

		return buttonPanel;
	}

	private void addListeners() {
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				parametersProxyPanel.setParametersProxy();
				if (!parametersDateAndTimePanel.run()) {
					return;
				}
				ApplicationData.isClearLogsBeforeRun = parametersDebugOptions.isClearLogsBeforeRun
						.isSelected();
				setVisible(false);
			}
		});
	}

}
