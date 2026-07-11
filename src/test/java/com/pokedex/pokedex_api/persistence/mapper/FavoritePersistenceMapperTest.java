package com.pokedex.pokedex_api.persistence.mapper;

import com.pokedex.pokedex_api.core.model.Favorite;
import com.pokedex.pokedex_api.persistence.entity.relational.FavoriteEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FavoritePersistenceMapperTest {

    private final FavoritePersistenceMapper mapper = new FavoritePersistenceMapperImpl();

    @Test
    void toDomain_mapsFields() {
        LocalDateTime now = LocalDateTime.now();
        FavoriteEntity entity = FavoriteEntity.builder().id(1L).userId(2L).pokemonId(25L).addedAt(now).build();

        Favorite favorite = mapper.toDomain(entity);

        assertThat(favorite.getId()).isEqualTo(1L);
        assertThat(favorite.getAddedAt()).isEqualTo(now);
    }

    @Test
    void toDomain_null_returnsNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    void toEntity_ignoresId() {
        Favorite favorite = Favorite.builder().id(9L).userId(2L).pokemonId(25L)
                .addedAt(LocalDateTime.now()).build();

        FavoriteEntity entity = mapper.toEntity(favorite);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getPokemonId()).isEqualTo(25L);
    }

    @Test
    void toEntity_null_returnsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }
}
