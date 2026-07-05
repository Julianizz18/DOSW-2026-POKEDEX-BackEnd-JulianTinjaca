package com.pokedex.pokedex_api.controller.dto.request;

import jakarta.validation.constraints.NotNull;

public record TeamPokemonRequest(
        @NotNull Long pokemonId
) {}
