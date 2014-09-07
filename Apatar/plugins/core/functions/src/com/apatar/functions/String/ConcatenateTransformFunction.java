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

package com.apatar.functions.String;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.apatar.core.ValueAbstractApatarFunction;
import com.apatar.functions.FunctionInfo;
import com.apatar.ui.FunctionCategory;

public class ConcatenateTransformFunction extends ValueAbstractApatarFunction {

	static FunctionInfo	fi	= new FunctionInfo("Concatenate", 2, 1);

	public Object execute(List list) {
		String str = "";

		if (list == null) {
			return str;
		}

		if ((list.get(0) instanceof Date) && (list.get(1) instanceof Date)) {

			Date date = (Date) list.get(0);
			Date time = (Date) list.get(1);
			Timestamp ts = new Timestamp(date.getYear(), date.getMonth(), date
					.getDate(), time.getHours(), time.getMinutes(), time
					.getSeconds(), 0);

			return ts;

		} else {
			String delimeter = getValue();

			int i;
			for (i = 0; i < list.size(); i++) {
				if (i != 0) {
					str = str.concat(delimeter);
				}
				Object inputParam = list.get(i);
				if (inputParam != null) {
					str = str.concat(inputParam.toString());
				}
			}
		}
		return str;
	}

	static {
		fi.getCategories().add(FunctionCategory.String);
		fi.getCategories().add(FunctionCategory.ALL);
	}

	public FunctionInfo getFunctionInfo() {
		return fi;
	}
}
