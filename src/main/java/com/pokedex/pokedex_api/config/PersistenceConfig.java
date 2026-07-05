package com.pokedex.pokedex_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.pokedex.pokedex_api.persistence.repository.relational")
@EnableMongoRepositories(basePackages = "com.pokedex.pokedex_api.persistence.repository.document")
public class PersistenceConfig {
}