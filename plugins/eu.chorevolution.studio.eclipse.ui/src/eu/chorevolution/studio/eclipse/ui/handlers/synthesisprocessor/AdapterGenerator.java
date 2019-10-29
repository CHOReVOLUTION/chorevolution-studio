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
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServiceThingSourceModelPrefs;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServicesURIPrefs;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionSynthesisSourceModelPrefs;
import eu.chorevolution.synthesisprocessor.rest.api.AdapterGeneratorResponse;
import eu.chorevolution.studio.eclipse.core.utils.WSDLData;

public abstract class AdapterGenerator {
	public final static String ADAPTER_GENERATOR_PREFIX_NAME = "ad";
	public final static String WAR_EXTENSION = "war";
	public final static String TAR_GZ_EXTENSION = "tar.gz";
	public final static String WSDL_EXTENSION = "wsdl";
	public final static String ADAPTER_EXTENSION = "adapter";
	public final static String EMBEDDED_PREFIX_LOCATION = "file://";
	
	private IProject project;
	private Map<String, ChorevolutionPreferenceData> propertyValues;

	public AdapterGenerator(IProject project) {
		this.project = project;
		
		ChorevolutionSynthesisSourceModelPrefs chorevolutionSynthesisModelPrefs = new ChorevolutionSynthesisSourceModelPrefs();
		propertyValues = chorevolutionSynthesisModelPrefs.readProjectOrWorkspacePreferences(project);
		propertyValues.putAll((new ChorevolutionServicesURIPrefs()).readProjectOrWorkspacePreferences(project));
		propertyValues.putAll((new ChorevolutionServiceThingSourceModelPrefs()).readProjectOrWorkspacePreferences(project));

	}

	public IProject getProject() {
		return project;
	}

	public Map<String, ChorevolutionPreferenceData> getPropertyValues() {
		return propertyValues;
	}

	public abstract List<WSDLData> generateAndStoreAdapters() throws Exception;

}
