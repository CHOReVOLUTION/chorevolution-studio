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
package eu.chorevolution.studio.eclipse.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import eu.chorevolution.studio.eclipse.core.internal.editor.ChorevolutionEditorUtils;

public class ChorevolutionUIUtils {

	public static void openEditor(IFile modelFile) {

		Job openEditorJob = new Job("Open graphical editor") {
			protected IStatus run(IProgressMonitor monitor) {

				// this check is necessary to prevent the iteration on all
				// remaining resources if the monitor is cancelled
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}

				IFile graphicalEditor = ChorevolutionEditorUtils.getGraphicalEditor(modelFile);
				// create graphical Editor and/or open it
				IStatus status = Status.OK_STATUS;

				if (!graphicalEditor.exists()) {
					// create graphical editor
					status = ChorevolutionEditorUtils.createAird(graphicalEditor, modelFile, monitor);
					if (monitor.isCanceled()) {
						// TODO here the aird file is created and the user
						// has cancelled the operation then deleting aird
						// file ??
					}
				}

				// open graphical editor
				if (!monitor.isCanceled() && status.getCode() == IStatus.OK) {
					ChorevolutionEditorUtils.openDiagram(graphicalEditor, monitor);
				} else if (status.getCode() == IStatus.ERROR) {
					// TODO exception
				}

				monitor.done();

				return Status.OK_STATUS;
			}
		};
		openEditorJob.setUser(true);
		openEditorJob.schedule();
	}

}
