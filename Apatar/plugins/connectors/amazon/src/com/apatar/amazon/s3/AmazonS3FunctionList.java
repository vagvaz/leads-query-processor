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

package com.apatar.amazon.s3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.apatar.amazon.s3.function.BucketAmazonS3Function;
import com.apatar.amazon.s3.function.GetBucketAmazonS3Function;
import com.apatar.amazon.s3.function.GetListAllMyBucketsAmazonS3Function;
import com.apatar.amazon.s3.function.GetObjectAmazonS3Function;
import com.apatar.amazon.s3.function.GetStreamObjectAmazonS3Function;
import com.apatar.amazon.s3.function.ObjectAmazonS3Function;

public class AmazonS3FunctionList {
	static Map<Object, AmazonS3Function> functions = new HashMap<Object, AmazonS3Function>();

	public static Map<Object, AmazonS3Function> getFunctions() {
		return functions;
	}
	
	public static void addFunction(AmazonS3Function function) {
		functions.put(function.getDisplayName(), function);
	}
	
	public static AmazonS3Function getFunction(Object id) {
		return functions.get(id);
	}
	
	public static void removeFunction(Object id) {
		functions.remove(id);
	}
	
	public static AmazonS3Function getAmazonS3FunctionByName(String name) {
		return functions.get(name);
	}

	private static List<AmazonS3RequestParameters> generateAttributeList(AmazonS3RequestParameters[] attrs) {
		
		List<AmazonS3RequestParameters> list = new ArrayList<AmazonS3RequestParameters>();
		
		for (AmazonS3RequestParameters attr : attrs) {
			list.add(attr);
		}
		
		return list;
	}
	
	static {
		
		AmazonS3RequestParameters[] req;
		AmazonS3RequestParameters[] res;
		
		req = new AmazonS3RequestParameters[] {
				new AmazonS3RequestParameters("bucket", AmazonS3Node.STRING_TYPE)
				};
		res = new AmazonS3RequestParameters[] {
				new AmazonS3RequestParameters("x-amz-id-2", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("x-amz-meta-family", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("x-amz-request-id", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("Date", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("Location", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("ETag", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("Content-Length", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("Connection", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("Server", AmazonS3Node.STRING_TYPE)
		};
		addFunction(new BucketAmazonS3Function("PutBucket", "Put Bucket",
				generateAttributeList(req), generateAttributeList(res), BucketAmazonS3Function.PUT_FUNCTION, true, false));
		addFunction(new BucketAmazonS3Function("DeleteBucket", "Delete Bucket",
				generateAttributeList(req), generateAttributeList(res), BucketAmazonS3Function.DELETE_FUNCTION, true));
		
		req = new AmazonS3RequestParameters[] {
				new AmazonS3RequestParameters("bucket", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("prefix", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("marker", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("maxKeys", AmazonS3Node.STRING_TYPE)
		};
		res = new AmazonS3RequestParameters[] {
				new AmazonS3RequestParameters("eTag", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("key", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("lastModified", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("owner_id", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("owner_displayName", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("size", AmazonS3Node.STRING_TYPE)
		};
		addFunction(new GetBucketAmazonS3Function("GetBucket", "Get Bucket",
				generateAttributeList(req), generateAttributeList(res), true));
		
		req = new AmazonS3RequestParameters[] {
				new AmazonS3RequestParameters("bucket", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("key", AmazonS3Node.STRING_TYPE),
		};
		res = new AmazonS3RequestParameters[] {
				new AmazonS3RequestParameters("data", AmazonS3Node.BINARY_TYPE),
				new AmazonS3RequestParameters("x-amz-id-2", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("x-amz-request-id", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("Date", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("x-amz-meta-family", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("Last-Modified", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("ETag", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("Content-Type", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("Content-Length", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("Connection", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("Server", AmazonS3Node.STRING_TYPE),
		};
		addFunction(new GetObjectAmazonS3Function(generateAttributeList(req), generateAttributeList(res), false));
		//addFunction(new GetStreamObjectAmazonS3Function(generateAttributeList(req), generateAttributeList(res), false));
		
		res = new AmazonS3RequestParameters[] {
				new AmazonS3RequestParameters("x-amz-id-2", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("x-amz-request-id", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("Date", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("Connection", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("Server", AmazonS3Node.STRING_TYPE),
		};
		
		addFunction(new ObjectAmazonS3Function("DeleteObject", "Delete Object", generateAttributeList(req), generateAttributeList(res), ObjectAmazonS3Function.DELETE_FUNCTION, false));
		
		req = new AmazonS3RequestParameters[] {
				new AmazonS3RequestParameters("bucket", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("key", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("path", AmazonS3Node.STRING_TYPE)
		};
		res = new AmazonS3RequestParameters[] {
				new AmazonS3RequestParameters("x-amz-id-2", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("x-amz-meta-family", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("x-amz-request-id", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("Date", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("ETag", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("Content-Length", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("Connection", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("Server", AmazonS3Node.STRING_TYPE),
		};
		addFunction(new ObjectAmazonS3Function("PutObject", "Put Object", generateAttributeList(req), generateAttributeList(res), ObjectAmazonS3Function.PUT_FUNCTION, false, false));
		req = new AmazonS3RequestParameters[] {
				new AmazonS3RequestParameters("bucket", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("key", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("stream", AmazonS3Node.BINARY_TYPE)
		};
		addFunction(new ObjectAmazonS3Function("PutStreamObject", "Put Object from Stream", generateAttributeList(req), generateAttributeList(res), ObjectAmazonS3Function.PUT_STREAM_FUNCTION, false, false));
		
		res = new AmazonS3RequestParameters[] {
				new AmazonS3RequestParameters("name", AmazonS3Node.STRING_TYPE),
				new AmazonS3RequestParameters("creationDate", AmazonS3Node.STRING_TYPE),
		};
		req = new AmazonS3RequestParameters[] {
		};
		addFunction(new GetListAllMyBucketsAmazonS3Function("listAllMyBucket", "Get Buckets List", generateAttributeList(req), generateAttributeList(res)));
	}
	
}
