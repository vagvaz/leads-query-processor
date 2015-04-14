package com.apatar.version;


import com.apatar.core.AbstractNode;

public class OrderByNodeFactory  extends XValidateNodeFactory {

	@Override
	public String getTitle() {
		return "Sort";
	}

	public AbstractNode createNode() {
		return new OrderByNode();
	}

	@Override
	public String getNodeClass() {
		return OrderByNode.class.getName();
	}
}
