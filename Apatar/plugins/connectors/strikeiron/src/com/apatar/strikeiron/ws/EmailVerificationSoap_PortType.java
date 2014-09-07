/**
 * EmailVerificationSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.strikeiron.ws;

public interface EmailVerificationSoap_PortType extends java.rmi.Remote {

    /**
     * Verify the email is valid or not
     */
    public com.apatar.strikeiron.ws.ValidateEmailResult validateEmail(java.lang.String email, boolean checkAllServers) throws java.rmi.RemoteException;
    public void getRemainingHits() throws java.rmi.RemoteException;
}
