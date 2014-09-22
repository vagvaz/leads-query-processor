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

package com.apatar.functions.Logic;

import java.util.List;

import com.apatar.core.ValueAbstractApatarFunction;
import com.apatar.functions.FunctionInfo;
import com.apatar.ui.FunctionCategory;

public class LessThanValidateFunction extends ValueAbstractApatarFunction {
	public Object execute(List list) {
		if (list == null || list.size() < 1) {
			return false;
		}

		Object ob = list.get(0);
		if (ob == null) {
			return false;
		}

		try {
			if ((list.size() == 1) || (null == list.get(0))
					|| (null == list.get(1))) {
				return CompareUtils.Compare(ob, value) < 0;
			} else {
				return CompareUtils.Compare(ob, list.get(1)) < 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	static FunctionInfo fi = new FunctionInfo("Less Than", 2, 1);
	static {
		fi.getCategories().add(FunctionCategory.ALL);
		fi.getCategories().add(FunctionCategory.String);
		fi.getCategories().add(FunctionCategory.Number);
		fi.getCategories().add(FunctionCategory.Date_and_Time);
	}

	public FunctionInfo getFunctionInfo() {
		return fi;
	}

}