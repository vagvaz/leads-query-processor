package com.apatar.functions.constant;

import com.l2fprod.common.beans.BaseBeanInfo;

public class LimitFunctionBeanInfo extends BaseBeanInfo {

	public LimitFunctionBeanInfo(Class type) {
		super(type);
		addProperty("value");
	}

}
