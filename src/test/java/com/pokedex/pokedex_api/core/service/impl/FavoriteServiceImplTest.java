package com.pokedex.pokedex_api.core.service.impl;

import com.pokedex.pokedex_api.core.exception.DuplicateResourceException;
import com.pokedex.pokedex_api.core.model.Favorite;
import com.pokedex.pokedex_api.core.port.FavoritePersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceImplTest {

    @Mock
    private FavoritePersistencePort favoritePort;

    @InjectMocks
    private FavoriteServiceImpl service;

    @Test
    void findByUserId_delegatesToPort() {
        Favorite favorite = Favorite.builder().id(1L).userId(2L).pokemonId(25L).build();
        when(favoritePort.findByUserId(2L)).thenReturn(List.of(favorite));

        assertThat(service.findByUserId(2L)).containsExactly(favorite);
    }

    @Test
    void add_whenNotDuplicate_saves() {
        when(favoritePort.existsByUserIdAndPokemonId(2L, 25L)).thenReturn(false);
        when(favoritePort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Favorite result = service.add(2L, 25L);

        assertThat(result.getUserId()).isEqualTo(2L);
        assertThat(result.getPokemonId()).isEqualTo(25L);
        assertThat(result.getAddedAt()).isNotNull();
    }

    @Test
    void add_whenDuplicate_throws() {
        when(favoritePort.existsByUserIdAndPokemonId(2L, 25L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.add(2L, 25L));
        verify(favoritePort, never()).save(any());
    }

    @Test
    void remove_delegatesToPort() {
        service.remove(2L, 25L);

        verify(favoritePort).deleteByUserIdAndPokemonId(2L, 25L);
    }
}
