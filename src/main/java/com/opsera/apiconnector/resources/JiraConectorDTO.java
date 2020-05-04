package com.opsera.apiconnector.resources;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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

}
