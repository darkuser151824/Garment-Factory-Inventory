package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableAsync
public class GarmentfactoryApplication {

	public static void main(String[] args) {

        ConfigurableApplicationContext run = SpringApplication.run(GarmentfactoryApplication.class, args);

    }
}
