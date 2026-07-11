package com.pokedex.pokedex_api.persistence.mapper;

import com.pokedex.pokedex_api.core.model.Team;
import com.pokedex.pokedex_api.persistence.entity.relational.TeamEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TeamPersistenceMapperTest {

    private final TeamPersistenceMapper mapper = new TeamPersistenceMapperImpl();

    @Test
    void toDomain_mapsFields() {
        TeamEntity entity = TeamEntity.builder().id(1L).userId(2L).name("Equipo Kanto")
                .pokemonIds(List.of(1L, 2L)).build();

        Team team = mapper.toDomain(entity);

        assertThat(team.getId()).isEqualTo(1L);
        assertThat(team.getPokemonIds()).containsExactly(1L, 2L);
    }

    @Test
    void toDomain_null_returnsNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    void toEntity_ignoresId() {
        Team team = Team.builder().id(5L).userId(2L).name("Equipo Kanto").pokemonIds(List.of(1L)).build();

        TeamEntity entity = mapper.toEntity(team);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getUserId()).isEqualTo(2L);
        assertThat(entity.getName()).isEqualTo("Equipo Kanto");
    }

    @Test
    void toEntity_null_returnsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }
}
