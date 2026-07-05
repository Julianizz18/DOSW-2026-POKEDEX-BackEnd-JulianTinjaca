package com.pokedex.pokedex_api.core.service.interfaces;

import com.pokedex.pokedex_api.core.model.Pokemon;
import com.pokedex.pokedex_api.core.model.PokemonFilterCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PokemonService {

    Page<Pokemon> findAll(Pageable pageable);

    Pokemon findById(Long id);

    Pokemon findByNationalNumber(Integer number);

    Pokemon create(Pokemon pokemon);

    Pokemon update(Long id, Pokemon pokemon);

    void delete(Long id);

    List<Pokemon> filterByCriteria(PokemonFilterCriteria criteria);

    List<Pokemon> search(String query);
}
