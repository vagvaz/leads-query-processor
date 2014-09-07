package com.apatar.keyvaluestore.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.apatar.ui.ApatarUiMain;

public class KVSModePanel extends JPanel{

	private static final long serialVersionUID = 1L;
	private final static JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
	private final static JFileChooser directoryChooser = new JFileChooser(System.getProperty("user.dir"));
    private JTextField mapperPath, reducerPath, combinerPath, xmlPath, outputFolder;
    private JButton mapperButton, reducerButton, combinerButton, xmlButton, outputButton;
    
    static {
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        directoryChooser.setMultiSelectionEnabled(false);
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }
    
	public KVSModePanel() {
		super();
		createPanel();
	}
	
	

	public JTextField getMapperPath() {
		return mapperPath;
	}


	public void setMapperPath(JTextField mapperPath) {
		this.mapperPath = mapperPath;
	}



	public JTextField getReducerPath() {
		return reducerPath;
	}



	public void setReducerPath(JTextField reducerPath) {
		this.reducerPath = reducerPath;
	}



	public JTextField getCombinerPath() {
		return combinerPath;
	}



	public void setCombinerPath(JTextField combinerPath) {
		this.combinerPath = combinerPath;
	}



	public JTextField getXmlPath() {
		return xmlPath;
	}



	public void setXmlPath(JTextField xmlPath) {
		this.xmlPath = xmlPath;
	}



	private void createPanel() {

        JPanel filePanel = new JPanel();
        filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.Y_AXIS));
		add(filePanel, BorderLayout.CENTER);
		filePanel.add(new JLabel("Enter the paths of the requested files:"));
		mapperPath = new JTextField(20);
		mapperButton = createButton("...", mapperPath,true);
        filePanel.add(wrapComponents(new FlowLayout(FlowLayout.RIGHT), new JLabel("Mapper:"), mapperPath, mapperButton));
        reducerPath = new JTextField(20);
		reducerButton = createButton("...", reducerPath,true);
        filePanel.add(wrapComponents(new FlowLayout(FlowLayout.RIGHT), new JLabel("Reducer:"), reducerPath, reducerButton));
        combinerPath = new JTextField(20);
		combinerButton = createButton("...", combinerPath,true);
        filePanel.add(wrapComponents(new FlowLayout(FlowLayout.RIGHT), new JLabel("Combiner:"), combinerPath, combinerButton));
        xmlPath = new JTextField(20);
        xmlButton = createButton("...", xmlPath,true);
        filePanel.add(wrapComponents(new FlowLayout(FlowLayout.RIGHT), new JLabel("XML:"), xmlPath, xmlButton));
        outputFolder = new JTextField(20);
        outputButton = createButton("...", outputFolder,false);
        filePanel.add(wrapComponents(new FlowLayout(FlowLayout.RIGHT), new JLabel("Output directory:"), outputFolder, outputButton));
        
	}
	
	private JButton createButton(String label, final JTextField field,final boolean onlyFile) {
        JButton button = new JButton(label);
        button.setLayout(new FlowLayout());
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	int returnValue;
                if (onlyFile) {
                    returnValue = fileChooser.showOpenDialog(ApatarUiMain.MAIN_FRAME);
                }
                else {
                    returnValue = directoryChooser.showOpenDialog(ApatarUiMain.MAIN_FRAME);
                }
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File file;
                    if (onlyFile) {
                        file = fileChooser.getSelectedFile();
                    }
                    else {
                        file = directoryChooser.getSelectedFile();
                    }
                    field.setText(file.getAbsolutePath());
                }
            }
        });
        return button;
    }
	
	private JPanel wrapComponents(Component... components) {
        return wrapComponents(null, components);
    }
	
	private JPanel wrapComponents(LayoutManager layoutManager, Component... components) {
        JPanel panel = new JPanel();
        for (Component component : components) {
            panel.add(component);
        }

        if (layoutManager != null) {
            panel.setLayout(layoutManager);
        }
        return panel;
    }



	public JTextField getOutputFolder() {
		return outputFolder;
	}



	public void setOutputFolder(JTextField outputFolder) {
		this.outputFolder = outputFolder;
	}
	
}
