/**
 * LicenseCheckSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.cdyne.ws.licenseCheck;

public interface LicenseCheckSoap_PortType extends java.rmi.Remote {

    /**
     * Returns information about license key<br/><b>-3:</b> Service
     * error<br/><b>-2:</b> Key not found<br/><b>-1:</b> Not a guid<br/><b>0:</b>
     * Valid license key<br/><b>1:</b> Disabled key<br/><b>2:</b> Suspended
     * key<br/><b>3:</b> Valid demo key<br/><b>4:</b> Exhausted demo key
     */
    public int getKeyInfo(java.lang.String key) throws java.rmi.RemoteException;
}
