/**
 * DemographixQuerySoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.cdyne.ws.demographix;

public interface DemographixQuerySoap_PortType extends java.rmi.Remote {

    /**
     * This function will return Summary Information about a location
     * via Longitude and Latitude. Use a LicenseKey of 0 for testing.
     */
    public com.apatar.cdyne.ws.demographix.SummaryInformation getLocationInformationByLatitudeLongitude(java.math.BigDecimal latitude, java.math.BigDecimal longitude, java.lang.String licenseKey) throws java.rmi.RemoteException;

    /**
     * This function will return Summary Information about a location
     * via the Address.  Address Line 2 is not needed for Summary information.
     * For more advanced address information use CDYNE's Postal Address Corrector.
     * <b>This method is the best place to start!</b> Use a LicenseKey of
     * 0 for testing.
     */
    public com.apatar.cdyne.ws.demographix.SummaryInformation getLocationInformationByAddress(java.lang.String addressLine1, java.lang.String city, java.lang.String stateAbbrev, java.lang.String zipCode, java.lang.String licenseKey) throws java.rmi.RemoteException;

    /**
     * This function will return only Income and House Value. Use
     * a LicenseKey of 0 for testing.
     */
    public com.apatar.cdyne.ws.demographix.IncomeAndHouseValue getIncomeHouseValueByAddress(java.lang.String addressLine1, java.lang.String city, java.lang.String stateAbbrev, java.lang.String zipCode, java.lang.String licenseKey) throws java.rmi.RemoteException;

    /**
     * This function will return the Place ID that can be used with
     * more of the advanced functions via StateAbbrev, Tract, and Block.
     */
    public com.apatar.cdyne.ws.demographix.PlaceInfo getPlaceIDbyAddress(java.lang.String addressLine1, java.lang.String city, java.lang.String stateAbbrev, java.lang.String zipCode, java.lang.String licenseKey) throws java.rmi.RemoteException;

    /**
     * This function will return the Place ID that can be used with
     * more of the advanced functions via StateAbbrev, Tract, and Block.
     */
    public com.apatar.cdyne.ws.demographix.PlaceInfo getPlaceIDbyCensusTractAndBlock(java.lang.String stateAbbrev, java.lang.String censusTract, java.lang.String censusBlock, java.lang.String licenseKey) throws java.rmi.RemoteException;

    /**
     * This function will return Summary Information about a location
     * via StateAbbrev and PlaceID.
     */
    public com.apatar.cdyne.ws.demographix.SummaryInformation getSummaryInformationByPlaceID(java.lang.String stateAbbrev, java.lang.String placeID) throws java.rmi.RemoteException;

    /**
     * This function will return the Age Populations in a DataSet.
     * Use a LicenseKey of 0 for testing.
     */
    public com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet getNeighborhoodAgeInDataSet(java.lang.String stateAbbrev, java.lang.String placeID) throws java.rmi.RemoteException;

    /**
     * This function will return the Male Age Populations in a DataSet.
     * Use a LicenseKey of 0 for testing.
     */
    public com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet getNeighborhoodAgeGenderMaleInDataSet(java.lang.String stateAbbrev, java.lang.String placeID) throws java.rmi.RemoteException;

    /**
     * This function will return the Female Age Populations in a DataSet.
     * Use a LicenseKey of 0 for testing.
     */
    public com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet getNeighborhoodAgeGenderFemaleInDataSet(java.lang.String stateAbbrev, java.lang.String placeID) throws java.rmi.RemoteException;

    /**
     * This function will return the Realty Value Population in a
     * DataSet. Use a LicenseKey of 0 for testing.
     */
    public com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet getNeighborhoodRealtyValueInDataSet(java.lang.String stateAbbrev, java.lang.String placeID) throws java.rmi.RemoteException;

    /**
     * This function will return the number of cars for each household
     * in the given neighborhood in a dataset. Use a LicenseKey of 0 for
     * testing.
     */
    public com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet getNeighborhoodVehiclesPerHouseholdInDataset(java.lang.String stateAbbr, java.lang.String placeID) throws java.rmi.RemoteException;

    /**
     * This function will return the place of birth by their citizenship
     * status in a dataset. Use a LicenseKey of 0 for testing.
     */
    public com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet getNeighborhoodPlaceofBirthbyCitizenshipStatusInDataset(java.lang.String stateAbbr, java.lang.String placeID) throws java.rmi.RemoteException;

    /**
     * This function will return the year the the houses build during
     * a period in a dataset. Use a LicenseKey of 0 for testing.
     */
    public com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet getNeighborhoodYearStructuresBuilt(java.lang.String stateAbbr, java.lang.String placeID) throws java.rmi.RemoteException;

    /**
     * This function will return isolation of different cultures in
     * a particular area in a dataset. Use a LicenseKey of 0 for testing.
     */
    public com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet getNeighborhoodLinguisticIsolation(java.lang.String stateAbbr, java.lang.String placeID) throws java.rmi.RemoteException;

    /**
     * Version Information.
     */
    public java.lang.String getVersion() throws java.rmi.RemoteException;
}
