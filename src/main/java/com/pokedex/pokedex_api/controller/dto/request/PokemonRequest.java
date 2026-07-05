package com.pokedex.pokedex_api.controller.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record PokemonRequest(
        @NotNull(message = "El número nacional es obligatorio")
        @Min(value = 1, message = "El número debe ser mayor a 0")
        Integer nationalNumber,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        String name,

        String description,

        String imageUrl,

        @NotEmpty(message = "Debe tener al menos un tipo")
        @Size(max = 2, message = "Un Pokémon puede tener máximo 2 tipos")
        List<String> types,

        String region,

        @NotNull @Min(1) Integer generation,

        Boolean hasMega,

        @Valid PokemonStatsRequest stats
) {}
