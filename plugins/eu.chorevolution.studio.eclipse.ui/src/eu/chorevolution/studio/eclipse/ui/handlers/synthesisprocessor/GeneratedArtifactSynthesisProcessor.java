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
package eu.chorevolution.studio.eclipse.ui.handlers.synthesisprocessor;

import java.util.ArrayList;
import java.util.List;

import eu.chorevolution.transformations.generativeapproach.choreographyarchitecturegenerator.model.AdapterComponentData;
import eu.chorevolution.transformations.generativeapproach.choreographyarchitecturegenerator.model.BindingComponentData;
import eu.chorevolution.transformations.generativeapproach.choreographyarchitecturegenerator.model.ComponentData;
import eu.chorevolution.transformations.generativeapproach.choreographyarchitecturegenerator.model.ConsumerComponentData;
import eu.chorevolution.transformations.generativeapproach.choreographyarchitecturegenerator.model.SecurityComponentData;

public class GeneratedArtifactSynthesisProcessor {
	private List<ComponentData> clientComponents;
	private List<ComponentData> prosumerComponents;
	private List<ComponentData> providerComponents;

	public GeneratedArtifactSynthesisProcessor() {
		super();
		this.clientComponents = new ArrayList<ComponentData>();
		this.prosumerComponents = new ArrayList<ComponentData>();
		this.providerComponents = new ArrayList<ComponentData>();		
	}

	// Client Component
	public ComponentData addClientComponent(String participantName, String componentName, String componentLocation, boolean overrideComponent) {
		ComponentData componentData = new ComponentData(participantName, componentName, componentLocation);

		if (!clientComponents.contains(componentData)) {
			clientComponents.add(componentData);
		}
		
		if (overrideComponent){
			ComponentData original = clientComponents.get(clientComponents.indexOf(componentData));
			original.setName(componentName);
			original.setLocation(componentLocation);
		}
		
		return clientComponents.get(clientComponents.indexOf(componentData));
	}

	public void addBindingComponentToClientComponent(ComponentData componentData, String bindingComponentName,
			String bindingComponentLocation) {
		componentData.setBindingComponentData(new BindingComponentData(bindingComponentName, bindingComponentLocation));
	}

	public void addSecurityFilterToClientComponent(ComponentData componentData, String securityFilterName,
			String securityFilterLocation) {
		componentData.setSecurityComponentData(new SecurityComponentData(securityFilterName, securityFilterLocation));
	}
	
	public void addAdapterToClientComponent(ComponentData componentData, String adapterName,
			String adapterLocation) {
		componentData.setAdapterComponentData(new AdapterComponentData(adapterName, adapterLocation));
	}

	// Prosumer Component
	public ComponentData addProsumerComponent(String participantName, String componentName, String componentLocation, boolean overrideComponent) {
		ComponentData componentData = new ComponentData(participantName, componentName, componentLocation);

		if (!prosumerComponents.contains(componentData)) {
			prosumerComponents.add(componentData);
		}
		
		if (overrideComponent){
			ComponentData original = prosumerComponents.get(prosumerComponents.indexOf(componentData));
			original.setName(componentName);
			original.setLocation(componentLocation);
		}
		
		return prosumerComponents.get(prosumerComponents.indexOf(componentData));
	}

	public void addConsumerComponentToProsumerComponent(ComponentData componentData, String consumerComponentName,
			String consumerComponentLocation) {
		componentData
				.setConsumerComponentData(new ConsumerComponentData(consumerComponentName, consumerComponentLocation));
	}

	public void addBindingComponentToProsumerComponent(ComponentData componentData, String bindingComponentName,
			String bindingComponentLocation) {
		componentData.setBindingComponentData(new BindingComponentData(bindingComponentName, bindingComponentLocation));
	}

	public void addSecurityFilterToProsumerComponent(ComponentData componentData, String securityFilterName,
			String securityFilterLocation) {
		componentData.setSecurityComponentData(new SecurityComponentData(securityFilterName, securityFilterLocation));
	}
	
	public void addAdapterToProsumerComponent(ComponentData componentData, String adapterName,
			String adapterLocation) {
		componentData.setAdapterComponentData(new AdapterComponentData(adapterName, adapterLocation));
	}

	// Provider Component
	public ComponentData addProviderComponent(String participantName, String componentName, String componentLocation, boolean overrideComponent) {
		ComponentData componentData = new ComponentData(participantName, componentName, componentLocation);
		if (!providerComponents.contains(componentData)) {
			providerComponents.add(componentData);
		}
		
		if (overrideComponent){
			ComponentData original = providerComponents.get(providerComponents.indexOf(componentData));
			original.setName(componentName);
			original.setLocation(componentLocation);
		}
		
		return providerComponents.get(providerComponents.indexOf(componentData));
	}

	public void addBindingComponentToProviderComponent(ComponentData componentData, String bindingComponentName,
			String bindingComponentLocation) {
		componentData.setBindingComponentData(new BindingComponentData(bindingComponentName, bindingComponentLocation));
	}

	public void addSecurityFilterToProviderComponent(ComponentData componentData, String securityFilterName,
			String securityFilterLocation) {
		componentData.setSecurityComponentData(new SecurityComponentData(securityFilterName, securityFilterLocation));
	}
	
	public void addAdapterToProviderComponent(ComponentData componentData, String adapterName,
			String adapterLocation) {
		componentData.setAdapterComponentData(new AdapterComponentData(adapterName, adapterLocation));
	}

	
	public List<ComponentData> getClientComponents() {
		return clientComponents;
	}

	public List<ComponentData> getProsumerComponents() {
		return prosumerComponents;
	}

	public List<ComponentData> getProviderComponents() {
		return providerComponents;
	}

	public ComponentData getClientComponent(String participantName) {
		return addClientComponent(participantName, "", "", false);
	}

	public ComponentData getProsumerComponent(String participantName) {
		return addProsumerComponent(participantName, "", "", false);
	}

	public ComponentData getProviderComponent(String participantName) {
		return addProviderComponent(participantName, "", "", false);
	}

}
