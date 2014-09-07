package com.apatar.output;

import org.jdom.Element;

import com.apatar.core.ApplicationData;
import com.apatar.core.DataDirection;
import com.apatar.core.FolderPath;
import com.apatar.core.IPersistent;
import com.apatar.core.PersistentUtils;

public class OutputParams implements IPersistent {
	
	FolderPath directory = new FolderPath();
	String cache = "";
	String cacheName="";
	
	
	public void initFromElement(Element e) {
		String text = e.getChildText("directory");
		cacheName = e.getAttributeValue("cacheName");
		if (text != null)
			directory = new FolderPath(text);
		else
			ApplicationData.COUNT_INIT_ERROR++;
	}

	public Element saveToElement() {
		Element e = PersistentUtils.CreateElement(this);
		Element dir = new Element("directory");
		dir.setText(directory.getPath());
		e.addContent(dir);
		e.setAttribute("cacheName", cacheName);
		//Element elUrl = new Element("appKey");
		//elUrl.setText(cache);
		//e.addContent(elUrl);
		return e;
	}

	public FolderPath getDirectory() {
		return directory;
	}

	public void setDirectory(FolderPath directory) {
		this.directory = directory;
	}
	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

}
