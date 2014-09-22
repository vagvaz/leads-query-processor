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

package propertysheet;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.apatar.ui.JDefaultContextMenu;

public class JPropertyNameDialog extends JDialog{
	private static final long serialVersionUID = 1L;

	private JTextField nameConnector;
    private JButton buttonOk;
    private JButton buttonCancel;

    private boolean ok = false;

    public JPropertyNameDialog(JDialog owner, String title, boolean modal) throws HeadlessException {
    	super(owner, title, modal);
        nameConnector = new JTextField();
        createComponent();
        setSize(300,100);
        setResizable(false);
    }
    
    public JPropertyNameDialog(Frame owner, String title, JTextField textField, boolean modal) throws HeadlessException {
        super(owner, title, modal);
        nameConnector = textField;
        createComponent();
    }
    
    public JPropertyNameDialog(JDialog owner, String title, JTextField textField, boolean modal) throws HeadlessException {
        super(owner, title, modal);
        nameConnector = textField;
        createComponent();
    }

    private void createComponent() {
    	
    	setSize(300,100);
        setResizable(false);
    	
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        getContentPane().setLayout(gridbag);
        c.insets=new Insets(5,5,5,5);
        c.fill=GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty=0.0;
        c.gridwidth=GridBagConstraints.REMAINDER;   
        nameConnector.addKeyListener(new KeyAdapter(this));
        nameConnector.setComponentPopupMenu(
				new JDefaultContextMenu(nameConnector) );
        makeComponent(nameConnector,gridbag,c);
        c.gridwidth=1;
        buttonOk = new JButton("Ok");
        buttonOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ok=true;
                setVisible(false);
            }
        });
        buttonCancel = new JButton("Cancel");
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        c.gridwidth=GridBagConstraints.REMAINDER;
        buttonOk.setEnabled(false);
                
        GridLayout buttonGrid = new GridLayout(1,2,10,10);
        JPanel panelButton=new JPanel(buttonGrid);
        c.fill=GridBagConstraints.NONE;
        c.anchor=GridBagConstraints.EAST;
        c.weightx=0.0;
        c.weighty=0.0;
        makeComponent(panelButton,gridbag,c);
        panelButton.add(buttonOk);
        panelButton.add(buttonCancel);
        
    }

    public String getNameConnector() {
        return nameConnector.getText();
    }
    public boolean isOk() {
        return ok;
    }
    public JButton getButtonOk() {
        return buttonOk;
    }
    public void setNodeName(String name) {
    	nameConnector.setText(name);
    }

    class KeyAdapter implements KeyListener {
    	JPropertyNameDialog dlg;

        public KeyAdapter(JPropertyNameDialog dlg) {
            this.dlg = dlg;
        }

        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode()==KeyEvent.VK_ENTER) {
                if (dlg.getNameConnector().length()>0) {
                    ok=true;
                    setVisible(false);
                }
            }
            if (e.getKeyCode()==KeyEvent.VK_ESCAPE)
            	setVisible(false);
        }
        public void keyReleased(KeyEvent e) {
            if (dlg.getNameConnector().length()>0) dlg.getButtonOk().setEnabled(true);
            else
                dlg.getButtonOk().setEnabled(false);
        }
        public void keyTyped(KeyEvent e) {

        }
    }

    void makeComponent(Component component,GridBagLayout gridbag,GridBagConstraints c) {
        gridbag.setConstraints(component, c);
        getContentPane().add(component);
    }

}
