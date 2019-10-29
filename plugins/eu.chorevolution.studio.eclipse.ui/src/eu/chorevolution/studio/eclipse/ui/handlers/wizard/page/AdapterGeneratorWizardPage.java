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
package eu.chorevolution.studio.eclipse.ui.handlers.wizard.page;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.Choreography;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIMessages;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;
import eu.chorevolution.studio.eclipse.ui.Utilities;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.composite.RolesToAdaptTableViewerComposite;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.AdapterModelDataType;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.ParticipantTableViewerRecord;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.RolesViewerRecord;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.ValidElementsForParticipant;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.WSDLOrGIDLParticipantsData;

public class AdapterGeneratorWizardPage extends WizardPage {
    	private IProject project;
        private RolesToAdaptTableViewerComposite rolesToAdaptComposite;
        private Image synthesisProcessorImageStep;
        private List<ParticipantTableViewerRecord> providerParticipants;
        private List<Choreography> choreographies;
        private IFile bpmnFile;
        private byte[] typesXSD;
        private List<WSDLOrGIDLParticipantsData> errorsToBeMapped;
        private List<ValidElementsForParticipant> validElementsForParticipant;
    
    public AdapterGeneratorWizardPage(IProject project, List<ParticipantTableViewerRecord> providerParticipants, List<Choreography> choreographies, IFile bpmnFile, byte[] typesXSD) {
        super(ChorevolutionUIMessages.AdapterGeneratorWizardPage_name);
        setTitle(ChorevolutionUIMessages.AdapterGeneratorWizardPage_title);
        setMessage(ChorevolutionUIMessages.AdapterGeneratorWizardPage_description, IMessageProvider.INFORMATION);
        this.project = project;
        this.synthesisProcessorImageStep = ChorevolutionUIPlugin.getImageDescriptor("icons/full/wizban/synthesis_processor/adapterGeneratorWizardPage.png").createImage();
        this.rolesToAdaptComposite = new RolesToAdaptTableViewerComposite(this, null, choreographies, bpmnFile, providerParticipants, typesXSD);
        this.errorsToBeMapped = null;
        this.validElementsForParticipant = null;
        
        this.providerParticipants = providerParticipants;
        this.choreographies = choreographies;
        this.bpmnFile = bpmnFile;
        this.typesXSD = typesXSD;
    }

	@Override
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 3;
        layout.marginWidth = 3;
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = false;
        composite.setLayout(layout);
        setControl(composite);
        	
        Composite compositeLeft = new Composite(composite, SWT.NONE);
		GridLayout layoutLeft = new GridLayout();
		layoutLeft.marginHeight = 3;
		layoutLeft.marginWidth = 3;
		compositeLeft.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		compositeLeft.setLayout(layoutLeft);
		
		
        rolesToAdaptComposite.createPartControl(compositeLeft);

		
        // set the image of the process
        GridData imageGridData = new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1);
        imageGridData.minimumWidth=360;
		imageGridData.minimumHeight=401;
		imageGridData.widthHint=360;
		imageGridData.heightHint=401;
		final Canvas canvas = new Canvas(composite, SWT.PUSH);
		canvas.setLayoutData(imageGridData);
		canvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				if (synthesisProcessorImageStep != null) {
					e.gc.drawImage(synthesisProcessorImageStep, 0, 20);
					
					//this will be called on the page opening
			        List<RolesViewerRecord> isCorrectWSDLOrGIDL = isWSDLOrGIDLCorrect();
			        if(isCorrectWSDLOrGIDL == null) {
			        	//malformed file, deny the user to proceed
			        }
			        else if(isCorrectWSDLOrGIDL.isEmpty()) {
			        	//ok, skip this page or print a continue message
			        }
			        else {
			        	//show the errors
			        	rolesToAdaptComposite.setRolesToAdapt(isCorrectWSDLOrGIDL);			        	
			        	rolesToAdaptComposite.refreshRoleToAdapt(errorsToBeMapped, validElementsForParticipant);//data needed to open the mapper
			        	isAdapterCreationFinished();
			        	getWizard().getContainer().updateButtons();
			        }
				}
				
			}
		});

        setPageComplete(validatePage());
        
    }
    
	public boolean isAdapterCreationFinished() {
		return rolesToAdaptComposite.isAdapterCreationFinished();
	}
	
    public IProject getProject() {
        return project;
    }
    
    @Override
    public boolean canFlipToNextPage() {
    	return isPageComplete();
    }

    public boolean validatePage() {    	
    	return true;
    }

    @Override
    public boolean isPageComplete() {
    	if(!isAdapterCreationFinished()) {
    		setErrorMessage("Please create all the Adapter(s) before proceeding.");
    		return false;
    	}
    	else {
            setMessage(ChorevolutionUIMessages.AdapterGeneratorWizardPage_description, IMessageProvider.INFORMATION);
    		return true;
    	}
    }
 
    public RolesToAdaptTableViewerComposite getProviderServiceComposite() {
        return rolesToAdaptComposite;
    }
    
    public List<AdapterModelDataType> getAdapterModelsToBeCreated(){
		return rolesToAdaptComposite.getAdapterModelsToBeCreated();
	}
    
    public List<RolesViewerRecord> isWSDLOrGIDLCorrect() {
    	
		List<WSDLOrGIDLParticipantsData> validFile = null;
		
		List<WSDLOrGIDLParticipantsData> allErrorsToBeMapped = new ArrayList<WSDLOrGIDLParticipantsData>();
		List<WSDLOrGIDLParticipantsData> allErrorsToBeMappedWithDuplicates = new ArrayList<WSDLOrGIDLParticipantsData>();
		
		List<ValidElementsForParticipant> allValidElementsForParticipant = new ArrayList<ValidElementsForParticipant>();
		List<RolesViewerRecord> rolesToView= new ArrayList<RolesViewerRecord>();
		
		//I have to verify Before if the gidl or wsdl is correct, then merge the participants and invert them
    	for(ParticipantTableViewerRecord providerParticipant : providerParticipants) {
    		try{
	    		
    			ValidElementsForParticipant validElement = null;
    			
	    		if(Utilities.isWSDL(providerParticipant.getService().getInterfaceDescriptionContent())) {
	    			validFile= Utilities.isValidWSDL(providerParticipant.getService().getInterfaceDescriptionContent(), choreographies, bpmnFile, providerParticipant.getParticipant());
	    			validElement = new ValidElementsForParticipant(providerParticipant.getParticipant(), Utilities.getTaskNamesFromWSDL(providerParticipant.getService().getInterfaceDescriptionContent()), Utilities.getMessagesNamesFromWSDL(providerParticipant.getService().getInterfaceDescriptionContent()), Utilities.getMessagesTypesFromWSDL(providerParticipant.getService().getInterfaceDescriptionContent()));
	    		}
	    		else if(Utilities.isGIDL(providerParticipant.getService().getInterfaceDescriptionContent())) {
	    			validFile = Utilities.isValidGIDL(providerParticipant.getService().getInterfaceDescriptionContent(), choreographies, bpmnFile, providerParticipant.getParticipant());	    			
	    			validElement = new ValidElementsForParticipant(providerParticipant.getParticipant(), Utilities.getTaskNamesFromGIDL(providerParticipant.getService().getInterfaceDescriptionContent()), Utilities.getMessagesNamesFromGIDL(providerParticipant.getService().getInterfaceDescriptionContent()), Utilities.getMessagesTypesFromGIDL(providerParticipant.getService().getInterfaceDescriptionContent()));
	    		}
	    		else {
	    			//malformed file - not a gidl or wsdl
	    			validElement = new ValidElementsForParticipant(providerParticipant.getParticipant(), null, null, null);
	    			return null;
	    		}
	    		
	    		allValidElementsForParticipant.add(validElement);

	    		for(WSDLOrGIDLParticipantsData error : validFile) {
	    			boolean found = false;
	    			for(WSDLOrGIDLParticipantsData errorToBeMapped : allErrorsToBeMapped) {//remove duplicates
	    				if((errorToBeMapped.getParticipantName().equals(error.getParticipantName()))) {
	    					if((errorToBeMapped.getProviderParticipantName().equals(error.getProviderParticipantName()))) {
	    						if(errorToBeMapped.getTaskName().equals(error.getTaskName())) {
	    							found = true;
	    							continue;
	    						}
	    					}
	    				}
	    			}
	    			
	    			if(!found) {
	    				allErrorsToBeMapped.add(error);
	    			}
    				allErrorsToBeMappedWithDuplicates.add(error);//i need this to be passed to the mapper modal

	    		}	

    		}
    		catch(Exception e) {
    			//TODO
    		}

    	}
    	
    	//i initialize HERE the variable to be passed to the modal editor
    	this.errorsToBeMapped = allErrorsToBeMappedWithDuplicates;
    	this.validElementsForParticipant = allValidElementsForParticipant;
    	
		List<String> alreadyInsertedFirstColumn = new ArrayList<String>();
		for(WSDLOrGIDLParticipantsData errorToBeMapped : allErrorsToBeMapped) {

			boolean alreadyInserted = false;
			
			for(String alreadyInsertedIterator : alreadyInsertedFirstColumn) {
				if(errorToBeMapped.getParticipantName().equals(alreadyInsertedIterator)) {//already visited
					alreadyInserted = true;
					break;
				}
			}
			
			if(!alreadyInserted) {

				//insert header row
				rolesToView.add(new RolesViewerRecord(errorToBeMapped.getParticipantName(), "", "", "", "", "", ""));
		    	
				for(WSDLOrGIDLParticipantsData innerErrorToBeMapped : allErrorsToBeMapped) {
					if(innerErrorToBeMapped.getParticipantName().equals(errorToBeMapped.getParticipantName())) {
				    	for(ParticipantTableViewerRecord providerParticipant : providerParticipants) {//i have to get service data
				    		if(providerParticipant.getParticipant().equals(innerErrorToBeMapped.getProviderParticipantName())) {
			    				rolesToView.add(new RolesViewerRecord("", innerErrorToBeMapped.getProviderParticipantName(), providerParticipant.getService().getKey(), innerErrorToBeMapped.getTaskName(), innerErrorToBeMapped.getTaskID(), providerParticipant.getService().getName(), providerParticipant.getService().getLocation()));
				    			break;//i don't need this for anymore
				    		}
				    	}
					}
				}
				alreadyInsertedFirstColumn.add(errorToBeMapped.getParticipantName());
			}
			
		}
    	
    	return rolesToView;
	}
    
    

}
