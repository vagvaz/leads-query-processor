package com.apatar.sliding_window;


import javax.swing.ImageIcon;

import com.apatar.core.AbstractApatarActions;
import com.apatar.core.FunctionsPlugin;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.JSubProjectDialog;

public class OrderByNode extends XValidateNode {

	public OrderByNode() {
		super();
		title = "Sort";
		outputConnectionList
				.remove(getConnPoint(ValidateNode.OUTPUT_CONN_POINT_FALSE));
	}

	@Override
	public ImageIcon getIcon() {
		return ValidateUtils.FILTER_ICON;
	}

	@Override
	public boolean realEdit(AbstractApatarActions actions) {
		
		return JSubProjectDialog.showDialog(ApatarUiMain.MAIN_FRAME,
				"Sorting", this, new String[] { INPUT_CONN_POINT },
				FunctionsPlugin.getNodesFunctionOrderBy(), false,
				"help.operation.groupby") == JSubProjectDialog.OK_OPTION;
				
	}

}
