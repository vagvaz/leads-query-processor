/**
 * WorkbookQuery.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.editgrid.ws;

public class WorkbookQuery implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -3335319274473731109L;
	private java.lang.String	id;
	private java.lang.String	name;
	private java.lang.String	isTemplate;
	private java.lang.String	timeZone;
	private java.lang.String	note;
	private java.lang.String	createUserId;
	private java.lang.String	createTime;
	private java.lang.String	lastModifiedUserId;
	private java.lang.String	lastModifiedTime;

	public WorkbookQuery() {
	}

	public WorkbookQuery(java.lang.String id, java.lang.String name,
			java.lang.String isTemplate, java.lang.String timeZone,
			java.lang.String note, java.lang.String createUserId,
			java.lang.String createTime, java.lang.String lastModifiedUserId,
			java.lang.String lastModifiedTime) {
		this.id = id;
		this.name = name;
		this.isTemplate = isTemplate;
		this.timeZone = timeZone;
		this.note = note;
		this.createUserId = createUserId;
		this.createTime = createTime;
		this.lastModifiedUserId = lastModifiedUserId;
		this.lastModifiedTime = lastModifiedTime;
	}

	/**
	 * Gets the id value for this WorkbookQuery.
	 * 
	 * @return id
	 */
	public java.lang.String getId() {
		return id;
	}

	/**
	 * Sets the id value for this WorkbookQuery.
	 * 
	 * @param id
	 */
	public void setId(java.lang.String id) {
		this.id = id;
	}

	/**
	 * Gets the name value for this WorkbookQuery.
	 * 
	 * @return name
	 */
	public java.lang.String getName() {
		return name;
	}

	/**
	 * Sets the name value for this WorkbookQuery.
	 * 
	 * @param name
	 */
	public void setName(java.lang.String name) {
		this.name = name;
	}

	/**
	 * Gets the isTemplate value for this WorkbookQuery.
	 * 
	 * @return isTemplate
	 */
	public java.lang.String getIsTemplate() {
		return isTemplate;
	}

	/**
	 * Sets the isTemplate value for this WorkbookQuery.
	 * 
	 * @param isTemplate
	 */
	public void setIsTemplate(java.lang.String isTemplate) {
		this.isTemplate = isTemplate;
	}

	/**
	 * Gets the timeZone value for this WorkbookQuery.
	 * 
	 * @return timeZone
	 */
	public java.lang.String getTimeZone() {
		return timeZone;
	}

	/**
	 * Sets the timeZone value for this WorkbookQuery.
	 * 
	 * @param timeZone
	 */
	public void setTimeZone(java.lang.String timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * Gets the note value for this WorkbookQuery.
	 * 
	 * @return note
	 */
	public java.lang.String getNote() {
		return note;
	}

	/**
	 * Sets the note value for this WorkbookQuery.
	 * 
	 * @param note
	 */
	public void setNote(java.lang.String note) {
		this.note = note;
	}

	/**
	 * Gets the createUserId value for this WorkbookQuery.
	 * 
	 * @return createUserId
	 */
	public java.lang.String getCreateUserId() {
		return createUserId;
	}

	/**
	 * Sets the createUserId value for this WorkbookQuery.
	 * 
	 * @param createUserId
	 */
	public void setCreateUserId(java.lang.String createUserId) {
		this.createUserId = createUserId;
	}

	/**
	 * Gets the createTime value for this WorkbookQuery.
	 * 
	 * @return createTime
	 */
	public java.lang.String getCreateTime() {
		return createTime;
	}

	/**
	 * Sets the createTime value for this WorkbookQuery.
	 * 
	 * @param createTime
	 */
	public void setCreateTime(java.lang.String createTime) {
		this.createTime = createTime;
	}

	/**
	 * Gets the lastModifiedUserId value for this WorkbookQuery.
	 * 
	 * @return lastModifiedUserId
	 */
	public java.lang.String getLastModifiedUserId() {
		return lastModifiedUserId;
	}

	/**
	 * Sets the lastModifiedUserId value for this WorkbookQuery.
	 * 
	 * @param lastModifiedUserId
	 */
	public void setLastModifiedUserId(java.lang.String lastModifiedUserId) {
		this.lastModifiedUserId = lastModifiedUserId;
	}

	/**
	 * Gets the lastModifiedTime value for this WorkbookQuery.
	 * 
	 * @return lastModifiedTime
	 */
	public java.lang.String getLastModifiedTime() {
		return lastModifiedTime;
	}

	/**
	 * Sets the lastModifiedTime value for this WorkbookQuery.
	 * 
	 * @param lastModifiedTime
	 */
	public void setLastModifiedTime(java.lang.String lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	private java.lang.Object	__equalsCalc	= null;

	@Override
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof WorkbookQuery)) {
			return false;
		}
		WorkbookQuery other = (WorkbookQuery) obj;
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true
				&& ((id == null && other.getId() == null) || (id != null && id
						.equals(other.getId())))
				&& ((name == null && other.getName() == null) || (name != null && name
						.equals(other.getName())))
				&& ((isTemplate == null && other.getIsTemplate() == null) || (isTemplate != null && isTemplate
						.equals(other.getIsTemplate())))
				&& ((timeZone == null && other.getTimeZone() == null) || (timeZone != null && timeZone
						.equals(other.getTimeZone())))
				&& ((note == null && other.getNote() == null) || (note != null && note
						.equals(other.getNote())))
				&& ((createUserId == null && other.getCreateUserId() == null) || (createUserId != null && createUserId
						.equals(other.getCreateUserId())))
				&& ((createTime == null && other.getCreateTime() == null) || (createTime != null && createTime
						.equals(other.getCreateTime())))
				&& ((lastModifiedUserId == null && other
						.getLastModifiedUserId() == null) || (lastModifiedUserId != null && lastModifiedUserId
						.equals(other.getLastModifiedUserId())))
				&& ((lastModifiedTime == null && other.getLastModifiedTime() == null) || (lastModifiedTime != null && lastModifiedTime
						.equals(other.getLastModifiedTime())));
		__equalsCalc = null;
		return _equals;
	}

	private boolean	__hashCodeCalc	= false;

	@Override
	public synchronized int hashCode() {
		if (__hashCodeCalc) {
			return 0;
		}
		__hashCodeCalc = true;
		int _hashCode = 1;
		if (getId() != null) {
			_hashCode += getId().hashCode();
		}
		if (getName() != null) {
			_hashCode += getName().hashCode();
		}
		if (getIsTemplate() != null) {
			_hashCode += getIsTemplate().hashCode();
		}
		if (getTimeZone() != null) {
			_hashCode += getTimeZone().hashCode();
		}
		if (getNote() != null) {
			_hashCode += getNote().hashCode();
		}
		if (getCreateUserId() != null) {
			_hashCode += getCreateUserId().hashCode();
		}
		if (getCreateTime() != null) {
			_hashCode += getCreateTime().hashCode();
		}
		if (getLastModifiedUserId() != null) {
			_hashCode += getLastModifiedUserId().hashCode();
		}
		if (getLastModifiedTime() != null) {
			_hashCode += getLastModifiedTime().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc	typeDesc	= new org.apache.axis.description.TypeDesc(
																			WorkbookQuery.class,
																			true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://api.editgrid.com", "WorkbookQuery"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("id");
		elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("name");
		elemField.setXmlName(new javax.xml.namespace.QName("", "name"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("isTemplate");
		elemField.setXmlName(new javax.xml.namespace.QName("", "isTemplate"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("timeZone");
		elemField.setXmlName(new javax.xml.namespace.QName("", "timeZone"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("note");
		elemField.setXmlName(new javax.xml.namespace.QName("", "note"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("createUserId");
		elemField.setXmlName(new javax.xml.namespace.QName("", "createUserId"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("createTime");
		elemField.setXmlName(new javax.xml.namespace.QName("", "createTime"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("lastModifiedUserId");
		elemField.setXmlName(new javax.xml.namespace.QName("",
				"lastModifiedUserId"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("lastModifiedTime");
		elemField.setXmlName(new javax.xml.namespace.QName("",
				"lastModifiedTime"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
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
			java.lang.String mechType, java.lang.Class _javaType,
			javax.xml.namespace.QName _xmlType) {
		return new org.apache.axis.encoding.ser.BeanSerializer(_javaType,
				_xmlType, typeDesc);
	}

	/**
	 * Get Custom Deserializer
	 */
	public static org.apache.axis.encoding.Deserializer getDeserializer(
			java.lang.String mechType, java.lang.Class _javaType,
			javax.xml.namespace.QName _xmlType) {
		return new org.apache.axis.encoding.ser.BeanDeserializer(_javaType,
				_xmlType, typeDesc);
	}

}
