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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePlugin;
import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePreferences;
import eu.chorevolution.studio.eclipse.core.internal.project.model.impl.ChorevolutionSynthesisProjectStructureFolder;
import eu.chorevolution.studio.eclipse.core.utils.WSDLData;
import eu.chorevolution.studio.eclipse.core.utils.bpmn.BPMNUtil;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIMessages;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.GeneratedArtifactSynthesisProcessor;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.ServiceSelection;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.rest.AdapterGeneratorRest;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.rest.BindingComponentGeneratorRest;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.rest.ChoreographyArchitectureGeneratorRest;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.rest.ChoreographyProjectionRest;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.rest.ChoreographyValidatorRest;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.rest.CoordinationDelegateGenerationRest;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.rest.SecurityFilterGeneratorRest;
import eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor.rest.SynthesisProcessorAccessControlRest;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.ChorevolutionSynthesisProcessorWizard;

public class ChorevolutionSynthesisProcessor extends AbstractHandler {
	private static final String XSD_FILE_NAME = "types";
	private static final String XSD_FILE_EXTENSION = "xsd";
	private static final String CHOREOGRAPHY_FILE_EXTENSION = ".bpmn2";

	private byte[] typesXSDByteArray;
	private IFile bpmnFile;
	private IProject project;
	private ChorevolutionSynthesisProcessorWizard generateServiceRoleBindingsWizard;
	private String choreographyName;
	private ChoreographyValidatorRest choreographyValdiator;

	private List<WSDLData> wsdlData;
	private GeneratedArtifactSynthesisProcessor generatedArtifactSynthesisProcessor;

	@Override
	public Object execute(ExecutionEvent event) {

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();

		Iterator iterator = selection.iterator();

		this.project = null;
		this.bpmnFile = null;
		this.wsdlData = new ArrayList<WSDLData>();
		this.generatedArtifactSynthesisProcessor = new GeneratedArtifactSynthesisProcessor();

		// get Services folder root. The while iterate in only one element
		// (services folder)
		while (iterator.hasNext()) {
			// get service folder
			bpmnFile = (IFile) iterator.next();
			project = bpmnFile.getProject();
		}

		// i can have only 1 choreography diagram per project
		IPath path = project.getLocation()
				.append(ChorevolutionSynthesisProjectStructureFolder.CHOREOGRAPHY_DIAGRAMS_FOLDER_NAME);

		if (searchChoreographyFileNumber(path.toString()) > 1) {
			MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
					ChorevolutionUIMessages.ChorevolutionSynthesisProcessor_title,
					ChorevolutionUIMessages.ChorevolutionSynthesisProcessorWizard_notOneChoreographyDiagram);
			return null;
		}

		ChorevolutionCorePreferences prefs = ChorevolutionCorePreferences.getProjectOrWorkspacePreferences(project,
				ChorevolutionCorePlugin.PLUGIN_ID);

		// get types.xsd
		IPath typesXSDPath = bpmnFile.getRawLocation().makeAbsolute().removeFileExtension().removeLastSegments(1)
				.append(XSD_FILE_NAME).addFileExtension(XSD_FILE_EXTENSION);
		File typesXSDFile = typesXSDPath.toFile();
		typesXSDByteArray = null;

		this.choreographyName = project.getName();// bpmnFile.getName().replace("." + bpmnFile.getFileExtension(), "");

		try {
			if (typesXSDFile.exists()) {
				typesXSDByteArray = FileUtils.readFileToByteArray(typesXSDFile);
			}
		} catch (Exception e) {
			MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
					ChorevolutionUIMessages.ChorevolutionSynthesisProcessor_title,
					NLS.bind(ChorevolutionUIMessages.ChorevolutionSynthesisProcessorWizard_openError, e.getMessage()));
		}

		loadSynthesisProcessor();

		return null;
	}

	private void loadSynthesisProcessor() {
		Job loadSynthesisProcessor = new Job("Choreography Validation") {
			protected IStatus run(IProgressMonitor monitor) {

				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}

				SubMonitor subMonitor = SubMonitor.convert(monitor, 4);

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						subMonitor.setTaskName("Validating the Choreography...");
						subMonitor.worked(1);
					}
				});

				try {
					// executes login
					SynthesisProcessorAccessControlRest spacr = new SynthesisProcessorAccessControlRest(project);

					if (!spacr.executeLogin()) {
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
					choreographyValdiator = new ChoreographyValidatorRest(project, choreographyName,
							FileUtils.readFileToByteArray(bpmnFile.getRawLocation().makeAbsolute().toFile()),
							typesXSDByteArray);

					// TODO manage the response
					if (!choreographyValdiator.validateBpmn2ChoreographyDiagram()) {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
										ChorevolutionUIMessages.Validation_Information,
										NLS.bind(ChorevolutionUIMessages.Vaidation_bpmnChoreographyDiagramError,
												bpmnFile.getName(), choreographyValdiator.getErrors()));
							}
						});
						return Status.CANCEL_STATUS;
					}

					subMonitor.worked(1);

					// open synthesis processor wizard
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							try {
								generateServiceRoleBindingsWizard = new ChorevolutionSynthesisProcessorWizard(project,
										BPMNUtil.loadBPMNModel(bpmnFile), bpmnFile, typesXSDByteArray);
								WizardDialog dialog = new WizardDialog(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
										generateServiceRoleBindingsWizard);
								dialog.create();

								int results = dialog.open();
								if (results == IDialogConstants.OK_ID || results == IDialogConstants.FINISH_ID) {
									executeSynthesisProcessor();
								}
							} catch (Exception e) {
								MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
										ChorevolutionUIMessages.ChorevolutionSynthesisProcessor_title,
										NLS.bind(
												ChorevolutionUIMessages.ChorevolutionSynthesisProcessorWizard_openError,
												e.getMessage()));
							}

						}
					});

					subMonitor.worked(1);

				} catch (Exception e) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
									ChorevolutionUIMessages.ChorevolutionSynthesisProcessor_title,
									NLS.bind(ChorevolutionUIMessages.ChorevolutionSynthesisProcessorWizard_openError,
											e.getMessage()));
						}
					});
				}

				return Status.OK_STATUS;

			}
		};
		loadSynthesisProcessor.setUser(true);
		loadSynthesisProcessor.schedule();
	}

	private void executeSynthesisProcessor() {
		Job executeSynthesisProcessor = new Job("Synthesis Processor") {
			protected IStatus run(IProgressMonitor monitor) {

				// this check is necessary to prevent the iteration on all
				// remaining resources if the monitor is cancelled
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}

				SubMonitor subMonitor = SubMonitor.convert(monitor, 6);

				// store all projection and populate WSDL data
				try {
					ChoreographyProjectionRest choreographyProjection = new ChoreographyProjectionRest(
							generateServiceRoleBindingsWizard.getProsumerServiceTableViewerRecords(),
							generateServiceRoleBindingsWizard.getClientServiceTableViewerRecords(),
							generateServiceRoleBindingsWizard.getCorrelationChoreographyTasks(), project, bpmnFile,
							typesXSDByteArray, subMonitor.split(1));

					choreographyProjection.storeProjections();
					wsdlData.addAll(choreographyProjection.getWsdlDatas());

				} catch (Exception e) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
									ChorevolutionUIMessages.TransformatorORGeneator_Error,
									NLS.bind(ChorevolutionUIMessages.Bpmn2ChoreographyProjector_projectionError,
											e.getMessage()));
						}
					});

					ChorevolutionUIPlugin.log(NLS.bind(
							ChorevolutionUIMessages.Bpmn2ChoreographyProjector_projectionError, e.getMessage()), e);
					monitor.setCanceled(true);
				} finally {
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
				}

				// TODO implement an algorithm do better use the getWsdlDatas and decomment the
				// relatives commented lines

				List<WSDLData> providerData = null;

				// store Provider Services retrieved from the inventory
				try {
					ServiceSelection serviceSelection = new ServiceSelection(
							generateServiceRoleBindingsWizard.getProviderServiceTableViewerRecords(),
							generatedArtifactSynthesisProcessor, project, subMonitor.split(1));
					serviceSelection.storeServiceProviders();
					// wsdlData.addAll(serviceSelection.getWsdlDatas());//i do it later, maybe it
					// needs to be adapted
					providerData = serviceSelection.getWsdlDatas();
				} catch (Exception e) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
									ChorevolutionUIMessages.TransformatorORGeneator_Error,
									NLS.bind(ChorevolutionUIMessages.ServiceInventory_servicesCreationError,
											e.getMessage()));
						}
					});

					ChorevolutionUIPlugin.log(
							NLS.bind(ChorevolutionUIMessages.ServiceInventory_servicesCreationError, e.getMessage()),
							e);
					monitor.setCanceled(true);
				} finally {

					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
				}

				// TODO remove this when you add the bindingComponent
				// subMonitor.worked(1);
				// store binding components

				List<WSDLData> bindingData = null;

				try {
					BindingComponentGeneratorRest bindingComponentGenerator = new BindingComponentGeneratorRest(
							choreographyName, generateServiceRoleBindingsWizard.getProviderServiceTableViewerRecords(),
							generateServiceRoleBindingsWizard.generateBindingComponentForClientParticipant(),
							generateServiceRoleBindingsWizard.getClientServiceTableViewerRecords(),
							generatedArtifactSynthesisProcessor, project, providerData, wsdlData, subMonitor.split(1));

					bindingComponentGenerator.storeBindingComponents();
					// wsdlData.addAll(bindingComponentGenerator.getWsdlDatas());//maybe they need
					// to be adapted
					bindingData = bindingComponentGenerator.getWsdlDatas();

				} catch (Exception e) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
									ChorevolutionUIMessages.TransformatorORGeneator_Error,
									NLS.bind(ChorevolutionUIMessages.BindingComponents_generatorError, e.getMessage()));
						}
					});

					ChorevolutionUIPlugin
							.log(NLS.bind(ChorevolutionUIMessages.BindingComponents_generatorError, e.getMessage()), e);
					monitor.setCanceled(true);
				} finally {
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
				}

				// Generates the adapter
				try {
					AdapterGeneratorRest adapterGenerator = new AdapterGeneratorRest(project, choreographyName,
							FileUtils.readFileToByteArray(bpmnFile.getRawLocation().makeAbsolute().toFile()),
							typesXSDByteArray, generateServiceRoleBindingsWizard.getProviderServiceTableViewerRecords(),
							generateServiceRoleBindingsWizard.getClientServiceTableViewerRecords(),
							generateServiceRoleBindingsWizard.generateBindingComponentForClientParticipant(),
							bindingData, providerData,
							generateServiceRoleBindingsWizard.getServiceToBeMappedTableViewerRecords(),
							generatedArtifactSynthesisProcessor,
							generateServiceRoleBindingsWizard.getAdapterModelsToBeCreated(), subMonitor.split(1));

					// returns all the WSDL, adapted or not, to be passed to the cdGenerator
					wsdlData.addAll(adapterGenerator.generateAndStoreAdapters());
				} catch (Exception e) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
									ChorevolutionUIMessages.TransformatorORGeneator_Error,
									NLS.bind(ChorevolutionUIMessages.Adapter_generatorError, e.getMessage()));
						}
					});

					ChorevolutionUIPlugin.log(NLS.bind(ChorevolutionUIMessages.Adapter_generatorError, e.getMessage()),
							e);
					monitor.setCanceled(true);
				} finally {
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
				}

				// store security filters
				try {
					SecurityFilterGeneratorRest securityFilterGenerator = new SecurityFilterGeneratorRest(
							choreographyName, generateServiceRoleBindingsWizard.getProviderServiceTableViewerRecords(),
							generateServiceRoleBindingsWizard.getClientServiceTableViewerRecords(),
							generateServiceRoleBindingsWizard.getSecurityFilterInformation(),
							generatedArtifactSynthesisProcessor, project, subMonitor.split(1));

					securityFilterGenerator.storeSecurityFilters();

				} catch (Exception e) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
									ChorevolutionUIMessages.TransformatorORGeneator_Error,
									NLS.bind(ChorevolutionUIMessages.SecurityFilters_generatorError, e.getMessage()));
						}
					});

					ChorevolutionUIPlugin
							.log(NLS.bind(ChorevolutionUIMessages.SecurityFilters_generatorError, e.getMessage()), e);
					monitor.setCanceled(true);
				} finally {
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
				}

				// store coordination delegate
				try {
					CoordinationDelegateGenerationRest coordinationDelegateGeneration = new CoordinationDelegateGenerationRest(
							choreographyName, generateServiceRoleBindingsWizard.getProsumerServiceTableViewerRecords(),
							generateServiceRoleBindingsWizard.getClientServiceTableViewerRecords(), wsdlData,
							generateServiceRoleBindingsWizard.getCorrelationChoreographyTasks(),
							generatedArtifactSynthesisProcessor, project, subMonitor.split(1));

					coordinationDelegateGeneration.storeCoordinatioDelegates();

				} catch (Exception e) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
									ChorevolutionUIMessages.TransformatorORGeneator_Error,
									NLS.bind(ChorevolutionUIMessages.CoordinationDelegate_generatorError,
											e.getMessage()));
						}
					});

					ChorevolutionUIPlugin.log(
							NLS.bind(ChorevolutionUIMessages.CoordinationDelegate_generatorError, e.getMessage()), e);
					monitor.setCanceled(true);
				} finally {
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
				}

				// generate choreography Architecture
				try {
					ChoreographyArchitectureGeneratorRest choreographyArchitectureGenerator = new ChoreographyArchitectureGeneratorRest(
							generatedArtifactSynthesisProcessor, bpmnFile, project, subMonitor.split(1));

					choreographyArchitectureGenerator.storeChoreographyArchitecture();

				} catch (Exception e) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
									ChorevolutionUIMessages.TransformatorORGeneator_Error,
									NLS.bind(ChorevolutionUIMessages.Transformator_bpmn2chorarchError,
											bpmnFile.getName(), e.getMessage()));
						}
					});

					ChorevolutionUIPlugin.log(NLS.bind(ChorevolutionUIMessages.Transformator_bpmn2chorarchError,
							bpmnFile.getName(), e.getMessage()), e);
					monitor.setCanceled(true);
				}
				return Status.OK_STATUS;
			}
		};
		executeSynthesisProcessor.setUser(true);
		executeSynthesisProcessor.schedule();
	}

	public int searchChoreographyFileNumber(String dirName) {
		File dir = new File(dirName);
		int counter = 0;
		for (File file : dir.listFiles()) {
			if (file.getName().endsWith((CHOREOGRAPHY_FILE_EXTENSION))) {
				counter++;
			}
		}
		return counter;
	}

}
