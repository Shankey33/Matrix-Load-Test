package com.pm.loadtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@EnableAsync
@SpringBootApplication(scanBasePackages = "com.pm")
public class LoadTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoadTestApplication.class, args);
    }

}
