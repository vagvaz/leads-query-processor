/**
 * ArrayOfUSAddress.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.strikeiron.ws.usaddress;

public class ArrayOfUSAddress  implements java.io.Serializable {
    private com.apatar.strikeiron.ws.usaddress.USAddress[] USAddress;

    public ArrayOfUSAddress() {
    }

    public ArrayOfUSAddress(
           com.apatar.strikeiron.ws.usaddress.USAddress[] USAddress) {
           this.USAddress = USAddress;
    }


    /**
     * Gets the USAddress value for this ArrayOfUSAddress.
     * 
     * @return USAddress
     */
    public com.apatar.strikeiron.ws.usaddress.USAddress[] getUSAddress() {
        return USAddress;
    }


    /**
     * Sets the USAddress value for this ArrayOfUSAddress.
     * 
     * @param USAddress
     */
    public void setUSAddress(com.apatar.strikeiron.ws.usaddress.USAddress[] USAddress) {
        this.USAddress = USAddress;
    }

    public com.apatar.strikeiron.ws.usaddress.USAddress getUSAddress(int i) {
        return this.USAddress[i];
    }

    public void setUSAddress(int i, com.apatar.strikeiron.ws.usaddress.USAddress _value) {
        this.USAddress[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfUSAddress)) return false;
        ArrayOfUSAddress other = (ArrayOfUSAddress) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.USAddress==null && other.getUSAddress()==null) || 
             (this.USAddress!=null &&
              java.util.Arrays.equals(this.USAddress, other.getUSAddress())));
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
        if (getUSAddress() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getUSAddress());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getUSAddress(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ArrayOfUSAddress.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.strikeiron.com", "ArrayOfUSAddress"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("USAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "USAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.strikeiron.com", "USAddress"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
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
