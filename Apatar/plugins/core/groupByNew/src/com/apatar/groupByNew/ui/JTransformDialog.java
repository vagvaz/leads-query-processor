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

package com.apatar.groupByNew.ui;

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
import com.apatar.core.FunctionsPlugin;
//import com.apatar.core.FunctionsPlugin;
import com.apatar.core.Project;
import com.apatar.core.SchemaTable;
import com.apatar.core.TableInfo;
import com.apatar.functions.FunctionUtils;
import com.apatar.groupByNew.ui.JTransformDialog;
import com.apatar.groupByNew.GroupByNewNode;
import com.apatar.groupByNew.GroupByNewNodeUtils;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.JOutputPanel;
import com.apatar.ui.JPaneToolbar;
import com.apatar.ui.JShortcutBar;
import com.apatar.ui.JWorkPane;
import com.apatar.ui.MouseHyperLinkEvent;
import com.apatar.ui.NodeFactory;
import com.apatar.ui.UiUtils;
import com.apatar.ui.schematable.JSchemaDialog;

public class JTransformDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	JSplitPane mainSplitPane;
	JSplitPane rightSplitPane;
	JWorkPane workPane;
	JPaneToolbar toolBar;
	JTabbedPane tabbedPane;

	JOutputPanel outputPanel;

	private JButton ok;
	private JButton cancel;

	public static int OK_OPTION = 1;
	public static int CANCEL_OPTION = 0;

	int option = CANCEL_OPTION;

	private JLabel keyForReferringToDescriptionLabel;

	public void setKeyForReferringToDescription(
			String keyForReferringToDescription) {
		String url = ApplicationData
				.getGadgetHelpProperty(keyForReferringToDescription);
		//keyForReferringToDescriptionLabel.setText("<html><a href='" + url
		//		+ "'>View operation guide</a></html>");
	}

	Project project;
	GroupByNewNode node;

	TableInfo inputTI;

	@SuppressWarnings("unchecked")
	public JTransformDialog(final Project project, final GroupByNewNode node)
			throws HeadlessException {
		super(ApatarUiMain.MAIN_FRAME, "GroupBy");
		this.node = node;
		inputTI = node.getTiForConnection(GroupByNewNode.INPUT_CONN_POINT);
		setModal(true);

		Rectangle rc = getGraphicsConfiguration().getBounds();
		setBounds(50, 10, rc.width - 200, rc.height - 200);
		setLayout(new BorderLayout());

		tabbedPane = new JTabbedPane(SwingConstants.BOTTOM);
		mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		// create buttons and bind them
		toolBar = new JPaneToolbar();
		try {
			toolBar.AddButton(GroupByNewNodeUtils.AUTO_MAP, false, this, this
					.getClass().getMethod("AutoMap", new Class[] {}));

			toolBar.AddButton("Edit Output", false, this, this.getClass()
					.getMethod("EditOutputs", new Class[] {}));
		} catch (NoSuchMethodException ex) {
			ex.printStackTrace();
		}

		this.project = project;
		workPane = new JWorkPane(project);

		getContentPane().add(mainSplitPane, BorderLayout.CENTER);
		getContentPane().add(toolBar, BorderLayout.NORTH);

		rightSplitPane.setLeftComponent(new JScrollPane(workPane));

		mainSplitPane.setDividerLocation(150);
		mainSplitPane.setLeftComponent(tabbedPane);
		mainSplitPane.setRightComponent(rightSplitPane);

		rightSplitPane.setResizeWeight(1.0);
		rightSplitPane.setDividerLocation(rc.width - 500);

		tabbedPane
				.addTab(
						"Inputs",
						UiUtils.SMALL_COLUMN_ICON,
						new JShortcutBar(
								AbstractNode
										.getOtherSideTableInfo(node
												.getConnPoint(GroupByNewNode.INPUT_CONN_POINT)),
								GroupByNewNode.INPUT_CONN_POINT,
								node
										.getConnectedNodeName(GroupByNewNode.INPUT_CONN_POINT),
								false, SwingConstants.LEFT, new String[] {}));

		// merge output schemas
		//node.MergeSchemas(GroupByNewNode.OUTPUT_CONN_POINT); @@GIANNIS_merge

		outputPanel = new JOutputPanel(node
				.getTiForConnection(GroupByNewNode.OUTPUT_CONN_POINT),
				GroupByNewNode.OUTPUT_CONN_POINT, rightSplitPane, node);
		rightSplitPane.setRightComponent(outputPanel);
		tabbedPane.addTab("Functions", GroupByNewNodeUtils.SMALL_TRANSFORM_ICON,
				createTabFunction());

		ok = new JButton("Ok");
		cancel = new JButton("Cancel");

		ok.setPreferredSize(cancel.getPreferredSize());

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

		createListeners();
	}

	private void createListeners() {
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				option = OK_OPTION;
				setVisible(false);
				// dispose();
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				option = CANCEL_OPTION;
				// dispose();
				setVisible(false);
			}
		});
	}

	private JShortcutBar createTabFunction() {
		List<NodeFactory> list = FunctionsPlugin.getNodesFunctionGroupBy();
		return new JShortcutBar(list, SwingConstants.LEFT);
	}

	public void autoMap() {
		FunctionUtils.createNodeColumns(workPane, project, node
				.getTiForConnection(GroupByNewNode.OUTPUT_CONN_POINT), node
				.getTiForConnection(GroupByNewNode.INPUT_CONN_POINT), 390, 10,
				10);
	}

	public JOutputPanel getOutputPanel() {
		return outputPanel;
	}

	public void AutoMap() {
		workPane.deleteAllComponent();
		autoMap();
	}

	public void EditOutputs() {
		SchemaTable schema = node.getTiForConnection(
				GroupByNewNode.OUTPUT_CONN_POINT).getSchemaTable();
		JSchemaDialog dlg = new JSchemaDialog(true, ApplicationData
				.getTempDataBase().getDataBaseInfo().getAvailableTypes(),
				schema, node);
		// dlg.setVisible()

		if (JSchemaDialog.OK_OPTION == dlg.showDialog()) {
			getOutputPanel().updateSchema();
		}
	}

	public static int showDialog(final Project project, final GroupByNewNode node) {
		JTransformDialog dlg = new JTransformDialog(project, node);
		dlg.setKeyForReferringToDescription("help.operation.transform");
		dlg.setVisible(true);
		dlg.dispose();
		return dlg.option;
	}
}
