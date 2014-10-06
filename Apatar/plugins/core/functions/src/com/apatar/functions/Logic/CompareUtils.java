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

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.apatar.core.ApatarException;
import com.apatar.core.ApplicationData;

public class CompareUtils {
	/*
	 * public static int Compare(Object ob, String inputStr) throws
	 * ParseException { // cast to the identical type if (ob instanceof Integer)
	 * { return ((Integer) ob).compareTo(Integer.parseInt(inputStr)); } else if
	 * (ob instanceof Long) { return ((Long)
	 * ob).compareTo(Long.parseLong(inputStr)); } else if (ob instanceof Double)
	 * { return ((Double) ob).compareTo(Double.parseDouble(inputStr)); } else if
	 * (ob instanceof String) { return ((String) ob).compareTo(inputStr); } else
	 * if (ob instanceof Date) { try { if (ob instanceof Timestamp) { ob = new
	 * Date(((Timestamp) ob).getTime()); } return ((Date) ob)
	 * .compareTo(ApplicationData.DATAMAP_DATE_SETTINGS
	 * .getFormat().parse(inputStr)); } catch (ParseException e) {
	 * ApplicationData.ProcessingProgress.Log(e);
	 * ApplicationData.ProcessingProgress.Log("Format must be " +
	 * ApplicationData.DATAMAP_DATE_SETTINGS.getFormat() .toPattern()); throw e;
	 * } } return 1; }
	 */

	public static int Compare(Object ob, Object obj1) throws ApatarException {

		if (ob == null && obj1 == null) {
			return 0;
		}
		if (ob == null || obj1 == null) {
			return 1;
		}

		if (ob instanceof Number) {
			return compareByType((Number) ob, obj1);
		} else if (ob instanceof Boolean) {
			return compareByType((Boolean) ob, obj1);
		} else if (ob instanceof String) {
			return compareByType((String) ob, obj1);
		} else if (ob instanceof Timestamp) {
			return compareByType((Timestamp) ob, obj1);
		} else if (ob instanceof Date) {
			return compareByType((Date) ob, obj1);
		} else {
			throw new ApatarException("Cannot compare two objects: "
					+ ob.getClass().getName() + " and "
					+ obj1.getClass().getName());
		}
	}

	private static int compareByType(Boolean obj1, Object obj2)
			throws ApatarException {
		int obj1int = ((Boolean) obj1 == true ? 1 : 0);
		int obj2int;
		if (obj2 instanceof Boolean) {
			obj2int = ((Boolean) obj2 == true ? 1 : 0);
			return compareByType(obj1int, obj2int);
		} else if (obj2 instanceof Number) {
			return compareByType(obj1int, obj2);
		} else if (obj2 instanceof String) {
			String obj2String = (String) obj2;
			if ("true".equalsIgnoreCase(obj2String)
					|| "yes".equalsIgnoreCase(obj2String)) {
				obj2int = 1;
				return compareByType(obj1int, obj2int);
			} else if ("false".equalsIgnoreCase(obj2String)
					|| "no".equalsIgnoreCase(obj2String)) {
				obj2int = 0;
				return compareByType(obj1int, obj2int);
			} else {
				try {
					Double obj2Double = Double.valueOf(obj2String);
					return compareByType(obj1int, obj2Double);
				} catch (Exception e) {
					throw new ApatarException(
							"Cannot compare Boolean and String: "
									+ obj1.getClass().getName() + " and "
									+ obj2.getClass().getName()
									+ ". String has'nt proper Number value.");
				}
			}
		} else {
			throw new ApatarException(
					"Cannot compare Boolean and other object: "
							+ obj1.getClass().getName() + " and "
							+ obj2.getClass().getName());
		}
	}

	private static int compareByType(Number obj1, Object obj2)
			throws ApatarException {
		if (obj1 instanceof Integer) {
			return compareNumberTo(obj1.intValue(), obj2);
		} else if (obj1 instanceof Short) {
			return compareNumberTo(obj1.shortValue(), obj2);
		} else if (obj1 instanceof Byte) {
			return compareNumberTo(obj1.byteValue(), obj2);
		} else if (obj1 instanceof Double) {
			return compareNumberTo(obj1.doubleValue(), obj2);
		} else if (obj1 instanceof Float) {
			return compareNumberTo(obj1.floatValue(), obj2);
		} else if (obj1 instanceof Long) {
			return compareNumberTo(obj1.longValue(), obj2);
		} else {
			throw new ApatarException(
					"Cannot compare Number and other object: "
							+ obj1.getClass().getName() + " and "
							+ obj2.getClass().getName());
		}
	}

	private static int compareNumberTo(Integer obj1, Object obj2)
			throws ApatarException {
		return compareNumberTo(obj1.longValue(), obj2);
	}

	private static int compareNumberTo(Short obj1, Object obj2)
			throws ApatarException {
		return compareNumberTo(obj1.longValue(), obj2);
	}

	private static int compareNumberTo(Byte obj1, Object obj2)
			throws ApatarException {
		return compareNumberTo(obj1.longValue(), obj2);
	}

	private static int compareNumberTo(Long obj1, Object obj2)
			throws ApatarException {
		if (obj2 instanceof Boolean) {
			Long obj2Long = ((Integer) ((Boolean) obj2 == true ? 1 : 0))
					.longValue();
			return obj1.compareTo(obj2Long);
		} else if (obj2 instanceof Date) {
			GregorianCalendar obj2Date = getCalendarByDate((Date) obj2);

			return obj1.compareTo(obj2Date.getTimeInMillis());

		} else if (obj2 instanceof String) {
			try {
				Long obj2Long = Long.valueOf((String) obj2);
				return obj1.compareTo(obj2Long);
			} catch (Exception e) {
				throw new ApatarException("Cannot compare Number and String: "
						+ obj1.getClass().getName() + " and "
						+ obj2.getClass().getName()
						+ ". String has'nt proper Number value.");
			}
		} else if (obj2 instanceof Number) {
			if (obj2 instanceof Double || obj2 instanceof Float) {
				Double obj1Double = obj1.doubleValue();
				return obj1Double.compareTo(((Number) obj2).doubleValue());
			} else {
				return obj1.compareTo(((Number) obj2).longValue());
			}
		} else {
			throw new ApatarException(
					"Cannot compare Number and other object: "
							+ obj1.getClass().getName() + " and "
							+ obj2.getClass().getName());
		}
	}

	private static int compareNumberTo(Double obj1, Object obj2)
			throws ApatarException {
		if (obj2 instanceof Date) {
			GregorianCalendar obj2Date = getCalendarByDate((Date) obj2);

			return obj1.compareTo(((Number) obj2Date.getTimeInMillis())
					.doubleValue());

		} else if (obj2 instanceof String) {
			try {
				Double obj2Double = Double.valueOf((String) obj2);
				return obj1.compareTo(obj2Double);
			} catch (Exception e) {
				throw new ApatarException(
						"Cannot compare Number (Double) and String: "
								+ obj1.getClass().getName() + " and "
								+ obj2.getClass().getName()
								+ ". String has'nt proper Number value.");
			}
		} else if (obj2 instanceof Number) {
			return obj1.compareTo(((Number) obj2).doubleValue());
		} else {
			throw new ApatarException(
					"Cannot compare Number and other object: "
							+ obj1.getClass().getName() + " and "
							+ obj2.getClass().getName());
		}
	}

	private static GregorianCalendar getCalendarByDate(Date obj2) {
		GregorianCalendar d1 = new GregorianCalendar();
		d1.setTime(obj2);
		if (obj2 instanceof java.sql.Time) {
			d1.set(Calendar.YEAR, 0);
			d1.set(Calendar.MONTH, 0);
			d1.set(Calendar.DAY_OF_MONTH, 0);
		} else if (obj2 instanceof java.sql.Date) {
			d1.set(Calendar.MILLISECOND, 0);
			d1.set(Calendar.SECOND, 0);
			d1.set(Calendar.MINUTE, 0);
			d1.set(Calendar.HOUR_OF_DAY, 0);
		}
		return d1;
	}

	private static int compareNumberTo(Float obj1, Object obj2)
			throws ApatarException {
		return compareNumberTo(obj1.doubleValue(), obj2);
	}

	private static int compareByType(String obj1, Object obj2)
			throws ApatarException {
		if (obj2 instanceof String) {
			return obj1.compareTo((String) obj2);
		} else if (obj2 instanceof Date) {
			GregorianCalendar obj2Date = getCalendarByDate((Date) obj2);
			return obj1.compareTo(obj2Date.toString());
		} else if (obj2 instanceof Number) {
			return obj1.compareTo(((Number) obj2).toString());
		} else {
			throw new ApatarException(
					"Cannot compare String and other object: "
							+ obj1.getClass().getName() + " and "
							+ obj2.getClass().getName());
		}
	}

	private static int compareByType(Date obj1, Object obj2)
			throws ApatarException {
		GregorianCalendar obj1Date = getCalendarByDate(obj1);
		if (!(obj1 instanceof Timestamp)) {
			obj1Date.set(Calendar.HOUR_OF_DAY, 0);
			obj1Date.set(Calendar.MINUTE, 0);
			obj1Date.set(Calendar.SECOND, 0);
			obj1Date.set(Calendar.MILLISECOND, 0);
		}
		if (obj2 instanceof String) {
			DateFormat df = ApplicationData.DATAMAP_DATE_SETTINGS.getFormat();
			try {
				Date obj2Date = df.parse((String) obj2);
				return obj1Date.getTime().compareTo(obj2Date);
			} catch (Exception e) {
				throw new ApatarException("Cannot compare Date and String: "
						+ obj1.getClass().getName() + " and "
						+ obj2.getClass().getName()
						+ ". String has'nt proper Number value.");
			}

		} else if (obj2 instanceof Number) {
			GregorianCalendar obj2Date = new GregorianCalendar();
			try {
				if (obj2 instanceof Double || obj2 instanceof Float) {
					obj2Date.setTimeInMillis(Math.round(((Number) obj2)
							.doubleValue()));
				} else {
					obj2Date.setTimeInMillis(((Number) obj2).longValue());
				}
				return obj1Date.compareTo(obj2Date);
			} catch (Exception e) {
				throw new ApatarException("Cannot compare Date and Number: "
						+ obj1.getClass().getName() + " and "
						+ obj2.getClass().getName());
			}
		} else if (obj2 instanceof Timestamp) {
			GregorianCalendar obj2Date = getCalendarByDate((Date) obj2);
			return obj1Date.compareTo(obj2Date);
		} else if (obj2 instanceof Date) {
			GregorianCalendar obj2Date = getCalendarByDate((Date) obj2);

			obj2Date.set(Calendar.HOUR_OF_DAY, 0);
			obj2Date.set(Calendar.MINUTE, 0);
			obj2Date.set(Calendar.SECOND, 0);
			obj2Date.set(Calendar.MILLISECOND, 0);

			return obj1Date.compareTo(obj2Date);
		} else {
			throw new ApatarException("Cannot compare Date and other object: "
					+ obj1.getClass().getName() + " and "
					+ obj2.getClass().getName());
		}
	}
	// we have 3 main groups to compare: Number, String, Date

	// Number-String
	// Number-Number
	// Number-Date

	// String-Number
	// String-String
	// String-Date

	// Date-Number
	// Date-Date
	// Date-String

	// Boolean
	// Number
	// Byte
	// Double
	// Float
	// Integer
	// Long
	// Short
	// String
	// Date

}
