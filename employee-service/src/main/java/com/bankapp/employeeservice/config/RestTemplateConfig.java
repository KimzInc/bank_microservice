package com.bankapp.employeeservice.config;

import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @LoadBalanced is what makes the difference here: a plain RestTemplate needs
 * a real host:port. This one lets AuthServiceClient call "http://auth-service/..."
 * using auth-service's logical Eureka name - Spring Cloud LoadBalancer resolves
 * that to an actual instance address at request time, the same way the API
 * Gateway resolves lb://auth-service in its routes. This is the direct
 * service-to-service equivalent of that gateway mechanism.
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    @LoadBalanced
    public RestTemplate loadBalancedRestTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
