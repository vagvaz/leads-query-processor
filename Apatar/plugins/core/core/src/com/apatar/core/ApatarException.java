/*TODO recorded refactoring
 * added exception class ApatarException
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

/**
 * @author Konstantin Maximchik
 * 
 */
public class ApatarException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String message;

	public ApatarException() {
		message = null;
	}

	public ApatarException(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "Apatar Exception: " + message;
	}
}
