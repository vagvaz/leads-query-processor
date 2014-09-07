package com.apatar.keyvaluestore.gui;

import java.io.File;

import javax.swing.JOptionPane;

import com.apatar.keyvaluestore.KeyValueStoreNode;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class KVSModeDescriptor extends WizardPanelDescriptor {

	public static final String IDENTIFIER = "KVSMODE_PANEL";

	KVSModePanel panel = new KVSModePanel();
	Object backDescriptor;
	Object nextDescriptor;
	KeyValueStoreNode node;

	public KVSModeDescriptor(KeyValueStoreNode node, Object backDescriptor,
			Object nextDescriptor) {
		super();
		setPanelDescriptorIdentifier(IDENTIFIER);
		setPanelComponent(panel);
		this.backDescriptor = backDescriptor;
		this.nextDescriptor = nextDescriptor;
		this.node = node;
	}

	public Object getNextPanelDescriptor() {
		return nextDescriptor;
	}

	public Object getBackPanelDescriptor() {
		return backDescriptor;
	}

	public void aboutToDisplayPanel() {
		getWizard().setTitleComment("");
		getWizard().setAdditionalComment("");
	}

	public void displayingPanel() {

	}

	public int aboutToHidePanel(String actionCommand) {
		if (actionCommand.equals(Wizard.NEXT_BUTTON_ACTION_COMMAND)) {
			node.setMapperPath(panel.getMapperPath().getText());
			node.setReducerPath(panel.getReducerPath().getText());
			node.setCombinerPath(panel.getCombinerPath().getText());
			node.setXmlPath(panel.getXmlPath().getText());
			node.setOutputFolder(panel.getOutputFolder().getText());
			File mapperFile = new File(node.getMapperPath());
			File reducerFile = new File(node.getReducerPath());
			File combinerFile = new File(node.getCombinerPath());
			File xmlFile = new File(node.getXmlPath());
			File outputDirectory = new File(node.getOutputFolder());
			if (!mapperFile.exists()){
				JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
						"Mapper file does not exist.");
				return LEAVE_CURRENT_PANEL;
			}
			else if(!reducerFile.exists()){
				JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
						"Reducer file does not exist.");
				return LEAVE_CURRENT_PANEL;
			}
			else if(!combinerFile.exists()){
				JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
						"Combiner file does not exist.");
				return LEAVE_CURRENT_PANEL;
			}
			else if(!xmlFile.exists()){
				JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
						"XML file does not exist.");
				return LEAVE_CURRENT_PANEL;
			}
			else if(!outputDirectory.isDirectory()){
				JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
						"Output directory given is not a directory.");
				return LEAVE_CURRENT_PANEL;
			}
		}
		return CHANGE_PANEL;
	}
}
