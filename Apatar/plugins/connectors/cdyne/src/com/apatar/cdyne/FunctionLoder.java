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
 


package com.apatar.cdyne;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.jdom.JDOMException;

import com.apatar.core.ReadWriteXMLData;
import com.apatar.ui.NodeFactory;

public class FunctionLoder {
	static List<NodeFactory> listFunction;
	public static Map<String, ClassLoader> functionLoaders = new Hashtable<String, ClassLoader>();
	
	public static void loadFunctionNodesPlugins() {
		listFunction = new LinkedList<NodeFactory>();
		Element root;
		try {
			root = ReadWriteXMLData.getRootElement(CdyneUtils.functionFile);
			for (Object funcObj : root.getChildren("function")) {
				Element func = (Element)funcObj;
				String className = func.getAttributeValue("className");
				//ClassLoader classLoader = ClassLoader.getSystemClassLoader();
				ClassLoader classLoader = CdyneFunctionNodeFactory.class.getClassLoader();
				Class nodeFactClass = classLoader.loadClass("com.apatar.cdyne.CdyneFunctionNodeFactory");
				NodeFactory factory = (CdyneFunctionNodeFactory)nodeFactClass.getConstructor(
						new Class[]{
								ClassLoader.class,
								String.class
								})
								.newInstance(new Object[] {
										classLoader,
										className
								});
				listFunction.add(factory);
				functionLoaders.put(className, classLoader);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadNodeFunction(String className, List<NodeFactory> functionNodeFactory) {
		try {
			ClassLoader classLoader = CdyneFunctionNodeFactory.class.getClassLoader();
			Class nodeFactClass = classLoader.loadClass("com.apatar.cdyne.CdyneFunctionNodeFactory");
			NodeFactory factory = (CdyneFunctionNodeFactory)nodeFactClass.getConstructor(
					new Class[]{
							ClassLoader.class,
							String.class
							})
							.newInstance(new Object[] {
									classLoader,
									className
							});
			functionNodeFactory.add(factory);
			functionLoaders.put(className, classLoader);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} 
	}

}

