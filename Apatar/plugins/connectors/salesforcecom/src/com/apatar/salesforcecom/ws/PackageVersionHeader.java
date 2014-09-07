/**
 * PackageVersionHeader.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.apatar.salesforcecom.ws;

public class PackageVersionHeader  implements java.io.Serializable {
    private com.apatar.salesforcecom.ws.PackageVersion[] packageVersions;

    public PackageVersionHeader() {
    }

    public PackageVersionHeader(
           com.apatar.salesforcecom.ws.PackageVersion[] packageVersions) {
           this.packageVersions = packageVersions;
    }


    /**
     * Gets the packageVersions value for this PackageVersionHeader.
     * 
     * @return packageVersions
     */
    public com.apatar.salesforcecom.ws.PackageVersion[] getPackageVersions() {
        return packageVersions;
    }


    /**
     * Sets the packageVersions value for this PackageVersionHeader.
     * 
     * @param packageVersions
     */
    public void setPackageVersions(com.apatar.salesforcecom.ws.PackageVersion[] packageVersions) {
        this.packageVersions = packageVersions;
    }

    public com.apatar.salesforcecom.ws.PackageVersion getPackageVersions(int i) {
        return this.packageVersions[i];
    }

    public void setPackageVersions(int i, com.apatar.salesforcecom.ws.PackageVersion _value) {
        this.packageVersions[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PackageVersionHeader)) return false;
        PackageVersionHeader other = (PackageVersionHeader) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.packageVersions==null && other.getPackageVersions()==null) || 
             (this.packageVersions!=null &&
              java.util.Arrays.equals(this.packageVersions, other.getPackageVersions())));
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
        if (getPackageVersions() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPackageVersions());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPackageVersions(), i);
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
        new org.apache.axis.description.TypeDesc(PackageVersionHeader.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:partner.soap.sforce.com", ">PackageVersionHeader"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("packageVersions");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:partner.soap.sforce.com", "packageVersions"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:partner.soap.sforce.com", "PackageVersion"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
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
