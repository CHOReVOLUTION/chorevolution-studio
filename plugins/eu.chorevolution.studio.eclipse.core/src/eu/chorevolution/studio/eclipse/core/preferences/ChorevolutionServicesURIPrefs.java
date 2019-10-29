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

public class ChorevolutionServicesURIPrefs extends ChorevolutionPrefs{

	public final static String PREF_APACHE_SYNCOPE_URI = "apache.syncope.uri.property";
	public final static String PREF_APACHE_SYNCOPE_USERNAME = "apache.syncope.username.property";
	public final static String PREF_APACHE_SYNCOPE_PASSWORD = "apache.syncope.password.property";
    public final static String PREF_APACHE_SYNCOPE_DOMAIN = "apache.syncope.domain.property";
    	
    public final static String PREF_SYNTHESIS_PROCESSOR_USERNAME = "synthesis.processor.username.property";
    public final static String PREF_SYNTHESIS_PROCESSOR_PASSWORD = "synthesis.processor.password.property";
    public final static String PREF_SYNTHESIS_PROCESSOR_TOKEN = "synthesis.processor.token.property";
    
    public final static String PREF_SYNTHESIS_PROCESSOR_NAME = "synthesis.processor.name.property";
    public final static String PREF_SYNTHESIS_PROCESSOR_URI = "synthesis.processor.uri.property";
    public final static String PREF_SYNTHESIS_PROCESSOR_KEY = "synthesis.processor.key.property";
    public final static String PREF_SYNTHESIS_PROCESSOR_LOGIN_URI = "synthesis.processor.login.uri.property";
    public final static String PREF_SYNTHESIS_PROCESSOR_BPMN2_CHOREOGRAPHY_PROJECTOR_URI = "synthesis.processor.bpmn2.choreography.projector.uri.property";
    public final static String PREF_SYNTHESIS_PROCESSOR_BPMN2_CHOREOGRAPHY_VALIDATOR_URI = "synthesis.processor.bpmn2.choreography.validator.uri.property";
    public final static String PREF_SYNTHESIS_PROCESSOR_ADAPTER_GENERATOR_URI = "synthesis.processor.adapter.generator.uri.property";
    public final static String PREF_SYNTHESIS_PROCESSOR_CD_GENERATOR_URI = "synthesis.processor.cd.generator.uri.property";
    public final static String PREF_SYNTHESIS_PROCESSOR_BC_GENERATOR_URI = "synthesis.processor.bc.generator.uri.property";
    public final static String PREF_SYNTHESIS_PROCESSOR_SF_GENERATOR_URI = "synthesis.processor.sf.generator.uri.property";
    public final static String PREF_SYNTHESIS_PROCESSOR_CHOREOGRAPHY_ARCHITECTURE_GENERATOR_URI = "synthesis.processor.choreography.architecture.generator.uri.property";
    public final static String PREF_SYNTHESIS_PROCESSOR_CHOREOGRAPHY_DEPLOYMENT_DESCRIPTION_GENERATOR_URI = "synthesis.processor.choreography.deployment.description.generator.uri.property";
    
	public final static String PREF_CHOREOGRAPHY_ID = "synthesis.choreography.id.property";
	public final static String PREF_CHOREOGRAPHY_NAME = "synthesis.choreography.name.property";    
	public final static String PREF_CHOREOGRAPHY_NAMESPACE = "synthesis.choreography.namespace.property";    
	
	public ChorevolutionServicesURIPrefs() {
		defaultPropertyValues =  new ArrayList<ChorevolutionPreferenceData>();
		defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_APACHE_SYNCOPE_URI, "Apache Syncope URI", "http://localhost:9080/syncope/rest/", ""));
		defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_APACHE_SYNCOPE_USERNAME, "Username", "admin", ""));
		defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_APACHE_SYNCOPE_PASSWORD, "Password", "password", ""));
        defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_APACHE_SYNCOPE_DOMAIN, "Domain", "Master", ""));
        
        defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_SYNTHESIS_PROCESSOR_USERNAME, "Username", "admin", ""));
        defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_SYNTHESIS_PROCESSOR_PASSWORD, "Password", "password", ""));
        defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_SYNTHESIS_PROCESSOR_TOKEN, "Token", "_", ""));
        
        defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_SYNTHESIS_PROCESSOR_NAME, "Synthesis Processor Name", "default_name", ""));
        defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_SYNTHESIS_PROCESSOR_URI, "Synthesis Processor URI", "default_uri", ""));
        defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_SYNTHESIS_PROCESSOR_KEY, "Synthesis Processor KEY", "default_key", ""));
        defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_SYNTHESIS_PROCESSOR_LOGIN_URI, "Synthesis Processor Login URI", "http://localhost:9091/synthesisprocessor/login/", ""));
        defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_SYNTHESIS_PROCESSOR_BPMN2_CHOREOGRAPHY_PROJECTOR_URI, "BPMN2 Choreography Projection Generator URI", "http://localhost:9091/synthesisprocessor/choreographyprojectiongenerator/", ""));
        defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_SYNTHESIS_PROCESSOR_BPMN2_CHOREOGRAPHY_VALIDATOR_URI, "BPMN2 Choreography Validator URI", "http://localhost:9091/synthesisprocessor/choreographyvalidator/", ""));
        defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_SYNTHESIS_PROCESSOR_ADAPTER_GENERATOR_URI, "Adapter Generator URI", "http://localhost:9091/synthesisprocessor/adaptergenerator/", ""));
        defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_SYNTHESIS_PROCESSOR_CD_GENERATOR_URI, "Coordination Delegate Generator URI", "http://localhost:9091/synthesisprocessor/coordinationdelegategenerator/", ""));
        defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_SYNTHESIS_PROCESSOR_BC_GENERATOR_URI, "Binding Component Generator URI", "http://localhost:9091/synthesisprocessor/bindingComponentGenerator/", ""));
        defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_SYNTHESIS_PROCESSOR_SF_GENERATOR_URI, "Security Filter Generator URI", "http://localhost:9091/synthesisprocessor/securityfiltergenerator/", ""));
        defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_SYNTHESIS_PROCESSOR_CHOREOGRAPHY_ARCHITECTURE_GENERATOR_URI, "Choreography Architecture Generator URI", "http://localhost:9091/synthesisprocessor/choreographyarchitecturegenerator/", ""));
        defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_SYNTHESIS_PROCESSOR_CHOREOGRAPHY_DEPLOYMENT_DESCRIPTION_GENERATOR_URI, "Choreography Deployment Descriptor Generator URI", "http://localhost:9091/synthesisprocessor/choreographydeploymentdescriptorgenerator/", ""));

		defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_CHOREOGRAPHY_NAME, "Choreography Name", "choreography_name", ""));
		defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_CHOREOGRAPHY_NAMESPACE, "Choreography Namespace", "http://eu.chorevolution", ""));
		defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_CHOREOGRAPHY_ID, "Choreography ID", "null", ""));
	}

	@Override
	public List<ChorevolutionPreferenceData> restoreDefaults() {
		return defaultPropertyValues;
	}
}
