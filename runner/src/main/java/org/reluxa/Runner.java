package org.reluxa;

import org.springframework.boot.SpringApplication;

public class Runner {

    public static void main(String args[]) throws ClassNotFoundException {
        SpringApplication.run(Class.forName("org.reluxa.helloboot.App"));
        SpringApplication.run(Class.forName("org.reluxa.hellospring.App"));

    }


}
