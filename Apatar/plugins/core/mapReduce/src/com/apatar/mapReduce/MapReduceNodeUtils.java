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

package com.apatar.mapReduce;

import javax.swing.ImageIcon;

import com.apatar.mapReduce.MapReduceNodeFactory;

public class MapReduceNodeUtils {
	
	public static final ImageIcon TRANSFORM_ICON = new ImageIcon(MapReduceNodeFactory.class
	        .getResource("mapreduce32.png"));
	public static final ImageIcon SMALL_TRANSFORM_ICON = new ImageIcon(MapReduceNodeFactory.class
	        .getResource("mapreduce16.png"));
	public static final ImageIcon TRANSFORM_ICON_NODE = new ImageIcon(MapReduceNodeFactory.class
	        .getResource("mapreduce32.png"));
	
	
	public static final ImageIcon LEFT_END_ARROW = new ImageIcon(MapReduceNodeFactory.class
	        .getResource("left_end_arrow.png"));
	public static final ImageIcon RIGHT_END_ARROW = new ImageIcon(MapReduceNodeFactory.class
	        .getResource("right_end_arrow.png"));
	public static final ImageIcon LEFT_ARROW = new ImageIcon(MapReduceNodeFactory.class
	        .getResource("left_arrow.png"));
	public static final ImageIcon RIGHT_ARROW = new ImageIcon(MapReduceNodeFactory.class
	        .getResource("right_arrow.png"));
	public static final ImageIcon AUTO_MAP = new ImageIcon(MapReduceNodeFactory.class
	        .getResource("auto_map.png"));
	public static final ImageIcon EDIT = new ImageIcon(MapReduceNodeFactory.class
	        .getResource("edit.png"));
	public static final ImageIcon RESET = new ImageIcon(MapReduceNodeFactory.class
	        .getResource("reset.png"));

}
