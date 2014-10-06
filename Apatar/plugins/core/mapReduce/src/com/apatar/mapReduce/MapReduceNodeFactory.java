

package com.apatar.mapReduce;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.apatar.core.AbstractNode;
import com.apatar.mapReduce.MapReduceNode;
import com.apatar.mapReduce.MapReduceNodeUtils;
import com.apatar.ui.NodeFactory;

public class MapReduceNodeFactory extends NodeFactory {
	
	public MapReduceNodeFactory() {
		super();
	}

	public List<String> getCategory() {
		List<String> res = new ArrayList<String>();
		res.add("LEADS");
		return res;
	}

	public String getTitle() {
		return "MapReduce";
	}

	public ImageIcon getIcon() {
		return MapReduceNodeUtils.SMALL_TRANSFORM_ICON;
	}

	public AbstractNode createNode() {
		return new MapReduceNode();
	}

	public int getHorizontalTextPosition() {
		return JLabel.CENTER;
	}

	public int getVerticalTextPosition() {
		return JLabel.BOTTOM;
	}

	public String getNodeClass() {
		return MapReduceNode.class.getName();
	}
	
	public Color getTextColor() {
		return Color.BLACK;
	}

	public boolean MainPaneNode()
	{ return true; }
}
