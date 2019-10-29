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
package eu.chorevolution.studio.eclipse.wizard.template.chorevolutionservicething;

import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import eu.chorevolution.modelingnotations.servicething.InterfaceDescription;
import eu.chorevolution.modelingnotations.servicething.ServiceThingModel;
import eu.chorevolution.modelingnotations.servicething.ServicethingFactory;
import eu.chorevolution.modelingnotations.servicething.impl.ServicethingFactoryImpl;
import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePlugin;
import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePreferences;
import eu.chorevolution.studio.eclipse.core.ChorevolutionCoreUtils;
import eu.chorevolution.studio.eclipse.core.internal.project.ChorevolutionProjectUtils;
import eu.chorevolution.studio.eclipse.core.utils.syncope.InterfaceDescriptionType;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;


public class ChorevolutionServiceThingProjectWizard extends Wizard implements INewWizard, IExecutableExtension  {
	private static final String WIZARD_NAME = ChorevolutionServiceThingProjectWizardMessages.NewProject_windowTitle;

	private ChorevolutionServiceThingProjectWizardMainPage mainPage;
	private ChorevolutionServiceThingProjectWizardStructurePage structurePage;
	private ISelection selection;
	private IConfigurationElement configurationElement;

	/**
	 * Constructor for ChorevolutionProjectWizard.
	 */
	public ChorevolutionServiceThingProjectWizard() {
		setWindowTitle(WIZARD_NAME);
		setNeedsProgressMonitor(true);
	}

	/**
	 * We will accept the selection in the workbench to see if we can initialize from it.
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
		mainPage = new ChorevolutionServiceThingProjectWizardMainPage(selection);
		addPage(mainPage);

		structurePage = new ChorevolutionServiceThingProjectWizardStructurePage(selection);
		addPage(structurePage);
	}

	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		// TODO help
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(getShell(), IHelpContexts.New_File_Wizard);
	}

	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		configurationElement = config;
	}
	
	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We will create an operation and run it using wizard as execution context.
	 */
	@Override
	public boolean performFinish() {
		String projectName = this.mainPage.getProjectName();
		URI location = null;
		if (!this.mainPage.useDefaults()) {
			location = mainPage.getLocationURI();
		}

		
		IStatus projectStatus = ChorevolutionProjectUtils.createProjectWithNature(projectName, ChorevolutionCorePlugin.SERVICE_THING_NATURE_ID, structurePage.getProjectSettings(), location, structurePage.getStructureFolders(), new NullProgressMonitor());
		if(!projectStatus.isOK()) {
			
			MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
					"Error during the Project Creation",
					projectStatus.getMessage());
			return false;
			
		}
		
		//add a default Service/thing model
		ServiceThingModel serviceThingModel = ServicethingFactory.eINSTANCE.createServiceThingModel();
		serviceThingModel.setName(projectName);
		
        //show the display monitor
    	ChorevolutionCorePreferences displayMonitorPrefs = ChorevolutionCorePreferences.getProjectOrWorkspacePreferences(null, "org.eclipse.ui.workbench", true);	
    	displayMonitorPrefs.putStringWithoutNamespace("PLUGINS_NOT_ACTIVATED_ON_STARTUP", ";org.eclipse.m2e.discovery;");
    	displayMonitorPrefs.putStringWithoutNamespace("RUN_IN_BACKGROUND", "false");
    	displayMonitorPrefs.putStringWithoutNamespace("eclipse.preferences.version", "1");
		

		InterfaceDescription interfaceDescription = null;
		if (structurePage.getServiceInterfaceDescription() == InterfaceDescriptionType.WSDL){
			interfaceDescription = ServicethingFactory.eINSTANCE.createWSDLModel();
		}else if (structurePage.getServiceInterfaceDescription() == InterfaceDescriptionType.WADL){
			interfaceDescription = ServicethingFactory.eINSTANCE.createWADLModel();
		}else if (structurePage.getServiceInterfaceDescription() == InterfaceDescriptionType.GIDL){
			interfaceDescription = ServicethingFactory.eINSTANCE.createCOAPModel();
		} 
		
		serviceThingModel.setInterfaceDescription(interfaceDescription);
		
		IFile modelFile  = ChorevolutionCorePlugin.getWorkspace().getRoot().getProject(projectName).getFile(new Path(serviceThingModel.getName()).addFileExtension("servicething"));
		
		ChorevolutionCoreUtils.saveEObject(serviceThingModel, modelFile);
		
		// now the wizard automatically opens the correct perspective
		BasicNewProjectResourceWizard.updatePerspective(configurationElement);

		return true;
	}
	
	
	

}