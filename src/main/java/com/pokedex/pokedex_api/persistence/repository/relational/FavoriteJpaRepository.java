package com.pokedex.pokedex_api.persistence.repository.relational;

import com.pokedex.pokedex_api.persistence.entity.relational.FavoriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteJpaRepository extends JpaRepository<FavoriteEntity, Long> {
    List<FavoriteEntity> findByUserId(Long userId);
    boolean existsByUserIdAndPokemonId(Long userId, Long pokemonId);
    void deleteByUserIdAndPokemonId(Long userId, Long pokemonId);
}
