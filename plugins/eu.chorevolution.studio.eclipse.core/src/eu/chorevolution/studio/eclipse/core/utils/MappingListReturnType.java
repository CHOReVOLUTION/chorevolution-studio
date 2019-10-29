/*
  * Copyright 2015 The CHOReVOLUTION project
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package eu.chorevolution.studio.eclipse.core.utils;

public class MappingListReturnType {
	
	private String item;
	private TreeNode<TreeNodeDataType> treeNode;
	
	public MappingListReturnType(String item, TreeNode<TreeNodeDataType> treeNode) {
		this.item = item;
		this.treeNode = treeNode;
		
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public TreeNode<TreeNodeDataType> getTreeNode() {
		return treeNode;
	}

	public void setTreeNode(TreeNode<TreeNodeDataType> treeNode) {
		this.treeNode = treeNode;
	}
	
	
	
}
