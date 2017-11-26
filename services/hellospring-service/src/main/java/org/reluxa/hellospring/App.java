package org.reluxa.hellospring;

import org.reluxa.ExampleUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableDiscoveryClient
public class App {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
      return "Hello Spring";
    }

    public static void main(String[] args) throws Exception {
      SpringApplication.run(App.class, args);
      ExampleUtil.add(1,2);
    }
}
