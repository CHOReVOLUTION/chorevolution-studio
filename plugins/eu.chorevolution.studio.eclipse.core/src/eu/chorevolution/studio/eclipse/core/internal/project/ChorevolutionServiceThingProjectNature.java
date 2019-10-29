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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;


public class ChorevolutionServiceThingProjectNature implements IProjectNature {

	private IProject project;

	/**
	 * Constructor needed for <code>IProject.getNature()</code> and
	 * <code>IProject.addNature()</code>.
	 *
	 * @see #setProject(IProject)
	 */
	public ChorevolutionServiceThingProjectNature() {
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	public IProject getProject() {
		return project;
	}

	/**
	 * Adds Chorevolution builder to project's list of external builders.
	 */
	public void configure() throws CoreException {
		// TODO for now not implements the building of the chorevolutin project.
	}

	/**
	 * Removes Chorevolution valdiator from project's list of external builders.
	 */
	public void deconfigure() throws CoreException {
		// TODO for now not implements the building of the chorevolutin project.
	}
}
