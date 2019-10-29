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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.ChoreographyProjection;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.CorrelationChoreographyTaskTableViewerRecord;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.ParticipantTableViewerRecord;
import eu.chorevolution.synthesisprocessor.rest.api.ChoreographyProjectionGeneratorRequest;
import eu.chorevolution.synthesisprocessor.rest.api.ChoreographyProjectionGeneratorResponse;
import eu.chorevolution.synthesisprocessor.rest.api.CoordinationDelegateGeneratorRequest;
import eu.chorevolution.synthesisprocessor.rest.api.CoordinationDelegateGeneratorResponse;
import eu.chorevolution.synthesisprocessor.rest.api.client.ChoreographyProjectionGeneratorClient;
import eu.chorevolution.synthesisprocessor.rest.api.client.CoordinationDelegateGeneratorClient;
import eu.chorevolution.synthesisprocessor.rest.business.CoordinationDelegateGeneratorException;
import eu.chorevolution.studio.eclipse.core.utils.WSDLData;

public class ChoreographyProjectionRest extends ChoreographyProjection{
	private IProgressMonitor monitor;
	
	private Map<String, List<CorrelationChoreographyTaskTableViewerRecord>> correlationChoreographyTasks;
	private List<ParticipantTableViewerRecord> prosumerParticipants;
	private List<ParticipantTableViewerRecord> clientParticipants;
	private IFile bpmnFile;
	private byte[] typesXSDByteArray;
	private ChoreographyProjectionGeneratorClient choreographyProjectionGeneratorClient;
	private CoordinationDelegateGeneratorClient coordinationDelegateGeneratorClient; 

	public ChoreographyProjectionRest(List<ParticipantTableViewerRecord> prosumerParticipants,
			List<ParticipantTableViewerRecord> clientParticipants,
			Map<String, List<CorrelationChoreographyTaskTableViewerRecord>> correlationChoreographyTasks,
			IProject project, IFile bpmnFile, byte[] typesXSDByteArray, IProgressMonitor monitor) {
		
		super(project, new ArrayList<WSDLData>());

		this.monitor = monitor;
		this.prosumerParticipants = prosumerParticipants;
		this.clientParticipants = clientParticipants;
		this.correlationChoreographyTasks = correlationChoreographyTasks;
		this.bpmnFile = bpmnFile;
		this.typesXSDByteArray = typesXSDByteArray;
		this.choreographyProjectionGeneratorClient = new ChoreographyProjectionGeneratorClient(super.getPropertyValues().get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_BPMN2_CHOREOGRAPHY_PROJECTOR_URI).getValue());
		this.coordinationDelegateGeneratorClient = new CoordinationDelegateGeneratorClient(super.getPropertyValues().get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_CD_GENERATOR_URI).getValue());
	}

	
	@Override
	public void storeProjections() throws Exception {
		monitor.beginTask("Choreography Projection", prosumerParticipants.size() + clientParticipants.size());
		for (ParticipantTableViewerRecord participantTableViewerRecord : prosumerParticipants) {
			if (monitor.isCanceled()) {
				return;
			}

			monitor.subTask(
					"Project the Choreography on Prosumer Participant: " + participantTableViewerRecord.getRecordLabel());
			IFolder projectedModelFolder = storeProjection(participantTableViewerRecord);
			generateCDProsumerWSDL(participantTableViewerRecord, getProjectedModelContent(projectedModelFolder,participantTableViewerRecord.getServiceProjectName()), getXSDFile(projectedModelFolder));
			
			monitor.worked(1);
		}

		for (ParticipantTableViewerRecord participantTableViewerRecord : clientParticipants) {
			if (monitor.isCanceled()) {
				return;
			}

			monitor.subTask(
					"Project the Choreography on Client Participant: " + participantTableViewerRecord.getRecordLabel());

			
			IFolder projectedModelFolder = storeProjection(participantTableViewerRecord);
			generateCDClientWSDL(participantTableViewerRecord, getProjectedModelContent(projectedModelFolder,participantTableViewerRecord.getServiceProjectName()), getXSDFile(projectedModelFolder));
			
			monitor.worked(1);
		}
	}

	
	private IFolder storeProjection(ParticipantTableViewerRecord participantTableViewerRecord)
			throws Exception {

		IFolder participantFolder = null;

		participantFolder = ChorevolutionCoreUtils
				.createFolder(participantTableViewerRecord.getParticipant(),
						super.getProject().getFolder(ChorevolutionSynthesisProjectStructureFolder.SYNTHESIS_PROCESSOR_FOLDER_NAME)
								.getFolder(super.getPropertyValues()
										.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_COORD)
										.getValue()));

		// call projector
		
		ChoreographyProjectionGeneratorRequest request = new ChoreographyProjectionGeneratorRequest();
		request.setBpmn2Content(FileUtils.readFileToByteArray(bpmnFile.getRawLocation().makeAbsolute().toFile()));
		request.setParticipant(participantTableViewerRecord.getParticipant());
		request.setAccessToken(super.getPropertyValues()
				.get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_TOKEN)
				.getValue());
		
		ChoreographyProjectionGeneratorResponse bpmn2ChoreographyProjectorResponse = choreographyProjectionGeneratorClient.generateChoreographyProjection(request);
		
		
		// save the projected model and type.xsd
		IFolder modelFolder = ChorevolutionCoreUtils
				.createFolder(ChorevolutionSynthesisProjectStructureFolder.MODELS_FOLDER_NAME, participantFolder);
		IFile projectedFile = ChorevolutionUIPlugin.getWorkspace().getRoot().getFile(modelFolder.getFullPath()
				.append(participantTableViewerRecord.getServiceProjectName()).addFileExtension(BPMN2_FILE_EXTENSION));
		FileUtils.writeByteArrayToFile(projectedFile.getRawLocation().makeAbsolute().toFile(), bpmn2ChoreographyProjectorResponse.getBpmn2Content());

		if (typesXSDByteArray!= null) {
			IFile xsdFile = ChorevolutionUIPlugin.getWorkspace().getRoot()
					.getFile(modelFolder.getFullPath().append(XSD_FILE_NAME).addFileExtension(XSD_FILE_EXTENSION));
			FileUtils.writeByteArrayToFile(xsdFile.getRawLocation().makeAbsolute().toFile(),typesXSDByteArray);
		}

		super.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		return modelFolder;

	}

	private void generateCDClientWSDL(ParticipantTableViewerRecord participantTableViewerRecord, byte[] projectedModelContent, byte[] projectedXSDTypes) throws IOException, CoordinationDelegateGeneratorException {
		Map<String,String> correlationDatas = new HashMap<String,String>();
		for (CorrelationChoreographyTaskTableViewerRecord correlationChoreographyTask : correlationChoreographyTasks
				.get(participantTableViewerRecord.getParticipant())) {
			if (!correlationChoreographyTask.getCorrelatedWith()
					.equals(CorrelationChoreographyTaskTableViewerRecord.NONE_CORRELLATION)) {
				correlationDatas.put(correlationChoreographyTask.getChoreographyTask(),correlationChoreographyTask.getCorrelatedWith());
			}
		}
		
		CoordinationDelegateGeneratorRequest request = new CoordinationDelegateGeneratorRequest(bpmnFile.getName().replace("."+bpmnFile.getFileExtension(), ""),participantTableViewerRecord.getServiceProjectName(),
				participantTableViewerRecord.getParticipant(),
				projectedModelContent,
				projectedXSDTypes,
				correlationDatas, super.getPropertyValues()
				.get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_TOKEN)
				.getValue());
		
		CoordinationDelegateGeneratorResponse response = coordinationDelegateGeneratorClient.generateWSDLCoordinationDelegateClient(request);		

		WSDLData wsdlData = new WSDLData();
		wsdlData.setParticipantName(participantTableViewerRecord.getParticipant());
		wsdlData.setWsdl(response.getWsdlContent());
		super.getWsdlDatas().add(wsdlData);

	}

	private void generateCDProsumerWSDL(ParticipantTableViewerRecord participantTableViewerRecord, byte[] projectedModelContent, byte[] projectedXSDTypes) throws CoordinationDelegateGeneratorException {
		CoordinationDelegateGeneratorRequest request = new CoordinationDelegateGeneratorRequest(bpmnFile.getName().replace("."+bpmnFile.getFileExtension(), ""),participantTableViewerRecord.getServiceProjectName(),
				participantTableViewerRecord.getParticipant(),
				projectedModelContent,
				projectedXSDTypes,
				null, super.getPropertyValues()
				.get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_TOKEN)
				.getValue());
		
		CoordinationDelegateGeneratorResponse response = coordinationDelegateGeneratorClient.generateWSDLCoordinationDelegateProsumer(request);

		WSDLData wsdlData = new WSDLData();
		wsdlData.setParticipantName(participantTableViewerRecord.getParticipant());
		wsdlData.setWsdl(response.getWsdlContent());
		super.getWsdlDatas().add(wsdlData);
	}
	
	private byte[] getProjectedModelContent(IFolder modelFolder, String projectedFileName) throws IOException{
		IFile projectedFile = ChorevolutionUIPlugin.getWorkspace().getRoot().getFile(modelFolder.getFullPath()
				.append(projectedFileName).addFileExtension(BPMN2_FILE_EXTENSION));
		
		if (projectedFile == null || !projectedFile.exists()){
			return null;
		}
		
		return FileUtils.readFileToByteArray(projectedFile.getRawLocation().makeAbsolute().toFile());	
	}
	
	private byte[] getXSDFile(IFolder modelFolder) throws IOException{
		IFile xsdFile = ChorevolutionUIPlugin.getWorkspace().getRoot()
				.getFile(modelFolder.getFullPath().append(XSD_FILE_NAME).addFileExtension(XSD_FILE_EXTENSION));

		if (xsdFile == null || !xsdFile.exists()){
			return null;
		}
		
		return FileUtils.readFileToByteArray(xsdFile.getRawLocation().makeAbsolute().toFile());
	}


}
