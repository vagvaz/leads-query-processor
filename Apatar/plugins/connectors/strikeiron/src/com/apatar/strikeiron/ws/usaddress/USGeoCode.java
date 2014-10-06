/**
 * USGeoCode.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.strikeiron.ws.usaddress;

public class USGeoCode  extends com.apatar.strikeiron.ws.usaddress.GeoCode  implements java.io.Serializable {
    private java.lang.String censusTract;
    private java.lang.String stateNumber;
    private java.lang.String countyNumber;
    private java.lang.String blockNumber;
    private java.lang.String blockGroup;

    public USGeoCode() {
    }

    public USGeoCode(
           java.lang.String censusTract,
           java.lang.String stateNumber,
           java.lang.String countyNumber,
           java.lang.String blockNumber,
           java.lang.String blockGroup) {
           this.censusTract = censusTract;
           this.stateNumber = stateNumber;
           this.countyNumber = countyNumber;
           this.blockNumber = blockNumber;
           this.blockGroup = blockGroup;
    }


    /**
     * Gets the censusTract value for this USGeoCode.
     * 
     * @return censusTract
     */
    public java.lang.String getCensusTract() {
        return censusTract;
    }


    /**
     * Sets the censusTract value for this USGeoCode.
     * 
     * @param censusTract
     */
    public void setCensusTract(java.lang.String censusTract) {
        this.censusTract = censusTract;
    }


    /**
     * Gets the stateNumber value for this USGeoCode.
     * 
     * @return stateNumber
     */
    public java.lang.String getStateNumber() {
        return stateNumber;
    }


    /**
     * Sets the stateNumber value for this USGeoCode.
     * 
     * @param stateNumber
     */
    public void setStateNumber(java.lang.String stateNumber) {
        this.stateNumber = stateNumber;
    }


    /**
     * Gets the countyNumber value for this USGeoCode.
     * 
     * @return countyNumber
     */
    public java.lang.String getCountyNumber() {
        return countyNumber;
    }


    /**
     * Sets the countyNumber value for this USGeoCode.
     * 
     * @param countyNumber
     */
    public void setCountyNumber(java.lang.String countyNumber) {
        this.countyNumber = countyNumber;
    }


    /**
     * Gets the blockNumber value for this USGeoCode.
     * 
     * @return blockNumber
     */
    public java.lang.String getBlockNumber() {
        return blockNumber;
    }


    /**
     * Sets the blockNumber value for this USGeoCode.
     * 
     * @param blockNumber
     */
    public void setBlockNumber(java.lang.String blockNumber) {
        this.blockNumber = blockNumber;
    }


    /**
     * Gets the blockGroup value for this USGeoCode.
     * 
     * @return blockGroup
     */
    public java.lang.String getBlockGroup() {
        return blockGroup;
    }


    /**
     * Sets the blockGroup value for this USGeoCode.
     * 
     * @param blockGroup
     */
    public void setBlockGroup(java.lang.String blockGroup) {
        this.blockGroup = blockGroup;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof USGeoCode)) return false;
        USGeoCode other = (USGeoCode) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.censusTract==null && other.getCensusTract()==null) || 
             (this.censusTract!=null &&
              this.censusTract.equals(other.getCensusTract()))) &&
            ((this.stateNumber==null && other.getStateNumber()==null) || 
             (this.stateNumber!=null &&
              this.stateNumber.equals(other.getStateNumber()))) &&
            ((this.countyNumber==null && other.getCountyNumber()==null) || 
             (this.countyNumber!=null &&
              this.countyNumber.equals(other.getCountyNumber()))) &&
            ((this.blockNumber==null && other.getBlockNumber()==null) || 
             (this.blockNumber!=null &&
              this.blockNumber.equals(other.getBlockNumber()))) &&
            ((this.blockGroup==null && other.getBlockGroup()==null) || 
             (this.blockGroup!=null &&
              this.blockGroup.equals(other.getBlockGroup())));
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
        if (getCensusTract() != null) {
            _hashCode += getCensusTract().hashCode();
        }
        if (getStateNumber() != null) {
            _hashCode += getStateNumber().hashCode();
        }
        if (getCountyNumber() != null) {
            _hashCode += getCountyNumber().hashCode();
        }
        if (getBlockNumber() != null) {
            _hashCode += getBlockNumber().hashCode();
        }
        if (getBlockGroup() != null) {
            _hashCode += getBlockGroup().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(USGeoCode.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.strikeiron.com", "USGeoCode"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("censusTract");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "CensusTract"));
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
        elemField.setFieldName("countyNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "CountyNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("blockNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "BlockNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("blockGroup");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "BlockGroup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
