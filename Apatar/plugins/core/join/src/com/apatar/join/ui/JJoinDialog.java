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

package com.apatar.join.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
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

import com.apatar.core.ApplicationData;
import com.apatar.core.FunctionsPlugin;
import com.apatar.core.Project;
import com.apatar.core.SchemaTable;
import com.apatar.core.TableInfo;
import com.apatar.join.JoinNode;
import com.apatar.join.JoinUtils;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.JConditionPanel;
import com.apatar.ui.JOutputPanel;
import com.apatar.ui.JPaneToolbar;
import com.apatar.ui.JShortcutBar;
import com.apatar.ui.JWorkPane;
import com.apatar.ui.MouseHyperLinkEvent;
import com.apatar.ui.NodeFactory;
import com.apatar.ui.UiUtils;
import com.apatar.ui.schematable.JSchemaDialog;

public class JJoinDialog extends JDialog {

	/**
	 *
	 */
	private static final long serialVersionUID = 1914507319793308144L;
	JSplitPane mainSplitPane;
	JSplitPane rightSplitPane;
	JWorkPane workPane;
	JConditionPanel conditionPanel;
	JTabbedPane columnTabbedPane;
	JTabbedPane workTabedPane;

	private final JButton ok;
	private final JButton cancel;

	Project project;
	JoinNode node;

	Map inputColumns1;
	Map inputColumns2;
	Map outputColumns;

	JComboBox cb1;
	JComboBox cb2;
	JComboBox joinTypeBox;

	JOutputPanel outputPanel;

	JPaneToolbar toolbar = new JPaneToolbar();

	public static int OK_OPTION = 1;
	public static int CANCEL_OPTION = 0;

	int option = CANCEL_OPTION;

	private final JLabel keyForReferringToDescriptionLabel;

	public void setKeyForReferringToDescription(
			String keyForReferringToDescription) {
		String url = ApplicationData
				.getGadgetHelpProperty(keyForReferringToDescription);
		//keyForReferringToDescriptionLabel.setText("<html><a href='" + url
		//		+ "'>View operation guide</a></html>");
	}

	@SuppressWarnings("unchecked")
	public JJoinDialog(JFrame owner, final Project project, final JoinNode node)
			throws HeadlessException {
		super(owner, "Join");
		this.node = node;

		Rectangle rc = getGraphicsConfiguration().getBounds();
		setBounds(50, 10, rc.width - 100, rc.height - 100);
		setLayout(new BorderLayout());

		setModal(true);

		try {
			toolbar.AddButton("Edit Output", false, this, this.getClass()
					.getMethod("EditOutputs", new Class[] {}));
			Vector<String> jtElements = new Vector<String>();
			//jtElements.add(JoinNode.LEFT_JOIN);@@
			//jtElements.add(JoinNode.RIGHT_JOIN);@@
			jtElements.add(JoinNode.INNER_JOIN);

			joinTypeBox = new JComboBox(jtElements);
			joinTypeBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					node.setTableJoinType((String) joinTypeBox
							.getSelectedItem());
				}
			});

			joinTypeBox.setSelectedItem(this.node.getTableJoinType());

			JPanel jtPanel = new JPanel();
			// jtPanel.setBackground(new Color(255, 0, 0));
			jtPanel.add(new JLabel("Join Type:"));
			jtPanel.add(joinTypeBox);
			toolbar.add(jtPanel);

		} catch (NoSuchMethodException ex) {
			ex.printStackTrace();
		}

		TableInfo input1TI = node
				.getTiForConnection(JoinNode.INPUT_CONN_POINT_1);
		TableInfo input2TI = node
				.getTiForConnection(JoinNode.INPUT_CONN_POINT_2);

		columnTabbedPane = new JTabbedPane(SwingConstants.BOTTOM);
		mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		this.project = project;
		workPane = new JWorkPane(project);
		workTabedPane = new JTabbedPane();
		//workTabedPane.addTab("Work", new JScrollPane(workPane));//@@G

		getContentPane().add(mainSplitPane, BorderLayout.CENTER);
		getContentPane().add(toolbar, BorderLayout.NORTH);

		mainSplitPane.setRightComponent(rightSplitPane);
		mainSplitPane.setDividerLocation(200);

		rightSplitPane.setDividerLocation((getSize().width / 5) * 3);
		rightSplitPane.setDividerSize(5);
		rightSplitPane.setResizeWeight(1.0);

		rightSplitPane.setLeftComponent(new JScrollPane(workTabedPane));

		mainSplitPane.setLeftComponent(columnTabbedPane);

		columnTabbedPane.addTab("Input Table1", UiUtils.SMALL_COLUMN_ICON,
				new JShortcutBar(input1TI, JoinNode.INPUT_CONN_POINT_1, node
						.getConnectedNodeName(JoinNode.INPUT_CONN_POINT_1),
						false, SwingConstants.LEFT, new String[] {}));

		columnTabbedPane.addTab("Input Table2", UiUtils.SMALL_COLUMN_ICON,
				new JShortcutBar(input2TI, JoinNode.INPUT_CONN_POINT_2, node
						.getConnectedNodeName(JoinNode.INPUT_CONN_POINT_2),
						false, SwingConstants.LEFT, new String[] {}));
		//columnTabbedPane.addTab("Functions", JoinUtils.SMALL_JOIN_ICON,
			//	createTabFunction());@@G

		conditionPanel = new JConditionPanel(input1TI, input2TI, node
				.getConnectedNodeName(JoinNode.INPUT_CONN_POINT_1), node
				.getConnectedNodeName(JoinNode.INPUT_CONN_POINT_2));

		workTabedPane.addTab("Join on", conditionPanel);
		List conditions = node.getConditions();
		if (conditions != null) {
			conditionPanel.updateCondition(conditions);
		}

		// Merge scheme with the output scheme
		//node.MergeSchemas(JoinNode.OUTPUT_CONN_POINT); @@GIANNIS_merge

		outputPanel = new JOutputPanel(node
				.getTiForConnection(JoinNode.OUTPUT_CONN_POINT),
				JoinNode.OUTPUT_CONN_POINT, rightSplitPane, node);

		rightSplitPane.setRightComponent(outputPanel);

		// tabbedPane.addTab("Function", TransformUtils.SMALL_TRANSFORM_ICON,
		// createTabFunction());

		ok = new JButton("Ok");
		cancel = new JButton("Cancel");

		ok.setPreferredSize(cancel.getPreferredSize());
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				List conditions = getConditions();
				node.setConditions(conditions);
				option = OK_OPTION;
				setVisible(false);
				// dispose();
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				option = CANCEL_OPTION;
				setVisible(false);
				// dispose();
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

	public List getConditions() {
		return conditionPanel.getConditions();
	}

	public JOutputPanel getOutputPanel() {
		return outputPanel;
	}

	public void EditOutputs() {
		SchemaTable schema = node
				.getTiForConnection(JoinNode.OUTPUT_CONN_POINT)
				.getSchemaTable();
		JSchemaDialog dlg = new JSchemaDialog(true, ApplicationData
				.getTempDataBase().getDataBaseInfo().getAvailableTypes(),
				schema, node);
		if (JSchemaDialog.OK_OPTION == dlg.showDialog()) {
			getOutputPanel().updateSchema();
		}
	}

	public static int showDialog(final Project project, final JoinNode node) {
		JJoinDialog dlg = new JJoinDialog(ApatarUiMain.MAIN_FRAME, project,
				node);
		dlg.setKeyForReferringToDescription("help.operation.join");
		dlg.setVisible(true);
		dlg.dispose();
		return dlg.option;
	}

	private JShortcutBar createTabFunction() {
		List<NodeFactory> list = FunctionsPlugin.getNodesFunction();
		return new JShortcutBar(list, SwingConstants.LEFT);
	}

}
