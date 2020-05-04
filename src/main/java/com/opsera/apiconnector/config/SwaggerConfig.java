/**
 * 
 */
package com.opsera.apiconnector.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Purusothaman
 *
 */

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * Method for swagger API documentation.
     * 
     * 
     * @return
     */
    @Bean
    public Docket postsApi() {
        return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.basePackage("com.opsera.apiconnector.controller")).paths(PathSelectors.any()).build().apiInfo(apiInfo());
    }

    /**
     * API information
     * 
     * Update this method for title, description, terms of service.
     * 
     * 
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Opsera ApiConnector API").description("Opsera API for apiconnector to jira,githun and gitLab").termsOfServiceUrl("https://opsera.io/legal.html")
                .version("1.0").build();
    }
}
