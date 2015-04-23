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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jdom.Element;

import com.apatar.core.IPersistent;

public class JPropertySheetDialog extends JDialog{
	private static final long serialVersionUID = 1L;
	
	private JConnectionPanel panel = new JConnectionPanel();
    private JButton buttonOk;
    private JButton buttonCancel;
    private Element backup = null;
    
    private Object data;
	
    public JPropertySheetDialog(JDialog ownerDialog, Object data) {
    	super(ownerDialog, true);
    	this.data = data;
    	init(data);
    }

    public JPropertySheetDialog(JFrame ownerDialog, Object data) {
    	super(ownerDialog, true);
    	init(data);
    }
    
    private void init(Object value)
    {
    	this.data = value;
    	if (data instanceof IPersistent)
    		backup = ((IPersistent)data).saveToElement(); 
    	
        buttonOk = new JButton("Ok");
        buttonOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
    	
        buttonCancel = new JButton("Cancel");
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                if (backup != null)
                	((IPersistent)data).initFromElement(backup);
            }
        });
    	
        this.setSize(new Dimension(500,500));
        
        GridLayout buttonGrid = new GridLayout(1,2,10,10);
        JPanel panelButton=new JPanel(buttonGrid);
        panelButton.add(buttonOk);
        panelButton.add(buttonCancel);
        add(panelButton, BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);
        
		panel.updateBean(data);
    }
}
