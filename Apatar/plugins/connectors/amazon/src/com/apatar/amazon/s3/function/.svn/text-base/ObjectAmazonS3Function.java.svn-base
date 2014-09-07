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

package com.apatar.amazon.s3.function;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.apatar.amazon.s3.AmazonS3Function;
import com.apatar.amazon.s3.AmazonS3RequestParameters;
import com.apatar.amazon.s3.AmazonS3Utils;
import com.apatar.core.KeyInsensitiveMap;
import com.amazon.s3.AWSAuthConnection;
import com.amazon.s3.Response;
import com.amazon.s3.S3Object;
import com.silvasoftinc.s3.S3StreamObject;

public class ObjectAmazonS3Function extends AmazonS3Function {
	
	String function;
	
	public static String DELETE_FUNCTION = "DELETE";
	public static String PUT_FUNCTION = "PUT";
	public static String PUT_STREAM_FUNCTION = "PUT_STREAM";

	public ObjectAmazonS3Function(Object id, String displayName, List<AmazonS3RequestParameters> request, List<AmazonS3RequestParameters> response, String function, boolean workingWithBucket) {
		super(id, displayName, request, response, workingWithBucket);
		this.function = function;
	}
	public ObjectAmazonS3Function(Object id, String displayName, List<AmazonS3RequestParameters> request, List<AmazonS3RequestParameters> response, String function, boolean workingWithBucket, boolean allowManualy) {
		super(id, displayName, request, response, workingWithBucket, allowManualy);
		this.function = function;
	}
	
	public List<KeyInsensitiveMap> execute(KeyInsensitiveMap attributes, AWSAuthConnection conn) throws MalformedURLException, IOException {
		Response res;
		if (function.equalsIgnoreCase(DELETE_FUNCTION)) {
			res = conn.delete((String)attributes.get("bucket", true),
				(String)attributes.get("key", true),
				(Map)attributes.get("headers", true));
		}
		else if (function.equalsIgnoreCase(PUT_FUNCTION)) {
			
			File datafile = new File((String)attributes.get("path", true));
			
			byte[] buf = new byte[(int) datafile.length()];

			DataInputStream dis = new DataInputStream(new FileInputStream(datafile));
			try {
				dis.readFully(buf);
			} finally {
				dis.close();
			}

			S3Object s3Object = new S3Object(buf, null);
			
			res = conn.put((String)attributes.get("bucket", true),
					(String)attributes.get("key", true),
					s3Object,
					(Map)attributes.get("headers", true));
		} else {
			
			ByteArrayInputStream inputStream = new ByteArrayInputStream((byte[])attributes.get("stream", true));
			
			res = conn.putStream((String)attributes.get("bucket", true),
					(String)attributes.get("key", true),
					new S3StreamObject(inputStream, null),
					(Map)attributes.get("headers", true));
		}
		
		KeyInsensitiveMap kim = AmazonS3Utils.generateKeyInsensitiveMap(response, res.connection);
		List<KeyInsensitiveMap> lkim = new ArrayList<KeyInsensitiveMap>();
		lkim.add(kim);
		
		return lkim;
	}

}
