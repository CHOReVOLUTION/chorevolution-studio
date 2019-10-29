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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import eu.chorevolution.studio.eclipse.core.ChorevolutionCoreUtils;
import eu.chorevolution.studio.eclipse.core.internal.project.model.impl.ChorevolutionSynthesisProjectStructureFolder;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServicesURIPrefs;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionSynthesisSourceModelPrefs;
import eu.chorevolution.studio.eclipse.core.utils.syncope.ServiceAuthenticationType;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.GeneratedArtifactSynthesisProcessor;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.SecurityFilterGenerator;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.rest.util.SynthesisProcessorClientUtils;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.ParticipantTableViewerRecord;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.SecurityFilter;
import eu.chorevolution.synthesisprocessor.rest.api.SecurityFilterGeneratorRequest;
import eu.chorevolution.synthesisprocessor.rest.api.SecurityFilterGeneratorResponse;
import eu.chorevolution.synthesisprocessor.rest.api.client.SecurityFilterGeneratorClient;
import eu.chorevolution.synthesisprocessor.rest.api.client.SynthesisProcessorClient;
import eu.chorevolution.transformations.generativeapproach.choreographyarchitecturegenerator.model.ComponentData;
import eu.chorevolution.synthesisprocessor.rest.api.ConnectionAccountType;

public class SecurityFilterGeneratorRest extends SecurityFilterGenerator {
	private GeneratedArtifactSynthesisProcessor generatedArtifactSynthesisProcessor;
	
	private String choreographyname;
	private IProgressMonitor monitor;
	private SecurityFilter securityFilter;
	private List<ParticipantTableViewerRecord> providerParticipants;
	private List<ParticipantTableViewerRecord> clientParticipants;
	private SecurityFilterGeneratorClient client;
	private SynthesisProcessorClient synthesisProcessorClient;

	public SecurityFilterGeneratorRest(String choreographyname, List<ParticipantTableViewerRecord> providerParticipants,
			List<ParticipantTableViewerRecord> clientParticipants, SecurityFilter securityFilter,
			GeneratedArtifactSynthesisProcessor generatedArtifactSynthesisProcessor, IProject project,
			IProgressMonitor monitor) {
		super(project);
		this.choreographyname = choreographyname;
		this.generatedArtifactSynthesisProcessor = generatedArtifactSynthesisProcessor;
		this.monitor = monitor;
		this.providerParticipants = providerParticipants;
		this.clientParticipants = clientParticipants;
		this.securityFilter = securityFilter;
		this.client = new SecurityFilterGeneratorClient(super.getPropertyValues()
				.get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_SF_GENERATOR_URI).getValue());
		
		// TODO improve this lines
		String uriCdGenerator = super.getPropertyValues()
				.get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_CD_GENERATOR_URI).getValue();
		this.synthesisProcessorClient = new SynthesisProcessorClient(uriCdGenerator.replace("coordinationdelegategenerator/", ""));
			
	}

	@Override
	public void storeSecurityFilters() throws Exception {
		monitor.beginTask("Security Filter Generator", providerParticipants.size() + clientParticipants.size());

		for (ParticipantTableViewerRecord participantTableViewerRecord : providerParticipants) {
			if (monitor.isCanceled()) {
				return;
			}

			monitor.subTask("Generate Security Filter for Secured Provider Service: "
					+ participantTableViewerRecord.getRecordLabel());
			storeSecurityFilterProviderService(participantTableViewerRecord);
			monitor.worked(1);
		}

		for (ParticipantTableViewerRecord participantTableViewerRecord : clientParticipants) {
			if (monitor.isCanceled()) {
				return;
			}

			monitor.subTask("Generate Security Filter for Client: " + participantTableViewerRecord.getRecordLabel());
			storeSecurityFilterClient(participantTableViewerRecord);
			monitor.worked(1);
		}

	}

	private void storeSecurityFilterClient(ParticipantTableViewerRecord participantTableViewerRecord) throws Exception {
		// generate the Sf for the client if the choreography is secured
		if (!securityFilter.isSecuredChoreography()) {
			return;
		}
		
		IFolder participantFolder = ChorevolutionCoreUtils.createFolder(participantTableViewerRecord.getParticipant(),
				super.getProject().getFolder(ChorevolutionSynthesisProjectStructureFolder.SYNTHESIS_PROCESSOR_FOLDER_NAME)
						.getFolder(super.getPropertyValues()
								.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_SECURITYFILTER)
								.getValue()));

		List<String> securityRoles = new ArrayList<String>();
		securityFilter.getselectedSecurityRoleRecords().forEach(item -> {
			securityRoles.add(item.getName());
		});

		String securityFilterName = SECURITY_FILTER_PREFIX_NAME
				+ ChorevolutionCoreUtils.removeBlankSpaces(participantTableViewerRecord.getParticipant());
		String serviceInventoryDomain = super.getPropertyValues()
				.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_DOMAIN).getValue();

		SecurityFilterGeneratorRequest request = new SecurityFilterGeneratorRequest(choreographyname, securityFilterName,
				serviceInventoryDomain, securityRoles, super.getPropertyValues()
				.get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_TOKEN)
				.getValue());
		SecurityFilterGeneratorResponse response = client.generateSecurityFilterClient(request);

		if (response != null && response.getLocation()!= null && !response.getLocation().isEmpty()) {
			ChorevolutionCoreUtils.createFile(securityFilterName + "." + WAR_EXTENSION,
					participantFolder, SynthesisProcessorClientUtils.getArtifactContent(synthesisProcessorClient,
							response.getLocation()));
			
			ComponentData clientComponentData = generatedArtifactSynthesisProcessor
					.getClientComponent(participantTableViewerRecord.getParticipant());
			generatedArtifactSynthesisProcessor.addSecurityFilterToClientComponent(clientComponentData,
					securityFilterName,
					response.getLocation());
		}

		super.getProject().refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
	}

	private void storeSecurityFilterProviderService(ParticipantTableViewerRecord participantTableViewerRecord)
			throws Exception {
		// generate security filter only service that has security information
		if (participantTableViewerRecord.getService() != null
				&& participantTableViewerRecord.getService().getSecurityDescriptionType() == null) {
			return;
		}
		
		IFolder participantFolder = ChorevolutionCoreUtils.createFolder(participantTableViewerRecord.getParticipant(),
				super.getProject().getFolder(ChorevolutionSynthesisProjectStructureFolder.SYNTHESIS_PROCESSOR_FOLDER_NAME)
						.getFolder(super.getPropertyValues()
								.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_SECURITYFILTER)
								.getValue()));

		List<String> securityRoles = new ArrayList<String>();
		securityFilter.getselectedSecurityRoleRecords().forEach(item -> {
			securityRoles.add(item.getName());
		});

		/*
		ConnectionAccount connectionAccount;
		if (participantTableViewerRecord.getService()
				.getServiceAuthenticationType() == ServiceAuthenticationType.SHARED) {
			connectionAccount = new LoginPasswordConnectionAccount();

			((LoginPasswordConnectionAccount) connectionAccount)
					.setLogin(participantTableViewerRecord.getSecurityServiceAuthenticationSharedUsername());
			((LoginPasswordConnectionAccount) connectionAccount)
					.setPassword(participantTableViewerRecord.getSecurityServiceAuthenticationSharedPassword());

		} else {
			connectionAccount = null;
		}
		*/
		
		String securityFilterName = SECURITY_FILTER_PREFIX_NAME
				+ ChorevolutionCoreUtils.removeBlankSpaces(participantTableViewerRecord.getParticipant());
		String serviceInventoryDomain = super.getPropertyValues()
				.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_DOMAIN).getValue();

		SecurityFilterGeneratorRequest request = null;

		if (participantTableViewerRecord.getService()
				.getServiceAuthenticationType() == ServiceAuthenticationType.SHARED) {
			request = new SecurityFilterGeneratorRequest(choreographyname, securityFilterName, serviceInventoryDomain, securityRoles,
					participantTableViewerRecord.getService().getSecurityDescriptionContent(),
					ConnectionAccountType.USERNAME_PASSWORD,
					participantTableViewerRecord.getSecurityServiceAuthenticationSharedUsername(),
					participantTableViewerRecord.getSecurityServiceAuthenticationSharedPassword(), null, super.getPropertyValues()
					.get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_TOKEN)
					.getValue());
		} else {
			request = new SecurityFilterGeneratorRequest(choreographyname, securityFilterName, serviceInventoryDomain, securityRoles,
					participantTableViewerRecord.getService().getSecurityDescriptionContent(), null, null, null, 
					participantTableViewerRecord.getService().getCustomAuthFileJAR(), super.getPropertyValues()
					.get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_TOKEN)
					.getValue());
		}

		SecurityFilterGeneratorResponse response = client.generateSecurityFilterClient(request);

		if (response != null && response.getLocation()!= null && !response.getLocation().isEmpty()) {
			ChorevolutionCoreUtils.createFile(securityFilterName + "." + WAR_EXTENSION,
					participantFolder, SynthesisProcessorClientUtils.getArtifactContent(synthesisProcessorClient,
							response.getLocation()));
			
			ComponentData providerComponentData = generatedArtifactSynthesisProcessor
					.getProviderComponent(participantTableViewerRecord.getParticipant());

			generatedArtifactSynthesisProcessor.addSecurityFilterToProviderComponent(providerComponentData,
					securityFilterName,
					response.getLocation());
		}

		super.getProject().refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
	}

}
