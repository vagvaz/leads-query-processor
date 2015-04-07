package tuc.core.apon;

import java.util.LinkedList;

/**
 * This class holds the tree structure that
 * will be used to analyze complex types
 * 
 * @author apon
 */
public class TreeNode {
	String name;
	String fullName;	//fullName = father+node name
	String type;
	String base;		//used in enums etc.
	int flag;			//indicating array, enum XS_ANY etc.
	boolean rootRenamed;
	boolean hasExtension;
	TreeNode father;
	LinkedList<String> enumIndex;
	LinkedList<TreeNode> children;
	int color;			//used for DFS algorithm

	TreeNode(){
		this.name = null;
		this.fullName=null;
		this.type = null;
		this.father = null;
		this.children = null; 
		this.color=0;
		this.flag=-1;
		this.enumIndex=null;
		this.base = null;
		this.rootRenamed = false;
		this.hasExtension = false;
	}
	
	TreeNode(String name,
			 String fullName,
			 String type, 
			 TreeNode father, 
			 LinkedList<TreeNode> children, 
			 int flag, 
			 LinkedList<String> enumIndex,
			 String base){
		this.name = name;
		this.fullName = fullName;
		this.type = type;
		this.father = father;
		this.children = children;
		this.flag=flag;
		this.enumIndex = enumIndex;
		this.base = base;
		this.color=0;
		this.rootRenamed=false;
		this.hasExtension =false;
	}
	
	public TreeNode getFather() {
		return father;
	}

	public void setFather(TreeNode father) {
		this.father = father;
	}

	public LinkedList<TreeNode> getChildren() {
		return children;
	}

	public void setChildren(LinkedList<TreeNode> children) {
		this.children = children;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public int getColor() {
		return color;
	}
	
	public void setColor(int color) {
		this.color = color;
	}
	
	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public LinkedList<String> getEnumIndex() {
		return enumIndex;
	}

	public void setEnumIndex(LinkedList<String> enumIndex) {
		this.enumIndex = enumIndex;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public boolean isRootRenamed() {
		return rootRenamed;
	}

	public void setRootRenamed(boolean rootRenamed) {
		this.rootRenamed = rootRenamed;
	}

	public boolean isHasExtension() {
		return hasExtension;
	}

	public void setHasExtension(boolean hasExtension) {
		this.hasExtension = hasExtension;
	}
	
	public static TreeNode copyNodes(TreeNode srcNode){
		
		TreeNode dst = new TreeNode();
		
		if(srcNode.getBase()!=null)
			dst.setBase(srcNode.getBase());
		
		if(srcNode.getChildren()!=null){
			LinkedList<TreeNode> tmpChildren = new LinkedList<TreeNode>();
			tmpChildren.addAll(srcNode.getChildren());
			dst.setChildren(tmpChildren);			
		}

		dst.setColor(srcNode.getColor());
		
		if(srcNode.getEnumIndex()!=null){
			LinkedList<String>tmpEnIndx = new LinkedList<String>();
			tmpEnIndx.addAll(srcNode.getEnumIndex());
			dst.setEnumIndex(tmpEnIndx);
		}
		
		if(srcNode.getFather()!=null)
			dst.setFather(copyNodes(srcNode.getFather()));
		
		dst.setFlag(srcNode.getFlag());
		if(srcNode.getFullName()!=null)
			dst.setFullName(srcNode.getFullName());
		
		dst.setHasExtension(srcNode.hasExtension);
		if(srcNode.getName()!=null)
			dst.setName(srcNode.getName());
		
		dst.setRootRenamed(srcNode.rootRenamed);
		if(srcNode.getType()!=null)
			dst.setType(srcNode.getType());
		
		return dst;
		
	}
	
}

