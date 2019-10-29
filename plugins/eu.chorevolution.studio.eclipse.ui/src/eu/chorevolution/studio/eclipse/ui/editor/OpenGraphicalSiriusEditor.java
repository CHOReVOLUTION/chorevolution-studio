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
package eu.chorevolution.studio.eclipse.ui.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorLauncher;

import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePlugin;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIUtils;

public class OpenGraphicalSiriusEditor implements IEditorLauncher {

	@Override
	public void open(IPath file) {
		IFile ifile = ChorevolutionCorePlugin.getWorkspace().getRoot().getFileForLocation(file);
		ChorevolutionUIUtils.openEditor(ifile);
	}
}