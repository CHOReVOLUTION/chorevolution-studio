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

import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import eu.chorevolution.studio.eclipse.core.ChorevolutionCoreUtils;
import eu.chorevolution.studio.eclipse.core.internal.project.model.impl.ChorevolutionSynthesisProjectStructureFolder;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServiceThingSourceModelPrefs;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServicesURIPrefs;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionSynthesisSourceModelPrefs;
import eu.chorevolution.studio.eclipse.core.utils.WSDLData;
import eu.chorevolution.studio.eclipse.core.utils.syncope.InterfaceDescriptionType;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.BindingComponentGenerator;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.GeneratedArtifactSynthesisProcessor;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.ParticipantTableViewerRecord;
import eu.chorevolution.synthesisprocessor.rest.api.BindingComponentGeneratorRequest;
import eu.chorevolution.synthesisprocessor.rest.api.BindingComponentGeneratorResponse;
import eu.chorevolution.synthesisprocessor.rest.api.client.BindingComponentGeneratorClient;
import eu.chorevolution.synthesisprocessor.rest.api.client.SynthesisProcessorClient;
import eu.chorevolution.synthesisprocessor.rest.business.model.BindingComponentProtocolType;
import eu.chorevolution.transformations.generativeapproach.choreographyarchitecturegenerator.model.ComponentData;

public class BindingComponentGeneratorRest extends BindingComponentGenerator {
	private GeneratedArtifactSynthesisProcessor generatedArtifactSynthesisProcessor;

	private String choreographyname;
	private IProgressMonitor monitor;
	private List<ParticipantTableViewerRecord> providerParticipants;
	private boolean generateBindingComponentClient;
	private List<ParticipantTableViewerRecord> clientParticipants;
	private BindingComponentGeneratorClient client;
	private SynthesisProcessorClient synthesisProcessorClient;
	private List<WSDLData> providerData;
	private List<WSDLData> wsdlOuterData;
	private int bcGenerationType;
	private String fileExtension;

	public BindingComponentGeneratorRest(String choreographyname,
			List<ParticipantTableViewerRecord> providerParticipants, boolean generateBindingComponentClient,
			List<ParticipantTableViewerRecord> clientParticipants,
			GeneratedArtifactSynthesisProcessor generatedArtifactSynthesisProcessor, IProject project,
			List<WSDLData> providerData, List<WSDLData> wsdlOuterData, IProgressMonitor monitor) {
		super(project);
		this.choreographyname = choreographyname;

		this.providerData = providerData;
		this.wsdlOuterData = wsdlOuterData;

		this.generatedArtifactSynthesisProcessor = generatedArtifactSynthesisProcessor;
		this.monitor = monitor;
		this.providerParticipants = providerParticipants;
		this.generateBindingComponentClient = generateBindingComponentClient;
		this.clientParticipants = clientParticipants;
		this.client = new BindingComponentGeneratorClient(super.getPropertyValues()
				.get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_BC_GENERATOR_URI).getValue());

		// TODO improve this lines
		String uriCdGenerator = super.getPropertyValues()
				.get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_CD_GENERATOR_URI).getValue();
		this.synthesisProcessorClient = new SynthesisProcessorClient(
				uriCdGenerator.replace("coordinationdelegategenerator/", ""));
		if (ChorevolutionSynthesisSourceModelPrefs.SYNTHESIS_GENERATOR_SOURCE_CODE
				.equalsIgnoreCase(super.getPropertyValues()
						.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESIS_GENERATION).getValue())) {
			bcGenerationType = BindingComponentGeneratorRequest.BC_GENERATION_TYPE_SRC;
			fileExtension = TAR_GZ_EXTENSION;
		} else {
			bcGenerationType = BindingComponentGeneratorRequest.BC_GENERATION_TYPE_WAR;
			fileExtension = WAR_EXTENSION;
		}
	}

	@Override
	public void storeBindingComponents() throws Exception {
		if (generateBindingComponentClient) {
			monitor.beginTask("Binding Component Generator", providerParticipants.size() + clientParticipants.size());
		} else {
			monitor.beginTask("Binding Component Generator", providerParticipants.size());
		}

		for (ParticipantTableViewerRecord participantTableViewerRecord : providerParticipants) {
			if (monitor.isCanceled()) {
				return;
			}

			monitor.subTask("Generate Binding Component for Provider Service: "
					+ participantTableViewerRecord.getRecordLabel());
			WSDLData wsdlData = storeBindingComponentProviderService(participantTableViewerRecord);

			if (wsdlData != null) {
				super.getWsdlDatas().add(wsdlData);
			}
			monitor.worked(1);
		}
		if (generateBindingComponentClient) {
			for (ParticipantTableViewerRecord participantTableViewerRecord : clientParticipants) {
				if (monitor.isCanceled()) {
					return;
				}

				monitor.subTask("Generate Binding Component for Client Participant: "
						+ participantTableViewerRecord.getRecordLabel());
				storeBindingComponentClientParticipant(participantTableViewerRecord);
				monitor.worked(1);
			}
		}

	}

	private WSDLData storeBindingComponentProviderService(ParticipantTableViewerRecord participantTableViewerRecord)
			throws Exception {
		// generate binding component only service that has interface
		// description different to WSDL
		if (participantTableViewerRecord.getService() != null
				&& participantTableViewerRecord.getService().getInterfaceDescriptionType() != null
				&& participantTableViewerRecord.getService()
						.getInterfaceDescriptionType() == InterfaceDescriptionType.WSDL) {
			return null;
		}

		String bindingComponentName = BINDING_COMPONENT_PREFIX_NAME
				+ ChorevolutionCoreUtils.removeBlankSpaces(participantTableViewerRecord.getParticipant());

		ChorevolutionCoreUtils.createFolder(participantTableViewerRecord.getParticipant(),
				super.getProject()
						.getFolder(ChorevolutionSynthesisProjectStructureFolder.SYNTHESIS_PROCESSOR_FOLDER_NAME)
						.getFolder(super.getPropertyValues()
								.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_BINDINGCOMPONENT)
								.getValue()));

		// now create the file in folders
		IFolder bcModelFolder = ChorevolutionCoreUtils.createFolder((super.getPropertyValues()
				.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_BINDINGCOMPONENT_MODEL).getValue()),
				super.getProject()
						.getFolder(ChorevolutionSynthesisProjectStructureFolder.SYNTHESIS_PROCESSOR_FOLDER_NAME)
						.getFolder(super.getPropertyValues()
								.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_BINDINGCOMPONENT)
								.getValue())
						.getFolder(participantTableViewerRecord.getParticipant()));

		IFolder bcArtifactFolder = ChorevolutionCoreUtils.createFolder(
				(super.getPropertyValues()
						.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_BINDINGCOMPONENT_ARTIFACT)
						.getValue()),
				super.getProject()
						.getFolder(ChorevolutionSynthesisProjectStructureFolder.SYNTHESIS_PROCESSOR_FOLDER_NAME)
						.getFolder(super.getPropertyValues()
								.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_BINDINGCOMPONENT)
								.getValue())
						.getFolder(participantTableViewerRecord.getParticipant()));

		IFile gidl = super.getProject()
				.getFolder(super.getPropertyValues()
						.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SERVICEINVENTORY_SERVICES).getValue())
				.getFolder(participantTableViewerRecord.getService().getName())
				.getFolder(super.getPropertyValues()
						.get(ChorevolutionServiceThingSourceModelPrefs.PREF_SERVICE_THING_INTERFACE_DESCRIPTION)
						.getValue())
				.getFile(participantTableViewerRecord.getService().getName() + "."
						+ participantTableViewerRecord.getService().getInterfaceDescriptionType().name().toLowerCase());

		BindingComponentGeneratorRequest request = new BindingComponentGeneratorRequest();
		request.setChoreographyName(choreographyname);
		request.setInterfaceDescriptionContent(
				FileUtils.readFileToByteArray(gidl.getRawLocation().makeAbsolute().toFile()));
		request.setBindingComponentName(bindingComponentName);
		request.setBindingComponentProtocolType(BindingComponentProtocolType.SOAP);
		request.setAccessToken(
				super.getPropertyValues().get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_TOKEN).getValue());

		request.setBcGenerationType(bcGenerationType);

		BindingComponentGeneratorResponse response = client.generateBindingComponent(request);

		// Save BC in the artifact folder
		try {
			ChorevolutionCoreUtils.createFile(bindingComponentName + "." + fileExtension, bcArtifactFolder,
					response.getBindingComponentContent());
		} catch (Exception e) {
		}

		ChorevolutionCoreUtils.createFile(bindingComponentName + "." + WSDL_EXTENSION, bcModelFolder,
				response.getWsdlContent());

		ChorevolutionCoreUtils.createFile(bindingComponentName + "." + GIDL_EXTENSION, bcModelFolder,
				FileUtils.readFileToByteArray(gidl.getRawLocation().makeAbsolute().toFile()));
		String location = response.getLocation();
		if (bcGenerationType == BindingComponentGeneratorRequest.BC_GENERATION_TYPE_SRC) {
			String projectName = ChorevolutionCoreUtils.unTar(response.getBindingComponentContent(),
					ChorevolutionUIPlugin.getWorkspace().getRoot().getLocation());

			IProject importedProject = ChorevolutionCoreUtils.importMavenProject(projectName);
			location = importedProject.getLocation().makeAbsolute().toOSString();
		}
		
		ComponentData providerComponentData = generatedArtifactSynthesisProcessor
				.getProviderComponent(participantTableViewerRecord.getParticipant());
		generatedArtifactSynthesisProcessor.addBindingComponentToProviderComponent(providerComponentData,
				bindingComponentName, location);

		WSDLData wsdlData = new WSDLData();
		wsdlData.setParticipantName(participantTableViewerRecord.getParticipant());
		wsdlData.setWsdl(response.getWsdlContent());

		super.getProject().refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());

		return wsdlData;
	}

	private void storeBindingComponentClientParticipant(ParticipantTableViewerRecord participantTableViewerRecord)
			throws Exception {
		String bindingComponentName = BINDING_COMPONENT_PREFIX_NAME
				+ ChorevolutionCoreUtils.removeBlankSpaces(participantTableViewerRecord.getParticipant());

		byte[] clientCDWSDL = null;
		for (WSDLData data : super.getWsdlDatas()) {
			if (data.getParticipantName().equals(participantTableViewerRecord.getParticipant())) {
				clientCDWSDL = data.getWsdl();
			}
		}
		for (WSDLData data : providerData) {
			if (data.getParticipantName().equals(participantTableViewerRecord.getParticipant())) {
				clientCDWSDL = data.getWsdl();
			}
		}
		for (WSDLData data : wsdlOuterData) {
			if (data.getParticipantName().equals(participantTableViewerRecord.getParticipant())) {
				clientCDWSDL = data.getWsdl();
			}
		}
		if (clientCDWSDL == null) {
			// TODO throw an exception
		}

		IFolder participantFolder = ChorevolutionCoreUtils.createFolder(participantTableViewerRecord.getParticipant(),
				super.getProject()
						.getFolder(ChorevolutionSynthesisProjectStructureFolder.SYNTHESIS_PROCESSOR_FOLDER_NAME)
						.getFolder(super.getPropertyValues()
								.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_BINDINGCOMPONENT)
								.getValue()));

		// now create the file in folders
		IFolder bcModelFolder = ChorevolutionCoreUtils.createFolder((super.getPropertyValues()
				.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_BINDINGCOMPONENT_MODEL).getValue()),
				super.getProject()
						.getFolder(ChorevolutionSynthesisProjectStructureFolder.SYNTHESIS_PROCESSOR_FOLDER_NAME)
						.getFolder(super.getPropertyValues()
								.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_BINDINGCOMPONENT)
								.getValue())
						.getFolder(participantTableViewerRecord.getParticipant()));

		IFolder bcArtifactFolder = ChorevolutionCoreUtils.createFolder(
				(super.getPropertyValues()
						.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_BINDINGCOMPONENT_ARTIFACT)
						.getValue()),
				super.getProject()
						.getFolder(ChorevolutionSynthesisProjectStructureFolder.SYNTHESIS_PROCESSOR_FOLDER_NAME)
						.getFolder(super.getPropertyValues()
								.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_BINDINGCOMPONENT)
								.getValue())
						.getFolder(participantTableViewerRecord.getParticipant()));

		ChorevolutionCoreUtils.createFile(bindingComponentName + "." + WSDL_EXTENSION, bcModelFolder, clientCDWSDL);

		BindingComponentGeneratorRequest request = new BindingComponentGeneratorRequest();
		request.setChoreographyName(choreographyname);
		request.setInterfaceDescriptionContent(clientCDWSDL);
		request.setBindingComponentName(bindingComponentName);
		request.setBindingComponentProtocolType(BindingComponentProtocolType.REST);
		request.setAccessToken(
				super.getPropertyValues().get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_TOKEN).getValue());

		BindingComponentGeneratorResponse response = client.generateBindingComponent(request);

		// Save BC
		try {
			ChorevolutionCoreUtils.createFile(bindingComponentName + "." + fileExtension, bcArtifactFolder,
					response.getBindingComponentContent());
		} catch (Exception e) {

		}

		try {
			ChorevolutionCoreUtils.createFile(bindingComponentName + "." + WSDL_EXTENSION, bcModelFolder,
					response.getWsdlContent());
		} catch (Exception e) {
			// may be empty?
		}
		String location = response.getLocation();
		if (bcGenerationType == BindingComponentGeneratorRequest.BC_GENERATION_TYPE_SRC) {
			String projectName = ChorevolutionCoreUtils.unTar(response.getBindingComponentContent(),
					ChorevolutionUIPlugin.getWorkspace().getRoot().getLocation());
			IProject importedProject = ChorevolutionCoreUtils.importMavenProject(projectName);

			location = importedProject.getLocation().makeAbsolute().toOSString();
		}

		ComponentData clientComponentData = generatedArtifactSynthesisProcessor
				.getClientComponent(participantTableViewerRecord.getParticipant());
		generatedArtifactSynthesisProcessor.addBindingComponentToClientComponent(clientComponentData,
				bindingComponentName, location);

		WSDLData wsdlData = new WSDLData();
		wsdlData.setParticipantName(participantTableViewerRecord.getParticipant());
		wsdlData.setWsdl(response.getWsdlContent());

		super.getProject().refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());

	}
}
