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

import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.java.plugin.Plugin;
import org.java.plugin.PluginLifecycleException;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.registry.Extension.Parameter;

import com.apatar.functions.ConstantFunctionNodeFactory;
import com.apatar.functions.FunctionNodeFactory;
import com.apatar.functions.ReplaceIfFunctionNodeFactory;
import com.apatar.ui.NodeFactory;


public class FunctionsPlugin extends Plugin {

	static List<NodeFactory> listFunction;
	public static Map<String, ClassLoader> functionLoaders = new Hashtable<String, ClassLoader>();
	
	public void loadFunctionNodesPlugins() {
		//listFunction = new LinkedList<NodeFactory>();
		ExtensionPoint toolExtPoint = getManager().getRegistry()
		.getExtensionPoint(getDescriptor().getId(), "functionFactory");
		for (Iterator it = toolExtPoint.getConnectedExtensions().iterator(); it
				.hasNext();) {
			Extension ext = (Extension) it.next();
		
			Parameter cvParam = ext.getParameter("classFunction");

			try {
				PluginDescriptor pluginDescr = ext
						.getDeclaringPluginDescriptor();
				getManager().activatePlugin(pluginDescr.getId());
				ClassLoader classLoader = getManager().getPluginClassLoader(
						pluginDescr);
				Class nodeFactClass = classLoader.loadClass("com.apatar.functions.FunctionNodeFactory");
				NodeFactory factory = (FunctionNodeFactory)nodeFactClass.getConstructor(
						new Class[]{
								ClassLoader.class,
								String.class
								})
								.newInstance(new Object[] {
										classLoader,
										cvParam.valueAsString()
								});
				listFunction.add(factory);
				functionLoaders.put(cvParam.valueAsString(), classLoader);
				
			} catch (PluginLifecycleException e) {
				e.printStackTrace();
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
	public void loadFunctionConstNodesPlugins() {
	//	listFunction = new LinkedList<NodeFactory>();
		ExtensionPoint toolExtPoint = getManager().getRegistry()
		.getExtensionPoint(getDescriptor().getId(), "functionConstFactory");
		for (Iterator it = toolExtPoint.getConnectedExtensions().iterator(); it
				.hasNext();) {
			Extension ext = (Extension) it.next();
		
			Parameter cvParam = ext.getParameter("classFunction");

			try {
				PluginDescriptor pluginDescr = ext
						.getDeclaringPluginDescriptor();
				getManager().activatePlugin(pluginDescr.getId());
				ClassLoader classLoader = getManager().getPluginClassLoader(
						pluginDescr);
				Class nodeFactClass = classLoader.loadClass("com.apatar.functions.ConstantFunctionNodeFactory");
				NodeFactory factory = (ConstantFunctionNodeFactory)nodeFactClass.getConstructor(
						new Class[]{
								ClassLoader.class,
								String.class
								})
								.newInstance(new Object[] {
										classLoader,
										cvParam.valueAsString()
								});
				listFunction.add(factory);
				functionLoaders.put(cvParam.valueAsString(), classLoader);
				
			} catch (PluginLifecycleException e) {
				e.printStackTrace();
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
	public void loadOtherFunctionNodesPlugins() {
		//	listFunction = new LinkedList<NodeFactory>();
			ExtensionPoint toolExtPoint = getManager().getRegistry()
			.getExtensionPoint(getDescriptor().getId(), "otherFunctionFactory");
			for (Iterator it = toolExtPoint.getConnectedExtensions().iterator(); it
					.hasNext();) {
				Extension ext = (Extension) it.next();
			
				Parameter cvParam = ext.getParameter("classFunction");
				Parameter cnParam = ext.getParameter("classNode");

				try {
					PluginDescriptor pluginDescr = ext
							.getDeclaringPluginDescriptor();
					getManager().activatePlugin(pluginDescr.getId());
					ClassLoader classLoader = getManager().getPluginClassLoader(
							pluginDescr);
					Class nodeFactClass = classLoader.loadClass(cnParam.valueAsString());
					NodeFactory factory = (ReplaceIfFunctionNodeFactory)nodeFactClass.getConstructor(
							new Class[]{
									ClassLoader.class,
									String.class
									})
									.newInstance(new Object[] {
											classLoader,
											cvParam.valueAsString()
									});
					listFunction.add(factory);
					functionLoaders.put(cvParam.valueAsString(), classLoader);
					
				} catch (PluginLifecycleException e) {
					e.printStackTrace();
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
	
	public static List<NodeFactory> getNodesFunction() {
		return listFunction;
	}

	protected void doStart() throws Exception {
		listFunction = new LinkedList<NodeFactory>();
		loadFunctionNodesPlugins();
		loadFunctionConstNodesPlugins();
		loadOtherFunctionNodesPlugins();
	}

	protected void doStop() throws Exception {}
}
