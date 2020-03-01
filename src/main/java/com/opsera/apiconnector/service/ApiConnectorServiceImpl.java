package com.opsera.apiconnector.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.opsera.apiconnector.controller.JiraConectorDTO;
import com.opsera.apiconnector.repository.ApiConnectorDetails;
import com.opsera.apiconnector.repository.ApiConnectorDetailsRepository;
import com.opsera.models.ApiConnectorDetail;
import com.opsera.models.JobDetails;

@Service
public class ApiConnectorServiceImpl implements ApiConnectorService {

	private static Logger logger = LogManager.getLogger(ApiConnectorServiceImpl.class);

	@Autowired
	ApiConnectorDetailsRepository apiConnectorDetailsRepository;

	@Autowired
	ModelMapper modelMapper;

	@Override
	@Transactional
	public void saveJiraDetails(JiraConectorDTO jiraConectorDTO) {
		apiConnectorDetailsRepository.save(convertApiConnectorDetails(jiraConectorDTO));
	}

	@Override
	public List<ApiConnectorDetail> findAll() {
		List<ApiConnectorDetails> apiConnectorDetails = apiConnectorDetailsRepository.findAll();
		return convertToDto(apiConnectorDetails);
	}

	@Override
	public void raiseJiraTicket(ApiConnectorDetail apiConnectorDetail) {
		try {
			Map<String, String[]> buildFailureMap = getJenkinDetails(apiConnectorDetail);
			if (!buildFailureMap.isEmpty()) {
				byte[] jiraDecriptPassword = Base64.decodeBase64(apiConnectorDetail.getJiraPassword().getBytes());
				URI jiraServerUri = new URI(apiConnectorDetail.getJiraUrl());
				JiraRestClient restClient = new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(
						jiraServerUri, apiConnectorDetail.getJiraUserName(), new String(jiraDecriptPassword));
				String key = "";
				for (BasicProject project : restClient.getProjectClient().getAllProjects().claim()) {
					if (project.getName().equals(apiConnectorDetail.getProjectName())) {

						key = project.getKey();
						break;
					}
				}

				if (null != key && !key.isEmpty()) {
					List<JobDetails> details = apiConnectorDetail.getJobDetails();
					IssueRestClient issueClient = restClient.getIssueClient();
					for (JobDetails jobDetails : details) {
						String[] desc = buildFailureMap.get(jobDetails.getJobName());
						if (null != desc) {
							jobDetails.setLastFailedBuildTime(Long.valueOf(desc[1]));
							IssueInput newIssue = new IssueInputBuilder(key, (long) 10002,
									"Status of recent JenkinsBuild:Failure").setDescription(desc[0]).build();
							issueClient.createIssue(newIssue).claim().getKey();
						}
					}
				}
			}
			apiConnectorDetailsRepository.save(convertToDtoObject(apiConnectorDetail));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	private ApiConnectorDetails convertApiConnectorDetails(JiraConectorDTO jiraConectorDTO) {
		ApiConnectorDetails apiConnectorDetails = new ApiConnectorDetails();
		apiConnectorDetails.setJenkinUrl(jiraConectorDTO.getJenkinUrl());
		apiConnectorDetails.setJenkinPort(jiraConectorDTO.getJenkinPort());
		byte[] jenkinEncryptPassword = Base64.encodeBase64(jiraConectorDTO.getJenkinPassword().getBytes());
		apiConnectorDetails.setJenkinPassword(new String(jenkinEncryptPassword));
		apiConnectorDetails.setJiraUrl(jiraConectorDTO.getJiraUrl());
		apiConnectorDetails.setJiraPort(jiraConectorDTO.getJiraPort());
		byte[] jiraEncryptPassword = Base64.encodeBase64(jiraConectorDTO.getJiraPassword().getBytes());
		apiConnectorDetails.setJiraPassword(new String(jiraEncryptPassword));
		apiConnectorDetails.setProjectName(jiraConectorDTO.getProjectName());
		apiConnectorDetails.setJiraType(jiraConectorDTO.getJiraType());
		apiConnectorDetails.setJenkinUserName(jiraConectorDTO.getJenkinUserName());
		apiConnectorDetails.setJiraUserName(jiraConectorDTO.getJiraUserName());
		apiConnectorDetails = getJenkinDetails(apiConnectorDetails);
		return apiConnectorDetails;
	}

	private List<ApiConnectorDetail> convertToDto(List<ApiConnectorDetails> apiConnectorDetailsList) {
		List<ApiConnectorDetail> connectorDetailList = new ArrayList<>();
		for (ApiConnectorDetails apiConnectorDetails : apiConnectorDetailsList) {
			ApiConnectorDetail apiConnectorDetail = new ApiConnectorDetail();
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

	private ApiConnectorDetails convertToDtoObject(ApiConnectorDetail apiConnectorDetail) {
		ApiConnectorDetails apiConnectorDetails = new ApiConnectorDetails();
		apiConnectorDetails.setId(apiConnectorDetail.getId());
		apiConnectorDetails.setJenkinUrl(apiConnectorDetail.getJenkinUrl());
		apiConnectorDetails.setJenkinPort(apiConnectorDetail.getJenkinPort());
		apiConnectorDetails.setJenkinPassword(apiConnectorDetail.getJenkinPassword());
		apiConnectorDetails.setJiraUrl(apiConnectorDetail.getJiraUrl());
		apiConnectorDetails.setJiraPort(apiConnectorDetail.getJiraPort());
		apiConnectorDetails.setJiraPassword(apiConnectorDetail.getJiraPassword());
		apiConnectorDetails.setProjectName(apiConnectorDetail.getProjectName());
		apiConnectorDetails.setJiraType(apiConnectorDetail.getJiraType());
		apiConnectorDetails.setJenkinUserName(apiConnectorDetail.getJenkinUserName());
		apiConnectorDetails.setJiraUserName(apiConnectorDetail.getJiraUserName());
		apiConnectorDetails.setJobDetails(apiConnectorDetail.getJobDetails());
		return apiConnectorDetails;
	}

	private Map<String, String[]> getJenkinDetails(ApiConnectorDetail apiConnectorDetail) {
		Map<String, String[]> buildFailureMap = new HashMap<>();
		try {
			byte[] jenkinDecriptPassword = Base64.decodeBase64(apiConnectorDetail.getJenkinPassword().getBytes());
			final URI jenkinServerUri = new URI(apiConnectorDetail.getJenkinUrl());
			Map<String, Job> jobs = new JenkinsServer(jenkinServerUri, apiConnectorDetail.getJenkinUserName(),
					new String(jenkinDecriptPassword)).getJobs();
			List<JobDetails> jobDetails = apiConnectorDetail.getJobDetails();
			for (JobDetails details : jobDetails) {
				for (Entry<String, Job> job : jobs.entrySet()) {
					if (details.getJobName().equals(job.getKey())) {
						Job jobObj = job.getValue();
						JobWithDetails jobWithDetails = jobObj.details();
						Build build = jobWithDetails.getLastBuild();
						BuildWithDetails buildWithDetails = build.details();
						Date lastBuildDate = new Date(buildWithDetails.getTimestamp());
						Date lastBuildDbTime = new Date(details.getLastFailedBuildTime());
						if (buildWithDetails.getResult().name().equals("FAILURE")
								&& lastBuildDate.after(lastBuildDbTime)) {
							String desc = "Job Name:" + jobObj.getName() + "\n" + "Build Name:" + build.getNumber()
									+ "\n" + "Build Time:" + lastBuildDate + "\n" + "Build Status:Failure";
							buildFailureMap.put(jobObj.getName(),
									new String[] { desc, String.valueOf(buildWithDetails.getTimestamp()) });
						}
						break;
					}
				}
			}

			return buildFailureMap;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return buildFailureMap;
	}

	private ApiConnectorDetails getJenkinDetails(ApiConnectorDetails apiConnectorDetails) {
		try {
			byte[] jenkinDecriptPassword = Base64.decodeBase64(apiConnectorDetails.getJenkinPassword().getBytes());
			final URI jenkinServerUri = new URI(apiConnectorDetails.getJenkinUrl());
			JenkinsServer jenkins = new JenkinsServer(jenkinServerUri, apiConnectorDetails.getJenkinUserName(),
					new String(jenkinDecriptPassword));
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
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return apiConnectorDetails;
	}

}
