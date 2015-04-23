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
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.apatar.core.AbstractNode;
import com.apatar.core.ApplicationData;
import com.apatar.core.DataTransNode;

public class JSubProjectDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	JWorkPane workPane;
	protected DataTransNode node;

	protected JTabbedPane inputTabbed;
	protected JTabbedPane workTabedPane;

	public static int OK_OPTION = 1;
	public static int CANCEL_OPTION = 0;

	protected int option = CANCEL_OPTION;

	private JLabel keyForReferringToDescriptionLabel;

	public void setKeyForReferringToDescription(
			String keyForReferringToDescription) {
		String url = ApplicationData
				.getGadgetHelpProperty(keyForReferringToDescription);
		//keyForReferringToDescriptionLabel.setText("<html><a href='" + url
		//		+ "'>View operation guide</a></html>");
	}

	public JSubProjectDialog(JFrame owner, String name,
			final DataTransNode node, String[] inputTableList,
			List<NodeFactory> utilities, Boolean tabbed)
			throws HeadlessException {
		super(owner, name);
		init(node, inputTableList, utilities, tabbed);
	}

	public JSubProjectDialog(JDialog owner, String name,
			final DataTransNode node, String[] inputTableList,
			List<NodeFactory> utilities, Boolean tabbed)
			throws HeadlessException {
		super(owner, name);
		init(node, inputTableList, utilities, tabbed);
	}

	public void init(final DataTransNode node, String[] inputTableList,
			List<NodeFactory> utilities, Boolean tabbed) {
		this.node = node;

		setModal(true);
		Rectangle rc = getGraphicsConfiguration().getBounds();
		setBounds(50, 10, rc.width - 200, rc.height - 200);
		setLayout(new BorderLayout());

		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		JSplitPane rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		getContentPane().add(mainSplitPane, BorderLayout.CENTER);
		mainSplitPane.setRightComponent(rightSplitPane);
		mainSplitPane.setDividerLocation(150);

		rightSplitPane.setResizeWeight(1.0);
		rightSplitPane.setDividerLocation(rc.width - 500);

		// load the project into the main working pane
		workPane = new JWorkPane(node.getSubProject());
		if (tabbed) {
			workTabedPane = new JTabbedPane();
			workTabedPane.addTab("Work", new JScrollPane(workPane));
			rightSplitPane.setLeftComponent(workTabedPane);
		} else {
			rightSplitPane.setLeftComponent(new JScrollPane(workPane));
		}

		// load input
		inputTabbed = (inputTableList.length > 1) ? new JTabbedPane() : null;
		mainSplitPane.setLeftComponent(inputTabbed);
		for (String connectionName : inputTableList) {
			JShortcutBar jsb = new JShortcutBar(AbstractNode
					.getOtherSideTableInfo(node.getConnPoint(connectionName)),
					connectionName, node.getConnectedNodeName(connectionName),
					false, SwingConstants.LEFT, new String[] {});

			if (inputTabbed != null) {
				inputTabbed.addTab(node.getConnectedNodeName(connectionName),
						UiUtils.SMALL_COLUMN_ICON, jsb);
			} else {
				mainSplitPane.setLeftComponent(jsb);
			}
		}

		// load output
		JShortcutBar jsb = new JShortcutBar(utilities, SwingConstants.LEFT);
		rightSplitPane.setRightComponent(jsb);

		CreateButtons();
	}

	private void CreateButtons() {
		JButton ok = new JButton("Ok");
		JButton cancel = new JButton("Cancel");

		ok.setMinimumSize(cancel.getSize());
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				OnOK();
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				OnCancel();
			}
		});

		JPanel buttonPanel = new JPanel();
		JSeparator separator = new JSeparator();
		Box buttonBox = new Box(BoxLayout.X_AXIS);

		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.add(separator, BorderLayout.NORTH);

		buttonBox.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));

		keyForReferringToDescriptionLabel = new JLabel();
		keyForReferringToDescriptionLabel.setFont(UiUtils.NORMAL_SIZE_12_FONT);
		keyForReferringToDescriptionLabel
				.addMouseListener(new MouseHyperLinkEvent());
		keyForReferringToDescriptionLabel.setCursor(Cursor
				.getPredefinedCursor(Cursor.HAND_CURSOR));

		buttonBox.add(keyForReferringToDescriptionLabel);
		buttonBox.add(Box.createHorizontalStrut(50));

		buttonBox.add(ok);
		buttonBox.add(Box.createHorizontalStrut(10));
		buttonBox.add(cancel);

		buttonPanel.add(buttonBox, java.awt.BorderLayout.EAST);
		getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);
	}

	protected void OnOK() {
		option = OK_OPTION;
		setVisible(false);
		// dispose();
	}

	protected void OnCancel() {
		option = CANCEL_OPTION;
		setVisible(false);
		// dispose();
	}

	public DataTransNode getNode() {
		return node;
	}

	public static int showDialog(JFrame owner, String name,
			final DataTransNode node, String[] inputTableList,
			List<NodeFactory> utilities, Boolean tabbed, String keyForReferring) {
		JSubProjectDialog dlg = new JSubProjectDialog(owner, name, node,
				inputTableList, utilities, tabbed);
		dlg.setKeyForReferringToDescription(keyForReferring);
		dlg.setVisible(true);
		dlg.dispose();
		return dlg.option;
	}
}
