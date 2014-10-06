/**
 * 
 */
package com.apatar.systemtray.ui;

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.apatar.systemtray.Scheduling;

/**
 * @author konstantin.m
 */
public class ApatarSchedulingChangeAction implements ChangeListener {

	private Scheduling	scheduling	= null;

	public ApatarSchedulingChangeAction(Scheduling scheduling) {
		super();
		this.scheduling = scheduling;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		if (null != scheduling) {
			if ("JSpinner".equals(e.getSource().getClass().getSimpleName())) {
				JSpinner spinner = (JSpinner) e.getSource();

				if ("dailyRepeatTaskEveryMinuteOrHour"
						.equals(spinner.getName())) {
					try {
						scheduling.setDailyRepeatTaskEveryMinuteOrHour(
								(Integer) spinner.getValue(), true);
					} catch (IllegalArgumentException e1) {
						System.err.println(e1.getMessage());
						spinner.setValue(scheduling
								.getDailyRepeatTaskEveryMinuteOrHour());
					}
				}
				if ("repeatTaskEveryDWM".equals(spinner.getName())) {
					try {
						scheduling.setRepeatTaskEveryDWM((Integer) spinner
								.getValue(), true);
					} catch (IllegalArgumentException e1) {
						System.err.println(e1.getMessage());
						spinner.setValue(scheduling.getRepeatTaskEveryDWM());
					}
				}
				if ("monthlyRunAtDayOfMonth".equals(spinner.getName())) {
					try {
						scheduling.setMonthlyRunAtDayOfMonth((Integer) spinner
								.getValue(), true);
					} catch (IllegalArgumentException e1) {
						System.err.println(e1.getMessage());
						spinner
								.setValue(scheduling
										.getMonthlyRunAtDayOfMonth());
					}
				}
				if ("totalNumberTaskRuns".equals(spinner.getName())) {
					try {
						scheduling.setTotalNumberTaskRuns((Integer) spinner
								.getValue(), true);
					} catch (IllegalArgumentException e1) {
						System.err.println(e1.getMessage());
						spinner.setValue(scheduling.getTotalNumberTaskRuns());
					}
				}
			}
		} else {
			System.out
					.println("Value cannot be validated. scheduling property is null");
		}
	}

}
