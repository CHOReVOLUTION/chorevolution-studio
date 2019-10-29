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
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import eu.chorevolution.idm.common.to.ChoreographyTO;
import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePlugin;
import eu.chorevolution.studio.eclipse.core.internal.project.ChorevolutionProjectUtils;
import eu.chorevolution.studio.eclipse.core.internal.project.model.impl.ChorevolutionSynthesisProjectStructureFolder;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPreferenceData;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServicesURIPrefs;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionSynthesisSourceModelPrefs;
import eu.chorevolution.studio.eclipse.core.utils.syncope.ApacheSyncopeUtilities;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIMessages;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;
import eu.chorevolution.studio.eclipse.ui.handlers.dialogs.DownloadChoreographyFromSyncopeDialog;
import eu.chorevolution.studio.eclipse.ui.handlers.dialogs.EnactChoreographyDeploymentDescriptorDialog;

/**
 * This action toggles the selected project's Chorevolution project nature.
 * 
 */
public class DownloadChoreographyFromSyncope extends AbstractHandler {

	private static final String CHORSPEC_FILE_EXTENSION = ".xml";
	private static final String CHOREOGRAPHY_FILE_EXTENSION = ".bpmn2";
	private static final String MESSAGES_FILE_NAME = "types";	
	private static final String MESSAGES_FILE_EXTENSION = ".xsd";
	
	@Override
	public Object execute(ExecutionEvent event) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();

		Iterator iterator = selection.iterator();

		while (iterator.hasNext()) {

			Object obj = iterator.next();
			if (obj instanceof IJavaProject) {
				obj = ((IJavaProject) obj).getProject();
			}

			if (obj instanceof IProject && ((IProject) obj).isOpen()) {
				IProject project = (IProject) obj;
	
                
				DownloadChoreographyFromSyncopeDialog downloadChoreographyFromSyncopeDialog = new DownloadChoreographyFromSyncopeDialog(ChorevolutionUIPlugin.getActiveWorkbenchShell(), project);
				downloadChoreographyFromSyncopeDialog.create();
            
            
				if (downloadChoreographyFromSyncopeDialog.open() == IDialogConstants.OK_ID) {

					ApacheSyncopeUtilities apacheSyncopeUtilities = new ApacheSyncopeUtilities(
	                		downloadChoreographyFromSyncopeDialog.getServiceInventoryURL(),
	                		downloadChoreographyFromSyncopeDialog.getServiceInventoryUsername(),
	                		downloadChoreographyFromSyncopeDialog.getServiceInventoryPassword(),
	                		downloadChoreographyFromSyncopeDialog.getServiceInventoryDomain());
					
					ChoreographyTO choreographyToImport = apacheSyncopeUtilities.getChoreographyByKey(downloadChoreographyFromSyncopeDialog.getSelectedChoreography().getKey());
					
					//chorspec download
					ChorevolutionSynthesisSourceModelPrefs chorevolutionSynthesisSourceModelPrefs = new ChorevolutionSynthesisSourceModelPrefs();
					Map<String, ChorevolutionPreferenceData> propertyValues = chorevolutionSynthesisSourceModelPrefs.readProjectOrWorkspacePreferences(project);
					
					
					byte[] chorSpec = choreographyToImport.getChorspec();
					String chorspecFileName = downloadChoreographyFromSyncopeDialog.getSelectedChoreography().getName()+CHORSPEC_FILE_EXTENSION;
					IPath chorspecFilePath = project.getLocation().makeAbsolute().append(propertyValues.get(ChorevolutionSynthesisSourceModelPrefs.PREF_CHOREOGRAPHYDEPLOYMENT).getValue()).addTrailingSeparator().append(chorspecFileName);
					try {
						FileUtils.writeByteArrayToFile(new File(chorspecFilePath.toString()), chorSpec);						
                		MessageDialog.openInformation(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
        					"Choreography Import",
        					"Choreography correctly imported.");
						
					} catch (Exception e) {
						MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
	        					"Choreography Import Error", "Error in the creation of file.");
					}
					
					//choreography download
					byte[] choreographyDiagram = choreographyToImport.getDiagram();
					String choreographyFileName = downloadChoreographyFromSyncopeDialog.getSelectedChoreography().getName()+CHOREOGRAPHY_FILE_EXTENSION;
					IPath choreographyFilePath = project.getLocation().makeAbsolute().append(ChorevolutionSynthesisProjectStructureFolder.CHOREOGRAPHY_DIAGRAMS_FOLDER_NAME).addTrailingSeparator().append(choreographyFileName);
					try {
						FileUtils.writeByteArrayToFile(new File(choreographyFilePath.toString()), choreographyDiagram);	
					} catch (Exception e) {
						MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
	        					"Choreography Diagram Import Error", "Error in the creation of file.");
					}
					
					// types download
					byte[] messagesDiagram = choreographyToImport.getMessages();
					String messagesFileName = MESSAGES_FILE_NAME+MESSAGES_FILE_EXTENSION;
					IPath messagesFilePath = project.getLocation().makeAbsolute().append(ChorevolutionSynthesisProjectStructureFolder.CHOREOGRAPHY_DIAGRAMS_FOLDER_NAME).addTrailingSeparator().append(messagesFileName);
					try {
						FileUtils.writeByteArrayToFile(new File(messagesFilePath.toString()), messagesDiagram);	
					} catch (Exception e) {
						MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
	        					"Messages Import Error", "Error in the creation of file.");
					}
					
					try {
						project.refreshLocal(IResource.DEPTH_INFINITE, null);
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
            
            }

		}
		return null;
	}

}
