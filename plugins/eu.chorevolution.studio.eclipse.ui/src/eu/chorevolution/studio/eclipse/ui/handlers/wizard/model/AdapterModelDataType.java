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
package eu.chorevolution.studio.eclipse.ui.handlers.wizard.model;

import java.util.List;

import eu.chorevolution.modelingnotations.chorarch.Adapter;
import eu.chorevolution.studio.eclipse.core.utils.TreeNode;
import eu.chorevolution.studio.eclipse.core.utils.TreeNodeDataType;

public class AdapterModelDataType {

	private String participant;
	private String roleToMap;
	private String taskName;
	private String taskID;
	private TreeNode<TreeNodeDataType> treeFromTask;
	private byte[] wsdlOrGidlFile;
	private TreeNode<TreeNodeDataType> treeMapping;
	private TreeNode<TreeNodeDataType> treeFromWSDLOrGIDL;
	private Adapter adapterModel = null;
	
	public AdapterModelDataType(String participant, String roleToMap, String taskName, String taskID, TreeNode<TreeNodeDataType> treeFromTask, byte[] wsdlOrGidlFile, TreeNode<TreeNodeDataType> treeMapping, TreeNode<TreeNodeDataType> treeFromWSDLOrGIDL) {
		this.participant = participant;
		this.roleToMap = roleToMap;
		this.taskName = taskName;
		this.taskID = taskID;
		this.treeFromTask = treeFromTask;
		this.wsdlOrGidlFile = wsdlOrGidlFile;
		this.treeMapping = treeMapping;
		this.treeFromWSDLOrGIDL = treeFromWSDLOrGIDL;
	}

	public TreeNode<TreeNodeDataType> getTreeFromWSDLOrGIDL() {
		return treeFromWSDLOrGIDL;
	}

	public void setTreeFromWSDLOrGIDL(TreeNode<TreeNodeDataType> treeFromWSDLOrGIDL) {
		this.treeFromWSDLOrGIDL = treeFromWSDLOrGIDL;
	}

	public byte[] getWsdlOrGidlFile() {
		return wsdlOrGidlFile;
	}

	public void setWsdlOrGidlFile(byte[] wsdlOrGidlFile) {
		this.wsdlOrGidlFile = wsdlOrGidlFile;
	}

	public String getParticipant() {
		return participant;
	}

	public void setParticipant(String participant) {
		this.participant = participant;
	}

	public String getRoleToMap() {
		return roleToMap;
	}

	public void setRoleToMap(String roleToMap) {
		this.roleToMap = roleToMap;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskID() {
		return taskID;
	}

	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}
	
	public TreeNode<TreeNodeDataType> getTreeFromTask() {
		return treeFromTask;
	}

	public void setTreeFromTask(TreeNode<TreeNodeDataType> treeFromTask) {
		this.treeFromTask = treeFromTask;
	}

	public TreeNode<TreeNodeDataType> getTreeMapping() {
		return treeMapping;
	}

	public void setTreeMapping(TreeNode<TreeNodeDataType> treeMapping) {
		this.treeMapping = treeMapping;
	}

	public Adapter getAdapterModel() {
		return adapterModel;
	}

	public void setAdapterModel(Adapter adapterModel) {
		this.adapterModel = adapterModel;
	}
	

	
	
	
}
