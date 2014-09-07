package com.apatar.validate;


import com.apatar.core.AbstractNode;

public class GroupByNodeFactory  extends XValidateNodeFactory {

	@Override
	public String getTitle() {
		return "GroupBy";
	}

	public AbstractNode createNode() {
		return new GroupByNode();
	}

	@Override
	public String getNodeClass() {
		return GroupByNode.class.getName();
	}
}
