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

package com.apatar.email.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.ParseException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

public class JEmailSettingPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final JCheckBox isDeleteAll = new JCheckBox(
			"Remove messages from the server");
	private JTextField count = null;

	public JEmailSettingPanel() {
		super();
		createPanel();
	}

	private void createPanel() {
		setLayout(new BorderLayout(5, 5));

		JPanel modePanel = new JPanel();
		modePanel.setLayout(new BoxLayout(modePanel, BoxLayout.Y_AXIS));

		JPanel countPanel = new JPanel();
		countPanel.setLayout(new BoxLayout(countPanel, BoxLayout.X_AXIS));
		countPanel
				.add(new JLabel("Maximum number of attached files to process"));
		countPanel.add(Box.createHorizontalStrut(5));

		MaskFormatter format = null;
		try {
			format = new MaskFormatter("##");
			format.setPlaceholderCharacter(' ');
			// format.setValueClass(Integer.class);
			// format.setOverwriteMode( true );
			count = new JTextField();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		// format.setAllowsInvalid(false);

		count.setColumns(2);
		count.setMaximumSize(new Dimension(60, 20));
		count.setMinimumSize(new Dimension(60, 20));
		count.setPreferredSize(new Dimension(60, 20));

		countPanel.add(count);
		countPanel.add(Box.createHorizontalGlue());

		modePanel.add(countPanel);
		modePanel.add(Box.createVerticalStrut(10));

		JPanel deletePanel = new JPanel();
		deletePanel.setLayout(new BoxLayout(deletePanel, BoxLayout.X_AXIS));
		deletePanel.add(isDeleteAll);
		deletePanel.add(Box.createHorizontalGlue());

		modePanel.add(deletePanel);
		modePanel.add(Box.createVerticalGlue());

		add(modePanel, BorderLayout.NORTH);
	}

	public boolean isDeleteAll() {
		return isDeleteAll.isSelected();
	}

	public void setDeleteAll(boolean b) {
		isDeleteAll.setSelected(b);
	}

	public int getCountAttachedFiles() throws NumberFormatException {
		try {
			return Integer.parseInt(count.getText().trim());
		} catch (NumberFormatException e) {
			throw e;
		}
	}

	public void setCountAttachedFiles(int num) {
		count.setText("" + num);
	}

}
