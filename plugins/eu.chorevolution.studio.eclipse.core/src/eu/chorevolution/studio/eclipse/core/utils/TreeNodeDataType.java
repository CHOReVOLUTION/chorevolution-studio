package eu.chorevolution.studio.eclipse.core.utils;

import java.util.ArrayList;
import java.util.List;

import eu.chorevolution.modelingnotations.adapter.ChoreographyDataItem;
import eu.chorevolution.modelingnotations.adapter.DataItem;

public class TreeNodeDataType {
	private String type;
	private String dataType;
	private String content;
	private String maxOccurrences;
	private String minOccurrences;
	private String complexOrSimpleType;
	private boolean isRestriction;
	private List<TreeNode<TreeNodeDataType>> isMappedWithTreeNode;
	private DataItem isMappedWithDataItem;
	private ChoreographyDataItem isMappedWithChoreographyDataItem;
	private String transformationRule;
	private boolean isSecurity;

	
	public TreeNodeDataType() {
		this.type = null;
		this.dataType = null;
		this.content = null;
		this.maxOccurrences = "one";
		this.minOccurrences = "one";
		this.complexOrSimpleType = "simpleType";
		this.isRestriction = false;
		this.isMappedWithTreeNode = new ArrayList<TreeNode<TreeNodeDataType>>();
		this.isMappedWithDataItem = null;
		this.isMappedWithChoreographyDataItem = null;
		this.transformationRule = null;
		this.setSecurity(false);
	}
	
	public TreeNodeDataType(String type, String content) {
		this.type = type;
		this.dataType = type;
		this.content = content;
		this.maxOccurrences = "one";
		this.minOccurrences = "one";
		this.complexOrSimpleType = "simpleType";
		this.isRestriction = false;
		this.isMappedWithTreeNode = new ArrayList<TreeNode<TreeNodeDataType>>();
		this.isMappedWithDataItem = null;
		this.isMappedWithChoreographyDataItem = null;
		this.transformationRule = null;
	}

	

	public String getTransformationRule() {
		return transformationRule;
	}



	public void setTransformationRule(String transformationRule) {
		this.transformationRule = transformationRule;
	}



	public boolean isRestriction() {
		return isRestriction;
	}



	public void setRestriction(boolean isRestriction) {
		this.isRestriction = isRestriction;
	}



	public String getDataType() {
		return dataType;
	}



	public void setDataType(String dataType) {
		this.dataType = dataType;
	}



	public ChoreographyDataItem getIsMappedWithChoreographyDataItem() {
		return isMappedWithChoreographyDataItem;
	}



	public void setIsMappedWithChoreographyDataItem(ChoreographyDataItem isMappedWithChoreographyDataItem) {
		this.isMappedWithChoreographyDataItem = isMappedWithChoreographyDataItem;
	}



	public DataItem getIsMappedWithDataItem() {
		return isMappedWithDataItem;
	}



	public void setIsMappedWithDataItem(DataItem isMappedWithDataItem) {
		this.isMappedWithDataItem = isMappedWithDataItem;
	}



	public List<TreeNode<TreeNodeDataType>> getIsMappedWithTreeNode() {
		if(isMappedWithTreeNode == null)
			isMappedWithTreeNode = new ArrayList<TreeNode<TreeNodeDataType>>();
		return isMappedWithTreeNode;
	}



	public void setIsMappedWithTreeNode(List<TreeNode<TreeNodeDataType>> isMappedWithTreeNode) {
		this.isMappedWithTreeNode = isMappedWithTreeNode;
	}

	public void addMappedWithTreeNode(TreeNode<TreeNodeDataType> addMappedWithTreeNode) {
		if(isMappedWithTreeNode == null)
			isMappedWithTreeNode = new ArrayList<TreeNode<TreeNodeDataType>>();
		
		//not add duplicates
		boolean found = false;
		for(TreeNode<TreeNodeDataType> treeNode : isMappedWithTreeNode) {
			if(addMappedWithTreeNode == treeNode) {
				found = true;
				break;
			}
		}
		
		if(!found)
			isMappedWithTreeNode.add(addMappedWithTreeNode);
	}


	public String getMaxOccurrences() {
		return maxOccurrences;
	}



	public void setMaxOccurrences(String maxOccurrences) {
		this.maxOccurrences = maxOccurrences;
	}



	public String getMinOccurrences() {
		return minOccurrences;
	}



	public void setMinOccurrences(String minOccurrences) {
		this.minOccurrences = minOccurrences;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public String getComplexOrSimpleType() {
		return complexOrSimpleType;
	}


	public void setComplexOrSimpleType(String complexOrSimpleType) {
		this.complexOrSimpleType = complexOrSimpleType;
	}

	public boolean isSecurity() {
		return isSecurity;
	}

	public void setSecurity(boolean isSecurity) {
		this.isSecurity = isSecurity;
	}
	
	
	
}
