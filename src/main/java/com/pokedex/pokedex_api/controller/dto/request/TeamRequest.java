package com.pokedex.pokedex_api.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record TeamRequest(
        @NotBlank @Size(max = 50) String name,
        List<Long> pokemonIds
) {}
