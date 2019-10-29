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
package eu.chorevolution.studio.eclipse.wizard.template.bpmn;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.bpmn2.modeler.help.IHelpContexts;
import org.eclipse.bpmn2.modeler.ui.wizards.BPMN2DiagramCreator;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;

import eu.chorevolution.studio.eclipse.wizard.ChorevolutionWizardMessages;
import eu.chorevolution.studio.eclipse.wizard.ChorevolutionWizardPlugin;
import eu.chorevolution.studio.eclipse.wizard.template.Utilities;

public class BPMN2DiagramWizard extends Wizard implements INewWizard {
	private static final String WIZARD_NAME = BPMN2DiagramWizardMessages.BPMN2DiagramWizardPage_windowTitle;

	private BPMN2DiagramWizardPage page;
	private ISelection selection;

	/**
	 * Constructor for BPMN2DiagramWizard.
	 */
	public BPMN2DiagramWizard() {
		setWindowTitle(WIZARD_NAME);
		setNeedsProgressMonitor(true);
	}

	/**
	 * Adding the page to the wizard.
	 */

	@Override
	public void addPages() {
		page = new BPMN2DiagramWizardPage(selection);
		addPage(page);
	}

	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getShell(), IHelpContexts.New_File_Wizard);
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We will create an operation and run it using wizard as execution context.
	 */
	@Override
	public boolean performFinish() {
		final String fileName = page.getFileName();
		final IResource container = page.getDiagramContainer();
		final String targetNamespace = page.getTargetNamespace();

		IRunnableWithProgress op = new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					IPath path = container.getFullPath().append(fileName);
					URI uri = URI.createPlatformResourceURI(path.toString(), true);
					BPMN2DiagramCreator.createDiagram(uri, page.getDiagramType(), targetNamespace);

					// configure BPMN2 project nature
					Utilities.configureBPMN2Nature(page.getFileContainer().getProject(), true);

				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(ChorevolutionWizardPlugin.getActiveWorkbenchShell(), ChorevolutionWizardMessages.BPMN2DiagramWizard_errorMessage, NLS.bind(ChorevolutionWizardMessages.BPMN2DiagramWizard_createError, fileName, realException.getMessage()));
			return false;
		}
		return true;
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

}
