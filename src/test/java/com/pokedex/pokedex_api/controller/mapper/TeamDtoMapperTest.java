package com.pokedex.pokedex_api.controller.mapper;

import com.pokedex.pokedex_api.controller.dto.request.TeamRequest;
import com.pokedex.pokedex_api.controller.dto.response.TeamResponse;
import com.pokedex.pokedex_api.core.model.Team;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TeamDtoMapperTest {

    private final TeamDtoMapper mapper = new TeamDtoMapperImpl();

    @Test
    void toResponse_mapsFields() {
        Team team = Team.builder().id(1L).userId(2L).name("Equipo Kanto").pokemonIds(List.of(1L, 4L)).build();

        TeamResponse response = mapper.toResponse(team);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.userId()).isEqualTo(2L);
        assertThat(response.pokemonIds()).containsExactly(1L, 4L);
    }

    @Test
    void toResponse_null_returnsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }

    @Test
    void toDomain_ignoresIdAndUserId() {
        TeamRequest request = new TeamRequest("Equipo Kanto", List.of(1L, 2L));

        Team team = mapper.toDomain(request);

        assertThat(team.getId()).isNull();
        assertThat(team.getUserId()).isNull();
        assertThat(team.getName()).isEqualTo("Equipo Kanto");
        assertThat(team.getPokemonIds()).containsExactly(1L, 2L);
    }

    @Test
    void toDomain_null_returnsNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }
}
