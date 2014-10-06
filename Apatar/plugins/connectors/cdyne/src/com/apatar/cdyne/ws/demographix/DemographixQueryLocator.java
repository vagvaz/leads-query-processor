/**
 * DemographixQueryLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.cdyne.ws.demographix;

public class DemographixQueryLocator extends org.apache.axis.client.Service implements com.apatar.cdyne.ws.demographix.DemographixQuery {

/**
 * CDYNE�s Demographics� is a neighborhood level segmentation system
 * built using various data sources in one combined database. By classifying
 * all U.S. neighborhoods according to consumers� different stages of
 * life and related income levels, it provides a current, accurate and
 * consistent framework to view customers at a neighborhood level. It
 * enables users to compare customers across their product mix, across
 * time and across their enterprise. With high coverage, Demographix
 * also enables users to see differences in how U.S. neighborhoods spend
 * time and money. This turns raw data about customers into accurate,
 * actionable information.  Use a Licensekey of 0 for testing.
 */

    public DemographixQueryLocator() {
    }


    public DemographixQueryLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public DemographixQueryLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for DemographixQuerySoap
    private java.lang.String DemographixQuerySoap_address = "http://ws.cdyne.com/DemographixWS/DemographixQuery.asmx";

    public java.lang.String getDemographixQuerySoapAddress() {
        return DemographixQuerySoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String DemographixQuerySoapWSDDServiceName = "DemographixQuerySoap";

    public java.lang.String getDemographixQuerySoapWSDDServiceName() {
        return DemographixQuerySoapWSDDServiceName;
    }

    public void setDemographixQuerySoapWSDDServiceName(java.lang.String name) {
        DemographixQuerySoapWSDDServiceName = name;
    }

    public com.apatar.cdyne.ws.demographix.DemographixQuerySoap_PortType getDemographixQuerySoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(DemographixQuerySoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getDemographixQuerySoap(endpoint);
    }

    public com.apatar.cdyne.ws.demographix.DemographixQuerySoap_PortType getDemographixQuerySoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.apatar.cdyne.ws.demographix.DemographixQuerySoap_BindingStub _stub = new com.apatar.cdyne.ws.demographix.DemographixQuerySoap_BindingStub(portAddress, this);
            _stub.setPortName(getDemographixQuerySoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setDemographixQuerySoapEndpointAddress(java.lang.String address) {
        DemographixQuerySoap_address = address;
    }


    // Use to get a proxy class for DemographixQuerySoap12
    private java.lang.String DemographixQuerySoap12_address = "http://ws.cdyne.com/DemographixWS/DemographixQuery.asmx";

    public java.lang.String getDemographixQuerySoap12Address() {
        return DemographixQuerySoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String DemographixQuerySoap12WSDDServiceName = "DemographixQuerySoap12";

    public java.lang.String getDemographixQuerySoap12WSDDServiceName() {
        return DemographixQuerySoap12WSDDServiceName;
    }

    public void setDemographixQuerySoap12WSDDServiceName(java.lang.String name) {
        DemographixQuerySoap12WSDDServiceName = name;
    }

    public com.apatar.cdyne.ws.demographix.DemographixQuerySoap_PortType getDemographixQuerySoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(DemographixQuerySoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getDemographixQuerySoap12(endpoint);
    }

    public com.apatar.cdyne.ws.demographix.DemographixQuerySoap_PortType getDemographixQuerySoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.apatar.cdyne.ws.demographix.DemographixQuerySoap12Stub _stub = new com.apatar.cdyne.ws.demographix.DemographixQuerySoap12Stub(portAddress, this);
            _stub.setPortName(getDemographixQuerySoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setDemographixQuerySoap12EndpointAddress(java.lang.String address) {
        DemographixQuerySoap12_address = address;
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
            if (com.apatar.cdyne.ws.demographix.DemographixQuerySoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.apatar.cdyne.ws.demographix.DemographixQuerySoap_BindingStub _stub = new com.apatar.cdyne.ws.demographix.DemographixQuerySoap_BindingStub(new java.net.URL(DemographixQuerySoap_address), this);
                _stub.setPortName(getDemographixQuerySoapWSDDServiceName());
                return _stub;
            }
            if (com.apatar.cdyne.ws.demographix.DemographixQuerySoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.apatar.cdyne.ws.demographix.DemographixQuerySoap12Stub _stub = new com.apatar.cdyne.ws.demographix.DemographixQuerySoap12Stub(new java.net.URL(DemographixQuerySoap12_address), this);
                _stub.setPortName(getDemographixQuerySoap12WSDDServiceName());
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
        if ("DemographixQuerySoap".equals(inputPortName)) {
            return getDemographixQuerySoap();
        }
        else if ("DemographixQuerySoap12".equals(inputPortName)) {
            return getDemographixQuerySoap12();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "DemographixQuery");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "DemographixQuerySoap"));
            ports.add(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "DemographixQuerySoap12"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("DemographixQuerySoap".equals(portName)) {
            setDemographixQuerySoapEndpointAddress(address);
        }
        else 
if ("DemographixQuerySoap12".equals(portName)) {
            setDemographixQuerySoap12EndpointAddress(address);
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
