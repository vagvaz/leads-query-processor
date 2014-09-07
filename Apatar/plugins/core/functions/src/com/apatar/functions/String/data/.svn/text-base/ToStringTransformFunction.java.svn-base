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

import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Types;
import java.util.List;

import com.apatar.core.AbstractApatarFunction;
import com.apatar.functions.FunctionInfo;
import com.apatar.ui.FunctionCategory;

public class ToStringTransformFunction extends AbstractApatarFunction {

	public Object execute(List list) {
		Object value = list.get(0);
		Object retValue = value;

		if (value != null) {
			if (value instanceof Clob) {
				Clob clobValue = (Clob) value;
				try {
					Reader f = clobValue.getCharacterStream();
					StringBuffer sb = new StringBuffer();
					char[] buf = new char[32768];
					int len;
					try {
						while ((len = f.read(buf, 0, buf.length)) >= 0) {
							sb.append(buf, 0, len);
						}
					} catch (Exception e) {
						// e.printStackTrace();
					}
					retValue = sb.toString();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (value instanceof Blob) {
				Blob blobValue = (Blob) value;
				try {
					InputStreamReader f = new InputStreamReader(blobValue
							.getBinaryStream());
					StringBuffer sb = new StringBuffer();
					char[] buf = new char[32768];
					int len;
					while ((len = f.read(buf, 0, buf.length)) >= 0) {
						sb.append(buf, 0, len);
					}
					return sb.toString();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (value instanceof Integer || value instanceof Long
					|| value instanceof Double || value instanceof BigDecimal
					|| value instanceof BigInteger || value instanceof Float
					|| value instanceof Byte || value instanceof Long
					|| value instanceof Boolean) {
				retValue = String.valueOf(value);
			}
			if (value instanceof Date) {
				Date nValue = (Date) value;
				retValue = nValue.toString();
			}
		} else {
			retValue = value;
		}

		return retValue;
	}

	static FunctionInfo fi = new FunctionInfo("To String", 1, 1);
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
