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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.syncope.common.lib.to.AnyObjectTO;
import org.apache.syncope.common.lib.to.AttrTO;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.NLS;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPreferenceData;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServiceThingSourceModelPrefs;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServicesURIPrefs;
import eu.chorevolution.studio.eclipse.core.utils.syncope.ApacheSyncopeUtilities;
import eu.chorevolution.studio.eclipse.core.utils.syncope.ServiceRole;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIMessages;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;

public class UploadServiceIntoServiceInventoryDialog extends TitleAreaDialog {
	// private Image image;

	private Text textServiceInventoryURL;
	private Text textServiceInventoryUsername;
	private Text textServiceInventoryPassword;
	private Text textServiceInventoryDomanin;
	private Text textRoleName;
	private Text textRoleDescription;
	private Text textFilterServiceRole;
	private Text textServiceEndpoint;
	private Text textUploadSecurityName;
	private Button createRoleButton;
	private Button uploadSecurityButton;
	private Button filterServiceRoleButton;
	private TreeViewer treeViewerAllServiceRoles;
	private Vector<ServiceRole> allServiceRoleRecords;
	private Button moveToSelectedRoleButton;
	private Button moveToAvailableRoleButton;

	private TreeViewer treeViewerSelectedServiceRoles;
	private Vector<ServiceRole> selectedServiceRoleRecords;
	
	private String endpointServiceSelected;
	private String customAuthFileSelected;

	private IProject project;
	// private ServiceRole selectedServiceRoleRecord;

	public UploadServiceIntoServiceInventoryDialog(Shell shell, IProject project) {
		super(shell);
		this.project = project;
		allServiceRoleRecords = new Vector<ServiceRole>();
		selectedServiceRoleRecords = new Vector<ServiceRole>();
		endpointServiceSelected = "";
		customAuthFileSelected = "";
		
		/*
		 * try { image = new Image(null, new FileInputStream("jface.gif")); }
		 * catch (FileNotFoundException e) { }
		 */
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(ChorevolutionUIMessages.ChorevolutionServiceThing_title);
	}

	/*
	 * public boolean close() { if (image != null) image.dispose(); return
	 * super.close(); }
	 */
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);

		setTitle("Service Inventory");

		setMessage("Upload Service \"" + project.getName() + "\" into the Service Inventory.",
				IMessageProvider.INFORMATION);

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

		ChorevolutionServicesURIPrefs chorevolutionRestUrlsPrefs = new ChorevolutionServicesURIPrefs();
		Map<String, ChorevolutionPreferenceData> propertyValues = chorevolutionRestUrlsPrefs
				.readProjectOrWorkspacePreferences(project);
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
		
		// create service endpoint
		Composite compositeServiceEndpoint = new Composite(composite, SWT.NONE);
		compositeServiceEndpoint.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
		GridLayout layoutServiceEndpoint = new GridLayout();
		layoutServiceEndpoint.numColumns = 2;
		layoutServiceEndpoint.horizontalSpacing = 8;
		compositeServiceEndpoint.setLayout(layoutServiceEndpoint);
		Label labelEndpointService = new Label(compositeServiceEndpoint, SWT.NONE);
		labelEndpointService.setText("Service URI:");
		textServiceEndpoint = new Text(compositeServiceEndpoint, SWT.BORDER);
		textServiceEndpoint.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textServiceEndpoint.setText("");
		textServiceEndpoint.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				endpointServiceSelected = textServiceEndpoint.getText();
				validateEnableOrDisableOKButton();
			}
		});

		// Create Role
		Group groupCreateRole = new Group(composite, SWT.SHADOW_ETCHED_IN);
		groupCreateRole.setText("Create Service Role");
		groupCreateRole.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
		GridLayout layoutCreateRole = new GridLayout();
		layoutCreateRole.numColumns = 2;
		layoutCreateRole.horizontalSpacing = 8;
		groupCreateRole.setLayout(layoutCreateRole);

		Label labelRoleName = new Label(groupCreateRole, SWT.NONE);
		labelRoleName.setText("Name:");
		textRoleName = new Text(groupCreateRole, SWT.BORDER);
		textRoleName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textRoleName.setText("");

		Label labelRoleDescription = new Label(groupCreateRole, SWT.NONE);
		labelRoleDescription.setText("Description:");
		textRoleDescription = new Text(groupCreateRole, SWT.BORDER);
		textRoleDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textRoleDescription.setText("");

		new Label(groupCreateRole, SWT.NONE);
		createRoleButton = new Button(groupCreateRole, SWT.PUSH);
		createRoleButton.setText("Create");
		createRoleButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		createRoleButton.addSelectionListener(createRoleListener);

		
		//group upload security auth jar
		Group groupUploadSecurity = new Group(composite, SWT.SHADOW_ETCHED_IN);
		groupUploadSecurity.setText("Upload Custom Security ZIP");
		groupUploadSecurity.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
		GridLayout layoutUploadSecurity = new GridLayout();
		layoutUploadSecurity.numColumns = 2;
		layoutUploadSecurity.horizontalSpacing = 8;
		groupUploadSecurity.setLayout(layoutUploadSecurity);

		Label labelUploadSecurityName = new Label(groupUploadSecurity, SWT.NONE);
		labelUploadSecurityName.setText("ZIP Path:");
		textUploadSecurityName = new Text(groupUploadSecurity, SWT.BORDER);
		textUploadSecurityName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textUploadSecurityName.setText("");
		textUploadSecurityName.setEditable(false);


		new Label(groupUploadSecurity, SWT.NONE);
		uploadSecurityButton = new Button(groupUploadSecurity, SWT.PUSH);
		uploadSecurityButton.setText("Browse Files");
		uploadSecurityButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		uploadSecurityButton.addSelectionListener(uploadSecurityListener);
		
		uploadSecurityButton.setEnabled(enableOrDisableSecurityAuthPath());
		textUploadSecurityName.setEnabled(enableOrDisableSecurityAuthPath());
		groupUploadSecurity.setEnabled(enableOrDisableSecurityAuthPath());
		
		Composite compositeRolesAndSelectedRoles = new Composite(composite, SWT.NONE);
		compositeRolesAndSelectedRoles.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
		GridLayout layoutRolesAndSelectedRoles = new GridLayout();
		layoutRolesAndSelectedRoles.numColumns = 3;
		layoutRolesAndSelectedRoles.horizontalSpacing = 8;
		compositeRolesAndSelectedRoles.setLayout(layoutRolesAndSelectedRoles);

		// All Service Role
		Group groupFilterRole = new Group(compositeRolesAndSelectedRoles, SWT.SHADOW_ETCHED_IN);
		groupFilterRole.setText("Available Roles");
		groupFilterRole.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
		GridLayout layoutFilterRole = new GridLayout();
		layoutFilterRole.numColumns = 3;
		layoutFilterRole.horizontalSpacing = 8;
		groupFilterRole.setLayout(layoutFilterRole);

		Label labelRoleSearchname = new Label(groupFilterRole, SWT.NONE);
		labelRoleSearchname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		labelRoleSearchname.setText("filter by role name:");

		textFilterServiceRole = new Text(groupFilterRole, SWT.BORDER);
		textFilterServiceRole.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		// textFilterServiceRole.setText(project.getName());

		filterServiceRoleButton = new Button(groupFilterRole, SWT.PUSH);
		filterServiceRoleButton.setText("Search");
		filterServiceRoleButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		filterServiceRoleButton.addSelectionListener(searchListener);

		treeViewerAllServiceRoles = new TreeViewer(groupFilterRole, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		treeViewerAllServiceRoles.setContentProvider(new ViewContentProvider());
		treeViewerAllServiceRoles.setLabelProvider(
				new DelegatingStyledCellLabelProvider(new ViewLabelProvider(createImageDescriptor())));

		GridData gridDataTreeViewerAllRoles = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		gridDataTreeViewerAllRoles.widthHint = new PixelConverter(treeViewerAllServiceRoles.getControl())
				.convertWidthInCharsToPixels(60);
		gridDataTreeViewerAllRoles.heightHint = new PixelConverter(treeViewerAllServiceRoles.getControl())
				.convertHeightInCharsToPixels(15);
		treeViewerAllServiceRoles.getControl().setLayoutData(gridDataTreeViewerAllRoles);
		treeViewerAllServiceRoles.setInput(allServiceRoleRecords);

		// buttons
		Composite compositeActionRolesButton = new Composite(compositeRolesAndSelectedRoles, SWT.NONE);
		compositeActionRolesButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
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
					IStructuredSelection selection = treeViewerAllServiceRoles.getStructuredSelection();
					if (!selection.isEmpty()) {
						Object selectedElement = selection.getFirstElement();
						if (selectedElement instanceof ServiceRole) {
							selectedServiceRoleRecords.add((ServiceRole) selectedElement);
							allServiceRoleRecords.remove((ServiceRole) selectedElement);
							treeViewerAllServiceRoles.refresh(false);
							treeViewerSelectedServiceRoles.refresh(false);

							validateEnableOrDisableOKButton();
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
					IStructuredSelection selection = treeViewerSelectedServiceRoles.getStructuredSelection();
					if (!selection.isEmpty()) {
						Object selectedElement = selection.getFirstElement();
						if (selectedElement instanceof ServiceRole) {
							allServiceRoleRecords.add((ServiceRole) selectedElement);
							selectedServiceRoleRecords.remove((ServiceRole) selectedElement);
							treeViewerAllServiceRoles.refresh(false);
							treeViewerSelectedServiceRoles.refresh(false);

							validateEnableOrDisableOKButton();
						}
					}
					break;
				}

			}
		});

		// Selected Service Role
		Group groupSelectedServiceRole = new Group(compositeRolesAndSelectedRoles, SWT.SHADOW_ETCHED_IN);
		groupSelectedServiceRole.setText("Selected Roles");
		groupSelectedServiceRole.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		GridLayout layoutSelectedServiceRole = new GridLayout();
		layoutSelectedServiceRole.numColumns = 1;
		layoutSelectedServiceRole.horizontalSpacing = 8;
		groupSelectedServiceRole.setLayout(layoutSelectedServiceRole);

		treeViewerSelectedServiceRoles = new TreeViewer(groupSelectedServiceRole,
				SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		treeViewerSelectedServiceRoles.setContentProvider(new ViewContentProvider());
		treeViewerSelectedServiceRoles.setLabelProvider(
				new DelegatingStyledCellLabelProvider(new ViewLabelProvider(createImageDescriptor())));

		GridData gridDataTreeViewerSelectedRoles = new GridData(SWT.FILL, SWT.BOTTOM, true, true, 3, 1);
		gridDataTreeViewerSelectedRoles.widthHint = new PixelConverter(treeViewerSelectedServiceRoles.getControl())
				.convertWidthInCharsToPixels(60);
		gridDataTreeViewerSelectedRoles.heightHint = new PixelConverter(treeViewerSelectedServiceRoles.getControl())
				.convertHeightInCharsToPixels(15);
		treeViewerSelectedServiceRoles.getControl().setLayoutData(gridDataTreeViewerSelectedRoles);
		treeViewerSelectedServiceRoles.setInput(selectedServiceRoleRecords);

		return composite;
	}
	
	private boolean enableOrDisableSecurityAuthPath() {
		
        ChorevolutionServiceThingSourceModelPrefs chorevolutionServiceThingSourceModelPrefs = new ChorevolutionServiceThingSourceModelPrefs();
        Map<String, ChorevolutionPreferenceData> propertyValues = chorevolutionServiceThingSourceModelPrefs
                .readProjectOrWorkspacePreferences(project);
		
        IPath securityPath = project.getLocation().append(new Path(propertyValues
                        .get(ChorevolutionServiceThingSourceModelPrefs.PREF_SERVICE_THING_SECURITY_DESCRIPTION)
                        .getValue()));
        
		File securityFolder = new File(securityPath.toString());
		File[] listOfFiles = securityFolder.listFiles();
		
		//take the first file with extension .security
		for(File file : listOfFiles) {
			String[] nameSplitted = file.getName().split("\\.");
			if(nameSplitted[nameSplitted.length-1].equals("security")) {

				try {
					Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new FileInputStream(file.getAbsolutePath()));
					NodeList elementsAuth = ((org.w3c.dom.Document) d).getElementsByTagName("authentication");
					for (int i = 0; i < elementsAuth.getLength(); i++) {
						if(elementsAuth.item(i).getAttributes().getNamedItem("AuthNTypeForwarded").getNodeValue().equals("CustomAccount")) {
							return true;
						}
					}					
				} catch (Exception e) {
					// TODO Auto-generated catch block
				}
				
			}
		}
		
        return false;
		
		
	}

	private void validateEnableOrDisableOKButton() {
		try {
			URL url = new URL(textServiceEndpoint.getText());
			url.toURI();
			getButton(IDialogConstants.OK_ID).setEnabled(true);
			setMessage("Upload Service \"" + project.getName() + "\" into the Service Inventory.",
					IMessageProvider.INFORMATION);
		
		} catch (MalformedURLException | URISyntaxException e) {
			getButton(IDialogConstants.OK_ID).setEnabled(false);
			setMessage("Insert a valid service endpoint.", IMessageProvider.ERROR);
			return;
		} 
			
		if (selectedServiceRoleRecords.isEmpty()) {
			getButton(IDialogConstants.OK_ID).setEnabled(false);
			setMessage("Select one or more Service Role.", IMessageProvider.ERROR);
			return;

		} else {
			try {
				// find service Role that has the name equals to service name
				ServiceRole serviceRole = getServiceRoleByName(project.getName());
				if (serviceRole == null || serviceRole.getKey().isEmpty()) {
					getButton(IDialogConstants.OK_ID).setEnabled(true);
					setMessage("Upload Service \"" + project.getName() + "\" into the Service Inventory.",
							IMessageProvider.INFORMATION);
				} else {
					getButton(IDialogConstants.OK_ID).setEnabled(false);
					setMessage(
							"Error to upload a Service with name \"" + project.getName()
									+ "\" into the Service Inventory because a Service Role with the same name already exist.",
							IMessageProvider.ERROR);
					return;
				}
			} catch (Exception ex) {
				// leave empty skip the exception
			}

		}
		
		if ((getSecurityAuthPath().length() < 5) && (enableOrDisableSecurityAuthPath())) {
			getButton(IDialogConstants.OK_ID).setEnabled(false);
			setMessage("Select a ZIP file to upload for Custom Security.", IMessageProvider.ERROR);
			return;

		}

	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
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
				return ((Vector<ServiceRole>) inputElement).toArray();
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
			if (element instanceof ServiceRole) {
				ServiceRole service = (ServiceRole) element;
				return new StyledString(service.getName() + " (ID: " + service.getKey() + ")");
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

	public List<ServiceRole> getSelectedServiceRole() {
		return new ArrayList<ServiceRole>(selectedServiceRoleRecords);
	}
	
	public String getServiceEndpoint(){
		return endpointServiceSelected;
	}

	public void addServiceRoleRecord(ServiceRole serviceRecord) {
		if (!selectedServiceRoleRecords.contains(serviceRecord)) {
			allServiceRoleRecords.add(serviceRecord);
			treeViewerAllServiceRoles.refresh(false);
		}
	}

	public void removeAllServiceRoleRecord() {
		allServiceRoleRecords.removeAllElements();
		treeViewerAllServiceRoles.refresh(false);
	}

	
	/* Listeners */
	private SelectionListener uploadSecurityListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {

			if ((Button) e.widget == uploadSecurityButton) {

				   FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
				   dialog.setFilterExtensions(new String [] {"*.zip"});
				   String securityPath = dialog.open();
				   textUploadSecurityName.setText(securityPath);
				   customAuthFileSelected = securityPath;

				   validateEnableOrDisableOKButton();
				   
			}
		}
	};
	
	private SelectionListener createRoleListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {

			if ((Button) e.widget == createRoleButton) {
				try {
					ApacheSyncopeUtilities syncopeUtilities = new ApacheSyncopeUtilities(
							textServiceInventoryURL.getText(), textServiceInventoryUsername.getText(),
							textServiceInventoryPassword.getText(), textServiceInventoryDomanin.getText());
					syncopeUtilities.createRole(textRoleName.getText(), textRoleDescription.getText());

					MessageDialog.openInformation(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
							ChorevolutionUIMessages.ServiceInventory_infoMessage,
							NLS.bind(ChorevolutionUIMessages.ServiceInventory_serviceRoleCreated,
									textRoleName.getText()));

					textRoleName.setText("");
					textRoleDescription.setText("");

					SafeRunnable.run(new SafeRunnable() {
						@Override
						public void run() {
							Event ee = new Event();
							ee.widget = filterServiceRoleButton;
							searchListener.widgetSelected(new SelectionEvent(ee));
						}
					});

				} catch (Exception e1) {

					MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
							ChorevolutionUIMessages.ServiceInventory_errorMessage,
							NLS.bind(ChorevolutionUIMessages.ServiceInventory_servicesRoleCreationError,
									textRoleName.getText(), e1.getLocalizedMessage()));
				}

			}
		}
	};

	/* Listeners */
	private SelectionListener searchListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if ((Button) e.widget == filterServiceRoleButton) {
				try {
					List<ServiceRole> serviceRoles = getAllServiceRoles(textFilterServiceRole.getText());

					removeAllServiceRoleRecord();
					for (ServiceRole serviceRole : serviceRoles) {
						addServiceRoleRecord(serviceRole);
					}

				} catch (Exception e1) {
					// TODO adjust this
					// error
					MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
							ChorevolutionUIMessages.ServiceInventory_errorMessage,
							NLS.bind(ChorevolutionUIMessages.ServiceInventory_serviceRoleSearchError,
									textServiceInventoryURL.getText(), e1.getLocalizedMessage()));

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

	private ServiceRole getServiceRoleByName(String roleName) {

		ApacheSyncopeUtilities syncopeUtilities = new ApacheSyncopeUtilities(textServiceInventoryURL.getText(),
				textServiceInventoryUsername.getText(), textServiceInventoryPassword.getText(),
				textServiceInventoryDomanin.getText());

		AnyObjectTO role = syncopeUtilities.getRoleByName(roleName);
		if (role == null) {
			return null;
		}
		ServiceRole serviceRoleRecord = new ServiceRole();
		serviceRoleRecord.setKey(role.getKey());
		serviceRoleRecord.setName(role.getName());
		Iterator iterator = role.getPlainAttrs().iterator();
		while (iterator.hasNext()) {

			AttrTO attr = (AttrTO) iterator.next();
			if (attr.getSchema().equalsIgnoreCase(ApacheSyncopeUtilities.SERVICE_ROLE_DESCRIPTION)) {
				serviceRoleRecord.setDescription(attr.getValues().get(0));
			}

		}

		return serviceRoleRecord;

	}

	public String getSecurityAuthPath() {
		return customAuthFileSelected;
	}
	
	private List<ServiceRole> getAllServiceRoles(String filter) {
		List<ServiceRole> results = new ArrayList<ServiceRole>();

		List<AnyObjectTO> roles = new ArrayList<AnyObjectTO>();
		ApacheSyncopeUtilities syncopeUtilities = new ApacheSyncopeUtilities(textServiceInventoryURL.getText(),
				textServiceInventoryUsername.getText(), textServiceInventoryPassword.getText(),
				textServiceInventoryDomanin.getText());

		roles = syncopeUtilities.getRolesByNameContains(textFilterServiceRole.getText());

		for (AnyObjectTO role : roles) {
			ServiceRole serviceRoleRecord = new ServiceRole();
			serviceRoleRecord.setKey(role.getKey());
			serviceRoleRecord.setName(role.getName());
			Iterator iterator = role.getPlainAttrs().iterator();
			while (iterator.hasNext()) {

				AttrTO attr = (AttrTO) iterator.next();
				if (attr.getSchema().equalsIgnoreCase(ApacheSyncopeUtilities.SERVICE_ROLE_DESCRIPTION)) {
					serviceRoleRecord.setDescription(attr.getValues().get(0));
				}

			}
			results.add(serviceRoleRecord);
		}

		return results;

	}

	@Override
	protected boolean isResizable() {
		return true;
	}
}
