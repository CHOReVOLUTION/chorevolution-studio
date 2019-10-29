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

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import eu.chorevolution.studio.eclipse.core.ChorevolutionCoreUtils;
import eu.chorevolution.studio.eclipse.core.internal.project.model.impl.ChorevolutionSynthesisProjectStructureFolder;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServicesURIPrefs;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionSynthesisSourceModelPrefs;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.ChoreographyArchitectureGenerator;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.GeneratedArtifactSynthesisProcessor;
import eu.chorevolution.synthesisprocessor.rest.api.ChoreographyArchitectureGeneratorRequest;
import eu.chorevolution.synthesisprocessor.rest.api.ChoreographyArchitectureGeneratorResponse;
import eu.chorevolution.synthesisprocessor.rest.api.client.ChoreographyArchitectureGeneratorClient;
import eu.chorevolution.synthesisprocessor.rest.business.model.ChoreographyArchitectureComponentData;
import eu.chorevolution.synthesisprocessor.rest.business.model.ChoreographyArchitectureComponentDependencyData;
import eu.chorevolution.transformations.generativeapproach.choreographyarchitecturegenerator.model.ComponentData;

public class ChoreographyArchitectureGeneratorRest extends ChoreographyArchitectureGenerator{

	private GeneratedArtifactSynthesisProcessor generatedArtifactSynthesisProcessor;
	private IFile bpmnFile;
	private IProgressMonitor monitor;
	private ChoreographyArchitectureGeneratorClient client;
	
	public ChoreographyArchitectureGeneratorRest(
			GeneratedArtifactSynthesisProcessor generatedArtifactSynthesisProcessor, IFile bpmnFile, IProject project,
			IProgressMonitor monitor) {
		super(project);
		this.generatedArtifactSynthesisProcessor = generatedArtifactSynthesisProcessor;
		this.bpmnFile = bpmnFile;
		this.monitor = monitor;
		this.client = new ChoreographyArchitectureGeneratorClient(super.getPropertyValues().get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_CHOREOGRAPHY_ARCHITECTURE_GENERATOR_URI).getValue());
	}

	@Override
	public void storeChoreographyArchitecture()
			throws Exception {
		monitor.beginTask("Choreography Architecture Generator", 1);
		
		ChoreographyArchitectureGeneratorRequest request = new ChoreographyArchitectureGeneratorRequest();
		request.setBpmn2Content(FileUtils.readFileToByteArray(bpmnFile.getRawLocation().makeAbsolute().toFile()));
		request.setAccessToken(super.getPropertyValues()
				.get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_TOKEN)
				.getValue());
		
		for (ComponentData componentData : generatedArtifactSynthesisProcessor
				.getClientComponents()) {
			ChoreographyArchitectureComponentData choreographyArchitectureComponentData = new ChoreographyArchitectureComponentData(
					componentData.getParticipantName(),
					componentData.getName(),
					removeSlashOnHttp(componentData.getLocation()));
			request.getClientParticipants().add(choreographyArchitectureComponentData);
			
			if (componentData.getBindingComponentData() != null) {
				ChoreographyArchitectureComponentDependencyData bindingComponentData = new ChoreographyArchitectureComponentDependencyData(
						componentData.getBindingComponentData().getName(),
						removeSlashOnHttp(componentData.getBindingComponentData().getLocation()));
				choreographyArchitectureComponentData.setBindingComponentData(bindingComponentData);
			}

			if (componentData.getConsumerComponentData() != null) {
				ChoreographyArchitectureComponentDependencyData consumerComponentData = new ChoreographyArchitectureComponentDependencyData(
						componentData.getConsumerComponentData().getName(),
						removeSlashOnHttp(componentData.getConsumerComponentData().getLocation()));
				choreographyArchitectureComponentData.setConsumerComponentData(consumerComponentData);
			
			}

			if (componentData.getSecurityComponentData() != null) {
				ChoreographyArchitectureComponentDependencyData securityComponentData = new ChoreographyArchitectureComponentDependencyData(
						componentData.getSecurityComponentData().getName(),
						removeSlashOnHttp(componentData.getSecurityComponentData().getLocation()));
				choreographyArchitectureComponentData.setSecurityComponentData(securityComponentData);
			
			}
			
			if (componentData.getAdapterComponentData() != null) {
				ChoreographyArchitectureComponentDependencyData adapterComponentData = new ChoreographyArchitectureComponentDependencyData(
						componentData.getAdapterComponentData().getName(),
						removeSlashOnHttp(componentData.getAdapterComponentData().getLocation()));
				choreographyArchitectureComponentData.setAdapterComponentData(adapterComponentData);
			
			}
		}

		for (ComponentData componentData : generatedArtifactSynthesisProcessor
				.getProsumerComponents()) {
			ChoreographyArchitectureComponentData choreographyArchitectureComponentData = new ChoreographyArchitectureComponentData(
					componentData.getParticipantName(),
					componentData.getName(),
					removeSlashOnHttp(componentData.getLocation()));
			request.getProsumerParticipants().add(choreographyArchitectureComponentData);
			
			if (componentData.getBindingComponentData() != null) {
				ChoreographyArchitectureComponentDependencyData bindingComponentData = new ChoreographyArchitectureComponentDependencyData(
						componentData.getBindingComponentData().getName(),
						removeSlashOnHttp(componentData.getBindingComponentData().getLocation()));
				choreographyArchitectureComponentData.setBindingComponentData(bindingComponentData);
			}

			if (componentData.getConsumerComponentData() != null) {
				ChoreographyArchitectureComponentDependencyData consumerComponentData = new ChoreographyArchitectureComponentDependencyData(
						componentData.getConsumerComponentData().getName(),
						removeSlashOnHttp(componentData.getConsumerComponentData().getLocation()));
				choreographyArchitectureComponentData.setConsumerComponentData(consumerComponentData);
			
			}

			if (componentData.getSecurityComponentData() != null) {
				ChoreographyArchitectureComponentDependencyData securityComponentData = new ChoreographyArchitectureComponentDependencyData(
						componentData.getSecurityComponentData().getName(),
						removeSlashOnHttp(componentData.getSecurityComponentData().getLocation()));
				choreographyArchitectureComponentData.setSecurityComponentData(securityComponentData);
			
			}
			
			if (componentData.getAdapterComponentData() != null) {
				ChoreographyArchitectureComponentDependencyData adapterComponentData = new ChoreographyArchitectureComponentDependencyData(
						componentData.getAdapterComponentData().getName(),
						removeSlashOnHttp(componentData.getAdapterComponentData().getLocation()));
				choreographyArchitectureComponentData.setAdapterComponentData(adapterComponentData);
			
			}
		}
		
		for (ComponentData  componentData : generatedArtifactSynthesisProcessor.getProviderComponents()) {
			ChoreographyArchitectureComponentData choreographyArchitectureComponentData = new ChoreographyArchitectureComponentData(
					componentData.getParticipantName(),
					componentData.getName(),
					removeSlashOnHttp(componentData.getLocation()));
			request.getProviderParticipants().add(choreographyArchitectureComponentData);
			
			if (componentData.getBindingComponentData() != null) {
				ChoreographyArchitectureComponentDependencyData bindingComponentData = new ChoreographyArchitectureComponentDependencyData(
						componentData.getBindingComponentData().getName(),
						removeSlashOnHttp(componentData.getBindingComponentData().getLocation()));
				choreographyArchitectureComponentData.setBindingComponentData(bindingComponentData);
			}

			if (componentData.getConsumerComponentData() != null) {
				ChoreographyArchitectureComponentDependencyData consumerComponentData = new ChoreographyArchitectureComponentDependencyData(
						componentData.getConsumerComponentData().getName(),
						removeSlashOnHttp(componentData.getConsumerComponentData().getLocation()));
				choreographyArchitectureComponentData.setConsumerComponentData(consumerComponentData);
			
			}

			if (componentData.getSecurityComponentData() != null) {
				ChoreographyArchitectureComponentDependencyData securityComponentData = new ChoreographyArchitectureComponentDependencyData(
						componentData.getSecurityComponentData().getName(),
						removeSlashOnHttp(componentData.getSecurityComponentData().getLocation()));
				choreographyArchitectureComponentData.setSecurityComponentData(securityComponentData);
			
			}
			
			if (componentData.getAdapterComponentData() != null) {
				ChoreographyArchitectureComponentDependencyData adapterComponentData = new ChoreographyArchitectureComponentDependencyData(
						componentData.getAdapterComponentData().getName(),
						removeSlashOnHttp(componentData.getAdapterComponentData().getLocation()));
				choreographyArchitectureComponentData.setAdapterComponentData(adapterComponentData);
			
			}
		}
		
		
		ChoreographyArchitectureGeneratorResponse choreographyArchitectureGeneratorResponse = client.generateChoreographyArchitecture(request);
		
		
		IFolder architectureFolder = bpmnFile.getProject()
				.getFolder(ChorevolutionSynthesisProjectStructureFolder.SYNTHESIS_PROCESSOR_FOLDER_NAME)
				.getFolder(super.getPropertyValues()
						.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_ARCHITECTURALSTYLE)
						.getValue());

		ChorevolutionCoreUtils.createFile(ChorevolutionCoreUtils.changeExtension(bpmnFile.getName(), CHOREOGRAPHY_ARCHITECTURE_FILE_EXTENSION),
				architectureFolder, choreographyArchitectureGeneratorResponse.getChoreographyArchitectureContent());

		super.getProject().refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());

		monitor.worked(1);
	}

	public String removeSlashOnHttp(String initialString) {
		if(initialString.startsWith("http")) {
			if(initialString.charAt(initialString.length()-1) == '/') {
				return initialString.substring(0, initialString.length()-1);
			}
		}
	
		return initialString;
	}
	
}
