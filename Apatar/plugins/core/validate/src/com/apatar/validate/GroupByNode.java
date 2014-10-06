package com.apatar.validate;


import javax.swing.ImageIcon;

import com.apatar.core.AbstractApatarActions;
import com.apatar.core.XFunctionsPlugin;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.JSubProjectDialog;

public class GroupByNode extends XValidateNode {

	public GroupByNode() {
		super();
		title = "GroupBy";
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
				"Filtration", this, new String[] { INPUT_CONN_POINT },
				XFunctionsPlugin.getNodesFunction(), false,
				"help.operation.groupby") == JSubProjectDialog.OK_OPTION;

	}

}
