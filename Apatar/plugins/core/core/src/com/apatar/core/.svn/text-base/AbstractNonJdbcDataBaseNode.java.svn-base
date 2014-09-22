/*TODO recorded refactoring
 * добавлен класс AbstractNonJdbcDataBaseNode для имплементации абстрактных
 * методов, объявленных в родительском классе, но которые должны индивидуально
 * (если это возможно) быть реализованы в потомках. Это актуально для
 * web-сервисов и других коннекторов, не являющимися собственно базами данных
 * Все не JDBC коннекторы должны наследоваться именно от этого класса
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

import java.util.List;

/**
 * @author Konstantin Maximchik
 */
public abstract class AbstractNonJdbcDataBaseNode extends AbstractDataBaseNode {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.AbstractDataBaseNode#executeUpdateQuery(java.lang.String)
	 */
	@Override
	public int executeUpdateQuery(String query) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.AbstractDataBaseNode#getTotalRecodrsCount(com.apatar.core.TableInfo)
	 */
	@Override
	public int getTotalRecodrsCount(TableInfo ti) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.AbstractDataBaseNode#getTotalRecodrsCount(com.apatar.core.TableInfo,
	 *      com.apatar.core.JdbcParams)
	 */
	@Override
	public int getTotalRecodrsCount(TableInfo ti, JdbcParams params) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.AbstractDataBaseNode#moveDataFromTempToReal(java.util.List,
	 *      com.apatar.core.TableInfo)
	 */
	@Override
	public void moveDataFromTempToReal(List<String> identificationFields,
			TableInfo inputTi) {

	}

}
