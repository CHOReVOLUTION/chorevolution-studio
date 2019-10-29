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
package eu.chorevolution.studio.eclipse.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

public class ChorevolutionCorePreferences {

	private final String propertyNamespace;

	private final IEclipsePreferences preferences;

	private ChorevolutionCorePreferences(IProject project, String qualifier, boolean viewMonitor) {
		this.propertyNamespace = qualifier + '.';
		this.preferences = getEclipsePreferences(project, qualifier, viewMonitor);
	}

	public static ChorevolutionCorePreferences getProjectOrWorkspacePreferences(IProject project, String qualifier) {
		return new ChorevolutionCorePreferences(project, qualifier, false);
	}
	
	public static ChorevolutionCorePreferences getProjectOrWorkspacePreferences(IProject project, String qualifier, boolean viewMonitor) {
		return new ChorevolutionCorePreferences(project, qualifier, viewMonitor);
	}
	
	private IEclipsePreferences getEclipsePreferences(IProject project, String qualifier, boolean viewMonitor) {
		if (project != null){
			//get project preference
			IScopeContext context = new ProjectScope(project);
			return context.getNode(qualifier);
		}else{
			//get workspace preference
			if(viewMonitor) {
				//get the workbench preferences
				return InstanceScope.INSTANCE.getNode("org.eclipse.ui.workbench");
			}
			else {
				//get the instantiated preference
				return InstanceScope.INSTANCE.getNode(ChorevolutionCorePlugin.PLUGIN_ID);
			}
		}
	}

	public void putString(String key, String value) {
		if (key == null || value == null) {
			return;
		}
		try {
			this.preferences.put(propertyNamespace + key, value);
			this.preferences.flush();
		} catch (BackingStoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ChorevolutionCorePlugin.PLUGIN_ID,
					"An error occurred updating preferences", e));
		}
	}

	public void putStringWithoutNamespace(String key, String value) {
		if (key == null || value == null) {
			return;
		}
		try {
			this.preferences.put(key, value);
			this.preferences.flush();
		} catch (BackingStoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ChorevolutionCorePlugin.PLUGIN_ID,
					"An error occurred updating preferences", e));
		}
	}
	
	public void putBoolean(String key, boolean value) {
		if (key == null) {
			return;
		}
		try {
			this.preferences.putBoolean(propertyNamespace + key, value);
			this.preferences.flush();
		} catch (BackingStoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ChorevolutionCorePlugin.PLUGIN_ID,
					"An error occurred updating preferences", e));
		}
	}

	public String getString(String key, String defaultValue) {
		return this.preferences.get(propertyNamespace + key, defaultValue);
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		return this.preferences.getBoolean(propertyNamespace + key, defaultValue);
	}

	public IEclipsePreferences getProjectPreferences() {
		return this.preferences;
	}

}
