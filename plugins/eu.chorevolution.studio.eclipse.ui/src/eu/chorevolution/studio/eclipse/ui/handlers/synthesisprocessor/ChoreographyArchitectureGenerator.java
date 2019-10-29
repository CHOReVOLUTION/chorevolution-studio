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

import java.util.Map;

import org.eclipse.core.resources.IProject;

import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPreferenceData;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServicesURIPrefs;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionSynthesisSourceModelPrefs;

public abstract class ChoreographyArchitectureGenerator {
	public static final String CHOREOGRAPHY_ARCHITECTURE_FILE_EXTENSION = "chorarch";
	private IProject project;
	private Map<String, ChorevolutionPreferenceData> propertyValues;

	public ChoreographyArchitectureGenerator(IProject project) {
		this.project = project;

		ChorevolutionSynthesisSourceModelPrefs chorevolutionSynthesisSourceModelPrefs = new ChorevolutionSynthesisSourceModelPrefs();
		propertyValues = chorevolutionSynthesisSourceModelPrefs.readProjectOrWorkspacePreferences(project);
		propertyValues.putAll((new ChorevolutionServicesURIPrefs()).readProjectOrWorkspacePreferences(project));
	}

	public abstract void storeChoreographyArchitecture()
			throws Exception;

	public IProject getProject() {
		return project;
	}
	
	public Map<String, ChorevolutionPreferenceData> getPropertyValues() {
		return propertyValues;
	}

}
