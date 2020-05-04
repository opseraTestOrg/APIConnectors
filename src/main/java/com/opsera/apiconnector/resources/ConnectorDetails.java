package com.opsera.apiconnector.resources;

import java.util.List;

import com.opsera.apiconnector.repository.JobDetails;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConnectorDetails {
    private String id;
    private String jiraUrl;
    private Integer jiraPort;
    private String jiraPassword;
    private String projectName;
    private String jiraType;
    private String jenkinUrl;
    private Integer jenkinPort;
    private String jenkinPassword;
    private String jenkinUserName;
    private String jiraUserName;
    private List<JobDetails> jobDetails;

}
