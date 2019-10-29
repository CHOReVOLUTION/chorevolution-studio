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

import java.util.ArrayList;
import java.util.List;

public class ChorevolutionServiceThingSourceModelPrefs extends ChorevolutionPrefs{

	public final static String PREF_SERVICE_THING_INTERFACE_DESCRIPTION = "servicething.interface.description.sourcefolder.property";
	public final static String PREF_SERVICE_THING_INTERACTIONPROTOCOL_DESCRIPTION = "servicething.interactionprotocol.description.sourcefolder.property";
	//public final static String PREF_SERVICE_THING_QOS_DESCRIPTION = "servicething.qos.description.sourcefolder.property";
	//public final static String PREF_SERVICE_THING_IDENTITY_DESCRIPTION = "servicething.identity.description.sourcefolder.property";
	public final static String PREF_SERVICE_THING_SECURITY_DESCRIPTION = "servicething.security.description.sourcefolder.property";

	public ChorevolutionServiceThingSourceModelPrefs() {
		defaultPropertyValues =  new ArrayList<ChorevolutionPreferenceData>();
		defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_SERVICE_THING_INTERFACE_DESCRIPTION, "Interface Description", "Interface", ""));
		defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_SERVICE_THING_INTERACTIONPROTOCOL_DESCRIPTION, "Interaction Protocol Description", "Interaction Protocol", ""));
		//defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_SERVICE_THING_QOS_DESCRIPTION, "QoS Description:", "qos", ""));
		//defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_SERVICE_THING_IDENTITY_DESCRIPTION, "Identity Description:", "identity", ""));
		defaultPropertyValues.add(new ChorevolutionPreferenceData( PREF_SERVICE_THING_SECURITY_DESCRIPTION, "Security Description:", "Security", ""));
	}

	@Override
	public List<ChorevolutionPreferenceData> restoreDefaults() {
		return defaultPropertyValues;
	}
}
