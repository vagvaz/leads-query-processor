//////////////////////////////////////////////////////////
// Firebird Utils
// used to display images in left pan and in client area
// Author : Aamir Tauqeer (Kuwait)
// Date   : December 12, 2008
// Final  : December 25, 2008
//////////////////////////////////////////////////////////

package com.apatar.firebird;

import javax.swing.ImageIcon;

import com.apatar.firebird.FirebirdNodeFactory;

public class FirebirdUtils 
{
    public FirebirdUtils()
    {
    }
       
    public static final ImageIcon READ_FIRBIRD_ICON = new ImageIcon(FirebirdNodeFactory.class.getResource("16-firbird.png"));
    public static final ImageIcon READ_FIRBIRD_NODE_ICON = new ImageIcon(FirebirdNodeFactory.class.getResource("32-firbird.png"));
}
