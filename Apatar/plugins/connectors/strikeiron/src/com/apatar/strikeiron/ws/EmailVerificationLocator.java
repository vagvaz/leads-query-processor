/**
 * EmailVerificationLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.strikeiron.ws;

public class EmailVerificationLocator extends org.apache.axis.client.Service implements com.apatar.strikeiron.ws.EmailVerification {

/**
 * Email Verification Web Services
 */

    public EmailVerificationLocator() {
    }


    public EmailVerificationLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public EmailVerificationLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for EmailVerificationSoap
    private java.lang.String EmailVerificationSoap_address = "http://ws.strikeiron.com/apatar.Dimple/EmailAddressValidator";

    public java.lang.String getEmailVerificationSoapAddress() {
        return EmailVerificationSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String EmailVerificationSoapWSDDServiceName = "EmailVerificationSoap";

    public java.lang.String getEmailVerificationSoapWSDDServiceName() {
        return EmailVerificationSoapWSDDServiceName;
    }

    public void setEmailVerificationSoapWSDDServiceName(java.lang.String name) {
        EmailVerificationSoapWSDDServiceName = name;
    }

    public com.apatar.strikeiron.ws.EmailVerificationSoap_PortType getEmailVerificationSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(EmailVerificationSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getEmailVerificationSoap(endpoint);
    }

    public com.apatar.strikeiron.ws.EmailVerificationSoap_PortType getEmailVerificationSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.apatar.strikeiron.ws.EmailVerificationSoap_BindingStub _stub = new com.apatar.strikeiron.ws.EmailVerificationSoap_BindingStub(portAddress, this);
            _stub.setPortName(getEmailVerificationSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setEmailVerificationSoapEndpointAddress(java.lang.String address) {
        EmailVerificationSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.apatar.strikeiron.ws.EmailVerificationSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.apatar.strikeiron.ws.EmailVerificationSoap_BindingStub _stub = new com.apatar.strikeiron.ws.EmailVerificationSoap_BindingStub(new java.net.URL(EmailVerificationSoap_address), this);
                _stub.setPortName(getEmailVerificationSoapWSDDServiceName());
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
        if ("EmailVerificationSoap".equals(inputPortName)) {
            return getEmailVerificationSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.strikeiron.com", "EmailVerification");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.strikeiron.com", "EmailVerificationSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("EmailVerificationSoap".equals(portName)) {
            setEmailVerificationSoapEndpointAddress(address);
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
