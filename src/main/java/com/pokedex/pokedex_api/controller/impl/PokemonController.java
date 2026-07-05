package com.pokedex.pokedex_api.controller.impl;

import com.pokedex.pokedex_api.controller.api.PokemonApi;
import com.pokedex.pokedex_api.controller.dto.request.PokemonRequest;
import com.pokedex.pokedex_api.controller.dto.response.PokemonResponse;
import com.pokedex.pokedex_api.controller.mapper.PokemonDtoMapper;
import com.pokedex.pokedex_api.core.model.Pokemon;
import com.pokedex.pokedex_api.core.model.PokemonFilterCriteria;
import com.pokedex.pokedex_api.core.service.interfaces.PokemonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PokemonController implements PokemonApi {

    private final PokemonService pokemonService;
    private final PokemonDtoMapper mapper;

    @Override
    public ResponseEntity<Page<PokemonResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(pokemonService.findAll(pageable).map(mapper::toResponse));
    }

    @Override
    public ResponseEntity<PokemonResponse> findById(Long id) {
        return ResponseEntity.ok(mapper.toResponse(pokemonService.findById(id)));
    }

    @Override
    public ResponseEntity<List<PokemonResponse>> search(String query) {
        List<PokemonResponse> results = pokemonService.search(query).stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(results);
    }

    @Override
    public ResponseEntity<List<PokemonResponse>> filterByRegion(String region) {
        var criteria = new PokemonFilterCriteria(null, region, null, null);
        List<PokemonResponse> results = pokemonService.filterByCriteria(criteria).stream()
                .map(mapper::toResponse).toList();
        return ResponseEntity.ok(results);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PokemonResponse> create(PokemonRequest request) {
        Pokemon created = pokemonService.create(mapper.toDomain(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(mapper.toResponse(created));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PokemonResponse> update(Long id, PokemonRequest request) {
        Pokemon updated = pokemonService.update(id, mapper.toDomain(request));
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(Long id) {
        pokemonService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
