/**
 * ValidateEmailResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.strikeiron.ws.login;

public class ValidateEmailResult  implements java.io.Serializable {
    private java.lang.String isValid;
    private java.lang.String smtpResponse;

    public ValidateEmailResult() {
    }

    public ValidateEmailResult(
           java.lang.String isValid,
           java.lang.String smtpResponse) {
           this.isValid = isValid;
           this.smtpResponse = smtpResponse;
    }


    /**
     * Gets the isValid value for this ValidateEmailResult.
     * 
     * @return isValid
     */
    public java.lang.String getIsValid() {
        return isValid;
    }


    /**
     * Sets the isValid value for this ValidateEmailResult.
     * 
     * @param isValid
     */
    public void setIsValid(java.lang.String isValid) {
        this.isValid = isValid;
    }


    /**
     * Gets the smtpResponse value for this ValidateEmailResult.
     * 
     * @return smtpResponse
     */
    public java.lang.String getSmtpResponse() {
        return smtpResponse;
    }


    /**
     * Sets the smtpResponse value for this ValidateEmailResult.
     * 
     * @param smtpResponse
     */
    public void setSmtpResponse(java.lang.String smtpResponse) {
        this.smtpResponse = smtpResponse;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ValidateEmailResult)) return false;
        ValidateEmailResult other = (ValidateEmailResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.isValid==null && other.getIsValid()==null) || 
             (this.isValid!=null &&
              this.isValid.equals(other.getIsValid()))) &&
            ((this.smtpResponse==null && other.getSmtpResponse()==null) || 
             (this.smtpResponse!=null &&
              this.smtpResponse.equals(other.getSmtpResponse())));
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
        if (getIsValid() != null) {
            _hashCode += getIsValid().hashCode();
        }
        if (getSmtpResponse() != null) {
            _hashCode += getSmtpResponse().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ValidateEmailResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.strikeiron.com", "ValidateEmailResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isValid");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "IsValid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("smtpResponse");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "SmtpResponse"));
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
