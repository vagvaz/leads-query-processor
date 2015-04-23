
package com.apatar.functions.math;

import java.util.List;

import com.apatar.core.AbstractApatarFunction;
import com.apatar.core.IntValueAbstractApatarFunction;
import com.apatar.functions.FunctionInfo;
import com.apatar.ui.FunctionCategory;

public class MultipleSortFunction extends AbstractApatarFunction {

	static FunctionInfo fi = new FunctionInfo("MultipleSort", 2, 1); //@@ Edw na dokimasw parapanw apo 1 etc gia na dw pws einai ???

	public Object execute(List list) {
		int res = 0;

		if (list == null)
			return res;

		//int value_to_add = getValue();
/*
		if ((list.size() == 1) || (null == list.get(0)) || (null == list.get(1))) {
			if (null != list.get(0)) {
				return (Integer.valueOf(list.get(0).toString()))+value_to_add;
			} else if (null != list.get(1)) {
				return (Integer.valueOf(list.get(1).toString()))+value_to_add;
			}
		} else {
			int i;
			for(i=0; i<list.size(); i++){
				res += (Integer.valueOf(list.get(i).toString()));
			}
		}
		*/
		return res;
	}

	static
	{
		//fi.getCategories().add(FunctionCategory.Math);
		fi.getCategories().add(FunctionCategory.Sort);
	}

	public FunctionInfo getFunctionInfo() {
		return fi;
	}

}
