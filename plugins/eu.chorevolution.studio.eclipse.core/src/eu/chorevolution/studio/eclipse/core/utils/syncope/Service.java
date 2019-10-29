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

public class Service {

	private String key;

    private String name;
    
    private String location;

    private byte[] interfaceDescriptionContent;

    private InterfaceDescriptionType interfaceDescriptionType;

    private byte[] interactionProtocolDescriptionContent;

    private InteractionProtocolDescriptionType interactionProtocolDescriptionType;

    private byte[] qosDescriptionContent;

    private QoSDescriptionType qosDescriptionType;

    private byte[] securityDescriptionContent;

    private SecurityDescriptionType securityDescriptionType;

    private ServiceAuthenticationType serviceAuthenticationType = ServiceAuthenticationType.NONE;
    
    private List<ServiceRole> serviceRoles;
    
    private byte[] customAuthFileJAR;

    public Service() {
        super();
    }

    public Service(String name, String location, byte[] interfaceDescriptionContent,
            InterfaceDescriptionType interfaceDescriptionType) {
        super();
        this.name = name;
        this.location = location;
        this.interfaceDescriptionContent = interfaceDescriptionContent;
        this.interfaceDescriptionType = interfaceDescriptionType;
    }

    
    
    public byte[] getCustomAuthFileJAR() {
		return customAuthFileJAR;
	}

	public void setCustomAuthFileJAR(byte[] customAuthFileJAR) {
		this.customAuthFileJAR = customAuthFileJAR;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public byte[] getInterfaceDescriptionContent() {
        return interfaceDescriptionContent;
    }

    public void setInterfaceDescriptionContent(byte[] interfaceDescriptionContent) {
        this.interfaceDescriptionContent = interfaceDescriptionContent;
    }

    public InterfaceDescriptionType getInterfaceDescriptionType() {
        return interfaceDescriptionType;
    }

    public void setInterfaceDescriptionType(InterfaceDescriptionType interfaceDescriptionType) {
        this.interfaceDescriptionType = interfaceDescriptionType;
    }

    public byte[] getInteractionProtocolDescriptionContent() {
        return interactionProtocolDescriptionContent;
    }

    public void setInteractionProtocolDescriptionContent(byte[] interactionProtocolDescriptionContent) {
        this.interactionProtocolDescriptionContent = interactionProtocolDescriptionContent;
    }

    public InteractionProtocolDescriptionType getInteractionProtocolDescriptionType() {
        return interactionProtocolDescriptionType;
    }

    public void setInteractionProtocolDescriptionType(
            InteractionProtocolDescriptionType interactionProtocolDescriptionType) {
        this.interactionProtocolDescriptionType = interactionProtocolDescriptionType;
    }

    public byte[] getQosDescriptionContent() {
        return qosDescriptionContent;
    }

    public void setQosDescriptionContent(byte[] qosDescriptionContent) {
        this.qosDescriptionContent = qosDescriptionContent;
    }

    public QoSDescriptionType getQosDescriptionType() {
        return qosDescriptionType;
    }

    public void setQosDescriptionType(QoSDescriptionType qosDescriptionType) {
        this.qosDescriptionType = qosDescriptionType;
    }

    public List<ServiceRole> getServiceRoles() {
        if (serviceRoles == null) {
            serviceRoles = new ArrayList<ServiceRole>();
        }
        return serviceRoles;
    }

    public void setServiceRoles(List<ServiceRole> serviceRoles) {
        this.serviceRoles = serviceRoles;
    }

    public void addServiceRole(ServiceRole serviceRole) {
        getServiceRoles().add(serviceRole);
    }

    public byte[] getSecurityDescriptionContent() {
        return securityDescriptionContent;
    }

    public void setSecurityDescriptionContent(byte[] securityDescriptionContent) {
        this.securityDescriptionContent = securityDescriptionContent;
    }

    public SecurityDescriptionType getSecurityDescriptionType() {
        return securityDescriptionType;
    }

    public void setSecurityDescriptionType(SecurityDescriptionType securityDescriptionType) {
        this.securityDescriptionType = securityDescriptionType;
    }

    public ServiceAuthenticationType getServiceAuthenticationType() {
        return serviceAuthenticationType;
    }

    public void setServiceAuthenticationType(ServiceAuthenticationType serviceAuthenticationType) {
        this.serviceAuthenticationType = serviceAuthenticationType;
    }
    
}
