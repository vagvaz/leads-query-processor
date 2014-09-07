/**
 * QueryDeathIndexSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.cdyne.ws.deathindex;

public interface QueryDeathIndexSoap_PortType extends java.rmi.Remote {

    /**
     * Enter a SSN to get deceased information.  Use a LicenseKey
     * of 0 for testing.
     */
    public com.apatar.cdyne.ws.deathindex.DeceasedInfo deceasedBySSN(java.lang.String ssn, java.lang.String licenseKey) throws java.rmi.RemoteException;

    /**
     * Enter information in a minimum of 1 field.  No wildcards accepted.
     * The system will return a maximum of 50 records.  Leave fields blank
     * that you do not have information for.  Use a LicenseKey of 0 for testing.
     */
    public com.apatar.cdyne.ws.deathindex.SearchDeceasedInfoReturnDataSetResponseSearchDeceasedInfoReturnDataSetResult searchDeceasedInfoReturnDataSet(java.lang.String lastName, java.lang.String firstName, java.lang.String stateAbbrev, java.lang.String licenseKey) throws java.rmi.RemoteException;
}
