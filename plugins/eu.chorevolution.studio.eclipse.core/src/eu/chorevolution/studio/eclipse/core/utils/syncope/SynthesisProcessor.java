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

import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPreferenceData;

public class SynthesisProcessor {
	private String name;
	private String location;//is the URI
	private String key;
	private String loginUri;
	private String projectorUri;
	private String validatorUri;
	private String cdGeneratorUri;
	private String bcGeneratorUri;
	private String sfGeneratorUri;
	private String architectureUri;
	private String deploymentUri;
	

	public SynthesisProcessor() {
		super();
	}

	public SynthesisProcessor(String name) {
		super();
		this.name = name;
	}

	public SynthesisProcessor(String key, String name) {
		super();
		this.key = key;
		this.name = name;
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
	
	// TODO do better!
	public void setLocationURIs(String location) {
		this.location = location;
		
		setLoginUri(location+"/login/");
		setProjectorUri(location+"/choreographyprojectiongenerator/");
		setValidatorUri(location+"/choreographyvalidator/");
		setCdGeneratorUri(location+"/coordinationdelegategenerator/");
		setBcGeneratorUri(location+"/bindingComponentGenerator/");
		setSfGeneratorUri(location+"/securityfiltergenerator/");
		setArchitectureUri(location+"/choreographyarchitecturegenerator/");
		setDeploymentUri(location+"/choreographydeploymentdescriptorgenerator/");
		
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof SynthesisProcessor))
			return false;
		SynthesisProcessor synthesis = (SynthesisProcessor) obj;
		return this.getKey().equals(synthesis.getKey());
	}

	public String getLoginUri() {
		return loginUri;
	}

	public void setLoginUri(String loginUri) {
		this.loginUri = loginUri;
	}

	public String getProjectorUri() {
		return projectorUri;
	}

	public void setProjectorUri(String projectorUri) {
		this.projectorUri = projectorUri;
	}

	public String getValidatorUri() {
		return validatorUri;
	}

	public void setValidatorUri(String validatorUri) {
		this.validatorUri = validatorUri;
	}

	public String getCdGeneratorUri() {
		return cdGeneratorUri;
	}

	public void setCdGeneratorUri(String cdGeneratorUri) {
		this.cdGeneratorUri = cdGeneratorUri;
	}

	public String getBcGeneratorUri() {
		return bcGeneratorUri;
	}

	public void setBcGeneratorUri(String bcGeneratorUri) {
		this.bcGeneratorUri = bcGeneratorUri;
	}

	public String getSfGeneratorUri() {
		return sfGeneratorUri;
	}

	public void setSfGeneratorUri(String sfGeneratorUri) {
		this.sfGeneratorUri = sfGeneratorUri;
	}

	public String getArchitectureUri() {
		return architectureUri;
	}

	public void setArchitectureUri(String architectureUri) {
		this.architectureUri = architectureUri;
	}

	public String getDeploymentUri() {
		return deploymentUri;
	}

	public void setDeploymentUri(String deploymentUri) {
		this.deploymentUri = deploymentUri;
	}

}
