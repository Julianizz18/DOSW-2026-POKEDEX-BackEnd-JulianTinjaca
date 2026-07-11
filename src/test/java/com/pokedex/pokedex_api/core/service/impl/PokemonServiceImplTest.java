package com.pokedex.pokedex_api.core.service.impl;

import com.pokedex.pokedex_api.core.exception.DuplicateResourceException;
import com.pokedex.pokedex_api.core.exception.ResourceNotFoundException;
import com.pokedex.pokedex_api.core.model.Pokemon;
import com.pokedex.pokedex_api.core.model.PokemonFilterCriteria;
import com.pokedex.pokedex_api.core.model.PokemonStats;
import com.pokedex.pokedex_api.core.port.PokemonPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PokemonServiceImplTest {

    @Mock
    private PokemonPersistencePort pokemonPort;

    @InjectMocks
    private PokemonServiceImpl service;

    private Pokemon pikachu;

    @BeforeEach
    void setUp() {
        pikachu = Pokemon.builder()
                .id(1L).nationalNumber(25).name("Pikachu")
                .types(List.of("Electric")).region("Kanto")
                .generation(1).hasMega(false)
                .stats(PokemonStats.builder().hp(35).attack(55).defense(40)
                        .specialAttack(50).specialDefense(50).speed(90).build())
                .build();
    }

    @Test
    @DisplayName("findById: debe retornar el Pokemon cuando existe")
    void findById_whenExists_returnsPokemon() {
        when(pokemonPort.findById(1L)).thenReturn(Optional.of(pikachu));

        Pokemon result = service.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Pikachu");
        verify(pokemonPort).findById(1L);
    }

    @Test
    @DisplayName("findById: debe lanzar ResourceNotFoundException cuando no existe")
    void findById_whenNotFound_throws() {
        when(pokemonPort.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    @DisplayName("create: debe lanzar DuplicateResourceException si el número ya existe")
    void create_whenDuplicate_throws() {
        when(pokemonPort.existsByNationalNumber(25)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.create(pikachu));
        verify(pokemonPort, never()).save(any());
    }

    @Test
    @DisplayName("create: debe guardar cuando el número no existe todavía")
    void create_whenNotDuplicate_saves() {
        when(pokemonPort.existsByNationalNumber(25)).thenReturn(false);
        when(pokemonPort.save(any())).thenReturn(pikachu);

        Pokemon result = service.create(pikachu);

        assertThat(result.getName()).isEqualTo("Pikachu");
        verify(pokemonPort).save(pikachu);
    }

    @Test
    @DisplayName("delete: debe lanzar ResourceNotFoundException si el Pokemon no existe")
    void delete_whenNotFound_throws() {
        when(pokemonPort.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));
        verify(pokemonPort, never()).deleteById(any());
    }

    @Test
    @DisplayName("delete: debe eliminar cuando el Pokemon existe")
    void delete_whenExists_deletes() {
        when(pokemonPort.findById(1L)).thenReturn(Optional.of(pikachu));

        service.delete(1L);

        verify(pokemonPort).deleteById(1L);
    }

    @Test
    @DisplayName("findAll: debe delegar en el puerto")
    void findAll_delegatesToPort() {
        Page<Pokemon> page = new PageImpl<>(List.of(pikachu));
        when(pokemonPort.findAll(any(Pageable.class))).thenReturn(page);

        Page<Pokemon> result = service.findAll(Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(pikachu);
    }

    @Test
    @DisplayName("findByNationalNumber: debe retornar el Pokemon cuando existe")
    void findByNationalNumber_whenExists_returnsPokemon() {
        when(pokemonPort.findByNationalNumber(25)).thenReturn(Optional.of(pikachu));

        assertThat(service.findByNationalNumber(25)).isEqualTo(pikachu);
    }

    @Test
    @DisplayName("findByNationalNumber: debe lanzar ResourceNotFoundException cuando no existe")
    void findByNationalNumber_whenNotFound_throws() {
        when(pokemonPort.findByNationalNumber(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findByNationalNumber(999));
    }

    @Test
    @DisplayName("update: debe combinar los cambios sobre el Pokemon existente")
    void update_whenExists_mergesChanges() {
        when(pokemonPort.findById(1L)).thenReturn(Optional.of(pikachu));
        when(pokemonPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Pokemon changes = Pokemon.builder().name("Raichu").description("evolucionado")
                .types(List.of("Electric")).generation(1).hasMega(false).build();

        Pokemon result = service.update(1L, changes);

        assertThat(result.getName()).isEqualTo("Raichu");
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("filterByCriteria: cuando hay region, debe delegar en findByRegion")
    void filterByCriteria_withRegion_delegatesToFindByRegion() {
        when(pokemonPort.findByRegion("Kanto")).thenReturn(List.of(pikachu));
        PokemonFilterCriteria criteria = new PokemonFilterCriteria(null, "Kanto", null, null);

        assertThat(service.filterByCriteria(criteria)).containsExactly(pikachu);
    }

    @Test
    @DisplayName("filterByCriteria: sin region, debe retornar todo el catalogo")
    void filterByCriteria_withoutRegion_returnsAll() {
        Page<Pokemon> page = new PageImpl<>(List.of(pikachu));
        when(pokemonPort.findAll(any(Pageable.class))).thenReturn(page);
        PokemonFilterCriteria criteria = new PokemonFilterCriteria(null, null, null, null);

        assertThat(service.filterByCriteria(criteria)).containsExactly(pikachu);
    }

    @Test
    @DisplayName("search: debe delegar en el puerto")
    void search_delegatesToPort() {
        when(pokemonPort.searchByNameOrNumber("pika")).thenReturn(List.of(pikachu));

        assertThat(service.search("pika")).containsExactly(pikachu);
    }
}
