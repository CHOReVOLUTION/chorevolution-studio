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
package eu.chorevolution.studio.eclipse.ui.handlers.wizard.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Vector;

public class SecurityFilter {

    private boolean isSecuredChoreography;

    private AuthenticationType authenticationType;
    private Vector<SecurityRole>  selectedSecurityRoleRecords;
    private CommunicationType communicationType;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);


    public SecurityFilter(boolean isSecuredChoreography) {
        setSecuredChoreography(isSecuredChoreography);
        setAuthenticationType(AuthenticationType.getDefault());
        this.selectedSecurityRoleRecords = new Vector<SecurityRole>();
        setCommunicationType(CommunicationType.getDefault());
    }
    
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public boolean isSecuredChoreography() {
        return isSecuredChoreography;
    }

    public void setSecuredChoreography(boolean isSecuredChoreography) {
        propertyChangeSupport.firePropertyChange("isSecuredChoreography", this.isSecuredChoreography,
                this.isSecuredChoreography = isSecuredChoreography);
    }

    public AuthenticationType getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(AuthenticationType authenticationType) {
        propertyChangeSupport.firePropertyChange("authenticationType", this.authenticationType,
                this.authenticationType = authenticationType);
    }

    public Vector<SecurityRole> getselectedSecurityRoleRecords() {
        return selectedSecurityRoleRecords;
    }

    public void setselectedSecurityRoleRecords(Vector<SecurityRole> selectedSecurityRoleRecords) {
        propertyChangeSupport.firePropertyChange("selectedSecurityRoleRecords", this.selectedSecurityRoleRecords,
                this.selectedSecurityRoleRecords = selectedSecurityRoleRecords);
    }

    public CommunicationType getCommunicationType() {
        return communicationType;
    }

    public void setCommunicationType(CommunicationType communicationType) {
        propertyChangeSupport.firePropertyChange("communicationType", this.communicationType,
                this.communicationType = communicationType);
    }

    public static enum AuthenticationType {
        USERNAME_PASSWORD("Username and Password");

        private String label;

        AuthenticationType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static AuthenticationType getDefault() {
            return AuthenticationType.USERNAME_PASSWORD;
        }
    }

    public static enum AuthorizationType {
        YES("yes"), NO("no");

        private String label;

        AuthorizationType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static AuthorizationType getDefault() {
            return AuthorizationType.NO;
        }
    }

    public static enum CommunicationType {
        HTTP("HTTP"), HTTPS("HTTPS");

        private String label;

        CommunicationType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static CommunicationType getDefault() {
            return CommunicationType.HTTP;
        }
    }
}
