/**
 * PhoneVerifyLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.cdyne.ws;

public class PhoneVerifyLocator extends org.apache.axis.client.Service implements com.apatar.cdyne.ws.PhoneVerify {

    public PhoneVerifyLocator() {
    }


    public PhoneVerifyLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public PhoneVerifyLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for PhoneVerifySoap12
    private java.lang.String PhoneVerifySoap12_address = "http://ws.cdyne.com/phoneverify/phoneverify.asmx";

    public java.lang.String getPhoneVerifySoap12Address() {
        return PhoneVerifySoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String PhoneVerifySoap12WSDDServiceName = "PhoneVerifySoap12";

    public java.lang.String getPhoneVerifySoap12WSDDServiceName() {
        return PhoneVerifySoap12WSDDServiceName;
    }

    public void setPhoneVerifySoap12WSDDServiceName(java.lang.String name) {
        PhoneVerifySoap12WSDDServiceName = name;
    }

    public com.apatar.cdyne.ws.PhoneVerifySoap_PortType getPhoneVerifySoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(PhoneVerifySoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getPhoneVerifySoap12(endpoint);
    }

    public com.apatar.cdyne.ws.PhoneVerifySoap_PortType getPhoneVerifySoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.apatar.cdyne.ws.PhoneVerifySoap12Stub _stub = new com.apatar.cdyne.ws.PhoneVerifySoap12Stub(portAddress, this);
            _stub.setPortName(getPhoneVerifySoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setPhoneVerifySoap12EndpointAddress(java.lang.String address) {
        PhoneVerifySoap12_address = address;
    }


    // Use to get a proxy class for PhoneVerifySoap
    private java.lang.String PhoneVerifySoap_address = "http://ws.cdyne.com/phoneverify/phoneverify.asmx";

    public java.lang.String getPhoneVerifySoapAddress() {
        return PhoneVerifySoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String PhoneVerifySoapWSDDServiceName = "PhoneVerifySoap";

    public java.lang.String getPhoneVerifySoapWSDDServiceName() {
        return PhoneVerifySoapWSDDServiceName;
    }

    public void setPhoneVerifySoapWSDDServiceName(java.lang.String name) {
        PhoneVerifySoapWSDDServiceName = name;
    }

    public com.apatar.cdyne.ws.PhoneVerifySoap_PortType getPhoneVerifySoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(PhoneVerifySoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getPhoneVerifySoap(endpoint);
    }

    public com.apatar.cdyne.ws.PhoneVerifySoap_PortType getPhoneVerifySoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.apatar.cdyne.ws.PhoneVerifySoap_BindingStub _stub = new com.apatar.cdyne.ws.PhoneVerifySoap_BindingStub(portAddress, this);
            _stub.setPortName(getPhoneVerifySoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setPhoneVerifySoapEndpointAddress(java.lang.String address) {
        PhoneVerifySoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     * This service has multiple ports for a given interface;
     * the proxy implementation returned may be indeterminate.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.apatar.cdyne.ws.PhoneVerifySoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.apatar.cdyne.ws.PhoneVerifySoap12Stub _stub = new com.apatar.cdyne.ws.PhoneVerifySoap12Stub(new java.net.URL(PhoneVerifySoap12_address), this);
                _stub.setPortName(getPhoneVerifySoap12WSDDServiceName());
                return _stub;
            }
            if (com.apatar.cdyne.ws.PhoneVerifySoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.apatar.cdyne.ws.PhoneVerifySoap_BindingStub _stub = new com.apatar.cdyne.ws.PhoneVerifySoap_BindingStub(new java.net.URL(PhoneVerifySoap_address), this);
                _stub.setPortName(getPhoneVerifySoapWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("PhoneVerifySoap12".equals(inputPortName)) {
            return getPhoneVerifySoap12();
        }
        else if ("PhoneVerifySoap".equals(inputPortName)) {
            return getPhoneVerifySoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://ws.cdyne.com/PhoneVerify/query", "PhoneVerify");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://ws.cdyne.com/PhoneVerify/query", "PhoneVerifySoap12"));
            ports.add(new javax.xml.namespace.QName("http://ws.cdyne.com/PhoneVerify/query", "PhoneVerifySoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("PhoneVerifySoap12".equals(portName)) {
            setPhoneVerifySoap12EndpointAddress(address);
        }
        else 
if ("PhoneVerifySoap".equals(portName)) {
            setPhoneVerifySoapEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
