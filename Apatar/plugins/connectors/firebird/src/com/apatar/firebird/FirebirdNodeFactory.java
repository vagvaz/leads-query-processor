//////////////////////////////////////////////////////////
// Firebird Node Factory
// Author : Aamir Tauqeer (Kuwait)
// Date   : December 12, 2008
// Final  : December 25, 2008
//////////////////////////////////////////////////////////

package com.apatar.firebird;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.apatar.core.AbstractNode;
import com.apatar.ui.NodeFactory;

public class FirebirdNodeFactory extends NodeFactory {
	public FirebirdNodeFactory() {
	}

	@Override
	public AbstractNode createNode() {
		return new FirebirdNode();
	}

	@Override
	public List<String> getCategory() {
		List<String> res = new ArrayList<String>();
		res.add("Connectors");
		return res;
	}

	@Override
	public ImageIcon getIcon() {
		return FirebirdUtils.READ_FIRBIRD_ICON;
	}

	@Override
	public String getTitle() {
		return "Firebird";
	}

	@Override
	public String getNodeClass() {
		return FirebirdNode.class.getName();
	}

	@Override
	public int getHorizontalTextPosition() {
		return 0;
	}

	@Override
	public int getVerticalTextPosition() {
		return 3;
	}

	@Override
	public java.awt.Color getTextColor() {
		return java.awt.Color.BLACK;
	}

	@Override
	public boolean MainPaneNode() {
		return true;
	}
}
