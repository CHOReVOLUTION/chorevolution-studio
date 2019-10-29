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
package eu.chorevolution.studio.eclipse.wizard.template.chorevolutionservicething;

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
import eu.chorevolution.studio.eclipse.core.internal.project.model.impl.ChorevolutionServiceThingProjectStructureFolder;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPreferenceData;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServiceThingSourceModelPrefs;
import eu.chorevolution.studio.eclipse.core.utils.syncope.InterfaceDescriptionType;
import eu.chorevolution.studio.eclipse.wizard.template.Utilities;

public class ChorevolutionServiceThingProjectWizardStructurePage extends WizardPage {
	private static final String PAGE_NAME = ChorevolutionServiceThingProjectWizardMessages.NewProject_structurePageName;

	private ISelection selection;
	private Map<String, Text> textWidgets;

	private Button resetButton;

	private Button wsdlServiceInterfaceDescriptionButton;
	private Button coapServiceInterfaceDescriptionButton;
	private Button wadlServiceInterfaceDescriptionButton;

	// private RadioGroupFieldEditor serviceInterfaceDescription;

	private ChorevolutionServiceThingSourceModelPrefs chorevolutionServiceThingSourceModelPrefs;

	/**
	 * Constructor for ChorevolutionProjectWizardStructurePage.
	 *
	 */
	public ChorevolutionServiceThingProjectWizardStructurePage(ISelection selection) {
		super(PAGE_NAME);

		setTitle(ChorevolutionServiceThingProjectWizardMessages.NewProject_structureProjectTitle);
		setDescription(ChorevolutionServiceThingProjectWizardMessages.NewProject_structureProjectDescription);

		this.selection = selection;

		textWidgets = new HashMap<String, Text>();

		chorevolutionServiceThingSourceModelPrefs = new ChorevolutionServiceThingSourceModelPrefs();

	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		
		try {
			GridLayout noSpace = new GridLayout();
			noSpace.marginBottom = -80;//needed to align "restore default" to the bottom
			parent.getParent().setLayout(noSpace);
		}
		catch(Exception e) {
			
		}
		
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.verticalSpacing = 10;
		container.setLayout(gridLayout);

		// Add radio component to select WSDL COAP REST

		Group groupServiceInterfaceDescription = new Group(container, SWT.SHADOW_ETCHED_IN);
		groupServiceInterfaceDescription.setText("Service Interface Description");
		groupServiceInterfaceDescription.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));

		Composite serviceInterfaceDescriptionComposite = new Composite(groupServiceInterfaceDescription, SWT.NONE);
		GridLayout serviceInterfaceDescriptionCompositeLayout = new GridLayout(6, false);

		serviceInterfaceDescriptionCompositeLayout.marginTop = 10;
		serviceInterfaceDescriptionCompositeLayout.marginBottom = 10;
		serviceInterfaceDescriptionComposite.setLayout(serviceInterfaceDescriptionCompositeLayout);

		groupServiceInterfaceDescription.setLayout(serviceInterfaceDescriptionCompositeLayout);

		wsdlServiceInterfaceDescriptionButton = new Button(groupServiceInterfaceDescription, SWT.RADIO);
		wsdlServiceInterfaceDescriptionButton.setText("Web Services Description Language");
		wsdlServiceInterfaceDescriptionButton.setSelection(true);

		wadlServiceInterfaceDescriptionButton = new Button(groupServiceInterfaceDescription, SWT.RADIO);
		wadlServiceInterfaceDescriptionButton.setText("Web Application Description Language");

		coapServiceInterfaceDescriptionButton = new Button(groupServiceInterfaceDescription, SWT.RADIO);
		coapServiceInterfaceDescriptionButton.setText("General Interface Description Language");

		//////////////////// TODO CONTINUE

		/*
		 * serviceInterfaceDescription = new
		 * RadioGroupFieldEditor("serviceInterfaceDescription",
		 * "Serice Interface Description", 3, new String[][] { {
		 * "Web Services Description Language",
		 * InterfaceDescriptionType.WSDL.name()}, {
		 * "Web Application Description Language",
		 * InterfaceDescriptionType.WADL.name()},{
		 * "Constrained Application Protocol",
		 * InterfaceDescriptionType.COAP.name()}
		 * },serviceInterfaceDescriptionComposite,false);
		 * 
		 * 
		 * serviceInterfaceDescription. //gridLayout.verticalSpacing = 10;
		 * 
		 */

		// get preferences value for workspace or selected project if specified
		Map<String, ChorevolutionPreferenceData> projectOrWorkspacePreferencesValues = chorevolutionServiceThingSourceModelPrefs
				.readProjectOrWorkspacePreferences(null);

		List<ChorevolutionPreferenceData> servicethingModels = new ArrayList<ChorevolutionPreferenceData>();
		servicethingModels.add(projectOrWorkspacePreferencesValues
				.get(ChorevolutionServiceThingSourceModelPrefs.PREF_SERVICE_THING_INTERFACE_DESCRIPTION));
		servicethingModels.add(projectOrWorkspacePreferencesValues
				.get(ChorevolutionServiceThingSourceModelPrefs.PREF_SERVICE_THING_INTERACTIONPROTOCOL_DESCRIPTION));
		// servicethingModels.add(projectOrWorkspacePreferencesValues.get(ChorevolutionServiceThingSourceModelPrefs.PREF_SERVICE_THING_QOS_DESCRIPTION));
		// servicethingModels.add(projectOrWorkspacePreferencesValues.get(ChorevolutionServiceThingSourceModelPrefs.PREF_SERVICE_THING_IDENTITY_DESCRIPTION));
		servicethingModels.add(projectOrWorkspacePreferencesValues
				.get(ChorevolutionServiceThingSourceModelPrefs.PREF_SERVICE_THING_SECURITY_DESCRIPTION));

		createListenerTexts(Utilities.createGrupContents(container, "Service Thing Models", servicethingModels, true));

		// gridLayout.verticalSpacing = 10;

		Composite buttonBar = new Composite(container, SWT.NONE);
		GridLayout buttonBarLayout = new GridLayout();
		buttonBarLayout.numColumns = 1;
		buttonBarLayout.marginHeight = 0;
		buttonBarLayout.marginWidth = 0;
		buttonBarLayout.makeColumnsEqualWidth = false;
		buttonBar.setLayout(buttonBarLayout);

		buttonBarLayout.marginBottom = 0;

		buttonBar.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, true));

		resetButton = new Button(buttonBar, SWT.PUSH);
		resetButton.setText("Restore Defaults");
		resetButton.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, true));
		resetButton.addSelectionListener(resetListener);
		
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
		Map<String, ChorevolutionPreferenceData> projectOrWorkspacePreferencesValues = chorevolutionServiceThingSourceModelPrefs
				.readProjectOrWorkspacePreferences(null);

		for (Map.Entry<String, Text> entry : textWidgets.entrySet()) {
			ChorevolutionPreferenceData chorevolutionPreferenceData = new ChorevolutionPreferenceData(
					projectOrWorkspacePreferencesValues.get(entry.getKey()).getID(),
					projectOrWorkspacePreferencesValues.get(entry.getKey()).getLabel(), entry.getValue().getText(),
					projectOrWorkspacePreferencesValues.get(entry.getKey()).getDescriptionProperty());
			projectSettings.add(chorevolutionPreferenceData);
		}
		return projectSettings;

	}

	public ChorevolutionStructureFolder getStructureFolders() {
		Map<String, String> properties = new HashMap<String, String>();
		for (Map.Entry<String, Text> entry : textWidgets.entrySet()) {
			properties.put(entry.getKey(), entry.getValue().getText().trim());
		}
		return new ChorevolutionServiceThingProjectStructureFolder(properties);
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

	public void performDefaults() {
		// read workspace preferences
		Map<String, ChorevolutionPreferenceData> propertyValues = chorevolutionServiceThingSourceModelPrefs
				.readProjectOrWorkspacePreferences(null);
		for (Map.Entry<String, ChorevolutionPreferenceData> entry : propertyValues.entrySet()) {
			textWidgets.get(entry.getKey()).setText(entry.getValue().getValue());
			textWidgets.get(entry.getKey()).update();
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
					setErrorMessage(ChorevolutionServiceThingProjectWizardMessages.NewProject_folderStructureError);
				}
			}
		});
		textWidgets.put(key, text);
	}

	public InterfaceDescriptionType getServiceInterfaceDescription() {
		if (wsdlServiceInterfaceDescriptionButton.getSelection())
			return InterfaceDescriptionType.WSDL;
		if (wadlServiceInterfaceDescriptionButton.getSelection())
			return InterfaceDescriptionType.WADL;
		if (coapServiceInterfaceDescriptionButton.getSelection())
			return InterfaceDescriptionType.GIDL;
		return null;
	}

}
