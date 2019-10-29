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
package eu.chorevolution.studio.eclipse.core.internal.project;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePlugin;
import eu.chorevolution.studio.eclipse.core.ChorevolutionCoreUtils;
import eu.chorevolution.studio.eclipse.core.internal.project.model.ChorevolutionStructureFolder;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPreferenceData;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPrefs;

public class ChorevolutionProjectUtils {

	/**
	 * Adds given nature as first nature to specified project.
	 */
	public static void addProjectNature(IProject iProject, String nature, IProgressMonitor monitor)
			throws CoreException {
		if (iProject != null && nature != null) {
			if (!iProject.hasNature(nature)) {
				IProjectDescription desc = iProject.getDescription();
				String[] oldNatures = desc.getNatureIds();
				String[] newNatures = new String[oldNatures.length + 1];
				newNatures[0] = nature;
				if (oldNatures.length > 0) {
					System.arraycopy(oldNatures, 0, newNatures, 1, oldNatures.length);
				}
				desc.setNatureIds(newNatures);
				iProject.setDescription(desc, monitor);
			}
		}

	}

	/**
	 * Removes given nature from specified project.
	 */
	public static void removeProjectNature(IProject project, String nature, IProgressMonitor monitor)
			throws CoreException {
		if (project != null && nature != null) {
			if (project.exists() && project.hasNature(nature)) {

				// first remove all problem markers (including the inherited
				// ones) from Chorevolution project
				if (nature.equals(ChorevolutionCorePlugin.SYNTHESIS_NATURE_ID)
						|| nature.equals(ChorevolutionCorePlugin.SERVICE_THING_NATURE_ID)) {
					project.deleteMarkers(ChorevolutionCoreUtils.MARKER_ID, true, IResource.DEPTH_INFINITE);
				}

				// now remove project nature
				IProjectDescription desc = project.getDescription();
				String[] oldNatures = desc.getNatureIds();
				String[] newNatures = new String[oldNatures.length - 1];
				int newIndex = oldNatures.length - 2;
				for (int i = oldNatures.length - 1; i >= 0; i--) {
					if (!oldNatures[i].equals(nature)) {
						newNatures[newIndex--] = oldNatures[i];
					}
				}
				desc.setNatureIds(newNatures);
				project.setDescription(desc, monitor);
			}
		}
	}

	/**
	 * Returns a list of all projects with the Chorevolution project nature.
	 */
	public static Set<IProject> getChorevolutionProjects() {
		Set<IProject> projects = new LinkedHashSet<IProject>();
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			if (ChorevolutionProjectUtils.isChorevolutionProject(project)) {
				projects.add(project);
			}
		}
		return projects;
	}

	/**
	 * Returns true if given resource's project is a Chorevolution project.
	 */
	public static boolean isChorevolutionProject(IResource resource) {
		return ChorevolutionCoreUtils.hasNature(resource, ChorevolutionCorePlugin.SYNTHESIS_NATURE_ID)
				|| ChorevolutionCoreUtils.hasNature(resource, ChorevolutionCorePlugin.SERVICE_THING_NATURE_ID);
	}

	public static boolean isChorevolutionSynthesisProjectNature(IResource resource) {
		return ChorevolutionCoreUtils.hasNature(resource, ChorevolutionCorePlugin.SYNTHESIS_NATURE_ID);
	}

	public static boolean isChorevolutionServiceThingProjectNature(IResource resource) {
		return ChorevolutionCoreUtils.hasNature(resource, ChorevolutionCorePlugin.SERVICE_THING_NATURE_ID);
	}

	public static IStatus createProjectWithNature(String projectName, String projectNature,
			List<ChorevolutionPreferenceData> projectSettings, URI location,
			ChorevolutionStructureFolder chorevolutionStructureFolder, IProgressMonitor monitor) {

		IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		// if project exists not, then create one
		if (!newProject.exists()) {
			URI projectLocation = location;
			// Set description
			IProjectDescription desc = newProject.getWorkspace().newProjectDescription(newProject.getName());
			if (location != null && ResourcesPlugin.getWorkspace().getRoot().getLocationURI().equals(location)) {
				projectLocation = null;
			}
			desc.setLocationURI(projectLocation);
			try {
				newProject.create(desc, null);
				if (!newProject.isOpen()) {
					newProject.open(null);
				}
			} catch (CoreException e) {
				return new Status(IStatus.ERROR, ChorevolutionCorePlugin.PLUGIN_ID,
						NLS.bind(ChorevolutionProjectMessage.Project_createProjectError, newProject.getName(),
								e.getLocalizedMessage()));
			}
		}
		// TODO use monitor ??
		try {
			addProjectNature(newProject, projectNature, new NullProgressMonitor());
		} catch (CoreException e) {
			return new Status(IStatus.ERROR, ChorevolutionCorePlugin.PLUGIN_ID,
					NLS.bind(ChorevolutionProjectMessage.ProjectNature_addProjectNatureError, newProject.getName(),
							e.getLocalizedMessage()));
		}

		try {
			// create chorevolution project structure
			chorevolutionStructureFolder.createChorevolutionProjectStructure(newProject);

			// create preferences into the project
			ChorevolutionPrefs.setProjectOrWorkspacePreferences(projectSettings, newProject);

		} catch (CoreException e) {
			return new Status(IStatus.ERROR, ChorevolutionCorePlugin.PLUGIN_ID,
					NLS.bind(ChorevolutionProjectMessage.ProjectStructure_createProjectStructureError,
							newProject.getName(), e.getLocalizedMessage()));
		}

		return new Status(IStatus.OK, ChorevolutionCorePlugin.PLUGIN_ID,
				NLS.bind(ChorevolutionProjectMessage.Project_createProject, newProject.getFullPath()));
	}

}
