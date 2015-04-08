package com.apatar.functions.constant;

import com.l2fprod.common.beans.BaseBeanInfo;

public class VersionToFunctionBeanInfo extends BaseBeanInfo {

	public VersionToFunctionBeanInfo(Class type) {
		super(type);
		addProperty("value");
	}

}
