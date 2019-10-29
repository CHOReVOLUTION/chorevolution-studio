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
package eu.chorevolution.studio.eclipse.ui.handlers.dialogs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.syncope.common.lib.to.AnyObjectTO;
import org.apache.syncope.common.lib.to.AttrTO;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import eu.chorevolution.idm.common.to.ChoreographyTO;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPreferenceData;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServicesURIPrefs;
import eu.chorevolution.studio.eclipse.core.utils.syncope.ApacheSyncopeUtilities;
import eu.chorevolution.studio.eclipse.core.utils.syncope.ChoreographyToDownload;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIMessages;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;

public class DownloadChoreographyFromSyncopeDialog extends TitleAreaDialog {
	// private Image image;

	private Text textServiceInventoryURL;
	private Text textServiceInventoryUsername;
	private Text textServiceInventoryPassword;
	private Text textServiceInventoryDomanin;

	private Text textFilterChoreography;
	private Button filterChoreographyButton;
	private TreeViewer treeViewerAllChoreography;
	private Vector<ChoreographyToDownload> allChoreographyRecords;	
	private ChoreographyToDownload selectedChoreography;
	private Map<String, ChorevolutionPreferenceData> propertyValues;
	private IProject project;
	private Group groupFilterChoreography;
	private ApacheSyncopeUtilities syncopeUtilities;

	
	public DownloadChoreographyFromSyncopeDialog(Shell shell, IProject project) {
		super(shell);
		this.project = project;
		allChoreographyRecords = new Vector<ChoreographyToDownload>();
		selectedChoreography = null;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(ChorevolutionUIMessages.ChorevolutionSynthesisProcessor_title);

	}

	/*
	 * public boolean close() { if (image != null) image.dispose(); return
	 * super.close(); }
	 */
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);

		setTitle("Import Choreography From The IdM");

		setMessage("Import a complete Choreography from the IdM.", IMessageProvider.INFORMATION);

		/*
		 * if (image != null) setTitleImage(image);
		 */

		return contents;
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 3;
		layout.marginWidth = 3;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Connect to the Service inventory
		Group groupConnectToInventory = new Group(composite, SWT.SHADOW_ETCHED_IN);
		groupConnectToInventory.setText("");
		groupConnectToInventory.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
		GridLayout layoutServiceInventory = new GridLayout();
		layoutServiceInventory.marginHeight = 3;
		layoutServiceInventory.marginWidth = 3;
		layoutServiceInventory.numColumns = 2;
		groupConnectToInventory.setLayout(layoutServiceInventory);

		Label labelServiceInventoryURL = new Label(groupConnectToInventory, SWT.NONE);
		labelServiceInventoryURL.setText("Service Inventory URI:");

		propertyValues = (new ChorevolutionServicesURIPrefs()).readProjectOrWorkspacePreferences(project);
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
		textServiceInventoryDomanin = new Text(groupConnectToInventory, SWT.BORDER);
		textServiceInventoryDomanin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textServiceInventoryDomanin
				.setText(propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_DOMAIN).getValue());
		
		
		// All Choreography
		groupFilterChoreography = new Group(composite, SWT.SHADOW_ETCHED_IN);
		groupFilterChoreography.setText("Select a Choreography");
		groupFilterChoreography.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
		GridLayout layoutFilterChoreography = new GridLayout();
		layoutFilterChoreography.numColumns = 3;
		layoutFilterChoreography.horizontalSpacing = 8;
		groupFilterChoreography.setLayout(layoutFilterChoreography);
		
	
		
		Label labelChoreographySearchname = new Label(groupFilterChoreography, SWT.NONE);
		labelChoreographySearchname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		labelChoreographySearchname.setText("Filter by name:");

		textFilterChoreography = new Text(groupFilterChoreography, SWT.BORDER);
		textFilterChoreography.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

		filterChoreographyButton = new Button(groupFilterChoreography, SWT.PUSH);
		filterChoreographyButton.setText("Search");
		filterChoreographyButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		filterChoreographyButton.addSelectionListener(searchListener);

		treeViewerAllChoreography = new TreeViewer(groupFilterChoreography, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		treeViewerAllChoreography.setContentProvider(new ViewContentProvider());
		treeViewerAllChoreography.setLabelProvider(
				new DelegatingStyledCellLabelProvider(new ViewLabelProvider(createImageDescriptor())));

		GridData gridDataTreeViewerAllChoreographies = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		gridDataTreeViewerAllChoreographies.widthHint = new PixelConverter(treeViewerAllChoreography.getControl())
				.convertWidthInCharsToPixels(60);
		gridDataTreeViewerAllChoreographies.heightHint = new PixelConverter(treeViewerAllChoreography.getControl())
				.convertHeightInCharsToPixels(3);
		treeViewerAllChoreography.getControl().setLayoutData(gridDataTreeViewerAllChoreographies);
		treeViewerAllChoreography.addSelectionChangedListener(new ChoreographySelectionChangedListener());
		treeViewerAllChoreography.setInput(allChoreographyRecords);
		
		
		return composite;
	}

	
	
	private ImageDescriptor createImageDescriptor() {
		return ChorevolutionUIPlugin.getImageDescriptor("icons/full/obj16/workingsets_obj.gif");
	}

	private class ViewContentProvider implements ITreeContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement != null && inputElement instanceof Vector) {
				return ((Vector<ChoreographyToDownload>) inputElement).toArray();
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

	private class ViewLabelProvider extends LabelProvider implements IStyledLabelProvider {

		private ImageDescriptor imageDescriptor;
		private ResourceManager resourceManager;

		public ViewLabelProvider(ImageDescriptor imageDescriptor) {
			this.imageDescriptor = imageDescriptor;
		}

		@Override
		public StyledString getStyledText(Object element) {
			if (element instanceof ChoreographyToDownload) {
				ChoreographyToDownload choreography = (ChoreographyToDownload) element;
				return new StyledString(
						choreography.getName() + " - (ID: " + choreography.getKey() + ")");
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

	
	

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}

	
	/* Listeners */
	private SelectionListener searchListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {

			if ((Button) e.widget == filterChoreographyButton) {
				try {
					List<ChoreographyToDownload> choreographies = getAllChoreographies(textFilterChoreography.getText());
					
					removeAllChoreographyRecord();
					for (ChoreographyToDownload choreography : choreographies) {
						addChoreographyRecord(choreography);
					}

				} catch (Exception e1) {
					// TODO adjust this
					// error
					MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
							"Choreography Import Error", "Error in Downloading Choreography");
					/*
					 * MessageDialog.openError( ChorevolutionUIPlugin.
					 * getActiveWorkbenchShell(), ChorevolutionUIMessages.
					 * ServiceInventory_infoMessage,
					 * NLS.bind(ChorevolutionUIMessages.
					 * ServiceInventory_connectionRefused,
					 * textServiceInventoryURL.getText()));
					 */
					// here the task 1 is performed
					return;
				}
			}
		}
	};

	private List<ChoreographyToDownload> getAllChoreographies(String filter) {
		List<ChoreographyToDownload> results = new ArrayList<ChoreographyToDownload>();

		syncopeUtilities = new ApacheSyncopeUtilities(textServiceInventoryURL.getText(),
				textServiceInventoryUsername.getText(), textServiceInventoryPassword.getText(),
				textServiceInventoryDomanin.getText());

		List<ChoreographyTO> choreographies = syncopeUtilities.getChoreographyListByName(textFilterChoreography.getText());

		for (ChoreographyTO choreography : choreographies) {
			
			ChoreographyToDownload choreographyToDownload = new ChoreographyToDownload();

			choreographyToDownload.setKey(choreography.getKey());
			choreographyToDownload.setName(choreography.getName());

			results.add(choreographyToDownload);
		}

		return results;

	}


	
	private void validateEnableOrDisableOKButton() {
		IStructuredSelection selection = treeViewerAllChoreography.getStructuredSelection();
		if (!selection.isEmpty()) {
			Object selectedElement = selection.getFirstElement();
			if (selectedElement instanceof ChoreographyToDownload) {
				getButton(IDialogConstants.OK_ID).setEnabled(true);
				getButton(IDialogConstants.OK_ID).setFocus();
				setMessage("Import the selected Choreography.");
			}
		} else {
			getButton(IDialogConstants.OK_ID).setEnabled(false);
			setMessage("Select one Choreography.", IMessageProvider.ERROR);
		}
	}

	
	private void addChoreographyRecord(ChoreographyToDownload serviceRecord) {
		allChoreographyRecords.add(serviceRecord);
		treeViewerAllChoreography.refresh(false);
	}

	private void removeAllChoreographyRecord() {
		allChoreographyRecords.removeAllElements();
		treeViewerAllChoreography.refresh(false);
	}

	private class ChoreographySelectionChangedListener implements ISelectionChangedListener {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection selection = treeViewerAllChoreography.getStructuredSelection();
			if (!selection.isEmpty()) {
				Object selectedElement = selection.getFirstElement();
				if (selectedElement instanceof ChoreographyToDownload) {
					selectedChoreography = (ChoreographyToDownload) selectedElement;
				}
			}

			validateEnableOrDisableOKButton();
		}

	}
	
	
	
	
	
	public ChoreographyToDownload getSelectedChoreography() {
		return selectedChoreography;
	}

	
	public String getServiceInventoryURL() {
		return propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_URI).getValue();
	}

	public String getServiceInventoryUsername() {
		return propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_USERNAME).getValue();
	}

	public String getServiceInventoryPassword() {
		return propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_PASSWORD).getValue();
	}

	public String getServiceInventoryDomain() {
		return propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_DOMAIN).getValue();
	}
	

	@Override
	protected boolean isResizable() {
		return true;
	}
}
