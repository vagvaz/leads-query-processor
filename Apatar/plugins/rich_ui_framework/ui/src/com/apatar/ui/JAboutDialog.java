/*
_______________________
Apatar Open Source Data Integration
Copyright (C) 2005-2007, Apatar, Inc.
info@apatar.com
195 Meadow St., 2nd Floor
Chicopee, MA 01013

### This program is free software; you can redistribute it and/or modify
### it under the terms of the GNU General Public License as published by
### the Free Software Foundation; either version 2 of the License, or
### (at your option) any later version.

### This program is distributed in the hope that it will be useful,
### but WITHOUT ANY WARRANTY; without even the implied warranty of
### MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.# See the
### GNU General Public License for more details.

### You should have received a copy of the GNU General Public License along
### with this program; if not, write to the Free Software Foundation, Inc.,
### 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
________________________

 */

package com.apatar.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import com.apatar.core.ApplicationData;

public class JAboutDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	JButton okButton = new JButton("Ok");

	public JAboutDialog(Dialog dlg) {
		super(dlg);
		createDialog();
	}

	public JAboutDialog(Dialog dlg, boolean flag) {
		super(dlg, flag);
		createDialog();
	}

	public JAboutDialog(Frame frm) {
		super(frm);
		createDialog();
	}

	public JAboutDialog(Frame frm, boolean flag) {
		super(frm, flag);

		createDialog();
	}

	private void createDialog() {
		setLayout(new BorderLayout(10, 10));

		setSize(400, 420);
		setResizable(false);
		setTitle(ApplicationData.VERSION + " - About");

		JPanel logoPanel = new JPanel(new BorderLayout(5, 5));
		logoPanel.setBorder(new EmptyBorder(15, 10, 5, 10));
		logoPanel.add(new JLabel(UiUtils.APATAR_LOGO_ICON), BorderLayout.WEST);

		JLabel version = new JLabel(ApplicationData.VERSION);
		version.setFont(UiUtils.BOLD_SIZE_12_FONT);
		logoPanel.add(version, BorderLayout.EAST);

		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
		textPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		textPanel.setOpaque(false);

		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setOpaque(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setFont(UiUtils.NORMAL_SIZE_11_FONT);
		textArea.setBorder(null);
		textArea.setComponentPopupMenu(new JDefaultContextMenu(textArea));

		textArea
				.setText("Apatar Open Source Data Integration helps users, DBAs, and mashup developers join their on-premises data sources with the Web without coding. Users install a visual job designer application to create integration jobs called DataMaps, link data between the source(s) and the target(s), and schedule one-time or recurring data transformations."
						+ "\n\n"
						+ "100% of the source code is released under GPL v. 2.0."
						+ "\n\n" + "Visit Apatar Web sites:");

		textPanel.add(textArea);
		textPanel.add(Box.createVerticalGlue());

		JPanel link1Panel = new JPanel(new BorderLayout());

		JLabel apatarLinkLabel = new JLabel(
				"<html><a href='http://www.apatar.com'>http://www.apatar.com</a></html>");
		JLabel apatarHomeLabel = new JLabel(" - Apatar Home");

		apatarLinkLabel.addMouseListener(new MouseHyperLinkEvent());
		apatarLinkLabel.setCursor(Cursor
				.getPredefinedCursor(Cursor.HAND_CURSOR));

		link1Panel.add(apatarLinkLabel, BorderLayout.WEST);
		link1Panel.add(apatarHomeLabel, BorderLayout.CENTER);

		textPanel.add(link1Panel);
		textPanel.add(Box.createVerticalGlue());

		JPanel link2Panel = new JPanel(new BorderLayout());

		JLabel apatarforgeLinkLabel = new JLabel(
				"<html><a href='http://www.apatarforge.org'>http://www.apatarforge.org</a></html>");
		JLabel apatarforgeHomeLabel = new JLabel(
				" - Catalog of Apatar DataMaps");

		apatarforgeLinkLabel.addMouseListener(new MouseHyperLinkEvent());
		apatarforgeLinkLabel.setCursor(Cursor
				.getPredefinedCursor(Cursor.HAND_CURSOR));

		JPanel link3Panel = new JPanel(new BorderLayout());

		JLabel planetApatarLinkLabel = new JLabel(
				"<html><a href='http://www.planetapatar.org'>http://www.planetapatar.org</a></html>");
		JLabel planetApatarHomeLabel = new JLabel(" - Planet Apatar");

		planetApatarLinkLabel.addMouseListener(new MouseHyperLinkEvent());
		planetApatarLinkLabel.setCursor(Cursor
				.getPredefinedCursor(Cursor.HAND_CURSOR));

		link2Panel.add(apatarforgeLinkLabel, BorderLayout.WEST);
		link2Panel.add(apatarforgeHomeLabel, BorderLayout.CENTER);

		link3Panel.add(planetApatarLinkLabel, BorderLayout.WEST);
		link3Panel.add(planetApatarHomeLabel, BorderLayout.CENTER);

		textPanel.add(link2Panel);
		textPanel.add(link3Panel);

		JPanel copyrightPanel = new JPanel(new BorderLayout(5, 15));
		copyrightPanel.setBorder(new EmptyBorder(15, 10, 0, 10));
		JLabel copyrightlabel = new JLabel(
				"<html>&copy; 2008-2009 Apatar, Inc.</html>");
		copyrightlabel.setFont(UiUtils.NORMAL_SIZE_11_FONT);
		copyrightPanel.add(copyrightlabel);

		textPanel.add(copyrightPanel);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(okButton);
		buttonPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});

		getContentPane().add(logoPanel, BorderLayout.NORTH);
		getContentPane().add(textPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
	}
}
