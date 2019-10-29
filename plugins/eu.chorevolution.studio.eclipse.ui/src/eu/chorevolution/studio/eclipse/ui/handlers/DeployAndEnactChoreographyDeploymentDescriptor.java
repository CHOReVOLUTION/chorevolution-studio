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

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.eclipse.bpmn2.util.Bpmn2Resource;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePlugin;
import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePreferences;
import eu.chorevolution.studio.eclipse.core.internal.project.ChorevolutionProjectUtils;
import eu.chorevolution.studio.eclipse.core.internal.project.model.impl.ChorevolutionSynthesisProjectStructureFolder;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPreferenceData;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServicesURIPrefs;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionSynthesisSourceModelPrefs;
import eu.chorevolution.studio.eclipse.core.utils.bpmn.BPMNUtil;
import eu.chorevolution.studio.eclipse.core.utils.syncope.ApacheSyncopeUtilities;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIMessages;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;
import eu.chorevolution.studio.eclipse.ui.handlers.dialogs.DeployAndEnactChoreographyDeploymentDescriptorDialog;

public class DeployAndEnactChoreographyDeploymentDescriptor extends AbstractHandler {

	private static final String CHOREOGRAPHY_FILE_EXTENSION = ".bpmn2";
	private static final String XSD_FILE_NAME = "types";
	private static final String XSD_FILE_EXTENSION = "xsd";
	
	private boolean isError;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();

		Iterator iterator = selection.iterator();
		
		while (iterator.hasNext()) {
			Boolean enacted=false;
			IFile file = (IFile) iterator.next();
            if (ChorevolutionProjectUtils.isChorevolutionProject(file)) {
                IProject project = file.getProject();
                DeployAndEnactChoreographyDeploymentDescriptorDialog deployAndEnactChoreographyDeploymentDescriptorDialog = new DeployAndEnactChoreographyDeploymentDescriptorDialog(ChorevolutionUIPlugin.getActiveWorkbenchShell(), project);
                deployAndEnactChoreographyDeploymentDescriptorDialog.create();
                
                if (deployAndEnactChoreographyDeploymentDescriptorDialog.open() == IDialogConstants.OK_ID) {
                ApacheSyncopeUtilities apacheSyncopeUtilities = new ApacheSyncopeUtilities(
                			deployAndEnactChoreographyDeploymentDescriptorDialog.getServiceInventoryURL(),
                			deployAndEnactChoreographyDeploymentDescriptorDialog.getServiceInventoryUsername(),
                			deployAndEnactChoreographyDeploymentDescriptorDialog.getServiceInventoryPassword(),
                			deployAndEnactChoreographyDeploymentDescriptorDialog.getServiceInventoryDomain());
                		
						    ChorevolutionSynthesisSourceModelPrefs chorevolutionSynthesisSourceModelPrefs;
						    chorevolutionSynthesisSourceModelPrefs = new ChorevolutionSynthesisSourceModelPrefs();
					        Map<String, ChorevolutionPreferenceData> projectOrWorkspacePreferencesValues = chorevolutionSynthesisSourceModelPrefs.readProjectOrWorkspacePreferences(project);
			
						    ChorevolutionServicesURIPrefs chorevolutionServicesURIPrefs;
						    chorevolutionServicesURIPrefs = new ChorevolutionServicesURIPrefs();
					        Map<String, ChorevolutionPreferenceData> projectOrWorkspacePreferencesURIValues = chorevolutionServicesURIPrefs.readProjectOrWorkspacePreferences(project);
			
					        
			                try {
			                	
		                		isError=false;	    
			                	String chorDescription =  deployAndEnactChoreographyDeploymentDescriptorDialog.getDescription();
			                	
			                	//i should pass the bpmn2 choreography and types as parameters	
			                	IPath choreographyFolderLocation = project.getLocation().makeAbsolute().append(ChorevolutionSynthesisProjectStructureFolder.CHOREOGRAPHY_DIAGRAMS_FOLDER_NAME);
			                	String choreographyFileName = searchChoreographyFile(choreographyFolderLocation.toString());
			                	IPath typesFileName = choreographyFolderLocation.append(XSD_FILE_NAME).addFileExtension(XSD_FILE_EXTENSION);
			                	
			                	if(choreographyFileName == null || typesFileName == null) {
			                		throw new FileNotFoundException("");
			                	}

			                	//i have to verify if we need to upload image
			                	byte[] imageToUpload=null;
			                	if(deployAndEnactChoreographyDeploymentDescriptorDialog.isNeedToPassImage()) {
            		        		imageToUpload = IOUtils.toByteArray(new FileInputStream(new File(deployAndEnactChoreographyDeploymentDescriptorDialog.getChoreographyImageUrl())));
			                	}	
			                	
                				//i do this always (if chor not exist), i need to createChoreography      
                		        apacheSyncopeUtilities.createChoreography(project,  projectOrWorkspacePreferencesURIValues.get(chorevolutionServicesURIPrefs.PREF_CHOREOGRAPHY_NAME).getValue(), projectOrWorkspacePreferencesURIValues.get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_KEY).getValue(),
                		        		chorDescription,
                		        		IOUtils.toByteArray(new FileInputStream(file.getRawLocation().makeAbsolute().toFile())),
                		        		imageToUpload,
                		        		IOUtils.toByteArray(new FileInputStream(new File(choreographyFileName))),
                		        		IOUtils.toByteArray(new FileInputStream(new File(typesFileName.toString()))));
			                	
                		        //refresh preferences
    						    chorevolutionSynthesisSourceModelPrefs = new ChorevolutionSynthesisSourceModelPrefs();
    						    projectOrWorkspacePreferencesValues = chorevolutionSynthesisSourceModelPrefs.readProjectOrWorkspacePreferences(project);
    							
    						    chorevolutionServicesURIPrefs = new ChorevolutionServicesURIPrefs();
    						    projectOrWorkspacePreferencesURIValues = chorevolutionServicesURIPrefs.readProjectOrWorkspacePreferences(project);

                		        
								//now i have to verify if the user have chosen also the enact command, if true i enact chor passing name
								if(deployAndEnactChoreographyDeploymentDescriptorDialog.isNeedToEnact()) {
									enacted=true;
									apacheSyncopeUtilities.enactChoreography(projectOrWorkspacePreferencesURIValues.get(ChorevolutionServicesURIPrefs.PREF_CHOREOGRAPHY_ID).getValue(), deployAndEnactChoreographyDeploymentDescriptorDialog.getSelectedEnactment());
								}
								else {
									enacted=false;
								}
								
								
							} 
			                catch(FileNotFoundException e) {//file missing
								isError=true;
								MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
			        					ChorevolutionUIMessages.DeployAndEnactment_Error,
			        					NLS.bind(ChorevolutionUIMessages.EnactAndDeploy_choreographyDeploymentDescriptorError, file.getName(),
			        							"Choreography file or Types file not found. The files should be put in 'Choreography Diagram' directory." + e.getMessage()));
			                }
			                catch (Exception e) {//choreography already uploaded
								isError=true;
			                	if(enacted==true) {
			                		MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
			        					ChorevolutionUIMessages.DeployAndEnactment_Error,
			        					NLS.bind(ChorevolutionUIMessages.EnactAndDeploy_choreographyDeploymentDescriptorError, file.getName(),
												"Error in Choreography Upload or Enact."));
			                	}
			                	else {
			                		MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
			        					ChorevolutionUIMessages.DeployAndEnactment_Error,
			        					NLS.bind(ChorevolutionUIMessages.EnactAndDeploy_choreographyDeploymentDescriptorError, file.getName(),
												"Error in Choreography Upload."));
			                	}
							}
                	
			        if(!isError) {
	                	if(enacted==true) {
	                		MessageDialog.openInformation(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
	        					ChorevolutionUIMessages.DeployAndEnactment_Information,
	        					ChorevolutionUIMessages.EnactAndDeploy_choreographyDeploymentDescriptorInformation);
	                	}
	                	else {
	                		MessageDialog.openInformation(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
	        					ChorevolutionUIMessages.DeployAndEnactment_Information,
	        					ChorevolutionUIMessages.Deploy_choreographyDeploymentDescriptorInformation);
	                	}
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