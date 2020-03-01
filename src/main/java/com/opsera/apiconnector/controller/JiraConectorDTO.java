package com.opsera.apiconnector.controller;

public class JiraConectorDTO {
	private String jiraUrl;
	private Integer jiraPort;
	private String jiraPassword;
	private String projectName;
	private String jiraType;
	private String jenkinUrl;
	private Integer jenkinPort;
	private String jenkinPassword;
	private String jiraUserName;
	private String jenkinUserName;

	public String getJiraUrl() {
		return jiraUrl;
	}

	public void setJiraUrl(String jiraUrl) {
		this.jiraUrl = jiraUrl;
	}

	public Integer getJiraPort() {
		return jiraPort;
	}

	public void setJiraPort(Integer jiraPort) {
		this.jiraPort = jiraPort;
	}

	public String getJiraPassword() {
		return jiraPassword;
	}

	public void setJiraPassword(String jiraPassword) {
		this.jiraPassword = jiraPassword;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getJiraType() {
		return jiraType;
	}

	public void setJiraType(String jiraType) {
		this.jiraType = jiraType;
	}

	public String getJenkinUrl() {
		return jenkinUrl;
	}

	public void setJenkinUrl(String jenkinUrl) {
		this.jenkinUrl = jenkinUrl;
	}

	public Integer getJenkinPort() {
		return jenkinPort;
	}

	public void setJenkinPort(Integer jenkinPort) {
		this.jenkinPort = jenkinPort;
	}

	public String getJenkinPassword() {
		return jenkinPassword;
	}

	public void setJenkinPassword(String jenkinPassword) {
		this.jenkinPassword = jenkinPassword;
	}

	public String getJiraUserName() {
		return jiraUserName;
	}

	public void setJiraUserName(String jiraUserName) {
		this.jiraUserName = jiraUserName;
	}

	public String getJenkinUserName() {
		return jenkinUserName;
	}

	public void setJenkinUserName(String jenkinUserName) {
		this.jenkinUserName = jenkinUserName;
	}

}
