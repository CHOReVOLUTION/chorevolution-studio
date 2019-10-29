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
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.composite.ProviderParticipantsTableViewerComposite;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.ParticipantTableViewerRecord;

public class ProviderServiceRoleBindingsWizardPage extends WizardPage {
    	private IProject project;
        private ProviderParticipantsTableViewerComposite providerServiceComposite;
        private Image synthesisProcessorImageStep;
    
    public ProviderServiceRoleBindingsWizardPage(IProject project, List<ParticipantTableViewerRecord> providerParticipants) {
        super(ChorevolutionUIMessages.ProviderServiceRoleBindingsWizardPage_name);
        setTitle(ChorevolutionUIMessages.ProviderServiceRoleBindingsWizardPage_title);
        setMessage(ChorevolutionUIMessages.ProviderServiceRoleBindingsWizardPage_description, IMessageProvider.INFORMATION);
        this.project = project;
        this.synthesisProcessorImageStep = ChorevolutionUIPlugin.getImageDescriptor("icons/full/wizban/synthesis_processor/providerServiceRoleBindingsWizardPage.png").createImage();
        this.providerServiceComposite = new ProviderParticipantsTableViewerComposite(this, providerParticipants);
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
		
        // TODO GET First Choreography and check if is not empty
        this.providerServiceComposite.createPartControl(compositeLeft);
        
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
       
        setPageComplete(validatePage());
        
    }
    
    public IProject getProject() {
        return project;
    }
    
    @Override
    public boolean canFlipToNextPage() {
        return true;
        // return isPageComplete();
    }

    public boolean validatePage() {
        /*for (ParticipantTableViewerRecord element : providerServiceComposite.getServiceTableViewerRecords()) {
            if (element.isGenerate() && "".equals(element.getServiceProjectName())) {
                return false;
            } else if (!element.isGenerate() && element.getService() == null) {
                return false;

            }
        }
        */
        
        return true;
    }

 
    public ProviderParticipantsTableViewerComposite getProviderServiceComposite() {
        return providerServiceComposite;
    }
    

}
