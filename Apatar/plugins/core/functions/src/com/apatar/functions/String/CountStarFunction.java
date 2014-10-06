package com.apatar.functions.String;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.apatar.core.AbstractApatarFunction;
import com.apatar.functions.FunctionInfo;
import com.apatar.ui.FunctionCategory;

public class CountStarFunction extends AbstractApatarFunction {

	public Object execute(List list) {

		return null;
	}

	static FunctionInfo fi = new FunctionInfo("Count*", 1, 1);
	static {
		//fi.getCategories().add(FunctionCategory.String);
		fi.getCategories().add(FunctionCategory.Aggregation_functions);
	}

	public FunctionInfo getFunctionInfo() {
		return fi;
	}

}
