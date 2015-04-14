package com.apatar.sliding_window;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.apatar.core.AbstractNode;
import com.apatar.ui.NodeFactory;

public class XValidateNodeFactory extends NodeFactory {

	public XValidateNodeFactory() {
		super();
	}

	@Override
	public List<String> getCategory() {
		List<String> res = new ArrayList<String>();
		res.add("LEADS");
		return res;
	}

	@Override
	public String getTitle() {
		return "XValidate";
	}

	@Override
	public ImageIcon getIcon() {
		return XValidateUtils.SMALL_VALIDATE_ICON;
	}

	public AbstractNode createNode() {
		return new XValidateNode();
	}

	@Override
	public String getNodeClass() {
		return XValidateNode.class.getName();
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
