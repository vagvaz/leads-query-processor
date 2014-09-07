/**
 * Revision.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.editgrid.ws;

public class Revision implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -7780791791943771458L;
	private java.lang.String	id;
	private java.lang.String	workbookId;
	private java.lang.String	workbook;
	private java.lang.String	lastModifiedUserId;
	private java.lang.String	lastModifiedUser;
	private java.lang.String	lastModifiedTime;
	private java.lang.String	note;
	private java.lang.Integer	isCurrent;
	private java.lang.Integer	isAutoSave;

	public Revision() {
	}

	public Revision(java.lang.String id, java.lang.String workbookId,
			java.lang.String workbook, java.lang.String lastModifiedUserId,
			java.lang.String lastModifiedUser,
			java.lang.String lastModifiedTime, java.lang.String note,
			java.lang.Integer isCurrent, java.lang.Integer isAutoSave) {
		this.id = id;
		this.workbookId = workbookId;
		this.workbook = workbook;
		this.lastModifiedUserId = lastModifiedUserId;
		this.lastModifiedUser = lastModifiedUser;
		this.lastModifiedTime = lastModifiedTime;
		this.note = note;
		this.isCurrent = isCurrent;
		this.isAutoSave = isAutoSave;
	}

	/**
	 * Gets the id value for this Revision.
	 * 
	 * @return id
	 */
	public java.lang.String getId() {
		return id;
	}

	/**
	 * Sets the id value for this Revision.
	 * 
	 * @param id
	 */
	public void setId(java.lang.String id) {
		this.id = id;
	}

	/**
	 * Gets the workbookId value for this Revision.
	 * 
	 * @return workbookId
	 */
	public java.lang.String getWorkbookId() {
		return workbookId;
	}

	/**
	 * Sets the workbookId value for this Revision.
	 * 
	 * @param workbookId
	 */
	public void setWorkbookId(java.lang.String workbookId) {
		this.workbookId = workbookId;
	}

	/**
	 * Gets the workbook value for this Revision.
	 * 
	 * @return workbook
	 */
	public java.lang.String getWorkbook() {
		return workbook;
	}

	/**
	 * Sets the workbook value for this Revision.
	 * 
	 * @param workbook
	 */
	public void setWorkbook(java.lang.String workbook) {
		this.workbook = workbook;
	}

	/**
	 * Gets the lastModifiedUserId value for this Revision.
	 * 
	 * @return lastModifiedUserId
	 */
	public java.lang.String getLastModifiedUserId() {
		return lastModifiedUserId;
	}

	/**
	 * Sets the lastModifiedUserId value for this Revision.
	 * 
	 * @param lastModifiedUserId
	 */
	public void setLastModifiedUserId(java.lang.String lastModifiedUserId) {
		this.lastModifiedUserId = lastModifiedUserId;
	}

	/**
	 * Gets the lastModifiedUser value for this Revision.
	 * 
	 * @return lastModifiedUser
	 */
	public java.lang.String getLastModifiedUser() {
		return lastModifiedUser;
	}

	/**
	 * Sets the lastModifiedUser value for this Revision.
	 * 
	 * @param lastModifiedUser
	 */
	public void setLastModifiedUser(java.lang.String lastModifiedUser) {
		this.lastModifiedUser = lastModifiedUser;
	}

	/**
	 * Gets the lastModifiedTime value for this Revision.
	 * 
	 * @return lastModifiedTime
	 */
	public java.lang.String getLastModifiedTime() {
		return lastModifiedTime;
	}

	/**
	 * Sets the lastModifiedTime value for this Revision.
	 * 
	 * @param lastModifiedTime
	 */
	public void setLastModifiedTime(java.lang.String lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	/**
	 * Gets the note value for this Revision.
	 * 
	 * @return note
	 */
	public java.lang.String getNote() {
		return note;
	}

	/**
	 * Sets the note value for this Revision.
	 * 
	 * @param note
	 */
	public void setNote(java.lang.String note) {
		this.note = note;
	}

	/**
	 * Gets the isCurrent value for this Revision.
	 * 
	 * @return isCurrent
	 */
	public java.lang.Integer getIsCurrent() {
		return isCurrent;
	}

	/**
	 * Sets the isCurrent value for this Revision.
	 * 
	 * @param isCurrent
	 */
	public void setIsCurrent(java.lang.Integer isCurrent) {
		this.isCurrent = isCurrent;
	}

	/**
	 * Gets the isAutoSave value for this Revision.
	 * 
	 * @return isAutoSave
	 */
	public java.lang.Integer getIsAutoSave() {
		return isAutoSave;
	}

	/**
	 * Sets the isAutoSave value for this Revision.
	 * 
	 * @param isAutoSave
	 */
	public void setIsAutoSave(java.lang.Integer isAutoSave) {
		this.isAutoSave = isAutoSave;
	}

	private java.lang.Object	__equalsCalc	= null;

	@Override
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Revision)) {
			return false;
		}
		Revision other = (Revision) obj;
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
				&& ((workbookId == null && other.getWorkbookId() == null) || (workbookId != null && workbookId
						.equals(other.getWorkbookId())))
				&& ((workbook == null && other.getWorkbook() == null) || (workbook != null && workbook
						.equals(other.getWorkbook())))
				&& ((lastModifiedUserId == null && other
						.getLastModifiedUserId() == null) || (lastModifiedUserId != null && lastModifiedUserId
						.equals(other.getLastModifiedUserId())))
				&& ((lastModifiedUser == null && other.getLastModifiedUser() == null) || (lastModifiedUser != null && lastModifiedUser
						.equals(other.getLastModifiedUser())))
				&& ((lastModifiedTime == null && other.getLastModifiedTime() == null) || (lastModifiedTime != null && lastModifiedTime
						.equals(other.getLastModifiedTime())))
				&& ((note == null && other.getNote() == null) || (note != null && note
						.equals(other.getNote())))
				&& ((isCurrent == null && other.getIsCurrent() == null) || (isCurrent != null && isCurrent
						.equals(other.getIsCurrent())))
				&& ((isAutoSave == null && other.getIsAutoSave() == null) || (isAutoSave != null && isAutoSave
						.equals(other.getIsAutoSave())));
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
		if (getWorkbookId() != null) {
			_hashCode += getWorkbookId().hashCode();
		}
		if (getWorkbook() != null) {
			_hashCode += getWorkbook().hashCode();
		}
		if (getLastModifiedUserId() != null) {
			_hashCode += getLastModifiedUserId().hashCode();
		}
		if (getLastModifiedUser() != null) {
			_hashCode += getLastModifiedUser().hashCode();
		}
		if (getLastModifiedTime() != null) {
			_hashCode += getLastModifiedTime().hashCode();
		}
		if (getNote() != null) {
			_hashCode += getNote().hashCode();
		}
		if (getIsCurrent() != null) {
			_hashCode += getIsCurrent().hashCode();
		}
		if (getIsAutoSave() != null) {
			_hashCode += getIsAutoSave().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc	typeDesc	= new org.apache.axis.description.TypeDesc(
																			Revision.class,
																			true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://api.editgrid.com", "Revision"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("id");
		elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("workbookId");
		elemField.setXmlName(new javax.xml.namespace.QName("", "workbookId"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("workbook");
		elemField.setXmlName(new javax.xml.namespace.QName("", "workbook"));
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
		elemField.setFieldName("lastModifiedUser");
		elemField.setXmlName(new javax.xml.namespace.QName("",
				"lastModifiedUser"));
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
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("note");
		elemField.setXmlName(new javax.xml.namespace.QName("", "note"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("isCurrent");
		elemField.setXmlName(new javax.xml.namespace.QName("", "isCurrent"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("isAutoSave");
		elemField.setXmlName(new javax.xml.namespace.QName("", "isAutoSave"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
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
