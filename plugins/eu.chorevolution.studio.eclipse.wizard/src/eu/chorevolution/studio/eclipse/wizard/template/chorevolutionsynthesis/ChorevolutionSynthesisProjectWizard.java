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
package eu.chorevolution.studio.eclipse.wizard.template.chorevolutionsynthesis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePlugin;
import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePreferences;
import eu.chorevolution.studio.eclipse.core.internal.project.ChorevolutionProjectUtils;
import eu.chorevolution.studio.eclipse.core.internal.project.ChorevolutionSynthesisProjectNature;
import eu.chorevolution.studio.eclipse.core.internal.project.model.impl.ChorevolutionSynthesisProjectStructureFolder;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPreferenceData;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServicesURIPrefs;
import eu.chorevolution.studio.eclipse.core.utils.syncope.SynthesisProcessor;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;
import eu.chorevolution.studio.eclipse.wizard.ChorevolutionWizardPlugin;
import eu.chorevolution.studio.eclipse.wizard.template.Utilities;

public class ChorevolutionSynthesisProjectWizard extends Wizard implements INewWizard, IExecutableExtension {
	private static final String WIZARD_NAME = ChorevolutionSynthesisProjectWizardMessages.NewProject_windowTitle;

	private static final String CHOREOGRAPHY_FILE_EXTENSION = "bpmn2";
	private static final String MESSAGES_FILE_NAME = "types";
	private static final String MESSAGES_FILE_EXTENSION = "xsd";

	private ChorevolutionSynthesisProjectWizardMainPage mainPage;
	private ChorevolutionSynthesisProjectWizardStructurePage structurePage;
	private ISelection selection;
	private IConfigurationElement configurationElement;

	/**
	 * Constructor for ChorevolutionProjectWizard.
	 */
	public ChorevolutionSynthesisProjectWizard() {
		setWindowTitle(WIZARD_NAME);
		setNeedsProgressMonitor(true);
	}

	/**
	 * We will accept the selection in the workbench to see if we can initialize
	 * from it.
	 * 
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	/**
	 * Adding the pages to the wizard.
	 */
	@Override
	public void addPages() {
		mainPage = new ChorevolutionSynthesisProjectWizardMainPage(selection);
		addPage(mainPage);

		structurePage = new ChorevolutionSynthesisProjectWizardStructurePage(selection);
		addPage(structurePage);
	}

	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		// TODO help
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(getShell(),
		// IHelpContexts.New_File_Wizard);
	}

	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {
		configurationElement = config;
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We will
	 * create an operation and run it using wizard as execution context.
	 */
	@Override
	public boolean performFinish() {
		String projectName = this.mainPage.getProjectName();
		String choreographyNamespace = mainPage.getChoreographyNamespace();
		URI location = null;

		if (mainPage.getSelectedSynthesis() == null && !mainPage.isSynthesisProcessorEmbeddedMode()) {
			// a synthesis processor needs to be selected
			mainPage.setPageComplete(false);
			mainPage.setErrorMessage(
					ChorevolutionSynthesisProjectWizardMessages.NewProject_synthesisProcessorChoiceError);
			structurePage.setErrorMessage(
					ChorevolutionSynthesisProjectWizardMessages.NewProject_synthesisProcessorChoiceError);
			return false;
		}

		if (!this.mainPage.useDefaults()) {
			location = mainPage.getLocationURI();
		}
		List<ChorevolutionPreferenceData> preferences = new ArrayList<ChorevolutionPreferenceData>();
		preferences.addAll(mainPage.getSynthesisProcessorURIs());
		preferences.addAll(structurePage.getProjectSettings());

		IStatus projectStatus = ChorevolutionProjectUtils.createProjectWithNature(projectName,
				ChorevolutionCorePlugin.SYNTHESIS_NATURE_ID, preferences, location, structurePage.getStructureFolders(),
				new NullProgressMonitor());
		if (!projectStatus.isOK()) {

			MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
					"Error during the Project Creation", projectStatus.getMessage());
			return false;

		}

		// create property to set the embedded of rest use of the synthesis processor
		// and configure BPMN2 nature to the created project
		for (IProject project : ChorevolutionProjectUtils.getChorevolutionProjects()) {
			if (project.getName().equals(projectName)) {
				// create property to set the embedded of rest use of the synthesis processor
				ChorevolutionCorePreferences prefs = ChorevolutionCorePreferences
						.getProjectOrWorkspacePreferences(project, ChorevolutionCorePlugin.PLUGIN_ID);
				prefs.putString(ChorevolutionSynthesisProjectNature.PREF_SYNTHESIS_PROCESSOR_RUN_EMBEDDED,
						String.valueOf(mainPage.isSynthesisProcessorEmbeddedMode()));

				if (!mainPage.isSynthesisProcessorEmbeddedMode()) {
					SynthesisProcessor synthesisProcessor = mainPage.getSelectedSynthesis();
					prefs.putString(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_NAME,
							synthesisProcessor.getName());
					prefs.putString(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_URI,
							synthesisProcessor.getLocation());
					prefs.putString(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_KEY,
							synthesisProcessor.getKey());
					prefs.putString(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_LOGIN_URI,
							synthesisProcessor.getLoginUri());
					prefs.putString(
							ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_BPMN2_CHOREOGRAPHY_PROJECTOR_URI,
							synthesisProcessor.getProjectorUri());
					prefs.putString(
							ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_BPMN2_CHOREOGRAPHY_VALIDATOR_URI,
							synthesisProcessor.getValidatorUri());
					prefs.putString(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_CD_GENERATOR_URI,
							synthesisProcessor.getCdGeneratorUri());
					prefs.putString(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_BC_GENERATOR_URI,
							synthesisProcessor.getBcGeneratorUri());
					prefs.putString(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_SF_GENERATOR_URI,
							synthesisProcessor.getSfGeneratorUri());
					prefs.putString(
							ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_CHOREOGRAPHY_ARCHITECTURE_GENERATOR_URI,
							synthesisProcessor.getArchitectureUri());
					prefs.putString(
							ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_CHOREOGRAPHY_DEPLOYMENT_DESCRIPTION_GENERATOR_URI,
							synthesisProcessor.getDeploymentUri());
				}

				// enable chor extension
				ChorevolutionCorePreferences bpmnPrefs = ChorevolutionCorePreferences
						.getProjectOrWorkspacePreferences(project, "org.eclipse.bpmn2.modeler.core");
				bpmnPrefs.putStringWithoutNamespace("check.project.nature", "true");
				bpmnPrefs.putStringWithoutNamespace("eclipse.preferences.version", "1");
				
				// show the display monitor
				ChorevolutionCorePreferences displayMonitorPrefs = ChorevolutionCorePreferences
						.getProjectOrWorkspacePreferences(null, "org.eclipse.ui.workbench", true);
				displayMonitorPrefs.putStringWithoutNamespace("PLUGINS_NOT_ACTIVATED_ON_STARTUP",
						";org.eclipse.m2e.discovery;");
				displayMonitorPrefs.putStringWithoutNamespace("RUN_IN_BACKGROUND", "false");
				displayMonitorPrefs.putStringWithoutNamespace("eclipse.preferences.version", "1");

				// i need to create the choreography file
				ChorevolutionServicesURIPrefs chorevolutionServicesURIPrefs = new ChorevolutionServicesURIPrefs();
				Map<String, ChorevolutionPreferenceData> propertyValues = chorevolutionServicesURIPrefs
						.readProjectOrWorkspacePreferences(project);

				IPath choreographyDiagramPath = project.getProjectRelativePath().append(project.getName())
						.addTrailingSeparator()
						.append(ChorevolutionSynthesisProjectStructureFolder.CHOREOGRAPHY_DIAGRAMS_FOLDER_NAME)
						.addTrailingSeparator()
						.append(propertyValues.get(ChorevolutionServicesURIPrefs.PREF_CHOREOGRAPHY_NAME).getValue())
						.addFileExtension(CHOREOGRAPHY_FILE_EXTENSION);
				IPath typesDiagramPath = project.getLocation().makeAbsolute()
						.append(ChorevolutionSynthesisProjectStructureFolder.CHOREOGRAPHY_DIAGRAMS_FOLDER_NAME)
						.addTrailingSeparator().append(MESSAGES_FILE_NAME).addFileExtension(MESSAGES_FILE_EXTENSION);

				File typesFile = new File(typesDiagramPath.toString());
				typesFile.getParentFile().mkdirs();

				try {
					// now i open relatives files and edit them
					IPath choreographyAbsoluteDiagramPath = project.getLocation()
							.append(ChorevolutionSynthesisProjectStructureFolder.CHOREOGRAPHY_DIAGRAMS_FOLDER_NAME)
							.addTrailingSeparator()
							.append(propertyValues.get(ChorevolutionServicesURIPrefs.PREF_CHOREOGRAPHY_NAME).getValue())
							.addFileExtension(CHOREOGRAPHY_FILE_EXTENSION);

					URL url = FileLocator.find(ChorevolutionWizardPlugin.getDefault().getBundle(),
							new Path("ChoreographyTemplate.tpl"), null);
					url = FileLocator.toFileURL(url);
					File fileToRead = URIUtil.toFile(URIUtil.toURI(url));

					Scanner input = new Scanner(fileToRead);
					BufferedWriter writer = new BufferedWriter(
							new FileWriter(choreographyAbsoluteDiagramPath.toOSString()));
					String line = null;

					while (input.hasNextLine()) {
						line = input.nextLine();
						line = line.replaceAll("%typesnamespace%", choreographyNamespace + "/types");
						line = line.replaceAll("%targetnamespace%", choreographyNamespace);
						writer.write(line);
						writer.newLine();
					}

					input.close();
					writer.close();

					PrintWriter typesWriter = new PrintWriter(typesDiagramPath.toOSString(), "UTF-8");
					typesWriter.println("<xsd:schema xmlns=\"" + choreographyNamespace + "/types"
							+ "\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:tns=\"" + choreographyNamespace
							+ "/types" + "\" targetNamespace=\"" + choreographyNamespace + "\">");
					typesWriter.println("</xsd:schema>");
					typesWriter.close();

				} catch (Exception e) {
					MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
							"Error during the Choreography Diagram Creation", projectStatus.getMessage());
				}

				// configure BPMN2 project nature
				Utilities.configureBPMN2Nature(project, true);
			}
		}

		// now the wizard automatically opens the correct perspective
		BasicNewProjectResourceWizard.updatePerspective(configurationElement);

		return true;
	}

}