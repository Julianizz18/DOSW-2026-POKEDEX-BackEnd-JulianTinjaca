package com.pokedex.pokedex_api.controller.api;

import com.pokedex.pokedex_api.controller.dto.response.FavoriteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Favorite", description = "Favoritos del usuario autenticado")
@RequestMapping("/v1/favorites")
@SecurityRequirement(name = "Bearer Authentication")
public interface FavoriteApi {

    @Operation(summary = "Consultar lista de favoritos (RF-13)")
    @GetMapping
    ResponseEntity<List<FavoriteResponse>> findMyFavorites(Authentication authentication);

    @Operation(summary = "Marcar Pokémon como favorito (RF-12)")
    @PostMapping("/{pokemonId}")
    ResponseEntity<FavoriteResponse> add(@PathVariable Long pokemonId, Authentication authentication);

    @Operation(summary = "Desmarcar Pokémon como favorito (RF-12)")
    @DeleteMapping("/{pokemonId}")
    ResponseEntity<Void> remove(@PathVariable Long pokemonId, Authentication authentication);
}
