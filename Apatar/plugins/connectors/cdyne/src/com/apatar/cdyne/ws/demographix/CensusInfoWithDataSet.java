/**
 * CensusInfoWithDataSet.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.cdyne.ws.demographix;

public class CensusInfoWithDataSet  implements java.io.Serializable {
    private boolean error;
    private java.lang.String errorString;
    private com.apatar.cdyne.ws.demographix.CensusInfoWithDataSetCensusDataSet censusDataSet;

    public CensusInfoWithDataSet() {
    }

    public CensusInfoWithDataSet(
           boolean error,
           java.lang.String errorString,
           com.apatar.cdyne.ws.demographix.CensusInfoWithDataSetCensusDataSet censusDataSet) {
           this.error = error;
           this.errorString = errorString;
           this.censusDataSet = censusDataSet;
    }


    /**
     * Gets the error value for this CensusInfoWithDataSet.
     * 
     * @return error
     */
    public boolean isError() {
        return error;
    }


    /**
     * Sets the error value for this CensusInfoWithDataSet.
     * 
     * @param error
     */
    public void setError(boolean error) {
        this.error = error;
    }


    /**
     * Gets the errorString value for this CensusInfoWithDataSet.
     * 
     * @return errorString
     */
    public java.lang.String getErrorString() {
        return errorString;
    }


    /**
     * Sets the errorString value for this CensusInfoWithDataSet.
     * 
     * @param errorString
     */
    public void setErrorString(java.lang.String errorString) {
        this.errorString = errorString;
    }


    /**
     * Gets the censusDataSet value for this CensusInfoWithDataSet.
     * 
     * @return censusDataSet
     */
    public com.apatar.cdyne.ws.demographix.CensusInfoWithDataSetCensusDataSet getCensusDataSet() {
        return censusDataSet;
    }


    /**
     * Sets the censusDataSet value for this CensusInfoWithDataSet.
     * 
     * @param censusDataSet
     */
    public void setCensusDataSet(com.apatar.cdyne.ws.demographix.CensusInfoWithDataSetCensusDataSet censusDataSet) {
        this.censusDataSet = censusDataSet;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CensusInfoWithDataSet)) return false;
        CensusInfoWithDataSet other = (CensusInfoWithDataSet) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.error == other.isError() &&
            ((this.errorString==null && other.getErrorString()==null) || 
             (this.errorString!=null &&
              this.errorString.equals(other.getErrorString()))) &&
            ((this.censusDataSet==null && other.getCensusDataSet()==null) || 
             (this.censusDataSet!=null &&
              this.censusDataSet.equals(other.getCensusDataSet())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += (isError() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getErrorString() != null) {
            _hashCode += getErrorString().hashCode();
        }
        if (getCensusDataSet() != null) {
            _hashCode += getCensusDataSet().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CensusInfoWithDataSet.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "CensusInfoWithDataSet"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("error");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "Error"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorString");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "ErrorString"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("censusDataSet");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "CensusDataSet"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", ">CensusInfoWithDataSet>CensusDataSet"));
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
