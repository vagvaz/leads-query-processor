package com.apatar.functions.constant;

import com.l2fprod.common.beans.BaseBeanInfo;

public class VersionFromFunctionBeanInfo extends BaseBeanInfo {

	public VersionFromFunctionBeanInfo(Class type) {
		super(type);
		addProperty("value");
	}

}
