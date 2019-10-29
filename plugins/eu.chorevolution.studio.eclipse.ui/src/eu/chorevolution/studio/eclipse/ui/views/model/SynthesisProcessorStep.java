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
package eu.chorevolution.studio.eclipse.ui.views.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class SynthesisProcessorStep {
	private StatusTypesSynthesisProcessor stepTypeSynthesisProcessor;
	private String name;
	private Integer stepNumber;
	private PropertyChangeSupport propertyChangeSupport;

	public SynthesisProcessorStep() {
		super();
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public SynthesisProcessorStep(StatusTypesSynthesisProcessor stepTypeSynthesisProcessor, String name, Integer stepNumber) {
		super();
		this.stepTypeSynthesisProcessor = stepTypeSynthesisProcessor;
		this.name = name;
		propertyChangeSupport = new PropertyChangeSupport(this);
		this.stepNumber = stepNumber;
	}

	public StatusTypesSynthesisProcessor getStepTypeSynthesisProcessor() {
		return stepTypeSynthesisProcessor;
	}

	public void setStepTypeSynthesisProcessor(StatusTypesSynthesisProcessor stepTypeSynthesisProcessor) {
		propertyChangeSupport.firePropertyChange("stepTypeSynthesisProcessor", this.stepTypeSynthesisProcessor, this.stepTypeSynthesisProcessor = stepTypeSynthesisProcessor);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		propertyChangeSupport.firePropertyChange("name", this.name, this.name = name);
	}
	
	public Integer getStepNumber(){
		return stepNumber;
	}

}
