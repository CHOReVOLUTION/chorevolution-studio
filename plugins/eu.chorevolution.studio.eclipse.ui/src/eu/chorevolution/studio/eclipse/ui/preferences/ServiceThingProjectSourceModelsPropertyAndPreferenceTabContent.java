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
package eu.chorevolution.studio.eclipse.ui.preferences;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPrefs;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPreferenceData;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServiceThingSourceModelPrefs;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIMessages;
import eu.chorevolution.studio.eclipse.ui.Utilities;

public class ServiceThingProjectSourceModelsPropertyAndPreferenceTabContent {

	private IProject project;

	private Shell shell;

	private Map<String, Text> textWidgets;

	private Button resetButton;
	
	private ChorevolutionServiceThingSourceModelPrefs chorevolutionServiceThingSourceModelPrefs;

	public ServiceThingProjectSourceModelsPropertyAndPreferenceTabContent(Shell shell, IProject project) {
		this.project = project;
		this.shell = shell;
		textWidgets = new HashMap<String, Text>();
		chorevolutionServiceThingSourceModelPrefs = new ChorevolutionServiceThingSourceModelPrefs();
		
	}

	public Control createContents(Composite parent) {
		Font font = parent.getFont();
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 3;
		layout.marginWidth = 3;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		if (project != null) {
			Label beansLabel = new Label(composite, SWT.NONE);
			beansLabel.setText(ChorevolutionUIMessages.ServiceThingProjectSourceFolderModelsPropertyTab_description);

		}
		

		// get preferences value for workspace or selected project if specified
		 Map<String, ChorevolutionPreferenceData> projectOrWorkspacePreferencesValues = chorevolutionServiceThingSourceModelPrefs.readProjectOrWorkspacePreferences(project);

		
		List<ChorevolutionPreferenceData> servicethingModels = new ArrayList<ChorevolutionPreferenceData>();
		servicethingModels.add(projectOrWorkspacePreferencesValues.get(ChorevolutionServiceThingSourceModelPrefs.PREF_SERVICE_THING_INTERFACE_DESCRIPTION));
		servicethingModels.add(projectOrWorkspacePreferencesValues.get(ChorevolutionServiceThingSourceModelPrefs.PREF_SERVICE_THING_INTERACTIONPROTOCOL_DESCRIPTION));
		//servicethingModels.add(projectOrWorkspacePreferencesValues.get(ChorevolutionServiceThingSourceModelPrefs.PREF_SERVICE_THING_QOS_DESCRIPTION));
		//servicethingModels.add(projectOrWorkspacePreferencesValues.get(ChorevolutionServiceThingSourceModelPrefs.PREF_SERVICE_THING_IDENTITY_DESCRIPTION));
		servicethingModels.add(projectOrWorkspacePreferencesValues.get(ChorevolutionServiceThingSourceModelPrefs.PREF_SERVICE_THING_SECURITY_DESCRIPTION));

		textWidgets.putAll(Utilities.createGrupContents(composite, "Service Thing Models", servicethingModels));

		layout.verticalSpacing = 10;

		
		resetButton = new Button(composite, SWT.PUSH);
		resetButton.setText("Restore Defaults");
		GridData data = new GridData(SWT.RIGHT, SWT.BOTTOM, true, true);
		resetButton.setLayoutData(data);
		resetButton.addSelectionListener(resetListener);

		return composite;
	}

	public void performDefaults() {
		BusyIndicator.showWhile(shell.getDisplay(), new Runnable() {
			public void run() {
				// get preferences value for workspace or selected project if specified
				for (ChorevolutionPreferenceData chorevolutionPreferenceData : chorevolutionServiceThingSourceModelPrefs.restoreDefaults()) {
					textWidgets.get(chorevolutionPreferenceData.getID()).setText(chorevolutionPreferenceData.getValue());
				}
			}
		});
		
		
	}

	public boolean performOk() {
		WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {

			@Override
			protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
				List<ChorevolutionPreferenceData> chorevolutionPreferenceDatas = new ArrayList<ChorevolutionPreferenceData>();
				// get preferences value for workspace or selected project if specified
				 Map<String, ChorevolutionPreferenceData> projectOrWorkspacePreferencesValues = chorevolutionServiceThingSourceModelPrefs.readProjectOrWorkspacePreferences(project);
				
				for (Map.Entry<String, Text> entry : textWidgets.entrySet()) {
					ChorevolutionPreferenceData chorevolutionPreferenceData = new ChorevolutionPreferenceData( projectOrWorkspacePreferencesValues.get(entry.getKey()).getID(), projectOrWorkspacePreferencesValues.get(entry.getKey()).getLabel(), entry.getValue().getText(),  projectOrWorkspacePreferencesValues.get(entry.getKey()).getDescriptionProperty());
					chorevolutionPreferenceDatas.add(chorevolutionPreferenceData);
				}

				// store preferences
				ChorevolutionPrefs.setProjectOrWorkspacePreferences(chorevolutionPreferenceDatas, project);
			}
		};

		try {
			operation.run(new NullProgressMonitor());
		} catch (InvocationTargetException e) {
		} catch (InterruptedException e) {
		}

		return true;
	}

	/* Listeners */
	private SelectionListener resetListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if ((Button) e.widget == resetButton) {
				performDefaults();
			}
		}
	};

}
