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
package eu.chorevolution.studio.eclipse.core.utils.bpmn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.bpmn2.Choreography;
import org.eclipse.bpmn2.ChoreographyActivity;
import org.eclipse.bpmn2.ChoreographyTask;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.MessageFlow;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.util.Bpmn2ResourceFactoryImpl;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;

import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePlugin;
import eu.chorevolution.studio.eclipse.core.StatusHandler;

/**
 * This class provides static utility methods for manipulate a BPMN model
 * 
 */
public class BPMNUtil {

	/**
	 * Loads a BPMN2 model from the specified {@link URI}, checking if it can be
	 * loaded and it contains {@link Choreography}.
	 * <p>
	 * Returns a list of {@link Choreography} founded in the BPMN2 model.
	 * <p>
	 * A {@link TransformatorException} is thrown if the BPMN2 model can not be
	 * loaded. A {@link TransformatorException} is thrown if the BPMN2 model is
	 * loaded but not contains any {@link Choreography}.
	 * 
	 * @param bpmnURI
	 *            the {@link IFile} that represents the BPMN2 Choreography
	 * @return the list of {@link Choreography} founded in BPMN2 the model.
	 * @throws TransformatorException
	 *             if the BPMN2 file can not be loaded or the BPMN2 model not
	 *             contains any Choreography.
	 */
	public static List<Choreography> loadBPMNModel(IFile bpmnFile) {
		URI bpmnURI = URI.createPlatformResourceURI(bpmnFile.getFullPath().toString(), true);
		List<Choreography> choreographies = new ArrayList<Choreography>();

		// register the BPMN2ResourceFactory in Factory registry
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		reg.getExtensionToFactoryMap().put("bpmn", new Bpmn2ResourceFactoryImpl());
		reg.getExtensionToFactoryMap().put("bpmn2", new Bpmn2ResourceFactoryImpl());

		// load the resource and resolve
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.createResource(bpmnURI);

		try {
			// load the resource
			resource.load(null);
			// avax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI
			// get all Choreography
			EObject root = resource.getContents().get(0);
			Definitions definitions;

			if (root instanceof DocumentRoot) {
				definitions = ((DocumentRoot) root).getDefinitions();
			} else {
				definitions = (Definitions) root;
			}

			for (EObject definition : definitions.eContents()) {
				if (definition instanceof Choreography) {
					choreographies.add((Choreography) definition);
				}
			}

		} catch (IOException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ChorevolutionCorePlugin.PLUGIN_ID,
					"Error to load the resource " + resource.getURI().toFileString(), e));
			return null;
		}

		if (choreographies.isEmpty()) {
			StatusHandler.log(new Status(IStatus.ERROR, ChorevolutionCorePlugin.PLUGIN_ID,
					"None choreography founded in the model " + resource.getURI().toFileString()));
			return null;

		}

		return choreographies;
	}

	public static List<Choreography> documentRootToChoreographyList(DocumentRoot documentRoot) {
		List<Choreography> choreographies = new ArrayList<Choreography>();

		Definitions definitions;

		if (documentRoot instanceof DocumentRoot) {
			definitions = ((DocumentRoot) documentRoot).getDefinitions();
		} else {
			definitions = (Definitions) documentRoot;
		}

		for (EObject definition : definitions.eContents()) {
			if (definition instanceof Choreography) {
				choreographies.add((Choreography) definition);
			}
		}

		if (choreographies.isEmpty()) {
			StatusHandler.log(new Status(IStatus.ERROR, ChorevolutionCorePlugin.PLUGIN_ID,
					"None choreography founded in the model "));
			return null;

		}

		return choreographies;
	}

	public static Choreography getDefaultChoreography(List<Choreography> choreographies) {
		String DEFAULT_CHOREOGRAPHY_ID = "Choreography_1";
		String DEFAULT_CHOREOGRAPHY_NAME = "Default Choreography";
		Choreography defaultChoreography = null;

		for (Choreography choreography : choreographies) {
			// set default choreography
			if (choreography.getId().equalsIgnoreCase(DEFAULT_CHOREOGRAPHY_ID)
					|| choreography.getName().equalsIgnoreCase(DEFAULT_CHOREOGRAPHY_NAME)) {
				defaultChoreography = choreography;
			}
		}

		// throw Bpmn2ChoreographyProjectorException
		if (defaultChoreography == null) {
			StatusHandler.log(new Status(IStatus.ERROR, ChorevolutionCorePlugin.PLUGIN_ID,
					"None default choreography founded in the BPMN Model"));
		}

		return defaultChoreography;

	}

	public static void saveBpmn2Model(DocumentRoot object, IFile file) {
		// create resource from the Model
		// URI fileUriTempModelNormalized =
		// URI.createFileURI(file.getRawLocation().makeAbsolute().toFile().getAbsolutePath());
		// Resource resourceModelNormalized = new
		// Bpmn2ResourceFactoryImpl().createResource(fileUriTempModelNormalized);
		// URI fileUriTempModelNormalized =
		// URI.createPlatformResourceURI(file.getFullPath().toString(), true);
		// load the resource and resolve
		URI fileUriTempModelNormalized = URI
				.createFileURI(file.getRawLocation().makeAbsolute().toFile().getAbsolutePath());
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.createResource(fileUriTempModelNormalized);
		/*
		 * Map<URI, URI> uriMap = resourceSet.getURIConverter().getURIMap();
		 * uriMap.put(xsdURI, fileUriTempModelNormalized);
		 */
		// add model in model resourceModel
		resource.getContents().add(object);
		
		Map<Object, Object> saveOptions =  new HashMap<Object, Object>();
		saveOptions.put(XMLResource.OPTION_SAVE_TYPE_INFORMATION, Boolean.FALSE);
		
		try {
			// resource.save(Collections.EMPTY_MAP);
			resource.save(saveOptions);

		} catch (IOException e) {
		}

	}

	public static DocumentRoot getBpmn2DocumentRoot(IFile bpmnFile) {

		URI bpmnURI = URI.createPlatformResourceURI(bpmnFile.getFullPath().toString(), true);

		// register the BPMN2ResourceFactory in Factory registry
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		reg.getExtensionToFactoryMap().put("bpmn", new Bpmn2ResourceFactoryImpl());
		reg.getExtensionToFactoryMap().put("bpmn2", new Bpmn2ResourceFactoryImpl());

		// load the resource and resolve
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.createResource(bpmnURI);

		try {
			// load the resource
			resource.load(null);
		} catch (IOException e) {
			e.printStackTrace();
			StatusHandler.log(new Status(IStatus.ERROR, ChorevolutionCorePlugin.PLUGIN_ID,
					"An error occurred loading resource " + bpmnFile.getFullPath(), e));
		}

		if (!resource.getContents().isEmpty() && resource.getContents().get(0) instanceof DocumentRoot) {
			return (DocumentRoot) resource.getContents().get(0);
		}

		StatusHandler.log(new Status(IStatus.ERROR, ChorevolutionCorePlugin.PLUGIN_ID,
				"BPMN2 model is loaded but not contain a BPMN2 DocumentRoot " + bpmnFile.getFullPath()));
		return null;
	}

	public static Participant getInitiatingParticipant(ChoreographyActivity choreographyActivity) {
		if (choreographyActivity.getInitiatingParticipantRef() != null)
			return choreographyActivity.getInitiatingParticipantRef();

		StatusHandler.log(new Status(IStatus.ERROR, ChorevolutionCorePlugin.PLUGIN_ID,
				"None Initiating participant founded in the choreography activity " + choreographyActivity.getName()));
		return null;

	}

	public static Participant getReceivingParticipant(ChoreographyActivity choreographyActivity) {
		for (Participant participant : choreographyActivity.getParticipantRefs()) {
			if (!participant.equals(choreographyActivity.getInitiatingParticipantRef())) {
				return participant;
			}
		}

		StatusHandler.log(new Status(IStatus.ERROR, ChorevolutionCorePlugin.PLUGIN_ID,
				"None target participant founded in the choreography activity " + choreographyActivity.getName()));
		return null;
	}

	public static List<Participant> getAllInitiatingParticipant(Choreography choreography) {
		List<Participant> initiatingParticipants = new ArrayList<Participant>();
		for (FlowElement flowElement : choreography.getFlowElements()) {
			if (flowElement instanceof ChoreographyTask) {
				Participant initiatingParticipant = getInitiatingParticipant((ChoreographyTask) flowElement);
				if (initiatingParticipant != null && !initiatingParticipants.contains(initiatingParticipant)) {
					initiatingParticipants.add(initiatingParticipant);
				}
			}
		}
		return initiatingParticipants;
	}

	public static List<Participant> getAllReceivingParticipant(Choreography choreography) {
		List<Participant> receivingParticipants = new ArrayList<Participant>();
		for (FlowElement flowElement : choreography.getFlowElements()) {
			if (flowElement instanceof ChoreographyTask) {
				Participant receivingParticipant = getReceivingParticipant((ChoreographyTask) flowElement);
				if (receivingParticipant != null && !receivingParticipants.contains(receivingParticipant)) {
					receivingParticipants.add(receivingParticipant);
				}
			}
		}
		return receivingParticipants;
	}

	public static List<String> getProsumerRoles(List<Choreography> choreographies) {
		List<String> prosumerServices = new ArrayList<String>();

		for (Choreography choreography : choreographies){
			List<Participant> initiatingParticipants = getAllInitiatingParticipant(choreography);
			List<Participant> receivingParticipants = getAllReceivingParticipant(choreography);
	
			for (Participant participant : initiatingParticipants) {
				if (receivingParticipants.contains(participant) && !prosumerServices.contains(participant.getName())) {
					prosumerServices.add(participant.getName());
				}
			}
		}
		

		Participant probablyClientRole = fixSearchClientRole(getDefaultChoreography(choreographies));
		if (prosumerServices.contains(probablyClientRole.getName())) {
			prosumerServices.remove(probablyClientRole.getName());
		}

		return prosumerServices;
	}

	public static List<String> getProviderRoles(List<Choreography> choreographies) {
		List<String> providerServices = new ArrayList<String>();
		for (Choreography choreography : choreographies){
			List<Participant> initiatingParticipants = getAllInitiatingParticipant(choreography);
			List<Participant> receivingParticipants = getAllReceivingParticipant(choreography);
			
			for (Participant participant : receivingParticipants) {
				if (!initiatingParticipants.contains(participant) && !providerServices.contains(participant.getName())) {
					providerServices.add(participant.getName());
				}
			}
		}
		return providerServices;
	}

	public static List<String> getClientRoles(List<Choreography> choreographies) {
		List<String> clientServices = new ArrayList<String>();
		
//		for (Choreography choreography : choreographies){	
//			List<Participant> initiatingParticipants = getAllInitiatingParticipant(choreography);
//			List<Participant> receivingParticipants = getAllReceivingParticipant(choreography);
//	
//			for (Participant participant : initiatingParticipants) {
//				if (!receivingParticipants.contains(participant) && !clientServices.contains(participant.getName())) {
//					clientServices.add(participant.getName());
//				}
//			}
//		}
		
		Participant probablyClientRole = fixSearchClientRole(getDefaultChoreography(choreographies));
		if (!clientServices.contains(probablyClientRole.getName())) {
			clientServices.add(probablyClientRole.getName());
		}
		
		return clientServices;
	}

	private static Participant fixSearchClientRole(Choreography choreography) {
		// this method is use to determinate the client participant. because in
		// some cases the client is fetch as prosumer
		StartEvent startEvent = getStartEvent(choreography);
		// assume there is one flow otugoing from StartEvent
		if (!startEvent.getOutgoing().isEmpty()) {
			if (startEvent.getOutgoing().get(0).getTargetRef() instanceof ChoreographyTask) {
				return getInitiatingParticipant((ChoreographyTask) startEvent.getOutgoing().get(0).getTargetRef());
			} else {
				return null;
			}
		}
		return null;

	}

	public static StartEvent getStartEvent(Choreography choreography) {
		// we assume that the sub-choreography has only one start event
		for (FlowElement flowElement : choreography.getFlowElements()) {
			if (flowElement instanceof StartEvent) {
				return (StartEvent) flowElement;
			}
		}
		return null;

	}

	public static EndEvent getEndEvent(final Choreography choreography) {
		// we assume that the choreography has only one end event
		for (FlowElement flowElement : choreography.getFlowElements()) {
			if (flowElement instanceof EndEvent) {
				return (EndEvent) flowElement;
			}
		}
		return null;
	}

	public static FlowElement getFirstAcceptableFlowElement(final Choreography choreography) {
		// we assume that the start event of the choreography has only one
		// outgoing transition
		return getStartEvent(choreography).getOutgoing().get(0).getTargetRef();
	}

	public static FlowElement getLastAcceptableFlowElement(final Choreography choreography) {
		// we assume that the end event of the choreography has only one
		// incoming transition
		return getEndEvent(choreography).getIncoming().get(0).getSourceRef();
	}
	
	public static boolean existMessageSentFromParticipant(ChoreographyTask choreographyTask,Participant participant){
		   
		   for (MessageFlow messageFlow : choreographyTask.getMessageFlowRef()) {
			   if(messageFlow.getSourceRef().equals(participant)){
				   return true;
			   }
		   }		
		   return false;
	   } 
	
	
	
	public static Map<ChoreographyTask, List<ChoreographyTask>> getTaskCorrelations (List<Choreography> choreographies, final String participant){
		
		Map<ChoreographyTask, List<ChoreographyTask>> correlations = new HashMap<ChoreographyTask, List<ChoreographyTask>>();
		for (Choreography choreography : choreographies){
			for (FlowElement flowElement : choreography.getFlowElements()) {
				//check if the choreography task can be correlated
				if (flowElement instanceof ChoreographyTask 
						&& getInitiatingParticipant((ChoreographyTask) flowElement).getName().equals(participant)
						&& existMessageSentFromParticipant((ChoreographyTask) flowElement,getInitiatingParticipant((ChoreographyTask) flowElement))
						&& !existMessageSentFromParticipant((ChoreographyTask) flowElement,getReceivingParticipant((ChoreographyTask) flowElement))) {
					
					//find correlated With
					List<ChoreographyTask> correlatedWith = new ArrayList<ChoreographyTask>(); 
					for (Choreography choreographyForCorrelatedWith : choreographies){
						for (FlowElement flowElementForCorrelatedWith : choreographyForCorrelatedWith.getFlowElements()) {
							if (flowElementForCorrelatedWith instanceof ChoreographyTask 
									&& getInitiatingParticipant((ChoreographyTask) flowElementForCorrelatedWith).getName().equals(getReceivingParticipant((ChoreographyTask) flowElement).getName())
									&& getReceivingParticipant((ChoreographyTask) flowElementForCorrelatedWith).getName().equals(getInitiatingParticipant((ChoreographyTask) flowElement).getName())
									&& existMessageSentFromParticipant((ChoreographyTask) flowElementForCorrelatedWith,getInitiatingParticipant((ChoreographyTask) flowElementForCorrelatedWith))
									&& !existMessageSentFromParticipant((ChoreographyTask) flowElementForCorrelatedWith,getReceivingParticipant((ChoreographyTask) flowElementForCorrelatedWith))) {
								
								correlatedWith.add((ChoreographyTask) flowElementForCorrelatedWith);
							}
						}
					}
					correlations.put((ChoreographyTask) flowElement, correlatedWith);
				}
			}
		}
		return correlations;
	}
	
	/*public static List<ChoreographyTask> getChoreographyTaskWithParticipantIsInitiating(DocumentRoot documentRoot,
			final String participant) {

		Choreography choreography = getDefaultChoreography(documentRootToChoreographyList(documentRoot));

		List<ChoreographyTask> choreographyTaskWithParticipantIsInitiating = new ArrayList<ChoreographyTask>();
		for (FlowElement flowElement : choreography.getFlowElements()) {
			if (flowElement instanceof ChoreographyTask
					&& getInitiatingParticipant((ChoreographyTask) flowElement).getName().equals(participant)) {
				choreographyTaskWithParticipantIsInitiating.add((ChoreographyTask) flowElement);
			}
		}
		return choreographyTaskWithParticipantIsInitiating;
	}
	public static List<ChoreographyTask> getChoreographyTaskWithParticipantIsReceiving(DocumentRoot documentRoot,
			final String participant) {

		Choreography choreography = getDefaultChoreography(documentRootToChoreographyList(documentRoot));

		List<ChoreographyTask> choreographyTaskWithParticipantIsReceiving = new ArrayList<ChoreographyTask>();
		for (FlowElement flowElement : choreography.getFlowElements()) {
			if (flowElement instanceof ChoreographyTask
					&& getReceivingParticipant((ChoreographyTask) flowElement).getName().equals(participant)) {
				choreographyTaskWithParticipantIsReceiving.add((ChoreographyTask) flowElement);
			}
		}
		return choreographyTaskWithParticipantIsReceiving;

	}
	*/

}
