/**
 * USBatchAddress.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.strikeiron.ws.usaddress;

public class USBatchAddress  implements java.io.Serializable {
    private java.lang.String addressLine1;
    private java.lang.String addressLine2;
    private java.lang.String city_state_zip;
    private java.lang.String firm;
    private java.lang.String urbanization;

    public USBatchAddress() {
    }

    public USBatchAddress(
           java.lang.String addressLine1,
           java.lang.String addressLine2,
           java.lang.String city_state_zip,
           java.lang.String firm,
           java.lang.String urbanization) {
           this.addressLine1 = addressLine1;
           this.addressLine2 = addressLine2;
           this.city_state_zip = city_state_zip;
           this.firm = firm;
           this.urbanization = urbanization;
    }


    /**
     * Gets the addressLine1 value for this USBatchAddress.
     * 
     * @return addressLine1
     */
    public java.lang.String getAddressLine1() {
        return addressLine1;
    }


    /**
     * Sets the addressLine1 value for this USBatchAddress.
     * 
     * @param addressLine1
     */
    public void setAddressLine1(java.lang.String addressLine1) {
        this.addressLine1 = addressLine1;
    }


    /**
     * Gets the addressLine2 value for this USBatchAddress.
     * 
     * @return addressLine2
     */
    public java.lang.String getAddressLine2() {
        return addressLine2;
    }


    /**
     * Sets the addressLine2 value for this USBatchAddress.
     * 
     * @param addressLine2
     */
    public void setAddressLine2(java.lang.String addressLine2) {
        this.addressLine2 = addressLine2;
    }


    /**
     * Gets the city_state_zip value for this USBatchAddress.
     * 
     * @return city_state_zip
     */
    public java.lang.String getCity_state_zip() {
        return city_state_zip;
    }


    /**
     * Sets the city_state_zip value for this USBatchAddress.
     * 
     * @param city_state_zip
     */
    public void setCity_state_zip(java.lang.String city_state_zip) {
        this.city_state_zip = city_state_zip;
    }


    /**
     * Gets the firm value for this USBatchAddress.
     * 
     * @return firm
     */
    public java.lang.String getFirm() {
        return firm;
    }


    /**
     * Sets the firm value for this USBatchAddress.
     * 
     * @param firm
     */
    public void setFirm(java.lang.String firm) {
        this.firm = firm;
    }


    /**
     * Gets the urbanization value for this USBatchAddress.
     * 
     * @return urbanization
     */
    public java.lang.String getUrbanization() {
        return urbanization;
    }


    /**
     * Sets the urbanization value for this USBatchAddress.
     * 
     * @param urbanization
     */
    public void setUrbanization(java.lang.String urbanization) {
        this.urbanization = urbanization;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof USBatchAddress)) return false;
        USBatchAddress other = (USBatchAddress) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.addressLine1==null && other.getAddressLine1()==null) || 
             (this.addressLine1!=null &&
              this.addressLine1.equals(other.getAddressLine1()))) &&
            ((this.addressLine2==null && other.getAddressLine2()==null) || 
             (this.addressLine2!=null &&
              this.addressLine2.equals(other.getAddressLine2()))) &&
            ((this.city_state_zip==null && other.getCity_state_zip()==null) || 
             (this.city_state_zip!=null &&
              this.city_state_zip.equals(other.getCity_state_zip()))) &&
            ((this.firm==null && other.getFirm()==null) || 
             (this.firm!=null &&
              this.firm.equals(other.getFirm()))) &&
            ((this.urbanization==null && other.getUrbanization()==null) || 
             (this.urbanization!=null &&
              this.urbanization.equals(other.getUrbanization())));
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
        if (getAddressLine1() != null) {
            _hashCode += getAddressLine1().hashCode();
        }
        if (getAddressLine2() != null) {
            _hashCode += getAddressLine2().hashCode();
        }
        if (getCity_state_zip() != null) {
            _hashCode += getCity_state_zip().hashCode();
        }
        if (getFirm() != null) {
            _hashCode += getFirm().hashCode();
        }
        if (getUrbanization() != null) {
            _hashCode += getUrbanization().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(USBatchAddress.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.strikeiron.com", "USBatchAddress"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("addressLine1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "addressLine1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("addressLine2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "addressLine2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("city_state_zip");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "city_state_zip"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firm");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "firm"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("urbanization");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "urbanization"));
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
