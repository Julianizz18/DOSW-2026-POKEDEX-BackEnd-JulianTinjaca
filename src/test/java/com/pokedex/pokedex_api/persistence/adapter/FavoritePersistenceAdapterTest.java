package com.pokedex.pokedex_api.persistence.adapter;

import com.pokedex.pokedex_api.core.model.Favorite;
import com.pokedex.pokedex_api.persistence.entity.relational.FavoriteEntity;
import com.pokedex.pokedex_api.persistence.mapper.FavoritePersistenceMapperImpl;
import com.pokedex.pokedex_api.persistence.repository.relational.FavoriteJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoritePersistenceAdapterTest {

    @Mock
    private FavoriteJpaRepository favoriteRepository;

    private FavoritePersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new FavoritePersistenceAdapter(favoriteRepository, new FavoritePersistenceMapperImpl());
    }

    @Test
    void findByUserId_mapsList() {
        FavoriteEntity entity = FavoriteEntity.builder().id(1L).userId(2L).pokemonId(25L)
                .addedAt(LocalDateTime.now()).build();
        when(favoriteRepository.findByUserId(2L)).thenReturn(List.of(entity));

        assertThat(adapter.findByUserId(2L)).hasSize(1);
    }

    @Test
    void existsByUserIdAndPokemonId_delegates() {
        when(favoriteRepository.existsByUserIdAndPokemonId(2L, 25L)).thenReturn(true);

        assertThat(adapter.existsByUserIdAndPokemonId(2L, 25L)).isTrue();
    }

    @Test
    void save_mapsRoundTrip() {
        Favorite favorite = Favorite.builder().userId(2L).pokemonId(25L).addedAt(LocalDateTime.now()).build();
        FavoriteEntity saved = FavoriteEntity.builder().id(1L).userId(2L).pokemonId(25L)
                .addedAt(LocalDateTime.now()).build();
        when(favoriteRepository.save(any())).thenReturn(saved);

        Favorite result = adapter.save(favorite);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void deleteByUserIdAndPokemonId_delegates() {
        adapter.deleteByUserIdAndPokemonId(2L, 25L);

        verify(favoriteRepository).deleteByUserIdAndPokemonId(2L, 25L);
    }
}
