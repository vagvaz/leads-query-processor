/**
 * @author apon
 */

package com.apatar.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import tuc.core.apon.WSClientGenerator;


public class JwsdlLocationDialog extends JDialog{

	JTextField wsdlLocationField = new JTextField();
	JButton okButton = new JButton("Ok");
	JButton cancelButton = new JButton("Cancel");

	JTextField lastNameField = new JTextField();
	JTextField emailField = new JTextField();

	public JwsdlLocationDialog(){
		super();
		setTitle("Web Service Client Generator :: WSDL's Location");
		createDialog();
		addListeners();
	}

	private void createDialog(){
		setLayout( new BorderLayout(5,5) );

		setSize(500, 130);

		JPanel textPanel = new JPanel(new BorderLayout(5,5));
		textPanel.setBorder( new EmptyBorder(5, 5, 5, 5) );

		textPanel.add( new JLabel("Please enter wsdl document's location"),
				BorderLayout.NORTH );

		JPanel contactPanel = new JPanel();
		contactPanel.setLayout(new BoxLayout(contactPanel, BoxLayout.Y_AXIS));

		JPanel wsdlLocationPanel = new JPanel();
		wsdlLocationPanel.setLayout( new BoxLayout(wsdlLocationPanel,BoxLayout.X_AXIS) );
		wsdlLocationPanel.add( new JLabel("wsdl location: ") );
		wsdlLocationPanel.add( wsdlLocationField );

		contactPanel.add( wsdlLocationPanel );

		textPanel.add( contactPanel, BorderLayout.SOUTH );

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		buttonPanel.setBorder( new EmptyBorder(0, 0, 1, 0) );

		getContentPane().add(textPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
	}

	private void addListeners(){
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				System.out.println(wsdlLocationField.getText());
				/*
				 * TODO: here goes the parser's instance
				 */
				WSClientGenerator wsClientGen = new WSClientGenerator();
				wsClientGen.generate(wsdlLocationField.getText());
				dispose();
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
	}
}
