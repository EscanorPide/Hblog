package com.hehaoran.hblog.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.hehaoran.hblog")
@EnableScheduling
public class HblogWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(HblogWebApplication.class, args);
    }

}