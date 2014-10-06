/*
_______________________
Apatar Open Source Data Integration
Copyright (C) 2005-2007, Apatar, Inc.
info@apatar.com
195 Meadow St., 2nd Floor
Chicopee, MA 01013

### This program is free software; you can redistribute it and/or modify
### it under the terms of the GNU General Public License as published by
### the Free Software Foundation; either version 2 of the License, or
### (at your option) any later version.

### This program is distributed in the hope that it will be useful,
### but WITHOUT ANY WARRANTY; without even the implied warranty of
### MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.# See the
### GNU General Public License for more details.

### You should have received a copy of the GNU General Public License along
### with this program; if not, write to the Free Software Foundation, Inc.,
### 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
________________________

 */

package com.apatar.systemtray;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JOptionPane;

import org.jdom.Element;

import com.apatar.core.ApplicationData;
import com.apatar.core.Runnable;
import com.apatar.ui.ProcessingProgressActions;
import com.apatar.ui.ReadWriteXMLDataUi;

public class Scheduling extends Thread {

	private String pathToDatamap;

	private String schedulingName;

	private AbstractApatarSchedulingActions actions = null;

	private boolean taskRunnig = false;

	private boolean taskEnabled = false;

	/**
	 * 1 - daily (default); 2 - weekly; 3 - monthly;
	 */
	private int recurringType = 1;

	private boolean[] weeklyRunAtDays = { false, false, false, false, false,
			false, false };

	/**
	 * means that task must be repeated every (Day or Week or Month) value of
	 * this field (default = 1). must have value greater than 0
	 */
	private int repeatTaskEveryDWM = 1;

	/**
	 * 1 - repeat task every minute (default); 2 - repeat task every hour
	 */
	private int repeatTaskEveryDWMtype = 1;

	private GregorianCalendar runTaskOnceAtTime = null;

	/**
	 * value 1 - means run once at specified time of the Day; value 2 - means
	 * recurring run every minute or every hour
	 */
	private int dailyRunType = 1;

	/**
	 * must have value greater than 0
	 */
	private int dailyRepeatTaskEveryMinuteOrHour = 1;

	private GregorianCalendar startTaskAtTime = null;

	private GregorianCalendar stopTaskAtTime = null;

	private GregorianCalendar startTaskAtDate = null;

	private GregorianCalendar stopTaskAtDate = null;

	private GregorianCalendar nextTaskRunDate = null;

	/**
	 * indicates how much times task have to be run. Zero value (default) means
	 * unlimited times. must have positive value.
	 */
	private int totalNumberTaskRuns = 0;

	/**
	 * 1 - run at every this.monthlyRunAtDayOfMonth day of month; 2 - run at
	 * specified (this.monthlyNumberOfDayOfWeek) day of week of the month;
	 */
	private int monthlyRunType = 1;

	/**
	 * value from 1 to 31
	 */
	private int monthlyRunAtDayOfMonth = 1;

	/**
	 * value from 1 to 5 (from first week to last)
	 */
	private int monthlyNumberOfDayOfWeek = 1;

	/**
	 * value from 1 to 7 (from Monday to Sunday)
	 */
	private int monthlyRunAtDayOfWeek = 1;

	private GregorianCalendar taskLastRunDate = null;

	private int taskRuns = 0;

	/*
	 * After calling method <b>getLastWarningMessage()</b> value of the
	 * lastWarningMessage becomes null
	 */
	private String lastWarningMessage = null;

	public boolean cancelTask() {
		setTaskEnabled(false);
		return false;
	}

	public String getTaskStatus() {
		if (taskEnabled && taskRunnig) {
			return "On (runnig)";
		}
		if (taskEnabled && !taskRunnig) {
			return "On (Idle)";
		}
		return "Off";
	}

	public Element generateXML() {
		Element Job = new Element("Job");
		Job.addContent(new Element("name").setText(schedulingName));
		Job.addContent(new Element("pathToDatamap").setText(pathToDatamap));
		Job.addContent(new Element("recurringType").setText(String
				.valueOf(recurringType)));
		Element recurringType = new Element(getRecurringType(true));
		recurringType.addContent(new Element("repeatTaskEveryDWM")
				.setText(String.valueOf(repeatTaskEveryDWM)));

		switch (getReccuringType()) {
		case 3: // monthly
			recurringType.addContent(new Element("monthlyRunType")
					.setText(String.valueOf(monthlyRunType)));
			if (monthlyRunType == 1) {
				recurringType.addContent(new Element("monthlyRunAtDayOfMonth")
						.setText(String.valueOf(monthlyRunAtDayOfMonth)));
			} else {
				recurringType
						.addContent(new Element("monthlyNumberOfDayOfWeek")
								.setText(String
										.valueOf(monthlyNumberOfDayOfWeek)));
				recurringType.addContent(new Element("monthlyRunAtDayOfWeek")
						.setText(String.valueOf(monthlyRunAtDayOfWeek)));
			}
			break;
		case 2: // weekly
			Element daysOfWeek = new Element("daysOfWeek");
			for (int i = 0; i < 7; i++) {
				Element dayOfWeek = new Element("dayOfWeek");
				dayOfWeek.setAttribute("number", String.valueOf(i + 1));
				dayOfWeek.setText(String.valueOf(weeklyRunAtDays[i]));

				daysOfWeek.addContent(dayOfWeek);
			}
			recurringType.addContent(daysOfWeek);

			break;
		case 1: // daily
		default:
			break;
		}
		Job.addContent(recurringType);
		Element DailyFrequency = new Element("DailyFrequency");

		DailyFrequency.addContent(new Element("repeatTaskEveryDWMtype")
				.setText(String.valueOf(repeatTaskEveryDWMtype)));
		switch (repeatTaskEveryDWMtype) {
		case 2:
			DailyFrequency.addContent(new Element("dailyRunType")
					.setText(String.valueOf(dailyRunType)));
			DailyFrequency.addContent(new Element(
					"dailyRepeatTaskEveryMinuteOrHour").setText(String
					.valueOf(dailyRepeatTaskEveryMinuteOrHour)));
			Element startTaskAtTime = new Element("startTaskAtTime");
			startTaskAtTime.setAttribute("hour", String
					.valueOf(this.startTaskAtTime.get(Calendar.HOUR_OF_DAY)));
			startTaskAtTime.setAttribute("minute", String
					.valueOf(this.startTaskAtTime.get(Calendar.MINUTE)));

			Element stopTaskAtTime = new Element("stopTaskAtTime");
			stopTaskAtTime.setAttribute("hour", String
					.valueOf(this.stopTaskAtTime.get(Calendar.HOUR_OF_DAY)));
			stopTaskAtTime.setAttribute("minute", String
					.valueOf(this.stopTaskAtTime.get(Calendar.MINUTE)));

			DailyFrequency.addContent(startTaskAtTime);
			DailyFrequency.addContent(stopTaskAtTime);

			break;
		case 1:
		default:
			Element runTaskOnceAtTime = new Element("runTaskOnceAtTime");
			runTaskOnceAtTime.setAttribute("hour", String
					.valueOf(this.runTaskOnceAtTime.get(Calendar.HOUR_OF_DAY)));
			runTaskOnceAtTime.setAttribute("minute", String
					.valueOf(this.runTaskOnceAtTime.get(Calendar.MINUTE)));
			DailyFrequency.addContent(runTaskOnceAtTime);
			break;
		}

		Job.addContent(DailyFrequency);

		Element Duration = new Element("Duration");

		Element startTaskAtDate = new Element("startTaskAtDate");
		startTaskAtDate.setAttribute("year", String
				.valueOf(this.startTaskAtDate.get(Calendar.YEAR)));
		startTaskAtDate.setAttribute("month", String
				.valueOf(this.startTaskAtDate.get(Calendar.MONTH)));
		startTaskAtDate.setAttribute("day", String.valueOf(this.startTaskAtDate
				.get(Calendar.DAY_OF_MONTH)));

		Duration.addContent(startTaskAtDate);

		if (null != stopTaskAtDate) {
			Element stopTaskAtDate = new Element("stopTaskAtDate");
			stopTaskAtDate.setAttribute("year", String
					.valueOf(this.stopTaskAtDate.get(Calendar.YEAR)));
			stopTaskAtDate.setAttribute("month", String
					.valueOf(this.stopTaskAtDate.get(Calendar.MONTH)));
			stopTaskAtDate.setAttribute("day", String
					.valueOf(this.stopTaskAtDate.get(Calendar.DAY_OF_MONTH)));
			Duration.addContent(stopTaskAtDate);
		}

		Duration.addContent(new Element("totalNumberTaskRuns").setText(String
				.valueOf(totalNumberTaskRuns)));

		Job.addContent(Duration);

		Element TaskInfo = new Element("TaskInfo");
		if (null != taskLastRunDate) {
			TaskInfo.addContent(new Element("taskLastRunDate").setText(String
					.valueOf(taskLastRunDate.getTimeInMillis())));
		}
		TaskInfo.addContent(new Element("taskRuns").setText(String
				.valueOf(taskRuns)));
		TaskInfo.addContent(new Element("taskEnabled").setText(String
				.valueOf(taskEnabled)));

		Job.addContent(TaskInfo);

		return Job;
	}

	private Element parseXML(Element xmlRoot) {
		boolean isOldFormat = false;
		if (null == xmlRoot) {
			setRunTaskOnceAtTime();
			setStartTaskAtDate();
			stopTaskAtDate = null;
			setStartTaskAtTime();
			setStopTaskAtTime();
			taskLastRunDate = null;
			return generateXML();
		}

		String value = xmlRoot.getChildText("name");
		if (value != null) {
			schedulingName = value;
		}
		value = null;
		// check old value, set as child node "filePath" of the "Job" node
		value = xmlRoot.getChildText("filePath");
		if (null == value) {
			// check new value, set as child node "pathToDatamap" of the "Job"
			// node
			value = xmlRoot.getChildText("pathToDatamap");
		}
		if (null == value) {
			throw new NullPointerException("Path to datamap not specified.");
		}
		setPathToDatamap(value);
		// check old value, set as attribute "type" of the "Job" node
		value = xmlRoot.getAttributeValue("type");
		if (value == null) {
			// check new value at child node "Job"
			value = xmlRoot.getChildText("recurringType");

		} else {
			isOldFormat = true;
		}
		String wasError = null;
		if (null != value) {
			try {
				this.setRecurringType(Integer.parseInt(value));
			} catch (NumberFormatException nfe) {
				wasError = nfe.getMessage();
				try {
					this.setRecurringType(value);
					wasError = null;
				} catch (Exception e) {
					wasError = e.getMessage();
				}
			} catch (Exception e) {
				wasError = e.getMessage();
			} finally {
				if (null != wasError) {
					System.err
							.println("reccuringType read with error. value set to default. Error is:\n"
									+ wasError);
				}

			}
		}
		value = null;
		String reccuringElementName = getRecurringType(true);
		Element reccuringElement = xmlRoot.getChild(reccuringElementName);
		// check old value child node "period"
		value = reccuringElement.getChildText("period");
		if (value == null) {
			value = xmlRoot.getChildText("repeatTaskEveryDWM");
		}
		if (null != value) {
			try {
				setRepeatTaskEveryDWM(Integer.parseInt(value), false);
			} catch (Exception e) {
				System.err
						.println("repeatTaskEveryDWM read with error. value set to default. Error is:\n"
								+ e.getMessage());
			}
		}
		wasError = null;
		value = null;
		switch (recurringType) {
		case 2: // weekly
			try {
				Element daysOfWeek = reccuringElement.getChild("daysOfWeek");

				for (Object obj : daysOfWeek.getChildren("dayOfWeek")) {
					Element dayOfWeek = (Element) obj;

					int dayNumber = Integer.parseInt(dayOfWeek
							.getAttributeValue("number"));
					boolean isSel = Boolean.parseBoolean(dayOfWeek.getText());
					if (dayNumber >= 1 && dayNumber <= 7) {
						weeklyRunAtDays[dayNumber - 1] = isSel;
					} else {
						wasError = "Week day number must be int value from 1 to 7. Given value is `"
								+ String.valueOf(dayNumber) + "`";
					}
				}
			} catch (Exception e) {
				wasError = e.getMessage();
			}
			if (null != wasError) {
				System.err
						.println("weekly reccuring settings read with error. Error is:\n"
								+ wasError);
			}

			break;
		case 3: // monthly
			// check old value, set as attribute "type" of the "Monthly"
			// node
			value = reccuringElement.getAttributeValue("type");
			if (null == value) {
				// check new value, set as child node "monthlyRunType" of
				// the "Monthly" node
				value = reccuringElement.getChildText("monthlyRunType");
			}
			if (null != value) {
				try {
					setMonthlyRunType(Integer.parseInt(value));
				} catch (Exception e) {
					System.err
							.println("monthlyRunType read with error. value set to default. Error is:\n"
									+ e.getMessage());
				}
			}
			value = null;
			switch (monthlyRunType) {
			case 2:
				// check old value, set as child node "number" of the
				// "Monthly" node
				value = reccuringElement.getChildText("number");
				if (null == value) {
					// check new value, set as child node
					// "monthlyNumberOfDayOfWeek" of
					// the "Monthly" node
					value = reccuringElement
							.getChildText("monthlyNumberOfDayOfWeek");
					isOldFormat = false;
				} else {
					// old values are starting from 0, but new are from
					// 1, so we
					// have to increment it
					isOldFormat = true;
					// value = String.valueOf(Integer.parseInt(value) +
					// 1);
				}
				if (null != value) {
					try {
						setMonthlyNumberOfDayOfWeek(Integer.parseInt(value)
								+ (isOldFormat ? 1 : 0));
					} catch (Exception e) {
						System.err
								.println("monthlyNumberOfDayOfWeek read with error. value set to default. Error is:\n"
										+ e.getMessage());
					}
				}
				value = null;
				// check old value, set as child node "dayOfWeek" of the
				// "Monthly" node
				value = reccuringElement.getChildText("dayOfWeek");
				if (null == value) {
					// check new value, set as child node
					// "monthlyRunAtDayOfWeek" of
					// the "Monthly" node
					isOldFormat = false;
					value = reccuringElement
							.getChildText("monthlyRunAtDayOfWeek");
				} else {
					isOldFormat = true;
				}
				if (null != value) {
					try {
						setMonthlyRunAtDayOfWeek(Integer.parseInt(value)
								+ (isOldFormat ? 1 : 0));
					} catch (Exception e) {
						System.err
								.println("monthlyRunAtDayOfWeek read with error. value set to default. Error is:\n"
										+ e.getMessage());
					}
				}
				break;
			case 1:
			default:
				// check old value, set as child node "day" of the
				// "Monthly" node
				value = reccuringElement.getChildText("day");
				if (null == value) {
					// check new value, set as child node
					// "monthlyRunAtDayOfMonth" of
					// the "Monthly" node
					value = reccuringElement
							.getChildText("monthlyRunAtDayOfMonth");
				}
				if (null != value) {
					try {
						setMonthlyRunAtDayOfMonth(Integer.parseInt(value),
								false);
					} catch (Exception e) {
						System.err
								.println("monthlyRunAtDayOfMonth read with error. value set to default. Error is:\n"
										+ e.getMessage());
					}
				}
				break;
			}
			break;
		case 1: // daily. Data already read from XML. it was
			// repeatTaskEveryDWM property
		default:
			break;
		}
		value = null;
		Element dailyFrequency = xmlRoot.getChild("DailyFrequency");
		// check old value, set as attribute "type" of the "DailyFrequency"
		// node
		value = dailyFrequency.getAttributeValue("type");
		if (null == value) {
			// check new value, set as child node "repeatTaskEveryDWMtype" of
			// the "DailyFrequency" node
			value = dailyFrequency.getChildText("repeatTaskEveryDWMtype");
		}
		if (null != value) {
			try {
				setRepeatTaskEveryDWMtype(Integer.parseInt(value));
			} catch (Exception e) {
				System.err
						.println("repeatTaskEveryDWMtype read with error. value set to default. Error is:\n"
								+ e.getMessage());
			}
		}

		value = null;
		switch (repeatTaskEveryDWMtype) {
		case 2:
			// check old value, set as child node "typePeriod" of the
			// "DailyFrequency" node
			value = dailyFrequency.getChildText("typePeriod");
			if (null == value) {
				// check new value, set as child node
				// "DailyRunType" of
				// the "Monthly" node
				value = dailyFrequency.getChildText("DailyRunType");
			} else {
				// old values are starting from 0, but new are from 1, so we
				// have to increment it
				value = String.valueOf(Integer.parseInt(value) + 1);
			}
			if (null != value) {
				try {
					setDailyRunType(Integer.parseInt(value));
				} catch (Exception e) {
					System.err
							.println("DailyRunType read with error. value set to default. Error is:\n"
									+ e.getMessage());
				}
			}
			// check old value child node "period"
			value = dailyFrequency.getChildText("period");
			if (value == null) {
				value = dailyFrequency
						.getChildText("dailyRepeatTaskEveryMinuteOrHour");
			}
			if (null != value) {
				try {
					setDailyRepeatTaskEveryMinuteOrHour(
							Integer.parseInt(value), false);
				} catch (Exception e) {
					System.err
							.println("dailyRepeatTaskEveryMinuteOrHour read with error. value set to default. Error is:\n"
									+ e.getMessage());
				}
			}
			// check old value, set as three attributes of child node
			// "startingAt"
			// of the "DailyFrequency". Attributes are: hour, minute,
			// second. We do not need seconds.
			Element startTaskAtTime = dailyFrequency.getChild("startingAt");
			if (startTaskAtTime == null) {
				// check new value, set as two attributes of child node
				// "startTaskAtTime"
				// of the "DailyFrequency". Attributes are: hour and minute
				startTaskAtTime = dailyFrequency.getChild("startTaskAtTime");
			}
			if (this.startTaskAtTime == null) {
				setStartTaskAtTime();
			}
			try {
				this.startTaskAtTime.set(Calendar.HOUR_OF_DAY, Integer
						.parseInt(startTaskAtTime.getAttributeValue("hour")));
				this.startTaskAtTime.set(Calendar.MINUTE, Integer
						.parseInt(startTaskAtTime.getAttributeValue("minute")));
			} catch (NumberFormatException e) {
				System.err
						.println("startTaskAtTime read with error. value set to default. Error is:\n"
								+ e.getMessage());
			}
			// check old value, set as three attributes of child node
			// "endingAt"
			// of the "DailyFrequency". Attributes are: hour, minute,
			// second. We do not need seconds.
			Element stopTaskAtTime = dailyFrequency.getChild("endingAt");
			if (stopTaskAtTime == null) {
				// check new value, set as two attributes of child node
				// "stopTaskAtTime"
				// of the "DailyFrequency". Attributes are: hour and minute
				stopTaskAtTime = dailyFrequency.getChild("stopTaskAtTime");
			}
			if (this.stopTaskAtTime == null) {
				setStopTaskAtTime();
			}
			try {
				this.stopTaskAtTime.set(Calendar.HOUR_OF_DAY, Integer
						.parseInt(stopTaskAtTime.getAttributeValue("hour")));
				this.stopTaskAtTime.set(Calendar.MINUTE, Integer
						.parseInt(stopTaskAtTime.getAttributeValue("minute")));
			} catch (NumberFormatException e) {
				System.err
						.println("stopTaskAtTime read with error. value set to default. Error is:\n"
								+ e.getMessage());
			}
			break;

		case 1:
		default:
			// check old value, set as three attributes of child node "time"
			// of the "DailyFrequency". Attributes are: hour, minute,
			// second. We do not need seconds.
			Element runTaskOnceAtTime = dailyFrequency.getChild("time");
			if (runTaskOnceAtTime == null) {
				// check new value, set as two attributes of child node
				// "runTaskOnceAtTime"
				// of the "DailyFrequency". Attributes are: hour and minute
				runTaskOnceAtTime = dailyFrequency
						.getChild("runTaskOnceAtTime");
			}
			if (this.runTaskOnceAtTime == null) {
				setRunTaskOnceAtTime();
			}
			try {
				this.runTaskOnceAtTime.set(Calendar.HOUR_OF_DAY, Integer
						.parseInt(runTaskOnceAtTime.getAttributeValue("hour")));
				this.runTaskOnceAtTime.set(Calendar.MINUTE,
						Integer.parseInt(runTaskOnceAtTime
								.getAttributeValue("minute")));
			} catch (Exception e) {
				System.err
						.println("runTaskOnceAtTime read with error. value set to default. Error is:\n"
								+ e.getMessage());
			}
			break;
		}
		Element Duration = xmlRoot.getChild("Duration");
		// check old value, set as three attributes of child node "startDate"
		// of the "Duration". Attributes are day, month, year.
		Element startTaskAtDate = Duration.getChild("startDate");
		if (startTaskAtDate == null) {
			// check new value, set as three attributes of child node
			// "startTaskAtDate" of the "Duration". Attributes are: day, month,
			// year
			startTaskAtDate = Duration.getChild("startTaskAtDate");
		}
		if (this.startTaskAtDate == null) {
			setStartTaskAtDate();
		}
		try {
			this.startTaskAtDate.set(Calendar.YEAR, Integer
					.parseInt(startTaskAtDate.getAttributeValue("year")));
			this.startTaskAtDate.set(Calendar.MONTH, Integer
					.parseInt(startTaskAtDate.getAttributeValue("month")));
			this.startTaskAtDate.set(Calendar.DAY_OF_MONTH, Integer
					.parseInt(startTaskAtDate.getAttributeValue("day")));
		} catch (NumberFormatException e) {
			System.err
					.println("startTaskAtDate read with error. value set to default. Error is:\n"
							+ e.getMessage());
		}
		// check old value, set as three attributes of child node "endDate"
		// of the "Duration". Attributes are day, month, year.
		Element stopTaskAtDate = Duration.getChild("endDate");
		if (stopTaskAtDate == null) {
			// check new value, set as three attributes of child node
			// "stopTaskAtDate" of the "Duration". Attributes are: day, month,
			// year
			stopTaskAtDate = Duration.getChild("stopTaskAtDate");
		}
		if (null == stopTaskAtDate) {
			this.stopTaskAtDate = null;
		} else {
			if (this.stopTaskAtDate == null) {
				setStopTaskAtDate();
			}
			try {
				this.stopTaskAtDate.set(Calendar.YEAR, Integer
						.parseInt(stopTaskAtDate.getAttributeValue("year")));
				this.stopTaskAtDate.set(Calendar.MONTH, Integer
						.parseInt(stopTaskAtDate.getAttributeValue("month")));
				this.stopTaskAtDate.set(Calendar.DAY_OF_MONTH, Integer
						.parseInt(stopTaskAtDate.getAttributeValue("day")));
			} catch (NumberFormatException e) {
				System.err
						.println("stopTaskAtDate read with error. value set to default. Error is:\n"
								+ e.getMessage());
			}
		}

		value = null;
		value = Duration.getChildText("totalNumberTaskRuns");
		if (null == value) {
			setTotalNumberTaskRuns(0);
		} else {
			try {
				setTotalNumberTaskRuns(Integer.parseInt(value), false);
			} catch (Exception e) {
				System.err
						.println("totalNumberTaskRuns read with error. value set to default. Error is:\n"
								+ e.getMessage());
				setTotalNumberTaskRuns(0);
			}
		}
		value = null;
		Element TaskInfo = xmlRoot.getChild("TaskInfo");

		if (null != TaskInfo) {
			value = TaskInfo.getChildText("taskLastRunDate");
			if (null != value) {
				try {
					this.setTaskLastRunDate(Long.parseLong(value));
				} catch (Exception e) {
					System.err
							.println("taskLastRunDate read with error. value set to default. Error is:\n"
									+ e.getMessage());
				}
			} else {
				taskLastRunDate = null;
			}
			value = null;
			value = TaskInfo.getChildText("taskRuns");
			if (null != value) {
				try {
					setTaskRuns(Integer.parseInt(value));
				} catch (Exception e) {
					System.err
							.println("taskRuns read with error. value set to default. Error is:\n"
									+ e.getMessage());
				}
			}
			value = null;
			value = TaskInfo.getChildText("taskEnabled");
			if (null != value) {
				try {
					taskEnabled = Boolean.parseBoolean(value);
				} catch (Exception e) {
					System.err
							.println("taskEnabled read with error. value set to default. Error is:\n"
									+ e.getMessage());
				}
			}
		} else {
			System.err
					.println("TaskInfo node not found. All child nodes values set to default.");

		}
		return xmlRoot;
	}

	public Scheduling(Element root, AbstractApatarSchedulingActions actions) {
		super();
		this.actions = actions;
		parseXML(root);

		try {
			calculateNextTaskRunDate(true);
		} catch (ApatarSchedulingWeeklyException e) {
			System.err.println(e.getMessage());
		}
	}

	private int compareDates(GregorianCalendar stopDate, GregorianCalendar now) {
		GregorianCalendar temp1 = (GregorianCalendar) stopTaskAtDate.clone();
		GregorianCalendar temp2 = (GregorianCalendar) now.clone();

		temp1.set(Calendar.HOUR_OF_DAY, 0);
		temp1.set(Calendar.MINUTE, 0);
		temp1.set(Calendar.SECOND, 0);
		temp1.set(Calendar.MILLISECOND, 0);

		temp2.set(Calendar.HOUR_OF_DAY, 0);
		temp2.set(Calendar.MINUTE, 0);
		temp2.set(Calendar.SECOND, 0);
		temp2.set(Calendar.MILLISECOND, 0);

		return temp1.compareTo(temp2);
	}

	private void disableTask(boolean silentMode) {
		if (silentMode) {
			taskEnabled = false;
		} else {
			setTaskEnabled(false);
		}
	}

	private void enableTask(boolean silentMode) {
		if (silentMode) {
			taskEnabled = true;
		} else {
			setTaskEnabled(true);
		}
	}

	private void calculateNextTaskRunDate(boolean silentMode)
			throws ApatarSchedulingWeeklyException {
		GregorianCalendar now = new GregorianCalendar();
		// check if current date is within Start and Stop date
		if (((totalNumberTaskRuns > 0) && (taskRuns == totalNumberTaskRuns))
				|| ((null != stopTaskAtDate) && (compareDates(stopTaskAtDate,
						now) < 0))) {
			if ((totalNumberTaskRuns > 0) && (taskRuns == totalNumberTaskRuns)) {
				lastWarningMessage = "taskRuns value reached totalNumberTaskRuns.";
			} else {
				lastWarningMessage = "stopTaskAtDate date reachead";
			}
			systemOut(lastWarningMessage);
			disableTask(silentMode);
			nextTaskRunDate = null;
			return;
		}
		if (recurringType == 2) {
			// if weekly runs than at least one day of week must be selected
			if (!isAtLeastOneDayOfWeekSelected()) {
				disableTask(silentMode);
				throw new ApatarSchedulingWeeklyException(
						"Please select the day the task should be run on.");
			}
		}
		// to schedule task properly we have to compute next task run time based
		// on startDate or lastRunTime
		GregorianCalendar baseDate = (taskLastRunDate == null ? startTaskAtDate
				: taskLastRunDate);
		systemOut("Next run date calculated from "
				+ (taskLastRunDate == null ? "startTaskAtDate"
						: "taskLastRunDate"));
		systemOut("Base date is `" + baseDate.getTime().toString() + "`");
		setNextTaskRunDate(baseDate, now);
		if ((null != stopTaskAtDate)
				&& (compareDates(stopTaskAtDate, nextTaskRunDate) < 0)) {
			nextTaskRunDate = null;
			systemOut("Next run date is after stopTaskAtDate. Task disabled.");
			disableTask(silentMode);
		} else {
			systemOut("Next run date is `"
					+ nextTaskRunDate.getTime().toString() + "`");
			enableTask(silentMode);
		}
	}

	private void systemOut(String message) {
		System.out.println("Task: `" + schedulingName + "`. " + message);
	}

	private void setNextTaskRunDate(GregorianCalendar base,
			GregorianCalendar now) {

		if (base == null || now == null) {
			throw new IllegalArgumentException(
					"Both dates base and now should not be null");
		}

		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);

		GregorianCalendar tempBase = (GregorianCalendar) base.clone();
		tempBase.set(Calendar.HOUR_OF_DAY, 0);
		tempBase.set(Calendar.MINUTE, 0);
		tempBase.set(Calendar.SECOND, 0);
		tempBase.set(Calendar.MILLISECOND, 0);

		GregorianCalendar tempNow = (GregorianCalendar) now.clone();
		tempNow.set(Calendar.HOUR_OF_DAY, 0);
		tempNow.set(Calendar.MINUTE, 0);
		tempNow.set(Calendar.SECOND, 0);
		tempNow.set(Calendar.MILLISECOND, 0);

		int diff = base.compareTo(now);
		if (diff > 0) {
			System.out.println("BaseDate is in the future.");
		}
		diff = 0;

		// calculating next big period - month, week or day.

		diff = tempBase.compareTo(tempNow);
		while (diff < 0) {
			addOneBigPeriod(tempBase);
			diff = tempBase.compareTo(tempNow);
		}

		// calculate time
		switch (repeatTaskEveryDWMtype) {
		case 2:
			tempBase.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
			tempBase.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
			/*
			 * switch (dailyRunType) { case 2: // every hour
			 * tempBase.add(GregorianCalendar.HOUR_OF_DAY, -1); break; case 1:
			 * // every minute default: tempBase.add(GregorianCalendar.MINUTE,
			 * -1); break; }
			 */
			do {
				if (addOneSmallPeriod(tempBase)) {
					addOneBigPeriod(tempBase);
				}
				diff = tempBase.compareTo(now);
			} while (diff <= 0);
			break;
		case 1:
		default:
			tempBase.set(Calendar.HOUR_OF_DAY, runTaskOnceAtTime
					.get(Calendar.HOUR_OF_DAY));
			tempBase.set(Calendar.MINUTE, runTaskOnceAtTime
					.get(Calendar.MINUTE));
			tempBase.set(Calendar.SECOND, 0);
			if (tempBase.compareTo(now) <= 0) {
				addOneBigPeriod(tempBase);
			}
			break;
		}
		if (tempBase.compareTo(base) == 0) {

		}
		setNextTaskRunDate(tempBase);
	}

	/**
	 * Returns <b>true</b> if you should increase big period. In this case time
	 * will be already set to start values. Returns <b>false</b> if time is
	 * within the start and stop interval
	 * 
	 * @param time
	 * @return
	 */
	private boolean addOneSmallPeriod(GregorianCalendar time) {
		int addType = 0;
		switch (dailyRunType) {
		case 2: // every hour
			addType = Calendar.HOUR_OF_DAY;
			break;
		case 1: // every minute
		default:
			addType = Calendar.MINUTE;
			break;
		}
		time.add(addType, 1 * dailyRepeatTaskEveryMinuteOrHour);
		GregorianCalendar tempTime = (GregorianCalendar) time.clone();

		// first check if calculated time is greater or equal startTaskAtTime
		tempTime.set(Calendar.HOUR_OF_DAY, startTaskAtTime
				.get(Calendar.HOUR_OF_DAY));
		tempTime.set(Calendar.MINUTE, startTaskAtTime.get(Calendar.MINUTE));
		if (time.compareTo(tempTime) < 0) {
			time.set(Calendar.HOUR_OF_DAY, startTaskAtTime
					.get(Calendar.HOUR_OF_DAY));
			time.set(Calendar.MINUTE, startTaskAtTime.get(Calendar.MINUTE));
			return false;
		}

		tempTime.set(Calendar.HOUR_OF_DAY, stopTaskAtTime
				.get(Calendar.HOUR_OF_DAY));
		tempTime.set(Calendar.MINUTE, stopTaskAtTime.get(Calendar.MINUTE));

		if (tempTime.compareTo(time) < 0) {
			time.set(Calendar.HOUR_OF_DAY, startTaskAtTime
					.get(Calendar.HOUR_OF_DAY));
			time.set(Calendar.MINUTE, startTaskAtTime.get(Calendar.MINUTE));

			return true;
		} else {
			return false;
		}
	}

	private boolean necessarilyToAddWeek(GregorianCalendar date) {
		// given date plus 1 becomes MONDAY
		int dayOfWeek = date.get(Calendar.DAY_OF_WEEK) + 1;

		if (dayOfWeek == Calendar.MONDAY) {
			// given date plus 1 becomes MONDAY so we have to add one(more)
			// week(s)
			return true;
		}
		if (dayOfWeek == 8) {
			// given date plus 1 becomes SUNDAY
			dayOfWeek = Calendar.SUNDAY;
			// if SUNDAY is not selected so we have to add one(more) week(s)
			return !getWeeklyRunAtDayExtended(dayOfWeek);
		}
		// given date plus 1 becomes a day between TUESDAY and FRIDAY
		for (int i = dayOfWeek; i < 7; i++) {
			if (getWeeklyRunAtDayExtended(i)) {
				return false;
			}
		}
		return true;
	}

	private void addOneBigPeriod(GregorianCalendar date) {
		GregorianCalendar tempDate = null;
		switch (recurringType) {
		case 3:
			date.add(Calendar.MONTH, 1 * repeatTaskEveryDWM);
			tempDate = (GregorianCalendar) date.clone();
			switch (monthlyRunType) {
			case 2:
				int maximumWeekNumber = date
						.getActualMaximum(Calendar.WEEK_OF_MONTH);
				int minimumWeekNumber = date
						.getActualMinimum(Calendar.WEEK_OF_MONTH);
				if (getMonthlyNumberOfDayOfWeek() == maximumWeekNumber) {
					// set week to last week of the month
					tempDate.set(Calendar.WEEK_OF_MONTH, maximumWeekNumber);
					tempDate.set(Calendar.DAY_OF_WEEK,
							getMonthlyRunAtDayOfWeek());
					if (tempDate.get(Calendar.MONTH) > date.get(Calendar.MONTH)) {
						date.set(Calendar.WEEK_OF_MONTH, maximumWeekNumber - 1);
					}
				} else if (getMonthlyNumberOfDayOfWeek() == minimumWeekNumber) {
					tempDate.set(Calendar.WEEK_OF_MONTH, minimumWeekNumber);
					tempDate.set(Calendar.DAY_OF_WEEK,
							getMonthlyRunAtDayOfWeek());
					if (tempDate.get(Calendar.MONTH) < date.get(Calendar.MONTH)) {
						date.set(Calendar.WEEK_OF_MONTH, minimumWeekNumber + 1);
					}
				} else {
					date.set(Calendar.WEEK_OF_MONTH,
							getMonthlyNumberOfDayOfWeek());
				}
				date.set(Calendar.DAY_OF_WEEK, getMonthlyRunAtDayOfWeek());
				break;
			case 1:
			default:
				// TODO check how it will process data on 28 February
				// month
				tempDate
						.set(Calendar.DAY_OF_MONTH, getMonthlyRunAtDayOfMonth());
				if (tempDate.get(Calendar.MONTH) > date.get(Calendar.MONTH)) {
					// set last day of the month
					date.set(Calendar.DAY_OF_MONTH, date
							.getActualMaximum(Calendar.DAY_OF_MONTH));
				} else {
					date
							.set(Calendar.DAY_OF_MONTH,
									getMonthlyRunAtDayOfMonth());
				}
				break;
			}
			break;

		case 2:
			if (necessarilyToAddWeek(date)) {
				date.add(Calendar.WEEK_OF_MONTH, 1 * repeatTaskEveryDWM);
				for (int i = 0; i < 7; i++) {
					if (getWeeklyRunAtDayExtended(i + 1)) {
						date.set(Calendar.DAY_OF_WEEK, getDayOfWeekByIndex(i));
						break;
					}
				}
			} else {
				for (int i = 0; i < 7; i++) {
					date.add(Calendar.DAY_OF_WEEK, 1);
					if (getWeeklyRunAtDayExtended(date
							.get(Calendar.DAY_OF_WEEK))) {
						break;
					}
				}
			}
			break;

		case 1:
		default:
			date.add(Calendar.DAY_OF_MONTH, 1 * repeatTaskEveryDWM);
			break;
		}
	}

	private boolean isAtLeastOneDayOfWeekSelected() {
		for (int i = 0; i < 7; i++) {
			if (weeklyRunAtDays[i]) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void run() {
		setTaskRunnig(true);
		System.out.println("Work starting");
		Runnable rn = new Runnable();
		ApplicationData.COUNT_INIT_ERROR = 0;
		ReadWriteXMLDataUi rwXMLdata = new ReadWriteXMLDataUi();
		try {
			rwXMLdata.readXMLData(pathToDatamap, ApplicationData.getProject());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ReadWriteXMLData.loadDateAndTimeSettings(filePath);
		if (ApplicationData.COUNT_INIT_ERROR > 0) {
			setTaskEnabled(false);
			JOptionPane
					.showMessageDialog(
							null,
							"An error occured while opening the DataMap: Uninitialized "
									+ "properties were found.\nPlease, check node(-s) configuration.");
		} else {
			ApplicationData.clearLogsBeforeRun();
			rn.Run(ApplicationData.getProject().getNodes().values(), null,
					new ProcessingProgressActions());
			System.out.println("Work completed");
		}
		setTaskRuns(taskRuns + 1);
		setTaskLastRunDate(getNextTaskRunDate());
		try {
			calculateNextTaskRunDate(true);
		} catch (ApatarSchedulingWeeklyException e) {
			System.out.println(e.getMessage());
		}
		setTaskRunnig(false);
	}

	public String getSchedulingName() {
		return schedulingName;
	}

	public String getDate() {
		if (null == getNextTaskRunDate()) {
			return "never";
		} else {
			return getNextTaskRunDate().getTime().toString();
		}
		// return calendar.getTime().toString();
	}

	/**
	 * @return the reccuringType
	 */
	public int getReccuringType() {
		return recurringType;
	}

	public String getRecurringType(boolean getAsString) {
		switch (recurringType) {
		case 2:
			return "Weekly";

		case 3:
			return "Monthly";

		case 1:
		default:
			return "Daily";
		}
	}

	/**
	 * @param recurringType
	 *            the reccuringType to set
	 */
	public void setRecurringType(int recurringType) {
		if (recurringType >= 1 && recurringType <= 3) {
			this.recurringType = recurringType;
		} else {
			this.recurringType = 1;
			throw new IllegalArgumentException(
					"recurringType value must be from 1 to 3");
		}
	}

	public void setRecurringType(String recurringType) {
		if (null == recurringType) {
			this.recurringType = 1;
			throw new IllegalArgumentException(
					"reccuringType cannot be null. value set to default.");
		}
		if ("monthly".equalsIgnoreCase(recurringType)) {
			this.recurringType = 3;
		} else if ("weekly".equalsIgnoreCase(recurringType)) {
			this.recurringType = 2;
		} else if ("daily".equalsIgnoreCase(recurringType)) {
			this.recurringType = 1;
		}
	}

	/**
	 * @return the weeklyRunAtDays
	 */
	public boolean[] getWeeklyRunAtDays() {
		return weeklyRunAtDays;
	}

	/**
	 * return an array element value assuming that sunday have index 1, monday -
	 * 2 etc.
	 * 
	 * @param dayOfWeek
	 * @return
	 */
	public boolean getWeeklyRunAtDayExtended(int dayOfWeek) {
		int index = (dayOfWeek == 1 ? 7 : dayOfWeek - 1);
		return getWeeklyRunAtDay(index);
	}

	/**
	 * This method takes as a parameter index value from 0 to 6 which means days
	 * from Monday(index=0) to Sunday(index=6). It returns GregorianCalendar
	 * DAY_OF_WEEK value of the corresponding day
	 * 
	 * @param index
	 * @return
	 */
	private int getDayOfWeekByIndex(int index) {
		if (index >= 0 && index <= 6) {
			if (index == 6) {
				return Calendar.SUNDAY;
			} else {
				return index + 2;
			}
		} else {
			throw new IllegalArgumentException("index must be from 0 to 6");
		}
	}

	public boolean getWeeklyRunAtDay(int dayOfWeek) {
		if (dayOfWeek >= 1 && dayOfWeek <= 7) {
			return weeklyRunAtDays[dayOfWeek - 1];
		} else {
			throw new IllegalArgumentException(
					"Day Of Week value must be from 1 to 7. given value was `"
							+ String.valueOf(dayOfWeek) + "`");
		}
	}

	/**
	 * @param weeklyRunAtDays
	 *            the weeklyRunAtDays to set
	 */
	public void setWeeklyRunAtDays(boolean[] weeklyRunAtDays) {
		this.weeklyRunAtDays = weeklyRunAtDays;
	}

	public void setWeeklyRunAtDay(int dayOfWeek, boolean value) {
		if (dayOfWeek >= 1 && dayOfWeek <= 7) {
			weeklyRunAtDays[dayOfWeek - 1] = value;
		} else {
			throw new IllegalArgumentException(
					"Day Of Week value must be from 1 to 7. given value was `"
							+ String.valueOf(dayOfWeek) + "`");
		}
	}

	/**
	 * @return the repeatTaskEveryDWM
	 */
	public int getRepeatTaskEveryDWM() {
		return repeatTaskEveryDWM;
	}

	/**
	 * @param repeatTaskEveryDWM
	 *            the repeatTaskEveryDWM to set
	 */
	public void setRepeatTaskEveryDWM(int repeatTaskEveryDWM,
			boolean onlyValidate) {
		if (repeatTaskEveryDWM > 0) {
			if (!onlyValidate) {
				setRepeatTaskEveryDWM(repeatTaskEveryDWM);
			}
		} else {
			this.repeatTaskEveryDWM = 1;
			throw new IllegalArgumentException(
					"repeatTaskEveryDWM must be greater than zero");
		}
	}

	private void setRepeatTaskEveryDWM(int repeatTaskEveryDWM) {
		this.repeatTaskEveryDWM = repeatTaskEveryDWM;
	}

	/**
	 * @return the taskEnabled
	 */
	public boolean isTaskEnabled() {
		return taskEnabled;
	}

	/**
	 * @param taskEnabled
	 *            the taskEnabled to set
	 */
	public void setTaskEnabled(boolean taskEnabled) {
		this.taskEnabled = taskEnabled;
		if (actions != null) {
			actions.schedulingStatusChanged();
		}
	}

	/**
	 * @return the taskRunnig
	 */
	public boolean isTaskRunnig() {
		return taskRunnig;
	}

	/**
	 * @param taskRunnig
	 *            the taskRunnig to set
	 */
	public void StopTask() {
		setTaskRunnig(false);
		interrupt();
	}

	/**
	 * @return the repeatTaskEveryDWMtype
	 */
	public int getRepeatTaskEveryDWMtype() {
		return repeatTaskEveryDWMtype;
	}

	/**
	 * value 1 or 2
	 * 
	 * @param repeatTaskEveryDWMtype
	 *            the repeatTaskEveryDWMtype to set
	 */
	public void setRepeatTaskEveryDWMtype(int repeatTaskEveryDWMtype,
			boolean onlyValidate) {
		if (repeatTaskEveryDWMtype == 1 || repeatTaskEveryDWMtype == 2) {
			if (!onlyValidate) {
				setRepeatTaskEveryDWMtype(repeatTaskEveryDWMtype);
			}
		} else {
			this.repeatTaskEveryDWMtype = 1;
			throw new IllegalArgumentException(
					"repeatTaskEveryDWMtype must be 1 or 2. given value was `"
							+ String.valueOf(repeatTaskEveryDWMtype) + "`");
		}
	}

	private void setRepeatTaskEveryDWMtype(int repeatTaskEveryDWMtype) {
		this.repeatTaskEveryDWMtype = repeatTaskEveryDWMtype;
	}

	/**
	 * @return the runTaskOnceAtTime
	 */
	public GregorianCalendar getRunTaskOnceAtTime() {
		return runTaskOnceAtTime;
	}

	/**
	 * @param runTaskOnceAtTime
	 *            the runTaskOnceAtTime to set
	 */
	public void setRunTaskOnceAtTime(GregorianCalendar runTaskOnceAtTime) {
		this.runTaskOnceAtTime = runTaskOnceAtTime;
		this.runTaskOnceAtTime.set(Calendar.YEAR, 0);
		this.runTaskOnceAtTime.set(GregorianCalendar.MONTH, 0);
		this.runTaskOnceAtTime.set(GregorianCalendar.DAY_OF_MONTH, 0);
		this.runTaskOnceAtTime.set(GregorianCalendar.MILLISECOND, 0);
	}

	/**
	 * Creates new item
	 */
	public void setRunTaskOnceAtTime() {
		runTaskOnceAtTime = new GregorianCalendar();
		runTaskOnceAtTime.set(GregorianCalendar.YEAR, 0);
		runTaskOnceAtTime.set(GregorianCalendar.MONTH, 0);
		runTaskOnceAtTime.set(GregorianCalendar.DAY_OF_MONTH, 0);
		runTaskOnceAtTime.set(GregorianCalendar.MILLISECOND, 0);
	}

	/**
	 * @return the startTaskAtTime
	 */
	public GregorianCalendar getStartTaskAtTime() {
		return startTaskAtTime;
	}

	/**
	 * @param startTaskAtTime
	 *            the startTaskAtTime to set
	 */
	public void setStartTaskAtTime(GregorianCalendar startTaskAtTime) {
		this.startTaskAtTime = startTaskAtTime;
		this.startTaskAtTime.set(GregorianCalendar.YEAR, 0);
		this.startTaskAtTime.set(GregorianCalendar.MONTH, 0);
		this.startTaskAtTime.set(GregorianCalendar.DAY_OF_MONTH, 0);
		this.startTaskAtTime.set(GregorianCalendar.MILLISECOND, 0);
	}

	public void setStartTaskAtTime() {
		startTaskAtTime = new GregorianCalendar();
		startTaskAtTime.set(GregorianCalendar.YEAR, 0);
		startTaskAtTime.set(GregorianCalendar.MONTH, 0);
		startTaskAtTime.set(GregorianCalendar.DAY_OF_MONTH, 0);
		startTaskAtTime.set(GregorianCalendar.MILLISECOND, 0);
	}

	/**
	 * @return the stopTaskAtTime
	 */
	public GregorianCalendar getStopTaskAtTime() {
		return stopTaskAtTime;
	}

	/**
	 * @param stopTaskAtTime
	 *            the stopTaskAtTime to set
	 */
	public void setStopTaskAtTime(GregorianCalendar stopTaskAtTime) {
		this.stopTaskAtTime = stopTaskAtTime;
		this.stopTaskAtTime.set(GregorianCalendar.YEAR, 0);
		this.stopTaskAtTime.set(GregorianCalendar.MONTH, 0);
		this.stopTaskAtTime.set(GregorianCalendar.DAY_OF_MONTH, 0);
		this.stopTaskAtTime.set(GregorianCalendar.MILLISECOND, 0);
		this.stopTaskAtTime.set(GregorianCalendar.SECOND, 0);
	}

	public void setStopTaskAtTime() {
		stopTaskAtTime = new GregorianCalendar();
		stopTaskAtTime.set(GregorianCalendar.YEAR, 0);
		stopTaskAtTime.set(GregorianCalendar.MONTH, 0);
		stopTaskAtTime.set(GregorianCalendar.DAY_OF_MONTH, 0);
		stopTaskAtTime.set(GregorianCalendar.MILLISECOND, 0);
		stopTaskAtTime.set(GregorianCalendar.SECOND, 0);
	}

	/**
	 * @return the startTaskAtDate
	 */
	public GregorianCalendar getStartTaskAtDate() {
		return startTaskAtDate;
	}

	/**
	 * @param startTaskAtDate
	 *            the startTaskAtDate to set
	 */
	public void setStartTaskAtDate(GregorianCalendar startTaskAtDate) {
		this.startTaskAtDate = startTaskAtDate;
		this.startTaskAtDate.set(GregorianCalendar.HOUR_OF_DAY, 0);
		this.startTaskAtDate.set(GregorianCalendar.MINUTE, 0);
		this.startTaskAtDate.set(GregorianCalendar.SECOND, 0);
		this.startTaskAtDate.set(GregorianCalendar.MILLISECOND, 0);
	}

	public void setStartTaskAtDate() {
		startTaskAtDate = new GregorianCalendar();
		startTaskAtDate.set(GregorianCalendar.HOUR_OF_DAY, 0);
		startTaskAtDate.set(GregorianCalendar.MINUTE, 0);
		startTaskAtDate.set(GregorianCalendar.SECOND, 0);
		startTaskAtDate.set(GregorianCalendar.MILLISECOND, 0);
	}

	/**
	 * @return the stopTaskAtDate
	 */
	public GregorianCalendar getStopTaskAtDate() {
		return stopTaskAtDate;
	}

	/**
	 * @param stopTaskAtDate
	 *            the stopTaskAtDate to set
	 */
	public void setStopTaskAtDate(GregorianCalendar stopTaskAtDate) {
		this.stopTaskAtDate = stopTaskAtDate;
		if (null != stopTaskAtDate) {
			this.stopTaskAtDate.set(GregorianCalendar.HOUR, 0);
			this.stopTaskAtDate.set(GregorianCalendar.MINUTE, 0);
			this.stopTaskAtDate.set(GregorianCalendar.SECOND, 0);
			this.stopTaskAtDate.set(GregorianCalendar.MILLISECOND, 0);
		}
	}

	public void setStopTaskAtDate() {
		stopTaskAtDate = new GregorianCalendar();
		stopTaskAtDate.set(GregorianCalendar.HOUR, 0);
		stopTaskAtDate.set(GregorianCalendar.MINUTE, 0);
		stopTaskAtDate.set(GregorianCalendar.SECOND, 0);
		stopTaskAtDate.set(GregorianCalendar.MILLISECOND, 0);
	}

	/**
	 * @return the numberTaskRuns
	 */
	public int getTotalNumberTaskRuns() {
		return totalNumberTaskRuns;
	}

	public void setTotalNumberTaskRuns(int totalNumberTaskRuns,
			boolean onlyValidate) {
		if (totalNumberTaskRuns < 0) {
			throw new IllegalArgumentException(
					"totalNumberTaskRuns must be zero or greater");
		}
		if (!onlyValidate) {
			setTotalNumberTaskRuns(totalNumberTaskRuns);
		}
	}

	/**
	 * @param totalNumberTaskRuns
	 *            the numberTaskRuns to set
	 */
	private void setTotalNumberTaskRuns(int totalNumberTaskRuns) {
		this.totalNumberTaskRuns = totalNumberTaskRuns;
	}

	/**
	 * @return the monthlyRunType
	 */
	public int getMonthlyRunType() {
		return monthlyRunType;
	}

	/**
	 * @param monthlyRunType
	 *            the monthlyRunType to set
	 */
	public void setMonthlyRunType(int monthlyRunType) {
		if (monthlyRunType == 1 || monthlyRunType == 2) {
			this.monthlyRunType = monthlyRunType;
		} else {
			throw new IllegalArgumentException(
					"monthlyRunType must be 1 or 2 only");
		}
	}

	/**
	 * @return the monthlyRunAtDayOfMonth
	 */
	public int getMonthlyRunAtDayOfMonth() {
		return monthlyRunAtDayOfMonth;
	}

	/**
	 * value from 1 to 31
	 * 
	 * @param monthlyRunAtDayOfMonth
	 *            the monthlyRunAtDayOfMonth to set
	 */
	public void setMonthlyRunAtDayOfMonth(int monthlyRunAtDayOfMonth,
			boolean onlyValidate) {
		if (monthlyRunAtDayOfMonth >= 1 && monthlyRunAtDayOfMonth <= 31) {
			if (!onlyValidate) {
				setMonthlyRunAtDayOfMonth(monthlyRunAtDayOfMonth);
			}
		} else {
			this.monthlyRunAtDayOfMonth = 1;
			throw new IllegalArgumentException(
					"monthlyRunAtDayOfMonth must be from 1 to 31. given value was `"
							+ String.valueOf(monthlyRunAtDayOfMonth) + "`");
		}
	}

	private void setMonthlyRunAtDayOfMonth(int monthlyRunAtDayOfMonth) {
		this.monthlyRunAtDayOfMonth = monthlyRunAtDayOfMonth;
	}

	/**
	 * @return the monthlyNumberOfDayOfWeek
	 */
	public int getMonthlyNumberOfDayOfWeek() {
		return monthlyNumberOfDayOfWeek;
	}

	/**
	 * @param monthlyNumberOfDayOfWeek
	 *            the monthlyNumberOfDayOfWeek to set
	 */
	public void setMonthlyNumberOfDayOfWeek(int monthlyNumberOfDayOfWeek) {
		if (monthlyNumberOfDayOfWeek >= 1 && monthlyNumberOfDayOfWeek <= 5) {
			this.monthlyNumberOfDayOfWeek = monthlyNumberOfDayOfWeek;
		} else {
			this.monthlyNumberOfDayOfWeek = 1;
			throw new IllegalArgumentException(
					"monthlyNumberOfDayOfWeek value must be from 1 to 5. given value was `"
							+ String.valueOf(monthlyNumberOfDayOfWeek) + "`");
		}
	}

	/**
	 * @return the monthlyRunAtDayOfWeek
	 */
	public int getMonthlyRunAtDayOfWeek() {
		return monthlyRunAtDayOfWeek;
	}

	/**
	 * value from 1 to 7 (from Monday to Sunday)
	 * 
	 * @param monthlyRunAtDayOfWeek
	 *            the monthlyRunAtDayOfWeek to set
	 */
	public void setMonthlyRunAtDayOfWeek(int monthlyRunAtDayOfWeek) {
		if (monthlyRunAtDayOfWeek >= 1 && monthlyRunAtDayOfWeek <= 7) {
			this.monthlyRunAtDayOfWeek = monthlyRunAtDayOfWeek;
		} else {
			this.monthlyRunAtDayOfWeek = 1;
			throw new IllegalArgumentException(
					"monthlyRunAtDayOfWeek value must be from 1 to 7 (from Monday to Sunday). given value was `"
							+ String.valueOf(monthlyRunAtDayOfWeek) + "`");
		}
	}

	/**
	 * @param taskRunnig
	 *            the taskRunnig to set
	 */
	private void setTaskRunnig(boolean taskRunnig) {
		this.taskRunnig = taskRunnig;
		if (actions != null) {
			actions.schedulingStatusChanged();
		}
	}

	public int getDailyRunType() {
		return dailyRunType;
	}

	public void setDailyRunType(int dailyRunType) {
		if (dailyRunType == 1 || dailyRunType == 2) {
			this.dailyRunType = dailyRunType;
		} else {
			this.dailyRunType = 1;
			throw new IllegalArgumentException(
					"dailyRunType value must be 1 or 2. given value was `"
							+ String.valueOf(dailyRunType) + "`");
		}
	}

	/**
	 * @return the dailyRepeatTaskEveryMinuteOrHour
	 */
	public int getDailyRepeatTaskEveryMinuteOrHour() {
		return dailyRepeatTaskEveryMinuteOrHour;
	}

	/**
	 * @param dailyRepeatTaskEveryMinuteOrHour
	 *            the dailyRepeatTaskEveryMinuteOrHour to set
	 */
	public void setDailyRepeatTaskEveryMinuteOrHour(
			int dailyRepeatTaskEveryMinuteOrHour, boolean onlyValidate) {
		if (dailyRepeatTaskEveryMinuteOrHour > 0) {
			if (!onlyValidate) {
				setDailyRepeatTaskEveryMinuteOrHour(dailyRepeatTaskEveryMinuteOrHour);
			}
		} else {
			this.dailyRepeatTaskEveryMinuteOrHour = 1;
			throw new IllegalArgumentException(
					"dailyRepeatTaskEveryMinuteOrHour must be greater than zero");
		}
	}

	private void setDailyRepeatTaskEveryMinuteOrHour(
			int dailyRepeatTaskEveryMinuteOrHour) {
		this.dailyRepeatTaskEveryMinuteOrHour = dailyRepeatTaskEveryMinuteOrHour;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setSchedulingName(String name) {
		schedulingName = name;
	}

	/**
	 * @return the pathToDatamap
	 */
	public String getPathToDatamap() {
		return pathToDatamap;
	}

	/**
	 * @param pathToDatamap
	 *            the pathToDatamap to set
	 */
	public void setPathToDatamap(String pathToDatamap) {
		this.pathToDatamap = pathToDatamap;
	}

	/**
	 * @return the taskLastRunDate
	 */
	public GregorianCalendar getTaskLastRunDate() {
		return taskLastRunDate;
	}

	/**
	 * @param taskLastRunDate
	 *            the taskLastRunDate to set
	 */
	private void setTaskLastRunDate(GregorianCalendar taskLastRunDate) {
		this.taskLastRunDate = taskLastRunDate;
	}

	private void setTaskLastRunDate(long timeInMilliseconds) {
		if (timeInMilliseconds < 1) {
			throw new IllegalArgumentException(
					"timeInMilliseconds must be greater than zero");
		}
		if (null == taskLastRunDate) {
			taskLastRunDate = new GregorianCalendar();
		}
		taskLastRunDate.setTimeInMillis(timeInMilliseconds);
	}

	/**
	 * @return the taskRuns
	 */
	public int getTaskRuns() {
		return taskRuns;
	}

	/**
	 * @param taskRuns
	 *            the taskRuns to set
	 */
	private void setTaskRuns(int taskRuns) {
		if (taskRuns < 0) {
			this.taskRuns = 0;
			throw new IllegalArgumentException(
					"taskRuns must be greater than zero");
		}
		this.taskRuns = taskRuns;
	}

	/**
	 * @return the nextTaskRunDate
	 */
	public GregorianCalendar getNextTaskRunDate() {
		return nextTaskRunDate;
	}

	/**
	 * @return the nextTaskRunDate
	 * @throws ApatarSchedulingWeeklyException
	 */
	public GregorianCalendar getNextTaskRunDate(boolean calculateNew)
			throws ApatarSchedulingWeeklyException {
		if (calculateNew) {
			calculateNextTaskRunDate(false);
		}
		return nextTaskRunDate;
	}

	/**
	 * @param nextTaskRunDate
	 *            the nextTaskRunDate to set
	 */
	private void setNextTaskRunDate(GregorianCalendar nextTaskRunDate) {
		this.nextTaskRunDate = nextTaskRunDate;
	}

	/**
	 * After calling method <b>getLastWarningMessage()</b> value of the
	 * lastWarningMessage becomes null
	 * 
	 * @return the lastWarningMessage
	 */
	public String getLastWarningMessage() {
		if (null != lastWarningMessage) {
			String message = lastWarningMessage.substring(0);
			lastWarningMessage = null;
			return message;
		} else {
			return null;
		}
	}
}
