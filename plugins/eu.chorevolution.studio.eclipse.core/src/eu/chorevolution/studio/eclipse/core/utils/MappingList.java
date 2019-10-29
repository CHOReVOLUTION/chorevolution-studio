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

import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

public class MappingList extends List {

	private java.util.List<TreeNode<TreeNodeDataType>> associatedTreeNode = null;
	
	public MappingList(Composite parent, int style) {
		super(parent, style);

		associatedTreeNode = new ArrayList<TreeNode<TreeNodeDataType>>();
		
		// TODO Auto-generated constructor stub
	}

	//TODO this hack is needed to bypass the no-subclass by SWT
	@Override
	public void checkSubclass() {
		
	}
	
	@Override
	public void removeAll() {
		super.removeAll();
		
		associatedTreeNode = new ArrayList<TreeNode<TreeNodeDataType>>();
		
	}
	
	public void addMapping(String toAdd, TreeNode<TreeNodeDataType> treeNode) {
		add(toAdd);
		setAssociatedTreeNode(treeNode);
	}
	
	//only one element
	public MappingListReturnType getMapping() {
		String selected = getSelection()[0];
		int index = getSelectionIndices()[0];
		return new MappingListReturnType(selected, getAssociatedTreeNode(index));
	}
	
	//only one element
	public MappingListReturnType getMappingItem(int position) {
		String selected = getItem(position);
		int index = position;
		return new MappingListReturnType(selected, getAssociatedTreeNode(index));
	}
	
	public TreeNode<TreeNodeDataType> getAssociatedTreeNode(int position) {
		return associatedTreeNode.get(position);
	}

	public void setAssociatedTreeNode(TreeNode<TreeNodeDataType> associatedTreeNode) {
		this.associatedTreeNode.add(associatedTreeNode);
	}
	
	

}
