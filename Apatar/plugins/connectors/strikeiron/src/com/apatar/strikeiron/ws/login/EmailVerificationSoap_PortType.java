/**
 * EmailVerificationSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.strikeiron.ws.login;

public interface EmailVerificationSoap_PortType extends java.rmi.Remote {

    /**
     * Verify the email is valid or not
     */
    public void validateEmail(java.lang.String unregisteredUserEmail, java.lang.String userID, java.lang.String password, java.lang.String email, boolean checkAllServers, com.apatar.strikeiron.ws.login.holders.ValidateEmailResultHolder validateEmailResult, com.apatar.strikeiron.ws.login.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException;
    public void getRemainingHits(java.lang.String unregisteredUserEmail, java.lang.String userID, java.lang.String password, javax.xml.rpc.holders.IntHolder licenseStatusCode, javax.xml.rpc.holders.StringHolder licenseStatus, javax.xml.rpc.holders.IntHolder licenseActionCode, javax.xml.rpc.holders.StringHolder licenseAction, javax.xml.rpc.holders.IntHolder remainingHits, javax.xml.rpc.holders.BigDecimalHolder amount) throws java.rmi.RemoteException;
}
