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

import java.util.List;


public class ValidElementsForParticipant {
	private String participant;
	private List<String> validTasksNames;
	private List<String> validMessagesNames;
	private List<String> validMessagesTypes;

	public ValidElementsForParticipant(String participant, List<String> validTaskNames, List<String> validMessagesNames, List<String> validMessagesTypes) {
		this.participant = participant;
		this.validTasksNames = validTaskNames;
		this.validMessagesNames = validMessagesNames;
		this.validMessagesTypes = validMessagesTypes;
	}

	public String getParticipant() {
		return participant;
	}

	public void setParticipant(String participant) {
		this.participant = participant;
	}

	public List<String> getValidTasksNames() {
		return validTasksNames;
	}

	public void setValidTasksNames(List<String> validTasksNames) {
		this.validTasksNames = validTasksNames;
	}

	public List<String> getValidMessagesNames() {
		return validMessagesNames;
	}

	public void setValidMessagesNames(List<String> validMessagesNames) {
		this.validMessagesNames = validMessagesNames;
	}

	public List<String> getValidMessagesTypes() {
		return validMessagesTypes;
	}

	public void setValidMessagesTypes(List<String> validMessagesTypes) {
		this.validMessagesTypes = validMessagesTypes;
	}


}
