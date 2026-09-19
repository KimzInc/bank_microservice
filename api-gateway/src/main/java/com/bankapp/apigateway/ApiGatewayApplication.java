package com.bankapp.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Single entry point for every client request. Routes are defined in application.yml
 * and resolve target services by name via Eureka (see lb://auth-service etc.).
 *
 * Start this THIRD, after discovery-server and config-server.
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
