package com.apatar.salesforcecom;

import com.sforce.async.Error;
import com.sforce.async.SaveResult;

public class sfRestErrorRecord {
	private String Id = "";
	private String errorMessage = "";

	public sfRestErrorRecord(SaveResult saveRes) {
		String errMessage = "";
		setId(saveRes.getId());
		for (Error error : saveRes.getErrors()) {
			errMessage += error.getMessage() + "; ";
		}
		setErrorMessage(errMessage);
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return Id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		Id = id;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage
	 *            the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
