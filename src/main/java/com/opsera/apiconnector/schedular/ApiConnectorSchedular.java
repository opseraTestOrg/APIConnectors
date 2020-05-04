package com.opsera.apiconnector.schedular;

import static com.opsera.apiconnector.resources.Constants.RAISE_JIRA_TICKET_PATH;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.opsera.apiconnector.config.AppConfig;
import com.opsera.apiconnector.config.IServiceFactory;
import com.opsera.apiconnector.resources.ConnectorDetails;

@Component
public class ApiConnectorSchedular {

    private IServiceFactory serviceFactory;

    private AppConfig appConfig;

    @Autowired
    public ApiConnectorSchedular(IServiceFactory serviceFactory, AppConfig appConfig) {
        super();
        this.serviceFactory = serviceFactory;
        this.appConfig = appConfig;
    }

    /**
     * email scheduler 
     * 
     * 
     */
    @Scheduled(fixedRateString = "${scheduled.time}")
    public void emailSchedular() {
        List<ConnectorDetails> connectorDetails = serviceFactory.getApiConnectorService().findAll();
        if (!CollectionUtils.isEmpty(connectorDetails)) {
            for (ConnectorDetails apiConnectorDetail : connectorDetails) {
                serviceFactory.getRestTemplate().postForObject(appConfig.getRabbitmqBaseUrl() + RAISE_JIRA_TICKET_PATH, apiConnectorDetail, Object.class);
            }
        }
    }

}
