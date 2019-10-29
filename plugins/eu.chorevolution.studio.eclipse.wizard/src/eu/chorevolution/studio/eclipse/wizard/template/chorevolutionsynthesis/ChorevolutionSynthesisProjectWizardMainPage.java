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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.syncope.common.lib.to.AnyObjectTO;
import org.apache.syncope.common.lib.to.AttrTO;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.internal.debug.ui.jres.JREsPreferencePage;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import eu.chorevolution.studio.eclipse.core.ChorevolutionCoreUtils;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPreferenceData;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServicesURIPrefs;
import eu.chorevolution.studio.eclipse.core.utils.syncope.ApacheSyncopeUtilities;
import eu.chorevolution.studio.eclipse.core.utils.syncope.SynthesisProcessor;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIMessages;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;
import eu.chorevolution.studio.eclipse.wizard.template.Utilities;
import eu.chorevolution.studio.eclipse.wizard.template.bpmn.BPMN2DiagramWizardMessages;

public class ChorevolutionSynthesisProjectWizardMainPage extends WizardNewProjectCreationPage {

	private static final String PAGE_NAME = ChorevolutionSynthesisProjectWizardMessages.NewProject_mainPageName;

	private ISelection selection;
	private Button embeddedSynthesisProcessorButton;
	private Button restSynthesisProcessorButton;
	private Map<String, Text> textWidgets;
	private Button fUseDefaultJRE;
	
	private Text textFilterSynthesis;
	private Button filterSynthesisButton;
	private TreeViewer treeViewerAllSynthesis;	
	private Vector<SynthesisProcessor> allSynthesisRecords;
	private SynthesisProcessor selectedSynthesisProcessor;
	private Map<String, ChorevolutionPreferenceData> propertyValues;
	private Group groupFilterSynthesis;
	private Group groupChoreography;
	private Group groupConnectToInventory;

	private Text textServiceInventoryURL;
	private Text textServiceInventoryUsername;
	private Text textServiceInventoryPassword;
	private Text textServiceInventoryDomain;
	
	public ChorevolutionSynthesisProjectWizardMainPage(ISelection selection) {
		super(PAGE_NAME);
		this.selection = selection;
		setTitle(ChorevolutionSynthesisProjectWizardMessages.NewProject_projectTitle);
		setDescription(ChorevolutionSynthesisProjectWizardMessages.NewProject_projectDescription);
		textWidgets = new HashMap<String, Text>();

		allSynthesisRecords = new Vector<SynthesisProcessor>();
		selectedSynthesisProcessor = null;
		
	}
	
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		
		Control control = this.getControl();
		
		try {
			GridLayout noSpace = new GridLayout();
			noSpace.marginBottom = -80;//needed to align "restore default" to the bottom
			control.getParent().getParent().setLayout(noSpace);
		}
		catch(Exception e) {
			
		}
		
		if (control instanceof Composite) {

			Composite composite = (Composite) control;
			GridLayout layout = new GridLayout();
			composite.setLayout(layout);
			
			Map<String, ChorevolutionPreferenceData> projectOrWorkspacePreferencesValues = (new ChorevolutionServicesURIPrefs())
					.readProjectOrWorkspacePreferences(null);
			
			//choreography name
			groupChoreography = new Group(composite, SWT.SHADOW_ETCHED_IN);
			groupChoreography.setText("Choreography Data");
			groupChoreography.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
			GridLayout layoutChoreography = new GridLayout();
			layoutChoreography.numColumns = 3;
			layoutChoreography.horizontalSpacing = 8;
			groupChoreography.setLayout(layoutChoreography);
			
			List<ChorevolutionPreferenceData> choreographyData = new ArrayList<ChorevolutionPreferenceData>();
			choreographyData.add(projectOrWorkspacePreferencesValues
					.get(ChorevolutionServicesURIPrefs.PREF_CHOREOGRAPHY_NAME));
			choreographyData.add(projectOrWorkspacePreferencesValues
					.get(ChorevolutionServicesURIPrefs.PREF_CHOREOGRAPHY_NAMESPACE));	
			createListenerTexts(Utilities.createGrupContents(groupChoreography, "", choreographyData, false));
						
			// JDK composite
			Group fGroup= new Group(composite, SWT.SHADOW_ETCHED_IN);
			fGroup.setFont(composite.getFont());
			fGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			GridLayout gridLayoutfGroup = new GridLayout(2, false);
			gridLayoutfGroup.marginTop = 10;
			gridLayoutfGroup.marginBottom = 10;
			fGroup.setLayout(gridLayoutfGroup);
			//fGroup.setText(NewWizardMessages.NewJavaProjectWizardPageOne_JREGroup_title);
			fGroup.setText("Configure JDK");

			fUseDefaultJRE= new Button(fGroup, SWT.RADIO);
			fUseDefaultJRE.setText("Use default JDK (currently '"+getDefaultJVMName()+"')");
			fUseDefaultJRE.setSelection(true);
			
			
			Link fPreferenceLink= new Link(fGroup, SWT.NONE);
			fPreferenceLink.setFont(fGroup.getFont());
			//fPreferenceLink.setText(NewWizardMessages.NewJavaProjectWizardPageOne_JREGroup_link_description);
			fPreferenceLink.setText("<a>Configure...</a>");
			fPreferenceLink.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
			fPreferenceLink.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					ChorevolutionCoreUtils.showPrefPage(JREsPreferencePage.ID);
					fUseDefaultJRE.setText("Use default JDK (currently '"+getDefaultJVMName()+"')");
				}
			});		
			

			// Connect to the Service inventory
			groupConnectToInventory = new Group(composite, SWT.SHADOW_ETCHED_IN);
			groupConnectToInventory.setText("Apache Syncope");
			groupConnectToInventory.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
			GridLayout layoutServiceInventory = new GridLayout();
			layoutServiceInventory.marginHeight = 3;
			layoutServiceInventory.marginWidth = 3;
			layoutServiceInventory.numColumns = 2;
			groupConnectToInventory.setLayout(layoutServiceInventory);
			
			Label labelServiceInventoryURL = new Label(groupConnectToInventory, SWT.NONE);
			labelServiceInventoryURL.setText("Service Inventory URI:");

			propertyValues = (new ChorevolutionServicesURIPrefs()).readProjectOrWorkspacePreferences(null);//null passed instead of project
			textServiceInventoryURL = new Text(groupConnectToInventory, SWT.BORDER);
			textServiceInventoryURL.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			textServiceInventoryURL
					.setText(propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_URI).getValue());

			Label labelServiceInventoryUsername = new Label(groupConnectToInventory, SWT.NONE);
			labelServiceInventoryUsername.setText("Username:");
			textServiceInventoryUsername = new Text(groupConnectToInventory, SWT.BORDER);
			textServiceInventoryUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			textServiceInventoryUsername
					.setText(propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_USERNAME).getValue());

			Label labelServiceInventoryPassword = new Label(groupConnectToInventory, SWT.NONE);
			labelServiceInventoryPassword.setText("Password:");
			textServiceInventoryPassword = new Text(groupConnectToInventory, SWT.BORDER);
			textServiceInventoryPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			textServiceInventoryPassword
					.setText(propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_PASSWORD).getValue());

			Label labelServiceInventoryDomain = new Label(groupConnectToInventory, SWT.NONE);
			labelServiceInventoryDomain.setText("Domain:");
			textServiceInventoryDomain = new Text(groupConnectToInventory, SWT.BORDER);
			textServiceInventoryDomain.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			textServiceInventoryDomain
					.setText(propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_DOMAIN).getValue());
			
			
			//All Synthesis
			groupFilterSynthesis = new Group(composite, SWT.SHADOW_ETCHED_IN);
			groupFilterSynthesis.setText("Synthesis Processor Configuration");
			groupFilterSynthesis.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
			GridLayout layoutFilterSynthesis = new GridLayout();
			layoutFilterSynthesis.numColumns = 3;
			layoutFilterSynthesis.horizontalSpacing = 8;
			groupFilterSynthesis.setLayout(layoutFilterSynthesis);
			
		
			
			Label labelSynthesisSearchname = new Label(groupFilterSynthesis, SWT.NONE);
			labelSynthesisSearchname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			labelSynthesisSearchname.setText("Filter by name:");

			textFilterSynthesis = new Text(groupFilterSynthesis, SWT.BORDER);
			textFilterSynthesis.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

			filterSynthesisButton = new Button(groupFilterSynthesis, SWT.PUSH);
			filterSynthesisButton.setText("Search");
			filterSynthesisButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			filterSynthesisButton.addSelectionListener(searchListenerSynthesis);

			treeViewerAllSynthesis = new TreeViewer(groupFilterSynthesis, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
			treeViewerAllSynthesis.setContentProvider(new ViewContentProviderSynthesis());
			treeViewerAllSynthesis.setLabelProvider(
					new DelegatingStyledCellLabelProvider(new ViewLabelProvider(createImageDescriptor())));

			GridData gridDataTreeViewerAllSynthesis = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
			gridDataTreeViewerAllSynthesis.widthHint = new PixelConverter(treeViewerAllSynthesis.getControl())
					.convertWidthInCharsToPixels(60);
			gridDataTreeViewerAllSynthesis.heightHint = new PixelConverter(treeViewerAllSynthesis.getControl())
					.convertHeightInCharsToPixels(3);
			treeViewerAllSynthesis.getControl().setLayoutData(gridDataTreeViewerAllSynthesis);
			treeViewerAllSynthesis.addSelectionChangedListener(new SynthesisSelectionChangedListener());
			treeViewerAllSynthesis.setInput(allSynthesisRecords);

			/*
			embeddedSynthesisProcessorButton = new Button(group, SWT.RADIO);
			embeddedSynthesisProcessorButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
			embeddedSynthesisProcessorButton.setText("Internal");
			embeddedSynthesisProcessorButton.setSelection(false);
			embeddedSynthesisProcessorButton.addSelectionListener(selectSynthesisProcessorConfiguration);
			
			restSynthesisProcessorButton = new Button(group, SWT.RADIO);
			restSynthesisProcessorButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
			restSynthesisProcessorButton.setText("External (REST)");
			restSynthesisProcessorButton.setSelection(true);
			restSynthesisProcessorButton.addSelectionListener(selectSynthesisProcessorConfiguration);
*/
			
			List<ChorevolutionPreferenceData> synthesisProcessorRestLoginData = new ArrayList<ChorevolutionPreferenceData>();
			synthesisProcessorRestLoginData.add(projectOrWorkspacePreferencesValues
					.get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_USERNAME));
			synthesisProcessorRestLoginData.add(projectOrWorkspacePreferencesValues
					.get(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_PASSWORD));			

			createListenerTexts(Utilities.createGrupContents(groupFilterSynthesis, "", synthesisProcessorRestLoginData, false));
			
			initializeFields();
			enableOrDisableSynthesisProcessorURIs(true);
		}
	}

	private SelectionListener selectSynthesisProcessorConfiguration = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if ((Button) e.widget == embeddedSynthesisProcessorButton) {
				enableOrDisableSynthesisProcessorURIs(false);
				setErrorMessage(null);
			} else if ((Button) e.widget == restSynthesisProcessorButton) {
				enableOrDisableSynthesisProcessorURIs(true);
			}
		}
	};

	private void initializeFields() {
		Map<String, ChorevolutionPreferenceData> projectOrWorkspacePreferencesValues = (new ChorevolutionServicesURIPrefs())
				.readProjectOrWorkspacePreferences(null);
		for(String textWidgetsName : textWidgets.keySet()) {
			if(textWidgetsName.equals(ChorevolutionServicesURIPrefs.PREF_CHOREOGRAPHY_NAMESPACE))
				textWidgets.get(textWidgetsName).setText(textWidgets.get(textWidgetsName).getText()+"."+projectOrWorkspacePreferencesValues
						.get(ChorevolutionServicesURIPrefs.PREF_CHOREOGRAPHY_NAME).getValue());
		}
	}
	
	private void enableOrDisableSynthesisProcessorURIs(boolean enabled) {
		for(String textWidgetsName : textWidgets.keySet()) {
			if(!textWidgetsName.equals(ChorevolutionServicesURIPrefs.PREF_CHOREOGRAPHY_NAME))
				textWidgets.get(textWidgetsName).setEnabled(enabled);
			if(!textWidgetsName.equals(ChorevolutionServicesURIPrefs.PREF_CHOREOGRAPHY_NAMESPACE))
				textWidgets.get(textWidgetsName).setEnabled(enabled);			
			if(textWidgetsName.equals(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_USERNAME))
				textWidgets.get(textWidgetsName).setEnabled(false);
			if(textWidgetsName.equals(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_PASSWORD))
				textWidgets.get(textWidgetsName).setEnabled(false);
		}
		textServiceInventoryURL.setEnabled(enabled);
		textServiceInventoryUsername.setEnabled(enabled);
		textServiceInventoryPassword.setEnabled(enabled);
		textServiceInventoryDomain.setEnabled(enabled);
		textFilterSynthesis.setEnabled(enabled);
		treeViewerAllSynthesis.getControl().setEnabled(enabled);
		validateWizardPage();
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
				validateWizardPage();
			}
		});
		textWidgets.put(key, text);
	}

	private boolean validateWizardPage() {
		for (String value : textWidgets.keySet()) {
			if (textWidgets.get(value).getText().trim().isEmpty()) {
				setPageComplete(false);
				setErrorMessage(ChorevolutionSynthesisProjectWizardMessages.NewProject_synthesisProcessorURIError);
				return false;
			}
			if(value.equals(ChorevolutionServicesURIPrefs.PREF_CHOREOGRAPHY_NAMESPACE)) {
				String targetNamespace = textWidgets.get(value).getText();
		
				if (targetNamespace==null || targetNamespace.isEmpty()) {
					setErrorMessage(BPMN2DiagramWizardMessages.BPMN2DiagramWizardPage_error_No_TargetNamespace);
					return false;
				}
				URI uri = URI.createURI(targetNamespace);
				if (!(uri.hasAuthority() && uri.scheme()!=null)) {
					setErrorMessage(BPMN2DiagramWizardMessages.BPMN2DiagramWizardPage_error_Invalid_TargetNamespace);
					return false;
				}
			}

		}
		
		
		if(!isSynthesisProcessorEmbeddedMode()) {
			IStructuredSelection selection = treeViewerAllSynthesis.getStructuredSelection();
			
			if (!selection.isEmpty()) {
				Object selectedElement = selection.getFirstElement();
				if (selectedElement instanceof SynthesisProcessor) {
					setPageComplete(true);
					setErrorMessage(null);
					return true;
				}
			} else {
				setPageComplete(false);
				setErrorMessage(ChorevolutionSynthesisProjectWizardMessages.NewProject_synthesisProcessorChoiceError);
				return false;
			}
		
		}
		setPageComplete(false);
		setErrorMessage(ChorevolutionSynthesisProjectWizardMessages.NewProject_synthesisProcessorURIError);
		return false;
		
	}


	private void performDefaults() {
		// read workspace preferences
		Map<String, ChorevolutionPreferenceData> propertyValues = (new ChorevolutionServicesURIPrefs())
				.readProjectOrWorkspacePreferences(null);
		for (Map.Entry<String, ChorevolutionPreferenceData> entry : propertyValues.entrySet()) {
			if (textWidgets.get(entry.getKey()) != null) {
				textWidgets.get(entry.getKey()).setText(entry.getValue().getValue());
				textWidgets.get(entry.getKey()).update();
			}
		}

	}
	
	private String getDefaultJVMName() {
		IVMInstall install= JavaRuntime.getDefaultVMInstall();
		if (install != null) {
			return install.getName();
		} else {
			return NewWizardMessages.NewJavaProjectWizardPageOne_UnknownDefaultJRE_name;
		}
	}
	

	
	private List<SynthesisProcessor> getAllSynthesis(String filter) {
		List<SynthesisProcessor> results = new ArrayList<SynthesisProcessor>();

		ApacheSyncopeUtilities syncopeUtilities = new ApacheSyncopeUtilities(textServiceInventoryURL.getText(),
				textServiceInventoryUsername.getText(), textServiceInventoryPassword.getText(),
				textServiceInventoryDomain.getText());

		List<AnyObjectTO> synthesisProcessors = syncopeUtilities
				.getSynthesisProcessorsByNameContains(textFilterSynthesis.getText());

		for (AnyObjectTO synthesis : synthesisProcessors) {
			SynthesisProcessor synthesisProcessor = new SynthesisProcessor();
			synthesisProcessor.setKey(synthesis.getKey());
			synthesisProcessor.setName(synthesis.getName());

			Iterator iterator = synthesis.getPlainAttrs().iterator();
			while (iterator.hasNext()) {
				AttrTO attr = (AttrTO) iterator.next();
				if (attr.getSchema().equalsIgnoreCase(ApacheSyncopeUtilities.SYNTHESIS_PROCESSOR_URL)) {
					synthesisProcessor.setLocationURIs(attr.getValues().get(0));
				}

			}
			results.add(synthesisProcessor);
		}

		return results;

	}
	
	public List<ChorevolutionPreferenceData> getSynthesisProcessorURIs() {
		List<ChorevolutionPreferenceData> projectSettings = new ArrayList<ChorevolutionPreferenceData>();
		// get preferences value for workspace or selected project if specified
		Map<String, ChorevolutionPreferenceData> projectOrWorkspacePreferencesValues = (new ChorevolutionServicesURIPrefs())
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
	
	private SelectionListener searchListenerSynthesis = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {

			if ((Button) e.widget == filterSynthesisButton) {
				try {
					List<SynthesisProcessor> synthesis = getAllSynthesis(textFilterSynthesis.getText());

					removeAllSynthesisRecord();
					for (SynthesisProcessor snth : synthesis) {
						addSynthesisRecord(snth);
					}

				} catch (Exception e1) {
					// TODO adjust this
					// error
					MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
							ChorevolutionUIMessages.ServiceInventory_errorMessage,"Error in loading Synthesis Processor");
					return;
				}
			}
		}
	};
	
	
	private class SynthesisSelectionChangedListener implements ISelectionChangedListener {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection selection = treeViewerAllSynthesis.getStructuredSelection();
			if (!selection.isEmpty()) {
				Object selectedElement = selection.getFirstElement();
				if (selectedElement instanceof SynthesisProcessor) {
					selectedSynthesisProcessor = (SynthesisProcessor) selectedElement;
				}
				
				for(String textWidgetsName : textWidgets.keySet()) {
					if(textWidgetsName.equals(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_USERNAME))
						textWidgets.get(textWidgetsName).setEnabled(true);
					if(textWidgetsName.equals(ChorevolutionServicesURIPrefs.PREF_SYNTHESIS_PROCESSOR_PASSWORD))
						textWidgets.get(textWidgetsName).setEnabled(true);
				}

				
			}

			validateWizardPage();		
		}

	}
	
	private void addSynthesisRecord(SynthesisProcessor serviceRecord) {
		allSynthesisRecords.add(serviceRecord);
		treeViewerAllSynthesis.refresh(false);
	}

	private void removeAllSynthesisRecord() {
		allSynthesisRecords.removeAllElements();
		treeViewerAllSynthesis.refresh(false);
	}
	
	
	private class ViewLabelProvider extends LabelProvider implements IStyledLabelProvider {

		private ImageDescriptor imageDescriptor;
		private ResourceManager resourceManager;

		public ViewLabelProvider(ImageDescriptor imageDescriptor) {
			this.imageDescriptor = imageDescriptor;
		}

		@Override
		public StyledString getStyledText(Object element) {
			if (element instanceof SynthesisProcessor) {
				SynthesisProcessor synthesis = (SynthesisProcessor) element;
				return new StyledString(
						synthesis.getName() + " - " + synthesis.getLocation() + " (ID: " + synthesis.getKey() + ")");
			}
			return null;
		}

		@Override
		public Image getImage(Object element) {
			return getResourceManager().createImage(imageDescriptor);
		}

		@Override
		public void dispose() {
			
			
			// garbage collect system resources
			if (resourceManager != null) {
				resourceManager.dispose();
				resourceManager = null;
			}
		}

		protected ResourceManager getResourceManager() {
			if (resourceManager == null) {
				resourceManager = new LocalResourceManager(JFaceResources.getResources());
			}
			return resourceManager;
		}

	}
	
	private class ViewContentProviderSynthesis implements ITreeContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement != null && inputElement instanceof Vector) {
				return ((Vector<SynthesisProcessor>) inputElement).toArray();
			}
			return new Object[0];
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			return null;
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return false;
		}

	}
	
	private ImageDescriptor createImageDescriptor() {
		return ChorevolutionUIPlugin.getImageDescriptor("icons/full/obj16/workingsets_obj.gif");
	}

	
	
	public boolean isSynthesisProcessorEmbeddedMode() {
		//if (embeddedSynthesisProcessorButton.getSelection()) {
		//	return true;
		//} else {
			return false;//DELETE, only embedded
		//}
	}
	
	public SynthesisProcessor getSelectedSynthesis() {
		return selectedSynthesisProcessor;
	}

	public String getChoreographyNamespace() {
		for(String textWidgetsName : textWidgets.keySet()) {
			if(textWidgetsName.equals(ChorevolutionServicesURIPrefs.PREF_CHOREOGRAPHY_NAMESPACE))
				return textWidgets.get(textWidgetsName).getText();
		}
		return null;
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return super.validatePage();
	}

}
