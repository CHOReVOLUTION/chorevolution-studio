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
package eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import eu.chorevolution.studio.eclipse.core.ChorevolutionCoreUtils;
import eu.chorevolution.studio.eclipse.core.internal.project.model.impl.ChorevolutionServiceThingProjectStructureFolder;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPreferenceData;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServiceThingSourceModelPrefs;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionSynthesisSourceModelPrefs;
import eu.chorevolution.studio.eclipse.core.utils.syncope.InterfaceDescriptionType;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.ParticipantTableViewerRecord;
import eu.chorevolution.studio.eclipse.core.utils.WSDLData;

public class ServiceSelection {
	private GeneratedArtifactSynthesisProcessor generatedArtifactSynthesisProcessor;
	private IProject project;
	private IProgressMonitor monitor;
	private Map<String, ChorevolutionPreferenceData> serviceThingPreferenceDatas;
	private Map<String, ChorevolutionPreferenceData> synthesisProjectChorevolutionPreferenceDatas;
	private Map<String, String> serviceThingDefaultWorkspaceProperties;

	private List<ParticipantTableViewerRecord> servicesFromInventory;
	private List<WSDLData> wsdlDatas;

	public ServiceSelection(List<ParticipantTableViewerRecord> servicesFromInventory,
			GeneratedArtifactSynthesisProcessor generatedArtifactSynthesisProcessor, IProject project,
			IProgressMonitor monitor) {
		this.generatedArtifactSynthesisProcessor = generatedArtifactSynthesisProcessor;
		this.project = project;
		this.monitor = monitor;
		this.servicesFromInventory = servicesFromInventory;
		this.wsdlDatas = new ArrayList<WSDLData>();

		initProjectPreferences();
	}

	public void storeServiceProviders() throws CoreException {
		monitor.beginTask("Service Selection", servicesFromInventory.size());
		for (ParticipantTableViewerRecord participantTableViewerRecord : servicesFromInventory) {
			if (monitor.isCanceled()) {
				return;
			}

			monitor.subTask("Store Provider Service: " + participantTableViewerRecord.getRecordLabel());
			
			WSDLData wsdlData = storeServiceProvider(participantTableViewerRecord); 
			if(wsdlData != null && wsdlData.getParticipantName() != null){
				wsdlDatas.add(wsdlData);
			}
			
			monitor.worked(1);
		}
	}

	private void initProjectPreferences() {
		ChorevolutionServiceThingSourceModelPrefs chorevolutionServiceThingSourceModelPrefs = new ChorevolutionServiceThingSourceModelPrefs();
		serviceThingPreferenceDatas = chorevolutionServiceThingSourceModelPrefs.readProjectOrWorkspacePreferences(null);

		ChorevolutionSynthesisSourceModelPrefs chorevolutionSynthesisModelPrefs = new ChorevolutionSynthesisSourceModelPrefs();
		synthesisProjectChorevolutionPreferenceDatas = chorevolutionSynthesisModelPrefs
				.readProjectOrWorkspacePreferences(project);

		serviceThingDefaultWorkspaceProperties = new HashMap<String, String>();

		for (ChorevolutionPreferenceData chorevolutionPreferenceData : serviceThingPreferenceDatas.values()) {
			serviceThingDefaultWorkspaceProperties.put(chorevolutionPreferenceData.getID(),
					chorevolutionPreferenceData.getValue());
		}
	}

	private WSDLData storeServiceProvider(ParticipantTableViewerRecord participantTableViewerRecord)
			throws CoreException {

		WSDLData wsdlData = new WSDLData();

		ChorevolutionServiceThingProjectStructureFolder structureFolder = new ChorevolutionServiceThingProjectStructureFolder(
				serviceThingDefaultWorkspaceProperties);
		IFolder serviceFolder = ChorevolutionCoreUtils
				.createFolder(participantTableViewerRecord.getService().getName(),
						project.getFolder(synthesisProjectChorevolutionPreferenceDatas
								.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SERVICEINVENTORY_SERVICES)
								.getValue()));
		structureFolder.createChorevolutionProjectStructure(serviceFolder);

		// create Interface Description model
		if (participantTableViewerRecord.getService().getInterfaceDescriptionContent() != null) {
			ChorevolutionCoreUtils.createFile(
					participantTableViewerRecord.getService().getName() + "."
							+ participantTableViewerRecord.getService().getInterfaceDescriptionType().name()
									.toLowerCase(),
					serviceFolder.getFolder(serviceThingPreferenceDatas
							.get(ChorevolutionServiceThingSourceModelPrefs.PREF_SERVICE_THING_INTERFACE_DESCRIPTION)
							.getValue()),
					participantTableViewerRecord.getService().getInterfaceDescriptionContent());

			if (participantTableViewerRecord.getService().getInterfaceDescriptionType() == InterfaceDescriptionType.WSDL) {
				wsdlData.setParticipantName(participantTableViewerRecord.getParticipant());
				//TODO set only WSDL, call the BC generator in order to convert GIDL WADL to WSDL
				wsdlData.setWsdl(participantTableViewerRecord.getService().getInterfaceDescriptionContent());
			}else{
				//TODO call georgeos or frederic
			}
		}

		// create Interaction protocol Model
		if (participantTableViewerRecord.getService().getInteractionProtocolDescriptionContent() != null) {
			ChorevolutionCoreUtils.createFile(
					participantTableViewerRecord.getService().getName() + "."
							+ participantTableViewerRecord.getService().getInteractionProtocolDescriptionType().name()
									.toLowerCase(),
					serviceFolder.getFolder(serviceThingPreferenceDatas
							.get(ChorevolutionServiceThingSourceModelPrefs.PREF_SERVICE_THING_INTERACTIONPROTOCOL_DESCRIPTION)
							.getValue()),
					participantTableViewerRecord.getService().getInteractionProtocolDescriptionContent());
		}
		// create QoS Description Model
		/*
		 * if
		 * (participantTableViewerRecord.getService().getQosDescriptionContent()
		 * != null) { ChorevolutionCoreUtils .createFile(
		 * participantTableViewerRecord.getService().getServiceName() + "." +
		 * participantTableViewerRecord.getService().getQosDescriptionType().
		 * name() .toLowerCase(),
		 * serviceFolder.getFolder(serviceThingPreferenceDatas
		 * .get(ChorevolutionServiceThingSourceModelPrefs.
		 * PREF_SERVICE_THING_QOS_DESCRIPTION) .getValue()),
		 * participantTableViewerRecord.getService().getQosDescriptionContent())
		 * ; }
		 */
		// create Security Description Model
		if (participantTableViewerRecord.getService().getSecurityDescriptionContent() != null) {
			ChorevolutionCoreUtils.createFile(
					participantTableViewerRecord.getService().getName() + "."
							+ participantTableViewerRecord.getService().getSecurityDescriptionType().name()
									.toLowerCase(),
					serviceFolder.getFolder(serviceThingPreferenceDatas
							.get(ChorevolutionServiceThingSourceModelPrefs.PREF_SERVICE_THING_SECURITY_DESCRIPTION)
							.getValue()),
					participantTableViewerRecord.getService().getSecurityDescriptionContent());
			
			//save the custom auth file
			if(participantTableViewerRecord.getService().getCustomAuthFileJAR() != null) {
				ChorevolutionCoreUtils.createFile("customAuth.zip",
						serviceFolder.getFolder(serviceThingPreferenceDatas
								.get(ChorevolutionServiceThingSourceModelPrefs.PREF_SERVICE_THING_SECURITY_DESCRIPTION)
								.getValue()),
						participantTableViewerRecord.getService().getCustomAuthFileJAR());
				
			}
		}

		generatedArtifactSynthesisProcessor.addProviderComponent(participantTableViewerRecord.getParticipant(),
				participantTableViewerRecord.getService().getName(),
				participantTableViewerRecord.getService().getLocation(),true);
		project.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
		return wsdlData;
	}

	public List<WSDLData> getWsdlDatas() {
		return wsdlDatas;
	}

}
