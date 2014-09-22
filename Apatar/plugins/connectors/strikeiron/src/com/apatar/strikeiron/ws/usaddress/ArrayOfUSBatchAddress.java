/**
 * ArrayOfUSBatchAddress.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.strikeiron.ws.usaddress;

public class ArrayOfUSBatchAddress  implements java.io.Serializable {
    private com.apatar.strikeiron.ws.usaddress.USBatchAddress[] USBatchAddress;

    public ArrayOfUSBatchAddress() {
    }

    public ArrayOfUSBatchAddress(
           com.apatar.strikeiron.ws.usaddress.USBatchAddress[] USBatchAddress) {
           this.USBatchAddress = USBatchAddress;
    }


    /**
     * Gets the USBatchAddress value for this ArrayOfUSBatchAddress.
     * 
     * @return USBatchAddress
     */
    public com.apatar.strikeiron.ws.usaddress.USBatchAddress[] getUSBatchAddress() {
        return USBatchAddress;
    }


    /**
     * Sets the USBatchAddress value for this ArrayOfUSBatchAddress.
     * 
     * @param USBatchAddress
     */
    public void setUSBatchAddress(com.apatar.strikeiron.ws.usaddress.USBatchAddress[] USBatchAddress) {
        this.USBatchAddress = USBatchAddress;
    }

    public com.apatar.strikeiron.ws.usaddress.USBatchAddress getUSBatchAddress(int i) {
        return this.USBatchAddress[i];
    }

    public void setUSBatchAddress(int i, com.apatar.strikeiron.ws.usaddress.USBatchAddress _value) {
        this.USBatchAddress[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfUSBatchAddress)) return false;
        ArrayOfUSBatchAddress other = (ArrayOfUSBatchAddress) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.USBatchAddress==null && other.getUSBatchAddress()==null) || 
             (this.USBatchAddress!=null &&
              java.util.Arrays.equals(this.USBatchAddress, other.getUSBatchAddress())));
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
        if (getUSBatchAddress() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getUSBatchAddress());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getUSBatchAddress(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfUSBatchAddress.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.strikeiron.com", "ArrayOfUSBatchAddress"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("USBatchAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "USBatchAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.strikeiron.com", "USBatchAddress"));
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
