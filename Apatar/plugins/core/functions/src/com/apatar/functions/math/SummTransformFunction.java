/*
_______________________
Apatar Open Source Data Integration
Copyright (C) 2005-2007, Apatar, Inc.
info@apatar.com
195 Meadow St., 2nd Floor
Chicopee, MA 01013

### This program is free software; you can redistribute it and/or modify
### it under the terms of the GNU General Public License as published by
### the Free Software Foundation; either version 2 of the License, or
### (at your option) any later version.

### This program is distributed in the hope that it will be useful,
### but WITHOUT ANY WARRANTY; without even the implied warranty of
### MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.# See the
### GNU General Public License for more details.

### You should have received a copy of the GNU General Public License along
### with this program; if not, write to the Free Software Foundation, Inc.,
### 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
________________________

*/

package com.apatar.functions.math;

import java.util.List;

import com.apatar.core.IntValueAbstractApatarFunction;
import com.apatar.functions.FunctionInfo;
import com.apatar.ui.FunctionCategory;

public class SummTransformFunction extends IntValueAbstractApatarFunction {

	static FunctionInfo fi = new FunctionInfo("Summ", 2, 1);

	public Object execute(List list) {
		int res = 0;

		if (list == null)
			return res;

		int value_to_add = getValue();

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
		return res;
	}

	static
	{
		fi.getCategories().add(FunctionCategory.Math);
		fi.getCategories().add(FunctionCategory.ALL);
	}

	public FunctionInfo getFunctionInfo() {
		return fi;
	}

}
