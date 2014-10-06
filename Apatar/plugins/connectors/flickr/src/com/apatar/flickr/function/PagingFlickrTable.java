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

package com.apatar.flickr.function;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import com.apatar.core.ETableMode;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.flickr.FlickrNode;
import com.apatar.flickr.FlickrPermission;
import com.apatar.flickr.FlickrTable;
import com.apatar.flickr.FlickrUtils;
import com.apatar.flickr.objects.Formula;
import com.apatar.flickr.objects.Function;

public class PagingFlickrTable extends FlickrTable {
	
	int max_per_page;

	public PagingFlickrTable(String name, String comment, ETableMode mode, boolean paged, String resultXPath, Map<String, Object> optionals, Map<String, Object> fieldPath, FlickrPermission permission, String url, int max_per_page) {
		super(name, comment, mode, paged, resultXPath, optionals, fieldPath, permission, url);
		this.max_per_page = max_per_page;
	}

	public PagingFlickrTable(String name, String comment, ETableMode mode, boolean paged, String resultXPath, Map<String, Object> optionals, Map<String, Object> fieldPath, int max_per_page) {
		super(name, comment, mode, paged, resultXPath, optionals, fieldPath);
		this.max_per_page = max_per_page;
	}

	public PagingFlickrTable(String name, String comment, ETableMode mode, boolean paged, String resultXPath, Map<String, Object> optionals, Map<String, Object> fieldPath, FlickrPermission permission, int max_per_page) {
		super(name, comment, mode, paged, resultXPath, optionals, fieldPath, permission);
		this.max_per_page = max_per_page;
	}

	public PagingFlickrTable(String name, int max_per_page) {
		super(name);
		this.max_per_page = max_per_page;
	}

	protected List<KeyInsensitiveMap> execute(FlickrNode node, Hashtable<String, Object> values, String strApi, String strSecret) throws IOException, XmlRpcException, JDOMException {
		
		XmlRpcClient rpcClient = new XmlRpcClient(new URL(getUrl()));
	    
	    Vector<Object> vals = new Vector<Object>();
	    Hashtable<String, Object> attr = new Hashtable<String, Object>();
	    Map<String, List> lists = new HashMap<String, List>();
	    for (String str : values.keySet()) {
	    	Object obj = values.get(str);
	    	if (obj instanceof Formula)
	    		attr.put(str, ((Formula)obj).getValue());
	    	if (obj instanceof Function)
	    		lists.put(str, ((Function)obj).getValues(node, null, strApi, strSecret));
	    	attr.put(str, obj);
	    }
	    
	    attr.put("per_page", max_per_page);
	  //  vals.add(attr);
	    
	    
	    Object[] keyLists = lists.keySet().toArray();
	    List[] valueLists = new List[keyLists.length];
	    for (int i = 0; i < keyLists.length; i++) {
	    	valueLists[i] = lists.get(keyLists[i]);
	    }
	    int[] indexes = new int[keyLists.length];
	    for(int i = 0; i < keyLists.length; i++)
	    	indexes[i] = 0;
	    
	    Element root = new Element("root");
	    if (lists.size() > 1) {
		    while(!FlickrUtils.getNextIndexes(valueLists, indexes)) {
		    	for(int i = 0; i < indexes.length; i++) {
		    		attr.put(keyLists[indexes[i]].toString(), valueLists[indexes[i]]);
		    	}
		    	paging(rpcClient, vals, values, root, strApi, strSecret);
		    }
	    }
		else
			paging(rpcClient, vals, values, root, strApi, strSecret);
		return createKeyInsensitiveMapFromElement(root);
		
	}
	
	private void paging(XmlRpcClient rpcClient, Vector<Object> vals, Hashtable<String, Object> values, Element root, String strApi, String strSecret) throws XmlRpcException, IOException, JDOMException {
		int page = 1;
		int count = max_per_page;
    	while(count >= max_per_page) {
	    	values.put("page", page++);
	    	
	    	vals.add(values);
		    
		    FlickrUtils.Sign(values, strApi, strSecret);
		    
		    Object rv = rpcClient.execute(getTableName(), vals);
		    
		    if (rv instanceof XmlRpcException) {
		    	throw (XmlRpcException)rv;
			}
		    
		    Document doc = new Document();
		    // wrap in root and load into the DOM
			SAXBuilder builder = new SAXBuilder();
			try {
				doc = builder.build(new StringReader(rv.toString()));
				Element retRoot = doc.getRootElement();
				XPath path = XPath.newInstance(getResultXPath());
				List nodes = path.selectNodes(retRoot);
				root.addContent(nodes);
				count = nodes.size();
			} 
			catch (JDOMException e) {
				e.printStackTrace();
				throw e;
			}
	    }
	}

}
