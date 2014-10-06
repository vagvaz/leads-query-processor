/**
 * USAddress.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.strikeiron.ws.usaddress;

public class USAddress  extends com.apatar.strikeiron.ws.usaddress.Address  implements java.io.Serializable {
    private java.lang.String state;
    private java.lang.String urbanization;
    private java.lang.String zipPlus4;
    private java.lang.String zip;
    private java.lang.String zipAddOn;
    private java.lang.String carrierRoute;
    private java.lang.String PMB;
    private java.lang.String PMBDesignator;
    private java.lang.String deliveryPoint;
    private java.lang.String DPCheckDigit;
    private java.lang.String LACS;
    private java.lang.String CMRA;
    private java.lang.String DPV;
    private java.lang.String DPVFootnote;
    private java.lang.String RDI;
    private java.lang.String recordType;
    private java.lang.String congressDistrict;
    private java.lang.String county;
    private java.lang.String countyNumber;
    private java.lang.String stateNumber;
    private com.apatar.strikeiron.ws.usaddress.USGeoCode geoCode;

    public USAddress() {
    }

    public USAddress(
           java.lang.String state,
           java.lang.String urbanization,
           java.lang.String zipPlus4,
           java.lang.String zip,
           java.lang.String zipAddOn,
           java.lang.String carrierRoute,
           java.lang.String PMB,
           java.lang.String PMBDesignator,
           java.lang.String deliveryPoint,
           java.lang.String DPCheckDigit,
           java.lang.String LACS,
           java.lang.String CMRA,
           java.lang.String DPV,
           java.lang.String DPVFootnote,
           java.lang.String RDI,
           java.lang.String recordType,
           java.lang.String congressDistrict,
           java.lang.String county,
           java.lang.String countyNumber,
           java.lang.String stateNumber,
           com.apatar.strikeiron.ws.usaddress.USGeoCode geoCode) {
           this.state = state;
           this.urbanization = urbanization;
           this.zipPlus4 = zipPlus4;
           this.zip = zip;
           this.zipAddOn = zipAddOn;
           this.carrierRoute = carrierRoute;
           this.PMB = PMB;
           this.PMBDesignator = PMBDesignator;
           this.deliveryPoint = deliveryPoint;
           this.DPCheckDigit = DPCheckDigit;
           this.LACS = LACS;
           this.CMRA = CMRA;
           this.DPV = DPV;
           this.DPVFootnote = DPVFootnote;
           this.RDI = RDI;
           this.recordType = recordType;
           this.congressDistrict = congressDistrict;
           this.county = county;
           this.countyNumber = countyNumber;
           this.stateNumber = stateNumber;
           this.geoCode = geoCode;
    }


    /**
     * Gets the state value for this USAddress.
     * 
     * @return state
     */
    public java.lang.String getState() {
        return state;
    }


    /**
     * Sets the state value for this USAddress.
     * 
     * @param state
     */
    public void setState(java.lang.String state) {
        this.state = state;
    }


    /**
     * Gets the urbanization value for this USAddress.
     * 
     * @return urbanization
     */
    public java.lang.String getUrbanization() {
        return urbanization;
    }


    /**
     * Sets the urbanization value for this USAddress.
     * 
     * @param urbanization
     */
    public void setUrbanization(java.lang.String urbanization) {
        this.urbanization = urbanization;
    }


    /**
     * Gets the zipPlus4 value for this USAddress.
     * 
     * @return zipPlus4
     */
    public java.lang.String getZipPlus4() {
        return zipPlus4;
    }


    /**
     * Sets the zipPlus4 value for this USAddress.
     * 
     * @param zipPlus4
     */
    public void setZipPlus4(java.lang.String zipPlus4) {
        this.zipPlus4 = zipPlus4;
    }


    /**
     * Gets the zip value for this USAddress.
     * 
     * @return zip
     */
    public java.lang.String getZip() {
        return zip;
    }


    /**
     * Sets the zip value for this USAddress.
     * 
     * @param zip
     */
    public void setZip(java.lang.String zip) {
        this.zip = zip;
    }


    /**
     * Gets the zipAddOn value for this USAddress.
     * 
     * @return zipAddOn
     */
    public java.lang.String getZipAddOn() {
        return zipAddOn;
    }


    /**
     * Sets the zipAddOn value for this USAddress.
     * 
     * @param zipAddOn
     */
    public void setZipAddOn(java.lang.String zipAddOn) {
        this.zipAddOn = zipAddOn;
    }


    /**
     * Gets the carrierRoute value for this USAddress.
     * 
     * @return carrierRoute
     */
    public java.lang.String getCarrierRoute() {
        return carrierRoute;
    }


    /**
     * Sets the carrierRoute value for this USAddress.
     * 
     * @param carrierRoute
     */
    public void setCarrierRoute(java.lang.String carrierRoute) {
        this.carrierRoute = carrierRoute;
    }


    /**
     * Gets the PMB value for this USAddress.
     * 
     * @return PMB
     */
    public java.lang.String getPMB() {
        return PMB;
    }


    /**
     * Sets the PMB value for this USAddress.
     * 
     * @param PMB
     */
    public void setPMB(java.lang.String PMB) {
        this.PMB = PMB;
    }


    /**
     * Gets the PMBDesignator value for this USAddress.
     * 
     * @return PMBDesignator
     */
    public java.lang.String getPMBDesignator() {
        return PMBDesignator;
    }


    /**
     * Sets the PMBDesignator value for this USAddress.
     * 
     * @param PMBDesignator
     */
    public void setPMBDesignator(java.lang.String PMBDesignator) {
        this.PMBDesignator = PMBDesignator;
    }


    /**
     * Gets the deliveryPoint value for this USAddress.
     * 
     * @return deliveryPoint
     */
    public java.lang.String getDeliveryPoint() {
        return deliveryPoint;
    }


    /**
     * Sets the deliveryPoint value for this USAddress.
     * 
     * @param deliveryPoint
     */
    public void setDeliveryPoint(java.lang.String deliveryPoint) {
        this.deliveryPoint = deliveryPoint;
    }


    /**
     * Gets the DPCheckDigit value for this USAddress.
     * 
     * @return DPCheckDigit
     */
    public java.lang.String getDPCheckDigit() {
        return DPCheckDigit;
    }


    /**
     * Sets the DPCheckDigit value for this USAddress.
     * 
     * @param DPCheckDigit
     */
    public void setDPCheckDigit(java.lang.String DPCheckDigit) {
        this.DPCheckDigit = DPCheckDigit;
    }


    /**
     * Gets the LACS value for this USAddress.
     * 
     * @return LACS
     */
    public java.lang.String getLACS() {
        return LACS;
    }


    /**
     * Sets the LACS value for this USAddress.
     * 
     * @param LACS
     */
    public void setLACS(java.lang.String LACS) {
        this.LACS = LACS;
    }


    /**
     * Gets the CMRA value for this USAddress.
     * 
     * @return CMRA
     */
    public java.lang.String getCMRA() {
        return CMRA;
    }


    /**
     * Sets the CMRA value for this USAddress.
     * 
     * @param CMRA
     */
    public void setCMRA(java.lang.String CMRA) {
        this.CMRA = CMRA;
    }


    /**
     * Gets the DPV value for this USAddress.
     * 
     * @return DPV
     */
    public java.lang.String getDPV() {
        return DPV;
    }


    /**
     * Sets the DPV value for this USAddress.
     * 
     * @param DPV
     */
    public void setDPV(java.lang.String DPV) {
        this.DPV = DPV;
    }


    /**
     * Gets the DPVFootnote value for this USAddress.
     * 
     * @return DPVFootnote
     */
    public java.lang.String getDPVFootnote() {
        return DPVFootnote;
    }


    /**
     * Sets the DPVFootnote value for this USAddress.
     * 
     * @param DPVFootnote
     */
    public void setDPVFootnote(java.lang.String DPVFootnote) {
        this.DPVFootnote = DPVFootnote;
    }


    /**
     * Gets the RDI value for this USAddress.
     * 
     * @return RDI
     */
    public java.lang.String getRDI() {
        return RDI;
    }


    /**
     * Sets the RDI value for this USAddress.
     * 
     * @param RDI
     */
    public void setRDI(java.lang.String RDI) {
        this.RDI = RDI;
    }


    /**
     * Gets the recordType value for this USAddress.
     * 
     * @return recordType
     */
    public java.lang.String getRecordType() {
        return recordType;
    }


    /**
     * Sets the recordType value for this USAddress.
     * 
     * @param recordType
     */
    public void setRecordType(java.lang.String recordType) {
        this.recordType = recordType;
    }


    /**
     * Gets the congressDistrict value for this USAddress.
     * 
     * @return congressDistrict
     */
    public java.lang.String getCongressDistrict() {
        return congressDistrict;
    }


    /**
     * Sets the congressDistrict value for this USAddress.
     * 
     * @param congressDistrict
     */
    public void setCongressDistrict(java.lang.String congressDistrict) {
        this.congressDistrict = congressDistrict;
    }


    /**
     * Gets the county value for this USAddress.
     * 
     * @return county
     */
    public java.lang.String getCounty() {
        return county;
    }


    /**
     * Sets the county value for this USAddress.
     * 
     * @param county
     */
    public void setCounty(java.lang.String county) {
        this.county = county;
    }


    /**
     * Gets the countyNumber value for this USAddress.
     * 
     * @return countyNumber
     */
    public java.lang.String getCountyNumber() {
        return countyNumber;
    }


    /**
     * Sets the countyNumber value for this USAddress.
     * 
     * @param countyNumber
     */
    public void setCountyNumber(java.lang.String countyNumber) {
        this.countyNumber = countyNumber;
    }


    /**
     * Gets the stateNumber value for this USAddress.
     * 
     * @return stateNumber
     */
    public java.lang.String getStateNumber() {
        return stateNumber;
    }


    /**
     * Sets the stateNumber value for this USAddress.
     * 
     * @param stateNumber
     */
    public void setStateNumber(java.lang.String stateNumber) {
        this.stateNumber = stateNumber;
    }


    /**
     * Gets the geoCode value for this USAddress.
     * 
     * @return geoCode
     */
    public com.apatar.strikeiron.ws.usaddress.USGeoCode getGeoCode() {
        return geoCode;
    }


    /**
     * Sets the geoCode value for this USAddress.
     * 
     * @param geoCode
     */
    public void setGeoCode(com.apatar.strikeiron.ws.usaddress.USGeoCode geoCode) {
        this.geoCode = geoCode;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof USAddress)) return false;
        USAddress other = (USAddress) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.state==null && other.getState()==null) || 
             (this.state!=null &&
              this.state.equals(other.getState()))) &&
            ((this.urbanization==null && other.getUrbanization()==null) || 
             (this.urbanization!=null &&
              this.urbanization.equals(other.getUrbanization()))) &&
            ((this.zipPlus4==null && other.getZipPlus4()==null) || 
             (this.zipPlus4!=null &&
              this.zipPlus4.equals(other.getZipPlus4()))) &&
            ((this.zip==null && other.getZip()==null) || 
             (this.zip!=null &&
              this.zip.equals(other.getZip()))) &&
            ((this.zipAddOn==null && other.getZipAddOn()==null) || 
             (this.zipAddOn!=null &&
              this.zipAddOn.equals(other.getZipAddOn()))) &&
            ((this.carrierRoute==null && other.getCarrierRoute()==null) || 
             (this.carrierRoute!=null &&
              this.carrierRoute.equals(other.getCarrierRoute()))) &&
            ((this.PMB==null && other.getPMB()==null) || 
             (this.PMB!=null &&
              this.PMB.equals(other.getPMB()))) &&
            ((this.PMBDesignator==null && other.getPMBDesignator()==null) || 
             (this.PMBDesignator!=null &&
              this.PMBDesignator.equals(other.getPMBDesignator()))) &&
            ((this.deliveryPoint==null && other.getDeliveryPoint()==null) || 
             (this.deliveryPoint!=null &&
              this.deliveryPoint.equals(other.getDeliveryPoint()))) &&
            ((this.DPCheckDigit==null && other.getDPCheckDigit()==null) || 
             (this.DPCheckDigit!=null &&
              this.DPCheckDigit.equals(other.getDPCheckDigit()))) &&
            ((this.LACS==null && other.getLACS()==null) || 
             (this.LACS!=null &&
              this.LACS.equals(other.getLACS()))) &&
            ((this.CMRA==null && other.getCMRA()==null) || 
             (this.CMRA!=null &&
              this.CMRA.equals(other.getCMRA()))) &&
            ((this.DPV==null && other.getDPV()==null) || 
             (this.DPV!=null &&
              this.DPV.equals(other.getDPV()))) &&
            ((this.DPVFootnote==null && other.getDPVFootnote()==null) || 
             (this.DPVFootnote!=null &&
              this.DPVFootnote.equals(other.getDPVFootnote()))) &&
            ((this.RDI==null && other.getRDI()==null) || 
             (this.RDI!=null &&
              this.RDI.equals(other.getRDI()))) &&
            ((this.recordType==null && other.getRecordType()==null) || 
             (this.recordType!=null &&
              this.recordType.equals(other.getRecordType()))) &&
            ((this.congressDistrict==null && other.getCongressDistrict()==null) || 
             (this.congressDistrict!=null &&
              this.congressDistrict.equals(other.getCongressDistrict()))) &&
            ((this.county==null && other.getCounty()==null) || 
             (this.county!=null &&
              this.county.equals(other.getCounty()))) &&
            ((this.countyNumber==null && other.getCountyNumber()==null) || 
             (this.countyNumber!=null &&
              this.countyNumber.equals(other.getCountyNumber()))) &&
            ((this.stateNumber==null && other.getStateNumber()==null) || 
             (this.stateNumber!=null &&
              this.stateNumber.equals(other.getStateNumber()))) &&
            ((this.geoCode==null && other.getGeoCode()==null) || 
             (this.geoCode!=null &&
              this.geoCode.equals(other.getGeoCode())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getState() != null) {
            _hashCode += getState().hashCode();
        }
        if (getUrbanization() != null) {
            _hashCode += getUrbanization().hashCode();
        }
        if (getZipPlus4() != null) {
            _hashCode += getZipPlus4().hashCode();
        }
        if (getZip() != null) {
            _hashCode += getZip().hashCode();
        }
        if (getZipAddOn() != null) {
            _hashCode += getZipAddOn().hashCode();
        }
        if (getCarrierRoute() != null) {
            _hashCode += getCarrierRoute().hashCode();
        }
        if (getPMB() != null) {
            _hashCode += getPMB().hashCode();
        }
        if (getPMBDesignator() != null) {
            _hashCode += getPMBDesignator().hashCode();
        }
        if (getDeliveryPoint() != null) {
            _hashCode += getDeliveryPoint().hashCode();
        }
        if (getDPCheckDigit() != null) {
            _hashCode += getDPCheckDigit().hashCode();
        }
        if (getLACS() != null) {
            _hashCode += getLACS().hashCode();
        }
        if (getCMRA() != null) {
            _hashCode += getCMRA().hashCode();
        }
        if (getDPV() != null) {
            _hashCode += getDPV().hashCode();
        }
        if (getDPVFootnote() != null) {
            _hashCode += getDPVFootnote().hashCode();
        }
        if (getRDI() != null) {
            _hashCode += getRDI().hashCode();
        }
        if (getRecordType() != null) {
            _hashCode += getRecordType().hashCode();
        }
        if (getCongressDistrict() != null) {
            _hashCode += getCongressDistrict().hashCode();
        }
        if (getCounty() != null) {
            _hashCode += getCounty().hashCode();
        }
        if (getCountyNumber() != null) {
            _hashCode += getCountyNumber().hashCode();
        }
        if (getStateNumber() != null) {
            _hashCode += getStateNumber().hashCode();
        }
        if (getGeoCode() != null) {
            _hashCode += getGeoCode().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(USAddress.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.strikeiron.com", "USAddress"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("state");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "State"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("urbanization");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "Urbanization"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("zipPlus4");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "ZipPlus4"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("zip");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "Zip"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("zipAddOn");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "ZipAddOn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("carrierRoute");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "CarrierRoute"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("PMB");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "PMB"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("PMBDesignator");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "PMBDesignator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deliveryPoint");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "DeliveryPoint"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("DPCheckDigit");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "DPCheckDigit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("LACS");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "LACS"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CMRA");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "CMRA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("DPV");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "DPV"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("DPVFootnote");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "DPVFootnote"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("RDI");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "RDI"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recordType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "RecordType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("congressDistrict");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "CongressDistrict"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("county");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "County"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("countyNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "CountyNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stateNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "StateNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("geoCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "GeoCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.strikeiron.com", "USGeoCode"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
