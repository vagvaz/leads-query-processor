/**
 * QueryDeathIndexLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.cdyne.ws.deathindex;

public class QueryDeathIndexLocator extends org.apache.axis.client.Service implements com.apatar.cdyne.ws.deathindex.QueryDeathIndex {

    public QueryDeathIndexLocator() {
    }


    public QueryDeathIndexLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public QueryDeathIndexLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for QueryDeathIndexSoap
    private java.lang.String QueryDeathIndexSoap_address = "http://ws.cdyne.com/DeathIndex/QueryDeathIndex.asmx";

    public java.lang.String getQueryDeathIndexSoapAddress() {
        return QueryDeathIndexSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String QueryDeathIndexSoapWSDDServiceName = "QueryDeathIndexSoap";

    public java.lang.String getQueryDeathIndexSoapWSDDServiceName() {
        return QueryDeathIndexSoapWSDDServiceName;
    }

    public void setQueryDeathIndexSoapWSDDServiceName(java.lang.String name) {
        QueryDeathIndexSoapWSDDServiceName = name;
    }

    public com.apatar.cdyne.ws.deathindex.QueryDeathIndexSoap_PortType getQueryDeathIndexSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(QueryDeathIndexSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getQueryDeathIndexSoap(endpoint);
    }

    public com.apatar.cdyne.ws.deathindex.QueryDeathIndexSoap_PortType getQueryDeathIndexSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.apatar.cdyne.ws.deathindex.QueryDeathIndexSoap_BindingStub _stub = new com.apatar.cdyne.ws.deathindex.QueryDeathIndexSoap_BindingStub(portAddress, this);
            _stub.setPortName(getQueryDeathIndexSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setQueryDeathIndexSoapEndpointAddress(java.lang.String address) {
        QueryDeathIndexSoap_address = address;
    }


    // Use to get a proxy class for QueryDeathIndexSoap12
    private java.lang.String QueryDeathIndexSoap12_address = "http://ws.cdyne.com/DeathIndex/QueryDeathIndex.asmx";

    public java.lang.String getQueryDeathIndexSoap12Address() {
        return QueryDeathIndexSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String QueryDeathIndexSoap12WSDDServiceName = "QueryDeathIndexSoap12";

    public java.lang.String getQueryDeathIndexSoap12WSDDServiceName() {
        return QueryDeathIndexSoap12WSDDServiceName;
    }

    public void setQueryDeathIndexSoap12WSDDServiceName(java.lang.String name) {
        QueryDeathIndexSoap12WSDDServiceName = name;
    }

    public com.apatar.cdyne.ws.deathindex.QueryDeathIndexSoap_PortType getQueryDeathIndexSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(QueryDeathIndexSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getQueryDeathIndexSoap12(endpoint);
    }

    public com.apatar.cdyne.ws.deathindex.QueryDeathIndexSoap_PortType getQueryDeathIndexSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.apatar.cdyne.ws.deathindex.QueryDeathIndexSoap12Stub _stub = new com.apatar.cdyne.ws.deathindex.QueryDeathIndexSoap12Stub(portAddress, this);
            _stub.setPortName(getQueryDeathIndexSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setQueryDeathIndexSoap12EndpointAddress(java.lang.String address) {
        QueryDeathIndexSoap12_address = address;
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
            if (com.apatar.cdyne.ws.deathindex.QueryDeathIndexSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.apatar.cdyne.ws.deathindex.QueryDeathIndexSoap_BindingStub _stub = new com.apatar.cdyne.ws.deathindex.QueryDeathIndexSoap_BindingStub(new java.net.URL(QueryDeathIndexSoap_address), this);
                _stub.setPortName(getQueryDeathIndexSoapWSDDServiceName());
                return _stub;
            }
            if (com.apatar.cdyne.ws.deathindex.QueryDeathIndexSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.apatar.cdyne.ws.deathindex.QueryDeathIndexSoap12Stub _stub = new com.apatar.cdyne.ws.deathindex.QueryDeathIndexSoap12Stub(new java.net.URL(QueryDeathIndexSoap12_address), this);
                _stub.setPortName(getQueryDeathIndexSoap12WSDDServiceName());
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
        if ("QueryDeathIndexSoap".equals(inputPortName)) {
            return getQueryDeathIndexSoap();
        }
        else if ("QueryDeathIndexSoap12".equals(inputPortName)) {
            return getQueryDeathIndexSoap12();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://ws.cdyne.com/DeathIndex/QueryDeathIndex", "QueryDeathIndex");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://ws.cdyne.com/DeathIndex/QueryDeathIndex", "QueryDeathIndexSoap"));
            ports.add(new javax.xml.namespace.QName("http://ws.cdyne.com/DeathIndex/QueryDeathIndex", "QueryDeathIndexSoap12"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("QueryDeathIndexSoap".equals(portName)) {
            setQueryDeathIndexSoapEndpointAddress(address);
        }
        else 
if ("QueryDeathIndexSoap12".equals(portName)) {
            setQueryDeathIndexSoap12EndpointAddress(address);
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
