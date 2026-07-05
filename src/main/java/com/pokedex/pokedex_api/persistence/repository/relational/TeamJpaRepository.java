package com.pokedex.pokedex_api.persistence.repository.relational;

import com.pokedex.pokedex_api.persistence.entity.relational.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamJpaRepository extends JpaRepository<TeamEntity, Long> {
    List<TeamEntity> findByUserId(Long userId);
}
