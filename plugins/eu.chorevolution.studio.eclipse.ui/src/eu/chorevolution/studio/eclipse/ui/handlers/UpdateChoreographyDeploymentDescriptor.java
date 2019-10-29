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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.syncope.common.lib.to.GroupTO;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.validation.internal.util.Log;
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
import eu.chorevolution.studio.eclipse.ui.handlers.dialogs.UpdateChoreographyDeploymentDescriptorDialog;

public class UpdateChoreographyDeploymentDescriptor extends AbstractHandler {

	private static final String CHOREOGRAPHY_FOLDER_NAME = "Choreography Diagram";
	private static final String CHOREOGRAPHY_FILE_EXTENSION = ".bpmn2";
	private static final String XSD_FILE_NAME = "types";
	private static final String XSD_FILE_EXTENSION = "xsd";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();

		Iterator iterator = selection.iterator();
		
		while (iterator.hasNext()) {
			IFile file = (IFile) iterator.next();
            if (ChorevolutionProjectUtils.isChorevolutionProject(file)) {
                IProject project = file.getProject();
                                
                UpdateChoreographyDeploymentDescriptorDialog updateChoreographyDeploymentDescriptorDialog = new UpdateChoreographyDeploymentDescriptorDialog(ChorevolutionUIPlugin.getActiveWorkbenchShell(), project);
                updateChoreographyDeploymentDescriptorDialog.create();
                
			    ChorevolutionSynthesisSourceModelPrefs chorevolutionSynthesisSourceModelPrefs;
			    chorevolutionSynthesisSourceModelPrefs = new ChorevolutionSynthesisSourceModelPrefs();
		        Map<String, ChorevolutionPreferenceData> projectOrWorkspacePreferencesValues = chorevolutionSynthesisSourceModelPrefs.readProjectOrWorkspacePreferences(project);
                
			    ChorevolutionServicesURIPrefs chorevolutionServicesURIPrefs;
			    chorevolutionServicesURIPrefs = new ChorevolutionServicesURIPrefs();
		        Map<String, ChorevolutionPreferenceData> projectOrWorkspacePreferencesURIValues = chorevolutionServicesURIPrefs.readProjectOrWorkspacePreferences(project);
		        
                if (updateChoreographyDeploymentDescriptorDialog.open() == IDialogConstants.OK_ID) {
                ApacheSyncopeUtilities apacheSyncopeUtilities = new ApacheSyncopeUtilities(
                			updateChoreographyDeploymentDescriptorDialog.getServiceInventoryURL(),
                			updateChoreographyDeploymentDescriptorDialog.getServiceInventoryUsername(),
                			updateChoreographyDeploymentDescriptorDialog.getServiceInventoryPassword(),
                			updateChoreographyDeploymentDescriptorDialog.getServiceInventoryDomain());
                										
							
								
                			if(projectOrWorkspacePreferencesURIValues.get(chorevolutionServicesURIPrefs.PREF_CHOREOGRAPHY_ID).getValue().equals("null")) {
								//it this fails, i need to deploy

								MessageDialog.openInformation(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
										ChorevolutionUIMessages.DeployAndEnactment_Information,
										ChorevolutionUIMessages.Update_choreographyDeploymentDescriptorInformationError);
								
								return new DeployAndEnactChoreographyDeploymentDescriptor().execute(event);
							}

						//i update it
						try {
							
							/*
		                	//i should pass the bpmn2 choreography and types as parameters	
		    				IPath choreographyFolderLocation = project.getLocation().makeAbsolute().append(CHOREOGRAPHY_FOLDER_NAME);
		                	String choreographyFileName = searchChoreographyFile(choreographyFolderLocation.toString());
		    				
		                	IPath typesFileName = choreographyFolderLocation.append(XSD_FILE_NAME).addFileExtension(XSD_FILE_EXTENSION);
		                	if(choreographyFileName.equals(null) || typesFileName == null) {
		                		throw new FileNotFoundException("Choreography file or Types file not found. The files should be put in 'Choreography Diagrams' directory.");
		                	}
							*/
							//next commit
							apacheSyncopeUtilities.updateChoreography(projectOrWorkspacePreferencesURIValues.get(chorevolutionServicesURIPrefs.PREF_CHOREOGRAPHY_ID).getValue(), projectOrWorkspacePreferencesURIValues.get(chorevolutionServicesURIPrefs.PREF_CHOREOGRAPHY_NAME).getValue(), 
									IOUtils.toByteArray(new FileInputStream(file.getRawLocation().makeAbsolute().toFile())));
							
							MessageDialog.openInformation(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
									ChorevolutionUIMessages.DeployAndEnactment_Information,
									ChorevolutionUIMessages.Update_choreographyDeploymentDescriptorInformation);
							
						} catch (Exception e) {
								MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
			        					ChorevolutionUIMessages.DeployAndEnactment_Error,
										ChorevolutionUIMessages.Update_choreographyDeploymentDescriptorError);
			        					
						}

                    
                } 
            }
		}
		
		return null;
	}
	
	   public String searchChoreographyFile(String dirName){
		   File dir = new File(dirName);
		   for (File file : dir.listFiles()) {
		     if (file.getName().endsWith((CHOREOGRAPHY_FILE_EXTENSION))) {
		       return file.getAbsolutePath().toString();
		     }
		   }
		   return null;
	   }

}