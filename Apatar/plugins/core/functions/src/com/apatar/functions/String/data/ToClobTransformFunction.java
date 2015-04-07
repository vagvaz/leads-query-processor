package com.apatar.functions.String.data;

/*
 _______________________
 Apatar Open Source Data Integration
 Copyright (C) 2005-2007, Apatar, Inc.
 info@apatar.com
 195 Meadow St., 2nd Floor
 Chicopee, MA 01013

 ��� This program is free software; you can redistribute it and/or modify
 ��� it under the terms of the GNU General Public License as published by
 ��� the Free Software Foundation; either version 2 of the License, or
 ��� (at your option) any later version.

 ��� This program is distributed in the hope that it will be useful,
 ��� but WITHOUT ANY WARRANTY; without even the implied warranty of
 ��� MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.� See the
 ��� GNU General Public License for more details.

 ��� You should have received a copy of the GNU General Public License along
 ��� with this program; if not, write to the Free Software Foundation, Inc.,
 ��� 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 ________________________

 */

import java.util.List;

import javax.sql.rowset.serial.SerialClob;

import com.apatar.core.AbstractApatarFunction;
import com.apatar.functions.FunctionInfo;
import com.apatar.ui.FunctionCategory;

public class ToClobTransformFunction extends AbstractApatarFunction {

	public Object execute(List list) {
		Object value = list.get(0);
		Object retValue = value;

		if (value != null) {
			try {
				SerialClob sclobValue = new SerialClob(((String) value)
						.toCharArray());
				// sclobValue.setString(1, value.toString());
				return sclobValue;
			} catch (Exception e) {
				e.printStackTrace();
				return value;
			}
		} else {
			retValue = value;
		}

		return retValue;
	}

	static FunctionInfo fi = new FunctionInfo("To Clob", 1, 1);
	static {
		fi.getCategories().add(FunctionCategory.ALL);
		fi.getCategories().add(FunctionCategory.String);
		fi.getCategories().add(FunctionCategory.Number);
		fi.getCategories().add(FunctionCategory.Date_and_Time);
		fi.getCategories().add(FunctionCategory.Boolean);
	}

	public FunctionInfo getFunctionInfo() {
		return fi;
	}

}
