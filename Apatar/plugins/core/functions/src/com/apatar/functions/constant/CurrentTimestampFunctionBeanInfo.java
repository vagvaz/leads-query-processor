package com.apatar.functions.constant;

import com.l2fprod.common.beans.BaseBeanInfo;

public class CurrentTimestampFunctionBeanInfo extends BaseBeanInfo {

	public CurrentTimestampFunctionBeanInfo(Class type) {
		super(type);
		addProperty("value");
	}
}
