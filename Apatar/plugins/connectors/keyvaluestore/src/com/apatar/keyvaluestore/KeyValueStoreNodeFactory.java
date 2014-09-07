package com.apatar.keyvaluestore;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.apatar.core.AbstractNode;
import com.apatar.ui.NodeFactory;

public class KeyValueStoreNodeFactory extends NodeFactory{
	
	public KeyValueStoreNodeFactory() {
	}
	
	@Override
	public AbstractNode createNode() {
		return new KeyValueStoreNode();
	}

	@Override
	public List<String> getCategory() {
		// TODO Auto-generated method stub
		List<String> res = new ArrayList<String>();
		res.add("Connectors");
		return res;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "MapReduce";
	}

	@Override
	public ImageIcon getIcon() {
		// TODO Auto-generated method stub
		return KeyValueStoreUtils.READ_KEYVALUESTORE_ICON;
	}

	@Override
	public String getNodeClass() {
		// TODO Auto-generated method stub
		return KeyValueStoreNode.class.getName();
	}

	@Override
	public int getHorizontalTextPosition() {
		// TODO Auto-generated method stub
		return JLabel.CENTER;
	}

	@Override
	public int getVerticalTextPosition() {
		// TODO Auto-generated method stub
		return JLabel.BOTTOM;
	}

	@Override
	public Color getTextColor() {
		// TODO Auto-generated method stub
		return java.awt.Color.BLACK;
	}

	@Override
	public boolean MainPaneNode() {
		// TODO Auto-generated method stub
		return true;
	}

}
