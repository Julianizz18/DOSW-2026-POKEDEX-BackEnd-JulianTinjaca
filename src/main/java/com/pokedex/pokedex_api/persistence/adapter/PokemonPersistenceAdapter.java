package com.pokedex.pokedex_api.persistence.adapter;

import com.pokedex.pokedex_api.core.exception.ResourceNotFoundException;
import com.pokedex.pokedex_api.core.model.Pokemon;
import com.pokedex.pokedex_api.core.port.PokemonPersistencePort;
import com.pokedex.pokedex_api.persistence.entity.relational.PokemonEntity;
import com.pokedex.pokedex_api.persistence.entity.relational.PokemonStatsEntity;
import com.pokedex.pokedex_api.persistence.entity.relational.RegionEntity;
import com.pokedex.pokedex_api.persistence.entity.relational.TypeEntity;
import com.pokedex.pokedex_api.persistence.mapper.PokemonPersistenceMapper;
import com.pokedex.pokedex_api.persistence.repository.relational.PokemonJpaRepository;
import com.pokedex.pokedex_api.persistence.repository.relational.RegionJpaRepository;
import com.pokedex.pokedex_api.persistence.repository.relational.TypeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PokemonPersistenceAdapter implements PokemonPersistencePort {

    private final PokemonJpaRepository pokemonRepository;
    private final TypeJpaRepository typeRepository;
    private final RegionJpaRepository regionRepository;
    private final PokemonPersistenceMapper mapper;

    @Override
    public Optional<Pokemon> findById(Long id) {
        return pokemonRepository.findWithDetailsById(id).map(mapper::toDomain);
    }

    @Override
    public Page<Pokemon> findAll(Pageable pageable) {
        return pokemonRepository.findAllWithTypes(pageable).map(mapper::toDomain);
    }

    @Override
    public Optional<Pokemon> findByNationalNumber(Integer number) {
        return pokemonRepository.findWithDetailsByNationalNumber(number).map(mapper::toDomain);
    }

    @Override
    public boolean existsByNationalNumber(Integer number) {
        return pokemonRepository.existsByNationalNumber(number);
    }

    @Override
    public List<Pokemon> findByRegion(String regionName) {
        return pokemonRepository.findByRegionName(regionName).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Pokemon> searchByNameOrNumber(String query) {
        return pokemonRepository.searchByNameOrNumber(query).stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public Pokemon save(Pokemon pokemon) {
        PokemonEntity existing = pokemon.getId() != null
                ? pokemonRepository.findById(pokemon.getId())
                  .orElseThrow(() -> new ResourceNotFoundException("Pokemon", "id", pokemon.getId()))
                : null;

        List<TypeEntity> types = resolveTypes(pokemon.getTypes());
        RegionEntity region = pokemon.getRegion() != null ? resolveRegion(pokemon.getRegion()) : null;

        PokemonEntity toSave;
        if (existing != null) {
            toSave = existing.toBuilder()
                    .name(pokemon.getName())
                    .imageUrl(pokemon.getImageUrl())
                    .generation(pokemon.getGeneration())
                    .hasMega(pokemon.getHasMega())
                    .types(types)
                    .region(region)
                    .build();
        } else {
            toSave = mapper.toNewEntity(pokemon).toBuilder()
                    .types(types)
                    .region(region)
                    .build();
        }

        PokemonEntity saved = pokemonRepository.save(toSave);

        if (pokemon.getStats() != null) {
            PokemonStatsEntity statsEntity = PokemonStatsEntity.builder()
                    .id(saved.getStats() != null ? saved.getStats().getId() : null)
                    .hp(pokemon.getStats().getHp())
                    .attack(pokemon.getStats().getAttack())
                    .defense(pokemon.getStats().getDefense())
                    .specialAttack(pokemon.getStats().getSpecialAttack())
                    .specialDefense(pokemon.getStats().getSpecialDefense())
                    .speed(pokemon.getStats().getSpeed())
                    .pokemon(saved)
                    .build();
            saved = saved.toBuilder().stats(statsEntity).build();
            saved = pokemonRepository.save(saved);
        }

        return mapper.toDomain(pokemonRepository.findWithDetailsById(saved.getId()).orElseThrow());
    }

    @Override
    public void deleteById(Long id) {
        pokemonRepository.deleteById(id);
    }

    private List<TypeEntity> resolveTypes(List<String> typeNames) {
        if (typeNames == null || typeNames.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(typeNames.stream()
                .map(name -> typeRepository.findByName(name)
                        .orElseGet(() -> typeRepository.save(TypeEntity.builder().name(name).build())))
                .toList());
    }

    private RegionEntity resolveRegion(String regionName) {
        return regionRepository.findByName(regionName)
                .orElseGet(() -> regionRepository.save(RegionEntity.builder().name(regionName).build()));
    }
}