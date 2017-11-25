package org.reluxa.serviceregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableEurekaServer
public class ServiceRegistry {

    public static void main(String[] args) throws Exception {
      SpringApplication.run(ServiceRegistry.class, args);
    }

}
