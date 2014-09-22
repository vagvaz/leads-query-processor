package com.apatar.mysql;

public class ColumnMetaData implements Cloneable {
	private String columnName;
	private String originalColumnName;
	private long charMaxLength;
	private boolean isNullable;
	private String dataType;
	private String baseTable;

	/**
	 * @return the columnName
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * @param columnName
	 *            the columnName to set
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	/**
	 * @return the charMaxLength
	 */
	public long getCharMaxLength() {
		return charMaxLength;
	}

	public ColumnMetaData(String columnName, long charMaxLength,
			boolean isNullable, String dataType) {
		super();
		this.columnName = columnName;
		this.charMaxLength = charMaxLength;
		this.isNullable = isNullable;
		this.dataType = dataType;
	}

	public ColumnMetaData(String columnName, long charMaxLength,
			boolean isNullable, String dataType, String table) {
		super();
		this.columnName = columnName;
		this.charMaxLength = charMaxLength;
		this.isNullable = isNullable;
		this.dataType = dataType;
		baseTable = table;
	}

	public ColumnMetaData(String columnName, String originalColumnName,
			String table, String dataType) {
		super();
		this.columnName = columnName;
		baseTable = table;
		this.originalColumnName = originalColumnName;
		this.dataType = dataType;
		isNullable = true;
		charMaxLength = 0L;
	}

	/**
	 * @param charMaxLength
	 *            the charMaxLength to set
	 */
	public void setCharMaxLength(long charMaxLength) {
		this.charMaxLength = charMaxLength;
	}

	/**
	 * @return the isNullable
	 */
	public boolean isNullable() {
		return isNullable;
	}

	/**
	 * @param isNullable
	 *            the isNullable to set
	 */
	public void setNullable(boolean isNullable) {
		this.isNullable = isNullable;
	}

	/**
	 * @return the dataType
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * @param dataType
	 *            the dataType to set
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the baseTable
	 */
	public String getBaseTable() {
		return baseTable;
	}

	/**
	 * @return the originalColumnName
	 */
	public String getOriginalColumnName() {
		return originalColumnName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getColumnName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected ColumnMetaData clone() throws CloneNotSupportedException {
		return new ColumnMetaData(columnName, charMaxLength, isNullable,
				dataType);
	}
}
