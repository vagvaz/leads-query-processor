/*TODO recorded refactoring
 * метод createNodeColumns перенесён из TransformUIUtils в FunctionUtils
 * ****
 * TransformUIUtils.java удалён
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.ImageIcon;

import com.apatar.core.AbstractNode;
import com.apatar.core.Connector;
import com.apatar.core.Project;

public class UiUtils {

	public final static Color		whiteColor						= Color
																			.decode("#FFFFFF");
	public final static Color		blackColor						= Color
																			.decode("#000000");
	public static final Color		blueColor						= Color
																			.decode("#C2D9F9");
	public static final Color		yelowColor						= Color
																			.decode("#FFF1C8");
	public static final Color		grayColor						= Color
																			.decode("#D3D3D3");
	public static final Color		darkGrayColor					= Color
																			.decode("#716F64");

	public static final Dimension	fieldSize						= new Dimension(
																			300,
																			22);
	public static final Dimension	fieldSmallSize					= new Dimension(
																			150,
																			22);

	public static final ImageIcon	INPUT_CONN_POINT_ICON			= new ImageIcon(
																			UiUtils.class
																					.getResource("inputConnPoint.png"));

	public static final ImageIcon	OUTPUT_CONN_POINT_ICON			= new ImageIcon(
																			UiUtils.class
																					.getResource("outputConnPoint.png"));

	public static final ImageIcon	EXT_CONN_POINT_ICON				= new ImageIcon(
																			UiUtils.class
																					.getResource("extConnPoint.png"));

	public static final ImageIcon	ARROW_ICON						= new ImageIcon(
																			UiUtils.class
																					.getResource("arrows.png"));

	public static final ImageIcon	CONN_POINT_SMALL_ICON			= new ImageIcon(
																			UiUtils.class
																					.getResource("data_small.png"));

	public static final ImageIcon	COLUMN_ICON						= new ImageIcon(
																			UiUtils.class
																					.getResource("column.png"));
	public static final ImageIcon	SMALL_COLUMN_ICON				= new ImageIcon(
																			UiUtils.class
																					.getResource("column_small.png"));

	public static final ImageIcon	MAIN_ICON						= new ImageIcon(
																			UiUtils.class
																					.getResource("main16-16.png"));

	public static final ImageIcon	SPLASH_ICON						= new ImageIcon(
																			UiUtils.class
																					.getResource("splash.gif"));

	public static final ImageIcon	APATAR_LOGO_ICON				= new ImageIcon(
																			UiUtils.class
																					.getResource("apatar_logo.gif"));

	/***************************************************************************
	 * ColumnNode Icons
	 **************************************************************************/
	public static final ImageIcon	BINARY_COLUMN_NODE_ICON			= new ImageIcon(
																			UiUtils.class
																					.getResource("binary.png"));
	public static final ImageIcon	BOOLEAN_COLUMN_NODE_ICON		= new ImageIcon(
																			UiUtils.class
																					.getResource("boolean.png"));
	public static final ImageIcon	CURRENCY_COLUMN_NODE_ICON		= new ImageIcon(
																			UiUtils.class
																					.getResource("currency.png"));
	public static final ImageIcon	DATETIME_COLUMN_NODE_ICON		= new ImageIcon(
																			UiUtils.class
																					.getResource("data_time.png"));
	public static final ImageIcon	DATE_COLUMN_NODE_ICON			= new ImageIcon(
																			UiUtils.class
																					.getResource("date.png"));
	public static final ImageIcon	DECIMAL_COLUMN_NODE_ICON		= new ImageIcon(
																			UiUtils.class
																					.getResource("decimal.png"));
	public static final ImageIcon	ENUM_COLUMN_NODE_ICON			= new ImageIcon(
																			UiUtils.class
																					.getResource("enum.png"));
	public static final ImageIcon	MEDIA_COLUMN_NODE_ICON			= new ImageIcon(
																			UiUtils.class
																					.getResource("media.png"));
	public static final ImageIcon	NUMERIC_COLUMN_NODE_ICON		= new ImageIcon(
																			UiUtils.class
																					.getResource("numeric.png"));
	public static final ImageIcon	OBJECT_COLUMN_NODE_ICON			= new ImageIcon(
																			UiUtils.class
																					.getResource("object.png"));
	public static final ImageIcon	SPACIAL_COLUMN_NODE_ICON		= new ImageIcon(
																			UiUtils.class
																					.getResource("spacial.png"));
	public static final ImageIcon	TEXT_COLUMN_NODE_ICON			= new ImageIcon(
																			UiUtils.class
																					.getResource("text.png"));
	public static final ImageIcon	TEXTUNICODE_COLUMN_NODE_ICON	= new ImageIcon(
																			UiUtils.class
																					.getResource("text_unicode.png"));
	public static final ImageIcon	TIME_COLUMN_NODE_ICON			= new ImageIcon(
																			UiUtils.class
																					.getResource("time.png"));
	public static final ImageIcon	TIMESTAMP_COLUMN_NODE_ICON		= new ImageIcon(
																			UiUtils.class
																					.getResource("timestamp.png"));
	public static final ImageIcon	XML_COLUMN_NODE_ICON			= new ImageIcon(
																			UiUtils.class
																					.getResource("xml.png"));
	public static final ImageIcon	PLUS_NODE_ICON					= new ImageIcon(
																			UiUtils.class
																					.getResource("plus.png"));

	/***************************************************************************
	 * Fonts
	 **************************************************************************/

	public static final Font		NORMAL_SIZE_12_FONT				= new Font(
																			"Arial",
																			Font.PLAIN,
																			12);
	public static final Font		NORMAL_SIZE_10_FONT				= new Font(
																			"Arial",
																			Font.PLAIN,
																			10);
	public static final Font		NORMAL_SIZE_11_FONT				= new Font(
																			"Arial",
																			Font.PLAIN,
																			11);
	public static final Font		BOLD_SIZE_12_FONT				= new Font(
																			"Arial",
																			Font.BOLD,
																			12);

	/*
	 * Delete all nodes @workPane - JWorkPanel - work panel
	 */
	public static void clearWorkPane(JWorkPane workPane) {
		Component[] components = workPane.getComponents();
		for (Component element : components) {
			workPane.removeComponent(element);
		}
		workPane.updateUI();
	}

	public static void updatePane(Project project, JWorkPane workPane) {
		workPane.removeAll();
		for (Object element : project.getNodes().values()) {
			AbstractNode node = (AbstractNode) element;
			new JNodePanel(workPane, node);
		}

		for (Connector connector : project.getConnectors()) {
			new JConnectorArrow(workPane, connector);
		}

		workPane.updateUI();
	}

}
