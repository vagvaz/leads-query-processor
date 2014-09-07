/**
 * LicenseCheckLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.cdyne.ws.licenseCheck;

public class LicenseCheckLocator extends org.apache.axis.client.Service implements com.apatar.cdyne.ws.licenseCheck.LicenseCheck {

    public LicenseCheckLocator() {
    }


    public LicenseCheckLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public LicenseCheckLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for LicenseCheckSoap12
    private java.lang.String LicenseCheckSoap12_address = "http://ws.cdyne.com/LicenseCheckws/LicenseCheck.asmx";

    public java.lang.String getLicenseCheckSoap12Address() {
        return LicenseCheckSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String LicenseCheckSoap12WSDDServiceName = "LicenseCheckSoap12";

    public java.lang.String getLicenseCheckSoap12WSDDServiceName() {
        return LicenseCheckSoap12WSDDServiceName;
    }

    public void setLicenseCheckSoap12WSDDServiceName(java.lang.String name) {
        LicenseCheckSoap12WSDDServiceName = name;
    }

    public com.apatar.cdyne.ws.licenseCheck.LicenseCheckSoap_PortType getLicenseCheckSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(LicenseCheckSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getLicenseCheckSoap12(endpoint);
    }

    public com.apatar.cdyne.ws.licenseCheck.LicenseCheckSoap_PortType getLicenseCheckSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.apatar.cdyne.ws.licenseCheck.LicenseCheckSoap12Stub _stub = new com.apatar.cdyne.ws.licenseCheck.LicenseCheckSoap12Stub(portAddress, this);
            _stub.setPortName(getLicenseCheckSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setLicenseCheckSoap12EndpointAddress(java.lang.String address) {
        LicenseCheckSoap12_address = address;
    }


    // Use to get a proxy class for LicenseCheckSoap
    private java.lang.String LicenseCheckSoap_address = "http://ws.cdyne.com/LicenseCheckws/LicenseCheck.asmx";

    public java.lang.String getLicenseCheckSoapAddress() {
        return LicenseCheckSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String LicenseCheckSoapWSDDServiceName = "LicenseCheckSoap";

    public java.lang.String getLicenseCheckSoapWSDDServiceName() {
        return LicenseCheckSoapWSDDServiceName;
    }

    public void setLicenseCheckSoapWSDDServiceName(java.lang.String name) {
        LicenseCheckSoapWSDDServiceName = name;
    }

    public com.apatar.cdyne.ws.licenseCheck.LicenseCheckSoap_PortType getLicenseCheckSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(LicenseCheckSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getLicenseCheckSoap(endpoint);
    }

    public com.apatar.cdyne.ws.licenseCheck.LicenseCheckSoap_PortType getLicenseCheckSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.apatar.cdyne.ws.licenseCheck.LicenseCheckSoap_BindingStub _stub = new com.apatar.cdyne.ws.licenseCheck.LicenseCheckSoap_BindingStub(portAddress, this);
            _stub.setPortName(getLicenseCheckSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setLicenseCheckSoapEndpointAddress(java.lang.String address) {
        LicenseCheckSoap_address = address;
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
            if (com.apatar.cdyne.ws.licenseCheck.LicenseCheckSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.apatar.cdyne.ws.licenseCheck.LicenseCheckSoap12Stub _stub = new com.apatar.cdyne.ws.licenseCheck.LicenseCheckSoap12Stub(new java.net.URL(LicenseCheckSoap12_address), this);
                _stub.setPortName(getLicenseCheckSoap12WSDDServiceName());
                return _stub;
            }
            if (com.apatar.cdyne.ws.licenseCheck.LicenseCheckSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.apatar.cdyne.ws.licenseCheck.LicenseCheckSoap_BindingStub _stub = new com.apatar.cdyne.ws.licenseCheck.LicenseCheckSoap_BindingStub(new java.net.URL(LicenseCheckSoap_address), this);
                _stub.setPortName(getLicenseCheckSoapWSDDServiceName());
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
        if ("LicenseCheckSoap12".equals(inputPortName)) {
            return getLicenseCheckSoap12();
        }
        else if ("LicenseCheckSoap".equals(inputPortName)) {
            return getLicenseCheckSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://ws.cdyne.com/", "LicenseCheck");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://ws.cdyne.com/", "LicenseCheckSoap12"));
            ports.add(new javax.xml.namespace.QName("http://ws.cdyne.com/", "LicenseCheckSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("LicenseCheckSoap12".equals(portName)) {
            setLicenseCheckSoap12EndpointAddress(address);
        }
        else 
if ("LicenseCheckSoap".equals(portName)) {
            setLicenseCheckSoapEndpointAddress(address);
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
