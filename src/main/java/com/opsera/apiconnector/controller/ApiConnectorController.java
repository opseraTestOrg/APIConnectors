package com.opsera.apiconnector.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.opsera.apiconnector.service.ApiConnectorService;
import com.opsera.models.ApiConnectorDetail;

@RestController
public class ApiConnectorController {

	@Autowired
	ApiConnectorService apiConnectorService;

	@GetMapping("/status")
	public ResponseEntity<String> status() {
		return new ResponseEntity<>("Api Connector Service running....", HttpStatus.OK);
	}

	@PostMapping(value = "/saveJiraDetails")
	public void saveJiraDetails(@RequestBody JiraConectorDTO jiraConectorDTO) {
		apiConnectorService.saveJiraDetails(jiraConectorDTO);
	}

	@PostMapping(value = "/raiseJiraTicket")
	public Object raiseJiraTicket(@RequestBody ApiConnectorDetail apiConnectorDetail) {
		apiConnectorService.raiseJiraTicket(apiConnectorDetail);
		return apiConnectorDetail;
	}
}
