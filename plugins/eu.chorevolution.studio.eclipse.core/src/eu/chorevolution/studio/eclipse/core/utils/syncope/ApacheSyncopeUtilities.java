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
package eu.chorevolution.studio.eclipse.core.utils.syncope;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.common.util.Base64Utility;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.syncope.client.lib.SyncopeClient;
import org.apache.syncope.client.lib.SyncopeClientFactoryBean;
import org.apache.syncope.common.lib.SyncopeConstants;
import org.apache.syncope.common.lib.patch.AnyObjectPatch;
import org.apache.syncope.common.lib.patch.RelationshipPatch;
import org.apache.syncope.common.lib.search.AnyObjectFiqlSearchConditionBuilder;
import org.apache.syncope.common.lib.search.GroupFiqlSearchConditionBuilder;
import org.apache.syncope.common.lib.to.AnyObjectTO;
import org.apache.syncope.common.lib.to.AttrTO;
import org.apache.syncope.common.lib.to.GroupTO;
import org.apache.syncope.common.lib.to.PagedResult;
import org.apache.syncope.common.lib.to.RelationshipTO;
import org.apache.syncope.common.lib.types.PatchOperation;
import org.apache.syncope.common.rest.api.beans.AnyQuery;
import org.apache.syncope.common.rest.api.service.AnyObjectService;
import org.apache.syncope.common.rest.api.service.ChoreographyService;
import org.apache.syncope.common.rest.api.service.GroupService;
import org.eclipse.core.resources.IProject;

import eu.chorevolution.idm.common.to.ChoreographyTO;
import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePlugin;
import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePreferences;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServicesURIPrefs;

public class ApacheSyncopeUtilities {
	public static final String ANY_OBJECT_TO_NAME_PROPERTY = "name";
	public static final String ANY_OBJECT_TO_KEY_PROPERTY = "key";
	public static final String CHOREOGRAPHY_ID_SCHEMA = "id";

	// Choreography Schema
	public static final String CHOREOGRAPHY_SCHEMA = "Choreography";
	public static final String CHOREOGRAPHY_IS_CHOREOGRAPHY = "isChoreography";

	// Enactment Schema
	public static final String ENACTMENT_ENGINE_SCHEMA = "ENACTMENT ENGINE";
	public static final String ENACTMENT_ENGINE_URL = "enactmentEngineBaseURL";

	// Sythesis Schema
	public static final String SYNTHESIS_PROCESSOR_SCHEMA = "SYNTHESIS PROCESSOR";
	public static final String SYNTHESIS_PROCESSOR_URL = "synthesisProcessorBaseURL";
	
	// Service Role Schema
	public static final String SERVICE_ROLE_SCHEMA = "SERVICE ROLE";
	public static final String SERVICE_ROLE_DESCRIPTION = "Service Role Description";

	// Service Schema
	public static final String SERVICE_SCHEMA = "SERVICE";

	public static final String INTERFACE_DESCRIPTION_CONTENT = "Service Interface Description - content";
	public static final String INTERFACE_DESCRIPTION_TYPE = "Service Interface Description - type"; // enumerationValues="WSDL;WADL;GIDL"

	public static final String INTERACTIONPROTOCOL_DESCRIPTION_CONTENT = "Service Interaction Protocol Description - content";
	public static final String INTERACTIONPROTOCOL_DESCRIPTION_TYPE = "Service Interaction Protocol Description - type"; // "IPLTS;WSBPEL"

	public static final String QOS_DESCRIPTION_CONTENT = "Service QoS Description - content";
	public static final String QOS_DESCRIPTION_TYPE = "Service QoS Description - type"; // enumerationValues=WSLA"

	public static final String SECURITY_DESCRIPTION_CONTENT = "Service Security Description - content";
	public static final String SECURITY_DESCRIPTION_TYPE = "Service Security Description - type"; // enumerationValues=SECURITY"
	public static final String SERVICE_AUTHENTICATION_TYPE = "Service Authentication Type"; // enumerationValues=NONE;SHARED;PER_USER;CUSTOM"

	public static final String SERVICE_LOCATION = "Service Location";

	public static final String SERVICE_CUSTOM_AUTH_JAR = "Custom Authentication JAR File";
	
	// Relation Service Service Rore [1..n]
	public static final String SERVICE_ROLE_RELATIONSHIP = "SERVICE ROLE MEMBERSHIP";

	private SyncopeClient client;
	private AnyObjectService aoservice;
	private GroupService groupService;
	private ChoreographyService choreographyService;

	public ApacheSyncopeUtilities(String restUrl, String username, String password, String domain) {
		client = new SyncopeClientFactoryBean().setAddress(restUrl).setDomain(domain).create(username, password);
		aoservice = client.getService(AnyObjectService.class);
		groupService = client.getService(GroupService.class);
		choreographyService = client.getService(ChoreographyService.class);
		WebClient.client(choreographyService).accept(MediaType.APPLICATION_JSON_TYPE)
				.type(MediaType.APPLICATION_XML_TYPE);

	}

	// Service Management
	public void createService(Service service) {
		// create service
		AnyObjectTO serviceAnyObjectTO = new AnyObjectTO();
		serviceAnyObjectTO.setRealm(SyncopeConstants.ROOT_REALM);
		serviceAnyObjectTO.setName(service.getName());
		serviceAnyObjectTO.setType(SERVICE_SCHEMA);

		serviceAnyObjectTO.getPlainAttrs()
				.add(new AttrTO.Builder().schema(SERVICE_LOCATION).value(service.getLocation()).build());

		if (service.getInterfaceDescriptionContent() != null) {
			serviceAnyObjectTO.getPlainAttrs().add(new AttrTO.Builder().schema(INTERFACE_DESCRIPTION_CONTENT)
					.value(Base64Utility.encode(service.getInterfaceDescriptionContent())).build());
			serviceAnyObjectTO.getPlainAttrs().add(new AttrTO.Builder().schema(INTERFACE_DESCRIPTION_TYPE)
					.value(service.getInterfaceDescriptionType().name()).build());
		}

		if (service.getInteractionProtocolDescriptionContent() != null) {
			serviceAnyObjectTO.getPlainAttrs().add(new AttrTO.Builder().schema(INTERACTIONPROTOCOL_DESCRIPTION_CONTENT)
					.value(Base64Utility.encode(service.getInteractionProtocolDescriptionContent())).build());
			serviceAnyObjectTO.getPlainAttrs().add(new AttrTO.Builder().schema(INTERACTIONPROTOCOL_DESCRIPTION_TYPE)
					.value(service.getInteractionProtocolDescriptionType().name()).build());
		}

		if (service.getQosDescriptionContent() != null) {
			serviceAnyObjectTO.getPlainAttrs().add(new AttrTO.Builder().schema(QOS_DESCRIPTION_CONTENT)
					.value(Base64Utility.encode(service.getQosDescriptionContent())).build());
			serviceAnyObjectTO.getPlainAttrs().add(new AttrTO.Builder().schema(QOS_DESCRIPTION_TYPE)
					.value(service.getQosDescriptionType().name()).build());
		}

		if (service.getSecurityDescriptionContent() != null) {
			serviceAnyObjectTO.getPlainAttrs().add(new AttrTO.Builder().schema(SECURITY_DESCRIPTION_CONTENT)
					.value(Base64Utility.encode(service.getSecurityDescriptionContent())).build());
			serviceAnyObjectTO.getPlainAttrs().add(new AttrTO.Builder().schema(SECURITY_DESCRIPTION_TYPE)
					.value(service.getSecurityDescriptionType().name()).build());
		}

		if (service.getServiceAuthenticationType() != null) {
			serviceAnyObjectTO.getPlainAttrs().add(new AttrTO.Builder().schema(SERVICE_AUTHENTICATION_TYPE)
					.value(service.getServiceAuthenticationType().name()).build());
		}
		
		if (service.getCustomAuthFileJAR() != null) {
			serviceAnyObjectTO.getPlainAttrs().add(new AttrTO.Builder().schema(SERVICE_CUSTOM_AUTH_JAR)
					.value(Base64Utility.encode(service.getCustomAuthFileJAR())).build());
		}

		/*
		 * skip the setting of relations to roles for (ServiceRole serviceRole :
		 * service.getServiceRoles()) {
		 * serviceAnyObjectTO.getRelationships().add(new
		 * RelationshipTO.Builder().type(SERVICE_ROLE_RELATIONSHIP)
		 * .right(SERVICE_ROLE_SCHEMA, serviceRole.getRoleKey()).build()); }
		 */

		aoservice.create(serviceAnyObjectTO);

		// Fix ISSUE SYNCOPE-907

		serviceAnyObjectTO = getServiceByName(service.getName());
		updateServiceRelationships(serviceAnyObjectTO, service.getServiceRoles());

	}

	public AnyObjectTO getServiceByName(String serviceName) {
		return aoservice.search(new AnyQuery.Builder().fiql(new AnyObjectFiqlSearchConditionBuilder(SERVICE_SCHEMA)
				.is(ANY_OBJECT_TO_NAME_PROPERTY).equalTo(serviceName).query()).build()).getResult().get(0);
	}

	public List<AnyObjectTO> getServicesByNameContains(String serviceName) {
		List<AnyObjectTO> res = new ArrayList<>();

		PagedResult<AnyObjectTO> pageres;

		int page = 1;
		do {
			pageres = aoservice.search(
					new AnyQuery.Builder().page(page).fiql(new AnyObjectFiqlSearchConditionBuilder(SERVICE_SCHEMA)
							.is(ANY_OBJECT_TO_NAME_PROPERTY).equalTo("*" + serviceName + "*").query()).build());

			res.addAll(pageres.getResult());
			page++;
		} while (pageres.getNext() != null);
		return res;
	}

	public List<AnyObjectTO> getServicesByRoleName(String roleName) {
		AnyObjectTO role = getRoleByName(roleName);

		List<AnyObjectTO> res = new ArrayList<>();

		PagedResult<AnyObjectTO> pageres;

		int page = 1;
		do {
			pageres = aoservice.search(new AnyQuery.Builder().page(page).fiql(
					new AnyObjectFiqlSearchConditionBuilder(SERVICE_SCHEMA).inRelationships(role.getKey()).query())
					.build());

			res.addAll(pageres.getResult());
			page++;
		} while (pageres.getNext() != null);
		return res;
	}

	// Service Role Management
	public AnyObjectTO createRole(String roleName, String roleDescription) {
		// create service inventory role
		AnyObjectTO serviceInventoryRole = new AnyObjectTO();
		serviceInventoryRole.setRealm(SyncopeConstants.ROOT_REALM);
		serviceInventoryRole.setName(roleName);
		serviceInventoryRole.setType(SERVICE_ROLE_SCHEMA);
		serviceInventoryRole.getPlainAttrs()
				.add(new AttrTO.Builder().schema(SERVICE_ROLE_DESCRIPTION).value(roleDescription).build());
		aoservice.create(serviceInventoryRole);

		return getRoleByName(roleName);
	}

	public AnyObjectTO getRoleByName(String roleName) {
		return aoservice.search(new AnyQuery.Builder().fiql(new AnyObjectFiqlSearchConditionBuilder(SERVICE_ROLE_SCHEMA)
				.is(ANY_OBJECT_TO_NAME_PROPERTY).equalTo(roleName).query()).build()).getResult().get(0);
	}

	public List<AnyObjectTO> getRolesByNameContains(String roleName) {
		List<AnyObjectTO> res = new ArrayList<>();

		PagedResult<AnyObjectTO> pageres;

		int page = 1;
		do {
			pageres = aoservice.search(
					new AnyQuery.Builder().page(page).fiql(new AnyObjectFiqlSearchConditionBuilder(SERVICE_ROLE_SCHEMA)
							.is(ANY_OBJECT_TO_NAME_PROPERTY).equalTo("*" + roleName + "*").query()).build());

			res.addAll(pageres.getResult());
			page++;
		} while (pageres.getNext() != null);
		return res;
	}

	// Security Management
	public List<GroupTO> getSecurityRolesByNameContains(String securityRoleName) {
		List<GroupTO> res = new ArrayList<>();

		PagedResult<GroupTO> pageres;

		int page = 1;
		do {
			pageres = groupService.search(new AnyQuery.Builder()
					.page(page).fiql(new GroupFiqlSearchConditionBuilder().is(CHOREOGRAPHY_IS_CHOREOGRAPHY).nullValue()
							.and().is(ANY_OBJECT_TO_NAME_PROPERTY).equalTo("*" + securityRoleName + "*").query())
					.build());

			res.addAll(pageres.getResult());
			page++;
		} while (pageres.getNext() != null);
		return res;
	}

	// Enactment Engine Management
	public List<AnyObjectTO> getEnactmentEnginesByNameContains(String enactmentEngineName) {
		List<AnyObjectTO> res = new ArrayList<>();

		PagedResult<AnyObjectTO> pageres;

		int page = 1;
		do {
			pageres = aoservice
					.search(new AnyQuery.Builder().page(page)
							.fiql(new AnyObjectFiqlSearchConditionBuilder(ENACTMENT_ENGINE_SCHEMA)
									.is(ANY_OBJECT_TO_NAME_PROPERTY).equalTo("*" + enactmentEngineName + "*").query())
							.build());

			res.addAll(pageres.getResult());
			page++;
		} while (pageres.getNext() != null);
		return res;
	}
	
	// Synthesis Processor Management
	public List<AnyObjectTO> getSynthesisProcessorsByNameContains(String synthesisProcessorName) {
		List<AnyObjectTO> res = new ArrayList<>();

		PagedResult<AnyObjectTO> pageres;

		int page = 1;
		do {
			pageres = aoservice
					.search(new AnyQuery.Builder().page(page)
							.fiql(new AnyObjectFiqlSearchConditionBuilder(SYNTHESIS_PROCESSOR_SCHEMA)
									.is(ANY_OBJECT_TO_NAME_PROPERTY).equalTo("*" + synthesisProcessorName + "*").query())
							.build());

			res.addAll(pageres.getResult());
			page++;
		} while (pageres.getNext() != null);
		return res;
	}
	

	// Choreography Management
	public void createChoreography(IProject project, String choreographyName, String synthesisProcessorKey, String choreographyDescription,
			byte[] choreographyDeployment, byte[] choreographyImage, byte[] choreographyDiagram, byte[] choreographyMessages) {
		ChoreographyTO choreographyToUpload = new ChoreographyTO();
		choreographyToUpload.setName(choreographyName);
		choreographyToUpload.setSynthesisProcessorKey(synthesisProcessorKey);
		choreographyToUpload.setDescription(choreographyDescription);
		choreographyToUpload.setChorspec(choreographyDeployment);
		choreographyToUpload.setImage(choreographyImage);
		choreographyToUpload.setDiagram(choreographyDiagram);
		choreographyToUpload.setMessages(choreographyMessages);
		
		Response chorResponse=choreographyService.create(choreographyToUpload);
		//i save the chor name in the properties
		String chorID=chorResponse.getHeaderString("X-Syncope-Key");
    	ChorevolutionCorePreferences prefs = ChorevolutionCorePreferences.getProjectOrWorkspacePreferences(project, ChorevolutionCorePlugin.PLUGIN_ID);
        prefs.putString(ChorevolutionServicesURIPrefs.PREF_CHOREOGRAPHY_ID, chorID);
	}

	public void updateChoreography(String choreographyID, String choreographyName, byte[] choreographyDeployment) {
		ChoreographyTO choreographyToUpload = new ChoreographyTO();
		choreographyToUpload.setChoreographyId(choreographyID);
		choreographyToUpload.setName(choreographyName);
		choreographyToUpload.setChorspec(choreographyDeployment);
		
		choreographyService.update(choreographyToUpload);	
	}
	
	public void enactChoreography(String choreographyID, EnactmentEngine enactmentEngine) {
		choreographyService.enact(choreographyID, enactmentEngine.getKey());
		
	}
	
	
	public List<ChoreographyTO> getChoreographyList() {
		Response response = choreographyService.list();
		List<ChoreographyTO> listChoreography = response.readEntity(new GenericType<List<ChoreographyTO>>() {});
		return listChoreography;
	}
	
	public List<ChoreographyTO> getChoreographyListByName(String choreographyName) {

		List<ChoreographyTO> listChoreographies = getChoreographyList();

		List<ChoreographyTO> listToReturn = new ArrayList<ChoreographyTO>();
		for(ChoreographyTO choreography : listChoreographies) {
			if(choreography.getName().contains(choreographyName)) {
				listToReturn.add(choreography);
			}
		}
		return listToReturn;
	}
	
	public ChoreographyTO getChoreographyByKey(String choreographyKey) {
		
		Response response = choreographyService.read(choreographyKey);
		ChoreographyTO choreographyToRead =  response.readEntity(ChoreographyTO.class);
		
		return choreographyToRead;
	}
	
	public GroupTO getChoreographyByName(String choreographyName) {
		return groupService
				.search(new AnyQuery.Builder()
						.fiql(new GroupFiqlSearchConditionBuilder().is(CHOREOGRAPHY_IS_CHOREOGRAPHY).equalTo("true")
								.and().is(ANY_OBJECT_TO_NAME_PROPERTY).equalTo(choreographyName).query())
						.build())
				.getResult().get(0);
	}

	public List<GroupTO> getChoreographiesByNameContains(String choreographyName) {
		List<GroupTO> res = new ArrayList<>();

		PagedResult<GroupTO> pageres;

		int page = 1;
		do {
			pageres = groupService.search(new AnyQuery.Builder().page(page)
					.fiql(new GroupFiqlSearchConditionBuilder().is(CHOREOGRAPHY_IS_CHOREOGRAPHY).equalTo("true").and()
							.is(ANY_OBJECT_TO_NAME_PROPERTY).equalTo("*" + choreographyName + "*").query())
					.build());

			res.addAll(pageres.getResult());
			page++;
		} while (pageres.getNext() != null);
		return res;
	}

	private void updateServiceRelationships(AnyObjectTO serviceAnyObjectTO, List<ServiceRole> serviceRoles) {
		AnyObjectPatch anyObjectPatch = new AnyObjectPatch();
		anyObjectPatch.setKey(serviceAnyObjectTO.getKey());

		for (ServiceRole serviceRole : serviceRoles) {
			anyObjectPatch.getRelationships().add(new RelationshipPatch.Builder()
					.operation(PatchOperation.ADD_REPLACE).relationshipTO(new RelationshipTO.Builder()
							.type(SERVICE_ROLE_RELATIONSHIP).right(SERVICE_ROLE_SCHEMA, serviceRole.getKey()).build())
					.build());
		}

		aoservice.update(anyObjectPatch);
	}
}
