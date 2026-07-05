package com.pokedex.pokedex_api.controller.api;

import com.pokedex.pokedex_api.controller.dto.request.TeamPokemonRequest;
import com.pokedex.pokedex_api.controller.dto.request.TeamRequest;
import com.pokedex.pokedex_api.controller.dto.response.TeamResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Team", description = "Equipos Pokémon del usuario autenticado")
@RequestMapping("/v1/teams")
@SecurityRequirement(name = "Bearer Authentication")
public interface TeamApi {

    @Operation(summary = "Consultar mis equipos (RF-18)")
    @GetMapping
    ResponseEntity<List<TeamResponse>> findMyTeams(Authentication authentication);

    @Operation(summary = "Crear equipo (RF-14)")
    @PostMapping
    ResponseEntity<TeamResponse> create(@Valid @RequestBody TeamRequest request, Authentication authentication);

    @Operation(summary = "Consultar análisis competitivo de un equipo (RF-15)")
    @GetMapping("/{id}/analysis")
    ResponseEntity<String> analysis(@PathVariable Long id);

    @Operation(summary = "Agregar Pokémon a un equipo (RF-16)")
    @PostMapping("/{id}/pokemon")
    ResponseEntity<TeamResponse> addPokemon(@PathVariable Long id, @Valid @RequestBody TeamPokemonRequest request);

    @Operation(summary = "Quitar Pokémon de un equipo (RF-16)")
    @DeleteMapping("/{id}/pokemon/{pokemonId}")
    ResponseEntity<TeamResponse> removePokemon(@PathVariable Long id, @PathVariable Long pokemonId);

    @Operation(summary = "Eliminar equipo (RF-17)")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id);
}
