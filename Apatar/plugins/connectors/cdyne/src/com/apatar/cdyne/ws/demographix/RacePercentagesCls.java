/**
 * RacePercentagesCls.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.cdyne.ws.demographix;

public class RacePercentagesCls  implements java.io.Serializable {
    private java.math.BigDecimal asian;
    private java.math.BigDecimal black;
    private java.math.BigDecimal indian;
    private java.math.BigDecimal mixed;
    private java.math.BigDecimal nativeHawaiian;
    private java.math.BigDecimal other;
    private java.math.BigDecimal white;

    public RacePercentagesCls() {
    }

    public RacePercentagesCls(
           java.math.BigDecimal asian,
           java.math.BigDecimal black,
           java.math.BigDecimal indian,
           java.math.BigDecimal mixed,
           java.math.BigDecimal nativeHawaiian,
           java.math.BigDecimal other,
           java.math.BigDecimal white) {
           this.asian = asian;
           this.black = black;
           this.indian = indian;
           this.mixed = mixed;
           this.nativeHawaiian = nativeHawaiian;
           this.other = other;
           this.white = white;
    }


    /**
     * Gets the asian value for this RacePercentagesCls.
     * 
     * @return asian
     */
    public java.math.BigDecimal getAsian() {
        return asian;
    }


    /**
     * Sets the asian value for this RacePercentagesCls.
     * 
     * @param asian
     */
    public void setAsian(java.math.BigDecimal asian) {
        this.asian = asian;
    }


    /**
     * Gets the black value for this RacePercentagesCls.
     * 
     * @return black
     */
    public java.math.BigDecimal getBlack() {
        return black;
    }


    /**
     * Sets the black value for this RacePercentagesCls.
     * 
     * @param black
     */
    public void setBlack(java.math.BigDecimal black) {
        this.black = black;
    }


    /**
     * Gets the indian value for this RacePercentagesCls.
     * 
     * @return indian
     */
    public java.math.BigDecimal getIndian() {
        return indian;
    }


    /**
     * Sets the indian value for this RacePercentagesCls.
     * 
     * @param indian
     */
    public void setIndian(java.math.BigDecimal indian) {
        this.indian = indian;
    }


    /**
     * Gets the mixed value for this RacePercentagesCls.
     * 
     * @return mixed
     */
    public java.math.BigDecimal getMixed() {
        return mixed;
    }


    /**
     * Sets the mixed value for this RacePercentagesCls.
     * 
     * @param mixed
     */
    public void setMixed(java.math.BigDecimal mixed) {
        this.mixed = mixed;
    }


    /**
     * Gets the nativeHawaiian value for this RacePercentagesCls.
     * 
     * @return nativeHawaiian
     */
    public java.math.BigDecimal getNativeHawaiian() {
        return nativeHawaiian;
    }


    /**
     * Sets the nativeHawaiian value for this RacePercentagesCls.
     * 
     * @param nativeHawaiian
     */
    public void setNativeHawaiian(java.math.BigDecimal nativeHawaiian) {
        this.nativeHawaiian = nativeHawaiian;
    }


    /**
     * Gets the other value for this RacePercentagesCls.
     * 
     * @return other
     */
    public java.math.BigDecimal getOther() {
        return other;
    }


    /**
     * Sets the other value for this RacePercentagesCls.
     * 
     * @param other
     */
    public void setOther(java.math.BigDecimal other) {
        this.other = other;
    }


    /**
     * Gets the white value for this RacePercentagesCls.
     * 
     * @return white
     */
    public java.math.BigDecimal getWhite() {
        return white;
    }


    /**
     * Sets the white value for this RacePercentagesCls.
     * 
     * @param white
     */
    public void setWhite(java.math.BigDecimal white) {
        this.white = white;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RacePercentagesCls)) return false;
        RacePercentagesCls other = (RacePercentagesCls) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.asian==null && other.getAsian()==null) || 
             (this.asian!=null &&
              this.asian.equals(other.getAsian()))) &&
            ((this.black==null && other.getBlack()==null) || 
             (this.black!=null &&
              this.black.equals(other.getBlack()))) &&
            ((this.indian==null && other.getIndian()==null) || 
             (this.indian!=null &&
              this.indian.equals(other.getIndian()))) &&
            ((this.mixed==null && other.getMixed()==null) || 
             (this.mixed!=null &&
              this.mixed.equals(other.getMixed()))) &&
            ((this.nativeHawaiian==null && other.getNativeHawaiian()==null) || 
             (this.nativeHawaiian!=null &&
              this.nativeHawaiian.equals(other.getNativeHawaiian()))) &&
            ((this.other==null && other.getOther()==null) || 
             (this.other!=null &&
              this.other.equals(other.getOther()))) &&
            ((this.white==null && other.getWhite()==null) || 
             (this.white!=null &&
              this.white.equals(other.getWhite())));
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
        if (getAsian() != null) {
            _hashCode += getAsian().hashCode();
        }
        if (getBlack() != null) {
            _hashCode += getBlack().hashCode();
        }
        if (getIndian() != null) {
            _hashCode += getIndian().hashCode();
        }
        if (getMixed() != null) {
            _hashCode += getMixed().hashCode();
        }
        if (getNativeHawaiian() != null) {
            _hashCode += getNativeHawaiian().hashCode();
        }
        if (getOther() != null) {
            _hashCode += getOther().hashCode();
        }
        if (getWhite() != null) {
            _hashCode += getWhite().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RacePercentagesCls.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "RacePercentagesCls"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("asian");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "Asian"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("black");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "Black"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("indian");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "Indian"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mixed");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "Mixed"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nativeHawaiian");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "NativeHawaiian"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("other");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "Other"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("white");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ws.cdyne.com/DemographixWS", "White"));
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
