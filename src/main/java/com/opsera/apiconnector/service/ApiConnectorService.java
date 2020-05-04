package com.opsera.apiconnector.service;

import static com.opsera.apiconnector.resources.Constants.JENKINS_JIRA_NOT_RUNNING;
import static com.opsera.apiconnector.resources.Constants.JOB_DESC;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.opsera.apiconnector.config.IServiceFactory;
import com.opsera.apiconnector.exception.ResourcesNotAvailable;
import com.opsera.apiconnector.repository.ApiConnectorDetails;
import com.opsera.apiconnector.repository.ApiConnectorDetailsRepository;
import com.opsera.apiconnector.repository.JobDetails;
import com.opsera.apiconnector.resources.ConnectorDetails;
import com.opsera.apiconnector.resources.JiraConectorDTO;

@Service
public class ApiConnectorService {

    private static Logger logger = LogManager.getLogger(ApiConnectorService.class);

    @Autowired
    private ApiConnectorDetailsRepository apiConnectorDetailsRepository;

    @Autowired
    private IServiceFactory serviceFactory;

    /**
     * To store the jira details to db
     * 
     * @param jiraConectorDTO
     * @throws ResourcesNotAvailable
     */
    @Transactional
    public void saveJiraDetails(JiraConectorDTO jiraConectorDTO) throws ResourcesNotAvailable {
        apiConnectorDetailsRepository.save(convertApiConnectorDetails(jiraConectorDTO));
    }

    /**
     * Raise the jira ticket when build failed
     * 
     * @param apiConnectorDetail
     * @throws ResourcesNotAvailable
     */
    public void raiseJiraTicket(ConnectorDetails connectorDetails) throws ResourcesNotAvailable {
        try {
            Map<String, String[]> buildFailureMap = getJenkinDetails(connectorDetails);
            if (!CollectionUtils.isEmpty(buildFailureMap)) {
                createIssueClient(connectorDetails, buildFailureMap);
            }
            apiConnectorDetailsRepository.save(convertToDtoObject(connectorDetails));
        } catch (URISyntaxException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * To get the connectors detils from db
     * 
     * @return
     */
    public List<ConnectorDetails> findAll() {
        List<ApiConnectorDetails> apiConnectorDetails = apiConnectorDetailsRepository.findAll();
        return convertToDto(apiConnectorDetails);
    }

    /**
     * Convert the apiconnector details object from JiraConectorDTO
     * 
     * @param jiraConectorDTO
     * @return
     * @throws ResourcesNotAvailable
     */
    private ApiConnectorDetails convertApiConnectorDetails(JiraConectorDTO jiraConectorDTO) throws ResourcesNotAvailable {
        String jenkinEncryptPassword = serviceFactory.getApiConnectorsUtils().encodeString(jiraConectorDTO.getJenkinPassword());
        String jiraEncryptPassword = serviceFactory.getApiConnectorsUtils().encodeString(jiraConectorDTO.getJiraPassword());
        ApiConnectorDetails apiConnectorDetails = new ApiConnectorDetails();
        apiConnectorDetails.setJenkinUrl(jiraConectorDTO.getJenkinUrl());
        apiConnectorDetails.setJenkinPort(jiraConectorDTO.getJenkinPort());
        apiConnectorDetails.setJenkinPassword(jenkinEncryptPassword);
        apiConnectorDetails.setJiraUrl(jiraConectorDTO.getJiraUrl());
        apiConnectorDetails.setJiraPort(jiraConectorDTO.getJiraPort());
        apiConnectorDetails.setJiraPassword(jiraEncryptPassword);
        apiConnectorDetails.setProjectName(jiraConectorDTO.getProjectName());
        apiConnectorDetails.setJiraType(jiraConectorDTO.getJiraType());
        apiConnectorDetails.setJenkinUserName(jiraConectorDTO.getJenkinUserName());
        apiConnectorDetails.setJiraUserName(jiraConectorDTO.getJiraUserName());
        getJenkinDetails(apiConnectorDetails);
        return apiConnectorDetails;
    }

    /**
     * Convert the apiconnector details object
     * 
     * @param apiConnectorDetailsList
     * @return
     */
    private List<ConnectorDetails> convertToDto(List<ApiConnectorDetails> apiConnectorDetailsList) {
        List<ConnectorDetails> connectorDetailList = new ArrayList<>();
        for (ApiConnectorDetails apiConnectorDetails : apiConnectorDetailsList) {
            ConnectorDetails apiConnectorDetail = new ConnectorDetails();
            apiConnectorDetail.setJenkinUrl(apiConnectorDetails.getJenkinUrl());
            apiConnectorDetail.setJenkinPort(apiConnectorDetails.getJenkinPort());
            apiConnectorDetail.setJenkinPassword(apiConnectorDetails.getJenkinPassword());
            apiConnectorDetail.setJiraUrl(apiConnectorDetails.getJiraUrl());
            apiConnectorDetail.setJiraPort(apiConnectorDetails.getJiraPort());
            apiConnectorDetail.setJiraPassword(apiConnectorDetails.getJiraPassword());
            apiConnectorDetail.setProjectName(apiConnectorDetails.getProjectName());
            apiConnectorDetail.setJiraType(apiConnectorDetails.getJiraType());
            apiConnectorDetail.setJenkinUserName(apiConnectorDetails.getJenkinUserName());
            apiConnectorDetail.setJiraUserName(apiConnectorDetails.getJiraUserName());
            apiConnectorDetail.setJobDetails(apiConnectorDetails.getJobDetails());
            apiConnectorDetail.setId(apiConnectorDetails.getId());
            connectorDetailList.add(apiConnectorDetail);
        }
        return connectorDetailList;
    }

    /**
     * Convert the apiconnector details object
     * 
     * @param apiConnectorDetailsList
     * @return
     */
    private ApiConnectorDetails convertToDtoObject(ConnectorDetails connectorDetails) {
        ApiConnectorDetails apiConnectorDetails = new ApiConnectorDetails();
        apiConnectorDetails.setId(connectorDetails.getId());
        apiConnectorDetails.setJenkinUrl(connectorDetails.getJenkinUrl());
        apiConnectorDetails.setJenkinPort(connectorDetails.getJenkinPort());
        apiConnectorDetails.setJenkinPassword(connectorDetails.getJenkinPassword());
        apiConnectorDetails.setJiraUrl(connectorDetails.getJiraUrl());
        apiConnectorDetails.setJiraPort(connectorDetails.getJiraPort());
        apiConnectorDetails.setJiraPassword(connectorDetails.getJiraPassword());
        apiConnectorDetails.setProjectName(connectorDetails.getProjectName());
        apiConnectorDetails.setJiraType(connectorDetails.getJiraType());
        apiConnectorDetails.setJenkinUserName(connectorDetails.getJenkinUserName());
        apiConnectorDetails.setJiraUserName(connectorDetails.getJiraUserName());
        apiConnectorDetails.setJobDetails(connectorDetails.getJobDetails());
        return apiConnectorDetails;
    }

    /**
     * To get the jenkins details to check the build failed
     * 
     * @param apiConnectorDetail
     * @return
     * @throws ResourcesNotAvailable
     */
    private Map<String, String[]> getJenkinDetails(ConnectorDetails connectorDetails) throws ResourcesNotAvailable {
        Map<String, String[]> buildFailureMap = new HashMap<>();
        try {
            String jenkinDecriptPassword = serviceFactory.getApiConnectorsUtils().decodeString(connectorDetails.getJenkinPassword());
            Map<String, Job> jobs = serviceFactory.getApiConnectorsUtils().getJenkinsServer(connectorDetails.getJenkinUrl(), connectorDetails.getJenkinUserName(), jenkinDecriptPassword).getJobs();
            List<JobDetails> jobDetails = connectorDetails.getJobDetails();
            for (JobDetails details : jobDetails) {
                for (Entry<String, Job> job : jobs.entrySet()) {
                    if (details.getJobName().equals(job.getKey())) {
                        Job jobObj = job.getValue();
                        JobWithDetails jobWithDetails = jobObj.details();
                        Build build = jobWithDetails.getLastBuild();
                        BuildWithDetails buildWithDetails = build.details();
                        Date lastBuildDate = new Date(buildWithDetails.getTimestamp());
                        Date lastBuildDbTime = new Date(details.getLastFailedBuildTime());
                        if (buildWithDetails.getResult().name().equals("FAILURE") && lastBuildDate.after(lastBuildDbTime)) {
                            String desc = String.format(JOB_DESC, jobObj.getName(), build.getNumber(), lastBuildDate);
                            buildFailureMap.put(jobObj.getName(), new String[] {
                                    desc, String.valueOf(buildWithDetails.getTimestamp())
                            });
                        }
                        break;
                    }
                }
            }
            return buildFailureMap;
        } catch (IOException | URISyntaxException e) {
            throw new ResourcesNotAvailable(JENKINS_JIRA_NOT_RUNNING + e.getMessage());
        }
    }

    /**
     * To get the jenkins details
     * 
     * @param apiConnectorDetails
     * @return
     * @throws ResourcesNotAvailable
     * @throws URISyntaxException
     * @throws IOException
     */
    private ApiConnectorDetails getJenkinDetails(ApiConnectorDetails apiConnectorDetails) throws ResourcesNotAvailable {
        try {
            String jenkinDecriptPassword = serviceFactory.getApiConnectorsUtils().decodeString(apiConnectorDetails.getJenkinPassword());
            JenkinsServer jenkins = serviceFactory.getApiConnectorsUtils().getJenkinsServer(apiConnectorDetails.getJenkinUrl(), apiConnectorDetails.getJenkinUserName(), jenkinDecriptPassword);
            Map<String, Job> jobs = jenkins.getJobs();
            List<JobDetails> details = new ArrayList<>();
            for (Entry<String, Job> job : jobs.entrySet()) {
                Job jobObj = job.getValue();
                JobWithDetails jobWithDetails = jobObj.details();
                Build build = jobWithDetails.getLastBuild();
                JobDetails jobDetails = new JobDetails();
                jobDetails.setJobName(jobObj.getName());
                if (null != build) {
                    BuildWithDetails buildWithDetails = build.details();
                    jobDetails.setLastFailedBuildTime(buildWithDetails.getTimestamp());

                } else {
                    jobDetails.setLastFailedBuildTime(System.currentTimeMillis());
                }
                details.add(jobDetails);
            }
            if (!details.isEmpty()) {
                apiConnectorDetails.setJobDetails(details);
            }
            return apiConnectorDetails;
        } catch (IOException | URISyntaxException e) {
            throw new ResourcesNotAvailable(JENKINS_JIRA_NOT_RUNNING + e.getMessage());
        }

    }

    /**
     * Create issue client for raise the ticket
     * 
     * @param apiConnectorDetail
     * @param buildFailureMap
     * @throws URISyntaxException
     * @throws ResourcesNotAvailable
     */
    private void createIssueClient(ConnectorDetails connectorDetails, Map<String, String[]> buildFailureMap) throws URISyntaxException, ResourcesNotAvailable {
        try {
            String jiraDecriptPassword = serviceFactory.getApiConnectorsUtils().decodeString(connectorDetails.getJiraPassword());
            JiraRestClient restClient = serviceFactory.getApiConnectorsUtils().getJiraRestClient(connectorDetails.getJiraUrl(), connectorDetails.getJiraUserName(), jiraDecriptPassword);
            String key = "";
            for (BasicProject project : restClient.getProjectClient().getAllProjects().claim()) {
                if (project.getName().equals(connectorDetails.getProjectName())) {
                    key = project.getKey();
                    break;
                }
            }
            if (!StringUtils.isEmpty(key)) {
                List<JobDetails> details = connectorDetails.getJobDetails();
                IssueRestClient issueClient = restClient.getIssueClient();
                for (JobDetails jobDetails : details) {
                    String[] desc = buildFailureMap.get(jobDetails.getJobName());
                    if (null != desc) {
                        jobDetails.setLastFailedBuildTime(Long.valueOf(desc[1]));
                        IssueInput newIssue = new IssueInputBuilder(key, (long) 10002, "Status of recent JenkinsBuild:Failure").setDescription(desc[0]).build();
                        issueClient.createIssue(newIssue).claim().getKey();
                    }
                }
            }
        } catch (URISyntaxException e) {
            throw new ResourcesNotAvailable(JENKINS_JIRA_NOT_RUNNING + e.getMessage());
        }
    }

}
