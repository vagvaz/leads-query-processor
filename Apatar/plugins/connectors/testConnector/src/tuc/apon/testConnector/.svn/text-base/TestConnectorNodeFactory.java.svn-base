package tuc.apon.testConnector;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.apatar.core.AbstractNode;
import com.apatar.ui.NodeFactory;

/**
 * 
 * @author aposton
 *
 */
public class TestConnectorNodeFactory extends NodeFactory{

	public boolean MainPaneNode() {
		return true;
	}
	
	public AbstractNode createNode() {
		return new TestConnectorNode();
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
		return TestConnectorUtils.TEST_CONNECTOR_ICON;
	}

	public String getNodeClass() {
		return TestConnectorNode.class.getName();
	}

	public Color getTextColor() {
		return Color.BLACK;
	}

	public String getTitle() {
		return "testConnector";
	}

	public int getVerticalTextPosition() {
		return JLabel.BOTTOM;
	}

}
