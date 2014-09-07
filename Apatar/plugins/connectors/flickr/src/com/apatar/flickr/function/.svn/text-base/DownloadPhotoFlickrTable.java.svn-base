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
 


package com.apatar.flickr.function;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.xmlrpc.XmlRpcException;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.apatar.core.ETableMode;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.flickr.FlickrNode;
import com.apatar.flickr.FlickrPermission;
import com.apatar.flickr.FlickrTable;
import com.apatar.flickr.FlickrTableList;
/*
 * Download photo from flickr
 */
public class DownloadPhotoFlickrTable extends FlickrTable {

	public DownloadPhotoFlickrTable(String name, String comment, ETableMode mode, boolean paged, String resultXPath, Map<String, Object> optionals, Map<String, Object> fieldPath, FlickrPermission permission, String url) {
		super(name, comment, mode, paged, resultXPath, optionals, fieldPath,
				permission, url);
	}

	public DownloadPhotoFlickrTable(String name, String comment, ETableMode mode, boolean paged, String resultXPath, Map<String, Object> optionals, Map<String, Object> fieldPath, FlickrPermission permission) {
		super(name, comment, mode, paged, resultXPath, optionals, fieldPath, permission);
	}

	public DownloadPhotoFlickrTable(String name, String comment, ETableMode mode, boolean paged, String resultXPath, Map<String, Object> optionals, Map<String, Object> fieldPath) {
		super(name, comment, mode, paged, resultXPath, optionals, fieldPath);
	}

	public DownloadPhotoFlickrTable(String name, String comment) {
		super(name, comment);
	}

	public DownloadPhotoFlickrTable(String name) {
		super(name);
	}
	/*
	 * (non-Javadoc)
	 * @see com.apatar.flickr.FlickrTable#execute(com.apatar.flickr.FlickrNode, java.util.Hashtable, java.lang.String, java.lang.String)
	 */
	public List<KeyInsensitiveMap> execute(FlickrNode node, Hashtable<String, Object> values, String strApi, String strSecret) throws IOException, XmlRpcException {
		FlickrTable table = FlickrTableList.getTableByName("flickr.photos.getSizes");
		if (table == null)
			return null;
		
		List<KeyInsensitiveMap> list = new ArrayList<KeyInsensitiveMap>();
		KeyInsensitiveMap map = new KeyInsensitiveMap();
		list.add(map);
		try {
			Element element = table.getResponse(node, values, strApi, strSecret);
			for(Object obj : element.getChild("sizes").getChildren("size")) {
				Element child = (Element)obj;
				if (child.getAttributeValue("label").equalsIgnoreCase("Medium")) {
					GetMethod getMethod = new GetMethod(child.getAttributeValue("source"));
					HttpClient client = new HttpClient();
		            client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		            client.executeMethod(getMethod);
		            map.put("photo", getMethod.getResponseBody());
		            map.put("photo_id", values.get("photo_id"));
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		
		return list;
	}

}

