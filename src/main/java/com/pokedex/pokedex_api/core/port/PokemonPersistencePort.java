package com.pokedex.pokedex_api.core.port;

import com.pokedex.pokedex_api.core.model.Pokemon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de persistencia para Pokemon. El Core define QUÉ necesita;
 * la capa persistence (PokemonPersistenceAdapter, día miércoles/jueves)
 * define CÓMO se hace. Esto es Inversión de Dependencias: el Core no
 * depende de JPA, es JPA quien depende de este contrato.
 */
public interface PokemonPersistencePort {

    Optional<Pokemon> findById(Long id);

    Page<Pokemon> findAll(Pageable pageable);

    Optional<Pokemon> findByNationalNumber(Integer number);

    boolean existsByNationalNumber(Integer number);

    List<Pokemon> findByRegion(String regionName);

    List<Pokemon> searchByNameOrNumber(String query);

    Pokemon save(Pokemon pokemon);

    void deleteById(Long id);
}
