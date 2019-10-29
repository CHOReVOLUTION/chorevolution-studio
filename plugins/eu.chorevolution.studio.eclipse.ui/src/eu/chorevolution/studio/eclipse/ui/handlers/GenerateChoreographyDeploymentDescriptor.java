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

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePlugin;
import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePreferences;
import eu.chorevolution.studio.eclipse.core.internal.project.ChorevolutionProjectUtils;
import eu.chorevolution.studio.eclipse.core.internal.project.ChorevolutionSynthesisProjectNature;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIMessages;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.ChoreographyDeploymentDescriptorGenerator;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.rest.ChoreographyDeploymentDescriptorGeneratorRest;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.rest.SynthesisProcessorAccessControlRest;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.ChoreographyDeploymentDescriptorJDKWizard;

public class GenerateChoreographyDeploymentDescriptor extends AbstractHandler {

	private WizardDialog jRESelectorDialog;
	private boolean execCorrect;


	@Override
	public Object execute(ExecutionEvent event) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();

		Iterator iterator = selection.iterator();
		
		while (iterator.hasNext()) {
			IFile file = (IFile) iterator.next();
			if (ChorevolutionProjectUtils.isChorevolutionProject(file)) {
				IPath location = file.getLocation();
				if (location != null) {
					executeChoreographyDeploymentDescriptorGenerator(file);
				}
			}

		}

		return null;
	}

	private void executeChoreographyDeploymentDescriptorGenerator(IFile choreographyArchitectureFile) {
		Job executeSynthesisProcessor = new Job("Choreography Deployment Descriptor Generator") {
			protected IStatus run(IProgressMonitor monitor) {
				// this check is necessary to prevent the iteration on all
				// remaining resources if the monitor is cancelled
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
				//SubMonitor subMonitor = SubMonitor.convert(monitor, 1);
					
				do {
					try {
						String choreographyName = choreographyArchitectureFile.getProject().getName();
						ChorevolutionCorePreferences prefs = ChorevolutionCorePreferences.getProjectOrWorkspacePreferences(
								choreographyArchitectureFile.getProject(), ChorevolutionCorePlugin.PLUGIN_ID);

						ChoreographyDeploymentDescriptorGeneratorRest choreographyDeploymentDescriptorGenerator = null;
							
						SynthesisProcessorAccessControlRest spacr= new SynthesisProcessorAccessControlRest(choreographyArchitectureFile.getProject());
						
						try {
							if(!spacr.executeLogin()) {
								MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
										"Login incorrect",
										"The username or the password of the Synthesis Processor are incorrect");
								return null;			
							}
						} catch (Exception e) {
							MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
									"Unable to do Login",
									e.toString());
						}
						
						choreographyDeploymentDescriptorGenerator = new ChoreographyDeploymentDescriptorGeneratorRest(
								choreographyName, choreographyArchitectureFile.getProject(),
								choreographyArchitectureFile, monitor);

						execCorrect = true;
						choreographyDeploymentDescriptorGenerator.storeChoreographyDeploymentDescriptor();	
					}
					catch (Exception e) {
						//if is not selected a JDK
						if( (e instanceof CoreException) && (((CoreException) e).getStatus().getMessage().equals("not_a_JDK")) ) {
							Display.getDefault().syncExec(new Runnable() {
								public void run() {
									jRESelectorDialog = new WizardDialog(ChorevolutionUIPlugin.getActiveWorkbenchShell(), new ChoreographyDeploymentDescriptorJDKWizard());
									jRESelectorDialog.open();
									execCorrect=false;
								}
							});
							
						} else {
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
											ChorevolutionUIMessages.TransformatorORGeneator_Error,
											NLS.bind(ChorevolutionUIMessages.Transformator_chorarch2choreospecError,
												choreographyArchitectureFile.getName(), e.getMessage()));
								}
							});
	
							ChorevolutionUIPlugin.log(NLS.bind(ChorevolutionUIMessages.Transformator_chorarch2choreospecError,
									choreographyArchitectureFile.getName(), e.getMessage()), e);
							monitor.setCanceled(true);
							
						}
							

					} finally {
						if (monitor.isCanceled()) {
							return Status.CANCEL_STATUS;
						}
					}
				} while((!execCorrect)&&(jRESelectorDialog.getReturnCode()==0));
				return Status.OK_STATUS;
			}
		};
		executeSynthesisProcessor.setUser(true);
		executeSynthesisProcessor.schedule();
	}
	
	

}
