/*
_______________________

Apatar Open Source Data Integration

Copyright (C) 2005-2007, Apatar, Inc.

info@apatar.com

195 Meadow St., 2nd Floor

Chicopee, MA 01013

 

    This program is free software; you can redistribute it and/or modify

    it under the terms of the GNU General Public License as published by

    the Free Software Foundation; either version 2 of the License, or

    (at your option) any later version.

 

    This program is distributed in the hope that it will be useful,

    but WITHOUT ANY WARRANTY; without even the implied warranty of

    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the

    GNU General Public License for more details.

 

    You should have received a copy of the GNU General Public License along

    with this program; if not, write to the Free Software Foundation, Inc.,

    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

________________________

*/
 

package com.apatar.rss;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;

import com.apatar.core.ApplicationData;

public class RssElement implements Cloneable {
	
	protected String name;
	boolean isAttribute = false;
	RssElement parent = null;
	List<RssElement> childrens = new ArrayList<RssElement>();
	boolean isUnbounded = false;
	//int count;
	Namespace namespace = null;
	
	boolean hidden = false;
	String value;
	
	public RssElement() {
		super();
	}
	
	public RssElement(String name) {
		super();
		this.name = name;
	}
	
	public RssElement(RssElement element, boolean withChild) {
		super();
		this.name = element.name;
		this.isAttribute = element.isAttribute;
		this.parent = element.parent;
		if (withChild)
			this.childrens = element.childrens;
		this.isUnbounded = element.isUnbounded;
		//this.count = element.count;
		this.namespace = element.namespace;
		this.hidden = element.hidden;
		this.value = element.value;
	}

	public RssElement(String name, boolean isAttribute, RssElement parent) {
		super();
		this.name = name;
		this.isAttribute = isAttribute;
		this.parent = parent;
	}
	
	public RssElement(String name, RssElement parent) {
		super();
		this.name = name;
		this.parent = parent;
	}

	public RssElement(String name, boolean isAttribute) {
		super();
		this.name = name;
		this.isAttribute = isAttribute;
	}
	
	public RssElement(String name, RssElement parent, boolean isUnbounded) {
		super();
		this.name = name;
		this.parent = parent;
		this.isUnbounded = isUnbounded;
	}

	public RssElement(String name, boolean isAttribute, RssElement parent, List<RssElement> childrens) {
		super();
		this.name = name;
		this.isAttribute = isAttribute;
		this.parent = parent;
		this.childrens = childrens;
	}
	
	public RssElement(String name, boolean isAttribute, RssElement parent, boolean isUnbounded) {
		super();
		this.name = name;
		this.isAttribute = isAttribute;
		this.parent = parent;
		this.isUnbounded = isUnbounded;
	}

	public RssElement(String name, boolean isAttribute, boolean unbounded) {
		super();
		this.name = name;
		this.isAttribute = isAttribute;
		this.isUnbounded = unbounded;
	}
	
	public RssElement(String name, boolean isAttribute, RssElement parent, List<RssElement> childrens, boolean isUnbounded) {
		super();
		this.name = name;
		this.isAttribute = isAttribute;
		this.parent = parent;
		this.childrens = childrens;
		this.isUnbounded = isUnbounded;
	}

	public String generateFieldName() {
		return (this.parent != null ? this.parent.getName() + RssNode.SEPARATOR : "") + name;
	}
	public static String generateFieldName(String parentName, String name) {
		return (parentName != null ? parentName + RssNode.SEPARATOR : "") + name;
	}
	public List<RssElement> getChildrens() {
		return childrens;
	}
	public boolean isAttribute() {
		return isAttribute;
	}
	public String getName() {
		return name;
	}
	public RssElement getParent() {
		return parent;
	}
	public void addChild(RssElement child) {
		childrens.add(child);
	}
	public void removeChild(RssElement child) {
		childrens.remove(child);
	}
	public boolean isUnbounded() {
		return isUnbounded;
	}
	public Namespace getNamespace() {
		return namespace;
	}
	public void setNamespace(Namespace namespace) {
		this.namespace = namespace;
	}

	public String getPath(boolean isParent) {
		String path = "";
		if (parent != null)
			path += parent.getPath(true);
		if (isAttribute)
			path += "/@" + name;
		else
			path += "/" + name;
		return path;
	}
	
	public String getFullName() {
		String fullName = name;
		if (parent != null)
			fullName = parent.getFullName() + RssNode.SEPARATOR + fullName;
		return fullName;
	}
	
	public void getPathToItem(List<RssElement> result) {
		if (parent != null) {
			result.add(0, parent);
			parent.getPathToItem(result);
		}
	}
	
	public List<String> getValue(Element root) {
		List<String> result = new ArrayList<String>();
		
		List<Element> childs = new ArrayList<Element>();
		String value;
		if (parent != null) {
			childs = root.getChildren(parent.getName(), parent.getNamespace());
			for (Element child : childs) {
				
				if (isAttribute) {
					if (namespace == null)
						value = child.getAttributeValue(name);
					else
						value = child.getAttributeValue(name, namespace);
				}
				else {
					if (namespace == null)
						value = child.getChildText(name);
					else
						value = child.getChildText(name, namespace);
				}
				
				if (value !=null)
					result.add(value);
			}
		} else {
			childs = root.getChildren(getName(), getNamespace());
			for (Object obj : childs) {
				Element child = (Element)obj;
				result.add(child.getText());
			}
		}
		
		return result;
	}
	
	public XPath generateXPath() throws JDOMException {
		XPath xpath = XPath.newInstance("./"+getPath(false));
		return xpath;
	}
	
	public static String[] getChain(String name) {
		StringTokenizer tokenizer = new StringTokenizer(name, RssNode.SEPARATOR);
		String[] chain = new String[tokenizer.countTokens()];
		int i = 0;
		while(tokenizer.hasMoreElements()) {
			chain[i++] = tokenizer.nextToken();
		}
		return chain;
	}

	/*public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}*/
	
	public String toString() {
		return getFullName();
	}
	
	public RssElement clone() {
		RssElement clone = null;
		try{
            clone=(RssElement)super.clone();
            clone.childrens = new ArrayList<RssElement>(this.childrens);
        } catch(CloneNotSupportedException e) {
            System.err.println(this.getClass()+" can't be cloned");
        }
        return clone;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public Element saveToElement() {
		Element resultElement = new Element("rssElement");

		Element elname = new Element("name");
		elname.setText(name);
		Element elisAttribute = new Element("isAttribute");
		elisAttribute.setText(""+isAttribute);
		Element elchildrens = new Element("childrens");
		for (RssElement childRssElem : childrens) {
			elchildrens.addContent(childRssElem.saveToElement());
		}
		Element elisUnbounded = new Element("isUnbounded");
		elisUnbounded.setText(""+isUnbounded);
		Element elcount = new Element("count");
		//elcount.setText(""+count);
		if (namespace != null) {
			Element elnamespace = new Element("namespace");
			elnamespace.setAttribute("prefix", namespace.getPrefix());
			elnamespace.setAttribute("uri", namespace.getURI());
			resultElement.addContent(elnamespace);
		}
		Element elhidden = new Element("hidden");
		elhidden.setText(""+hidden);
		Element elvalue = new Element("value");
		elvalue.setText(value);
		
		
		resultElement.addContent(elname);
		resultElement.addContent(elisAttribute);
		resultElement.addContent(elchildrens);
		resultElement.addContent(elisUnbounded);
		resultElement.addContent(elcount);
		resultElement.addContent(elhidden);
		resultElement.addContent(elvalue);
		
		return resultElement;
	}
	
	public void initFromElement(Element e) {
		name = e.getChildText("name");
		if (name == null)
			ApplicationData.COUNT_INIT_ERROR++;
		String elvalue = e.getChildText("isAttribute");
		if (elvalue != null)
			isAttribute = Boolean.parseBoolean(elvalue);
		else
			ApplicationData.COUNT_INIT_ERROR++;
		childrens = new ArrayList<RssElement>();
		Element elchildrens = e.getChild("childrens");
		if (elchildrens != null) {
			List elChilds = elchildrens.getChildren();
			if (elChilds != null) {
				for (Object obj : elChilds) {
					Element childElem = (Element)obj;
					RssElement childRssElement = new RssElement();
					childRssElement.initFromElement(childElem);
					childrens.add(childRssElement);
				}
			}
			else ApplicationData.COUNT_INIT_ERROR++;
		} else ApplicationData.COUNT_INIT_ERROR++;
		elvalue = e.getChildText("isUnbounded");
		if (elvalue != null)
			isUnbounded = Boolean.parseBoolean(elvalue);
		else
			ApplicationData.COUNT_INIT_ERROR++;
		//count = Integer.parseInt(e.getChildText("count"));
		Element elnamespace = e.getChild("namespace");
		if (elnamespace != null)
			namespace = Namespace.getNamespace(elnamespace.getAttributeValue("prefix"), elnamespace.getAttributeValue("uri"));
		hidden = Boolean.parseBoolean(e.getChildText("hidden"));
		this.value = e.getChildText("value");
		
	}
	
	public RssElement getChild(String name) {
		for (RssElement elem : childrens) {
			if (elem.getName().equalsIgnoreCase(name))
				return elem;
		}
		return null;
	}
	
	public static RssElement getRssElementByFullName(String fulName, RssElement root) {
		String[] chain = getChain(fulName);
		for (String childname : chain) {
			try {
				Integer.parseInt(childname);
			} catch(Exception e) {
				root = root.getChild(childname);
				continue;
			}
		}
		return root;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof RssElement) {
			RssElement re = (RssElement)obj;
			return getFullName().equals(re.getFullName());
		}
		return false;
	}
	
}

