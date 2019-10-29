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
package eu.chorevolution.studio.eclipse.wizard.template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.bpmn2.modeler.core.builder.BPMN2Nature;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPreferenceData;

public class Utilities {

	public static Map<String, Text> createGrupContents(Composite container, String name, List<ChorevolutionPreferenceData> contents, boolean useBorderGroup) {
		Composite composite = null;
		if (useBorderGroup){
			composite = new Group(container, SWT.SHADOW_ETCHED_IN);
			((Group)composite).setText(name);
		}else{
			composite = new Composite(container, SWT.NULL);
		}
		
		composite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		GridLayout gl = new GridLayout(2, false);
		gl.marginTop = 10;
		gl.marginBottom = 10;
		composite.setLayout(gl);

		Map<String, Text> textsElements = new HashMap<String, Text>();

		for (ChorevolutionPreferenceData sourceModelPref : contents) {
			Label label = new Label(composite, SWT.NONE);
			label.setText(sourceModelPref.getLabel());

			Text text = new Text(composite, SWT.BORDER);
			text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			text.setText(sourceModelPref.getValue());

			textsElements.put(sourceModelPref.getID(), text);
		}
		return textsElements;
	}
	
	public static void configureBPMN2Nature (IProject project, boolean enable){
		BPMN2Nature.setBPMN2Nature(project, enable);
	}
}
