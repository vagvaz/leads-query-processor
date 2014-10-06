package com.apatar.output;


import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.apatar.core.AbstractNode;
import com.apatar.ui.NodeFactory;

public class OutputNodeFactory extends NodeFactory {

	@Override
	public List<String> getCategory() {
		List<String> res = new ArrayList<String>();
		res.add("LEADS");
		return res;
	}

	@Override
	public String getTitle() {
		return "Output";
	}

	@Override
	public ImageIcon getIcon() {
		return OutputUtils.READ_FS_ICON;
	}

	public AbstractNode createNode() {
		return new OutputNode();
	}

	@Override
	public String getNodeClass() {
		return OutputNode.class.getName();
	}

	@Override
	public int getHorizontalTextPosition() {
		return JLabel.CENTER;
	}

	@Override
	public int getVerticalTextPosition() {
		return JLabel.BOTTOM;
	}

	@Override
	public Color getTextColor() {
		return Color.BLACK;
	}

	@Override
	public boolean MainPaneNode() {
		return true;
	}

}
