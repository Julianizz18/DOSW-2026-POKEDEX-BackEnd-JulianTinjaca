package com.pokedex.pokedex_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
// Se delimita explícitamente qué paquete contiene repos JPA y cuál Mongo,
// para que Spring Data no intente adivinar (eso causaba los warnings de
// "Could not safely identify store assignment").
@EnableJpaRepositories(basePackages = "com.pokedex.pokedex_api.persistence.repository.relational")
@EnableMongoRepositories(basePackages = "com.pokedex.pokedex_api.persistence.repository.document")
public class PokedexApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PokedexApiApplication.class, args);
	}

}
