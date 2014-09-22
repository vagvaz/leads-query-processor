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

package com.apatar.functions.String.data;

import java.sql.Types;
import java.util.List;

import com.apatar.core.AbstractApatarFunction;
import com.apatar.functions.FunctionInfo;
import com.apatar.functions.FunctionNodeFactory;
import com.apatar.ui.FunctionCategory;

public class ToBooleanTransformFunction extends AbstractApatarFunction {
	public Object execute(List list) {

		if (null == list) {
			return 0;
		}
		Object value = list.get(0);

		if (value instanceof Boolean) {
			if ((Boolean) value == true) {
				value = 1;
			} else {
				value = 0;
			}
		} else if (value instanceof Number) {
			if (((Number) value).intValue() != 0) {
				value = 1;
			} else {
				value = 0;
			}
		} else if (value instanceof String) {
			if (((String) value).equalsIgnoreCase("true")
					|| ((String) value).equalsIgnoreCase("yes")
					|| ((String) value).equalsIgnoreCase("y")
					|| ((String) value).equals("1")) {
				value = 1;
			} else {
				try {
					if (Double.parseDouble((String) value) != 0.0) {
						value = 1;
					} else {
						value = 0;
					}
				} catch (Exception e) {
					value = 0;
				}
				// value = 0;
			}
		} else {
			value = 0;
		}

		return value;
	}

	static FunctionInfo fi = new FunctionInfo("To Boolean", 1, 1);
	static {
		fi.getCategories().add(FunctionCategory.String);
		fi.getCategories().add(FunctionCategory.Boolean);
		fi.getCategories().add(FunctionCategory.ALL);
		fi.getCategories().add(FunctionCategory.Number);
	}

	public FunctionInfo getFunctionInfo() {
		return fi;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.apatar.core.AbstractApatarFunction#getAntipodeFunction(java.lang.
	 * Object)
	 */
	@Override
	public AbstractApatarFunction getAntipodeFunction(Object base) {
		AbstractApatarFunction result = null;
		if (base instanceof Integer) {

			FunctionNodeFactory fnc = null;
			switch ((Integer) base) {
			case Types.BIGINT: // ToInt64
				fnc = new FunctionNodeFactory(this.getClass().getClassLoader(),
						"ToInt64TransformFunction");
				// result = fnc.
				break;

			case Types.INTEGER: // ToInt32
			case Types.SMALLINT: // ToInt32

				break;

			case Types.TINYINT: // ToInt16

				break;

			case Types.BIT: // ToSingle

				break;

			case Types.DECIMAL: // ToDecimal

				break;

			case Types.DOUBLE: // ToDouble
			case Types.FLOAT:
			case Types.NUMERIC:
			case Types.REAL:

				break;

			case Types.CHAR: // ToString
			case Types.CLOB:
			case Types.LONGVARCHAR:
			case Types.VARCHAR:

				break;
			}
		}
		return result;
	}

}
