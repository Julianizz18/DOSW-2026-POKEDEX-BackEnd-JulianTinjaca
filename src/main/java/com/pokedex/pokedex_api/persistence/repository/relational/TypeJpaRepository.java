package com.pokedex.pokedex_api.persistence.repository.relational;

import com.pokedex.pokedex_api.persistence.entity.relational.TypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeJpaRepository extends JpaRepository<TypeEntity, Long> {
    Optional<TypeEntity> findByName(String name);
}
