package com.opsera.apiconnector.repository;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.opsera.apiconnector.resources.ConnectorDetails;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "apiconnectordetails")
@Getter
@Setter
public class ApiConnectorDetails extends ConnectorDetails {

    @Id
    private String id;

}
