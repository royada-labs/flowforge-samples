package com.example.flowforge.market;

import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.royada.flowforge.spring.autoconfig.FlowForgeAutoConfiguration;

@Configuration
@ComponentScan("com.example.flowforge.market")
@EnableR2dbcRepositories("com.example.flowforge.market.repository")
@Import(FlowForgeAutoConfiguration.class)
public class AppConfig extends AbstractR2dbcConfiguration {

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        return new H2ConnectionFactory(
                H2ConnectionConfiguration.builder()
                        .inMemory("marketdb-plain")
                        .username("sa")
                        .password("")
                        .property("DB_CLOSE_DELAY", "-1") // CRITICAL for In-Memory DB persistence
                        .build()
        );
    }
}
