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
package eu.chorevolution.studio.eclipse.ui.preferences.pages;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbenchPropertyPage;

import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIMessages;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;

public class CommonsPropertyAndPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage,IWorkbenchPropertyPage {

	public static final String PREF_ID = "eu.chorevolution.studio.eclipse.ui.commonsPreferencePage"; //$NON-NLS-1$

	public static final String PROP_ID = "eu.chorevolution.studio.eclipse.ui.commonsPropertyPage"; //$NON-NLS-1$
	
	public CommonsPropertyAndPreferencePage() {
		super(GRID);
		this.setPreferenceStore(ChorevolutionUIPlugin.getDefault().getPreferenceStore());
		this.setDescription(ChorevolutionUIMessages.ChorevolutionCategoryPreferenceAndPropertyPage_description);
	}

	@Override
	protected void createFieldEditors() {
	}

	public void init(IWorkbench workbench) {
		noDefaultAndApplyButton();
	}

	@Override
	public IAdaptable getElement() {
		return null;
	}

	@Override
	public void setElement(IAdaptable element) {		
	}
	
	protected String getPreferencePageID() {
		return PREF_ID;
	}

	protected String getPropertyPageID() {
		return PROP_ID;
	}

}
