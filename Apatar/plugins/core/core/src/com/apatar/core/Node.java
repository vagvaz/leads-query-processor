/*TODO recorded refactoring
 * интерфейс Node унаследован от интерфейса IPersistent
 * *********************
 * сигнатуры методов
 *  public void edit(Window win);

	public boolean realEdit(Window win);

	public void afterEdit(boolean editRsult);

	изменены на:
    public void edit(AbstractApatarActions actions);

	public boolean realEdit(AbstractApatarActions actions);

	public void afterEdit(boolean editRsult, AbstractApatarActions actions);

	в результате в эти методы передаётся объект-потомок абстрактного класса
	AbstractApatarActions в котором соответствующие методы реализованы
	таким образом исключено использование в классах ядра объектов UI
 * *********************
 * *********************
 */

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

package com.apatar.core;

import java.util.Collection;
import java.util.List;

import javax.swing.Action;

import org.jdom.Element;

public interface Node extends IPersistent {

	/**
	 * core method.
	 * 
	 * @return unique number of the node whiting the project (datamap)
	 */
	public int getId();

	/**
	 * core method. sets unique number of the node whiting the project (datamap)
	 */
	public void setId(int id);

	/**
	 * core method.
	 * 
	 * @param name
	 *            connection point name
	 * @return connection point
	 */
	public ConnectionPoint getConnPoint(String name);

	/**
	 * core method.
	 * 
	 * @return connection points collection
	 */
	public Collection<ConnectionPoint> getConnPoints();

	/**
	 * core method.
	 * 
	 * @return input connection points collection
	 */
	public Collection<ConnectionPoint> getInputConnPoints();

	/**
	 * core method.
	 * 
	 * @return output connection points collection
	 */
	public Collection<ConnectionPoint> getOutputConnPoints();

	/**
	 * core method.
	 * 
	 * have to be removed, because it is unnecessary
	 * 
	 * @return connection points collection
	 */
	public Collection<ConnectionPoint> getExtConnPoints();

	/**
	 * core method
	 * 
	 * saves node to XML
	 * 
	 * @return XML element
	 */
	public Element saveToElement();

	/**
	 * core method.
	 * 
	 * restores node state from XML element
	 * 
	 * @param e
	 *            XML element
	 */
	public void initFromElement(Element e);

	/**
	 * core method.
	 * 
	 * build temporary schema table to be parsed as an input schema table for
	 * this node
	 * 
	 * @return SchemaTable
	 */
	public SchemaTable getExpectedShemaTable();

	/**
	 * core method.
	 * 
	 * @return AbstractNode.LAST_POSITION - if node has nodes, connected to
	 *         input and no nodes, connected to output (means last node in
	 *         chain). AbstractNode.FIRST_POSITION - if node has nodes,
	 *         connected to output and no nodes, connected to input (means first
	 *         node in chain). AbstractNode.MIDDLE_POSITION - if node has both
	 *         input and output nodes connected.
	 */

	public int getInlinePosition();

	/**
	 * Assume UI method.
	 * 
	 * Assume that it is completed.
	 * 
	 * @return null.
	 */
	public List<Action> getSpecialAction();

	/**
	 * Assume UI method.
	 * 
	 * actions to be performed before main edit action (when configuring node)
	 */
	public void beforeEdit();

	/**
	 * Assume UI method.
	 * 
	 * method that implements workflow of actions to be performed when
	 * configuring node: beforeEdit() -> realEdit() -> afterEdit().
	 */
	public void edit(AbstractApatarActions actions);

	/**
	 * Assume UI method.
	 * 
	 * actions to be performed when configuring node
	 */
	public boolean realEdit(AbstractApatarActions actions);

	/**
	 * Assume UI method.
	 * 
	 * actions to be performed after main edit action (when configuring node)
	 */
	public void afterEdit(boolean editRsult, AbstractApatarActions actions);

}
