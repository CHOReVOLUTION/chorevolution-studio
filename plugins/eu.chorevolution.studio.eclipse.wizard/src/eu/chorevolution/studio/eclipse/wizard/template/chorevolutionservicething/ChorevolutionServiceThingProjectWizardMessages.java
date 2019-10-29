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

import org.eclipse.osgi.util.NLS;


public final class ChorevolutionServiceThingProjectWizardMessages extends NLS {

	private static final String BUNDLE_NAME = "eu.chorevolution.studio.eclipse.wizard.template.chorevolutionservicething.ChorevolutionServiceThingProjectWizardMessages";

	private ChorevolutionServiceThingProjectWizardMessages() {
		// Do not instantiate
	}
	public static String NewProject_windowTitle;
	
	public static String NewProject_mainPageName;
	public static String NewProject_projectTitle;
	public static String NewProject_projectDescription;
	
	public static String NewProject_structurePageName;
	public static String NewProject_structureProjectTitle;
	public static String NewProject_structureProjectDescription;
	public static String NewProject_folderStructureError;

	static {
		NLS.initializeMessages(BUNDLE_NAME, ChorevolutionServiceThingProjectWizardMessages.class);
	}
}
