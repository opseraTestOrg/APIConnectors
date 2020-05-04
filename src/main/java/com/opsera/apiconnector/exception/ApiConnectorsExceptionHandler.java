package com.opsera.apiconnector.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.google.gson.Gson;
import com.opsera.apiconnector.config.IServiceFactory;

/**
 * 
 * @author Purusothaman
 *
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ApiConnectorsExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private IServiceFactory factory;

    /**
     * 
     * Custom exception handler for record not found
     * 
     * @param ex
     * @return
     */
    @ExceptionHandler(ResourcesNotAvailable.class)
    protected ResponseEntity<Object> handleRecordNotFound(ResourcesNotAvailable ex) {
        ApiConnectorErrorResponse apiConnectorErrorResponse = new ApiConnectorErrorResponse();
        apiConnectorErrorResponse.setMessage(ex.getMessage());
        apiConnectorErrorResponse.setStatus(HttpStatus.NOT_FOUND.name());
        return new ResponseEntity<>(apiConnectorErrorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * 
     * Handling Server side error while calling other service
     * 
     * @param ex
     * @return
     */
    @ExceptionHandler(HttpServerErrorException.class)
    protected ResponseEntity<Object> handleHttpServerErrorException(HttpServerErrorException ex) {
        String response = ex.getResponseBodyAsString();
        Gson gson = factory.gson();
        ApiConnectorErrorResponse apiConnectorErrorResponse = gson.fromJson(response, ApiConnectorErrorResponse.class);
        return new ResponseEntity<>(apiConnectorErrorResponse, ex.getStatusCode());
    }

    /**
     * 
     * Handling client side error while calling other service
     * 
     * @param ex
     * @return
     */
    @ExceptionHandler(HttpClientErrorException.class)
    protected ResponseEntity<Object> handleHttpClientErrorException(HttpClientErrorException ex) {
        String response = ex.getResponseBodyAsString();
        Gson gson = factory.gson();
        ApiConnectorErrorResponse apiConnectorErrorResponse = gson.fromJson(response, ApiConnectorErrorResponse.class);
        return new ResponseEntity<>(apiConnectorErrorResponse, ex.getStatusCode());
    }

}
