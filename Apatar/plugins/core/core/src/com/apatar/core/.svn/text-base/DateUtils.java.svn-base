package com.apatar.core;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Utility function to work with dates in SalesForce and QuickBooks format.
 * 
 * @author Maksim Mostovnikov
 * @author Ivan Holub
 */
public abstract class DateUtils {

	private static class QuickBooksDateFormat extends SimpleDateFormat {
		private static final String PRE_FORMAT = "yyyy-MM-dd'T'HH:mm:SSSZ";

		public QuickBooksDateFormat() {
			super(PRE_FORMAT);
		}

		@Override
		public StringBuffer format(Date date, StringBuffer toAppendTo,
				FieldPosition pos) {
			StringBuffer buffer = super.format(date, toAppendTo, pos);
			buffer.insert(22, ':');
			return buffer;
		}
	}

	/**
	 * Date format for short QB dates (like 2008-10-16).
	 */
	private static final DateFormat QUICKBOOKS_DATE_FORMAT_SHORT = new SimpleDateFormat(
			"yyyy-MM-dd");

	/**
	 * Date format for short SF dates (like 2008-10-16).
	 */
	private static final String SALESFORCE_DATE_FORMAT_SHORT_STR = "yyyy-MM-dd";

	/**
	 * Date format for short SF dates (like 2008-10-16).
	 */
	private static final DateFormat SALESFORCE_DATE_FORMAT_SHORT = new SimpleDateFormat(
			SALESFORCE_DATE_FORMAT_SHORT_STR);

	/**
	 * Format of date that comes from QuickBooks.
	 */
	private static DateFormat quickBooksDateFormat = new QuickBooksDateFormat(); // 2008-02-22T11:17:30+02:00

	/**
	 * Format of date that comes from SalesForce.
	 */
	private static DateFormat salesForceDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // 2008-02-22T09:47:24.000Z

	/**
	 * GMT time zone.
	 */
	private static final TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");

	static {
		QUICKBOOKS_DATE_FORMAT_SHORT.setTimeZone(gmtTimeZone);
		SALESFORCE_DATE_FORMAT_SHORT.setTimeZone(gmtTimeZone);
		quickBooksDateFormat.setTimeZone(gmtTimeZone);
		salesForceDateFormat.setTimeZone(gmtTimeZone);
	}

	/**
	 * Parses string that represents QuickBooks date to produce a date.
	 * <p/>
	 * QuickBooks doesn't take into consideration summer time. For example, if
	 * the current date is "Fri Apr 18 11:36:38 EEST 2008" then QuickBooks
	 * writes date "2008-04-18T11:36:38+02:00", but correct date must be
	 * "2008-04-18T11:36:38+03:00".
	 * <p/>
	 * To obtain correct date the function subtracts 1 hour from parsed date if
	 * this date is from summer time.
	 * 
	 * @param dateString
	 *            date represented as string
	 * @param timeZone
	 *            time zone in which QuickBooks works
	 * @return date parsed from the string
	 * @throws ParseException
	 *             if parsing fails
	 */
	public static Date parseQbDate(String dateString, TimeZone timeZone)
			throws ParseException { // 2008-02-10T19:31:14+02:00

		Date date = null;
		StringBuffer stringBuffer = new StringBuffer(dateString);

		int indPlus = dateString.lastIndexOf("+");

		if (indPlus == -1) {
			indPlus = dateString.lastIndexOf("-");
		}

		if (indPlus != -1) {
			String timeZoneStr = dateString.substring(indPlus, dateString
					.length());
			if (timeZoneStr.length() == 6) {
				String dateToParse = stringBuffer.deleteCharAt(
						dateString.length() - 3).toString();
				date = quickBooksDateFormat.parse(dateToParse);
			} else {
				date = quickBooksDateFormat.parse(stringBuffer.toString());
			}

			if (timeZone.inDaylightTime(date)) {

				int dstSavings = timeZone.getDSTSavings();

				GregorianCalendar calendar = new GregorianCalendar(gmtTimeZone);
				calendar.setTime(date);
				calendar.add(Calendar.MILLISECOND, -dstSavings);

				date = calendar.getTime();
			}
		}
		return date;
	}

	/**
	 * Formats date into a date string according to QuickBooks date format
	 * (DATETIMETYPE)
	 * <p/>
	 * QuickBooks doesn't take into consideration summer time. For example, if
	 * the current date is "Fri Apr 18 11:36:38 EEST 2008" then QuickBooks
	 * writes date "2008-04-18T11:36:38+02:00", but correct date must be
	 * "2008-04-18T11:36:38+03:00".
	 * <p/>
	 * To obtain correct date string the function adds 1 hour to given date if
	 * this date is from summer time.
	 * 
	 * @param date
	 *            the given date
	 * @param timeZone
	 *            time zone in which QuickBooks works
	 * @return string representation of date
	 */
	public static String formatDateQb(Date date, TimeZone timeZone) {

		if (timeZone.inDaylightTime(date)) {

			int dstSavings = timeZone.getDSTSavings();

			GregorianCalendar calendar = new GregorianCalendar(gmtTimeZone);
			calendar.setTime(date);
			calendar.add(Calendar.MILLISECOND, dstSavings);

			date = calendar.getTime();
		}

		return quickBooksDateFormat.format(date);
	}

	public static Date parseSalesForceDate(String dateString)
			throws ParseException { // 2008-02-08T18:00:40.000Z
		Date date = salesForceDateFormat.parse(dateString);
		return date;
	}

	/**
	 * Formats a Date into a SalesForce date/time string.
	 * 
	 * @param date
	 *            the time value to be formatted into a date/time string
	 * @return the formatted date/time string
	 */
	public static String formatDateSf(Date date) {
		return salesForceDateFormat.format(date);
	}

	/**
	 * !!! Dates must be in one timezone !!!
	 * 
	 * @param strDate1
	 *            the date in SalesForce format
	 * @param strDate2
	 *            the date in SalesForce format
	 * @param strDate3
	 *            the date in SalesForce format
	 * @return the newest notnull date, or null if all dates are null
	 */
	public static String sfSelectTheNewestDate(String strDate1,
			String strDate2, String strDate3) {
		String strDate12 = sfSelectTheNewestDate(strDate1, strDate2);
		String retValue = sfSelectTheNewestDate(strDate12, strDate3);
		return retValue;
	}

	/**
	 * !!! Dates must be in one timezone !!! Otherwise throws
	 * {@link IllegalStateException}
	 * 
	 * @param strDate1
	 *            the date in SalesForce format
	 * @param strDate2
	 *            the date in SalesForce format
	 * @return the newest notnull date, or null if all dates are null
	 */
	public static String sfSelectTheNewestDate(String strDate1, String strDate2) {

		if (strDate1 == null) {
			return strDate2;
		}

		if (strDate2 == null) {
			return strDate1;
		}

		if (!strDate1.substring(strDate1.length() - 5).equals(
				strDate2.substring(strDate2.length() - 5))) {
			throw new IllegalStateException(
					"SalesForce dates must be of one timezone:('" + strDate1
							+ "','" + strDate2 + "')");
		}

		return strDate1.compareTo(strDate2) > 0 ? strDate1 : strDate2;
	}

	/**
	 * Parses short QB date (like 2008-10-16).
	 * 
	 * @param dateString
	 *            short QB date
	 * @return date in GMT
	 */
	public static Date parseQbDateShort(String dateString) { // 2008-02-10
		if (dateString == null) {
			return null;
		}
		try {
			Date date = QUICKBOOKS_DATE_FORMAT_SHORT.parse(dateString);
			return date;
		} catch (ParseException e) {
			throw new RuntimeException("Can't parse date, date: " + dateString,
					e);
		}
	}

	/**
	 * Formats short SF date (like 2008-10-16).
	 * 
	 * @param date
	 *            the date in GMT
	 * @return short SF date
	 */
	public static String formatDateSfShort(Date date) {
		if (date == null) {
			return null;
		}
		return SALESFORCE_DATE_FORMAT_SHORT.format(date);
	}

	/**
	 * Formats short SF date (like 2008-10-16) using specified time zone.
	 * 
	 * @param date
	 *            the date
	 * @param timeZone
	 *            the time zone
	 * @return short SF date
	 */
	public static String formatDateSfShort(Date date, TimeZone timeZone) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(
				SALESFORCE_DATE_FORMAT_SHORT_STR);
		sdf.setTimeZone(timeZone);
		return sdf.format(date);
	}

}
