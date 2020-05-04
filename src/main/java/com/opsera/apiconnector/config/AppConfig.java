/**
 * 
 */
package com.opsera.apiconnector.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;

/**
 * @author Purusothaman
 *
 */

@Configuration
@Getter
public class AppConfig {

    @Value("${rabbitmq.baseurl}")
    private String rabbitmqBaseUrl;

    /**
     * Service factory bean creation. Bean factory will be created with the
     * interface, spring will take care of maintaining the bean lifecycle.
     * 
     * 
     * @return
     */
    @Bean
    public ServiceLocatorFactoryBean serviceLocatorFactoryBean() {
        ServiceLocatorFactoryBean factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(IServiceFactory.class);
        return factoryBean;
    }

    /**
     * Creates a prototype bean for stop watch bean
     * 
     * 
     * @return
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public StopWatch stopWatch() {
        return new StopWatch();
    }

    /**
     * create rest template bean
     * 
     * 
     * @return
     */
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
