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
import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javax.swing.ImageIcon;

import org.apache.xmlrpc.XmlRpcException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.apatar.core.CoreUtils;


public class FlickrUtils {
	public static final ImageIcon READ_SUGARCRM_ICON = new ImageIcon(FlickrNodeFactory.class
	        .getResource("16-FLICKR.png"));
	public static final ImageIcon READ_SUGARCRM_NODE_ICON = new ImageIcon(FlickrNodeFactory.class
	        .getResource("32-FLICKR.png"));
	
	public static String Sign(Hashtable<String, Object> values, String strApi, String strSecret)
	{
		values.remove("api_sig");
		
		if (!values.containsKey("api_key"))
			values.put("api_key", strApi);
		
		Object[] keys = values.keySet().toArray();
		
		Arrays.sort(keys);
		
		String strval = strSecret;
		for (int i=0; i < keys.length; i++)
		{
			String strkey = (String)keys[i];
			strval+=strkey + values.get(strkey);
		}
		
		String sign = CoreUtils.getMD5(strval);
		
		values.put("api_sig", sign);
		
		return sign;
	}
	
	public static Element getRootElement(String str) throws IOException, XmlRpcException, JDOMException
	{
	    String rv = String.format("<root> %s </root>", str);

	    Document doc = new Document();
	    // wrap in root and load into the DOM
		SAXBuilder builder = new SAXBuilder();
		try {
			doc = builder.build(new StringReader(rv));
		} 
		catch (JDOMException e) {
			e.printStackTrace();
		}
		
        return doc.getRootElement();
	}
	
	public static Element getRootElementFromInputStream(InputStream is) {
		Document doc = new Document();
	    // wrap in root and load into the DOM
		SAXBuilder builder = new SAXBuilder();
		try {
			doc = builder.build(is);
		} 
		catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        return doc.getRootElement();
	}
	
	/*public static List<String> getValues(String param, FlickrNode node, FlickrTable table, Hashtable<String, Object> values, String strApi, String strSecret) throws IOException, XmlRpcException, JDOMException {
		Element root = table.execute(node, values, strApi, strSecret);
		
		XPath path = XPath.newInstance(table.getResultXPath());
		List nodes = path.selectNodes(root);
		
		Map<String, Object>	returnFieldPathes = table.getReturnFieldsPathes();
		List<String> data = new ArrayList<String>();
		
		for (Object obj : nodes)
		{
			Element elem = (Element)obj;
			
			XPath xpath = XPath.newInstance(returnFieldPathes.get(param).toString());
			Object selnode = xpath.selectSingleNode(elem);
			data.add(selnode.toString());
		}
		return data;
	}*/
	
	public static boolean getNextIndexes(List[] lists, int[] indexes) {
		int size = lists.length;
		return getNextIndexes(lists, indexes, size-1);
	}
	private static boolean getNextIndexes(List[] lists, int[] indexes, int curentListIndex) {
		if (indexes[curentListIndex] < lists[curentListIndex].size()) {
			indexes[curentListIndex]++;
			return false;
		}
		else {
			if (--curentListIndex < 0)
				return true;
			for (int i=curentListIndex+1; i < lists.length; i++)
				indexes[i] = 0;
			return getNextIndexes(lists, indexes, curentListIndex);
		}
	}
	
}
