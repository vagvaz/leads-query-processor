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

package com.apatar.transform.ui;

import java.awt.BorderLayout;
import java.awt.Container;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.apatar.core.AbstractNode;
import com.apatar.core.ApplicationData;
import com.apatar.core.FunctionsPlugin;
import com.apatar.core.Project;
import com.apatar.core.SchemaTable;
import com.apatar.core.TableInfo;
import com.apatar.functions.FunctionUtils;
import com.apatar.transform.ui.JAggregateDialog;
import com.apatar.transform.AggregateNode;
import com.apatar.transform.TransformUtils;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.JOutputPanel;
import com.apatar.ui.JPaneToolbar;
import com.apatar.ui.JShortcutBar;
import com.apatar.ui.JWorkPane;
import com.apatar.ui.MouseHyperLinkEvent;
import com.apatar.ui.NodeFactory;
import com.apatar.ui.UiUtils;
import com.apatar.ui.schematable.JSchemaDialog;

public class JAggregateDialog extends JDialog {

	/**
	 *
	 */
	private static final long serialVersionUID = 347078760952829491L;
	private JSplitPane mainSplitPane;
	private JSplitPane rightSplitPane;
	private JWorkPane workPane_1;
	private JWorkPane workPane_2;
	private JPaneToolbar toolBar;
	private JTabbedPane tabbedPane;
	private JTabbedPane workSpace;

	private JOutputPanel outputPanel;

	private JButton ok;
	private JButton cancel;

	private Project project_1;
	private Project project_2;
	private AggregateNode node;

	private TableInfo inputTI1;
	private TableInfo inputTI2;

	public static int OK_OPTION = 1;
	public static int CANCEL_OPTION = 0;

	int option = CANCEL_OPTION;

	private JLabel keyForReferringToDescriptionLabel;

	public void setKeyForReferringToDescription(
			String keyForReferringToDescription) {
		String url = ApplicationData
				.getGadgetHelpProperty(keyForReferringToDescription);
		getKeyForReferringToDescriptionLabel().setText(
				"<html><a href='" + url + "'>View operation guide</a></html>");
	}

	public JAggregateDialog(final Project project_1, final Project project_2,
			final AggregateNode a_node) throws HeadlessException {
		super(ApatarUiMain.MAIN_FRAME, "Aggregation");
		node = a_node;
		setInputTI1(node.getTiForConnection(AggregateNode.INPUT_CONN_POINT_1));
		setInputTI2(node.getTiForConnection(AggregateNode.INPUT_CONN_POINT_2));
		setModal(true);

		Rectangle rc = getGraphicsConfiguration().getBounds();
		setBounds(50, 10, rc.width - 100, rc.height - 100);
		setLayout(new BorderLayout());

		setTabbedPane(new JTabbedPane(SwingConstants.BOTTOM));
		setMainSplitPane(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT));
		setRightSplitPane(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT));

		// create buttons and bind them
		setToolBar(new JPaneToolbar());
		try {
			// getToolBar().AddButton(TransformUtils.AUTO_MAP, false, this,
			// this.getClass().getMethod("autoMap", new Class[] {}));

			getToolBar().AddButton("Edit Output", false, this,
					this.getClass().getMethod("EditOutputs", new Class[] {}));
		} catch (NoSuchMethodException ex) {
			ex.printStackTrace();
		}

		setProject_1(project_1);
		setProject_2(project_2);
		setWorkPane_1(new JWorkPane(getProject_1()));
		setWorkPane_2(new JWorkPane(getProject_2()));

		Container content = getContentPane();

		content.add(getMainSplitPane(), BorderLayout.CENTER);
		content.add(getToolBar(), BorderLayout.NORTH);

		getRightSplitPane().setDividerLocation(0.7);
		getRightSplitPane().setDividerSize(5);
		getRightSplitPane().setResizeWeight(0.7);

		setWorkSpace(new JTabbedPane());
		getWorkSpace()
				.addTab(
						"From `"
								+ getConnectedNodeName(AggregateNode.INPUT_CONN_POINT_1)
								+ "`", new JScrollPane(getWorkPane_1()));
		getWorkSpace()
				.addTab(
						"From `"
								+ getConnectedNodeName(AggregateNode.INPUT_CONN_POINT_2)
								+ "`", new JScrollPane(getWorkPane_2()));
		getRightSplitPane().setLeftComponent(getWorkSpace());

		getMainSplitPane().setDividerLocation(150);
		getMainSplitPane().setLeftComponent(getTabbedPane());
		getMainSplitPane().setRightComponent(getRightSplitPane());

		getTabbedPane()
				.addTab(
						getConnectedNodeName(AggregateNode.INPUT_CONN_POINT_1),
						UiUtils.SMALL_COLUMN_ICON,
						new JShortcutBar(
								AbstractNode
										.getOtherSideTableInfo(node
												.getConnPoint(AggregateNode.INPUT_CONN_POINT_1)),
								AggregateNode.INPUT_CONN_POINT_1,
								node
										.getConnectedNodeName(AggregateNode.INPUT_CONN_POINT_1),
								false, SwingConstants.LEFT, new String[] {}));
		getTabbedPane()
				.addTab(
						getConnectedNodeName(AggregateNode.INPUT_CONN_POINT_2),
						UiUtils.SMALL_COLUMN_ICON,
						new JShortcutBar(
								AbstractNode
										.getOtherSideTableInfo(node
												.getConnPoint(AggregateNode.INPUT_CONN_POINT_2)),
								AggregateNode.INPUT_CONN_POINT_2,
								node
										.getConnectedNodeName(AggregateNode.INPUT_CONN_POINT_2),
								false, SwingConstants.LEFT, new String[] {}));

		getWorkSpace().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				switch (getWorkSpace().getSelectedIndex()) {
				case 0:
					if (getTabbedPane().getSelectedIndex() < 2) {
						getTabbedPane().setSelectedIndex(0);
					}
					break;
				case 1:
					if (getTabbedPane().getSelectedIndex() < 2) {
						getTabbedPane().setSelectedIndex(1);
					}
					break;
				}
			}
		});
		getTabbedPane().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				switch (getTabbedPane().getSelectedIndex()) {
				case 0:
					getWorkSpace().setSelectedIndex(0);
					break;
				case 1:
					getWorkSpace().setSelectedIndex(1);
					break;
				}
			}
		});
		// merge output schemas
		//node.MergeSchemas(AggregateNode.OUTPUT_CONN_POINT); @@GIANNIS_merge

		setOutputPanel(new JOutputPanel(node
				.getTiForConnection(AggregateNode.OUTPUT_CONN_POINT),
				AggregateNode.OUTPUT_CONN_POINT, getRightSplitPane(), node));
		getRightSplitPane().setRightComponent(outputPanel);
		getTabbedPane().addTab("Functions",
				TransformUtils.SMALL_TRANSFORM_ICON, createTabFunction());

		setOk(new JButton("Ok"));
		setCancel(new JButton("Cancel"));

		getOk().setPreferredSize(getCancel().getPreferredSize());

		JPanel buttonPanel = new JPanel();
		JSeparator separator = new JSeparator();
		Box buttonBox = new Box(BoxLayout.X_AXIS);

		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.add(separator, BorderLayout.NORTH);

		buttonBox.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));

		setKeyForReferringToDescriptionLabel(new JLabel());
		getKeyForReferringToDescriptionLabel().setFont(
				UiUtils.NORMAL_SIZE_12_FONT);
		getKeyForReferringToDescriptionLabel().addMouseListener(
				new MouseHyperLinkEvent());
		getKeyForReferringToDescriptionLabel().setCursor(
				Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		buttonBox.add(getKeyForReferringToDescriptionLabel());
		buttonBox.add(Box.createHorizontalStrut(50));
		buttonBox.add(getOk());
		buttonBox.add(Box.createHorizontalStrut(10));
		buttonBox.add(getCancel());

		buttonPanel.add(buttonBox, java.awt.BorderLayout.EAST);

		content.add(buttonPanel, java.awt.BorderLayout.SOUTH);

		createListeners();
	}

	private String getConnectedNodeName(String connectionPoint) {
		try {
			String res = node.getConnectedNodeName(connectionPoint);
			if ("".equals(res)) {
				return connectionPoint.equals(AggregateNode.INPUT_CONN_POINT_1) ? "Inputs 1"
						: "Inputs 2";
			} else {
				return res;
			}
		} catch (RuntimeException e) {
			System.err.println("No connected node");
			return connectionPoint.equals(AggregateNode.INPUT_CONN_POINT_1) ? "Inputs 1"
					: "Inputs 2";
		}
	}

	private void createListeners() {
		getOk().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				option = OK_OPTION;
				setVisible(false);
				// dispose();
			}
		});
		getCancel().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				option = CANCEL_OPTION;
				setVisible(false);
				// dispose();
			}
		});
	}

	private JShortcutBar createTabFunction() {
		List<NodeFactory> list = FunctionsPlugin.getNodesFunction();
		return new JShortcutBar(list, SwingConstants.LEFT);
	}

	public JOutputPanel getOutputPanel() {
		return outputPanel;
	}

	public void EditOutputs() {
		SchemaTable schema = node.getTiForConnection(
				AggregateNode.OUTPUT_CONN_POINT).getSchemaTable();
		JSchemaDialog dlg = new JSchemaDialog(true, ApplicationData
				.getTempDataBase().getDataBaseInfo().getAvailableTypes(),
				schema, node);

		if (JSchemaDialog.OK_OPTION == dlg.showDialog()) {
			getOutputPanel().updateSchema();
		}
	}

	public void autoMap() {
		int y = FunctionUtils.createNodeColumns(getWorkPane_1(),
				getProject_1(), node
						.getTiForConnection(AggregateNode.OUTPUT_CONN_POINT),
				node.getTiForConnection(AggregateNode.INPUT_CONN_POINT_1), 390,
				10, 10);
		FunctionUtils.createNodeColumns(getWorkPane_1(), getProject_1(), node
				.getTiForConnection(AggregateNode.OUTPUT_CONN_POINT), node
				.getTiForConnection(AggregateNode.INPUT_CONN_POINT_2), 390, y,
				10);
	}

	public static int showDialog(final Project project_1,
			final Project project_2, final AggregateNode node) {
		JAggregateDialog dlg = new JAggregateDialog(project_1, project_2, node);
		dlg.setKeyForReferringToDescription("help.operation.aggregate");
		dlg.setVisible(true);
		dlg.dispose();
		return dlg.option;
	}

	/**
	 * @return the workSpace
	 */
	public JTabbedPane getWorkSpace() {
		return workSpace;
	}

	/**
	 * @param workSpace
	 *            the workSpace to set
	 */
	public void setWorkSpace(JTabbedPane workSpace) {
		this.workSpace = workSpace;
	}

	/**
	 * @return the mainSplitPane
	 */
	public JSplitPane getMainSplitPane() {
		return mainSplitPane;
	}

	/**
	 * @param mainSplitPane
	 *            the mainSplitPane to set
	 */
	public void setMainSplitPane(JSplitPane mainSplitPane) {
		this.mainSplitPane = mainSplitPane;
	}

	/**
	 * @return the rightSplitPane
	 */
	public JSplitPane getRightSplitPane() {
		return rightSplitPane;
	}

	/**
	 * @param rightSplitPane
	 *            the rightSplitPane to set
	 */
	public void setRightSplitPane(JSplitPane rightSplitPane) {
		this.rightSplitPane = rightSplitPane;
	}

	/**
	 * @return the workPane_1
	 */
	public JWorkPane getWorkPane_1() {
		return workPane_1;
	}

	/**
	 * @param workPane_1
	 *            the workPane_1 to set
	 */
	public void setWorkPane_1(JWorkPane workPane_1) {
		this.workPane_1 = workPane_1;
	}

	/**
	 * @return the toolBar
	 */
	public JPaneToolbar getToolBar() {
		return toolBar;
	}

	/**
	 * @param toolBar
	 *            the toolBar to set
	 */
	public void setToolBar(JPaneToolbar toolBar) {
		this.toolBar = toolBar;
	}

	/**
	 * @return the tabbedPane
	 */
	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	/**
	 * @param tabbedPane
	 *            the tabbedPane to set
	 */
	public void setTabbedPane(JTabbedPane tabbedPane) {
		this.tabbedPane = tabbedPane;
	}

	/**
	 * @return the project_1
	 */
	public Project getProject() {
		return project_1;
	}

	/**
	 * @param project_1
	 *            the project_1 to set
	 */
	public void setProject(Project project) {
		project_1 = project;
	}

	/**
	 * @return the node
	 */
	public AggregateNode getNode() {
		return node;
	}

	/**
	 * @param node
	 *            the node to set
	 */
	public void setNode(AggregateNode node) {
		this.node = node;
	}

	/**
	 * @return the inputTI1
	 */
	public TableInfo getInputTI1() {
		return inputTI1;
	}

	/**
	 * @param inputTI1
	 *            the inputTI1 to set
	 */
	public void setInputTI1(TableInfo inputTI1) {
		this.inputTI1 = inputTI1;
	}

	/**
	 * @return the inputTI2
	 */
	public TableInfo getInputTI2() {
		return inputTI2;
	}

	/**
	 * @param inputTI2
	 *            the inputTI2 to set
	 */
	public void setInputTI2(TableInfo inputTI2) {
		this.inputTI2 = inputTI2;
	}

	/**
	 * @param outputPanel
	 *            the outputPanel to set
	 */
	public void setOutputPanel(JOutputPanel outputPanel) {
		this.outputPanel = outputPanel;
	}

	/**
	 * @return the ok
	 */
	public JButton getOk() {
		return ok;
	}

	/**
	 * @param ok
	 *            the ok to set
	 */
	public void setOk(JButton ok) {
		this.ok = ok;
	}

	/**
	 * @return the cancel
	 */
	public JButton getCancel() {
		return cancel;
	}

	/**
	 * @param cancel
	 *            the cancel to set
	 */
	public void setCancel(JButton cancel) {
		this.cancel = cancel;
	}

	/**
	 * @return the keyForReferringToDescriptionLabel
	 */
	public JLabel getKeyForReferringToDescriptionLabel() {
		return keyForReferringToDescriptionLabel;
	}

	/**
	 * @param keyForReferringToDescriptionLabel
	 *            the keyForReferringToDescriptionLabel to set
	 */
	public void setKeyForReferringToDescriptionLabel(
			JLabel keyForReferringToDescriptionLabel) {
		this.keyForReferringToDescriptionLabel = keyForReferringToDescriptionLabel;
	}

	/**
	 * @return the workPane_2
	 */
	public JWorkPane getWorkPane_2() {
		return workPane_2;
	}

	/**
	 * @param workPane_2
	 *            the workPane_2 to set
	 */
	public void setWorkPane_2(JWorkPane workPane_2) {
		this.workPane_2 = workPane_2;
	}

	/**
	 * @return the project_1
	 */
	public Project getProject_1() {
		return project_1;
	}

	/**
	 * @param project_1
	 *            the project_1 to set
	 */
	public void setProject_1(Project project_1) {
		this.project_1 = project_1;
	}

	/**
	 * @return the project_2
	 */
	public Project getProject_2() {
		return project_2;
	}

	/**
	 * @param project_2
	 *            the project_2 to set
	 */
	public void setProject_2(Project project_2) {
		this.project_2 = project_2;
	}
}
