package com.pokedex.pokedex_api.core.service.impl;

import com.pokedex.pokedex_api.core.exception.DuplicateResourceException;
import com.pokedex.pokedex_api.core.exception.ResourceNotFoundException;
import com.pokedex.pokedex_api.core.model.Pokemon;
import com.pokedex.pokedex_api.core.model.PokemonFilterCriteria;
import com.pokedex.pokedex_api.core.port.PokemonPersistencePort;
import com.pokedex.pokedex_api.core.service.interfaces.PokemonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PokemonServiceImpl implements PokemonService {

    private final PokemonPersistencePort pokemonPort; // dependemos del PUERTO, no de JPA

    @Override
    public Page<Pokemon> findAll(Pageable pageable) {
        return pokemonPort.findAll(pageable);
    }

    @Override
    public Pokemon findById(Long id) {
        log.debug("Buscando Pokemon con id: {}", id);
        return pokemonPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pokemon", "id", id));
    }

    @Override
    public Pokemon findByNationalNumber(Integer number) {
        return pokemonPort.findByNationalNumber(number)
                .orElseThrow(() -> new ResourceNotFoundException("Pokemon", "nationalNumber", number));
    }

    @Override
    public Pokemon create(Pokemon pokemon) {
        if (pokemonPort.existsByNationalNumber(pokemon.getNationalNumber())) {
            throw new DuplicateResourceException("Pokemon", "nationalNumber", pokemon.getNationalNumber());
        }
        log.info("Creando Pokemon: {}", pokemon.getName());
        return pokemonPort.save(pokemon);
    }

    @Override
    public Pokemon update(Long id, Pokemon pokemon) {
        Pokemon existing = findById(id); // valida que exista, lanza 404 si no
        Pokemon updated = existing.toBuilder()
                .name(pokemon.getName())
                .description(pokemon.getDescription())
                .imageUrl(pokemon.getImageUrl())
                .types(pokemon.getTypes())
                .region(pokemon.getRegion())
                .generation(pokemon.getGeneration())
                .hasMega(pokemon.getHasMega())
                .stats(pokemon.getStats())
                .build();
        return pokemonPort.save(updated);
    }

    @Override
    public void delete(Long id) {
        findById(id);
        pokemonPort.deleteById(id);
    }

    @Override
    public List<Pokemon> filterByCriteria(PokemonFilterCriteria criteria) {
        if (criteria.region() != null) {
            return pokemonPort.findByRegion(criteria.region());
        }
        return pokemonPort.findAll(Pageable.unpaged()).getContent();
    }

    @Override
    public List<Pokemon> search(String query) {
        return pokemonPort.searchByNameOrNumber(query);
    }
}
