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

package com.apatar.ui.registration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.apatar.core.ApatarException;
import com.apatar.core.ApatarHttpClient;
import com.apatar.core.ApatarRegExp;
import com.apatar.core.ApplicationData;
import com.apatar.core.CoreUtils;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.ComponentBuilder;
import com.apatar.ui.JParametersProxyPanel;
import com.apatar.ui.MouseHyperLinkEvent;

public class JRegistrationDialog extends JDialog {

	int option = CANCEL_OPTION;
	public static final int CANCEL_OPTION = 0;
	public static final int OK_OPTION = 1;

	private final ApatarHttpClient client = new ApatarHttpClient();

	JRadioButton newUser = new JRadioButton("New User");
	JRadioButton existingUser = new JRadioButton("Existing User", true);

	JTextField exUserName = new JTextField();
	JPasswordField exPassword = new JPasswordField();

	JTextField fName = new JTextField();
	JTextField lName = new JTextField();
	JTextField userName = new JTextField();
	JTextField email = new JTextField();
	JTextField phone = new JTextField();
	JPasswordField password = new JPasswordField();
	JPasswordField confirmPassword = new JPasswordField();
	JLabel messExistingLogin = new JLabel();
	JLabel userCheck = new JLabel();

	JButton ok = new JButton("Ok");
	JButton cancel = new JButton("Cancel");
	JButton ok2 = new JButton("Ok");
	JButton cancel2 = new JButton("Cancel");
	JButton checkAvalaibility = new JButton("Check Availability");

	JParametersProxyPanel proxyPanel;

	final String domain = "http://www.apatarforge.org/";

	public JRegistrationDialog() throws HeadlessException {
		super(ApatarUiMain.MAIN_FRAME);

		setModal(true);
		setTitle("Apatar Registration");
		createPanel();
		ButtonGroup bg = new ButtonGroup();
		bg.add(newUser);
		bg.add(existingUser);
	}

	/*
	 * Create Panel
	 */
	private void createPanel() {
		setLocationByPlatform(true);
		proxyPanel = new JParametersProxyPanel(client);
		setSize(500, 600);
		setResizable(false);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		add(createExistingUserPanel());
		add(createNewUserPanel());
		add(proxyPanel);
		add(createButtonPanel(ok, cancel));
		setListeners();
	}

	private JPanel createNewUserPanel() {

		GridBagLayout layout = new GridBagLayout();
		JPanel userInfoPanel = new JPanel(layout);
		// add(userInfoPanel, BorderLayout.CENTER);
		userInfoPanel.setBorder(BorderFactory.createTitledBorder(""));

		GridBagConstraints constraintValue = new GridBagConstraints();
		GridBagConstraints constraintName = new GridBagConstraints();

		Insets insets = new Insets(3, 5, 5, 5);

		constraintName.anchor = GridBagConstraints.EAST;
		constraintName.gridwidth = 1;
		constraintName.insets = insets;
		constraintName.fill = GridBagConstraints.NONE;
		constraintName.weightx = 0.0;

		constraintValue.anchor = GridBagConstraints.WEST;
		constraintValue.gridwidth = GridBagConstraints.REMAINDER;
		constraintValue.insets = insets;
		constraintValue.fill = GridBagConstraints.BOTH;
		constraintValue.weightx = 1.0;

		ComponentBuilder.makeComponent(newUser, layout, constraintValue,
				userInfoPanel);
		ComponentBuilder.makeComponent(new JLabel("First Name:"), layout,
				constraintName, userInfoPanel);
		ComponentBuilder.makeComponent(fName, layout, constraintValue,
				userInfoPanel);
		ComponentBuilder.makeComponent(new JLabel("Last Name:"), layout,
				constraintName, userInfoPanel);
		ComponentBuilder.makeComponent(lName, layout, constraintValue,
				userInfoPanel);
		ComponentBuilder.makeComponent(new JLabel("User Name:"), layout,
				constraintName, userInfoPanel);
		constraintValue.gridwidth = 1;
		ComponentBuilder.makeComponent(userName, layout, constraintValue,
				userInfoPanel);
		constraintValue.weightx = 0.0;
		ComponentBuilder.makeComponent(checkAvalaibility, layout,
				constraintValue, userInfoPanel);
		constraintValue.gridwidth = GridBagConstraints.REMAINDER;
		userCheck.setMinimumSize(new Dimension(45,
				userCheck.getPreferredSize().height));
		userCheck.setPreferredSize(new Dimension(45, userCheck
				.getPreferredSize().height));
		ComponentBuilder.makeComponent(userCheck, layout, constraintValue,
				userInfoPanel);
		constraintValue.weightx = 1.0;
		ComponentBuilder.makeComponent(new JLabel("Password:"), layout,
				constraintName, userInfoPanel);
		ComponentBuilder.makeComponent(password, layout, constraintValue,
				userInfoPanel);
		ComponentBuilder.makeComponent(new JLabel("Confirm Password:"), layout,
				constraintName, userInfoPanel);
		ComponentBuilder.makeComponent(confirmPassword, layout,
				constraintValue, userInfoPanel);
		ComponentBuilder.makeComponent(new JLabel("E-mail:"), layout,
				constraintName, userInfoPanel);
		ComponentBuilder.makeComponent(email, layout, constraintValue,
				userInfoPanel);
		ComponentBuilder.makeComponent(new JLabel("Phone number (optional):"),
				layout, constraintName, userInfoPanel);
		ComponentBuilder.makeComponent(phone, layout, constraintValue,
				userInfoPanel);

		constraintValue.weighty = 2.0;
		ComponentBuilder.makeComponent(new JPanel(), layout, constraintValue,
				userInfoPanel);

		return userInfoPanel;

	}

	private JPanel createExistingUserPanel() {
		JPanel panel = new JPanel();

		JLabel apatarLinkLabel = new JLabel("<html><a href='" + domain
				+ "profile/lostpassword.html'>Forgot Your Password?</a></html>");

		apatarLinkLabel.addMouseListener(new MouseHyperLinkEvent());
		apatarLinkLabel.setCursor(Cursor
				.getPredefinedCursor(Cursor.HAND_CURSOR));

		GridBagLayout layout = new GridBagLayout();
		panel.setBorder(BorderFactory.createTitledBorder(""));

		panel.setLayout(layout);

		GridBagConstraints constraintValue = new GridBagConstraints();
		GridBagConstraints constraintName = new GridBagConstraints();

		Insets insets = new Insets(3, 5, 5, 5);

		constraintName.anchor = GridBagConstraints.EAST;
		constraintName.gridwidth = 1;
		constraintName.insets = insets;
		constraintName.fill = GridBagConstraints.NONE;
		constraintName.weightx = 0.0;

		constraintValue.anchor = GridBagConstraints.WEST;
		constraintValue.gridwidth = GridBagConstraints.REMAINDER;
		constraintValue.insets = insets;
		constraintValue.fill = GridBagConstraints.BOTH;
		constraintValue.weightx = 1.0;

		ComponentBuilder.makeComponent(existingUser, layout, constraintValue,
				panel);
		ComponentBuilder.makeComponent(new JLabel("User Name:"), layout,
				constraintName, panel);
		ComponentBuilder.makeComponent(exUserName, layout, constraintValue,
				panel);
		ComponentBuilder.makeComponent(new JLabel("Password:"), layout,
				constraintName, panel);
		ComponentBuilder.makeComponent(exPassword, layout, constraintValue,
				panel);
		constraintValue.anchor = GridBagConstraints.EAST;
		ComponentBuilder.makeComponent(apatarLinkLabel, layout,
				constraintValue, panel);

		ComponentBuilder.makeComponent(createButtonPanel(ok2, cancel2), layout,
				constraintValue, panel);

		return panel;
	}

	private JPanel createButtonPanel(JButton ok, JButton cancel) {

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

	private void setListeners() {
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				option = CANCEL_OPTION;
				setVisible(false);
			}
		});
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (okAction()) {
					option = OK_OPTION;
					setVisible(false);
				}
			}
		});
		cancel2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				option = CANCEL_OPTION;
				setVisible(false);
			}
		});
		ok2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (okAction()) {
					option = OK_OPTION;
					setVisible(false);
				}
			}
		});
		checkAvalaibility.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				proxyPanel.setParametersProxy();
				String url = domain;
				url += "index.php?option=com_apatarmailer&app_version=1162&task=checkusername&check_username="
						+ userName.getText();
				try {
					String res = client.sendGetHttpQuery(url);
					System.out.println("Apatarforge response: \n" + res);
					StringTokenizer tokenizer;
					tokenizer = new StringTokenizer(res, ";");
					String kode = tokenizer.nextToken();
					if (kode.equals("1")) {
						userCheck.setText("Ok");
						userCheck.setForeground(new Color(0, 64, 0));
					} else {
						userCheck.setText("Taken");
						userCheck.setForeground(Color.RED);
					}
				} catch (IOException e) {
					System.err.println("Apatarforge error response: \n"
							+ e.getMessage());
					JOptionPane.showMessageDialog(getContentPane(), e
							.getMessage());
				}
			}
		});
	}

	String error;
	boolean isUserNameWarningShow = true;

	public boolean okAction() {
		proxyPanel.setParametersProxy();
		HashMap<String, String> map = new HashMap<String, String>();
		String res = null;

		String registeredUN = newUser.isSelected() ? userName.getText()
				: exUserName.getText();

		if (isUserNameWarningShow) {
			if (registeredUN.indexOf('@') >= 0) {
				JUserNameWarningDialog unDlg = new JUserNameWarningDialog(this,
						registeredUN);
				unDlg.setVisible(true);
				isUserNameWarningShow = !unDlg.boxShow.isSelected();
			}
		}

		if (newUser.isSelected()) {
			if (!validateParams()) {
				JOptionPane.showMessageDialog(getContentPane(), error);
				return false;
			}

			String url = "";
			url += domain + "index.php";
			map.put("option", "com_apatarmailer");
			map.put("task", "registernewuser");
			map.put("app_version", "1162");
			map.put("version", ApplicationData.VERSION);
			map.put("f_name", fName.getText());
			map.put("l_name", lName.getText());
			map.put("username", userName.getText());
			map.put("email", email.getText());
			// (phone.getText().equals("") ? "" : "&phone=" + phone.getText()) +
			map.put("pass", new String(password.getPassword()));
			try {
				res = client.sendPostHttpQuery(url, map);
				System.out.println("Apatarforge response: \n" + res);
			} catch (Exception e) {
				System.err.println("Apatarforge error response: \n"
						+ e.getMessage());
				JOptionPane.showMessageDialog(getContentPane(), e.getMessage());
			}
		} else {
			if (!validateParams()) {
				JOptionPane.showMessageDialog(getContentPane(), error);
				return false;
			}
			String strUrl = "";
			strUrl += domain + "index.php";
			map.put("option", "com_apatarmailer");
			map.put("task", "login");
			map.put("app_version", "1162");
			map.put("username", exUserName.getText());
			map.put("pass", CoreUtils.getMD5(new String(exPassword
					.getPassword())));
			try {
				res = client.sendPostHttpQuery(strUrl, map);
				System.out.println("Apatarforge response: \n" + res);
			} catch (Exception e) {
				System.err.println("Apatarforge error response: \n"
						+ e.getMessage());
				JOptionPane.showMessageDialog(getContentPane(), e.getMessage());
			}

		}

		System.out.println("User Name=" + map.get("username") + " Response="
				+ res);

		StringTokenizer tokenizer;
		tokenizer = new StringTokenizer(res, ";");
		String kode = tokenizer.nextToken();
		JOptionPane.showMessageDialog(getContentPane(), tokenizer.nextToken());
		if (kode.equals("1")) {
			ApplicationData.httpClient = client;
		} else {
			return false;
		}
		return true;
	}

	private boolean validateParams() {
		if (newUser.isSelected()) {
			String un = userName.getText();
			if (!validateTextInUserNameOrPassword(un)) {
				error = "The user name should only contain letters (a-z, A-Z) and/or digits (0-9) and/or . and _";
				return false;
			}
			String firstName = fName.getText();
			if (firstName.length() < 1) {
				error = "Please, enter your first name.";
				return false;
			}
			String lastName = lName.getText();
			if (lastName.length() < 1) {
				error = "Please, enter your last name.";
				return false;
			}

			String strPassword = new String(password.getPassword());
			String strConfirmPassword = new String(confirmPassword
					.getPassword());
			if (strPassword.length() < 6) {
				error = "The minimum password length is 6 characters.";
				return false;
			}
			if (!strPassword.equals(strConfirmPassword)) {
				error = "The password and Confirm Password you typed do not match!";
				return false;
			}
			if (!validateTextInUserNameOrPassword(strPassword)) {
				error = "The password should only contain letters (a-z, A-Z) and/or digits (0-9) and/or . and _";
				return false;
			}
			String strEmail = email.getText();
			try {
				if (!CoreUtils.validEmail(strEmail)) {
					error = "Email is not valid!";
					return false;
				}
			} catch (ApatarException e) {
				return false;
			}
		} else {
			String userName = exUserName.getText();
			if (userName.length() < 1) {
				error = "Please, enter your User Name.";
				return false;
			}
			if (!validateTextInUserNameOrPassword(userName)) {
				error = "The user name should only contain letters (a-z, A-Z) and/or digits (0-9) and/or . and _";
				return false;
			}
			String strExPassword = new String(exPassword.getPassword());
			if (!validateTextInUserNameOrPassword(strExPassword)) {
				error = "The password should only contain letters (a-z, A-Z) and/or digits (0-9) and/or . and _";
				return false;
			}
			if (strExPassword.length() < 6) {
				error = "The minimum password length is 6 characters.";
				return false;
			}
		}
		return true;
	}

	private boolean validateTextInUserNameOrPassword(String text) {
		// [\w\s\.]
		try {
			return ApatarRegExp.matchRegExp("[\\w\\s\\.]+", text);
		} catch (ApatarException e) {
			return false;
		}
	}

	public int selectOption() {
		setVisible(true);
		return option;
	}

}
