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
package eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;

import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPreferenceData;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServicesURIPrefs;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionSynthesisSourceModelPrefs;
import eu.chorevolution.studio.eclipse.core.utils.WSDLData;

public abstract class ChoreographyProjection {
	public static final String XSD_FILE_NAME = "types";
	public static final String XSD_FILE_EXTENSION = "xsd";
	public static final String BPMN2_FILE_EXTENSION = "bpmn2";

	private IProject project;

	private List<WSDLData> wsdlDatas;
	private Map<String, ChorevolutionPreferenceData> propertyValues;

	public ChoreographyProjection(IProject project, List<WSDLData> wsdlDatas) {
		this.project = project;
		this.wsdlDatas = wsdlDatas;

		ChorevolutionSynthesisSourceModelPrefs chorevolutionSynthesisModelPrefs = new ChorevolutionSynthesisSourceModelPrefs();
		propertyValues = chorevolutionSynthesisModelPrefs.readProjectOrWorkspacePreferences(project);
		propertyValues.putAll((new ChorevolutionServicesURIPrefs()).readProjectOrWorkspacePreferences(project));
	}

	public abstract void storeProjections() throws Exception;
	
	public IProject getProject() {
		return project;
	}

	public Map<String, ChorevolutionPreferenceData> getPropertyValues() {
		return propertyValues;
	}

	public List<WSDLData> getWsdlDatas() {
		return wsdlDatas;
	}

}
