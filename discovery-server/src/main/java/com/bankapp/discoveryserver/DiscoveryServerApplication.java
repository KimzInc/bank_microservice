package com.bankapp.discoveryserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Every microservice (auth, customer, account, loan, ...) will register itself here on startup.
 * The API Gateway also looks things up here instead of using hardcoded URLs.
 *
 * Start this FIRST, before any other service.
 * Dashboard: http://localhost:8761
 */
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServerApplication.class, args);
    }
}
