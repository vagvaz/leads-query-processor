/**
 * PhoneVerifySoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.cdyne.ws;

public interface PhoneVerifySoap_PortType extends java.rmi.Remote {

    /**
     * Insert a phone number and your license key in the fields below
     * to verify the phone number. This method also provides additional information
     * about the phone number which includes the RateCenter, Telecom Address,
     * Wireless, Switch Information, CLLI, LATA, Company, TimeZone, and more.<br
     * /><br />For more information, please visit our <a href='http://wiki.cdyne.com/wiki/index.php?title=Phone_Verification'>wiki</a>.
     */
    public com.apatar.cdyne.ws.PhoneReturn checkPhoneNumber(java.lang.String phoneNumber, java.lang.String licenseKey) throws java.rmi.RemoteException;

    /**
     * This method is the same as <a href='http://ws.cdyne.com/phoneverify/phoneverify.asmx?op=CheckPhoneNumber'>CheckPhoneNumber</a>,
     * although you are allowed to insert an array of phone numbers to be
     * validated.<br /><br />For more information, please visit our <a href='http://wiki.cdyne.com/wiki/index.php?title=Phone_Verification'>wiki</a>.
     */
    public com.apatar.cdyne.ws.ArrayOfPhoneReturn checkPhoneNumbers(com.apatar.cdyne.ws.ArrayOfString phoneNumbers, java.lang.String licenseKey) throws java.rmi.RemoteException;
}
