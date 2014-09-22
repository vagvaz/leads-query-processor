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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.apatar.core.ApatarHttpClient;

public class JParametersProxyPanel extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 2838814935512186237L;
	JCheckBox isProxy = new JCheckBox("Set up Proxy Server");
	JTextField host = new JTextField();
	JTextField port = new JTextField();
	JTextField proxyUserName = new JTextField();
	JPasswordField proxyPassword = new JPasswordField();

	ApatarHttpClient client;

	public JParametersProxyPanel(ApatarHttpClient client) {
		super();
		this.client = client;
		createProxyPanel();

		if (client != null) {
			isProxy.setSelected(client.isUseProxy());
			host.setText(client.getHost());
			port.setText("" + client.getPort());
			proxyUserName.setText(client.getUserName());
			proxyPassword.setText(client.getPassword());
		}
	}

	private void createProxyPanel() {

		GridBagLayout layout = new GridBagLayout();
		setBorder(BorderFactory.createTitledBorder(""));

		GridBagConstraints constraintValue = new GridBagConstraints();
		GridBagConstraints constraintName = new GridBagConstraints();

		Insets insets = new Insets(3, 5, 5, 5);

		constraintName.anchor = GridBagConstraints.EAST;
		constraintName.gridwidth = 1;
		constraintName.insets = insets;
		constraintName.fill = GridBagConstraints.NONE;
		constraintName.weightx = 0.0;

		constraintValue.anchor = GridBagConstraints.WEST;
		// constraintValue.gridwidth=GridBagConstraints.REMAINDER;
		constraintValue.insets = insets;
		constraintValue.fill = GridBagConstraints.BOTH;
		constraintValue.weightx = 1.0;

		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		setLayout(new BorderLayout());

		JPanel workPanel = new JPanel();

		workPanel.setLayout(new BoxLayout(workPanel, BoxLayout.Y_AXIS));
		workPanel.add(isProxy);

		JPanel isProxyPanel = new JPanel(new BorderLayout());
		isProxyPanel.add(isProxy, BorderLayout.WEST);
		workPanel.add(isProxyPanel);

		JPanel settingsPanel = new JPanel(new GridLayout(2, 4, 15, 15));

		settingsPanel.setLayout(layout);

		ComponentBuilder.makeComponent(new JLabel("Host:"), layout,
				constraintName, settingsPanel);
		ComponentBuilder.makeComponent(host, layout, constraintValue,
				settingsPanel);
		constraintValue.gridwidth = GridBagConstraints.REMAINDER;
		ComponentBuilder.makeComponent(new JLabel("Port:"), layout,
				constraintName, settingsPanel);
		ComponentBuilder.makeComponent(port, layout, constraintValue,
				settingsPanel);
		constraintValue.gridwidth = 1;
		ComponentBuilder.makeComponent(new JLabel("User:"), layout,
				constraintName, settingsPanel);
		ComponentBuilder.makeComponent(proxyUserName, layout, constraintValue,
				settingsPanel);
		constraintValue.gridwidth = GridBagConstraints.REMAINDER;
		ComponentBuilder.makeComponent(new JLabel("Password:"), layout,
				constraintName, settingsPanel);
		ComponentBuilder.makeComponent(proxyPassword, layout, constraintValue,
				settingsPanel);

		workPanel.add(settingsPanel);

		add(workPanel, BorderLayout.NORTH);
		add(new JPanel(), BorderLayout.CENTER);

	}

	public JCheckBox getIsProxy() {
		return isProxy;
	}

	public JTextField getHost() {
		return host;
	}

	public JTextField getPort() {
		return port;
	}

	public JTextField getProxyUserName() {
		return proxyUserName;
	}

	public JPasswordField getProxyPassword() {
		return proxyPassword;
	}

	public ApatarHttpClient setParametersProxy() {
		client.setUseProxy(getIsProxy().isSelected());
		client.setParametersProxy(getHost().getText(), Integer.parseInt((""
				.equals(getPort().getText()) ? "0" : getPort().getText())),
				getProxyUserName().getText(), new String(getProxyPassword()
						.getPassword()));
		if (client.isUseProxy()) {
			System.setProperty("http.proxyHost", client.getHost());
			System.setProperty("http.proxyPort", String.valueOf(client
					.getPort()));
			String proxyUser = client.getUserName();
			if (proxyUser != null) {
				System.setProperty("http.proxyUser", proxyUser);
				String proxyPassword = client.getPassword();
				if (proxyPassword != null) {
					System.setProperty("http.proxyPassword", proxyPassword);
				}
			}
		} else {
			System.setProperty("http.proxyHost", "");
			System.setProperty("http.proxyPort", "");
			System.setProperty("http.proxyUser", "");
			System.setProperty("http.proxyPassword", "");
		}

		return client;
	}

}
