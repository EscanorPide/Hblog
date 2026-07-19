package com.hehaoran.hblog.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.hehaoran.hblog")
public class HblogWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(HblogWebApplication.class, args);
    }

}