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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import eu.chorevolution.studio.eclipse.core.internal.project.model.impl.ChorevolutionSynthesisProjectStructureFolder;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPreferenceData;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPrefs;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionSynthesisSourceModelPrefs;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIMessages;
import eu.chorevolution.studio.eclipse.ui.Utilities;

public class SynthesisProjectSourceModelsPropertyAndPreferenceTabContent {

	private IProject project;

	private Shell shell;

	private Map<String, Text> textWidgets;

	private Button resetButton;

	private ChorevolutionSynthesisSourceModelPrefs chorevolutionSynthesisSourceModelPrefs;
	

	private Button sourceCodeButton;
	private Button executableArtefactsButton;
	private String synthesisGenerationValue = ChorevolutionSynthesisSourceModelPrefs.SYNTHESIS_GENERATOR_SOURCE_CODE;
	

	public SynthesisProjectSourceModelsPropertyAndPreferenceTabContent(Shell shell, IProject project) {
		this.project = project;
		this.shell = shell;
		textWidgets = new HashMap<String, Text>();

		chorevolutionSynthesisSourceModelPrefs = new ChorevolutionSynthesisSourceModelPrefs();
		

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
			beansLabel.setText(ChorevolutionUIMessages.SynthesisProjectSourceFolderModelsPropertyTab_description);

		}
		
		// get preferences value for workspace or selected project if specified
		Map<String, ChorevolutionPreferenceData> projectOrWorkspacePreferencesValues = chorevolutionSynthesisSourceModelPrefs.readProjectOrWorkspacePreferences(project);

		/*List<ChorevolutionPreferenceData> additionalModels = new ArrayList<ChorevolutionPreferenceData>();
		additionalModels.add(projectOrWorkspacePreferencesValues.get(ChorevolutionSynthesisSourceModelPrefs.PREF_ADDITIONALMODELS_VARIABILITY));
		additionalModels.add(projectOrWorkspacePreferencesValues.get(ChorevolutionSynthesisSourceModelPrefs.PREF_ADDITIONALMODELS_QOS));
		additionalModels.add(projectOrWorkspacePreferencesValues.get(ChorevolutionSynthesisSourceModelPrefs.PREF_ADDITIONALMODELS_IDENTITY));

		textWidgets.putAll(Utilities.createGrupContents(composite, ChorevolutionSynthesisProjectStructureFolder.ADDITIONAL_MODELS_FOLDER_NAME, additionalModels));
		 */
		//layout.verticalSpacing = 10;

		List<ChorevolutionPreferenceData> synthesisProcessor = new ArrayList<ChorevolutionPreferenceData>();
		synthesisProcessor.add(projectOrWorkspacePreferencesValues
				.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_COORD));
		synthesisProcessor.add(projectOrWorkspacePreferencesValues
				.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_ADAPTER));
		synthesisProcessor.add(projectOrWorkspacePreferencesValues
				.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_SECURITYFILTER));
		synthesisProcessor.add(projectOrWorkspacePreferencesValues
				.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_BINDINGCOMPONENT));
		synthesisProcessor.add(projectOrWorkspacePreferencesValues
				.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_ARCHITECTURALSTYLE));

		textWidgets.putAll(Utilities.createGrupContents(composite, ChorevolutionSynthesisProjectStructureFolder.SYNTHESIS_PROCESSOR_FOLDER_NAME, synthesisProcessor));
		
		// Enactment property

		layout.verticalSpacing = 10;

		Composite compositeChoreographyDeployment = new Composite(composite, SWT.NONE);
		GridLayout layoutChoreographyDeployment = new GridLayout();
		layoutChoreographyDeployment.marginHeight = 3;
		layoutChoreographyDeployment.marginWidth = 3;
		layoutChoreographyDeployment.numColumns = 2;
		compositeChoreographyDeployment.setLayout(layoutChoreographyDeployment);
		compositeChoreographyDeployment.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));

		ChorevolutionPreferenceData choreographyDeploymentPref = projectOrWorkspacePreferencesValues
				.get(ChorevolutionSynthesisSourceModelPrefs.PREF_CHOREOGRAPHYDEPLOYMENT);

		Label labelchoreographyDeployment = new Label(compositeChoreographyDeployment, SWT.NONE);
		labelchoreographyDeployment.setText(choreographyDeploymentPref.getLabel());

		Text textchoreographyDeployment = new Text(compositeChoreographyDeployment, SWT.BORDER);
		textchoreographyDeployment.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textchoreographyDeployment.setText(choreographyDeploymentPref.getValue());

		textWidgets.put(choreographyDeploymentPref.getID(), textchoreographyDeployment);
		
		// Synthesis Generation: Source code or executable
		layout.verticalSpacing = 10;
		Composite compositeSynthesisGeneration = new Composite(composite, SWT.NONE);
		GridLayout layoutSynthesisGeneration = new GridLayout();
		layoutSynthesisGeneration.marginHeight = 3;
		layoutSynthesisGeneration.marginWidth = 3;
		layoutSynthesisGeneration.numColumns = 2;
		compositeSynthesisGeneration.setLayout(layoutSynthesisGeneration);
		compositeSynthesisGeneration.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		ChorevolutionPreferenceData synthesisGenerationPref = projectOrWorkspacePreferencesValues
				.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESIS_GENERATION);

		Label labelSynthesisGeneration = new Label(compositeSynthesisGeneration, SWT.NONE);
		labelSynthesisGeneration.setText(synthesisGenerationPref.getLabel());
		Group buttonGroup = new Group(compositeSynthesisGeneration, SWT.NONE);
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 3;
		buttonGroup.setLayout(gridLayout1);
		buttonGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		sourceCodeButton = new Button(buttonGroup, SWT.RADIO);
		sourceCodeButton.setText("Source Code");
		sourceCodeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				synthesisGenerationValue = ChorevolutionSynthesisSourceModelPrefs.SYNTHESIS_GENERATOR_SOURCE_CODE;
			};
		});
		executableArtefactsButton = new Button(buttonGroup, SWT.RADIO);
		executableArtefactsButton.setText("Executable Artefacts");
		executableArtefactsButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				synthesisGenerationValue = ChorevolutionSynthesisSourceModelPrefs.SYNTHESIS_GENERATOR_EXECUTABLE_ARTEFACTS;
			};
		});
		
		setDefaultSynthesisGeneration(synthesisGenerationPref);		

		// Services property
		Composite compositeServices = new Composite(composite, SWT.NONE);
		GridLayout layoutServices = new GridLayout();
		layoutServices.marginHeight = 3;
		layoutServices.marginWidth = 3;
		layoutServices.numColumns = 2;
		compositeServices.setLayout(layoutServices);
		compositeServices.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
		
		ChorevolutionPreferenceData servicesPref = projectOrWorkspacePreferencesValues
				.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SERVICEINVENTORY_SERVICES);

		Label labelServices = new Label(compositeServices, SWT.NONE);
		labelServices.setText(servicesPref.getLabel());

		Text textServices = new Text(compositeServices, SWT.BORDER);
		textServices.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textServices.setText(servicesPref.getValue());

		textWidgets.put(servicesPref.getID(), textServices);

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
				ChorevolutionPreferenceData synthesisGenerationPref = null;
				for (ChorevolutionPreferenceData chorevolutionPreferenceData : chorevolutionSynthesisSourceModelPrefs.restoreDefaults()) {
					//Added for excluding PREF_SYNTHESIS_GENERATION
					if (!ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESIS_GENERATION.equalsIgnoreCase(chorevolutionPreferenceData.getID())) {
						if (textWidgets.get(chorevolutionPreferenceData.getID())!=null) {
							textWidgets.get(chorevolutionPreferenceData.getID()).setText(chorevolutionPreferenceData.getValue());	
						}						
					} else {
						synthesisGenerationPref = chorevolutionPreferenceData;
					}
				}
				
				setDefaultSynthesisGeneration(synthesisGenerationPref);
			}
		});

	}

	public boolean performOk() {
		WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {

			@Override
			protected void execute(IProgressMonitor monitor)
					throws CoreException, InvocationTargetException, InterruptedException {
				List<ChorevolutionPreferenceData> newChorevolutionPreferenceData = new ArrayList<ChorevolutionPreferenceData>();
				
				// get preferences value for workspace or selected project if specified
				Map<String, ChorevolutionPreferenceData> projectOrWorkspacePreferencesValues = chorevolutionSynthesisSourceModelPrefs.readProjectOrWorkspacePreferences(project);

				for (Map.Entry<String, Text> entry : textWidgets.entrySet()) {
					ChorevolutionPreferenceData chorevolutionPreferenceData = new ChorevolutionPreferenceData(
							projectOrWorkspacePreferencesValues.get(entry.getKey()).getID(),
							projectOrWorkspacePreferencesValues.get(entry.getKey()).getLabel(),
							entry.getValue().getText(),
							projectOrWorkspacePreferencesValues.get(entry.getKey()).getDescriptionProperty());
					newChorevolutionPreferenceData.add(chorevolutionPreferenceData);
				}
				// Add synthesis generation preference
				ChorevolutionPreferenceData synthesisGenerationPref = projectOrWorkspacePreferencesValues.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESIS_GENERATION);
				ChorevolutionPreferenceData chorevolutionPreferenceData = new ChorevolutionPreferenceData(synthesisGenerationPref.getID(),
						synthesisGenerationPref.getLabel(), synthesisGenerationValue, synthesisGenerationPref.getDescriptionProperty());
				newChorevolutionPreferenceData.add(chorevolutionPreferenceData);
				
				//renaming folder if the tab is opened throw project properties
				if (project != null){
					// TODO
					//ChorevolutionCoreUtils.renameFolder("services", project, ChorevolutionUIPlugin.getActiveWorkbenchShell());
				}
				
				// store preferences
				ChorevolutionPrefs.setProjectOrWorkspacePreferences(newChorevolutionPreferenceData, project);		
			}
		};

		try {
			operation.run(new NullProgressMonitor());
		} catch (InvocationTargetException e) {
		} catch (InterruptedException e) {
		}

		return true;
	}
	
	private void setDefaultSynthesisGeneration(ChorevolutionPreferenceData synthesisGenerationPref) {
		if (ChorevolutionSynthesisSourceModelPrefs.SYNTHESIS_GENERATOR_SOURCE_CODE.equalsIgnoreCase(synthesisGenerationPref.getValue())) {
			synthesisGenerationValue = ChorevolutionSynthesisSourceModelPrefs.SYNTHESIS_GENERATOR_SOURCE_CODE;
			//Set default synthesis generation
			sourceCodeButton.setSelection(true);
			executableArtefactsButton.setSelection(false);
		} else {
			sourceCodeButton.setSelection(false);
			executableArtefactsButton.setSelection(true);
			synthesisGenerationValue = ChorevolutionSynthesisSourceModelPrefs.SYNTHESIS_GENERATOR_EXECUTABLE_ARTEFACTS;
		}
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
