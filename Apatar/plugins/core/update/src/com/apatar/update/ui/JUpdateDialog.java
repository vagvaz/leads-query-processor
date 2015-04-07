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

package com.apatar.update.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.apatar.core.ApplicationData;
import com.apatar.core.Condition;
import com.apatar.core.FunctionsPlugin;
import com.apatar.core.Project;
import com.apatar.core.TableInfo;
import com.apatar.functions.FunctionUtils;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.JConditionPanel;
import com.apatar.ui.JOutputPanel;
import com.apatar.ui.JPaneToolbar;
import com.apatar.ui.JShortcutBar;
import com.apatar.ui.JWorkPane;
import com.apatar.ui.MouseHyperLinkEvent;
import com.apatar.ui.NodeFactory;
import com.apatar.ui.UiUtils;
import com.apatar.update.UpdateNode;
import com.apatar.update.UpdateUtils;

public class JUpdateDialog extends JDialog {

	/**
	 *
	 */
	private static final long serialVersionUID = -5679615305746142313L;
	JSplitPane mainSplitPane;
	JSplitPane rightSplitPane;
	private final Project leftToRightProject;
	private final Project rightToLeftProject;
	private final Project updateConditionsProject;
	JWorkPane leftToRightPane;
	JWorkPane rightToLeftPane;
	JWorkPane updateConditionsPane;
	JConditionPanel conditionPanel;
	JTabbedPane columnTabbedPane;
	JTabbedPane workTabedPane;

	private final JButton ok;
	private final JButton cancel;

	UpdateNode node;

	Map<?, ?> inputColumns1;
	Map<?, ?> inputColumns2;
	Map<?, ?> outputColumns;

	JComboBox cb1;
	JComboBox cb2;

	JOutputPanel outputPanelLeftToRight;
	JOutputPanel outputPanelRightToLeft;

	JPaneToolbar toolbar = new JPaneToolbar();

	public static int OK_OPTION = 1;
	public static int CANCEL_OPTION = 0;

	int option = CANCEL_OPTION;

	private final JLabel keyForReferringToDescriptionLabel;

	public void setKeyForReferringToDescription(
			String keyForReferringToDescription) {
		String url = ApplicationData
				.getGadgetHelpProperty(keyForReferringToDescription);
	//	keyForReferringToDescriptionLabel.setText("<html><a href='" + url
	//			+ "'>View operation guide</a></html>");
	}

	public JUpdateDialog(JFrame owner, final Project leftTorightTransformation,
			final Project rightToLeftTransformation,
			final Project updateConditions, final UpdateNode node)
			throws HeadlessException {
		super(owner, "Update");
		this.node = node;
		leftToRightProject = leftTorightTransformation;
		rightToLeftProject = rightToLeftTransformation;
		updateConditionsProject = updateConditions;

		Rectangle rc = getGraphicsConfiguration().getBounds();
		setBounds(50, 10, rc.width - 100, rc.height - 100);
		setLayout(new BorderLayout());

		setModal(true);

		// try {
		// toolbar.AddButton(TransformUtils.AUTO_MAP, false, this, this
		// .getClass().getMethod("autoMap", new Class[] {}));
		// } catch (Exception e1) {
		// e1.printStackTrace();
		// }

		TableInfo input1TI = node
				.getTiForConnection(UpdateNode.LEFT_TO_RIGHT_CONN_POINT);
		TableInfo input2TI = node
				.getTiForConnection(UpdateNode.RIGHT_TO_LEFT_CONN_POINT);

		columnTabbedPane = new JTabbedPane(SwingConstants.BOTTOM);
		mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		leftToRightPane = new JWorkPane(leftToRightProject);
		rightToLeftPane = new JWorkPane(rightToLeftProject);
		updateConditionsPane = new JWorkPane(updateConditionsProject);
		workTabedPane = new JTabbedPane();
		workTabedPane
				.addTab(
						node
								.getConnectedNodeName(UpdateNode.LEFT_TO_RIGHT_CONN_POINT)
								+ " > "
								+ node
										.getConnectedNodeName(UpdateNode.RIGHT_TO_LEFT_CONN_POINT),
						new JScrollPane(leftToRightPane));

		workTabedPane
				.addTab(
						node
								.getConnectedNodeName(UpdateNode.RIGHT_TO_LEFT_CONN_POINT)
								+ " > "
								+ node
										.getConnectedNodeName(UpdateNode.LEFT_TO_RIGHT_CONN_POINT),
						new JScrollPane(rightToLeftPane));

		getContentPane().add(mainSplitPane, BorderLayout.CENTER);
		getContentPane().add(toolbar, BorderLayout.NORTH);

		mainSplitPane.setRightComponent(rightSplitPane);
		mainSplitPane.setDividerLocation(200);

		rightSplitPane.setDividerLocation((getSize().width / 5) * 3);
		rightSplitPane.setDividerSize(5);
		rightSplitPane.setResizeWeight(1.0);

		rightSplitPane.setLeftComponent(new JScrollPane(workTabedPane));

		mainSplitPane.setLeftComponent(columnTabbedPane);

		columnTabbedPane
				.addTab(
						node
								.getConnectedNodeName(UpdateNode.LEFT_TO_RIGHT_CONN_POINT),
						UiUtils.SMALL_COLUMN_ICON,
						new JShortcutBar(
								input1TI,
								UpdateNode.LEFT_TO_RIGHT_CONN_POINT,
								node
										.getConnectedNodeName(UpdateNode.LEFT_TO_RIGHT_CONN_POINT),
								false, SwingConstants.LEFT, new String[] {}));
		columnTabbedPane
				.addTab(
						node
								.getConnectedNodeName(UpdateNode.RIGHT_TO_LEFT_CONN_POINT),
						UiUtils.SMALL_COLUMN_ICON,
						new JShortcutBar(
								input2TI,
								UpdateNode.RIGHT_TO_LEFT_CONN_POINT,
								node
										.getConnectedNodeName(UpdateNode.RIGHT_TO_LEFT_CONN_POINT),
								false, SwingConstants.LEFT, new String[] {}));
		columnTabbedPane.setEnabledAt(1, false);

		columnTabbedPane.addTab("Functions", UpdateUtils.SMALL_UPDATE_ICON,
				createTabFunction());

		conditionPanel = new JConditionPanel(input1TI, input2TI, node
				.getConnectedNodeName(UpdateNode.LEFT_TO_RIGHT_CONN_POINT),
				node.getConnectedNodeName(UpdateNode.RIGHT_TO_LEFT_CONN_POINT));

		workTabedPane.addTab("Update conditions", new JScrollPane(
				updateConditionsPane));
		workTabedPane.addTab("Match by", conditionPanel);
		List<Condition> conditions = node.getMatchByConditions();
		if (conditions != null) {
			conditionPanel.updateCondition(conditions);
		}

		workTabedPane.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				switch (workTabedPane.getSelectedIndex()) {
				case 0:
					columnTabbedPane.setEnabledAt(1, false);
					columnTabbedPane.setEnabledAt(0, true);
					columnTabbedPane.setSelectedIndex(0);
					rightSplitPane.setRightComponent(outputPanelLeftToRight);
					rightSplitPane.getRightComponent().setVisible(true);
					if (rightToLeftProject.getNodes().size() == 0) {
						FunctionUtils
								.reverseNodeColumns(
										rightToLeftPane,
										leftToRightProject,
										rightToLeftProject,
										node
												.getTiForConnection(UpdateNode.LEFT_TO_RIGHT_CONN_POINT),
										node
												.getTiForConnection(UpdateNode.RIGHT_TO_LEFT_CONN_POINT),
										390, 10, 10);
					}
					break;
				case 1:
					columnTabbedPane.setEnabledAt(0, false);
					columnTabbedPane.setEnabledAt(1, true);
					columnTabbedPane.setSelectedIndex(1);
					rightSplitPane.setRightComponent(outputPanelRightToLeft);
					rightSplitPane.getRightComponent().setVisible(true);
					break;
				case 2:
					columnTabbedPane.setEnabledAt(1, true);
					columnTabbedPane.setEnabledAt(0, true);
					columnTabbedPane.setSelectedIndex(0);
					rightSplitPane.getRightComponent().setVisible(false);
					break;
				}
			}
		});
		// Merge scheme with the output scheme
		// node.MergeSchemas(UpdateNode.OUTPUT_CONN_POINT);

		outputPanelLeftToRight = new JOutputPanel(node
				.getTiForConnection(UpdateNode.RIGHT_TO_LEFT_CONN_POINT), node
				.getConnectedNodeName(UpdateNode.RIGHT_TO_LEFT_CONN_POINT),
				rightSplitPane, node);

		outputPanelRightToLeft = new JOutputPanel(node
				.getTiForConnection(UpdateNode.LEFT_TO_RIGHT_CONN_POINT), node
				.getConnectedNodeName(UpdateNode.LEFT_TO_RIGHT_CONN_POINT),
				rightSplitPane, node);

		rightSplitPane.setRightComponent(outputPanelLeftToRight);

		// tabbedPane.addTab("Function", TransformUtils.SMALL_TRANSFORM_ICON,
		// createTabFunction());

		ok = new JButton("Ok");
		cancel = new JButton("Cancel");

		ok.setPreferredSize(cancel.getPreferredSize());
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				List<Condition> conditions = getConditions();
				node.setMatchByConditions(conditions);
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

	public List<Condition> getConditions() {
		return conditionPanel.getConditions();
	}

	public JOutputPanel getOutputPanel() {
		return outputPanelLeftToRight;
	}

	public void EditOutputs() {
		// SchemaTable schema = node.getTiForConnection(
		// UpdateNode.OUTPUT_CONN_POINT).getSchemaTable();
		// JSchemaDialog dlg = new JSchemaDialog(true, ApplicationData
		// .getTempDataBase().getDataBaseInfo().getAvailableTypes(),
		// schema, node);
		// if (JSchemaDialog.OK_OPTION == dlg.showDialog()) {
		// getOutputPanel().updateSchema();
		// }
	}

	public static int showDialog(final Project updateConditions,
			final Project rightToLeftTransformation,
			final Project leftTorightTransformation, final UpdateNode node) {
		JUpdateDialog dlg = new JUpdateDialog(ApatarUiMain.MAIN_FRAME,
				leftTorightTransformation, rightToLeftTransformation,
				updateConditions, node);
		dlg.setKeyForReferringToDescription("help.operation.update");
		dlg.setVisible(true);
		dlg.dispose();
		return dlg.option;
	}

	private JShortcutBar createTabFunction() {
		List<NodeFactory> list = FunctionsPlugin.getNodesFunction();
		return new JShortcutBar(list, SwingConstants.LEFT);
	}

}
