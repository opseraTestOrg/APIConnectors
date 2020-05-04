package com.opsera.apiconnector.config;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.opsera.apiconnector.service.ApiConnectorService;
import com.opsera.apiconnector.utils.ApiConnectorsUtils;

@Component
public interface IServiceFactory {

    public RestTemplate getRestTemplate();

    public Gson gson();

    ApiConnectorsUtils getApiConnectorsUtils();

    ApiConnectorService getApiConnectorService();

}
