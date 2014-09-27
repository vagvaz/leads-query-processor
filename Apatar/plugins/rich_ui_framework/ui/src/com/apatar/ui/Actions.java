/*TODO recorded refactoring
 * класс fileFilter убран как дублирующий код. Вместо него используется класс ApatarFileFilter
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

package com.apatar.ui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

import com.apatar.core.ApatarException;
import com.apatar.core.ApatarRegExp;
import com.apatar.core.ApplicationData;
import com.apatar.core.CoreUtils;
import com.apatar.core.Runnable;

public class Actions {

	private final JFrame frame;
	private final JWorkPane workPane;
	private JToolBar toolbar;
	private JMenuBar menubar;

	private Action newPrj, open, save, saveAs, publishToApatar, runScheduling,
			run, options, exit;

	// apon
	private Action newWebService;

	private Action windowsLookAndFeel, metalLookAndFeel, motifLookAndFeel;
	private Action debugOutput, errorOutput;
	private Action catalogOfApatars, demos, forums, wiki;
	private Action about, featureRequest, submitBug, tutorials;

	private static final JFileChooser fileChooser = new JFileChooser(
			System.getProperty("user.dir"));
	private static ApatarFileFilter FF = new ApatarFileFilter();

	private static List<RegisterRunnable> runnabelClasses = new ArrayList<RegisterRunnable>();

	private static String PUBLISH_TO_APATAR_URL = "http://www.apatarforge.org/index.php";

	public Actions(ApatarUiMain main, JFrame frame, JWorkPane workPane) {

		this.frame = frame;
		this.workPane = workPane;

		createActions();
		createToolbar();
		createMenubar();
	}

	public static void addToListenRun(RegisterRunnable node) {
		runnabelClasses.add(node);
	}

	private void createMenubar() {

		menubar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		fileMenu.add(newPrj);

		fileMenu.add(open);
		fileMenu.addSeparator();
		fileMenu.add(save);
		fileMenu.add(saveAs);
		fileMenu.addSeparator();
		// fileMenu.add(publishToApatar);@@
		// fileMenu.add(runScheduling);@@
		// fileMenu.addSeparator();@@
		fileMenu.add(run);
		// fileMenu.addSeparator();
		// apon
		// fileMenu.add(newWebService);@@
		// fileMenu.addSeparator();@@

		// fileMenu.add(options);@@
		// fileMenu.addSeparator();@@
		fileMenu.add(exit);

		// JMenu viewMenu = new JMenu("View");//@@
		// viewMenu.add(windowsLookAndFeel);//@@
		// viewMenu.add(metalLookAndFeel); //@@
		// viewMenu.add(motifLookAndFeel); //@@

		// JMenu debugMenu = new JMenu("Debug");//@@
		// debugMenu.add(debugOutput);//@@
		// debugMenu.add(errorOutput);//@@

		// JMenu communityMenu = new JMenu("Community");//@@
		// communityMenu.add(catalogOfApatars);//@@
		// communityMenu.add(demos);//@@
		// communityMenu.add(forums);//@@
		// communityMenu.add(wiki);//@@

		/*
		 * @@ JMenu helpMenu = new JMenu("Help"); helpMenu.add(about);
		 * helpMenu.addSeparator(); helpMenu.add(featureRequest);
		 * helpMenu.add(submitBug); helpMenu.add(tutorials);
		 */
		menubar.add(fileMenu);
		// menubar.add(viewMenu);//@@
		// menubar.add(debugMenu);//@@
		// menubar.add(communityMenu);//@@
		// menubar.add(helpMenu);//@@
	}

	private void createToolbar() {
		toolbar = new JToolBar();
	}

	@SuppressWarnings("serial")
	private void createActions() {

		// apon
		newWebService = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				JwsdlLocationDialog wsdlDiag = new JwsdlLocationDialog();
				wsdlDiag.setVisible(true);
			}
		};
		newWebService.putValue(Action.NAME, "New Web Service");
		newWebService.putValue(Action.SHORT_DESCRIPTION,
				"create a Web Service dynamic client");

		// new progect
		newPrj = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (!ApatarUiMain.saveProject()) {
						return;
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
				UiUtils.clearWorkPane(workPane);
				ApplicationData.PROJECT_PATH = null;
				ApplicationData.getProject().removeAllElements();
				ApplicationData.STATUS_APPLICATION = ApplicationData.SAVED_STATUS;
				ApplicationData.DATAMAP_DATE_SETTINGS
						.init(ApplicationData.APLICATION_DATE_SETTINGS);
				ApatarUiMain.MAIN_FRAME.setTitle(String.format(
						JApatarMainUIFrame.FRAME_TITLE, ""));
			}
		};
		newPrj.putValue(Action.NAME, "New");
		newPrj.putValue(Action.SHORT_DESCRIPTION, "New");

		// opening data
		open = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (!ApatarUiMain.saveProject()) {
						return;
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
				openProject(frame, workPane);
				ApplicationData.STATUS_APPLICATION = ApplicationData.SAVED_STATUS;
			}
		};
		open.putValue(Action.NAME, "Open");
		open.putValue(Action.SHORT_DESCRIPTION, "Open");

		save = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					saveProject();
					ApplicationData.STATUS_APPLICATION = ApplicationData.SAVED_STATUS;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		save.putValue(Action.NAME, "Save");
		save.putValue(Action.SHORT_DESCRIPTION, "Save");

		// saving As data
		saveAs = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					saveAs();
					ApplicationData.STATUS_APPLICATION = ApplicationData.SAVED_STATUS;
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		};
		saveAs.putValue(Action.NAME, "Save As");
		saveAs.putValue(Action.SHORT_DESCRIPTION, "Save As");

		publishToApatar = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					publishToApatar();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (HttpException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		publishToApatar.putValue(Action.NAME, "Publish to Apatar");
		publishToApatar.putValue(Action.SHORT_DESCRIPTION, "Publish to Apatar");

		runScheduling = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					String osName = System.getProperty("os.name");

					if (osName.contains("Windows")) {
						String pathPrj = (ApplicationData.REPOSITORIES == null ? ""
								: ApplicationData.REPOSITORIES);
						Runtime.getRuntime().exec(pathPrj + "scheduling.bat");
					} else {
						Runtime.getRuntime().exec("./scheduling.bat");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		runScheduling.putValue(Action.NAME, "Scheduling");
		runScheduling.putValue(Action.SHORT_DESCRIPTION, "Scheduling");

		// exit
		exit = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					ApatarUiMain.exit();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		exit.putValue(Action.NAME, "Exit");
		exit.putValue(Action.SHORT_DESCRIPTION, "Exit");

		options = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionsDialog dlg = new JOptionsDialog(ApatarUiMain.MAIN_FRAME);
				dlg.setVisible(true);
			}
		};
		options.putValue(Action.NAME, "Options");
		options.putValue(Action.SHORT_DESCRIPTION, "Options");

		windowsLookAndFeel = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			}
		};
		windowsLookAndFeel.putValue(Action.NAME, "Windows");
		windowsLookAndFeel.putValue(Action.SHORT_DESCRIPTION, "Windows");

		metalLookAndFeel = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			}
		};
		metalLookAndFeel.putValue(Action.NAME, "Metal");
		metalLookAndFeel.putValue(Action.SHORT_DESCRIPTION, "Metal");

		motifLookAndFeel = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			}
		};
		motifLookAndFeel.putValue(Action.NAME, "Motif");
		motifLookAndFeel.putValue(Action.SHORT_DESCRIPTION, "Motif");

		debugOutput = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				JErrorDebugDialog dlg = new JErrorDebugDialog(
						ApatarUiMain.MAIN_FRAME, true,
						JErrorDebugDialog.DEBUG_DIALOG);
				dlg.setVisible(true);
			}
		};
		debugOutput.putValue(Action.NAME, "Show Output");
		debugOutput
				.putValue(Action.SHORT_DESCRIPTION, "Show Debug Information");

		errorOutput = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				JErrorDebugDialog dlg = new JErrorDebugDialog(
						ApatarUiMain.MAIN_FRAME, true,
						JErrorDebugDialog.ERROR_DIALOG);
				dlg.setVisible(true);
			}
		};
		errorOutput.putValue(Action.NAME, "Show Error");
		errorOutput
				.putValue(Action.SHORT_DESCRIPTION, "Show Error Information");

		run = new AbstractAction() { // @@ TODO Send the aptr file to Lefteri
			public void actionPerformed(ActionEvent e) {
				System.out.println("Project Path"
						+ ApplicationData.PROJECT_PATH);
				System.out.println("Saved Status"
						+ ApplicationData.SAVED_STATUS);

				try {
					saveProject();
					final String dir = System.getProperty("user.dir");
					System.out.println("current dir = " + dir);
					String jarname = "resultsDisplayWindow.jar";
					File jar = new File(jarname);
					if (jar.exists()) {
						Process proc = Runtime.getRuntime().exec(
								"java -jar  " + jarname + " "
										+ ApplicationData.PROJECT_PATH);

						JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
								"Please Wait for the results");
					} else {
						JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
								"Unable to find executable: " + dir + "/"
										+ jarname);
					}

					// proc.waitFor();
					// System.out.println("Process Status: "+proc.exitValue());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				ApplicationData.STATUS_APPLICATION = ApplicationData.SAVED_STATUS;

				ApplicationData.clearLogsBeforeRun();

				// Runnable rn = new Runnable();
				// rn.Run(ApplicationData.getProject().getNodes().values(),
				// null,
				// new ProcessingProgressActions());
			}
		};

		run.putValue(Action.NAME, "Run");
		run.putValue(Action.SHORT_DESCRIPTION, "Run");

		submitBug = new AbstractAction() {

			public void actionPerformed(ActionEvent arg0) {
				JSubmitHelpDialog dlg = new JSubmitHelpDialog(
						ApatarUiMain.MAIN_FRAME, true);
				dlg.setVisible(true);
			}

		};
		submitBug.putValue(Action.NAME, "Submit Bug");

		featureRequest = new AbstractAction() {

			public void actionPerformed(ActionEvent arg0) {
				JFeatureRequestHelpDialog dlg = new JFeatureRequestHelpDialog(
						ApatarUiMain.MAIN_FRAME, true);
				dlg.setVisible(true);
			}

		};
		featureRequest.putValue(Action.NAME, "Feature Request");

		catalogOfApatars = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				Thread tr;
				try {
					tr = new OpenWebBrowser("http://www.apatarforge.org/");
					tr.start();
				} catch (ApatarException e) {
					e.printStackTrace();
				}

			}
		};
		catalogOfApatars.putValue(Action.NAME, "Catalog Of DataMaps");
		catalogOfApatars.putValue(Action.SHORT_DESCRIPTION,
				"Catalog Of Apatars");

		demos = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				Thread tr;
				try {
					tr = new OpenWebBrowser(
							"http://www.apatar.com/web_demo.html");
					tr.start();
				} catch (ApatarException e) {
					e.printStackTrace();
				}

			}
		};
		demos.putValue(Action.NAME, "Demos");
		demos.putValue(Action.SHORT_DESCRIPTION, "Demos");

		forums = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				Thread tr;
				try {
					tr = new OpenWebBrowser(
							"http://www.apatarforge.org/forums/");
					tr.start();
				} catch (ApatarException e) {
					e.printStackTrace();
				}

			}
		};
		forums.putValue(Action.NAME, "Forums");
		forums.putValue(Action.SHORT_DESCRIPTION, "Forums");

		wiki = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				Thread tr;
				try {
					tr = new OpenWebBrowser("http://www.apatarforge.org/wiki/");
					tr.start();
				} catch (ApatarException e) {
					e.printStackTrace();
				}
			}
		};
		wiki.putValue(Action.NAME, "Wiki");
		wiki.putValue(Action.SHORT_DESCRIPTION, "Wiki");

		tutorials = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				Thread tr;
				try {
					tr = new OpenWebBrowser(
							"http://www.apatar.com/community_documentation.html");
					tr.start();
				} catch (ApatarException e) {
					e.printStackTrace();
				}
			}
		};
		tutorials.putValue(Action.NAME, "Tutorials");
		tutorials.putValue(Action.SHORT_DESCRIPTION, "Tutorials");

		about = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				JAboutDialog dlg = new JAboutDialog(ApatarUiMain.MAIN_FRAME,
						true);
				dlg.setVisible(true);
			}
		};
		about.putValue(Action.NAME, "About");
		about.putValue(Action.SHORT_DESCRIPTION, "About");

	}

	private static File saveAs() throws IOException {
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileFilter(FF);

		int returnValue = fileChooser.showSaveDialog(ApatarUiMain.MAIN_FRAME);

		File fileSrc = null;

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			fileSrc = fileChooser.getSelectedFile();
			ReadWriteXMLDataUi rwXMLdata = new ReadWriteXMLDataUi();

			rwXMLdata.writeXMLData(fileSrc.toString(),
					ApplicationData.getProject(), false);
			ApplicationData.PROJECT_PATH = fileSrc.getPath();
		}
		ApatarUiMain.MAIN_FRAME.setTitle(String.format(
				JApatarMainUIFrame.FRAME_TITLE, fileSrc.getName() + " - "));
		return fileSrc;
	}

	private static void save(String path) throws IOException {
		ReadWriteXMLDataUi rwXMLdata = new ReadWriteXMLDataUi();
		rwXMLdata.writeXMLData(path, ApplicationData.getProject(), true);
		ApplicationData.PROJECT_PATH = path;
	}

	public void setLookAndFeel(String className) {
		try {
			UIManager.setLookAndFeel(className);
		} catch (Exception e) {
		}
		SwingUtilities.updateComponentTreeUI(frame);
	}

	public Action getMetalLookAndFeel() {
		return metalLookAndFeel;
	}

	public Action getMotifLookAndFeel() {
		return motifLookAndFeel;
	}

	public Action getWindowsLookAndFeel() {
		return windowsLookAndFeel;
	}

	public JMenuBar getMenubar() {
		return menubar;
	}

	public JToolBar getToolbar() {
		return toolbar;
	}

	public static boolean saveProject() throws IOException {
		if (ApplicationData.PROJECT_PATH == null
				|| ApplicationData.PROJECT_PATH.equals("")) {
			if (saveAs() == null) {
				return false;
			} else {
				return true;
			}
		} else {
			save(ApplicationData.PROJECT_PATH);
			return true;
		}
	}

	private void publishToApatar() throws HttpException, IOException {
		JPublishToApatarDialog dlg = new JPublishToApatarDialog(
				ApatarUiMain.MAIN_FRAME);
		dlg.setVisible(true);

		if (dlg.getOption() == JPublishToApatarDialog.CANCEL_OPTION) {
			return;
		}

		PostMethod method = new PostMethod(PUBLISH_TO_APATAR_URL);

		File file;
		if (dlg.isSelectFromFile()) {
			file = new File(dlg.getFilePath());
		} else {
			String tempFolderName = "tempdatamap/";
			File tempFolder = new File(tempFolderName);
			if (!tempFolder.exists()) {
				tempFolder.mkdir();
			}
			String fileName = "tempdatamap/"
					+ dlg.getDataMapName().replaceAll("[|/\\:*?\"<> ]", "_")
					+ ".aptr";
			ReadWriteXMLDataUi rwXMLdata = new ReadWriteXMLDataUi();
			file = rwXMLdata.writeXMLData(fileName.toString(),
					ApplicationData.getProject(), true);
		}

		Part[] parts = new Part[14];
		parts[0] = new StringPart("option", "com_remository");
		parts[1] = new StringPart("task", "");
		parts[1] = new StringPart("func", "savefile");
		parts[2] = new StringPart("element", "component");
		parts[3] = new StringPart("client", "");
		parts[4] = new StringPart("oldid", "0");
		parts[5] = new FilePart("userfile", file);
		parts[6] = new StringPart("containerid", ""
				+ dlg.getDataMapLocation().getId());
		parts[7] = new StringPart("filetitle", dlg.getDataMapName());
		parts[8] = new StringPart("description", dlg.getDataMapDescription());
		parts[9] = new StringPart("smalldesc", dlg.getShortDescription());
		parts[10] = new StringPart("filetags", dlg.getTags());
		parts[11] = new StringPart("pubExternal", "true");
		parts[12] = new StringPart("username", dlg.getUserName());
		parts[13] = new StringPart("password", CoreUtils.getMD5(dlg
				.getPassword()));

		method.setRequestEntity(new MultipartRequestEntity(parts, method
				.getParams()));

		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams()
				.setConnectionTimeout(10000);
		int status = client.executeMethod(method);
		if (status != HttpStatus.SC_OK) {
			JOptionPane.showMessageDialog(
					ApatarUiMain.MAIN_FRAME,
					"Upload failed, response="
							+ HttpStatus.getStatusText(status));
		} else {
			StringBuffer buff = new StringBuffer(
					method.getResponseBodyAsString());

			try {
				Matcher matcher = ApatarRegExp
						.getMatcher(
								"<meta name=\"apatarResponse\" content=\"[a-zA-Z_0-9]+\"",
								buff.toString());
				boolean patternFound = false;
				while (matcher.find()) {
					patternFound = true;
					String result = matcher.group();
					result = result.replaceFirst(
							"<meta name=\"apatarResponse\" content=\"", "");
					result = result.replace("\"", "");
					if (result.equalsIgnoreCase("done")) {
						JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
								"File has been published");
					} else if (result.equalsIgnoreCase("error_xml")) {
						JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
								"File is not valid");
					} else if (result.equalsIgnoreCase("error_login")) {
						JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
								"Name or Password is not valid");
					}
				}
				if (!patternFound) {
					JOptionPane
							.showMessageDialog(ApatarUiMain.MAIN_FRAME,
									"Wrong response from server. Please check your connection.");
				}
			} catch (ApatarException e) {
				e.printStackTrace();
			}
		}
	}

	public static void openProject(JFrame frame, JWorkPane workPane) {
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileFilter(FF);

		int returnValue = fileChooser.showOpenDialog(frame);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File fileSrc = fileChooser.getSelectedFile();
			ApplicationData.COUNT_INIT_ERROR = 0;

			// ReadWriteXMLData.loadDateAndTimeSettings(fileSrc.toString());
			ReadWriteXMLDataUi rwXMLdata = new ReadWriteXMLDataUi();
			try {
				ApatarUiMain.MAIN_FRAME.setTitle(String.format(
						JApatarMainUIFrame.FRAME_TITLE,
						rwXMLdata.readXMLData(fileSrc.toString(),
								ApplicationData.getProject()) + " - "));
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (ApplicationData.COUNT_INIT_ERROR > 0) {
				JOptionPane
						.showMessageDialog(
								ApatarUiMain.MAIN_FRAME,
								"An error occured while opening the DataMap: Uninitialized properties were found.\nPlease, check node(-s) configuration.");
			}
			UiUtils.updatePane(ApplicationData.getProject(), workPane);
			ApplicationData.PROJECT_PATH = fileSrc.getPath();
		}
	}

}
