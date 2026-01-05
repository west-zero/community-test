package com.community.bo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.community.bo", "com.community.core"})
@EntityScan(basePackages = "com.community.core.entity")
@EnableJpaRepositories(basePackages = "com.community.core.repository")
public class BoApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(BoApiApplication.class, args);
    }
}
