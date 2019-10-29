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

public class CorrelationChoreographyTaskTableViewerRecord {
	public static final String NONE_CORRELLATION = "none";
	
	private String choreographyTask;
	private String correlatedWith;
	
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	public CorrelationChoreographyTaskTableViewerRecord(String choreographyTask) {
		this.choreographyTask = choreographyTask;
		this.correlatedWith="";
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public String getChoreographyTask() {
		return choreographyTask;
	}

	public String getCorrelatedWith() {
		return correlatedWith;
	}

	public void setChoreographyTask(String choreographyTask) {
		propertyChangeSupport.firePropertyChange("choreographyTask", this.choreographyTask,
		        this.choreographyTask = choreographyTask);
	}
	
	public void setCorrelatedWith(String correlatedWith) {
		propertyChangeSupport.firePropertyChange("correlatedWith", this.correlatedWith,
		        this.correlatedWith = correlatedWith);
	}
}
