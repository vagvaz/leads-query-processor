/*TODO recorded refactoring
 * добавлен абстрактный класс AbstractMainApatarPluginActions объявляющий методы событий, происходящих при старте приложения.
 * *********************
 * класс AbstractMainApatarPluginActions унаследован от интерфейса IApatarActions
 * *********************
 * класс AbstractMainApatarPluginActions переименован в AbstractApatarActions
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
public abstract class AbstractApatarActions implements IApatarActions {
	protected final AbstractNode node;

	/**
	 * 
	 */
	public AbstractApatarActions() {
		super();
		node = null;
	}

	/**
	 * @param node
	 */
	public AbstractApatarActions(AbstractNode node) {
		super();
		this.node = node;
	}

	public abstract void beforeStart();

	public abstract boolean callRegistrationMethod();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.IApatarActions#dialogAction()
	 */
	public abstract void dialogAction(String message);

	public boolean configureFunctionAction(ApatarFunction func) {
		return false;
	}

	public boolean configureConstantFunctionAction(ApatarFunction func) {
		return false;
	}
}
