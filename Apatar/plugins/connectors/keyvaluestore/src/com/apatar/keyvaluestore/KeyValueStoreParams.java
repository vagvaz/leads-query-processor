package com.apatar.keyvaluestore;

import java.util.Properties;

import javax.swing.JFileChooser;

import org.jdom.Element;

import com.apatar.core.ApplicationData;
import com.apatar.core.IPersistent;
import com.apatar.core.JdbcParams;
import com.apatar.core.PersistentUtils;


public class KeyValueStoreParams extends JdbcParams{


	//FolderPath mapperPath = new FolderPath(), reducerPath = new FolderPath(), combinerPath = new FolderPath(), xmlPath = new FolderPath();
	String mapperPath = new String(), reducerPath = new String(), combinerPath = new String(), xmlPath = new String();
	
	@Override
    public Element saveToElement() {
        Element element = super.saveToElement();
        element.setAttribute("mapperPath", mapperPath);
        element.setAttribute("reducerPath", reducerPath);
        element.setAttribute("combinerPath", combinerPath);
        element.setAttribute("xmlPath", xmlPath);
        return super.saveToElement();
    }

	@Override
    public void initFromElement(Element element) {
        super.initFromElement(element);
        mapperPath = element.getAttributeValue("mapperPath");
        reducerPath = element.getAttributeValue("reducerPath");
        combinerPath = element.getAttributeValue("combinerPath");
        xmlPath = element.getAttributeValue("xmlPath");
        if (mapperPath == null || reducerPath == null || combinerPath == null || xmlPath == null ) {
            ApplicationData.COUNT_INIT_ERROR++;
        }
    }


	public String getMapperPath() {
		return mapperPath;
	}

	public void setMapperPath(String mapperPath) {
		this.mapperPath = mapperPath;
	}

	public String getReducerPath() {
		return reducerPath;
	}

	public void setReducerPath(String reducerPath) {
		this.reducerPath = reducerPath;
	}

	public String getCombinerPath() {
		return combinerPath;
	}

	public void setCombinerPath(String combinerPath) {
		this.combinerPath = combinerPath;
	}

	public String getXmlPath() {
		return xmlPath;
	}

	public void setXmlPath(String xmlPath) {
		this.xmlPath = xmlPath;
	}

	@Override
	public String getConnUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Properties getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProperties(Properties property) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		
	}
	
	
}
