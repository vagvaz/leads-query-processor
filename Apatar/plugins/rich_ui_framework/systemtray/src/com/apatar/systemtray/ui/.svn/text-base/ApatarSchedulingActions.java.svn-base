/**
 * 
 */
package com.apatar.systemtray.ui;

import com.apatar.systemtray.AbstractApatarSchedulingActions;

/**
 * @author konstantin.m
 */
public class ApatarSchedulingActions extends AbstractApatarSchedulingActions {

	private JSchedulingPropertyDialog	dlg	= null;

	/**
	 * 
	 */
	public ApatarSchedulingActions(JSchedulingPropertyDialog dlg) {
		this.dlg = dlg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.systemtray.AbstractApatarSchedulingActions#schedulingStatusChanged()
	 */
	@Override
	public void schedulingStatusChanged() {
		if (null != dlg) {
			dlg.renewTaskTable();
		}
	}
}
