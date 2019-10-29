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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePlugin;
import eu.chorevolution.studio.eclipse.core.internal.project.ChorevolutionProjectUtils;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIMessages;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;

/**
 * This action toggles the selected project's Chorevolution project nature.
 * 
 */
public class AddRemoveChorevolutionServiceThingNature extends AbstractHandler {

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
				if (ChorevolutionProjectUtils.isChorevolutionProject(project)) {
					IProgressMonitor pm = new NullProgressMonitor();
					try {
						ChorevolutionProjectUtils.removeProjectNature(project, ChorevolutionCorePlugin.SERVICE_THING_NATURE_ID, pm);
						// TODO not refresh icon nature
						project.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
					} catch (CoreException e) {
						MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(), ChorevolutionUIMessages.ProjectNature_errorMessage, NLS.bind(ChorevolutionUIMessages.ProjectNature_removeError, project.getName(), e.getLocalizedMessage()));
					}
				} else {
					IProgressMonitor pm = new NullProgressMonitor();
					try {
						ChorevolutionProjectUtils.removeProjectNature(project, ChorevolutionCorePlugin.SERVICE_THING_NATURE_ID, pm);
						ChorevolutionProjectUtils.addProjectNature(project, ChorevolutionCorePlugin.SERVICE_THING_NATURE_ID, pm);
						// TODO not refresh icon nature
						project.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
					} catch (CoreException e) {
						MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(), ChorevolutionUIMessages.ProjectNature_errorMessage, NLS.bind(ChorevolutionUIMessages.ProjectNature_addError, project.getName(), e.getLocalizedMessage()));
					}
				}
			}
		}
		return null;
	}

}
