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

import java.sql.Time;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.apatar.core.AbstractApatarFunction;
import com.apatar.functions.FunctionInfo;
import com.apatar.ui.FunctionCategory;

public class ToTimeTransformFunction extends AbstractApatarFunction {

	public Object execute(List list) {
		Object retValue = null;
		Object value = list.get(0);

		if (value instanceof String) {
			Time time = null;
			try {
				time = Time.valueOf(value.toString());
				retValue = time;
			} catch (Exception e1) {
				retValue = value;
			}
		} else {
			if (value instanceof Date) {
				Date date = (Date) value;
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				String strDate = df.format(date).substring(11);
				GregorianCalendar gc = new GregorianCalendar();
				gc.set(Calendar.YEAR, 0);
				gc.set(Calendar.MONTH, 0);
				gc.set(Calendar.DAY_OF_MONTH, 0);
				gc.set(Calendar.HOUR_OF_DAY, Integer.valueOf(strDate.substring(
						0, 2)));
				gc.set(Calendar.MINUTE, Integer
						.valueOf(strDate.substring(3, 5)));
				gc.set(Calendar.SECOND, Integer
						.valueOf(strDate.substring(6, 8)));
				gc.set(Calendar.MILLISECOND, Integer.valueOf(strDate
						.substring(9)));

				retValue = new Time(gc.getTimeInMillis());
			} else {
				retValue = value;
			}
		}

		return retValue;

	}

	static FunctionInfo fi = new FunctionInfo("To Time", 1, 1);
	static {
		fi.getCategories().add(FunctionCategory.String);
		fi.getCategories().add(FunctionCategory.Date_and_Time);
		fi.getCategories().add(FunctionCategory.ALL);
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
			switch ((Integer) base) {
			case Types.BIGINT: // ToInt64

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

			case Types.CHAR:
			case Types.CLOB:
			case Types.LONGVARCHAR:
			case Types.VARCHAR:

				break;

			case Types.DATE:
			case Types.TIME:
			case Types.TIMESTAMP:

				break;
			}
		}
		return result;

	}

}
