package eu.chorevolution.studio.eclipse.ui.handlers.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.bpmn2.Choreography;
import org.eclipse.bpmn2.ChoreographyTask;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.Wizard;

import eu.chorevolution.studio.eclipse.core.ChorevolutionCoreUtils;
import eu.chorevolution.studio.eclipse.core.utils.bpmn.BPMNUtil;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIMessages;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.AdapterModelDataType;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.CorrelationChoreographyTaskTableViewerRecord;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.ParticipantTableViewerRecord;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.RolesViewerRecord;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.SecurityFilter;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.page.AdapterGeneratorWizardPage;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.page.BindingComponentsWizardPage;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.page.CoordinationDelegatesWizardPage;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.page.ProviderServiceRoleBindingsWizardPage;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.page.SecurityFiltersWizardPage;

public class ChorevolutionSynthesisProcessorWizard extends Wizard {
    private IProject project;

    private List<ParticipantTableViewerRecord> providerParticipants;
    private List<ParticipantTableViewerRecord> prosumerParticipants;
    private List<ParticipantTableViewerRecord> clientParticipants;
    private Map<String, List<CorrelationChoreographyTaskTableViewerRecord>> correlationChoreographyTaskMap;
    private Map<String, List<String>> correlatedChoreographyTaskWithMap;
    private List<Choreography> choreographies;
    private IFile bpmnFile;
    private byte[] typesXSD;
    
    private ProviderServiceRoleBindingsWizardPage providerServiceRoleBindingsWizardPage;
    private BindingComponentsWizardPage bindingComponentsWizardPage;
    private SecurityFiltersWizardPage securityFiltersWizardPage;
    private AdapterGeneratorWizardPage adapterGeneratorWizardPage;
    private CoordinationDelegatesWizardPage coordinationDelegatesWizardPage;

    public ChorevolutionSynthesisProcessorWizard(IProject project, List<Choreography> choreographies, IFile bpmnFile, byte[] typesXSD) throws Exception {
        setWindowTitle(ChorevolutionUIMessages.ChorevolutionSynthesisProcessor_title);
        setNeedsProgressMonitor(true);

        this.project = project;
        this.choreographies = choreographies;
        this.bpmnFile = bpmnFile;
        this.typesXSD = typesXSD;

        // initialize provider participants
        this.providerParticipants = new ArrayList<ParticipantTableViewerRecord>();
        for (String serviceRole : !choreographies.isEmpty() ? BPMNUtil.getProviderRoles(choreographies)
                : new ArrayList<String>()) {
            ParticipantTableViewerRecord serviceRecord = new ParticipantTableViewerRecord(serviceRole);
            serviceRecord.setGenerate(false);
            providerParticipants.add(serviceRecord);
        }

        // inizialize prosumer participants
        this.prosumerParticipants = new ArrayList<ParticipantTableViewerRecord>();
        for (String serviceRole : !choreographies.isEmpty() ? BPMNUtil.getProsumerRoles(choreographies)
                : new ArrayList<String>()) {
            ParticipantTableViewerRecord serviceRecord = new ParticipantTableViewerRecord(serviceRole);
            serviceRecord.setServiceProjectName("cd" + ChorevolutionCoreUtils.removeBlankSpaces(serviceRole));
            serviceRecord.setGenerate(true);
            prosumerParticipants.add(serviceRecord);
        }

        // initialize client participants
        this.clientParticipants = new ArrayList<ParticipantTableViewerRecord>();
        for (String serviceRole : !choreographies.isEmpty() ? BPMNUtil.getClientRoles(choreographies): new ArrayList<String>()) {
            ParticipantTableViewerRecord serviceRecord = new ParticipantTableViewerRecord(serviceRole);
            serviceRecord.setServiceProjectName("cd" + ChorevolutionCoreUtils.removeBlankSpaces(serviceRole));
            serviceRecord.setGenerate(true);
            clientParticipants.add(serviceRecord);
        }
        
        // initialize correlationChoreographyTaskMap
        this.correlationChoreographyTaskMap = new HashMap<String, List<CorrelationChoreographyTaskTableViewerRecord>>();
        this.correlatedChoreographyTaskWithMap = new HashMap<String, List<String>>();
        for (ParticipantTableViewerRecord clientParticipant : this.clientParticipants){
        	String clientParticipantName = clientParticipant.getParticipant();
        	
        	Map<ChoreographyTask, List<ChoreographyTask>> getTaskCorrelations = BPMNUtil.getTaskCorrelations(choreographies,clientParticipantName);
        	
        	List<CorrelationChoreographyTaskTableViewerRecord> choreographyTaskTableViewerRecords = new ArrayList<CorrelationChoreographyTaskTableViewerRecord>();
	        for (Map.Entry<ChoreographyTask, List<ChoreographyTask>> entry : getTaskCorrelations.entrySet()) {
	            CorrelationChoreographyTaskTableViewerRecord choreographyTaskTableViewerRecord = new CorrelationChoreographyTaskTableViewerRecord(entry.getKey().getName());
	            choreographyTaskTableViewerRecord.setCorrelatedWith(CorrelationChoreographyTaskTableViewerRecord.NONE_CORRELLATION);
	            choreographyTaskTableViewerRecords.add(choreographyTaskTableViewerRecord);
	            correlationChoreographyTaskMap.put(clientParticipantName, choreographyTaskTableViewerRecords);
	        }
	        
	        List<String> ChoreographyTasksWith = new ArrayList<String>();
	        for (Map.Entry<ChoreographyTask, List<ChoreographyTask>> entry : getTaskCorrelations.entrySet()) {
	        	for (ChoreographyTask choreographyTask : entry.getValue()) {
		            ChoreographyTasksWith.add(choreographyTask.getName());
		            correlatedChoreographyTaskWithMap.put(clientParticipantName, ChoreographyTasksWith);
		        }
	        }
	        
        }
    }

    @Override
    public void addPages() {
        providerServiceRoleBindingsWizardPage = new ProviderServiceRoleBindingsWizardPage(project,
                providerParticipants);
        addPage(providerServiceRoleBindingsWizardPage);

        bindingComponentsWizardPage = new BindingComponentsWizardPage(providerParticipants);
        addPage(bindingComponentsWizardPage);
        
        securityFiltersWizardPage = new SecurityFiltersWizardPage(project,providerParticipants);
        addPage(securityFiltersWizardPage);
        
        adapterGeneratorWizardPage = new AdapterGeneratorWizardPage(project,providerParticipants, choreographies, bpmnFile, typesXSD);
        addPage(adapterGeneratorWizardPage);
        
        coordinationDelegatesWizardPage = new CoordinationDelegatesWizardPage(project, clientParticipants, prosumerParticipants, correlationChoreographyTaskMap, correlatedChoreographyTaskWithMap);
        addPage(coordinationDelegatesWizardPage);
        
    }

    @Override
    public boolean canFinish() {
    	return adapterGeneratorWizardPage.isPageComplete()
    			&& coordinationDelegatesWizardPage.enableFinish();
    }
    
    @Override
    public boolean performFinish() {
        return providerServiceRoleBindingsWizardPage.isPageComplete()
                // && bindingComponentsWizardPage.isPageComplete()
                && securityFiltersWizardPage.isPageComplete() 
                && coordinationDelegatesWizardPage.isPageComplete()
                && adapterGeneratorWizardPage.isPageComplete();
        
    }
    
    public List<ParticipantTableViewerRecord> getClientServiceTableViewerRecords() {
        return clientParticipants;
    }

    public List<ParticipantTableViewerRecord> getProsumerServiceTableViewerRecords() {
        return prosumerParticipants;
    }

    public List<ParticipantTableViewerRecord> getProviderServiceTableViewerRecords() {
        return providerParticipants;
    }
    
    public List<RolesViewerRecord> getServiceToBeMappedTableViewerRecords() {
        return adapterGeneratorWizardPage.getProviderServiceComposite().getUniqueNotEmptyServiceTableViewerRecords();
    }
    
    public List<AdapterModelDataType> getAdapterModelsToBeCreated(){
		return adapterGeneratorWizardPage.getAdapterModelsToBeCreated();
	}
    
    public boolean isAdapterCreationFinished() {
    	return adapterGeneratorWizardPage.isAdapterCreationFinished();
    }
    
    public  Map<String, List<CorrelationChoreographyTaskTableViewerRecord>> getCorrelationChoreographyTasks(){
        return correlationChoreographyTaskMap;
    }
    
    public SecurityFilter getSecurityFilterInformation(){
        return securityFiltersWizardPage.getSecurityFilterModel();
    }
    
    public boolean generateBindingComponentForClientParticipant(){
    	return bindingComponentsWizardPage.generateBindingComponentForClientParticipant();
    }

}
