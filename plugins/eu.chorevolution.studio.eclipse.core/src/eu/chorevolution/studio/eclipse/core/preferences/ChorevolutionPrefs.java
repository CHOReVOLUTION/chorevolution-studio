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
package eu.chorevolution.studio.eclipse.core.preferences;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;

import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePlugin;
import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePreferences;

public abstract class ChorevolutionPrefs {
	
	protected List<ChorevolutionPreferenceData> defaultPropertyValues;
	
	public  List<ChorevolutionPreferenceData> getDefaultPropertyValues() {
		return defaultPropertyValues;
	}

	public String getDefaultPropertyValue(String key) {
		for (ChorevolutionPreferenceData chorevolutionPreferenceData : defaultPropertyValues) {
			if (chorevolutionPreferenceData.getID().equals(key)){
				return chorevolutionPreferenceData.getID();
			}
		}
		return "";
	}

	public String getPropertyLabel(String key) {
		for (ChorevolutionPreferenceData chorevolutionPreferenceData : defaultPropertyValues) {
			if (chorevolutionPreferenceData.getID().equals(key)){
				return chorevolutionPreferenceData.getLabel();
			}
		}
		return "";
	}

	public Map<String, ChorevolutionPreferenceData> readProjectOrWorkspacePreferences(IProject project) {
		 Map<String, ChorevolutionPreferenceData> propertyValues = new HashMap<String,ChorevolutionPreferenceData>();
		ChorevolutionCorePreferences prefs = ChorevolutionCorePreferences.getProjectOrWorkspacePreferences(project, ChorevolutionCorePlugin.PLUGIN_ID);
		
		for (ChorevolutionPreferenceData chorevolutionPreferenceData : defaultPropertyValues) {
			String valuePref = prefs.getString(chorevolutionPreferenceData.getID(), chorevolutionPreferenceData.getValue());
			propertyValues.put(chorevolutionPreferenceData.getID(), new ChorevolutionPreferenceData(chorevolutionPreferenceData.getID(), chorevolutionPreferenceData.getLabel(), valuePref, chorevolutionPreferenceData.getDescriptionProperty()));
		}
		
		return propertyValues;
	}

	public static void setProjectOrWorkspacePreferences(List<ChorevolutionPreferenceData> newPropertyValues, IProject project) {
		ChorevolutionCorePreferences prefs = ChorevolutionCorePreferences.getProjectOrWorkspacePreferences(project, ChorevolutionCorePlugin.PLUGIN_ID);
		for (ChorevolutionPreferenceData chorevolutionPreferenceData : newPropertyValues) {
			prefs.putString(chorevolutionPreferenceData.getID(), chorevolutionPreferenceData.getValue());
		}
	}

	public abstract List<ChorevolutionPreferenceData> restoreDefaults();

}
