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

public class ChorevolutionPreferenceData {
	private String ID;
	private String label;
	private String value;
	private String description;

	public ChorevolutionPreferenceData(String ID, String label, String value, String description) {
		this.ID = ID;
		this.label = label;
		this.value = value;
		this.description = description;
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescriptionProperty() {
		return description;
	}

	public void setDescriptionProperty(String descriptionProperty) {
		this.description = descriptionProperty;
	}
	
	
	@Override
	public boolean equals(Object object){
	    if (object == null) return false;
	    if (object == this) return true;
	    if (!(object instanceof ChorevolutionPreferenceData))return false;
	    ChorevolutionPreferenceData chorevolutionPreferenceData = (ChorevolutionPreferenceData)object;
	    if (this.ID.equals(chorevolutionPreferenceData.getID())){
	    	return true;
	    }
	    return false;
	}
}
