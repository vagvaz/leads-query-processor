/*
_______________________
Apatar Open Source Data Integration
Copyright (C) 2005-2007, Apatar, Inc.
info@apatar.com
195 Meadow St., 2nd Floor
Chicopee, MA 01013

��� This program is free software; you can redistribute it and/or modify
��� it under the terms of the GNU General Public License as published by
��� the Free Software Foundation; either version 2 of the License, or
��� (at your option) any later version.

��� This program is distributed in the hope that it will be useful,
��� but WITHOUT ANY WARRANTY; without even the implied warranty of
��� MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.� See the
��� GNU General Public License for more details.

��� You should have received a copy of the GNU General Public License along
��� with this program; if not, write to the Free Software Foundation, Inc.,
��� 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
________________________

*/

package com.apatar.flickr;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

import com.apatar.core.ETableMode;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.flickr.objects.Formula;
import com.apatar.flickr.objects.Function;

public class FlickrTable {

	private String tableName	= "";
	private String comment;
	private ETableMode mode		= ETableMode.ReadOnly;
	private boolean paged = false; 
	private Map<String, Object> optionalArguments = new HashMap<String, Object>();
	private Map<String, Object>	returnFieldPathes = new HashMap<String, Object>();
	
	private String url = "http://api.flickr.com/services/xmlrpc";
	
	private String resultXPath = "";
	
	private FlickrPermission permission = FlickrPermission.read;
	
	private boolean hidden = false;

	public FlickrTable(String name, String comment){
		this(name, comment, ETableMode.ReadOnly, false, null, null, null, FlickrPermission.read);
	}

	public FlickrTable(String name){
		this(name, null, ETableMode.ReadOnly, false, null, null, null, FlickrPermission.read);
	}
	
	public FlickrTable(String name, String comment, ETableMode mode, 
			boolean paged, String resultXPath, 
			Map<String, Object> optionals,
			Map<String, Object> fieldPath,
			FlickrPermission permission){
		this(name, comment, mode, paged, resultXPath, optionals, fieldPath);
		
		this.permission = permission;
	}
	
	public FlickrTable(String name, String comment, ETableMode mode, 
			boolean paged, String resultXPath, 
			Map<String, Object> optionals,
			Map<String, Object> fieldPath){
		this.tableName = name;
		this.comment = comment;
		this.mode = mode;
		this.paged = paged;
		this.resultXPath = resultXPath;
		if (optionals != null)
			optionalArguments.putAll(optionals);
	
		if (returnFieldPathes != null && fieldPath != null)
			returnFieldPathes.putAll(fieldPath);
	}
	
	public FlickrTable(String name, String comment, ETableMode mode, 
			boolean paged, String resultXPath, 
			Map<String, Object> optionals, Map<String, Object> fieldPath,
			FlickrPermission permission,
			String url){
		this(name, comment, mode, paged, resultXPath, optionals, fieldPath, permission);
				
		this.url = url;
	}
	
	public String getTableName(){
		return this.tableName;
	}
	
	public void setTableName(String name){
		this.tableName = name;
	}
	
	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}

	public ETableMode getMode() {
		return mode;
	}

	public void setMode(ETableMode mode) {
		this.mode = mode;
	}

	public Map<String, Object> getOptionalArguments() {
		return optionalArguments;
	}

	public boolean isPaged() {
		return paged;
	}

	public Map<String, Object> getReturnFieldsPathes() {
		return returnFieldPathes;
	}
	public void setReturnFieldPathes(Map<String, Object> returnFieldPathes) {
		this.returnFieldPathes = returnFieldPathes;
	}
	
	// returns the permission string requrid for the table access
	// TODO - may be specified in the constructor
	public FlickrPermission getFlickrPermissions()
	{
		return permission;
	}

	public String getResultXPath() {
		return resultXPath;
	}

	public void setResultXPath(String resultXPath) {
		this.resultXPath = resultXPath;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	/*
	 * Execute function
	 */
	protected List<KeyInsensitiveMap> execute(FlickrNode node, Hashtable<String, Object> values, String strApi, String strSecret) throws IOException, XmlRpcException, JDOMException {
		Element rootElement = getResponse(node, values, strApi, strSecret);
		 
		return createKeyInsensitiveMapFromElement(rootElement);
	}
	
	private Object execute(XmlRpcClient rpcClient, Vector<Object> vals, Hashtable<String, Object> values, String strApi, String strSecret) throws XmlRpcException, IOException {
		FlickrUtils.Sign(values, strApi, strSecret);
	    
	    Object rv = rpcClient.execute(getTableName(), vals);
	    
	    if (rv instanceof XmlRpcException) {
	    	throw (XmlRpcException)rv;
		}
	    
	    return rv;
	}
	/*
	 * Create KeyInsensitiveMap from JDOM Element
	 */
	protected List<KeyInsensitiveMap> createKeyInsensitiveMapFromElement(Element rootElem) throws JDOMException {
		List<KeyInsensitiveMap> result = new ArrayList<KeyInsensitiveMap>();
		
		String resXPath = getResultXPath();
		if (resXPath != null && !resXPath.equals("")) {
			XPath path = XPath.newInstance(resXPath);
			List nodes = path.selectNodes(rootElem);
			
			for (Object obj : nodes)
			{
				KeyInsensitiveMap data = new KeyInsensitiveMap();
				Element elem = (Element)obj;
				
				for(String key : returnFieldPathes.keySet()) {
					XPath xpath = XPath.newInstance(returnFieldPathes.get(key).toString());
					Object node = xpath.selectSingleNode(elem);
					if (node instanceof Attribute)
						data.put(key, ((Attribute)node).getValue());
					else {
						if (node instanceof Element)
							data.put(key, ((Element)node).getText());
					}
				}
				
				//if (getMode() != ETableMode.ReadOnly) {
				for(String key : optionalArguments.keySet()) {
					data.put(key, optionalArguments.get(key));
				}
				//}
				
				result.add(data);
			}
		}
		return result;
	}
	/*
	 * Execute function
	 */
	public Element getResponse(FlickrNode node, Hashtable<String, Object> values, String strApi, String strSecret) throws XmlRpcException, IOException, JDOMException {
		XmlRpcClient rpcClient = new XmlRpcClient(new URL(getUrl()));
	    
	    Vector<Object> vals = new Vector<Object>();
	    Hashtable<String, Object> attr = new Hashtable<String, Object>();
	    Map<String, List> lists = new HashMap<String, List>();
	    for (String str : values.keySet()) {
	    	Object obj = values.get(str);
	    	if (obj instanceof Formula)
	    		attr.put(str, ((Formula)obj).getValue());
	    	/*if (obj instanceof Function)
	    		lists.put(str, ((Function)obj).getValues(node, null, strApi, strSecret));*/
	    	attr.put(str, obj);
	    }
	    
	    String result = "";
	    
	    vals.add(attr);
	    //vals.add(values);
	    Object[] keyLists = lists.keySet().toArray();
	    List[] valueLists = new List[keyLists.length];
	    for (int i = 0; i < keyLists.length; i++) {
	    	valueLists[i] = lists.get(keyLists[i]);
	    }
	    int[] indexes = new int[keyLists.length];
	    for(int i = 0; i < keyLists.length; i++)
	    	indexes[i] = 0;
	    if (lists.size() >= 1)
		    while(!FlickrUtils.getNextIndexes(valueLists, indexes)) {
		    	for(int i = 0; i < indexes.length; i++) {
		    		attr.put(keyLists[indexes[i]].toString(), valueLists[indexes[i]]);
		    	}
		    
			    result += execute(rpcClient, vals, attr, strApi, strSecret).toString();
		    }
	    else
	    	result = execute(rpcClient, vals, attr, strApi, strSecret).toString();
	    
		 return FlickrUtils.getRootElement(result);
	}
	
}
