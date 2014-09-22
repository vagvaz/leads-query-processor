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
import com.amazon.s3.GetStreamResponse;

public class GetStreamObjectAmazonS3Function extends AmazonS3Function {

	public GetStreamObjectAmazonS3Function(List<AmazonS3RequestParameters> request, List<AmazonS3RequestParameters> response, boolean workingWithBucket) {
		super("GetStreamObject", "Read an object from S3 using streaming", request, response, workingWithBucket);
	}

	public List<KeyInsensitiveMap> execute(KeyInsensitiveMap attributes, AWSAuthConnection conn) throws MalformedURLException, IOException {
		List<KeyInsensitiveMap> lkim = new ArrayList<KeyInsensitiveMap>();
		
		GetStreamResponse res;
		res = conn.getStream(
				(String)attributes.get("bucket", true),
				(String)attributes.get("key", true),
				(Map)attributes.get("headers", true));
		
		KeyInsensitiveMap kim = AmazonS3Utils.generateKeyInsensitiveMap(response, res.connection);
		kim.put("data", res.object.stream);
		
		lkim.add(kim);
		return lkim;
	}

}
