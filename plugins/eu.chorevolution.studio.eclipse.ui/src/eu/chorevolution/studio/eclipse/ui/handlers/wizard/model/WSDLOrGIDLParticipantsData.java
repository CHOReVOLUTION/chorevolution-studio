package eu.chorevolution.studio.eclipse.ui.handlers.wizard.model;

public class WSDLOrGIDLParticipantsData {

	private String type;
	private String name;
	private String id;
	private String taskName;
	private String taskID;
	private String participantName;
	private String providerParticipantName;

	public WSDLOrGIDLParticipantsData(String type, String name, String id, String taskName, String taskID, String participantName, String providerParticipantName) {
		this.type=type;
		this.name=name;
		this.id=id;
		this.taskName = taskName;
		this.taskID = taskID;
		this.participantName=participantName;	
		this.providerParticipantName=providerParticipantName;
	}

	public String getTaskID() {
		return taskID;
	}

	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}

	public String getTaskName() {
		return taskName;
	}


	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getParticipantName() {
		return participantName;
	}


	public void setParticipantName(String participantName) {
		this.participantName = participantName;
	}
	
	
	public String getProviderParticipantName() {
		return providerParticipantName;
	}


	public void setProviderParticipantName(String providerParticipantName) {
		this.providerParticipantName = providerParticipantName;
	}
	
}
