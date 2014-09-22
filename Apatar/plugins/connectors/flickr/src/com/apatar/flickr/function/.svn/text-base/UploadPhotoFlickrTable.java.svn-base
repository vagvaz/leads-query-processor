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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.xmlrpc.XmlRpcException;

import com.apatar.core.ApplicationData;
import com.apatar.core.ETableMode;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.flickr.FlickrNode;
import com.apatar.flickr.FlickrPermission;
import com.apatar.flickr.FlickrTable;
import com.apatar.flickr.FlickrUtils;
import com.apatar.ui.ApatarUiMain;

public class UploadPhotoFlickrTable extends FlickrTable {
		
	public UploadPhotoFlickrTable(String name, String comment) {
		super(name, comment);
	}

	public UploadPhotoFlickrTable(String name, String comment, ETableMode mode, boolean paged, String resultXPath, Map<String, Object> optionals, Map<String, Object> fieldPath, FlickrPermission permission, String url) {
		super(name, comment, mode, paged, resultXPath, optionals, fieldPath, permission, url);
	}

	public UploadPhotoFlickrTable(String name, String comment, ETableMode mode, boolean paged, String resultXPath, Map<String, Object> optionals, Map<String, Object> fieldPath, FlickrPermission permission) {
		super(name, comment, mode, paged, resultXPath, optionals, fieldPath, permission);
	}

	public UploadPhotoFlickrTable(String name, String comment, ETableMode mode, boolean paged, String resultXPath, Map<String, Object> optionals, Map<String, Object> fieldPath) {
		super(name, comment, mode, paged, resultXPath, optionals, fieldPath);
	}

	public UploadPhotoFlickrTable(String name) {
		super(name);
	}

	public List<KeyInsensitiveMap> execute(FlickrNode node, Hashtable<String, Object> values, String strApi, String strSecret) throws IOException, XmlRpcException
	{
		byte[] photo = (byte[])values.get("photo");
		
		int size = values.size()+2;
		
		values.remove("photo");
					
		File newFile = ApplicationData.createFile("flicr", photo);
		
		PostMethod post = new PostMethod("http://api.flickr.com/services/upload");
		Part[] parts = new Part[size];
		
		parts[0] = new FilePart("photo", newFile);
		parts[1] = new StringPart("api_key", strApi);
		int i = 2;
		for (String key : values.keySet())
			parts[i++] = new StringPart(key, values.get(key).toString());
		
		values.put("api_key", strApi);
		
		parts[i] = new StringPart("api_sig", FlickrUtils.Sign(values, strApi, strSecret));
		
		post.setRequestEntity(
				new MultipartRequestEntity(parts, post.getParams())
        );
		
		HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        int status = client.executeMethod(post);
        if (status != HttpStatus.SC_OK) {
        	JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME, "Upload failed, response=" + HttpStatus.getStatusText(status));
        	return null;
        }
        else {
        	InputStream is = post.getResponseBodyAsStream();
        	try {
				return createKeyInsensitiveMapFromElement(FlickrUtils.getRootElementFromInputStream(is));
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
		return null;
	}
	
	public FlickrPermission getFlickrPermissions()
	{
		return FlickrPermission.write;
	}

}
