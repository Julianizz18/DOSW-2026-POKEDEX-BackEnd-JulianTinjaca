package com.pokedex.pokedex_api.persistence.repository.relational;

import com.pokedex.pokedex_api.persistence.entity.relational.PokemonEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PokemonJpaRepository extends JpaRepository<PokemonEntity, Long> {

    @EntityGraph(attributePaths = {"types", "stats", "region"})
    Optional<PokemonEntity> findWithDetailsById(Long id);

    @EntityGraph(attributePaths = {"types", "stats", "region"})
    Optional<PokemonEntity> findWithDetailsByNationalNumber(Integer nationalNumber);

    @EntityGraph(attributePaths = {"types", "region"})
    @Query("SELECT p FROM PokemonEntity p")
    Page<PokemonEntity> findAllWithTypes(Pageable pageable);

    @EntityGraph(attributePaths = {"types", "region"})
    @Query("SELECT p FROM PokemonEntity p WHERE p.region.name = :regionName")
    List<PokemonEntity> findByRegionName(@Param("regionName") String regionName);

    @EntityGraph(attributePaths = {"types", "region"})
    @Query("SELECT p FROM PokemonEntity p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) "
            + "OR CAST(p.nationalNumber AS string) = :query")
    List<PokemonEntity> searchByNameOrNumber(@Param("query") String query);

    boolean existsByNationalNumber(Integer nationalNumber);
}
