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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.osgi.service.prefs.BackingStoreException;

import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePlugin;
import eu.chorevolution.studio.eclipse.ui.preferences.ServicesURIPropertyAndPreferenceContent;
import eu.chorevolution.studio.eclipse.ui.preferences.SynthesisProcessorPreferenceContent;

public class SynthesisProcessorPreferencePage extends PropertyAndPreferencePage {

	
	public static final String PREF_ID = "eu.chorevolution.studio.eclipse.ui.synthesisProcessorPreferencePage"; //$NON-NLS-1$

	public static final String PROP_ID = "eu.chorevolution.studio.eclipse.ui.synthesisProcessorPropertyPage"; //$NON-NLS-1$

	private SynthesisProcessorPreferenceContent synthProcContent = null;
	
	public SynthesisProcessorPreferencePage() {
		noDefaultAndApplyButton();
	}

	protected Control createPreferenceContent(Composite composite) {
		Composite content = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		content.setLayout(layout);
		content.setLayoutData(new GridData(GridData.FILL_BOTH));
				
		synthProcContent = new SynthesisProcessorPreferenceContent(getShell(), ((IProject) getElement()));
		
		synthProcContent.createContents(content);
		

		return content;
	}

	protected String getPreferencePageID() {
		return PREF_ID;
	}

	protected String getPropertyPageID() {
		return PROP_ID;
	}

	public boolean performOk() {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ChorevolutionCorePlugin.PLUGIN_ID);
		
		this.synthProcContent.performOk();
		
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			ChorevolutionCorePlugin.log(e);
			return false;
		}
		// always say it is ok
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		this.synthProcContent.performDefaults();
		
	}

}
