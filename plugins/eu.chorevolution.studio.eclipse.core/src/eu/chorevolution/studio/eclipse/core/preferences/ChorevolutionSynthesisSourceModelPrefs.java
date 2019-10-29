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
package eu.chorevolution.studio.eclipse.core.preferences;

import java.util.ArrayList;
import java.util.List;

public class ChorevolutionSynthesisSourceModelPrefs extends ChorevolutionPrefs {


	public final static String PREF_SYNTHESISPROCESSOR_COORD = "synthesis.coord.sourcefolder.property";
	public final static String PREF_SYNTHESISPROCESSOR_ADAPTER = "synthesis.adapter.sourcefolder.property";
	public final static String PREF_SYNTHESISPROCESSOR_ADAPTER_ARTIFACT = "synthesis.adapter.artifact.sourcefolder.property";
	public final static String PREF_SYNTHESISPROCESSOR_ADAPTER_MODEL = "synthesis.adapter.model.sourcefolder.property";
	public final static String PREF_SYNTHESISPROCESSOR_SECURITYFILTER = "synthesis.securityfilter.sourcefolder.property";
	public final static String PREF_SYNTHESISPROCESSOR_BINDINGCOMPONENT = "synthesis.bindingcomponent.sourcefolder.property";
	public final static String PREF_SYNTHESISPROCESSOR_BINDINGCOMPONENT_ARTIFACT = "synthesis.bindingcomponent.artifact.sourcefolder.property";
	public final static String PREF_SYNTHESISPROCESSOR_BINDINGCOMPONENT_MODEL = "synthesis.bindingcomponent.model.sourcefolder.property";
	public final static String PREF_SYNTHESISPROCESSOR_ARCHITECTURALSTYLE = "synthesis.architecturalstyle.sourcefolder.property";

	public final static String PREF_CHOREOGRAPHYDEPLOYMENT = "synthesis.choreographydeployment.sourcefolder.property";
	public final static String PREF_SYNTHESIS_GENERATION = "synthesis.syntesisgeneration.property";
	public static final String SYNTHESIS_GENERATOR_SOURCE_CODE = "SRC";
	public static final String SYNTHESIS_GENERATOR_EXECUTABLE_ARTEFACTS = "EXE";

	public final static String PREF_SERVICEINVENTORY_SERVICES = "synthesis.serviceinventory.sourcefolder.property";

	public ChorevolutionSynthesisSourceModelPrefs() {
		defaultPropertyValues = new ArrayList<ChorevolutionPreferenceData>();
		// defaultPropertyValues.add(new ChorevolutionPreferenceData(
		// PREF_ADDITIONALMODELS_VARIABILITY, "Variability", "Variability", ""));
		// defaultPropertyValues.add(new ChorevolutionPreferenceData(
		// PREF_ADDITIONALMODELS_QOS, "QoS", "QoS", ""));
		// defaultPropertyValues.add(new ChorevolutionPreferenceData(
		// PREF_ADDITIONALMODELS_IDENTITY, "Identity", "Identity", ""));
		defaultPropertyValues.add(new ChorevolutionPreferenceData(PREF_SYNTHESISPROCESSOR_COORD,
				"Coordination Delegates", "Coordination Delegates", ""));
		defaultPropertyValues
				.add(new ChorevolutionPreferenceData(PREF_SYNTHESISPROCESSOR_ADAPTER, "Adapters", "Adapters", ""));
		defaultPropertyValues.add(
				new ChorevolutionPreferenceData(PREF_SYNTHESISPROCESSOR_ADAPTER_ARTIFACT, "artifact", "artifact", ""));
		defaultPropertyValues
				.add(new ChorevolutionPreferenceData(PREF_SYNTHESISPROCESSOR_ADAPTER_MODEL, "model", "model", ""));
		defaultPropertyValues.add(new ChorevolutionPreferenceData(PREF_SYNTHESISPROCESSOR_SECURITYFILTER,
				"Security Filters", "Security Filters", ""));
		defaultPropertyValues.add(new ChorevolutionPreferenceData(PREF_SYNTHESISPROCESSOR_BINDINGCOMPONENT,
				"Binding Components", "Binding Components", ""));
		defaultPropertyValues.add(new ChorevolutionPreferenceData(PREF_SYNTHESISPROCESSOR_BINDINGCOMPONENT_ARTIFACT,
				"artifact", "artifact", ""));
		defaultPropertyValues.add(
				new ChorevolutionPreferenceData(PREF_SYNTHESISPROCESSOR_BINDINGCOMPONENT_MODEL, "model", "model", ""));
		defaultPropertyValues.add(new ChorevolutionPreferenceData(PREF_CHOREOGRAPHYDEPLOYMENT,
				"Choreography Deployment", "Choreography Deployment", ""));
		defaultPropertyValues
				.add(new ChorevolutionPreferenceData(PREF_SERVICEINVENTORY_SERVICES, "Services", "Services", ""));
		defaultPropertyValues.add(new ChorevolutionPreferenceData(PREF_SYNTHESISPROCESSOR_ARCHITECTURALSTYLE,
				"Architecture", "Architecture", ""));
		defaultPropertyValues.add(new ChorevolutionPreferenceData(PREF_SYNTHESIS_GENERATION, "Synthesis Generation",
				SYNTHESIS_GENERATOR_SOURCE_CODE, ""));
	}

	@Override
	public List<ChorevolutionPreferenceData> restoreDefaults() {
		return defaultPropertyValues;
	}

}
