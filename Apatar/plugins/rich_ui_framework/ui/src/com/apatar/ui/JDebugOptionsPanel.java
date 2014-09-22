package com.apatar.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class JDebugOptionsPanel extends JPanel {

	JCheckBox isClearLogsBeforeRun = new JCheckBox(
			"Clear debug and error logs at before each run");
	/**
	 * 
	 */
	private static final long serialVersionUID = 5045912206655003220L;

	public JDebugOptionsPanel(boolean isClearLogsBeforeRun) {
		super();
		createDebuOptionsPanel();
		this.isClearLogsBeforeRun.setSelected(isClearLogsBeforeRun);
	}

	private void createDebuOptionsPanel() {
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
		workPanel.add(isClearLogsBeforeRun);
		JPanel isClearLogsBeforeRunPanel = new JPanel(new BorderLayout());
		isClearLogsBeforeRunPanel.add(isClearLogsBeforeRun, BorderLayout.WEST);
		workPanel.add(isClearLogsBeforeRunPanel);
		add(workPanel, BorderLayout.NORTH);
		add(new JPanel(), BorderLayout.CENTER);
	}
}
