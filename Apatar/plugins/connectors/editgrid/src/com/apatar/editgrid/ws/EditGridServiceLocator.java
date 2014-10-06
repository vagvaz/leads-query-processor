/**
 * EditGridServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.editgrid.ws;

public class EditGridServiceLocator extends org.apache.axis.client.Service
		implements com.apatar.editgrid.ws.EditGridService {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8024631732660604385L;

	public EditGridServiceLocator() {
	}

	public EditGridServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public EditGridServiceLocator(java.lang.String wsdlLoc,
			javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for EditGridServicePort
	private java.lang.String	EditGridServicePort_address	= "http://www.editgrid.com/api/v1/soap";

	public java.lang.String getEditGridServicePortAddress() {
		return EditGridServicePort_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String	EditGridServicePortWSDDServiceName	= "EditGridServicePort";

	public java.lang.String getEditGridServicePortWSDDServiceName() {
		return EditGridServicePortWSDDServiceName;
	}

	public void setEditGridServicePortWSDDServiceName(java.lang.String name) {
		EditGridServicePortWSDDServiceName = name;
	}

	public com.apatar.editgrid.ws.EditGridServicePort_PortType getEditGridServicePort()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(EditGridServicePort_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getEditGridServicePort(endpoint);
	}

	public com.apatar.editgrid.ws.EditGridServicePort_PortType getEditGridServicePort(
			java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			com.apatar.editgrid.ws.EditGridServiceBindingStub _stub = new com.apatar.editgrid.ws.EditGridServiceBindingStub(
					portAddress, this);
			_stub.setPortName(getEditGridServicePortWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setEditGridServicePortEndpointAddress(java.lang.String address) {
		EditGridServicePort_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has
	 * no port for the given interface, then ServiceException is thrown.
	 */
	@Override
	public java.rmi.Remote getPort(Class serviceEndpointInterface)
			throws javax.xml.rpc.ServiceException {
		try {
			if (com.apatar.editgrid.ws.EditGridServicePort_PortType.class
					.isAssignableFrom(serviceEndpointInterface)) {
				com.apatar.editgrid.ws.EditGridServiceBindingStub _stub = new com.apatar.editgrid.ws.EditGridServiceBindingStub(
						new java.net.URL(EditGridServicePort_address), this);
				_stub.setPortName(getEditGridServicePortWSDDServiceName());
				return _stub;
			}
		} catch (java.lang.Throwable t) {
			throw new javax.xml.rpc.ServiceException(t);
		}
		throw new javax.xml.rpc.ServiceException(
				"There is no stub implementation for the interface:  "
						+ (serviceEndpointInterface == null ? "null"
								: serviceEndpointInterface.getName()));
	}

	/**
	 * For the given interface, get the stub implementation. If this service has
	 * no port for the given interface, then ServiceException is thrown.
	 */
	@Override
	public java.rmi.Remote getPort(javax.xml.namespace.QName portName,
			Class serviceEndpointInterface)
			throws javax.xml.rpc.ServiceException {
		if (portName == null) {
			return getPort(serviceEndpointInterface);
		}
		java.lang.String inputPortName = portName.getLocalPart();
		if ("EditGridServicePort".equals(inputPortName)) {
			return getEditGridServicePort();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	@Override
	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://api.editgrid.com",
				"EditGridService");
	}

	private java.util.HashSet	ports	= null;

	@Override
	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://api.editgrid.com",
					"EditGridServicePort"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName,
			java.lang.String address) throws javax.xml.rpc.ServiceException {

		if ("EditGridServicePort".equals(portName)) {
			setEditGridServicePortEndpointAddress(address);
		} else { // Unknown Port Name
			throw new javax.xml.rpc.ServiceException(
					" Cannot set Endpoint Address for Unknown Port" + portName);
		}
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(javax.xml.namespace.QName portName,
			java.lang.String address) throws javax.xml.rpc.ServiceException {
		setEndpointAddress(portName.getLocalPart(), address);
	}

}
