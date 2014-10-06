/*
_______________________
Apatar Open Source Data Integration
Copyright (C) 2005-2007, Apatar, Inc.
info@apatar.com
195 Meadow St., 2nd Floor
Chicopee, MA 01013

��� This program is free software; you can redistribute it and/or modify
��� it under the terms of the GNU General Public License as published by
��� the Free Software Foundation; either version 2 of the License, or
��� (at your option) any later version.

��� This program is distributed in the hope that it will be useful,
��� but WITHOUT ANY WARRANTY; without even the implied warranty of
��� MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.� See the
��� GNU General Public License for more details.

��� You should have received a copy of the GNU General Public License along
��� with this program; if not, write to the Free Software Foundation, Inc.,
��� 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
________________________

 */

package com.apatar.ftp;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.apatar.core.AbstractNode;
import com.apatar.ui.NodeFactory;

public class FtpNodeFactory extends NodeFactory {

	@Override
	public List<String> getCategory() {
		List<String> res = new ArrayList<String>();
		res.add("Connectors");
		return res;
	}

	@Override
	public String getTitle() {
		return "FTP";
	}

	@Override
	public ImageIcon getIcon() {
		return FtpUtils.READ_FTP_ICON;
	}

	public AbstractNode createNode() {
		return new FtpNode();
	}

	@Override
	public String getNodeClass() {
		return FtpNode.class.getName();
	}

	@Override
	public int getHorizontalTextPosition() {
		return JLabel.CENTER;
	}

	@Override
	public int getVerticalTextPosition() {
		return JLabel.BOTTOM;
	}

	@Override
	public Color getTextColor() {
		return Color.BLACK;
	}

	@Override
	public boolean MainPaneNode() {
		return true;
	}
}