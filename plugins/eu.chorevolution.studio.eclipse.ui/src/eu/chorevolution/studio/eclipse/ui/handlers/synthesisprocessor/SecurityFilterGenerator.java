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

public abstract class SecurityFilterGenerator {
	public final static String SECURITY_FILTER_PREFIX_NAME = "sf";
	public final static String WAR_EXTENSION = "war";
	public final static String EMBEDDED_PREFIX_LCOATION = "file://";
	 
	private IProject project;
	
	private Map<String, ChorevolutionPreferenceData> propertyValues;
	
	public SecurityFilterGenerator(IProject project) {
		this.project = project;
		
		ChorevolutionSynthesisSourceModelPrefs chorevolutionSynthesisModelPrefs = new ChorevolutionSynthesisSourceModelPrefs();
		propertyValues = chorevolutionSynthesisModelPrefs
				.readProjectOrWorkspacePreferences(project);
		propertyValues
				.putAll((new ChorevolutionServicesURIPrefs()).readProjectOrWorkspacePreferences(project));
	}

	public abstract void storeSecurityFilters() throws Exception;
	
	public IProject getProject() {
		return project;
	}
	
	public Map<String, ChorevolutionPreferenceData> getPropertyValues() {
		return propertyValues;
	}

}
