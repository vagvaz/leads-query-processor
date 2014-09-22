/**
 * ConvertLead.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.apatar.salesforcecom.ws;

public class ConvertLead  implements java.io.Serializable {
    private com.apatar.salesforcecom.ws.LeadConvert[] leadConverts;

    public ConvertLead() {
    }

    public ConvertLead(
           com.apatar.salesforcecom.ws.LeadConvert[] leadConverts) {
           this.leadConverts = leadConverts;
    }


    /**
     * Gets the leadConverts value for this ConvertLead.
     * 
     * @return leadConverts
     */
    public com.apatar.salesforcecom.ws.LeadConvert[] getLeadConverts() {
        return leadConverts;
    }


    /**
     * Sets the leadConverts value for this ConvertLead.
     * 
     * @param leadConverts
     */
    public void setLeadConverts(com.apatar.salesforcecom.ws.LeadConvert[] leadConverts) {
        this.leadConverts = leadConverts;
    }

    public com.apatar.salesforcecom.ws.LeadConvert getLeadConverts(int i) {
        return this.leadConverts[i];
    }

    public void setLeadConverts(int i, com.apatar.salesforcecom.ws.LeadConvert _value) {
        this.leadConverts[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ConvertLead)) return false;
        ConvertLead other = (ConvertLead) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.leadConverts==null && other.getLeadConverts()==null) || 
             (this.leadConverts!=null &&
              java.util.Arrays.equals(this.leadConverts, other.getLeadConverts())));
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
        if (getLeadConverts() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getLeadConverts());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getLeadConverts(), i);
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
        new org.apache.axis.description.TypeDesc(ConvertLead.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:partner.soap.sforce.com", ">convertLead"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("leadConverts");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:partner.soap.sforce.com", "leadConverts"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:partner.soap.sforce.com", "LeadConvert"));
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
