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

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;

import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServicesURIPrefs;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.ChoreographyValidator;
import eu.chorevolution.synthesisprocessor.rest.api.ChoreographyValidatorRequest;
import eu.chorevolution.synthesisprocessor.rest.api.ChoreographyValidatorResponse;
import eu.chorevolution.synthesisprocessor.rest.api.client.ChoreographyValidatorClient;

public class ChoreographyValidatorRest extends ChoreographyValidator {
	private String choreographyName;
	private byte[] bpmn2Content;
	private byte[] typesContent;
	private ChoreographyValidatorClient client;
	private ChoreographyValidatorResponse response;

	public ChoreographyValidatorRest(IProject project, String choreographyName, byte[] bpmn2Content,
			byte[] typesContent) {
		super(project);
		this.choreographyName = choreographyName;
		this.bpmn2Content = bpmn2Content;
		this.typesContent = typesContent;
		client = new ChoreographyValidatorClient(super.getPropertyValues()
				.get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_BPMN2_CHOREOGRAPHY_VALIDATOR_URI)
				.getValue());
	
	
		//executes login
		SynthesisProcessorAccessControlRest spacr= new SynthesisProcessorAccessControlRest(project);

		try {
			if(!spacr.executeLogin()) {
				MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
						"Login incorrect",
						"The username or the password of the Synthesis Processor are incorrect");
				return;			
			}
		} catch (Exception e) {
			MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
					"Unable to do Login",
					e.toString());
		}
	
	}

	@Override
	public boolean validateBpmn2ChoreographyDiagram() throws Exception {
		ChoreographyValidatorRequest request = new ChoreographyValidatorRequest(choreographyName, bpmn2Content,
				typesContent, super.getPropertyValues()
				.get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_TOKEN)
				.getValue());
		this.response = client.validateChoreography(request);
		return this.response.isChoreographyValidated();
	}

	@Override
	public String getErrors() {
		if (this.response != null) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("\n");
			for (String error : response.getErrors()) {
				stringBuilder.append(error+"\n");
			}
			return stringBuilder.toString();
		}
		return "";
	}

}
