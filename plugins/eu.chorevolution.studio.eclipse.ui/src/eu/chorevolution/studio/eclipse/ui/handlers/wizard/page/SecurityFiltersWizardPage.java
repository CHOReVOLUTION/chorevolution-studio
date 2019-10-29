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
package eu.chorevolution.studio.eclipse.ui.handlers.wizard.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.syncope.common.lib.to.GroupTO;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPreferenceData;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServicesURIPrefs;
import eu.chorevolution.studio.eclipse.core.utils.syncope.ApacheSyncopeUtilities;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIMessages;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.composite.SecuredProviderParticipantsTableViewerComposite;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.ParticipantTableViewerRecord;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.SecurityFilter;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.SecurityFilter.AuthenticationType;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.SecurityFilter.CommunicationType;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.SecurityRole;

public class SecurityFiltersWizardPage extends WizardPage {

	private SecurityFilter securityFilterModel;
	private IProject project;
	private Map<String, ChorevolutionPreferenceData> propertyValues;
	private Image synthesisProcessorImageStep;
	private Button securedChoreographyYesButton;
	private Button securedChoreographyNoButton;

	// private ComboViewer authenticationComboViewer;

	// authorization part
	private Text textFilterSecurityRole;
	private Button filterSecurityRoleButton;
	private TreeViewer treeViewerAllSecurityRoles;
	private TreeViewer treeViewerSelectedSecurityRoles;
	private Vector<SecurityRole> allSecurityRoleRecords;
	// private Vector<SecurityRole> selectedSecurityRoleRecords;
	private Button moveToSelectedRoleButton;
	private Button moveToAvailableRoleButton;

	private ComboViewer communicationComboViewer;

	private SecuredProviderParticipantsTableViewerComposite securedProviderServiceComposite;

	public SecurityFiltersWizardPage(IProject project, List<ParticipantTableViewerRecord> providerParticipants) {
		super(ChorevolutionUIMessages.SecurityFiltersWizardPage_name);
		setTitle(ChorevolutionUIMessages.SecurityFiltersWizardPage_title);
		setMessage(ChorevolutionUIMessages.SecurityFiltersWizardPage_description, IMessageProvider.INFORMATION);
		  this.synthesisProcessorImageStep = ChorevolutionUIPlugin.getImageDescriptor("icons/full/wizban/synthesis_processor/securityFiltersWizardPage.png").createImage();

		this.securedProviderServiceComposite = new SecuredProviderParticipantsTableViewerComposite(this,
				providerParticipants);
		this.securityFilterModel = new SecurityFilter(false);
		this.project = project;
		this.allSecurityRoleRecords = new Vector<SecurityRole>();

		propertyValues = (new ChorevolutionServicesURIPrefs()).readProjectOrWorkspacePreferences(project);
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 3;
		layout.marginWidth = 3;
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = false;
		composite.setLayout(layout);

		setControl(composite);
		
		Composite compositeLeft = new Composite(composite, SWT.NONE);
		GridLayout layoutLeft = new GridLayout();
		layoutLeft.marginHeight = 3;
		layoutLeft.marginWidth = 3;
		compositeLeft.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		compositeLeft.setLayout(layoutLeft);

		// create secured provider
		this.securedProviderServiceComposite.createPartControl(compositeLeft);

		Composite groupSecuredChoreography = new Composite(compositeLeft, SWT.NONE);
		GridLayout layoutSecuredChoreography = new GridLayout();
		layoutSecuredChoreography.marginHeight = 3;
		layoutSecuredChoreography.marginWidth = 3;
		layoutSecuredChoreography.numColumns = 3;
		layoutSecuredChoreography.horizontalSpacing = 8;
		groupSecuredChoreography.setLayout(layoutSecuredChoreography);

		Label securedChoreographyLabel = new Label(groupSecuredChoreography, SWT.NONE);
		securedChoreographyLabel.setText("Secured Choreography ?");

		securedChoreographyNoButton = new Button(groupSecuredChoreography, SWT.RADIO);
		securedChoreographyNoButton.setText("No");
		securedChoreographyNoButton.setSelection(true);
		securedChoreographyNoButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					enableOrDisableSecuredChoreographyInformation(false);
					break;
				}
			}
		});

		securedChoreographyYesButton = new Button(groupSecuredChoreography, SWT.RADIO);
		securedChoreographyYesButton.setText("Yes");
		securedChoreographyYesButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					enableOrDisableSecuredChoreographyInformation(true);
					break;
				}
			}
		});

		Composite groupAuthentication = new Composite(compositeLeft, SWT.SHADOW_ETCHED_IN);
		GridLayout layoutAuthentication = new GridLayout();
		layoutAuthentication.marginHeight = 3;
		layoutAuthentication.marginWidth = 3;
		layoutAuthentication.numColumns = 2;
		layoutAuthentication.horizontalSpacing = 8;
		groupAuthentication.setLayout(layoutAuthentication);
		/*
		 * Label authenticationLabel = new Label(groupAuthentication, SWT.NONE);
		 * authenticationLabel.setText("Authentication:");
		 * 
		 * authenticationComboViewer = new ComboViewer(groupAuthentication,
		 * SWT.READ_ONLY);
		 * authenticationComboViewer.setContentProvider(ArrayContentProvider.
		 * getInstance()); authenticationComboViewer.setLabelProvider(new
		 * LabelProvider() { public String getText(Object element) { return
		 * ((AuthenticationType) element).getLabel(); } });
		 * authenticationComboViewer.addSelectionChangedListener(new
		 * ISelectionChangedListener() {
		 * 
		 * @Override public void selectionChanged(SelectionChangedEvent event) {
		 * IStructuredSelection selection = (IStructuredSelection)
		 * event.getSelection(); AuthenticationType selectedObject =
		 * (AuthenticationType) selection.getFirstElement();
		 * securityFilterModel.setAuthenticationType(selectedObject); } });
		 * authenticationComboViewer.setInput(AuthenticationType.values());
		 * authenticationComboViewer.setSelection(new
		 * StructuredSelection(securityFilterModel.getAuthenticationType()));
		 */
		Group groupAuthorization = new Group(compositeLeft, SWT.SHADOW_ETCHED_IN);
		groupAuthorization.setText("Authorization");
		groupAuthorization.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
		GridLayout layoutAuthorization = new GridLayout();
		layoutAuthorization.marginHeight = 3;
		layoutAuthorization.marginWidth = 3;
		layoutAuthorization.numColumns = 1;
		groupAuthorization.setLayout(layoutAuthorization);

		Composite groupSecurityRoles = new Composite(groupAuthorization, SWT.SHADOW_ETCHED_IN);
		groupSecurityRoles.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
		GridLayout layoutSecurityRoles = new GridLayout();
		layoutSecurityRoles.marginHeight = 3;
		layoutSecurityRoles.marginWidth = 3;
		layoutSecurityRoles.numColumns = 3;
		layoutSecurityRoles.horizontalSpacing = 8;
		groupSecurityRoles.setLayout(layoutSecurityRoles);

		Group groupFilterRole = new Group(groupSecurityRoles, SWT.SHADOW_ETCHED_IN);
		groupFilterRole.setText("Available Security Roles");
		groupFilterRole.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
		GridLayout layoutFilterRole = new GridLayout();
		layoutFilterRole.numColumns = 3;
		layoutFilterRole.horizontalSpacing = 8;
		groupFilterRole.setLayout(layoutFilterRole);

		Label labelRoleSearchname = new Label(groupFilterRole, SWT.NONE);
		labelRoleSearchname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		labelRoleSearchname.setText("filter by security role name:");

		textFilterSecurityRole = new Text(groupFilterRole, SWT.BORDER);
		textFilterSecurityRole.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		// textFilterServiceRole.setText(project.getName());

		filterSecurityRoleButton = new Button(groupFilterRole, SWT.PUSH);
		filterSecurityRoleButton.setText("Search");
		filterSecurityRoleButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		filterSecurityRoleButton.addSelectionListener(searchListener);

		treeViewerAllSecurityRoles = new TreeViewer(groupFilterRole, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		treeViewerAllSecurityRoles.setContentProvider(new ViewContentProvider());
		treeViewerAllSecurityRoles.setLabelProvider(
				new DelegatingStyledCellLabelProvider(new ViewLabelProvider(createImageDescriptor())));

		GridData gridDataTreeViewerAllRoles = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		// convertWidthInCharsToPixels(60) = 360 px
		gridDataTreeViewerAllRoles.widthHint = new PixelConverter(treeViewerAllSecurityRoles.getControl())
				.convertWidthInCharsToPixels(60);
		gridDataTreeViewerAllRoles.heightHint = new PixelConverter(treeViewerAllSecurityRoles.getControl())
				.convertHeightInCharsToPixels(7);
		treeViewerAllSecurityRoles.getControl().setLayoutData(gridDataTreeViewerAllRoles);
		treeViewerAllSecurityRoles.setInput(allSecurityRoleRecords);

		// buttons
		Composite compositeActionRolesButton = new Composite(groupSecurityRoles, SWT.NONE);
		// compositeActionRolesButton.setLayoutData(new GridData(SWT.LEFT,
		// SWT.CENTER, true, false, 1, 1));
		GridLayout layoutActionRolesButton = new GridLayout();
		layoutActionRolesButton.numColumns = 1;
		layoutActionRolesButton.horizontalSpacing = 8;
		compositeActionRolesButton.setLayout(layoutActionRolesButton);

		moveToSelectedRoleButton = new Button(compositeActionRolesButton, SWT.PUSH);
		moveToSelectedRoleButton.setText(">");
		moveToSelectedRoleButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		moveToSelectedRoleButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Selection:
					IStructuredSelection selection = treeViewerAllSecurityRoles.getStructuredSelection();
					if (!selection.isEmpty()) {
						Object selectedElement = selection.getFirstElement();
						if (selectedElement instanceof SecurityRole) {
							securityFilterModel.getselectedSecurityRoleRecords().add((SecurityRole) selectedElement);
							allSecurityRoleRecords.remove((SecurityRole) selectedElement);
							treeViewerAllSecurityRoles.refresh(false);
							treeViewerSelectedSecurityRoles.refresh(false);
						}
					}
					break;
				}

			}
		});

		moveToAvailableRoleButton = new Button(compositeActionRolesButton, SWT.PUSH);
		moveToAvailableRoleButton.setText("<");
		moveToAvailableRoleButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		moveToAvailableRoleButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Selection:
					IStructuredSelection selection = treeViewerSelectedSecurityRoles.getStructuredSelection();
					if (!selection.isEmpty()) {
						Object selectedElement = selection.getFirstElement();
						if (selectedElement instanceof SecurityRole) {
							allSecurityRoleRecords.add((SecurityRole) selectedElement);
							securityFilterModel.getselectedSecurityRoleRecords().remove((SecurityRole) selectedElement);
							treeViewerAllSecurityRoles.refresh(false);
							treeViewerSelectedSecurityRoles.refresh(false);
						}
					}
					break;
				}

			}
		});

		// Selected Service Role
		Group groupSelectedServiceRole = new Group(groupSecurityRoles, SWT.SHADOW_ETCHED_IN);
		groupSelectedServiceRole.setText("Selected Security Roles");
		groupSelectedServiceRole.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		GridLayout layoutSelectedServiceRole = new GridLayout();
		layoutSelectedServiceRole.numColumns = 1;
		layoutSelectedServiceRole.horizontalSpacing = 8;
		groupSelectedServiceRole.setLayout(layoutSelectedServiceRole);

		treeViewerSelectedSecurityRoles = new TreeViewer(groupSelectedServiceRole,
				SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		treeViewerSelectedSecurityRoles.setContentProvider(new ViewContentProvider());
		treeViewerSelectedSecurityRoles.setLabelProvider(
				new DelegatingStyledCellLabelProvider(new ViewLabelProvider(createImageDescriptor())));

		GridData gridDataTreeViewerSelectedRoles = new GridData(SWT.FILL, SWT.BOTTOM, true, true, 3, 1);
		gridDataTreeViewerSelectedRoles.widthHint = new PixelConverter(treeViewerSelectedSecurityRoles.getControl())
				.convertWidthInCharsToPixels(60);
		gridDataTreeViewerSelectedRoles.heightHint = new PixelConverter(treeViewerSelectedSecurityRoles.getControl())
				.convertHeightInCharsToPixels(7);
		treeViewerSelectedSecurityRoles.getControl().setLayoutData(gridDataTreeViewerSelectedRoles);
		treeViewerSelectedSecurityRoles.setInput(securityFilterModel.getselectedSecurityRoleRecords());

		Composite groupCommunication = new Composite(compositeLeft, SWT.SHADOW_ETCHED_IN);
		GridLayout layoutCommunication = new GridLayout();
		layoutCommunication.marginHeight = 3;
		layoutCommunication.marginWidth = 3;
		layoutCommunication.numColumns = 2;
		layoutCommunication.horizontalSpacing = 8;
		groupCommunication.setLayout(layoutCommunication);

		Label communicationLabel = new Label(groupCommunication, SWT.NONE);
		communicationLabel.setText("Communication:");

		communicationComboViewer = new ComboViewer(groupCommunication, SWT.READ_ONLY);
		communicationComboViewer.setContentProvider(ArrayContentProvider.getInstance());
		communicationComboViewer.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return ((CommunicationType) element).getLabel();
			}
		});
		communicationComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				CommunicationType selectedObject = (CommunicationType) selection.getFirstElement();
				securityFilterModel.setCommunicationType(selectedObject);
			}
		});
		communicationComboViewer.setInput(CommunicationType.values());
		communicationComboViewer.setSelection(new StructuredSelection(securityFilterModel.getCommunicationType()));

		enableOrDisableSecuredChoreographyInformation(false);
		
		
		
		// set the image of the process
        GridData imageGridData = new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1);
        imageGridData.minimumWidth=360;
		imageGridData.minimumHeight=401;
		imageGridData.widthHint=360;
		imageGridData.heightHint=401;
		final Canvas canvas = new Canvas(composite, SWT.PUSH);
		canvas.setLayoutData(imageGridData);
		canvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				if (synthesisProcessorImageStep != null) {
					e.gc.drawImage(synthesisProcessorImageStep, 0, 20);
				}
				
			}
		});
	}

	public void refreshSecuredParticipants() {
		securedProviderServiceComposite.refreshSecuredParticipants();
	}

	public SecurityFilter getSecurityFilterModel() {
		return securityFilterModel;
	}

	class AuthenticationLabelProvider implements ILabelProvider {

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
		}

		@Override
		public Image getImage(Object element) {
			return null;
		}

		@Override
		public String getText(Object element) {
			AuthenticationType value = (AuthenticationType) element;
			return value.getLabel();
		}

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
				return ((Vector<SecurityRole>) inputElement).toArray();
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
			if (element instanceof SecurityRole) {
				SecurityRole role = (SecurityRole) element;
				return new StyledString(role.getName() + " (ID: " + role.getKey() + ")");
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

	/* Listeners */
	private SelectionListener searchListener = new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {

            if ((Button) event.widget == filterSecurityRoleButton) {
            	try {
                    List<SecurityRole> securityRoles = getAllSecurityRoles(
                            textFilterSecurityRole.getText());

                    removeAllSecurityRoleRecord();
                    for (SecurityRole securityRole : securityRoles) {
                        addSecurityRoleRecord(securityRole);
                    }

                } catch (Exception e) {
                    // TODO adjust this
                    // error
                    MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
                            ChorevolutionUIMessages.ServiceInventory_errorMessage,
                            NLS.bind(ChorevolutionUIMessages.ServiceInventory_securityRoleSearchError,
                            		propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_URI).getValue(), e.getLocalizedMessage()));

                    /*
                     * MessageDialog.openError(
                     * ChorevolutionUIPlugin.
                     * getActiveWorkbenchShell(),
                     * ChorevolutionUIMessages.
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

	private ImageDescriptor createImageDescriptor() {
		return ChorevolutionUIPlugin.getImageDescriptor("icons/full/obj16/workingsets_obj.gif");
	}

	private List<SecurityRole> getAllSecurityRoles(String filter) {
		List<SecurityRole> results = new ArrayList<SecurityRole>();

		List<GroupTO> roles = new ArrayList<GroupTO>();
		ApacheSyncopeUtilities syncopeUtilities = new ApacheSyncopeUtilities(
				propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_URI).getValue(),
				propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_USERNAME).getValue(),
				propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_PASSWORD).getValue(),
				propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_DOMAIN).getValue());

		roles = syncopeUtilities.getSecurityRolesByNameContains(textFilterSecurityRole.getText());

		for (GroupTO role : roles) {
			SecurityRole securityRoleRecord = new SecurityRole();
			securityRoleRecord.setKey(role.getKey());
			securityRoleRecord.setName(role.getName());
			results.add(securityRoleRecord);
		}

		return results;

	}

	private void enableOrDisableSecuredChoreographyInformation(boolean enabled) {
		securityFilterModel.setSecuredChoreography(enabled);
		// authenticationComboViewer.getControl().setEnabled(enable);
		filterSecurityRoleButton.setEnabled(enabled);
		textFilterSecurityRole.setEnabled(enabled);
		moveToAvailableRoleButton.setEnabled(enabled);
		moveToSelectedRoleButton.setEnabled(enabled);
		treeViewerAllSecurityRoles.getControl().setEnabled(enabled);
		treeViewerSelectedSecurityRoles.getControl().setEnabled(enabled);
		communicationComboViewer.getControl().setEnabled(enabled);
	}

	public boolean validatePage() {
		return true;
	}

	public void addSecurityRoleRecord(SecurityRole serviceRecord) {
		if (!securityFilterModel.getselectedSecurityRoleRecords().contains(serviceRecord)) {
			allSecurityRoleRecords.add(serviceRecord);
			treeViewerAllSecurityRoles.refresh(false);
		}
	}

	public void removeAllSecurityRoleRecord() {
		allSecurityRoleRecords.removeAllElements();
		treeViewerAllSecurityRoles.refresh(false);
	}

}