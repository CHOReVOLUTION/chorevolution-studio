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
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePlugin;
import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePreferences;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIMessages;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.rest.ChoreographyValidatorRest;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.rest.SynthesisProcessorAccessControlRest;

public class ValidateBpmnChoreographyDiagram extends AbstractHandler {
	private static final String XSD_FILE_NAME = "types";
	private static final String XSD_FILE_EXTENSION = "xsd";

	private IFile file;
	private byte[] typesXSDByteArray;
	private File typesXSDFile;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();

		Iterator iterator = selection.iterator();

		while (iterator.hasNext()) {
			file = (IFile) iterator.next();
			try {
				IPath location = file.getLocation();

				// get types.xsd
				IPath typesXSDPath = file.getRawLocation().makeAbsolute().removeFileExtension().removeLastSegments(1)
						.append(XSD_FILE_NAME).addFileExtension(XSD_FILE_EXTENSION);
				typesXSDFile = typesXSDPath.toFile();
				typesXSDByteArray = null;

				if (location != null) {
					if (typesXSDFile.exists()) {
						typesXSDByteArray = FileUtils.readFileToByteArray(typesXSDFile);
					}
					
					loadChoreographyValidator();
					
				}

			} catch (Exception e) {
				MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
						ChorevolutionUIMessages.Validation_Information,
						NLS.bind(ChorevolutionUIMessages.Vaidation_bpmnChoreographyDiagramError, file.getName(),
								e.getMessage()));
				return null;
			}

			// get only one bpmn2 file
			return null;

		}

		return null;

	}
	
	
	private void loadChoreographyValidator() {
		Job loadChoreographyValidator = new Job("Choreography Validation") {
			protected IStatus run(IProgressMonitor monitor) {
				
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
				
				SubMonitor subMonitor = SubMonitor.convert(monitor, 3);
				
				Display.getDefault().asyncExec(new Runnable() {
				    public void run() {
		                subMonitor.setTaskName("Validating the Choreography...");
		                subMonitor.worked(1);
				    }
				});
				

				try {
					
					ChorevolutionCorePreferences prefs = ChorevolutionCorePreferences
							.getProjectOrWorkspacePreferences(file.getProject(), ChorevolutionCorePlugin.PLUGIN_ID);

					SynthesisProcessorAccessControlRest spacr= new SynthesisProcessorAccessControlRest(file.getProject());

					if(!spacr.executeLogin()) {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
										"Login incorrect",
										"The username or the password of the Synthesis Processor are incorrect");
							}
						});
						return Status.CANCEL_STATUS;			
					}

					
	                subMonitor.worked(1);
					
					// validate bpmn2 choreography
					ChoreographyValidatorRest choreographyValdiator = new ChoreographyValidatorRest(file.getProject(), file.getName(),
								FileUtils.readFileToByteArray(file.getRawLocation().makeAbsolute().toFile()),
								typesXSDByteArray);

					// TODO manage the response
					if (!choreographyValdiator.validateBpmn2ChoreographyDiagram()) {
						String validatorErrors = choreographyValdiator.getErrors();
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
										ChorevolutionUIMessages.Validation_Information,
										NLS.bind(ChorevolutionUIMessages.Vaidation_bpmnChoreographyDiagramError, file.getName(),
												validatorErrors));
							}
						});
						return Status.CANCEL_STATUS;
					}
					
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
								MessageDialog.openInformation(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
										ChorevolutionUIMessages.Validation_Information,
										ChorevolutionUIMessages.Vaidation_bpmnChoreographyDiagramInformation);
							}
						});

					subMonitor.worked(1);
					
				} catch (Exception e) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
									ChorevolutionUIMessages.ChorevolutionSynthesisProcessor_title,
									NLS.bind(ChorevolutionUIMessages.ChorevolutionValidator_openError, e.getMessage()));
						}
					});
				}
				
				
				return Status.OK_STATUS;
				
			}
		};
		loadChoreographyValidator.setUser(true);
		loadChoreographyValidator.schedule();
	}
	
	

	
	

}