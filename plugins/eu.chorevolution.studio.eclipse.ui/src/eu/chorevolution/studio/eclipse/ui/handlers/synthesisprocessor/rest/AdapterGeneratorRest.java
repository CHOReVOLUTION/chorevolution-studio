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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;

import eu.chorevolution.modelingnotations.adapter.AdapterModel;
import eu.chorevolution.studio.eclipse.core.ChorevolutionCoreUtils;
import eu.chorevolution.studio.eclipse.core.internal.project.model.impl.ChorevolutionSynthesisProjectStructureFolder;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServicesURIPrefs;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionSynthesisSourceModelPrefs;
import eu.chorevolution.studio.eclipse.core.utils.WSDLData;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;
import eu.chorevolution.studio.eclipse.ui.Utilities;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.AdapterGenerator;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.GeneratedArtifactSynthesisProcessor;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.AdapterModelDataType;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.ParticipantTableViewerRecord;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.RolesViewerRecord;
import eu.chorevolution.synthesisprocessor.rest.api.AdapterGeneratorRequest;
import eu.chorevolution.synthesisprocessor.rest.api.AdapterGeneratorResponse;
import eu.chorevolution.synthesisprocessor.rest.api.client.AdapterGeneratorClient;
import eu.chorevolution.synthesisprocessor.rest.api.client.SynthesisProcessorClient;
import eu.chorevolution.transformations.generativeapproach.choreographyarchitecturegenerator.model.ComponentData;

public class AdapterGeneratorRest extends AdapterGenerator {
	private GeneratedArtifactSynthesisProcessor generatedArtifactSynthesisProcessor;
	private String choreographyName;
	private byte[] bpmn2Content; // choreography file
	private byte[] typesContent; // types file
	private List<WSDLData> addedWSDL;

	private List<WSDLData> bindingComponentWSDL;
	private List<WSDLData> otherWSDL;

	private List<WSDLData> adapterGenerated;
	private List<String> warGenerated;

	private IProgressMonitor monitor;

	private AdapterGeneratorClient client;

	private SynthesisProcessorClient synthesisProcessorClient;
	private List<ParticipantTableViewerRecord> clientParticipantsList;

	private List<RolesViewerRecord> servicesToBeMapped;
	private List<RolesViewerRecord> servicesMapped;
	private List<AdapterModelDataType> adapterModelsToBeCreated;

	private int adGenerationType;
	private String fileExtension;

	public AdapterGeneratorRest(IProject project, String choreographyName, byte[] bpmn2Content, byte[] typesContent,
			List<ParticipantTableViewerRecord> providerParticipantsList,
			List<ParticipantTableViewerRecord> clientParticipantsList, boolean generateClientBC,
			List<WSDLData> bindingComponentWsdl, List<WSDLData> otherWsdl, List<RolesViewerRecord> servicesToBeMapped,
			GeneratedArtifactSynthesisProcessor generatedArtifactSynthesisProcessor,
			List<AdapterModelDataType> adapterModelsToBeCreated, IProgressMonitor monitor) {

		super(project);
		this.choreographyName = choreographyName;
		this.bpmn2Content = bpmn2Content;
		this.typesContent = typesContent;
		this.bindingComponentWSDL = bindingComponentWsdl;
		this.otherWSDL = otherWsdl;
		this.monitor = monitor;
		this.clientParticipantsList = clientParticipantsList;
		this.generatedArtifactSynthesisProcessor = generatedArtifactSynthesisProcessor;
		this.addedWSDL = new ArrayList<WSDLData>();
		this.adapterModelsToBeCreated = adapterModelsToBeCreated;
		this.servicesToBeMapped = servicesToBeMapped;
		if (ChorevolutionSynthesisSourceModelPrefs.SYNTHESIS_GENERATOR_SOURCE_CODE
				.equalsIgnoreCase(super.getPropertyValues()
						.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESIS_GENERATION).getValue())) {
			adGenerationType = AdapterGeneratorRequest.AD_GENERATION_TYPE_SRC;
			fileExtension = TAR_GZ_EXTENSION;
		} else {
			adGenerationType = AdapterGeneratorRequest.AD_GENERATION_TYPE_WAR;
			fileExtension = WAR_EXTENSION;
		}
		client = new AdapterGeneratorClient(super.getPropertyValues()
				.get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_ADAPTER_GENERATOR_URI).getValue());

		// TODO improve this lines
		String uriCdGenerator = super.getPropertyValues()
				.get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_CD_GENERATOR_URI).getValue();
		this.synthesisProcessorClient = new SynthesisProcessorClient(
				uriCdGenerator.replace("coordinationdelegategenerator/", ""));

		// executes login
		SynthesisProcessorAccessControlRest spacr = new SynthesisProcessorAccessControlRest(project);

		try {
			if (!spacr.executeLogin()) {
				MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(), "Login incorrect",
						"The username or the password of the Synthesis Processor are incorrect");
				return;
			}
		} catch (Exception e) {
			MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(), "Unable to do Login",
					e.toString());
		}

		// i have to generate n adapters, one for each participant (for now)
		// one for each TASK-OPERATION (PARTICIPANT-ROLE)
		// so, i have to make UNIQUE the roleToMap field, summing up all the task with
		// the same roletomap in a single list
		this.servicesMapped = new ArrayList<RolesViewerRecord>();
		for (RolesViewerRecord serviceToBeMapped : servicesToBeMapped) {
			boolean found = false;

			for (RolesViewerRecord serviceMapped : servicesMapped) {
				if (serviceMapped.getRoleToMap().equals(serviceToBeMapped.getRoleToMap()))
					found = true;
			}

			if (!found)
				servicesMapped.add(serviceToBeMapped);

		}

		for (RolesViewerRecord serviceToBeMapped : servicesMapped) {

			for (ParticipantTableViewerRecord participant : providerParticipantsList) {
				// if is in binding component list, the wsdl provided by the BC should be mapped
				if (serviceToBeMapped.getRoleToMap().equals(participant.getParticipant())) {// this participant needs
																							// adapter
					for (WSDLData wsdlData : bindingComponentWsdl) {
						if (wsdlData.getParticipantName().equals(participant.getParticipant())) {
							addedWSDL.add(wsdlData);
							break;// i go to the other participant
						}
					}
				}
			}

			// if bc maps also the client participants, the wsdl provided by BC should be
			// mapped
			if (generateClientBC) {
				for (ParticipantTableViewerRecord participant : clientParticipantsList) {
					// if is in binding component list, the wsdl provided by the BC should be mapped
					if (serviceToBeMapped.getRoleToMap().equals(participant.getParticipant())) {// this participant
																								// needs adapter
						for (WSDLData wsdlData : bindingComponentWsdl) {
							if (wsdlData.getParticipantName().equals(participant.getParticipant())) {
								addedWSDL.add(wsdlData);
								break;// i go to the other participant
							}
						}
					}
				}
			}

			// if the service needs to be mapped but is not already mapped, map with the
			// original wsdl (if it were gidl, it would be mapped)
			for (ParticipantTableViewerRecord participant : providerParticipantsList) {
				boolean found = false;
				for (WSDLData wsdlData : addedWSDL) {
					if (wsdlData.getParticipantName().equals(participant.getParticipant())) {
						found = true;
						break;
					}
				}

				if (!found) {
					if (serviceToBeMapped.getRoleToMap().equals(participant.getParticipant())) {// should be mapped if
																								// present
						addedWSDL.add(new WSDLData(participant.getParticipant(),
								participant.getService().getInterfaceDescriptionContent()));
					}
				}
			}
		}
	}

	// when implemented, it returns the adapter model
	@Override
	public List<WSDLData> generateAndStoreAdapters() throws Exception {
		List<WSDLData> wsdlToReturn = new ArrayList<WSDLData>();

		this.warGenerated = new ArrayList<String>();
		this.adapterGenerated = new ArrayList<WSDLData>();

		monitor.beginTask("Adapter Generator", this.addedWSDL.size());

		// in toGenerate there are the UNIQUE participant list taken from the wizard
		// page
		for (WSDLData toGenerate : this.addedWSDL) {
			if (monitor.isCanceled()) {
				return null;
			}
			monitor.subTask("Generate Adapter for Service: " + toGenerate.getParticipantName());

			List<AdapterModelDataType> adapterModelsPassed = new ArrayList<AdapterModelDataType>();

			// these are all the models generated by the user, so i can take all the task
			// names and models
			for (AdapterModelDataType adapterModel : adapterModelsToBeCreated) {
				if (adapterModel.getRoleToMap().equals(toGenerate.getParticipantName()))
					adapterModelsPassed.add(adapterModel);
			}

			AdapterModel generatedAdapter = Utilities.generateAdapterModels(adapterModelsPassed);
			String adapterName = ADAPTER_GENERATOR_PREFIX_NAME
					+ ChorevolutionCoreUtils.removeBlankSpaces(toGenerate.getParticipantName());

			// now create the file in folders
			IFolder adapterFolder = ChorevolutionCoreUtils.createFolder(toGenerate.getParticipantName(),
					super.getProject()
							.getFolder(ChorevolutionSynthesisProjectStructureFolder.SYNTHESIS_PROCESSOR_FOLDER_NAME)
							.getFolder(super.getPropertyValues()
									.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_ADAPTER)
									.getValue()));

			// now create the file in folders
			IFolder adapterModelFolder = ChorevolutionCoreUtils.createFolder((super.getPropertyValues()
					.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_ADAPTER_MODEL).getValue()),
					super.getProject()
							.getFolder(ChorevolutionSynthesisProjectStructureFolder.SYNTHESIS_PROCESSOR_FOLDER_NAME)
							.getFolder(super.getPropertyValues()
									.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_ADAPTER)
									.getValue())
							.getFolder(toGenerate.getParticipantName()));

			// create adapter file
			ChorevolutionCoreUtils.createFile(adapterName + "." + ADAPTER_EXTENSION, adapterModelFolder,
					Utilities.getAdapterContent(generatedAdapter));

			try {

				AdapterGeneratorRequest request = new AdapterGeneratorRequest(
						choreographyName, bpmn2Content, typesContent, adapterName,
						Utilities.getAdapterContent(generatedAdapter), toGenerate.getWsdl(), super.getPropertyValues()
								.get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_TOKEN).getValue(),
						adGenerationType);
				AdapterGeneratorResponse response = client.generateAdapter(request);

				this.adapterGenerated.add(new WSDLData(toGenerate.getParticipantName(), response.getWsdlFile()));
				this.warGenerated.add(response.getLocation());

				wsdlToReturn.add(new WSDLData(toGenerate.getParticipantName(), response.getWsdlFile()));

				// now create the file in folders
				IFolder adapterArtifactFolder = ChorevolutionCoreUtils.createFolder(
						(super.getPropertyValues()
								.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_ADAPTER_ARTIFACT)
								.getValue()),
						super.getProject()
								.getFolder(ChorevolutionSynthesisProjectStructureFolder.SYNTHESIS_PROCESSOR_FOLDER_NAME)
								.getFolder(super.getPropertyValues()
										.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_ADAPTER)
										.getValue())
								.getFolder(toGenerate.getParticipantName()));

				// Save AD in the artifact folder
				try {
					ChorevolutionCoreUtils.createFile(adapterName + "." + fileExtension, adapterArtifactFolder,
							response.getAdapterContent());
				} catch (Exception e) {
				}

				// Save wsdl file
				ChorevolutionCoreUtils.createFile(adapterName + "." + WSDL_EXTENSION, adapterModelFolder,
						response.getWsdlFile());
				String location = response.getLocation();
				if (adGenerationType == AdapterGeneratorRequest.AD_GENERATION_TYPE_SRC) {
					String projectName = ChorevolutionCoreUtils.unTar(response.getAdapterContent(),
							ChorevolutionUIPlugin.getWorkspace().getRoot().getLocation());
					IProject importedProject = ChorevolutionCoreUtils.importMavenProject(projectName);
					location = importedProject.getLocation().makeAbsolute().toOSString();
				}

				if (clientParticipantsList.contains(toGenerate.getParticipantName())) {
					ComponentData providerComponentData = generatedArtifactSynthesisProcessor
							.getClientComponent(toGenerate.getParticipantName());
					generatedArtifactSynthesisProcessor.addAdapterToClientComponent(providerComponentData, adapterName,
							location);
				} else {
					ComponentData providerComponentData = generatedArtifactSynthesisProcessor
							.getProviderComponent(toGenerate.getParticipantName());
					generatedArtifactSynthesisProcessor.addAdapterToProviderComponent(providerComponentData,
							adapterName, location);
				}
			} catch (Exception e) {
				// non stop the process
			}

			monitor.worked(1);
		}

		// i add before the not adapted bc wsdl, later the not adapted normal wsdl
		for (WSDLData bcWSDL : bindingComponentWSDL) {
			boolean present = false;
			for (WSDLData toReturn : wsdlToReturn) {
				if (toReturn.getParticipantName().equals(bcWSDL.getParticipantName())) {
					present = true;
					break;
				}
			}
			if (!present) {
				wsdlToReturn.add(bcWSDL);
			}
		}

		for (WSDLData oWSDL : otherWSDL) {
			boolean present = false;
			for (WSDLData toReturn : wsdlToReturn) {
				if (toReturn.getParticipantName().equals(oWSDL.getParticipantName())) {
					present = true;
					break;
				}
			}
			if (!present) {
				wsdlToReturn.add(oWSDL);
			}
		}

		// i return all the wsdl needed to make cd, adapted and not, in wsdlToReturn
		return wsdlToReturn;
	}

}
