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
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPreferenceData;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIMessages;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.composite.NonSoapProviderParticipantsTableViewerComposite;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.ParticipantTableViewerRecord;
 
public class BindingComponentsWizardPage extends WizardPage {
    private static final String SOAP_CLIENT_APPLICATION = "SOAP";
    private static final String REST_CLIENT_APPLICATION = "REST";
    
    private Map<String, ChorevolutionPreferenceData> propertyValues;

    //private Button clientRequireBindingComponentYesButton;
    //private Button clientRequireBindingComponentNoButton;
    private Image synthesisProcessorImageStep;
    private String[] clientApplicationItems;
    private Combo clientApplicationCombo;
    private boolean generateBindingComponentForClientParticipant;
    
    private NonSoapProviderParticipantsTableViewerComposite nonSoapProviderServiceComposite;
    

    public BindingComponentsWizardPage(List<ParticipantTableViewerRecord> providerParticipants) {
        super(ChorevolutionUIMessages.BindingComponentsWizardPage_name);
        setTitle(ChorevolutionUIMessages.BindingComponentsWizardPage_title);
        setMessage(ChorevolutionUIMessages.BindingComponentsWizardPage_description, IMessageProvider.INFORMATION);
        this.synthesisProcessorImageStep = ChorevolutionUIPlugin.getImageDescriptor("icons/full/wizban/synthesis_processor/bindingComponentsWizardPage.png").createImage();
        this.nonSoapProviderServiceComposite = new NonSoapProviderParticipantsTableViewerComposite(this, providerParticipants);
        this.clientApplicationItems = new String[2];
        this.clientApplicationItems[0]=REST_CLIENT_APPLICATION;
        this.clientApplicationItems[1]=SOAP_CLIENT_APPLICATION;
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
		
        // create secured provider
        this.nonSoapProviderServiceComposite.createPartControl(compositeLeft);
             
        Composite groupSecuredChoreography = new Composite(compositeLeft, SWT.NONE);
        GridLayout layoutSecuredChoreography = new GridLayout();
        layoutSecuredChoreography.marginHeight = 3;
        layoutSecuredChoreography.marginWidth = 3;
        layoutSecuredChoreography.numColumns = 3;
        layoutSecuredChoreography.horizontalSpacing = 8;
        groupSecuredChoreography.setLayout(layoutSecuredChoreography);
        groupSecuredChoreography.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

        Label securedChoreographyLabel = new Label(groupSecuredChoreography, SWT.NONE);
        securedChoreographyLabel.setText("Interaction paradigm of the Client participant:");

        clientApplicationCombo = new Combo(groupSecuredChoreography, SWT.READ_ONLY);
        clientApplicationCombo.setItems(clientApplicationItems);
        clientApplicationCombo.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
              if (clientApplicationCombo.getText().equals(SOAP_CLIENT_APPLICATION)) {
            	  generateBindingComponentForClientParticipant = false;
              } else {
            	  generateBindingComponentForClientParticipant = true;
              }
            }
          });
        // select rest protocol
        clientApplicationCombo.select(0);
        generateBindingComponentForClientParticipant = true;
        
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
				}
				
			}
		});
    }
    

    public void refreshNonSoapParticipants() {
        nonSoapProviderServiceComposite.refreshNonSoapParticipants();
    }

    public boolean generateBindingComponentForClientParticipant(){
    	return generateBindingComponentForClientParticipant;
    }
    public List<ParticipantTableViewerRecord> getNonSoapProviderServiceComposite(){
    	return nonSoapProviderServiceComposite.getServiceTableViewerRecords();
    }
    
    
    public boolean validatePage() {
        return true;
    }

}