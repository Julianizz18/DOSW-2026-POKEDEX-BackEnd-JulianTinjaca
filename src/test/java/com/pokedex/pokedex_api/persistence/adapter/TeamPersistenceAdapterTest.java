package com.pokedex.pokedex_api.persistence.adapter;

import com.pokedex.pokedex_api.core.model.Team;
import com.pokedex.pokedex_api.persistence.entity.relational.TeamEntity;
import com.pokedex.pokedex_api.persistence.mapper.TeamPersistenceMapperImpl;
import com.pokedex.pokedex_api.persistence.repository.relational.TeamJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamPersistenceAdapterTest {

    @Mock
    private TeamJpaRepository teamRepository;

    private TeamPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new TeamPersistenceAdapter(teamRepository, new TeamPersistenceMapperImpl());
    }

    @Test
    void findById_whenExists_mapsToDomain() {
        TeamEntity entity = TeamEntity.builder().id(1L).userId(2L).name("Equipo").pokemonIds(List.of(1L)).build();
        when(teamRepository.findById(1L)).thenReturn(Optional.of(entity));

        assertThat(adapter.findById(1L)).isPresent();
    }

    @Test
    void findById_whenNotExists_returnsEmpty() {
        when(teamRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(adapter.findById(99L)).isEmpty();
    }

    @Test
    void findByUserId_mapsList() {
        TeamEntity entity = TeamEntity.builder().id(1L).userId(2L).name("Equipo").pokemonIds(List.of()).build();
        when(teamRepository.findByUserId(2L)).thenReturn(List.of(entity));

        assertThat(adapter.findByUserId(2L)).hasSize(1);
    }

    @Test
    void save_whenNew_doesNotForceId() {
        Team team = Team.builder().userId(2L).name("Nuevo").pokemonIds(List.of()).build();
        TeamEntity saved = TeamEntity.builder().id(5L).userId(2L).name("Nuevo").pokemonIds(List.of()).build();
        when(teamRepository.save(any())).thenReturn(saved);

        Team result = adapter.save(team);

        assertThat(result.getId()).isEqualTo(5L);
    }

    @Test
    void save_whenExisting_keepsId() {
        Team team = Team.builder().id(1L).userId(2L).name("Actualizado").pokemonIds(List.of(1L)).build();
        when(teamRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Team result = adapter.save(team);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Actualizado");
    }

    @Test
    void deleteById_delegates() {
        adapter.deleteById(1L);

        verify(teamRepository).deleteById(1L);
    }
}
