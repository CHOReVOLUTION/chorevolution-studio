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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import eu.chorevolution.studio.eclipse.core.ChorevolutionCoreUtils;
import eu.chorevolution.studio.eclipse.core.internal.project.model.impl.ChorevolutionSynthesisProjectStructureFolder;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServicesURIPrefs;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionSynthesisSourceModelPrefs;
import eu.chorevolution.studio.eclipse.core.utils.WSDLData;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.CoordinationDelegateGeneration;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.GeneratedArtifactSynthesisProcessor;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.rest.util.SynthesisProcessorClientUtils;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.CorrelationChoreographyTaskTableViewerRecord;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.ParticipantTableViewerRecord;
import eu.chorevolution.synthesisprocessor.rest.api.CoordinationDelegateGeneratorRequest;
import eu.chorevolution.synthesisprocessor.rest.api.CoordinationDelegateGeneratorResponse;
import eu.chorevolution.synthesisprocessor.rest.api.client.CoordinationDelegateGeneratorClient;
import eu.chorevolution.synthesisprocessor.rest.api.client.SynthesisProcessorClient;
import eu.chorevolution.transformations.generativeapproach.choreographyarchitecturegenerator.model.ComponentData;

public class CoordinationDelegateGenerationRest extends CoordinationDelegateGeneration {
	private GeneratedArtifactSynthesisProcessor generatedArtifactSynthesisProcessor;
	private String choreographyname;
	private IProgressMonitor monitor;
	private List<WSDLData> wsdlDatas;
	private Map<String, List<CorrelationChoreographyTaskTableViewerRecord>> correlationChoreographyTasks;
	private CoordinationDelegateGeneratorClient client;
	private SynthesisProcessorClient synthesisProcessorClient;
	private boolean srcGenerationType;

	public CoordinationDelegateGenerationRest(String choreographyname,
			List<ParticipantTableViewerRecord> prosumerParticipants,
			List<ParticipantTableViewerRecord> clientParticipants, List<WSDLData> wsdlDatas,
			Map<String, List<CorrelationChoreographyTaskTableViewerRecord>> correlationChoreographyTasks,
			GeneratedArtifactSynthesisProcessor generatedArtifactSynthesisProcessor, IProject project,
			IProgressMonitor monitor) {

		super(project, prosumerParticipants, clientParticipants);

		this.choreographyname = choreographyname;
		this.generatedArtifactSynthesisProcessor = generatedArtifactSynthesisProcessor;
		this.monitor = monitor;
		this.wsdlDatas = wsdlDatas;
		this.correlationChoreographyTasks = correlationChoreographyTasks;
		this.client = new CoordinationDelegateGeneratorClient(super.getPropertyValues()
				.get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_CD_GENERATOR_URI).getValue());

		// TODO improve this lines
		String uriCdGenerator = super.getPropertyValues()
				.get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_CD_GENERATOR_URI).getValue();
		this.synthesisProcessorClient = new SynthesisProcessorClient(
				uriCdGenerator.replace("coordinationdelegategenerator/", ""));
		if (ChorevolutionSynthesisSourceModelPrefs.SYNTHESIS_GENERATOR_SOURCE_CODE
				.equalsIgnoreCase(super.getPropertyValues()
						.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESIS_GENERATION).getValue())) {
			srcGenerationType = true;
		} else {
			srcGenerationType = false;
		}
	}

	@Override
	public void storeCoordinatioDelegates() throws Exception {
		monitor.beginTask("Coordination Delegate Generator",
				super.getProsumerParticipants().size() + super.getClientParticipants().size());

		for (ParticipantTableViewerRecord participantTableViewerRecord : super.getProsumerParticipants()) {
			if (monitor.isCanceled()) {
				return;
			}

			monitor.subTask("Generate the Coordination Delegate for Prosumer Participant: "
					+ participantTableViewerRecord.getRecordLabel());
			storeProsumerCoordinationDelegate(participantTableViewerRecord);
			monitor.worked(1);
		}

		for (ParticipantTableViewerRecord participantTableViewerRecord : super.getClientParticipants()) {
			if (monitor.isCanceled()) {
				return;
			}

			monitor.subTask("Generate the Coordination Delegate for Client Participant: "
					+ participantTableViewerRecord.getRecordLabel());

			storeClientCoordinationDelegate(participantTableViewerRecord);
			monitor.worked(1);
		}

	}

	private void storeProsumerCoordinationDelegate(ParticipantTableViewerRecord participantTableViewerRecord)
			throws Exception {
		IFolder participantFolder = super.getProject()
				.getFolder(ChorevolutionSynthesisProjectStructureFolder.SYNTHESIS_PROCESSOR_FOLDER_NAME)
				.getFolder(super.getPropertyValues()
						.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_COORD).getValue())
				.getFolder(participantTableViewerRecord.getParticipant());

		IFolder artifactFolder = ChorevolutionCoreUtils
				.createFolder(ChorevolutionSynthesisProjectStructureFolder.ARTIFACTS_FOLDER_NAME, participantFolder);

		IFolder modelFolder = participantFolder
				.getFolder(ChorevolutionSynthesisProjectStructureFolder.MODELS_FOLDER_NAME);

		IFile projectedFile = ChorevolutionUIPlugin.getWorkspace().getRoot().getFile(modelFolder.getFullPath()
				.append(participantTableViewerRecord.getServiceProjectName()).addFileExtension(BPMN2_FILE_EXTENSION));
		IFile xsdTypesFile = ChorevolutionUIPlugin.getWorkspace().getRoot()
				.getFile(modelFolder.getFullPath().append(XSD_FILE_NAME).addFileExtension(XSD_FILE_EXTENSION));

		Map<String, byte[]> wsdlContents = new HashMap<String, byte[]>();
		wsdlDatas.forEach(item -> {
			wsdlContents.put(item.getParticipantName(), item.getWsdl());
		});

		List<String> clientParticipantsName = new ArrayList<String>();
		super.getClientParticipants().forEach(item -> {
			clientParticipantsName.add(item.getParticipant());
		});

		CoordinationDelegateGeneratorRequest request = new CoordinationDelegateGeneratorRequest(choreographyname,
				participantTableViewerRecord.getServiceProjectName(), super.getAllProsumerAndClientParticipantName(),
				clientParticipantsName, participantTableViewerRecord.getParticipant(),
				FileUtils.readFileToByteArray(projectedFile.getRawLocation().makeAbsolute().toFile()),
				FileUtils.readFileToByteArray(xsdTypesFile.getRawLocation().makeAbsolute().toFile()), null,
				wsdlContents,
				super.getPropertyValues().get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_TOKEN).getValue());
		CoordinationDelegateGeneratorResponse coordinationDelegateProsumerResponse = client
				.generateCoordinationDelegateProsumer(request);

		// Save CD
		ChorevolutionCoreUtils.createFile(participantTableViewerRecord.getServiceProjectName() + "." + TARGZ_EXTENSION,
				artifactFolder, coordinationDelegateProsumerResponse.getCoordinationDelegateContent());
		String location = coordinationDelegateProsumerResponse.getLocation();
		if (srcGenerationType) {
			IPath coordinationDelegateFolder = ChorevolutionCoreUtils.createFolderIntoWorkspace(
					coordinationDelegateProsumerResponse.getName(), ChorevolutionUIPlugin.getWorkspace().getRoot());

			ChorevolutionCoreUtils.unTar(coordinationDelegateProsumerResponse.getCoordinationDelegateContent(),
					coordinationDelegateFolder);
			IProject openProject = ChorevolutionCoreUtils
					.openBPELProject(coordinationDelegateProsumerResponse.getName());
			
			//location = openProject.getLocation().makeAbsolute().toOSString();
		}
		ComponentData prosumerComponentData = generatedArtifactSynthesisProcessor.addProsumerComponent(
				participantTableViewerRecord.getParticipant(), participantTableViewerRecord.getServiceProjectName(),
				location, true);

		// create Coordination Delegate Consumer part
		CoordinationDelegateGeneratorResponse coordinationDelegateConsumerResponse = client
				.generateConsumer(new CoordinationDelegateGeneratorRequest(choreographyname,
						participantTableViewerRecord.getServiceProjectName().replaceFirst("cd", ""),
						coordinationDelegateProsumerResponse.getConsumerWsdlContent(), super.getPropertyValues()
								.get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_TOKEN).getValue()));

		String consumerProjectName = ChorevolutionCoreUtils.unTar(
				coordinationDelegateConsumerResponse.getConsumerContent(),
				ChorevolutionUIPlugin.getWorkspace().getRoot().getLocation());
		IProject importedProject = ChorevolutionCoreUtils.importMavenProject(consumerProjectName);

		// add project location
		generatedArtifactSynthesisProcessor.addConsumerComponentToProsumerComponent(prosumerComponentData,
				participantTableViewerRecord.getServiceProjectName().replaceFirst("cd", ""),
				importedProject.getLocation().makeAbsolute().toOSString());

		super.getProject().refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
	}

	private void storeClientCoordinationDelegate(ParticipantTableViewerRecord participantTableViewerRecord)
			throws Exception {
		IFolder participantFolder = super.getProject()
				.getFolder(ChorevolutionSynthesisProjectStructureFolder.SYNTHESIS_PROCESSOR_FOLDER_NAME)
				.getFolder(super.getPropertyValues()
						.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_COORD).getValue())
				.getFolder(participantTableViewerRecord.getParticipant());

		IFolder artifactFolder = ChorevolutionCoreUtils
				.createFolder(ChorevolutionSynthesisProjectStructureFolder.ARTIFACTS_FOLDER_NAME, participantFolder);

		IFolder modelFolder = participantFolder
				.getFolder(ChorevolutionSynthesisProjectStructureFolder.MODELS_FOLDER_NAME);

		IFile projectedFile = ChorevolutionUIPlugin.getWorkspace().getRoot().getFile(modelFolder.getFullPath()
				.append(participantTableViewerRecord.getServiceProjectName()).addFileExtension(BPMN2_FILE_EXTENSION));
		IFile xsdTypesFile = ChorevolutionUIPlugin.getWorkspace().getRoot()
				.getFile(modelFolder.getFullPath().append(XSD_FILE_NAME).addFileExtension(XSD_FILE_EXTENSION));

		Map<String, String> correlationDatas = new HashMap<String, String>();
		for (CorrelationChoreographyTaskTableViewerRecord correlationChoreographyTask : correlationChoreographyTasks
				.get(participantTableViewerRecord.getParticipant())) {
			if (!correlationChoreographyTask.getCorrelatedWith()
					.equals(CorrelationChoreographyTaskTableViewerRecord.NONE_CORRELLATION)) {
				correlationDatas.put(correlationChoreographyTask.getChoreographyTask(),
						correlationChoreographyTask.getCorrelatedWith());
			}
		}

		Map<String, byte[]> wsdlContents = new HashMap<String, byte[]>();
		wsdlDatas.forEach(item -> {
			wsdlContents.put(item.getParticipantName(), item.getWsdl());
		});

		CoordinationDelegateGeneratorRequest request = new CoordinationDelegateGeneratorRequest(choreographyname,
				participantTableViewerRecord.getServiceProjectName(), participantTableViewerRecord.getParticipant(),
				FileUtils.readFileToByteArray(projectedFile.getRawLocation().makeAbsolute().toFile()),
				FileUtils.readFileToByteArray(xsdTypesFile.getRawLocation().makeAbsolute().toFile()), correlationDatas,
				wsdlContents,
				super.getPropertyValues().get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_TOKEN).getValue());

		CoordinationDelegateGeneratorResponse response = client.generateCoordinationDelegateClient(request);

		ChorevolutionCoreUtils.createFile(participantTableViewerRecord.getServiceProjectName() + "." + TARGZ_EXTENSION,
				artifactFolder,
				SynthesisProcessorClientUtils.getArtifactContent(synthesisProcessorClient, response.getLocation()));

		IPath coordinationDelegateFolder = ChorevolutionCoreUtils.createFolderIntoWorkspace(response.getName(),
				ChorevolutionUIPlugin.getWorkspace().getRoot());

		String location = response.getLocation();
		if (srcGenerationType) {
			ChorevolutionCoreUtils.unTar(response.getCoordinationDelegateContent(), coordinationDelegateFolder);
			IProject openProject = ChorevolutionCoreUtils.openBPELProject(response.getName());
			// location = openProject.getLocation().makeAbsolute().toOSString();
		}

		generatedArtifactSynthesisProcessor.addClientComponent(participantTableViewerRecord.getParticipant(),
				participantTableViewerRecord.getServiceProjectName(), location, true);

		super.getProject().refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
	}

}
