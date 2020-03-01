package com.opsera.apiconnector.schedular;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.opsera.apiconnector.service.ApiConnectorService;
import com.opsera.models.ApiConnectorDetail;

@Component
public class ApiConnectorSchedular {

	@Autowired
	ApiConnectorService apiConnectorService;

	@Autowired
	RestTemplate restTemplate;

	@Value("${jiraTicketRabbitMq}")
	private String jiraTicketRabbitMq;

	@Scheduled(fixedRateString = "${scheduled-time}")
	public void emailSchedular() {
		List<ApiConnectorDetail> apiConnectorDetails = apiConnectorService.findAll();
		if (!CollectionUtils.isEmpty(apiConnectorDetails)) {
			for (ApiConnectorDetail apiConnectorDetail : apiConnectorDetails) {
				restTemplate.postForObject(jiraTicketRabbitMq, apiConnectorDetail, Object.class);
			}
		}
	}

}
