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
package eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.rest;

import java.util.Map;

import org.eclipse.core.resources.IProject;

import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePlugin;
import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePreferences;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPreferenceData;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServicesURIPrefs;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.SynthesisProcessorAccessControl;
import eu.chorevolution.synthesisprocessor.rest.api.SynthesisProcessorLoginRequest;
import eu.chorevolution.synthesisprocessor.rest.api.SynthesisProcessorLoginResponse;
import eu.chorevolution.synthesisprocessor.rest.api.authentication.SynthesisProcessorEncrypter;
import eu.chorevolution.synthesisprocessor.rest.api.client.SynthesisProcessorAccessControlClient;

public class SynthesisProcessorAccessControlRest extends SynthesisProcessorAccessControl {
	

	private SynthesisProcessorAccessControlClient client;
	private SynthesisProcessorLoginResponse response;
	private Map<String, ChorevolutionPreferenceData> propertyValues;

	
	public SynthesisProcessorAccessControlRest(IProject project) {
		super(project);
		ChorevolutionCorePreferences prefs = ChorevolutionCorePreferences.getProjectOrWorkspacePreferences(this.getProject(), ChorevolutionCorePlugin.PLUGIN_ID);

		client = new SynthesisProcessorAccessControlClient(super.getPropertyValues()
				.get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_LOGIN_URI)
				.getValue());
		}

	@Override
	public boolean executeLogin() throws Exception {

		SynthesisProcessorLoginRequest request = new SynthesisProcessorLoginRequest();
		request.setUsername(SynthesisProcessorEncrypter.encrypt(SynthesisProcessorEncrypter.getKey(), SynthesisProcessorEncrypter.getInitVector(), super.getPropertyValues()
				.get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_USERNAME)
				.getValue()));
		request.setPassword(SynthesisProcessorEncrypter.encrypt(SynthesisProcessorEncrypter.getKey(), SynthesisProcessorEncrypter.getInitVector(), super.getPropertyValues()
				.get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_PASSWORD)
				.getValue()));

		this.response = client.executeLogin(request);
		
		if(this.response.isUserLogged()) {
			//i save the token into preferencies
			ChorevolutionCorePreferences prefs = ChorevolutionCorePreferences.getProjectOrWorkspacePreferences(this.getProject(), ChorevolutionCorePlugin.PLUGIN_ID);
	        prefs.putString(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_TOKEN, this.response.getToken());
		}
		else {
			//reset the token
			ChorevolutionCorePreferences prefs = ChorevolutionCorePreferences.getProjectOrWorkspacePreferences(this.getProject(), ChorevolutionCorePlugin.PLUGIN_ID);
	        prefs.putString(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_TOKEN, "_");			
		}
		
		return this.response.isUserLogged();
	}


	
	

}
