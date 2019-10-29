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

public class ParticipantTableViewerRecord {
	private String participant;
	private String serviceProjectName;
	private boolean generate;
	private Service service;
	
	private String securityServiceAuthenticationSharedUsername;
	private String securityServiceAuthenticationSharedPassword;
	
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	public ParticipantTableViewerRecord(String participant) {
		this.participant = participant;
		this.service = null;
		this.serviceProjectName="";
		this.securityServiceAuthenticationSharedUsername = "";
		this.securityServiceAuthenticationSharedPassword = "";
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public String getParticipant() {
		return participant;
	}

	public Service getService() {
		return service;
	}
	
	public boolean isGenerate() {
		return generate;
	}

	public String getServiceProjectName() {
		return serviceProjectName;
	}
	
	public String getSecurityServiceAuthenticationSharedUsername() {
		return securityServiceAuthenticationSharedUsername;
	}

	public String getSecurityServiceAuthenticationSharedPassword() {
		return securityServiceAuthenticationSharedPassword;
	}
	
	public void setParticipant(String participant) {
		propertyChangeSupport.firePropertyChange("participant", this.participant,
		        this.participant = participant);
	}
	
	public void setService(Service providerService) {
		propertyChangeSupport.firePropertyChange("service", this.service,
		        this.service = providerService);
	}

	public void setGenerate(boolean generate) {
		propertyChangeSupport.firePropertyChange("generate", this.generate,
		        this.generate = generate);
	}

	public void setServiceProjectName(String serviceProjectName) {
		propertyChangeSupport.firePropertyChange("serviceProjectName", this.serviceProjectName,
		        this.serviceProjectName = serviceProjectName);
	}
	
	public void setSecurityServiceAuthenticationSharedUsername(String securityServiceAuthenticationSharedUsername) {
		propertyChangeSupport.firePropertyChange("securityServiceAuthenticationSharedUsername", this.securityServiceAuthenticationSharedUsername,
		        this.securityServiceAuthenticationSharedUsername = securityServiceAuthenticationSharedUsername);
	}

	public void setSecurityServiceAuthenticationSharedPassword(String securityServiceAuthenticationSharedPassword) {
		propertyChangeSupport.firePropertyChange("securityServiceAuthenticationSharedPassword", this.securityServiceAuthenticationSharedPassword,
		        this.securityServiceAuthenticationSharedPassword = securityServiceAuthenticationSharedPassword);
	}

	public String getRecordLabel() {
		return this.service == null ? participant
				: participant + " -> " + service.getName() + " (ID: " + service.getKey() + ")";
	}
}
