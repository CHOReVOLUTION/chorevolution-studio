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

import org.apache.cxf.common.util.Base64Exception;
import org.apache.cxf.common.util.Base64Utility;
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
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
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

import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPreferenceData;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServicesURIPrefs;
import eu.chorevolution.studio.eclipse.core.utils.syncope.ApacheSyncopeUtilities;
import eu.chorevolution.studio.eclipse.core.utils.syncope.InteractionProtocolDescriptionType;
import eu.chorevolution.studio.eclipse.core.utils.syncope.InterfaceDescriptionType;
import eu.chorevolution.studio.eclipse.core.utils.syncope.SecurityDescriptionType;
import eu.chorevolution.studio.eclipse.core.utils.syncope.Service;
import eu.chorevolution.studio.eclipse.core.utils.syncope.ServiceAuthenticationType;
import eu.chorevolution.studio.eclipse.core.utils.syncope.ServiceRole;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIMessages;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;

public class SelectionServiceFromInventoryDialog extends TitleAreaDialog {
    // private Image image;

    // service roles
    private Text textFilterServiceRole;
    private TreeViewer treeViewerAllServiceRoles;
    private Vector<ServiceRole> allServiceRoleRecords;
    private Button filterServiceRoleButton;

    // services
    private Text textFilterService;
    private TreeViewer treeViewerAllServices;
    private Vector<Service> allServiceRecords;
    private Button filterServiceButton;
    private Service selectedServiceRecord;

    private IProject project;
    private Button okButton;
    private Button cancelButton;
    
    private String participantName;
    
    Map<String, ChorevolutionPreferenceData> propertyValues;

    public SelectionServiceFromInventoryDialog(Shell shell, IProject project, String participantName) {
        super(shell);
        this.project = project;
        allServiceRecords = new Vector<Service>();
        allServiceRoleRecords = new Vector<ServiceRole>();
        selectedServiceRecord = null;
        this.participantName = participantName;
        
        propertyValues = (new ChorevolutionServicesURIPrefs()).readProjectOrWorkspacePreferences(project);
        /*
         * try { image = new Image(null, new FileInputStream("jface.gif")); }
         * catch (FileNotFoundException e) { }
         */
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

        setTitle("Service Inventory");

        setMessage("Select concrete services/things that can play the roles of the choreography \""+participantName+"\" participant.", IMessageProvider.INFORMATION);
        

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

        Composite compositeRoleAndService = new Composite(composite, SWT.NONE);
        compositeRoleAndService.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
        GridLayout layoutFilterServices = new GridLayout();
        layoutFilterServices.numColumns = 2;
        layoutFilterServices.horizontalSpacing = 8;
        compositeRoleAndService.setLayout(layoutFilterServices);

        // service role group
        Group groupRoles = new Group(compositeRoleAndService, SWT.NONE);
        groupRoles.setText("Available Roles");
        groupRoles.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
        GridLayout layoutRoles = new GridLayout();
        layoutRoles.numColumns = 3;
        layoutRoles.horizontalSpacing = 8;
        groupRoles.setLayout(layoutRoles);

        Label labelRoleSearchName = new Label(groupRoles, SWT.NONE);
        labelRoleSearchName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
        labelRoleSearchName.setText("filter by role name:");

        textFilterServiceRole = new Text(groupRoles, SWT.BORDER);
        textFilterServiceRole.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
        //textFilterServiceRole.setText(participantName);

        filterServiceRoleButton = new Button(groupRoles, SWT.PUSH);
        filterServiceRoleButton.setText("Search");
        filterServiceRoleButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        filterServiceRoleButton.addSelectionListener(searchListener);

        treeViewerAllServiceRoles = new TreeViewer(groupRoles, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
        treeViewerAllServiceRoles.setContentProvider(new ViewContentProvider());
        treeViewerAllServiceRoles.setLabelProvider(
                new DelegatingStyledCellLabelProvider(new ViewLabelProvider(createImageDescriptor())));
        GridData gridDataTreeViewerAllRoles = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
        gridDataTreeViewerAllRoles.widthHint = new PixelConverter(treeViewerAllServiceRoles.getControl())
                .convertWidthInCharsToPixels(60);
        gridDataTreeViewerAllRoles.heightHint = new PixelConverter(treeViewerAllServiceRoles.getControl())
                .convertHeightInCharsToPixels(15);
        treeViewerAllServiceRoles.getControl().setLayoutData(gridDataTreeViewerAllRoles);
        treeViewerAllServiceRoles.addSelectionChangedListener(new ServiceRoleRecordSelectionChangedListener());
        treeViewerAllServiceRoles.setInput(allServiceRoleRecords);
        

        // Services group
        Group groupServices = new Group(compositeRoleAndService, SWT.NONE);
        groupServices.setText("Available Services");
        groupServices.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
        GridLayout layoutServices = new GridLayout();
        layoutServices.numColumns = 3;
        layoutServices.horizontalSpacing = 8;
        groupServices.setLayout(layoutServices);

        Label labelServiceSearchName = new Label(groupServices, SWT.NONE);
        labelServiceSearchName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
        labelServiceSearchName.setText("filter by service name:");

        textFilterService = new Text(groupServices, SWT.BORDER);
        textFilterService.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

        filterServiceButton = new Button(groupServices, SWT.PUSH);
        filterServiceButton.setText("Search");
        filterServiceButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        filterServiceButton.addSelectionListener(searchListener);

        treeViewerAllServices = new TreeViewer(groupServices, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
        treeViewerAllServices.setContentProvider(new ViewContentProvider());
        treeViewerAllServices.setLabelProvider(
                new DelegatingStyledCellLabelProvider(new ViewLabelProvider(createImageDescriptor())));

        GridData gridDataTreeViewerAllServices = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
        gridDataTreeViewerAllServices.widthHint = new PixelConverter(treeViewerAllServices.getControl())
                .convertWidthInCharsToPixels(60);
        gridDataTreeViewerAllServices.heightHint = new PixelConverter(treeViewerAllServices.getControl())
                .convertHeightInCharsToPixels(15);
        treeViewerAllServices.getControl().setLayoutData(gridDataTreeViewerAllServices);
        treeViewerAllServices.addSelectionChangedListener(new ServiceRecordSelectionChangedListener());
        treeViewerAllServices.setInput(allServiceRecords);

        return composite;
    }

    protected void createButtonsForButtonBar(Composite parent) {
        okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        cancelButton = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
        okButton.setEnabled(false);
        cancelButton.setFocus();
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
                return ((Vector<Service>) inputElement).toArray();
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
            if (element instanceof Service) {
                Service service = (Service) element;
                return new StyledString(service.getName() + " (ID: " + service.getKey() + ")");
            } else if (element instanceof ServiceRole) {
                ServiceRole serviceRole = (ServiceRole) element;
                return new StyledString(serviceRole.getName() + " (ID: " + serviceRole.getKey() + ")");
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

    private class ServiceRoleRecordSelectionChangedListener implements ISelectionChangedListener {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
        	IStructuredSelection selection = treeViewerAllServiceRoles.getStructuredSelection();
            if (!selection.isEmpty()) {
                Object selectedElement = selection.getFirstElement();
                if (selectedElement instanceof ServiceRole) {
                    ServiceRole selectedServiceRoleRecord = (ServiceRole) selectedElement;
                    getAllServices(FilterType.ROLE, selectedServiceRoleRecord.getName());
                }
            }

        }

    }

    private class ServiceRecordSelectionChangedListener implements ISelectionChangedListener {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            IStructuredSelection selection = treeViewerAllServices.getStructuredSelection();
            if (!selection.isEmpty()) {
                Object selectedElement = selection.getFirstElement();
                if (selectedElement instanceof Service) {
                    selectedServiceRecord = (Service) selectedElement;
                    okButton.setEnabled(true);
                    okButton.setFocus();
                } 
            } else {
                okButton.setEnabled(false);
                cancelButton.setFocus();
            }

        }

    }

    public Service getSelectedService() {
        return selectedServiceRecord;
    }

    public void addServiceRecord(Service serviceRecord) {
        allServiceRecords.add(serviceRecord);
        treeViewerAllServices.refresh(false);
    }

    public void removeAllServiceRecord() {
        allServiceRecords.removeAllElements();
        treeViewerAllServices.refresh(false);
    }

    public void addServiceRoleRecord(ServiceRole serviceRoleRecord) {
        allServiceRoleRecords.add(serviceRoleRecord);
        treeViewerAllServiceRoles.refresh(false);
    }

    public void removeAllServiceRoleRecord() {
        allServiceRoleRecords.removeAllElements();
        treeViewerAllServiceRoles.refresh(false);
    }

    /* Listeners */
    private SelectionListener searchListener = new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            if ((Button) e.widget == filterServiceButton || (Button) e.widget == filterServiceRoleButton) {
            	if ((Button) e.widget == filterServiceButton) {
                    getAllServices(FilterType.SERVICE, textFilterService.getText());
                } else if ((Button) e.widget == filterServiceRoleButton) {
                    getAllRoles(FilterType.ROLE, textFilterServiceRole.getText());
                }
            }

        }
    };

    private enum FilterType {
        SERVICE, ROLE;
    }

    private void getAllServices(FilterType filterType, String filter) {
        removeAllServiceRecord();

    	List<AnyObjectTO> services = new ArrayList<AnyObjectTO>();
        try {
            ApacheSyncopeUtilities syncopeUtilities = new ApacheSyncopeUtilities(propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_URI).getValue(),
            		propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_USERNAME).getValue(), propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_PASSWORD).getValue(),
            		propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_DOMAIN).getValue());

            if (filterType == FilterType.SERVICE) {
                services = syncopeUtilities.getServicesByNameContains(filter);
            } else if (filterType == FilterType.ROLE) {
                services = syncopeUtilities.getServicesByRoleName(filter);
            }

        } catch (Exception e) {
            // TODO adjust this
            // error
            MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
                    ChorevolutionUIMessages.ServiceInventory_errorMessage,
                    NLS.bind(ChorevolutionUIMessages.ServiceInventory_serviceSearchError,
                    		propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_URI).getValue(), e.getLocalizedMessage()));

            /*
             * MessageDialog.openError( ChorevolutionUIPlugin.
             * getActiveWorkbenchShell(), ChorevolutionUIMessages.
             * ServiceInventory_infoMessage, NLS.bind(ChorevolutionUIMessages.
             * ServiceInventory_connectionRefused,
             * textServiceInventoryURL.getText()));
             */
            return;
        }

        for (AnyObjectTO service : services) {
            Service serviceRecord = new Service();
            serviceRecord.setKey(service.getKey());
            serviceRecord.setName(service.getName());
            // org.eclipse.ui.wizards.datatransfer.ExternalProjectImportWizard
            Iterator iterator = service.getPlainAttrs().iterator();
            while (iterator.hasNext()) {
                try {
                    AttrTO attr = (AttrTO) iterator.next();
                    if (attr.getSchema().equalsIgnoreCase(ApacheSyncopeUtilities.SERVICE_LOCATION)) {
                        serviceRecord.setLocation(attr.getValues().get(0));
                    } else if (attr.getSchema()
                            .equalsIgnoreCase(ApacheSyncopeUtilities.INTERFACE_DESCRIPTION_CONTENT)) {
                        serviceRecord.setInterfaceDescriptionContent(Base64Utility.decode(attr.getValues().get(0)));

                    } else if (attr.getSchema().equalsIgnoreCase(ApacheSyncopeUtilities.INTERFACE_DESCRIPTION_TYPE)) {
                        if (attr.getValues().get(0).equalsIgnoreCase(InterfaceDescriptionType.WSDL.name())) {
                            serviceRecord.setInterfaceDescriptionType(InterfaceDescriptionType.WSDL);
                        } else if (attr.getValues().get(0).equalsIgnoreCase(InterfaceDescriptionType.WADL.name())) {
                            serviceRecord.setInterfaceDescriptionType(InterfaceDescriptionType.WADL);
                        } else if (attr.getValues().get(0).equalsIgnoreCase(InterfaceDescriptionType.GIDL.name())) {
                            serviceRecord.setInterfaceDescriptionType(InterfaceDescriptionType.GIDL);
                        }

                    } else if (attr.getSchema()
                            .equalsIgnoreCase(ApacheSyncopeUtilities.INTERACTIONPROTOCOL_DESCRIPTION_CONTENT)) {
                        serviceRecord.setInteractionProtocolDescriptionContent(
                                Base64Utility.decode(attr.getValues().get(0)));
                    } else if (attr.getSchema()
                            .equalsIgnoreCase(ApacheSyncopeUtilities.INTERACTIONPROTOCOL_DESCRIPTION_TYPE)) {
                        if (attr.getValues().get(0).equalsIgnoreCase(InteractionProtocolDescriptionType.IPLTS.name())) {
                            serviceRecord
                                    .setInteractionProtocolDescriptionType(InteractionProtocolDescriptionType.IPLTS);
                        } else if (attr.getValues().get(0)
                                .equalsIgnoreCase(InteractionProtocolDescriptionType.WSBPEL.name())) {
                            serviceRecord
                                    .setInteractionProtocolDescriptionType(InteractionProtocolDescriptionType.WSBPEL);
                        }
                    } else if (attr.getSchema().equalsIgnoreCase(ApacheSyncopeUtilities.SECURITY_DESCRIPTION_CONTENT)) {
                        serviceRecord.setSecurityDescriptionContent(Base64Utility.decode(attr.getValues().get(0)));

                    }
                    else if (attr.getSchema().equalsIgnoreCase(ApacheSyncopeUtilities.SERVICE_CUSTOM_AUTH_JAR)) {
                        serviceRecord.setCustomAuthFileJAR(Base64Utility.decode(attr.getValues().get(0)));
                    }
                    else if (attr.getSchema().equalsIgnoreCase(ApacheSyncopeUtilities.SECURITY_DESCRIPTION_TYPE)) {
                        if (attr.getValues().get(0).equalsIgnoreCase(SecurityDescriptionType.SECURITY.name())) {
                            serviceRecord.setSecurityDescriptionType(SecurityDescriptionType.SECURITY);
                        }
                    }else if(attr.getSchema().equalsIgnoreCase(ApacheSyncopeUtilities.SERVICE_AUTHENTICATION_TYPE)){
                    	if (attr.getValues().get(0).equalsIgnoreCase(ServiceAuthenticationType.SHARED.name())){
                    		serviceRecord.setServiceAuthenticationType(ServiceAuthenticationType.SHARED);
                    	} else if (attr.getValues().get(0).equalsIgnoreCase(ServiceAuthenticationType.PER_USER.name())){
                    		serviceRecord.setServiceAuthenticationType(ServiceAuthenticationType.PER_USER);
                    	} else if (attr.getValues().get(0).equalsIgnoreCase(ServiceAuthenticationType.CUSTOM.name())){
                    		serviceRecord.setServiceAuthenticationType(ServiceAuthenticationType.CUSTOM);
                    	}
                    	else{
                    		serviceRecord.setServiceAuthenticationType(ServiceAuthenticationType.NONE);
                    	}                    	
                    }
                } catch (Base64Exception e) {
                    // TODO adjust this
                    // error
                    MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
                            ChorevolutionUIMessages.ServiceInventory_errorMessage,
                            NLS.bind(ChorevolutionUIMessages.ServiceInventory_servicesCreationError,
                                    e.getLocalizedMessage()));
                }

            }
            addServiceRecord(serviceRecord);
        }
    }

    private void getAllRoles(FilterType filterType, String filter) {
        removeAllServiceRoleRecord();
      
        List<AnyObjectTO> roles = new ArrayList<AnyObjectTO>();
        try {
        	ApacheSyncopeUtilities syncopeUtilities = new ApacheSyncopeUtilities(propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_URI).getValue(),
            		propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_USERNAME).getValue(), propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_PASSWORD).getValue(),
            		propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_DOMAIN).getValue());

            if (filterType == FilterType.ROLE) {
                roles = syncopeUtilities.getRolesByNameContains(filter);
            }
        } catch (Exception e) {
            // TODO adjust this
            // error
            MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
                    ChorevolutionUIMessages.ServiceInventory_errorMessage,
                    NLS.bind(ChorevolutionUIMessages.ServiceInventory_serviceRoleSearchError,
                    		propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_URI).getValue(), e.getLocalizedMessage()));

            /*
             * MessageDialog.openError( ChorevolutionUIPlugin.
             * getActiveWorkbenchShell(), ChorevolutionUIMessages.
             * ServiceInventory_infoMessage, NLS.bind(ChorevolutionUIMessages.
             * ServiceInventory_connectionRefused,
             * textServiceInventoryURL.getText()));
             */
            return;
        }

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
            addServiceRoleRecord(serviceRoleRecord);
        }
    }

    @Override
    protected boolean isResizable() {
        return true;
    }
}
