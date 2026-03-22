package com.example.flowforge.travel;

import org.royada.flowforge.spring.autoconfig.FlowForgeAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ComponentScan(basePackages = "com.example.flowforge.travel")
@Import(FlowForgeAutoConfiguration.class)
public class AppConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}
