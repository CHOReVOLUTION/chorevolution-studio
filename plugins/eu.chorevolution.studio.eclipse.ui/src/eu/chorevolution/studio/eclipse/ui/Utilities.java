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
package eu.chorevolution.studio.eclipse.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.eclipse.bpmn2.Choreography;
import org.eclipse.bpmn2.ChoreographyTask;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.MessageFlow;
import org.eclipse.bpmn2.Participant;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import eu.chorevolution.modelingnotations.adapter.AdapterModel;
import eu.chorevolution.modelingnotations.adapter.ChoreographyComplexItem;
import eu.chorevolution.modelingnotations.adapter.ChoreographyDataItem;
import eu.chorevolution.modelingnotations.adapter.ChoreographyMessage;
import eu.chorevolution.modelingnotations.adapter.ChoreographyMessageType;
import eu.chorevolution.modelingnotations.adapter.ChoreographySimpleItem;
import eu.chorevolution.modelingnotations.adapter.ComplexItem;
import eu.chorevolution.modelingnotations.adapter.DataItem;
import eu.chorevolution.modelingnotations.adapter.DataItemRelation;
import eu.chorevolution.modelingnotations.adapter.Message;
import eu.chorevolution.modelingnotations.adapter.MessageRelation;
import eu.chorevolution.modelingnotations.adapter.MessageType;
import eu.chorevolution.modelingnotations.adapter.OccurencesType;
import eu.chorevolution.modelingnotations.adapter.OperationRelation;
import eu.chorevolution.modelingnotations.adapter.SimpleItem;
import eu.chorevolution.studio.eclipse.core.ChorevolutionCoreUtils;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPreferenceData;
import eu.chorevolution.studio.eclipse.core.utils.MappingAssociation;
import eu.chorevolution.studio.eclipse.core.utils.MappingListReturnType;
import eu.chorevolution.studio.eclipse.core.utils.Tree;
import eu.chorevolution.studio.eclipse.core.utils.TreeNode;
import eu.chorevolution.studio.eclipse.core.utils.TreeNodeDataType;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.AdapterModelDataType;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.WSDLOrGIDLParticipantsData;
import eu.chorevolution.synthesisprocessor.rest.business.AdapterGeneratorException;

public class Utilities {

	private final static String WSDLPlaceHolder = "wsdl";
	private final static String GIDLPlaceHolder = "XMI";
	private final static String TASK_NAME = "TASK_NAME";
	private final static String MESSAGE_NAME = "MESSAGE_NAME";
	private final static String MESSAGE_TYPE = "MESSAGE_TYPE";
	
	private final static String TASK_TYPE = "0";
	private final static String INITIATING_MESSAGE_TYPE = "1";
	private final static String NON_INITIATING_MESSAGE_TYPE = "2";
	private final static String INITIATING_TYPE_TYPE = "3";
	private final static String NON_INITIATING_TYPE_TYPE = "4";
	private final static String STARTING_TYPES_NODE = "5";
	
	private final static String ONLY_INITIATING = "0";
	private final static String ONLY_RECEIVING = "1";
	private final static String BOTH_INITIATING_RECEIVING = "2";

	private static final String TEMPFILE_SUFFIX = "adaptergenerator";
	private static final String ADAPTER_FILE_EXTENSION = ".adapter";
	
	private static final String INPUT_FAKE_TYPE = "request_fakeTypeNotShown";
	private static final String OUTPUT_FAKE_TYPE = "response_fakeTypeNotShown";
	
	private static final String INPUT_ADDED_TYPE = "inputDataType";
	private static final String OUTPUT_ADDED_TYPE = "outputDataType";
	
	private static final String TRANSFORMATION_RULE_STATICVALUE = "\"\"";
	private static final String TRANSFORMATION_RULE_VALUEOF = "valueof";
	private static final String TRANSFORMATION_RULE_CONCAT = "+";
	private static final String TRANSFORMATION_RULE_JSON = "json";
	private static final String TRANSFORMATION_RULE_PARSE_DATE = "parseDate";
	private static final String TRANSFORMATION_RULE_INPUT_MESSAGE = "inputMessage";
	
	public static String getTransformationRuleInputMessage() {
		return TRANSFORMATION_RULE_INPUT_MESSAGE;
	}
	
	public static String getTransformationRuleParseDate() {
		return TRANSFORMATION_RULE_PARSE_DATE;
	}

	public static String getTransformationRuleStaticvalue() {
		return TRANSFORMATION_RULE_STATICVALUE;
	}

	public static String getTransformationRuleValueof() {
		return TRANSFORMATION_RULE_VALUEOF;
	}

	public static String getTransformationRuleConcat() {
		return TRANSFORMATION_RULE_CONCAT;
	}
	
	public static String getTransformationRuleJson() {
		return TRANSFORMATION_RULE_JSON;
	}

	public static String getInputFakeType() {
		return INPUT_FAKE_TYPE;
	}

	public static String getOutputFakeType() {
		return OUTPUT_FAKE_TYPE;
	}

	public static String getSTARTING_TYPES_NODE() {
		return STARTING_TYPES_NODE;
	}

	public static String getONLY_INITIATING() {
		return ONLY_INITIATING;
	}

	public static String getONLY_RECEIVING() {
		return ONLY_RECEIVING;
	}

	public static String getBOTH_INITIATING_RECEIVING() {
		return BOTH_INITIATING_RECEIVING;
	}

	public static String getTASK_TYPE() {
		return TASK_TYPE;
	}

	public static String getINITIATING_TYPE_TYPE() {
		return INITIATING_TYPE_TYPE;
	}
	
	public static String getNON_INITIATING_TYPE_TYPE() {
		return NON_INITIATING_TYPE_TYPE;
	}

	public static String getINITIATING_MESSAGE_TYPE() {
		return INITIATING_MESSAGE_TYPE;
	}

	public static String getNON_INITIATING_MESSAGE_TYPE() {
		return NON_INITIATING_MESSAGE_TYPE;
	}

	public static String getInputAddedType() {
		return INPUT_ADDED_TYPE;
	}

	public static String getOutputAddedType() {
		return OUTPUT_ADDED_TYPE;
	}

	public static Map<String, Text> createGrupContents(Composite container, String name, List<ChorevolutionPreferenceData> contents) {
		Group group = new Group(container, SWT.SHADOW_ETCHED_IN);
		group.setText(name);
		group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		GridLayout gl = new GridLayout(2, false);
		gl.marginTop = 10;
		gl.marginBottom = 10;
		group.setLayout(gl);

		Map<String, Text> textsElements = new HashMap<String, Text>();

		for (ChorevolutionPreferenceData sourceModelPref : contents) {
			Label label = new Label(group, SWT.NONE);
			label.setText(sourceModelPref.getLabel());

			Text text = new Text(group, SWT.BORDER);
			text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			text.setText(sourceModelPref.getValue());

			textsElements.put(sourceModelPref.getID(), text);
		}
		return textsElements;
	}
	
	public static Map<String, Text> createContents(Composite parent, String name, List<ChorevolutionPreferenceData> contents) {
		Composite container = new Composite(parent, SWT.NULL);
		
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.verticalSpacing = 10;
		gridLayout.marginBottom = 0;
		container.setLayout(gridLayout);
		
		container.setLayout(gridLayout);

		Map<String, Text> textsElements = new HashMap<String, Text>();

		for (ChorevolutionPreferenceData sourceModelPref : contents) {
			Label label = new Label(container, SWT.NONE);
			label.setText(sourceModelPref.getLabel());

			Text text = new Text(container, SWT.BORDER);
			text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			text.setText(sourceModelPref.getValue());

			textsElements.put(sourceModelPref.getID(), text);
		}
		return textsElements;
	}
	
	public static boolean isWSDL(byte[] document) throws SAXException, IOException, ParserConfigurationException {

		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(document));
		NodeList elements = ((org.w3c.dom.Document) d).getElementsByTagName("definitions");
		ArrayList<String> operations = new ArrayList<String>();
		for (int i = 0; i < elements.getLength(); i++) {
			operations.add(elements.item(i).getAttributes().getNamedItem("xmlns").getNodeValue());
		}
		
		if(operations.isEmpty())
			return false;
		
		for(int i = 0; i < operations.size(); i++)
			if(operations.get(i).contains(WSDLPlaceHolder))
				return true;
		
		//return operations.toArray(new String[operations.size()]);
		
		return false;
	}
	
	public static boolean isGIDL(byte[] document) throws SAXException, IOException, ParserConfigurationException {
		
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(document));
		NodeList elements = ((org.w3c.dom.Document) d).getElementsByTagName("gidl:GIDLModel");
		ArrayList<String> operations = new ArrayList<String>();
		for (int i = 0; i < elements.getLength(); i++) {
			operations.add(elements.item(i).getAttributes().getNamedItem("xmlns:xmi").getNodeValue());
		}
		
		if(operations.isEmpty())
			return false;
		
		for(int i = 0; i < operations.size(); i++)
			if(operations.get(i).contains(GIDLPlaceHolder))
				return true;
		
		
		return false;
	}
	
	
	public static Tree buildTreeFromTask(String taskName, String taskID, List<Choreography> choreographies, IFile bpmnFile, byte[] typesXSD) {
		
		Tree returnTree = new Tree();

		ChoreographyTask taskToExamine = null;

		for (Choreography choreography : choreographies) {
			for(FlowElement flowEle : choreography.getFlowElements()) {
				if(flowEle instanceof ChoreographyTask) {
					ChoreographyTask chorTask = (ChoreographyTask) flowEle;
					if(chorTask.getId().equals(taskID)) {
						taskToExamine = chorTask;
						break;
					}
				}   
			}
		}
		
		TreeNode<TreeNodeDataType> root = new TreeNode<TreeNodeDataType>(new TreeNodeDataType("root", "root"));
		TreeNode<TreeNodeDataType> startingNode = new TreeNode<TreeNodeDataType>(new TreeNodeDataType(getTASK_TYPE(), taskToExamine.getName()));

		root.addChild(startingNode);
		
		for(MessageFlow messageFlow : taskToExamine.getMessageFlowRef()) {
			
			Participant initiatingParticipant = taskToExamine.getInitiatingParticipantRef();
			
			String messageSenderName=((Participant)messageFlow.getSourceRef()).getName();
			String messageName=messageFlow.getMessageRef().getName();
			
			String messageKind = null;//initiating or not
			String typeKind = null;
			TreeNode<TreeNodeDataType> kind = null;
			if(initiatingParticipant.getName().equals(messageSenderName)) {
				typeKind = getINITIATING_TYPE_TYPE();
				messageKind = getINITIATING_MESSAGE_TYPE();
			}
			else {
				typeKind = getNON_INITIATING_TYPE_TYPE();
				messageKind = getNON_INITIATING_MESSAGE_TYPE();
			}
			
			kind = new TreeNode<TreeNodeDataType>(new TreeNodeDataType(messageKind, messageName));
			startingNode.addChild(kind);
			
			String messageType;
			TreeNode<TreeNodeDataType> type = null;
			try {
				messageType = getTypeFromMessageFlow(messageFlow, bpmnFile);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				messageType = null;
			}
			
			type = new TreeNode<TreeNodeDataType>(new TreeNodeDataType(typeKind, messageType));
			kind.addChild(type);
			
		    

			try {
				List<TreeNode<TreeNodeDataType>> listOfTypes = getCompleteTypeDefinitionsFromTypesXSD(typesXSD, messageType);
				for(TreeNode<TreeNodeDataType> node : listOfTypes) {
					type.addChild(node);	
				}
			} catch (SAXException | IOException | ParserConfigurationException | CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			
			
		}
		
		returnTree.setRootElement(root);
		
		return returnTree;
	}
	
	
	
	public static Tree buildTreeFromWSDL(byte[] documentWSDL) throws SAXException, IOException, ParserConfigurationException {
		
		Tree returnTree = new Tree();
		TreeNode<TreeNodeDataType> root = new TreeNode<TreeNodeDataType>(new TreeNodeDataType("root", "root"));
		returnTree.setRootElement(root);

		List<String> opNames = Utilities.getTaskNamesFromWSDL(documentWSDL);

		for(String opName : opNames) {
		
			TreeNode<TreeNodeDataType> startingNode = new TreeNode<TreeNodeDataType>(new TreeNodeDataType(getTASK_TYPE(), opName));
			root.addChild(startingNode);
			
			String typeKind = null;
			
			for(int h=0; h<2; h++) {
				
				if(h==0)
					typeKind = Utilities.getONLY_INITIATING();
				else
					typeKind = Utilities.getONLY_RECEIVING();
				
				String messagesName = Utilities.getMessageNamesFromTaskWSDL(documentWSDL, opName, typeKind).get(0);	

				TreeNode<TreeNodeDataType> kind = null;
				
				if(h==0)
					kind = new TreeNode<TreeNodeDataType>(new TreeNodeDataType(Utilities.getINITIATING_MESSAGE_TYPE(), messagesName));
				else
					kind = new TreeNode<TreeNodeDataType>(new TreeNodeDataType(Utilities.getNON_INITIATING_MESSAGE_TYPE(), messagesName));

				startingNode.addChild(kind);
				
				String messageType;
				TreeNode<TreeNodeDataType> type = null;
				try {
					messageType = Utilities.getMessagesTypesFromMessageWSDL(documentWSDL, messagesName).get(0);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					messageType = null;
				}
				
				if(h==0)
					type = new TreeNode<TreeNodeDataType>(new TreeNodeDataType(Utilities.getINITIATING_TYPE_TYPE(), messageType));
				else
					type = new TreeNode<TreeNodeDataType>(new TreeNodeDataType(Utilities.getNON_INITIATING_TYPE_TYPE(), messageType));
					
				kind.addChild(type);

				try {
					TreeNode<TreeNodeDataType> listOfTypes = getCompleteTypeDefinitionsFromWSDL(documentWSDL, messageType);
						type.addChild(listOfTypes.getChildren().get(0));
						
				} catch (SAXException | IOException | ParserConfigurationException | CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
			}	
		
		}
		
		return returnTree;
	}
	
	
	
	public static Tree buildTreeFromGIDL(byte[] documentGIDL) throws SAXException, IOException, ParserConfigurationException {
		
		Tree returnTree = new Tree();
		TreeNode<TreeNodeDataType> root = new TreeNode<TreeNodeDataType>(new TreeNodeDataType("root", "root"));
		returnTree.setRootElement(root);

		List<String> opNames = Utilities.getTaskNamesFromGIDL(documentGIDL);

		for(String opName : opNames) {
		
			TreeNode<TreeNodeDataType> startingNode = new TreeNode<TreeNodeDataType>(new TreeNodeDataType(getTASK_TYPE(), opName));
			root.addChild(startingNode);
			
			String typeKind = null;
			
			for(int h=0; h<2; h++) {
				
				if(h==0)
					typeKind = Utilities.getONLY_INITIATING();
				else
					typeKind = Utilities.getONLY_RECEIVING();
				
				String messagesName = Utilities.getMessageNamesFromTaskGIDL(documentGIDL, opName, typeKind).get(0);	

				TreeNode<TreeNodeDataType> kind = null;
				
				if(h==0)
					kind = new TreeNode<TreeNodeDataType>(new TreeNodeDataType(Utilities.getINITIATING_MESSAGE_TYPE(), messagesName));
				else
					kind = new TreeNode<TreeNodeDataType>(new TreeNodeDataType(Utilities.getNON_INITIATING_MESSAGE_TYPE(), messagesName));

				startingNode.addChild(kind);
				
				String messageType;
				TreeNode<TreeNodeDataType> type = null;
				try {
					messageType = Utilities.getMessagesTypesFromMessageGIDL(documentGIDL, messagesName).get(0);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					messageType = null;
				}
				
				if(h==0)
					type = new TreeNode<TreeNodeDataType>(new TreeNodeDataType(Utilities.getINITIATING_TYPE_TYPE(), messageType));
				else
					type = new TreeNode<TreeNodeDataType>(new TreeNodeDataType(Utilities.getNON_INITIATING_TYPE_TYPE(), messageType));
					
				kind.addChild(type);

				try {
					TreeNode<TreeNodeDataType> listOfTypes = getCompleteTypeDefinitionsFromGIDL(documentGIDL, messageType, messagesName);
						type.addChild(listOfTypes.getChildren().get(0));
						
				} catch (SAXException | IOException | ParserConfigurationException | CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
			}	
		
		}
		
		return returnTree;
	}
	
	
	public static List<WSDLOrGIDLParticipantsData> isValidWSDL(byte[] documentWSDL, List<Choreography> choreographies, IFile bpmnFile, String providerParticipantName) throws SAXException, IOException, ParserConfigurationException {
		
		//in this format "TASKNAME:nameoftask:3:participantName" where 3 is the id of the task or the message
		List<WSDLOrGIDLParticipantsData> notValidFields = new ArrayList<WSDLOrGIDLParticipantsData>();
		
		List<ChoreographyTask> taskToExamine = new ArrayList<ChoreographyTask>();

		   for (Choreography choreography : choreographies) {
			   for(FlowElement flowEle : choreography.getFlowElements()) {
				   if(flowEle instanceof ChoreographyTask) {
					   ChoreographyTask chorTask = (ChoreographyTask) flowEle;
					   
					   //first i have to filter all the tasks with the provider participants sender and/or receiver
					   for(Participant participant : chorTask.getParticipantRefs()) {
							   if(participant.getName().equals(providerParticipantName)) {
								   //this is a chortask to examine
								   taskToExamine.add(chorTask);
							   }
						   }		   
					   }   
				   }
			   }
		   		   
		   	//now i search for operation name (=task name)
			List<String> validTaskNames = getTaskNamesFromWSDL(documentWSDL);
	   
		   //now i have to examine name of tasks, tolowercase and without spaces
		   for(ChoreographyTask chorTask : taskToExamine) {
			   boolean valid = false;
			   for(String validTaskName : validTaskNames) {
				   if(chorTask.getName().toLowerCase().replaceAll("\\s+","").equals(validTaskName.toLowerCase().replaceAll("\\s+","")))
					   valid = true;
			   }
			   
			   if(!valid) {
				   String participantName = "";
				   for(Participant participant : chorTask.getParticipantRefs()) {
					   if(!participant.getName().equals(providerParticipantName)) {
						   participantName = participant.getName();
						   break;
					   }
				   }
				   
				   notValidFields.add(new WSDLOrGIDLParticipantsData(TASK_NAME, chorTask.getName(), chorTask.getId(), chorTask.getName(), chorTask.getId(), participantName, providerParticipantName));
			   }
		   }
			   
		   //now i search for messages name (message name.. and message types input message e output message)
			List<String> validMessagesNames = getMessagesNamesFromWSDL(documentWSDL);
			
			//in wsdl the type is not pure (is tns:type)
			List<String> validMessagesTypes = getMessagesTypesFromWSDL(documentWSDL);
						
			   //now i have to examine message name and type of right participant
			   for(ChoreographyTask chorTask : taskToExamine) {
				   for(MessageFlow messageFlow : chorTask.getMessageFlowRef()) {
					   boolean valid = false;
					   boolean validType = false;
					   
					   String messageSenderName=((Participant)messageFlow.getSourceRef()).getName();
					   String messageName=messageFlow.getMessageRef().getName();
					   String messageType;
					   try {
						   messageType = getTypeFromMessageFlow(messageFlow, bpmnFile);
					   } catch (Exception e1) {
						   messageType = null;
					   }
					   		   
					   
					   if(messageSenderName.equals(providerParticipantName)) {
						   
						   valid = true;
						   
						   for(String messageTypeInList : validMessagesTypes) {
							   if(messageTypeInList.equals(messageType))
								   validType = true;
						   }
						   
					   }
					   else {
						   valid = true;
						   validType = true;
					   }
						   
						   
					   if(!valid) {
						   String participantName = "";
						   for(Participant participant : chorTask.getParticipantRefs()) {
							   if(!participant.getName().equals(providerParticipantName)) {
								   participantName = participant.getName();
								   break;
							   }
						   }
						   notValidFields.add(new WSDLOrGIDLParticipantsData(MESSAGE_NAME, messageName, messageFlow.getMessageRef().getId(), chorTask.getName(), chorTask.getId(), participantName, providerParticipantName));
					   }
					   
					   if(!validType) {
						   String participantName = "";
						   for(Participant participant : chorTask.getParticipantRefs()) {
							   if(!participant.getName().equals(providerParticipantName)) {
								   participantName = participant.getName();
								   break;
							   }
						   }
						   notValidFields.add(new WSDLOrGIDLParticipantsData(MESSAGE_TYPE, messageType, messageFlow.getMessageRef().getId(), chorTask.getName(), chorTask.getId(), participantName, providerParticipantName));
					   }
				   }
			   }
		
		return notValidFields;
	}
	
	
	
	
	
	public static List<String> getTaskNamesFromWSDL(byte[] documentWSDL) throws SAXException, IOException, ParserConfigurationException {
		   		   
	   	//now i search for operation name (=task name)
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(documentWSDL));

		ArrayList<String> validTaskNames = new ArrayList<String>();
		NodeList elements = ((org.w3c.dom.Document) d).getElementsByTagName("operation");
		for (int i = 0; i < elements.getLength(); i++) {
			try {
				boolean isDuplicate=false;
				for(String duplicate : validTaskNames) {
					if(elements.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(duplicate)) {
						isDuplicate=true;
						break;
					}
				}
				if(!isDuplicate)
					validTaskNames.add(elements.item(i).getAttributes().getNamedItem("name").getNodeValue());
			}
			catch(Exception e) {
				//cannot find "name"	
			}
		}
		
		return validTaskNames;
	}
	
	
	public static List<String> getMessageNamesFromTaskWSDL(byte[] documentWSDL, String taskName, String whichKind) throws SAXException, IOException, ParserConfigurationException {
		   		   
		//with whichKind = 0 only the initiating, =1 only the receiving, with = 2 everything
		
	   	//now i search for operation name (=task name)
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(documentWSDL));

		ArrayList<String> validMessagesNames = new ArrayList<String>();
		NodeList elements = ((org.w3c.dom.Document) d).getElementsByTagName("operation");
		
		for (int i = 0; i < elements.getLength(); i++) {
			try {
				
				//for each operation i get the messages sender and receiver name from inside
				if(elements.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(taskName)) {
					//i have to look inside the node
					
					Node eleminside = elements.item(i).getFirstChild();
					do {
						if(eleminside==null)
							break;
						
						//is the input message
						if(whichKind.equals(getONLY_INITIATING()) || whichKind.equals(getBOTH_INITIATING_RECEIVING())) 
							if(eleminside.getNodeName().equals("input")) {
								String thisMessage = eleminside.getAttributes().getNamedItem("message").getNodeValue();
								String[] thisMessageSplit = thisMessage.split(":");
								validMessagesNames.add(thisMessageSplit[thisMessageSplit.length-1]);

							}
						
						if(whichKind.equals(getONLY_RECEIVING()) || whichKind.equals(getBOTH_INITIATING_RECEIVING())) 
							if(eleminside.getNodeName().equals("output")) {
								String thisMessage = eleminside.getAttributes().getNamedItem("message").getNodeValue();
								String[] thisMessageSplit = thisMessage.split(":");
								validMessagesNames.add(thisMessageSplit[thisMessageSplit.length-1]);
								
							}		
						
						
					} while((eleminside = eleminside.getNextSibling()) != null);
					
				}
				
			}
			catch(Exception e) {
				//cannot find "name"	
			}
		}
		
		return validMessagesNames;
	}
	
	
	public static List<String> getMessagesTypesFromMessageWSDL(byte[] documentWSDL, String messageName) throws SAXException, IOException, ParserConfigurationException {
		   
	   	//now i search for operation name (=task name)
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(documentWSDL));

		ArrayList<String> validMessagesTypes = new ArrayList<String>();
		NodeList elements = ((org.w3c.dom.Document) d).getElementsByTagName("message");
		for (int i = 0; i < elements.getLength(); i++) {
			try {
				
				if(elements.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(messageName)) {
					//i search for the part elements in the message
					
					Node eleminside = elements.item(i).getFirstChild();
					do {
						if(eleminside==null)
							break;
						
						//is the part where there is the type description
						if(eleminside.getNodeName().equals("part")) {
							
							String thisTypeDefinition = eleminside.getAttributes().getNamedItem("element").getNodeValue();
							String[] thisTypeDefinitionSplit = thisTypeDefinition.split(":");
							
							//now i search for the definition on the top of the file
							Document d2 = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(documentWSDL));
							NodeList elements2 = ((org.w3c.dom.Document) d).getElementsByTagName("xsd:element");

							String typeToFind = thisTypeDefinitionSplit[thisTypeDefinitionSplit.length-1];
							for(int j=0; j<elements2.getLength(); j++) {
								if(elements2.item(j).getAttributes().getNamedItem("name").getNodeValue().equals(typeToFind)) {
									
									String thisType = elements2.item(j).getAttributes().getNamedItem("type").getNodeValue();
									String[] thisTypeSplit = thisType.split(":");
									validMessagesTypes.add(thisTypeSplit[thisTypeSplit.length-1]);
									
									
								}
								
							}

						}

						
					} while((eleminside = eleminside.getNextSibling()) != null);
					
				}
				
				
			}
			catch(Exception e) {
				//cannot find "name"	
			}
		}
		
		return validMessagesTypes;
	}
	
	
	
	public static List<String> getMessagesNamesFromWSDL(byte[] documentWSDL) throws SAXException, IOException, ParserConfigurationException {
		   
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(documentWSDL));

		//now i search for messages name (message name.. and message types input message e output message)
		ArrayList<String> validMessagesNames = new ArrayList<String>();
		NodeList elements2 = ((org.w3c.dom.Document) d).getElementsByTagName("message");
		for (int i = 0; i < elements2.getLength(); i++) {
			try {
				boolean isDuplicate=false;
				for(String duplicate : validMessagesNames) {
					if(elements2.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(duplicate)) {
						isDuplicate=true;
						break;
					}
				}
				if(!isDuplicate)
					validMessagesNames.add(elements2.item(i).getAttributes().getNamedItem("name").getNodeValue());
			}
			catch(Exception e) {
				//cannot find "name"
			}
		}
		
		return validMessagesNames;
	}
	
	
	public static List<String> getMessagesTypesFromWSDL(byte[] documentWSDL) throws SAXException, IOException, ParserConfigurationException {
		   
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(documentWSDL));

		//in wsdl the type is not pure (is tns:type)
		ArrayList<String> validMessagesTypes = new ArrayList<String>();
		NodeList elements3 = ((org.w3c.dom.Document) d).getElementsByTagName("input");
		for (int i = 0; i < elements3.getLength(); i++) {
			try {
				
				String typeToSplit = elements3.item(i).getAttributes().getNamedItem("message").getNodeValue();
				String[] typeSplitted = typeToSplit.split(":");

				NodeList elementMessage = d.getElementsByTagName("message");
				for(int g=0; g<elementMessage.getLength(); g++) {
					if(elementMessage.item(g).getAttributes().getNamedItem("name").getNodeValue().equals(typeSplitted[typeSplitted.length-1])) {
						Element elemMsg = (Element) elementMessage.item(g);
						NodeList parts = ((Element) elemMsg).getElementsByTagName("part");
						Element part = (Element) parts.item(0);

						String typeToSplit1 = part.getAttributes().getNamedItem("element").getNodeValue();
						String[] typeSplitted1 = typeToSplit1.split(":");

						NodeList elementElem = d.getElementsByTagName("xsd:element");
						for(int h=0; h<elementElem.getLength(); h++) {
							if(elementElem.item(h).getAttributes().getNamedItem("name").getNodeValue().equals(typeSplitted1[typeSplitted1.length-1])) {
								String typeToSplit2 = elementElem.item(h).getAttributes().getNamedItem("type").getNodeValue();
								String[] typeSplitted2 = typeToSplit2.split(":");
								
								
								boolean isDuplicate=false;
								for(String duplicate : validMessagesTypes) {
									if(typeSplitted2[typeSplitted2.length-1].equals(duplicate)) {
										isDuplicate=true;
										break;
									}
								}
								if(!isDuplicate)
									validMessagesTypes.add(typeSplitted2[typeSplitted2.length-1]);
								
							}
						
						}
						
					}
					
				}
				

			}
			catch(Exception e) {
				//cannot find "name"	
			}
		}
		
		NodeList elements4 = ((org.w3c.dom.Document) d).getElementsByTagName("output");
		for (int i = 0; i < elements4.getLength(); i++) {
			try {
				
				String typeToSplit = elements4.item(i).getAttributes().getNamedItem("message").getNodeValue();
				String[] typeSplitted = typeToSplit.split(":");

				NodeList elementMessage = d.getElementsByTagName("message");
				for(int g=0; g<elementMessage.getLength(); g++) {
					if(elementMessage.item(g).getAttributes().getNamedItem("name").getNodeValue().equals(typeSplitted[typeSplitted.length-1])) {
						Element elemMsg = (Element) elementMessage.item(g);
						NodeList parts = ((Element) elemMsg).getElementsByTagName("part");
						Element part = (Element) parts.item(0);

						String typeToSplit1 = part.getAttributes().getNamedItem("element").getNodeValue();
						String[] typeSplitted1 = typeToSplit1.split(":");

						NodeList elementElem = d.getElementsByTagName("xsd:element");
						for(int h=0; h<elementElem.getLength(); h++) {
							if(elementElem.item(h).getAttributes().getNamedItem("name").getNodeValue().equals(typeSplitted1[typeSplitted1.length-1])) {
								String typeToSplit2 = elementElem.item(h).getAttributes().getNamedItem("type").getNodeValue();
								String[] typeSplitted2 = typeToSplit2.split(":");
								
								
								boolean isDuplicate=false;
								for(String duplicate : validMessagesTypes) {
									if(typeSplitted2[typeSplitted2.length-1].equals(duplicate)) {
										isDuplicate=true;
										break;
									}
								}
								if(!isDuplicate)
									validMessagesTypes.add(typeSplitted2[typeSplitted2.length-1]);
								
							}
						
						}
						
					}
					
				}	
			}
			catch(Exception e) {
				//cannot find "name"
			}
		}

		return validMessagesTypes;
	}
	
	
	
	public static List<WSDLOrGIDLParticipantsData> isValidGIDL(byte[] documentGIDL, List<Choreography> choreographies, IFile bpmnFile, String providerParticipantName) throws SAXException, IOException, ParserConfigurationException {
		
		//in this format "TASKNAME:nameoftask:3" where 3 is the id of the task or the message
		List<WSDLOrGIDLParticipantsData> notValidFields = new ArrayList<WSDLOrGIDLParticipantsData>();
		
		List<ChoreographyTask> taskToExamine = new ArrayList<ChoreographyTask>();

		   for (Choreography choreography : choreographies) {
			   for(FlowElement flowEle : choreography.getFlowElements()) {
				   if(flowEle instanceof ChoreographyTask) {
					   ChoreographyTask chorTask = (ChoreographyTask) flowEle;
					   
					   //first i have to filter all the tasks with the provider participants sender and/or receiver
					   for(Participant participant : chorTask.getParticipantRefs()) {
							   if(participant.getName().equals(providerParticipantName)) {
								   //this is a chortask to examine
								   taskToExamine.add(chorTask);
							   }
						   }		   
					   }   
				   }
			   }
		   		   
		   	//now i search for operation name (=task name)
			List<String> validTaskNames = getTaskNamesFromGIDL(documentGIDL);
			
			//now i have to examine name of tasks, tolowercase and without spaces
		   for(ChoreographyTask chorTask : taskToExamine) {
			   boolean valid = false;
			   for(String validTaskName : validTaskNames) {
				   if(chorTask.getName().toLowerCase().replaceAll("\\s+","").equals(validTaskName.toLowerCase().replaceAll("\\s+","")))
					   valid = true;
			   }
			   
			   if(!valid) {
				   String participantName = "";
				   for(Participant participant : chorTask.getParticipantRefs()) {
					   if(!participant.getName().equals(providerParticipantName)) {
						   participantName = participant.getName();
						   break;
					   }
				   }
				   
				   notValidFields.add(new WSDLOrGIDLParticipantsData(TASK_NAME, chorTask.getName(), chorTask.getId(), chorTask.getName(), chorTask.getId(), participantName, providerParticipantName));
			   }
		   }
			   
		   	//now i search for messages name (message name.. and message types input message e output message)
			List<String> validMessagesNames = getMessagesNamesFromGIDL(documentGIDL);

			//types
			List<String> validMessagesTypes = getMessagesTypesFromGIDL(documentGIDL);

						
			   //now i have to examine message name and type of right participant
			   for(ChoreographyTask chorTask : taskToExamine) {
				   for(MessageFlow messageFlow : chorTask.getMessageFlowRef()) {
					   boolean valid = false;
					   boolean validType = false;
					   
					   String messageSenderName=((Participant)messageFlow.getSourceRef()).getName();
					   String messageName=messageFlow.getMessageRef().getName();
					   String messageType;
					   try {
						   messageType = getTypeFromMessageFlow(messageFlow, bpmnFile);
					   } catch (Exception e1) {
						   // TODO Auto-generated catch block
						   messageType = null;
					   }
					   

					   if(messageSenderName.equals(providerParticipantName)) {
						   //must have one of the names specified above
						   for(String validMessageName : validMessagesNames) {

							   if(messageName.equals(validMessageName)) {
								   valid = true;
								   //now i examine the message type
								   validType = false;
								   for(String messageTypeInList : validMessagesTypes) {
									   if(messageTypeInList.equals(messageType))
										   validType = true;
								   }
						   		}
						   }
					   }
					   else {
						   valid = true;
						   validType = true;
					   }
						   
						   
					   if(!valid) {
						   String participantName = "";
						   for(Participant participant : chorTask.getParticipantRefs()) {
							   if(!participant.getName().equals(providerParticipantName)) {
								   participantName = participant.getName();
								   break;
							   }
						   }
						   
						   notValidFields.add(new WSDLOrGIDLParticipantsData(MESSAGE_NAME, messageName, messageFlow.getMessageRef().getId(), chorTask.getName(), chorTask.getId(), participantName, providerParticipantName));
					   }
					   
					   if(!validType) {
						   String participantName = "";
						   for(Participant participant : chorTask.getParticipantRefs()) {
							   if(!participant.getName().equals(providerParticipantName)) {
								   participantName = participant.getName();
								   break;
							   }
						   }
						   
						   notValidFields.add(new WSDLOrGIDLParticipantsData(MESSAGE_TYPE, messageType, messageFlow.getMessageRef().getId(), chorTask.getName(), chorTask.getId(), participantName, providerParticipantName));
					   }
				   }
			   }
		
		return notValidFields;
	}
	
	
	
	
	public static List<String> getTaskNamesFromGIDL(byte[] documentGIDL) throws SAXException, IOException, ParserConfigurationException {
		   		   
	   	//now i search for operation name (=task name)
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(documentGIDL));

		ArrayList<String> validTaskNames = new ArrayList<String>();
		NodeList elements = ((org.w3c.dom.Document) d).getElementsByTagName("hasOperations");
		for (int i = 0; i < elements.getLength(); i++) {
			try {
				boolean isDuplicate=false;
				for(String duplicate : validTaskNames) {
					if(elements.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(duplicate)) {
						isDuplicate=true;
						break;
					}
				}
				if(!isDuplicate)				
					validTaskNames.add(elements.item(i).getAttributes().getNamedItem("name").getNodeValue());
			}
			catch(Exception e) {
				//cannot find "name"	
			}
		}
		
		return validTaskNames;
	}
	
	
	public static List<String> getMessageNamesFromTaskGIDL(byte[] documentGIDL, String taskName, String whichKind) throws SAXException, IOException, ParserConfigurationException {
		   
	   	//now i search for operation name (=task name)
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(documentGIDL));

		ArrayList<String> validMessagesNames = new ArrayList<String>();
		NodeList elements = ((org.w3c.dom.Document) d).getElementsByTagName("hasOperations");
		
		for (int i = 0; i < elements.getLength(); i++) {
			try {

				//for each operation i get the messages sender and receiver name from inside
				if(elements.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(taskName)) {
					//i have to look inside the node

					Node eleminside = elements.item(i).getFirstChild();
					do {
						if(eleminside==null)
							break;

						//is the input message
						if(whichKind.equals(getONLY_INITIATING()) || whichKind.equals(getBOTH_INITIATING_RECEIVING())) 
							if(eleminside.getNodeName().equals("inputData")) {
								//go deeper
								
								validMessagesNames.add(eleminside.getAttributes().getNamedItem("name").getNodeValue());

							}
						
						if(whichKind.equals(getONLY_RECEIVING()) || whichKind.equals(getBOTH_INITIATING_RECEIVING())) 
							if(eleminside.getNodeName().equals("outputData")) {
								
								Node eleminsideinside = eleminside.getFirstChild();
								validMessagesNames.add(eleminside.getAttributes().getNamedItem("name").getNodeValue());

							}		
						
						
					} while((eleminside = eleminside.getNextSibling()) != null);
					
				}
				
			}
			catch(Exception e) {
				//cannot find "name"	
			}
		}
		
		return validMessagesNames;
	}
	
	
	public static List<String> getMessagesTypesFromMessageGIDL(byte[] documentGIDL, String messageName) throws SAXException, IOException, ParserConfigurationException {
		
		//with the current GIDL formatting, is not necessary to return the function above
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(documentGIDL));
		ArrayList<String> validMessagesTypes = new ArrayList<String>();

		Element message = (Element) ((org.w3c.dom.Document) d).getElementsByTagName("inputData").item(0);
		if(message.getAttributes().getNamedItem("name").getNodeValue().equals(messageName)) {
			Element nodeInside = (Element) message.getFirstChild();
			validMessagesTypes.add(nodeInside.getAttributes().getNamedItem("name").getNodeValue());
		}
		else {
			message = (Element) ((org.w3c.dom.Document) d).getElementsByTagName("outputData").item(0);
			
			Element nodeInside = (Element) message.getFirstChild();
			validMessagesTypes.add(nodeInside.getAttributes().getNamedItem("name").getNodeValue());

		}
		
		//validMessagesTypes.add(messageName);
		return validMessagesTypes;
	}
	
	
	
	
	public static List<String> getMessagesNamesFromGIDL(byte[] documentGIDL) throws SAXException, IOException, ParserConfigurationException {
		   
	   	//now i search for operation name (=task name)
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(documentGIDL));
		ArrayList<String> validMessagesNames = new ArrayList<String>();
		
		Node eleminside = ((org.w3c.dom.Document) d).getElementsByTagName("inputData").item(0);
		if(eleminside.getNodeName().equals("inputData")) {
			//go deeper
			Node eleminsideinside = eleminside.getFirstChild();
			do {
				try {
					if(eleminsideinside.getNodeName().equals("hasDataType")) {

						boolean isDuplicate=false;
						for(String duplicate : validMessagesNames) {
							if(eleminsideinside.getAttributes().getNamedItem("name").getNodeValue().equals(duplicate)) {
								isDuplicate=true;
								break;
							}
						}
						if(!isDuplicate) {
							validMessagesNames.add(eleminsideinside.getAttributes().getNamedItem("name").getNodeValue());
						}
					}
				}
				catch(Exception e) {
				}
				
			} while((eleminsideinside = eleminsideinside.getNextSibling()) != null);

		}
		
		eleminside = ((org.w3c.dom.Document) d).getElementsByTagName("outputData").item(0);
		if(eleminside.getNodeName().equals("outputData")) {
			//go deeper
			Node eleminsideinside = eleminside.getFirstChild();
			do {
				try {
					if(eleminsideinside.getNodeName().equals("hasDataType")) {

						boolean isDuplicate=false;
						for(String duplicate : validMessagesNames) {
							if(eleminsideinside.getAttributes().getNamedItem("name").getNodeValue().equals(duplicate)) {
								isDuplicate=true;
								break;
							}
						}
						if(!isDuplicate) {
							validMessagesNames.add(eleminsideinside.getAttributes().getNamedItem("name").getNodeValue());
						}
					}
				}
				catch(Exception e) {
				}
				
			} while((eleminsideinside = eleminsideinside.getNextSibling()) != null);

		}
		
		
		return validMessagesNames;
	}
	
	
	public static List<String> getMessagesTypesFromGIDL(byte[] documentGIDL) throws SAXException, IOException, ParserConfigurationException {
		   
	   	//now i search for operation name (=task name)
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(documentGIDL));
		
		ArrayList<String> validMessagesTypes = new ArrayList<String>();
		NodeList elements3 = ((org.w3c.dom.Document) d).getElementsByTagName("hasDataType");
		for (int i = 0; i < elements3.getLength(); i++) {
			try {
				boolean isDuplicate=false;
				for(String duplicate : validMessagesTypes) {
					if(elements3.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(duplicate)) {
						isDuplicate=true;
						break;
					}
				}
				if(!isDuplicate)
					validMessagesTypes.add(elements3.item(i).getAttributes().getNamedItem("name").getNodeValue());
			}
			catch(Exception e) {
				//cannot find "name"	
			}
		}
		
		return validMessagesTypes;
	}
	
	
	public static String getTypeFromMessageFlow(MessageFlow messageFlow, IFile bpmnFile) throws SAXException, IOException, ParserConfigurationException, CoreException {
	   String definitionID = messageFlow.getMessageRef().getItemRef().getId();
		
	   Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(bpmnFile.getContents());
	   NodeList elements = ((org.w3c.dom.Document) d).getElementsByTagName("bpmn2:itemDefinition");
	   for (int i = 0; i < elements.getLength(); i++) {
		   if(elements.item(i).getAttributes().getNamedItem("id").getNodeValue().equals(definitionID)) {
			   try {
				   String thisType = elements.item(i).getAttributes().getNamedItem("structureRef").getNodeValue();
				   String[] thisTypeSplit = thisType.split(":");  
				   return thisTypeSplit[thisTypeSplit.length-1];
			   }
			   catch(Exception e) {
				   //cannot find type
				   return null;
			   }
		   }
	   }
	   return null;
	}
	
	
	
	
	


	public static TreeNode<TreeNodeDataType> getCompleteTypeDefinitionsFromGIDL(byte[] documentGIDL, String typeName, String msgName) throws SAXException, IOException, ParserConfigurationException, CoreException {
		
		TreeNode<TreeNodeDataType> rootElem = getTypeDefinitionsFromGIDL(documentGIDL, typeName, 0, msgName);
		
		//now i have to clean the tree, delete all nodes with getStarting_types_node

		rootElem = cleanTree(rootElem);
		
		//the first is getstarting..
		
		List<TreeNode<TreeNodeDataType>> listToReturn = new ArrayList<TreeNode<TreeNodeDataType>>();
		for(TreeNode<TreeNodeDataType> node : rootElem.getChildren()) {
			listToReturn.add(node);
		}
		
    	TreeNode<TreeNodeDataType> thisinput = new TreeNode<TreeNodeDataType>();
		for(TreeNode<TreeNodeDataType> trr : listToReturn) {
			thisinput.addChild(trr);
		}
		
		assignOccurrencesToGIDLTree(documentGIDL, thisinput);
		assignComplexOrSimpleToGIDLTree(documentGIDL, thisinput);
		refineComplexOrSimpleTypes(thisinput);
		
		return thisinput;
	}
	
	
	public static void assignOccurrencesToGIDLTree(byte[] gidlDocument, TreeNode<TreeNodeDataType> treeNode) throws SAXException, IOException, ParserConfigurationException {
		
	   	//now i search for operation name (=task name)
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(gidlDocument));

		//element
		NodeList elements = ((org.w3c.dom.Document) d).getElementsByTagName("hasDataType");
		for (int i = 0; i < elements.getLength(); i++) {
			try {
				if(elements.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(treeNode.getData().getContent())) {					
					try {//for simple types its needed
						Node occurrence = elements.item(i);	

						try {

							treeNode.getData().setMaxOccurrences(replaceNumbersWithLetters(occurrence.getAttributes().getNamedItem("maxOccurs").getNodeValue()));
							treeNode.getData().setMinOccurrences(replaceNumbersWithLetters(occurrence.getAttributes().getNamedItem("minOccurs").getNodeValue()));
							
						}
						catch(Exception e) {
						
						}
						

					}
					catch(Exception e) {}
				}
			}
			catch(Exception e) {}
		}		
		
		
		for(TreeNode<TreeNodeDataType> node : treeNode.getChildren())
			assignOccurrencesToGIDLTree(gidlDocument, node);
		
	}
	
	

	public static void assignComplexOrSimpleToGIDLTree(byte[] gidlDocument, TreeNode<TreeNodeDataType> treeNode) throws SAXException, IOException, ParserConfigurationException {
		
	   	//now i search for operation name (=task name)
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(gidlDocument));

		//element
		NodeList elements = ((org.w3c.dom.Document) d).getElementsByTagName("hasDataType");
		for (int i = 0; i < elements.getLength(); i++) {
			try {
				if(elements.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(treeNode.getData().getContent())) {					
					try {//for simple types its needed
						Node occurrence = elements.item(i);	

						if(occurrence.getAttributes().getNamedItem("xsi:type").getNodeValue().equals("gidl:ComplexType"))
							treeNode.getData().setComplexOrSimpleType("complexType");
						else
							treeNode.getData().setComplexOrSimpleType("simpleType");
					}
					catch(Exception e) {}
				}
			}
			catch(Exception e) {}
		}		
		
		
		for(TreeNode<TreeNodeDataType> node : treeNode.getChildren())
			assignComplexOrSimpleToGIDLTree(gidlDocument, node);
		
		
	}
	
	
	public static TreeNode<TreeNodeDataType> getTypeDefinitionsFromGIDL(byte[] documentGIDL, String typeName, int iterationCalled, String msgName) throws SAXException, IOException, ParserConfigurationException, CoreException {

		TreeNode<TreeNodeDataType> rootElem = new TreeNode<TreeNodeDataType>(new TreeNodeDataType(getSTARTING_TYPES_NODE(), getSTARTING_TYPES_NODE()));
		TreeNode<TreeNodeDataType> rootElem2;

		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(documentGIDL));
		
		//is needed to distinguish between input and output message: there can be omonimous types in input and output (es: root)
		NodeList elementsMsgs = ((org.w3c.dom.Document) d).getElementsByTagName("inputData");
		Node element = null;
		for(int i = 0; i<elementsMsgs.getLength(); i++) {
			if(elementsMsgs.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(msgName)) {
				element = elementsMsgs.item(i);
				break;
			}
		}
		if(element == null) {
			elementsMsgs = ((org.w3c.dom.Document) d).getElementsByTagName("outputData");
			for(int i = 0; i<elementsMsgs.getLength(); i++) {
				if(elementsMsgs.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(msgName)) {
					element = elementsMsgs.item(i);
					break;
				}
			}	
		}

		
		//all types (only in GIDL)
		NodeList elementsComplex = ((Element) element).getElementsByTagName("hasDataType");
		for (int i = 0; i < elementsComplex.getLength(); i++) {
			if(elementsComplex.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(typeName)) {
				try {
					Node eleminside = elementsComplex.item(i).getFirstChild();
					
					//added
					if(iterationCalled == 0) {
						rootElem.addChild(new TreeNode<TreeNodeDataType>(new TreeNodeDataType(elementsComplex.item(i).getAttributes().getNamedItem("name").getNodeValue(), typeName)));
						rootElem2 = rootElem;
						rootElem = rootElem.getChildren().get(rootElem.getNumberOfChildren()-1);
					}
					else
						rootElem2 = rootElem;
					
					
					List<TreeNode<TreeNodeDataType>> listReturn = recursiveGIDLElement(eleminside, documentGIDL, msgName);

					for(TreeNode tree : listReturn)
						rootElem.addChild(tree);

					return rootElem2;

				}
				catch(Exception e) {
					//cannot find type
					return null;
				}
			}
		}

		if(rootElem.getNumberOfChildren() == 0) {//is an ending-type

			NodeList elements = ((org.w3c.dom.Document) d).getElementsByTagName("hasDataType");
			for(int i=0; i<elements.getLength(); i++) {
				if(elements.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(typeName)) {
					
					if(elements.item(i).getAttributes().getNamedItem("xsi:type").getNodeValue().equals("gidl:ComplexType")) {//complex type without "type" attribute
						return new TreeNode<TreeNodeDataType>(new TreeNodeDataType(elements.item(i).getAttributes().getNamedItem("name").getNodeValue(), elements.item(i).getAttributes().getNamedItem("name").getNodeValue()));
					}
					else {
						return new TreeNode<TreeNodeDataType>(new TreeNodeDataType(elements.item(i).getAttributes().getNamedItem("type").getNodeValue(), elements.item(i).getAttributes().getNamedItem("name").getNodeValue()));
					}
					
				}
			}
		}

		return rootElem;
	}


	public static List<TreeNode<TreeNodeDataType>> recursiveGIDLElement(Node nodeInside, byte[] documentGIDL, String msgName) {

		List<TreeNode<TreeNodeDataType>> listTree = new ArrayList<TreeNode<TreeNodeDataType>>();
		Node eleminside = nodeInside;

		if(eleminside != null) {

			//for all types
			if(eleminside.getNodeName().equals("hasDataType")) {
				if(eleminside.getAttributes().getNamedItem("xsi:type").getNodeValue().equals("gidl:ComplexType")) {//complex type without "type" attribute
					listTree.add(new TreeNode<TreeNodeDataType>(new TreeNodeDataType(eleminside.getAttributes().getNamedItem("name").getNodeValue(), eleminside.getAttributes().getNamedItem("name").getNodeValue())));
				}
				else {
					listTree.add(new TreeNode<TreeNodeDataType>(new TreeNodeDataType(eleminside.getAttributes().getNamedItem("type").getNodeValue(), eleminside.getAttributes().getNamedItem("name").getNodeValue())));
				}
				
					try {
						listTree.get(listTree.size()-1).addChild(getTypeDefinitionsFromGIDL(documentGIDL, eleminside.getAttributes().getNamedItem("name").getNodeValue(), 1, msgName));
					} catch (SAXException | IOException | ParserConfigurationException | CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			}
			
			
			if(eleminside.getNextSibling() != null) {
				List<TreeNode<TreeNodeDataType>> listTreeChild = recursiveGIDLElement(eleminside.getNextSibling(), documentGIDL, msgName);
				for(TreeNode tree : listTreeChild) {
					listTree.add(tree);  
				}

			}

		}

		return listTree;
	}

	
	
	
	
	
	
	
	

	public static TreeNode<TreeNodeDataType> getCompleteTypeDefinitionsFromWSDL(byte[] documentWSDL, String typeName) throws SAXException, IOException, ParserConfigurationException, CoreException {

		TreeNode<TreeNodeDataType> rootElem = getTypeDefinitionsFromWSDL(documentWSDL, typeName, 0);
		
		//now i have to clean the tree, delete all nodes with getStarting_types_node

		rootElem = cleanTree(rootElem);
		
		//the first is getstarting..
		
		List<TreeNode<TreeNodeDataType>> listToReturn = new ArrayList<TreeNode<TreeNodeDataType>>();
		for(TreeNode<TreeNodeDataType> node : rootElem.getChildren()) {
			listToReturn.add(node);
		}
		
    	TreeNode<TreeNodeDataType> thisinput = new TreeNode<TreeNodeDataType>();
		for(TreeNode<TreeNodeDataType> trr : listToReturn) {
			thisinput.addChild(trr);
		}
		
		assignOccurrencesToWSDLTree(documentWSDL, thisinput);
		assignComplexOrSimpleToWSDLTree(documentWSDL, thisinput);
		refineComplexOrSimpleTypes(thisinput);
		fixRestrictionsToTree(thisinput);
		
		return thisinput;
	}
	

	private static void fixRestrictionsToTree(TreeNode<TreeNodeDataType> treeNode) {

		
		try {
			if(treeNode.getData().getComplexOrSimpleType().equals("complexType")) {
				if(treeNode.getNumberOfChildren() == 1) {
					if(treeNode.getChildren().get(0).getData().isRestriction()) {
						TreeNode<TreeNodeDataType> treeChild = treeNode.getChildren().get(0);
						//set this as simple type, with type the restriction type
						treeNode.getData().setType(treeChild.getData().getType());
						treeNode.getData().setDataType(treeChild.getData().getDataType());

						treeNode.getData().setComplexOrSimpleType("simpleType");
						treeNode.setChildren(null);
					}
				}
			}
		}
		catch(Exception e) {}
		
		for(TreeNode<TreeNodeDataType> node : treeNode.getChildren())
			Utilities.fixRestrictionsToTree(node);
		
	}

	public static String replaceNumbersWithLetters(String input) {
		
		if(input.equals("0"))
			return "zero";
		if(input.equals("1"))
			return "one";
		
		return input;
		
	}
	
	public static void assignOccurrencesToWSDLTree(byte[] wsdlDocument, TreeNode<TreeNodeDataType> treeNode) throws SAXException, IOException, ParserConfigurationException {
		
	   	//now i search for operation name (=task name)
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(wsdlDocument));

		//element
		NodeList elements = ((org.w3c.dom.Document) d).getElementsByTagName("xsd:element");
		for (int i = 0; i < elements.getLength(); i++) {
			try {
				if(elements.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(treeNode.getData().getContent())) {					
					try {//for simple types its needed
						Node occurrence = elements.item(i);	

						
						treeNode.getData().setMaxOccurrences(replaceNumbersWithLetters(occurrence.getAttributes().getNamedItem("maxOccurs").getNodeValue()));
						treeNode.getData().setMinOccurrences(replaceNumbersWithLetters(occurrence.getAttributes().getNamedItem("minOccurs").getNodeValue()));
						
						
					}
					catch(Exception e) {//here if is complex type
					
						Node occurrence = elements.item(i).getParentNode();	
			
						treeNode.getData().setMaxOccurrences(replaceNumbersWithLetters(occurrence.getAttributes().getNamedItem("maxOccurs").getNodeValue()));
						treeNode.getData().setMinOccurrences(replaceNumbersWithLetters(occurrence.getAttributes().getNamedItem("minOccurs").getNodeValue()));
						
					}
				}
			}
			catch(Exception e) {}
		}		
		
		
		for(TreeNode<TreeNodeDataType> node : treeNode.getChildren())
			assignOccurrencesToWSDLTree(wsdlDocument, node);
		
	}
	
	

	public static void assignComplexOrSimpleToWSDLTree(byte[] wsdlDocument, TreeNode<TreeNodeDataType> treeNode) throws SAXException, IOException, ParserConfigurationException {
		
	   	//now i search for operation name (=task name)
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(wsdlDocument));
		
		//complextype
		NodeList elements = ((org.w3c.dom.Document) d).getElementsByTagName("xsd:complexType");
		for (int i = 0; i < elements.getLength(); i++) {
			try {
				if(elements.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(treeNode.getData().getType())) {
					treeNode.getData().setComplexOrSimpleType("complexType");
				}
			}
			catch(Exception e) {}
		}
				
		//simpletype
		NodeList elements2 = ((org.w3c.dom.Document) d).getElementsByTagName("xsd:simpleType");
		for (int i = 0; i < elements2.getLength(); i++) {
			try {
				if(elements2.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(treeNode.getData().getType())) {
					treeNode.getData().setComplexOrSimpleType("simpleType");		
				}
			}
			catch(Exception e) {}
		}
		

			//element not declared, search for it in elements
			NodeList elements3 = ((org.w3c.dom.Document) d).getElementsByTagName("xsd:element");
			for (int i = 0; i < elements3.getLength(); i++) {
				try {
					if(elements3.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(treeNode.getData().getContent())) {
						String[] typeSplit = elements3.item(i).getAttributes().getNamedItem("type").getNodeValue().split(":");
						if((typeSplit[0].equals("tns"))||(!typeSplit[0].equals("xsd"))) 
							treeNode.getData().setComplexOrSimpleType("complexType");		
						else
							treeNode.getData().setComplexOrSimpleType("simpleType");		
					}
				}
				catch(Exception e) {}
			}

		
		for(TreeNode<TreeNodeDataType> node : treeNode.getChildren())
			assignComplexOrSimpleToWSDLTree(wsdlDocument, node);
		
	}
	
	
	
	public static TreeNode<TreeNodeDataType> getTypeDefinitionsFromWSDL(byte[] documentWSDL, String typeName, int iterationCalled) throws SAXException, IOException, ParserConfigurationException {

		TreeNode<TreeNodeDataType> rootElem = new TreeNode<TreeNodeDataType>(new TreeNodeDataType(getSTARTING_TYPES_NODE(), getSTARTING_TYPES_NODE()));
		TreeNode<TreeNodeDataType> rootElem2;
		
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(documentWSDL));
		//complextype
		NodeList elementsComplex = ((org.w3c.dom.Document) d).getElementsByTagName("xsd:complexType");
		for (int i = 0; i < elementsComplex.getLength(); i++) {
			if(elementsComplex.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(typeName)) {
				try {
					Node eleminside = elementsComplex.item(i).getFirstChild();
					
					//added
					if(iterationCalled == 0) {
						TreeNode<TreeNodeDataType> temp = new TreeNode<TreeNodeDataType>(new TreeNodeDataType(elementsComplex.item(i).getAttributes().getNamedItem("name").getNodeValue(), typeName));
						temp.getData().setDataType(typeName);
						rootElem.addChild(temp);
						rootElem2 = rootElem;
						rootElem = rootElem.getChildren().get(rootElem.getNumberOfChildren()-1);
					}
					else
						rootElem2 = rootElem;
					
					List<TreeNode<TreeNodeDataType>> listReturn = recursiveWSDLElement(eleminside, documentWSDL);

					for(TreeNode tree : listReturn)
						rootElem.addChild(tree);

					return rootElem2;

				}
				catch(Exception e) {
					//cannot find type
					return null;
				}
			}
		}
		
		//simpletype
		NodeList elementsSimple = ((org.w3c.dom.Document) d).getElementsByTagName("xsd:simpleType");
		for (int i = 0; i < elementsSimple.getLength(); i++) {
			if(elementsSimple.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(typeName)) {
				try {
					Node eleminside = elementsSimple.item(i).getFirstChild();
					
					//added
					if(iterationCalled == 0) {
						rootElem.addChild(new TreeNode<TreeNodeDataType>(new TreeNodeDataType(elementsSimple.item(i).getAttributes().getNamedItem("name").getNodeValue(), typeName)));
						rootElem2 = rootElem;
						rootElem = rootElem.getChildren().get(rootElem.getNumberOfChildren()-1);
					}
					else
						rootElem2 = rootElem;

					List<TreeNode<TreeNodeDataType>> listReturn = recursiveWSDLElement(eleminside, documentWSDL);

					for(TreeNode<TreeNodeDataType> tree : listReturn) {
						rootElem.addChild(tree);
					}

					return rootElem2;

				}
				catch(Exception e) {
					//cannot find type
					return null;
				}
			}
		}

		if(rootElem.getNumberOfChildren() == 0) {//is an ending-type

			NodeList elements = ((org.w3c.dom.Document) d).getElementsByTagName("xsd:element");
			for(int i=0; i<elements.getLength(); i++) {
				if(elements.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(typeName)) {
					String[] typeSplit = elements.item(i).getAttributes().getNamedItem("type").getNodeValue().split(":");
					return new TreeNode<TreeNodeDataType>(new TreeNodeDataType(typeSplit[typeSplit.length-1], elements.item(i).getAttributes().getNamedItem("name").getNodeValue()));

				}
			}



		}

		return rootElem;
	}

	public static List<TreeNode<TreeNodeDataType>> recursiveWSDLElement(Node nodeInside, byte[] documentWSDL) {

		List<TreeNode<TreeNodeDataType>> listTree = new ArrayList<TreeNode<TreeNodeDataType>>();
		Node eleminside = nodeInside;

		if(eleminside != null) {
			//for complextypes
			if(eleminside.getNodeName().equals("xsd:element")) {
				String[] typeSplit = eleminside.getAttributes().getNamedItem("type").getNodeValue().split(":");

				listTree.add(new TreeNode<TreeNodeDataType>(new TreeNodeDataType(typeSplit[typeSplit.length-1], eleminside.getAttributes().getNamedItem("name").getNodeValue())));

				
				//!typeSplit[0].equals("xsd") because the tns may be omitted
				if((typeSplit[0].equals("tns"))||(!typeSplit[0].equals("xsd"))) {//recursively visit all other nodes (double ricorsion :()
					try {
						listTree.get(listTree.size()-1).addChild(getTypeDefinitionsFromWSDL(documentWSDL, typeSplit[typeSplit.length-1], 1));
					} catch (SAXException | IOException | ParserConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			//for simpletypes
			if(eleminside.getNodeName().equals("xsd:restriction")) {
				String[] typeSplit = eleminside.getAttributes().getNamedItem("base").getNodeValue().split(":");

				//FIXED last line of the method with nodeFather
				Node nodeFather = eleminside.getParentNode();
				
				TreeNode<TreeNodeDataType> toAdd = new TreeNode<TreeNodeDataType>(new TreeNodeDataType(typeSplit[typeSplit.length-1], nodeFather.getAttributes().getNamedItem("name").getNodeValue()));
				toAdd.getData().setRestriction(true);
				listTree.add(toAdd);

				if((typeSplit[0].equals("tns"))||(!typeSplit[0].equals("xsd"))) {//recursively visit all other nodes (double ricorsion :()
					try {
						listTree.get(listTree.size()-1).addChild(getTypeDefinitionsFromWSDL(documentWSDL, typeSplit[typeSplit.length-1], 1));
					} catch (SAXException | IOException | ParserConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			if(eleminside.getFirstChild() != null) {
				List<TreeNode<TreeNodeDataType>> listTreeChild = recursiveWSDLElement(eleminside.getFirstChild(), documentWSDL);
				for(TreeNode tree : listTreeChild) {
					listTree.add(tree);  
				}

			}

			if(eleminside.getNextSibling() != null) {
				List<TreeNode<TreeNodeDataType>> listTreeChild = recursiveWSDLElement(eleminside.getNextSibling(), documentWSDL);
				for(TreeNode tree : listTreeChild) {
					listTree.add(tree);  
				}

			}

		}

		return listTree;
	}


	
	
	
	
	
	
	

	public static List<TreeNode<TreeNodeDataType>> getCompleteTypeDefinitionsFromTypesXSD(byte[] typesXSD, String typeName) throws SAXException, IOException, ParserConfigurationException, CoreException {

		TreeNode<TreeNodeDataType> rootElem = getTypeDefinitionsFromTypesXSD(typesXSD, typeName);
		
		//now i have to clean the tree, delete all nodes with getStarting_types_node

		rootElem = cleanTree(rootElem);
		
		//the first is getstarting..
		
		List<TreeNode<TreeNodeDataType>> listToReturn = new ArrayList<TreeNode<TreeNodeDataType>>();
		for(TreeNode<TreeNodeDataType> node : rootElem.getChildren()) {
			listToReturn.add(node);
		}
		
    	TreeNode<TreeNodeDataType> thisinput = new TreeNode<TreeNodeDataType>();
		for(TreeNode<TreeNodeDataType> trr : listToReturn) {
			thisinput.addChild(trr);
		}
		
		assignOccurrencesToXSDTree(typesXSD, thisinput);
		assignComplexOrSimpleToXSDTree(typesXSD, thisinput);
		refineComplexOrSimpleTypes(thisinput);
		fixRestrictionsToTree(thisinput);

		
		return listToReturn;
	}
	
	

	public static void assignOccurrencesToXSDTree(byte[] xsdDocument, TreeNode<TreeNodeDataType> treeNode) throws SAXException, IOException, ParserConfigurationException {
		
	   	//now i search for operation name (=task name)
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xsdDocument));

		//element
		NodeList elements = ((org.w3c.dom.Document) d).getElementsByTagName("xsd:element");
		for (int i = 0; i < elements.getLength(); i++) {
			try {
				if(elements.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(treeNode.getData().getContent())) {					
					try {//for simple types its needed
						Node occurrence = elements.item(i);	

						treeNode.getData().setMaxOccurrences(replaceNumbersWithLetters(occurrence.getAttributes().getNamedItem("maxOccurs").getNodeValue()));
						treeNode.getData().setMinOccurrences(replaceNumbersWithLetters(occurrence.getAttributes().getNamedItem("minOccurs").getNodeValue()));

					}
					catch(Exception e) {//here if is complex type
					
						Node occurrence = elements.item(i).getParentNode();	
			
						treeNode.getData().setMaxOccurrences(replaceNumbersWithLetters(occurrence.getAttributes().getNamedItem("maxOccurs").getNodeValue()));
						treeNode.getData().setMinOccurrences(replaceNumbersWithLetters(occurrence.getAttributes().getNamedItem("minOccurs").getNodeValue()));

					}
				}
			}
			catch(Exception e) {}
		}		
		
		
		for(TreeNode<TreeNodeDataType> node : treeNode.getChildren())
			assignOccurrencesToXSDTree(xsdDocument, node);
		
	}
	
	

	public static void assignComplexOrSimpleToXSDTree(byte[] xsdDocument, TreeNode<TreeNodeDataType> treeNode) throws SAXException, IOException, ParserConfigurationException {
		
	   	//now i search for operation name (=task name)
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xsdDocument));
		
		//complextype
		NodeList elements = ((org.w3c.dom.Document) d).getElementsByTagName("xsd:complexType");
		for (int i = 0; i < elements.getLength(); i++) {
			try {
				if(elements.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(treeNode.getData().getType())) {
					treeNode.getData().setComplexOrSimpleType("complexType");
				}
			}
			catch(Exception e) {}
		}
				
		//simpletype
		NodeList elements2 = ((org.w3c.dom.Document) d).getElementsByTagName("xsd:simpleType");
		for (int i = 0; i < elements2.getLength(); i++) {
			try {
				if(elements2.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(treeNode.getData().getType())) {
					treeNode.getData().setComplexOrSimpleType("simpleType");		
				}
			}
			catch(Exception e) {}
		}
		

			//element not declared, search for it in elements
			NodeList elements3 = ((org.w3c.dom.Document) d).getElementsByTagName("xsd:element");
			for (int i = 0; i < elements3.getLength(); i++) {
				try {
					if(elements3.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(treeNode.getData().getContent())) {
						String[] typeSplit = elements3.item(i).getAttributes().getNamedItem("type").getNodeValue().split(":");
						if((typeSplit[0].equals("tns"))||(!typeSplit[0].equals("xsd"))) 
							treeNode.getData().setComplexOrSimpleType("complexType");		
						else
							treeNode.getData().setComplexOrSimpleType("simpleType");		
					}
				}
				catch(Exception e) {}
			}

		
		for(TreeNode<TreeNodeDataType> node : treeNode.getChildren())
			assignComplexOrSimpleToXSDTree(xsdDocument, node);
		
	}
	
	
	
	public static TreeNode<TreeNodeDataType> cleanTree(TreeNode<TreeNodeDataType> treeNode) {
		
		if(treeNode == null)
			treeNode = new TreeNode<TreeNodeDataType>(new TreeNodeDataType(getSTARTING_TYPES_NODE(), getSTARTING_TYPES_NODE()));
		
		for(int i=0; i<treeNode.getNumberOfChildren(); i++) {
			TreeNode<TreeNodeDataType> tempNode = treeNode.getChildren().get(i);
			treeNode.removeChildAt(i);
			treeNode.insertChildAt(i, cleanTree(tempNode));
		}	
	
		
		
		if(treeNode.getNumberOfChildren() == 1) {//maybe is a fake children
			TreeNode<TreeNodeDataType> tempNode = treeNode.getChildren().get(0);
			if(tempNode.getData().getType().equals(getSTARTING_TYPES_NODE())) {
				treeNode.removeChildAt(0);
				for(int i=0; i<tempNode.getNumberOfChildren(); i++) {
					treeNode.addChild(tempNode.getChildren().get(i));
				}
			}
		}

		

			
		
		return treeNode;
	}
	
	
	public static TreeNode<TreeNodeDataType> getTypeDefinitionsFromTypesXSD(byte[] typesXSD, String typeName) throws SAXException, IOException, ParserConfigurationException, CoreException {

		TreeNode<TreeNodeDataType> rootElem = new TreeNode<TreeNodeDataType>(new TreeNodeDataType(getSTARTING_TYPES_NODE(), getSTARTING_TYPES_NODE()));
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(typesXSD));
		
		//complextype
		NodeList elementsComplex = ((org.w3c.dom.Document) d).getElementsByTagName("xsd:complexType");
		for (int i = 0; i < elementsComplex.getLength(); i++) {
			if(elementsComplex.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(typeName)) {
				try {

					Node eleminside = elementsComplex.item(i).getFirstChild();
					List<TreeNode<TreeNodeDataType>> listReturn = recursiveTypesElement(eleminside, typesXSD);

					rootElem.getData().setDataType(typeName);
					
					for(TreeNode tree : listReturn)
						rootElem.addChild(tree);

					return rootElem;

				}
				catch(Exception e) {
					//cannot find type
					return null;
				}
			}
		}

		//simpletype
		NodeList elementsSimple = ((org.w3c.dom.Document) d).getElementsByTagName("xsd:simpleType");
		for (int i = 0; i < elementsSimple.getLength(); i++) {
			if(elementsSimple.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(typeName)) {
				try {

					Node eleminside = elementsSimple.item(i).getFirstChild();
					List<TreeNode<TreeNodeDataType>> listReturn = recursiveTypesElement(eleminside, typesXSD);

					for(TreeNode tree : listReturn)
						rootElem.addChild(tree);

					return rootElem;

				}
				catch(Exception e) {
					//cannot find type
					return null;
				}
			}
		}
		
		if(rootElem.getNumberOfChildren() == 0) {//is an ending-type

			NodeList elements = ((org.w3c.dom.Document) d).getElementsByTagName("xsd:element");
			for(int i=0; i<elements.getLength(); i++) {
				if(elements.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(typeName)) {
					String[] typeSplit = elements.item(i).getAttributes().getNamedItem("type").getNodeValue().split(":");
					return new TreeNode<TreeNodeDataType>(new TreeNodeDataType(typeSplit[typeSplit.length-1], elements.item(i).getAttributes().getNamedItem("name").getNodeValue()));

				}
			}



		}

		return rootElem;
	}


	public static List<TreeNode<TreeNodeDataType>> recursiveTypesElement(Node nodeInside, byte[] typesXSD) {

		List<TreeNode<TreeNodeDataType>> listTree = new ArrayList<TreeNode<TreeNodeDataType>>();
		Node eleminside = nodeInside;

		if(eleminside != null) {

			//for elements and complex types
			if(eleminside.getNodeName().equals("xsd:element")) {
				String[] typeSplit = eleminside.getAttributes().getNamedItem("type").getNodeValue().split(":");

				listTree.add(new TreeNode<TreeNodeDataType>(new TreeNodeDataType(typeSplit[typeSplit.length-1], eleminside.getAttributes().getNamedItem("name").getNodeValue())));

				if(typeSplit[0].equals("tns")) {//recursively visit all other nodes (double ricorsion :()
					try {
						listTree.get(listTree.size()-1).addChild(getTypeDefinitionsFromTypesXSD(typesXSD, typeSplit[typeSplit.length-1]));
					} catch (SAXException | IOException | ParserConfigurationException | CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			//for simpletypes
			if(eleminside.getNodeName().equals("xsd:restriction")) {
				String[] typeSplit = eleminside.getAttributes().getNamedItem("base").getNodeValue().split(":");

				Node parentNode = eleminside.getParentNode();
				TreeNode<TreeNodeDataType> toAdd = new TreeNode<TreeNodeDataType>(new TreeNodeDataType(typeSplit[typeSplit.length-1], parentNode.getAttributes().getNamedItem("name").getNodeValue()));
				toAdd.getData().setRestriction(true);
				listTree.add(toAdd);

				if(typeSplit[0].equals("tns")) {//recursively visit all other nodes (double ricorsion :()
					try {
						listTree.get(listTree.size()-1).addChild(getTypeDefinitionsFromTypesXSD(typesXSD, typeSplit[typeSplit.length-1]));
					} catch (SAXException | IOException | ParserConfigurationException | CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			if(eleminside.getFirstChild() != null) {
				List<TreeNode<TreeNodeDataType>> listTreeChild = recursiveTypesElement(eleminside.getFirstChild(), typesXSD);
				for(TreeNode tree : listTreeChild) {
					listTree.add(tree);  
				}

			}

			if(eleminside.getNextSibling() != null) {
				List<TreeNode<TreeNodeDataType>> listTreeChild = recursiveTypesElement(eleminside.getNextSibling(), typesXSD);
				for(TreeNode tree : listTreeChild) {
					listTree.add(tree);  
				}

			}

		}

		return listTree;
	}

	

	public static void unmapTree(TreeNode<TreeNodeDataType> treeNode) {
		
		//set the tree unmapped from treeNode and above
		
		treeNode.getData().setIsMappedWithTreeNode(null);
		
		for(TreeNode<TreeNodeDataType> node : treeNode.getChildren())
			unmapTree(node);
		
		
	}
	
	
	public static boolean sonOfType(TreeNode<TreeNodeDataType> treeNode, String type, TreeNode<TreeNodeDataType> wholeTree, boolean found) {
				
		boolean canBeTrue = false;
	
		//is the same object
		if(wholeTree.equals(treeNode))
			if(found==true)
				return true;
		
		if(wholeTree.getData().getType().equals(type))
			found = true;

		for(TreeNode<TreeNodeDataType> node : wholeTree.getChildren())
			if(sonOfType(treeNode, type, node, found) == true)
				return true;


		return canBeTrue;
	}
	
	
	public static List<String> flatTree(TreeNode<TreeNodeDataType> treeNode, int level) {
		List<String> returnList = new ArrayList<String>();
		
		String levelCounter = "";
		for(int i=0; i<level; i++)
			levelCounter = levelCounter+"-";
		
		levelCounter = levelCounter + ">";
		
		try {//can be empty or not valid!
			if((!treeNode.getData().getContent().equals(INPUT_FAKE_TYPE)) && (!treeNode.getData().getContent().equals(OUTPUT_FAKE_TYPE)))
				returnList.add(levelCounter + treeNode.getData().getContent()+":"+treeNode.getData().getType());
		}
		catch(Exception e) {
			//do nothing
		}
		
		for(TreeNode<TreeNodeDataType> node : treeNode.getChildren())
			returnList.addAll(flatTree(node, level+1));


		return returnList;
	}
	
	
	public static List<String> getFlattenedTreeFromTypeGIDL(byte[] documentGidl, String typeToSearch) {
	   
		try {
	    	TreeNode<TreeNodeDataType> thisinput = Utilities.getCompleteTypeDefinitionsFromGIDL(documentGidl, typeToSearch, typeToSearch);

			List<String> listFlattened = Utilities.flatTree(thisinput, 0);
			return listFlattened;
			
	    } catch (SAXException | IOException | ParserConfigurationException | CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return null;
	}
	
	
	public static List<String> getFlattenedTreeFromTypeWSDL(byte[] documentWSDL, String typeToSearch) {
		   
		try {
	    	TreeNode<TreeNodeDataType> thisinput = Utilities.getCompleteTypeDefinitionsFromWSDL(documentWSDL, typeToSearch);


			List<String> listFlattened = Utilities.flatTree(thisinput, 0);
			return listFlattened;
			
	    } catch (SAXException | IOException | ParserConfigurationException | CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return null;
	}
	
	
	public static String getWSDLPlaceHolder() {
		return WSDLPlaceHolder;
	}


	public static TreeNode<TreeNodeDataType> createMappingTreeFromMappedTree(TreeNode<TreeNodeDataType> mappedTree, TreeNode<TreeNodeDataType> newTree) {
	
		
		for(int i=0; i<mappedTree.getNumberOfChildren(); i++) {
			try {
				if(mappedTree.getChildren().get(i).getData().getIsMappedWithTreeNode().isEmpty())  {
					//mappedTree.removeChildAt(i);
				}
				else {
					for(int j=0; j<mappedTree.getChildren().get(i).getData().getIsMappedWithTreeNode().size(); j++) {
						newTree.addChild(new TreeNode<TreeNodeDataType>(new TreeNodeDataType(mappedTree.getChildren().get(i).getData().getType(), mappedTree.getChildren().get(i).getData().getIsMappedWithTreeNode().get(j).getData().getContent())));
						createMappingTreeFromMappedTree(mappedTree.getChildren().get(i), newTree.getChildren().get(newTree.getNumberOfChildren()-1));
					}
				}
			}
			catch(Exception e) {
					
			}
		}


		return newTree;
			
	}
	
	
	
	public static TreeNode<TreeNodeDataType> createCompleteMappingTreeFromMappedTree(TreeNode<TreeNodeDataType> mappedTree) {
						
		TreeNode<TreeNodeDataType> newTree = new TreeNode<TreeNodeDataType>(new TreeNodeDataType(Utilities.getSTARTING_TYPES_NODE(), Utilities.getSTARTING_TYPES_NODE()));
		
		TreeNode<TreeNodeDataType> treeReturn = createMappingTreeFromMappedTree(mappedTree, newTree);
		treeReturn = cleanTree(treeReturn);
		
		return treeReturn;
			
	}
	
	
	public static List<TreeNode<TreeNodeDataType>> getElementsFromTree(TreeNode<TreeNodeDataType> treeNode, String type) {
		List<TreeNode<TreeNodeDataType>> listToReturn = new ArrayList<TreeNode<TreeNodeDataType>>();

		try {
			if(treeNode.getData().getType().equals(type)) {
				listToReturn.add(treeNode);
			}
		}
		catch(Exception e) {}
		try {
			for(int i = 0; i<treeNode.getNumberOfChildren(); i++) {
				listToReturn.addAll(getElementsFromTree(treeNode.getChildren().get(i), type));
			}
		}
		catch(Exception e) {}
			
		return listToReturn;
		
	}
	
	
	public static byte[] getAdapterContent(final AdapterModel adapterModel) throws AdapterGeneratorException, SAXException, IOException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {

		File adapterFile;
		try {
			adapterFile = File.createTempFile(TEMPFILE_SUFFIX, ADAPTER_FILE_EXTENSION);
		} catch (IOException e) {
			throw new AdapterGeneratorException("Internal Error while creating the Adapter Model");
		}

		// create resource from the Model
		URI fileUriTempModelNormalized = URI.createFileURI(adapterFile.getAbsolutePath());
		Resource resourceModelNormalized = new XMLResourceFactoryImpl().createResource(fileUriTempModelNormalized);
		// add model in model resourceModel
		resourceModelNormalized.getContents().add(adapterModel);

		byte[] adapterContent;

		try {	
			resourceModelNormalized.save(Collections.EMPTY_MAP);
			adapterContent = FileUtils.readFileToByteArray(adapterFile);

		} catch (IOException e) {
			throw new AdapterGeneratorException("Internal Error while reading the Adapter Model");
		} finally {
			FileDeleteStrategy.FORCE.deleteQuietly(adapterFile);
		}
		return adapterContent;

	}
	
	public static AdapterModel generateAdapterModels(List<AdapterModelDataType> adapterModelsToBeCreated) throws IOException, AdapterGeneratorException, SAXException, ParserConfigurationException, CoreException {
		AdapterModel adapterToReturn = eu.chorevolution.modelingnotations.adapter.impl.AdapterFactoryImpl.eINSTANCE.createAdapterModel();
		TreeNode<TreeNodeDataType> taskTree;
		TreeNode<TreeNodeDataType> treeFromWSDLOrGIDL;
		byte[] wsdlOrGidlFile;
		
		//avoid duplicates
		List<String> createdOperations = new ArrayList<String>();
		String currentTaskName = null;
		
		eu.chorevolution.modelingnotations.adapter.ChoreographyTask currentTaskItem = null;
		eu.chorevolution.modelingnotations.adapter.Operation currentOperationItem = null;
		
		//same participant-role and maybe different tasks in the same model
		for(AdapterModelDataType adapterModelToBeCreated : adapterModelsToBeCreated) {
			
			//go through the task tree
			taskTree = adapterModelToBeCreated.getTreeFromTask();
			treeFromWSDLOrGIDL = adapterModelToBeCreated.getTreeFromWSDLOrGIDL();
			wsdlOrGidlFile = adapterModelToBeCreated.getWsdlOrGidlFile();
			
			List<TreeNode<TreeNodeDataType>> taskNodes = Utilities.getElementsFromTree(taskTree, Utilities.getTASK_TYPE());
			for(TreeNode<TreeNodeDataType> taskNode : taskNodes) {
				
				currentTaskName = ChorevolutionCoreUtils.removeBlankSpaces(taskNode.getData().getContent());
				
				eu.chorevolution.modelingnotations.adapter.ChoreographyTask task = eu.chorevolution.modelingnotations.adapter.impl.AdapterFactoryImpl.eINSTANCE.createChoreographyTask();
				task.setName(ChorevolutionCoreUtils.removeBlankSpaces(taskNode.getData().getContent()));
				adapterToReturn.getHasChoreographyTasks().add(task);

				currentTaskItem = task;
				
				//for each task i have at max 2 messages
				for(int j=0; j<2; j++) {
					List<TreeNode<TreeNodeDataType>> taskMessages;
					
					if(j==0)
						taskMessages = Utilities.getElementsFromTree(taskTree, Utilities.getINITIATING_MESSAGE_TYPE());
					else
						taskMessages = Utilities.getElementsFromTree(taskTree, Utilities.getNON_INITIATING_MESSAGE_TYPE());
				
					if(!taskMessages.isEmpty()) {
						ChoreographyMessage message = eu.chorevolution.modelingnotations.adapter.impl.AdapterFactoryImpl.eINSTANCE.createChoreographyMessage();
						message.setName(taskMessages.get(0).getData().getContent());
						
						if(j==0)
							message.setType(ChoreographyMessageType.INITIATING);
						else
							message.setType(ChoreographyMessageType.RETURN);

						
						task.getHasChoreographyMessages().add(message);

						//TODO do it better!
						//the first type is not in the treenode so i add it "manually"
						taskMessages.get(0).getChildren().get(0).getData().setComplexOrSimpleType("complexType");
						message.setHasChoreographyDataItem(getChoreographyDataItemTreeFromTree(taskMessages.get(0).getChildren().get(0)));
						taskMessages.get(0).getChildren().get(0).getData().setIsMappedWithChoreographyDataItem(message.getHasChoreographyDataItem());
						

					}
				}

				//just one task name per mapping editor
				break;
			}
			
			
			//go through the WSDL / GIDL
			
			List<String> opNames = null;
			byte[] documentWG = adapterModelToBeCreated.getWsdlOrGidlFile();
			if(Utilities.isGIDL(documentWG))
				opNames = Utilities.getTaskNamesFromGIDL(adapterModelToBeCreated.getWsdlOrGidlFile());
			else
				opNames = Utilities.getTaskNamesFromWSDL(adapterModelToBeCreated.getWsdlOrGidlFile());
		
			
			for(String opName : opNames) {
			
				//avoid duplicates
				boolean added = false;
				for(String createdOperation : createdOperations) {
					if(createdOperation.equals(opName)) {
						added = true;
						break;
					}
				}

				
				createdOperations.add(opName);
				
				eu.chorevolution.modelingnotations.adapter.Operation operation = eu.chorevolution.modelingnotations.adapter.impl.AdapterFactoryImpl.eINSTANCE.createOperation();
				operation.setName(ChorevolutionCoreUtils.removeBlankSpaces(opName));
				adapterToReturn.getHasOperations().add(operation);	

				currentOperationItem = operation;
				
				//for each operation i have at max 2 messages
				for(int j=0; j<2; j++) {
					List<String> mappingMessages;
					
					if(j==0) {
						if(Utilities.isGIDL(documentWG))
							mappingMessages = Utilities.getMessageNamesFromTaskGIDL(documentWG, opName, Utilities.getONLY_INITIATING());
						else
							mappingMessages = Utilities.getMessageNamesFromTaskWSDL(documentWG, opName, Utilities.getONLY_INITIATING());

					}
					else {
						if(Utilities.isGIDL(documentWG))
							mappingMessages = Utilities.getMessageNamesFromTaskGIDL(documentWG, opName, Utilities.getONLY_RECEIVING());
						else
							mappingMessages = Utilities.getMessageNamesFromTaskWSDL(documentWG, opName, Utilities.getONLY_RECEIVING());
					}

				
					if(!mappingMessages.isEmpty()) {
						Message message = eu.chorevolution.modelingnotations.adapter.impl.AdapterFactoryImpl.eINSTANCE.createMessage();
						//message.setName(mappingMessages.get(0));
						//Fixed the name in the adapter generation
						if(j==0)
							message.setName(mappingMessages.get(0));
						else
							message.setName(mappingMessages.get(0));
						
						if(j==0)
							message.setType(MessageType.INPUT);
						else
							message.setType(MessageType.OUTPUT);
						
						operation.getHasMessages().add(message);
						
						TreeNode<TreeNodeDataType> secondTree;
						
						if(j==0)
							secondTree = Utilities.getElementsFromTree(treeFromWSDLOrGIDL, Utilities.getINITIATING_MESSAGE_TYPE()).get(0).getChildren().get(0);
						else
							secondTree = Utilities.getElementsFromTree(treeFromWSDLOrGIDL, Utilities.getNON_INITIATING_MESSAGE_TYPE()).get(0).getChildren().get(0);
						
						//now i change the first type (hasMessageDataItem) "request" and "response" name into the new names chosen
						//by Patient, took from hasOperation name
						
						String falseTypeName = opName;
						if(j == 0) {//own logic
							//
						}
						else {
							falseTypeName = falseTypeName+"Response";
						}
							
						message.setHasMessageDataItem(getDataItemTreeFromTree(secondTree, 0));						
						message.getHasMessageDataItem().setName(falseTypeName);
						message.getHasMessageDataItem().setMaxOccurs(OccurencesType.getByName(secondTree.getChildren().get(0).getData().getMaxOccurrences()));
						message.getHasMessageDataItem().setMinOccurs(OccurencesType.getByName(secondTree.getChildren().get(0).getData().getMinOccurrences()));
						secondTree.getData().setIsMappedWithDataItem(message.getHasMessageDataItem());
							
					}
				}
			}
			
			//third step
			
			//go through the other tree (mapping)
			TreeNode<TreeNodeDataType> mappingTree = taskTree;//and use the .ismappedwith()
			OperationRelation opRelation = eu.chorevolution.modelingnotations.adapter.impl.AdapterFactoryImpl.eINSTANCE.createOperationRelation();
			adapterToReturn.getHasOperationsRelations().add(opRelation);
			
			List<TreeNode<TreeNodeDataType>> mappingNodes = Utilities.getElementsFromTree(mappingTree, Utilities.getTASK_TYPE());
			for(TreeNode<TreeNodeDataType> mappingNode : mappingNodes) {
			
				eu.chorevolution.modelingnotations.adapter.ChoreographyTask currentTask = currentTaskItem;
				eu.chorevolution.modelingnotations.adapter.Operation currentOperation = currentOperationItem;
				
				for(int j=0; j<2; j++) {
					//at maximum 2 messages
					
					List<TreeNode<TreeNodeDataType>> mappingMessages = null;
					ChoreographyMessage currentChorMessage = null;
					Message currentMessage = null;
					
					if(j==0)
						mappingMessages = Utilities.getElementsFromTree(mappingTree, Utilities.getINITIATING_MESSAGE_TYPE());
					else
						mappingMessages = Utilities.getElementsFromTree(mappingTree, Utilities.getNON_INITIATING_MESSAGE_TYPE());
				
					if(!mappingMessages.isEmpty()) {

						for(ChoreographyMessage tempMsg : currentTask.getHasChoreographyMessages()) {
							if(j==0) {
								if(tempMsg.getType().equals(ChoreographyMessageType.INITIATING)) {
									currentChorMessage = tempMsg;
									break;
								}
							}
							else {
								if(tempMsg.getType().equals(ChoreographyMessageType.RETURN)) {
									currentChorMessage = tempMsg;
									break;
								}
							}
						}

						for(Message tempMsg : currentOperation.getHasMessages()) {
							if(j==0) {
								if(tempMsg.getType().equals(MessageType.INPUT)) {
									currentMessage = tempMsg;
									break;
								}
							}
							else {
								if(tempMsg.getType().equals(MessageType.OUTPUT)) {
									currentMessage = tempMsg;
									break;
								}
							}
						}
						
						MessageRelation msgRelation = eu.chorevolution.modelingnotations.adapter.impl.AdapterFactoryImpl.eINSTANCE.createMessageRelation();
						opRelation.getHasMessagesRelations().add(msgRelation);
						
						msgRelation.setChoreographyMessage(currentChorMessage);
						msgRelation.setMessage(currentMessage);
						
						//forces the relation change because of the inputDataType and outputDataType
						
						
						if(Utilities.isGIDL(wsdlOrGidlFile)) {
							if(j==0) {
								mappingMessages.get(0).getChildren().get(0).getData().getIsMappedWithTreeNode().get(0).getData().setIsMappedWithDataItem(((ComplexItem)currentMessage.getHasMessageDataItem()).getHasDataItems().get(0));
								mappingMessages.get(0).getChildren().get(0).getData().setIsMappedWithChoreographyDataItem(currentChorMessage.getHasChoreographyDataItem());
							}
							else {
								
								  //if outputdatatype is shown to the user, don't force auto-map, else force the automap
		    	    			  try {
	    	    					  TreeNode<TreeNodeDataType> nodeOnLeft = Utilities.getElementsFromTree(mappingTree, Utilities.getNON_INITIATING_TYPE_TYPE()).get(0).getChildren().get(0);
	
		    	    				  if(Utilities.isAComplexTypeOnTopOfTree(Utilities.getNON_INITIATING_TYPE_TYPE(), treeFromWSDLOrGIDL, "right") == true
		    	    						  && nodeOnLeft.getData().getComplexOrSimpleType().equals("complexType") && nodeOnLeft.getData().getMaxOccurrences().equals("unbounded")) {
		    	    					  //don't skip the node, so it won't be auto-mapped with the outputDataType element
		    								mappingMessages.get(0).getChildren().get(0).getData().getIsMappedWithTreeNode().get(0).getData().setIsMappedWithDataItem(currentMessage.getHasMessageDataItem());
		    								mappingMessages.get(0).getChildren().get(0).getData().setIsMappedWithChoreographyDataItem(currentChorMessage.getHasChoreographyDataItem());
		    	    				  }
		    	    				  else {
		    	    					  //skips the node, so map it with the outputDataType element
		    	    					  mappingMessages.get(0).getChildren().get(0).getData().getIsMappedWithTreeNode().get(0).getData().setIsMappedWithDataItem(((ComplexItem)currentMessage.getHasMessageDataItem()).getHasDataItems().get(0));
		    	    					  mappingMessages.get(0).getChildren().get(0).getData().setIsMappedWithChoreographyDataItem(currentChorMessage.getHasChoreographyDataItem());
		    	    				  } 
	    	    	    		  }
		    	    		  	  catch(Exception e) {
		    	    		  		  e.printStackTrace();
		    	    		  	  }	
							}
						}
						else {
							mappingMessages.get(0).getChildren().get(0).getData().getIsMappedWithTreeNode().get(0).getData().setIsMappedWithDataItem(currentMessage.getHasMessageDataItem());
							mappingMessages.get(0).getChildren().get(0).getData().setIsMappedWithChoreographyDataItem(currentChorMessage.getHasChoreographyDataItem());
						}
						
						//msgRelation.getHasDataItemsRelations().addAll(getDataItemRelationTreeFromTree(mappingMessages.get(0).getChildren().get(0), currentChorMessage, currentMessage));
						
						//i do the merge for the pieces items with the same dataitem
						EList<DataItemRelation> dataItemRelations = new BasicEList<DataItemRelation>();
						dataItemRelations.addAll(getDataItemRelationTreeFromTree(mappingMessages.get(0).getChildren().get(0), currentChorMessage, currentMessage));

						
						//this code clusterizes duplicates in adapter, only for input message
						if(j==0) {
							int itemLen = dataItemRelations.size();
							for(int i = 0; i < itemLen; i++) {
								for(int z = i+1; z < itemLen; z++) {
									try {
										if(dataItemRelations.get(i).getDataItem().get(0).equals(dataItemRelations.get(z).getDataItem().get(0))) {
											//they are equals, merge them
											dataItemRelations.get(i).getChoreographyDataItem().addAll(dataItemRelations.get(z).getChoreographyDataItem());
											dataItemRelations.remove(z);
											z-=1;//because the "remove" shifts the indexes to the left
											itemLen = dataItemRelations.size();
										}
									}
									catch(Exception e) {
										//dataItem can be both null
									}
								}
							}
						}
						
						//now i should add the elements not mapped in the WSDLorGIDL tree but that has a transformation rule (staticvalue)
						List<TreeNode<TreeNodeDataType>> singleNodes = Utilities.listOfTreeNodeNotMappedButWithTransformationRule(taskTree, treeFromWSDLOrGIDL);
						for(TreeNode<TreeNodeDataType> node : singleNodes) {
							
							DataItemRelation dataItemRelation = eu.chorevolution.modelingnotations.adapter.impl.AdapterFactoryImpl.eINSTANCE.createDataItemRelation();
							
							DataItem serviceItem = Utilities.getDataItemFromItem(currentMessage.getHasMessageDataItem(), node);

							if(serviceItem != null) {
								dataItemRelation.getDataItem().add(serviceItem);
								dataItemRelation.setTransformationRule(node.getData().getTransformationRule());

								dataItemRelations.add(dataItemRelation);
							}
							
						}
						
						//now i should add the elements not mapped in the WSDLorGIDL tree but that has security
						List<TreeNode<TreeNodeDataType>> securityNodes = Utilities.listOfTreeNodeNotMappedButWithSecurity(taskTree, treeFromWSDLOrGIDL);
						for(TreeNode<TreeNodeDataType> node : securityNodes) {
							
							DataItemRelation dataItemRelation = eu.chorevolution.modelingnotations.adapter.impl.AdapterFactoryImpl.eINSTANCE.createDataItemRelation();
							
							DataItem serviceItem = Utilities.getDataItemFromItem(currentMessage.getHasMessageDataItem(), node);

							if(serviceItem != null) {
								dataItemRelation.getDataItem().add(serviceItem);
								dataItemRelation.setTransformationRule("\"\"");

								dataItemRelations.add(dataItemRelation);
							}
							
						}
						
						//now i should parse all the transformation rules in order to double backslash the + inside the quotes
						for(DataItemRelation rel : dataItemRelations) {
							try {
								if(rel.getTransformationRule() != null) {
									rel.setTransformationRule(parseTransformationRule(rel.getTransformationRule()));
								}
							}
							catch(Exception e) {
								
							}
						}
						
						msgRelation.getHasDataItemsRelations().addAll(dataItemRelations);
						
					}
					
				}
				
				//i should have only one of these operations mapped here
				break;
			}
		
		
		}
		
		
		return adapterToReturn;
	}
	


	public static List<TreeNode<TreeNodeDataType>> listOfTreeNodeNotMappedButWithTransformationRule(TreeNode<TreeNodeDataType> firstFullTree, TreeNode<TreeNodeDataType> treeNode) {
		List<TreeNode<TreeNodeDataType>> toReturn = new ArrayList<TreeNode<TreeNodeDataType>>();
		
		if(treeNode.getData().getTransformationRule() != null && !Utilities.isTreeNodeMappedWithItemOfTree(treeNode, firstFullTree))
			toReturn.add(treeNode);
		
		for(TreeNode<TreeNodeDataType> node : treeNode.getChildren())
			toReturn.addAll(listOfTreeNodeNotMappedButWithTransformationRule(firstFullTree, node));
		
		
		return toReturn;
	}
	
	public static List<TreeNode<TreeNodeDataType>> listOfTreeNodeNotMappedButWithSecurity(TreeNode<TreeNodeDataType> firstFullTree, TreeNode<TreeNodeDataType> treeNode) {
		List<TreeNode<TreeNodeDataType>> toReturn = new ArrayList<TreeNode<TreeNodeDataType>>();
		
		if(treeNode.getData().isSecurity() && !Utilities.isTreeNodeMappedWithItemOfTree(treeNode, firstFullTree))
			toReturn.add(treeNode);
		
		for(TreeNode<TreeNodeDataType> node : treeNode.getChildren())
			toReturn.addAll(listOfTreeNodeNotMappedButWithSecurity(firstFullTree, node));
		
		
		return toReturn;
	}
	
	
	public static ChoreographyDataItem getChoreographyDataItemTreeFromTree(TreeNode<TreeNodeDataType> treeNode) {
		ChoreographyDataItem toReturn = eu.chorevolution.modelingnotations.adapter.impl.AdapterFactoryImpl.eINSTANCE.createChoreographyComplexItem();
		
		try {
			if(treeNode.getData().getComplexOrSimpleType().equals("complexType"))
				toReturn = eu.chorevolution.modelingnotations.adapter.impl.AdapterFactoryImpl.eINSTANCE.createChoreographyComplexItem();
			else {
				toReturn = eu.chorevolution.modelingnotations.adapter.impl.AdapterFactoryImpl.eINSTANCE.createChoreographySimpleItem();
				((ChoreographySimpleItem)toReturn).setType(getAssociatedSimpleType(treeNode.getData().getType()));
			}
				
			toReturn.setName(treeNode.getData().getContent());
			toReturn.setMaxOccurs(eu.chorevolution.modelingnotations.adapter.OccurencesType.getByName(treeNode.getData().getMaxOccurrences()));
			toReturn.setMinOccurs(eu.chorevolution.modelingnotations.adapter.OccurencesType.getByName(treeNode.getData().getMinOccurrences()));
			
			if(toReturn instanceof ChoreographySimpleItem) {
				treeNode.getData().setIsMappedWithChoreographyDataItem(toReturn);//i add the mapping into the treeNode
				return toReturn;
			}
			else if(toReturn instanceof ChoreographyComplexItem) {
				if((!treeNode.getData().getDataType().equals(Utilities.getINITIATING_TYPE_TYPE()))&&((!treeNode.getData().getDataType().equals(Utilities.getNON_INITIATING_TYPE_TYPE())))) {
					((ChoreographyComplexItem)toReturn).setTypeName(treeNode.getData().getDataType());
				}
				else {//in case of type non defined
					((ChoreographyComplexItem)toReturn).setTypeName(treeNode.getData().getContent());
				}
			}
		}
		catch(Exception e) {}
		
		for(TreeNode<TreeNodeDataType> node : treeNode.getChildren()) {
			((ChoreographyComplexItem)toReturn).getHasChoreographyDataItems().add(getChoreographyDataItemTreeFromTree(node));
		}
		
		treeNode.getData().setIsMappedWithChoreographyDataItem(toReturn);//i add the mapping into the treeNode
		return toReturn;
		
	}
	
	
	public static DataItem getDataItemTreeFromTree(TreeNode<TreeNodeDataType> treeNode, int instanceNumber) {
		if(instanceNumber == 0) {//skip duplicate son
			return getDataItemTreeFromTree(treeNode.getChildren().get(0), 1);
		}
		
		DataItem toReturn = eu.chorevolution.modelingnotations.adapter.impl.AdapterFactoryImpl.eINSTANCE.createComplexItem();
		
		try {
			if(treeNode.getData().getComplexOrSimpleType().equals("complexType")) {
				toReturn = eu.chorevolution.modelingnotations.adapter.impl.AdapterFactoryImpl.eINSTANCE.createComplexItem();
			}
			else {
				toReturn = eu.chorevolution.modelingnotations.adapter.impl.AdapterFactoryImpl.eINSTANCE.createSimpleItem();
				((SimpleItem)toReturn).setType(getAssociatedSimpleType(treeNode.getData().getType()));
			}
				
			toReturn.setName(treeNode.getData().getContent());
			toReturn.setMaxOccurs(eu.chorevolution.modelingnotations.adapter.OccurencesType.getByName(treeNode.getData().getMaxOccurrences()));
			toReturn.setMinOccurs(eu.chorevolution.modelingnotations.adapter.OccurencesType.getByName(treeNode.getData().getMinOccurrences()));


			if(toReturn instanceof SimpleItem) {
				treeNode.getData().setIsMappedWithDataItem(toReturn);//i add the mapping into the treeNode
				return toReturn;
			}
		}
		catch(Exception e) {}
		
		for(TreeNode<TreeNodeDataType> node : treeNode.getChildren()) {
			((ComplexItem)toReturn).getHasDataItems().add(getDataItemTreeFromTree(node, 1));
		}
		
		treeNode.getData().setIsMappedWithDataItem(toReturn);//i add the mapping into the treeNode
		return toReturn;
		
	}
	
	public static EList<DataItemRelation> getDataItemRelationTreeFromTree(TreeNode<TreeNodeDataType> treeNode, ChoreographyMessage chorMessage, Message serviceMessage) {

		EList<DataItemRelation> listToReturn = new BasicEList<DataItemRelation>();
				
		DataItemRelation toReturn = eu.chorevolution.modelingnotations.adapter.impl.AdapterFactoryImpl.eINSTANCE.createDataItemRelation();
		
		ChoreographyDataItem choreogItem = Utilities.getChoreographyDataItemFromChoreographyItem(chorMessage.getHasChoreographyDataItem(), treeNode);	
		//this can also be null, in case of staticvalue transformation rule
		//can also have multiple entries
		
		for(int j=0; j<treeNode.getData().getIsMappedWithTreeNode().size(); j++) {
		
			DataItem serviceItem = Utilities.getDataItemFromItem(serviceMessage.getHasMessageDataItem(), treeNode.getData().getIsMappedWithTreeNode().get(j));
	
			if(serviceItem != null) {
				toReturn.getChoreographyDataItem().add(choreogItem);
				toReturn.getDataItem().add(serviceItem);
	
				if(treeNode.getData().getIsMappedWithTreeNode().get(j).getData().getTransformationRule() != null) {
					toReturn.setTransformationRule(treeNode.getData().getIsMappedWithTreeNode().get(j).getData().getTransformationRule());
				}
				else if(treeNode.getData().getTransformationRule() != null) {//transformation rule can be also on left side
					toReturn.setTransformationRule(treeNode.getData().getTransformationRule());
				}
				listToReturn.add(toReturn);
			}
			else {
				//it is possible that is a single mapped element with transformation rule
				if(treeNode.getData().getTransformationRule() != null) {//in case of staticvalue
					toReturn.getChoreographyDataItem().add(choreogItem);
					toReturn.setTransformationRule(treeNode.getData().getTransformationRule());
	
					listToReturn.add(toReturn);
				}
			}
		
		}

		for(TreeNode<TreeNodeDataType> node : treeNode.getChildren()) {
			listToReturn.addAll(getDataItemRelationTreeFromTree(node, chorMessage, serviceMessage));
		}
		
		return listToReturn;
		
	}
	
	
	public static ChoreographyDataItem getChoreographyDataItemFromChoreographyItem(ChoreographyDataItem chorItem, TreeNode<TreeNodeDataType> typeName) {
		
			if(chorItem.equals(typeName.getData().getIsMappedWithChoreographyDataItem())) {
				return chorItem;
			}
			
			if(chorItem instanceof ChoreographyComplexItem) {
				ChoreographyComplexItem comp = (ChoreographyComplexItem) chorItem;
				ChoreographyDataItem definitive = null;
				for(ChoreographyDataItem toReturn : comp.getHasChoreographyDataItems()) {
					definitive = Utilities.getChoreographyDataItemFromChoreographyItem(toReturn, typeName);
					if(definitive != null)
						return definitive;
				}
			}

			
			return null;
	}
	
	
	public static DataItem getDataItemFromItem(DataItem chorItem, TreeNode<TreeNodeDataType> typeName) {

		try {//is possible that a type does not had been mapped, so return null
			if(typeName == null)
				return null;
			
			if(chorItem.equals(typeName.getData().getIsMappedWithDataItem())) {
				return chorItem;
			}
	
			//TODO verify for WP4 with two tasks
				
			if(chorItem instanceof ComplexItem) {
				ComplexItem comp = (ComplexItem) chorItem;
				DataItem definitive = null;
				for(DataItem toReturn : comp.getHasDataItems()) {
					definitive = Utilities.getDataItemFromItem(toReturn, typeName);
					if(definitive != null) {
						return definitive;
					}
				}
			}
			
		}
		catch(Exception e) {
			return null;
		}
		
			return null;
	}
	
	public static eu.chorevolution.modelingnotations.adapter.SimpleTypes getAssociatedSimpleType(String type) {
		
		eu.chorevolution.modelingnotations.adapter.SimpleTypes result = eu.chorevolution.modelingnotations.adapter.SimpleTypes.getByName(type);

		if(result == null) {

			//non-associated types
			if(type.equals("long"))
				return eu.chorevolution.modelingnotations.adapter.SimpleTypes.DOUBLE;
			else if(type.equals("float"))
				return eu.chorevolution.modelingnotations.adapter.SimpleTypes.DOUBLE;
			else if(type.equals("int"))
				return eu.chorevolution.modelingnotations.adapter.SimpleTypes.INTEGER;
			else if(type.equals("integer"))
				return eu.chorevolution.modelingnotations.adapter.SimpleTypes.INTEGER;
			else if(type.equals("string"))
				return eu.chorevolution.modelingnotations.adapter.SimpleTypes.STRING;
			else if(type.equals("double"))
				return eu.chorevolution.modelingnotations.adapter.SimpleTypes.DOUBLE;	
			else if(type.equals("dateTime"))
				return eu.chorevolution.modelingnotations.adapter.SimpleTypes.DATE_TIME;//TODO verify this
			else if(type.equals("date"))
				return eu.chorevolution.modelingnotations.adapter.SimpleTypes.DATE;
			else if(type.equals("time"))
				return eu.chorevolution.modelingnotations.adapter.SimpleTypes.TIME;
			else if(type.equals("boolean"))
				return eu.chorevolution.modelingnotations.adapter.SimpleTypes.BOOLEAN;
			else if(type.equals("bool"))
				return eu.chorevolution.modelingnotations.adapter.SimpleTypes.BOOLEAN;
						
			
		}
		
		return result;
		

	}
	
	
	public static boolean isTreeMappingComplete(TreeNode<TreeNodeDataType> treeNode) {
		boolean complete = false;

		if(!treeNode.getData().getIsMappedWithTreeNode().isEmpty())
			complete = true;
		else if(treeNode.getData().getTransformationRule() != null)
			complete = true;
		else if(treeNode.getData().getComplexOrSimpleType().equals("complexType") && Utilities.isAllSubTypesMapped(treeNode, true))
			complete = true;
		else if(treeNode.getData().getMinOccurrences().equals("zero"))
			complete = true;
		
		for(TreeNode<TreeNodeDataType> node : treeNode.getChildren()) {
			complete = complete && isTreeMappingComplete(node);
		}
		
		return complete;
	}

	
	public static byte[] addFakeTypeToGIDL(byte[] documentGIDL) throws SAXException, IOException, ParserConfigurationException, TransformerException {

		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(documentGIDL));

		try {
			Element element = (Element) d.getElementsByTagName("inputData").item(0);
	        Element elemInner = (Element) element.getFirstChild().getNextSibling();
			NodeList elemToDelete = element.getChildNodes();

			Element toAppend = d.createElement("hasDataType");
	        toAppend.setAttribute("xsi:type", "gidl:ComplexType");
	        toAppend.setAttribute("name", INPUT_FAKE_TYPE);
	        toAppend.setAttribute("occurences", "one");
	        
	        element.insertBefore(toAppend, elemInner);
	        
	        for(int i=0; i<elemToDelete.getLength(); i++)
	        	toAppend.appendChild(elemToDelete.item(i));
	        
	        //skip first new node to delete
	        elemToDelete = element.getChildNodes();
	        for(int i=1; i<elemToDelete.getLength(); i++)
	        	element.removeChild(elemToDelete.item(i));
	        
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			Element element = (Element) d.getElementsByTagName("outputData").item(0);
	        Element elemInner = (Element) element.getFirstChild().getNextSibling();
			NodeList elemToDelete = element.getChildNodes();

			Element toAppend = d.createElement("hasDataType");
	        toAppend.setAttribute("xsi:type", "gidl:ComplexType");
	        toAppend.setAttribute("name", OUTPUT_FAKE_TYPE);
	        toAppend.setAttribute("occurences", "one");
	        
	        element.insertBefore(toAppend, elemInner);
	        
	        for(int i=0; i<elemToDelete.getLength(); i++)
	        	toAppend.appendChild(elemToDelete.item(i));
	        
	        //skip first new node to delete
	        elemToDelete = element.getChildNodes();
	        for(int i=1; i<elemToDelete.getLength(); i++)
	        	element.removeChild(elemToDelete.item(i));
		}
		catch(Exception e) {}
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Result output = new StreamResult(os);
		Source input = new DOMSource(d);
		
		transformer.transform(input, output);
		
		return os.toByteArray();
			
		
	}
	
	public static String getGIDLPlaceHolder() {
		return GIDLPlaceHolder;
	}



	public static String getTASK_NAME() {
		return TASK_NAME;
	}


	public static String getMESSAGE_NAME() {
		return MESSAGE_NAME;
	}



	public static String getMESSAGE_TYPE() {
		return MESSAGE_TYPE;
	}

	
	public static void cleanTreeMapping(TreeNode<TreeNodeDataType> rootElement) {
		
		rootElement.getData().setIsMappedWithTreeNode(new ArrayList<TreeNode<TreeNodeDataType>>());
		rootElement.getData().setTransformationRule(null);
		
		for(TreeNode<TreeNodeDataType> node : rootElement.getChildren())
			cleanTreeMapping(node);
		
	}
	
	public static void autoMapTree(TreeNode<TreeNodeDataType> rootElement, TreeNode<TreeNodeDataType> wholeTree, TreeNode<TreeNodeDataType> treeNodeWSDLOrGIDL, byte[] wsdlOrGidlFile, String adName, List<String> listString) throws SAXException, IOException, ParserConfigurationException, CoreException, URISyntaxException {

		URL url = FileLocator.find(ChorevolutionUIPlugin.getDefault().getBundle(), new Path("adload/"+ChorevolutionCoreUtils.removeBlankSpaces(adName)+".adload"), null);
		url = FileLocator.toFileURL(url);
		File file = URIUtil.toFile(URIUtil.toURI(url));
		
		Scanner input = new Scanner(file);
		List<String> list = null;
		List<Integer> toRemove = new ArrayList<Integer>();
		
		if(listString == null) {
			list = new ArrayList<String>();

			while (input.hasNextLine()) {
				list.add(input.nextLine());
			}
		}
		else {
			list = listString;
		}
		
		boolean firstTree;
		if(Utilities.sonOfType(rootElement, Utilities.getINITIATING_MESSAGE_TYPE(), wholeTree, false))
			firstTree=true;//is the initiatingmessagetree
		else
			firstTree=false;
		
		if(!firstTree) {
			if(rootElement.getData().getType().equals(Utilities.getTASK_TYPE()))
				firstTree = true;
			else if(rootElement.getData().getType().equals(Utilities.getINITIATING_MESSAGE_TYPE()))
				firstTree = true;
		}
		
		int counterXSD = 0;
		boolean reachedSecond = false;
		boolean found = false;
		
		if(rootElement.getData().getType().equals("0")) {
			TreeNode<TreeNodeDataType> flattened = Utilities.getElementsFromTree(treeNodeWSDLOrGIDL, "0").get(0);
			rootElement.getData().addMappedWithTreeNode(flattened);		
		}
		else if(rootElement.getData().getType().equals("1")) {
			TreeNode<TreeNodeDataType> flattened = Utilities.getElementsFromTree(treeNodeWSDLOrGIDL, "1").get(0);
			rootElement.getData().addMappedWithTreeNode(flattened);		
		}
		else if(rootElement.getData().getType().equals("2")) {
			TreeNode<TreeNodeDataType> flattened = Utilities.getElementsFromTree(treeNodeWSDLOrGIDL, "2").get(0);
			rootElement.getData().addMappedWithTreeNode(flattened);		
		}
		else if(rootElement.getData().getType().equals("3")) {
			TreeNode<TreeNodeDataType> flattened = Utilities.getElementsFromTree(treeNodeWSDLOrGIDL, "3").get(0);
			rootElement.getData().addMappedWithTreeNode(flattened);		
		}
		else if(rootElement.getData().getType().equals("4")) {
			TreeNode<TreeNodeDataType> flattened = Utilities.getElementsFromTree(treeNodeWSDLOrGIDL, "4").get(0);
			rootElement.getData().addMappedWithTreeNode(flattened);		
		}
		else {
			int listCounter = -1;
			int sameLevel =-1;
			for(String toMap : list) {
				listCounter += 1;
				try {
					if(toMap.charAt(0) == '$' || toMap.charAt(0) == '%') {//% is the symbol for left single mapping, $ is for right single mapping
										
						if(toMap.charAt(0) == '%') {
							String[] splitted = toMap.split("%")[1].split("~")[0].split(":");
							int countFirst=0;
							List<MappingListReturnType> flattened = Utilities.flatTreeToTreesWithInputOutputMsg(wholeTree, 0);
							
							for(MappingListReturnType flat : flattened) {
								if(flat.getTreeNode().getData().getContent().equals(splitted[0])) {
									if(Integer.toString(countFirst).equals(splitted[2])) {							
										flat.getTreeNode().getData().setTransformationRule(toMap.split("~")[1]);
										toRemove.add(new Integer(listCounter));
										break;
									}
									else {
										countFirst++;
									}
								}
							}
						}
						else if(toMap.charAt(0) == '$') {
							String[] splitted = toMap.split("\\$")[1].split("~")[0].split(":");
							int countFirst=0;
							List<MappingListReturnType> flattened = Utilities.flatTreeToTreesWithInputOutputMsg(treeNodeWSDLOrGIDL, 0);
							
							for(MappingListReturnType flat : flattened) {
								if(flat.getTreeNode().getData().getContent().equals(splitted[0])) {
									if(Integer.toString(countFirst).equals(splitted[2])) {							
										flat.getTreeNode().getData().setTransformationRule(toMap.split("~")[1]);
										toRemove.add(new Integer(listCounter));
										break;
									}
									else {
										countFirst++;
									}
								}
							}
						}
						continue;
					}
				
				}
				catch(Exception e) {

				}
				
				String[] splitted = toMap.split("~")[0].split(":");
				if(splitted[0].equals("-")) {
					reachedSecond = true;
					continue;
				}
	
				//im in first tree
				if(firstTree && !reachedSecond) {
					TreeNode<TreeNodeDataType> toPass = Utilities.getElementsFromTree(treeNodeWSDLOrGIDL, "3").get(0);

					if(splitted[0].equals(rootElement.getData().getContent())) {
						if(splitted[1].equals(rootElement.getData().getType())) {
							if(sameLevel == -1 || Integer.toString(sameLevel).equals(splitted[2])) {
								List<MappingListReturnType> flattened = Utilities.flatTreeToTreesWithInputOutputMsg(toPass, 0);
								int counterG = 0;
								
								for(MappingListReturnType flat : flattened) {
									
									if(splitted[3].equals("null")) {
										sameLevel = Integer.parseInt(splitted[2]);
										found = true;
										toRemove.add(new Integer(listCounter));	
										break;
									}
									
									TreeNode<TreeNodeDataType> nodeToVerify = flat.getTreeNode();
									
									if(nodeToVerify.getData().getContent().equals(splitted[3])) {
										if(Integer.toString(counterG).equals(splitted[5])) {
											sameLevel = Integer.parseInt(splitted[2]);
											rootElement.getData().addMappedWithTreeNode(nodeToVerify);	
											found = true;
											toRemove.add(new Integer(listCounter));
											
											//search for transformation rule, in request i add it on the RIGHT
											if(toMap.split("~").length > 1) {
												nodeToVerify.getData().setTransformationRule(toMap.split("~")[1]);
											}
											
											break;
		
										}
										else {
											counterG++;
										}
									}
									
								}
								
							
							}
						}
					}
				}
				else if(!firstTree && reachedSecond) {
					TreeNode<TreeNodeDataType> toPass = Utilities.getElementsFromTree(treeNodeWSDLOrGIDL, "4").get(0);
					
					if(splitted[0].equals(rootElement.getData().getContent())) {
						if(splitted[1].equals(rootElement.getData().getType())) {
							if(sameLevel == -1 || Integer.toString(sameLevel).equals(splitted[2])) {
								List<MappingListReturnType> flattened = Utilities.flatTreeToTreesWithInputOutputMsg(toPass, 0);
								int counterG = 0;
								
								for(MappingListReturnType flat : flattened) {
									
									if(splitted[3].equals("null")) {
										sameLevel = Integer.parseInt(splitted[2]);
										found = true;
										toRemove.add(new Integer(listCounter));	
										break;
									}
									
									TreeNode<TreeNodeDataType> nodeToVerify = flat.getTreeNode();
									
									if(nodeToVerify.getData().getContent().equals(splitted[3])) {
										if(Integer.toString(counterG).equals(splitted[5])) {
											sameLevel = Integer.parseInt(splitted[2]);
											rootElement.getData().addMappedWithTreeNode(nodeToVerify);	
											found = true;
											toRemove.add(new Integer(listCounter));
											
											//search for transformation rule, in response i add it on the LEFT
											if(toMap.split("~").length > 1) {
												rootElement.getData().setTransformationRule(toMap.split("~")[1]);
											}
											
											break;
		
										}
										else {
											counterG++;
										}
									}
									
								}
								
							
							}
						}
					}	
				}
				
			}
		}
		
		for(Integer r : toRemove) {
			list.remove(r.intValue());
		}
		

		
		for(TreeNode<TreeNodeDataType> node : rootElement.getChildren()) {
			Utilities.autoMapTree(node, wholeTree, treeNodeWSDLOrGIDL, wsdlOrGidlFile, adName, list);
		}
		
		
		
	}
	
	public static void autoMapTreeAlpha(TreeNode<TreeNodeDataType> leftTree, TreeNode<TreeNodeDataType> rightTree) {
		//removes root:root
		leftTree = leftTree.getChildren().get(0);
		rightTree = rightTree.getChildren().get(0);

		
		List<MappingListReturnType> flattenedLeft = Utilities.flatTreeToTreesWithInputOutputMsg(leftTree, 0);
		List<MappingListReturnType> flattenedRight = Utilities.flatTreeToTreesWithInputOutputMsg(rightTree, 0);
		
		List<TreeNode<TreeNodeDataType>> treeNodesLeftFirst = new ArrayList<TreeNode<TreeNodeDataType>>();
		List<TreeNode<TreeNodeDataType>> treeNodesRightFirst = new ArrayList<TreeNode<TreeNodeDataType>>();

		List<TreeNode<TreeNodeDataType>> treeNodesLeftSecond = new ArrayList<TreeNode<TreeNodeDataType>>();
		List<TreeNode<TreeNodeDataType>> treeNodesRightSecond = new ArrayList<TreeNode<TreeNodeDataType>>();
		
		//eliminate basic types from the tree
		for(MappingListReturnType flatLeft : flattenedLeft) {
			TreeNode<TreeNodeDataType> treeNodeLeft = flatLeft.getTreeNode();

			if(treeNodeLeft.getData().getType().equals("0")) {
				TreeNode<TreeNodeDataType> flattened = Utilities.getElementsFromTree(rightTree, "0").get(0);
				treeNodeLeft.getData().addMappedWithTreeNode(flattened);		
			}
			else if(treeNodeLeft.getData().getType().equals("1")) {
				TreeNode<TreeNodeDataType> flattened = Utilities.getElementsFromTree(rightTree, "1").get(0);
				treeNodeLeft.getData().addMappedWithTreeNode(flattened);		
			}
			else if(treeNodeLeft.getData().getType().equals("2")) {
				TreeNode<TreeNodeDataType> flattened = Utilities.getElementsFromTree(rightTree, "2").get(0);
				treeNodeLeft.getData().addMappedWithTreeNode(flattened);		
			}
			else if(treeNodeLeft.getData().getType().equals("3")) {
				TreeNode<TreeNodeDataType> flattened = Utilities.getElementsFromTree(rightTree, "3").get(0);
				treeNodeLeft.getData().addMappedWithTreeNode(flattened);		
			}
			else if(treeNodeLeft.getData().getType().equals("4")) {
				TreeNode<TreeNodeDataType> flattened = Utilities.getElementsFromTree(rightTree, "4").get(0);
				treeNodeLeft.getData().addMappedWithTreeNode(flattened);		
			}
			else {
				if(Utilities.sonOfType(treeNodeLeft, Utilities.getINITIATING_TYPE_TYPE(), leftTree, false))
					treeNodesLeftFirst.add(treeNodeLeft);
				else
					treeNodesLeftSecond.add(treeNodeLeft);
			}
		}
		
		for(MappingListReturnType flatRight : flattenedRight) {
			TreeNode<TreeNodeDataType> treeNodeRight = flatRight.getTreeNode();

			if(treeNodeRight.getData().getType().equals("0")) {
				TreeNode<TreeNodeDataType> flattened = Utilities.getElementsFromTree(rightTree, "0").get(0);
				treeNodeRight.getData().addMappedWithTreeNode(flattened);		
			}
			else if(treeNodeRight.getData().getType().equals("1")) {
				TreeNode<TreeNodeDataType> flattened = Utilities.getElementsFromTree(rightTree, "1").get(0);
				treeNodeRight.getData().addMappedWithTreeNode(flattened);		
			}
			else if(treeNodeRight.getData().getType().equals("2")) {
				TreeNode<TreeNodeDataType> flattened = Utilities.getElementsFromTree(rightTree, "2").get(0);
				treeNodeRight.getData().addMappedWithTreeNode(flattened);		
			}
			else if(treeNodeRight.getData().getType().equals("3")) {
				TreeNode<TreeNodeDataType> flattened = Utilities.getElementsFromTree(rightTree, "3").get(0);
				treeNodeRight.getData().addMappedWithTreeNode(flattened);		
			}
			else if(treeNodeRight.getData().getType().equals("4")) {
				TreeNode<TreeNodeDataType> flattened = Utilities.getElementsFromTree(rightTree, "4").get(0);
				treeNodeRight.getData().addMappedWithTreeNode(flattened);		
			}
			else if(treeNodeRight.getData().getContent().equals(getInputAddedType())) {
				
			}
			else if(treeNodeRight.getData().getContent().equals(getOutputAddedType())) {
				
			}
			else {
				if(Utilities.sonOfType(treeNodeRight, Utilities.getINITIATING_TYPE_TYPE(), rightTree, false))
					treeNodesRightFirst.add(treeNodeRight);
				else
					treeNodesRightSecond.add(treeNodeRight);
			}
		}


		double[][] firstScores = new double[treeNodesLeftFirst.size()][treeNodesRightFirst.size()];
		int posLeft = 0;
		int posRight = 0;
		
		for(TreeNode<TreeNodeDataType> treeNodeLeft : treeNodesLeftFirst) {
			posRight = 0;
			
			for(TreeNode<TreeNodeDataType> treeNodeRight : treeNodesRightFirst) {
				String nameLeft = treeNodeLeft.getData().getContent();
				String nameRight = treeNodeRight.getData().getContent();
				String typeLeft = treeNodeLeft.getData().getType();
				String typeRight = treeNodeRight.getData().getType();
				String minOccursLeft = treeNodeLeft.getData().getMinOccurrences();
				String minOccursRight = treeNodeRight.getData().getMinOccurrences();
				String maxOccursLeft = treeNodeLeft.getData().getMaxOccurrences();
				String maxOccursRight = treeNodeRight.getData().getMaxOccurrences();
				String simpleComplexTypeLeft = treeNodeLeft.getData().getComplexOrSimpleType();
				String simpleComplexTypeRight = treeNodeRight.getData().getComplexOrSimpleType();
				
				//i compute similarities and scores
				double patternScore = calculatePatternScore(nameLeft, nameRight);
				double typeScore = calculateTypeScore(typeLeft, typeRight);
				double minOccursScore = calculateMinOccursScore(minOccursLeft, minOccursRight);
				double maxOccursScore = calculateMaxOccursScore(maxOccursLeft, maxOccursRight);
				double simpleComplexTypeScore = calculateSimpleComplexTypeScore(simpleComplexTypeLeft, simpleComplexTypeRight);		
				
				firstScores[posLeft][posRight] = (patternScore * scorePatternMultiplier);
				firstScores[posLeft][posRight] += (typeScore * scoreTypeMultiplier);
				firstScores[posLeft][posRight] += (minOccursScore * scoreMinOccursMultiplier);
				firstScores[posLeft][posRight] += (maxOccursScore * scoreMaxOccursMultiplier);
				firstScores[posLeft][posRight] += (simpleComplexTypeScore * scoreSimpleComplexTypeMultiplier);

				firstScores[posLeft][posRight] *= scoreMultiplier;
				
				
				posRight++;
			}
		
			posLeft++;
		}
		
		
		//now that the matrix is full, let's assign all the higher points and remove from row
		
		for(int d = 0; d<treeNodesLeftFirst.size(); d++) {
			double maxValue = -9999;
			int iMax = 0;
			int jMax = 0;
			
			for(int i=0; i<treeNodesLeftFirst.size(); i++) {
				for(int j=0; j<treeNodesRightFirst.size(); j++) {
					if(firstScores[i][j] > maxValue) {
						maxValue = firstScores[i][j];
						iMax = i;
						jMax = j;
					}
				}
			}
			
			if(maxValue >= scoreMinScoreForAcceptance)
				treeNodesLeftFirst.get(iMax).getData().addMappedWithTreeNode(treeNodesRightFirst.get(jMax));
			
			for(int g = 0; g < treeNodesLeftFirst.size(); g++)
				firstScores[g][jMax] = -99999;
			
			for(int g = 0; g < treeNodesRightFirst.size(); g++)
				firstScores[iMax][g] = -99999;
			
		}
		
		//start with second side of tree (response)
		
		double[][] secondScores = new double[treeNodesLeftSecond.size()][treeNodesRightSecond.size()];
		posLeft = 0;
		posRight = 0;
		
		for(TreeNode<TreeNodeDataType> treeNodeLeft : treeNodesLeftSecond) {
			posRight = 0;
			
			for(TreeNode<TreeNodeDataType> treeNodeRight : treeNodesRightSecond) {
				String nameLeft = treeNodeLeft.getData().getContent();
				String nameRight = treeNodeRight.getData().getContent();
				String typeLeft = treeNodeLeft.getData().getType();
				String typeRight = treeNodeRight.getData().getType();
				String minOccursLeft = treeNodeLeft.getData().getMinOccurrences();
				String minOccursRight = treeNodeRight.getData().getMinOccurrences();
				String maxOccursLeft = treeNodeLeft.getData().getMaxOccurrences();
				String maxOccursRight = treeNodeRight.getData().getMaxOccurrences();
				String simpleComplexTypeLeft = treeNodeLeft.getData().getComplexOrSimpleType();
				String simpleComplexTypeRight = treeNodeRight.getData().getComplexOrSimpleType();
				
				//i compute similarities and scores
				double patternScore = calculatePatternScore(nameLeft, nameRight);
				double typeScore = calculateTypeScore(typeLeft, typeRight);
				double minOccursScore = calculateMinOccursScore(minOccursLeft, minOccursRight);
				double maxOccursScore = calculateMaxOccursScore(maxOccursLeft, maxOccursRight);
				double simpleComplexTypeScore = calculateSimpleComplexTypeScore(simpleComplexTypeLeft, simpleComplexTypeRight);		
				
				secondScores[posLeft][posRight] = (patternScore * scorePatternMultiplier);
				secondScores[posLeft][posRight] += (typeScore * scoreTypeMultiplier);
				secondScores[posLeft][posRight] += (minOccursScore * scoreMinOccursMultiplier);
				secondScores[posLeft][posRight] += (maxOccursScore * scoreMaxOccursMultiplier);
				secondScores[posLeft][posRight] += (simpleComplexTypeScore * scoreSimpleComplexTypeMultiplier);

				secondScores[posLeft][posRight] *= scoreMultiplier;
				
				posRight++;
			}
		
			posLeft++;
		}
		
		
		//now that the matrix is full, let's assign all the higher points and remove from row
		
		for(int d = 0; d<treeNodesLeftSecond.size(); d++) {
			double maxValue = -9999;
			int iMax = 0;
			int jMax = 0;
			
			for(int i=0; i<treeNodesLeftSecond.size(); i++) {
				for(int j=0; j<treeNodesRightSecond.size(); j++) {
					if(secondScores[i][j] > maxValue) {
						maxValue = secondScores[i][j];
						iMax = i;
						jMax = j;
					}
				}
			}
			
			if(maxValue >= scoreMinScoreForAcceptance)
				treeNodesLeftSecond.get(iMax).getData().addMappedWithTreeNode(treeNodesRightSecond.get(jMax));
			
			for(int g = 0; g < treeNodesLeftSecond.size(); g++)
				secondScores[g][jMax] = -99999;
			
			for(int g = 0; g < treeNodesRightSecond.size(); g++)
				secondScores[iMax][g] = -99999;
			
		}
		
		
	}
	
	static double scoreStarting = 1;
	static double scoreMultiplier = 1;
	static double scoreMinScoreForAcceptance = 70;
	
	static double scoreLetterMultiplier = 1;
	static double scoreSameLetterScore = 5;
	static double scoreSameLetterPositionScore = 20;
	static double scoreLetterDifferenceMultiplier = 0.85;//for each character in len difference
	
	static double scorePatternMultiplier = 1;
	static double scoreSamePatternScore = 1.1;
	static double scoreSamePatternPositionScore = 1.2;
	static double scorePatternDifferenceMultiplier = 0.1;
	static double scorePatternLengthMultiplier = 15;
	
	static double scoreTypeMultiplier = 1;
	static double scoreSameTypeScore = 40;
	static double scoreSimilarTypeScore = 20;
	static double scoreDifferentTypeScore = -40;
	
	static double scoreMinOccursMultiplier = 1;
	static double scoreMinOccursSameScore = 5;
	static double scoreMinOccursSimilarScore = 2.5;
	static double scoreMinOccursDifferentScore = -7;
	
	static double scoreMaxOccursMultiplier = 1;
	static double scoreMaxOccursSameScore = 5;
	static double scoreMaxOccursSimilarScore = 2.5;
	static double scoreMaxOccursDifferentScore = -7;

	
	static double scoreSimpleComplexTypeMultiplier = 1;
	static double scoreSimpleComplexSameTypeScore = 10;
	
	static double scoreFirstLetterBonus = 1.5;
	
	static double scoreDivisionFactor = 1.2;
	
	
	static double calculateSimpleComplexTypeScore(String firstType, String secondType) {
		firstType = firstType.toLowerCase();
		secondType = secondType.toLowerCase();
		double thisScore = 0;

		if(firstType.equals(secondType))
			thisScore += scoreSimpleComplexSameTypeScore;
		
		
		return thisScore;
	}

	static double calculateMinOccursScore(String firstMinOccurs, String secondMinOccurs) {
		firstMinOccurs = firstMinOccurs.toLowerCase();
		secondMinOccurs = secondMinOccurs.toLowerCase();
		double thisScore = 0;
		
		if(firstMinOccurs.equals(secondMinOccurs))
			thisScore += scoreMinOccursSameScore;
		else {
			
			if(firstMinOccurs.equals("zero") && secondMinOccurs.equals("one")) {
				thisScore += scoreMinOccursSimilarScore;
			}
			else if(firstMinOccurs.equals("zero") && secondMinOccurs.equals("unbounded")) {
				thisScore += scoreMinOccursDifferentScore;
			}
			else if(firstMinOccurs.equals("one") && secondMinOccurs.equals("unbounded")) {
				thisScore += scoreMinOccursDifferentScore;
			}
			else if(firstMinOccurs.equals("one") && secondMinOccurs.equals("zero")) {
				thisScore += scoreMinOccursSimilarScore;
			}
			else if(firstMinOccurs.equals("unbounded") && secondMinOccurs.equals("zero")) {
				thisScore += scoreMinOccursDifferentScore;
			}
			else if(firstMinOccurs.equals("unbounded") && secondMinOccurs.equals("one")) {
				thisScore += scoreMinOccursDifferentScore;
			}
			
		}
		return thisScore;
		
	}
		
	static double calculateMaxOccursScore(String firstMaxOccurs, String secondMaxOccurs) {
		firstMaxOccurs = firstMaxOccurs.toLowerCase();
		secondMaxOccurs = secondMaxOccurs.toLowerCase();
		double thisScore = 0;
		
		if(firstMaxOccurs.equals(secondMaxOccurs))
			thisScore += scoreMinOccursSameScore;
		else {
			
			if(firstMaxOccurs.equals("zero") && secondMaxOccurs.equals("one")) {
				thisScore += scoreMaxOccursDifferentScore;
			}
			else if(firstMaxOccurs.equals("zero") && secondMaxOccurs.equals("unbounded")) {
				thisScore += scoreMaxOccursDifferentScore;
			}
			else if(firstMaxOccurs.equals("one") && secondMaxOccurs.equals("unbounded")) {
				thisScore += scoreMaxOccursSimilarScore;
			}
			else if(firstMaxOccurs.equals("one") && secondMaxOccurs.equals("zero")) {
				thisScore += scoreMaxOccursDifferentScore;
			}
			else if(firstMaxOccurs.equals("unbounded") && secondMaxOccurs.equals("zero")) {
				thisScore += scoreMaxOccursDifferentScore;
			}
			else if(firstMaxOccurs.equals("unbounded") && secondMaxOccurs.equals("one")) {
				thisScore += scoreMaxOccursSimilarScore;
			}
			
		}
		return thisScore;
		
	}
	

	static double calculateTypeScore(String firstType, String secondType) {
		firstType = firstType.toLowerCase();
		secondType = secondType.toLowerCase();
		double thisScore = 0;
		
		if(firstType.equals("int"))
			firstType = "integer";
		if(firstType.equals("bool"))
			firstType = "boolean";
		if(secondType.equals("int"))
			secondType = "integer";
		if(secondType.equals("bool"))
			secondType = "boolean";
		
		if(firstType.equals(secondType))
			thisScore += scoreSameTypeScore;
		else {
			
			if(firstType.equals("string") && secondType.equals("integer")) {
				thisScore += scoreSimilarTypeScore;
			}
			else if(firstType.equals("string") && secondType.equals("double")) {
				thisScore += scoreSimilarTypeScore;
			}
			else if(firstType.equals("string") && secondType.equals("float")) {
				thisScore += scoreSimilarTypeScore;
			}
			else if(firstType.equals("string") && secondType.equals("boolean")) {
				thisScore += scoreSimilarTypeScore;
			}
			else if(firstType.equals("string") && secondType.equals("datetime")) {
				thisScore += scoreDifferentTypeScore;
			}
			else if(firstType.equals("integer") && secondType.equals("float")) {
				thisScore += scoreSimilarTypeScore;
			}
			else if(firstType.equals("integer") && secondType.equals("double")) {
				thisScore += scoreSimilarTypeScore;
			}
			else if(firstType.equals("integer") && secondType.equals("boolean")) {
				thisScore += scoreDifferentTypeScore;
			}
			else if(firstType.equals("integer") && secondType.equals("datetime")) {
				thisScore += scoreDifferentTypeScore;
			}
			else if(firstType.equals("float") && secondType.equals("double")) {
				thisScore += scoreSimilarTypeScore;
			}
			else if(firstType.equals("float") && secondType.equals("boolean")) {
				thisScore += scoreDifferentTypeScore;
			}
			else if(firstType.equals("float") && secondType.equals("datetime")) {
				thisScore += scoreDifferentTypeScore;
			}
			else if(firstType.equals("double") && secondType.equals("boolean")) {
				thisScore += scoreDifferentTypeScore;
			}
			else if(firstType.equals("double") && secondType.equals("datetime")) {
				thisScore += scoreDifferentTypeScore;
			}//do vice-versa
			if(secondType.equals("string") && firstType.equals("integer")) {
				thisScore += scoreSimilarTypeScore;
			}
			else if(secondType.equals("string") && firstType.equals("double")) {
				thisScore += scoreSimilarTypeScore;
			}
			else if(secondType.equals("string") && firstType.equals("float")) {
				thisScore += scoreSimilarTypeScore;
			}
			else if(secondType.equals("string") && firstType.equals("boolean")) {
				thisScore += scoreSimilarTypeScore;
			}
			else if(secondType.equals("string") && firstType.equals("datetime")) {
				thisScore += scoreDifferentTypeScore;
			}
			else if(secondType.equals("integer") && firstType.equals("float")) {
				thisScore += scoreSimilarTypeScore;
			}
			else if(secondType.equals("integer") && firstType.equals("double")) {
				thisScore += scoreSimilarTypeScore;
			}
			else if(secondType.equals("integer") && firstType.equals("boolean")) {
				thisScore += scoreDifferentTypeScore;
			}
			else if(secondType.equals("integer") && firstType.equals("datetime")) {
				thisScore += scoreDifferentTypeScore;
			}
			else if(secondType.equals("float") && firstType.equals("double")) {
				thisScore += scoreSimilarTypeScore;
			}
			else if(secondType.equals("float") && firstType.equals("boolean")) {
				thisScore += scoreDifferentTypeScore;
			}
			else if(secondType.equals("float") && firstType.equals("datetime")) {
				thisScore += scoreDifferentTypeScore;
			}
			else if(secondType.equals("double") && firstType.equals("boolean")) {
				thisScore += scoreDifferentTypeScore;
			}
			else if(secondType.equals("double") && firstType.equals("datetime")) {
				thisScore += scoreDifferentTypeScore;
			}

		}
		
		
		return thisScore;
	}
	
	
	//score = scoreStarting * (scoreLetterMultiplier * scoreSameLetter * scoreSameLetterPositionScore
	static double calculateLetterScore(String firstWord, String secondWord) {
		firstWord = firstWord.toLowerCase();
		secondWord = secondWord.toLowerCase();
		double thisScore = 0;
	
		for(int i=0; i<firstWord.length(); i++) {
			for(int j=0; j<secondWord.length(); j++) {
				double internalScore = scoreStarting;
				
				if(firstWord.charAt(i) == secondWord.charAt(j)) {
					if(i == j)
						internalScore *= scoreLetterMultiplier * scoreSameLetterScore * scoreSameLetterPositionScore;
					else
						internalScore *= scoreLetterMultiplier * scoreSameLetterScore;
				}
				
				internalScore *= (Math.max(firstWord.length(), secondWord.length()) - Math.abs(firstWord.length() - secondWord.length())) * scorePatternLengthMultiplier;
				
				thisScore += internalScore;
				
			}
		}
		
		thisScore -= (Math.abs(firstWord.length() - secondWord.length()) * scoreLetterDifferenceMultiplier);
		
		return thisScore;
	}
	
	
	static double calculatePatternScore(String firstWord, String secondWord) {
		firstWord = firstWord.toLowerCase();
		secondWord = secondWord.toLowerCase();
		double thisScore = 0;
	
		
		List<String> firstWordList = makeAllSubstrings(firstWord);
		List<String> secondWordList = makeAllSubstrings(secondWord);

		
		
		for(int i=0; i<firstWordList.size(); i++) {
			for(int j=0; j<secondWordList.size(); j++) {
				String firstPattern = firstWordList.get(i);
				String secondPattern = secondWordList.get(j);
				double internalScore = scoreStarting;

				internalScore = calculateLetterScore(firstPattern, secondPattern) * scorePatternMultiplier;
		
				//now i have to divide the score, because the greater number of letters, the greatest score
				//internalScore /= ((firstPattern.length() + secondPattern.length()) / 2 );
				internalScore /= ((firstWordList.size() + secondWordList.size()) * (firstWordList.size() + secondWordList.size()) );

				
				thisScore += internalScore;

			}
		}
		
		//give a bonus if first letter is the same
		if(firstWord.charAt(0) == secondWord.charAt(0))
			thisScore *= scoreFirstLetterBonus;
		
		
		return thisScore;
	}
	
	
	
	//make all possibles substrings
	static List<List<String>> makeAllSubstringsRecursive(String input) {


	    if (input.length() == 1)
	        return Collections.singletonList(Collections.singletonList(input));

	    List<List<String>> result = new ArrayList<>();

	    for (List<String> subresult : makeAllSubstringsRecursive(input.substring(1))) {

	        List<String> l2 = new ArrayList<>(subresult);
	        l2.set(0, input.charAt(0) + l2.get(0));
	        result.add(l2);

	        List<String> l = new ArrayList<>(subresult);
	        l.add(0, input.substring(0, 1));
	        result.add(l);
	    }

	    return result;
	}
	
	static List<String> makeAllSubstrings(String s) {
		List<String> toReturn = new ArrayList<>();
		
		if(s.length() == 1) {
			toReturn.add(s);
			return toReturn;
		}
		
		for(int i = 1; i < s.length(); i++) {
			for (int j = 0; j <= s.length() - i; j++) {
			    toReturn.add(s.substring(j, j + i));
			}
		}
		
		return toReturn;
	}
	
	
	
	public static List<MappingListReturnType> flatTreeToTrees(TreeNode<TreeNodeDataType> treeNode, int level) {
		List<MappingListReturnType> returnList = new ArrayList<MappingListReturnType>();
		
		String levelCounter = "";
		for(int i=0; i<level; i++)
			levelCounter = levelCounter+"-";
		
		levelCounter = levelCounter + ">";
		
		try {//can be empty or not valid!
			//TODO verify if needed
			if((!treeNode.getData().getContent().equals(INPUT_FAKE_TYPE)) && (!treeNode.getData().getContent().equals(OUTPUT_FAKE_TYPE)))
				returnList.add(new MappingListReturnType(levelCounter + treeNode.getData().getContent()+":"+treeNode.getData().getType(), treeNode));
		}
		catch(Exception e) {
			//do nothing
		}
		
		for(TreeNode<TreeNodeDataType> node : treeNode.getChildren())
			returnList.addAll(flatTreeToTrees(node, level+1));


		return returnList;
	}
	
	public static List<MappingListReturnType> flatTreeToTreesWithInputOutputMsg(TreeNode<TreeNodeDataType> treeNode, int level) {
		List<MappingListReturnType> returnList = new ArrayList<MappingListReturnType>();
		
		String levelCounter = "";
		for(int i=0; i<level; i++)
			levelCounter = levelCounter+"-";
		
		levelCounter = levelCounter + ">";
		
		try {//can be empty or not valid!
			returnList.add(new MappingListReturnType(levelCounter + treeNode.getData().getContent()+":"+treeNode.getData().getType(), treeNode));
		}
		catch(Exception e) {
			//do nothing
		}
		
		for(TreeNode<TreeNodeDataType> node : treeNode.getChildren())
			returnList.addAll(flatTreeToTrees(node, level+1));


		return returnList;
	}
	
	public static boolean isTreeNodeMappedWithItemOfTree(TreeNode<TreeNodeDataType> singleNode, TreeNode<TreeNodeDataType> wholeTree) {
		for(int j=0; j<wholeTree.getData().getIsMappedWithTreeNode().size(); j++) {
			if(singleNode == wholeTree.getData().getIsMappedWithTreeNode().get(j)) {
				return true;
			}
		}

		for(TreeNode<TreeNodeDataType> node : wholeTree.getChildren()) {
			if(isTreeNodeMappedWithItemOfTree(singleNode, node))
				return true;
		}
		
		return false;
	}
	
	public static void refineComplexOrSimpleTypes(TreeNode<TreeNodeDataType> treeNode) {
		
		for(TreeNode<TreeNodeDataType> node : treeNode.getChildren())
			Utilities.refineComplexOrSimpleTypes(node);
		
		
		try {
			if(isSimpleType(treeNode.getData().getType())) {
				treeNode.getData().setComplexOrSimpleType("simpleType");
				treeNode.setChildren(null);
			}
			else {
				treeNode.getData().setComplexOrSimpleType("complexType");
			}
		}
		catch(Exception e) {}
		
	}

	
	public static boolean isSimpleType(String type) {
		
		//non-associated types
		if(type.equals("long"))
			return true;
		else if(type.equals("float"))
			return true;
		else if(type.equals("int"))
			return true;
		else if(type.equals("integer"))
			return true;
		else if(type.equals("string"))
			return true;
		else if(type.equals("double"))
			return true;
		else if(type.equals("dateTime"))
			return true;
		else if(type.equals("date"))
			return true;
		else if(type.equals("time"))
			return true;
		else if(type.equals("boolean"))
			return true;
		else if(type.equals("bool"))
			return true;
		
		return false;
	}
	
	
	public static String getLabelSymbolFromTreeNode(TreeNode<TreeNodeDataType> node) {
		//zero - one - unbounded
		/*
		min 0 max 0 - 0
		min 0 max 1 - ?
		min 0 max inf - *
		min 1 max 1 - 1 (default - hidden?)
		min 1 max inf - +
		min inf max inf - *
		*/
		
		if(node.getData().getMinOccurrences().equals("zero")) {
			if(node.getData().getMaxOccurrences().equals("zero"))
				return "0";
			else if(node.getData().getMaxOccurrences().equals("one"))
				return "?";
			else if(node.getData().getMaxOccurrences().equals("unbounded"))
				return "*";
		}
		else if(node.getData().getMinOccurrences().equals("one")) {
			if(node.getData().getMaxOccurrences().equals("one"))
				return "1";
			else if(node.getData().getMaxOccurrences().equals("unbounded"))
				return "+";
		}
		else if(node.getData().getMinOccurrences().equals("unbounded")) {
			if(node.getData().getMaxOccurrences().equals("unbounded"))
				return "*";
		}
		
		return ".";
	}
	
	public static boolean isAComplexTypeOnTopOfTree(String type, TreeNode<TreeNodeDataType> wholeTree, String leftRight) {
		//type is the initiatingmessagetype or noninitiatingmessagetype
		//leftRight is the left or right tree of the mapper
		try {
			TreeNode<TreeNodeDataType> nodeOnTop = Utilities.getElementsFromTree(wholeTree, type).get(0);
			if(leftRight.equals("left")) {
				if(nodeOnTop.getNumberOfChildren() == 1 && nodeOnTop.getData().getComplexOrSimpleType().equals("complexType"))
					return true;
			}
			else {
				//skip fake children
				nodeOnTop = nodeOnTop.getChildren().get(0);
				if(nodeOnTop.getNumberOfChildren() == 1 && nodeOnTop.getData().getComplexOrSimpleType().equals("complexType"))
					return true;
			}
		}
		catch(Exception e) {
			
		}
		
		return false;
	}
	
	public static boolean sonOfTreeNode(TreeNode<TreeNodeDataType> simpleNode, TreeNode<TreeNodeDataType> wholeTree) {
		if(simpleNode == wholeTree)
			return true;

		if(wholeTree.getNumberOfChildren() > 0) {
			for(TreeNode<TreeNodeDataType> node : wholeTree.getChildren()) {
				if(sonOfTreeNode(simpleNode, node) == true)
					return true;
			}
		}
		
		return false;
	}
	
	
	public static List<MappingAssociation> cloneAssociation(AdapterModelDataType toCopy) {
		List<MappingAssociation> toReturn = new ArrayList<MappingAssociation>();
		
		fullFillList(toCopy.getTreeFromTask(), toReturn);

		return toReturn;
	}
	
	public static void fullFillList(TreeNode<TreeNodeDataType> treeNode, List<MappingAssociation> mappingAssociation) {
		if(!treeNode.getData().getIsMappedWithTreeNode().isEmpty()) {
			for(TreeNode<TreeNodeDataType> node : treeNode.getData().getIsMappedWithTreeNode())
			 mappingAssociation.add(new MappingAssociation(treeNode, node));

		}
			
		for(TreeNode<TreeNodeDataType> node : treeNode.getChildren())
			fullFillList(node, mappingAssociation);
			
	}
	
	public static void remapTreeUsingList(TreeNode<TreeNodeDataType> treeNode, List<MappingAssociation> mappingAssociation) {
		for(MappingAssociation mapp : mappingAssociation) {
			if(mapp.getFirstTreeNode() == treeNode)
				treeNode.getData().addMappedWithTreeNode(mapp.getSecondTreeNode());
		}
		
		for(TreeNode<TreeNodeDataType> node : treeNode.getChildren())
			remapTreeUsingList(node, mappingAssociation);
		
	}
	
	public static String getTreeFullPath(TreeNode<TreeNodeDataType> treeNode, TreeNode<TreeNodeDataType> fullTree, String whichTree) {
	
		String fullPath = getTreeFullPathAux(treeNode, fullTree, "", false);

		//TODO verify for PATH
		if(whichTree.equals("secondTree"))
			fullPath = fullPath.split("\\.", 4)[3];
		else if(whichTree.equals("firstTree"))
			fullPath = fullPath.split("\\.", 5)[4];
		else
			fullPath = fullPath.split("\\.", 4)[3];
		
		return fullPath;
		
		
	}
	public static String getTreeFullPathAux(TreeNode<TreeNodeDataType> treeNode, TreeNode<TreeNodeDataType> fullTree, String path, boolean append) {
		
		if(fullTree == treeNode)
			return path+"."+treeNode.getData().getContent();

		for(TreeNode<TreeNodeDataType> node : fullTree.getChildren()) {
			if(node.getData().getType().equals(Utilities.getINITIATING_TYPE_TYPE()) || node.getData().getType().equals(Utilities.getNON_INITIATING_TYPE_TYPE()))
				append = true;
			
			String other;
			if(append)
				other = getTreeFullPathAux(treeNode, node, path+"."+fullTree.getData().getContent(), append);
			else
				other = getTreeFullPathAux(treeNode, node, "", append);
			
			if(other != null)
				return other;
		}
		
		return null;
		
	}
	
	//used for the right side (secondTree)
	public static List<String> getFullPathOfElementsMappedWith(TreeNode<TreeNodeDataType> fullFirstTree, TreeNode<TreeNodeDataType> fullFirstTreeNotEdit, TreeNode<TreeNodeDataType> treeNodeSecondTree) {
		List<String> listStr = new ArrayList<String>();
		
		for(int j=0; j<fullFirstTree.getData().getIsMappedWithTreeNode().size(); j++) {
			if(fullFirstTree.getData().getIsMappedWithTreeNode().get(j) == treeNodeSecondTree)
				listStr.add(getTreeFullPath(fullFirstTree, fullFirstTreeNotEdit, "secondTree"));
		}
		
		for(TreeNode<TreeNodeDataType> node : fullFirstTree.getChildren()) {
			listStr.addAll(getFullPathOfElementsMappedWith(node, fullFirstTreeNotEdit, treeNodeSecondTree));
		}
		
		
		return listStr;
	}
	
	//used for the left side (firstTree)
	public static List<String> getFullPathOfElementsMapped(TreeNode<TreeNodeDataType> fullSecondTreeNotEdit, TreeNode<TreeNodeDataType> treeNodeFirstTree, boolean outputDataTypeVisible) {
		outputDataTypeVisible = true;//for now is always visible
		List<String> listStr = new ArrayList<String>();
		
		for(int j=0; j<treeNodeFirstTree.getData().getIsMappedWithTreeNode().size(); j++) {
			String fullPath = getTreeFullPath(treeNodeFirstTree.getData().getIsMappedWithTreeNode().get(j), fullSecondTreeNotEdit, "firstTree");

			//if outputDataTypeVisible send the full path, else send the path without outputdatatype
			if(!outputDataTypeVisible && fullPath.contains(getOutputAddedType()))
				fullPath = fullPath.split("\\.", 2)[1];

			listStr.add(fullPath);
		}
		
		
		return listStr;
	}
	
	
	public static List<String> getSecurityNamesFromSecurityFile(byte[] securityFile) throws SAXException, IOException, ParserConfigurationException {
		List<String> toReturn = new ArrayList<String>();
		
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(securityFile));
		NodeList elements = ((org.w3c.dom.Document) d).getElementsByTagName("customParametersNames");

		for (int i = 0; i < elements.getLength(); i++) {
			toReturn.add(elements.item(i).getTextContent());
		}
		
		return toReturn;
	}
	
	public static boolean isAllSubTypesMapped(TreeNode<TreeNodeDataType> treeNode, boolean skip) {
		boolean isAllMapped = true;
		
		if(skip) {
			skip = false;
		}
		else {
			if(treeNode.getData().getComplexOrSimpleType().equals("complexType") && isAllSubTypesMapped(treeNode, true))//recursion, may be inner complex type
				isAllMapped = true;
			else if(treeNode.getData().getTransformationRule() != null)
				isAllMapped = true;
			else if(treeNode.getData().getMinOccurrences().equals("zero"))
				isAllMapped = true;
			else if(treeNode.getData().getIsMappedWithTreeNode().isEmpty())
				isAllMapped = false;
		}
		
		
		for(TreeNode<TreeNodeDataType> node : treeNode.getChildren()) {
			isAllMapped = isAllMapped && isAllSubTypesMapped(node, skip);
		}
		
		return isAllMapped;
	}


	
	public static String parseTransformationRule(String transformationRule) {
		String tRule = transformationRule;
		
		int pos1=0;
		int pos2=0;
		boolean found = false;
		
		int wordLength = tRule.length();
		for(int i=0; i<wordLength; i++) {
			if(tRule.charAt(i) == '"') {
				if(!found) {
					pos1=i;
					found = true;
				}
				else {
					pos2=i;
					
					String replaced = parseTransformationRuleHelper(tRule, pos1, pos2);
					String aux1 = tRule.substring(0, pos1);
					String aux2 = tRule.substring(pos2, tRule.length());
					tRule = aux1 + replaced + aux2;

					//update indexes
					int dist = tRule.length()-wordLength;
					i += dist;
					wordLength = tRule.length();
					
					found = false;
				}
			}
		}
		
		
		return tRule;
	}

	public static String parseTransformationRuleHelper(String transformationRule, int pos1, int pos2) {
		String tRule = transformationRule.substring(pos1, pos2);

		tRule = tRule.replaceAll("\\+", "\\\\\\\\+");
		
		return tRule;
	}
	
	
	
	public static List<String> getFullPathOfRequestElements(TreeNode<TreeNodeDataType> fullFirstTree, TreeNode<TreeNodeDataType> fullFirstTreeNotEdit, boolean skip) {
		List<String> listStr = new ArrayList<String>();
		
		if(!skip && fullFirstTree.getData().getComplexOrSimpleType().equals("simpleType"))
			listStr.add(getTreeFullPath(fullFirstTree, fullFirstTreeNotEdit, "requestTree"));
			
		for(TreeNode<TreeNodeDataType> node : fullFirstTree.getChildren()) {
			listStr.addAll(getFullPathOfRequestElements(node, fullFirstTreeNotEdit, false));
		}
		
		
		return listStr;
	}


	
}
