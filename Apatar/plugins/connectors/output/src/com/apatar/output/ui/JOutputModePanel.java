package com.apatar.output.ui;



import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.apatar.core.AbstractDataBaseNode;

public class JOutputModePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	JCheckBox isDeleteAll = new JCheckBox("Clear all files.");
	JRadioButton yesMode = new JRadioButton("YES");
	JRadioButton noMode = new JRadioButton("NO");
	
	public JOutputModePanel() {
		super();
		createPanel();
	}

	private void createPanel() {
		setLayout(new BorderLayout(5,5));
		
		JPanel modePanel = new JPanel();
		modePanel.setLayout(new BoxLayout(modePanel, BoxLayout.Y_AXIS));
		add(modePanel, BorderLayout.CENTER);
		modePanel.add(new JLabel("Overwrite existing cache(s)?"));
		modePanel.add(Box.createVerticalStrut(5));
		modePanel.add(yesMode);
		modePanel.add(Box.createVerticalStrut(2));
		modePanel.add(noMode);
		modePanel.add(Box.createVerticalStrut(5));
		//modePanel.add(isDeleteAll);
		modePanel.add(Box.createVerticalGlue());
		
		
		ButtonGroup modeBG = new ButtonGroup();
		modeBG.add(yesMode);
		modeBG.add(noMode);
		noMode.setSelected(true);
	}
	
	public int getMode() {
		if (yesMode.isSelected())
			return AbstractDataBaseNode.INSERT_MODE;
		return AbstractDataBaseNode.UPDATE_MODE;
	}
	
	public void setMode(int mode) {
		if (mode == AbstractDataBaseNode.INSERT_MODE)
			yesMode.setSelected(true);
		else
			noMode.setSelected(true);
	}
	
	public boolean isDeleteAll() {
		return isDeleteAll.isSelected();
	}
	
	public void setDeleteAll(boolean b) {
		isDeleteAll.setSelected(b);
	}
}
