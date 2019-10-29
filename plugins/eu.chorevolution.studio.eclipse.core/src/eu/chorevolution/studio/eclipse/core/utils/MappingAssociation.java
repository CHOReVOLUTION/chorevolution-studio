package eu.chorevolution.studio.eclipse.core.utils;

public class MappingAssociation {

	private TreeNode<TreeNodeDataType> firstTreeNode;
	private TreeNode<TreeNodeDataType> secondTreeNode;
	
	public MappingAssociation(TreeNode<TreeNodeDataType> firstTreeNode, TreeNode<TreeNodeDataType> secondTreeNode) {
		this.setFirstTreeNode(firstTreeNode);
		this.setSecondTreeNode(secondTreeNode);
	}

	public TreeNode<TreeNodeDataType> getFirstTreeNode() {
		return firstTreeNode;
	}

	public void setFirstTreeNode(TreeNode<TreeNodeDataType> firstTreeNode) {
		this.firstTreeNode = firstTreeNode;
	}

	public TreeNode<TreeNodeDataType> getSecondTreeNode() {
		return secondTreeNode;
	}

	public void setSecondTreeNode(TreeNode<TreeNodeDataType> secondTreeNode) {
		this.secondTreeNode = secondTreeNode;
	}
	
	
	
}
