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

import org.eclipse.osgi.util.NLS;

import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePlugin;


public final class ChorevolutionProjectMessage extends NLS{
	private static final String BUNDLE_NAME = ChorevolutionCorePlugin.PLUGIN_ID + ".internal.project.ChorevolutionProjectMessage";
	
	private ChorevolutionProjectMessage() {
		// Do not instantiate
	}
	
	public static String Project_createProject;
	public static String Project_createProjectError;
	public static String ProjectNature_addProjectNatureError;
	public static String ProjectStructure_createProjectStructureError;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, ChorevolutionProjectMessage.class);
	}

}
