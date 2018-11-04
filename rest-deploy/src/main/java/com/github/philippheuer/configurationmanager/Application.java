package com.github.philippheuer.configurationmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {

    /**
     * App Entrypoints
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
