/**
 * 
 */
package com.apatar.systemtray;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author konstantin.m
 */
public class ApatarSchedulingTimer extends Timer {

	private long	checkingInterval	= 500;

	private class schedulingsManager extends TimerTask {

		@Override
		public void run() {
			for (Scheduling scheduling : schedulings.values()) {
				if (scheduling.isTaskEnabled() && !scheduling.isTaskRunnig()) {
					GregorianCalendar now = new GregorianCalendar();
					now.set(GregorianCalendar.MILLISECOND, 0);
					try {
						int diff = scheduling.getNextTaskRunDate().compareTo(
								now);
						if (diff < 0) {
							try {
								if (scheduling.getNextTaskRunDate(true)
										.compareTo(now) == 0) {
									scheduling.run();
								}
							} catch (ApatarSchedulingWeeklyException e) {
								System.err.println(e.getMessage());
							}
						}
						if (diff == 0) {
							scheduling.run();
						}
					} catch (RuntimeException e) {
						scheduling.setTaskEnabled(false);
						e.printStackTrace();
					}
				}
			}

		}
	}

	private HashMap<String, Scheduling>	schedulings	= new HashMap<String, Scheduling>();
	private schedulingsManager			manager		= new schedulingsManager();

	/**
	 * 
	 */
	public ApatarSchedulingTimer() {
		super();
	}

	public ApatarSchedulingTimer(boolean immediateRun) {
		super();
		if (immediateRun) {
			schedule(manager, 0, checkingInterval);
		}
	}

	public void runManager() {
		schedule(manager, 0, checkingInterval);
	}

	/**
	 * @return the schedulings
	 */
	public HashMap<String, Scheduling> getSchedulings() {
		return schedulings;
	}

}
