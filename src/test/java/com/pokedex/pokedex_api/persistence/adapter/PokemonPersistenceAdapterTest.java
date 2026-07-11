package com.pokedex.pokedex_api.persistence.adapter;

import com.pokedex.pokedex_api.core.model.Pokemon;
import com.pokedex.pokedex_api.core.model.PokemonStats;
import com.pokedex.pokedex_api.persistence.entity.relational.PokemonEntity;
import com.pokedex.pokedex_api.persistence.entity.relational.RegionEntity;
import com.pokedex.pokedex_api.persistence.entity.relational.TypeEntity;
import com.pokedex.pokedex_api.persistence.mapper.PokemonPersistenceMapperImpl;
import com.pokedex.pokedex_api.persistence.repository.relational.PokemonJpaRepository;
import com.pokedex.pokedex_api.persistence.repository.relational.PokemonStatsJpaRepository;
import com.pokedex.pokedex_api.persistence.repository.relational.RegionJpaRepository;
import com.pokedex.pokedex_api.persistence.repository.relational.TypeJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PokemonPersistenceAdapterTest {

    @Mock
    private PokemonJpaRepository pokemonRepository;
    @Mock
    private PokemonStatsJpaRepository statsRepository;
    @Mock
    private TypeJpaRepository typeRepository;
    @Mock
    private RegionJpaRepository regionRepository;

    private PokemonPersistenceAdapter adapter;

    private PokemonEntity pikachuEntity;

    @BeforeEach
    void setUp() {
        adapter = new PokemonPersistenceAdapter(pokemonRepository, statsRepository, typeRepository,
                regionRepository, new PokemonPersistenceMapperImpl());

        pikachuEntity = PokemonEntity.builder()
                .id(1L).nationalNumber(25).name("Pikachu").generation(1).hasMega(false)
                .types(List.of(TypeEntity.builder().id(1L).name("Electric").build()))
                .region(RegionEntity.builder().id(1L).name("Kanto").build())
                .build();
    }

    @Test
    void findById_whenExists_mapsToDomain() {
        when(pokemonRepository.findWithDetailsById(1L)).thenReturn(Optional.of(pikachuEntity));

        Optional<Pokemon> result = adapter.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Pikachu");
    }

    @Test
    void findById_whenNotExists_returnsEmpty() {
        when(pokemonRepository.findWithDetailsById(99L)).thenReturn(Optional.empty());

        assertThat(adapter.findById(99L)).isEmpty();
    }

    @Test
    void findAll_mapsPage() {
        Page<PokemonEntity> page = new PageImpl<>(List.of(pikachuEntity));
        when(pokemonRepository.findAllWithTypes(any(Pageable.class))).thenReturn(page);

        Page<Pokemon> result = adapter.findAll(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void findByNationalNumber_delegates() {
        when(pokemonRepository.findWithDetailsByNationalNumber(25)).thenReturn(Optional.of(pikachuEntity));

        assertThat(adapter.findByNationalNumber(25)).isPresent();
    }

    @Test
    void existsByNationalNumber_delegates() {
        when(pokemonRepository.existsByNationalNumber(25)).thenReturn(true);

        assertThat(adapter.existsByNationalNumber(25)).isTrue();
    }

    @Test
    void findByRegion_mapsList() {
        when(pokemonRepository.findByRegionName("Kanto")).thenReturn(List.of(pikachuEntity));

        assertThat(adapter.findByRegion("Kanto")).hasSize(1);
    }

    @Test
    void searchByNameOrNumber_mapsList() {
        when(pokemonRepository.searchByNameOrNumber("pika")).thenReturn(List.of(pikachuEntity));

        assertThat(adapter.searchByNameOrNumber("pika")).hasSize(1);
    }

    @Test
    void save_whenNew_createsEntityResolvingTypesAndRegion() {
        Pokemon newPokemon = Pokemon.builder()
                .nationalNumber(25).name("Pikachu").generation(1).hasMega(false)
                .types(List.of("Electric")).region("Kanto")
                .stats(PokemonStats.builder().hp(35).attack(55).defense(40)
                        .specialAttack(50).specialDefense(50).speed(90).build())
                .build();

        when(typeRepository.findByName("Electric")).thenReturn(Optional.empty());
        when(typeRepository.save(any())).thenReturn(TypeEntity.builder().id(1L).name("Electric").build());
        when(regionRepository.findByName("Kanto")).thenReturn(Optional.of(
                RegionEntity.builder().id(1L).name("Kanto").build()));
        when(pokemonRepository.save(any())).thenReturn(pikachuEntity);
        when(statsRepository.findByPokemonId(1L)).thenReturn(Optional.empty());
        when(pokemonRepository.findWithDetailsById(1L)).thenReturn(Optional.of(pikachuEntity));

        Pokemon result = adapter.save(newPokemon);

        assertThat(result.getName()).isEqualTo("Pikachu");
        verify(statsRepository).save(any());
    }

    @Test
    void save_whenUpdatingExisting_reusesEntityAndSkipsStatsWhenNull() {
        Pokemon toUpdate = Pokemon.builder()
                .id(1L).nationalNumber(25).name("Raichu").generation(1).hasMega(false)
                .types(List.of()).build();

        when(pokemonRepository.findById(1L)).thenReturn(Optional.of(pikachuEntity));
        when(pokemonRepository.save(any())).thenReturn(pikachuEntity);
        when(pokemonRepository.findWithDetailsById(1L)).thenReturn(Optional.of(pikachuEntity));

        Pokemon result = adapter.save(toUpdate);

        assertThat(result).isNotNull();
        verify(statsRepository, never()).save(any());
    }

    @Test
    void deleteById_delegates() {
        adapter.deleteById(1L);

        verify(pokemonRepository).deleteById(1L);
    }
}
