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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.osgi.service.prefs.BackingStoreException;

import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePlugin;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIMessages;
import eu.chorevolution.studio.eclipse.ui.preferences.ServiceThingProjectSourceModelsPropertyAndPreferenceTabContent;
import eu.chorevolution.studio.eclipse.ui.preferences.SynthesisProjectSourceModelsPropertyAndPreferenceTabContent;

public class SourceModelsPropertyAndPreferencePage extends PropertyAndPreferencePage {

	private IProject project;

	public static final String PREF_ID = "eu.chorevolution.studio.eclipse.ui.sourceModelsPreferencePage"; //$NON-NLS-1$

	public static final String PROP_ID = "eu.chorevolution.studio.eclipse.ui.sourceModelsPropertyPage"; //$NON-NLS-1$

	private SynthesisProjectSourceModelsPropertyAndPreferenceTabContent synthesisProjectSourceModelsPropertyTab = null;
	private ServiceThingProjectSourceModelsPropertyAndPreferenceTabContent serviceThingProjectSourceModelsPropertyTab = null;

	public SourceModelsPropertyAndPreferencePage() {
		noDefaultAndApplyButton();
	}

	protected Control createPreferenceContent(Composite composite) {

		TabFolder folder = new TabFolder(composite, SWT.NONE);
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));

		// TODO diff for the project or workspace
		project = (IProject) getElement();
		try {
			if (project == null || project.hasNature(ChorevolutionCorePlugin.SYNTHESIS_NATURE_ID)) {
				TabItem synthesisProjectSourceModelsPropertyTabItem = new TabItem(folder, SWT.NONE);
				this.synthesisProjectSourceModelsPropertyTab = new SynthesisProjectSourceModelsPropertyAndPreferenceTabContent(getShell(),project);
				synthesisProjectSourceModelsPropertyTabItem
						.setControl(synthesisProjectSourceModelsPropertyTab.createContents(folder));
				synthesisProjectSourceModelsPropertyTabItem
						.setText(ChorevolutionUIMessages.SynthesisProjectSourceFolderModelsPropertyTab_title);
			}
		} catch (CoreException e) {
			ChorevolutionCorePlugin.log(e);
		}
		try {
			if (project == null || project.hasNature(ChorevolutionCorePlugin.SERVICE_THING_NATURE_ID)) {
				TabItem serviceThingProjectSourceModelsPropertyTabItem = new TabItem(folder, SWT.NONE);
				this.serviceThingProjectSourceModelsPropertyTab = new ServiceThingProjectSourceModelsPropertyAndPreferenceTabContent(
						getShell(), project);
				serviceThingProjectSourceModelsPropertyTabItem
						.setControl(serviceThingProjectSourceModelsPropertyTab.createContents(folder));
				serviceThingProjectSourceModelsPropertyTabItem
						.setText(ChorevolutionUIMessages.ServiceThingProjectSourceFolderModelsPropertyTab_title);
			}
		} catch (CoreException e) {
			ChorevolutionCorePlugin.log(e);
		}

		Dialog.applyDialogFont(folder);

		return folder;
	}

	protected String getPreferencePageID() {
		return PREF_ID;
	}

	protected String getPropertyPageID() {
		return PROP_ID;
	}

	public boolean performOk() {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ChorevolutionCorePlugin.PLUGIN_ID);
		
		try {
			if (project == null || project.hasNature(ChorevolutionCorePlugin.SYNTHESIS_NATURE_ID)) {
				this.synthesisProjectSourceModelsPropertyTab.performOk();
			}
		} catch (CoreException e) {
			ChorevolutionCorePlugin.log(e);
		}
		try {
			if (project == null || project.hasNature(ChorevolutionCorePlugin.SERVICE_THING_NATURE_ID)) {
				this.serviceThingProjectSourceModelsPropertyTab.performOk();
			}
		} catch (CoreException e) {
			ChorevolutionCorePlugin.log(e);
		}
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
		try {
			if (project == null || project.hasNature(ChorevolutionCorePlugin.SYNTHESIS_NATURE_ID)) {
				this.synthesisProjectSourceModelsPropertyTab.performDefaults();
			}
		} catch (CoreException e) {
			ChorevolutionCorePlugin.log(e);
		}
		try {
			if (project == null || project.hasNature(ChorevolutionCorePlugin.SERVICE_THING_NATURE_ID)) {
				this.serviceThingProjectSourceModelsPropertyTab.performDefaults();
			}
		} catch (CoreException e) {
			ChorevolutionCorePlugin.log(e);
		}
	}

}
