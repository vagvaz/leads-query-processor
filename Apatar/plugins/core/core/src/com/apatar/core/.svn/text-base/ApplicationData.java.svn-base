/*TODO refactoring
 * метод getNumericVersion() удалён как неиспользуемый
 * *********************
 */
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
//http://help.sap.com/javadocs/NW04/current/bi/docs/connectors.html
//http://help.sap.com/javadocs/NW04/current/bi/docs/connectors/jdbc_howto.html
//http://maxdb.sap.com/currentdoc/43/313c13198d0d26e10000000a1553f7/content.htm
package com.apatar.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import com.apatar.ui.NodeFactory;

public class ApplicationData {
	// this block contains global settings for application
	public static Boolean DebugAutoFill = false;
	public static Boolean SalesForceAppExchange = true;
	public static Boolean SugarcrmAppExchange = true;
	public static Boolean FlickrAppExchange = true;

	public static ApatarHttpClient httpClient;
	public static boolean isClearLogsBeforeRun = false;

	public static boolean isApplication = true;

	public static boolean withoutUI = false;

	public static TempDataBase tempDataBase;

	public static String PROJECT_PATH;
	public static boolean IMMEDIATE_RUN = false;
	public static boolean CLOSE_AFTER_FINISH = false;
	public static boolean SILENT_RUN = false;
	private static Project project = new Project();

	/**
	 * we assume that: Major: can have any positive value Minor and Revision:
	 * cannot be less than zero and greater than 999
	 */
	public static final String VERSION = "Apatar_v1.12.12";
	public static String DATAMAP_VERSION = VERSION;

	public static boolean DEBUG = false;

	public static String REPOSITORIES = null;

	private static Properties helpProperties;

	public static int COUNT_INIT_ERROR = 0;

	public final static int NOEDITED_STATUS = 0;
	public final static int EDITED_STATUS = 1;
	public final static int SAVED_STATUS = 2;
	public static int STATUS_APPLICATION = NOEDITED_STATUS;

	public static DateAndTimeSettings APLICATION_DATE_SETTINGS = new DateAndTimeSettings();
	public static DateAndTimeSettings DATAMAP_DATE_SETTINGS = new DateAndTimeSettings();

	public static boolean isSchedulerRunning = false;

	public static Project getProject() {
		return project;
	}

	public static void clearLogsBeforeRun() {
		if (isClearLogsBeforeRun) {
			if (!ApplicationData.DEBUG) {
				clearLogs();
			}
			System.out.println("Logs cleared.");
		}
	}

	public static void clearLogs() {
		String pathPrj = (ApplicationData.REPOSITORIES == null ? ""
				: ApplicationData.REPOSITORIES);
		ApplicationData.REPOSITORIES = new String(pathPrj);

		String logFileSuffix = "";
		if (isSchedulerRunning) {
			logFileSuffix = "2";
		}
		File fOut = new File(pathPrj + "debug_output" + logFileSuffix + ".txt");
		File fErr = new File(pathPrj + "error_output" + logFileSuffix + ".txt");
		try {
			PrintStream printStream = new PrintStream(new BufferedOutputStream(
					new FileOutputStream(fOut)), true);
			System.setOut(null);
			fOut.createNewFile();
			System.setOut(printStream);
			CoreUtils.printInfoToConsol(CoreUtils.DEBUG_INFO);

			printStream = new PrintStream(new BufferedOutputStream(
					new FileOutputStream(fErr)), true);
			System.setErr(null);
			fErr.createNewFile();
			System.setErr(printStream);
			CoreUtils.printInfoToConsol(CoreUtils.ERROR_INFO);

		} catch (Exception e) {
			System.err.println("Error clearing log files");
			e.printStackTrace();
		}
	}

	public static TempDataBase getTempDataBase() {
		return tempDataBase;
	}

	public static JdbcParams getTempJDBC() {
		return tempDataBase.getJdbcParams();
	}

	public static Connection getTempJDBCConnection() {
		try {
			return tempDataBase.getJdbcParams().getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static DataBaseInfo getTempDataBaseInfo() {
		return tempDataBase.getDataBaseInfo();
	}

	protected static Collection<NodeFactory> nodes;

	public static Collection<NodeFactory> getNodeFactoryCollection() {
		return nodes;
	}

	public static Collection<NodeFactory> setNodeFactoryCollection(
			Collection<NodeFactory> value) {
		return nodes = value;
	}

	// this is list of class loaders
	private static ArrayList<ClassLoader> loaders = new ArrayList<ClassLoader>();

	public static void addLoader(ClassLoader loader) {
		loaders.add(loader);
	}

	// create object by type
	public static Object CreateObject(String className) {
		for (ClassLoader classLoader : loaders) {
			ClassLoader loader = classLoader;
			Class<?> vs = null;
			try {
				vs = loader.loadClass(className);
			} catch (ClassNotFoundException e) {
				continue;
			}
			try {
				return vs.newInstance();
			} catch (Exception e) {
				continue;
			}
		}
		return null;
	}

	public static Class<?> classForName(String className)
			throws ClassNotFoundException {
		for (ClassLoader classLoader : loaders) {
			ClassLoader loader = classLoader;
			try {
				loader.loadClass(className);
			} catch (ClassNotFoundException e) {
				continue;
			}

			return Class.forName(className, true, loader);
		}
		return null;
	}

	// create object by type
	public static Object CreateBinInfoObject(String className, Class<?> cls) {
		for (ClassLoader classLoader : loaders) {
			ClassLoader loader = classLoader;
			Class<?> vs = null;

			try {
				vs = loader.loadClass(className);
			} catch (ClassNotFoundException e) {
				continue;
			}
			try {
				return vs.getConstructor(new Class[] { Class.class })
						.newInstance(new Object[] { cls });
			} catch (Exception e) {
				continue;
			}
		}
		return null;
	}

	public final static List<Record> convertToTempDbType(List<Record> recs) {
		List<Record> result = new ArrayList<Record>();
		for (Record rec : recs) {
			DBTypeRecord orig = DataConversionAlgorithm.bestRecordLookup(
					ApplicationData.getTempDataBase().getDataBaseInfo(), rec);
			rec.setType(orig.getType());
			rec.setOriginalType(orig.getOriginalType());
			rec.setLengthMin(orig.getLengthMin());
			rec.setSqlType(orig.getSqlType());
			result.add(rec/*
						 * new Record(orig, rec.getFieldName(), rec.getLength(),
						 * rec.isNullable(), rec.isSigned(), rec.isPrimaryKey())
						 */);
		}
		return result;
	}

	public static IProcessingProgress ProcessingProgress = null;

	public static File createFile(String name, byte[] bytes) throws IOException {
		InputStream is = new BufferedInputStream(
				new ByteArrayInputStream(bytes));

		if (is != null) {
			File file = createFile(new File(name), is);
			is.close();
			return file;
		}
		return null;
	}

	public static File createFile(File newFile, InputStream is)
			throws IOException {
		int count = 4 * 1024 * 1024;
		byte[] buff = new byte[count];

		if (is != null) {

			BufferedInputStream bis = new BufferedInputStream(is);

			FileOutputStream out = new FileOutputStream(newFile);

			int offset = 0;

			int total = count;

			while (total >= count) {
				total = bis.read(buff);
				if (total > -1) {
					out.write(buff, 0, total);
				}
				offset += count;
			}
			out.close();
			bis.close();

			return newFile;
		}
		return null;
	}

	public static File createTempFile(InputStream is, String prefix)
			throws IOException {
		File tempDir = new File("tempfiles");
		if (!tempDir.exists()) {
			tempDir.mkdir();
		}

		File newFile = File.createTempFile(prefix, null, tempDir);
		int count = 8 * 1024 * 1024;
		byte[] buff = new byte[count];

		if (is != null) {

			FileOutputStream out = new FileOutputStream(newFile);

			int offset = 0;

			int total = count;

			while (total >= count) {
				total = is.read(buff, 0, count);
				if (total > -1) {
					out.write(buff, 0, total);
				}
				offset += count;
			}
			out.close();

			return newFile;
		}
		return null;
	}

	public static String getGadgetHelpProperty(String key) {
		if (helpProperties == null) {
			helpProperties = new Properties();
			try {
				helpProperties
						.load(new FileInputStream("gadgethelp.properties"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return helpProperties.getProperty(key);
	}

	public static String parseLinkLabel(String linkLabel)
			throws ApatarException {
		if (CoreUtils.validUrl(linkLabel)) {
			return linkLabel;
		}
		List<String> res = ApatarRegExp.getSubstrings(
				".*href=[\"'](.+?)[\"'].*", linkLabel);

		return res.get(0);
	}

	public static int getVersion(int whichNumber) throws Exception {
		return getVersion(ApplicationData.VERSION, whichNumber);
	}

	public static int getVersion(String version, int whichNumber)
			throws Exception {
		if (whichNumber < 1 || whichNumber > 3) {
			throw new ApatarException(
					"whichNumber must be equal to 1 or 2 or 3 only");
		}
		return Integer.parseInt(ApatarRegExp.getSubstrings(
				".*?_v([\\d]+)\\.([\\d]+)\\.([\\d]+)", version, whichNumber));
	}

	public static int getVersionMajor() throws Exception {
		return getVersion(1);
	}

	public static int getVersionMajor(String version) throws Exception {
		return getVersion(version, 1);
	}

	public static int getVersionMinor() throws Exception {
		return getVersion(2);
	}

	public static int getVersionMinor(String version) throws Exception {
		return getVersion(version, 2);
	}

	public static int getVersionRevision() throws Exception {
		return getVersion(3);
	}

	public static int getVersionRevision(String version) throws Exception {
		return getVersion(version, 3);
	}

	public static int CompateTwoVerions(int major_1, int minor_1,
			int revision_1, int major_2, int minor_2, int revision_2) {
		int version_1 = major_1 * 1000000 + minor_1 * 1000 + revision_1;
		int version_2 = major_2 * 1000000 + minor_2 * 1000 + revision_2;

		if (version_1 == version_2) {
			return 0;
		} else if (version_1 > version_2) {
			return 1;
		} else {
			return -1;
		}
	}

	public static int CompateTwoVerions(String version_1, String version_2)
			throws Exception {
		return CompateTwoVerions(getVersionMajor(version_1),
				getVersionMinor(version_1), getVersionRevision(version_1),
				getVersionMajor(version_2), getVersionMinor(version_2),
				getVersionRevision(version_2));
	}

	public static int CompateToCurrentVerion(int major, int minor, int revision)
			throws Exception {
		return CompateTwoVerions(getVersionMajor(ApplicationData.VERSION),
				getVersionMinor(ApplicationData.VERSION),
				getVersionRevision(ApplicationData.VERSION), major, minor,
				revision);
	}

	public static int CompateToCurrentVerion(String version_2) throws Exception {
		return CompateTwoVerions(getVersionMajor(ApplicationData.VERSION),
				getVersionMinor(ApplicationData.VERSION),
				getVersionRevision(ApplicationData.VERSION),
				getVersionMajor(version_2), getVersionMinor(version_2),
				getVersionRevision(version_2));
	}
}
