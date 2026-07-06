package com.pokedex.pokedex_api.persistence.repository.relational;

import com.pokedex.pokedex_api.persistence.entity.relational.PokemonStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PokemonStatsJpaRepository extends JpaRepository<PokemonStatsEntity, Long> {

    Optional<PokemonStatsEntity> findByPokemonId(Long pokemonId);
}
