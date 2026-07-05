package com.pokedex.pokedex_api.persistence.repository.relational;

import com.pokedex.pokedex_api.persistence.entity.relational.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
