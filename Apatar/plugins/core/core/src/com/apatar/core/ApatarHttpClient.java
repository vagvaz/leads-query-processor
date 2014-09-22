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
 


package com.apatar.core;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

public class ApatarHttpClient {
	boolean isUseProxy = false;
	String host = "";
	int port = 0;
	String userName = "";
	String password = "";
	
	public ApatarHttpClient() {
		super();
	}
	
	public ApatarHttpClient(boolean isUseProxy, String host, int port,
			String userName, String password) {
		super();
		this.isUseProxy = isUseProxy;
		this.host = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
	}
	
	public void setParametersProxy(String host, int port,
			String userName, String password) {
		this.host = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
	}

	private String sendHttpQuery(HttpMethod method) throws HttpException, IOException {
		HttpClient client = new HttpClient();
	    HostConfiguration hostConfig= client.getHostConfiguration();
	    if (isUseProxy) {
		    hostConfig.setProxy(host, port);
		    
		    if (userName != null && !userName.equals("")) {
			    client.getState().setProxyCredentials(AuthScope.ANY,
			    new NTCredentials(userName, password, "", ""));
		    }
	    }
	    
	    client.executeMethod(method);
        
        return method.getResponseBodyAsString();
	}
	public String sendPostHttpQuery(String url, HashMap<String, String> params) throws IOException {
		int size = params.size();
		Part[] parts = new Part[size];
		int i = 0;
		for (String param : params.keySet()) {
			parts[i++] = new StringPart(param, params.get(param));
		}
	    
	    PostMethod method = new PostMethod(url);
	    
	    method.setRequestEntity(
                new MultipartRequestEntity(parts, method.getParams())
        );
	    
        return sendHttpQuery(method);
		
	}
	public String sendGetHttpQuery(String query) throws IOException {
		GetMethod method = new GetMethod(query);
		return sendHttpQuery(method);
	}

	public boolean isUseProxy() {
		return isUseProxy;
	}

	public void setUseProxy(boolean isUseProxy) {
		this.isUseProxy = isUseProxy;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}

