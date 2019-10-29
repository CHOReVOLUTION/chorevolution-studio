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
package eu.chorevolution.studio.eclipse.wizard.template.chorevolutionsynthesis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.chorevolution.studio.eclipse.core.internal.project.model.ChorevolutionStructureFolder;
import eu.chorevolution.studio.eclipse.core.internal.project.model.impl.ChorevolutionSynthesisProjectStructureFolder;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPreferenceData;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionSynthesisSourceModelPrefs;
import eu.chorevolution.studio.eclipse.wizard.template.Utilities;

public class ChorevolutionSynthesisProjectWizardStructurePage extends WizardPage {
	
	private static final String PAGE_NAME = ChorevolutionSynthesisProjectWizardMessages.NewProject_structurePageName;
	
	private Map<String, Text> textWidgets;
	
	private Button resetButton;

	private ChorevolutionSynthesisSourceModelPrefs chorevolutionSynthesisSourceModelPrefs;

	private Button sourceCodeButton;
	private Button executableArtefactsButton;
	private String synthesisGenerationValue = ChorevolutionSynthesisSourceModelPrefs.SYNTHESIS_GENERATOR_SOURCE_CODE;

	/**
	 * Constructor for ChorevolutionProjectWizardStructurePage.
	 *
	 */
	public ChorevolutionSynthesisProjectWizardStructurePage(ISelection selection) {
		super(PAGE_NAME);

		setTitle(ChorevolutionSynthesisProjectWizardMessages.NewProject_structureProjectTitle);
		setDescription(ChorevolutionSynthesisProjectWizardMessages.NewProject_structureProjectDescription);

		this.textWidgets = new HashMap<String, Text>();

		chorevolutionSynthesisSourceModelPrefs = new ChorevolutionSynthesisSourceModelPrefs();
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.verticalSpacing = 10;
		gridLayout.marginBottom = 0;
		container.setLayout(gridLayout);

		// get preferences value for workspace or selected project if specified
		Map<String, ChorevolutionPreferenceData> projectOrWorkspacePreferencesValues = chorevolutionSynthesisSourceModelPrefs
				.readProjectOrWorkspacePreferences(null);

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

		createListenerTexts(Utilities.createGrupContents(container,
				ChorevolutionSynthesisProjectStructureFolder.SYNTHESIS_PROCESSOR_FOLDER_NAME, synthesisProcessor,
				true));

		// Enactment property
		gridLayout.verticalSpacing = 10;
		Composite compositeChoreographySpecification = new Composite(container, SWT.NONE);
		GridLayout layoutChoreographySpecification = new GridLayout();
		layoutChoreographySpecification.marginHeight = 3;
		layoutChoreographySpecification.marginWidth = 3;
		layoutChoreographySpecification.numColumns = 2;
		compositeChoreographySpecification.setLayout(layoutChoreographySpecification);
		compositeChoreographySpecification.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		ChorevolutionPreferenceData choreographySpecificationPref = projectOrWorkspacePreferencesValues
				.get(ChorevolutionSynthesisSourceModelPrefs.PREF_CHOREOGRAPHYDEPLOYMENT);
		Label labelChoreographySpecification = new Label(compositeChoreographySpecification, SWT.NONE);
		labelChoreographySpecification.setText(choreographySpecificationPref.getLabel());
		Text textChoreographySpecification = new Text(compositeChoreographySpecification, SWT.BORDER);
		textChoreographySpecification.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textChoreographySpecification.setText(choreographySpecificationPref.getValue());
		createListenerText(choreographySpecificationPref.getID(), textChoreographySpecification);

		// Synthesis Generation: Source code or executable
		gridLayout.verticalSpacing = 10;
		Composite compositeSynthesisGeneration = new Composite(container, SWT.NONE);
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
		gridLayout1.numColumns = 2;
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
		gridLayout.verticalSpacing = 10;

		Composite compositeServices = new Composite(container, SWT.NONE);
		GridLayout layoutServices = new GridLayout();
		layoutServices.marginHeight = 3;
		layoutServices.marginWidth = 3;
		layoutServices.numColumns = 2;
		compositeServices.setLayout(layoutServices);
		compositeServices.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));

		ChorevolutionPreferenceData servicesPref = projectOrWorkspacePreferencesValues
				.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SERVICEINVENTORY_SERVICES);

		Label labelServices = new Label(compositeServices, SWT.NONE);
		labelServices.setText(servicesPref.getLabel());

		Text textServices = new Text(compositeServices, SWT.BORDER);
		textServices.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textServices.setText(servicesPref.getValue());

		createListenerText(servicesPref.getID(), textServices);

		gridLayout.verticalSpacing = 10;

		resetButton = new Button(container, SWT.PUSH);
		resetButton.setText("Restore Defaults");
		resetButton.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, true));
		resetButton.addSelectionListener(resetListener);
		
		//Do not delete.
		Label emptySeparator = new Label(container, SWT.NULL);
			    
		Label separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
	    separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
	}

	/**
	 * check if all text box has a value
	 * 
	 * @return
	 */
	private boolean validateWizardPage() {
		int indexSubstring = 1;
		for (String value : textWidgets.keySet()) {
			if (textWidgets.get(value).getText().trim().isEmpty())
				return false;
			if (indexSubstring == textWidgets.size())
				return true;

			List<String> subList = new ArrayList<String>();
			subList.addAll(textWidgets.keySet());
			for (String value2 : subList.subList(indexSubstring++, textWidgets.size())) {
				if (textWidgets.get(value).getText().trim().equalsIgnoreCase(textWidgets.get(value2).getText().trim()))
					return false;
			}
		}
		return true;
	}

	public List<ChorevolutionPreferenceData> getProjectSettings() {
		List<ChorevolutionPreferenceData> projectSettings = new ArrayList<ChorevolutionPreferenceData>();
		// get preferences value for workspace or selected project if specified
		Map<String, ChorevolutionPreferenceData> projectOrWorkspacePreferencesValues = chorevolutionSynthesisSourceModelPrefs
				.readProjectOrWorkspacePreferences(null);

		for (Map.Entry<String, Text> entry : textWidgets.entrySet()) {
			ChorevolutionPreferenceData chorevolutionPreferenceData = new ChorevolutionPreferenceData(
					projectOrWorkspacePreferencesValues.get(entry.getKey()).getID(),
					projectOrWorkspacePreferencesValues.get(entry.getKey()).getLabel(), entry.getValue().getText(),
					projectOrWorkspacePreferencesValues.get(entry.getKey()).getDescriptionProperty());
			projectSettings.add(chorevolutionPreferenceData);
		}
		
		// Add synthesis generation preference
		ChorevolutionPreferenceData synthesisGenerationPref = projectOrWorkspacePreferencesValues.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESIS_GENERATION);
		ChorevolutionPreferenceData chorevolutionPreferenceData = new ChorevolutionPreferenceData(synthesisGenerationPref.getID(),
				synthesisGenerationPref.getLabel(), synthesisGenerationValue, synthesisGenerationPref.getDescriptionProperty());
		projectSettings.add(chorevolutionPreferenceData);
		return projectSettings;

	}

	public ChorevolutionStructureFolder getStructureFolders() {
		Map<String, String> properties = new HashMap<String, String>();
		for (Map.Entry<String, Text> entry : textWidgets.entrySet()) {
			properties.put(entry.getKey(), entry.getValue().getText().trim());
		}
		return new ChorevolutionSynthesisProjectStructureFolder(properties);
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

	private void performDefaults() {
		// read workspace preferences
		Map<String, ChorevolutionPreferenceData> propertyValues = chorevolutionSynthesisSourceModelPrefs
				.readProjectOrWorkspacePreferences(null);
		for (Map.Entry<String, ChorevolutionPreferenceData> entry : propertyValues.entrySet()) {
			if (textWidgets.get(entry.getKey())!=null) {
				textWidgets.get(entry.getKey()).setText(entry.getValue().getValue());
				textWidgets.get(entry.getKey()).update();	
			}
			
		}
		ChorevolutionPreferenceData synthesisGenerationPref = propertyValues
				.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESIS_GENERATION);
		setDefaultSynthesisGeneration(synthesisGenerationPref);
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

	private void createListenerTexts(Map<String, Text> texts) {
		for (Map.Entry<String, Text> entry : texts.entrySet()) {
			createListenerText(entry.getKey(), entry.getValue());
		}
	}

	private void createListenerText(String key, Text text) {
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (validateWizardPage()) {
					setPageComplete(true);
					setErrorMessage(null);
				} else {
					setPageComplete(false);
					setErrorMessage(ChorevolutionSynthesisProjectWizardMessages.NewProject_folderStructureError);
				}
			}
		});
		textWidgets.put(key, text);
	}

}
