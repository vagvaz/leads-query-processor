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

package com.apatar.flickr.objects;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;
import org.jdom.JDOMException;

import com.apatar.flickr.FlickrNode;
import com.apatar.flickr.FlickrTable;
import com.apatar.flickr.FlickrTableList;
import com.apatar.flickr.FlickrUtils;

public class Function {
	FlickrTable table;
	String param;
	
	public Function(String tableName, String param) {
		super();
		FlickrTableList.getTableByName(tableName);
		this.param = param;
	}
	
	public Function(FlickrTable table, String param) {
		super();
		this.table = table;
		this.param = param;
	}

	public List getValues(FlickrNode node, Hashtable<String, Object> values, String strApi, String strSecret) throws IOException, XmlRpcException, JDOMException {
		if (values == null) {
			values = new Hashtable<String, Object>();
			Map<String, Object> optionalParams = table.getOptionalArguments();
			values.putAll(optionalParams);
		}
		return null;//FlickrUtils.getValues(param, node, table, values, strApi, strSecret);
	}
	
}
