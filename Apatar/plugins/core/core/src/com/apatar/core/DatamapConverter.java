/*TODO recorded refactoring
 * добавлен класс DatamapConverter. Предназначен для конвертирования датамапов из версий старше 1.2 в текущую
 * *********************
 */

/*
 _______________________
 Apatar Open Source Data Integration
 Copyright (C) 2005-2008, Apatar, Inc.
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
package com.apatar.core;

import java.util.regex.Pattern;

/**
 * @author Konstantin Maximchik
 *
 */
public class DatamapConverter {

	private String originalContent;
	private String convertedContent;

	/**
	 * @param originalContent
	 */
	public DatamapConverter(String content) {
		super();
		originalContent = content;
	}

	public DatamapConverter() {
		super();
	}

	/**
	 * @return the convertedContent
	 */
	public String getConvertedContent() {
		return convertedContent;
	}

	/**
	 * Processing datamap conversion
	 *
	 * @return true
	 */
	public boolean ConvertDatamap() {
		convertedContent = originalContent.replaceAll(Pattern.quote("com.altoros.octoslave.mysql.MsSqlNode"), "com.apatar.mssql.MsSqlNode");
		convertedContent = convertedContent.replaceAll(Pattern.quote("com.altoros.octoslave.function."), "com.apatar.functions.");
		convertedContent = convertedContent.replaceAll(Pattern.quote("com.altoros.octoslave.xml.RssNode"), "com.apatar.rss.RssNode");
		convertedContent = convertedContent.replaceAll(Pattern.quote("com.altoros.octoslave.postgresql.EnterprisedbNode"), "com.apatar.enterprisedb.EnterprisedbNode");
		convertedContent = convertedContent.replaceAll(Pattern.quote("com.altoros.octoslave.generaljdbc"), "com.apatar.ldap");
		convertedContent = convertedContent.replaceAll(
				"com\\.altoros\\.octoslave\\.", "com.apatar.");
		convertedContent = convertedContent.replaceFirst(
				"(<document version=\")Apatar_v.*?(\">)", "$1"
						+ ApplicationData.VERSION + "$2");
		return true;
	}

	/**
	 * @return the originalContent
	 */
	public String getOriginalContent() {
		return originalContent;
	}

	/**
	 * @param originalContent
	 *            the originalContent to set
	 */
	public void setOriginalContent(String originalContent) {
		this.originalContent = originalContent;
	}
}
