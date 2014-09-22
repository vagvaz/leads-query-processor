/**
 * DemographixQuerySoap12Stub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.cdyne.ws.demographix;

public class DemographixQuerySoap12Stub extends org.apache.axis.client.Stub implements com.apatar.cdyne.ws.demographix.DemographixQuerySoap_PortType {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[15];
        _initOperationDesc1();
        _initOperationDesc2();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetLocationInformationByLatitudeLongitude");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "Latitude"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"), java.math.BigDecimal.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "Longitude"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"), java.math.BigDecimal.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "LicenseKey"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "SummaryInformation"));
        oper.setReturnClass(com.apatar.cdyne.ws.demographix.SummaryInformation.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetLocationInformationByLatitudeLongitudeResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetLocationInformationByAddress");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "AddressLine1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "City"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "StateAbbrev"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "ZipCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "LicenseKey"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "SummaryInformation"));
        oper.setReturnClass(com.apatar.cdyne.ws.demographix.SummaryInformation.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetLocationInformationByAddressResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetIncomeHouseValueByAddress");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "AddressLine1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "City"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "StateAbbrev"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "ZipCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "LicenseKey"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "IncomeAndHouseValue"));
        oper.setReturnClass(com.apatar.cdyne.ws.demographix.IncomeAndHouseValue.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetIncomeHouseValueByAddressResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetPlaceIDbyAddress");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "AddressLine1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "City"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "StateAbbrev"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "ZipCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "LicenseKey"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "PlaceInfo"));
        oper.setReturnClass(com.apatar.cdyne.ws.demographix.PlaceInfo.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetPlaceIDbyAddressResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetPlaceIDbyCensusTractAndBlock");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "StateAbbrev"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "CensusTract"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "CensusBlock"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "LicenseKey"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "PlaceInfo"));
        oper.setReturnClass(com.apatar.cdyne.ws.demographix.PlaceInfo.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetPlaceIDbyCensusTractAndBlockResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetSummaryInformationByPlaceID");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "StateAbbrev"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "PlaceID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "SummaryInformation"));
        oper.setReturnClass(com.apatar.cdyne.ws.demographix.SummaryInformation.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetSummaryInformationByPlaceIDResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetNeighborhoodAgeInDataSet");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "StateAbbrev"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "PlaceID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "CensusInfoWithDataSet"));
        oper.setReturnClass(com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetNeighborhoodAgeInDataSetResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetNeighborhoodAgeGenderMaleInDataSet");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "StateAbbrev"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "PlaceID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "CensusInfoWithDataSet"));
        oper.setReturnClass(com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetNeighborhoodAgeGenderMaleInDataSetResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetNeighborhoodAgeGenderFemaleInDataSet");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "StateAbbrev"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "PlaceID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "CensusInfoWithDataSet"));
        oper.setReturnClass(com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetNeighborhoodAgeGenderFemaleInDataSetResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetNeighborhoodRealtyValueInDataSet");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "StateAbbrev"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "PlaceID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "CensusInfoWithDataSet"));
        oper.setReturnClass(com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetNeighborhoodRealtyValueInDataSetResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[9] = oper;

    }

    private static void _initOperationDesc2(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetNeighborhoodVehiclesPerHouseholdInDataset");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "StateAbbr"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "PlaceID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "CensusInfoWithDataSet"));
        oper.setReturnClass(com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetNeighborhoodVehiclesPerHouseholdInDatasetResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[10] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetNeighborhoodPlaceofBirthbyCitizenshipStatusInDataset");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "StateAbbr"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "PlaceID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "CensusInfoWithDataSet"));
        oper.setReturnClass(com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetNeighborhoodPlaceofBirthbyCitizenshipStatusInDatasetResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[11] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetNeighborhoodYearStructuresBuilt");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "StateAbbr"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "PlaceID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "CensusInfoWithDataSet"));
        oper.setReturnClass(com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetNeighborhoodYearStructuresBuiltResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[12] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetNeighborhoodLinguisticIsolation");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "StateAbbr"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "PlaceID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "CensusInfoWithDataSet"));
        oper.setReturnClass(com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetNeighborhoodLinguisticIsolationResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[13] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetVersion");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetVersionResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[14] = oper;

    }

    public DemographixQuerySoap12Stub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public DemographixQuerySoap12Stub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public DemographixQuerySoap12Stub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", ">CensusInfoWithDataSet>CensusDataSet");
            cachedSerQNames.add(qName);
            cls = com.apatar.cdyne.ws.demographix.CensusInfoWithDataSetCensusDataSet.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "CensusInfoWithDataSet");
            cachedSerQNames.add(qName);
            cls = com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GenderPercentagesCls");
            cachedSerQNames.add(qName);
            cls = com.apatar.cdyne.ws.demographix.GenderPercentagesCls.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "IncomeAndHouseValue");
            cachedSerQNames.add(qName);
            cls = com.apatar.cdyne.ws.demographix.IncomeAndHouseValue.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "MaritalStatusPercentagesCls");
            cachedSerQNames.add(qName);
            cls = com.apatar.cdyne.ws.demographix.MaritalStatusPercentagesCls.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "PlaceInfo");
            cachedSerQNames.add(qName);
            cls = com.apatar.cdyne.ws.demographix.PlaceInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "PlaceInformationCls");
            cachedSerQNames.add(qName);
            cls = com.apatar.cdyne.ws.demographix.PlaceInformationCls.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "RacePercentagesCls");
            cachedSerQNames.add(qName);
            cls = com.apatar.cdyne.ws.demographix.RacePercentagesCls.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "SummaryInformation");
            cachedSerQNames.add(qName);
            cls = com.apatar.cdyne.ws.demographix.SummaryInformation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public com.apatar.cdyne.ws.demographix.SummaryInformation getLocationInformationByLatitudeLongitude(java.math.BigDecimal latitude, java.math.BigDecimal longitude, java.lang.String licenseKey) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.cdyne.com/DemographixWS/GetLocationInformationByLatitudeLongitude");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetLocationInformationByLatitudeLongitude"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {latitude, longitude, licenseKey});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.apatar.cdyne.ws.demographix.SummaryInformation) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.apatar.cdyne.ws.demographix.SummaryInformation) org.apache.axis.utils.JavaUtils.convert(_resp, com.apatar.cdyne.ws.demographix.SummaryInformation.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.apatar.cdyne.ws.demographix.SummaryInformation getLocationInformationByAddress(java.lang.String addressLine1, java.lang.String city, java.lang.String stateAbbrev, java.lang.String zipCode, java.lang.String licenseKey) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.cdyne.com/DemographixWS/GetLocationInformationByAddress");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetLocationInformationByAddress"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {addressLine1, city, stateAbbrev, zipCode, licenseKey});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.apatar.cdyne.ws.demographix.SummaryInformation) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.apatar.cdyne.ws.demographix.SummaryInformation) org.apache.axis.utils.JavaUtils.convert(_resp, com.apatar.cdyne.ws.demographix.SummaryInformation.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.apatar.cdyne.ws.demographix.IncomeAndHouseValue getIncomeHouseValueByAddress(java.lang.String addressLine1, java.lang.String city, java.lang.String stateAbbrev, java.lang.String zipCode, java.lang.String licenseKey) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.cdyne.com/DemographixWS/GetIncomeHouseValueByAddress");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetIncomeHouseValueByAddress"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {addressLine1, city, stateAbbrev, zipCode, licenseKey});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.apatar.cdyne.ws.demographix.IncomeAndHouseValue) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.apatar.cdyne.ws.demographix.IncomeAndHouseValue) org.apache.axis.utils.JavaUtils.convert(_resp, com.apatar.cdyne.ws.demographix.IncomeAndHouseValue.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.apatar.cdyne.ws.demographix.PlaceInfo getPlaceIDbyAddress(java.lang.String addressLine1, java.lang.String city, java.lang.String stateAbbrev, java.lang.String zipCode, java.lang.String licenseKey) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.cdyne.com/DemographixWS/GetPlaceIDbyAddress");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetPlaceIDbyAddress"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {addressLine1, city, stateAbbrev, zipCode, licenseKey});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.apatar.cdyne.ws.demographix.PlaceInfo) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.apatar.cdyne.ws.demographix.PlaceInfo) org.apache.axis.utils.JavaUtils.convert(_resp, com.apatar.cdyne.ws.demographix.PlaceInfo.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.apatar.cdyne.ws.demographix.PlaceInfo getPlaceIDbyCensusTractAndBlock(java.lang.String stateAbbrev, java.lang.String censusTract, java.lang.String censusBlock, java.lang.String licenseKey) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.cdyne.com/DemographixWS/GetPlaceIDbyCensusTractAndBlock");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetPlaceIDbyCensusTractAndBlock"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {stateAbbrev, censusTract, censusBlock, licenseKey});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.apatar.cdyne.ws.demographix.PlaceInfo) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.apatar.cdyne.ws.demographix.PlaceInfo) org.apache.axis.utils.JavaUtils.convert(_resp, com.apatar.cdyne.ws.demographix.PlaceInfo.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.apatar.cdyne.ws.demographix.SummaryInformation getSummaryInformationByPlaceID(java.lang.String stateAbbrev, java.lang.String placeID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.cdyne.com/DemographixWS/GetSummaryInformationByPlaceID");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetSummaryInformationByPlaceID"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {stateAbbrev, placeID});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.apatar.cdyne.ws.demographix.SummaryInformation) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.apatar.cdyne.ws.demographix.SummaryInformation) org.apache.axis.utils.JavaUtils.convert(_resp, com.apatar.cdyne.ws.demographix.SummaryInformation.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet getNeighborhoodAgeInDataSet(java.lang.String stateAbbrev, java.lang.String placeID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.cdyne.com/DemographixWS/GetNeighborhoodAgeInDataSet");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetNeighborhoodAgeInDataSet"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {stateAbbrev, placeID});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet) org.apache.axis.utils.JavaUtils.convert(_resp, com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet getNeighborhoodAgeGenderMaleInDataSet(java.lang.String stateAbbrev, java.lang.String placeID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.cdyne.com/DemographixWS/GetNeighborhoodAgeGenderMaleInDataSet");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetNeighborhoodAgeGenderMaleInDataSet"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {stateAbbrev, placeID});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet) org.apache.axis.utils.JavaUtils.convert(_resp, com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet getNeighborhoodAgeGenderFemaleInDataSet(java.lang.String stateAbbrev, java.lang.String placeID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.cdyne.com/DemographixWS/GetNeighborhoodAgeGenderFemaleInDataSet");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetNeighborhoodAgeGenderFemaleInDataSet"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {stateAbbrev, placeID});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet) org.apache.axis.utils.JavaUtils.convert(_resp, com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet getNeighborhoodRealtyValueInDataSet(java.lang.String stateAbbrev, java.lang.String placeID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.cdyne.com/DemographixWS/GetNeighborhoodRealtyValueInDataSet");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetNeighborhoodRealtyValueInDataSet"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {stateAbbrev, placeID});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet) org.apache.axis.utils.JavaUtils.convert(_resp, com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet getNeighborhoodVehiclesPerHouseholdInDataset(java.lang.String stateAbbr, java.lang.String placeID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[10]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.cdyne.com/DemographixWS/GetNeighborhoodVehiclesPerHouseholdInDataset");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetNeighborhoodVehiclesPerHouseholdInDataset"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {stateAbbr, placeID});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet) org.apache.axis.utils.JavaUtils.convert(_resp, com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet getNeighborhoodPlaceofBirthbyCitizenshipStatusInDataset(java.lang.String stateAbbr, java.lang.String placeID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[11]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.cdyne.com/DemographixWS/GetNeighborhoodPlaceofBirthbyCitizenshipStatusInDataset");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetNeighborhoodPlaceofBirthbyCitizenshipStatusInDataset"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {stateAbbr, placeID});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet) org.apache.axis.utils.JavaUtils.convert(_resp, com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet getNeighborhoodYearStructuresBuilt(java.lang.String stateAbbr, java.lang.String placeID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[12]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.cdyne.com/DemographixWS/GetNeighborhoodYearStructuresBuilt");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetNeighborhoodYearStructuresBuilt"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {stateAbbr, placeID});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet) org.apache.axis.utils.JavaUtils.convert(_resp, com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet getNeighborhoodLinguisticIsolation(java.lang.String stateAbbr, java.lang.String placeID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[13]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.cdyne.com/DemographixWS/GetNeighborhoodLinguisticIsolation");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetNeighborhoodLinguisticIsolation"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {stateAbbr, placeID});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet) org.apache.axis.utils.JavaUtils.convert(_resp, com.apatar.cdyne.ws.demographix.CensusInfoWithDataSet.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String getVersion() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[14]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.cdyne.com/DemographixWS/GetVersion");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GetVersion"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
