/**
 * IncomeAndHouseValue.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.cdyne.ws.demographix;

public class IncomeAndHouseValue  implements java.io.Serializable {
    private com.apatar.cdyne.ws.demographix.PlaceInformationCls placeInformation;
    private boolean error;
    private java.lang.String errorString;
    private int medianIncome;
    private int medianHouseValue;

    public IncomeAndHouseValue() {
    }

    public IncomeAndHouseValue(
           com.apatar.cdyne.ws.demographix.PlaceInformationCls placeInformation,
           boolean error,
           java.lang.String errorString,
           int medianIncome,
           int medianHouseValue) {
           this.placeInformation = placeInformation;
           this.error = error;
           this.errorString = errorString;
           this.medianIncome = medianIncome;
           this.medianHouseValue = medianHouseValue;
    }


    /**
     * Gets the placeInformation value for this IncomeAndHouseValue.
     * 
     * @return placeInformation
     */
    public com.apatar.cdyne.ws.demographix.PlaceInformationCls getPlaceInformation() {
        return placeInformation;
    }


    /**
     * Sets the placeInformation value for this IncomeAndHouseValue.
     * 
     * @param placeInformation
     */
    public void setPlaceInformation(com.apatar.cdyne.ws.demographix.PlaceInformationCls placeInformation) {
        this.placeInformation = placeInformation;
    }


    /**
     * Gets the error value for this IncomeAndHouseValue.
     * 
     * @return error
     */
    public boolean isError() {
        return error;
    }


    /**
     * Sets the error value for this IncomeAndHouseValue.
     * 
     * @param error
     */
    public void setError(boolean error) {
        this.error = error;
    }


    /**
     * Gets the errorString value for this IncomeAndHouseValue.
     * 
     * @return errorString
     */
    public java.lang.String getErrorString() {
        return errorString;
    }


    /**
     * Sets the errorString value for this IncomeAndHouseValue.
     * 
     * @param errorString
     */
    public void setErrorString(java.lang.String errorString) {
        this.errorString = errorString;
    }


    /**
     * Gets the medianIncome value for this IncomeAndHouseValue.
     * 
     * @return medianIncome
     */
    public int getMedianIncome() {
        return medianIncome;
    }


    /**
     * Sets the medianIncome value for this IncomeAndHouseValue.
     * 
     * @param medianIncome
     */
    public void setMedianIncome(int medianIncome) {
        this.medianIncome = medianIncome;
    }


    /**
     * Gets the medianHouseValue value for this IncomeAndHouseValue.
     * 
     * @return medianHouseValue
     */
    public int getMedianHouseValue() {
        return medianHouseValue;
    }


    /**
     * Sets the medianHouseValue value for this IncomeAndHouseValue.
     * 
     * @param medianHouseValue
     */
    public void setMedianHouseValue(int medianHouseValue) {
        this.medianHouseValue = medianHouseValue;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof IncomeAndHouseValue)) return false;
        IncomeAndHouseValue other = (IncomeAndHouseValue) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.placeInformation==null && other.getPlaceInformation()==null) || 
             (this.placeInformation!=null &&
              this.placeInformation.equals(other.getPlaceInformation()))) &&
            this.error == other.isError() &&
            ((this.errorString==null && other.getErrorString()==null) || 
             (this.errorString!=null &&
              this.errorString.equals(other.getErrorString()))) &&
            this.medianIncome == other.getMedianIncome() &&
            this.medianHouseValue == other.getMedianHouseValue();
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
        if (getPlaceInformation() != null) {
            _hashCode += getPlaceInformation().hashCode();
        }
        _hashCode += (isError() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getErrorString() != null) {
            _hashCode += getErrorString().hashCode();
        }
        _hashCode += getMedianIncome();
        _hashCode += getMedianHouseValue();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(IncomeAndHouseValue.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "IncomeAndHouseValue"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("placeInformation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "PlaceInformation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "PlaceInformationCls"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
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
        elemField.setFieldName("medianIncome");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "MedianIncome"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("medianHouseValue");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "MedianHouseValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
