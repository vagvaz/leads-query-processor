package com.apatar.output.ui;


import com.apatar.output.OutputNode;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class OutputModeDescriptor extends WizardPanelDescriptor {
	
	public static final String IDENTIFIER = "FSMODE_PANEL";
	
	JOutputModePanel panel = new JOutputModePanel();
	Object backDescriptor;
	Object nextDescriptor;
	OutputNode node;
	
	public OutputModeDescriptor(OutputNode node, Object backDescriptor, Object nextDescriptor) {
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
		panel.setDeleteAll(node.isDeleteAllInRDB());
		panel.setMode( node.getMode() );
		getWizard().setTitleComment("");
		getWizard().setAdditionalComment("");
    }

    public void displayingPanel() {

    }

    public int aboutToHidePanel(String actionCommand) {
    	if (actionCommand.equals(Wizard.NEXT_BUTTON_ACTION_COMMAND)) {
    		node.setMode(panel.getMode());
    		node.setDeleteAllInRDB(panel.isDeleteAll());
    	}
    	return CHANGE_PANEL;
    }
}
