package com.pokedex.pokedex_api.controller.api;

import com.pokedex.pokedex_api.controller.dto.request.PokemonRequest;
import com.pokedex.pokedex_api.controller.dto.response.PokemonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pokemon", description = "Gestión del catálogo de Pokémon")
@RequestMapping("/v1/pokemon")
public interface PokemonApi {

    @Operation(summary = "Listar todos los Pokémon", description = "Retorna lista paginada. Acceso público.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida")
    @GetMapping
    ResponseEntity<Page<PokemonResponse>> findAll(@PageableDefault(size = 20, sort = "nationalNumber") Pageable pageable);

    @Operation(summary = "Obtener Pokémon por ID")
    @ApiResponse(responseCode = "404", description = "Pokémon no encontrado")
    @GetMapping("/{id}")
    ResponseEntity<PokemonResponse> findById(@PathVariable Long id);

    @Operation(summary = "Buscar Pokémon por nombre o número")
    @GetMapping("/search")
    ResponseEntity<List<PokemonResponse>> search(@RequestParam String query);

    @Operation(summary = "Filtrar Pokémon por región")
    @GetMapping("/filter")
    ResponseEntity<List<PokemonResponse>> filterByRegion(@RequestParam(required = false) String region);

    @Operation(summary = "Crear Pokémon", description = "Solo ADMIN")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Creado"),
            @ApiResponse(responseCode = "409", description = "Número de Pokédex duplicado")})
    @PostMapping
    ResponseEntity<PokemonResponse> create(@Valid @RequestBody PokemonRequest request);

    @Operation(summary = "Actualizar Pokémon", description = "Solo ADMIN")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    ResponseEntity<PokemonResponse> update(@PathVariable Long id, @Valid @RequestBody PokemonRequest request);

    @Operation(summary = "Eliminar Pokémon", description = "Solo ADMIN")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id);
}
