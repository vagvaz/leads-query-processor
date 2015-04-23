/*TODO recorded refactoring
 * в класс DateAndTimeSettings добавлена имплементация интерфейса IPersistent
 * *********************
 */
/*
 _______________________

 Apatar Open Source Data Integration

 Copyright (C) 2005-2007, Apatar, Inc.

 info@apatar.com

 195 Meadow St., 2nd Floor

 Chicopee, MA 01013



 This program is free software; you can redistribute it and/or modify

 it under the terms of the GNU General Public License as published by

 the Free Software Foundation; either version 2 of the License, or

 (at your option) any later version.



 This program is distributed in the hope that it will be useful,

 but WITHOUT ANY WARRANTY; without even the implied warranty of

 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the

 GNU General Public License for more details.



 You should have received a copy of the GNU General Public License along

 with this program; if not, write to the Free Software Foundation, Inc.,

 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

 ________________________

 */

package com.apatar.core;

import java.text.SimpleDateFormat;

import org.jdom.Element;

public class DateAndTimeSettings implements IPersistent {
	boolean createWithApplication = false;
	String pattern;

	SimpleDateFormat format;

	String dateFormat = "";
	String dateSeparator = "";
	String timeFormat = "";
	String timeStandart = "";

	public DateAndTimeSettings() {
		super();
		format = new SimpleDateFormat();
		pattern = format.toPattern();
	}

	public DateAndTimeSettings(String pattern) {
		super();
		this.pattern = pattern;
	}

	public DateAndTimeSettings(boolean createWithApplication, String pattern) {
		super();
		this.createWithApplication = createWithApplication;
		this.pattern = pattern;
		format = new SimpleDateFormat(pattern);
	}

	public DateAndTimeSettings(boolean createWithApplication, String pattern,
			String dateFormat, String dateSeparator, String timeFormat,
			String timeStandart) {
		super();
		this.createWithApplication = createWithApplication;
		this.pattern = pattern;
		this.dateFormat = dateFormat;
		this.dateSeparator = dateSeparator;
		this.timeFormat = timeFormat;
		this.timeStandart = timeStandart;
		format = new SimpleDateFormat(pattern);
	}

	public void init(boolean createWithApplication, String pattern) {
		this.pattern = pattern;
		format = new SimpleDateFormat(pattern);
		this.createWithApplication = createWithApplication;
		dateFormat = "";
		dateSeparator = "";
		timeFormat = "";
	}

	/*
	 * public void init(String pattern) { init(false, pattern); }
	 */

	public void init(String pattern, String dateFormat, String dateSeparator,
			String timeFormat, String timeStandart) {
		createWithApplication = true;
		this.pattern = pattern;
		this.dateFormat = dateFormat;
		this.dateSeparator = dateSeparator;
		this.timeFormat = timeFormat;
		this.timeStandart = timeStandart;
		format = new SimpleDateFormat(pattern);
	}

	public void init(DateAndTimeSettings dts) {
		createWithApplication = dts.createWithApplication;
		pattern = dts.pattern;
		dateFormat = dts.dateFormat;
		dateSeparator = dts.dateSeparator;
		timeFormat = dts.timeFormat;
		timeStandart = dts.timeStandart;
		format = new SimpleDateFormat(pattern);
	}

	public boolean isCreateWithApplication() {
		return createWithApplication;
	}

	public void setCreateWithApplication(boolean createWithApplication) {
		this.createWithApplication = createWithApplication;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getDateSeparator() {
		return dateSeparator;
	}

	public void setDateSeparator(String dateSeparator) {
		this.dateSeparator = dateSeparator;
	}

	public String getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}

	public String getTimeStandart() {
		return timeStandart;
	}

	public void setTimeStandart(String timeStandart) {
		this.timeStandart = timeStandart;
	}

	public Element saveToElement() {
		Element elem = new Element("DateAndTime");
		elem.setAttribute("createWithApplication", "" + createWithApplication);
		elem.setAttribute("dateFormat", dateFormat);
		elem.setAttribute("dateSeparator", dateSeparator);
		elem.setAttribute("timeFormat", timeFormat);
		elem.setAttribute("timeStandart", timeStandart);
		Element elPattern = new Element("pattern");
		elPattern.setText(pattern);
		elem.addContent(elPattern);
		return elem;
	}

	public void initFromElement(Element elem) {
		createWithApplication = Boolean.parseBoolean(elem
				.getAttributeValue("createWithApplication"));

		pattern = elem.getChildText("pattern");

		format = new SimpleDateFormat(pattern);

		if (!createWithApplication) {
			return;
		}
		dateFormat = elem.getAttributeValue("dateFormat");
		dateSeparator = elem.getAttributeValue("dateSeparator");
		timeFormat = elem.getAttributeValue("timeFormat");
		timeStandart = elem.getAttributeValue("timeStandart");

	}

	public SimpleDateFormat getFormat() {
		return format;
	}

	@Override
	public boolean equals(Object obj) {
		DateAndTimeSettings dts = (DateAndTimeSettings) obj;
		if (dts.isCreateWithApplication() == createWithApplication
				&& dts.getPattern().equals(pattern)) {
			return true;
		}
		return false;
	}

}
