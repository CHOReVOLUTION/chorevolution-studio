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
package eu.chorevolution.studio.eclipse.ui;

import org.eclipse.osgi.util.NLS;


public final class ChorevolutionUIMessages extends NLS {

	private static final String BUNDLE_NAME = ChorevolutionUIPlugin.PLUGIN_ID + ".ChorevolutionUIMessages";

	private ChorevolutionUIMessages() {
		// Do not instantiate
	}

	public static String Plugin_internalError;
	public static String Plugin_featuresNotImplemented;

	public static String OpenGraphicalEditor_errorMessage;
	
	public static String ProjectNature_errorMessage;
	public static String ProjectNature_addError;
	public static String ProjectNature_removeError;

	public static String PropertiesPage_title;
	public static String ChorevolutionCategoryPreferenceAndPropertyPage_description;
	public static String SynthesisProjectSourceFolderModelsPropertyTab_title;
	public static String SynthesisProjectSourceFolderModelsPropertyTab_description;
	public static String ServiceThingProjectSourceFolderModelsPropertyTab_title;
	public static String ServiceThingProjectSourceFolderModelsPropertyTab_description;
	public static String ServiceURIProperty_description;
	public static String ProjectSourceFolderModelsPropertyTab_noSourceFolderModelsDescription;
	public static String ProjectSourceFolderModelsPropertyTab_sourceFolderModelsDescription;
	public static String ChorevolutionSynthesisProcessorWizard_notOneChoreographyDiagram;
	
	public static String TransformatorORGeneator_Error;
	public static String TransformatorORGeneator_Information;
	public static String DeployAndEnactment_Information;
	public static String DeployAndEnactment_Error;
	public static String Validation_Information;
	
	// used into GenerateChoreographyArchitecture.java
	public static String Transformator_bpmn2chorarchError;
	public static String Transformator_bpmn2chorarchInformation;
	
	// used into GenerateAdapter.java
	public static String Generator_adapterError;
	public static String Generator_adapterInformation;
	
	// used into GenerateChoreographySpecification.java
	public static String Transformator_chorarch2choreospecError;
	public static String Transformator_chorarch2choreospecErro_compile_project;
	public static String Transformator_chorarch2choreospecInformation;
	
	// used into EnactChoreographySpecification.java
	public static String EnactAndDeploy_choreographyDeploymentDescriptorError;
	public static String EnactAndDeploy_choreographyDeploymentDescriptorInformation;
	public static String Deploy_choreographyDeploymentDescriptorInformation;
	public static String Enact_choreographyDeploymentDescriptorInformation;
	public static String Enact_choreographyDeploymentDescriptorInformationError;
	public static String Update_choreographyDeploymentDescriptorInformation;
	public static String Update_choreographyDeploymentDescriptorInformationError;
	public static String Update_choreographyDeploymentDescriptorError;

	
	// used into ValidateBpmnChoreographyDiagram.java
	public static String Vaidation_bpmnChoreographyDiagramInformation;
	public static String Vaidation_bpmnChoreographyDiagramError;

	
	public static String ServiceInventory_errorMessage;
	public static String ServiceInventory_infoMessage;
	public static String ServiceInventory_serviceUploaded;
	public static String ServiceInventory_serviceRoleCreated;
	public static String ServiceInventory_connectionRefused;
	public static String ServiceInventory_confirmUploadService;
	public static String ServiceInventory_servicesCreationError;	
	public static String ServiceInventory_serviceUploadError;	
	public static String ServiceInventory_serviceSearchError;
	public static String ServiceInventory_serviceRoleSearchError;
	public static String ServiceInventory_servicesRoleCreationError;
	public static String ServiceInventory_securityRoleSearchError;
	public static String ServiceInventory_enactmentSearchError;
	
	public static String Bpmn2ChoreographyProjector_projectionError;
	public static String CoordinationDelegate_generatorError;
	public static String SecurityFilters_generatorError;
	public static String BindingComponents_generatorError;
	public static String Adapter_generatorError;

	
	public static String ChorevolutionServiceThing_title;
	// used into ChorevolutionSynthesisProcessorWizard.java
	public static String ChorevolutionSynthesisProcessor_title;
	public static String ChorevolutionSynthesisProcessorWizard_openError;
	public static String ChorevolutionValidator_openError;
	// used into ProviderServiceRoleBindingsWizardPage.java
	public static String ProviderServiceRoleBindingsWizardPage_name;
	public static String ProviderServiceRoleBindingsWizardPage_title;
	public static String ProviderServiceRoleBindingsWizardPage_description;
	// used into BindingComponentsWizardPage.java
	public static String BindingComponentsWizardPage_name;
	public static String BindingComponentsWizardPage_title;
	public static String BindingComponentsWizardPage_description;
	// used into SecurityFiltersWizardPage.java
	public static String SecurityFiltersWizardPage_name;
	public static String SecurityFiltersWizardPage_title;
	public static String SecurityFiltersWizardPage_description;
	// used into CoordinationDelegatesWizardPage.java
	public static String CoordinationDelegatesWizardPage_name;
	public static String CoordinationDelegatesWizardPage_title;
	public static String CoordinationDelegatesWizardPage_description;
	// used into AdapterGeneratorWizardPage.java
	public static String AdapterGeneratorWizardPage_name;
	public static String AdapterGeneratorWizardPage_title;
	public static String AdapterGeneratorWizardPage_description;
	

	static {
		NLS.initializeMessages(BUNDLE_NAME, ChorevolutionUIMessages.class);
	}
}
