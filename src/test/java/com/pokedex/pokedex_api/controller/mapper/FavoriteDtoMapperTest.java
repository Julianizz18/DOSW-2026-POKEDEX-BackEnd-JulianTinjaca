package com.pokedex.pokedex_api.controller.mapper;

import com.pokedex.pokedex_api.controller.dto.response.FavoriteResponse;
import com.pokedex.pokedex_api.core.model.Favorite;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FavoriteDtoMapperTest {

    private final FavoriteDtoMapper mapper = new FavoriteDtoMapperImpl();

    @Test
    void toResponse_mapsFields() {
        LocalDateTime now = LocalDateTime.now();
        Favorite favorite = Favorite.builder().id(1L).userId(2L).pokemonId(25L).addedAt(now).build();

        FavoriteResponse response = mapper.toResponse(favorite);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.userId()).isEqualTo(2L);
        assertThat(response.pokemonId()).isEqualTo(25L);
        assertThat(response.addedAt()).isEqualTo(now);
    }

    @Test
    void toResponse_null_returnsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }
}
