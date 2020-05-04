/**
 * 
 */
package com.opsera.apiconnector.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

import org.springframework.stereotype.Component;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.offbytwo.jenkins.JenkinsServer;

/**
 * @author Purusothaman
 *
 */
@Component
public class ApiConnectorsUtils {

    /**
     * Construct Jenkins URL.
     * 
     * @param baseUrl
     * @param jobName
     * @param token
     * @return
     */
    public String constructJenkinsURL(String baseUrl, String jobName, String token) {
        return String.format("%s/job/%s/build?token=%s", baseUrl, jobName, token);
    }

    /**
     * Create Jenkins Server Object with the parameters
     * 
     * @param url
     * @param userName
     * @param token
     * @return
     * @throws URISyntaxException
     */
    public JenkinsServer getJenkinsServer(String url, String userName, String token) throws URISyntaxException {
        return new JenkinsServer(new URI(url), userName, token);
    }

    /**
     * Create Jira rest client
     * 
     * @param url
     * @param userName
     * @param password
     * @return
     * @throws URISyntaxException
     */
    public JiraRestClient getJiraRestClient(String url, String userName, String password) throws URISyntaxException {
        return new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(new URI(url), userName, password);
    }

    /**
     * Encode the plaintext using base64
     * 
     * @param plainText
     * @return
     */
    public String encodeString(String plainText) {
        return Base64.getEncoder().encodeToString(plainText.getBytes());
    }

    /**
     * Decode the encodedstring using base64
     * 
     * @param encodeString
     * @return
     */
    public String decodeString(String encodeString) {
        byte[] actualByte = Base64.getDecoder().decode(encodeString);
        return new String(actualByte);
    }

}
