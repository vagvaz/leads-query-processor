/**
 *
 */
package com.apatar.core;

import java.awt.Point;

import javax.swing.ImageIcon;

/**
 * @author sm
 * 
 */
public interface NodeUI {
	/**
	 * UI method. returns width of the node element on the work pane.
	 * 
	 * @return node icon width
	 */
	public int getWidth();

	/**
	 * UI method. returns height of the node element on the work pane.
	 * 
	 * @return node icon height
	 */
	public int getHeigth();

	/**
	 * UI method.
	 * 
	 * @return node font
	 */
	public int getFontStyle();

	/**
	 * UI method. Sets node's title on the work pane
	 */
	public void setTitle(String title);

	/**
	 * UI method.
	 * 
	 * @return node's title on the work pane.
	 */
	public String getTitle();

	/**
	 * UI method.
	 * 
	 * @return icon for the node.
	 */
	public ImageIcon getIcon();

	// 11.01.2007 by Alex Mashko
	// public ImageIcon getConnPointIcon();

	/**
	 * UI method.
	 * 
	 * @return icon for the left node side icon
	 */
	public ImageIcon getInputConnPointIcon();

	/**
	 * UI method.
	 * 
	 * @return icon for the right node side icon
	 */
	public ImageIcon getOutputConnPointIcon();

	/**
	 * UI method.
	 * 
	 * have to be removed, because it is unnecessary
	 * 
	 * @return icon
	 */
	public ImageIcon getExtConnPointIcon();

	/**
	 * UI method to set node position on the WorkPane
	 */
	public void setPosition(Point position);

	/**
	 * UI method.
	 * 
	 * @return node position on the WorkPane
	 */
	public Point getPosition();

}
