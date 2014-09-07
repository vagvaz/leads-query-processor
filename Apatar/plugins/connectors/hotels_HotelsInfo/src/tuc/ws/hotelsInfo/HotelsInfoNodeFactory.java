/**
 *
 * this is auto generated code
 *
 */

package tuc.ws.hotelsInfo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.apatar.core.AbstractNode;
import com.apatar.ui.NodeFactory;

public class HotelsInfoNodeFactory extends NodeFactory{

	public boolean MainPaneNode() {
		return true;
	}
	public AbstractNode createNode() {
		return new HotelsInfoNode();
	}
	public List<String> getCategory() {
		List<String> res = new ArrayList<String>();
		res.add("Web Services");
		return res;
	}
	public int getHorizontalTextPosition() {
		return JLabel.CENTER;
	}
	public ImageIcon getIcon() {
		return HotelsInfoUtils.HOTELSINFO_ICON;
	}
	public String getNodeClass() {
	return HotelsInfoNode.class.getName();
	}
	public Color getTextColor() {
		return Color.BLACK;
	}
	public String getTitle() {
		return "hotelsInfo";
}
	public int getVerticalTextPosition() {
		return JLabel.BOTTOM;
	}
}
