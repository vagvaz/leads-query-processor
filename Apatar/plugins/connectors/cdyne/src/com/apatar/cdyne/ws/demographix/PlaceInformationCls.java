/**
 * PlaceInformationCls.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.cdyne.ws.demographix;

public class PlaceInformationCls  implements java.io.Serializable {
    private java.lang.String stateAbbrev;
    private java.lang.String placeID;
    private boolean rural;

    public PlaceInformationCls() {
    }

    public PlaceInformationCls(
           java.lang.String stateAbbrev,
           java.lang.String placeID,
           boolean rural) {
           this.stateAbbrev = stateAbbrev;
           this.placeID = placeID;
           this.rural = rural;
    }


    /**
     * Gets the stateAbbrev value for this PlaceInformationCls.
     * 
     * @return stateAbbrev
     */
    public java.lang.String getStateAbbrev() {
        return stateAbbrev;
    }


    /**
     * Sets the stateAbbrev value for this PlaceInformationCls.
     * 
     * @param stateAbbrev
     */
    public void setStateAbbrev(java.lang.String stateAbbrev) {
        this.stateAbbrev = stateAbbrev;
    }


    /**
     * Gets the placeID value for this PlaceInformationCls.
     * 
     * @return placeID
     */
    public java.lang.String getPlaceID() {
        return placeID;
    }


    /**
     * Sets the placeID value for this PlaceInformationCls.
     * 
     * @param placeID
     */
    public void setPlaceID(java.lang.String placeID) {
        this.placeID = placeID;
    }


    /**
     * Gets the rural value for this PlaceInformationCls.
     * 
     * @return rural
     */
    public boolean isRural() {
        return rural;
    }


    /**
     * Sets the rural value for this PlaceInformationCls.
     * 
     * @param rural
     */
    public void setRural(boolean rural) {
        this.rural = rural;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PlaceInformationCls)) return false;
        PlaceInformationCls other = (PlaceInformationCls) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.stateAbbrev==null && other.getStateAbbrev()==null) || 
             (this.stateAbbrev!=null &&
              this.stateAbbrev.equals(other.getStateAbbrev()))) &&
            ((this.placeID==null && other.getPlaceID()==null) || 
             (this.placeID!=null &&
              this.placeID.equals(other.getPlaceID()))) &&
            this.rural == other.isRural();
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
        if (getStateAbbrev() != null) {
            _hashCode += getStateAbbrev().hashCode();
        }
        if (getPlaceID() != null) {
            _hashCode += getPlaceID().hashCode();
        }
        _hashCode += (isRural() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PlaceInformationCls.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "PlaceInformationCls"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stateAbbrev");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "StateAbbrev"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("placeID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "PlaceID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rural");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "Rural"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
