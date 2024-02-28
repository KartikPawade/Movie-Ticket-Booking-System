package com.movienow.org;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MovienowApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovienowApplication.class, args);
    }
}
