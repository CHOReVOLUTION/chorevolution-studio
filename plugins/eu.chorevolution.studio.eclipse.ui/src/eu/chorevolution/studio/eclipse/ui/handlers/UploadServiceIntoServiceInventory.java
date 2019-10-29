package eu.chorevolution.studio.eclipse.ui.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.internal.core.util.Util;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import eu.chorevolution.modelingnotations.servicething.ServiceThingModel;
import eu.chorevolution.studio.eclipse.core.ChorevolutionCoreUtils;
import eu.chorevolution.studio.eclipse.core.internal.project.ChorevolutionProjectUtils;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPreferenceData;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServiceThingSourceModelPrefs;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServicesURIPrefs;
import eu.chorevolution.studio.eclipse.core.utils.syncope.ApacheSyncopeUtilities;
import eu.chorevolution.studio.eclipse.core.utils.syncope.InteractionProtocolDescriptionType;
import eu.chorevolution.studio.eclipse.core.utils.syncope.InterfaceDescriptionType;
import eu.chorevolution.studio.eclipse.core.utils.syncope.SecurityDescriptionType;
import eu.chorevolution.studio.eclipse.core.utils.syncope.Service;
import eu.chorevolution.studio.eclipse.core.utils.syncope.ServiceRole;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIMessages;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;
import eu.chorevolution.studio.eclipse.ui.handlers.dialogs.UploadServiceIntoServiceInventoryDialog;


public class UploadServiceIntoServiceInventory extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
        boolean createdService = false;
        boolean pressOK = false;
        Iterator iterator = selection.iterator();
        List<IFile> chorarchFiles = new ArrayList<IFile>();

        while (iterator.hasNext()) {
            IFile file = (IFile) iterator.next();
            if (ChorevolutionProjectUtils.isChorevolutionProject(file)) {
                IProject project = file.getProject();
                // get preferences value for workspace or selected project if
                // specified
                Map<String, String> serviceThingDefaultWorkspaceProperties = new HashMap<String, String>();
                ChorevolutionServiceThingSourceModelPrefs chorevolutionServiceThingSourceModelPrefs = new ChorevolutionServiceThingSourceModelPrefs();
                Map<String, ChorevolutionPreferenceData> propertyValues = chorevolutionServiceThingSourceModelPrefs
                        .readProjectOrWorkspacePreferences(project);
                for (ChorevolutionPreferenceData chorevolutionPreferenceData : propertyValues.values()) {
                    serviceThingDefaultWorkspaceProperties.put(chorevolutionPreferenceData.getID(),
                            chorevolutionPreferenceData.getValue());
                }

                // get Chorevolution rest urls
                ChorevolutionServicesURIPrefs chorevolutionRestUrlsPrefs = new ChorevolutionServicesURIPrefs();
                Map<String, ChorevolutionPreferenceData> restPropertyValues = chorevolutionRestUrlsPrefs
                        .readProjectOrWorkspacePreferences(project);

                
                    IPath location = file.getLocation();
                    if (location != null) {

                        ServiceThingModel model = ChorevolutionCoreUtils.loadServiceThingModel(
                                URI.createPlatformResourceURI(file.getFullPath().toString(), true));
                        
                        
                        UploadServiceIntoServiceInventoryDialog createServiceFromInventoryDialog = new UploadServiceIntoServiceInventoryDialog(ChorevolutionUIPlugin.getActiveWorkbenchShell(), project);
                        createServiceFromInventoryDialog.create();
                        
                        if (createServiceFromInventoryDialog.open() == IDialogConstants.OK_ID) {
                            pressOK = true;
                            List<ServiceRole> serviceRoles = new ArrayList<ServiceRole>();
                            createdService = createService(model, createServiceFromInventoryDialog.getServiceEndpoint(), file, propertyValues, restPropertyValues,  createServiceFromInventoryDialog.getSelectedServiceRole(), createServiceFromInventoryDialog.getSecurityAuthPath());
                            
                            
                        } 

                    }

                    /*
                     * MessageDialog.openError(ChorevolutionUIPlugin.
                     * getActiveWorkbenchShell(),
                     * ChorevolutionUIMessages.ServiceInventory_infoMessage,
                     * NLS.bind(ChorevolutionUIMessages.
                     * ServiceInventory_connectionRefused, restPropertyValues
                     * .get(ChorevolutionServicesURIPrefs.
                     * PREF_APACHE_SYNCOPE_URI) .getValue()));
                     */
                }

            }

        

        if (createdService && pressOK){
            MessageDialog.openInformation(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
                    ChorevolutionUIMessages.ServiceInventory_infoMessage,
                    ChorevolutionUIMessages.ServiceInventory_serviceUploaded);
        }

        return null;
    }

    private boolean createService(ServiceThingModel model, String serviceEndpoint, IFile file,
            Map<String, ChorevolutionPreferenceData> propertyValues,
            Map<String, ChorevolutionPreferenceData> restPropertyValues, List<ServiceRole> serviceRoles,
            String customAuthPath) {

        try {

            // message Confirmation
            /*
             * boolean uploadService = MessageDialog.openConfirm(
             * ChorevolutionUIPlugin.getActiveWorkbenchShell(),
             * ChorevolutionUIMessages.ServiceInventory_infoMessage,
             * NLS.bind(ChorevolutionUIMessages.
             * ServiceInventory_confirmUploadService, model.getName(),
             * restPropertyValues.get(ChorevolutionServicesURIPrefs.
             * PREF_APACHE_SYNCOPE_URI) .getValue())); if (!uploadService) {
             * return null; }
             */

            Service serviceToUploaded = new Service();
            serviceToUploaded.setName(model.getName());
            serviceToUploaded.setLocation(serviceEndpoint);
            serviceToUploaded.setServiceRoles(serviceRoles);

            // SET Interface Description model
            IFolder folder = file.getParent()
                    .getFolder(new Path(propertyValues
                            .get(ChorevolutionServiceThingSourceModelPrefs.PREF_SERVICE_THING_INTERFACE_DESCRIPTION)
                            .getValue()));
            // find WSDL
            for (IResource resource : folder.members()) {
                if (resource.getFileExtension().equalsIgnoreCase(InterfaceDescriptionType.WSDL.name())) {
                    serviceToUploaded
                            .setInterfaceDescriptionContent(Util.getResourceContentsAsByteArray((IFile) resource));
                    serviceToUploaded.setInterfaceDescriptionType(InterfaceDescriptionType.WSDL);
                }
            }
            // find WADL if WSDL not founded
            if (serviceToUploaded.getInterfaceDescriptionType() != InterfaceDescriptionType.WSDL) {
                for (IResource resource : folder.members()) {
                    if (resource.getFileExtension().equalsIgnoreCase(InterfaceDescriptionType.WADL.name())) {
                        serviceToUploaded
                                .setInterfaceDescriptionContent(Util.getResourceContentsAsByteArray((IFile) resource));
                        serviceToUploaded.setInterfaceDescriptionType(InterfaceDescriptionType.WADL);
                    }
                }
            }

            // find GIDL if WSDL not founded and WADL not founded
            if (serviceToUploaded.getInterfaceDescriptionType() != InterfaceDescriptionType.WSDL
                    && serviceToUploaded.getInterfaceDescriptionType() != InterfaceDescriptionType.WADL) {
                for (IResource resource : folder.members()) {
                    if (resource.getFileExtension().equalsIgnoreCase(InterfaceDescriptionType.GIDL.name())) {
                        serviceToUploaded
                                .setInterfaceDescriptionContent(Util.getResourceContentsAsByteArray((IFile) resource));
                        serviceToUploaded.setInterfaceDescriptionType(InterfaceDescriptionType.GIDL);
                    }
                }
            }

            // SET Interaction protocol Model
            folder = file.getParent()
                    .getFolder(new Path(propertyValues
                            .get(ChorevolutionServiceThingSourceModelPrefs.PREF_SERVICE_THING_INTERACTIONPROTOCOL_DESCRIPTION)
                            .getValue()));
            // find IPLTS
            for (IResource resource : folder.members()) {
                if (resource.getFileExtension().equalsIgnoreCase(InteractionProtocolDescriptionType.IPLTS.name())) {
                    serviceToUploaded.setInteractionProtocolDescriptionContent(
                            Util.getResourceContentsAsByteArray((IFile) resource));
                    serviceToUploaded.setInteractionProtocolDescriptionType(InteractionProtocolDescriptionType.IPLTS);
                }
            }
            // find WSBPEL if IPLTS not founded
            if (serviceToUploaded.getInteractionProtocolDescriptionType() != InteractionProtocolDescriptionType.IPLTS) {
                for (IResource resource : folder.members()) {
                    if (resource.getFileExtension()
                            .equalsIgnoreCase(InteractionProtocolDescriptionType.WSBPEL.name())) {
                        serviceToUploaded.setInteractionProtocolDescriptionContent(
                                Util.getResourceContentsAsByteArray((IFile) resource));
                        serviceToUploaded
                                .setInteractionProtocolDescriptionType(InteractionProtocolDescriptionType.WSBPEL);
                    }
                }
            }

            // SET QoS Description Model
            /*folder = file.getParent()
                    .getFolder(new Path(propertyValues
                            .get(ChorevolutionServiceThingSourceModelPrefs.PREF_SERVICE_THING_QOS_DESCRIPTION)
                            .getValue()));
           
            // find WSLA
            for (IResource resource : folder.members()) {
                if (resource.getFileExtension().equalsIgnoreCase(QoSDescriptionType.WSLA.name())) {
                    serviceToUploaded.setQosDescriptionContent(Util.getResourceContentsAsByteArray((IFile) resource));
                    serviceToUploaded.setQosDescriptionType(QoSDescriptionType.WSLA);
                }
            }
             */
            // SET Security Description Model
            folder = file.getParent()
                    .getFolder(new Path(propertyValues
                            .get(ChorevolutionServiceThingSourceModelPrefs.PREF_SERVICE_THING_SECURITY_DESCRIPTION)
                            .getValue()));
            // find SECURITY
            for (IResource resource : folder.members()) {
                if (resource.getFileExtension().equalsIgnoreCase(SecurityDescriptionType.SECURITY.name())) {
                    serviceToUploaded.setSecurityDescriptionContent(Util.getResourceContentsAsByteArray((IFile) resource));
                    serviceToUploaded.setSecurityDescriptionType(SecurityDescriptionType.SECURITY);
                    serviceToUploaded.setServiceAuthenticationType(ChorevolutionCoreUtils.getAuthenticationType(URI.createPlatformResourceURI(((IFile)resource).getFullPath().toString(), true)));
                }
            }

            //Search for security custom auth file
            if(!customAuthPath.equals("")) {
            	try {
                    serviceToUploaded.setCustomAuthFileJAR(FileUtils.readFileToByteArray(new File(customAuthPath)));
            	}
            	catch(Exception e) {
            		serviceToUploaded.setCustomAuthFileJAR(null);
            	}
            	
            }
            else {
            	serviceToUploaded.setCustomAuthFileJAR(null);
            }
            
            
            // SET Identity Description Model

            ApacheSyncopeUtilities syncopeUtilities = new ApacheSyncopeUtilities(
                    restPropertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_URI).getValue(),
                    restPropertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_USERNAME).getValue(),
                    restPropertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_PASSWORD).getValue(),
                    restPropertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_DOMAIN).getValue());
            
            syncopeUtilities.createService(serviceToUploaded);

        } catch (CoreException e) {
            MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
                    ChorevolutionUIMessages.ServiceInventory_errorMessage,
                    NLS.bind(ChorevolutionUIMessages.ServiceInventory_serviceUploadError,
                            restPropertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_URI).getValue(),
                            e.getLocalizedMessage()));

            return false;

        } catch (Exception e) {
            MessageDialog.openError(ChorevolutionUIPlugin.getActiveWorkbenchShell(),
                    ChorevolutionUIMessages.ServiceInventory_errorMessage,
                    NLS.bind(ChorevolutionUIMessages.ServiceInventory_serviceUploadError,
                            restPropertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_URI).getValue(),
                            e.getLocalizedMessage()));
            return false;

        }
        
        return true;
    }


}
