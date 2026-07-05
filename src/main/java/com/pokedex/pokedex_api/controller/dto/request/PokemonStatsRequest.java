package com.pokedex.pokedex_api.controller.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PokemonStatsRequest(
        @NotNull @Min(0) Integer hp,
        @NotNull @Min(0) Integer attack,
        @NotNull @Min(0) Integer defense,
        @NotNull @Min(0) Integer specialAttack,
        @NotNull @Min(0) Integer specialDefense,
        @NotNull @Min(0) Integer speed
) {}
