package org.reluxa.helloboot;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
@EnableDiscoveryClient
public class App {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
      return "Hello Boot";
    }

    public static void main(String[] args) throws Exception {
      SpringApplication.run(App.class, args);
    }
}
