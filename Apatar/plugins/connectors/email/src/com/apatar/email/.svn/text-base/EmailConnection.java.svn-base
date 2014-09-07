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


package com.apatar.email;

import org.jdom.Element;

import com.apatar.core.ApplicationData;
import com.apatar.core.IPersistent;
import com.apatar.core.PasswordString;
import com.apatar.core.PersistentUtils;

public class EmailConnection implements IPersistent {

	private Enum<ReceiveServerType> incomingMailServerType	= ReceiveServerType.POP3;
	private String incomingMailServer	= "";
	private String outgoingMailServer	= "";
	private String login			= "";
	private int incomingMailPort	= 110;
	private int outgoingMailPort	= 25;
	private boolean useSsl			= false;
	private PasswordString password	= new PasswordString();
	
	public EmailConnection() {
		this(ReceiveServerType.POP3, "", "", "", new PasswordString());
	}
	
	public EmailConnection(Enum<ReceiveServerType> recServerType, String receiveServer,
			String sendTo, String login, PasswordString password){
		this.incomingMailServerType	= recServerType;
		this.incomingMailServer		= receiveServer;
		this.outgoingMailServer		= sendTo;
		this.login				= login;
		this.password			= password;
	}
	
	public boolean getUseSsl(){
		return this.useSsl;
	}
	
	public void setUseSsl(boolean ssl){
		this.useSsl = ssl;
	}
	
	public String getLogin(){
		return this.login;
	}
	
	public void setLogin(String login){
		this.login = login;
	}
	
	public PasswordString getPassword(){
		return this.password;
	}
	
	public void setPassword(PasswordString password){
		this.password = password;
	}
	
	public int getIncomingMailPort() {
		return incomingMailPort;
	}

	public void setIncomingMailPort(int incomingMailPort) {
		this.incomingMailPort = incomingMailPort;
	}

	public String getIncomingMailServer() {
		return incomingMailServer;
	}

	public void setIncomingMailServer(String incomingMailServer) {
		this.incomingMailServer = incomingMailServer;
	}

	public int getOutgoingMailPort() {
		return outgoingMailPort;
	}

	public void setOutgoingMailPort(int outgoingMailPort) {
		this.outgoingMailPort = outgoingMailPort;
	}

	public String getOutgoingMailServer() {
		return outgoingMailServer;
	}

	public void setOutgoingMailServer(String outgoingMailServer) {
		this.outgoingMailServer = outgoingMailServer;
	}
	
	public Enum getIncomingMailServerType() {
		return incomingMailServerType;
	}

	public void setIncomingMailServerType(Enum incomingMailServerType) {
		this.incomingMailServerType = incomingMailServerType;
	}

	public Element saveToElement() {
		Element e = PersistentUtils.CreateElement(this);
		
		e.setAttribute("receiveServerType",	this.incomingMailServerType.toString());
		e.setAttribute("receiveServer",	this.incomingMailServer );
		e.setAttribute("sendToServer",	this.outgoingMailServer );
		e.setAttribute("receivePort",	String.valueOf( this.incomingMailPort ) );
		e.setAttribute("sendPort",		String.valueOf( this.outgoingMailPort ) );
		e.setAttribute("useSsl",		String.valueOf( this.useSsl ) );
		e.setAttribute("login",			this.login);
		
		e.addContent( getPassword().saveToElement() );
		
		return e;
	}

	public void initFromElement(Element e){
		if (e == null) {
			ApplicationData.COUNT_INIT_ERROR++;
			return;
		}
			
		this.incomingMailServer	= e.getAttributeValue("receiveServer");
		this.outgoingMailServer	= e.getAttributeValue("sendToServer");
		this.login			= e.getAttributeValue("login");
		String attribut = e.getAttributeValue("useSsl");
		if (attribut != null)
			this.useSsl		= Boolean.valueOf(attribut);
		else
			ApplicationData.COUNT_INIT_ERROR++;
		
		String port		= e.getAttributeValue("receivePort");
		this.incomingMailPort	= ( null == port || "".equals( port )
								? 110 : Integer.valueOf( port ) );
		
		port				= e.getAttributeValue("sendPort");
		this.outgoingMailPort		= ( null == port || "".equals( port )
								? 25 : Integer.valueOf( port ) );
		
		attribut = e.getAttributeValue("receiveServerType");
		if (attribut != null)
			incomingMailServerType	= Enum.valueOf(ReceiveServerType.class,
									attribut);
		else
			ApplicationData.COUNT_INIT_ERROR++;
		
		PersistentUtils.InitObjectFromChild(password, e);
	}
	
}

