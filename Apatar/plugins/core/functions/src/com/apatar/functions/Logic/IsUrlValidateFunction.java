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

import com.apatar.core.AbstractApatarFunction;
import com.apatar.core.ApatarException;
import com.apatar.core.CoreUtils;
import com.apatar.functions.FunctionInfo;
import com.apatar.ui.FunctionCategory;

public class IsUrlValidateFunction extends AbstractApatarFunction {

	public Object execute(List list) {
		if (list == null || list.size() < 1) {
			return false;
		}

		String url = (String) list.get(0);

		try {
			if (CoreUtils.validUrl(url)) {
				return true;
			} else {
				return false;
			}
		} catch (ApatarException e) {
			return false;
		}
	}

	static FunctionInfo fi = new FunctionInfo("Is URL", 1, 1);
	static {
		fi.getCategories().add(FunctionCategory.ALL);
		fi.getCategories().add(FunctionCategory.String);
	}

	public FunctionInfo getFunctionInfo() {
		return fi;
	}
}
