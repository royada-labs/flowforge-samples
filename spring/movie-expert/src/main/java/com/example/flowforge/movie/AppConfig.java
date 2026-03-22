package com.example.flowforge.movie;

import org.royada.flowforge.spring.autoconfig.FlowForgeAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "com.example.flowforge.movie")
@Import(FlowForgeAutoConfiguration.class)
public class AppConfig {
    // Basic Spring configuration
}
