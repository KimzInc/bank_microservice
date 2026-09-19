package com.bankapp.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Serves application.yml files out of the /config-repo folder (native profile) to every
 * other microservice. In production this would point at a real Git repo instead.
 *
 * Start this SECOND, right after discovery-server.
 * Try it: http://localhost:8888/customer-service/default
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
