package com.opsera.apiconnector.service;

import java.util.List;

import com.opsera.apiconnector.controller.JiraConectorDTO;
import com.opsera.models.ApiConnectorDetail;

public interface ApiConnectorService {

	void saveJiraDetails(JiraConectorDTO jiraConectorDTO);

	List<ApiConnectorDetail> findAll();

	void raiseJiraTicket(ApiConnectorDetail apiConnectorDetail);

}
