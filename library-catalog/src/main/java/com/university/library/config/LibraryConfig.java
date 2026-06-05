package com.university.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class LibraryConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
