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
package eu.chorevolution.studio.eclipse.ui.handlers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Map;

import org.apache.syncope.common.lib.to.GroupTO;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import eu.chorevolution.studio.eclipse.core.internal.project.ChorevolutionProjectUtils;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPreferenceData;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServicesURIPrefs;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionSynthesisSourceModelPrefs;
import eu.chorevolution.studio.eclipse.core.utils.syncope.ApacheSyncopeUtilities;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIMessages;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;
import eu.chorevolution.studio.eclipse.ui.handlers.dialogs.DeployAndEnactChoreographyDeploymentDescriptorDialog;
import eu.chorevolution.studio.eclipse.ui.handlers.dialogs.EnactChoreographyDeploymentDescriptorDialog;

public class EnactChoreographyDeploymentDescriptor extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();

		Iterator iterator = selection.iterator();
		
		while (iterator.hasNext()) {
			IFile file = (IFile) iterator.next();
            if (ChorevolutionProjectUtils.isChorevolutionProject(file)) {
                IProject project = file.getProject();
                
                                
                EnactChoreographyDeploymentDescriptorDialog enactChoreographyDeploymentDescriptorDialog = new EnactChoreographyDeploymentDescriptorDialog(ChorevolutionUIPlugin.getActiveWorkbenchShell(), project);
                enactChoreographyDeploymentDescriptorDialog.create();
                
                if (enactChoreographyDeploymentDescriptorDialog.open() == IDialogConstants.OK_ID) {
                ApacheSyncopeUtilities apacheSyncopeUtilities = new ApacheSyncopeUtilities(
                			enactChoreographyDeploymentDescriptorDialog.getServiceInventoryURL(),
                			enactChoreographyDeploymentDescriptorDialog.getServiceInventoryUsername(),
                			enactChoreographyDeploymentDescriptorDialog.getServiceInventoryPassword(),
                			enactChoreographyDeploymentDescriptorDialog.getServiceInventoryDomain());
                					
			    			ChorevolutionSynthesisSourceModelPrefs chorevolutionSynthesisSourceModelPrefs;
			    			chorevolutionSynthesisSourceModelPrefs = new ChorevolutionSynthesisSourceModelPrefs();
			    			Map<String, ChorevolutionPreferenceData> projectOrWorkspacePreferencesValues = chorevolutionSynthesisSourceModelPrefs.readProjectOrWorkspacePreferences(project);
				
						    ChorevolutionServicesURIPrefs chorevolutionServicesURIPrefs;
						    chorevolutionServicesURIPrefs = new ChorevolutionServicesURIPrefs();
					        Map<String, ChorevolutionPreferenceData> projectOrWorkspacePreferencesURIValues = chorevolutionServicesURIPrefs.readProjectOrWorkspacePreferences(project);
                
							try {
								
								//it this fails, i need to deploy
								GroupTO chorExist = apacheSyncopeUtilities.getChoreographyByName(projectOrWorkspacePreferencesURIValues.get(chorevolutionServicesURIPrefs.PREF_CHOREOGRAPHY_NAME).getValue());

							}
							catch (Exception e) {

								MessageDialog.openInformation(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
										ChorevolutionUIMessages.DeployAndEnactment_Information,
										ChorevolutionUIMessages.Enact_choreographyDeploymentDescriptorInformationError);
								
								return new DeployAndEnactChoreographyDeploymentDescriptor().execute(event);
							}

						//i enact it
						apacheSyncopeUtilities.enactChoreography(projectOrWorkspacePreferencesURIValues.get(chorevolutionServicesURIPrefs.PREF_CHOREOGRAPHY_ID).getValue(), enactChoreographyDeploymentDescriptorDialog.getSelectedEnactment());
                	

                		MessageDialog.openInformation(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
        					ChorevolutionUIMessages.DeployAndEnactment_Information,
        					ChorevolutionUIMessages.Enact_choreographyDeploymentDescriptorInformation);
                	
                    
                } 
            }
		}
		
		return null;
	}

}