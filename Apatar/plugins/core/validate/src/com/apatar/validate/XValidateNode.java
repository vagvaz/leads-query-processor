package com.apatar.validate;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import javax.swing.ImageIcon;

import org.jdom.Element;

import com.apatar.core.AbstractApatarActions;
import com.apatar.core.ApplicationData;
import com.apatar.core.ConnectionPoint;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DataProcessingInfo;
//import com.apatar.core.FunctionsPlugin;
import com.apatar.core.TableInfo;
import com.apatar.core.TransparentNode;
import com.apatar.core.XFunctionsPlugin;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.JSubProjectDialog;

public class XValidateNode extends TransparentNode {

	public static final String INPUT_CONN_POINT = "input";
	public static final String OUTPUT_CONN_POINT_TRUE = "outputTrue";
	public static final String OUTPUT_CONN_POINT_FALSE = "outputFalse";

	public XValidateNode() {
		super();
		title = "XValidate";

		outputConnectionList.put(new ConnectionPoint(OUTPUT_CONN_POINT_FALSE,
				false, this, true, "False", 2), new TableInfo("False"));
		outputConnectionList.put(new ConnectionPoint(OUTPUT_CONN_POINT_TRUE,
				false, this, true, "True", 1), new TableInfo("True"));
		inputConnectionList.add(new ConnectionPoint(INPUT_CONN_POINT, true,
				this, false));
	}

	@Override
	public ImageIcon getIcon() {
		return XValidateUtils.VALIDATE_ICON;
	}

	@Override
	public boolean realEdit(AbstractApatarActions actions) {
		return JSubProjectDialog.showDialog(ApatarUiMain.MAIN_FRAME,"Validation", this, new String[] { INPUT_CONN_POINT },
				XFunctionsPlugin.getNodesFunction(), false,
				"help.operation.validate") == JSubProjectDialog.OK_OPTION;

	}

	@Override
	public Element saveToElement() {
		return super.saveToElement();
	}

	@Override
	public void initFromElement(Element e) {
		super.initFromElement(e);
	}

	@Override
	public TableInfo getDebugTableInfo() {
		return getTiForConnection(OUTPUT_CONN_POINT_TRUE);
	}

	@Override
	public void Transform() {
		DataBaseTools.completeTransfer();
		TableInfo iTI = getTiForConnection(INPUT_CONN_POINT);
		ResultSet rs = null;
		try {
			rs = DataBaseTools.getRSWithAllFields(iTI.getTableName(),
					ApplicationData.getTempJDBC(), ApplicationData
							.getTempDataBaseInfo());
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				FillInputColumnNodes(rs, INPUT_CONN_POINT);

				// execute the project
				com.apatar.core.Runnable rn = new com.apatar.core.Runnable();
				rn.execute(prj.getNodes().values());

				boolean result = calculateResult();

				TableInfo destinationTableInfo = (result == false) ? getTiForConnection(XValidateNode.OUTPUT_CONN_POINT_FALSE)
						: getTiForConnection(XValidateNode.OUTPUT_CONN_POINT_TRUE);

				if (destinationTableInfo != null) {
					DataBaseTools.completeTransfer();
					DataBaseTools.insertData(
							new DataProcessingInfo(ApplicationData
									.getTempDataBase().getDataBaseInfo(),
									destinationTableInfo.getTableName(),
									destinationTableInfo.getRecords(),
									ApplicationData.getTempJDBC()), rs, rsmd);
				}
				ApplicationData.ProcessingProgress.Step();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();
		}

	}

}
