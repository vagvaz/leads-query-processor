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

package com.apatar.rss.ui;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.apatar.ui.ComponentBuilder;
import com.apatar.ui.JDefaultContextMenu;
import com.apatar.ui.MouseHyperLinkEvent;
import com.apatar.ui.UiUtils;

public class JPublishPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5248317765349889611L;
	JCheckBox publish = new JCheckBox("Publish RSS feed to ApatarForge.org");
	JTextField username = new JTextField();
	JPasswordField password = new JPasswordField();

	public JPublishPanel() {
		super();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		setLayout(gridbag);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 5, 5);

		JPanel panelLogin = new JPanel();
		panelLogin.setBorder(new EmptyBorder(10, 10, 0, 10));
		panelLogin.setLayout(new BoxLayout(panelLogin, BoxLayout.X_AXIS));
		panelLogin.add(new JLabel("User Name"));
		panelLogin.add(Box.createHorizontalStrut(5));
		panelLogin.add(username);
		username.setComponentPopupMenu(new JDefaultContextMenu(username));
		panelLogin.add(Box.createHorizontalStrut(5));
		panelLogin.add(new JLabel("Password"));
		panelLogin.add(Box.createHorizontalStrut(5));
		panelLogin.add(password);

		JPanel panel = new JPanel();
		BoxLayout layout = new BoxLayout(panel, BoxLayout.X_AXIS);
		panel.setLayout(layout);

		ComponentBuilder.makeComponent(panel, gridbag, c, this);
		ComponentBuilder.makeComponent(publish, gridbag, c, this);
		ComponentBuilder.makeComponent(panelLogin, gridbag, c, this);
		JTextArea comment = new JTextArea(
				"Please note that to publish RSS feed to ApatarForge.org,you need to be a registered user either at Apatar DataMap Repository, or Apatar Community Forum. Registration is free and takes less than 5 minutes. If you don#t have an account at ApatarForge.org, take a moment to register here:");
		comment.setFont(UiUtils.NORMAL_SIZE_12_FONT);

		JLabel apatarforgeLinkLabel = new JLabel(
				"<html><a href=>http://www.apatarforge.org/index.php?option=com_comprofiler&task=registers</a></html>");

		apatarforgeLinkLabel.setFont(UiUtils.NORMAL_SIZE_12_FONT);
		apatarforgeLinkLabel.addMouseListener(new MouseHyperLinkEvent());
		apatarforgeLinkLabel.setCursor(Cursor
				.getPredefinedCursor(Cursor.HAND_CURSOR));

		comment.setLineWrap(true);
		comment.setWrapStyleWord(true);
		comment.setBackground(getBackground());
		comment.setEditable(false);
		ComponentBuilder.makeComponent(comment, gridbag, c, this);
		ComponentBuilder.makeComponent(apatarforgeLinkLabel, gridbag, c, this);
		c.weighty = 2.0;
		ComponentBuilder.makeComponent(new JPanel(), gridbag, c, this);

		publish.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (publish.isSelected()) {
					username.setEnabled(true);
					password.setEnabled(true);
				} else {
					username.setEnabled(false);
					password.setEnabled(false);
				}
			}

		});
	}

	public void setUserName(String name) {
		username.setText(name);
	}

	public String getUserName() {
		return username.getText();
	}

	public void setPassword(String pass) {
		password.setText(pass);
	}

	public String getPassword() {
		return new String(password.getPassword());
	}

	public void setPublish(boolean arg) {
		publish.setSelected(arg);
	}

	public boolean isPublish() {
		return publish.isSelected();
	}
}
