package com.opsera.apiconnector.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.opsera.apiconnector.config.IServiceFactory;
import com.opsera.apiconnector.exception.ResourcesNotAvailable;
import com.opsera.apiconnector.resources.ConnectorDetails;
import com.opsera.apiconnector.resources.JiraConectorDTO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api("API interating to api connector with jira")
public class ApiConnectorController {

    @Autowired
    private IServiceFactory serviceFactory;

    /**
     * To check the status of the Apiconnector service
     * 
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("To check the service running....")
    public ResponseEntity<String> status() {
        log.debug("Api Connector Service running....");
        return new ResponseEntity<>("Api Connector Service running....", HttpStatus.OK);
    }

    /**
     * TO store jira details in db
     * 
     * @param jiraConectorDTO
     * @throws ResourcesNotAvailable
     */
    @PostMapping(value = "/saveJiraDetails")
    @ApiOperation("Api for save the jira details in db")
    public void saveJiraDetails(@RequestBody JiraConectorDTO jiraConectorDTO) throws ResourcesNotAvailable {
        Long startTime = System.currentTimeMillis();
        log.info("Starting to save jira details and time execute by {} ", startTime);
        serviceFactory.getApiConnectorService().saveJiraDetails(jiraConectorDTO);
        log.info("Successfully to save jira details  and time taken by {} ", System.currentTimeMillis() - startTime);

    }

    /**
     * To raise the jira ticket when build failed
     * 
     * @param apiConnectorDetail
     * @return
     * @throws ResourcesNotAvailable
     */
    @ApiOperation("Api for raise the jira in build failed")
    @PostMapping(value = "/raiseJiraTicket")
    public Object raiseJiraTicket(@RequestBody ConnectorDetails apiConnectorDetail) throws ResourcesNotAvailable {
        Long startTime = System.currentTimeMillis();
        log.info("Starting to raise the jira ticket and time execute by {} ", startTime);
        serviceFactory.getApiConnectorService().raiseJiraTicket(apiConnectorDetail);
        log.info("Successfully raised the jira ticket and time taken by {} ", System.currentTimeMillis() - startTime);
        return apiConnectorDetail;
    }
}
