/**
 * GenderPercentagesCls.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.cdyne.ws.demographix;

public class GenderPercentagesCls  implements java.io.Serializable {
    private java.math.BigDecimal female;
    private java.math.BigDecimal male;

    public GenderPercentagesCls() {
    }

    public GenderPercentagesCls(
           java.math.BigDecimal female,
           java.math.BigDecimal male) {
           this.female = female;
           this.male = male;
    }


    /**
     * Gets the female value for this GenderPercentagesCls.
     * 
     * @return female
     */
    public java.math.BigDecimal getFemale() {
        return female;
    }


    /**
     * Sets the female value for this GenderPercentagesCls.
     * 
     * @param female
     */
    public void setFemale(java.math.BigDecimal female) {
        this.female = female;
    }


    /**
     * Gets the male value for this GenderPercentagesCls.
     * 
     * @return male
     */
    public java.math.BigDecimal getMale() {
        return male;
    }


    /**
     * Sets the male value for this GenderPercentagesCls.
     * 
     * @param male
     */
    public void setMale(java.math.BigDecimal male) {
        this.male = male;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GenderPercentagesCls)) return false;
        GenderPercentagesCls other = (GenderPercentagesCls) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.female==null && other.getFemale()==null) || 
             (this.female!=null &&
              this.female.equals(other.getFemale()))) &&
            ((this.male==null && other.getMale()==null) || 
             (this.male!=null &&
              this.male.equals(other.getMale())));
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
        if (getFemale() != null) {
            _hashCode += getFemale().hashCode();
        }
        if (getMale() != null) {
            _hashCode += getMale().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GenderPercentagesCls.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "GenderPercentagesCls"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("female");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "Female"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("male");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "Male"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
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
