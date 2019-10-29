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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import eu.chorevolution.studio.eclipse.core.utils.syncope.Service;

public class RolesViewerRecord {
	private String participant;
	private String roleToMap;
	private String serviceID;
	private String taskName;
	private String taskID;
	private String serviceName;
	private String serviceLocation;
	private String adapterModel;

	public RolesViewerRecord(String participant, String roleToMap, String serviceID, String taskName, String taskID, String serviceName, String serviceLocation) {
		this.participant = participant;
		this.roleToMap = roleToMap;
		this.serviceID = serviceID;
		this.taskName = taskName;
		this.taskID = taskID;
		this.serviceName = serviceName;
		this.serviceLocation = serviceLocation;
	}

	public String getTaskID() {
		return taskID;
	}

	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
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

	public String getServiceID() {
		return serviceID;
	}

	public void setServiceID(String serviceID) {
		this.serviceID = serviceID;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceLocation() {
		return serviceLocation;
	}

	public void setServiceLocation(String serviceLocation) {
		this.serviceLocation = serviceLocation;
	}

	public String getAdapterModel() {
		return adapterModel;
	}

	public void setAdapterModel(String adapterModel) {
		this.adapterModel = adapterModel;
	}
	



}
