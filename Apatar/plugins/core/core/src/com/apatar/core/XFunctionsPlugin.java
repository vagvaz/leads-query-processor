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


public class XFunctionsPlugin extends Plugin {

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
		//loadFunctionNodesPlugins(); @@
		//loadFunctionConstNodesPlugins(); @@
		//loadOtherFunctionNodesPlugins(); @@
		//@@G useless etc ki alliws dn ta fortwnei 
	}

	protected void doStop() throws Exception {}
}
