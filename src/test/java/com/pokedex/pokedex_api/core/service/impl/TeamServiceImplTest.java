package com.pokedex.pokedex_api.core.service.impl;

import com.pokedex.pokedex_api.core.exception.ResourceNotFoundException;
import com.pokedex.pokedex_api.core.model.Pokemon;
import com.pokedex.pokedex_api.core.model.Team;
import com.pokedex.pokedex_api.core.port.TeamPersistencePort;
import com.pokedex.pokedex_api.core.service.interfaces.PokemonService;
import com.pokedex.pokedex_api.core.validator.TeamValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceImplTest {

    @Mock
    private TeamPersistencePort teamPort;
    @Mock
    private TeamValidator teamValidator;
    @Mock
    private PokemonService pokemonService;

    @InjectMocks
    private TeamServiceImpl service;

    private Team team;

    @BeforeEach
    void setUp() {
        team = Team.builder().id(1L).userId(10L).name("Equipo Kanto").pokemonIds(List.of(25L, 6L)).build();
    }

    @Test
    void findByUserId_delegatesToPort() {
        when(teamPort.findByUserId(10L)).thenReturn(List.of(team));

        assertThat(service.findByUserId(10L)).containsExactly(team);
    }

    @Test
    void findById_whenExists_returnsTeam() {
        when(teamPort.findById(1L)).thenReturn(Optional.of(team));

        assertThat(service.findById(1L)).isEqualTo(team);
    }

    @Test
    void findById_whenNotFound_throws() {
        when(teamPort.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void create_withNullPokemonIds_defaultsToEmptyList() {
        Team toCreate = Team.builder().userId(10L).name("Nuevo").pokemonIds(null).build();
        when(teamPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Team result = service.create(toCreate);

        assertThat(result.getPokemonIds()).isEmpty();
    }

    @Test
    void create_withPokemonIds_keepsThem() {
        when(teamPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Team result = service.create(team);

        assertThat(result.getPokemonIds()).containsExactly(25L, 6L);
    }

    @Test
    void addPokemon_validatesAndAppends() {
        when(teamPort.findById(1L)).thenReturn(Optional.of(team));
        when(teamPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Team result = service.addPokemon(1L, 9L);

        verify(teamValidator).validateCanAddPokemon(team);
        verify(teamValidator).validateNotDuplicate(team, 9L);
        assertThat(result.getPokemonIds()).contains(9L);
    }

    @Test
    void removePokemon_removesFromList() {
        when(teamPort.findById(1L)).thenReturn(Optional.of(team));
        when(teamPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Team result = service.removePokemon(1L, 25L);

        assertThat(result.getPokemonIds()).doesNotContain(25L);
    }

    @Test
    void delete_whenExists_deletes() {
        when(teamPort.findById(1L)).thenReturn(Optional.of(team));

        service.delete(1L);

        verify(teamPort).deleteById(1L);
    }

    @Test
    void delete_whenNotFound_throwsAndNeverDeletes() {
        when(teamPort.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));
        verify(teamPort, never()).deleteById(any());
    }

    @Test
    void analyzeTeam_whenEmpty_returnsMessage() {
        Team empty = Team.builder().id(2L).userId(10L).name("Vacio").pokemonIds(List.of()).build();
        when(teamPort.findById(2L)).thenReturn(Optional.of(empty));

        String result = service.analyzeTeam(2L);

        assertThat(result).isEqualTo("El equipo no tiene Pokémon todavía.");
    }

    @Test
    void analyzeTeam_aggregatesTypeCoverage() {
        when(teamPort.findById(1L)).thenReturn(Optional.of(team));
        when(pokemonService.findById(25L)).thenReturn(
                Pokemon.builder().id(25L).name("Pikachu").types(List.of("Electric")).build());
        when(pokemonService.findById(6L)).thenReturn(
                Pokemon.builder().id(6L).name("Charizard").types(List.of("Fire", "Flying")).build());

        String result = service.analyzeTeam(1L);

        assertThat(result).contains("Equipo Kanto").contains("2 Pokémon").contains("Electric").contains("Fire");
    }
}
