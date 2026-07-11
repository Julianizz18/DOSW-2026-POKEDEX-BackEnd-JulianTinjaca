package com.pokedex.pokedex_api.controller.mapper;

import com.pokedex.pokedex_api.controller.dto.request.PokemonRequest;
import com.pokedex.pokedex_api.controller.dto.request.PokemonStatsRequest;
import com.pokedex.pokedex_api.controller.dto.response.PokemonResponse;
import com.pokedex.pokedex_api.core.model.Pokemon;
import com.pokedex.pokedex_api.core.model.PokemonStats;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PokemonDtoMapperTest {

    private final PokemonDtoMapper mapper = new PokemonDtoMapperImpl();

    @Test
    void toResponse_mapsAllFieldsIncludingStatsTotal() {
        Pokemon pokemon = Pokemon.builder()
                .id(1L).nationalNumber(25).name("Pikachu").description("Raton electrico")
                .imageUrl("http://img").types(List.of("Electric")).region("Kanto")
                .generation(1).hasMega(false)
                .stats(PokemonStats.builder().hp(35).attack(55).defense(40)
                        .specialAttack(50).specialDefense(50).speed(90).build())
                .build();

        PokemonResponse response = mapper.toResponse(pokemon);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Pikachu");
        assertThat(response.types()).containsExactly("Electric");
        assertThat(response.stats().total()).isEqualTo(320);
    }

    @Test
    void toResponse_pokemonNull_returnsNull() {
        assertThat(mapper.toResponse((Pokemon) null)).isNull();
    }

    @Test
    void toResponse_statsNull_returnsNull() {
        assertThat(mapper.toResponse((PokemonStats) null)).isNull();
    }

    @Test
    void toDomain_fromRequest_ignoresIdAndDefaultsHasMegaFalse() {
        PokemonRequest request = new PokemonRequest(25, "Pikachu", "desc", "url",
                List.of("Electric"), "Kanto", 1, null,
                new PokemonStatsRequest(35, 55, 40, 50, 50, 90));

        Pokemon pokemon = mapper.toDomain(request);

        assertThat(pokemon.getId()).isNull();
        assertThat(pokemon.getHasMega()).isFalse();
        assertThat(pokemon.getStats().getHp()).isEqualTo(35);
    }

    @Test
    void toDomain_fromRequest_respectsHasMegaTrueAndNullStats() {
        PokemonRequest request = new PokemonRequest(6, "Charizard", null, null,
                List.of("Fire", "Flying"), "Kanto", 1, true, null);

        Pokemon pokemon = mapper.toDomain(request);

        assertThat(pokemon.getHasMega()).isTrue();
        assertThat(pokemon.getStats()).isNull();
    }

    @Test
    void toDomain_requestNull_returnsNull() {
        assertThat(mapper.toDomain((PokemonRequest) null)).isNull();
    }

    @Test
    void toDomain_statsRequestNull_returnsNull() {
        assertThat(mapper.toDomain((PokemonStatsRequest) null)).isNull();
    }
}
